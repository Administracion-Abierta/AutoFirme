package org.apache.fontbox.ttf;

import java.util.List;

/* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/ttf/CmapLookup.class */
public interface CmapLookup {
    int getGlyphId(int i);

    List<Integer> getCharCodes(int i);
}
