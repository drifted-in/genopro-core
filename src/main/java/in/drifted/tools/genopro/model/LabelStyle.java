/*
 * Copyright (c) 2021 Jan Tošovský <jan.tosovsky.cz@gmail.com>
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

public class LabelStyle {

    private final Size size;
    private final Alignment horizontalAlignment;
    private final Alignment verticalAlignment;
    private final int padding;
    private final Color textColor;
    private final Color fillColor;
    private final Border border;

    public LabelStyle(Size size, Alignment horizontalAlignment, Alignment verticalAlignment, int padding, Color textColor, Color fillColor, Border border) {
        this.size = size;
        this.horizontalAlignment = horizontalAlignment;
        this.verticalAlignment = verticalAlignment;
        this.padding = padding;
        this.textColor = textColor;
        this.fillColor = fillColor;
        this.border = border;
    }

    public Size getSize() {
        return size;
    }

    public Alignment getHorizontalAlignment() {
        return horizontalAlignment;
    }

    public Alignment getVerticalAlignment() {
        return verticalAlignment;
    }

    public int getPadding() {
        return padding;
    }

    public Color getTextColor() {
        return textColor;
    }

    public Color getFillColor() {
        return fillColor;
    }

    public Border getBorder() {
        return border;
    }

}
