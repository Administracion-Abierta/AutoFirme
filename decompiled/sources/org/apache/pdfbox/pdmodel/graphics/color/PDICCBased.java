package org.apache.pdfbox.pdmodel.graphics.color;

import com.aowagie.text.pdf.ColumnText;
import java.awt.color.CMMException;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;
import java.awt.color.ProfileDataException;
import java.awt.image.BufferedImage;
import java.awt.image.ComponentColorModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSFloat;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSObject;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.COSArrayList;
import org.apache.pdfbox.pdmodel.common.PDRange;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.util.Charsets;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/graphics/color/PDICCBased.class */
public final class PDICCBased extends PDCIEBasedColorSpace {
    private static final Log LOG = LogFactory.getLog(PDICCBased.class);
    private final PDStream stream;
    private int numberOfComponents;
    private ICC_Profile iccProfile;
    private PDColorSpace alternateColorSpace;
    private ICC_ColorSpace awtColorSpace;
    private PDColor initialColor;
    private boolean isRGB;
    private boolean useOnlyAlternateColorSpace;
    private static final boolean IS_KCMS;

    static {
        String cmmProperty = System.getProperty("sun.java2d.cmm");
        boolean result = false;
        if (!isMinJdk8()) {
            result = true;
        } else if ("sun.java2d.cmm.kcms.KcmsServiceProvider".equals(cmmProperty)) {
            try {
                Class.forName("sun.java2d.cmm.kcms.KcmsServiceProvider");
                result = true;
            } catch (ClassNotFoundException e) {
            }
        }
        IS_KCMS = result;
    }

    public PDICCBased(PDDocument doc) {
        this.numberOfComponents = -1;
        this.isRGB = false;
        this.useOnlyAlternateColorSpace = false;
        this.array = new COSArray();
        this.array.add((COSBase) COSName.ICCBASED);
        this.stream = new PDStream(doc);
        this.array.add(this.stream);
    }

    @Deprecated
    public PDICCBased(COSArray iccArray) throws IOException {
        this.numberOfComponents = -1;
        this.isRGB = false;
        this.useOnlyAlternateColorSpace = false;
        checkArray(iccArray);
        this.useOnlyAlternateColorSpace = System.getProperty("org.apache.pdfbox.rendering.UseAlternateInsteadOfICCColorSpace") != null;
        this.array = iccArray;
        this.stream = new PDStream((COSStream) iccArray.getObject(1));
        loadICCProfile();
    }

    public static PDICCBased create(COSArray iccArray, PDResources resources) throws IOException {
        checkArray(iccArray);
        COSBase base = iccArray.get(1);
        COSObject indirect = null;
        if (base instanceof COSObject) {
            indirect = (COSObject) base;
        }
        if (indirect != null && resources != null && resources.getResourceCache() != null) {
            PDColorSpace space = resources.getResourceCache().getColorSpace(indirect);
            if (space instanceof PDICCBased) {
                return (PDICCBased) space;
            }
        }
        PDICCBased space2 = new PDICCBased(iccArray);
        if (indirect != null && resources != 0 && resources.getResourceCache() != null) {
            resources.getResourceCache().put(indirect, space2);
        }
        return space2;
    }

    private static void checkArray(COSArray iccArray) throws IOException {
        if (iccArray.size() < 2) {
            throw new IOException("ICCBased colorspace array must have two elements");
        }
        if (!(iccArray.getObject(1) instanceof COSStream)) {
            throw new IOException("ICCBased colorspace array must have a stream as second element");
        }
    }

    @Override // org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace
    public String getName() {
        return COSName.ICCBASED.getName();
    }

    public PDStream getPDStream() {
        return this.stream;
    }

    private void loadICCProfile() throws IOException {
        if (this.useOnlyAlternateColorSpace) {
            try {
                fallbackToAlternateColorSpace(null);
                return;
            } catch (IOException e) {
                LOG.warn("Error initializing alternate color space: " + e.getLocalizedMessage());
            }
        }
        InputStream input = null;
        try {
            try {
                try {
                    try {
                        try {
                            try {
                                input = this.stream.createInputStream();
                                synchronized (LOG) {
                                    try {
                                        ICC_Profile profile = ICC_Profile.getInstance(input);
                                        if (is_sRGB(profile)) {
                                            this.isRGB = true;
                                            this.awtColorSpace = ColorSpace.getInstance(1000);
                                            this.iccProfile = this.awtColorSpace.getProfile();
                                        } else {
                                            ICC_Profile profile2 = ensureDisplayProfile(profile);
                                            this.awtColorSpace = new ICC_ColorSpace(profile2);
                                            this.iccProfile = profile2;
                                        }
                                        float[] initial = new float[getNumberOfComponents()];
                                        for (int c = 0; c < initial.length; c++) {
                                            initial[c] = Math.max(ColumnText.GLOBAL_SPACE_CHAR_RATIO, getRangeForComponent(c).getMin());
                                        }
                                        this.initialColor = new PDColor(initial, this);
                                        if (IS_KCMS) {
                                            this.awtColorSpace.toRGB(new float[getNumberOfComponents()]);
                                        } else {
                                            new ComponentColorModel(this.awtColorSpace, false, false, 1, 0);
                                        }
                                    } catch (Throwable th) {
                                        throw th;
                                    }
                                }
                                IOUtils.closeQuietly(input);
                            } catch (Throwable th2) {
                                IOUtils.closeQuietly(input);
                                throw th2;
                            }
                        } catch (CMMException e2) {
                            fallbackToAlternateColorSpace(e2);
                            IOUtils.closeQuietly(input);
                        }
                    } catch (IOException e3) {
                        fallbackToAlternateColorSpace(e3);
                        IOUtils.closeQuietly(input);
                    }
                } catch (ArrayIndexOutOfBoundsException e4) {
                    fallbackToAlternateColorSpace(e4);
                    IOUtils.closeQuietly(input);
                }
            } catch (IllegalArgumentException e5) {
                fallbackToAlternateColorSpace(e5);
                IOUtils.closeQuietly(input);
            }
        } catch (ProfileDataException e6) {
            fallbackToAlternateColorSpace(e6);
            IOUtils.closeQuietly(input);
        }
    }

    private void fallbackToAlternateColorSpace(Exception e) throws IOException {
        this.awtColorSpace = null;
        this.alternateColorSpace = getAlternateColorSpace();
        if (this.alternateColorSpace.equals(PDDeviceRGB.INSTANCE)) {
            this.isRGB = true;
        }
        if (e != null) {
            LOG.warn("Can't read embedded ICC profile (" + e.getLocalizedMessage() + "), using alternate color space: " + this.alternateColorSpace.getName());
        }
        this.initialColor = this.alternateColorSpace.getInitialColor();
    }

    private boolean is_sRGB(ICC_Profile profile) {
        byte[] bytes = Arrays.copyOfRange(profile.getData(1751474532), 52, 59);
        String deviceModel = new String(bytes, Charsets.US_ASCII).trim();
        return deviceModel.equals("sRGB");
    }

    private static ICC_Profile ensureDisplayProfile(ICC_Profile profile) {
        if (profile.getProfileClass() != 1) {
            byte[] profileData = profile.getData();
            if (profileData[64] == 0) {
                LOG.warn("ICC profile is Perceptual, ignoring, treating as Display class");
                intToBigEndian(1835955314, profileData, 12);
                return ICC_Profile.getInstance(profileData);
            }
        }
        return profile;
    }

    private static void intToBigEndian(int value, byte[] array, int index) {
        array[index] = (byte) (value >> 24);
        array[index + 1] = (byte) (value >> 16);
        array[index + 2] = (byte) (value >> 8);
        array[index + 3] = (byte) value;
    }

    @Override // org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace
    public float[] toRGB(float[] value) throws IOException {
        if (this.isRGB) {
            return value;
        }
        if (this.awtColorSpace != null) {
            return this.awtColorSpace.toRGB(clampColors(this.awtColorSpace, value));
        }
        return this.alternateColorSpace.toRGB(value);
    }

    private float[] clampColors(ICC_ColorSpace cs, float[] value) {
        float[] result = new float[value.length];
        for (int i = 0; i < value.length; i++) {
            float minValue = cs.getMinValue(i);
            float maxValue = cs.getMaxValue(i);
            result[i] = value[i] < minValue ? minValue : value[i] > maxValue ? maxValue : value[i];
        }
        return result;
    }

    @Override // org.apache.pdfbox.pdmodel.graphics.color.PDCIEBasedColorSpace, org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace
    public BufferedImage toRGBImage(WritableRaster raster) throws IOException {
        if (this.awtColorSpace != null) {
            return toRGBImageAWT(raster, this.awtColorSpace);
        }
        return this.alternateColorSpace.toRGBImage(raster);
    }

    @Override // org.apache.pdfbox.pdmodel.graphics.color.PDCIEBasedColorSpace, org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace
    public BufferedImage toRawImage(WritableRaster raster) throws IOException {
        if (this.awtColorSpace == null) {
            return this.alternateColorSpace.toRawImage(raster);
        }
        return toRawImage(raster, this.awtColorSpace);
    }

    @Override // org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace
    public int getNumberOfComponents() {
        int numIccComponents;
        if (this.numberOfComponents < 0) {
            this.numberOfComponents = this.stream.getCOSObject().getInt(COSName.N);
            if (this.iccProfile != null && (numIccComponents = this.iccProfile.getNumComponents()) != this.numberOfComponents) {
                LOG.warn("Using " + numIccComponents + " components from ICC profile info instead of " + this.numberOfComponents + " components from /N entry");
                this.numberOfComponents = numIccComponents;
            }
        }
        return this.numberOfComponents;
    }

    @Override // org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace
    public float[] getDefaultDecode(int bitsPerComponent) {
        if (this.awtColorSpace != null) {
            int n = getNumberOfComponents();
            float[] decode = new float[n * 2];
            for (int i = 0; i < n; i++) {
                decode[i * 2] = this.awtColorSpace.getMinValue(i);
                decode[(i * 2) + 1] = this.awtColorSpace.getMaxValue(i);
            }
            return decode;
        }
        return this.alternateColorSpace.getDefaultDecode(bitsPerComponent);
    }

    @Override // org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace
    public PDColor getInitialColor() {
        return this.initialColor;
    }

    public PDColorSpace getAlternateColorSpace() throws IOException {
        COSArray alternateArray;
        COSName csName;
        COSBase alternate = this.stream.getCOSObject().getDictionaryObject(COSName.ALTERNATE);
        if (alternate == null) {
            alternateArray = new COSArray();
            int numComponents = getNumberOfComponents();
            switch (numComponents) {
                case 1:
                    csName = COSName.DEVICEGRAY;
                    break;
                case 2:
                default:
                    throw new IOException("Unknown color space number of components:" + numComponents);
                case 3:
                    csName = COSName.DEVICERGB;
                    break;
                case 4:
                    csName = COSName.DEVICECMYK;
                    break;
            }
            alternateArray.add((COSBase) csName);
        } else if (alternate instanceof COSArray) {
            alternateArray = (COSArray) alternate;
        } else if (alternate instanceof COSName) {
            alternateArray = new COSArray();
            alternateArray.add(alternate);
        } else {
            throw new IOException("Error: expected COSArray or COSName and not " + alternate.getClass().getName());
        }
        return PDColorSpace.create(alternateArray);
    }

    public PDRange getRangeForComponent(int n) {
        COSArray rangeArray = (COSArray) this.stream.getCOSObject().getDictionaryObject(COSName.RANGE);
        if (rangeArray == null || rangeArray.size() < getNumberOfComponents() * 2) {
            return new PDRange();
        }
        return new PDRange(rangeArray, n);
    }

    public COSStream getMetadata() {
        return (COSStream) this.stream.getCOSObject().getDictionaryObject(COSName.METADATA);
    }

    public int getColorSpaceType() {
        if (this.iccProfile != null) {
            return this.iccProfile.getColorSpaceType();
        }
        switch (this.alternateColorSpace.getNumberOfComponents()) {
            case 1:
                return 6;
            case 2:
            default:
                return -1;
            case 3:
                return 5;
            case 4:
                return 9;
        }
    }

    @Deprecated
    public void setNumberOfComponents(int n) {
        this.numberOfComponents = n;
        this.stream.getCOSObject().setInt(COSName.N, n);
    }

    public void setAlternateColorSpaces(List<PDColorSpace> list) {
        COSArray altArray = null;
        if (list != null) {
            altArray = COSArrayList.converterToCOSArray(list);
        }
        this.stream.getCOSObject().setItem(COSName.ALTERNATE, (COSBase) altArray);
    }

    public void setRangeForComponent(PDRange range, int n) {
        COSArray rangeArray = (COSArray) this.stream.getCOSObject().getDictionaryObject(COSName.RANGE);
        if (rangeArray == null) {
            rangeArray = new COSArray();
            this.stream.getCOSObject().setItem(COSName.RANGE, (COSBase) rangeArray);
        }
        while (rangeArray.size() < (n + 1) * 2) {
            rangeArray.add((COSBase) new COSFloat(ColumnText.GLOBAL_SPACE_CHAR_RATIO));
            rangeArray.add((COSBase) new COSFloat(1.0f));
        }
        rangeArray.set(n * 2, (COSBase) new COSFloat(range.getMin()));
        rangeArray.set((n * 2) + 1, (COSBase) new COSFloat(range.getMax()));
    }

    public void setMetadata(COSStream metadata) {
        this.stream.getCOSObject().setItem(COSName.METADATA, (COSBase) metadata);
    }

    boolean isSRGB() {
        return this.isRGB;
    }

    @Override // org.apache.pdfbox.pdmodel.graphics.color.PDCIEBasedColorSpace
    public String toString() {
        return getName() + "{numberOfComponents: " + getNumberOfComponents() + "}";
    }

    private static boolean isMinJdk8() {
        String version = System.getProperty("java.specification.version");
        StringTokenizer st = new StringTokenizer(version, ".");
        try {
            int major = Integer.parseInt(st.nextToken());
            int minor = 0;
            if (st.hasMoreTokens()) {
                minor = Integer.parseInt(st.nextToken());
            }
            return major > 1 || (major == 1 && minor >= 8);
        } catch (NumberFormatException e) {
            return true;
        }
    }
}
