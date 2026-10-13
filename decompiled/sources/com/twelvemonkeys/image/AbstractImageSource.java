package com.twelvemonkeys.image;

import java.awt.image.ImageConsumer;
import java.awt.image.ImageProducer;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/image/AbstractImageSource.class */
public abstract class AbstractImageSource implements ImageProducer {
    private List<ImageConsumer> consumers = new ArrayList();
    protected int width;
    protected int height;
    protected int xOff;
    protected int yOff;

    protected abstract void initConsumer(ImageConsumer imageConsumer);

    protected abstract void sendPixels(ImageConsumer imageConsumer);

    public void addConsumer(ImageConsumer imageConsumer) {
        if (this.consumers.contains(imageConsumer)) {
            return;
        }
        this.consumers.add(imageConsumer);
        try {
            initConsumer(imageConsumer);
            sendPixels(imageConsumer);
            if (isConsumer(imageConsumer)) {
                imageConsumer.imageComplete(3);
                if (isConsumer(imageConsumer)) {
                    imageConsumer.imageComplete(1);
                    removeConsumer(imageConsumer);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (isConsumer(imageConsumer)) {
                imageConsumer.imageComplete(1);
            }
        }
    }

    public void removeConsumer(ImageConsumer imageConsumer) {
        this.consumers.remove(imageConsumer);
    }

    public void requestTopDownLeftRightResend(ImageConsumer imageConsumer) {
    }

    public void startProduction(ImageConsumer imageConsumer) {
        addConsumer(imageConsumer);
    }

    public boolean isConsumer(ImageConsumer imageConsumer) {
        return this.consumers.contains(imageConsumer);
    }
}
