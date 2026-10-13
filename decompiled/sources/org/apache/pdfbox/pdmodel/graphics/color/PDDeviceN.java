package org.apache.pdfbox.pdmodel.graphics.color;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSNull;
import org.apache.pdfbox.pdmodel.common.COSArrayList;
import org.apache.pdfbox.pdmodel.common.function.PDFunction;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/graphics/color/PDDeviceN.class */
public class PDDeviceN extends PDSpecialColorSpace {
    private static final int COLORANT_NAMES = 1;
    private static final int ALTERNATE_CS = 2;
    private static final int TINT_TRANSFORM = 3;
    private static final int DEVICEN_ATTRIBUTES = 4;
    private PDColorSpace alternateColorSpace;
    private PDFunction tintTransform;
    private PDDeviceNAttributes attributes;
    private PDColor initialColor;
    private int numColorants;
    private int[] colorantToComponent;
    private PDColorSpace processColorSpace;
    private PDSeparation[] spotColorSpaces;

    public PDDeviceN() {
        this.alternateColorSpace = null;
        this.tintTransform = null;
        this.array = new COSArray();
        this.array.add((COSBase) COSName.DEVICEN);
        this.array.add((COSBase) COSNull.NULL);
        this.array.add((COSBase) COSNull.NULL);
        this.array.add((COSBase) COSNull.NULL);
    }

    public PDDeviceN(COSArray deviceN) throws IOException {
        this.alternateColorSpace = null;
        this.tintTransform = null;
        this.array = deviceN;
        this.alternateColorSpace = PDColorSpace.create(this.array.getObject(2));
        this.tintTransform = PDFunction.create(this.array.getObject(3));
        if (this.array.size() > 4) {
            this.attributes = new PDDeviceNAttributes((COSDictionary) this.array.getObject(4));
        }
        initColorConversionCache();
        int n = getNumberOfComponents();
        float[] initial = new float[n];
        for (int i = 0; i < n; i++) {
            initial[i] = 1.0f;
        }
        this.initialColor = new PDColor(initial, this);
    }

    private void initColorConversionCache() throws IOException {
        if (this.attributes == null) {
            return;
        }
        List<String> colorantNames = getColorantNames();
        this.numColorants = colorantNames.size();
        this.colorantToComponent = new int[this.numColorants];
        for (int c = 0; c < this.numColorants; c++) {
            this.colorantToComponent[c] = -1;
        }
        if (this.attributes.getProcess() != null) {
            List<String> components = this.attributes.getProcess().getComponents();
            for (int c2 = 0; c2 < this.numColorants; c2++) {
                this.colorantToComponent[c2] = components.indexOf(colorantNames.get(c2));
            }
            this.processColorSpace = this.attributes.getProcess().getColorSpace();
        }
        this.spotColorSpaces = new PDSeparation[this.numColorants];
        Map<String, PDSeparation> spotColorants = this.attributes.getColorants();
        for (int c3 = 0; c3 < this.numColorants; c3++) {
            String name = colorantNames.get(c3);
            PDSeparation spot = spotColorants.get(name);
            if (spot != null) {
                this.spotColorSpaces[c3] = spot;
                if (!isNChannel()) {
                    this.colorantToComponent[c3] = -1;
                }
            } else {
                this.spotColorSpaces[c3] = null;
            }
        }
    }

    @Override // org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace
    public BufferedImage toRGBImage(WritableRaster raster) throws IOException {
        if (this.attributes != null) {
            return toRGBWithAttributes(raster);
        }
        return toRGBWithTintTransform(raster);
    }

    private BufferedImage toRGBWithAttributes(WritableRaster raster) throws IOException {
        PDColorSpace componentColorSpace;
        int width = raster.getWidth();
        int height = raster.getHeight();
        BufferedImage rgbImage = new BufferedImage(width, height, 1);
        WritableRaster rgbRaster = rgbImage.getRaster();
        Graphics2D g = rgbImage.createGraphics();
        g.setBackground(Color.WHITE);
        g.clearRect(0, 0, width, height);
        g.dispose();
        for (int c = 0; c < this.numColorants; c++) {
            if (this.colorantToComponent[c] >= 0) {
                componentColorSpace = this.processColorSpace;
            } else {
                if (this.spotColorSpaces[c] == null) {
                    return toRGBWithTintTransform(raster);
                }
                componentColorSpace = this.spotColorSpaces[c];
            }
            WritableRaster componentRaster = Raster.createBandedRaster(0, width, height, componentColorSpace.getNumberOfComponents(), new Point(0, 0));
            int[] samples = new int[this.numColorants];
            int[] componentSamples = new int[componentColorSpace.getNumberOfComponents()];
            boolean isProcessColorant = this.colorantToComponent[c] >= 0;
            int componentIndex = this.colorantToComponent[c];
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    raster.getPixel(x, y, samples);
                    if (isProcessColorant) {
                        componentSamples[componentIndex] = samples[c];
                    } else {
                        componentSamples[0] = samples[c];
                    }
                    componentRaster.setPixel(x, y, componentSamples);
                }
            }
            BufferedImage rgbComponentImage = componentColorSpace.toRGBImage(componentRaster);
            WritableRaster rgbComponentRaster = rgbComponentImage.getRaster();
            int[] rgbChannel = new int[3];
            int[] rgbComposite = new int[3];
            for (int y2 = 0; y2 < height; y2++) {
                for (int x2 = 0; x2 < width; x2++) {
                    rgbComponentRaster.getPixel(x2, y2, rgbChannel);
                    rgbRaster.getPixel(x2, y2, rgbComposite);
                    rgbChannel[0] = (rgbChannel[0] * rgbComposite[0]) >> 8;
                    rgbChannel[1] = (rgbChannel[1] * rgbComposite[1]) >> 8;
                    rgbChannel[2] = (rgbChannel[2] * rgbComposite[2]) >> 8;
                    rgbRaster.setPixel(x2, y2, rgbChannel);
                }
            }
        }
        return rgbImage;
    }

    private BufferedImage toRGBWithTintTransform(WritableRaster raster) throws IOException {
        HashMap map = new HashMap();
        StringBuilder keyBuilder = new StringBuilder();
        int width = raster.getWidth();
        int height = raster.getHeight();
        BufferedImage rgbImage = new BufferedImage(width, height, 1);
        WritableRaster rgbRaster = rgbImage.getRaster();
        int[] rgb = new int[3];
        int numSrcComponents = getColorantNames().size();
        float[] src = new float[numSrcComponents];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                raster.getPixel(x, y, src);
                keyBuilder.append(src[0]);
                for (int s = 1; s < numSrcComponents; s++) {
                    keyBuilder.append('#').append(src[s]);
                }
                String key = keyBuilder.toString();
                keyBuilder.setLength(0);
                int[] pxl = (int[]) map.get(key);
                if (pxl != null) {
                    rgbRaster.setPixel(x, y, pxl);
                } else {
                    for (int s2 = 0; s2 < numSrcComponents; s2++) {
                        src[s2] = src[s2] / 255.0f;
                    }
                    float[] result = this.tintTransform.eval(src);
                    float[] rgbFloat = this.alternateColorSpace.toRGB(result);
                    rgb[0] = (int) (rgbFloat[0] * 255.0f);
                    rgb[1] = (int) (rgbFloat[1] * 255.0f);
                    rgb[2] = (int) (rgbFloat[2] * 255.0f);
                    map.put(key, rgb.clone());
                    rgbRaster.setPixel(x, y, rgb);
                }
            }
        }
        return rgbImage;
    }

    @Override // org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace
    public float[] toRGB(float[] value) throws IOException {
        if (this.attributes != null) {
            return toRGBWithAttributes(value);
        }
        return toRGBWithTintTransform(value);
    }

    private float[] toRGBWithAttributes(float[] value) throws IOException {
        PDColorSpace componentColorSpace;
        float[] rgbValue = {1.0f, 1.0f, 1.0f};
        for (int c = 0; c < this.numColorants; c++) {
            boolean isProcessColorant = this.colorantToComponent[c] >= 0;
            if (isProcessColorant) {
                componentColorSpace = this.processColorSpace;
            } else {
                if (this.spotColorSpaces[c] == null) {
                    return toRGBWithTintTransform(value);
                }
                componentColorSpace = this.spotColorSpaces[c];
            }
            float[] componentSamples = new float[componentColorSpace.getNumberOfComponents()];
            if (isProcessColorant) {
                int componentIndex = this.colorantToComponent[c];
                componentSamples[componentIndex] = value[c];
            } else {
                componentSamples[0] = value[c];
            }
            float[] rgbComponent = componentColorSpace.toRGB(componentSamples);
            rgbValue[0] = rgbValue[0] * rgbComponent[0];
            rgbValue[1] = rgbValue[1] * rgbComponent[1];
            rgbValue[2] = rgbValue[2] * rgbComponent[2];
        }
        return rgbValue;
    }

    private float[] toRGBWithTintTransform(float[] value) throws IOException {
        float[] altValue = this.tintTransform.eval(value);
        return this.alternateColorSpace.toRGB(altValue);
    }

    @Override // org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace
    public BufferedImage toRawImage(WritableRaster raster) {
        return null;
    }

    public boolean isNChannel() {
        return this.attributes != null && this.attributes.isNChannel();
    }

    @Override // org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace
    public String getName() {
        return COSName.DEVICEN.getName();
    }

    @Override // org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace
    public final int getNumberOfComponents() {
        return getColorantNames().size();
    }

    @Override // org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace
    public float[] getDefaultDecode(int bitsPerComponent) {
        int n = getNumberOfComponents();
        float[] decode = new float[n * 2];
        for (int i = 0; i < n; i++) {
            decode[(i * 2) + 1] = 1.0f;
        }
        return decode;
    }

    @Override // org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace
    public PDColor getInitialColor() {
        return this.initialColor;
    }

    public List<String> getColorantNames() {
        COSArray names = (COSArray) this.array.getObject(1);
        return COSArrayList.convertCOSNameCOSArrayToList(names);
    }

    public PDDeviceNAttributes getAttributes() {
        return this.attributes;
    }

    public void setColorantNames(List<String> names) {
        COSArray namesArray = COSArrayList.convertStringListToCOSNameCOSArray(names);
        this.array.set(1, (COSBase) namesArray);
    }

    public void setAttributes(PDDeviceNAttributes attributes) {
        this.attributes = attributes;
        if (attributes == null) {
            this.array.remove(4);
            return;
        }
        while (this.array.size() <= 4) {
            this.array.add((COSBase) COSNull.NULL);
        }
        this.array.set(4, (COSBase) attributes.getCOSDictionary());
    }

    public PDColorSpace getAlternateColorSpace() throws IOException {
        if (this.alternateColorSpace == null) {
            this.alternateColorSpace = PDColorSpace.create(this.array.getObject(2));
        }
        return this.alternateColorSpace;
    }

    public void setAlternateColorSpace(PDColorSpace cs) {
        this.alternateColorSpace = cs;
        COSBase space = null;
        if (cs != null) {
            space = cs.getCOSObject();
        }
        this.array.set(2, space);
    }

    public PDFunction getTintTransform() throws IOException {
        if (this.tintTransform == null) {
            this.tintTransform = PDFunction.create(this.array.getObject(3));
        }
        return this.tintTransform;
    }

    public void setTintTransform(PDFunction tint) {
        this.tintTransform = tint;
        this.array.set(3, tint);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(getName());
        sb.append('{');
        for (String col : getColorantNames()) {
            sb.append('\"');
            sb.append(col);
            sb.append("\" ");
        }
        sb.append(this.alternateColorSpace.getName());
        sb.append(' ');
        sb.append(this.tintTransform);
        sb.append(' ');
        if (this.attributes != null) {
            sb.append(this.attributes);
        }
        sb.append('}');
        return sb.toString();
    }
}
