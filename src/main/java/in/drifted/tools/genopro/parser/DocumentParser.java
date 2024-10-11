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
package in.drifted.tools.genopro.parser;

import in.drifted.tools.genopro.model.Alignment;
import in.drifted.tools.genopro.model.Birth;
import in.drifted.tools.genopro.model.Border;
import in.drifted.tools.genopro.model.BoundaryRect;
import in.drifted.tools.genopro.model.Color;
import in.drifted.tools.genopro.model.Death;
import in.drifted.tools.genopro.model.DisplayStyle;
import in.drifted.tools.genopro.model.DocumentInfo;
import in.drifted.tools.genopro.model.Family;
import in.drifted.tools.genopro.model.FamilyEvent;
import in.drifted.tools.genopro.model.FamilyLineType;
import in.drifted.tools.genopro.model.FamilyRelationType;
import in.drifted.tools.genopro.model.Gender;
import in.drifted.tools.genopro.model.GenoDate;
import in.drifted.tools.genopro.model.GenoMap;
import in.drifted.tools.genopro.model.Hyperlink;
import in.drifted.tools.genopro.model.Individual;
import in.drifted.tools.genopro.model.Label;
import in.drifted.tools.genopro.model.LabelStyle;
import in.drifted.tools.genopro.model.Name;
import in.drifted.tools.genopro.model.PedigreeLink;
import in.drifted.tools.genopro.model.PedigreeLinkType;
import in.drifted.tools.genopro.model.Position;
import in.drifted.tools.genopro.model.Rect;
import in.drifted.tools.genopro.model.Size;
import in.drifted.tools.genopro.util.GenoMapIdUtil;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.zip.ZipInputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class DocumentParser {

    /**
     * Returns the GenoPro XML document.
     *
     * @param path path to GenoPro file
     * @return the GenoPro XML document
     * @throws IOException if an I/O error occurs while reading the file
     */
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

    /**
     * Returns the basic info of GenoPro document.
     *
     * @param document GenoPro XML document
     * @return the basic info of GenoPro document
     */
    public static DocumentInfo getDocumentInfo(Document document) {

        DocumentInfo documentInfo = new DocumentInfo("", "", DisplayStyle.NOTHING);

        Node documentNode = getSingleNode(document.getDocumentElement(), "Document");

        if (documentNode != null) {

            Map<String, String> nodeValueMap = getNodeValueMap(documentNode);
            String title = nodeValueMap.getOrDefault("Title", "");
            String description = nodeValueMap.getOrDefault("Description", "");
            DisplayStyle displayStyle = DisplayStyle.NOTHING;

            Node displayNode = getSingleNode(document.getDocumentElement(), "Tag");

            if (displayNode != null) {
                displayStyle = DisplayStyle.parse(displayNode.getTextContent());
            }

            documentInfo = new DocumentInfo(title, description, displayStyle);
        }

        return documentInfo;
    }

    /**
     * Returns the map of all GenoMaps.
     *
     * @param document GenoPro XML document
     * @return the map of all GenoMaps
     */
    public static Map<String, GenoMap> getGenoMapMap(Document document) {

        Map<String, GenoMap> genoMapMap = new LinkedHashMap<>();

        NodeList nodeList = document.getElementsByTagName("GenoMap");

        for (int i = 0; i < nodeList.getLength(); i++) {

            Element element = (Element) nodeList.item(i);

            String name = element.getAttribute("Name");

            if (!name.isEmpty()) {

                String boundaryRect = element.getAttribute("BoundaryRect");

                if (!boundaryRect.isEmpty()) {
                    Map<String, String> nodeValueMap = getNodeValueMap(element);
                    String title = nodeValueMap.get("Title");
                    String id = GenoMapIdUtil.getGenoMapId((title != null) ? title : name);

                    genoMapMap.put(name, new GenoMap(id, name, title, getBoundaryRect(boundaryRect)));
                }
            }
        }

        return genoMapMap;
    }

    /**
     * Returns the map of all individuals. The map can be pre-filtered if
     * additional options are specified.
     *
     * @param document              GenoPro XML document
     * @param genoMapMap            map of all GenoMaps
     * @param documentParserOptions document parser options
     * @return the map of all individuals
     */
    public static Map<String, Individual> getIndividualMap(
            Document document, Map<String, GenoMap> genoMapMap, DocumentParserOptions documentParserOptions) {

        Map<String, Individual> individualMap = new HashMap<>();

        Set<Individual> individualSet = getIndividualSet(document, genoMapMap, documentParserOptions);

        for (Individual individual : individualSet) {
            individualMap.put(individual.id(), individual);
        }

        return individualMap;
    }

    /**
     * Returns the set of all individuals. The list can be pre-filtered
     * if additional options are specified.
     *
     * @param document              GenoPro XML document
     * @param genoMapMap            map of all GenoMaps
     * @param documentParserOptions document parser options
     * @return the set of all individuals
     */
    public static Set<Individual> getIndividualSet(
            Document document, Map<String, GenoMap> genoMapMap, DocumentParserOptions documentParserOptions) {

        Set<Individual> individualSet = new HashSet<>();

        NodeList nodeList = document.getElementsByTagName("Individual");

        for (int i = 0; i < nodeList.getLength(); i++) {

            Individual individual = getIndividual(genoMapMap, (Element) nodeList.item(i));

            if (!(individual.name() == null && documentParserOptions.hasUnknownIndividualsExcluded())) {
                individualSet.add(individual);
            }
        }

        if (documentParserOptions.hasHyperlinkedIndividualInstancesDeduplicated()) {
            individualSet = getDeduplicatedIndividualSet(individualSet, documentParserOptions);
        }

        if (documentParserOptions.getAnonymizedSinceDate() != null) {
            individualSet = getAnonymizedIndividualSet(individualSet, documentParserOptions);
        }

        return individualSet;
    }

    private static Set<Individual> getDeduplicatedIndividualSet(
            Set<Individual> individualSet, DocumentParserOptions documentParserOptions) {

        Set<Individual> deduplicatedIndividualSet = new HashSet<>();

        Map<String, Individual> individualMap = new HashMap<>();
        Map<String, String> hyperlinkMap = new HashMap<>();

        for (Individual individual : individualSet) {

            individualMap.put(individual.id(), individual);

            if (individual.hyperlink() != null) {
                hyperlinkMap.put(individual.id(), individual.hyperlink().id());
                hyperlinkMap.put(individual.hyperlink().id(), individual.id());
            }
        }

        for (Entry<String, Individual> entry : individualMap.entrySet()) {

            String individualId = entry.getKey();
            Individual individual = entry.getValue();

            if (hyperlinkMap.containsKey(individualId)) {

                String hyperlinkId = hyperlinkMap.get(individualId);
                Individual targetIndividual = individualMap.get(hyperlinkId);
                GenoMap targetGenoMap = targetIndividual.genoMap();

                Hyperlink hyperlink = null;

                if (!(targetGenoMap.title() == null && documentParserOptions.hasUntitledGenoMapsExcluded())) {
                    hyperlink = new Hyperlink(targetIndividual.genoMap(), hyperlinkId);
                }

                if (individual.hyperlink() == null) {
                    deduplicatedIndividualSet.add(new Individual(individual.id(), individual.key(),
                            individual.genoMap(), hyperlink, individual.name(), individual.gender(), individual.birth(),
                            individual.death(), individual.isDeceased(), false, individual.position(),
                            individual.boundaryRect(), individual.highlightKeySet()));

                } else {
                    deduplicatedIndividualSet.add(new Individual(individual.id(), individual.key(),
                            individual.genoMap(), hyperlink, targetIndividual.name(), targetIndividual.gender(),
                            targetIndividual.birth(), targetIndividual.death(), targetIndividual.isDeceased(), false,
                            individual.position(), individual.boundaryRect(), individual.highlightKeySet()));
                }

            } else {
                deduplicatedIndividualSet.add(individual);
            }
        }

        return deduplicatedIndividualSet;
    }

    private static Set<Individual> getAnonymizedIndividualSet(
            Set<Individual> individualSet, DocumentParserOptions documentParserOptions) {

        Set<Individual> anonymizedIndividualSet = new HashSet<>();

        LocalDate anonymizedSinceLocalDate = documentParserOptions.getAnonymizedSinceDate();

        boolean anonymizeDatesOnly = anonymizedSinceLocalDate.equals(LocalDate.now());

        for (Individual individual : individualSet) {

            if (individual.isDeceased() || (!anonymizeDatesOnly
                    && individual.birth() != null
                    && individual.birth().hasDate()
                    && individual.birth().date().localDate().isBefore(anonymizedSinceLocalDate))) {

                anonymizedIndividualSet.add(individual);

            } else {
                if (anonymizeDatesOnly) {
                    anonymizedIndividualSet.add(new Individual(individual.id(), individual.key(), individual.genoMap(),
                            individual.hyperlink(), individual.name(), individual.gender(), null, null, false,
                            false, individual.position(), individual.boundaryRect(),
                            individual.highlightKeySet()));

                } else {
                    anonymizedIndividualSet.add(new Individual(individual.id(), individual.key(), individual.genoMap(),
                            null, null, individual.gender(), null, null, false, true, individual.position(),
                            individual.boundaryRect(), individual.highlightKeySet()));
                }
            }
        }

        return anonymizedIndividualSet;
    }

    /**
     * Returns the set of all families together with the pedigree links.
     *
     * @param document              GenoPro XML document
     * @param genoMapMap            map of all GenoMaps
     * @param individualMap         map of all individuals
     * @param familyPedigreeLinkMap map of family pedigree links
     * @param placeMap              map of all places
     * @return the set of all families
     */
    public static Set<Family> getFamilySet(
            Document document, Map<String, GenoMap> genoMapMap, Map<String, Individual> individualMap,
            Map<String, List<PedigreeLink>> familyPedigreeLinkMap, Map<String, String> placeMap) {

        Set<Family> familySet = new HashSet<>();

        Map<String, FamilyEvent> marriageMap = getMarriageMap(document, placeMap);

        NodeList nodeList = document.getElementsByTagName("Family");

        for (int i = 0; i < nodeList.getLength(); i++) {

            Element familyElement = (Element) nodeList.item(i);

            List<FamilyEvent> familyEventList = new ArrayList<>();

            Node unionsNode = getSingleNode(familyElement, "Unions");

            if (unionsNode != null) {
                String unions = unionsNode.getTextContent();
                if (marriageMap.containsKey(unions)) {
                    familyEventList.add(marriageMap.get(unions));
                }
            }

            Map<String, String> familyNodeValueMap = getNodeValueMap(familyElement);

            String familyId = familyElement.getAttribute("ID");

            if (familyPedigreeLinkMap.containsKey(familyId)) {

                List<PedigreeLink> pedigreeLinkList = familyPedigreeLinkMap.get(familyId);

                String fatherId = null;
                String motherId = null;

                for (PedigreeLink pedigreeLink : pedigreeLinkList) {

                    if (pedigreeLink.isParent()) {

                        Individual individual = individualMap.get(pedigreeLink.individualId());

                        if (individual.isMale()) {
                            fatherId = individual.id();

                        } else if (individual.isFemale()) {
                            motherId = individual.id();
                        }
                    }
                }

                boolean isParentAnonymized = false;

                for (PedigreeLink pedigreeLink : pedigreeLinkList) {

                    if (pedigreeLink.isParent()) {

                        Individual individual = individualMap.get(pedigreeLink.individualId());

                        if (individual.isAnonymized()) {
                            isParentAnonymized = true;
                            break;
                        }
                    }
                }

                boolean isChildAnonymized = false;

                for (PedigreeLink pedigreeLink : pedigreeLinkList) {
                    if (!pedigreeLink.isParent()) {
                        if (individualMap.get(pedigreeLink.individualId()).isAnonymized()) {
                            isChildAnonymized = true;
                            break;
                        }
                    }
                }

                boolean hasChildren = false;

                for (PedigreeLink pedigreeLink : pedigreeLinkList) {
                    if (!pedigreeLink.isParent()) {
                        hasChildren = true;
                        break;
                    }
                }

                List<PedigreeLink> childlessPedigreeLinkList = new ArrayList<>();

                if (!isParentAnonymized && hasChildren && isChildAnonymized) {
                    for (PedigreeLink pedigreeLink : pedigreeLinkList) {
                        if (pedigreeLink.isParent()) {
                            childlessPedigreeLinkList.add(pedigreeLink);
                        }
                    }

                    pedigreeLinkList = childlessPedigreeLinkList;
                }

                if (!(isParentAnonymized && !hasChildren) && !(isParentAnonymized && isChildAnonymized)) {

                    String label = familyNodeValueMap.get("DisplayText");
                    FamilyLineType familyLineType = FamilyLineType.parse(familyNodeValueMap.get("FamilyLine"));
                    FamilyRelationType relationType = FamilyRelationType.parse(familyNodeValueMap.get("Relation"));
                    Element positionElement = (Element) getSingleNode(familyElement, "Position");
                    GenoMap genoMap = getGenoMap(genoMapMap, positionElement.getAttribute("GenoMap"));
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
                        bottomBoundaryRect = getBoundaryRect(bottomNodeValueMap.get("Left") + ","
                                + bottomNodeValueMap.get("Right"));
                    }

                    familySet.add(new Family(familyId, getFamilyKey(familyId), fatherId, motherId, genoMap, label,
                            relationType, familyLineType, familyEventList, pedigreeLinkList, position, topBoundaryRect,
                            bottomBoundaryRect));
                }

            } else {
                System.out.println("Family not found: " + familyId);
            }
        }

        return familySet;
    }

    /**
     * Returns the map of pedigree links for all families.
     *
     * @param document      GenoPro XML document
     * @param individualMap map of all individuals
     * @return the map of family pedigree links for all families
     */
    public static Map<String, List<PedigreeLink>> getFamilyPedigreeLinkMap(
            Document document, Map<String, Individual> individualMap) {

        Map<String, List<PedigreeLink>> familyPedigreeLinkMap = new HashMap<>();

        NodeList nodeList = document.getElementsByTagName("PedigreeLink");

        Map<String, Position> individualPositionMap = new HashMap<>();

        for (Entry<String, Individual> entry : individualMap.entrySet()) {
            individualPositionMap.put(entry.getKey(), entry.getValue().position());
        }

        Map<String, Position> twinPositionMap = getTwinPositionMap(document);

        for (int i = 0; i < nodeList.getLength(); i++) {

            Element pedigreeLinkElement = (Element) nodeList.item(i);

            String familyId = pedigreeLinkElement.getAttribute("Family");
            String individualId = pedigreeLinkElement.getAttribute("Individual");
            PedigreeLinkType pedigreeLinkType = PedigreeLinkType.parse(pedigreeLinkElement.getAttribute("PedigreeLink"));
            Position position = individualPositionMap.get(individualId);
            String twin = pedigreeLinkElement.getAttribute("Twin");
            Position twinPosition = twin.isEmpty() ? null : twinPositionMap.get(twin);

            if (!familyPedigreeLinkMap.containsKey(familyId)) {
                familyPedigreeLinkMap.put(familyId, new ArrayList<>());
            }

            familyPedigreeLinkMap.get(familyId).add(new PedigreeLink(individualId, pedigreeLinkType, position, twinPosition));
        }

        return familyPedigreeLinkMap;
    }

    private static Map<String, FamilyEvent> getMarriageMap(Document document, Map<String, String> placeMap) {

        Map<String, FamilyEvent> marriageMap = new HashMap<>();

        NodeList nodeList = document.getElementsByTagName("Marriage");

        for (int i = 0; i < nodeList.getLength(); i++) {

            Element marriageElement = (Element) nodeList.item(i);
            Map<String, String> marriageNodeValueMap = getNodeValueMap(marriageElement);

            String id = marriageElement.getAttribute("ID");
            GenoDate date = GenoDate.fromDate(marriageNodeValueMap.get("Date"));
            String place = null;
            if (marriageNodeValueMap.containsKey("Place")) {
                place = placeMap.getOrDefault(marriageNodeValueMap.get("Place"), null);
            }
            String comment = marriageNodeValueMap.get("Comment");

            marriageMap.put(id, new FamilyEvent(FamilyEvent.MARRIAGE, date, place, comment));
        }

        return marriageMap;
    }

    /**
     * Returns the map of all places
     *
     * @param document GenoPro XML document
     * @return the map of all places
     */
    public static Map<String, String> getPlaceMap(Document document) {

        Map<String, String> placeMap = new HashMap<>();

        NodeList nodeList = document.getElementsByTagName("Place");

        for (int i = 0; i < nodeList.getLength(); i++) {

            Element placeElement = (Element) nodeList.item(i);
            Map<String, String> placeNodeValueMap = getNodeValueMap(placeElement);

            String id = placeElement.getAttribute("ID");
            String place = placeNodeValueMap.get("Name");

            placeMap.put(id, place);
        }

        return placeMap;
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

    /**
     * Returns the set of all labels
     *
     * @param document   GenoPro XML document
     * @param genoMapMap map of all GenoMaps
     * @return the set of all labels
     */
    public static Set<Label> getLabelSet(Document document, Map<String, GenoMap> genoMapMap) {

        Set<Label> labelSet = new HashSet<>();

        Element labelsElement = (Element) getSingleNode(document.getDocumentElement(), "Labels");

        if (labelsElement != null) {
            NodeList nodeList = labelsElement.getElementsByTagName("Label");

            for (int i = 0; i < nodeList.getLength(); i++) {

                Element labelElement = (Element) nodeList.item(i);
                Element positionElement = (Element) getSingleNode(labelElement, "Position");
                GenoMap genoMap = getGenoMap(genoMapMap, positionElement.getAttribute("GenoMap"));

                int zIndex = positionElement.hasAttribute("z") ? Integer.parseInt(positionElement.getAttribute("z")) : 0;
                Position position = getPosition(positionElement.getFirstChild().getTextContent().trim());
                int width = Integer.parseInt(positionElement.getAttribute("Width"));
                int height = Integer.parseInt(positionElement.getAttribute("Height"));
                Rect rect = new Rect(position.x(), position.y(), width, height);
                Element textElement = (Element) getSingleNode(labelElement, "Text");
                String text = textElement.getFirstChild().getTextContent().trim();
                Element alignmentElement = (Element) getSingleNode(textElement, "Alignment");

                Size textSize = getSize(positionElement.getAttribute("Size"), Size.M);
                Alignment horizontalAlignment = Alignment.CENTER;
                Alignment verticalAlignment = Alignment.CENTER;

                if (alignmentElement != null) {
                    horizontalAlignment = getAlignment(alignmentElement.getAttribute("Horizontal"), Alignment.CENTER);
                    verticalAlignment = getAlignment(alignmentElement.getAttribute("Vertical"), Alignment.CENTER);
                }

                int padding = Integer.parseInt(textElement.getAttribute("Padding"));

                Element colorElement = (Element) getSingleNode(labelElement, "Color");
                Color textColor = Color.fromHex(colorElement.getAttribute("Text"));
                Color fillColor = Color.fromHex(colorElement.getAttribute("Fill"));
                Color borderColor = Color.fromHex(colorElement.getAttribute("Border"));

                Element borderElement = (Element) getSingleNode(labelElement, "Border");
                Size borderSize = textSize;
                String borderPattern = "-";

                if (borderElement != null) {
                    borderSize = getSize(borderElement.getAttribute("Width"), borderSize);
                    borderPattern = borderElement.hasAttribute("Pattern") ? borderElement.getAttribute("Pattern") : "-";
                }

                Border border = new Border(borderColor, borderSize, borderPattern);

                LabelStyle labelStyle = new LabelStyle(textSize, horizontalAlignment, verticalAlignment, padding,
                        textColor, fillColor, border);
                labelSet.add(new Label(genoMap, text, rect, zIndex, labelStyle));
            }
        }

        return labelSet;
    }

    private static Individual getIndividual(Map<String, GenoMap> genoMapMap, Element individualElement) {

        Name name = getName(individualElement);

        Birth birth = getBirth(individualElement);
        Death death = getDeath(individualElement);

        String id = individualElement.getAttribute("ID");
        int key = getIndividualKey(id);

        String hyperlink = individualElement.getAttribute("IndividualInternalHyperlink");

        Map<String, String> individualNodeValueMap = getNodeValueMap(individualElement);

        Gender gender = Gender.parse(individualNodeValueMap.get("Gender"));
        boolean isDeceased = false;

        String isDeceasedValue = individualNodeValueMap.get("IsDead");

        if (isDeceasedValue != null) {
            isDeceased = isDeceasedValue.equals("Y");
        }

        Element positionElement = (Element) getSingleNode(individualElement, "Position");
        Position position = getPosition(positionElement.getTextContent());
        BoundaryRect boundaryRect = getBoundaryRect(positionElement.getAttribute("BoundaryRect"));
        GenoMap genoMap = getGenoMap(genoMapMap, positionElement.getAttribute("GenoMap"));

        Set<String> highlightKeySet = getHighlightKeySet(individualElement);

        return new Individual(id, key, genoMap, hyperlink.isEmpty() ? null : new Hyperlink(null, hyperlink),
                name, gender, birth, death, isDeceased, false, position, boundaryRect, highlightKeySet);
    }

    private static GenoMap getGenoMap(Map<String, GenoMap> genoMapMap, String genoMapName) {
        if (genoMapName.isEmpty()) {
            return genoMapMap.values().iterator().next();
        } else {
            return genoMapMap.get(genoMapName);
        }
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

            name = new Name(firstName, middleName, lastName, lastName2);
        }

        return name;
    }

    private static Birth getBirth(Element individual) {

        Birth birth = null;

        Node birthNode = getSingleNode(individual, "Birth");

        if (birthNode != null) {
            Map<String, String> birthMap = getNodeValueMap(birthNode);

            if (birthMap.containsKey("Date")) {
                String date = birthMap.get("Date");
                String comment = birthMap.get("Comment");

                birth = new Birth(GenoDate.fromDate(date), comment);
            }
        }

        return birth;
    }

    private static Death getDeath(Element individual) {

        Death death = null;

        Node deathNode = getSingleNode(individual, "Death");

        if (deathNode != null) {
            Map<String, String> deathMap = getNodeValueMap(deathNode);

            if (deathMap.containsKey("Date")) {
                String date = deathMap.get("Date");
                String comment = deathMap.get("Comment");

                death = new Death(GenoDate.fromDate(date), comment);
            }
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

        return BoundaryRect.fromPoints(values[0], values[1], values[2], values[3]);
    }

    private static Size getSize(String size, Size defaultSize) {
        if (size == null) {
            return defaultSize;
        } else {
            return switch (size) {
                case "T" -> Size.T;
                case "S" -> Size.S;
                case "L" -> Size.L;
                case "M" -> Size.M;
                case "X" -> Size.XL;
                case "XX" -> Size.XXL;
                case "XXX" -> Size.XXXL;
                case "XXXX" -> Size.XXXXL;
                default -> defaultSize;
            };
        }
    }

    private static Alignment getAlignment(String alignment, Alignment defaultAlignment) {
        if (alignment == null) {
            return defaultAlignment;
        } else {
            return switch (alignment) {
                case "Center" -> Alignment.CENTER;
                case "Top" -> Alignment.TOP;
                case "Left" -> Alignment.LEFT;
                case "Bottom" -> Alignment.BOTTOM;
                case "Right" -> Alignment.RIGHT;
                default -> defaultAlignment;
            };
        }
    }

    private static Set<String> getHighlightKeySet(Element individualElement) {

        Set<String> highlightKeySet = new HashSet<>();

        NodeList displayNodeList = individualElement.getElementsByTagName("Display");

        // display node can have various children, we look for Colors node only
        for (int i = 0; i < displayNodeList.getLength(); i++) {
            Node colorsNode = getSingleNode((Element) displayNodeList.item(i), "Colors");
            if (colorsNode != null) {
                Node genderNode = getSingleNode((Element) colorsNode, "Gender");
                if (genderNode != null) {
                    String highlightKey = ((Element) genderNode).getAttribute("Symbol");
                    if (!highlightKey.isEmpty()) {
                        highlightKeySet.add(highlightKey);
                    }
                    break;
                }
            }
        }

        return highlightKeySet;
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

            if (childNode instanceof Element element) {
                nodeValueMap.put(element.getTagName(), element.getTextContent());
            }
        }

        return nodeValueMap;
    }

    private static int getIndividualKey(String individualId) {
        return Integer.parseInt(individualId.replace("ind", ""));
    }

    private static int getFamilyKey(String familyId) {
        return Integer.parseInt(familyId.replace("fam", ""));
    }

}
