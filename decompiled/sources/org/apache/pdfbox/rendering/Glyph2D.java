package org.apache.pdfbox.rendering;

import java.awt.geom.GeneralPath;
import java.io.IOException;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/rendering/Glyph2D.class */
interface Glyph2D {
    GeneralPath getPathForCharacterCode(int i) throws IOException;

    void dispose();
}
