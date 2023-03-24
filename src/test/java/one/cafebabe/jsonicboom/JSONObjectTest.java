package one.cafebabe.jsonicboom;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JSONObjectTest {
    @Test
    public void testSimple(){
        String json = """
                {
                  "name": "山本 裕介",
                  "address": {
                    "zip": "1700012"
                  }
                }""";

        JSONObject jsonObject = JSON.parseObject(json);

    }

    @Test
    public void test() {
        String json = """
                {
                  "name": "山本 裕介",
                  "description": "This is a test JSON object with Unicode escaped characters: \\u0026 (ampersand), \\u0022 (double quote), and \\u0027 (single quote).",
                  "age": 30,
                  "isMarried": true,
                  "hobbies": [
                    "programing",
                    "video game",
                    "archery"
                  ],
                  "🍕_rating": "👍👍👍",
                  "notes": null,
                  "height": 1.65,
                  "weight": 56.7,
                  "address": {
                    "street": "東京都\\u4E2D\\u592E\\u533A",
                    "city": "San Francisco",
                    "state": "CA",
                    "zip": "1700012"
                  }
                }""";
        JSONObject jsonObject = JSON.parseObject(json);
        assertEquals(json, jsonObject.toString());
        assertEquals("山本 裕介", jsonObject.getString("name"));
        assertEquals("""
                This is a test JSON object with Unicode escaped characters: & (ampersand), " (double quote), and ' (single quote).""", jsonObject.getString("description"));
        assertEquals(30, jsonObject.getInt("age"));
        assertTrue(jsonObject.getBoolean("isMarried"));
        JSONArray hobbies = jsonObject.getJSONArray("hobbies");
        assert hobbies != null;
        assertEquals("""
                [
                    "programing",
                    "video game",
                    "archery"
                  ]""", hobbies.toString());
        assertEquals(3, hobbies.length());
        assertEquals("programing", hobbies.getString(0));
        assertEquals("video game", hobbies.getString(1));
        assertEquals("archery", hobbies.getString(2));
        assertEquals("👍👍👍", jsonObject.getString("🍕_rating"));
        assertNull(jsonObject.get("notes"));
        JSONObject address = jsonObject.get("address");
        assertNotNull(address);
        assertEquals("東京都中央区", address.getString("street"));
        assertEquals("San Francisco", address.getString("city"));
        assertEquals("CA", address.getString("state"));
        assertEquals("1700012", address.getString("zip"));

        assertThrows(IndexOutOfBoundsException.class, () -> hobbies.getString(3));

    }
}
