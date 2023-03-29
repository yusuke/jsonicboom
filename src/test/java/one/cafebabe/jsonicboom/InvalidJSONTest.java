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

import static org.junit.jupiter.api.Assertions.assertThrows;

@SuppressWarnings("SpellCheckingInspection")
public class InvalidJSONTest   {
    @Test
    void extraClosingBrace(){
        assertThrows(IllegalJSONFormatException.class, () -> JSON.parseObject("""
                {}}"""));
        assertThrows(IllegalJSONFormatException.class, () -> JSON.parseObject("""
                }"""));
    }
    @Test
    void extraClosingSquareBracket(){
        assertThrows(IllegalJSONFormatException.class, () -> JSON.parseArray("""
                []]"""));
        assertThrows(IllegalJSONFormatException.class, () -> JSON.parseArray("""
                ]"""));
    }
    @Test
    void braceBracket(){
        assertThrows(IllegalJSONFormatException.class, () -> JSON.parseArray("""
                {]"""));
    }
    @Test
    void bracketBrace(){
        assertThrows(IllegalJSONFormatException.class, () -> JSON.parseArray("""
                [}"""));
    }


    // invalid object format
    @Test
    void incompleteJSONObject() {
        assertThrows(IllegalJSONFormatException.class, () -> JSON.parseObject("""
                {"key": "value"
                """));
    }

    @Test
    void missingQuotesInKeyJSONObject() {
        assertThrows(IllegalJSONFormatException.class, () -> JSON.parseObject("""
                {key: "value"}
                """));
    }

    @Test
    void extraCommaJSONObject() {
        assertThrows(IllegalJSONFormatException.class, () -> JSON.parseObject("""
                {"key1": "value1", "key2": "value2",}
                """));
    }
    @Test
    void extraColonJSONObject() {
        assertThrows(IllegalJSONFormatException.class, () -> JSON.parseObject("""
                {"key1": "value1", "key2":: "value2"}
                """));
    }
    @Test
    void startWithCommaJSONObject() {
        assertThrows(IllegalJSONFormatException.class, () -> JSON.parseObject("""
                ,{"key1": "value1", "key2": "value2",}
                """));
    }

    @Test
    void singleQuotesInKeyJSONObject() {
        assertThrows(IllegalJSONFormatException.class, () -> JSON.parseObject("""
                {'key': 'value'}
                """));
    }

    // invalid array format
    @Test
    void incompleteJSONArray() {
        assertThrows(IllegalJSONFormatException.class, () -> JSON.parseArray("""
                ["value1", "value2"
                """));
    }

    @Test
    void extraCommaJSONArray() {
        assertThrows(IllegalJSONFormatException.class, () -> JSON.parseArray("""
                ["value1", "value2",]
                """));
    }

    @Test
    void startWithCommaJSONArray() {
        assertThrows(IllegalJSONFormatException.class, () -> JSON.parseArray("""
                , "value2",]
                """));
    }

    // invalid string
    @Test
    void singleQuotesInString() {
        assertThrows(IllegalJSONFormatException.class, () -> JSON.parseObject("""
                {"key": 'This is a string'}
                """));
    }

    @Test
    void unescapedControlCharacterInString() {
        assertThrows(IllegalJSONFormatException.class, () -> JSON.parseObject("""
                {"key": "This is a string with a newline
                "}
                """));
    }
    @Test
    void invalidEscapeSequence(){
        //'"' '\' '/' 'b' 'f' 'n' 'r' 't' 'u' hex hex hex hex
        assertThrows(IllegalJSONFormatException.class, () -> JSON.parseObject("""
                {"key": "\\a"}
                """));
        assertThrows(IllegalJSONFormatException.class, () -> JSON.parseObject("""
                {"key": "\\c"}
                """));
        assertThrows(IllegalJSONFormatException.class, () -> JSON.parseObject("""
                {"key": "\\u"}
                """));
        assertThrows(IllegalJSONFormatException.class, () ->         JSON.parseObject("""
                {"key": "\\\\""}
                """));

        JSON.parseObject("""
                {"key": "\\""}
                """);
        JSON.parseObject("""
                {"key": "\\b"}
                """);
        JSON.parseObject("""
                {"key": "\\f"}
                """);
        JSON.parseObject("""
                {"key": "\\n"}
                """);
        JSON.parseObject("""
                {"key": "\\r"}
                """);
        JSON.parseObject("""
                {"key": "\\t"}
                """);
        JSON.parseObject("""
                {"key": "\\u0000"}
                """);

    }

    // invalid number
    @Test
    void numberWithLeadingZero() {
        assertThrows(IllegalJSONFormatException.class, () -> JSON.parseObject("""
                {"key": 0123}
                """));
    }
    @Test
    void tooManyDecimalPoints() {
        assertThrows(IllegalJSONFormatException.class, () -> JSON.parseObject("""
                {"key": 0.1.23}
                """));
    }

    @Test
    void numberWithPositiveSign() {
        assertThrows(IllegalJSONFormatException.class, () -> JSON.parseObject("""
                {"key": +42}
                """));
    }

    @Test
    void numberWithInvalidCharacters() {
        assertThrows(IllegalJSONFormatException.class, () -> JSON.parseObject("""
                {"key": 123abc}
                """));
    }

    // invalid keyword
    @Test
    void keywordWithCapitalLetters() {
        assertThrows(IllegalJSONFormatException.class, () -> JSON.parseObject("""
                {"key": True}
                """));
    }

    @Test
    void keywordWithTypo() {
        assertThrows(IllegalJSONFormatException.class, () -> JSON.parseObject("""
                {"key": ture}
                """));
    }
    @Test
    void keywordWithTypoNull() {
        assertThrows(IllegalJSONFormatException.class, () -> JSON.parseObject("""
                {"key": nil}
                """));
        assertThrows(IllegalJSONFormatException.class, () -> JSON.parseObject("""
                {"key": nill}
                """));
    }

    // invalid syntax
    @Test
    void unclosedBracket() {
        assertThrows(IllegalJSONFormatException.class, () -> JSON.parseArray("""
                [1, 2, 3
                """));
    }

    @Test
    void unnecessaryComma() {
        assertThrows(IllegalJSONFormatException.class, () -> JSON.parseObject("""
                {,"key1": "value1","key2": "value2"}
                """));
        assertThrows(IllegalJSONFormatException.class, () -> JSON.parseObject("""
                {"key1": "value1",, "key2": "value2"}
                """));
        assertThrows(IllegalJSONFormatException.class, () -> JSON.parseObject("""
                {"key1": "value1", "key2": "value2",}
                """));
        assertThrows(IllegalJSONFormatException.class, () -> JSON.parseObject("""
                [,"value1","value2"}
                """));
        assertThrows(IllegalJSONFormatException.class, () -> JSON.parseObject("""
                ["value1",,"value2"}
                """));
        assertThrows(IllegalJSONFormatException.class, () -> JSON.parseObject("""
                ["value1","value2",}
                """));
    }
    @Test
    void withoutComma() {
        assertThrows(IllegalJSONFormatException.class, () -> JSON.parseObject("""
                {"key1": "value1" "key2": "value2"}
                """));
        assertThrows(IllegalJSONFormatException.class, () -> JSON.parseObject("""
                ["value1"  "value2"
                """));
    }

    @Test
    void invalidUnicodeEscapeSequence() {
        assertThrows(IllegalJSONFormatException.class, () -> JSON.parseObject("""
                {"key": "\\u12G6"}
                """));
    }}




