package org.apache.pdfbox.pdmodel.font;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.fontbox.cmap.CMap;
import org.apache.fontbox.cmap.CMapParser;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/font/CMapManager.class */
final class CMapManager {
    static Map<String, CMap> cMapCache = Collections.synchronizedMap(new HashMap());

    private CMapManager() {
    }

    public static CMap getPredefinedCMap(String cMapName) throws IOException {
        CMap cmap = cMapCache.get(cMapName);
        if (cmap != null) {
            return cmap;
        }
        CMap targetCmap = new CMapParser().parsePredefined(cMapName);
        cMapCache.put(targetCmap.getName(), targetCmap);
        return targetCmap;
    }

    public static CMap parseCMap(InputStream cMapStream) throws IOException {
        CMap targetCmap = null;
        if (cMapStream != null) {
            targetCmap = new CMapParser(true).parse(cMapStream);
        }
        return targetCmap;
    }
}
