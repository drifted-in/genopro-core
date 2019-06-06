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
    public static Map<String, Individual> getEnhancedIndividualMap(int highlightMode,
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
    public static Map<String, Set<String>> getHighlightKeyMap(int highlightMode, Map<String, Individual> individualMap,
            Map<String, List<PedigreeLink>> familyPedigreeLinkMap) {

        Map<String, Set<String>> highlightKeyMap = new HashMap<>();
        Map<String, Set<String>> deducedHighlightKeyMap = new HashMap<>();
        Map<String, Map<String, Set<String>>> childMap = new HashMap<>();

        Gender gender = (highlightMode == 2) ? Gender.FEMALE : Gender.MALE;

        for (List<PedigreeLink> pedigreeLinkList : familyPedigreeLinkMap.values()) {

            Map<String, Set<String>> childInfoMap = new HashMap<>();

            String parentId = null;

            // assing child info to the parent
            for (PedigreeLink pedigreeLink : pedigreeLinkList) {

                if (pedigreeLink.isParent()) {

                    Individual individual = individualMap.get(pedigreeLink.getIndividualId());

                    if (individual.getGender() == gender) {

                        parentId = pedigreeLink.getIndividualId();

                        // cummulate children from multiple families
                        if (childMap.containsKey(parentId)) {
                            childInfoMap = childMap.get(parentId);

                        } else {
                            childMap.put(parentId, new HashMap<>());
                            childInfoMap = childMap.get(parentId);
                        }
                    }
                }
            }

            // fill-in child info
            for (PedigreeLink pedigreeLink : pedigreeLinkList) {

                if (!pedigreeLink.isParent()) {
                    String childId = pedigreeLink.getIndividualId();
                    Individual individual = individualMap.get(childId);
                    if (individual.getGender() == gender) {
                        childInfoMap.put(childId, individual.getHighlightKeySet());
                    }
                }
            }

            // override parent keys if these can be deduced from children keys
            if (parentId != null) {

                // check for highlight key duplicities
                if (childInfoMap.isEmpty()) {
                    Set<String> highlightKeySet = new HashSet<>();
                    highlightKeySet.add("n/a");

                    deducedHighlightKeyMap.put(parentId, highlightKeySet);

                } else {

                    List<String> highlightKeyList = new ArrayList<>();

                    for (Set<String> highlightKeySet : childInfoMap.values()) {
                        highlightKeyList.addAll(highlightKeySet);
                    }

                    Set<String> duplicateSet = getDuplicateSet(highlightKeyList);

                    if (duplicateSet.size() == 1) {
                        deducedHighlightKeyMap.put(parentId, duplicateSet);
                    }
                }
            }
        }

        // fill terminal nodes
        for (Map.Entry<String, Map<String, Set<String>>> entry : childMap.entrySet()) {
            for (Map.Entry<String, Set<String>> childInfoEntry : entry.getValue().entrySet()) {

                if (!childMap.containsKey(childInfoEntry.getKey())) {
                    Set<String> highlightKeySet = childInfoEntry.getValue();
                    if (highlightKeySet.isEmpty()) {
                        highlightKeySet.add("n/a");
                    }
                    highlightKeyMap.put(childInfoEntry.getKey(), highlightKeySet);
                }
            }
        }

        Map<String, Set<String>> parentChildMap = new HashMap<>();

        for (Map.Entry<String, Map<String, Set<String>>> entry : childMap.entrySet()) {
            parentChildMap.put(entry.getKey(), entry.getValue().keySet());
        }

        Map<String, String> childParentMap = new HashMap<>();

        for (Map.Entry<String, Set<String>> entry : parentChildMap.entrySet()) {
            for (String childId : entry.getValue()) {
                childParentMap.put(childId, entry.getKey());
            }
        }

        boolean hasUnresolvedChildren;

        do {
            Set<String> processedChildIdSet = new HashSet<>();
            hasUnresolvedChildren = false;

            for (Map.Entry<String, Map<String, Set<String>>> entry : childMap.entrySet()) {

                String key = entry.getKey();

                if (deducedHighlightKeyMap.containsKey(key)) {
                    processedChildIdSet.add(key);
                    highlightKeyMap.put(key, deducedHighlightKeyMap.get(key));

                } else {
                    Set<String> hightlightKeySet = new HashSet<>();

                    for (Map.Entry<String, Set<String>> childInfoEntry : entry.getValue().entrySet()) {

                        if (highlightKeyMap.containsKey(childInfoEntry.getKey())) {
                            hightlightKeySet.addAll(highlightKeyMap.get(childInfoEntry.getKey()));

                        } else {
                            Set<String> childHighlightKeySet = childInfoEntry.getValue();

                            if (!childHighlightKeySet.isEmpty()) {
                                hightlightKeySet.addAll(childHighlightKeySet);

                            } else {
                                hasUnresolvedChildren = true;
                                hightlightKeySet.clear();
                                break;
                            }
                        }
                    }

                    if (!hightlightKeySet.isEmpty()) {
                        processedChildIdSet.add(key);
                        highlightKeyMap.put(key, hightlightKeySet);
                    }
                }
            }

            childMap.keySet().removeAll(processedChildIdSet);

        } while (hasUnresolvedChildren);

        // deduplication
        boolean hasChanged;

        do {
            hasChanged = false;

            for (Entry<String, Set<String>> entry : parentChildMap.entrySet()) {

                if (highlightKeyMap.get(entry.getKey()).size() > 1) {

                    List<String> highlightKeyList = new ArrayList<>();

                    for (String id : entry.getValue()) {
                        highlightKeyList.addAll(highlightKeyMap.get(id));
                    }

                    Set<String> duplicateSet = getDuplicateSet(highlightKeyList);

                    duplicateSet.remove("n/a");

                    if (duplicateSet.isEmpty()) {
                        Set<String> highlightKeySet = new HashSet<>(highlightKeyList);
                        highlightKeySet.remove("n/a");

                        if (highlightKeySet.size() == 1) {
                            highlightKeyMap.put(entry.getKey(), highlightKeySet);
                            hasChanged = true;

                        } else if (!highlightKeyList.isEmpty()) {
                            String parentId = childParentMap.get(entry.getKey());
                            if (highlightKeyMap.containsKey(parentId)) {
                                Set<String> parentHightlightKeySet = highlightKeyMap.get(parentId);
                                if (parentHightlightKeySet.size() == 1) {
                                    highlightKeyList.addAll(highlightKeyMap.get(parentId));
                                    duplicateSet = getDuplicateSet(highlightKeyList);
                                    duplicateSet.remove("n/a");
                                }
                            } else {
                                if (parentId == null) {
                                    int originalSize = highlightKeyMap.get(entry.getKey()).size();
                                    if (highlightKeySet.size() < originalSize) {
                                        highlightKeyMap.put(entry.getKey(), highlightKeySet);
                                        hasChanged = true;
                                    }
                                }
                            }
                        }
                    }

                    if (duplicateSet.size() == 1) {
                        highlightKeyMap.put(entry.getKey(), duplicateSet);
                        hasChanged = true;
                    }
                }
            }

        } while (hasChanged);

        // inherit parent value to ancestors
        Set<String> resolvedParentSet = new HashSet<>();
        Set<String> processedParentSet = new HashSet<>();

        for (String parentId : childParentMap.values()) {
            if (!childParentMap.containsKey(parentId)) {
                resolvedParentSet.add(parentId);
            }
        }

        do {
            hasChanged = false;

            for (Entry<String, Set<String>> entry : parentChildMap.entrySet()) {

                String parentId = entry.getKey();

                if (resolvedParentSet.contains(parentId)) {

                    if (!processedParentSet.contains(parentId)) {

                        Set<String> parentHighlightKeySet = highlightKeyMap.get(parentId);

                        List<String> singleKeyList = new ArrayList<>();

                        for (String childId : entry.getValue()) {
                            Set<String> childHighlightKeySet = highlightKeyMap.get(childId);
                            if (childHighlightKeySet.size() == 1) {
                                singleKeyList.addAll(childHighlightKeySet);
                            }
                        }

                        Set<String> duplicateSet = getDuplicateSet(singleKeyList);

                        for (String childId : entry.getValue()) {

                            Set<String> childHighlightKeySet = highlightKeyMap.get(childId);
                            Set<String> matchingSet = new HashSet<>(duplicateSet);
                            matchingSet.retainAll(childHighlightKeySet);

                            if (!individualMap.get(childId).getHighlightKeySet().isEmpty()) {
                                // do nothing if child has exact value

                            } else if (childHighlightKeySet.size() > 1 && matchingSet.isEmpty()) {
                                // do nothing if child apparently do not inherit from parent

                            } else if (childHighlightKeySet.size() == 1 && !matchingSet.isEmpty()) {
                                // override parent value(s) with the child one
                                if (parentHighlightKeySet.retainAll(childHighlightKeySet)) {
                                    // we need another iteration if the parent keys were reduced to a single value
                                    hasChanged = true;
                                }

                            } else {
                                if (childHighlightKeySet.addAll(parentHighlightKeySet)) {
                                    hasChanged = true;
                                }
                            }

                            resolvedParentSet.add(childId);
                        }

                        processedParentSet.add(parentId);
                    }
                }
            }

        } while (hasChanged);

        return highlightKeyMap;
    }

    public static Set<String> getDuplicateSet(List<String> list) {

        Set<String> duplicateSet = new HashSet<>();

        Set<String> tempSet = new HashSet<>();

        for (String item : list) {
            if (!tempSet.add(item)) {
                duplicateSet.add(item);
            }
        }

        return duplicateSet;
    }
}
