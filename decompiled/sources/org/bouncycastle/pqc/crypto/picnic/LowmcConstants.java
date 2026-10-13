package org.bouncycastle.pqc.crypto.picnic;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Properties;
import org.bouncycastle.util.Pack;
import org.bouncycastle.util.encoders.Hex;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/picnic/LowmcConstants.class */
abstract class LowmcConstants {
    protected int[] linearMatrices;
    protected int[] roundConstants;
    protected int[] keyMatrices;
    protected KMatrices LMatrix;
    protected KMatrices KMatrix;
    protected KMatrices RConstants;
    protected int[] linearMatrices_full;
    protected int[] keyMatrices_full;
    protected int[] keyMatrices_inv;
    protected int[] linearMatrices_inv;
    protected int[] roundConstants_full;
    protected KMatrices LMatrix_full;
    protected KMatrices LMatrix_inv;
    protected KMatrices KMatrix_full;
    protected KMatrices KMatrix_inv;
    protected KMatrices RConstants_full;

    LowmcConstants() {
    }

    static int[] readArray(DataInputStream dataInputStream) throws IOException {
        int[] iArr = new int[dataInputStream.readInt()];
        for (int i = 0; i != iArr.length; i++) {
            iArr[i] = dataInputStream.readInt();
        }
        return iArr;
    }

    static int[] ReadFromProperty(Properties properties, String str, int i) {
        byte[] bArrDecode = Hex.decode(removeCommas(properties.getProperty(str)));
        int[] iArr = new int[i];
        for (int i2 = 0; i2 < bArrDecode.length / 4; i2++) {
            iArr[i2] = Pack.littleEndianToInt(bArrDecode, i2 * 4);
        }
        return iArr;
    }

    private static byte[] removeCommas(String str) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        for (int i = 0; i != str.length(); i++) {
            if (str.charAt(i) != ',') {
                byteArrayOutputStream.write(str.charAt(i));
            }
        }
        return byteArrayOutputStream.toByteArray();
    }

    private KMatricesWithPointer GET_MAT(KMatrices kMatrices, int i) {
        KMatricesWithPointer kMatricesWithPointer = new KMatricesWithPointer(kMatrices);
        kMatricesWithPointer.setMatrixPointer(i * kMatricesWithPointer.getSize());
        return kMatricesWithPointer;
    }

    protected KMatricesWithPointer LMatrix(PicnicEngine picnicEngine, int i) {
        if (picnicEngine.stateSizeBits == 128) {
            return GET_MAT(this.LMatrix, i);
        }
        if (picnicEngine.stateSizeBits == 129) {
            return GET_MAT(this.LMatrix_full, i);
        }
        if (picnicEngine.stateSizeBits == 192) {
            return picnicEngine.numRounds == 4 ? GET_MAT(this.LMatrix_full, i) : GET_MAT(this.LMatrix, i);
        }
        if (picnicEngine.stateSizeBits == 255) {
            return GET_MAT(this.LMatrix_full, i);
        }
        if (picnicEngine.stateSizeBits == 256) {
            return GET_MAT(this.LMatrix, i);
        }
        return null;
    }

    protected KMatricesWithPointer LMatrixInv(PicnicEngine picnicEngine, int i) {
        if (picnicEngine.stateSizeBits == 129) {
            return GET_MAT(this.LMatrix_inv, i);
        }
        if ((picnicEngine.stateSizeBits == 192 && picnicEngine.numRounds == 4) || picnicEngine.stateSizeBits == 255) {
            return GET_MAT(this.LMatrix_inv, i);
        }
        return null;
    }

    protected KMatricesWithPointer KMatrix(PicnicEngine picnicEngine, int i) {
        if (picnicEngine.stateSizeBits == 128) {
            return GET_MAT(this.KMatrix, i);
        }
        if (picnicEngine.stateSizeBits == 129) {
            return GET_MAT(this.KMatrix_full, i);
        }
        if (picnicEngine.stateSizeBits == 192) {
            return picnicEngine.numRounds == 4 ? GET_MAT(this.KMatrix_full, i) : GET_MAT(this.KMatrix, i);
        }
        if (picnicEngine.stateSizeBits == 255) {
            return GET_MAT(this.KMatrix_full, i);
        }
        if (picnicEngine.stateSizeBits == 256) {
            return GET_MAT(this.KMatrix, i);
        }
        return null;
    }

    protected KMatricesWithPointer KMatrixInv(PicnicEngine picnicEngine) {
        if (picnicEngine.stateSizeBits == 129) {
            return GET_MAT(this.KMatrix_inv, 0);
        }
        if ((picnicEngine.stateSizeBits == 192 && picnicEngine.numRounds == 4) || picnicEngine.stateSizeBits == 255) {
            return GET_MAT(this.KMatrix_inv, 0);
        }
        return null;
    }

    protected KMatricesWithPointer RConstant(PicnicEngine picnicEngine, int i) {
        if (picnicEngine.stateSizeBits == 128) {
            return GET_MAT(this.RConstants, i);
        }
        if (picnicEngine.stateSizeBits == 129) {
            return GET_MAT(this.RConstants_full, i);
        }
        if (picnicEngine.stateSizeBits == 192) {
            return picnicEngine.numRounds == 4 ? GET_MAT(this.RConstants_full, i) : GET_MAT(this.RConstants, i);
        }
        if (picnicEngine.stateSizeBits == 255) {
            return GET_MAT(this.RConstants_full, i);
        }
        if (picnicEngine.stateSizeBits == 256) {
            return GET_MAT(this.RConstants, i);
        }
        return null;
    }
}
