package com.twelvemonkeys.imageio.plugins.jpeg;

import com.twelvemonkeys.imageio.spi.ImageWriterSpiBase;
import com.twelvemonkeys.imageio.util.IIOUtil;
import com.twelvemonkeys.lang.Validate;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.util.Locale;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadataFormat;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.spi.ServiceRegistry;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/plugins/jpeg/JPEGImageWriterSpi.class */
public class JPEGImageWriterSpi extends ImageWriterSpiBase {
    private ImageWriterSpi delegateProvider;

    public JPEGImageWriterSpi() {
        super(new JPEGProviderInfo());
    }

    protected JPEGImageWriterSpi(ImageWriterSpi imageWriterSpi) {
        this();
        this.delegateProvider = (ImageWriterSpi) Validate.notNull(imageWriterSpi);
    }

    public void onRegistration(ServiceRegistry serviceRegistry, Class<?> cls) {
        if (this.delegateProvider == null) {
            this.delegateProvider = (ImageWriterSpi) IIOUtil.lookupProviderByName(serviceRegistry, "com.sun.imageio.plugins.jpeg.JPEGImageWriterSpi", ImageWriterSpi.class);
        }
        if (this.delegateProvider != null) {
            serviceRegistry.setOrdering(cls, this, this.delegateProvider);
        } else {
            IIOUtil.deregisterProvider(serviceRegistry, this, cls);
        }
    }

    public String getVendorName() {
        return String.format("%s/%s", super.getVendorName(), this.delegateProvider.getVendorName());
    }

    public String getVersion() {
        return String.format("%s/%s", super.getVersion(), this.delegateProvider.getVersion());
    }

    public ImageWriter createWriterInstance(Object obj) throws IOException {
        return new JPEGImageWriter(this, this.delegateProvider.createWriterInstance(obj));
    }

    public String[] getFormatNames() {
        return this.delegateProvider.getFormatNames();
    }

    public boolean isStandardStreamMetadataFormatSupported() {
        return this.delegateProvider.isStandardStreamMetadataFormatSupported();
    }

    public String getNativeStreamMetadataFormatName() {
        return this.delegateProvider.getNativeStreamMetadataFormatName();
    }

    public String[] getExtraStreamMetadataFormatNames() {
        return this.delegateProvider.getExtraStreamMetadataFormatNames();
    }

    public boolean isStandardImageMetadataFormatSupported() {
        return this.delegateProvider.isStandardImageMetadataFormatSupported();
    }

    public String getNativeImageMetadataFormatName() {
        return this.delegateProvider.getNativeImageMetadataFormatName();
    }

    public String[] getExtraImageMetadataFormatNames() {
        return this.delegateProvider.getExtraImageMetadataFormatNames();
    }

    public IIOMetadataFormat getStreamMetadataFormat(String str) {
        return this.delegateProvider.getStreamMetadataFormat(str);
    }

    public IIOMetadataFormat getImageMetadataFormat(String str) {
        return this.delegateProvider.getImageMetadataFormat(str);
    }

    public boolean canEncodeImage(ImageTypeSpecifier imageTypeSpecifier) {
        return this.delegateProvider.canEncodeImage(imageTypeSpecifier);
    }

    public boolean canEncodeImage(RenderedImage renderedImage) {
        return this.delegateProvider.canEncodeImage(renderedImage);
    }

    public String getDescription(Locale locale) {
        return this.delegateProvider.getDescription(locale);
    }

    public boolean isFormatLossless() {
        return this.delegateProvider.isFormatLossless();
    }

    public Class[] getOutputTypes() {
        return this.delegateProvider.getOutputTypes();
    }
}
