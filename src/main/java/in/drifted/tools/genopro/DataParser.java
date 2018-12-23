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

import in.drifted.tools.genopro.model.Birth;
import in.drifted.tools.genopro.model.Individual;
import in.drifted.tools.genopro.model.BoundaryRect;
import in.drifted.tools.genopro.model.Death;
import in.drifted.tools.genopro.model.DocumentInfo;
import in.drifted.tools.genopro.model.Family;
import in.drifted.tools.genopro.model.Gender;
import in.drifted.tools.genopro.model.GenoDate;
import in.drifted.tools.genopro.model.GenoMap;
import in.drifted.tools.genopro.model.Hyperlink;
import in.drifted.tools.genopro.model.IndividualParserOptions;
import in.drifted.tools.genopro.model.Marriage;
import in.drifted.tools.genopro.model.Name;
import in.drifted.tools.genopro.model.PedigreeLink;
import in.drifted.tools.genopro.model.Position;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipInputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class DataParser {

    public static Document getDocument(Path path) throws IOException {

        Document document = null;

        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

            try (ZipInputStream zipInputStream = new ZipInputStream(Files.newInputStream(path))) {

                if (zipInputStream.getNextEntry() != null) {
                    document = builder.parse(zipInputStream);
                }
            }

        } catch (ParserConfigurationException | SAXException e) {
            throw new IOException(e);
        }

        return document;
    }

    public static DocumentInfo getDocumentInfo(Document document) {

        DocumentInfo documentInfo = null;

        Node documentNode = getSingleNode(document.getDocumentElement(), "Document");

        if (documentNode != null) {
            Map<String, String> nodeValueMap = getNodeValueMap(documentNode);
            String title = nodeValueMap.get("Title");
            String description = nodeValueMap.get("Description");

            documentInfo = new DocumentInfo((title != null) ? title : "", (description != null) ? description : "");
        }

        return documentInfo;
    }

    public static Map<String, GenoMap> getGenoMapMap(Document document) {

        Map<String, GenoMap> genoMapMap = new LinkedHashMap<>();

        NodeList nodeList = document.getElementsByTagName("GenoMap");

        for (int i = 0; i < nodeList.getLength(); i++) {

            Element element = (Element) nodeList.item(i);

            String name = element.getAttribute("Name");

            if (!name.isEmpty()) {

                BoundaryRect boundaryRect = getBoundaryRect(element.getAttribute("BoundaryRect"));
                Map<String, String> nodeValueMap = getNodeValueMap(element);

                genoMapMap.put(name, new GenoMap(name, nodeValueMap.get("Title"), boundaryRect));
            }
        }

        return genoMapMap;
    }

    public static Collection<Individual> getIndividualCollection(Document document, Map<String, GenoMap> genoMapMap, IndividualParserOptions parserOptions) {

        Collection<Individual> individualCollection = new HashSet<>();

        NodeList nodeList = document.getElementsByTagName("Individual");

        for (int i = 0; i < nodeList.getLength(); i++) {

            Individual individual = getIndividual(genoMapMap, (Element) nodeList.item(i));

            if (individual.getName() != null || parserOptions.getUnknownIndividuals()) {
                individualCollection.add(individual);
            }
        }

        if (parserOptions.getResolvedHyperlinks()) {
            individualCollection = getResolvedIndividualCollection(individualCollection);
        }

        if (parserOptions.getAnonymizedSinceDate() != null) {
            individualCollection = getAnonymizedIndividualCollection(individualCollection, parserOptions.getAnonymizedSinceDate());
        }

        return individualCollection;
    }

    private static Collection<Individual> getResolvedIndividualCollection(Collection<Individual> individualCollection) {

        Collection<Individual> resolvedIndividualCollection = new HashSet<>();

        Map<String, Individual> individualMap = new HashMap<>();
        Map<String, String> hyperlinkMap = new HashMap<>();

        for (Individual individual : individualCollection) {

            individualMap.put(individual.getId(), individual);

            if (individual.getHyperlink() != null) {
                hyperlinkMap.put(individual.getId(), individual.getHyperlink().getId());
                hyperlinkMap.put(individual.getHyperlink().getId(), individual.getId());
            }
        }

        for (Entry<String, Individual> entry : individualMap.entrySet()) {

            String individualId = entry.getKey();
            Individual individual = entry.getValue();

            if (hyperlinkMap.containsKey(individualId)) {

                String hyperlinkId = hyperlinkMap.get(individualId);
                Individual targetIndividual = individualMap.get(hyperlinkId);
                GenoMap targetGenoMap = targetIndividual.getGenoMap();

                Hyperlink hyperlink = null;

                if (targetGenoMap.getTitle() != null) {
                    hyperlink = new Hyperlink(targetIndividual.getGenoMap(), hyperlinkId);
                }

                if (individual.getHyperlink() == null) {
                    resolvedIndividualCollection.add(new Individual(individual.getId(), individual.getGenoMap(),
                            hyperlink, individual.getName(), individual.getGender(), individual.getBirth(),
                            individual.getDeath(), individual.isDead(), false, individual.getPosition(), individual.getBoundaryRect()));

                } else {
                    resolvedIndividualCollection.add(new Individual(individual.getId(), individual.getGenoMap(),
                            hyperlink, targetIndividual.getName(), targetIndividual.getGender(), targetIndividual.getBirth(),
                            targetIndividual.getDeath(), targetIndividual.isDead(), false, individual.getPosition(), individual.getBoundaryRect()));
                }

            } else {
                resolvedIndividualCollection.add(individual);
            }
        }

        return resolvedIndividualCollection;
    }

    private static Collection<Individual> getAnonymizedIndividualCollection(Collection<Individual> individualCollection, LocalDate localDate) {

        Collection<Individual> anonymizedIndividualCollection = new HashSet<>();

        boolean anonymizeDatesOnly = localDate.equals(LocalDate.now());

        for (Individual individual : individualCollection) {

            if (individual.isDead() || !anonymizeDatesOnly && individual.getBirth() != null && individual.getBirth().hasDate() && individual.getBirth().getDate().getLocalDate().isBefore(localDate)) {
                anonymizedIndividualCollection.add(individual);

            } else {
                if (anonymizeDatesOnly) {
                    anonymizedIndividualCollection.add(new Individual(individual.getId(), individual.getGenoMap(), individual.getHyperlink(), individual.getName(), individual.getGender(), null, null, false, false, individual.getPosition(), individual.getBoundaryRect()));

                } else {
                    anonymizedIndividualCollection.add(new Individual(individual.getId(), individual.getGenoMap(), null, null, individual.getGender(), null, null, false, true, individual.getPosition(), individual.getBoundaryRect()));
                }
            }
        }

        return anonymizedIndividualCollection;
    }

    public static Collection<Family> getFamilyCollection(Document document, Map<String, GenoMap> genoMapMap, Collection<Individual> individualCollection) {

        Collection<Family> familyCollection = new HashSet<>();

        Map<String, Integer> relationTypeMap = new HashMap<>();
        relationTypeMap.put("Marriage", 0);
        relationTypeMap.put("Divorce", 1);
        relationTypeMap.put("Separation", 2);

        Map<String, Individual> individualMap = new HashMap<>();

        for (Individual individual : individualCollection) {
            individualMap.put(individual.getId(), individual);
        }

        Map<String, List<PedigreeLink>> pedigreeLinkMap = getPedigreeLinkMap(document, individualCollection);
        Map<String, Marriage> marriageMap = getMarriageMap(document);

        NodeList nodeList = document.getElementsByTagName("Family");

        for (int i = 0; i < nodeList.getLength(); i++) {

            Element familyElement = (Element) nodeList.item(i);
            Node unionsNode = getSingleNode(familyElement, "Unions");

            GenoDate date = null;
            String comment = null;

            if (unionsNode != null) {

                String unions = unionsNode.getTextContent();
                Marriage marriage = marriageMap.get(unions);

                if (marriage != null) {
                    date = marriage.getDate();
                    comment = marriage.getComment();
                }
            }

            Map<String, String> familyNodeValueMap = getNodeValueMap(familyElement);

            String id = familyElement.getAttribute("ID");

            List<PedigreeLink> pedigreeLinkList = pedigreeLinkMap.get(id);

            boolean isParentAnonymized = false;

            for (PedigreeLink pedigreeLink : pedigreeLinkList) {
                if (pedigreeLink.getType() == PedigreeLink.PARENT) {
                    if (individualMap.get(pedigreeLink.getIndividualId()).isAnonymized()) {
                        isParentAnonymized = true;
                        break;
                    }
                }
            }

            boolean isChildAnonymized = false;

            for (PedigreeLink pedigreeLink : pedigreeLinkList) {
                if (pedigreeLink.getType() != PedigreeLink.PARENT) {
                    if (individualMap.get(pedigreeLink.getIndividualId()).isAnonymized()) {
                        isChildAnonymized = true;
                        break;
                    }
                }
            }

            boolean hasChildren = false;

            for (PedigreeLink pedigreeLink : pedigreeLinkList) {
                if (pedigreeLink.getType() != PedigreeLink.PARENT) {
                    hasChildren = true;
                    break;
                }
            }

            List<PedigreeLink> childlessPedigreeLinkList = new ArrayList<>();

            if (!isParentAnonymized && hasChildren && isChildAnonymized) {
                for (PedigreeLink pedigreeLink : pedigreeLinkList) {
                    if (pedigreeLink.getType() == PedigreeLink.PARENT) {
                        childlessPedigreeLinkList.add(pedigreeLink);
                    }
                }

                pedigreeLinkList = childlessPedigreeLinkList;
            }

            if (!(isParentAnonymized && !hasChildren) && !(isParentAnonymized && hasChildren && isChildAnonymized)) {

                String label = familyNodeValueMap.get("DisplayText");
                String relation = familyNodeValueMap.get("Relation");

                int relationType = 0;

                if (relation != null) {
                    if (!relationTypeMap.containsKey(relation)) {
                        System.out.println("unkn: " + relation);
                    }
                    relationType = relationTypeMap.get(relation);
                }

                Element positionElement = (Element) getSingleNode(familyElement, "Position");
                String genoMapName = positionElement.getAttribute("GenoMap");
                Position position = getPosition(positionElement.getFirstChild().getTextContent().trim());

                BoundaryRect topBoundaryRect = null;
                Node topNode = getSingleNode(positionElement, "Top");
                if (topNode != null) {
                    Map<String, String> topNodeValueMap = getNodeValueMap(topNode);
                    topBoundaryRect = getBoundaryRect(topNodeValueMap.get("Left") + "," + topNodeValueMap.get("Right"));
                }

                BoundaryRect bottomBoundaryRect = null;
                Node bottomNode = getSingleNode(positionElement, "Bottom");
                if (bottomNode != null) {
                    Map<String, String> bottomNodeValueMap = getNodeValueMap(bottomNode);
                    bottomBoundaryRect = getBoundaryRect(bottomNodeValueMap.get("Left") + "," + bottomNodeValueMap.get("Right"));
                }

                GenoMap genoMap = genoMapMap.get(genoMapName);

                if (genoMap == null) {
                    genoMap = genoMapMap.values().iterator().next();
                }

                familyCollection.add(new Family(id, genoMap, label, relationType, date, comment, pedigreeLinkList, position, topBoundaryRect, bottomBoundaryRect));
            }
        }

        return familyCollection;
    }

    /**
     * Returns a map of pedigree links for all families.
     *
     * @param document a source document
     * @param individualCollection a list of individuals, used for determining
     * link positions
     * @return a map of pedigree links for all families
     */
    private static Map<String, List<PedigreeLink>> getPedigreeLinkMap(Document document, Collection<Individual> individualCollection) {

        Map<String, List<PedigreeLink>> pedigreeLinkMap = new HashMap<>();

        NodeList nodeList = document.getElementsByTagName("PedigreeLink");

        Map<String, Position> individualPositionMap = new HashMap<>();

        for (Individual individual : individualCollection) {
            individualPositionMap.put(individual.getId(), individual.getPosition());
        }

        Map<String, Position> twinPositionMap = getTwinPositionMap(document);

        for (int i = 0; i < nodeList.getLength(); i++) {

            Element pedigreeLinkElement = (Element) nodeList.item(i);

            String familyId = pedigreeLinkElement.getAttribute("Family");
            String individualId = pedigreeLinkElement.getAttribute("Individual");
            String linkType = pedigreeLinkElement.getAttribute("PedigreeLink");
            Position position = individualPositionMap.get(individualId);
            String twin = pedigreeLinkElement.getAttribute("Twin");
            Position twinPosition = twin.isEmpty() ? null : twinPositionMap.get(twin);

            if (!pedigreeLinkMap.containsKey(familyId)) {
                pedigreeLinkMap.put(familyId, new ArrayList<>());
            }

            pedigreeLinkMap.get(familyId).add(new PedigreeLink(individualId, linkType, position, twinPosition));
        }

        return pedigreeLinkMap;
    }

    private static Map<String, Marriage> getMarriageMap(Document document) {

        Map<String, Marriage> marriageMap = new HashMap<>();

        NodeList nodeList = document.getElementsByTagName("Marriage");

        for (int i = 0; i < nodeList.getLength(); i++) {

            Element marriageElement = (Element) nodeList.item(i);
            Map<String, String> marriageNodeValueMap = getNodeValueMap(marriageElement);

            String id = marriageElement.getAttribute("ID");
            GenoDate date = new GenoDate(marriageNodeValueMap.get("Date"));
            String comment = marriageNodeValueMap.get("Comment");

            marriageMap.put(id, new Marriage(date, comment));
        }

        return marriageMap;
    }

    private static Map<String, Position> getTwinPositionMap(Document document) {

        Map<String, Position> twinPositionMap = new HashMap<>();

        NodeList nodeList = document.getElementsByTagName("Twin");

        for (int i = 0; i < nodeList.getLength(); i++) {

            Element twinElement = (Element) nodeList.item(i);
            String position = getSingleNode(twinElement, "Position").getTextContent();

            twinPositionMap.put(twinElement.getAttribute("ID"), new Position(Integer.parseInt(position), 0));
        }

        return twinPositionMap;
    }

    private static Individual getIndividual(Map<String, GenoMap> genoMapMap, Element individualElement) {

        Name name = getName(individualElement);

        Birth birth = getBirth(individualElement);
        Death death = getDeath(individualElement);

        String id = individualElement.getAttribute("ID");

        String hyperlink = individualElement.getAttribute("IndividualInternalHyperlink");

        Map<String, String> individualNodeValueMap = getNodeValueMap(individualElement);

        int gender = Gender.getValue(individualNodeValueMap.get("Gender"));
        boolean isDead = false;

        String isDeadValue = individualNodeValueMap.get("IsDead");

        if (isDeadValue != null) {
            isDead = isDeadValue.equals("Y");
        }

        Element positionElement = (Element) getSingleNode(individualElement, "Position");
        Position position = getPosition(positionElement.getTextContent());
        BoundaryRect boundaryRect = getBoundaryRect(positionElement.getAttribute("BoundaryRect"));
        String genoMapName = positionElement.getAttribute("GenoMap");

        GenoMap genoMap = genoMapMap.get(genoMapName);

        if (genoMap == null) {
            genoMap = genoMapMap.values().iterator().next();
        }

        return new Individual(id, genoMap, hyperlink.isEmpty() ? null : new Hyperlink(null, hyperlink), name, gender, birth, death, isDead, false, position, boundaryRect);
    }

    private static Name getName(Element individual) {

        Name name = null;

        Node nameNode = getSingleNode(individual, "Name");

        if (nameNode != null) {

            Map<String, String> nameMap = getNodeValueMap(nameNode);

            String firstName = nameMap.get("First");
            String middleName = nameMap.get("Middle");
            String lastName = nameMap.get("Last");
            String lastName2 = nameMap.get("Last2");

            if (!(firstName == null && lastName == null)) {
                name = new Name(firstName, middleName, lastName, lastName2);
            }
        }

        return name;
    }

    private static Birth getBirth(Element individual) {

        Birth birth = null;

        Node birthNode = getSingleNode(individual, "Birth");

        if (birthNode != null) {
            Map<String, String> birthMap = getNodeValueMap(birthNode);

            String date = birthMap.get("Date");
            String comment = birthMap.get("Comment");

            birth = new Birth(new GenoDate(date), comment);
        }

        return birth;
    }

    private static Death getDeath(Element individual) {

        Death death = null;

        Node deathNode = getSingleNode(individual, "Death");

        if (deathNode != null) {
            Map<String, String> deathMap = getNodeValueMap(deathNode);

            String date = deathMap.get("Date");
            String comment = deathMap.get("Comment");

            death = new Death(new GenoDate(date), comment);
        }

        return death;
    }

    private static Position getPosition(String strPosition) {

        int[] values = new int[2];

        String[] fragments = strPosition.split(",");

        for (int i = 0; i < fragments.length; i++) {
            values[i] = Integer.parseInt(fragments[i]);
        }

        return new Position(values[0], values[1]);
    }

    private static BoundaryRect getBoundaryRect(String strBoundaryRect) {

        int[] values = new int[4];

        String[] fragments = strBoundaryRect.split(",");

        for (int i = 0; i < fragments.length; i++) {
            values[i] = Integer.parseInt(fragments[i]);
        }

        return new BoundaryRect(values[0], values[1], values[2], values[3]);
    }

    private static Node getSingleNode(Element element, String name) {

        Node singleNode = null;

        NodeList nodeList = element.getElementsByTagName(name);

        if (nodeList.getLength() > 0) {
            singleNode = nodeList.item(0);
        }

        return singleNode;
    }

    private static Map<String, String> getNodeValueMap(Node node) {

        Map<String, String> nodeValueMap = new HashMap<>();

        Node childNode = node.getFirstChild();

        while (childNode != null) {

            childNode = childNode.getNextSibling();

            if (childNode instanceof Element) {
                Element element = (Element) childNode;
                nodeValueMap.put(element.getTagName(), element.getTextContent());
            }
        }

        return nodeValueMap;
    }

}
