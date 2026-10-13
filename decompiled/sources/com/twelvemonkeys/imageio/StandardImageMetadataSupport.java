package com.twelvemonkeys.imageio;

import com.aowagie.text.ElementTags;
import com.twelvemonkeys.lang.Validate;
import es.gob.afirma.core.signers.AOSignConstants;
import java.awt.color.ColorSpace;
import java.awt.image.BandedSampleModel;
import java.awt.image.ColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.IndexColorModel;
import java.awt.image.SampleModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import org.apache.pdfbox.pdmodel.documentinterchange.taggedpdf.StandardStructureTypes;
import org.mozilla.javascript.ES6Iterator;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/StandardImageMetadataSupport.class */
public class StandardImageMetadataSupport extends AbstractMetadata {
    private final ImageTypeSpecifier type;
    protected final ColorSpaceType colorSpaceType;
    protected final boolean blackIsZero;
    private final IndexColorModel palette;
    protected final String compressionName;
    protected final boolean compressionLossless;
    protected final PlanarConfiguration planarConfiguration;
    private final int[] bitsPerSample;
    private final int[] significantBits;
    private final int[] sampleMSB;
    protected final Double pixelAspectRatio;
    protected final ImageOrientation orientation;
    protected final String formatVersion;
    protected final SubimageInterpretation subimageInterpretation;
    private final Calendar documentCreationTime;
    private final Collection<TextEntry> textEntries;

    /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/StandardImageMetadataSupport$ImageOrientation.class */
    protected enum ImageOrientation {
        Normal,
        Rotate90,
        Rotate180,
        Rotate270,
        FlipH,
        FlipV,
        FlipHRotate90,
        FlipVRotate90
    }

    /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/StandardImageMetadataSupport$PlanarConfiguration.class */
    protected enum PlanarConfiguration {
        PixelInterleaved,
        PlaneInterleaved,
        LineInterleaved,
        TileInterleaved
    }

    /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/StandardImageMetadataSupport$SubimageInterpretation.class */
    protected enum SubimageInterpretation {
        Standalone,
        SinglePage,
        FullResolution,
        ReducedResolution,
        PyramidLayer,
        Preview,
        VolumeSlice,
        ObjectView,
        Panorama,
        AnimationFrame,
        TransparencyMask,
        CompositingLayer,
        SpectralSlice,
        Unknown
    }

    protected StandardImageMetadataSupport(Builder builder) {
        Validate.notNull(builder, "builder");
        this.type = builder.type;
        this.colorSpaceType = builder.colorSpaceType;
        this.blackIsZero = builder.blackIsZero;
        this.palette = builder.palette;
        this.compressionName = builder.compressionName;
        this.compressionLossless = builder.compressionLossless;
        this.planarConfiguration = builder.planarConfiguration;
        this.bitsPerSample = builder.bitsPerSample;
        this.significantBits = builder.significantBits;
        this.sampleMSB = builder.sampleMSB;
        this.orientation = builder.orientation;
        this.pixelAspectRatio = builder.pixelAspectRatio;
        this.formatVersion = builder.formatVersion;
        this.documentCreationTime = builder.documentCreationTime;
        this.subimageInterpretation = builder.subimageInterpretation;
        this.textEntries = builder.textEntries;
    }

    public static Builder builder(ImageTypeSpecifier imageTypeSpecifier) {
        return new Builder(imageTypeSpecifier);
    }

    /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/StandardImageMetadataSupport$Builder.class */
    public static class Builder {
        private final ImageTypeSpecifier type;
        private ColorSpaceType colorSpaceType;
        private IndexColorModel palette;
        private String compressionName;
        private PlanarConfiguration planarConfiguration;
        public int[] bitsPerSample;
        private int[] significantBits;
        private int[] sampleMSB;
        private Double pixelAspectRatio;
        private String formatVersion;
        private SubimageInterpretation subimageInterpretation;
        private Calendar documentCreationTime;
        private boolean blackIsZero = true;
        private boolean compressionLossless = true;
        private ImageOrientation orientation = ImageOrientation.Normal;
        private final Collection<TextEntry> textEntries = new ArrayList();

        protected Builder(ImageTypeSpecifier imageTypeSpecifier) {
            this.type = (ImageTypeSpecifier) Validate.notNull(imageTypeSpecifier, "type");
        }

        public Builder withColorSpaceType(ColorSpaceType colorSpaceType) {
            this.colorSpaceType = colorSpaceType;
            return this;
        }

        public Builder withBlackIsZero(boolean z) {
            this.blackIsZero = z;
            return this;
        }

        public Builder withPalette(IndexColorModel indexColorModel) {
            this.palette = indexColorModel;
            return this;
        }

        public Builder withCompressionTypeName(String str) {
            this.compressionName = ((String) Validate.notNull(str, "compressionName")).equalsIgnoreCase("none") ? null : str;
            return this;
        }

        public Builder withCompressionLossless(boolean z) {
            this.compressionLossless = ((Boolean) Validate.isTrue(z || this.compressionName != null, Boolean.valueOf(z), "Lossy compression requires compression name")).booleanValue();
            return this;
        }

        public Builder withPlanarConfiguration(PlanarConfiguration planarConfiguration) {
            this.planarConfiguration = planarConfiguration;
            return this;
        }

        public Builder withBitsPerSample(int... iArr) {
            this.bitsPerSample = iArr;
            return this;
        }

        public Builder withSignificantBitsPerSample(int... iArr) {
            this.significantBits = (int[]) Validate.isTrue(iArr.length == 1 || iArr.length == this.type.getNumBands(), iArr, String.format("single value or %d values expected", Integer.valueOf(this.type.getNumBands())));
            return this;
        }

        public Builder withSampleMSB(int... iArr) {
            this.sampleMSB = (int[]) Validate.isTrue(iArr.length == 1 || iArr.length == this.type.getNumBands(), iArr, String.format("single value or %d values expected", Integer.valueOf(this.type.getNumBands())));
            return this;
        }

        public Builder withPixelAspectRatio(Double d) {
            this.pixelAspectRatio = d;
            return this;
        }

        public Builder withOrientation(ImageOrientation imageOrientation) {
            this.orientation = (ImageOrientation) Validate.notNull(imageOrientation, ElementTags.ORIENTATION);
            return this;
        }

        public Builder withFormatVersion(String str) {
            this.formatVersion = (String) Validate.notNull(str, "formatVersion");
            return this;
        }

        public Builder withSubimageInterpretation(SubimageInterpretation subimageInterpretation) {
            this.subimageInterpretation = subimageInterpretation;
            return this;
        }

        public Builder withDocumentCreationTime(Calendar calendar) {
            this.documentCreationTime = calendar;
            return this;
        }

        public Builder withTextEntries(Map<String, String> map) {
            return withTextEntries(toTextEntries(((Map) Validate.notNull(map, "entries")).entrySet()));
        }

        private Collection<TextEntry> toTextEntries(Collection<Map.Entry<String, String>> collection) {
            TextEntry[] textEntryArr = new TextEntry[collection.size()];
            int i = 0;
            for (Map.Entry<String, String> entry : collection) {
                int i2 = i;
                i++;
                textEntryArr[i2] = new TextEntry(entry.getKey(), entry.getValue());
            }
            return Arrays.asList(textEntryArr);
        }

        public Builder withTextEntries(Collection<TextEntry> collection) {
            this.textEntries.addAll((Collection) Validate.notNull(collection, "entries"));
            return this;
        }

        public Builder withTextEntry(String str, String str2) {
            if (str2 != null && !str2.isEmpty()) {
                this.textEntries.add(new TextEntry((String) Validate.notNull(str, "keyword"), str2));
            }
            return this;
        }

        public IIOMetadata build() {
            return new StandardImageMetadataSupport(this);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/StandardImageMetadataSupport$ColorSpaceType.class */
    protected enum ColorSpaceType {
        XYZ(3),
        Lab(3),
        Luv(3),
        YCbCr(3),
        Yxy(3),
        YCCK(4),
        PhotoYCC(3),
        RGB(3),
        GRAY(1),
        HSV(3),
        HLS(3),
        CMYK(3),
        CMY(3),
        GENERIC_2CLR(2, "2CLR"),
        GENERIC_3CLR(3, "3CLR"),
        GENERIC_4CLR(4, "4CLR"),
        GENERIC_5CLR(5, "5CLR"),
        GENERIC_6CLR(6, "6CLR"),
        GENERIC_7CLR(7, "7CLR"),
        GENERIC_8CLR(8, "8CLR"),
        GENERIC_9CLR(9, "9CLR"),
        GENERIC_ACLR(10, "ACLR"),
        GENERIC_BCLR(11, "BCLR"),
        GENERIC_CCLR(12, "CCLR"),
        GENERIC_DCLR(13, "DCLR"),
        GENERIC_ECLR(14, "ECLR"),
        GENERIC_FCLR(15, "FCLR");

        final int numChannels;
        private final String nameOverride;

        ColorSpaceType(int i) {
            this(i, null);
        }

        ColorSpaceType(int i, String str) {
            this.numChannels = i;
            this.nameOverride = str;
        }

        @Override // java.lang.Enum
        public String toString() {
            return this.nameOverride != null ? this.nameOverride : super.toString();
        }
    }

    protected IIOMetadataNode getStandardChromaNode() {
        IIOMetadataNode iIOMetadataNode = new IIOMetadataNode("Chroma");
        ColorModel colorModel = this.colorSpaceType != null ? null : this.type.getColorModel();
        ColorSpaceType colorSpaceType = this.colorSpaceType != null ? this.colorSpaceType : colorSpaceType(colorModel.getColorSpace());
        int numComponents = this.colorSpaceType != null ? this.colorSpaceType.numChannels : colorModel.getNumComponents();
        IIOMetadataNode iIOMetadataNode2 = new IIOMetadataNode("ColorSpaceType");
        iIOMetadataNode.appendChild(iIOMetadataNode2);
        iIOMetadataNode2.setAttribute("name", colorSpaceType.toString());
        IIOMetadataNode iIOMetadataNode3 = new IIOMetadataNode("NumChannels");
        iIOMetadataNode3.setAttribute(ES6Iterator.VALUE_PROPERTY, String.valueOf(numComponents));
        iIOMetadataNode.appendChild(iIOMetadataNode3);
        IIOMetadataNode iIOMetadataNode4 = new IIOMetadataNode("BlackIsZero");
        iIOMetadataNode4.setAttribute(ES6Iterator.VALUE_PROPERTY, booleanString(this.blackIsZero));
        iIOMetadataNode.appendChild(iIOMetadataNode4);
        if ((colorModel instanceof IndexColorModel) || this.palette != null) {
            IndexColorModel indexColorModel = this.palette != null ? this.palette : (IndexColorModel) colorModel;
            IIOMetadataNode iIOMetadataNode5 = new IIOMetadataNode("Palette");
            iIOMetadataNode.appendChild(iIOMetadataNode5);
            for (int i = 0; i < indexColorModel.getMapSize(); i++) {
                IIOMetadataNode iIOMetadataNode6 = new IIOMetadataNode("PaletteEntry");
                iIOMetadataNode5.appendChild(iIOMetadataNode6);
                iIOMetadataNode6.setAttribute("index", Integer.toString(i));
                iIOMetadataNode6.setAttribute(ElementTags.RED, Integer.toString(indexColorModel.getRed(i)));
                iIOMetadataNode6.setAttribute(ElementTags.GREEN, Integer.toString(indexColorModel.getGreen(i)));
                iIOMetadataNode6.setAttribute(ElementTags.BLUE, Integer.toString(indexColorModel.getBlue(i)));
                if (indexColorModel.getTransparency() == 3) {
                    iIOMetadataNode6.setAttribute("alpha", Integer.toString(indexColorModel.getAlpha(i)));
                }
            }
            if (indexColorModel.getTransparentPixel() != -1) {
                IIOMetadataNode iIOMetadataNode7 = new IIOMetadataNode("BackgroundIndex");
                iIOMetadataNode.appendChild(iIOMetadataNode7);
                iIOMetadataNode7.setAttribute(ES6Iterator.VALUE_PROPERTY, Integer.toString(indexColorModel.getTransparentPixel()));
            }
        }
        return iIOMetadataNode;
    }

    private static ColorSpaceType colorSpaceType(ColorSpace colorSpace) {
        switch (colorSpace.getType()) {
            case 0:
                return ColorSpaceType.XYZ;
            case 1:
                return ColorSpaceType.Lab;
            case 2:
                return ColorSpaceType.Luv;
            case 3:
                return ColorSpaceType.YCbCr;
            case 4:
                return ColorSpaceType.Yxy;
            case 5:
                return ColorSpaceType.RGB;
            case 6:
                return ColorSpaceType.GRAY;
            case 7:
                return ColorSpaceType.HSV;
            case 8:
                return ColorSpaceType.HLS;
            case 9:
                return ColorSpaceType.CMYK;
            case 10:
            default:
                int numComponents = colorSpace.getNumComponents();
                if (numComponents == 1) {
                    return ColorSpaceType.GRAY;
                }
                if (numComponents < 16) {
                    return ColorSpaceType.valueOf("GENERIC_" + Integer.toHexString(numComponents) + "CLR");
                }
                throw new IllegalArgumentException("Unknown ColorSpace type: " + colorSpace);
            case 11:
                return ColorSpaceType.CMY;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/StandardImageMetadataSupport$TextEntry.class */
    protected static final class TextEntry {
        static final List<String> COMPRESSIONS = Arrays.asList("none", "lzw", "zip", "bzip", "other");
        final String keyword;
        final String value;
        final String language;
        final String encoding;
        final String compression;

        public TextEntry(String str, String str2) {
            this(str, str2, null, null, null);
        }

        public TextEntry(String str, String str2, String str3, String str4, String str5) {
            this.keyword = str;
            this.value = (String) Validate.notNull(str2, ES6Iterator.VALUE_PROPERTY);
            this.language = str3;
            this.encoding = str4;
            this.compression = (String) Validate.isTrue(str5 == null || COMPRESSIONS.contains(str5), str5, String.format("Unknown compression: %s (expected: %s)", str5, COMPRESSIONS));
        }
    }

    protected IIOMetadataNode getStandardCompressionNode() {
        if (this.compressionName == null) {
            return null;
        }
        IIOMetadataNode iIOMetadataNode = new IIOMetadataNode("Compression");
        IIOMetadataNode iIOMetadataNode2 = new IIOMetadataNode("CompressionTypeName");
        iIOMetadataNode2.setAttribute(ES6Iterator.VALUE_PROPERTY, this.compressionName);
        iIOMetadataNode.appendChild(iIOMetadataNode2);
        IIOMetadataNode iIOMetadataNode3 = new IIOMetadataNode("Lossless");
        iIOMetadataNode3.setAttribute(ES6Iterator.VALUE_PROPERTY, booleanString(this.compressionLossless));
        iIOMetadataNode.appendChild(iIOMetadataNode3);
        return iIOMetadataNode;
    }

    protected static String booleanString(boolean z) {
        return z ? "TRUE" : "FALSE";
    }

    protected IIOMetadataNode getStandardDataNode() {
        String string;
        IIOMetadataNode iIOMetadataNode = new IIOMetadataNode(AOSignConstants.CMS_CONTENTTYPE_DATA);
        IIOMetadataNode iIOMetadataNode2 = new IIOMetadataNode("PlanarConfiguration");
        iIOMetadataNode.appendChild(iIOMetadataNode2);
        if (this.planarConfiguration != null) {
            string = this.planarConfiguration.toString();
        } else {
            string = this.type.getSampleModel() instanceof BandedSampleModel ? "PlaneInterleaved" : "PixelInterleaved";
        }
        iIOMetadataNode2.setAttribute(ES6Iterator.VALUE_PROPERTY, string);
        String strSampleFormat = (this.colorSpaceType == null && (this.type.getColorModel() instanceof IndexColorModel)) ? StandardStructureTypes.INDEX : sampleFormat(this.type.getSampleModel());
        if (strSampleFormat != null) {
            IIOMetadataNode iIOMetadataNode3 = new IIOMetadataNode("SampleFormat");
            iIOMetadataNode3.setAttribute(ES6Iterator.VALUE_PROPERTY, strSampleFormat);
            iIOMetadataNode.appendChild(iIOMetadataNode3);
        }
        int[] sampleSize = this.bitsPerSample != null ? this.bitsPerSample : this.type.getSampleModel().getSampleSize();
        IIOMetadataNode iIOMetadataNode4 = new IIOMetadataNode("BitsPerSample");
        iIOMetadataNode4.setAttribute(ES6Iterator.VALUE_PROPERTY, createListValue(sampleSize.length, sampleSize));
        iIOMetadataNode.appendChild(iIOMetadataNode4);
        if (this.significantBits != null) {
            String strCreateListValue = createListValue(this.type.getNumBands(), this.significantBits);
            if (!strCreateListValue.equals(iIOMetadataNode4.getAttribute(ES6Iterator.VALUE_PROPERTY))) {
                IIOMetadataNode iIOMetadataNode5 = new IIOMetadataNode("SignificantBitsPerSample");
                iIOMetadataNode5.setAttribute(ES6Iterator.VALUE_PROPERTY, strCreateListValue);
                iIOMetadataNode.appendChild(iIOMetadataNode5);
            }
        }
        if (this.sampleMSB != null) {
            IIOMetadataNode iIOMetadataNode6 = new IIOMetadataNode("SampleMSB");
            iIOMetadataNode6.setAttribute(ES6Iterator.VALUE_PROPERTY, createListValue(this.type.getNumBands(), this.sampleMSB));
            iIOMetadataNode.appendChild(iIOMetadataNode6);
        }
        return iIOMetadataNode;
    }

    private static String createListValue(int i, int... iArr) {
        StringBuilder sb = new StringBuilder();
        for (int i2 = 0; i2 < i; i2++) {
            if (sb.length() > 0) {
                sb.append(' ');
            }
            sb.append(iArr[i2 % iArr.length]);
        }
        return sb.toString();
    }

    private static String sampleFormat(SampleModel sampleModel) {
        switch (sampleModel.getDataType()) {
            case 0:
            case 1:
                return "UnsignedIntegral";
            case 2:
            case 3:
                if (sampleModel instanceof ComponentSampleModel) {
                    return "SignedIntegral";
                }
                return "UnsignedIntegral";
            case 4:
            case 5:
                return "Real";
            default:
                return null;
        }
    }

    protected IIOMetadataNode getStandardDimensionNode() {
        IIOMetadataNode iIOMetadataNode = new IIOMetadataNode("Dimension");
        if (this.pixelAspectRatio != null) {
            IIOMetadataNode iIOMetadataNode2 = new IIOMetadataNode("PixelAspectRatio");
            iIOMetadataNode2.setAttribute(ES6Iterator.VALUE_PROPERTY, String.valueOf(this.pixelAspectRatio));
            iIOMetadataNode.appendChild(iIOMetadataNode2);
        }
        IIOMetadataNode iIOMetadataNode3 = new IIOMetadataNode("ImageOrientation");
        iIOMetadataNode3.setAttribute(ES6Iterator.VALUE_PROPERTY, this.orientation.toString());
        iIOMetadataNode.appendChild(iIOMetadataNode3);
        if (iIOMetadataNode.hasChildNodes()) {
            return iIOMetadataNode;
        }
        return null;
    }

    protected IIOMetadataNode getStandardDocumentNode() {
        IIOMetadataNode iIOMetadataNode = new IIOMetadataNode(StandardStructureTypes.DOCUMENT);
        if (this.formatVersion != null) {
            IIOMetadataNode iIOMetadataNode2 = new IIOMetadataNode("FormatVersion");
            iIOMetadataNode.appendChild(iIOMetadataNode2);
            iIOMetadataNode2.setAttribute(ES6Iterator.VALUE_PROPERTY, this.formatVersion);
        }
        if (this.subimageInterpretation != null) {
            IIOMetadataNode iIOMetadataNode3 = new IIOMetadataNode("SubimageInterpretation");
            iIOMetadataNode.appendChild(iIOMetadataNode3);
            iIOMetadataNode3.setAttribute(ES6Iterator.VALUE_PROPERTY, this.subimageInterpretation.toString());
        }
        if (this.documentCreationTime != null) {
            IIOMetadataNode iIOMetadataNode4 = new IIOMetadataNode("ImageCreationTime");
            iIOMetadataNode.appendChild(iIOMetadataNode4);
            iIOMetadataNode4.setAttribute("year", String.valueOf(this.documentCreationTime.get(1)));
            iIOMetadataNode4.setAttribute("month", String.valueOf(this.documentCreationTime.get(2) + 1));
            iIOMetadataNode4.setAttribute("day", String.valueOf(this.documentCreationTime.get(5)));
            iIOMetadataNode4.setAttribute("hour", String.valueOf(this.documentCreationTime.get(11)));
            iIOMetadataNode4.setAttribute("minute", String.valueOf(this.documentCreationTime.get(12)));
            iIOMetadataNode4.setAttribute("second", String.valueOf(this.documentCreationTime.get(13)));
        }
        if (iIOMetadataNode.hasChildNodes()) {
            return iIOMetadataNode;
        }
        return null;
    }

    protected IIOMetadataNode getStandardTextNode() {
        if (this.textEntries.isEmpty()) {
            return null;
        }
        IIOMetadataNode iIOMetadataNode = new IIOMetadataNode("Text");
        for (TextEntry textEntry : this.textEntries) {
            IIOMetadataNode iIOMetadataNode2 = new IIOMetadataNode("TextEntry");
            iIOMetadataNode.appendChild(iIOMetadataNode2);
            if (textEntry.keyword != null) {
                iIOMetadataNode2.setAttribute("keyword", textEntry.keyword);
            }
            iIOMetadataNode2.setAttribute(ES6Iterator.VALUE_PROPERTY, textEntry.value);
            if (textEntry.language != null) {
                iIOMetadataNode2.setAttribute("language", textEntry.language);
            }
            if (textEntry.encoding != null) {
                iIOMetadataNode2.setAttribute("encoding", textEntry.encoding);
            }
            if (textEntry.compression != null) {
                iIOMetadataNode2.setAttribute("compression", textEntry.compression);
            }
        }
        return iIOMetadataNode;
    }

    protected IIOMetadataNode getStandardTransparencyNode() {
        String str;
        IIOMetadataNode iIOMetadataNode = new IIOMetadataNode("Transparency");
        IndexColorModel colorModel = this.type.getColorModel();
        IIOMetadataNode iIOMetadataNode2 = new IIOMetadataNode("Alpha");
        iIOMetadataNode.appendChild(iIOMetadataNode2);
        if (colorModel.hasAlpha()) {
            str = colorModel.isAlphaPremultiplied() ? "premultiplied" : "nonpremultiplied";
        } else {
            str = "none";
        }
        iIOMetadataNode2.setAttribute(ES6Iterator.VALUE_PROPERTY, str);
        if (colorModel instanceof IndexColorModel) {
            IndexColorModel indexColorModel = colorModel;
            if (indexColorModel.getTransparentPixel() != -1) {
                IIOMetadataNode iIOMetadataNode3 = new IIOMetadataNode("TransparentIndex");
                iIOMetadataNode.appendChild(iIOMetadataNode3);
                iIOMetadataNode3.setAttribute(ES6Iterator.VALUE_PROPERTY, Integer.toString(indexColorModel.getTransparentPixel()));
            }
        }
        return iIOMetadataNode;
    }
}
