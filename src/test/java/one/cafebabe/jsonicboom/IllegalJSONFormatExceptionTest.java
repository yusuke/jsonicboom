package one.cafebabe.jsonicboom;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IllegalJSONFormatExceptionTest {

    @Test
    void composeMessage(){
        String message = IllegalJSONFormatException.composeMessage("expecting value, got '}'", """
                {"name": }
                """, 9);

        assertEquals("""
                expecting value, got '}'
                {"name": }
                         ^""", message);
    }
}