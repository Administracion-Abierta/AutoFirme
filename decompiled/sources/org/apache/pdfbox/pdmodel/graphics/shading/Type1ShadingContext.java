package org.apache.pdfbox.pdmodel.graphics.shading;

import com.aowagie.text.pdf.ColumnText;
import java.awt.PaintContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace;
import org.apache.pdfbox.util.Matrix;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/graphics/shading/Type1ShadingContext.class */
class Type1ShadingContext extends ShadingContext implements PaintContext {
    private static final Log LOG = LogFactory.getLog(Type1ShadingContext.class);
    private PDShadingType1 type1ShadingType;
    private AffineTransform rat;
    private final float[] domain;

    Type1ShadingContext(PDShadingType1 shading, ColorModel colorModel, AffineTransform xform, Matrix matrix) throws IOException {
        super(shading, colorModel, xform, matrix);
        this.type1ShadingType = shading;
        if (shading.getDomain() != null) {
            this.domain = shading.getDomain().toFloatArray();
        } else {
            this.domain = new float[]{ColumnText.GLOBAL_SPACE_CHAR_RATIO, 1.0f, ColumnText.GLOBAL_SPACE_CHAR_RATIO, 1.0f};
        }
        try {
            this.rat = shading.getMatrix().createAffineTransform().createInverse();
            this.rat.concatenate(matrix.createAffineTransform().createInverse());
            this.rat.concatenate(xform.createInverse());
        } catch (NoninvertibleTransformException e) {
            LOG.error(e.getMessage() + ", matrix: " + matrix, e);
            this.rat = new AffineTransform();
        }
    }

    @Override // org.apache.pdfbox.pdmodel.graphics.shading.ShadingContext
    public void dispose() {
        super.dispose();
        this.type1ShadingType = null;
    }

    @Override // org.apache.pdfbox.pdmodel.graphics.shading.ShadingContext
    public ColorModel getColorModel() {
        return super.getColorModel();
    }

    /* JADX WARN: Code duplicated, block: B:22:0x00a6  */
    /* JADX WARN: Code duplicated, block: B:36:0x00d9 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:38:0x00af A[EXC_TOP_SPLITTER, SYNTHETIC] */
    public Raster getRaster(int x, int y, int w, int h) {
        float[] tmpValues;
        PDColorSpace shadingColorSpace;
        WritableRaster raster = getColorModel().createCompatibleWritableRaster(w, h);
        int[] data = new int[w * h * 4];
        float[] values = new float[2];
        for (int j = 0; j < h; j++) {
            for (int i = 0; i < w; i++) {
                int index = ((j * w) + i) * 4;
                boolean useBackground = false;
                values[0] = x + i;
                values[1] = y + j;
                this.rat.transform(values, 0, values, 0, 1);
                if (values[0] < this.domain[0] || values[0] > this.domain[1] || values[1] < this.domain[2] || values[1] > this.domain[3]) {
                    if (getBackground() != null) {
                        useBackground = true;
                        if (useBackground) {
                            tmpValues = getBackground();
                        } else {
                            try {
                                tmpValues = this.type1ShadingType.evalFunction(values);
                            } catch (IOException e) {
                                LOG.error("error while processing a function", e);
                            }
                        }
                        shadingColorSpace = getShadingColorSpace();
                        if (shadingColorSpace != null) {
                            try {
                                tmpValues = shadingColorSpace.toRGB(tmpValues);
                            } catch (IOException e2) {
                                LOG.error("error processing color space", e2);
                            }
                        }
                        data[index] = (int) (tmpValues[0] * 255.0f);
                        data[index + 1] = (int) (tmpValues[1] * 255.0f);
                        data[index + 2] = (int) (tmpValues[2] * 255.0f);
                        data[index + 3] = 255;
                    }
                } else {
                    if (useBackground) {
                        tmpValues = getBackground();
                    } else {
                        tmpValues = this.type1ShadingType.evalFunction(values);
                    }
                    shadingColorSpace = getShadingColorSpace();
                    if (shadingColorSpace != null) {
                        tmpValues = shadingColorSpace.toRGB(tmpValues);
                    }
                    data[index] = (int) (tmpValues[0] * 255.0f);
                    data[index + 1] = (int) (tmpValues[1] * 255.0f);
                    data[index + 2] = (int) (tmpValues[2] * 255.0f);
                    data[index + 3] = 255;
                }
            }
        }
        raster.setPixels(0, 0, w, h, data);
        return raster;
    }

    public float[] getDomain() {
        return this.domain;
    }
}
