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

public class Death implements EventDate {

    private final GenoDate date;
    private final String comment;

    public Death(GenoDate date, String comment) {
        this.date = date;
        this.comment = comment;
    }

    @Override
    public boolean hasDate() {
        return date != null && date.getDate() != null;
    }

    @Override
    public GenoDate getDate() {
        return date;
    }

    @Override
    public String getComment() {
        return comment;
    }

}
