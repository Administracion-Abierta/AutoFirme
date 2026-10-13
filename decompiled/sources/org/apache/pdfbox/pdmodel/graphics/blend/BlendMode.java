package org.apache.pdfbox.pdmodel.graphics.blend;

import com.aowagie.text.pdf.ColumnText;
import java.util.HashMap;
import java.util.Map;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSName;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/graphics/blend/BlendMode.class */
public abstract class BlendMode {
    public static final SeparableBlendMode NORMAL = new SeparableBlendMode() { // from class: org.apache.pdfbox.pdmodel.graphics.blend.BlendMode.1
        @Override // org.apache.pdfbox.pdmodel.graphics.blend.SeparableBlendMode
        public float blendChannel(float srcValue, float dstValue) {
            return srcValue;
        }
    };
    public static final SeparableBlendMode COMPATIBLE = NORMAL;
    public static final SeparableBlendMode MULTIPLY = new SeparableBlendMode() { // from class: org.apache.pdfbox.pdmodel.graphics.blend.BlendMode.2
        @Override // org.apache.pdfbox.pdmodel.graphics.blend.SeparableBlendMode
        public float blendChannel(float srcValue, float dstValue) {
            return srcValue * dstValue;
        }
    };
    public static final SeparableBlendMode SCREEN = new SeparableBlendMode() { // from class: org.apache.pdfbox.pdmodel.graphics.blend.BlendMode.3
        @Override // org.apache.pdfbox.pdmodel.graphics.blend.SeparableBlendMode
        public float blendChannel(float srcValue, float dstValue) {
            return (srcValue + dstValue) - (srcValue * dstValue);
        }
    };
    public static final SeparableBlendMode OVERLAY = new SeparableBlendMode() { // from class: org.apache.pdfbox.pdmodel.graphics.blend.BlendMode.4
        @Override // org.apache.pdfbox.pdmodel.graphics.blend.SeparableBlendMode
        public float blendChannel(float srcValue, float dstValue) {
            return ((double) dstValue) <= 0.5d ? 2.0f * dstValue * srcValue : (2.0f * ((srcValue + dstValue) - (srcValue * dstValue))) - 1.0f;
        }
    };
    public static final SeparableBlendMode DARKEN = new SeparableBlendMode() { // from class: org.apache.pdfbox.pdmodel.graphics.blend.BlendMode.5
        @Override // org.apache.pdfbox.pdmodel.graphics.blend.SeparableBlendMode
        public float blendChannel(float srcValue, float dstValue) {
            return Math.min(srcValue, dstValue);
        }
    };
    public static final SeparableBlendMode LIGHTEN = new SeparableBlendMode() { // from class: org.apache.pdfbox.pdmodel.graphics.blend.BlendMode.6
        @Override // org.apache.pdfbox.pdmodel.graphics.blend.SeparableBlendMode
        public float blendChannel(float srcValue, float dstValue) {
            return Math.max(srcValue, dstValue);
        }
    };
    public static final SeparableBlendMode COLOR_DODGE = new SeparableBlendMode() { // from class: org.apache.pdfbox.pdmodel.graphics.blend.BlendMode.7
        @Override // org.apache.pdfbox.pdmodel.graphics.blend.SeparableBlendMode
        public float blendChannel(float srcValue, float dstValue) {
            if (dstValue == ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
                return ColumnText.GLOBAL_SPACE_CHAR_RATIO;
            }
            if (dstValue >= 1.0f - srcValue) {
                return 1.0f;
            }
            return dstValue / (1.0f - srcValue);
        }
    };
    public static final SeparableBlendMode COLOR_BURN = new SeparableBlendMode() { // from class: org.apache.pdfbox.pdmodel.graphics.blend.BlendMode.8
        @Override // org.apache.pdfbox.pdmodel.graphics.blend.SeparableBlendMode
        public float blendChannel(float srcValue, float dstValue) {
            if (dstValue == 1.0f) {
                return 1.0f;
            }
            if (1.0f - dstValue >= srcValue) {
                return ColumnText.GLOBAL_SPACE_CHAR_RATIO;
            }
            return 1.0f - ((1.0f - dstValue) / srcValue);
        }
    };
    public static final SeparableBlendMode HARD_LIGHT = new SeparableBlendMode() { // from class: org.apache.pdfbox.pdmodel.graphics.blend.BlendMode.9
        @Override // org.apache.pdfbox.pdmodel.graphics.blend.SeparableBlendMode
        public float blendChannel(float srcValue, float dstValue) {
            return ((double) srcValue) <= 0.5d ? 2.0f * dstValue * srcValue : (2.0f * ((srcValue + dstValue) - (srcValue * dstValue))) - 1.0f;
        }
    };
    public static final SeparableBlendMode SOFT_LIGHT = new SeparableBlendMode() { // from class: org.apache.pdfbox.pdmodel.graphics.blend.BlendMode.10
        @Override // org.apache.pdfbox.pdmodel.graphics.blend.SeparableBlendMode
        public float blendChannel(float srcValue, float dstValue) {
            if (srcValue <= 0.5d) {
                return dstValue - (((1.0f - (2.0f * srcValue)) * dstValue) * (1.0f - dstValue));
            }
            float d = ((double) dstValue) <= 0.25d ? ((((16.0f * dstValue) - 12.0f) * dstValue) + 4.0f) * dstValue : (float) Math.sqrt(dstValue);
            return dstValue + (((2.0f * srcValue) - 1.0f) * (d - dstValue));
        }
    };
    public static final SeparableBlendMode DIFFERENCE = new SeparableBlendMode() { // from class: org.apache.pdfbox.pdmodel.graphics.blend.BlendMode.11
        @Override // org.apache.pdfbox.pdmodel.graphics.blend.SeparableBlendMode
        public float blendChannel(float srcValue, float dstValue) {
            return Math.abs(dstValue - srcValue);
        }
    };
    public static final SeparableBlendMode EXCLUSION = new SeparableBlendMode() { // from class: org.apache.pdfbox.pdmodel.graphics.blend.BlendMode.12
        @Override // org.apache.pdfbox.pdmodel.graphics.blend.SeparableBlendMode
        public float blendChannel(float srcValue, float dstValue) {
            return (dstValue + srcValue) - ((2.0f * dstValue) * srcValue);
        }
    };
    public static final NonSeparableBlendMode HUE = new NonSeparableBlendMode() { // from class: org.apache.pdfbox.pdmodel.graphics.blend.BlendMode.13
        @Override // org.apache.pdfbox.pdmodel.graphics.blend.NonSeparableBlendMode
        public void blend(float[] srcValues, float[] dstValues, float[] result) {
            float[] temp = new float[3];
            BlendMode.getSaturationRGB(dstValues, srcValues, temp);
            BlendMode.getLuminosityRGB(dstValues, temp, result);
        }
    };
    public static final NonSeparableBlendMode SATURATION = new NonSeparableBlendMode() { // from class: org.apache.pdfbox.pdmodel.graphics.blend.BlendMode.14
        @Override // org.apache.pdfbox.pdmodel.graphics.blend.NonSeparableBlendMode
        public void blend(float[] srcValues, float[] dstValues, float[] result) {
            BlendMode.getSaturationRGB(srcValues, dstValues, result);
        }
    };
    public static final NonSeparableBlendMode COLOR = new NonSeparableBlendMode() { // from class: org.apache.pdfbox.pdmodel.graphics.blend.BlendMode.15
        @Override // org.apache.pdfbox.pdmodel.graphics.blend.NonSeparableBlendMode
        public void blend(float[] srcValues, float[] dstValues, float[] result) {
            BlendMode.getLuminosityRGB(dstValues, srcValues, result);
        }
    };
    public static final NonSeparableBlendMode LUMINOSITY = new NonSeparableBlendMode() { // from class: org.apache.pdfbox.pdmodel.graphics.blend.BlendMode.16
        @Override // org.apache.pdfbox.pdmodel.graphics.blend.NonSeparableBlendMode
        public void blend(float[] srcValues, float[] dstValues, float[] result) {
            BlendMode.getLuminosityRGB(srcValues, dstValues, result);
        }
    };
    private static final Map<COSName, BlendMode> BLEND_MODES = createBlendModeMap();
    private static final Map<BlendMode, COSName> BLEND_MODE_NAMES = createBlendModeNamesMap();

    BlendMode() {
    }

    public static BlendMode getInstance(COSBase cosBlendMode) {
        BlendMode result = null;
        if (cosBlendMode instanceof COSName) {
            result = BLEND_MODES.get(cosBlendMode);
        } else if (cosBlendMode instanceof COSArray) {
            COSArray cosBlendModeArray = (COSArray) cosBlendMode;
            for (int i = 0; i < cosBlendModeArray.size(); i++) {
                result = BLEND_MODES.get(cosBlendModeArray.getObject(i));
                if (result != null) {
                    break;
                }
            }
        }
        if (result != null) {
            return result;
        }
        return NORMAL;
    }

    public static COSName getCOSName(BlendMode bm) {
        return BLEND_MODE_NAMES.get(bm);
    }

    private static int get255Value(float val) {
        return (int) Math.floor(((double) val) >= 1.0d ? 255.0d : ((double) val) * 255.0d);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void getSaturationRGB(float[] srcValues, float[] dstValues, float[] result) {
        int scalemin;
        int scalemax;
        int rd = get255Value(dstValues[0]);
        int gd = get255Value(dstValues[1]);
        int bd = get255Value(dstValues[2]);
        int rs = get255Value(srcValues[0]);
        int gs = get255Value(srcValues[1]);
        int bs = get255Value(srcValues[2]);
        int minb = Math.min(rd, Math.min(gd, bd));
        int maxb = Math.max(rd, Math.max(gd, bd));
        if (minb == maxb) {
            result[0] = gd / 255.0f;
            result[1] = gd / 255.0f;
            result[2] = gd / 255.0f;
            return;
        }
        int mins = Math.min(rs, Math.min(gs, bs));
        int maxs = Math.max(rs, Math.max(gs, bs));
        int scale = ((maxs - mins) << 16) / (maxb - minb);
        int y = ((((rd * 77) + (gd * 151)) + (bd * 28)) + 128) >> 8;
        int r = y + ((((rd - y) * scale) + 32768) >> 16);
        int g = y + ((((gd - y) * scale) + 32768) >> 16);
        int b = y + ((((bd - y) * scale) + 32768) >> 16);
        if (((r | g | b) & 256) == 256) {
            int min = Math.min(r, Math.min(g, b));
            int max = Math.max(r, Math.max(g, b));
            if (min < 0) {
                scalemin = (y << 16) / (y - min);
            } else {
                scalemin = 65536;
            }
            if (max > 255) {
                scalemax = ((255 - y) << 16) / (max - y);
            } else {
                scalemax = 65536;
            }
            int scale2 = Math.min(scalemin, scalemax);
            r = y + ((((r - y) * scale2) + 32768) >> 16);
            g = y + ((((g - y) * scale2) + 32768) >> 16);
            b = y + ((((b - y) * scale2) + 32768) >> 16);
        }
        result[0] = r / 255.0f;
        result[1] = g / 255.0f;
        result[2] = b / 255.0f;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void getLuminosityRGB(float[] srcValues, float[] dstValues, float[] result) {
        int scale;
        int rd = get255Value(dstValues[0]);
        int gd = get255Value(dstValues[1]);
        int bd = get255Value(dstValues[2]);
        int rs = get255Value(srcValues[0]);
        int gs = get255Value(srcValues[1]);
        int bs = get255Value(srcValues[2]);
        int delta = (((((rs - rd) * 77) + ((gs - gd) * 151)) + ((bs - bd) * 28)) + 128) >> 8;
        int r = rd + delta;
        int g = gd + delta;
        int b = bd + delta;
        if (((r | g | b) & 256) == 256) {
            int y = ((((rs * 77) + (gs * 151)) + (bs * 28)) + 128) >> 8;
            if (delta > 0) {
                int max = Math.max(r, Math.max(g, b));
                scale = max == y ? 0 : ((255 - y) << 16) / (max - y);
            } else {
                int min = Math.min(r, Math.min(g, b));
                scale = y == min ? 0 : (y << 16) / (y - min);
            }
            r = y + ((((r - y) * scale) + 32768) >> 16);
            g = y + ((((g - y) * scale) + 32768) >> 16);
            b = y + ((((b - y) * scale) + 32768) >> 16);
        }
        result[0] = r / 255.0f;
        result[1] = g / 255.0f;
        result[2] = b / 255.0f;
    }

    private static Map<COSName, BlendMode> createBlendModeMap() {
        Map<COSName, BlendMode> map = new HashMap<>(13);
        map.put(COSName.NORMAL, NORMAL);
        map.put(COSName.COMPATIBLE, NORMAL);
        map.put(COSName.MULTIPLY, MULTIPLY);
        map.put(COSName.SCREEN, SCREEN);
        map.put(COSName.OVERLAY, OVERLAY);
        map.put(COSName.DARKEN, DARKEN);
        map.put(COSName.LIGHTEN, LIGHTEN);
        map.put(COSName.COLOR_DODGE, COLOR_DODGE);
        map.put(COSName.COLOR_BURN, COLOR_BURN);
        map.put(COSName.HARD_LIGHT, HARD_LIGHT);
        map.put(COSName.SOFT_LIGHT, SOFT_LIGHT);
        map.put(COSName.DIFFERENCE, DIFFERENCE);
        map.put(COSName.EXCLUSION, EXCLUSION);
        map.put(COSName.HUE, HUE);
        map.put(COSName.SATURATION, SATURATION);
        map.put(COSName.LUMINOSITY, LUMINOSITY);
        map.put(COSName.COLOR, COLOR);
        return map;
    }

    private static Map<BlendMode, COSName> createBlendModeNamesMap() {
        Map<BlendMode, COSName> map = new HashMap<>(13);
        map.put(NORMAL, COSName.NORMAL);
        map.put(COMPATIBLE, COSName.NORMAL);
        map.put(MULTIPLY, COSName.MULTIPLY);
        map.put(SCREEN, COSName.SCREEN);
        map.put(OVERLAY, COSName.OVERLAY);
        map.put(DARKEN, COSName.DARKEN);
        map.put(LIGHTEN, COSName.LIGHTEN);
        map.put(COLOR_DODGE, COSName.COLOR_DODGE);
        map.put(COLOR_BURN, COSName.COLOR_BURN);
        map.put(HARD_LIGHT, COSName.HARD_LIGHT);
        map.put(SOFT_LIGHT, COSName.SOFT_LIGHT);
        map.put(DIFFERENCE, COSName.DIFFERENCE);
        map.put(EXCLUSION, COSName.EXCLUSION);
        map.put(HUE, COSName.HUE);
        map.put(SATURATION, COSName.SATURATION);
        map.put(LUMINOSITY, COSName.LUMINOSITY);
        map.put(COLOR, COSName.COLOR);
        return map;
    }
}
