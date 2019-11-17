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

public class Individual implements Comparable<Individual> {

    private final String id;
    private final int key;
    private final GenoMap genoMap;
    private final Hyperlink hyperlink;
    private final Name name;
    private final Gender gender;
    private final Birth birth;
    private final Death death;
    private final boolean dead;
    private final boolean anonymized;
    private final Position position;
    private final BoundaryRect boundaryRect;
    private final Set<String> highlightKeySet;

    public Individual(String id, GenoMap genoMap, Hyperlink hyperlink, Name name, Gender gender,
            Birth birth, Death death, boolean dead, boolean anonymized, Position position,
            BoundaryRect boundaryRect, Set<String> highlightKeySet) {

        this.id = id;
        this.key = Integer.parseInt(id.replace("ind", ""));
        this.genoMap = genoMap;
        this.hyperlink = hyperlink;
        this.name = name;
        this.gender = gender;
        this.birth = birth;
        this.death = death;
        this.dead = dead;
        this.anonymized = anonymized;
        this.position = position;
        this.boundaryRect = boundaryRect;
        this.highlightKeySet = highlightKeySet;
    }

    @Override
    public int compareTo(Individual individual) {
        return Integer.compare(this.key, individual.key);
    }

    public String getId() {
        return id;
    }

    public GenoMap getGenoMap() {
        return genoMap;
    }

    public Hyperlink getHyperlink() {
        return hyperlink;
    }

    public Name getName() {
        return name;
    }

    public Gender getGender() {
        return gender;
    }

    public Birth getBirth() {
        return birth;
    }

    public Death getDeath() {
        return death;
    }

    public boolean isDead() {
        return dead;
    }

    public boolean isAnonymized() {
        return anonymized;
    }

    public Position getPosition() {
        return position;
    }

    public BoundaryRect getBoundaryRect() {
        return boundaryRect;
    }

    public Set<String> getHighlightKeySet() {
        return highlightKeySet;
    }

    public boolean isMale() {
        return gender == Gender.MALE;
    }

    public boolean isFemale() {
        return gender == Gender.FEMALE;
    }

}
