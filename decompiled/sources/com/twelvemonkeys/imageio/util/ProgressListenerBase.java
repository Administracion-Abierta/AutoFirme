package com.twelvemonkeys.imageio.util;

import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;
import javax.imageio.event.IIOReadProgressListener;
import javax.imageio.event.IIOWriteProgressListener;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/util/ProgressListenerBase.class */
public abstract class ProgressListenerBase implements IIOReadProgressListener, IIOWriteProgressListener {
    protected ProgressListenerBase() {
    }

    public void imageComplete(ImageReader imageReader) {
    }

    public void imageProgress(ImageReader imageReader, float f) {
    }

    public void imageStarted(ImageReader imageReader, int i) {
    }

    public void readAborted(ImageReader imageReader) {
    }

    public void sequenceComplete(ImageReader imageReader) {
    }

    public void sequenceStarted(ImageReader imageReader, int i) {
    }

    public void thumbnailComplete(ImageReader imageReader) {
    }

    public void thumbnailProgress(ImageReader imageReader, float f) {
    }

    public void thumbnailStarted(ImageReader imageReader, int i, int i2) {
    }

    public void imageComplete(ImageWriter imageWriter) {
    }

    public void imageProgress(ImageWriter imageWriter, float f) {
    }

    public void imageStarted(ImageWriter imageWriter, int i) {
    }

    public void thumbnailComplete(ImageWriter imageWriter) {
    }

    public void thumbnailProgress(ImageWriter imageWriter, float f) {
    }

    public void thumbnailStarted(ImageWriter imageWriter, int i, int i2) {
    }

    public void writeAborted(ImageWriter imageWriter) {
    }
}
