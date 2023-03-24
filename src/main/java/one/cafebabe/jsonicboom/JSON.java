package one.cafebabe.jsonicboom;

public final class JSON {
    private JSON() {
    }

    public static JSONObject parseObject(String json) {
        JSONTokenizer jsonTokenizer = new JSONTokenizer(json);
        JSONObject jsonObject = new JSONObject(jsonTokenizer, jsonTokenizer.next());
        if (jsonTokenizer.next() != null) {
            throw new IllegalJSONFormatException("Illegal JSON format");
        }
        return jsonObject;
    }

    public static JSONArray parseArray(String json) {
        JSONTokenizer jsonTokenizer = new JSONTokenizer(json);
        JSONArray jsonArray = new JSONArray(jsonTokenizer, jsonTokenizer.next());
        if (jsonTokenizer.next() != null) {
            throw new IllegalJSONFormatException("Illegal JSON format");
        }
        return jsonArray;
    }

}
