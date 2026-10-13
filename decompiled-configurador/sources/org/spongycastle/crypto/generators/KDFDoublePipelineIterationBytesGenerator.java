package org.spongycastle.crypto.generators;

import java.math.BigInteger;
import org.spongycastle.crypto.DataLengthException;
import org.spongycastle.crypto.DerivationParameters;
import org.spongycastle.crypto.Mac;
import org.spongycastle.crypto.MacDerivationFunction;
import org.spongycastle.crypto.params.KDFDoublePipelineIterationParameters;
import org.spongycastle.crypto.params.KeyParameter;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/generators/KDFDoublePipelineIterationBytesGenerator.class */
public class KDFDoublePipelineIterationBytesGenerator implements MacDerivationFunction {
    private static final BigInteger INTEGER_MAX = BigInteger.valueOf(2147483647L);
    private static final BigInteger TWO = BigInteger.valueOf(2);
    private final Mac prf;
    private final int h;
    private byte[] fixedInputData;
    private int maxSizeExcl;
    private byte[] ios;
    private boolean useCounter;
    private int generatedBytes;
    private byte[] a;
    private byte[] k;

    public KDFDoublePipelineIterationBytesGenerator(Mac prf) {
        this.prf = prf;
        this.h = prf.getMacSize();
        this.a = new byte[this.h];
        this.k = new byte[this.h];
    }

    @Override // org.spongycastle.crypto.DerivationFunction
    public void init(DerivationParameters params) {
        if (!(params instanceof KDFDoublePipelineIterationParameters)) {
            throw new IllegalArgumentException("Wrong type of arguments given");
        }
        KDFDoublePipelineIterationParameters dpiParams = (KDFDoublePipelineIterationParameters) params;
        this.prf.init(new KeyParameter(dpiParams.getKI()));
        this.fixedInputData = dpiParams.getFixedInputData();
        int r = dpiParams.getR();
        this.ios = new byte[r / 8];
        if (dpiParams.useCounter()) {
            BigInteger maxSize = TWO.pow(r).multiply(BigInteger.valueOf(this.h));
            this.maxSizeExcl = maxSize.compareTo(INTEGER_MAX) == 1 ? Integer.MAX_VALUE : maxSize.intValue();
        } else {
            this.maxSizeExcl = Integer.MAX_VALUE;
        }
        this.useCounter = dpiParams.useCounter();
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
        if (this.generatedBytes == 0) {
            this.prf.update(this.fixedInputData, 0, this.fixedInputData.length);
            this.prf.doFinal(this.a, 0);
        } else {
            this.prf.update(this.a, 0, this.a.length);
            this.prf.doFinal(this.a, 0);
        }
        this.prf.update(this.a, 0, this.a.length);
        if (this.useCounter) {
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
                    this.prf.update(this.ios, 0, this.ios.length);
                    break;
                default:
                    throw new IllegalStateException("Unsupported size of counter i");
            }
        }
        this.prf.update(this.fixedInputData, 0, this.fixedInputData.length);
        this.prf.doFinal(this.k, 0);
    }
}
