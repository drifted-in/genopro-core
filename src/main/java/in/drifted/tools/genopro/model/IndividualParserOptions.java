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

public class IndividualParserOptions {

    private final boolean resolvedHyperlinks;
    private final boolean unknownIndividuals;
    private final LocalDate anonymizedSinceDate;

    public IndividualParserOptions(boolean resolvedHyperlinks, boolean unknownIndividuals, LocalDate anonymizedSinceDate) {
        this.resolvedHyperlinks = resolvedHyperlinks;
        this.unknownIndividuals = unknownIndividuals;
        this.anonymizedSinceDate = anonymizedSinceDate;
    }

    public boolean getResolvedHyperlinks() {
        return resolvedHyperlinks;
    }

    public boolean getUnknownIndividuals() {
        return unknownIndividuals;
    }

    public LocalDate getAnonymizedSinceDate() {
        return anonymizedSinceDate;
    }

}
