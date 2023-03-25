package one.cafebabe.jsonicboom;

public class IllegalJSONFormatException extends RuntimeException {
    public IllegalJSONFormatException(String message, String jsonString, int index) {
        super(composeMessage(message, jsonString, index));
    }

    static String composeMessage(String message, String jsonString, int index) {
        int excerptStart = Math.max(0, index - 40);
        int excerptEnd = Math.min(index + 40, jsonString.length());
        String excerpt = jsonString.substring(excerptStart, excerptEnd);
        String indicator = "                                        ^".substring(40 - (index - excerptStart));

        // remove \n and \r from excerpt
        StringBuilder cleanedExcerpt = new StringBuilder();
        StringBuilder cleanedIndicator = new StringBuilder();
        for (int i = 0; i < excerpt.length(); i++) {
            char c = excerpt.charAt(i);
            if (c != '\n' && c != '\r') {
                cleanedExcerpt.append(c);
                if (i < indicator.length()) {
                    cleanedIndicator.append(indicator.charAt(i));
                }
            }
        }
        return String.format("%s\n%s\n%s", message, cleanedExcerpt, cleanedIndicator);
    }

    public IllegalJSONFormatException(String message) {
        super(message);
    }

}
