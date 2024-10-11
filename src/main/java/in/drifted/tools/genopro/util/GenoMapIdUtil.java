package in.drifted.tools.genopro.util;

import java.text.Normalizer;

public class GenoMapIdUtil {

    public static String getGenoMapId(String genoMapTitle) {

        if (genoMapTitle != null) {

            StringBuilder idBuilder = new StringBuilder();

            String normalizedTitle = Normalizer.normalize(genoMapTitle, Normalizer.Form.NFD)
                    .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

            for (char c : normalizedTitle.toLowerCase().toCharArray()) {

                if ((c >= 48 && c <= 57) || (c >= 97 && c <= 122)) {
                    idBuilder.append(c);

                } else {
                    idBuilder.append(" ");
                }
            }

            return idBuilder.toString().trim().replaceAll("\\s+", "-");
        }

        return null;
    }

}
