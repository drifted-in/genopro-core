/*
 * Copyright (c) 2019 Jan Tošovský <jan.tosovsky.cz@gmail.com>
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

public enum FamilyLineType {
    UNSPECIFIED,
    NO_MORE_CHILDREN,
    POSSIBLY_MORE_CHILDREN,
    TO_BE_COMPLETED;

    public static FamilyLineType parse(String familyLineType) {

        if (familyLineType == null) {
            return UNSPECIFIED;

        } else {
            return switch (familyLineType) {
                case "NoMoreChildren" -> NO_MORE_CHILDREN;
                case "PossiblyMoreChildren" -> POSSIBLY_MORE_CHILDREN;
                case "ToBeCompleted" -> TO_BE_COMPLETED;
                default -> UNSPECIFIED;
            };
        }
    }
}
