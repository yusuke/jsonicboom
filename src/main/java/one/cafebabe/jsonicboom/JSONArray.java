package one.cafebabe.jsonicboom;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class JSONArray {
    private final List<Object> arrayValues = new ArrayList<>();
    private final String jsonString;
    private final int startIndex;
    private final int endIndex;

    JSONArray(JSONTokenizer jsonTokenizer, JSONTokenizer.JsonIndices next) {
        jsonString = jsonTokenizer.jsonString;
        startIndex = next.startIndex;
        next = jsonTokenizer.next();
        int endIndex = -1;
        while (null != next) {
            boolean ended = false;
            switch (next.jsonEventType) {
                case START_OBJECT:
                    arrayValues.add(new JSONObject(jsonTokenizer, next));
                case END_OBJECT:
                    break;
                case START_ARRAY:
                    arrayValues.add(new JSONArray(jsonTokenizer, next));
                    break;
                case END_ARRAY:
                    ended = true;
                    endIndex = next.endIndex;
                    break;
                case KEY_NAME:
                    break;
                case VALUE_STRING:
                case VALUE_NUMBER:
                case VALUE_TRUE:
                case VALUE_FALSE:
                case VALUE_NULL:
                    arrayValues.add(next);
                    break;
            }
            if (ended) {
                break;
            }
            next = jsonTokenizer.next();
        }
        this.endIndex = endIndex;
    }

    public int length() {
        return arrayValues.size();
    }

    @Nullable
    public String getString(int index) {
        JSONTokenizer.JsonIndices jsonIndices = (JSONTokenizer.JsonIndices) arrayValues.get(index);
        return jsonIndices.getValue();
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JSONArray jsonArray = (JSONArray) o;
        return startIndex == jsonArray.startIndex && endIndex == jsonArray.endIndex && Objects.equals(arrayValues, jsonArray.arrayValues) && Objects.equals(jsonString, jsonArray.jsonString);
    }

    @Override
    public int hashCode() {
        return Objects.hash(arrayValues, jsonString, startIndex, endIndex);
    }

    @Override
    public String toString() {
        return jsonString.substring(startIndex, endIndex);
    }
}
