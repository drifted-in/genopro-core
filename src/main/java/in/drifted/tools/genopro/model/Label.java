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

public class Label {

    private final GenoMap genoMap;
    private final String text;
    private final Rect rect;
    private final int zIndex;
    private final LabelStyle labelStyle;

    public Label(GenoMap genoMap, String text, Rect rect, int zIndex, LabelStyle labelStyle) {
        this.genoMap = genoMap;
        this.text = text;
        this.rect = rect;
        this.zIndex = zIndex;
        this.labelStyle = labelStyle;
    }

    public GenoMap getGenoMap() {
        return genoMap;
    }

    public String getText() {
        return text;
    }

    public Rect getRect() {
        return rect;
    }

    public int getzIndex() {
        return zIndex;
    }

    public LabelStyle getLabelStyle() {
        return labelStyle;
    }

}
