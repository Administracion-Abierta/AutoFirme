package org.bouncycastle.pqc.crypto.hqc;

import com.sun.jna.platform.win32.WinError;
import org.bouncycastle.pqc.crypto.KEMParameters;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/hqc/HQCParameters.class */
public class HQCParameters implements KEMParameters {
    public static final HQCParameters hqc128 = new HQCParameters("hqc-128", 17669, 46, 384, 16, 31, 15, 66, 75, 75, 16767881, 4, new int[]{89, 69, 153, 116, 176, 117, 111, 75, 73, WinError.ERROR_PIPE_NOT_CONNECTED, 242, WinError.ERROR_PIPE_NOT_CONNECTED, 65, WinError.ERROR_THREAD_1_INACTIVE, 21, 139, 103, 173, 67, 118, 105, WinError.ERROR_THREAD_1_INACTIVE, 174, 110, 74, 69, 228, 82, 255, 181, 1});
    public static final HQCParameters hqc192 = new HQCParameters("hqc-192", 35851, 56, WinError.ERROR_MULTIPLE_FAULT_VIOLATION, 24, 33, 16, 100, 114, 114, 16742417, 5, new int[]{45, WinError.ERROR_EXE_MACHINE_TYPE_MISMATCH, 239, 24, 253, 104, 27, 40, 107, 50, 163, WinError.ERROR_THREAD_1_INACTIVE, 227, 134, 224, 158, 119, 13, 158, 1, 238, 164, 82, 43, 15, WinError.ERROR_NO_DATA, 246, 142, 50, 189, 29, WinError.ERROR_NO_DATA, 1});
    public static final HQCParameters hqc256 = new HQCParameters("hqc-256", 57637, 90, WinError.ERROR_MULTIPLE_FAULT_VIOLATION, 32, 59, 29, 131, 149, 149, 16772367, 5, new int[]{49, 167, 49, 39, 200, 121, 124, 91, 240, 63, 148, 71, 150, 123, 87, 101, 32, WinError.ERROR_NESTING_NOT_ALLOWED, 159, 71, 201, 115, 97, WinError.ERROR_THREAD_1_INACTIVE, 186, 183, 141, WinError.ERROR_EXE_CANNOT_MODIFY_SIGNED_BINARY, 123, 12, 31, 243, 180, 219, 152, 239, 99, 141, 4, 246, 191, 144, 8, WinError.ERROR_NO_DATA, 47, 27, 141, 178, 130, 64, 124, 47, 39, 188, WinError.ERROR_EXE_MACHINE_TYPE_MISMATCH, 48, 199, 187, 1});
    private final String name;
    private int n;
    private int n1;
    private int n2;
    private int k;
    private int g;
    private int delta;
    private int w;
    private int wr;
    private int we;
    private int utilRejectionThreshold;
    private int fft;
    private int[] generatorPoly;
    static final int PARAM_M = 8;
    static final int GF_MUL_ORDER = 255;
    private HQCEngine hqcEngine;

    private HQCParameters(String str, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, int i10, int i11, int[] iArr) {
        this.name = str;
        this.n = i;
        this.n1 = i2;
        this.n2 = i3;
        this.k = i4;
        this.delta = i6;
        this.w = i7;
        this.wr = i8;
        this.we = i9;
        this.generatorPoly = iArr;
        this.g = i5;
        this.utilRejectionThreshold = i10;
        this.fft = i11;
        this.hqcEngine = new HQCEngine(i, i2, i3, i4, i5, i6, i7, i8, i9, i10, i11, iArr);
    }

    int getN() {
        return this.n;
    }

    int getK() {
        return this.k;
    }

    int getDelta() {
        return this.delta;
    }

    int getW() {
        return this.w;
    }

    int getWr() {
        return this.wr;
    }

    int getWe() {
        return this.we;
    }

    int getN1() {
        return this.n1;
    }

    int getN2() {
        return this.n2;
    }

    int getSHA512_BYTES() {
        return 64;
    }

    int getSALT_SIZE_BYTES() {
        return 16;
    }

    int getN_BYTES() {
        return (this.n + 7) / 8;
    }

    int getN1N2_BYTES() {
        return ((this.n1 * this.n2) + 7) / 8;
    }

    HQCEngine getEngine() {
        return this.hqcEngine;
    }

    public int getSessionKeySize() {
        return this.k * 8;
    }

    public String getName() {
        return this.name;
    }
}
