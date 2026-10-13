package com.twelvemonkeys.imageio.stream;

import com.twelvemonkeys.imageio.spi.ProviderInfo;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.Iterator;
import java.util.Locale;
import javax.imageio.spi.ImageInputStreamSpi;
import javax.imageio.spi.ServiceRegistry;
import javax.imageio.stream.ImageInputStream;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/stream/BufferedFileImageInputStreamSpi.class */
public final class BufferedFileImageInputStreamSpi extends ImageInputStreamSpi {
    public BufferedFileImageInputStreamSpi() {
        this(new StreamProviderInfo());
    }

    private BufferedFileImageInputStreamSpi(ProviderInfo providerInfo) {
        super(providerInfo.getVendorName(), providerInfo.getVersion(), File.class);
    }

    public void onRegistration(ServiceRegistry serviceRegistry, Class<?> cls) {
        Iterator serviceProviders = serviceRegistry.getServiceProviders(ImageInputStreamSpi.class, new FileInputFilter(), true);
        while (serviceProviders.hasNext()) {
            ImageInputStreamSpi imageInputStreamSpi = (ImageInputStreamSpi) serviceProviders.next();
            if (imageInputStreamSpi != this) {
                serviceRegistry.setOrdering(ImageInputStreamSpi.class, this, imageInputStreamSpi);
            }
        }
    }

    public ImageInputStream createInputStreamInstance(Object obj, boolean z, File file) throws IOException {
        if (obj instanceof File) {
            try {
                return new BufferedChannelImageInputStream((File) obj);
            } catch (FileNotFoundException | NoSuchFileException e) {
                return null;
            }
        }
        throw new IllegalArgumentException("Expected input of type File: " + obj);
    }

    public boolean canUseCacheFile() {
        return false;
    }

    public String getDescription(Locale locale) {
        return "Service provider that instantiates an ImageInputStream from a File";
    }

    /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/stream/BufferedFileImageInputStreamSpi$FileInputFilter.class */
    private static class FileInputFilter implements ServiceRegistry.Filter {
        private FileInputFilter() {
        }

        public boolean filter(Object obj) {
            return ((ImageInputStreamSpi) obj).getInputClass() == File.class;
        }
    }
}
