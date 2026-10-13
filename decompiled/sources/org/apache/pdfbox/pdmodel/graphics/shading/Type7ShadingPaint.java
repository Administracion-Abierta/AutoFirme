package org.apache.pdfbox.pdmodel.graphics.shading;

import java.awt.Color;
import java.awt.PaintContext;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;
import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.util.Matrix;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/graphics/shading/Type7ShadingPaint.class */
class Type7ShadingPaint extends ShadingPaint<PDShadingType7> {
    private static final Log LOG = LogFactory.getLog(Type7ShadingPaint.class);

    Type7ShadingPaint(PDShadingType7 shading, Matrix matrix) {
        super(shading, matrix);
    }

    public int getTransparency() {
        return 0;
    }

    public PaintContext createContext(ColorModel cm, Rectangle deviceBounds, Rectangle2D userBounds, AffineTransform xform, RenderingHints hints) {
        try {
            return new Type7ShadingContext((PDShadingType7) this.shading, cm, xform, this.matrix, deviceBounds);
        } catch (IOException e) {
            LOG.error("An error occurred while painting", e);
            return new Color(0, 0, 0, 0).createContext(cm, deviceBounds, userBounds, xform, hints);
        }
    }
}
