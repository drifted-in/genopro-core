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

import java.time.LocalDate;
import java.util.Comparator;

public class IndividuaBirthDateComparator implements Comparator<Individual> {

    private boolean ascending = true;

    public IndividuaBirthDateComparator(boolean ascending) {
        this.ascending = ascending;
    }

    @Override
    public int compare(Individual individual01, Individual individual02) {

        Birth birth01 = individual01.getBirth();
        Birth birth02 = individual02.getBirth();

        LocalDate birthDate01 = (birth01 != null && birth01.hasDate()) ? birth01.getDate().getLocalDate() : LocalDate.MAX;
        LocalDate birthDate02 = (birth02 != null && birth02.hasDate()) ? birth02.getDate().getLocalDate() : LocalDate.MAX;

        int result = birthDate01.compareTo(birthDate02);

        if (result == 0) {

            String last01 = (individual01.getName().getLast() != null) ? individual01.getName().getLast() : "";
            String last02 = (individual02.getName().getLast() != null) ? individual02.getName().getLast() : "";

            result = last01.compareTo(last02);
        }

        if (result == 0) {

            String first01 = (individual01.getName().getFirst() != null) ? individual01.getName().getFirst() : "";
            String first02 = (individual02.getName().getFirst() != null) ? individual02.getName().getFirst() : "";

            result = first01.compareTo(first02);
        }

        if (result == 0) {
            result = individual01.compareTo(individual02);
        }

        return ascending ? result : -result;
    }
}
