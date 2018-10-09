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

import java.text.Normalizer;

public class GenoMap {

    private final String id;
    private final String name;
    private final String title;
    private final BoundaryRect boundaryRect;

    public GenoMap(String name, String title, BoundaryRect boundaryRect) {
        this.id = getId(title);
        this.name = name;
        this.title = title;
        this.boundaryRect = boundaryRect;
    }

    private static String getId(String title) {

        String id = null;

        if (title != null) {

            StringBuilder idBuilder = new StringBuilder();

            String normalizedTitle = Normalizer.normalize(title, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

            for (char c : normalizedTitle.toLowerCase().toCharArray()) {

                if ((c >= 48 && c <= 57) || (c >= 97 && c <= 122)) {
                    idBuilder.append(c);

                } else {
                    idBuilder.append(" ");
                }
            }

            id = idBuilder.toString().trim().replaceAll("\\s+", "-");
        }

        return id;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }

    public BoundaryRect getBoundaryRect() {
        return boundaryRect;
    }

}
