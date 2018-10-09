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
import java.time.Period;
import java.util.ResourceBundle;

public class BasicAgeFormatter implements AgeFormatter {

    private final String monthAbbrev;
    private final String dayAbbrev;

    public BasicAgeFormatter(ResourceBundle resourceBundle) {
        monthAbbrev = resourceBundle.getString("monthAbbrev");
        dayAbbrev = resourceBundle.getString("dayAbbrev");
    }

    @Override
    public String format(Birth birth, Death death) {

        String age = null;

        if (birth != null && birth.hasDate()) {

            Period period;

            if (death != null && death.hasDate()) {
                period = Period.between(birth.getDate().getLocalDate(), death.getDate().getLocalDate());

            } else {
                period = Period.between(birth.getDate().getLocalDate(), LocalDate.now());
            }

            StringBuilder ageBuilder = new StringBuilder();

            int years = period.getYears();

            if (years > 0) {
                ageBuilder.append(years);

            } else {
                int months = period.getMonths();

                if (months > 0) {
                    ageBuilder.append(months);
                    ageBuilder.append(monthAbbrev);

                } else {
                    ageBuilder.append(period.getDays());
                    ageBuilder.append(dayAbbrev);
                }
            }

            age = ageBuilder.toString();
        }

        return age;
    }

}
