package one.cafebabe.jsonicboom;

import org.jetbrains.annotations.Nullable;

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
                    if(first){
                        first = false;
                    }else{
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
    public String getString(String name) {
        JSONTokenizer.JsonIndices jsonIndices = (JSONTokenizer.JsonIndices) map.get(name);
        return jsonIndices.getValue();
    }

    public int getInt(String name) {
        String value = getString(name);
        if (value == null) {
            return -1;
        }
        return Integer.parseInt(value);
    }

    public boolean getBoolean(String name) {
        String value = getString(name);
        if (value == null) {
            return false;
        }
        return Boolean.parseBoolean(getString(name));
    }

    @Nullable
    public JSONArray getJSONArray(String name) {
        return arrayMap.get(name);
    }

    @Nullable
    public JSONObject get(String name) {
        Object o = map.get(name);
        if (o instanceof JSONTokenizer.JsonIndices &&
            ((JSONTokenizer.JsonIndices) o).jsonEventType == JSONTokenizer.JsonEventType.VALUE_NULL) {
            return null;
        } else if (o instanceof JSONObject) {
            return (JSONObject) o;
        }
        throw new UnsupportedOperationException(String.format("value for %s is not JSONObject: %s", name, o));
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
}
