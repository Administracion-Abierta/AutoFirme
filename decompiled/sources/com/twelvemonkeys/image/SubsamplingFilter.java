package com.twelvemonkeys.image;

import java.awt.image.ReplicateScaleFilter;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/image/SubsamplingFilter.class */
public class SubsamplingFilter extends ReplicateScaleFilter {
    private int xSub;
    private int ySub;

    public SubsamplingFilter(int i, int i2) {
        super(1, 1);
        if (i < 1 || i2 < 1) {
            throw new IllegalArgumentException("Subsampling factors must be positive.");
        }
        this.xSub = i;
        this.ySub = i2;
    }

    public void setDimensions(int i, int i2) {
        this.destWidth = ((i + this.xSub) - 1) / this.xSub;
        this.destHeight = ((i2 + this.ySub) - 1) / this.ySub;
        super.setDimensions(i, i2);
    }
}
