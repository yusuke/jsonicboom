package one.cafebabe.jsonicboom;

public class JSON {
    private JSON() {
    }

    public static JSONObject parseObject(String json) {
        JSONTokenizer jsonTokenizer = new JSONTokenizer(json);
        return new JSONObject(jsonTokenizer, jsonTokenizer.next());
    }

    public static JSONArray parseArray(String json) {
        JSONTokenizer jsonTokenizer = new JSONTokenizer(json);
        return new JSONArray(jsonTokenizer, jsonTokenizer.next());
    }

}
