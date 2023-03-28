package one.cafebabe.jsonicboom;

import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
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
                case COLON:
                case COMMA:
                case KEY_NAME:
                    break;
                case START_ARRAY:
                    arrayValues.add(new JSONArray(jsonTokenizer, next));
                    break;
                case END_ARRAY:
                    ended = true;
                    endIndex = next.endIndex;
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

    public long getLong(int index) {
        String value = getString(index);
        if (value == null) {
            return -1;
        }
        return Long.parseLong(value);
    }

    public double getDouble(int index) {
        String value = getString(index);
        if (value == null) {
            return -1;
        }
        return Double.parseDouble(value);
    }

    @Nullable
    public BigDecimal getBigDecimal(int index) {
        String value = getString(index);
        if (value == null) {
            return null;
        }
        return new BigDecimal(value);
    }

    public boolean getBoolean(int index) {
        String value = getString(index);
        if (value == null) {
            return false;
        }
        return Boolean.parseBoolean(value);
    }

    @Nullable
    public JSONArray getJSONArray(int index) {
        return (JSONArray) arrayValues.get(index);
    }

    @Nullable
    public JSONObject getJSONObject(int index) {
        return (JSONObject) arrayValues.get(index);
    }


    public int[] getIntArray() {
        int[] intArray = new int[length()];
        for (int i = 0; i < intArray.length; i++) {
            intArray[i] = getInt(i);
        }
        return intArray;
    }

    public long[] getLongArray() {
        long[] longArray = new long[length()];
        for (int i = 0; i < longArray.length; i++) {
            longArray[i] = getLong(i);
        }
        return longArray;
    }

    public double[] getDoubleArray() {
        double[] doubleArray = new double[length()];
        for (int i = 0; i < doubleArray.length; i++) {
            doubleArray[i] = getDouble(i);
        }
        return doubleArray;
    }

    public boolean[] getBooleanArray() {
        boolean[] booleanArray = new boolean[length()];
        for (int i = 0; i < booleanArray.length; i++) {
            booleanArray[i] = getBoolean(i);
        }
        return booleanArray;
    }

    public BigDecimal[] getBigDecimalArray() {
        BigDecimal[] bigDecimalArray = new BigDecimal[length()];
        for (int i = 0; i < bigDecimalArray.length; i++) {
            bigDecimalArray[i] = getBigDecimal(i);
        }
        return bigDecimalArray;
    }

    public String[] getStringArray() {
        String[] stringArray = new String[length()];
        for (int i = 0; i < stringArray.length; i++) {
            stringArray[i] = getString(i);
        }
        return stringArray;
    }

    public JSONArray[] getJSONArrayArray() {
        JSONArray[] jsonArray = new JSONArray[length()];
        for (int i = 0; i < jsonArray.length; i++) {
            jsonArray[i] = getJSONArray(i);
        }
        return jsonArray;
    }

    public JSONObject[] getJSONObjectArray() {
        JSONObject[] jsonObjectArray = new JSONObject[length()];
        for (int i = 0; i < jsonObjectArray.length; i++) {
            jsonObjectArray[i] = getJSONObject(i);
        }
        return jsonObjectArray;
    }


    public List<Integer> getIntList() {
        List<Integer> intList = new ArrayList<>(length());
        for (int i = 0; i < length(); i++) {
            intList.add(getInt(i));
        }
        return intList;
    }

    public List<Long> getLongList() {
        List<Long> longList = new ArrayList<>(length());
        for (int i = 0; i < length(); i++) {
            longList.add(getLong(i));
        }
        return longList;
    }

    public List<Double> getDoubleList() {
        List<Double> doubleList = new ArrayList<>(length());
        for (int i = 0; i < length(); i++) {
            doubleList.add(getDouble(i));
        }
        return doubleList;
    }

    public List<Boolean> getBooleanList() {
        List<Boolean> booleanList = new ArrayList<>(length());
        for (int i = 0; i < length(); i++) {
            booleanList.add(getBoolean(i));
        }
        return booleanList;
    }

    public List<BigDecimal> getBigDecimalList() {
        List<BigDecimal> bigDecimalList = new ArrayList<>(length());
        for (int i = 0; i < length(); i++) {
            bigDecimalList.add(getBigDecimal(i));
        }
        return bigDecimalList;
    }

    public List<String> getStringList() {
        List<String> stringList = new ArrayList<>(length());
        for (int i = 0; i < length(); i++) {
            stringList.add(getString(i));
        }
        return stringList;
    }

    public List<JSONArray> getJSONArrayList() {
        List<JSONArray> jsonList = new ArrayList<>(length());
        for (int i = 0; i < length(); i++) {
            jsonList.add(getJSONArray(i));
        }
        return jsonList;
    }

    public List<JSONObject> getJSONObjectList() {
        List<JSONObject> jsonObjectList = new ArrayList<>(length());
        for (int i = 0; i < length(); i++) {
            jsonObjectList.add(getJSONObject(i));
        }
        return jsonObjectList;
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
