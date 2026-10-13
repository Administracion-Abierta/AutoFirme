package org.apache.pdfbox.pdmodel.graphics.shading;

import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.ColorModel;
import java.io.IOException;
import org.apache.pdfbox.util.Matrix;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/graphics/shading/Type6ShadingContext.class */
class Type6ShadingContext extends PatchMeshesShadingContext {
    Type6ShadingContext(PDShadingType6 shading, ColorModel colorModel, AffineTransform xform, Matrix matrix, Rectangle deviceBounds) throws IOException {
        super(shading, colorModel, xform, matrix, deviceBounds, 12);
    }
}
