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

public final class JSON {
    private JSON() {
    }

    public static JSONObject parseObject(String json) {
        JSONTokenizer jsonTokenizer = new JSONTokenizer(json);
        JSONObject jsonObject = new JSONObject(jsonTokenizer, jsonTokenizer.next());
        if (jsonTokenizer.next() != null) {
            throw new IllegalJSONFormatException("Illegal JSON format");
        }
        return jsonObject;
    }

    public static JSONArray parseArray(String json) {
        JSONTokenizer jsonTokenizer = new JSONTokenizer(json);
        JSONArray jsonArray = new JSONArray(jsonTokenizer, jsonTokenizer.next());
        if (jsonTokenizer.next() != null) {
            throw new IllegalJSONFormatException("Illegal JSON format");
        }
        return jsonArray;
    }

}
