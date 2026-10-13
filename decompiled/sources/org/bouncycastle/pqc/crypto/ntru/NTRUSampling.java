package org.bouncycastle.pqc.crypto.ntru;

import com.github.markusbernhardt.proxy.search.wpad.dhcp.DHCPOptions;
import org.bouncycastle.pqc.math.ntru.HPSPolynomial;
import org.bouncycastle.pqc.math.ntru.HRSSPolynomial;
import org.bouncycastle.pqc.math.ntru.Polynomial;
import org.bouncycastle.pqc.math.ntru.parameters.NTRUHPSParameterSet;
import org.bouncycastle.pqc.math.ntru.parameters.NTRUHRSSParameterSet;
import org.bouncycastle.pqc.math.ntru.parameters.NTRUParameterSet;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/ntru/NTRUSampling.class */
class NTRUSampling {
    private final NTRUParameterSet params;

    public NTRUSampling(NTRUParameterSet nTRUParameterSet) {
        this.params = nTRUParameterSet;
    }

    public PolynomialPair sampleFg(byte[] bArr) {
        if (this.params instanceof NTRUHRSSParameterSet) {
            return new PolynomialPair(sampleIidPlus(Arrays.copyOfRange(bArr, 0, this.params.sampleIidBytes())), sampleIidPlus(Arrays.copyOfRange(bArr, this.params.sampleIidBytes(), bArr.length)));
        }
        if (this.params instanceof NTRUHPSParameterSet) {
            return new PolynomialPair((HPSPolynomial) sampleIid(Arrays.copyOfRange(bArr, 0, this.params.sampleIidBytes())), sampleFixedType(Arrays.copyOfRange(bArr, this.params.sampleIidBytes(), bArr.length)));
        }
        throw new IllegalArgumentException("Invalid polynomial type");
    }

    public PolynomialPair sampleRm(byte[] bArr) {
        if (this.params instanceof NTRUHRSSParameterSet) {
            return new PolynomialPair((HRSSPolynomial) sampleIid(Arrays.copyOfRange(bArr, 0, this.params.sampleIidBytes())), (HRSSPolynomial) sampleIid(Arrays.copyOfRange(bArr, this.params.sampleIidBytes(), bArr.length)));
        }
        if (this.params instanceof NTRUHPSParameterSet) {
            return new PolynomialPair((HPSPolynomial) sampleIid(Arrays.copyOfRange(bArr, 0, this.params.sampleIidBytes())), sampleFixedType(Arrays.copyOfRange(bArr, this.params.sampleIidBytes(), bArr.length)));
        }
        throw new IllegalArgumentException("Invalid polynomial type");
    }

    public Polynomial sampleIid(byte[] bArr) {
        Polynomial polynomialCreatePolynomial = this.params.createPolynomial();
        for (int i = 0; i < this.params.n() - 1; i++) {
            polynomialCreatePolynomial.coeffs[i] = (short) mod3(bArr[i] & 255);
        }
        polynomialCreatePolynomial.coeffs[this.params.n() - 1] = 0;
        return polynomialCreatePolynomial;
    }

    public HPSPolynomial sampleFixedType(byte[] bArr) {
        int iN = this.params.n();
        int iWeight = ((NTRUHPSParameterSet) this.params).weight();
        HPSPolynomial hPSPolynomial = new HPSPolynomial((NTRUHPSParameterSet) this.params);
        int[] iArr = new int[iN - 1];
        for (int i = 0; i < (iN - 1) / 4; i++) {
            iArr[(4 * i) + 0] = ((bArr[(15 * i) + 0] & 255) << 2) + ((bArr[(15 * i) + 1] & 255) << 10) + ((bArr[(15 * i) + 2] & 255) << 18) + ((bArr[(15 * i) + 3] & 255) << 26);
            iArr[(4 * i) + 1] = (((bArr[15 + (i * 3)] & 255) & 192) >> 4) + ((bArr[(15 * i) + 4] & 255) << 4) + ((bArr[(15 * i) + 5] & 255) << 12) + ((bArr[(15 * i) + 6] & 255) << 20) + ((bArr[(15 * i) + 7] & 255) << 28);
            iArr[(4 * i) + 2] = (((bArr[15 + (i * 7)] & 255) & 240) >> 2) + ((bArr[(15 * i) + 8] & 255) << 6) + ((bArr[(15 * i) + 9] & 255) << 14) + ((bArr[(15 * i) + 10] & 255) << 22) + ((bArr[(15 * i) + 11] & 255) << 30);
            iArr[(4 * i) + 3] = (bArr[(15 * i) + 11] & 255 & DHCPOptions.OPTION_PROXY_AUTODISCOVERY) + ((bArr[(15 * i) + 12] & 255) << 8) + ((bArr[(15 * i) + 13] & 255) << 16) + ((bArr[(15 * i) + 14] & 255) << 24);
        }
        if (iN - 1 > ((iN - 1) / 4) * 4) {
            int i2 = (iN - 1) / 4;
            iArr[(4 * i2) + 0] = ((bArr[(15 * i2) + 0] & 255) << 2) + ((bArr[(15 * i2) + 1] & 255) << 10) + ((bArr[(15 * i2) + 2] & 255) << 18) + ((bArr[(15 * i2) + 3] & 255) << 26);
            iArr[(4 * i2) + 1] = (((bArr[15 + (i2 * 3)] & 255) & 192) >> 4) + ((bArr[(15 * i2) + 4] & 255) << 4) + ((bArr[(15 * i2) + 5] & 255) << 12) + ((bArr[(15 * i2) + 6] & 255) << 20) + ((bArr[(15 * i2) + 7] & 255) << 28);
        }
        for (int i3 = 0; i3 < iWeight / 2; i3++) {
            int i4 = i3;
            iArr[i4] = iArr[i4] | 1;
        }
        for (int i5 = iWeight / 2; i5 < iWeight; i5++) {
            int i6 = i5;
            iArr[i6] = iArr[i6] | 2;
        }
        java.util.Arrays.sort(iArr);
        for (int i7 = 0; i7 < iN - 1; i7++) {
            hPSPolynomial.coeffs[i7] = (short) (iArr[i7] & 3);
        }
        hPSPolynomial.coeffs[iN - 1] = 0;
        return hPSPolynomial;
    }

    public HRSSPolynomial sampleIidPlus(byte[] bArr) {
        int iN = this.params.n();
        short s = 0;
        HRSSPolynomial hRSSPolynomial = (HRSSPolynomial) sampleIid(bArr);
        for (int i = 0; i < iN - 1; i++) {
            hRSSPolynomial.coeffs[i] = (short) (hRSSPolynomial.coeffs[i] | (-(hRSSPolynomial.coeffs[i] >>> 1)));
        }
        for (int i2 = 0; i2 < iN - 1; i2++) {
            s = (short) (s + ((short) (hRSSPolynomial.coeffs[i2 + 1] * hRSSPolynomial.coeffs[i2])));
        }
        short s2 = (short) (1 | (-((s & 65535) >>> 15)));
        for (int i3 = 0; i3 < iN - 1; i3 += 2) {
            hRSSPolynomial.coeffs[i3] = (short) (s2 * hRSSPolynomial.coeffs[i3]);
        }
        for (int i4 = 0; i4 < iN - 1; i4++) {
            hRSSPolynomial.coeffs[i4] = (short) (3 & ((hRSSPolynomial.coeffs[i4] & 65535) ^ ((hRSSPolynomial.coeffs[i4] & 65535) >>> 15)));
        }
        return hRSSPolynomial;
    }

    private static int mod3(int i) {
        return i % 3;
    }
}
