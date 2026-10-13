package com.twelvemonkeys.imageio.plugins.jpeg;

import com.sun.jna.platform.win32.WinError;
import com.twelvemonkeys.imageio.ImageWriterBase;
import com.twelvemonkeys.imageio.util.ImageTypeSpecifiers;
import com.twelvemonkeys.imageio.util.ProgressListenerBase;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.color.ICC_ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import javax.imageio.IIOImage;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.event.IIOWriteWarningListener;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import org.w3c.dom.NodeList;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/plugins/jpeg/JPEGImageWriter.class */
public final class JPEGImageWriter extends ImageWriterBase {
    private final ImageWriter delegate;
    private final ProgressDelegator progressDelegator;

    public JPEGImageWriter(JPEGImageWriterSpi jPEGImageWriterSpi, ImageWriter imageWriter) {
        super(jPEGImageWriterSpi);
        this.delegate = imageWriter;
        this.progressDelegator = new ProgressDelegator();
    }

    private void installListeners() {
        this.delegate.addIIOWriteProgressListener(this.progressDelegator);
        this.delegate.addIIOWriteWarningListener(this.progressDelegator);
    }

    @Override // com.twelvemonkeys.imageio.ImageWriterBase
    protected void resetMembers() {
        this.delegate.reset();
        installListeners();
    }

    @Override // com.twelvemonkeys.imageio.ImageWriterBase
    public void setOutput(Object obj) {
        super.setOutput(obj);
        this.delegate.setOutput(obj);
    }

    public Object getOutput() {
        return this.delegate.getOutput();
    }

    public Locale[] getAvailableLocales() {
        return this.delegate.getAvailableLocales();
    }

    public void setLocale(Locale locale) {
        this.delegate.setLocale(locale);
    }

    public Locale getLocale() {
        return this.delegate.getLocale();
    }

    public ImageWriteParam getDefaultWriteParam() {
        return this.delegate.getDefaultWriteParam();
    }

    @Override // com.twelvemonkeys.imageio.ImageWriterBase
    public IIOMetadata getDefaultStreamMetadata(ImageWriteParam imageWriteParam) {
        return this.delegate.getDefaultStreamMetadata(imageWriteParam);
    }

    public IIOMetadata getDefaultImageMetadata(ImageTypeSpecifier imageTypeSpecifier, ImageWriteParam imageWriteParam) {
        return this.delegate.getDefaultImageMetadata(imageTypeSpecifier, imageWriteParam);
    }

    @Override // com.twelvemonkeys.imageio.ImageWriterBase
    public IIOMetadata convertStreamMetadata(IIOMetadata iIOMetadata, ImageWriteParam imageWriteParam) {
        return this.delegate.convertStreamMetadata(iIOMetadata, imageWriteParam);
    }

    public IIOMetadata convertImageMetadata(IIOMetadata iIOMetadata, ImageTypeSpecifier imageTypeSpecifier, ImageWriteParam imageWriteParam) {
        return this.delegate.convertImageMetadata(iIOMetadata, imageTypeSpecifier, imageWriteParam);
    }

    public int getNumThumbnailsSupported(ImageTypeSpecifier imageTypeSpecifier, ImageWriteParam imageWriteParam, IIOMetadata iIOMetadata, IIOMetadata iIOMetadata2) {
        return this.delegate.getNumThumbnailsSupported(imageTypeSpecifier, imageWriteParam, iIOMetadata, iIOMetadata2);
    }

    public Dimension[] getPreferredThumbnailSizes(ImageTypeSpecifier imageTypeSpecifier, ImageWriteParam imageWriteParam, IIOMetadata iIOMetadata, IIOMetadata iIOMetadata2) {
        return this.delegate.getPreferredThumbnailSizes(imageTypeSpecifier, imageWriteParam, iIOMetadata, iIOMetadata2);
    }

    public boolean canWriteRasters() {
        return this.delegate.canWriteRasters();
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.imageio.metadata.IIOInvalidTreeException */
    public void write(IIOMetadata iIOMetadata, IIOImage iIOImage, ImageWriteParam imageWriteParam) throws IIOInvalidTreeException, IOException {
        if (isDestinationCMYK(iIOImage, imageWriteParam)) {
            writeCMYK(iIOMetadata, iIOImage, imageWriteParam);
            return;
        }
        if (iIOImage.getMetadata() instanceof JPEGImage10Metadata) {
            IIOMetadata defaultImageMetadata = this.delegate.getDefaultImageMetadata(iIOImage.hasRaster() ? null : ImageTypeSpecifiers.createFromRenderedImage(iIOImage.getRenderedImage()), imageWriteParam);
            JPEGImage10Metadata jPEGImage10Metadata = (JPEGImage10Metadata) iIOImage.getMetadata();
            defaultImageMetadata.setFromTree(jPEGImage10Metadata.getNativeMetadataFormatName(), jPEGImage10Metadata.getNativeTree());
            iIOImage.setMetadata(defaultImageMetadata);
        }
        this.delegate.write(iIOMetadata, iIOImage, imageWriteParam);
    }

    private boolean isDestinationCMYK(IIOImage iIOImage, ImageWriteParam imageWriteParam) {
        return (!iIOImage.hasRaster() && iIOImage.getRenderedImage().getColorModel().getColorSpace().getType() == 9) || !(imageWriteParam == null || imageWriteParam.getDestinationType() == null || imageWriteParam.getDestinationType().getColorModel().getColorSpace().getType() != 9);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.imageio.metadata.IIOInvalidTreeException */
    private void writeCMYK(IIOMetadata iIOMetadata, IIOImage iIOImage, ImageWriteParam imageWriteParam) throws IIOInvalidTreeException, IOException {
        ImageTypeSpecifier imageTypeSpecifierCreateFromRenderedImage;
        RenderedImage renderedImage = iIOImage.getRenderedImage();
        boolean z = (imageWriteParam == null || imageWriteParam.getDestinationType() == null) ? false : true;
        if (z) {
            imageTypeSpecifierCreateFromRenderedImage = imageWriteParam.getDestinationType();
        } else {
            imageTypeSpecifierCreateFromRenderedImage = ImageTypeSpecifiers.createFromRenderedImage(renderedImage);
        }
        ImageTypeSpecifier imageTypeSpecifier = imageTypeSpecifierCreateFromRenderedImage;
        IIOMetadata iIOMetadataConvertCMYKMetadata = convertCMYKMetadata(iIOImage.getMetadata(), imageTypeSpecifier, imageWriteParam);
        InvertedRaster invertedRaster = new InvertedRaster(getRaster(renderedImage));
        if (z) {
            imageWriteParam.setDestinationType((ImageTypeSpecifier) null);
        }
        try {
            this.delegate.write(iIOMetadata, new IIOImage(invertedRaster, (List) null, iIOMetadataConvertCMYKMetadata), imageWriteParam);
        } finally {
            if (z) {
                imageWriteParam.setDestinationType(imageTypeSpecifier);
            }
        }
    }

    /* JADX WARN: Code duplicated, block: B:18:0x00bd  */
    private IIOMetadata convertCMYKMetadata(IIOMetadata iIOMetadata, ImageTypeSpecifier imageTypeSpecifier, ImageWriteParam imageWriteParam) throws IIOInvalidTreeException {
        IIOMetadataNode iIOMetadataNode = new IIOMetadataNode("javax_imageio_jpeg_image_1.0");
        iIOMetadataNode.appendChild(new IIOMetadataNode("JPEGVariety"));
        IIOMetadataNode iIOMetadataNode2 = new IIOMetadataNode("markerSequence");
        iIOMetadataNode.appendChild(iIOMetadataNode2);
        IIOMetadataNode iIOMetadataNode3 = iIOMetadata != null ? (IIOMetadataNode) iIOMetadata.getAsTree("javax_imageio_jpeg_image_1.0") : new IIOMetadataNode("emptyNode");
        NodeList elementsByTagName = iIOMetadataNode3.getElementsByTagName("unknown");
        for (int i = 0; i < elementsByTagName.getLength(); i++) {
            IIOMetadataNode iIOMetadataNodeItem = elementsByTagName.item(i);
            if ("226".equals(iIOMetadataNodeItem.getAttribute("MarkerTag"))) {
                Object userObject = iIOMetadataNodeItem.getUserObject();
                if (!(userObject instanceof byte[]) || ((byte[]) userObject).length < "ICC_PROFILE".length() || !"ICC_PROFILE".equals(new String((byte[]) userObject, 0, "ICC_PROFILE".length()))) {
                    iIOMetadataNode2.appendChild(iIOMetadataNodeItem);
                }
            } else {
                iIOMetadataNode2.appendChild(iIOMetadataNodeItem);
            }
        }
        IIOMetadataNode iIOMetadataNode4 = new IIOMetadataNode("app14Adobe");
        iIOMetadataNode4.setAttribute("transform", "0");
        iIOMetadataNode2.appendChild(iIOMetadataNode4);
        ICC_ColorSpace colorSpace = imageTypeSpecifier.getColorModel().getColorSpace();
        if (colorSpace instanceof ICC_ColorSpace) {
            byte[] data = colorSpace.getProfile().getData();
            int length = "ICC_PROFILE".length();
            byte[] bytes = "ICC_PROFILE".getBytes(StandardCharsets.US_ASCII);
            int i2 = ((65535 - length) - 3) - 2;
            int iCeil = (int) Math.ceil(data.length / i2);
            for (int i3 = 0; i3 < iCeil; i3++) {
                IIOMetadataNode iIOMetadataNode5 = new IIOMetadataNode("unknown");
                iIOMetadataNode5.setAttribute("MarkerTag", String.valueOf(WinError.ERROR_VIRUS_DELETED));
                int iMin = Math.min(i2, data.length - (i3 * i2));
                byte[] bArr = new byte[length + 3 + iMin];
                System.arraycopy(bytes, 0, bArr, 0, length);
                bArr[length] = 0;
                bArr[length + 1] = (byte) (1 + i3);
                bArr[length + 2] = (byte) iCeil;
                System.arraycopy(data, i3 * i2, bArr, length + 3, iMin);
                iIOMetadataNode5.setUserObject(bArr);
                iIOMetadataNode2.appendChild(iIOMetadataNode5);
            }
        }
        NodeList elementsByTagName2 = iIOMetadataNode3.getElementsByTagName("COM");
        for (int i4 = 0; i4 < elementsByTagName2.getLength(); i4++) {
            iIOMetadataNode2.appendChild(elementsByTagName2.item(i4));
        }
        IIOMetadata defaultImageMetadata = this.delegate.getDefaultImageMetadata(imageTypeSpecifier, imageWriteParam);
        defaultImageMetadata.mergeTree("javax_imageio_jpeg_image_1.0", iIOMetadataNode);
        return defaultImageMetadata;
    }

    private static Raster getRaster(RenderedImage renderedImage) {
        if (renderedImage instanceof BufferedImage) {
            return ((BufferedImage) renderedImage).getRaster();
        }
        if (renderedImage.getNumXTiles() == 1 && renderedImage.getNumYTiles() == 1) {
            return renderedImage.getTile(0, 0);
        }
        return renderedImage.getData();
    }

    public boolean canWriteSequence() {
        return this.delegate.canWriteSequence();
    }

    public void prepareWriteSequence(IIOMetadata iIOMetadata) throws IOException {
        this.delegate.prepareWriteSequence(iIOMetadata);
    }

    public void writeToSequence(IIOImage iIOImage, ImageWriteParam imageWriteParam) throws IOException {
        this.delegate.writeToSequence(iIOImage, imageWriteParam);
    }

    public void endWriteSequence() throws IOException {
        this.delegate.endWriteSequence();
    }

    public boolean canReplaceStreamMetadata() throws IOException {
        return this.delegate.canReplaceStreamMetadata();
    }

    public void replaceStreamMetadata(IIOMetadata iIOMetadata) throws IOException {
        this.delegate.replaceStreamMetadata(iIOMetadata);
    }

    public boolean canReplaceImageMetadata(int i) throws IOException {
        return this.delegate.canReplaceImageMetadata(i);
    }

    public void replaceImageMetadata(int i, IIOMetadata iIOMetadata) throws IOException {
        this.delegate.replaceImageMetadata(i, iIOMetadata);
    }

    public boolean canInsertImage(int i) throws IOException {
        return this.delegate.canInsertImage(i);
    }

    public void writeInsert(int i, IIOImage iIOImage, ImageWriteParam imageWriteParam) throws IOException {
        this.delegate.writeInsert(i, iIOImage, imageWriteParam);
    }

    public boolean canRemoveImage(int i) throws IOException {
        return this.delegate.canRemoveImage(i);
    }

    public void removeImage(int i) throws IOException {
        this.delegate.removeImage(i);
    }

    public boolean canWriteEmpty() throws IOException {
        return this.delegate.canWriteEmpty();
    }

    public void prepareWriteEmpty(IIOMetadata iIOMetadata, ImageTypeSpecifier imageTypeSpecifier, int i, int i2, IIOMetadata iIOMetadata2, List<? extends BufferedImage> list, ImageWriteParam imageWriteParam) throws IOException {
        this.delegate.prepareWriteEmpty(iIOMetadata, imageTypeSpecifier, i, i2, iIOMetadata2, list, imageWriteParam);
    }

    public void endWriteEmpty() throws IOException {
        this.delegate.endWriteEmpty();
    }

    public boolean canInsertEmpty(int i) throws IOException {
        return this.delegate.canInsertEmpty(i);
    }

    public void prepareInsertEmpty(int i, ImageTypeSpecifier imageTypeSpecifier, int i2, int i3, IIOMetadata iIOMetadata, List<? extends BufferedImage> list, ImageWriteParam imageWriteParam) throws IOException {
        this.delegate.prepareInsertEmpty(i, imageTypeSpecifier, i2, i3, iIOMetadata, list, imageWriteParam);
    }

    public void endInsertEmpty() throws IOException {
        this.delegate.endInsertEmpty();
    }

    public boolean canReplacePixels(int i) throws IOException {
        return this.delegate.canReplacePixels(i);
    }

    public void prepareReplacePixels(int i, Rectangle rectangle) throws IOException {
        this.delegate.prepareReplacePixels(i, rectangle);
    }

    public void replacePixels(RenderedImage renderedImage, ImageWriteParam imageWriteParam) throws IOException {
        this.delegate.replacePixels(renderedImage, imageWriteParam);
    }

    public void replacePixels(Raster raster, ImageWriteParam imageWriteParam) throws IOException {
        this.delegate.replacePixels(raster, imageWriteParam);
    }

    public void endReplacePixels() throws IOException {
        this.delegate.endReplacePixels();
    }

    public void abort() {
        super.abort();
        this.delegate.abort();
    }

    @Override // com.twelvemonkeys.imageio.ImageWriterBase
    public void reset() {
        super.reset();
        this.delegate.reset();
    }

    @Override // com.twelvemonkeys.imageio.ImageWriterBase
    public void dispose() {
        super.dispose();
        this.delegate.dispose();
    }

    /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/plugins/jpeg/JPEGImageWriter$InvertedRaster.class */
    private static class InvertedRaster extends WritableRaster {
        InvertedRaster(final Raster raster) {
            super(raster.getSampleModel(), new DataBuffer(raster.getDataBuffer().getDataType(), raster.getDataBuffer().getSize()) { // from class: com.twelvemonkeys.imageio.plugins.jpeg.JPEGImageWriter.InvertedRaster.1
                private final DataBuffer delegate;

                {
                    this.delegate = raster.getDataBuffer();
                }

                public int getElem(int i, int i2) {
                    return 255 - this.delegate.getElem(i, i2);
                }

                public void setElem(int i, int i2, int i3) {
                    throw new UnsupportedOperationException("setElem");
                }
            }, new Point());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/plugins/jpeg/JPEGImageWriter$ProgressDelegator.class */
    private class ProgressDelegator extends ProgressListenerBase implements IIOWriteWarningListener {
        private ProgressDelegator() {
        }

        @Override // com.twelvemonkeys.imageio.util.ProgressListenerBase
        public void imageComplete(ImageWriter imageWriter) {
            JPEGImageWriter.this.processImageComplete();
        }

        @Override // com.twelvemonkeys.imageio.util.ProgressListenerBase
        public void imageProgress(ImageWriter imageWriter, float f) {
            JPEGImageWriter.this.processImageProgress(f);
        }

        @Override // com.twelvemonkeys.imageio.util.ProgressListenerBase
        public void imageStarted(ImageWriter imageWriter, int i) {
            JPEGImageWriter.this.processImageStarted(i);
        }

        @Override // com.twelvemonkeys.imageio.util.ProgressListenerBase
        public void thumbnailComplete(ImageWriter imageWriter) {
            JPEGImageWriter.this.processThumbnailComplete();
        }

        @Override // com.twelvemonkeys.imageio.util.ProgressListenerBase
        public void thumbnailProgress(ImageWriter imageWriter, float f) {
            JPEGImageWriter.this.processThumbnailProgress(f);
        }

        @Override // com.twelvemonkeys.imageio.util.ProgressListenerBase
        public void thumbnailStarted(ImageWriter imageWriter, int i, int i2) {
            JPEGImageWriter.this.processThumbnailStarted(i, i2);
        }

        @Override // com.twelvemonkeys.imageio.util.ProgressListenerBase
        public void writeAborted(ImageWriter imageWriter) {
            JPEGImageWriter.this.processWriteAborted();
        }

        public void warningOccurred(ImageWriter imageWriter, int i, String str) {
            JPEGImageWriter.this.processWarningOccurred(i, str);
        }
    }
}
