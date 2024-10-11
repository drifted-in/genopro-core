/*
 * Copyright (c) 2021 Jan Tošovský <jan.tosovsky.cz@gmail.com>
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
package in.drifted.tools.genopro.model;

public record Color(int r, int g, int b, double a) {

    public static Color fromHex(String hex) {
        if (hex == null) {
            return new Color(0, 0, 0, 1.0);
        } else if (hex.equals("Transparent")) {
            return new Color(255, 255, 255, 0);
        } else {
            return new Color(
                    Integer.parseInt(hex.substring(1, 3), 16),
                    Integer.parseInt(hex.substring(3, 5), 16),
                    Integer.parseInt(hex.substring(5, 7), 16),
                    (hex.length() == 9) ? Integer.parseInt(hex.substring(7, 9), 16) / 255.0 : 1.0
            );
        }
    }

    public String toHex() {
        return (a < 1) ?
                String.format("#%02x%02x%02x%02x", r, g, b, (int) a * 255) :
                String.format("#%02x%02x%02x", r, g, b);
    }

}
