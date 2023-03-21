package one.cafebabe.jsonicboom;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class JSONObject {

    private final Map<String, Object> map = new HashMap<>();
    private final Map<String, JSONArray> arrayMap = new HashMap<>();

    public JSONObject(String jsonString) {
        this(new Boom(jsonString));
    }

    JSONObject(Boom boom) {
        this(boom, boom.next());
    }

    JSONObject(Boom boom, Boom.JsonIndices next) {
        if (next.jsonEventType == Boom.JsonEventType.START_OBJECT) {
            next = boom.next();
        }
        String lastKey = null;
        boolean ended = false;
        while (!ended && null != next) {
            switch (next.jsonEventType) {
                case START_OBJECT:
                    map.put(lastKey, new JSONObject(boom));
                    break;
                case END_OBJECT:
                    ended = true;
                    break;
                case START_ARRAY:
                    arrayMap.put(lastKey, new JSONArray(boom));
                    break;
                case END_ARRAY:
                    break;
                case KEY_NAME:
                    lastKey = next.getValue();
                    break;
                case VALUE_STRING:
                    map.put(lastKey, next.getValue());
                    break;
                case VALUE_NUMBER:
                    map.put(lastKey, next.getValue());
                    break;
                case VALUE_TRUE:
                    map.put(lastKey, next.getValue());
                    break;
                case VALUE_FALSE:
                    map.put(lastKey, next.getValue());
                    break;
                case VALUE_NULL:
                    map.put(lastKey, null);
                    break;
            }
            next = boom.next();

        }
    }

    @Nullable
    public String getString(String name) {
        return (String) map.get(name);
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
        return (JSONObject) map.get(name);
    }
}
