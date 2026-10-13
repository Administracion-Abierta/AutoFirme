package com.twelvemonkeys.imageio.spi;

import javax.imageio.spi.ImageWriterSpi;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/spi/ImageWriterSpiBase.class */
public abstract class ImageWriterSpiBase extends ImageWriterSpi {
    protected ImageWriterSpiBase(ReaderWriterProviderInfo readerWriterProviderInfo) {
        super(readerWriterProviderInfo.getVendorName(), readerWriterProviderInfo.getVersion(), readerWriterProviderInfo.formatNames(), readerWriterProviderInfo.suffixes(), readerWriterProviderInfo.mimeTypes(), readerWriterProviderInfo.writerClassName(), readerWriterProviderInfo.outputTypes(), readerWriterProviderInfo.readerSpiClassNames(), readerWriterProviderInfo.supportsStandardStreamMetadataFormat(), readerWriterProviderInfo.nativeStreamMetadataFormatName(), readerWriterProviderInfo.nativeStreamMetadataFormatClassName(), readerWriterProviderInfo.extraStreamMetadataFormatNames(), readerWriterProviderInfo.extraStreamMetadataFormatClassNames(), readerWriterProviderInfo.supportsStandardImageMetadataFormat(), readerWriterProviderInfo.nativeImageMetadataFormatName(), readerWriterProviderInfo.nativeImageMetadataFormatClassName(), readerWriterProviderInfo.extraImageMetadataFormatNames(), readerWriterProviderInfo.extraImageMetadataFormatClassNames());
    }
}
