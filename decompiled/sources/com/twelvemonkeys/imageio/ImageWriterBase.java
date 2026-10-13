package com.twelvemonkeys.imageio;

import com.twelvemonkeys.imageio.util.IIOUtil;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.stream.ImageOutputStream;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/ImageWriterBase.class */
public abstract class ImageWriterBase extends ImageWriter {
    protected ImageOutputStream imageOutput;

    protected ImageWriterBase(ImageWriterSpi imageWriterSpi) {
        super(imageWriterSpi);
    }

    public String getFormatName() throws IOException {
        return getOriginatingProvider().getFormatNames()[0];
    }

    public void setOutput(Object obj) {
        resetMembers();
        super.setOutput(obj);
        if (obj instanceof ImageOutputStream) {
            this.imageOutput = (ImageOutputStream) obj;
        } else {
            this.imageOutput = null;
        }
    }

    protected void assertOutput() {
        if (getOutput() == null) {
            throw new IllegalStateException("getOutput() == null");
        }
    }

    public void dispose() {
        resetMembers();
        super.dispose();
    }

    public void reset() {
        resetMembers();
        super.reset();
    }

    protected void resetMembers() {
    }

    public IIOMetadata getDefaultStreamMetadata(ImageWriteParam imageWriteParam) {
        return null;
    }

    public IIOMetadata convertStreamMetadata(IIOMetadata iIOMetadata, ImageWriteParam imageWriteParam) {
        return null;
    }

    protected static Rectangle getSourceRegion(ImageWriteParam imageWriteParam, int i, int i2) {
        return IIOUtil.getSourceRegion(imageWriteParam, i, i2);
    }

    protected static BufferedImage fakeAOI(BufferedImage bufferedImage, ImageWriteParam imageWriteParam) {
        return IIOUtil.fakeAOI(bufferedImage, getSourceRegion(imageWriteParam, bufferedImage.getWidth(), bufferedImage.getHeight()));
    }

    protected static Image fakeSubsampling(Image image, ImageWriteParam imageWriteParam) {
        return IIOUtil.fakeSubsampling(image, imageWriteParam);
    }
}
