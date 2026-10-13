package com.twelvemonkeys.imageio.stream;

import com.twelvemonkeys.imageio.spi.ProviderInfo;
import java.io.File;
import java.io.RandomAccessFile;
import java.util.Iterator;
import java.util.Locale;
import javax.imageio.spi.ImageInputStreamSpi;
import javax.imageio.spi.ServiceRegistry;
import javax.imageio.stream.ImageInputStream;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/stream/BufferedRAFImageInputStreamSpi.class */
public final class BufferedRAFImageInputStreamSpi extends ImageInputStreamSpi {
    public BufferedRAFImageInputStreamSpi() {
        this(new StreamProviderInfo());
    }

    private BufferedRAFImageInputStreamSpi(ProviderInfo providerInfo) {
        super(providerInfo.getVendorName(), providerInfo.getVersion(), RandomAccessFile.class);
    }

    public void onRegistration(ServiceRegistry serviceRegistry, Class<?> cls) {
        Iterator serviceProviders = serviceRegistry.getServiceProviders(ImageInputStreamSpi.class, new RAFInputFilter(), true);
        while (serviceProviders.hasNext()) {
            ImageInputStreamSpi imageInputStreamSpi = (ImageInputStreamSpi) serviceProviders.next();
            if (imageInputStreamSpi != this) {
                serviceRegistry.setOrdering(ImageInputStreamSpi.class, this, imageInputStreamSpi);
            }
        }
    }

    public ImageInputStream createInputStreamInstance(Object obj, boolean z, File file) {
        if (obj instanceof RandomAccessFile) {
            return new BufferedChannelImageInputStream((RandomAccessFile) obj);
        }
        throw new IllegalArgumentException("Expected input of type RandomAccessFile: " + obj);
    }

    public boolean canUseCacheFile() {
        return false;
    }

    public String getDescription(Locale locale) {
        return "Service provider that instantiates an ImageInputStream from a RandomAccessFile";
    }

    /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/stream/BufferedRAFImageInputStreamSpi$RAFInputFilter.class */
    private static class RAFInputFilter implements ServiceRegistry.Filter {
        private RAFInputFilter() {
        }

        public boolean filter(Object obj) {
            return ((ImageInputStreamSpi) obj).getInputClass() == RandomAccessFile.class;
        }
    }
}
