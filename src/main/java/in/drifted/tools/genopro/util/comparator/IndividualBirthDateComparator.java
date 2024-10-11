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
package in.drifted.tools.genopro.util.comparator;

import in.drifted.tools.genopro.model.Birth;
import in.drifted.tools.genopro.model.Individual;
import java.time.LocalDate;
import java.util.Comparator;

public class IndividualBirthDateComparator implements Comparator<Individual> {

    private boolean ascending = true;

    public IndividualBirthDateComparator(boolean ascending) {
        this.ascending = ascending;
    }

    @Override
    public int compare(Individual individual01, Individual individual02) {

        Birth birth01 = individual01.birth();
        Birth birth02 = individual02.birth();

        LocalDate birthDate01 = (birth01 != null && birth01.hasDate()) ? birth01.date().localDate() : LocalDate.MAX;
        LocalDate birthDate02 = (birth02 != null && birth02.hasDate()) ? birth02.date().localDate() : LocalDate.MAX;

        int result = birthDate01.compareTo(birthDate02);

        if (result == 0) {

            String last01 = (individual01.name().last() != null) ? individual01.name().last() : "";
            String last02 = (individual02.name().last() != null) ? individual02.name().last() : "";

            result = last01.compareTo(last02);
        }

        if (result == 0) {

            String first01 = (individual01.name().first() != null) ? individual01.name().first() : "";
            String first02 = (individual02.name().first() != null) ? individual02.name().first() : "";

            result = first01.compareTo(first02);
        }

        if (result == 0) {
            result = individual01.compareTo(individual02);
        }

        return ascending ? result : -result;
    }
}
