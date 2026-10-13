package com.twelvemonkeys.imageio.stream;

import com.twelvemonkeys.imageio.spi.ProviderInfo;
import java.io.File;
import java.util.Locale;
import javax.imageio.spi.ImageInputStreamSpi;
import javax.imageio.stream.ImageInputStream;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/stream/ByteArrayImageInputStreamSpi.class */
public final class ByteArrayImageInputStreamSpi extends ImageInputStreamSpi {
    public ByteArrayImageInputStreamSpi() {
        this(new StreamProviderInfo());
    }

    private ByteArrayImageInputStreamSpi(ProviderInfo providerInfo) {
        super(providerInfo.getVendorName(), providerInfo.getVersion(), byte[].class);
    }

    public ImageInputStream createInputStreamInstance(Object obj, boolean z, File file) {
        if (obj instanceof byte[]) {
            return new ByteArrayImageInputStream((byte[]) obj);
        }
        throw new IllegalArgumentException("Expected input of type byte[]: " + obj);
    }

    public String getDescription(Locale locale) {
        return "Service provider that instantiates an ImageInputStream from a byte array";
    }
}
