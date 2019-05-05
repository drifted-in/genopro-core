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

import java.util.Collection;

public class GenoMapData {

    private final GenoMap genoMap;
    private final Collection<Individual> individualCollection;
    private final Collection<Family> familyCollection;
    private final Collection<Label> labelCollection;

    public GenoMapData(GenoMap genoMap, Collection<Individual> individualCollection,
            Collection<Family> familyCollection, Collection<Label> labelCollection) {

        this.genoMap = genoMap;
        this.individualCollection = individualCollection;
        this.familyCollection = familyCollection;
        this.labelCollection = labelCollection;
    }

    public GenoMap getGenoMap() {
        return genoMap;
    }

    public Collection<Individual> getIndividualCollection() {
        return individualCollection;
    }

    public Collection<Family> getFamilyCollection() {
        return familyCollection;
    }

    public Collection<Label> getLabelCollection() {
        return labelCollection;
    }

}
