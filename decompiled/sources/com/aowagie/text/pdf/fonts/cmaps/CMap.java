package com.aowagie.text.pdf.fonts.cmaps;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/fonts/cmaps/CMap.class */
public class CMap {
    private final List codeSpaceRanges = new ArrayList();
    private final Map singleByteMappings = new LinkedHashMap();
    private final Map doubleByteMappings = new LinkedHashMap();

    public String lookup(byte[] code, int offset, int length) {
        String result = null;
        if (length == 1) {
            Integer key = Integer.valueOf((code[offset] + 256) % 256);
            result = (String) this.singleByteMappings.get(key);
        } else if (length == 2) {
            int intKey = (code[offset] + 256) % 256;
            Integer key2 = Integer.valueOf((intKey << 8) + ((code[offset + 1] + 256) % 256));
            result = (String) this.doubleByteMappings.get(key2);
        }
        return result;
    }

    void addMapping(byte[] src, String dest) throws IOException {
        if (src.length == 1) {
            this.singleByteMappings.put(Integer.valueOf(src[0]), dest);
        } else {
            if (src.length == 2) {
                int intSrc = src[0] & 255;
                this.doubleByteMappings.put(Integer.valueOf((intSrc << 8) | (src[1] & 255)), dest);
                return;
            }
            throw new IOException("Mapping code should be 1 or two bytes and not " + src.length);
        }
    }

    void addCodespaceRange(CodespaceRange range) {
        this.codeSpaceRanges.add(range);
    }

    public List getCodeSpaceRanges() {
        return this.codeSpaceRanges;
    }
}
