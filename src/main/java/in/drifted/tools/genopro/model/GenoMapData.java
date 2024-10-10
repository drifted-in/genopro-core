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

public class GenoMapData {

    private final GenoMap genoMap;
    private final Set<Individual> individualSet;
    private final Set<Family> familySet;
    private final Set<Label> labelSet;

    public GenoMapData(GenoMap genoMap, Set<Individual> individualSet,
            Set<Family> familySet, Set<Label> labelSet) {

        this.genoMap = genoMap;
        this.individualSet = individualSet;
        this.familySet = familySet;
        this.labelSet = labelSet;
    }

    public GenoMap getGenoMap() {
        return genoMap;
    }

    public Set<Individual> getIndividualSet() {
        return individualSet;
    }

    public Set<Family> getFamilySet() {
        return familySet;
    }

    public Set<Label> getLabelSet() {
        return labelSet;
    }

}
