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
package in.drifted.tools.genopro.core.util;

import in.drifted.tools.genopro.core.model.EventDate;
import in.drifted.tools.genopro.core.model.Family;
import in.drifted.tools.genopro.core.model.FamilyRelation;
import in.drifted.tools.genopro.core.model.GenoMap;
import in.drifted.tools.genopro.core.model.GenoMapData;
import in.drifted.tools.genopro.core.model.Individual;
import in.drifted.tools.genopro.core.model.Label;
import in.drifted.tools.genopro.core.model.PedigreeLink;
import in.drifted.tools.genopro.core.parser.DocumentParser;
import in.drifted.tools.genopro.core.parser.DocumentParserOptions;
import in.drifted.tools.genopro.core.util.comparator.IndividualHorizontalPositionComparator;
import in.drifted.tools.genopro.core.util.formatter.DateFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.w3c.dom.Document;

public class DocumentDataUtil {

    /**
     * Returns the collection of data for each GenoMap. This result can be
     * pre-filtered if specific parser options are enabled.
     *
     * @param document GenoPro XML document
     * @param documentParserOptions parser options
     * @return the collection of data for each GenoMap
     */
    public static List<GenoMapData> getGenoMapDataList(Document document, DocumentParserOptions documentParserOptions) {

        List<GenoMapData> genoMapDataList = new ArrayList<>();

        Map<String, GenoMap> genoMapMap = DocumentParser.getGenoMapMap(document);
        Map<String, String> placeMap = DocumentParser.getPlaceMap(document);
        Map<String, Individual> individualMap = DocumentParser.getIndividualMap(document, genoMapMap, documentParserOptions);
        Map<String, List<PedigreeLink>> familyPedigreeLinkMap = DocumentParser.getFamilyPedigreeLinkMap(
                document, individualMap);
        Set<Family> familySet = DocumentParser.getFamilySet(
                document, genoMapMap, individualMap, familyPedigreeLinkMap, placeMap);
        Set<Label> labelSet = new HashSet<>();

        if (!documentParserOptions.hasTextLabelsExcluded()) {
            labelSet = DocumentParser.getLabelSet(document, genoMapMap);
        }

        Map<GenoMap, Set<Individual>> genoMapIndividualMap = new HashMap<>();

        for (GenoMap genoMap : genoMapMap.values()) {
            genoMapIndividualMap.put(genoMap, new HashSet<>());
        }

        for (Individual individual : individualMap.values()) {
            genoMapIndividualMap.get(individual.genoMap()).add(individual);
        }

        Map<GenoMap, Set<Family>> familyMap = new HashMap<>();

        for (GenoMap genoMap : genoMapMap.values()) {
            familyMap.put(genoMap, new HashSet<>());
        }

        for (Family family : familySet) {
            familyMap.get(family.genoMap()).add(family);
        }

        Map<GenoMap, Set<Label>> labelMap = new HashMap<>();

        for (GenoMap genoMap : genoMapMap.values()) {
            labelMap.put(genoMap, new HashSet<>());
        }

        for (Label label : labelSet) {
            labelMap.get(label.genoMap()).add(label);
        }

        for (GenoMap genoMap : genoMapMap.values()) {
            if (!(genoMap.title() == null && documentParserOptions.hasUntitledGenoMapsExcluded())) {
                genoMapDataList.add(new GenoMapData(genoMap, genoMapIndividualMap.get(genoMap),
                        familyMap.get(genoMap), labelMap.get(genoMap)));
            }
        }

        return genoMapDataList;
    }

    /**
     * Returns the map of family relations for all individuals.
     *
     * @param genoMapDataList collection of data for each GenoMap
     * @param individualMap map of all individuals used for determining
     * father/mother relation based on horizontal position
     * @return the map of family relations for all individuals
     */
    public static Map<String, FamilyRelation> getFamilyRelationMap(List<GenoMapData> genoMapDataList,
            Map<String, Individual> individualMap) {

        Map<String, FamilyRelation> familyRelationMap = new HashMap<>();

        Map<String, String> fatherMap = new HashMap<>();
        Map<String, String> motherMap = new HashMap<>();
        Map<String, List<Individual>> mateMap = new HashMap<>();

        Comparator<Individual> maleComparator = new IndividualHorizontalPositionComparator(true);
        Comparator<Individual> femaleComparator = new IndividualHorizontalPositionComparator(false);

        for (GenoMapData genoMapData : genoMapDataList) {

            for (Family family : genoMapData.familySet()) {

                Individual father = null;
                Individual mother = null;

                for (PedigreeLink pedigreeLink : family.pedigreeLinkList()) {

                    if (pedigreeLink.isParent()) {

                        String individualId = pedigreeLink.individualId();
                        Individual individual = individualMap.get(individualId);

                        if (individual != null) {
                            if (individual.isMale()) {
                                father = individual;
                            } else {
                                mother = individual;
                            }
                        }
                    }
                }

                for (PedigreeLink pedigreeLink : family.pedigreeLinkList()) {

                    String individualId = pedigreeLink.individualId();

                    if (pedigreeLink.isParent()) {

                        if (father != null && father.id().equals(individualId)) {
                            if (!mateMap.containsKey(individualId)) {
                                mateMap.put(individualId, new ArrayList<>());
                            }
                            List<Individual> mateList = mateMap.get(individualId);

                            if (mother != null) {
                                mateList.add(mother);
                            }
                        }

                        if (mother != null && mother.id().equals(individualId)) {
                            if (!mateMap.containsKey(individualId)) {
                                mateMap.put(individualId, new ArrayList<>());
                            }
                            List<Individual> mateList = mateMap.get(individualId);

                            if (father != null) {
                                mateList.add(father);
                            }
                        }

                    } else {
                        if (father != null) {
                            fatherMap.put(individualId, father.id());
                        }
                        if (mother != null) {
                            motherMap.put(individualId, mother.id());
                        }
                    }
                }
            }
        }

        for (Individual individual : individualMap.values()) {

            String individualId = individual.id();
            List<String> mateIdList = new ArrayList<>();

            if (mateMap.containsKey(individualId)) {

                List<Individual> mateList = mateMap.get(individualId);
                mateList.sort(individual.isMale() ? maleComparator : femaleComparator);

                for (Individual mate : mateList) {
                    mateIdList.add(mate.id());
                }
            }

            familyRelationMap.put(individualId, new FamilyRelation(fatherMap.get(individualId),
                    motherMap.get(individualId), mateIdList));
        }

        return familyRelationMap;
    }

    /**
     * Returns the formatted date
     *
     * @param date date
     * @param dateFormatter date formatter
     * @return the formatted date
     */
    public static String getFormattedDate(EventDate date, DateFormatter dateFormatter) {

        String formattedDate = "";

        if (date != null && date.hasDate()) {
            formattedDate = date.date().format(dateFormatter);
        }

        return formattedDate;
    }
}
