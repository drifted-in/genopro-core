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

    public static final int PARENT = 0;
    public static final int BIOLOGICAL = 1;
    public static final int ADOPTED = 2;

    private final String individualId;
    private final int type;
    private final Position position;
    private final Position twinPosition;

    public PedigreeLink(String individualId, String type, Position position, Position twinPosition) {
        this.individualId = individualId;
        this.type = getType(type);
        this.position = position;
        this.twinPosition = twinPosition;
    }

    public PedigreeLink(String individualId, int type, Position position, Position twinPosition) {
        this.individualId = individualId;
        this.type = type;
        this.position = position;
        this.twinPosition = twinPosition;
    }

    private int getType(String value) {

        switch (value) {
            case "Parent" : return PARENT;
            case "Adopted" : return ADOPTED;
            default: return BIOLOGICAL;
        }
    }

    public String getIndividualId() {
        return individualId;
    }

    public int getType() {
        return type;
    }

    public Position getPosition() {
        return position;
    }

    public Position getTwinPosition() {
        return twinPosition;
    }

}
