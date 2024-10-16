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
package in.drifted.tools.genopro.core.model;

import java.util.List;

public record Family(
        String id, int key, String fatherId, String motherId, GenoMap genoMap, String label,
        FamilyRelationType familyRelationType, FamilyLineType familyLineType, List<FamilyEvent> familyEventList,
        List<PedigreeLink> pedigreeLinkList, Position position, BoundaryRect topBoundaryRect,
        BoundaryRect bottomBoundaryRect)
        implements Comparable<Family> {

    @Override
    public int compareTo(Family family) {
        return Integer.compare(this.key, family.key);
    }

}
