package one.cafebabe.jsonicboom;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;

public class JSONTokenizer {
    final String jsonString;
    private final Deque<JsonEventType> state = new ArrayDeque<>();

    private int currentIndex = 0;
    private char currentChar;

    JSONTokenizer(String jsonString) {
        this.jsonString = jsonString;
    }

    void ensureFullyClosed() throws IllegalJSONFormatException {
        if (state.size() != 0) {
            throw new IllegalJSONFormatException("not closed", jsonString, currentIndex);
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
                if (isLastState(JsonEventType.COMMA) || isLastState(JsonEventType.COLON)) {
                    state.pop();
                }
                currentIndex++;
                state.push(JsonEventType.START_OBJECT);
                return new JsonIndices(JsonEventType.START_OBJECT, startIndex, currentIndex);
            case '}':
                if (isLastState(JsonEventType.COMMA) || isLastState(JsonEventType.COLON)) {
                    throw new IllegalJSONFormatException("expecting value, got '}'", jsonString, currentIndex);
                }
                if (isLastState(JsonEventType.START_ARRAY) || !isLastState(JsonEventType.START_OBJECT)) {
                    throw new IllegalJSONFormatException("expecting '{' or '[', got '}'", jsonString, currentIndex);
                }
                currentIndex++;
                state.pop();
                return new JsonIndices(JsonEventType.END_OBJECT, startIndex, currentIndex);
            case '[':
                currentIndex++;
                if (isLastState(JsonEventType.COMMA) || isLastState(JsonEventType.COLON)) {
                    state.pop();
                }
                state.push(JsonEventType.START_ARRAY);
                return new JsonIndices(JsonEventType.START_ARRAY, startIndex, currentIndex);
            case ']':
                if (isLastState(JsonEventType.COMMA) || isLastState(JsonEventType.COLON)) {
                    throw new IllegalJSONFormatException("expecting value, got ']'", jsonString, currentIndex);
                }
                if (isLastState(JsonEventType.START_OBJECT) || !isLastState(JsonEventType.START_ARRAY)) {
                    throw new IllegalJSONFormatException("expecting '{' or '[', got ']'", jsonString, currentIndex);
                }
                currentIndex++;
                state.pop();
                return new JsonIndices(JsonEventType.END_ARRAY, startIndex, currentIndex);

            case ':':
                currentIndex++;
                state.push(JsonEventType.COLON);
                return new JsonIndices(JsonEventType.COLON, startIndex, currentIndex);
            case ',':
                currentIndex++;
                state.push(JsonEventType.COMMA);
                return new JsonIndices(JsonEventType.COMMA, startIndex, currentIndex);
            case '\"':
                currentIndex++;
                char c1 = jsonString.charAt(currentIndex);
                do {
                    if (c1 == '\\') {
                        currentIndex++;
                        c1 = jsonString.charAt(currentIndex);
                        //'"' '\' '/' 'b' 'f' 'n' 'r' 't' 'u' hex hex hex hex
                        if ("\"\\/bfnrtu".indexOf(c1) != -1) {
                            if(c1 == 'u'){
                                if (jsonString.length() < currentIndex + 4) {
                                    throw new IllegalJSONFormatException("invalid escape sequence", jsonString, currentIndex);
                                }
                                for (int i = currentIndex + 1; i < currentIndex + 5; i++) {
                                    char hexChar = jsonString.charAt(i);
                                    if (!((hexChar >= '0' && hexChar <= '9') || (hexChar >= 'a' && hexChar <= 'f') || (hexChar >= 'A' && hexChar <= 'F'))) {
                                        throw new IllegalJSONFormatException("invalid escape sequence", jsonString, currentIndex);
                                    }
                                }
                                currentIndex += 4;
                            }else{
                                currentIndex++;
                            }
                        }else{
                            throw new IllegalJSONFormatException("invalid escape sequence", jsonString, currentIndex);
                        }
                        c1 = jsonString.charAt(currentIndex);

                    } else {
                        currentIndex++;
                        c1 = jsonString.charAt(currentIndex);
                    }
                } while (c1 != '\n' && c1 != '\"');
                currentIndex++;
                if (isLastState(JsonEventType.COLON)) {
                    state.pop();
                    return new JsonIndices(JsonEventType.VALUE_STRING, startIndex + 1, currentIndex - 1);
                } else if (isLastState(JsonEventType.COMMA)) {
                    state.pop();
                    if (isLastState(JsonEventType.START_ARRAY)) {
                        return new JsonIndices(JsonEventType.VALUE_STRING, startIndex + 1, currentIndex - 1);
                    } else {
                        return new JsonIndices(JsonEventType.KEY_NAME, startIndex + 1, currentIndex - 1);
                    }
                } else if (isLastState(JsonEventType.START_ARRAY)) {
                    return new JsonIndices(JsonEventType.VALUE_STRING, startIndex + 1, currentIndex - 1);
                } else {
                    return new JsonIndices(JsonEventType.KEY_NAME, startIndex + 1, currentIndex - 1);
                }
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
                int numberOfDecimalPoints = currentChar == '.' ? 1 : 0;
                currentIndex++;
                if (currentChar == '0' && jsonString.charAt(currentIndex) != '.') {
                    throw new IllegalJSONFormatException("leading zeros are not allowed", jsonString, currentIndex - 1);
                }
                while (jsonString.charAt(currentIndex) != ' ' &&
                       jsonString.charAt(currentIndex) != '\t' &&
                       jsonString.charAt(currentIndex) != '\n' &&
                       jsonString.charAt(currentIndex) != '\r' &&
                       jsonString.charAt(currentIndex) != ',' &&
                       jsonString.charAt(currentIndex) != ']' &&
                       jsonString.charAt(currentIndex) != '}'
                ) {
                    // current character must be number, comma
                    char c = jsonString.charAt(currentIndex);
                    if (c == '.') {
                        numberOfDecimalPoints++;
                        if (1 < numberOfDecimalPoints) {
                            throw new IllegalJSONFormatException("too many decimal points", jsonString, currentIndex);
                        }
                    }
                    if (c != '.' && !(c >= '0' && '9' >= c)) {
                        throw new IllegalJSONFormatException("expecting 'number', got '" + c + "'", jsonString, currentIndex);
                    }
                    currentIndex++;
                }
                if (isLastState(JsonEventType.COMMA) || isLastState(JsonEventType.COLON)) {
                    state.pop();
                }
                return new JsonIndices(JsonEventType.VALUE_NUMBER, startIndex, currentIndex);
            case 't':
                if (isLastState(JsonEventType.COMMA) || isLastState(JsonEventType.COLON)) {
                    state.pop();
                }
                if (jsonString.indexOf("rue", currentIndex + 1) == -1) {
                    throw new IllegalJSONFormatException("expecting 'true' got " + jsonString.substring(currentIndex, currentIndex + 4));
                }
                currentIndex += 4;
                return new JsonIndices(JsonEventType.VALUE_TRUE, startIndex, currentIndex);
            case 'f':
                if (isLastState(JsonEventType.COMMA) || isLastState(JsonEventType.COLON)) {
                    state.pop();
                }
                if (jsonString.indexOf("alse", currentIndex + 1) == -1) {
                    throw new IllegalJSONFormatException("expecting 'false' got " + jsonString.substring(currentIndex, currentIndex + 5));
                }

                currentIndex += 5;
                return new JsonIndices(JsonEventType.VALUE_FALSE, startIndex, currentIndex);
            case 'n':
                if (isLastState(JsonEventType.COMMA) || isLastState(JsonEventType.COLON)) {
                    state.pop();
                }
                if (jsonString.indexOf("ull", currentIndex + 1) == -1) {
                    throw new IllegalJSONFormatException("expecting 'null', got '" + jsonString.substring(currentIndex, currentIndex + 4) + "'", jsonString, currentIndex);
                }
                currentIndex += 4;
                return new JsonIndices(JsonEventType.VALUE_NULL, startIndex, currentIndex);

            default:
                throw new IllegalJSONFormatException("Unexpected character found: " + currentChar, jsonString, currentIndex);
        }
    }

    boolean isLastState(JsonEventType eventType) {
        return state.size() != 0 && state.getFirst() == eventType;
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
