package com.twelvemonkeys.imageio.plugins.jpeg;

import com.twelvemonkeys.imageio.spi.ImageReaderSpiBase;
import com.twelvemonkeys.imageio.spi.ReaderWriterProviderInfo;
import com.twelvemonkeys.imageio.util.IIOUtil;
import com.twelvemonkeys.lang.Validate;
import java.io.IOException;
import java.util.Locale;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadataFormat;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.spi.ServiceRegistry;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/plugins/jpeg/JPEGImageReaderSpi.class */
public final class JPEGImageReaderSpi extends ImageReaderSpiBase {
    ImageReaderSpi delegateProvider;

    public JPEGImageReaderSpi() {
        this(new JPEGProviderInfo());
    }

    JPEGImageReaderSpi(ImageReaderSpi imageReaderSpi) {
        this();
        this.delegateProvider = (ImageReaderSpi) Validate.notNull(imageReaderSpi);
    }

    private JPEGImageReaderSpi(ReaderWriterProviderInfo readerWriterProviderInfo) {
        super(readerWriterProviderInfo);
    }

    public void onRegistration(ServiceRegistry serviceRegistry, Class<?> cls) {
        if (this.delegateProvider == null) {
            this.delegateProvider = (ImageReaderSpi) IIOUtil.lookupProviderByName(serviceRegistry, "com.sun.imageio.plugins.jpeg.JPEGImageReaderSpi", ImageReaderSpi.class);
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

    public ImageReader createReaderInstance(Object obj) throws IOException {
        return new JPEGImageReader(this, this.delegateProvider.createReaderInstance(obj));
    }

    public boolean canDecodeInput(Object obj) throws IOException {
        return this.delegateProvider.canDecodeInput(obj);
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

    public String getDescription(Locale locale) {
        return this.delegateProvider.getDescription(locale);
    }

    public Class[] getInputTypes() {
        return this.delegateProvider.getInputTypes();
    }
}
