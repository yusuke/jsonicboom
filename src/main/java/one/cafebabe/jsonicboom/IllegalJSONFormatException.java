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
