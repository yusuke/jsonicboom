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

import org.jetbrains.annotations.NotNull;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.Objects;

public class JSONTokenizer {
    final String jsonString;
    private final JsonNestState state = new JsonNestState();

    class JsonNestState {

        boolean insideObject = false;
        boolean insideArray = false;
        private final Deque<JsonEventType> queue = new ArrayDeque<>();

        void push(JsonEventType eventType) {
            switch (eventType) {
                case START_OBJECT:
                    if (insideArray) {
                        ensurePreviousTokenIs(false, JsonEventType.START_ARRAY, JsonEventType.COMMA);
                    } else {
                        ensurePreviousTokenIs(true, JsonEventType.COLON);
                    }
                    insideArray = false;
                    insideObject = true;
                    break;
                case END_OBJECT:
                    if (insideArray) {
                        throw new IllegalJSONFormatException("Illegal JSON format. Expecting ']', got '}'", jsonString, currentIndex);
                    }
                    if (!insideObject) {
                        throw new IllegalJSONFormatException("Illegal JSON format. Got '}' without '{'", jsonString, currentIndex);
                    }
                    ensurePreviousTokenIs(false,
                            JsonEventType.START_OBJECT,
                            JsonEventType.VALUE_STRING,
                            JsonEventType.VALUE_NUMBER,
                            JsonEventType.VALUE_TRUE,
                            JsonEventType.VALUE_FALSE,
                            JsonEventType.VALUE_NULL);
                    // remove stack until START_OBJECT event
                    //noinspection StatementWithEmptyBody
                    while (queue.pop() != JsonEventType.START_OBJECT) {
                        // do nothing
                    }
                    if (isLastToken(JsonEventType.COLON) || isLastToken(JsonEventType.COMMA)) {
                        // put VALUE_STRING instead of OBJECT
                        queue.push(JsonEventType.VALUE_STRING);
                    }
                    checkInsideObjectOrArray();
                    break;
                case START_ARRAY:
                    if (insideArray) {
                        ensurePreviousTokenIs(false, JsonEventType.START_ARRAY, JsonEventType.COMMA);
                    } else {
                        ensurePreviousTokenIs(true, JsonEventType.COLON);
                    }
                    insideObject = false;
                    insideArray = true;
                    break;
                case END_ARRAY:
                    if (insideObject) {
                        throw new IllegalJSONFormatException("Illegal JSON format. Expecting '}', got ']'", jsonString, currentIndex);
                    }
                    if (!insideArray) {
                        throw new IllegalJSONFormatException("Illegal JSON format. Got ']' without '['", jsonString, currentIndex);
                    }
                    ensurePreviousTokenIs(false,
                            JsonEventType.START_ARRAY,
                            JsonEventType.VALUE_STRING,
                            JsonEventType.VALUE_NUMBER,
                            JsonEventType.VALUE_TRUE,
                            JsonEventType.VALUE_FALSE,
                            JsonEventType.VALUE_NULL);

                    // remove stack until START_ARRAY event
                    //noinspection StatementWithEmptyBody
                    while (queue.pop() != JsonEventType.START_ARRAY) {
                        // do nothing
                    }
                    if (isLastToken(JsonEventType.COLON) || isLastToken(JsonEventType.COMMA)) {
                        // put VALUE_STRING instead of OBJECT
                        queue.push(JsonEventType.VALUE_STRING);
                    }
                    checkInsideObjectOrArray();

                    break;
                case COMMA:
                    if (insideArray) {
                        ensurePreviousTokenIs(false,
                                JsonEventType.START_ARRAY,
                                JsonEventType.VALUE_STRING,
                                JsonEventType.VALUE_NUMBER,
                                JsonEventType.VALUE_TRUE,
                                JsonEventType.VALUE_FALSE,
                                JsonEventType.VALUE_NULL);
                    } else if (insideObject) {
                        ensurePreviousTokenIs(false,
                                JsonEventType.VALUE_STRING,
                                JsonEventType.VALUE_NUMBER,
                                JsonEventType.VALUE_TRUE,
                                JsonEventType.VALUE_FALSE,
                                JsonEventType.VALUE_NULL);
                    } else {
                        throw new IllegalJSONFormatException("Illegal JSON format", jsonString, currentIndex);
                    }
                    break;
                case COLON:
                    ensurePreviousTokenIs(false, JsonEventType.KEY_NAME);
                    break;
                case KEY_NAME:
                    if (!insideObject) {
                        throw new IllegalJSONFormatException("Illegal JSON format", jsonString, currentIndex);
                    }
                    ensurePreviousTokenIs(false, JsonEventType.START_OBJECT, JsonEventType.COMMA);
                    break;
                case VALUE_STRING:
                case VALUE_NUMBER:
                case VALUE_TRUE:
                case VALUE_FALSE:
                case VALUE_NULL:
                    if (insideArray) {
                        ensurePreviousTokenIs(false, JsonEventType.START_ARRAY, JsonEventType.COMMA);
                    } else if (insideObject) {
                        ensurePreviousTokenIs(false, JsonEventType.COLON);
                    } else {
                        throw new IllegalJSONFormatException("Illegal JSON format", jsonString, currentIndex);
                    }
                    queue.push(JsonEventType.VALUE_STRING);
                    break;
            }
            if (eventType != JsonEventType.END_OBJECT && eventType != JsonEventType.END_ARRAY) {
                queue.push(eventType);
            }

        }

        private void checkInsideObjectOrArray() {
            insideObject = false;
            insideArray = false;
            if (queue.size() == 0) {
                return;
            }
            Iterator<JsonEventType> iterator = queue.iterator();
            JsonEventType next;
            while (null != (next = iterator.next())) {
                if (next == JsonEventType.START_OBJECT) {
                    insideObject = true;
                    break;
                }
                if (next == JsonEventType.START_ARRAY) {
                    insideArray = true;
                    break;
                }
            }
        }

        void ensurePreviousTokenIs(boolean acceptEmpty, JsonEventType... eventTypes) {
            if (queue.size() == 0) {
                if (acceptEmpty) {
                    return;
                } else {
                    throw new IllegalJSONFormatException("Illegal JSON format.", jsonString, currentIndex);
                }
            }
            for (JsonEventType eventType : eventTypes) {
                if (queue.getFirst() == eventType) {
                    return;
                }
            }
            throw new IllegalJSONFormatException("Illegal JSON format.", jsonString, currentIndex);
        }

        public int size() {
            return queue.size();
        }

        boolean isLastToken(JsonEventType eventType) {
            return queue.size() != 0 && queue.getFirst() == eventType;
        }

    }

    private int currentIndex = 0;
    private char currentChar;

    JSONTokenizer(String jsonString) {
        this.jsonString = jsonString;
    }

    void ensureFullyClosed() throws IllegalJSONFormatException {
        if (state.size() != 0) {
            if (state.insideObject) {
                throw new IllegalJSONFormatException("Unexpected end of JSON string: expected '}' but not found.", jsonString, currentIndex);
            } else {
                throw new IllegalJSONFormatException("Unexpected end of JSON string: expected ']' but not found.", jsonString, currentIndex);
            }
        }
    }

    public JsonIndices next() {
        if (currentIndex >= jsonString.length()) {
            ensureFullyClosed();
            return null;
        }
        skipWhitespaces();
        if (currentIndex >= jsonString.length()) {
            ensureFullyClosed();
            return null;
        }

        int startIndex = currentIndex;
        switch (currentChar) {
            case '{':
                return checkTokenOrderValidity(startIndex, JsonEventType.START_OBJECT, ++currentIndex);
            case '}':
                return checkTokenOrderValidity(startIndex, JsonEventType.END_OBJECT, ++currentIndex);
            case '[':
                return checkTokenOrderValidity(startIndex, JsonEventType.START_ARRAY, ++currentIndex);
            case ']':
                return checkTokenOrderValidity(startIndex, JsonEventType.END_ARRAY, ++currentIndex);
            case ':':
                return checkTokenOrderValidity(startIndex, JsonEventType.COLON, ++currentIndex);
            case ',':
                return checkTokenOrderValidity(startIndex, JsonEventType.COMMA, ++currentIndex);
            case '\"':
                do {
                    if (getNextChar() == '\\') {
                        //'"' '\' '/' 'b' 'f' 'n' 'r' 't' 'u' hex hex hex hex
                        if (getNextChar() == 'u') {
                            for (int i = 0; i < 4; i++) {
                                getNextChar();
                                if (!((currentChar >= '0' && currentChar <= '9') || (currentChar >= 'a' && currentChar <= 'f') || (currentChar >= 'A' && currentChar <= 'F'))) {
                                    throw new IllegalJSONFormatException(String.format("Invalid escape sequence. Expecting [a-fA-F0-9], got '%s'", currentChar), jsonString, currentIndex);
                                }
                            }
                        } else if ("\"\\/bfnrtu".indexOf(currentChar) == -1) {
                            throw new IllegalJSONFormatException(String.format("Invalid escape sequence. Expecting [\"\\/bfnrtu], got '%s'", currentChar), jsonString, currentIndex);
                        }
                        getNextChar();
                    }
                } while (currentChar != '\n' && currentChar != '\"');
                return checkTokenOrderValidity(startIndex + 1,
                        state.isLastToken(JsonEventType.COLON) || state.insideArray ? JsonEventType.VALUE_STRING : JsonEventType.KEY_NAME
                        , currentIndex++);
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
            case '.':
            case '-':
                boolean decimalPointAlreadyFound = currentChar == '.';
                boolean checkNextCharIsDecimalPoint = currentChar == '0';
                boolean exponentFound = false;
                boolean isFloatingPointNumber = false;
                while (" \t\n\r,]}".indexOf(getNextChar()) == -1) {
                    // current character must be number, comma
                    if (currentChar == '.') {
                        if (decimalPointAlreadyFound) {
                            throw new IllegalJSONFormatException("Too many decimal points.", jsonString, currentIndex);
                        }
                        decimalPointAlreadyFound = true;
                        checkNextCharIsDecimalPoint = false;
                    } else if(currentChar == 'E' || currentChar == 'e'){
                        if (exponentFound) {
                            throw new IllegalJSONFormatException("Invalid number expression.", jsonString, currentIndex);
                        }
                        exponentFound = true;
                    }else if(currentChar == '-' || currentChar == '+'){
                        if (isFloatingPointNumber || !exponentFound) {
                            throw new IllegalJSONFormatException("Invalid number expression.", jsonString, currentIndex);
                        }
                        isFloatingPointNumber = true;
                    } else if (checkNextCharIsDecimalPoint) {
                        throw new IllegalJSONFormatException("Leading zeros are not allowed.", jsonString, currentIndex - 1);
                    }else if(currentChar >= '0' && '9' >= currentChar){
                        if(exponentFound && !isFloatingPointNumber){
                            isFloatingPointNumber = true;
                        }
                    } else {
                        throw new IllegalJSONFormatException("Expecting 'number', got '" + currentChar + "'.", jsonString, currentIndex);
                    }
                }
                if (exponentFound && !isFloatingPointNumber) {
                    throw new IllegalJSONFormatException("Invalid number expression.", jsonString, currentIndex);
                }
                return checkTokenOrderValidity(startIndex, JsonEventType.VALUE_NUMBER, currentIndex);
            case 't':
                return checkToken("true", startIndex, JsonEventType.VALUE_TRUE);
            case 'f':
                return checkToken("false", startIndex, JsonEventType.VALUE_FALSE);
            case 'n':
                return checkToken("null", startIndex, JsonEventType.VALUE_NULL);
            default:
                throw new IllegalJSONFormatException(String.format("Unexpected character found: '%s'.", currentChar), jsonString, currentIndex);
        }
    }

    private char getNextChar() {
        try {
            return currentChar = jsonString.charAt(++currentIndex);
        }catch(StringIndexOutOfBoundsException e){
            throw new IllegalJSONFormatException("Unexpected end of JSON.", jsonString, currentIndex - 1);
        }
    }

    @NotNull
    private JsonIndices checkTokenOrderValidity(int startIndex, JsonEventType eventType, int endIndex) {
        state.push(eventType);
        return new JsonIndices(eventType, startIndex, endIndex);
    }

    JsonIndices checkToken(String expectedToken, int startIndex, JsonEventType successEventType) {
        if (jsonString.indexOf(expectedToken, currentIndex++) == -1) {
            throw new IllegalJSONFormatException(String.format("Expecting '%s', got '%s'.",
                    expectedToken,
                    jsonString.substring(currentIndex - 1, currentIndex + expectedToken.length() - 1)), jsonString, currentIndex);
        }
        currentIndex += expectedToken.length() - 1;
        return checkTokenOrderValidity(startIndex, successEventType, currentIndex);
    }

    private void skipWhitespaces() {
        currentChar = jsonString.charAt(currentIndex);
        while (currentChar == '\r' ||
                currentChar == '\n' ||
                currentChar == '\t' ||
                currentChar == ' '
        ) {
            currentIndex++;
            if (currentIndex >= jsonString.length()) {
                break;
            }
            currentChar = jsonString.charAt(currentIndex);
        }
    }

    public enum JsonEventType {
        START_OBJECT,
        END_OBJECT,
        START_ARRAY,
        END_ARRAY,
        COMMA,
        COLON,
        KEY_NAME,
        VALUE_STRING,
        VALUE_NUMBER,
        VALUE_TRUE,
        VALUE_FALSE, VALUE_NULL
    }

    public final class JsonIndices {
        final JsonEventType jsonEventType;
        final int startIndex;
        final int endIndex;
        private String value = null;

        JsonIndices(JsonEventType jsonEventType, int startIndex, int endIndex) {
            this.jsonEventType = jsonEventType;
            this.startIndex = startIndex;
            this.endIndex = endIndex;
        }

        public String getValue() {
            if (jsonEventType == JsonEventType.VALUE_NULL) {
                return null;
            }
            if (value == null) {
                value = unescapeString(startIndex, endIndex);
            }
            return value;
        }

        private String unescapeString(int startIndex, int endIndex) {
            StringBuilder output = new StringBuilder();
            boolean escape = false;

            for (int i = startIndex; i < endIndex; i++) {
                char c = jsonString.charAt(i);

                if (!escape && c == '\\') {
                    escape = true;
                } else {
                    if (escape) {
                        escape = false;
                        switch (c) {
                            case '\"':
                                output.append('\"');
                                break;
                            case '\\':
                                output.append('\\');
                                break;
                            case '/':
                                output.append('/');
                                break;
                            case 'b':
                                output.append('\b');
                                break;
                            case 'f':
                                output.append('\f');
                                break;
                            case 'n':
                                output.append('\n');
                                break;
                            case 'r':
                                output.append('\r');
                                break;
                            case 't':
                                output.append('\t');
                                break;
                            case 'u':
                                String unicode = jsonString.substring(i + 1, i + 5);
                                int unicodeValue = Integer.parseInt(unicode, 16);
                                output.append((char) unicodeValue);
                                i += 4;
                                break;
                            default:
                                break;
                        }
                    } else {
                        output.append(c);
                    }
                }
            }

            return output.toString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            JsonIndices that = (JsonIndices) o;
            return startIndex == that.startIndex && endIndex == that.endIndex && jsonEventType == that.jsonEventType;
        }

        @Override
        public int hashCode() {
            return Objects.hash(jsonEventType, startIndex, endIndex);
        }

        @Override
        public String toString() {
            return "JsonIndices{" +
                    "jsonEventType=" + jsonEventType +
                    ", startIndex=" + startIndex +
                    ", endIndex=" + endIndex +
                    '}';
        }
    }
}
