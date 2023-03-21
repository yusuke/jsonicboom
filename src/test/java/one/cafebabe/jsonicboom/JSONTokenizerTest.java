package one.cafebabe.jsonicboom;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JSONTokenizerTest {

    @Test
    public void testEmptyObject() {
        String jsonString = "{}";
        JSONTokenizer parser = new JSONTokenizer(jsonString);
        JSONTokenizer.JsonIndices next = parser.next();
        assertSubstring(jsonString, next, JSONTokenizer.JsonEventType.START_OBJECT, "{");
        next = parser.next();
        assertSubstring(jsonString, next, JSONTokenizer.JsonEventType.END_OBJECT, "}");
        assertNull(parser.next());
    }

    @Test
    public void testEmptyArray() {
        String jsonString = "[]";
        JSONTokenizer parser = new JSONTokenizer(jsonString);
        JSONTokenizer.JsonIndices next = parser.next();
        assertSubstring(jsonString, next, JSONTokenizer.JsonEventType.START_ARRAY, "[");
        next = parser.next();
        assertSubstring(jsonString, next, JSONTokenizer.JsonEventType.END_ARRAY, "]");
        assertNull(parser.next());
    }

    @Test
    public void testQuotedValue() {
        // { "name"	 	:
        //"\"John Smith\""
        //}
        String jsonString = "{ \"name\"\t \t:\r\"\\\"John Smith\\\"\"\n}  ";
        JSONTokenizer parser = new JSONTokenizer(jsonString);
        JSONTokenizer.JsonIndices next = parser.next();
        assertSubstring(jsonString, next, JSONTokenizer.JsonEventType.START_OBJECT, "{");
        next = parser.next();
        assertSubstring(jsonString, next, JSONTokenizer.JsonEventType.KEY_NAME, "name");
        next = parser.next();
        assertSubstring(jsonString, next, JSONTokenizer.JsonEventType.VALUE_STRING, "\\\"John Smith\\\"");
        next = parser.next();
        assertSubstring(jsonString, next, JSONTokenizer.JsonEventType.END_OBJECT, "}");
        next = parser.next();
        assertNull(next);
    }

    @Test
    public void testEscape() {
        String jsonString = """
                {"name": "Yusuke\\" \\u2603"}""";
        JSONTokenizer parser = new JSONTokenizer(jsonString);
        JSONTokenizer.JsonIndices next = parser.next();
        assertSubstring(jsonString, next, JSONTokenizer.JsonEventType.START_OBJECT, "{");
        next = parser.next();
        assertSubstring(jsonString, next, JSONTokenizer.JsonEventType.KEY_NAME, "name");
        next = parser.next();
        assertSubstring(jsonString, next, JSONTokenizer.JsonEventType.VALUE_STRING, "Yusuke\\\" \\u2603");
        next = parser.next();
        assertSubstring(jsonString, next, JSONTokenizer.JsonEventType.END_OBJECT, "}");
        next = parser.next();
        assertNull(next);
    }
    @Test
    public void testEmoji() {
        String jsonString = """
                {"name": "Yusuke👍👨‍👩‍👧‍👧"}""";
        JSONTokenizer parser = new JSONTokenizer(jsonString);
        JSONTokenizer.JsonIndices next = parser.next();
        assertSubstring(jsonString, next, JSONTokenizer.JsonEventType.START_OBJECT, "{");
        next = parser.next();
        assertSubstring(jsonString, next, JSONTokenizer.JsonEventType.KEY_NAME, "name");
        next = parser.next();
        assertSubstring(jsonString, next, JSONTokenizer.JsonEventType.VALUE_STRING, "Yusuke👍👨‍👩‍👧‍👧");
        next = parser.next();
        assertSubstring(jsonString, next, JSONTokenizer.JsonEventType.END_OBJECT, "}");
        next = parser.next();
        assertNull(next);
    }
    @Test
    public void testMultipleValues() {
        String jsonString = """
                { "name":"John", "age": 30, "isEmployee" : true,
                "isMarried" : false}""";
        JSONTokenizer parser = new JSONTokenizer(jsonString);
        JSONTokenizer.JsonIndices next = parser.next();
        assertSubstring(jsonString, next, JSONTokenizer.JsonEventType.START_OBJECT, "{");
        next = parser.next();
        assertSubstring(jsonString, next, JSONTokenizer.JsonEventType.KEY_NAME, "name");
        next = parser.next();
        assertSubstring(jsonString, next, JSONTokenizer.JsonEventType.VALUE_STRING, "John");
        next = parser.next();
        assertSubstring(jsonString, next, JSONTokenizer.JsonEventType.KEY_NAME, "age");
        next = parser.next();
        assertSubstring(jsonString, next, JSONTokenizer.JsonEventType.VALUE_NUMBER, "30");
        next = parser.next();
        assertSubstring(jsonString, next, JSONTokenizer.JsonEventType.KEY_NAME, "isEmployee");
        next = parser.next();
        assertSubstring(jsonString, next, JSONTokenizer.JsonEventType.VALUE_TRUE, "true");
        next = parser.next();
        assertSubstring(jsonString, next, JSONTokenizer.JsonEventType.KEY_NAME, "isMarried");
        next = parser.next();
        assertSubstring(jsonString, next, JSONTokenizer.JsonEventType.VALUE_FALSE, "false");
        next = parser.next();
        assertSubstring(jsonString, next, JSONTokenizer.JsonEventType.END_OBJECT, "}");
        next = parser.next();
        assertNull(next);
    }

    @Test
    public void testArray() {
        String jsonString = """
                { "name":"John", "hobbies": ["Archery", "Tennis"]}""";
        JSONTokenizer parser = new JSONTokenizer(jsonString);
        JSONTokenizer.JsonIndices next = parser.next();
        assertSubstring(jsonString, next, JSONTokenizer.JsonEventType.START_OBJECT, "{");
        next = parser.next();
        assertSubstring(jsonString, next, JSONTokenizer.JsonEventType.KEY_NAME, "name");
        next = parser.next();
        assertSubstring(jsonString, next, JSONTokenizer.JsonEventType.VALUE_STRING, "John");
        next = parser.next();
        assertSubstring(jsonString, next, JSONTokenizer.JsonEventType.KEY_NAME, "hobbies");
        next = parser.next();

        assertSubstring(jsonString, next, JSONTokenizer.JsonEventType.START_ARRAY, "[");
        next = parser.next();
        assertSubstring(jsonString, next, JSONTokenizer.JsonEventType.VALUE_STRING, "Archery");
        next = parser.next();
        assertSubstring(jsonString, next, JSONTokenizer.JsonEventType.VALUE_STRING, "Tennis");
        next = parser.next();
        assertSubstring(jsonString, next, JSONTokenizer.JsonEventType.END_ARRAY, "]");
        next = parser.next();
        assertSubstring(jsonString, next, JSONTokenizer.JsonEventType.END_OBJECT, "}");
        next = parser.next();
        assertNull(next);
    }

    @Test
    public void testNull() {
        String jsonString = """
                { "name":"John", "hobbies": null}""";
        JSONTokenizer parser = new JSONTokenizer(jsonString);
        JSONTokenizer.JsonIndices next = parser.next();
        assertSubstring(jsonString, next, JSONTokenizer.JsonEventType.START_OBJECT, "{");
        next = parser.next();
        assertSubstring(jsonString, next, JSONTokenizer.JsonEventType.KEY_NAME, "name");
        next = parser.next();
        assertSubstring(jsonString, next, JSONTokenizer.JsonEventType.VALUE_STRING, "John");
        next = parser.next();
        assertSubstring(jsonString, next, JSONTokenizer.JsonEventType.KEY_NAME, "hobbies");
        next = parser.next();
        assertSubstring(jsonString, next, JSONTokenizer.JsonEventType.VALUE_NULL, "null");
        next = parser.next();
        assertSubstring(jsonString, next, JSONTokenizer.JsonEventType.END_OBJECT, "}");
        next = parser.next();
        assertNull(next);
    }


    @Test
    public void testNestedObject() {
        String jsonString = """
                {"person": {"name": "John", "age": 30}}""";
        JSONTokenizer parser = new JSONTokenizer(jsonString);
        JSONTokenizer.JsonIndices next = parser.next();
        assertSubstring(jsonString, next, JSONTokenizer.JsonEventType.START_OBJECT, "{");
        next = parser.next();
        assertSubstring(jsonString, next, JSONTokenizer.JsonEventType.KEY_NAME, "person");
        next = parser.next();
        assertSubstring(jsonString, next, JSONTokenizer.JsonEventType.START_OBJECT, "{");
        next = parser.next();
        assertSubstring(jsonString, next, JSONTokenizer.JsonEventType.KEY_NAME, "name");
        next = parser.next();
        assertSubstring(jsonString, next, JSONTokenizer.JsonEventType.VALUE_STRING, "John");
        next = parser.next();
        assertSubstring(jsonString, next, JSONTokenizer.JsonEventType.KEY_NAME, "age");
        next = parser.next();
        assertSubstring(jsonString, next, JSONTokenizer.JsonEventType.VALUE_NUMBER, "30");
        next = parser.next();
        assertSubstring(jsonString, next, JSONTokenizer.JsonEventType.END_OBJECT, "}");
        next = parser.next();
        assertSubstring(jsonString, next, JSONTokenizer.JsonEventType.END_OBJECT, "}");
        next = parser.next();
        assertNull(next);
    }

    @Test
    public void testNestedArray() {
        String jsonString = "[[1, 2], [3, 4]]";
        JSONTokenizer parser = new JSONTokenizer(jsonString);
        JSONTokenizer.JsonIndices next = parser.next();
        assertSubstring(jsonString, next, JSONTokenizer.JsonEventType.START_ARRAY, "[");
        next = parser.next();
        assertSubstring(jsonString, next, JSONTokenizer.JsonEventType.START_ARRAY, "[");
        next = parser.next();
        assertSubstring(jsonString, next, JSONTokenizer.JsonEventType.VALUE_NUMBER, "1");
        next = parser.next();
        assertSubstring(jsonString, next, JSONTokenizer.JsonEventType.VALUE_NUMBER, "2");
        next = parser.next();
        assertSubstring(jsonString, next, JSONTokenizer.JsonEventType.END_ARRAY, "]");
        next = parser.next();
        assertSubstring(jsonString, next, JSONTokenizer.JsonEventType.START_ARRAY, "[");
        next = parser.next();
        assertSubstring(jsonString, next, JSONTokenizer.JsonEventType.VALUE_NUMBER, "3");
        next = parser.next();
        assertSubstring(jsonString, next, JSONTokenizer.JsonEventType.VALUE_NUMBER, "4");
        next = parser.next();
        assertSubstring(jsonString, next, JSONTokenizer.JsonEventType.END_ARRAY, "]");
        next = parser.next();
        assertSubstring(jsonString, next, JSONTokenizer.JsonEventType.END_ARRAY, "]");
        next = parser.next();
        assertNull(next);
    }
    @Test
    public void testArrayStringObject() {
        String jsonString = """
                {
                  "addresses": [
                    "170",
                    {
                      "foo": "bar"
                    }
                  ]
                }""";
        JSONTokenizer parser = new JSONTokenizer(jsonString);
        JSONTokenizer.JsonIndices next = parser.next();
        assertSubstring(jsonString, next, JSONTokenizer.JsonEventType.START_OBJECT, "{");
        next = parser.next();
        assertSubstring(jsonString, next, JSONTokenizer.JsonEventType.KEY_NAME, "addresses");
        next = parser.next();
        assertSubstring(jsonString, next, JSONTokenizer.JsonEventType.START_ARRAY, "[");
        next = parser.next();
        assertSubstring(jsonString, next, JSONTokenizer.JsonEventType.VALUE_STRING, "170");
        next = parser.next();
        assertSubstring(jsonString, next, JSONTokenizer.JsonEventType.START_OBJECT, "{");
        next = parser.next();
        assertSubstring(jsonString, next, JSONTokenizer.JsonEventType.KEY_NAME, "foo");
        next = parser.next();
        assertSubstring(jsonString, next, JSONTokenizer.JsonEventType.VALUE_STRING, "bar");
        next = parser.next();
        assertSubstring(jsonString, next, JSONTokenizer.JsonEventType.END_OBJECT, "}");
        next = parser.next();
        assertSubstring(jsonString, next, JSONTokenizer.JsonEventType.END_ARRAY, "]");
        next = parser.next();
        assertSubstring(jsonString, next, JSONTokenizer.JsonEventType.END_OBJECT, "}");
        next = parser.next();
        assertNull(next);
    }
    @Test
    public void testNestedArrayObject() {
        String jsonString = """
                [[1, 2], [3, {"person": {"name": "John", "age": 30}},4,"foo"]]""";
        JSONTokenizer parser = new JSONTokenizer(jsonString);
        JSONTokenizer.JsonIndices next = parser.next();
        assertSubstring(jsonString, next, JSONTokenizer.JsonEventType.START_ARRAY, "[");
        next = parser.next();
        assertSubstring(jsonString, next, JSONTokenizer.JsonEventType.START_ARRAY, "[");
        next = parser.next();
        assertSubstring(jsonString, next, JSONTokenizer.JsonEventType.VALUE_NUMBER, "1");
        next = parser.next();
        assertSubstring(jsonString, next, JSONTokenizer.JsonEventType.VALUE_NUMBER, "2");
        next = parser.next();
        assertSubstring(jsonString, next, JSONTokenizer.JsonEventType.END_ARRAY, "]");
        next = parser.next();
        assertSubstring(jsonString, next, JSONTokenizer.JsonEventType.START_ARRAY, "[");
        next = parser.next();
        assertSubstring(jsonString, next, JSONTokenizer.JsonEventType.VALUE_NUMBER, "3");
        next = parser.next();
        assertSubstring(jsonString, next, JSONTokenizer.JsonEventType.START_OBJECT, "{");
        next = parser.next();
        assertSubstring(jsonString, next, JSONTokenizer.JsonEventType.KEY_NAME, "person");
        next = parser.next();
        assertSubstring(jsonString, next, JSONTokenizer.JsonEventType.START_OBJECT, "{");
        next = parser.next();
        assertSubstring(jsonString, next, JSONTokenizer.JsonEventType.KEY_NAME, "name");
        next = parser.next();
        assertSubstring(jsonString, next, JSONTokenizer.JsonEventType.VALUE_STRING, "John");
        next = parser.next();
        assertSubstring(jsonString, next, JSONTokenizer.JsonEventType.KEY_NAME, "age");
        next = parser.next();
        assertSubstring(jsonString, next, JSONTokenizer.JsonEventType.VALUE_NUMBER, "30");
        next = parser.next();
        assertSubstring(jsonString, next, JSONTokenizer.JsonEventType.END_OBJECT, "}");
        next = parser.next();
        assertSubstring(jsonString, next, JSONTokenizer.JsonEventType.END_OBJECT, "}");
        next = parser.next();
        assertSubstring(jsonString, next, JSONTokenizer.JsonEventType.VALUE_NUMBER, "4");
        next = parser.next();
        assertSubstring(jsonString, next, JSONTokenizer.JsonEventType.VALUE_STRING, "foo");
        next = parser.next();
        assertSubstring(jsonString, next, JSONTokenizer.JsonEventType.END_ARRAY, "]");
        next = parser.next();
        assertSubstring(jsonString, next, JSONTokenizer.JsonEventType.END_ARRAY, "]");
        next = parser.next();
        assertNull(next);
    }

    @Test
    public void testKeyValueTypes() {
        String jsonString = """
                {"name": "John", "age": 30, "isStudent": false, "city": null, "score": 85.5, "isEmployee": true}""";
        JSONTokenizer parser = new JSONTokenizer(jsonString);

        JSONTokenizer.JsonIndices next = parser.next();
        assertSubstring(jsonString, next, JSONTokenizer.JsonEventType.START_OBJECT, "{");
        next = parser.next();
        assertSubstring(jsonString, next, JSONTokenizer.JsonEventType.KEY_NAME, "name");

        next = parser.next();
        assertSubstring(jsonString, next, JSONTokenizer.JsonEventType.VALUE_STRING, "John");
        next = parser.next();
        assertSubstring(jsonString, next, JSONTokenizer.JsonEventType.KEY_NAME, "age");

        next = parser.next();
        assertSubstring(jsonString, next, JSONTokenizer.JsonEventType.VALUE_NUMBER, "30");
        next = parser.next();
        assertSubstring(jsonString, next, JSONTokenizer.JsonEventType.KEY_NAME, "isStudent");
        next = parser.next();
        assertSubstring(jsonString, next, JSONTokenizer.JsonEventType.VALUE_FALSE, "false");
        next = parser.next();
        assertSubstring(jsonString, next, JSONTokenizer.JsonEventType.KEY_NAME, "city");
        next = parser.next();
        assertSubstring(jsonString, next, JSONTokenizer.JsonEventType.VALUE_NULL, "null");
        next = parser.next();
        assertSubstring(jsonString, next, JSONTokenizer.JsonEventType.KEY_NAME, "score");
        next = parser.next();
        assertSubstring(jsonString, next, JSONTokenizer.JsonEventType.VALUE_NUMBER, "85.5");
        next = parser.next();
        assertSubstring(jsonString, next, JSONTokenizer.JsonEventType.KEY_NAME, "isEmployee");
        next = parser.next();
        assertSubstring(jsonString, next, JSONTokenizer.JsonEventType.VALUE_TRUE, "true");
        next = parser.next();
        assertSubstring(jsonString, next, JSONTokenizer.JsonEventType.END_OBJECT, "}");
        next = parser.next();
        assertNull(next);
    }

    void assertSubstring(String jsonString, JSONTokenizer.JsonIndices indices, JSONTokenizer.JsonEventType type, String str) {
        assertSame(type, indices.jsonEventType, str);

        assertEquals(str, jsonString.substring(indices.startIndex, indices.endIndex));
    }
}
