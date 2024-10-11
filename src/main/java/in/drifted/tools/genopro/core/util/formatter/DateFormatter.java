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
package in.drifted.tools.genopro.core.util.formatter;

import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;

public class DateFormatter {

    private final DateTimeFormatter dateTimeFormatter;
    private final Map<String, String> prefixReplacementMap;

    public DateFormatter(DateTimeFormatter dateTimeFormatter, Map<String, String> prefixReplacementMap) {
        this.dateTimeFormatter = dateTimeFormatter;
        this.prefixReplacementMap = prefixReplacementMap;
    }

    public DateFormatter(String pattern, Locale locale, Map<String, String> prefixReplacementMap) {
        this.dateTimeFormatter = DateTimeFormatter.ofPattern(pattern, locale);
        this.prefixReplacementMap = prefixReplacementMap;
    }

    public DateTimeFormatter getDateTimeFormatter() {
        return dateTimeFormatter;
    }

    public Map<String, String> getPrefixReplacementMap() {
        return prefixReplacementMap;
    }

}
