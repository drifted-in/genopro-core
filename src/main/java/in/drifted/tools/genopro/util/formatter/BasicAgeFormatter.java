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
package in.drifted.tools.genopro.util.formatter;

import in.drifted.tools.genopro.model.Birth;
import in.drifted.tools.genopro.model.Death;
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

        if (birth != null && birth.hasDate()) {

            Period period;

            if (death != null && death.hasDate()) {
                period = Period.between(birth.date().localDate(), death.date().localDate());

            } else {
                period = Period.between(birth.date().localDate(), LocalDate.now());
            }

            return getFormattedAge(period);
        }

        return null;
    }

    private String getFormattedAge(Period period) {

        StringBuilder formattedAgeBuilder = new StringBuilder();

        int years = period.getYears();

        if (years > 0) {
            formattedAgeBuilder.append(years);

        } else {
            int months = period.getMonths();

            if (months > 0) {
                formattedAgeBuilder.append(months);
                formattedAgeBuilder.append(monthAbbrev);

            } else {
                formattedAgeBuilder.append(period.getDays());
                formattedAgeBuilder.append(dayAbbrev);
            }
        }

        return formattedAgeBuilder.toString();
    }

}
