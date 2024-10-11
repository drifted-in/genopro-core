/*
 * Copyright (c) 2018 Jan Tošovský <jan.tosovsky.cz@gmail.com>
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

import java.util.Set;

public record Individual(
        String id, int key, GenoMap genoMap, Hyperlink hyperlink, Name name, Gender gender,
        Birth birth, Death death, boolean dead, boolean anonymized, Position position,
        BoundaryRect boundaryRect, Set<String> highlightKeySet)
        implements Comparable<Individual> {

    @Override
    public int compareTo(Individual individual) {
        return Integer.compare(this.key, individual.key);
    }

    public boolean isMale() {
        return gender == Gender.MALE;
    }

    public boolean isFemale() {
        return gender == Gender.FEMALE;
    }

}
