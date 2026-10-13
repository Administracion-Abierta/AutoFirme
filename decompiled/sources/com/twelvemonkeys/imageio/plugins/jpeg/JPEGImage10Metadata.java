package com.twelvemonkeys.imageio.plugins.jpeg;

import com.ctc.wstx.cfg.XmlConsts;
import com.twelvemonkeys.imageio.AbstractMetadata;
import com.twelvemonkeys.imageio.metadata.CompoundDirectory;
import com.twelvemonkeys.imageio.metadata.Directory;
import com.twelvemonkeys.imageio.metadata.Entry;
import com.twelvemonkeys.imageio.metadata.jpeg.JPEG;
import java.awt.color.ICC_Profile;
import java.util.List;
import javax.imageio.IIOException;
import javax.imageio.metadata.IIOMetadataNode;
import org.apache.pdfbox.pdmodel.documentinterchange.taggedpdf.PDLayoutAttributeObject;
import org.ini4j.Config;
import org.mozilla.javascript.ES6Iterator;
import org.w3c.dom.Node;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/plugins/jpeg/JPEGImage10Metadata.class */
class JPEGImage10Metadata extends AbstractMetadata {
    static final String JAVAX_IMAGEIO_JPEG_IMAGE_1_0 = "javax_imageio_jpeg_image_1.0";
    private final List<Segment> segments;
    private final Frame frame;
    private final JFIF jfif;
    private final AdobeDCT adobeDCT;
    private final JFXX jfxx;
    private final ICC_Profile embeddedICCProfile;
    private final CompoundDirectory exif;

    JPEGImage10Metadata(List<Segment> list, Frame frame, JFIF jfif, JFXX jfxx, ICC_Profile iCC_Profile, AdobeDCT adobeDCT, CompoundDirectory compoundDirectory) {
        super(true, JAVAX_IMAGEIO_JPEG_IMAGE_1_0, null, null, null);
        this.segments = list;
        this.frame = frame;
        this.jfif = jfif;
        this.adobeDCT = adobeDCT;
        this.jfxx = jfxx;
        this.embeddedICCProfile = iCC_Profile;
        this.exif = compoundDirectory;
    }

    @Override // com.twelvemonkeys.imageio.AbstractMetadata
    protected Node getNativeTree() {
        IIOMetadataNode iIOMetadataNode = new IIOMetadataNode(JAVAX_IMAGEIO_JPEG_IMAGE_1_0);
        IIOMetadataNode iIOMetadataNode2 = new IIOMetadataNode("JPEGvariety");
        boolean z = this.jfif != null;
        if (z) {
            IIOMetadataNode iIOMetadataNode3 = new IIOMetadataNode("app0JFIF");
            iIOMetadataNode3.setAttribute("majorVersion", Integer.toString(this.jfif.majorVersion));
            iIOMetadataNode3.setAttribute("minorVersion", Integer.toString(this.jfif.minorVersion));
            iIOMetadataNode3.setAttribute("resUnits", Integer.toString(this.jfif.units));
            iIOMetadataNode3.setAttribute("Xdensity", Integer.toString(this.jfif.xDensity));
            iIOMetadataNode3.setAttribute("Ydensity", Integer.toString(this.jfif.yDensity));
            iIOMetadataNode3.setAttribute("thumbWidth", Integer.toString(this.jfif.xThumbnail));
            iIOMetadataNode3.setAttribute("thumbHeight", Integer.toString(this.jfif.yThumbnail));
            iIOMetadataNode2.appendChild(iIOMetadataNode3);
            apendJFXX(iIOMetadataNode3);
            appendICCProfile(iIOMetadataNode3);
        }
        iIOMetadataNode.appendChild(iIOMetadataNode2);
        appendMarkerSequence(iIOMetadataNode, this.segments, z);
        return iIOMetadataNode;
    }

    /* JADX WARN: Code duplicated, block: B:74:0x04ab A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:75:0x0452 A[SYNTHETIC] */
    private void appendMarkerSequence(IIOMetadataNode iIOMetadataNode, List<Segment> list, boolean z) {
        IIOMetadataNode iIOMetadataNode2 = new IIOMetadataNode("markerSequence");
        iIOMetadataNode.appendChild(iIOMetadataNode2);
        for (Segment segment : list) {
            switch (segment.marker) {
                case JPEG.SOF0 /* 65472 */:
                case JPEG.SOF1 /* 65473 */:
                case JPEG.SOF2 /* 65474 */:
                case JPEG.SOF3 /* 65475 */:
                case JPEG.SOF5 /* 65477 */:
                case JPEG.SOF6 /* 65478 */:
                case JPEG.SOF7 /* 65479 */:
                case JPEG.SOF9 /* 65481 */:
                case JPEG.SOF10 /* 65482 */:
                case JPEG.SOF11 /* 65483 */:
                case JPEG.SOF13 /* 65485 */:
                case JPEG.SOF14 /* 65486 */:
                case JPEG.SOF15 /* 65487 */:
                    Frame frame = (Frame) segment;
                    IIOMetadataNode iIOMetadataNode3 = new IIOMetadataNode("sof");
                    iIOMetadataNode3.setAttribute("process", String.valueOf(frame.marker & 15));
                    iIOMetadataNode3.setAttribute("samplePrecision", String.valueOf(frame.samplePrecision));
                    iIOMetadataNode3.setAttribute("numLines", String.valueOf(frame.lines));
                    iIOMetadataNode3.setAttribute("samplesPerLine", String.valueOf(frame.samplesPerLine));
                    iIOMetadataNode3.setAttribute("numFrameComponents", String.valueOf(frame.componentsInFrame()));
                    for (Frame.Component component : frame.components) {
                        IIOMetadataNode iIOMetadataNode4 = new IIOMetadataNode("componentSpec");
                        iIOMetadataNode4.setAttribute("componentId", String.valueOf(component.id));
                        iIOMetadataNode4.setAttribute("HsamplingFactor", String.valueOf(component.hSub));
                        iIOMetadataNode4.setAttribute("VsamplingFactor", String.valueOf(component.vSub));
                        iIOMetadataNode4.setAttribute("QtableSelector", String.valueOf(component.qtSel));
                        iIOMetadataNode3.appendChild(iIOMetadataNode4);
                    }
                    iIOMetadataNode2.appendChild(iIOMetadataNode3);
                    break;
                case JPEG.DHT /* 65476 */:
                    HuffmanTable huffmanTable = (HuffmanTable) segment;
                    IIOMetadataNode iIOMetadataNode5 = new IIOMetadataNode("dht");
                    IIOMetadataNode iIOMetadataNode6 = new IIOMetadataNode("dht");
                    appendHuffmanTables(huffmanTable, 0, iIOMetadataNode5);
                    appendHuffmanTables(huffmanTable, 1, iIOMetadataNode6);
                    iIOMetadataNode2.appendChild(iIOMetadataNode5);
                    if (iIOMetadataNode5.getLength() + iIOMetadataNode6.getLength() > 4) {
                        iIOMetadataNode2.appendChild(iIOMetadataNode6);
                    } else {
                        while (iIOMetadataNode6.hasChildNodes()) {
                            iIOMetadataNode5.appendChild(iIOMetadataNode6.removeChild(iIOMetadataNode6.getFirstChild()));
                        }
                    }
                    break;
                case 65480:
                case JPEG.DAC /* 65484 */:
                case 65488:
                case 65489:
                case 65490:
                case 65491:
                case 65492:
                case 65493:
                case 65494:
                case 65495:
                case JPEG.SOI /* 65496 */:
                case JPEG.EOI /* 65497 */:
                case JPEG.DNL /* 65500 */:
                case JPEG.DHP /* 65502 */:
                case JPEG.EXP /* 65503 */:
                case 65505:
                case 65507:
                case 65508:
                case 65509:
                case 65510:
                case 65511:
                case 65512:
                case 65513:
                case 65514:
                case JPEG.APP11 /* 65515 */:
                case JPEG.APP12 /* 65516 */:
                case JPEG.APP13 /* 65517 */:
                case JPEG.APP15 /* 65519 */:
                case 65520:
                case 65521:
                case 65522:
                case 65523:
                case 65524:
                case 65525:
                case 65526:
                case JPEG.SOF55 /* 65527 */:
                case JPEG.LSE /* 65528 */:
                case 65529:
                case 65530:
                case 65531:
                case 65532:
                case 65533:
                default:
                    IIOMetadataNode iIOMetadataNode7 = new IIOMetadataNode("unknown");
                    iIOMetadataNode7.setAttribute("MarkerTag", String.valueOf(segment.marker & 255));
                    iIOMetadataNode7.setUserObject(((Application) segment).data);
                    iIOMetadataNode2.appendChild(iIOMetadataNode7);
                    break;
                case JPEG.SOS /* 65498 */:
                    Scan scan = (Scan) segment;
                    IIOMetadataNode iIOMetadataNode8 = new IIOMetadataNode("sos");
                    iIOMetadataNode8.setAttribute("numScanComponents", String.valueOf(scan.components.length));
                    iIOMetadataNode8.setAttribute("startSpectralSelection", String.valueOf(scan.spectralSelStart));
                    iIOMetadataNode8.setAttribute("endSpectralSelection", String.valueOf(scan.spectralSelEnd));
                    iIOMetadataNode8.setAttribute("approxHigh", String.valueOf(scan.approxHigh));
                    iIOMetadataNode8.setAttribute("approxLow", String.valueOf(scan.approxLow));
                    for (Scan.Component component2 : scan.components) {
                        IIOMetadataNode iIOMetadataNode9 = new IIOMetadataNode("scanComponentSpec");
                        iIOMetadataNode9.setAttribute("componentSelector", String.valueOf(component2.scanCompSel));
                        iIOMetadataNode9.setAttribute("dcHuffTable", String.valueOf(component2.dcTabSel));
                        iIOMetadataNode9.setAttribute("acHuffTable", String.valueOf(component2.acTabSel));
                        iIOMetadataNode8.appendChild(iIOMetadataNode9);
                    }
                    iIOMetadataNode2.appendChild(iIOMetadataNode8);
                    break;
                case JPEG.DQT /* 65499 */:
                    QuantizationTable quantizationTable = (QuantizationTable) segment;
                    IIOMetadataNode iIOMetadataNode10 = new IIOMetadataNode("dqt");
                    for (int i = 0; i < 4; i++) {
                        if (quantizationTable.isPresent(i)) {
                            IIOMetadataNode iIOMetadataNode11 = new IIOMetadataNode("dqtable");
                            iIOMetadataNode11.setAttribute("elementPrecision", quantizationTable.precision(i) != 16 ? "0" : "1");
                            iIOMetadataNode11.setAttribute("qtableId", Integer.toString(i));
                            iIOMetadataNode11.setUserObject(quantizationTable.toNativeTable(i));
                            iIOMetadataNode10.appendChild(iIOMetadataNode11);
                        }
                    }
                    iIOMetadataNode2.appendChild(iIOMetadataNode10);
                    break;
                case JPEG.DRI /* 65501 */:
                    RestartInterval restartInterval = (RestartInterval) segment;
                    IIOMetadataNode iIOMetadataNode12 = new IIOMetadataNode("dri");
                    iIOMetadataNode12.setAttribute("interval", Integer.toString(restartInterval.interval));
                    iIOMetadataNode2.appendChild(iIOMetadataNode12);
                    break;
                case JPEG.APP0 /* 65504 */:
                    if (!(segment instanceof JFIF) && (!z || !(segment instanceof JFXX))) {
                        if (z || !(segment instanceof ICCProfile)) {
                            if (segment instanceof AdobeDCT) {
                                AdobeDCT adobeDCT = (AdobeDCT) segment;
                                IIOMetadataNode iIOMetadataNode13 = new IIOMetadataNode("app14Adobe");
                                iIOMetadataNode13.setAttribute(XmlConsts.XML_DECL_KW_VERSION, String.valueOf(adobeDCT.version));
                                iIOMetadataNode13.setAttribute("flags0", String.valueOf(adobeDCT.flags0));
                                iIOMetadataNode13.setAttribute("flags1", String.valueOf(adobeDCT.flags1));
                                iIOMetadataNode13.setAttribute("transform", String.valueOf(adobeDCT.transform));
                                iIOMetadataNode2.appendChild(iIOMetadataNode13);
                            } else {
                                IIOMetadataNode iIOMetadataNode14 = new IIOMetadataNode("unknown");
                                iIOMetadataNode14.setAttribute("MarkerTag", String.valueOf(segment.marker & 255));
                                iIOMetadataNode14.setUserObject(((Application) segment).data);
                                iIOMetadataNode2.appendChild(iIOMetadataNode14);
                            }
                        }
                    }
                    break;
                case 65506:
                    if (z) {
                    }
                    if (segment instanceof AdobeDCT) {
                        AdobeDCT adobeDCT2 = (AdobeDCT) segment;
                        IIOMetadataNode iIOMetadataNode15 = new IIOMetadataNode("app14Adobe");
                        iIOMetadataNode15.setAttribute(XmlConsts.XML_DECL_KW_VERSION, String.valueOf(adobeDCT2.version));
                        iIOMetadataNode15.setAttribute("flags0", String.valueOf(adobeDCT2.flags0));
                        iIOMetadataNode15.setAttribute("flags1", String.valueOf(adobeDCT2.flags1));
                        iIOMetadataNode15.setAttribute("transform", String.valueOf(adobeDCT2.transform));
                        iIOMetadataNode2.appendChild(iIOMetadataNode15);
                    } else {
                        IIOMetadataNode iIOMetadataNode16 = new IIOMetadataNode("unknown");
                        iIOMetadataNode16.setAttribute("MarkerTag", String.valueOf(segment.marker & 255));
                        iIOMetadataNode16.setUserObject(((Application) segment).data);
                        iIOMetadataNode2.appendChild(iIOMetadataNode16);
                    }
                    break;
                case JPEG.APP14 /* 65518 */:
                    if (segment instanceof AdobeDCT) {
                        AdobeDCT adobeDCT3 = (AdobeDCT) segment;
                        IIOMetadataNode iIOMetadataNode17 = new IIOMetadataNode("app14Adobe");
                        iIOMetadataNode17.setAttribute(XmlConsts.XML_DECL_KW_VERSION, String.valueOf(adobeDCT3.version));
                        iIOMetadataNode17.setAttribute("flags0", String.valueOf(adobeDCT3.flags0));
                        iIOMetadataNode17.setAttribute("flags1", String.valueOf(adobeDCT3.flags1));
                        iIOMetadataNode17.setAttribute("transform", String.valueOf(adobeDCT3.transform));
                        iIOMetadataNode2.appendChild(iIOMetadataNode17);
                    } else {
                        IIOMetadataNode iIOMetadataNode18 = new IIOMetadataNode("unknown");
                        iIOMetadataNode18.setAttribute("MarkerTag", String.valueOf(segment.marker & 255));
                        iIOMetadataNode18.setUserObject(((Application) segment).data);
                        iIOMetadataNode2.appendChild(iIOMetadataNode18);
                    }
                    break;
                case JPEG.COM /* 65534 */:
                    IIOMetadataNode iIOMetadataNode19 = new IIOMetadataNode("com");
                    iIOMetadataNode19.setAttribute(Config.PROP_COMMENT, ((Comment) segment).comment);
                    iIOMetadataNode2.appendChild(iIOMetadataNode19);
                    break;
            }
        }
    }

    private void appendHuffmanTables(HuffmanTable huffmanTable, int i, IIOMetadataNode iIOMetadataNode) {
        for (int i2 = 0; i2 < 4; i2++) {
            if (huffmanTable.isPresent(i2, i)) {
                IIOMetadataNode iIOMetadataNode2 = new IIOMetadataNode("dhtable");
                iIOMetadataNode2.setAttribute("class", String.valueOf(i));
                iIOMetadataNode2.setAttribute("htableId", String.valueOf(i2));
                iIOMetadataNode2.setUserObject(huffmanTable.toNativeTable(i2, i));
                iIOMetadataNode.appendChild(iIOMetadataNode2);
            }
        }
    }

    private void appendICCProfile(IIOMetadataNode iIOMetadataNode) {
        if (this.embeddedICCProfile != null) {
            IIOMetadataNode iIOMetadataNode2 = new IIOMetadataNode("app2ICC");
            iIOMetadataNode2.setUserObject(this.embeddedICCProfile);
            iIOMetadataNode.appendChild(iIOMetadataNode2);
        }
    }

    private void apendJFXX(IIOMetadataNode iIOMetadataNode) {
        if (this.jfxx != null) {
            IIOMetadataNode iIOMetadataNode2 = new IIOMetadataNode("JFXX");
            iIOMetadataNode.appendChild(iIOMetadataNode2);
            IIOMetadataNode iIOMetadataNode3 = new IIOMetadataNode("app0JFXX");
            iIOMetadataNode3.setAttribute("extensionCode", Integer.toString(this.jfxx.extensionCode));
            iIOMetadataNode2.appendChild(iIOMetadataNode3);
            switch (this.jfxx.extensionCode) {
                case 16:
                    IIOMetadataNode iIOMetadataNode4 = new IIOMetadataNode("JFIFthumbJPEG");
                    iIOMetadataNode4.appendChild(new IIOMetadataNode("markerSequence"));
                    iIOMetadataNode3.appendChild(iIOMetadataNode4);
                    break;
                case 17:
                    IIOMetadataNode iIOMetadataNode5 = new IIOMetadataNode("JFIFthumbPalette");
                    iIOMetadataNode5.setAttribute("thumbWidth", Integer.toString(this.jfxx.thumbnail[0] & 255));
                    iIOMetadataNode5.setAttribute("thumbHeight", Integer.toString(this.jfxx.thumbnail[1] & 255));
                    iIOMetadataNode3.appendChild(iIOMetadataNode5);
                    break;
                case 19:
                    IIOMetadataNode iIOMetadataNode6 = new IIOMetadataNode("JFIFthumbRGB");
                    iIOMetadataNode6.setAttribute("thumbWidth", Integer.toString(this.jfxx.thumbnail[0] & 255));
                    iIOMetadataNode6.setAttribute("thumbHeight", Integer.toString(this.jfxx.thumbnail[1] & 255));
                    iIOMetadataNode3.appendChild(iIOMetadataNode6);
                    break;
            }
        }
    }

    protected IIOMetadataNode getStandardChromaNode() {
        IIOMetadataNode iIOMetadataNode = new IIOMetadataNode("Chroma");
        IIOMetadataNode iIOMetadataNode2 = new IIOMetadataNode("ColorSpaceType");
        iIOMetadataNode2.setAttribute("name", getColorSpaceType());
        iIOMetadataNode.appendChild(iIOMetadataNode2);
        IIOMetadataNode iIOMetadataNode3 = new IIOMetadataNode("NumChannels");
        iIOMetadataNode3.setAttribute(ES6Iterator.VALUE_PROPERTY, String.valueOf(this.frame.componentsInFrame()));
        iIOMetadataNode.appendChild(iIOMetadataNode3);
        return iIOMetadataNode;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.imageio.IIOException */
    private String getColorSpaceType() {
        try {
            switch (JPEGImageReader.getSourceCSType(this.jfif, this.adobeDCT, this.frame)) {
                case Gray:
                case GrayA:
                    return "GRAY";
                case YCbCr:
                case YCbCrA:
                    return "YCbCr";
                case RGB:
                case RGBA:
                    return "RGB";
                case PhotoYCC:
                case PhotoYCCA:
                    return "PhotoYCC";
                case YCCK:
                    return "YCCK";
                case CMYK:
                    return "CMYK";
                default:
                    return Integer.toString(this.frame.componentsInFrame(), 16) + "CLR";
            }
        } catch (IIOException e) {
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.imageio.IIOException */
    private boolean hasAlpha() {
        try {
            switch (JPEGImageReader.getSourceCSType(this.jfif, this.adobeDCT, this.frame)) {
                case GrayA:
                case YCbCrA:
                case RGBA:
                case PhotoYCCA:
                    return true;
                case YCbCr:
                case RGB:
                case PhotoYCC:
                default:
                    return false;
            }
        } catch (IIOException e) {
            return false;
        }
    }

    private boolean isLossess() {
        switch (this.frame.marker) {
            case JPEG.SOF3 /* 65475 */:
            case JPEG.SOF7 /* 65479 */:
            case JPEG.SOF11 /* 65483 */:
            case JPEG.SOF15 /* 65487 */:
                return true;
            default:
                return false;
        }
    }

    protected IIOMetadataNode getStandardTransparencyNode() {
        if (hasAlpha()) {
            IIOMetadataNode iIOMetadataNode = new IIOMetadataNode("Transparency");
            IIOMetadataNode iIOMetadataNode2 = new IIOMetadataNode("Alpha");
            iIOMetadataNode2.setAttribute(ES6Iterator.VALUE_PROPERTY, "nonpremultipled");
            iIOMetadataNode.appendChild(iIOMetadataNode2);
            return iIOMetadataNode;
        }
        return null;
    }

    protected IIOMetadataNode getStandardCompressionNode() {
        IIOMetadataNode iIOMetadataNode = new IIOMetadataNode("Compression");
        IIOMetadataNode iIOMetadataNode2 = new IIOMetadataNode("CompressionTypeName");
        iIOMetadataNode2.setAttribute(ES6Iterator.VALUE_PROPERTY, "JPEG");
        iIOMetadataNode.appendChild(iIOMetadataNode2);
        IIOMetadataNode iIOMetadataNode3 = new IIOMetadataNode("Lossless");
        iIOMetadataNode3.setAttribute(ES6Iterator.VALUE_PROPERTY, isLossess() ? "TRUE" : "FALSE");
        iIOMetadataNode.appendChild(iIOMetadataNode3);
        IIOMetadataNode iIOMetadataNode4 = new IIOMetadataNode("NumProgressiveScans");
        iIOMetadataNode4.setAttribute(ES6Iterator.VALUE_PROPERTY, "1");
        iIOMetadataNode.appendChild(iIOMetadataNode4);
        return iIOMetadataNode;
    }

    protected IIOMetadataNode getStandardDimensionNode() {
        IIOMetadataNode iIOMetadataNode = new IIOMetadataNode("Dimension");
        IIOMetadataNode iIOMetadataNode2 = new IIOMetadataNode("ImageOrientation");
        iIOMetadataNode2.setAttribute(ES6Iterator.VALUE_PROPERTY, getExifOrientation(this.exif));
        iIOMetadataNode.appendChild(iIOMetadataNode2);
        if (this.jfif != null) {
            float fMax = Math.max(1, this.jfif.xDensity);
            float fMax2 = Math.max(1, this.jfif.yDensity);
            float f = this.jfif.units == 0 ? fMax / fMax2 : fMax2 / fMax;
            IIOMetadataNode iIOMetadataNode3 = new IIOMetadataNode("PixelAspectRatio");
            iIOMetadataNode3.setAttribute(ES6Iterator.VALUE_PROPERTY, Float.toString(f));
            iIOMetadataNode.insertBefore(iIOMetadataNode3, iIOMetadataNode2);
            if (this.jfif.units != 0) {
                float f2 = this.jfif.units == 1 ? 25.4f : 10.0f;
                IIOMetadataNode iIOMetadataNode4 = new IIOMetadataNode("HorizontalPixelSize");
                iIOMetadataNode4.setAttribute(ES6Iterator.VALUE_PROPERTY, Float.toString(f2 / fMax));
                iIOMetadataNode.appendChild(iIOMetadataNode4);
                IIOMetadataNode iIOMetadataNode5 = new IIOMetadataNode("VerticalPixelSize");
                iIOMetadataNode5.setAttribute(ES6Iterator.VALUE_PROPERTY, Float.toString(f2 / fMax2));
                iIOMetadataNode.appendChild(iIOMetadataNode5);
            }
        }
        return iIOMetadataNode;
    }

    private String getExifOrientation(Directory directory) {
        Entry entryById;
        if (directory == null || (entryById = directory.getEntryById(274)) == null) {
            return PDLayoutAttributeObject.LINE_HEIGHT_NORMAL;
        }
        switch (((Number) entryById.getValue()).intValue()) {
            case 0:
            case 1:
            default:
                return PDLayoutAttributeObject.LINE_HEIGHT_NORMAL;
            case 2:
                return "FlipH";
            case 3:
                return "Rotate180";
            case 4:
                return "FlipV";
            case 5:
                return "FlipVRotate90";
            case 6:
                return "Rotate270";
            case 7:
                return "FlipHRotate90";
            case 8:
                return "Rotate90";
        }
    }

    protected IIOMetadataNode getStandardTextNode() {
        IIOMetadataNode iIOMetadataNode = new IIOMetadataNode("Text");
        for (Segment segment : this.segments) {
            if (segment instanceof Comment) {
                IIOMetadataNode iIOMetadataNode2 = new IIOMetadataNode("TextEntry");
                iIOMetadataNode2.setAttribute("keyword", Config.PROP_COMMENT);
                iIOMetadataNode2.setAttribute(ES6Iterator.VALUE_PROPERTY, ((Comment) segment).comment);
                iIOMetadataNode.appendChild(iIOMetadataNode2);
            }
        }
        if (iIOMetadataNode.hasChildNodes()) {
            return iIOMetadataNode;
        }
        return null;
    }
}
