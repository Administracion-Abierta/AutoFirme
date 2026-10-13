package org.apache.pdfbox.pdmodel.font.encoding;

import com.aowagie.text.pdf.BaseFont;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.COSObjectable;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/font/encoding/Encoding.class */
public abstract class Encoding implements COSObjectable {
    protected static final int CHAR_CODE = 0;
    protected static final int CHAR_NAME = 1;
    protected final Map<Integer, String> codeToName = new HashMap(250);
    protected final Map<String, Integer> inverted = new HashMap(250);

    public abstract String getEncodingName();

    public static Encoding getInstance(COSName name) {
        if (COSName.STANDARD_ENCODING.equals(name)) {
            return StandardEncoding.INSTANCE;
        }
        if (COSName.WIN_ANSI_ENCODING.equals(name)) {
            return WinAnsiEncoding.INSTANCE;
        }
        if (COSName.MAC_ROMAN_ENCODING.equals(name)) {
            return MacRomanEncoding.INSTANCE;
        }
        if (COSName.MAC_EXPERT_ENCODING.equals(name)) {
            return MacExpertEncoding.INSTANCE;
        }
        return null;
    }

    public Map<Integer, String> getCodeToNameMap() {
        return Collections.unmodifiableMap(this.codeToName);
    }

    public Map<String, Integer> getNameToCodeMap() {
        return Collections.unmodifiableMap(this.inverted);
    }

    protected void add(int code, String name) {
        this.codeToName.put(Integer.valueOf(code), name);
        if (!this.inverted.containsKey(name)) {
            this.inverted.put(name, Integer.valueOf(code));
        }
    }

    protected void overwrite(int code, String name) {
        Integer oldCode;
        String oldName = this.codeToName.get(Integer.valueOf(code));
        if (oldName != null && (oldCode = this.inverted.get(oldName)) != null && oldCode.intValue() == code) {
            this.inverted.remove(oldName);
        }
        this.inverted.put(name, Integer.valueOf(code));
        this.codeToName.put(Integer.valueOf(code), name);
    }

    public boolean contains(String name) {
        return this.inverted.containsKey(name);
    }

    public boolean contains(int code) {
        return this.codeToName.containsKey(Integer.valueOf(code));
    }

    public String getName(int code) {
        String name = this.codeToName.get(Integer.valueOf(code));
        if (name != null) {
            return name;
        }
        return BaseFont.notdef;
    }
}
