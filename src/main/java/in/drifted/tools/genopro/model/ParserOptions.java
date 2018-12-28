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

    private boolean excludeUntitledGenoMaps = false;
    private boolean excludeUnknownIndividuals = false;
    private boolean resolveHyperlinks = false;
    private LocalDate anonymizedSinceDate = null;

    public boolean isExcludeUntitledGenoMaps() {
        return excludeUntitledGenoMaps;
    }

    public void setExcludeUntitledGenoMaps(boolean excludeUntitledGenoMaps) {
        this.excludeUntitledGenoMaps = excludeUntitledGenoMaps;
    }

    public boolean isExcludeUnknownIndividuals() {
        return excludeUnknownIndividuals;
    }

    public void setExcludeUnknownIndividuals(boolean excludeUnknownIndividuals) {
        this.excludeUnknownIndividuals = excludeUnknownIndividuals;
    }

    public boolean isResolveHyperlinks() {
        return resolveHyperlinks;
    }

    public void setResolveHyperlinks(boolean resolveHyperlinks) {
        this.resolveHyperlinks = resolveHyperlinks;
    }

    public LocalDate getAnonymizedSinceDate() {
        return anonymizedSinceDate;
    }

    public void setAnonymizedSinceDate(LocalDate anonymizedSinceDate) {
        this.anonymizedSinceDate = anonymizedSinceDate;
    }

}
