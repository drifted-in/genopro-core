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
package in.drifted.tools.genopro;

import in.drifted.tools.genopro.model.DateFormatter;
import in.drifted.tools.genopro.model.EventDate;
import in.drifted.tools.genopro.model.Family;
import in.drifted.tools.genopro.model.FamilyRelation;
import in.drifted.tools.genopro.model.Gender;
import in.drifted.tools.genopro.model.GenoMap;
import in.drifted.tools.genopro.model.GenoMapData;
import in.drifted.tools.genopro.model.Individual;
import in.drifted.tools.genopro.model.IndividualParserOptions;
import in.drifted.tools.genopro.model.PedigreeLink;
import in.drifted.tools.genopro.model.HorizontalPositionComparator;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.w3c.dom.Document;

public class DataUtil {

    public static List<GenoMapData> getGenoMapDataList(Document document) {

        List<GenoMapData> genoMapDataList = new ArrayList<>();

        Map<String, GenoMap> genoMapMap = DataParser.getGenoMapMap(document);
        Collection<Individual> individualCollection = DataParser.getIndividualCollection(document, genoMapMap, new IndividualParserOptions(true, true, LocalDate.now().minus(Period.ofYears(100))));
        Collection<Family> familyCollection = DataParser.getFamilyCollection(document, genoMapMap, individualCollection);

        Map<String, Collection<Individual>> individualMap = new HashMap<>();

        for (GenoMap genoMap : genoMapMap.values()) {
            individualMap.put(genoMap.getTitle(), new HashSet<>());
        }

        for (Individual individual : individualCollection) {
            individualMap.get(individual.getGenoMap().getTitle()).add(individual);
        }

        Map<String, Collection<Family>> familyMap = new HashMap<>();

        for (GenoMap genoMap : genoMapMap.values()) {
            familyMap.put(genoMap.getTitle(), new HashSet<>());
        }

        for (Family family : familyCollection) {
            familyMap.get(family.getGenoMap().getTitle()).add(family);
        }

        for (GenoMap genoMap : genoMapMap.values()) {
            String genoMapTitle = genoMap.getTitle();
            if (genoMapTitle != null) {
                genoMapDataList.add(new GenoMapData(genoMap, individualMap.get(genoMapTitle), familyMap.get(genoMapTitle), null));
            }
        }

        return genoMapDataList;
    }

    /**
     * Returns a map of family relations for all individuals.
     * @param genoMapDataList source data to process
     * @param individualMap a map of all individuals used for determining father/mother based on horizontal position
     * @return a map of family relations for all individuals
     */
    public static Map<String, FamilyRelation> getFamilyRelationMap(List<GenoMapData> genoMapDataList, Map<String, Individual> individualMap) {

        Map<String, FamilyRelation> familyRelationMap = new HashMap<>();

        Map<String, String> fatherMap = new HashMap<>();
        Map<String, String> motherMap = new HashMap<>();
        Map<String, List<Individual>> mateMap = new HashMap<>();

        Comparator<Individual> maleComparator = new HorizontalPositionComparator(true);
        Comparator<Individual> femaleComparator = new HorizontalPositionComparator(false);

        for (GenoMapData genoMapData : genoMapDataList) {

            for (Family family : genoMapData.getFamilyCollection()) {

                Individual father = null;
                Individual mother = null;

                for (PedigreeLink pedigreeLink : family.getPedigreeLinkList()) {

                    if (pedigreeLink.getType() == PedigreeLink.PARENT) {

                        String individualId = pedigreeLink.getIndividualId();
                        Individual individual = individualMap.get(individualId);

                        if (individual != null) {
                            if (individual.getGender() == Gender.MALE) {
                                father = individual;
                            } else {
                                mother = individual;
                            }
                        }
                    }
                }

                for (PedigreeLink pedigreeLink : family.getPedigreeLinkList()) {

                    String individualId = pedigreeLink.getIndividualId();

                    if (pedigreeLink.getType() == PedigreeLink.PARENT) {

                        if (father != null && father.getId().equals(individualId)) {
                            if (!mateMap.containsKey(individualId)) {
                                mateMap.put(individualId, new ArrayList<>());
                            }
                            List<Individual> mateList = mateMap.get(individualId);

                            if (mother != null) {
                                mateList.add(mother);
                            }
                        }

                        if (mother != null && mother.getId().equals(individualId)) {
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
                            fatherMap.put(individualId, father.getId());
                        }
                        if (mother != null) {
                            motherMap.put(individualId, mother.getId());
                        }
                    }
                }
            }
        }

        for (Individual individual : individualMap.values()) {

            String individualId = individual.getId();
            List<String> mateIdList = new ArrayList<>();

            if (mateMap.containsKey(individualId)) {

                List<Individual> mateList = mateMap.get(individualId);
                Collections.sort(mateList, (individual.getGender() == Gender.MALE) ? maleComparator : femaleComparator);

                for (Individual mate : mateList) {
                    mateIdList.add(mate.getId());
                }
            }

            familyRelationMap.put(individualId, new FamilyRelation(fatherMap.get(individualId), motherMap.get(individualId), mateIdList));
        }

        return familyRelationMap;
    }

    public static String getFormattedDate(EventDate date, DateFormatter dateFormatter) {

        String formattedDate = "";

        if (date != null && date.hasDate()) {
            formattedDate = date.getDate().getDate(dateFormatter);
        }

        return formattedDate;
    }
}
