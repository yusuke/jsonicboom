package one.cafebabe.jsonicboom;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class JSONObject {

    private final Map<String, Object> map = new HashMap<>();
    private final Map<String, JSONArray> arrayMap = new HashMap<>();

    private final String jsonString;
    private final int startIndex;
    private final int endIndex;

    JSONObject(JSONTokenizer jsonTokenizer, JSONTokenizer.JsonIndices next) {
        jsonString = jsonTokenizer.jsonString;
        startIndex = next.startIndex;
        boolean first = true;
        String lastKey = null;
        int endIndex = -1;
        while (null != next) {
            boolean ended = false;
            switch (next.jsonEventType) {
                case START_OBJECT:
                    if (first) {
                        first = false;
                    } else {
                        map.put(lastKey, new JSONObject(jsonTokenizer, next));
                    }
                    break;
                case END_OBJECT:
                    ended = true;
                    endIndex = next.endIndex;
                    break;
                case START_ARRAY:
                    arrayMap.put(lastKey, new JSONArray(jsonTokenizer, next));
                    break;
                case END_ARRAY:
                    break;
                case KEY_NAME:
                    lastKey = next.getValue();
                    break;
                case VALUE_STRING:
                    map.put(lastKey, next);
                    break;
                case VALUE_NUMBER:
                    map.put(lastKey, next);
                    break;
                case VALUE_TRUE:
                    map.put(lastKey, next);
                    break;
                case VALUE_FALSE:
                    map.put(lastKey, next);
                    break;
                case VALUE_NULL:
                    map.put(lastKey, next);
                    break;
            }
            if (ended) {
                break;
            }
            next = jsonTokenizer.next();
        }
        this.endIndex = endIndex;
    }

    @Nullable
    public String getString(@NotNull String name) {
        Object o = map.get(name);
        if (o == null) {
            return null;
        } else if (o instanceof JSONTokenizer.JsonIndices) {
            JSONTokenizer.JsonIndices jsonIndices = (JSONTokenizer.JsonIndices) o;
            return jsonIndices.getValue();
        } else {
            return o.toString();
        }
    }

    public int getInt(@NotNull String name) {
        String value = getString(name);
        if (value == null) {
            return -1;
        }
        return Integer.parseInt(value);
    }

    public long getLong(@NotNull String name) {
        String value = getString(name);
        if (value == null) {
            return -1;
        }
        return Long.parseLong(value);
    }

    public double getDouble(@NotNull String name) {
        String value = getString(name);
        if (value == null) {
            return -1;
        }
        return Double.parseDouble(value);
    }

    @Nullable
    public BigDecimal getBigDecimal(@NotNull String name) {
        String value = getString(name);
        if (value == null) {
            return null;
        }
        return new BigDecimal(value);
    }

    public boolean getBoolean(@NotNull String name) {
        String value = getString(name);
        if (value == null) {
            return false;
        }
        return Boolean.parseBoolean(getString(name));
    }

    @Nullable
    public JSONArray getJSONArray(@NotNull String name) {
        return arrayMap.get(name);
    }

    @Nullable
    public Object get(@NotNull String name) {
        Object o = map.get(name);
        if (o instanceof JSONTokenizer.JsonIndices) {
            JSONTokenizer.JsonIndices jsonIndices = (JSONTokenizer.JsonIndices) o;
            JSONTokenizer.JsonEventType jsonEventType = jsonIndices.jsonEventType;
            switch (jsonEventType) {
                case VALUE_STRING:
                    return jsonIndices.getValue();
                case VALUE_NUMBER:
                    try {
                        //noinspection DataFlowIssue
                        return Integer.parseInt(jsonIndices.getValue());
                    } catch (NumberFormatException nfe) {
                        try {
                            //noinspection DataFlowIssue
                            return Long.valueOf(jsonIndices.getValue());
                        } catch (NumberFormatException nfe2) {
                            //noinspection DataFlowIssue
                            return Double.parseDouble(jsonIndices.getValue());
                        }
                    }
                case VALUE_TRUE:
                    return Boolean.TRUE;
                case VALUE_FALSE:
                    return Boolean.FALSE;
                case VALUE_NULL:
                    return null;
            }
        }
        if (o == null) {
            // JSONArray
            return arrayMap.get(name);
        }else {
            // JSONObject
            return o;
        }
    }

    @Nullable
    public JSONObject getJSONObject(@NotNull String name) {
        Object o = map.get(name);
        if (o instanceof JSONTokenizer.JsonIndices &&
                ((JSONTokenizer.JsonIndices) o).jsonEventType == JSONTokenizer.JsonEventType.VALUE_NULL) {
            return null;
        } else if (o instanceof JSONObject) {
            return (JSONObject) o;
        }
        throw new UnsupportedOperationException(String.format("Value for '%s' is not JSONObject: %s.", name, o));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JSONObject that = (JSONObject) o;
        return startIndex == that.startIndex && endIndex == that.endIndex && Objects.equals(map, that.map) && Objects.equals(arrayMap, that.arrayMap) && Objects.equals(jsonString, that.jsonString);
    }

    @Override
    public int hashCode() {
        return Objects.hash(map, arrayMap, jsonString, startIndex, endIndex);
    }

    @Override
    public String toString() {
        return jsonString.substring(startIndex, endIndex);
    }

    public boolean has(@NotNull String name) {
        return map.containsKey(name) || arrayMap.containsKey(name);
    }
}
