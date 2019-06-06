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

public class PedigreeLink {

    private final String individualId;
    private final PedigreeLinkType pedigreeLinkType;
    private final Position position;
    private final Position twinPosition;

    public PedigreeLink(String individualId, PedigreeLinkType pedigreeLinkType, Position position, Position twinPosition) {
        this.individualId = individualId;
        this.pedigreeLinkType = pedigreeLinkType;
        this.position = position;
        this.twinPosition = twinPosition;
    }

    public String getIndividualId() {
        return individualId;
    }

    public PedigreeLinkType getPedigreeLinkType() {
        return pedigreeLinkType;
    }

    public Position getPosition() {
        return position;
    }

    public Position getTwinPosition() {
        return twinPosition;
    }

    public boolean isParent() {
        return pedigreeLinkType == PedigreeLinkType.PARENT;
    }

}
