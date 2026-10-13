package org.apache.pdfbox.pdmodel.font.encoding;

import java.util.Map;
import org.apache.pdfbox.cos.COSBase;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/font/encoding/BuiltInEncoding.class */
public class BuiltInEncoding extends Encoding {
    public BuiltInEncoding(Map<Integer, String> codeToName) {
        for (Map.Entry<Integer, String> entry : codeToName.entrySet()) {
            add(entry.getKey().intValue(), entry.getValue());
        }
    }

    @Override // org.apache.pdfbox.pdmodel.common.COSObjectable
    public COSBase getCOSObject() {
        throw new UnsupportedOperationException("Built-in encodings cannot be serialized");
    }

    @Override // org.apache.pdfbox.pdmodel.font.encoding.Encoding
    public String getEncodingName() {
        return "built-in (TTF)";
    }
}
