package one.cafebabe.jsonicboom;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JSONArrayTest {
    @Test
    public void test() {
        JSONArray jsonArray = JSON.parseArray("""
                [1,2,-3]
                """);
        assertEquals(1, jsonArray.getInt(0));
        assertEquals(2, jsonArray.getInt(1));
        assertEquals(-3, jsonArray.getInt(2));
    }
    @Test
    public void testNested() {
        JSONArray jsonArray = JSON.parseArray("""
                [1,2,[-3,"foo","bar",-4,{"name":"yusuke"}]]
                """);
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
        assertEquals("yusuke", jsonObject.getString("name"));
    }
}
