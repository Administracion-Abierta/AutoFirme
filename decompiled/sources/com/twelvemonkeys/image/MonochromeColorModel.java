package com.twelvemonkeys.image;

import com.sun.jna.platform.win32.WinError;
import java.awt.image.IndexColorModel;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/image/MonochromeColorModel.class */
public class MonochromeColorModel extends IndexColorModel {
    private static final int[] MONO_PALETTE = {0, 16777215};
    private static MonochromeColorModel sInstance = new MonochromeColorModel();

    private MonochromeColorModel() {
        super(1, 2, MONO_PALETTE, 0, false, -1, 0);
    }

    public static IndexColorModel getInstance() {
        return sInstance;
    }

    public synchronized Object getDataElements(int i, Object obj) {
        byte[] bArr;
        int i2 = (((WinError.ERROR_BAD_FILE_TYPE * ((i >> 16) & 255)) + (WinError.ERROR_RECEIVE_PARTIAL * ((i >> 8) & 255))) + (71 * (i & 255))) / 1000;
        if (obj != null) {
            bArr = (byte[]) obj;
        } else {
            bArr = new byte[1];
        }
        if (i2 <= 128) {
            bArr[0] = 0;
        } else {
            bArr[0] = 1;
        }
        return bArr;
    }
}
