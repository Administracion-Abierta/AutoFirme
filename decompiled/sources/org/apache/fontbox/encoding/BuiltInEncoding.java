package org.apache.fontbox.encoding;

import java.util.Map;

/* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/encoding/BuiltInEncoding.class */
public class BuiltInEncoding extends Encoding {
    public BuiltInEncoding(Map<Integer, String> codeToName) {
        for (Map.Entry<Integer, String> entry : codeToName.entrySet()) {
            addCharacterEncoding(entry.getKey().intValue(), entry.getValue());
        }
    }
}
