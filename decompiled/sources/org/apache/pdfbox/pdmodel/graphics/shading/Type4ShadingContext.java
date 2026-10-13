package org.apache.pdfbox.pdmodel.graphics.shading;

import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.ColorModel;
import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.util.Matrix;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/graphics/shading/Type4ShadingContext.class */
class Type4ShadingContext extends GouraudShadingContext {
    private static final Log LOG = LogFactory.getLog(Type4ShadingContext.class);
    private final int bitsPerFlag;

    Type4ShadingContext(PDShadingType4 shading, ColorModel cm, AffineTransform xform, Matrix matrix, Rectangle deviceBounds) throws IOException {
        super(shading, cm, xform, matrix);
        LOG.debug("Type4ShadingContext");
        this.bitsPerFlag = shading.getBitsPerFlag();
        LOG.debug("bitsPerFlag: " + this.bitsPerFlag);
        setTriangleList(shading.collectTriangles(xform, matrix));
        createPixelTable(deviceBounds);
    }
}
