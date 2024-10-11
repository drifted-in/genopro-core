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

import java.awt.FontMetrics;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TextWrapUtil {

    public static List<String> getWrappedLineList(String line, int maxWidth, FontMetrics fontMetrics) {

        List<String> wrappedLineList = new ArrayList<>();

        if (line.isEmpty()) {
            wrappedLineList.add(line);

        } else {

            List<String> wordList = Arrays.asList(line.split(" "));

            int width = 0;
            int startIndex = 0;

            int spaceWidth = fontMetrics.stringWidth(" ");

            for (int i = 0; i < wordList.size(); i++) {
                width += fontMetrics.stringWidth(wordList.get(i));
                if (i < wordList.size() - 1) {
                    int nextWidth = fontMetrics.stringWidth(wordList.get(i + 1));
                    if (width + spaceWidth + nextWidth > maxWidth) {
                        wrappedLineList.add(getWrappedLine(wordList, startIndex, i));
                        width = 0;
                        startIndex = i + 1;
                    } else {
                        width += spaceWidth;
                    }
                } else {
                    wrappedLineList.add(getWrappedLine(wordList, startIndex, i));
                }
            }
        }

        return wrappedLineList;
    }

    private static String getWrappedLine(List<String> wordList, int startIndex, int endIndex) {
        return String.join(" ", wordList.subList(startIndex, endIndex + 1));
    }

}
