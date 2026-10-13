package com.twelvemonkeys.imageio.stream;

import com.twelvemonkeys.imageio.spi.ProviderInfo;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.util.Iterator;
import java.util.Locale;
import javax.imageio.spi.ImageInputStreamSpi;
import javax.imageio.spi.ServiceRegistry;
import javax.imageio.stream.ImageInputStream;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/stream/BufferedInputStreamImageInputStreamSpi.class */
public final class BufferedInputStreamImageInputStreamSpi extends ImageInputStreamSpi {
    public BufferedInputStreamImageInputStreamSpi() {
        this(new StreamProviderInfo());
    }

    private BufferedInputStreamImageInputStreamSpi(ProviderInfo providerInfo) {
        super(providerInfo.getVendorName(), providerInfo.getVersion(), InputStream.class);
    }

    public void onRegistration(ServiceRegistry serviceRegistry, Class<?> cls) {
        Iterator serviceProviders = serviceRegistry.getServiceProviders(ImageInputStreamSpi.class, new InputStreamFilter(), true);
        while (serviceProviders.hasNext()) {
            ImageInputStreamSpi imageInputStreamSpi = (ImageInputStreamSpi) serviceProviders.next();
            if (imageInputStreamSpi != this) {
                serviceRegistry.setOrdering(ImageInputStreamSpi.class, this, imageInputStreamSpi);
            }
        }
    }

    public ImageInputStream createInputStreamInstance(Object obj, boolean z, File file) throws IOException {
        if (obj instanceof InputStream) {
            ReadableByteChannel readableByteChannelNewChannel = Channels.newChannel((InputStream) obj);
            if (readableByteChannelNewChannel instanceof SeekableByteChannel) {
                return new BufferedChannelImageInputStream((SeekableByteChannel) readableByteChannelNewChannel);
            }
            return new BufferedChannelImageInputStream(z ? new FileCache(readableByteChannelNewChannel, file) : new MemoryCache(readableByteChannelNewChannel));
        }
        throw new IllegalArgumentException("Expected input of type InputStream: " + obj);
    }

    public boolean canUseCacheFile() {
        return true;
    }

    public String getDescription(Locale locale) {
        return "Service provider that instantiates an ImageInputStream from an InputStream";
    }

    /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/stream/BufferedInputStreamImageInputStreamSpi$InputStreamFilter.class */
    private static class InputStreamFilter implements ServiceRegistry.Filter {
        private InputStreamFilter() {
        }

        public boolean filter(Object obj) {
            return ((ImageInputStreamSpi) obj).getInputClass() == InputStream.class;
        }
    }
}
