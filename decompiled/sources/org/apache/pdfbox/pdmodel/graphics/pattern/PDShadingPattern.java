package org.apache.pdfbox.pdmodel.graphics.pattern;

import java.io.IOException;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.graphics.shading.PDShading;
import org.apache.pdfbox.pdmodel.graphics.state.PDExtendedGraphicsState;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/graphics/pattern/PDShadingPattern.class */
public class PDShadingPattern extends PDAbstractPattern {
    private PDExtendedGraphicsState extendedGraphicsState;
    private PDShading shading;

    public PDShadingPattern() {
        getCOSObject().setInt(COSName.PATTERN_TYPE, 2);
    }

    public PDShadingPattern(COSDictionary resourceDictionary) {
        super(resourceDictionary);
    }

    @Override // org.apache.pdfbox.pdmodel.graphics.pattern.PDAbstractPattern
    public int getPatternType() {
        return 2;
    }

    public PDExtendedGraphicsState getExtendedGraphicsState() {
        if (this.extendedGraphicsState == null) {
            COSBase base = getCOSObject().getDictionaryObject(COSName.EXT_G_STATE);
            if (base instanceof COSDictionary) {
                this.extendedGraphicsState = new PDExtendedGraphicsState((COSDictionary) base);
            }
        }
        return this.extendedGraphicsState;
    }

    public void setExtendedGraphicsState(PDExtendedGraphicsState extendedGraphicsState) {
        this.extendedGraphicsState = extendedGraphicsState;
        getCOSObject().setItem(COSName.EXT_G_STATE, extendedGraphicsState);
    }

    public PDShading getShading() throws IOException {
        if (this.shading == null) {
            COSBase base = getCOSObject().getDictionaryObject(COSName.SHADING);
            if (base instanceof COSDictionary) {
                this.shading = PDShading.create((COSDictionary) base);
            }
        }
        return this.shading;
    }

    public void setShading(PDShading shadingResources) {
        this.shading = shadingResources;
        getCOSObject().setItem(COSName.SHADING, shadingResources);
    }
}
