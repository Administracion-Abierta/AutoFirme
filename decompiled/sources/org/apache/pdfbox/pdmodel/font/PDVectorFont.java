package org.apache.pdfbox.pdmodel.font;

import java.awt.geom.GeneralPath;
import java.io.IOException;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/font/PDVectorFont.class */
public interface PDVectorFont {
    GeneralPath getPath(int i) throws IOException;

    boolean hasGlyph(int i) throws IOException;
}
