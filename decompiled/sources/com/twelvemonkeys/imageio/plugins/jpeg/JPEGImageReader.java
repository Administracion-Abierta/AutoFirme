package com.twelvemonkeys.imageio.plugins.jpeg;

import com.aowagie.text.pdf.ColumnText;
import com.twelvemonkeys.imageio.ImageReaderBase;
import com.twelvemonkeys.imageio.color.ColorProfiles;
import com.twelvemonkeys.imageio.color.ColorSpaces;
import com.twelvemonkeys.imageio.color.YCbCrConverter;
import com.twelvemonkeys.imageio.metadata.CompoundDirectory;
import com.twelvemonkeys.imageio.metadata.jpeg.JPEG;
import com.twelvemonkeys.imageio.metadata.jpeg.JPEGSegment;
import com.twelvemonkeys.imageio.metadata.jpeg.JPEGSegmentUtil;
import com.twelvemonkeys.imageio.metadata.tiff.TIFFReader;
import com.twelvemonkeys.imageio.stream.SubImageInputStream;
import com.twelvemonkeys.imageio.util.ImageTypeSpecifiers;
import com.twelvemonkeys.imageio.util.ProgressListenerBase;
import com.twelvemonkeys.lang.Validate;
import com.twelvemonkeys.xml.XMLSerializer;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.RasterOp;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.event.IIOReadUpdateListener;
import javax.imageio.event.IIOReadWarningListener;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;
import org.apache.pdfbox.contentstream.operator.OperatorName;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/plugins/jpeg/JPEGImageReader.class */
public final class JPEGImageReader extends ImageReaderBase {
    static final boolean DEBUG = "true".equalsIgnoreCase(System.getProperty("com.twelvemonkeys.imageio.plugins.jpeg.debug"));
    static final boolean FORCE_RASTER_CONVERSION = "force".equalsIgnoreCase(System.getProperty("com.twelvemonkeys.imageio.plugins.jpeg.raster"));
    static final int ALL_APP_MARKERS = -1;
    private final ImageReader delegate;
    private final ProgressDelegator progressDelegator;
    private ImageReader thumbnailReader;
    private List<ThumbnailReader> thumbnails;
    private List<Segment> segments;
    private int currentStreamIndex;
    private final List<Long> streamOffsets;

    JPEGImageReader(ImageReaderSpi imageReaderSpi, ImageReader imageReader) {
        super(imageReaderSpi);
        this.currentStreamIndex = 0;
        this.streamOffsets = new ArrayList();
        this.delegate = (ImageReader) Validate.notNull(imageReader);
        this.progressDelegator = new ProgressDelegator();
    }

    private void installListeners() {
        this.delegate.addIIOReadProgressListener(this.progressDelegator);
        this.delegate.addIIOReadUpdateListener(this.progressDelegator);
        this.delegate.addIIOReadWarningListener(this.progressDelegator);
    }

    @Override // com.twelvemonkeys.imageio.ImageReaderBase
    protected void resetMembers() {
        this.delegate.reset();
        this.currentStreamIndex = 0;
        this.streamOffsets.clear();
        this.segments = null;
        this.thumbnails = null;
        if (this.thumbnailReader != null) {
            this.thumbnailReader.reset();
        }
        installListeners();
    }

    @Override // com.twelvemonkeys.imageio.ImageReaderBase
    public void dispose() {
        super.dispose();
        if (this.thumbnailReader != null) {
            this.thumbnailReader.dispose();
            this.thumbnailReader = null;
        }
        this.delegate.dispose();
    }

    public String getFormatName() throws IOException {
        return this.delegate.getFormatName();
    }

    private boolean isLossless() throws IOException {
        assertInput();
        return getSOF().marker == 65475;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.imageio.IIOException */
    public int getWidth(int i) throws IOException, IIOException {
        checkBounds(i);
        initHeader(i);
        return getSOF().samplesPerLine;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.imageio.IIOException */
    public int getHeight(int i) throws IOException, IIOException {
        checkBounds(i);
        initHeader(i);
        return getSOF().lines;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.imageio.IIOException */
    public Iterator<ImageTypeSpecifier> getImageTypes(int i) throws IOException, IIOException {
        ImageTypeSpecifier rawImageType = getRawImageType(i);
        ColorModel colorModel = rawImageType.getColorModel();
        JPEGColorSpace sourceCSType = getSourceCSType(getJFIF(), getAdobeDCT(), getSOF());
        LinkedHashSet linkedHashSet = new LinkedHashSet();
        if (colorModel.getColorSpace().getType() != 6) {
            if (colorModel.hasAlpha()) {
                linkedHashSet.add(ImageTypeSpecifiers.createFromBufferedImageType(2));
                linkedHashSet.add(ImageTypeSpecifiers.createFromBufferedImageType(6));
                linkedHashSet.add(ImageTypeSpecifiers.createFromBufferedImageType(3));
                linkedHashSet.add(ImageTypeSpecifiers.createFromBufferedImageType(7));
            }
            linkedHashSet.add(ImageTypeSpecifiers.createFromBufferedImageType(5));
            linkedHashSet.add(ImageTypeSpecifiers.createFromBufferedImageType(1));
            linkedHashSet.add(ImageTypeSpecifiers.createFromBufferedImageType(4));
        }
        linkedHashSet.add(rawImageType);
        if (sourceCSType != JPEGColorSpace.RGB && sourceCSType != JPEGColorSpace.RGBA && sourceCSType != JPEGColorSpace.CMYK) {
            if (colorModel.hasAlpha()) {
                linkedHashSet.add(ImageTypeSpecifiers.createGrayscale(8, 0, false));
            }
            linkedHashSet.add(ImageTypeSpecifiers.createFromBufferedImageType(10));
        }
        return linkedHashSet.iterator();
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.imageio.IIOException */
    public ImageTypeSpecifier getRawImageType(int i) throws IOException, IIOException {
        ICC_ColorSpace colorSpace;
        ICC_ColorSpace colorSpace2;
        ICC_ColorSpace colorSpace3;
        ICC_ColorSpace colorSpace4;
        checkBounds(i);
        initHeader(i);
        JPEGColorSpace sourceCSType = getSourceCSType(getJFIF(), getAdobeDCT(), getSOF());
        ICC_Profile embeddedICCProfile = getEmbeddedICCProfile(false);
        boolean z = false;
        switch (sourceCSType) {
            case GrayA:
                z = true;
            case Gray:
                if (embeddedICCProfile != null && embeddedICCProfile.getNumComponents() == 1) {
                    colorSpace4 = ColorSpaces.createColorSpace(embeddedICCProfile);
                } else {
                    colorSpace4 = ColorSpaces.getColorSpace(1003);
                }
                return ImageTypeSpecifiers.createInterleaved(colorSpace4, z ? new int[]{1, 0} : new int[]{0}, 0, z, false);
            case YCbCrA:
            case RGBA:
            case PhotoYCCA:
                z = true;
            case YCbCr:
            case RGB:
            case PhotoYCC:
                if (sourceCSType == JPEGColorSpace.PhotoYCC || sourceCSType == JPEGColorSpace.PhotoYCCA) {
                    colorSpace2 = ColorSpaces.getColorSpace(1002);
                } else {
                    if (embeddedICCProfile != null && embeddedICCProfile.getNumComponents() == 3) {
                        colorSpace3 = ColorSpaces.createColorSpace(embeddedICCProfile);
                    } else {
                        colorSpace3 = ColorSpaces.getColorSpace(1000);
                    }
                    colorSpace2 = colorSpace3;
                }
                return ImageTypeSpecifiers.createInterleaved(colorSpace2, z ? new int[]{3, 2, 1, 0} : new int[]{2, 1, 0}, 0, z, false);
            case YCCK:
            case CMYK:
                if (embeddedICCProfile != null && embeddedICCProfile.getNumComponents() == 4) {
                    colorSpace = ColorSpaces.createColorSpace(embeddedICCProfile);
                } else {
                    colorSpace = ColorSpaces.getColorSpace(5001);
                }
                return ImageTypeSpecifiers.createInterleaved(colorSpace, new int[]{3, 2, 1, 0}, 0, false, false);
            default:
                throw new IIOException("Could not determine JPEG source color space");
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.imageio.IIOException */
    public BufferedImage read(int i, ImageReadParam imageReadParam) throws IOException, IIOException {
        checkBounds(i);
        initHeader(i);
        Frame sof = getSOF();
        ICC_Profile embeddedICCProfile = getEmbeddedICCProfile(false);
        AdobeDCT adobeDCT = getAdobeDCT();
        boolean z = false;
        if (adobeDCT != null && ((adobeDCT.transform == 1 && sof.componentsInFrame() != 3) || (adobeDCT.transform == 2 && sof.componentsInFrame() != 4))) {
            Object[] objArr = new Object[3];
            objArr[0] = adobeDCT.transform == 2 ? "YCCK/CMYK" : "YCC/RGB";
            objArr[1] = Integer.valueOf(sof.marker & 15);
            objArr[2] = Integer.valueOf(sof.componentsInFrame());
            processWarningOccurred(String.format("Invalid Adobe App14 marker. Indicates %s data, but SOF%d has %d color component(s). Ignoring Adobe App14 marker.", objArr));
            z = true;
            adobeDCT = null;
        }
        JFIF jfif = getJFIF();
        JPEGColorSpace sourceCSType = getSourceCSType(jfif, adobeDCT, sof);
        if (sof.marker == 65475) {
            if (DEBUG) {
                System.out.println("Reading using Lossless decoder");
            }
            BufferedImage image = new JPEGLosslessDecoderWrapper(this).readImage(this.segments, this.imageInput);
            BufferedImage destination = imageReadParam != null ? imageReadParam.getDestination() : null;
            if (destination != null) {
                destination.getRaster().setDataElements(0, 0, image.getRaster());
                return destination;
            }
            return image;
        }
        if (FORCE_RASTER_CONVERSION || z || ((embeddedICCProfile != null && !ColorProfiles.isCS_sRGB(embeddedICCProfile)) || ((long) sof.lines) * ((long) sof.samplesPerLine) > 2147483647L || delegateCSTypeMismatch(jfif, adobeDCT, sof, sourceCSType))) {
            if (DEBUG) {
                System.out.println("Reading using raster and extra conversion");
                System.out.println("ICC color profile: " + embeddedICCProfile);
            }
            return readImageAsRasterAndReplaceColorProfile(i, imageReadParam, sof, sourceCSType, embeddedICCProfile);
        }
        if (DEBUG) {
            System.out.println("Reading using delegate");
        }
        return this.delegate.read(0, imageReadParam);
    }

    private boolean delegateCSTypeMismatch(JFIF jfif, AdobeDCT adobeDCT, Frame frame, JPEGColorSpace jPEGColorSpace) throws IOException {
        switch (jPEGColorSpace) {
            case GrayA:
            case YCbCrA:
            case RGBA:
            case PhotoYCCA:
            case PhotoYCC:
            case YCCK:
            case CMYK:
                return true;
            case Gray:
            case YCbCr:
            case RGB:
            default:
                try {
                    ImageTypeSpecifier rawImageType = this.delegate.getRawImageType(0);
                    switch (jPEGColorSpace) {
                        case Gray:
                            return rawImageType == null || rawImageType.getColorModel().getColorSpace().getType() != 6;
                        case YCbCr:
                            if (rawImageType == null) {
                                return false;
                            }
                            if (jfif != null && (frame.components[0].id != 1 || frame.components[1].id != 2 || frame.components[2].id != 3)) {
                                return true;
                            }
                            if (adobeDCT == null && ((frame.components[0].id != 1 || frame.components[1].id != 2 || frame.components[2].id != 3) && (frame.components[0].hSub == 1 || frame.components[0].vSub == 1 || frame.components[1].hSub == 1 || frame.components[1].vSub == 1 || frame.components[2].hSub == 1 || frame.components[2].vSub == 1))) {
                                return true;
                            }
                            break;
                            break;
                        case RGB:
                            break;
                        default:
                            return false;
                    }
                    return rawImageType == null || rawImageType.getColorModel().getColorSpace().getType() != 5;
                } catch (IIOException | ArrayIndexOutOfBoundsException | NegativeArraySizeException | NullPointerException e) {
                    return true;
                }
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.imageio.IIOException */
    private BufferedImage readImageAsRasterAndReplaceColorProfile(int i, ImageReadParam imageReadParam, Frame frame, JPEGColorSpace jPEGColorSpace, ICC_Profile iCC_Profile) throws IOException, IIOException {
        int width = getWidth(i);
        int height = getHeight(i);
        BufferedImage destination = getDestination(imageReadParam, getImageTypes(i), width, height);
        WritableRaster raster = destination.getRaster();
        RasterOp fastCMYKToRGB = null;
        ICC_ColorSpace iCC_ColorSpaceCreateColorSpace = iCC_Profile != null ? ColorSpaces.createColorSpace(iCC_Profile) : null;
        if (raster.getNumBands() <= 2 && jPEGColorSpace != JPEGColorSpace.Gray && jPEGColorSpace != JPEGColorSpace.GrayA) {
            fastCMYKToRGB = new LuminanceToGray();
        } else if (iCC_Profile == null || (jPEGColorSpace != JPEGColorSpace.Gray && jPEGColorSpace != JPEGColorSpace.GrayA)) {
            if (iCC_ColorSpaceCreateColorSpace != null) {
                if (frame.componentsInFrame() != iCC_ColorSpaceCreateColorSpace.getNumComponents()) {
                    processWarningOccurred(String.format("Embedded ICC color profile is incompatible with image data. Profile indicates %d components, but SOF%d has %d color components. Ignoring ICC profile, assuming source color space %s.", Integer.valueOf(iCC_ColorSpaceCreateColorSpace.getNumComponents()), Integer.valueOf(frame.marker & 15), Integer.valueOf(frame.componentsInFrame()), jPEGColorSpace));
                    if (jPEGColorSpace == JPEGColorSpace.CMYK && destination.getColorModel().getColorSpace().getType() != 9) {
                        fastCMYKToRGB = new ColorConvertOp(ColorSpaces.getColorSpace(5001), destination.getColorModel().getColorSpace(), (RenderingHints) null);
                    }
                } else if (iCC_ColorSpaceCreateColorSpace != destination.getColorModel().getColorSpace()) {
                    if (DEBUG) {
                        System.err.println("Converting from " + iCC_ColorSpaceCreateColorSpace + " to " + ((Object) (destination.getColorModel().getColorSpace().isCS_sRGB() ? "sRGB" : destination.getColorModel().getColorSpace())));
                    }
                    fastCMYKToRGB = new ColorConvertOp(iCC_ColorSpaceCreateColorSpace, destination.getColorModel().getColorSpace(), (RenderingHints) null);
                }
            } else if (jPEGColorSpace == JPEGColorSpace.YCCK || jPEGColorSpace == JPEGColorSpace.CMYK) {
                ColorSpace colorSpace = ColorSpaces.getColorSpace(5001);
                if (colorSpace instanceof ICC_ColorSpace) {
                    processWarningOccurred("No embedded ICC color profile, defaulting to \"generic\" CMYK ICC profile. Colors may look incorrect.");
                    if (colorSpace != destination.getColorModel().getColorSpace()) {
                        fastCMYKToRGB = new ColorConvertOp(colorSpace, destination.getColorModel().getColorSpace(), (RenderingHints) null);
                    }
                } else {
                    processWarningOccurred("No embedded ICC color profile, will convert using inaccurate CMYK to RGB conversion. Colors may look incorrect.");
                    fastCMYKToRGB = new FastCMYKToRGB();
                }
            }
        }
        if (imageReadParam == null) {
            imageReadParam = this.delegate.getDefaultReadParam();
        }
        Rectangle sourceRegion = imageReadParam.getSourceRegion();
        Rectangle rectangle = new Rectangle();
        Rectangle rectangle2 = new Rectangle();
        computeRegions(imageReadParam, width, height, destination, rectangle, rectangle2);
        int subsamplingXOffset = imageReadParam.getSubsamplingXOffset();
        int subsamplingYOffset = imageReadParam.getSubsamplingYOffset();
        rectangle.translate(-subsamplingXOffset, -subsamplingYOffset);
        rectangle.width += subsamplingXOffset;
        rectangle.height += subsamplingYOffset;
        try {
            imageReadParam.setSourceRegion(rectangle);
            Raster raster2 = this.delegate.readRaster(0, imageReadParam);
            if (jPEGColorSpace == JPEGColorSpace.YCbCr) {
                convertYCbCr2RGB(raster2, 3);
            } else if (jPEGColorSpace == JPEGColorSpace.YCbCrA) {
                convertYCbCr2RGB(raster2, 4);
            } else if (jPEGColorSpace == JPEGColorSpace.YCCK) {
                convertYCCK2CMYK(raster2);
            } else if (jPEGColorSpace == JPEGColorSpace.CMYK) {
                invertCMYK(raster2);
            }
            WritableRaster writableRasterCreateWritableChild = raster.createWritableChild(rectangle2.x, rectangle2.y, raster2.getWidth(), raster2.getHeight(), 0, 0, imageReadParam.getDestinationBands());
            if (fastCMYKToRGB != null) {
                fastCMYKToRGB.filter(raster2, writableRasterCreateWritableChild);
            } else {
                writableRasterCreateWritableChild.setRect(0, 0, raster2);
            }
            return destination;
        } finally {
            imageReadParam.setSourceRegion(sourceRegion);
        }
    }

    static JPEGColorSpace getSourceCSType(JFIF jfif, AdobeDCT adobeDCT, Frame frame) throws IIOException {
        switch (frame.componentsInFrame()) {
            case 1:
                return JPEGColorSpace.Gray;
            case 2:
                return JPEGColorSpace.GrayA;
            case 3:
                if (jfif != null) {
                    return JPEGColorSpace.YCbCr;
                }
                if (adobeDCT != null) {
                    switch (adobeDCT.transform) {
                        case 0:
                            return JPEGColorSpace.RGB;
                        case 1:
                        default:
                            return JPEGColorSpace.YCbCr;
                    }
                }
                int i = frame.components[0].id;
                int i2 = frame.components[1].id;
                int i3 = frame.components[2].id;
                if (i == 1 && i2 == 2 && i3 == 3) {
                    return JPEGColorSpace.YCbCr;
                }
                if (i == 82 && i2 == 71 && i3 == 66) {
                    return JPEGColorSpace.RGB;
                }
                if (i == 89 && i2 == 67 && i3 == 99) {
                    return JPEGColorSpace.PhotoYCC;
                }
                return JPEGColorSpace.YCbCr;
            case 4:
                if (adobeDCT != null) {
                    switch (adobeDCT.transform) {
                        case 0:
                            return JPEGColorSpace.CMYK;
                        case 2:
                        default:
                            return JPEGColorSpace.YCCK;
                    }
                }
                int i4 = frame.components[0].id;
                int i5 = frame.components[1].id;
                int i6 = frame.components[2].id;
                int i7 = frame.components[3].id;
                if (i4 == 1 && i5 == 2 && i6 == 3 && i7 == 4) {
                    return JPEGColorSpace.YCbCrA;
                }
                if (i4 == 82 && i5 == 71 && i6 == 66 && i7 == 65) {
                    return JPEGColorSpace.RGBA;
                }
                if (i4 == 89 && i5 == 67 && i6 == 99 && i7 == 65) {
                    return JPEGColorSpace.PhotoYCCA;
                }
                return JPEGColorSpace.CMYK;
            default:
                throw new IIOException("Cannot determine source color space");
        }
    }

    private ICC_Profile ensureDisplayProfile(ICC_Profile iCC_Profile) throws IOException {
        if (iCC_Profile != null && iCC_Profile.getProfileClass() != 1) {
            byte[] data = iCC_Profile.getData();
            if (data[64] == 0) {
                processWarningOccurred("ICC profile is Perceptual, ignoring, treating as Display class");
                intToBigEndian(1835955314, data, 12);
                return ColorProfiles.createProfile(data);
            }
        }
        return iCC_Profile;
    }

    static int intFromBigEndian(byte[] bArr, int i) {
        return ((bArr[i] & 255) << 24) | ((bArr[i + 1] & 255) << 16) | ((bArr[i + 2] & 255) << 8) | (bArr[i + 3] & 255);
    }

    static void intToBigEndian(int i, byte[] bArr, int i2) {
        bArr[i2] = (byte) (i >> 24);
        bArr[i2 + 1] = (byte) (i >> 16);
        bArr[i2 + 2] = (byte) (i >> 8);
        bArr[i2 + 3] = (byte) i;
    }

    @Override // com.twelvemonkeys.imageio.ImageReaderBase
    public void setInput(Object obj, boolean z, boolean z2) {
        super.setInput(obj, z, z2);
        try {
            if (this.imageInput != null) {
                if (!(this.imageInput instanceof SubImageInputStream)) {
                    this.imageInput = new SubImageInputStream(this.imageInput, Long.MAX_VALUE);
                }
                this.streamOffsets.add(Long.valueOf(this.imageInput.getStreamPosition()));
            }
            initDelegate(z, z2);
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    private void initDelegate(boolean z, boolean z2) throws IOException {
        this.delegate.setInput(this.imageInput != null ? new JPEGSegmentImageInputStream(this.imageInput, new JPEGSegmentWarningDelegate()) : null, z, z2);
    }

    private void initHeader() throws IOException {
        if (this.segments == null) {
            long jCurrentTimeMillis = DEBUG ? System.currentTimeMillis() : 0L;
            List<JPEGSegment> segments = readSegments();
            ArrayList arrayList = new ArrayList(segments.size());
            for (JPEGSegment jPEGSegment : segments) {
                try {
                    DataInputStream dataInputStream = new DataInputStream(jPEGSegment.segmentData());
                    Throwable th = null;
                    try {
                        try {
                            arrayList.add(Segment.read(jPEGSegment.marker(), jPEGSegment.identifier(), jPEGSegment.segmentLength(), dataInputStream));
                            if (dataInputStream != null) {
                                if (0 != 0) {
                                    try {
                                        dataInputStream.close();
                                    } catch (Throwable th2) {
                                        th.addSuppressed(th2);
                                    }
                                } else {
                                    dataInputStream.close();
                                }
                            }
                        } catch (Throwable th3) {
                            th = th3;
                            throw th3;
                        }
                    } catch (Throwable th4) {
                        if (dataInputStream != null) {
                            if (th != null) {
                                try {
                                    dataInputStream.close();
                                } catch (Throwable th5) {
                                    th.addSuppressed(th5);
                                }
                            } else {
                                dataInputStream.close();
                            }
                        }
                        throw th4;
                    }
                } catch (IOException e) {
                    if (jPEGSegment.marker() >= 65504 && 65519 >= jPEGSegment.marker()) {
                        processWarningOccurred("Bogus APP" + (jPEGSegment.marker() & 15) + "/" + jPEGSegment.identifier() + " segment, ignoring");
                    } else {
                        throw e;
                    }
                }
            }
            this.segments = arrayList;
            if (DEBUG) {
                System.out.println("segments: " + arrayList);
                System.out.println("Read metadata in " + (System.currentTimeMillis() - jCurrentTimeMillis) + " ms");
            }
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.imageio.IIOException */
    private void initHeader(int i) throws IOException, IIOException {
        assertInput();
        if (i < 0) {
            throw new IndexOutOfBoundsException("imageIndex < 0: " + i);
        }
        if (i == this.currentStreamIndex) {
            initHeader();
            return;
        }
        gotoImage(i);
        this.segments = null;
        this.thumbnails = null;
        initDelegate(this.seekForwardOnly, this.ignoreMetadata);
        initHeader();
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.imageio.IIOException */
    private void gotoImage(int i) throws IIOException, IOException {
        if (i < this.streamOffsets.size()) {
            this.imageInput.seek(this.streamOffsets.get(i).longValue());
        } else {
            this.imageInput.seek(this.streamOffsets.get(this.streamOffsets.size() - 1).longValue());
            try {
                for (int size = this.streamOffsets.size() - 1; size < i; size++) {
                    long jCurrentTimeMillis = 0;
                    if (DEBUG) {
                        jCurrentTimeMillis = System.currentTimeMillis();
                        System.out.printf("Start seeking for image index %d%n", Integer.valueOf(size + 1));
                    }
                    JPEGSegmentUtil.readSegments(this.imageInput, Collections.emptyMap());
                    while (true) {
                        int i2 = this.imageInput.read();
                        if (i2 == -1) {
                            break;
                        }
                        if (i2 == 255 && (65280 | this.imageInput.readUnsignedByte()) == 65497) {
                            while (true) {
                                int i3 = this.imageInput.read();
                                if (i3 == -1) {
                                    break;
                                }
                                if (i3 == 255 && (65280 | this.imageInput.readUnsignedByte()) == 65496) {
                                    long streamPosition = this.imageInput.getStreamPosition() - 2;
                                    this.imageInput.seek(streamPosition);
                                    this.streamOffsets.add(Long.valueOf(streamPosition));
                                    break;
                                }
                            }
                        }
                    }
                    if (DEBUG) {
                        System.out.printf("Seek in %d ms%n", Long.valueOf(System.currentTimeMillis() - jCurrentTimeMillis));
                    }
                }
                if (i >= this.streamOffsets.size()) {
                    throw new IndexOutOfBoundsException("Image index " + i + " not found in stream");
                }
            } catch (EOFException e) {
                IndexOutOfBoundsException indexOutOfBoundsException = new IndexOutOfBoundsException("Image index " + i + " not found in stream");
                indexOutOfBoundsException.initCause(e);
                throw indexOutOfBoundsException;
            }
        }
        this.currentStreamIndex = i;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.imageio.IIOException */
    @Override // com.twelvemonkeys.imageio.ImageReaderBase
    public int getNumImages(boolean z) throws IIOException, IOException {
        assertInput();
        if (z) {
            if (this.seekForwardOnly) {
                throw new IllegalStateException("seekForwardOnly and allowSearch are both true");
            }
            int i = 0;
            int i2 = 0;
            while (true) {
                try {
                    int i3 = i;
                    i++;
                    gotoImage(i3);
                    try {
                        this.segments = null;
                        getSOF();
                        i2++;
                    } catch (IIOException e) {
                    }
                } catch (IndexOutOfBoundsException e2) {
                    this.imageInput.seek(this.streamOffsets.get(this.currentStreamIndex).longValue());
                    return i2;
                }
            }
        } else {
            return -1;
        }
    }

    private List<JPEGSegment> readSegments() throws IOException {
        this.imageInput.mark();
        try {
            this.imageInput.seek(this.streamOffsets.get(this.currentStreamIndex).longValue());
            return JPEGSegmentUtil.readSegments(this.imageInput, JPEGSegmentUtil.ALL_SEGMENTS);
        } catch (IIOException | IllegalArgumentException e) {
            if (DEBUG) {
                e.printStackTrace();
            }
            return Collections.emptyList();
        } finally {
            this.imageInput.reset();
        }
    }

    List<Application> getAppSegments(int i, String str) throws IOException {
        initHeader();
        List<Application> listEmptyList = Collections.emptyList();
        for (Segment segment : this.segments) {
            if ((segment instanceof Application) && (i == -1 || i == segment.marker)) {
                if (str == null || str.equals(((Application) segment).identifier)) {
                    if (listEmptyList == Collections.EMPTY_LIST) {
                        listEmptyList = new ArrayList(this.segments.size());
                    }
                    listEmptyList.add((Application) segment);
                }
            }
        }
        return listEmptyList;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.imageio.IIOException */
    Frame getSOF() throws IOException, IIOException {
        initHeader();
        for (Segment segment : this.segments) {
            if (segment instanceof Frame) {
                return (Frame) segment;
            }
        }
        throw new IIOException("No SOF segment in stream");
    }

    private Application lastAppSegment(int i, String str) throws IOException {
        List<Application> appSegments = getAppSegments(i, str);
        if (appSegments.isEmpty()) {
            return null;
        }
        return appSegments.get(appSegments.size() - 1);
    }

    AdobeDCT getAdobeDCT() throws IOException {
        return (AdobeDCT) lastAppSegment(JPEG.APP14, "Adobe");
    }

    JFIF getJFIF() throws IOException {
        return (JFIF) lastAppSegment(JPEG.APP0, "JFIF");
    }

    JFXX getJFXX() throws IOException {
        return (JFXX) lastAppSegment(JPEG.APP0, "JFXX");
    }

    private EXIF getExif() throws IOException {
        List<Application> appSegments = getAppSegments(65505, "Exif");
        if (appSegments.isEmpty()) {
            return null;
        }
        return (EXIF) appSegments.get(0);
    }

    private CompoundDirectory parseExif(EXIF exif) throws IOException {
        if (exif != null) {
            if (exif.data.length > exif.identifier.length() + 2) {
                try {
                    ImageInputStream imageInputStreamExifData = exif.exifData();
                    Throwable th = null;
                    try {
                        try {
                            CompoundDirectory compoundDirectory = (CompoundDirectory) new TIFFReader().read(imageInputStreamExifData);
                            if (imageInputStreamExifData != null) {
                                if (0 != 0) {
                                    try {
                                        imageInputStreamExifData.close();
                                    } catch (Throwable th2) {
                                        th.addSuppressed(th2);
                                    }
                                } else {
                                    imageInputStreamExifData.close();
                                }
                            }
                            return compoundDirectory;
                        } catch (Throwable th3) {
                            th = th3;
                            throw th3;
                        }
                    } catch (Throwable th4) {
                        if (imageInputStreamExifData != null) {
                            if (th != null) {
                                try {
                                    imageInputStreamExifData.close();
                                } catch (Throwable th5) {
                                    th.addSuppressed(th5);
                                }
                            } else {
                                imageInputStreamExifData.close();
                            }
                        }
                        throw th4;
                    }
                } catch (IIOException e) {
                    processWarningOccurred("Exif chunk is present, but can't be read: " + e.getMessage());
                    return null;
                }
            }
            processWarningOccurred("Exif chunk has no data.");
            return null;
        }
        return null;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.imageio.IIOException */
    ICC_Profile getEmbeddedICCProfile(boolean z) throws IOException, IIOException {
        List<Application> appSegments = getAppSegments(65506, "ICC_PROFILE");
        if (appSegments.size() == 1) {
            DataInputStream dataInputStream = new DataInputStream(appSegments.get(0).data());
            int unsignedByte = dataInputStream.readUnsignedByte();
            int unsignedByte2 = dataInputStream.readUnsignedByte();
            if (unsignedByte != 1 && unsignedByte2 != 1) {
                processWarningOccurred(String.format("Unexpected number of 'ICC_PROFILE' chunks: %d of %d. Ignoring ICC profile.", Integer.valueOf(unsignedByte), Integer.valueOf(unsignedByte2)));
                return null;
            }
            return readICCProfileSafe(dataInputStream, z);
        }
        if (!appSegments.isEmpty()) {
            DataInputStream dataInputStream2 = new DataInputStream(appSegments.get(0).data());
            int unsignedByte3 = dataInputStream2.readUnsignedByte();
            int unsignedByte4 = dataInputStream2.readUnsignedByte();
            boolean z2 = false;
            if (unsignedByte4 != appSegments.size()) {
                processWarningOccurred(String.format("Bad 'ICC_PROFILE' chunk count: %d. Ignoring ICC profile.", Integer.valueOf(unsignedByte4)));
                z2 = true;
                if (!z) {
                    return null;
                }
            }
            if (!z2 && unsignedByte3 < 1) {
                processWarningOccurred(String.format("Invalid 'ICC_PROFILE' chunk index: %d. Ignoring ICC profile.", Integer.valueOf(unsignedByte3)));
                if (!z) {
                    return null;
                }
            }
            int size = z2 ? appSegments.size() : unsignedByte4;
            InputStream[] inputStreamArr = new InputStream[size];
            inputStreamArr[z2 ? 0 : unsignedByte3 - 1] = dataInputStream2;
            for (int i = 1; i < size; i++) {
                DataInputStream dataInputStream3 = new DataInputStream(appSegments.get(i).data());
                int unsignedByte5 = dataInputStream3.readUnsignedByte();
                if (dataInputStream3.readUnsignedByte() != unsignedByte4 && !z2) {
                    throw new IIOException(String.format("Bad number of 'ICC_PROFILE' chunks: %d of %d.", Integer.valueOf(unsignedByte5), Integer.valueOf(unsignedByte4)));
                }
                inputStreamArr[z2 ? i : unsignedByte5 - 1] = dataInputStream3;
            }
            return readICCProfileSafe(new SequenceInputStream(Collections.enumeration(Arrays.asList(inputStreamArr))), z);
        }
        return null;
    }

    private ICC_Profile readICCProfileSafe(InputStream inputStream, boolean z) {
        try {
            return z ? ColorProfiles.readProfileRaw(inputStream) : ensureDisplayProfile(ColorProfiles.readProfile(inputStream));
        } catch (IOException | RuntimeException e) {
            processWarningOccurred(String.format("Bad 'ICC_PROFILE' chunk(s): %s. Ignoring ICC profile.", e.getMessage()));
            return null;
        }
    }

    public boolean canReadRaster() {
        return this.delegate.canReadRaster();
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.imageio.IIOException */
    public Raster readRaster(int i, ImageReadParam imageReadParam) throws IOException, IIOException {
        checkBounds(i);
        initHeader(i);
        if (isLossless()) {
            return new JPEGLosslessDecoderWrapper(this).readRaster(this.segments, this.imageInput);
        }
        try {
            return this.delegate.readRaster(0, imageReadParam);
        } catch (IndexOutOfBoundsException e) {
            throw new IIOException("Corrupt JPEG data: Bad segment length", e);
        }
    }

    public RenderedImage readAsRenderedImage(int i, ImageReadParam imageReadParam) throws IOException {
        return read(i, imageReadParam);
    }

    public void abort() {
        super.abort();
        this.delegate.abort();
    }

    public ImageReadParam getDefaultReadParam() {
        return this.delegate.getDefaultReadParam();
    }

    public boolean readerSupportsThumbnails() {
        return true;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.imageio.IIOException */
    private void readThumbnailMetadata(int i) throws IOException, IIOException {
        checkBounds(i);
        initHeader(i);
        if (this.thumbnails == null) {
            this.thumbnails = new ArrayList();
            try {
                ThumbnailReader thumbnailReaderFrom = JFIFThumbnail.from(getJFIF());
                if (thumbnailReaderFrom != null) {
                    this.thumbnails.add(thumbnailReaderFrom);
                }
            } catch (IOException e) {
                processWarningOccurred(e.getMessage());
            }
            try {
                ThumbnailReader thumbnailReaderFrom2 = JFXXThumbnail.from(getJFXX(), getThumbnailReader());
                if (thumbnailReaderFrom2 != null) {
                    this.thumbnails.add(thumbnailReaderFrom2);
                }
            } catch (IOException e2) {
                processWarningOccurred(e2.getMessage());
            }
            try {
                EXIF exif = getExif();
                ThumbnailReader thumbnailReaderFrom3 = EXIFThumbnail.from(exif, parseExif(exif), getThumbnailReader());
                if (thumbnailReaderFrom3 != null) {
                    this.thumbnails.add(thumbnailReaderFrom3);
                }
            } catch (IOException e3) {
                processWarningOccurred(e3.getMessage());
            }
        }
    }

    ImageReader getThumbnailReader() throws IOException {
        if (this.thumbnailReader == null) {
            this.thumbnailReader = this.delegate.getOriginatingProvider().createReaderInstance();
        }
        return this.thumbnailReader;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.imageio.IIOException */
    public int getNumThumbnails(int i) throws IOException, IIOException {
        readThumbnailMetadata(i);
        return this.thumbnails.size();
    }

    private void checkThumbnailBounds(int i, int i2) throws IOException {
        Validate.isTrue(i2 >= 0, Integer.valueOf(i2), "thumbnailIndex < 0; %d");
        Validate.isTrue(getNumThumbnails(i) > i2, Integer.valueOf(i2), "thumbnailIndex >= numThumbnails; %d");
    }

    public int getThumbnailWidth(int i, int i2) throws IOException {
        checkThumbnailBounds(i, i2);
        return this.thumbnails.get(i2).getWidth();
    }

    public int getThumbnailHeight(int i, int i2) throws IOException {
        checkThumbnailBounds(i, i2);
        return this.thumbnails.get(i2).getHeight();
    }

    public BufferedImage readThumbnail(int i, int i2) throws IOException {
        checkThumbnailBounds(i, i2);
        processThumbnailStarted(i, i2);
        processThumbnailProgress(ColumnText.GLOBAL_SPACE_CHAR_RATIO);
        BufferedImage bufferedImage = this.thumbnails.get(i2).read();
        processThumbnailProgress(100.0f);
        processThumbnailComplete();
        return bufferedImage;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.imageio.IIOException */
    @Override // com.twelvemonkeys.imageio.ImageReaderBase
    public IIOMetadata getImageMetadata(int i) throws IOException, IIOException {
        initHeader(i);
        return new JPEGImage10Metadata(this.segments, getSOF(), getJFIF(), getJFXX(), getEmbeddedICCProfile(true), getAdobeDCT(), parseExif(getExif()));
    }

    @Override // com.twelvemonkeys.imageio.ImageReaderBase
    public IIOMetadata getStreamMetadata() throws IOException {
        return this.delegate.getStreamMetadata();
    }

    protected void processWarningOccurred(String str) {
        super.processWarningOccurred(str);
    }

    private static void invertCMYK(Raster raster) {
        byte[] data = raster.getDataBuffer().getData();
        int length = data.length;
        for (int i = 0; i < length; i++) {
            data[i] = (byte) ((255 - data[i]) & 255);
        }
    }

    private static void convertYCbCr2RGB(Raster raster, int i) {
        int height = raster.getHeight();
        int width = raster.getWidth();
        byte[] data = raster.getDataBuffer().getData();
        for (int i2 = 0; i2 < height; i2++) {
            for (int i3 = 0; i3 < width; i3++) {
                YCbCrConverter.convertJPEGYCbCr2RGB(data, data, (i3 + (i2 * width)) * i);
            }
        }
    }

    private static void convertYCCK2CMYK(Raster raster) {
        int height = raster.getHeight();
        int width = raster.getWidth();
        byte[] data = raster.getDataBuffer().getData();
        for (int i = 0; i < height; i++) {
            for (int i2 = 0; i2 < width; i2++) {
                int i3 = (i2 + (i * width)) * 4;
                YCbCrConverter.convertJPEGYCbCr2RGB(data, data, i3);
                data[i3 + 3] = (byte) ((255 - data[i3 + 3]) & 255);
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/plugins/jpeg/JPEGImageReader$ProgressDelegator.class */
    private class ProgressDelegator extends ProgressListenerBase implements IIOReadUpdateListener, IIOReadWarningListener {
        private ProgressDelegator() {
        }

        @Override // com.twelvemonkeys.imageio.util.ProgressListenerBase
        public void imageComplete(ImageReader imageReader) {
            JPEGImageReader.this.processImageComplete();
        }

        @Override // com.twelvemonkeys.imageio.util.ProgressListenerBase
        public void imageProgress(ImageReader imageReader, float f) {
            JPEGImageReader.this.processImageProgress(f);
        }

        @Override // com.twelvemonkeys.imageio.util.ProgressListenerBase
        public void imageStarted(ImageReader imageReader, int i) {
            JPEGImageReader.this.processImageStarted(JPEGImageReader.this.currentStreamIndex);
        }

        @Override // com.twelvemonkeys.imageio.util.ProgressListenerBase
        public void readAborted(ImageReader imageReader) {
            JPEGImageReader.this.processReadAborted();
        }

        @Override // com.twelvemonkeys.imageio.util.ProgressListenerBase
        public void sequenceComplete(ImageReader imageReader) {
            JPEGImageReader.this.processSequenceComplete();
        }

        @Override // com.twelvemonkeys.imageio.util.ProgressListenerBase
        public void sequenceStarted(ImageReader imageReader, int i) {
            JPEGImageReader.this.processSequenceStarted(i);
        }

        @Override // com.twelvemonkeys.imageio.util.ProgressListenerBase
        public void thumbnailComplete(ImageReader imageReader) {
            JPEGImageReader.this.processThumbnailComplete();
        }

        @Override // com.twelvemonkeys.imageio.util.ProgressListenerBase
        public void thumbnailProgress(ImageReader imageReader, float f) {
            JPEGImageReader.this.processThumbnailProgress(f);
        }

        @Override // com.twelvemonkeys.imageio.util.ProgressListenerBase
        public void thumbnailStarted(ImageReader imageReader, int i, int i2) {
            JPEGImageReader.this.processThumbnailStarted(JPEGImageReader.this.currentStreamIndex, i2);
        }

        public void passStarted(ImageReader imageReader, BufferedImage bufferedImage, int i, int i2, int i3, int i4, int i5, int i6, int i7, int[] iArr) {
            JPEGImageReader.this.processPassStarted(bufferedImage, i, i2, i3, i4, i5, i6, i7, iArr);
        }

        public void imageUpdate(ImageReader imageReader, BufferedImage bufferedImage, int i, int i2, int i3, int i4, int i5, int i6, int[] iArr) {
            JPEGImageReader.this.processImageUpdate(bufferedImage, i, i2, i3, i4, i5, i6, iArr);
        }

        public void passComplete(ImageReader imageReader, BufferedImage bufferedImage) {
            JPEGImageReader.this.processPassComplete(bufferedImage);
        }

        public void thumbnailPassStarted(ImageReader imageReader, BufferedImage bufferedImage, int i, int i2, int i3, int i4, int i5, int i6, int i7, int[] iArr) {
            JPEGImageReader.this.processThumbnailPassStarted(bufferedImage, i, i2, i3, i4, i5, i6, i7, iArr);
        }

        public void thumbnailUpdate(ImageReader imageReader, BufferedImage bufferedImage, int i, int i2, int i3, int i4, int i5, int i6, int[] iArr) {
            JPEGImageReader.this.processThumbnailUpdate(bufferedImage, i, i2, i3, i4, i5, i6, iArr);
        }

        public void thumbnailPassComplete(ImageReader imageReader, BufferedImage bufferedImage) {
            JPEGImageReader.this.processThumbnailPassComplete(bufferedImage);
        }

        public void warningOccurred(ImageReader imageReader, String str) {
            JPEGImageReader.this.processWarningOccurred(str);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/plugins/jpeg/JPEGImageReader$JPEGSegmentWarningDelegate.class */
    private class JPEGSegmentWarningDelegate implements JPEGSegmentWarningListener {
        private JPEGSegmentWarningDelegate() {
        }

        @Override // com.twelvemonkeys.imageio.plugins.jpeg.JPEGSegmentWarningListener
        public void warningOccurred(String str) {
            JPEGImageReader.this.processWarningOccurred(str);
        }
    }

    protected static void showIt(BufferedImage bufferedImage, String str) {
        ImageReaderBase.showIt(bufferedImage, str);
    }

    public static void main(String[] strArr) throws IOException {
        BufferedImage bufferedImage;
        ImageIO.setUseCache(false);
        int i = 1;
        int i2 = 1;
        int i3 = 0;
        int i4 = 0;
        Rectangle rectangle = null;
        boolean z = false;
        boolean z2 = false;
        int i5 = 0;
        while (i5 < strArr.length) {
            final String str = strArr[i5];
            if (str.charAt(0) != '-') {
                File file = new File(str);
                ImageInputStream imageInputStreamCreateImageInputStream = ImageIO.createImageInputStream(file);
                if (imageInputStreamCreateImageInputStream == null) {
                    System.err.println("Could not read file: " + file);
                } else {
                    Iterator imageReaders = ImageIO.getImageReaders(imageInputStreamCreateImageInputStream);
                    if (imageReaders.hasNext()) {
                        ImageReader imageReader = (ImageReader) imageReaders.next();
                        System.err.println("Reading using: " + imageReader);
                        imageReader.addIIOReadWarningListener(new IIOReadWarningListener() { // from class: com.twelvemonkeys.imageio.plugins.jpeg.JPEGImageReader.1
                            public void warningOccurred(ImageReader imageReader2, String str2) {
                                System.err.println("Warning: " + str + ": " + str2);
                            }
                        });
                        imageReader.addIIOReadProgressListener(new ProgressListenerBase() { // from class: com.twelvemonkeys.imageio.plugins.jpeg.JPEGImageReader.2
                            private static final int MAX_W = 78;
                            int lastProgress = 0;

                            @Override // com.twelvemonkeys.imageio.util.ProgressListenerBase
                            public void imageStarted(ImageReader imageReader2, int i6) {
                                System.out.print("[");
                            }

                            @Override // com.twelvemonkeys.imageio.util.ProgressListenerBase
                            public void imageProgress(ImageReader imageReader2, float f) {
                                int i6 = ((int) (f * 78.0f)) / 100;
                                for (int i7 = this.lastProgress; i7 < i6; i7++) {
                                    System.out.print(".");
                                }
                                System.out.flush();
                                this.lastProgress = i6;
                            }

                            @Override // com.twelvemonkeys.imageio.util.ProgressListenerBase
                            public void imageComplete(ImageReader imageReader2) {
                                for (int i6 = this.lastProgress; i6 < 78; i6++) {
                                    System.out.print(".");
                                }
                                System.out.println("]");
                            }
                        });
                        imageReader.setInput(imageInputStreamCreateImageInputStream);
                        try {
                            try {
                                if (imageReader.getNumImages(true) == 0) {
                                    IIOMetadata streamMetadata = imageReader.getStreamMetadata();
                                    new XMLSerializer(System.out, System.getProperty("file.encoding")).serialize((IIOMetadataNode) streamMetadata.getAsTree(streamMetadata.getNativeMetadataFormatName()), false);
                                    imageInputStreamCreateImageInputStream.close();
                                } else {
                                    ImageReadParam defaultReadParam = imageReader.getDefaultReadParam();
                                    if (i > 1 || i2 > 1 || rectangle != null) {
                                        defaultReadParam.setSourceSubsampling(i, i2, i3, i4);
                                        defaultReadParam.setSourceRegion(rectangle);
                                        bufferedImage = null;
                                    } else {
                                        bufferedImage = null;
                                    }
                                    defaultReadParam.setDestination(bufferedImage);
                                    long jCurrentTimeMillis = DEBUG ? System.currentTimeMillis() : 0L;
                                    try {
                                        bufferedImage = imageReader.read(0, defaultReadParam);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        if (bufferedImage == null) {
                                            imageInputStreamCreateImageInputStream.close();
                                        }
                                        i5++;
                                    }
                                    if (DEBUG) {
                                        System.err.println("Read time: " + (System.currentTimeMillis() - jCurrentTimeMillis) + " ms");
                                        System.err.println("image: " + bufferedImage);
                                    }
                                    showIt(bufferedImage, String.format("Image: %s [%d x %d]", file.getName(), Integer.valueOf(imageReader.getWidth(0)), Integer.valueOf(imageReader.getHeight(0))));
                                    if (z) {
                                        try {
                                            IIOMetadata imageMetadata = imageReader.getImageMetadata(0);
                                            System.out.println("Metadata for File: " + file.getName());
                                            if (imageMetadata.getNativeMetadataFormatName() != null) {
                                                System.out.println("Native:");
                                                new XMLSerializer(System.out, System.getProperty("file.encoding")).serialize(imageMetadata.getAsTree(imageMetadata.getNativeMetadataFormatName()), false);
                                            }
                                            if (imageMetadata.isStandardMetadataFormatSupported()) {
                                                System.out.println("Standard:");
                                                new XMLSerializer(System.out, System.getProperty("file.encoding")).serialize(imageMetadata.getAsTree("javax_imageio_1.0"), false);
                                            }
                                            System.out.println();
                                        } catch (IIOException e2) {
                                            System.err.println("Could not read thumbnails: " + str + ": " + e2.getMessage());
                                            e2.printStackTrace();
                                        }
                                    }
                                    if (z2) {
                                        try {
                                            int numThumbnails = imageReader.getNumThumbnails(0);
                                            for (int i6 = 0; i6 < numThumbnails; i6++) {
                                                BufferedImage thumbnail = imageReader.readThumbnail(0, i6);
                                                showIt(thumbnail, String.format("Thumbnail: %s [%d x %d]", file.getName(), Integer.valueOf(thumbnail.getWidth()), Integer.valueOf(thumbnail.getHeight())));
                                            }
                                        } catch (IIOException e3) {
                                            System.err.println("Could not read thumbnails: " + str + ": " + e3.getMessage());
                                            e3.printStackTrace();
                                        }
                                    }
                                    imageInputStreamCreateImageInputStream.close();
                                }
                            } catch (Throwable th) {
                                System.err.println(file);
                                th.printStackTrace();
                                imageInputStreamCreateImageInputStream.close();
                            }
                        } catch (Throwable th2) {
                            imageInputStreamCreateImageInputStream.close();
                            throw th2;
                        }
                    } else {
                        System.err.println("No reader for: " + file);
                    }
                }
            } else if (str.equals("-s") || (str.equals("--subsample") && strArr.length > i5 + 1)) {
                i5++;
                String[] strArrSplit = strArr[i5].split(",");
                try {
                    if (strArrSplit.length >= 4) {
                        i = Integer.parseInt(strArrSplit[0]);
                        i2 = Integer.parseInt(strArrSplit[1]);
                        i3 = Integer.parseInt(strArrSplit[2]);
                        i4 = Integer.parseInt(strArrSplit[3]);
                    } else {
                        i = Integer.parseInt(strArrSplit[0]);
                        i2 = strArrSplit.length > 1 ? Integer.parseInt(strArrSplit[1]) : i;
                    }
                } catch (NumberFormatException e4) {
                    System.err.println("Bad sub sampling (x,y): '" + strArr[i5] + OperatorName.SHOW_TEXT_LINE);
                }
            } else if (str.equals("-r") || (str.equals("--roi") && strArr.length > i5 + 1)) {
                i5++;
                String[] strArrSplit2 = strArr[i5].split(",");
                try {
                    rectangle = strArrSplit2.length >= 4 ? new Rectangle(Integer.parseInt(strArrSplit2[0]), Integer.parseInt(strArrSplit2[1]), Integer.parseInt(strArrSplit2[2]), Integer.parseInt(strArrSplit2[3])) : new Rectangle(Integer.parseInt(strArrSplit2[0]), Integer.parseInt(strArrSplit2[1]));
                } catch (IndexOutOfBoundsException | NumberFormatException e5) {
                    System.err.println("Bad source region ([x,y,]w, h): '" + strArr[i5] + OperatorName.SHOW_TEXT_LINE);
                }
            } else if (str.equals("-m") || str.equals("--metadata")) {
                z = true;
            } else if (str.equals("-t") || str.equals("--thumbnails")) {
                z2 = true;
            } else {
                System.err.println("Unknown argument: '" + str + OperatorName.SHOW_TEXT_LINE);
                System.exit(-1);
            }
            i5++;
        }
    }
}
