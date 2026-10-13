package org.apache.pdfbox.pdmodel.graphics.shading;

import com.aowagie.text.pdf.ColumnText;
import java.awt.PaintContext;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBoolean;
import org.apache.pdfbox.pdmodel.common.function.PDFunction;
import org.apache.pdfbox.util.Matrix;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/graphics/shading/RadialShadingContext.class */
public class RadialShadingContext extends ShadingContext implements PaintContext {
    private static final Log LOG = LogFactory.getLog(RadialShadingContext.class);
    private PDShadingType3 radialShadingType;
    private final float[] coords;
    private final float[] domain;
    private final boolean[] extend;
    private final double x1x0;
    private final double y1y0;
    private final double r1r0;
    private final double r0pow2;
    private final float d1d0;
    private final double denom;
    private final int factor;
    private final int[] colorTable;
    private AffineTransform rat;

    public RadialShadingContext(PDShadingType3 shading, ColorModel colorModel, AffineTransform xform, Matrix matrix, Rectangle deviceBounds) throws IOException {
        super(shading, colorModel, xform, matrix);
        this.radialShadingType = shading;
        this.coords = shading.getCoords().toFloatArray();
        if (this.radialShadingType.getDomain() != null) {
            this.domain = shading.getDomain().toFloatArray();
        } else {
            this.domain = new float[]{ColumnText.GLOBAL_SPACE_CHAR_RATIO, 1.0f};
        }
        COSArray extendValues = shading.getExtend();
        if (extendValues != null) {
            this.extend = new boolean[2];
            this.extend[0] = ((COSBoolean) extendValues.getObject(0)).getValue();
            this.extend[1] = ((COSBoolean) extendValues.getObject(1)).getValue();
        } else {
            this.extend = new boolean[]{false, false};
        }
        this.x1x0 = this.coords[3] - this.coords[0];
        this.y1y0 = this.coords[4] - this.coords[1];
        this.r1r0 = this.coords[5] - this.coords[2];
        this.r0pow2 = Math.pow(this.coords[2], 2.0d);
        this.denom = (Math.pow(this.x1x0, 2.0d) + Math.pow(this.y1y0, 2.0d)) - Math.pow(this.r1r0, 2.0d);
        this.d1d0 = this.domain[1] - this.domain[0];
        try {
            this.rat = matrix.createAffineTransform().createInverse();
            this.rat.concatenate(xform.createInverse());
        } catch (NoninvertibleTransformException e) {
            LOG.error(e.getMessage() + ", matrix: " + matrix, e);
            this.rat = new AffineTransform();
        }
        AffineTransform shadingToDevice = (AffineTransform) xform.clone();
        shadingToDevice.concatenate(matrix.createAffineTransform());
        double dist = Math.sqrt(Math.pow(deviceBounds.getMaxX() - deviceBounds.getMinX(), 2.0d) + Math.pow(deviceBounds.getMaxY() - deviceBounds.getMinY(), 2.0d));
        this.factor = (int) Math.ceil(dist);
        this.colorTable = calcColorTable();
    }

    private int[] calcColorTable() throws IOException {
        int[] map = new int[this.factor + 1];
        if (this.factor == 0 || this.d1d0 == ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
            float[] values = this.radialShadingType.evalFunction(this.domain[0]);
            map[0] = convertToRGB(values);
        } else {
            for (int i = 0; i <= this.factor; i++) {
                float t = this.domain[0] + ((this.d1d0 * i) / this.factor);
                float[] values2 = this.radialShadingType.evalFunction(t);
                map[i] = convertToRGB(values2);
            }
        }
        return map;
    }

    @Override // org.apache.pdfbox.pdmodel.graphics.shading.ShadingContext
    public void dispose() {
        super.dispose();
        this.radialShadingType = null;
    }

    @Override // org.apache.pdfbox.pdmodel.graphics.shading.ShadingContext
    public ColorModel getColorModel() {
        return super.getColorModel();
    }

    /* JADX WARN: Code duplicated, block: B:69:0x0195  */
    /* JADX WARN: Code duplicated, block: B:70:0x019e  */
    public Raster getRaster(int x, int y, int w, int h) {
        int value;
        WritableRaster raster = getColorModel().createCompatibleWritableRaster(w, h);
        float inputValue = -1.0f;
        int[] data = new int[w * h * 4];
        float[] values = new float[2];
        for (int j = 0; j < h; j++) {
            for (int i = 0; i < w; i++) {
                values[0] = x + i;
                values[1] = y + j;
                this.rat.transform(values, 0, values, 0, 1);
                boolean useBackground = false;
                float[] inputValues = calculateInputValues(values[0], values[1]);
                if (!Float.isNaN(inputValues[0]) || !Float.isNaN(inputValues[1])) {
                    if (inputValues[0] < ColumnText.GLOBAL_SPACE_CHAR_RATIO || inputValues[0] > 1.0f) {
                        if (inputValues[1] >= ColumnText.GLOBAL_SPACE_CHAR_RATIO && inputValues[1] <= 1.0f) {
                            inputValue = inputValues[1];
                        } else if (this.extend[0] && this.extend[1]) {
                            inputValue = Math.max(inputValues[0], inputValues[1]);
                        } else if (this.extend[0]) {
                            inputValue = inputValues[0];
                        } else if (this.extend[1]) {
                            inputValue = inputValues[1];
                        } else if (getBackground() != null) {
                            useBackground = true;
                        }
                    } else if (inputValues[1] >= ColumnText.GLOBAL_SPACE_CHAR_RATIO && inputValues[1] <= 1.0f) {
                        inputValue = Math.max(inputValues[0], inputValues[1]);
                    } else {
                        inputValue = inputValues[0];
                    }
                    if (inputValue > 1.0f) {
                        if (this.extend[1] && this.coords[5] > ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
                            inputValue = 1.0f;
                        } else if (getBackground() != null) {
                            useBackground = true;
                        }
                        if (useBackground) {
                            value = getRgbBackground();
                        } else {
                            int key = (int) (inputValue * this.factor);
                            value = this.colorTable[key];
                        }
                        int index = ((j * w) + i) * 4;
                        data[index] = value & 255;
                        int value2 = value >> 8;
                        data[index + 1] = value2 & 255;
                        data[index + 2] = (value2 >> 8) & 255;
                        data[index + 3] = 255;
                    } else if (inputValue < ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
                        if (this.extend[0] && this.coords[2] > ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
                            inputValue = 0.0f;
                        } else if (getBackground() != null) {
                            useBackground = true;
                        }
                        if (useBackground) {
                            value = getRgbBackground();
                        } else {
                            int key2 = (int) (inputValue * this.factor);
                            value = this.colorTable[key2];
                        }
                        int index2 = ((j * w) + i) * 4;
                        data[index2] = value & 255;
                        int value3 = value >> 8;
                        data[index2 + 1] = value3 & 255;
                        data[index2 + 2] = (value3 >> 8) & 255;
                        data[index2 + 3] = 255;
                    } else {
                        if (useBackground) {
                            value = getRgbBackground();
                        } else {
                            int key3 = (int) (inputValue * this.factor);
                            value = this.colorTable[key3];
                        }
                        int index3 = ((j * w) + i) * 4;
                        data[index3] = value & 255;
                        int value4 = value >> 8;
                        data[index3 + 1] = value4 & 255;
                        data[index3 + 2] = (value4 >> 8) & 255;
                        data[index3 + 3] = 255;
                    }
                } else if (getBackground() != null) {
                    useBackground = true;
                    if (useBackground) {
                        value = getRgbBackground();
                    } else {
                        int key4 = (int) (inputValue * this.factor);
                        value = this.colorTable[key4];
                    }
                    int index4 = ((j * w) + i) * 4;
                    data[index4] = value & 255;
                    int value5 = value >> 8;
                    data[index4 + 1] = value5 & 255;
                    data[index4 + 2] = (value5 >> 8) & 255;
                    data[index4 + 3] = 255;
                }
            }
        }
        raster.setPixels(0, 0, w, h, data);
        return raster;
    }

    private float[] calculateInputValues(double x, double y) {
        double p = (((-(x - ((double) this.coords[0]))) * this.x1x0) - ((y - ((double) this.coords[1])) * this.y1y0)) - (((double) this.coords[2]) * this.r1r0);
        double q = (Math.pow(x - ((double) this.coords[0]), 2.0d) + Math.pow(y - ((double) this.coords[1]), 2.0d)) - this.r0pow2;
        double root = Math.sqrt((p * p) - (this.denom * q));
        float root1 = (float) (((-p) + root) / this.denom);
        float root2 = (float) (((-p) - root) / this.denom);
        if (this.denom < 0.0d) {
            return new float[]{root1, root2};
        }
        return new float[]{root2, root1};
    }

    public float[] getCoords() {
        return this.coords;
    }

    public float[] getDomain() {
        return this.domain;
    }

    public boolean[] getExtend() {
        return this.extend;
    }

    public PDFunction getFunction() throws IOException {
        return this.radialShadingType.getFunction();
    }
}
