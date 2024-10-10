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

public class ParserOptions {

    private boolean textLabelsExcluded = false;
    private boolean untitledGenoMapsExcluded = false;
    private boolean unknownIndividualsExcluded = false;
    private boolean hyperlinkedIndividualInstancesDeduplicated = false;
    private LocalDate anonymizedSinceDate = null;

    public boolean hasTextLabelsExcluded() {
        return textLabelsExcluded;
    }

    public void setTextLabelsExcluded(boolean textLabelsExcluded) {
        this.textLabelsExcluded = textLabelsExcluded;
    }

    public boolean hasUntitledGenoMapsExcluded() {
        return untitledGenoMapsExcluded;
    }

    public void setUntitledGenoMapsExcluded(boolean untitledGenoMapsExcluded) {
        this.untitledGenoMapsExcluded = untitledGenoMapsExcluded;
    }

    public boolean hasUnknownIndividualsExcluded() {
        return unknownIndividualsExcluded;
    }

    public void setUnknownIndividualsExcluded(boolean unknownIndividualsExcluded) {
        this.unknownIndividualsExcluded = unknownIndividualsExcluded;
    }

    public boolean hasHyperlinkedIndividualInstancesDeduplicated() {
        return hyperlinkedIndividualInstancesDeduplicated;
    }

    public void setHyperlinkedIndividualInstancesDeduplicated(boolean hyperlinkedIndividualInstancesDeduplicated) {
        this.hyperlinkedIndividualInstancesDeduplicated = hyperlinkedIndividualInstancesDeduplicated;
    }

    public LocalDate getAnonymizedSinceDate() {
        return anonymizedSinceDate;
    }

    public void setAnonymizedSinceDate(LocalDate anonymizedSinceDate) {
        this.anonymizedSinceDate = anonymizedSinceDate;
    }

}
