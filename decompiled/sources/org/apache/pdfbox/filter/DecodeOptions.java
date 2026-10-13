package org.apache.pdfbox.filter;

import java.awt.Rectangle;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/filter/DecodeOptions.class */
public class DecodeOptions {
    public static final DecodeOptions DEFAULT = new FinalDecodeOptions(true);
    private Rectangle sourceRegion;
    private int subsamplingX;
    private int subsamplingY;
    private int subsamplingOffsetX;
    private int subsamplingOffsetY;
    private boolean filterSubsampled;

    public DecodeOptions() {
        this.sourceRegion = null;
        this.subsamplingX = 1;
        this.subsamplingY = 1;
        this.subsamplingOffsetX = 0;
        this.subsamplingOffsetY = 0;
        this.filterSubsampled = false;
    }

    public DecodeOptions(Rectangle sourceRegion) {
        this.sourceRegion = null;
        this.subsamplingX = 1;
        this.subsamplingY = 1;
        this.subsamplingOffsetX = 0;
        this.subsamplingOffsetY = 0;
        this.filterSubsampled = false;
        this.sourceRegion = sourceRegion;
    }

    public DecodeOptions(int x, int y, int width, int height) {
        this(new Rectangle(x, y, width, height));
    }

    public DecodeOptions(int subsampling) {
        this.sourceRegion = null;
        this.subsamplingX = 1;
        this.subsamplingY = 1;
        this.subsamplingOffsetX = 0;
        this.subsamplingOffsetY = 0;
        this.filterSubsampled = false;
        this.subsamplingX = subsampling;
        this.subsamplingY = subsampling;
    }

    public Rectangle getSourceRegion() {
        return this.sourceRegion;
    }

    public void setSourceRegion(Rectangle sourceRegion) {
        this.sourceRegion = sourceRegion;
    }

    public int getSubsamplingX() {
        return this.subsamplingX;
    }

    public void setSubsamplingX(int ssX) {
        this.subsamplingX = ssX;
    }

    public int getSubsamplingY() {
        return this.subsamplingY;
    }

    public void setSubsamplingY(int ssY) {
        this.subsamplingY = ssY;
    }

    public int getSubsamplingOffsetX() {
        return this.subsamplingOffsetX;
    }

    public void setSubsamplingOffsetX(int ssOffsetX) {
        this.subsamplingOffsetX = ssOffsetX;
    }

    public int getSubsamplingOffsetY() {
        return this.subsamplingOffsetY;
    }

    public void setSubsamplingOffsetY(int ssOffsetY) {
        this.subsamplingOffsetY = ssOffsetY;
    }

    public boolean isFilterSubsampled() {
        return this.filterSubsampled;
    }

    void setFilterSubsampled(boolean filterSubsampled) {
        this.filterSubsampled = filterSubsampled;
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/filter/DecodeOptions$FinalDecodeOptions.class */
    private static class FinalDecodeOptions extends DecodeOptions {
        FinalDecodeOptions(boolean filterSubsampled) {
            super.setFilterSubsampled(filterSubsampled);
        }

        @Override // org.apache.pdfbox.filter.DecodeOptions
        public void setSourceRegion(Rectangle sourceRegion) {
            throw new UnsupportedOperationException("This instance may not be modified.");
        }

        @Override // org.apache.pdfbox.filter.DecodeOptions
        public void setSubsamplingX(int ssX) {
            throw new UnsupportedOperationException("This instance may not be modified.");
        }

        @Override // org.apache.pdfbox.filter.DecodeOptions
        public void setSubsamplingY(int ssY) {
            throw new UnsupportedOperationException("This instance may not be modified.");
        }

        @Override // org.apache.pdfbox.filter.DecodeOptions
        public void setSubsamplingOffsetX(int ssOffsetX) {
            throw new UnsupportedOperationException("This instance may not be modified.");
        }

        @Override // org.apache.pdfbox.filter.DecodeOptions
        public void setSubsamplingOffsetY(int ssOffsetY) {
            throw new UnsupportedOperationException("This instance may not be modified.");
        }

        @Override // org.apache.pdfbox.filter.DecodeOptions
        void setFilterSubsampled(boolean filterSubsampled) {
        }
    }
}
