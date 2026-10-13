package com.twelvemonkeys.image;

import com.twelvemonkeys.lang.StringUtil;
import com.twelvemonkeys.lang.Validate;
import java.awt.Image;
import java.awt.image.IndexColorModel;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/image/InverseColorMapIndexColorModel.class */
public class InverseColorMapIndexColorModel extends IndexColorModel {
    protected int[] rgbs;
    protected int mapSize;
    protected InverseColorMap inverseMap;
    private static final int ALPHA_THRESHOLD = 128;
    private int whiteIndex;
    private static final int WHITE = 16777215;
    private static final int RGB_MASK = 16777215;

    public InverseColorMapIndexColorModel(IndexColorModel indexColorModel) {
        this((IndexColorModel) Validate.notNull(indexColorModel, "color model"), getRGBs(indexColorModel));
    }

    private InverseColorMapIndexColorModel(IndexColorModel indexColorModel, int[] iArr) {
        super(indexColorModel.getComponentSize()[0], indexColorModel.getMapSize(), iArr, 0, indexColorModel.getTransferType(), indexColorModel.getValidPixels());
        this.inverseMap = null;
        this.whiteIndex = -1;
        this.rgbs = iArr;
        this.mapSize = this.rgbs.length;
        this.inverseMap = new InverseColorMap(this.rgbs);
        this.whiteIndex = getWhiteIndex();
    }

    private static int[] getRGBs(IndexColorModel indexColorModel) {
        int[] iArr = new int[indexColorModel.getMapSize()];
        indexColorModel.getRGBs(iArr);
        return iArr;
    }

    public InverseColorMapIndexColorModel(int i, int i2, int[] iArr, int i3, boolean z, int i4, int i5) {
        super(i, i2, iArr, i3, z, i4, i5);
        this.inverseMap = null;
        this.whiteIndex = -1;
        this.rgbs = getRGBs(this);
        this.mapSize = this.rgbs.length;
        this.inverseMap = new InverseColorMap(this.rgbs, i4);
        this.whiteIndex = getWhiteIndex();
    }

    public InverseColorMapIndexColorModel(int i, int i2, byte[] bArr, byte[] bArr2, byte[] bArr3, int i3) {
        super(i, i2, bArr, bArr2, bArr3, i3);
        this.inverseMap = null;
        this.whiteIndex = -1;
        this.rgbs = getRGBs(this);
        this.mapSize = this.rgbs.length;
        this.inverseMap = new InverseColorMap(this.rgbs, i3);
        this.whiteIndex = getWhiteIndex();
    }

    public InverseColorMapIndexColorModel(int i, int i2, byte[] bArr, byte[] bArr2, byte[] bArr3) {
        super(i, i2, bArr, bArr2, bArr3);
        this.inverseMap = null;
        this.whiteIndex = -1;
        this.rgbs = getRGBs(this);
        this.mapSize = this.rgbs.length;
        this.inverseMap = new InverseColorMap(this.rgbs);
        this.whiteIndex = getWhiteIndex();
    }

    private int getWhiteIndex() {
        for (int i = 0; i < this.rgbs.length; i++) {
            if ((this.rgbs[i] & 16777215) == 16777215) {
                return i;
            }
        }
        return -1;
    }

    public static IndexColorModel create(Image image, int i, int i2) {
        InverseColorMapIndexColorModel inverseColorMapIndexColorModel;
        IndexColorModel indexColorModel = IndexImage.getIndexColorModel(image, i, i2);
        if (indexColorModel instanceof InverseColorMapIndexColorModel) {
            inverseColorMapIndexColorModel = (InverseColorMapIndexColorModel) indexColorModel;
        } else {
            inverseColorMapIndexColorModel = new InverseColorMapIndexColorModel(indexColorModel);
        }
        return inverseColorMapIndexColorModel;
    }

    public Object getDataElements(int i, Object obj) {
        int indexNearest;
        if ((i >>> 24) < 128 && getTransparentPixel() != -1) {
            indexNearest = getTransparentPixel();
        } else {
            int i2 = i & 16777215;
            if (i2 == 16777215 && this.whiteIndex != -1) {
                indexNearest = this.whiteIndex;
            } else {
                indexNearest = this.inverseMap.getIndexNearest(i2);
            }
        }
        return installpixel(obj, indexNearest);
    }

    private Object installpixel(Object obj, int i) {
        short[] sArr;
        byte[] bArr;
        int[] iArr;
        switch (this.transferType) {
            case 0:
                if (obj == null) {
                    byte[] bArr2 = new byte[1];
                    bArr = bArr2;
                    obj = bArr2;
                } else {
                    bArr = (byte[]) obj;
                }
                bArr[0] = (byte) i;
                break;
            case 1:
                if (obj == null) {
                    short[] sArr2 = new short[1];
                    sArr = sArr2;
                    obj = sArr2;
                } else {
                    sArr = (short[]) obj;
                }
                sArr[0] = (short) i;
                break;
            case 2:
            default:
                throw new UnsupportedOperationException("This method has not been implemented for transferType " + this.transferType);
            case 3:
                if (obj == null) {
                    int[] iArr2 = new int[1];
                    iArr = iArr2;
                    obj = iArr2;
                } else {
                    iArr = (int[]) obj;
                }
                iArr[0] = i;
                break;
        }
        return obj;
    }

    public String toString() {
        return StringUtil.replace(super.toString(), "IndexColorModel: ", getClass().getName() + ": ");
    }
}
