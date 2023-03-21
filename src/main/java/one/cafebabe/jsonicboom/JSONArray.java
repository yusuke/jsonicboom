package one.cafebabe.jsonicboom;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class JSONArray {
    private final List<Object> arrayValues = new ArrayList<>();

    JSONArray(JSONTokenizer jsonTokenizer) {
        JSONTokenizer.JsonIndices next = jsonTokenizer.next();
        if (next.jsonEventType == JSONTokenizer.JsonEventType.START_ARRAY) {
            next = jsonTokenizer.next();
        }
        while (null != next) {
            boolean ended = false;
            switch (next.jsonEventType) {
                case START_OBJECT:
                    arrayValues.add(new JSONObject(jsonTokenizer));
                case END_OBJECT:
                    break;
                case START_ARRAY:
                    arrayValues.add(new JSONArray(jsonTokenizer));
                    break;
                case END_ARRAY:
                    ended = true;
                    break;
                case KEY_NAME:
                    break;
                case VALUE_STRING:
                case VALUE_NUMBER:
                case VALUE_TRUE:
                case VALUE_FALSE:
                    arrayValues.add(next.getValue());
                    break;
                case VALUE_NULL:
                    arrayValues.add(null);
                    break;
            }
            if (ended) {
                break;
            }
            next = jsonTokenizer.next();
        }
    }

    public int length() {
        return arrayValues.size();
    }

    @Nullable
    public String getString(int index) {
        return (String) arrayValues.get(index);
    }

    public int getInt(int index) {
        String value = getString(index);
        if (value == null) {
            return -1;
        }
        return Integer.parseInt(value);
    }

    @Nullable
    public JSONArray getJSONArray(int index) {
        return (JSONArray) arrayValues.get(index);
    }

    @Nullable
    public JSONObject getJSONObject(int index) {
        return (JSONObject) arrayValues.get(index);
    }
}
