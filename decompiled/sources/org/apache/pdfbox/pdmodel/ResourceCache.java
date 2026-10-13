package org.apache.pdfbox.pdmodel;

import java.io.IOException;
import org.apache.pdfbox.cos.COSObject;
import org.apache.pdfbox.pdmodel.documentinterchange.markedcontent.PDPropertyList;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace;
import org.apache.pdfbox.pdmodel.graphics.pattern.PDAbstractPattern;
import org.apache.pdfbox.pdmodel.graphics.shading.PDShading;
import org.apache.pdfbox.pdmodel.graphics.state.PDExtendedGraphicsState;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/ResourceCache.class */
public interface ResourceCache {
    PDFont getFont(COSObject cOSObject) throws IOException;

    PDColorSpace getColorSpace(COSObject cOSObject) throws IOException;

    PDExtendedGraphicsState getExtGState(COSObject cOSObject);

    PDShading getShading(COSObject cOSObject) throws IOException;

    PDAbstractPattern getPattern(COSObject cOSObject) throws IOException;

    PDPropertyList getProperties(COSObject cOSObject);

    PDXObject getXObject(COSObject cOSObject) throws IOException;

    void put(COSObject cOSObject, PDFont pDFont) throws IOException;

    void put(COSObject cOSObject, PDColorSpace pDColorSpace) throws IOException;

    void put(COSObject cOSObject, PDExtendedGraphicsState pDExtendedGraphicsState);

    void put(COSObject cOSObject, PDShading pDShading) throws IOException;

    void put(COSObject cOSObject, PDAbstractPattern pDAbstractPattern) throws IOException;

    void put(COSObject cOSObject, PDPropertyList pDPropertyList);

    void put(COSObject cOSObject, PDXObject pDXObject) throws IOException;
}
