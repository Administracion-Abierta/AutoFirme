package org.apache.fontbox.ttf;

/* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/ttf/GlyphDescription.class */
public interface GlyphDescription {
    int getEndPtOfContours(int i);

    byte getFlags(int i);

    short getXCoordinate(int i);

    short getYCoordinate(int i);

    boolean isComposite();

    int getPointCount();

    int getContourCount();

    void resolve();
}
