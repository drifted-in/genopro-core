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

import java.util.List;

public class Family {

    private final String id;
    private final String fatherId;
    private final String motherId;
    private final GenoMap genoMap;
    private final String label;
    private final int type;
    private final GenoDate date;
    private final String comment;
    private final List<PedigreeLink> pedigreeLinkList;
    private final Position position;
    private final BoundaryRect topBoundaryRect;
    private final BoundaryRect bottomBoundaryRect;

    public Family(String id, String fatherId, String motherId, GenoMap genoMap, String label, int type, GenoDate date,
            String comment, List<PedigreeLink> pedigreeLinkList, Position position,
            BoundaryRect topBoundaryRect, BoundaryRect bottomBoundaryRect) {

        this.id = id;
        this.fatherId = fatherId;
        this.motherId = motherId;
        this.genoMap = genoMap;
        this.label = label;
        this.type = type;
        this.date = date;
        this.comment = comment;
        this.pedigreeLinkList = pedigreeLinkList;
        this.position = position;
        this.topBoundaryRect = topBoundaryRect;
        this.bottomBoundaryRect = bottomBoundaryRect;
    }

    public String getId() {
        return id;
    }

    public String getFatherId() {
        return fatherId;
    }

    public String getMotherId() {
        return motherId;
    }

    public GenoMap getGenoMap() {
        return genoMap;
    }

    public String getLabel() {
        return label;
    }

    public int getType() {
        return type;
    }

    public GenoDate getDate() {
        return date;
    }

    public String getComment() {
        return comment;
    }

    public List<PedigreeLink> getPedigreeLinkList() {
        return pedigreeLinkList;
    }

    public Position getPosition() {
        return position;
    }

    public BoundaryRect getTopBoundaryRect() {
        return topBoundaryRect;
    }

    public BoundaryRect getBottomBoundaryRect() {
        return bottomBoundaryRect;
    }

}
