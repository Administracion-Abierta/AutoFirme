package org.spongycastle.crypto.generators;

import java.math.BigInteger;
import org.spongycastle.crypto.DataLengthException;
import org.spongycastle.crypto.DerivationParameters;
import org.spongycastle.crypto.Mac;
import org.spongycastle.crypto.MacDerivationFunction;
import org.spongycastle.crypto.params.KDFCounterParameters;
import org.spongycastle.crypto.params.KeyParameter;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/generators/KDFCounterBytesGenerator.class */
public class KDFCounterBytesGenerator implements MacDerivationFunction {
    private static final BigInteger INTEGER_MAX = BigInteger.valueOf(2147483647L);
    private static final BigInteger TWO = BigInteger.valueOf(2);
    private final Mac prf;
    private final int h;
    private byte[] fixedInputDataCtrPrefix;
    private byte[] fixedInputData_afterCtr;
    private int maxSizeExcl;
    private byte[] ios;
    private int generatedBytes;
    private byte[] k;

    public KDFCounterBytesGenerator(Mac prf) {
        this.prf = prf;
        this.h = prf.getMacSize();
        this.k = new byte[this.h];
    }

    @Override // org.spongycastle.crypto.DerivationFunction
    public void init(DerivationParameters param) {
        if (!(param instanceof KDFCounterParameters)) {
            throw new IllegalArgumentException("Wrong type of arguments given");
        }
        KDFCounterParameters kdfParams = (KDFCounterParameters) param;
        this.prf.init(new KeyParameter(kdfParams.getKI()));
        this.fixedInputDataCtrPrefix = kdfParams.getFixedInputDataCounterPrefix();
        this.fixedInputData_afterCtr = kdfParams.getFixedInputDataCounterSuffix();
        int r = kdfParams.getR();
        this.ios = new byte[r / 8];
        BigInteger maxSize = TWO.pow(r).multiply(BigInteger.valueOf(this.h));
        this.maxSizeExcl = maxSize.compareTo(INTEGER_MAX) == 1 ? Integer.MAX_VALUE : maxSize.intValue();
        this.generatedBytes = 0;
    }

    @Override // org.spongycastle.crypto.MacDerivationFunction
    public Mac getMac() {
        return this.prf;
    }

    @Override // org.spongycastle.crypto.DerivationFunction
    public int generateBytes(byte[] out, int outOff, int len) throws DataLengthException, IllegalArgumentException {
        int generatedBytesAfter = this.generatedBytes + len;
        if (generatedBytesAfter < 0 || generatedBytesAfter >= this.maxSizeExcl) {
            throw new DataLengthException("Current KDFCTR may only be used for " + this.maxSizeExcl + " bytes");
        }
        if (this.generatedBytes % this.h == 0) {
            generateNext();
        }
        int toGenerate = len;
        int posInK = this.generatedBytes % this.h;
        int leftInK = this.h - (this.generatedBytes % this.h);
        int toCopy = Math.min(leftInK, toGenerate);
        System.arraycopy(this.k, posInK, out, outOff, toCopy);
        this.generatedBytes += toCopy;
        while (true) {
            toGenerate -= toCopy;
            outOff += toCopy;
            if (toGenerate > 0) {
                generateNext();
                toCopy = Math.min(this.h, toGenerate);
                System.arraycopy(this.k, 0, out, outOff, toCopy);
                this.generatedBytes += toCopy;
            } else {
                return len;
            }
        }
    }

    private void generateNext() {
        int i = (this.generatedBytes / this.h) + 1;
        switch (this.ios.length) {
            case 4:
                this.ios[0] = (byte) (i >>> 24);
            case 3:
                this.ios[this.ios.length - 3] = (byte) (i >>> 16);
            case 2:
                this.ios[this.ios.length - 2] = (byte) (i >>> 8);
            case 1:
                this.ios[this.ios.length - 1] = (byte) i;
                this.prf.update(this.fixedInputDataCtrPrefix, 0, this.fixedInputDataCtrPrefix.length);
                this.prf.update(this.ios, 0, this.ios.length);
                this.prf.update(this.fixedInputData_afterCtr, 0, this.fixedInputData_afterCtr.length);
                this.prf.doFinal(this.k, 0);
                return;
            default:
                throw new IllegalStateException("Unsupported size of counter i");
        }
    }
}
