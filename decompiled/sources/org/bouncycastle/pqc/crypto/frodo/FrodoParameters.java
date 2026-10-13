package org.bouncycastle.pqc.crypto.frodo;

import com.sun.jna.platform.win32.WinError;
import org.bouncycastle.crypto.Xof;
import org.bouncycastle.crypto.digests.SHAKEDigest;
import org.bouncycastle.pqc.crypto.KEMParameters;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/frodo/FrodoParameters.class */
public class FrodoParameters implements KEMParameters {
    private static final short[] cdf_table640 = {4643, 13363, 20579, 25843, 29227, 31145, 32103, 32525, 32689, 32745, 32762, 32766, Short.MAX_VALUE};
    private static final short[] cdf_table976 = {5638, 15915, 23689, 28571, 31116, 32217, 32613, 32731, 32760, 32766, Short.MAX_VALUE};
    private static final short[] cdf_table1344 = {9142, 23462, 30338, 32361, 32725, 32765, Short.MAX_VALUE};
    public static final FrodoParameters frodokem640aes = new FrodoParameters("frodokem640aes", WinError.ERROR_MULTIPLE_FAULT_VIOLATION, 15, 2, cdf_table640, new SHAKEDigest(128), new FrodoMatrixGenerator.Aes128MatrixGenerator(WinError.ERROR_MULTIPLE_FAULT_VIOLATION, 32768));
    public static final FrodoParameters frodokem640shake = new FrodoParameters("frodokem640shake", WinError.ERROR_MULTIPLE_FAULT_VIOLATION, 15, 2, cdf_table640, new SHAKEDigest(128), new FrodoMatrixGenerator.Shake128MatrixGenerator(WinError.ERROR_MULTIPLE_FAULT_VIOLATION, 32768));
    public static final FrodoParameters frodokem976aes = new FrodoParameters("frodokem976aes", 976, 16, 3, cdf_table976, new SHAKEDigest(256), new FrodoMatrixGenerator.Aes128MatrixGenerator(976, 65536));
    public static final FrodoParameters frodokem976shake = new FrodoParameters("frodokem976shake", 976, 16, 3, cdf_table976, new SHAKEDigest(256), new FrodoMatrixGenerator.Shake128MatrixGenerator(976, 65536));
    public static final FrodoParameters frodokem1344aes = new FrodoParameters("frodokem1344aes", WinError.ERROR_ALLOTTED_SPACE_EXCEEDED, 16, 4, cdf_table1344, new SHAKEDigest(256), new FrodoMatrixGenerator.Aes128MatrixGenerator(WinError.ERROR_ALLOTTED_SPACE_EXCEEDED, 65536));
    public static final FrodoParameters frodokem1344shake = new FrodoParameters("frodokem1344shake", WinError.ERROR_ALLOTTED_SPACE_EXCEEDED, 16, 4, cdf_table1344, new SHAKEDigest(256), new FrodoMatrixGenerator.Shake128MatrixGenerator(WinError.ERROR_ALLOTTED_SPACE_EXCEEDED, 65536));
    private final String name;
    private final int n;
    private final int D;
    private final int B;
    private final int defaultKeySize;
    private final FrodoEngine engine;

    private FrodoParameters(String str, int i, int i2, int i3, short[] sArr, Xof xof, FrodoMatrixGenerator frodoMatrixGenerator) {
        this.name = str;
        this.n = i;
        this.D = i2;
        this.B = i3;
        this.defaultKeySize = i3 * 8 * 8;
        this.engine = new FrodoEngine(i, i2, i3, sArr, xof, frodoMatrixGenerator);
    }

    public String getName() {
        return this.name;
    }

    public int getSessionKeySize() {
        return this.defaultKeySize;
    }

    FrodoEngine getEngine() {
        return this.engine;
    }

    int getN() {
        return this.n;
    }

    int getD() {
        return this.D;
    }

    int getB() {
        return this.B;
    }
}
