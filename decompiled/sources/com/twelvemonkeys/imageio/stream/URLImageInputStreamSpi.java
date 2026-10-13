package com.twelvemonkeys.imageio.stream;

import com.twelvemonkeys.imageio.spi.ProviderInfo;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Locale;
import javax.imageio.spi.ImageInputStreamSpi;
import javax.imageio.stream.ImageInputStream;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/stream/URLImageInputStreamSpi.class */
public final class URLImageInputStreamSpi extends ImageInputStreamSpi {
    public URLImageInputStreamSpi() {
        this(new StreamProviderInfo());
    }

    private URLImageInputStreamSpi(ProviderInfo providerInfo) {
        super(providerInfo.getVendorName(), providerInfo.getVersion(), URL.class);
    }

    public ImageInputStream createInputStreamInstance(Object obj, boolean z, File file) throws IOException {
        if (obj instanceof URL) {
            URL url = (URL) obj;
            if ("file".equals(url.getProtocol())) {
                try {
                    return new BufferedChannelImageInputStream(new File(url.toURI()));
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
            InputStream inputStreamOpenStream = url.openStream();
            return new BufferedChannelImageInputStream(z ? new FileCache(inputStreamOpenStream, file) : new MemoryCache(inputStreamOpenStream));
        }
        throw new IllegalArgumentException("Expected input of type URL: " + obj);
    }

    public boolean canUseCacheFile() {
        return true;
    }

    public String getDescription(Locale locale) {
        return "Service provider that instantiates an ImageInputStream from a URL";
    }
}
