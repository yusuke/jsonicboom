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

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JSONArrayTest {
    @Test
    public void test() {
        String json = """
                [1,2,-3]""";
        JSONArray jsonArray = JSON.parseArray(json);
        assertEquals(1, jsonArray.getInt(0));
        assertEquals(2, jsonArray.getInt(1));
        assertEquals(-3, jsonArray.getInt(2));
        assertEquals(json, jsonArray.toString());
    }

    @Test
    public void testNested() {
        String json = """
                [1,2,[-3,"foo","bar",-4,{"name":"yusuke"}]]""";
        JSONArray jsonArray = JSON.parseArray(json);
        assertEquals(1, jsonArray.getInt(0));
        assertEquals(2, jsonArray.getInt(1));
        JSONArray jsonArray1 = jsonArray.getJSONArray(2);
        assert jsonArray1 != null;
        assertEquals(5, jsonArray1.length());
        assertEquals(-3, jsonArray1.getInt(0));
        assertEquals("foo", jsonArray1.getString(1));
        assertEquals("bar", jsonArray1.getString(2));
        assertEquals(-4, jsonArray1.getInt(3));
        JSONObject jsonObject = jsonArray1.getJSONObject(4);
        assert jsonObject != null;
        assertEquals("""
                {"name":"yusuke"}""", jsonObject.toString());
        assertEquals("yusuke", jsonObject.getString("name"));
        assertEquals(json, jsonArray.toString());
        assertEquals("""
                [-3,"foo","bar",-4,{"name":"yusuke"}]""", jsonArray1.toString());
    }
}
