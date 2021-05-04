/*
 * Copyright (c) 2019 Jan Tošovský <jan.tosovsky.cz@gmail.com>
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

import in.drifted.tools.genopro.model.Gender;
import in.drifted.tools.genopro.model.HighlightMode;
import in.drifted.tools.genopro.model.Individual;
import in.drifted.tools.genopro.model.PedigreeLink;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class HighlightUtil {

    /**
     * Returns the map of all individuals with resolved highlight keys.
     *
     * @param highlightMode highlight mode
     * @param individualMap map of all individuals
     * @param familyPedigreeLinkMap map of all family pedigree links
     * @return the map of all individuals with resolved highlight keys
     */
    public static Map<String, Individual> getEnhancedIndividualMap(HighlightMode highlightMode,
            Map<String, Individual> individualMap, Map<String, List<PedigreeLink>> familyPedigreeLinkMap) {

        Map<String, Individual> enhancedIndividualMap = new HashMap<>();

        Map<String, Set<String>> highlightKeyMap = getHighlightKeyMap(highlightMode, individualMap,
                familyPedigreeLinkMap);

        for (Entry<String, Individual> entry : individualMap.entrySet()) {

            Individual individual = entry.getValue();

            Individual enhancedIndividual = new Individual(individual.getId(), individual.getGenoMap(),
                    individual.getHyperlink(), individual.getName(), individual.getGender(), individual.getBirth(),
                    individual.getDeath(), individual.isDead(), individual.isAnonymized(), individual.getPosition(),
                    individual.getBoundaryRect(), highlightKeyMap.get(individual.getId()));

            enhancedIndividualMap.put(entry.getKey(), enhancedIndividual);
        }

        return enhancedIndividualMap;
    }

    /**
     * Returns the map of highlight keys for all individuals.
     *
     * @param highlightMode highlight mode
     * @param individualMap map of all individuals
     * @param familyPedigreeLinkMap map of all family pedigree links
     * @return the map of highlight keys for all individuals
     */
    public static Map<String, Set<String>> getHighlightKeyMap(HighlightMode highlightMode, Map<String, Individual> individualMap,
            Map<String, List<PedigreeLink>> familyPedigreeLinkMap) {

        Map<String, Set<String>> highlightKeyMap = new HashMap<>();

        Gender gender = (highlightMode == HighlightMode.MATERNAL) ? Gender.FEMALE : Gender.MALE;

        Map<String, Map<String, Set<String>>> childMap = getChildMap(familyPedigreeLinkMap, individualMap, gender);

        highlightKeyMap.putAll(getTerminalNodesHighlightKeyMap(childMap));
        
         // resolving children
        boolean hasUnresolvedChildren;

        do {
            Set<String> processedChildIdSet = new HashSet<>();
            hasUnresolvedChildren = false;

            for (Map.Entry<String, Map<String, Set<String>>> entry : childMap.entrySet()) {

                String parentId = entry.getKey();

                int childrenCount = entry.getValue().size();

                if (childrenCount == 0) {
                    processedChildIdSet.add(parentId);

                } else if (childrenCount == 1) {
                    String childId = entry.getValue().keySet().iterator().next();
                    if (highlightKeyMap.containsKey(childId)) {
                        highlightKeyMap.put(parentId, highlightKeyMap.get(childId));
                        processedChildIdSet.add(parentId);
                    } else {
                        hasUnresolvedChildren = true;
                    }

                } else {
                    List<String> highlightKeyList = new ArrayList<>();

                    // for each of two or more children
                    for (Map.Entry<String, Set<String>> childInfoEntry : entry.getValue().entrySet()) {

                        if (highlightKeyMap.containsKey(childInfoEntry.getKey())) {
                            highlightKeyList.addAll(highlightKeyMap.get(childInfoEntry.getKey()));

                        } else {
                            Set<String> childHighlightKeySet = childInfoEntry.getValue();

                            if (!childHighlightKeySet.isEmpty()) {
                                highlightKeyList.addAll(childHighlightKeySet);

                            } else {
                                hasUnresolvedChildren = true;
                                highlightKeyList.clear();
                                break;
                            }
                        }
                    }

                    if (!highlightKeyList.isEmpty()) {

                        // if at least two children share same key (but only one), it is set also to parent
                        if (childrenCount > 1) {
                            Map<String, Integer> highlightKeyHistogram = new HashMap<>();
                            for (String highlightKey : highlightKeyList) {
                                int count = highlightKeyHistogram.getOrDefault(highlightKey, 0);
                                highlightKeyHistogram.put(highlightKey, count + 1);
                            }
                            Set<String> candidateSet = new HashSet<>();
                            for (Map.Entry<String, Integer> histogramEntry : highlightKeyHistogram.entrySet()) {
                                if (histogramEntry.getValue() > 1) {
                                    candidateSet.add(histogramEntry.getKey());
                                }
                            }
                            if (candidateSet.size() == 1 && !candidateSet.iterator().next().equals("n/a")) {
                                highlightKeyList.clear();
                                highlightKeyList.addAll(candidateSet);
                            }
                        }

                        processedChildIdSet.add(parentId);
                        highlightKeyMap.put(parentId, new HashSet<>(highlightKeyList));
                    }
                }
            }

            childMap.keySet().removeAll(processedChildIdSet);

        } while (hasUnresolvedChildren);

        return highlightKeyMap;
    }

    private static Map<String, Map<String, Set<String>>> getChildMap(Map<String, List<PedigreeLink>> familyPedigreeLinkMap,
            Map<String, Individual> individualMap, Gender gender) {

        Map<String, Map<String, Set<String>>> childMap = new HashMap<>();

        for (List<PedigreeLink> pedigreeLinkList : familyPedigreeLinkMap.values()) {

            String parentId = getParentId(individualMap, pedigreeLinkList, gender);

            childMap.put(parentId, getChildInfoMap(individualMap, pedigreeLinkList, gender));
        }

        return childMap;
    }

    private static String getParentId(Map<String, Individual> individualMap,
            List<PedigreeLink> pedigreeLinkList, Gender gender) {

        String parentId = null;

        for (PedigreeLink pedigreeLink : pedigreeLinkList) {

            if (pedigreeLink.isParent()) {

                Individual individual = individualMap.get(pedigreeLink.getIndividualId());

                if (individual.getGender() == gender) {
                    parentId = pedigreeLink.getIndividualId();
                }
            }
        }

        return parentId;
    }

    private static Map<String, Set<String>> getChildInfoMap(Map<String, Individual> individualMap,
            List<PedigreeLink> pedigreeLinkList, Gender gender) {

        Map<String, Set<String>> childInfoMap = new HashMap<>();

        for (PedigreeLink pedigreeLink : pedigreeLinkList) {

            if (!pedigreeLink.isParent()) {

                String childId = pedigreeLink.getIndividualId();
                Individual individual = individualMap.get(childId);

                if (individual.getGender() == gender) {
                    childInfoMap.put(childId, individual.getHighlightKeySet());
                }
            }
        }

        return childInfoMap;
    }
           
    private static Map<String, Set<String>> getTerminalNodesHighlightKeyMap(Map<String, Map<String, Set<String>>> childMap) {
        
        Map<String, Set<String>> highlightKeyMap = new HashMap<>();
        
        Set<String> defaultHighlightKeySet = new HashSet<>();
        defaultHighlightKeySet.add("n/a");
        
        // for each parent
        for (Map.Entry<String, Map<String, Set<String>>> entry : childMap.entrySet()) {
            // if parent has no children
            if (entry.getValue().isEmpty()) {
                highlightKeyMap.put(entry.getKey(), defaultHighlightKeySet);
            }
            // for each children
            for (Map.Entry<String, Set<String>> childInfoEntry : entry.getValue().entrySet()) {
                // if child doesn't have children
                if (!childMap.containsKey(childInfoEntry.getKey())) {
                    Set<String> highlightKeySet = childInfoEntry.getValue();
                    if (highlightKeySet.isEmpty()) {
                        highlightKeySet.addAll(defaultHighlightKeySet);
                    }

                    highlightKeyMap.put(childInfoEntry.getKey(), highlightKeySet);
                }
            }
        }
         
        return highlightKeyMap;
    }

}
