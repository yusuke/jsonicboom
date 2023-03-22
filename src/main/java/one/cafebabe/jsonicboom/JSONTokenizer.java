package one.cafebabe.jsonicboom;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;

public class JSONTokenizer {
    final String jsonString;
    private final Deque<JsonEventType> state = new ArrayDeque<>();

    private int currentIndex = 0;
    private char currentChar;
    private boolean afterKey = false;

    JSONTokenizer(String jsonString) {
        this.jsonString = jsonString;
    }

    public JsonIndices next() {
        if (currentIndex >= jsonString.length()) {
            return null;
        }
        skipWhitespaces();
        if (currentIndex >= jsonString.length()) {
            return null;
        }
        if (currentChar == ':' || currentChar == ',') {
            currentIndex++;
            skipWhitespaces();
        }

        int startIndex = currentIndex;

        switch (currentChar) {
            case '{':
                currentIndex++;
                afterKey = false;
                state.push(JsonEventType.START_OBJECT);
                return new JsonIndices(JsonEventType.START_OBJECT, startIndex, currentIndex);
            case '}':
                currentIndex++;
                state.pop();
                return new JsonIndices(JsonEventType.END_OBJECT, startIndex, currentIndex);
            case '[':
                currentIndex++;
                afterKey = false;
                state.push(JsonEventType.START_ARRAY);
                return new JsonIndices(JsonEventType.START_ARRAY, startIndex, currentIndex);
            case ']':
                currentIndex++;
                state.pop();
                return new JsonIndices(JsonEventType.END_ARRAY, startIndex, currentIndex);
            case '\"':
                currentIndex++;
                while (jsonString.charAt(currentIndex) != '\"' || jsonString.charAt(currentIndex - 1) == '\\') {
                    currentIndex++;
                }
                currentIndex++;
                if (afterKey ||
                    (state.getFirst() == JsonEventType.START_ARRAY)) {
                    afterKey = false;

                    return new JsonIndices(JsonEventType.VALUE_STRING, startIndex + 1, currentIndex - 1);
                } else {
                    afterKey = true;
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
                currentIndex++;
                while (jsonString.charAt(currentIndex) != ' ' &&
                       jsonString.charAt(currentIndex) != '\t' &&
                       jsonString.charAt(currentIndex) != '\n' &&
                       jsonString.charAt(currentIndex) != '\r' &&
                       jsonString.charAt(currentIndex) != ',' &&
                       jsonString.charAt(currentIndex) != ']' &&
                       jsonString.charAt(currentIndex) != '}'
                ) {
                    currentIndex++;
                }
                afterKey = false;
                return new JsonIndices(JsonEventType.VALUE_NUMBER, startIndex, currentIndex);
            case 't':
                currentIndex += 4;
                afterKey = false;
                return new JsonIndices(JsonEventType.VALUE_TRUE, startIndex, currentIndex);
            case 'f':
                currentIndex += 5;
                afterKey = false;
                return new JsonIndices(JsonEventType.VALUE_FALSE, startIndex, currentIndex);

            case 'n':
                currentIndex += 4;
                afterKey = false;
                return new JsonIndices(JsonEventType.VALUE_NULL, startIndex, currentIndex);

            default:
                throw new UnsupportedOperationException("Unsupported character found: " + currentChar);
        }
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
        KEY_NAME,
        VALUE_STRING,
        VALUE_NUMBER,
        VALUE_TRUE,
        VALUE_FALSE, VALUE_NULL
    }

    class JsonIndices {
        final JsonEventType jsonEventType;
        final int startIndex;
        final int endIndex;

        public JsonIndices(JsonEventType jsonEventType, int startIndex, int endIndex) {
            this.jsonEventType = jsonEventType;
            this.startIndex = startIndex;
            this.endIndex = endIndex;
        }

        public String getValue() {
            return unescapeString(jsonString.substring(startIndex, endIndex));
        }

        private String unescapeString(String input) {
            StringBuilder output = new StringBuilder();
            int length = input.length();
            boolean escape = false;

            for (int i = 0; i < length; i++) {
                char c = input.charAt(i);

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
                                String unicode = input.substring(i + 1, i + 5);
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
