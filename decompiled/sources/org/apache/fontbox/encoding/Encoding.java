package org.apache.fontbox.encoding;

import com.aowagie.text.pdf.BaseFont;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/encoding/Encoding.class */
public abstract class Encoding {
    protected Map<Integer, String> codeToName = new HashMap(250);
    protected Map<String, Integer> nameToCode = new HashMap(250);

    protected void addCharacterEncoding(int code, String name) {
        this.codeToName.put(Integer.valueOf(code), name);
        this.nameToCode.put(name, Integer.valueOf(code));
    }

    public Integer getCode(String name) {
        return this.nameToCode.get(name);
    }

    public String getName(int code) {
        String name = this.codeToName.get(Integer.valueOf(code));
        if (name != null) {
            return name;
        }
        return BaseFont.notdef;
    }

    public Map<Integer, String> getCodeToNameMap() {
        return Collections.unmodifiableMap(this.codeToName);
    }
}
