package com.twelvemonkeys.image;

import com.twelvemonkeys.lang.Validate;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageConsumer;
import java.awt.image.ImageProducer;
import java.lang.reflect.Array;
import java.util.EventListener;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/image/BufferedImageFactory.class */
public final class BufferedImageFactory {
    private List<ProgressListener> listeners;
    private int percentageDone;
    private ImageProducer producer;
    private ImageConversionException consumerException;
    private volatile boolean fetching;
    private boolean readColorModelOnly;
    private int x;
    private int y;
    private int width;
    private int height;
    private int xSub;
    private int ySub;
    private int offset;
    private int scanSize;
    private ColorModel sourceColorModel;
    private Hashtable<?, ?> sourceProperties;
    private Object sourcePixels;
    private BufferedImage buffered;
    private ColorModel colorModel;
    private final Consumer consumer;

    /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/image/BufferedImageFactory$ProgressListener.class */
    public interface ProgressListener extends EventListener {
        void progress(BufferedImageFactory bufferedImageFactory, float f);
    }

    public BufferedImageFactory(Image image) {
        this(image != null ? image.getSource() : null);
    }

    public BufferedImageFactory(ImageProducer imageProducer) {
        this.x = 0;
        this.y = 0;
        this.width = -1;
        this.height = -1;
        this.xSub = 1;
        this.ySub = 1;
        this.consumer = new Consumer();
        Validate.notNull(imageProducer, "source");
        this.producer = imageProducer;
    }

    public BufferedImage getBufferedImage() throws ImageConversionException {
        doFetch(false);
        return this.buffered;
    }

    public ColorModel getColorModel() throws ImageConversionException {
        doFetch(true);
        return this.buffered != null ? this.buffered.getColorModel() : this.colorModel;
    }

    public void dispose() {
        freeResources();
        this.buffered = null;
        this.colorModel = null;
    }

    public void abort() {
        this.consumer.imageComplete(4);
    }

    public void setSourceRegion(Rectangle rectangle) {
        if (this.x != rectangle.x || this.y != rectangle.y || this.width != rectangle.width || this.height != rectangle.height) {
            dispose();
        }
        this.x = rectangle.x;
        this.y = rectangle.y;
        this.width = rectangle.width;
        this.height = rectangle.height;
    }

    public void setSourceSubsampling(int i, int i2) {
        if (this.xSub != i || this.ySub != i2) {
            dispose();
        }
        if (i > 1) {
            this.xSub = i;
        }
        if (i2 > 1) {
            this.ySub = i2;
        }
    }

    private synchronized void doFetch(boolean z) throws ImageConversionException {
        if (this.fetching) {
            return;
        }
        if ((!z && this.buffered == null) || (this.buffered == null && this.sourceColorModel == null)) {
            if (!z && (this.xSub > 1 || this.ySub > 1)) {
                if (this.width > 0 && this.height > 0) {
                    this.width = ((this.width + this.xSub) - 1) / this.xSub;
                    this.height = ((this.height + this.ySub) - 1) / this.ySub;
                    this.x = ((this.x + this.xSub) - 1) / this.xSub;
                    this.y = ((this.y + this.ySub) - 1) / this.ySub;
                }
                this.producer = new FilteredImageSource(this.producer, new SubsamplingFilter(this.xSub, this.ySub));
            }
            this.fetching = true;
            this.readColorModelOnly = z;
            this.producer.startProduction(this.consumer);
            while (this.fetching) {
                try {
                    wait(200L);
                } catch (InterruptedException e) {
                    throw new ImageConversionException("Image conversion aborted: " + e.getMessage(), e);
                }
            }
            try {
                if (this.consumerException != null) {
                    throw new ImageConversionException("Image conversion failed: " + this.consumerException.getMessage(), this.consumerException);
                }
                if (z) {
                    createColorModel();
                } else {
                    createBuffered();
                }
                freeResources();
            } catch (Throwable th) {
                freeResources();
                throw th;
            }
        }
    }

    private void createColorModel() {
        this.colorModel = this.sourceColorModel;
    }

    private void createBuffered() {
        if (this.width > 0 && this.height > 0) {
            if (this.sourceColorModel != null && this.sourcePixels != null) {
                this.buffered = new BufferedImage(this.sourceColorModel, ImageUtil.createRaster(this.width, this.height, this.sourcePixels, this.sourceColorModel), this.sourceColorModel.isAlphaPremultiplied(), this.sourceProperties);
            } else {
                this.buffered = ImageUtil.createClear(this.width, this.height, null);
            }
        }
        if (this.buffered == null) {
            throw new ImageConversionException("Could not create BufferedImage");
        }
    }

    private void freeResources() {
        this.sourceColorModel = null;
        this.sourcePixels = null;
        this.sourceProperties = null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void processProgress(int i) {
        int i2;
        if (this.listeners != null && (i2 = (100 * i) / this.height) > this.percentageDone) {
            this.percentageDone = i2;
            Iterator<ProgressListener> it = this.listeners.iterator();
            while (it.hasNext()) {
                it.next().progress(this, i2);
            }
        }
    }

    public void addProgressListener(ProgressListener progressListener) {
        if (progressListener == null) {
            return;
        }
        if (this.listeners == null) {
            this.listeners = new CopyOnWriteArrayList();
        }
        this.listeners.add(progressListener);
    }

    public void removeProgressListener(ProgressListener progressListener) {
        if (progressListener == null || this.listeners == null) {
            return;
        }
        this.listeners.remove(progressListener);
    }

    public void removeAllProgressListeners() {
        if (this.listeners != null) {
            this.listeners.clear();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static short[] toShortPixels(int[] iArr) {
        short[] sArr = new short[iArr.length];
        for (int i = 0; i < sArr.length; i++) {
            sArr[i] = (short) (iArr[i] & 65535);
        }
        return sArr;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/image/BufferedImageFactory$Consumer.class */
    private class Consumer implements ImageConsumer {
        private Consumer() {
        }

        private void setPixelsImpl(int i, int i2, int i3, int i4, ColorModel colorModel, Object obj, int i5, int i6) {
            setColorModelOnce(colorModel);
            if (obj != null) {
                if (BufferedImageFactory.this.sourcePixels != null) {
                    if (BufferedImageFactory.this.sourcePixels.getClass() != obj.getClass()) {
                        throw new IllegalStateException("Only one pixel type allowed");
                    }
                } else {
                    BufferedImageFactory.this.sourcePixels = Array.newInstance(obj.getClass().getComponentType(), BufferedImageFactory.this.width * BufferedImageFactory.this.height);
                    BufferedImageFactory.this.scanSize = BufferedImageFactory.this.width;
                    BufferedImageFactory.this.offset = 0;
                }
                if (i2 < BufferedImageFactory.this.y) {
                    int i7 = BufferedImageFactory.this.y - i2;
                    if (i7 >= i4) {
                        return;
                    }
                    i5 += i6 * i7;
                    i2 += i7;
                    i4 -= i7;
                }
                if (i2 + i4 > BufferedImageFactory.this.y + BufferedImageFactory.this.height) {
                    i4 = (BufferedImageFactory.this.y + BufferedImageFactory.this.height) - i2;
                    if (i4 <= 0) {
                        return;
                    }
                }
                if (i < BufferedImageFactory.this.x) {
                    int i8 = BufferedImageFactory.this.x - i;
                    if (i8 >= i3) {
                        return;
                    }
                    i5 += i8;
                    i += i8;
                    i3 -= i8;
                }
                if (i + i3 > BufferedImageFactory.this.x + BufferedImageFactory.this.width) {
                    i3 = (BufferedImageFactory.this.x + BufferedImageFactory.this.width) - i;
                    if (i3 <= 0) {
                        return;
                    }
                }
                int i9 = BufferedImageFactory.this.offset + ((i2 - BufferedImageFactory.this.y) * BufferedImageFactory.this.scanSize) + (i - BufferedImageFactory.this.x);
                for (int i10 = i4; i10 > 0; i10--) {
                    System.arraycopy(obj, i5, BufferedImageFactory.this.sourcePixels, i9, i3);
                    i5 += i6;
                    i9 += BufferedImageFactory.this.scanSize;
                }
                BufferedImageFactory.this.processProgress(i2 + i4);
            }
        }

        public void setPixels(int i, int i2, int i3, int i4, ColorModel colorModel, short[] sArr, int i5, int i6) {
            setPixelsImpl(i, i2, i3, i4, colorModel, sArr, i5, i6);
        }

        private void setColorModelOnce(ColorModel colorModel) {
            if (BufferedImageFactory.this.sourceColorModel != colorModel) {
                if (BufferedImageFactory.this.sourcePixels == null) {
                    BufferedImageFactory.this.sourceColorModel = colorModel;
                } else {
                    throw new IllegalStateException("Change of ColorModel after pixel delivery not supported");
                }
            }
            if (BufferedImageFactory.this.readColorModelOnly) {
                BufferedImageFactory.this.consumer.imageComplete(4);
            }
        }

        public void imageComplete(int i) {
            BufferedImageFactory.this.fetching = false;
            if (BufferedImageFactory.this.producer != null) {
                BufferedImageFactory.this.producer.removeConsumer(this);
            }
            if (i == 1) {
                BufferedImageFactory.this.consumerException = new ImageConversionException("ImageConsumer.IMAGEERROR");
            }
            synchronized (BufferedImageFactory.this) {
                BufferedImageFactory.this.notifyAll();
            }
        }

        public void setColorModel(ColorModel colorModel) {
            setColorModelOnce(colorModel);
        }

        public void setDimensions(int i, int i2) {
            if (BufferedImageFactory.this.width < 0) {
                BufferedImageFactory.this.width = i - BufferedImageFactory.this.x;
            }
            if (BufferedImageFactory.this.height < 0) {
                BufferedImageFactory.this.height = i2 - BufferedImageFactory.this.y;
            }
            if (BufferedImageFactory.this.width <= 0 || BufferedImageFactory.this.height <= 0) {
                imageComplete(3);
            }
        }

        public void setHints(int i) {
        }

        public void setPixels(int i, int i2, int i3, int i4, ColorModel colorModel, byte[] bArr, int i5, int i6) {
            setPixelsImpl(i, i2, i3, i4, colorModel, bArr, i5, i6);
        }

        public void setPixels(int i, int i2, int i3, int i4, ColorModel colorModel, int[] iArr, int i5, int i6) {
            if (colorModel.getTransferType() == 1) {
                setPixelsImpl(i, i2, i3, i4, colorModel, BufferedImageFactory.toShortPixels(iArr), i5, i6);
            } else {
                setPixelsImpl(i, i2, i3, i4, colorModel, iArr, i5, i6);
            }
        }

        public void setProperties(Hashtable hashtable) {
            BufferedImageFactory.this.sourceProperties = hashtable;
        }
    }
}
