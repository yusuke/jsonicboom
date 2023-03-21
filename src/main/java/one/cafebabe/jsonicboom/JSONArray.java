package one.cafebabe.jsonicboom;

import java.util.ArrayList;
import java.util.List;

public class JSONArray {
    private final List<Object> arrayValues = new ArrayList<>();

    JSONArray(Boom boom) {
        boolean ended = false;
        Boom.JsonIndices next;
        while (!ended && null != (next = boom.next())) {
            switch (next.jsonEventType) {
                case START_OBJECT:
                    arrayValues.add(new JSONObject(boom));
                case END_OBJECT:
                    break;
                case START_ARRAY:
                    new JSONArray(boom);
                    break;
                case END_ARRAY:
                    ended = true;
                    break;
                case KEY_NAME:
                    break;
                case VALUE_STRING:
                    arrayValues.add(next.getValue());
                    break;
                case VALUE_NUMBER:
                    arrayValues.add(next.getValue());
                    break;
                case VALUE_TRUE:
                    arrayValues.add(next.getValue());
                    break;
                case VALUE_FALSE:
                    arrayValues.add(next.getValue());
                    break;
                case VALUE_NULL:
                    arrayValues.add(null);
                    break;
            }
        }
    }

    public int length() {
        return arrayValues.size();
    }

    public String getString(int index) {
        return (String) arrayValues.get(index);
    }
}
