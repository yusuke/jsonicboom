package one.cafebabe.jsonicboom;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JSONTokenizerObjectTest {
    @Test
    public void test() {
        JSONObject jsonObject = new JSONObject("""
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
                }
                """);
        assertEquals("山本 裕介", jsonObject.getString("name"));
        assertEquals("""
        This is a test JSON object with Unicode escaped characters: & (ampersand), " (double quote), and ' (single quote).""", jsonObject.getString("description"));
        assertEquals(30, jsonObject.getInt("age"));
        assertTrue(jsonObject.getBoolean("isMarried"));
        JSONArray cars = jsonObject.getJSONArray("hobbies");
        assertEquals(3, cars.length());
        assertEquals("programing", cars.getString(0));
        assertEquals("video game", cars.getString(1));
        assertEquals("archery", cars.getString(2));
        assertEquals("👍👍👍", jsonObject.getString("🍕_rating"));
        assertNull(jsonObject.get("notes"));
        JSONObject address = jsonObject.get("address");
        assertNotNull(address);
        assertEquals("東京都中央区", address.getString("street"));
        assertEquals("San Francisco", address.getString("city"));
        assertEquals("CA", address.getString("state"));
        assertEquals("1700012", address.getString("zip"));

        assertThrows(IndexOutOfBoundsException.class, ()->    cars.getString(3));

    }
}
