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
package in.drifted.tools.genopro.util;

import java.awt.FontMetrics;
import java.util.ArrayList;
import java.util.List;

public class StringUtil {

    public static List<String> getWrappedLineList(String line, int maxWidth, FontMetrics fontMetrics) {

        List<String> wrappedLineList = new ArrayList<>();

        if (!line.isEmpty()) {

            String[] words = line.split(" ");

            int width = 0;
            int startIndex = 0;

            int spaceWidth = fontMetrics.stringWidth(" ");

            for (int i = 0; i < words.length; i++) {
                width += fontMetrics.stringWidth(words[i]);
                if (i < words.length - 1) {
                    int nextWidth = fontMetrics.stringWidth(words[i + 1]);
                    if (width + spaceWidth + nextWidth > maxWidth) {
                        wrappedLineList.add(getWrappedLine(words, startIndex, i));
                        width = 0;
                        startIndex = i + 1;
                    }
                } else {
                    wrappedLineList.add(getWrappedLine(words, startIndex, i));
                }
            }
        }

        return wrappedLineList;
    }

    private static String getWrappedLine(String[] words, int startIndex, int endIndex) {

        String wrappedLine = "";

        for (int i = startIndex; i <= endIndex; i++) {
            wrappedLine += words[i] + " ";
        }

        return wrappedLine.trim();
    }

}
