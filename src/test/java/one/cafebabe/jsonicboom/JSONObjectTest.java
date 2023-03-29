/*
 * Copyright 2023 Yusuke Yamamoto
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package one.cafebabe.jsonicboom;


import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class JSONObjectTest {
    @SuppressWarnings("DataFlowIssue")
    @Test
    public void testGetters() {

        String jsonStr = """
                {
                  "string": "Yusuke Yamamoto",
                  "int": 1,
                  "int_max": 2147483647,
                  "int_min": -2147483648,
                  "long": 2,
                  "long_max": 9223372036854775807,
                  "long_min": -9223372036854775808,
                  "decimal_point": 0.1,
                  "true":true,
                  "false":false,
                  "null": null,
                  "person":{"name":"Yusuke \\nYamamoto"},
                  "intArray":[10,20,30],
                  "stringArray":["yusuke","yamamoto","json"],
                  "boolArray":[true,false,false],
                  "arrayArray":[[1,1],[2,2],[3,4]],
                  "objectArray":[{"name":"yusuke"},{}]
                }""";
        JSONObject json = JSON.parseObject(jsonStr);
        assertTrue(json.has("int"));
        assertEquals(1, json.get("int"));
        assertEquals(1, json.getInt("int"));
        assertTrue(json.has("string"));
        assertEquals("Yusuke Yamamoto", json.get("string"));
        assertTrue(json.has("int_max"));
        assertEquals(2147483647, json.get("int_max"));
        assertEquals(2147483647, json.getInt("int_max"));
        assertEquals(-2147483648, json.getInt("int_min"));
        assertEquals(2, json.get("long"));
        assertEquals(2, json.getLong("long"));
        assertEquals("2", json.getString("long"));
        assertEquals("9223372036854775807", json.getString("long_max"));
        assertEquals(9223372036854775807L, json.getLong("long_max"));
        assertEquals(-9223372036854775808L, json.getLong("long_min"));
        assertEquals(0.1d, json.getDouble("decimal_point"));
        assertEquals(0.1d, json.get("decimal_point"));
        assertEquals("0.1", json.getString("decimal_point"));
        assertEquals(new BigDecimal("0.1"), json.getBigDecimal("decimal_point"));
        assertNull(json.get("null"));
        assertNull(json.getString("null"));
        assertTrue(json.has("null"));
        assertFalse(json.has("nill"));
        assertNull(json.getString("nill"));
        assertEquals("{\"name\":\"Yusuke \\nYamamoto\"}", json.getString("person"));
        assertEquals(JSONObject.class, json.get("person").getClass());
        assertEquals("Yusuke \nYamamoto", json.getJSONObject("person").getString("name"));
        assertTrue(json.has("intArray"));
        assertEquals(JSONArray.class, json.get("intArray").getClass());
        assertArrayEquals(new int[]{10, 20, 30}, json.getJSONArray("intArray").getIntArray());
        assertEquals(3, json.getJSONArray("intArray").getIntList().size());
        assertArrayEquals(new long[]{10, 20, 30}, json.getJSONArray("intArray").getLongArray());
        assertEquals(3, json.getJSONArray("intArray").getLongList().size());
        assertArrayEquals(new double[]{10d, 20d, 30d}, json.getJSONArray("intArray").getDoubleArray());
        assertEquals(3, json.getJSONArray("intArray").getDoubleList().size());
        assertArrayEquals(new BigDecimal[]{new BigDecimal("10"), new BigDecimal("20"), new BigDecimal("30")}
                , json.getJSONArray("intArray").getBigDecimalArray());
        assertEquals(3, json.getJSONArray("intArray").getBigDecimalList().size());
        assertArrayEquals(new boolean[]{true,false,false}, json.getJSONArray("boolArray").getBooleanArray());
        assertEquals(3, json.getJSONArray("boolArray").getBooleanList().size());
        assertArrayEquals(new String[]{"yusuke", "yamamoto", "json"}, json.getJSONArray("stringArray").getStringArray());
        assertEquals(3, json.getJSONArray("stringArray").getStringList().size());
        assertEquals(3, json.getJSONArray("arrayArray").getJSONArrayArray().length);
        assertEquals(3, json.getJSONArray("arrayArray").getJSONArrayList().size());
        assertEquals(4, json.getJSONArray("arrayArray").getJSONArrayArray()[2].getIntArray()[1]);
        assertEquals(2, json.getJSONArray("objectArray").getJSONObjectArray().length);
        assertEquals(2, json.getJSONArray("objectArray").getJSONObjectList().size());
        assertEquals("yusuke", json.getJSONArray("objectArray").getJSONObjectArray()[0].getString("name"));

    }

    @Test
    public void testSimple() {
        String json = """
                {
                  "name": "Yusuke Yamamoto",
                  "address": {
                    "zip": "1700012"
                  }
                }""";

        @SuppressWarnings("unused") JSONObject jsonObject = JSON.parseObject(json);

    }

    @Test
    public void test() {
        String json = """
                {
                  "name": "Yusuke Yamamoto",
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
        assertEquals("Yusuke Yamamoto", jsonObject.getString("name"));
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
        assertNull(jsonObject.getJSONObject("notes"));
        JSONObject address = jsonObject.getJSONObject("address");
        assertNotNull(address);
        assertEquals("東京都中央区", address.getString("street"));
        assertEquals("San Francisco", address.getString("city"));
        assertEquals("CA", address.getString("state"));
        assertEquals("1700012", address.getString("zip"));

        assertThrows(IndexOutOfBoundsException.class, () -> hobbies.getString(3));

    }
    @Test
    void floatingPoint(){
        assertEquals(-31.2, JSON.parseObject("""
                {"age":-3.12E1}""").getDouble("age"));
        assertEquals(-31.2, JSON.parseObject("""
                {"age":-3.12E+1}""").getDouble("age"));
        assertEquals(-312, JSON.parseObject("""
                {"age":-3.12E+2}""").getDouble("age"));
        assertEquals(-0.0312, JSON.parseObject("""
                {"age":-3.12E-2}""").getDouble("age"));
        assertEquals(-0.0000312, JSON.parseObject("""
                {"age":-3.12E-5}""").getDouble("age"));
    }
}
