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
package in.drifted.tools.genopro.core.model;

import in.drifted.tools.genopro.core.util.formatter.DateFormatter;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;

public record GenoDate(String prefix, LocalDate localDate, String date, boolean yearOnly) {

    private static final DateTimeFormatter DATE_IN_FORMATTER
            = DateTimeFormatter.ofPattern("d MMM yyyy", Locale.ENGLISH);

    public static GenoDate fromDate(String date) {

        String prefix = null;
        LocalDate localDate = LocalDate.MAX;
        String normalizedDate = null;
        boolean yearOnly = false;

        if (date != null && !date.isEmpty()) {

            String rawPrefix = "";
            String firstChar = date.substring(0, 1);
            String modifiedDate = date;

            if ("<>~".contains(firstChar)) {
                rawPrefix = firstChar;
                modifiedDate = date.substring(1);
            }

            String[] dateFragments = modifiedDate.split(" ");

            prefix = rawPrefix;

            if (dateFragments.length == 3) {
                localDate = LocalDate.parse(modifiedDate, DATE_IN_FORMATTER);
                normalizedDate = date;

            } else {
                int year = Integer.parseInt(dateFragments[dateFragments.length - 1]);
                localDate = LocalDate.of(year, Month.DECEMBER, 31);
                normalizedDate = prefix + year;
                yearOnly = true;
            }
        }

        return new GenoDate(prefix, localDate, normalizedDate, yearOnly);
    }

    public String format(DateFormatter dateFormatter) {

        if (prefix != null) {

            String replacedPrefix = prefix;

            Map<String, String> prefixReplacementMap = dateFormatter.getPrefixReplacementMap();

            if (prefixReplacementMap != null && prefixReplacementMap.containsKey(prefix)) {
                replacedPrefix = prefixReplacementMap.get(prefix);
            }

            if (yearOnly) {
                return replacedPrefix + localDate.getYear();

            } else {
                return replacedPrefix + localDate.format(dateFormatter.getDateTimeFormatter());
            }

        } else {
            return date;
        }
    }

}
