package org.apache.fontbox;

import java.awt.geom.GeneralPath;
import java.io.IOException;
import java.util.List;
import org.apache.fontbox.util.BoundingBox;

/* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/FontBoxFont.class */
public interface FontBoxFont {
    String getName() throws IOException;

    BoundingBox getFontBBox() throws IOException;

    List<Number> getFontMatrix() throws IOException;

    GeneralPath getPath(String str) throws IOException;

    float getWidth(String str) throws IOException;

    boolean hasGlyph(String str) throws IOException;
}
