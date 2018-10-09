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

import java.util.Comparator;

public class HorizontalPositionComparator implements Comparator<Individual> {

    private boolean ascending = true;

    public HorizontalPositionComparator(boolean ascending) {
        this.ascending = ascending;
    }

    @Override
    public int compare(Individual o1, Individual o2) {
        int result = Integer.compare(o1.getBoundaryRect().getTopLeft().getX(), o2.getBoundaryRect().getTopLeft().getX());
        return ascending ? result : -result;
    }
}
