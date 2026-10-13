package org.apache.fontbox.cff;

import org.apache.fontbox.encoding.Encoding;

/* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/cff/CFFEncoding.class */
public abstract class CFFEncoding extends Encoding {
    CFFEncoding() {
    }

    public void add(int code, int sid, String name) {
        addCharacterEncoding(code, name);
    }

    protected void add(int code, int sid) {
        addCharacterEncoding(code, CFFStandardString.getName(sid));
    }
}
