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

public class FamilyEvent {

    public static final int MARRIAGE = 0;
    public static final int DIVORCE = 1;

    private final int type;
    private final GenoDate date;
    private final String place;
    private final String comment;

    public FamilyEvent(int type, GenoDate date, String place, String comment) {
        this.type = type;
        this.date = date;
        this.place = place;
        this.comment = comment;
    }

    public int getType() {
        return type;
    }

    public GenoDate getDate() {
        return date;
    }

    public String getPlace() {
        return place;
    }

    public String getComment() {
        return comment;
    }

}
