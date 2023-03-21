package one.cafebabe.jsonicboom;

public class JSON {
    private JSON() {
    }

    public static JSONObject parseObject(String json) {
        return new JSONObject(new JSONTokenizer(json));
    }

    public static JSONArray parseArray(String json) {
        return new JSONArray(new JSONTokenizer(json));
    }

}
