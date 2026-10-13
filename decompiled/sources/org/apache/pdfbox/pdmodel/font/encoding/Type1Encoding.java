package org.apache.pdfbox.pdmodel.font.encoding;

import java.util.Map;
import org.apache.fontbox.afm.CharMetric;
import org.apache.fontbox.afm.FontMetrics;
import org.apache.pdfbox.cos.COSBase;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/font/encoding/Type1Encoding.class */
public class Type1Encoding extends Encoding {
    public static Type1Encoding fromFontBox(org.apache.fontbox.encoding.Encoding encoding) {
        Map<Integer, String> codeToName = encoding.getCodeToNameMap();
        Type1Encoding enc = new Type1Encoding();
        for (Map.Entry<Integer, String> entry : codeToName.entrySet()) {
            enc.add(entry.getKey().intValue(), entry.getValue());
        }
        return enc;
    }

    public Type1Encoding() {
    }

    public Type1Encoding(FontMetrics fontMetrics) {
        for (CharMetric nextMetric : fontMetrics.getCharMetrics()) {
            add(nextMetric.getCharacterCode(), nextMetric.getName());
        }
    }

    @Override // org.apache.pdfbox.pdmodel.common.COSObjectable
    public COSBase getCOSObject() {
        return null;
    }

    @Override // org.apache.pdfbox.pdmodel.font.encoding.Encoding
    public String getEncodingName() {
        return "built-in (Type 1)";
    }
}
