package org.apache.pdfbox.pdmodel.graphics.blend;

import com.aowagie.text.pdf.ColumnText;
import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.CompositeContext;
import java.awt.RenderingHints;
import java.awt.color.ColorSpace;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/graphics/blend/BlendComposite.class */
public final class BlendComposite implements Composite {
    private static final Log LOG = LogFactory.getLog(BlendComposite.class);
    private final BlendMode blendMode;
    private final float constantAlpha;

    public static Composite getInstance(BlendMode blendMode, float constantAlpha) {
        if (constantAlpha < ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
            LOG.warn("using 0 instead of incorrect Alpha " + constantAlpha);
            constantAlpha = 0.0f;
        } else if (constantAlpha > 1.0f) {
            LOG.warn("using 1 instead of incorrect Alpha " + constantAlpha);
            constantAlpha = 1.0f;
        }
        if (blendMode == BlendMode.NORMAL) {
            return AlphaComposite.getInstance(3, constantAlpha);
        }
        return new BlendComposite(blendMode, constantAlpha);
    }

    private BlendComposite(BlendMode blendMode, float constantAlpha) {
        this.blendMode = blendMode;
        this.constantAlpha = constantAlpha;
    }

    public CompositeContext createContext(ColorModel srcColorModel, ColorModel dstColorModel, RenderingHints hints) {
        return new BlendCompositeContext(srcColorModel, dstColorModel);
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/graphics/blend/BlendComposite$BlendCompositeContext.class */
    class BlendCompositeContext implements CompositeContext {
        private final ColorModel srcColorModel;
        private final ColorModel dstColorModel;

        BlendCompositeContext(ColorModel srcColorModel, ColorModel dstColorModel) {
            this.srcColorModel = srcColorModel;
            this.dstColorModel = dstColorModel;
        }

        public void dispose() {
        }

        public void compose(Raster src, Raster dstIn, WritableRaster dstOut) {
            float[] srcConverted;
            float[] dstConverted;
            float[] srcConverted2;
            int x0 = src.getMinX();
            int y0 = src.getMinY();
            int width = Math.min(Math.min(src.getWidth(), dstIn.getWidth()), dstOut.getWidth());
            int height = Math.min(Math.min(src.getHeight(), dstIn.getHeight()), dstOut.getHeight());
            int x1 = x0 + width;
            int y1 = y0 + height;
            int dstInXShift = dstIn.getMinX() - x0;
            int dstInYShift = dstIn.getMinY() - y0;
            int dstOutXShift = dstOut.getMinX() - x0;
            int dstOutYShift = dstOut.getMinY() - y0;
            ColorSpace srcColorSpace = this.srcColorModel.getColorSpace();
            int numSrcColorComponents = this.srcColorModel.getNumColorComponents();
            int numSrcComponents = src.getNumBands();
            boolean srcHasAlpha = numSrcComponents > numSrcColorComponents;
            ColorSpace dstColorSpace = this.dstColorModel.getColorSpace();
            int numDstColorComponents = this.dstColorModel.getNumColorComponents();
            int numDstComponents = dstIn.getNumBands();
            boolean dstHasAlpha = numDstComponents > numDstColorComponents;
            int srcColorSpaceType = srcColorSpace.getType();
            int dstColorSpaceType = dstColorSpace.getType();
            boolean subtractive = (dstColorSpaceType == 5 || dstColorSpaceType == 6) ? false : true;
            boolean blendModeIsSeparable = BlendComposite.this.blendMode instanceof SeparableBlendMode;
            SeparableBlendMode separableBlendMode = blendModeIsSeparable ? (SeparableBlendMode) BlendComposite.this.blendMode : null;
            NonSeparableBlendMode nonSeparableBlendMode = !blendModeIsSeparable ? (NonSeparableBlendMode) BlendComposite.this.blendMode : null;
            boolean needsColorConversion = !srcColorSpace.equals(dstColorSpace);
            Object srcPixel = null;
            Object dstPixel = null;
            float[] srcComponents = new float[numSrcComponents];
            float[] dstComponents = null;
            float[] srcColor = new float[numSrcColorComponents];
            float[] rgbResult = blendModeIsSeparable ? null : new float[dstHasAlpha ? 4 : 3];
            for (int y = y0; y < y1; y++) {
                for (int x = x0; x < x1; x++) {
                    srcPixel = src.getDataElements(x, y, srcPixel);
                    Object dstPixel2 = dstIn.getDataElements(dstInXShift + x, dstInYShift + y, dstPixel);
                    srcComponents = this.srcColorModel.getNormalizedComponents(srcPixel, srcComponents, 0);
                    dstComponents = this.dstColorModel.getNormalizedComponents(dstPixel2, dstComponents, 0);
                    float srcAlpha = srcHasAlpha ? srcComponents[numSrcColorComponents] : 1.0f;
                    float dstAlpha = dstHasAlpha ? dstComponents[numDstColorComponents] : 1.0f;
                    float srcAlpha2 = srcAlpha * BlendComposite.this.constantAlpha;
                    float resultAlpha = (dstAlpha + srcAlpha2) - (srcAlpha2 * dstAlpha);
                    float srcAlphaRatio = resultAlpha > ColumnText.GLOBAL_SPACE_CHAR_RATIO ? srcAlpha2 / resultAlpha : ColumnText.GLOBAL_SPACE_CHAR_RATIO;
                    if (separableBlendMode != null) {
                        System.arraycopy(srcComponents, 0, srcColor, 0, numSrcColorComponents);
                        if (needsColorConversion) {
                            float[] cieXYZ = srcColorSpace.toCIEXYZ(srcColor);
                            srcConverted2 = dstColorSpace.fromCIEXYZ(cieXYZ);
                        } else {
                            srcConverted2 = srcColor;
                        }
                        for (int k = 0; k < numDstColorComponents; k++) {
                            float srcValue = srcConverted2[k];
                            float dstValue = dstComponents[k];
                            if (subtractive) {
                                srcValue = 1.0f - srcValue;
                                dstValue = 1.0f - dstValue;
                            }
                            float value = separableBlendMode.blendChannel(srcValue, dstValue);
                            float value2 = dstValue + (srcAlphaRatio * ((srcValue + (dstAlpha * (value - srcValue))) - dstValue));
                            if (subtractive) {
                                value2 = 1.0f - value2;
                            }
                            dstComponents[k] = value2;
                        }
                    } else {
                        if (srcColorSpaceType == 5) {
                            srcConverted = srcComponents;
                        } else {
                            srcConverted = srcColorSpace.toRGB(srcComponents);
                        }
                        if (dstColorSpaceType == 5) {
                            dstConverted = dstComponents;
                        } else {
                            dstConverted = dstColorSpace.toRGB(dstComponents);
                        }
                        nonSeparableBlendMode.blend(srcConverted, dstConverted, rgbResult);
                        for (int k2 = 0; k2 < 3; k2++) {
                            float srcValue2 = srcConverted[k2];
                            float dstValue2 = dstConverted[k2];
                            float value3 = rgbResult[k2];
                            rgbResult[k2] = dstValue2 + (srcAlphaRatio * ((srcValue2 + (dstAlpha * (Math.max(Math.min(value3, 1.0f), ColumnText.GLOBAL_SPACE_CHAR_RATIO) - srcValue2))) - dstValue2));
                        }
                        if (dstColorSpaceType == 5) {
                            System.arraycopy(rgbResult, 0, dstComponents, 0, dstComponents.length);
                        } else {
                            float[] temp = dstColorSpace.fromRGB(rgbResult);
                            System.arraycopy(temp, 0, dstComponents, 0, Math.min(dstComponents.length, temp.length));
                        }
                    }
                    if (dstHasAlpha) {
                        dstComponents[numDstColorComponents] = resultAlpha;
                    }
                    dstPixel = this.dstColorModel.getDataElements(dstComponents, 0, dstPixel2);
                    dstOut.setDataElements(dstOutXShift + x, dstOutYShift + y, dstPixel);
                }
            }
        }
    }
}
