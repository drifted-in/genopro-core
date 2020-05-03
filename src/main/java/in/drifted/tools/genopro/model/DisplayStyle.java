/*
 * Copyright (c) 2020 Jan Tošovský <jan.tosovsky.cz@gmail.com>
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

public enum DisplayStyle {

    NOTHING,
    YEAR_OF_BIRTH_AND_YEAR_OF_DEATH,
    DATE_OF_BIRTH_AND_DATE_OF_DEATH,
    DATE_OF_BIRTH_AND_DATE_OF_DEATH_ON_SEPARATE_LINES,
    YEAR_OF_BIRTH_AND_YEAR_OF_DEATH_ID,
    DATE_OF_BIRTH_AND_DATE_OF_DEATH_ID,
    ID;

    public static DisplayStyle parse(String displayStyle) {

        if (displayStyle == null) {
            return NOTHING;

        } else {
            switch (displayStyle) {
                case "YoB_YoD":
                    return YEAR_OF_BIRTH_AND_YEAR_OF_DEATH;
                case "DoB_DoD":
                    return DATE_OF_BIRTH_AND_DATE_OF_DEATH;
                case "DoB_DoD_2lines":
                    return DATE_OF_BIRTH_AND_DATE_OF_DEATH_ON_SEPARATE_LINES;
                case "YoB_YoD_ID":
                    return YEAR_OF_BIRTH_AND_YEAR_OF_DEATH_ID;
                case "DoB_DoD_ID":
                    return DATE_OF_BIRTH_AND_DATE_OF_DEATH_ID;
                case "ID":
                    return ID;
                default:
                    return YEAR_OF_BIRTH_AND_YEAR_OF_DEATH;
            }
        }
    }
}
