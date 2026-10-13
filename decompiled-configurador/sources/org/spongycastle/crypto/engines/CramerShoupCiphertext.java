package org.spongycastle.crypto.engines;

import java.math.BigInteger;
import org.spongycastle.util.Arrays;
import org.spongycastle.util.Pack;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/engines/CramerShoupCiphertext.class */
public class CramerShoupCiphertext {
    BigInteger u1;
    BigInteger u2;
    BigInteger e;
    BigInteger v;

    public CramerShoupCiphertext() {
    }

    public CramerShoupCiphertext(BigInteger u1, BigInteger u2, BigInteger e, BigInteger v) {
        this.u1 = u1;
        this.u2 = u2;
        this.e = e;
        this.v = v;
    }

    public CramerShoupCiphertext(byte[] c) {
        int s = Pack.bigEndianToInt(c, 0);
        int off = 0 + 4;
        byte[] tmp = Arrays.copyOfRange(c, off, off + s);
        int off2 = off + s;
        this.u1 = new BigInteger(tmp);
        int s2 = Pack.bigEndianToInt(c, off2);
        int off3 = off2 + 4;
        byte[] tmp2 = Arrays.copyOfRange(c, off3, off3 + s2);
        int off4 = off3 + s2;
        this.u2 = new BigInteger(tmp2);
        int s3 = Pack.bigEndianToInt(c, off4);
        int off5 = off4 + 4;
        byte[] tmp3 = Arrays.copyOfRange(c, off5, off5 + s3);
        int off6 = off5 + s3;
        this.e = new BigInteger(tmp3);
        int s4 = Pack.bigEndianToInt(c, off6);
        int off7 = off6 + 4;
        byte[] tmp4 = Arrays.copyOfRange(c, off7, off7 + s4);
        int i = off7 + s4;
        this.v = new BigInteger(tmp4);
    }

    public BigInteger getU1() {
        return this.u1;
    }

    public void setU1(BigInteger u1) {
        this.u1 = u1;
    }

    public BigInteger getU2() {
        return this.u2;
    }

    public void setU2(BigInteger u2) {
        this.u2 = u2;
    }

    public BigInteger getE() {
        return this.e;
    }

    public void setE(BigInteger e) {
        this.e = e;
    }

    public BigInteger getV() {
        return this.v;
    }

    public void setV(BigInteger v) {
        this.v = v;
    }

    public String toString() {
        StringBuffer result = new StringBuffer();
        result.append("u1: " + this.u1.toString());
        result.append("\nu2: " + this.u2.toString());
        result.append("\ne: " + this.e.toString());
        result.append("\nv: " + this.v.toString());
        return result.toString();
    }

    public byte[] toByteArray() {
        byte[] u1Bytes = this.u1.toByteArray();
        int u1Length = u1Bytes.length;
        byte[] u2Bytes = this.u2.toByteArray();
        int u2Length = u2Bytes.length;
        byte[] eBytes = this.e.toByteArray();
        int eLength = eBytes.length;
        byte[] vBytes = this.v.toByteArray();
        int vLength = vBytes.length;
        byte[] result = new byte[u1Length + u2Length + eLength + vLength + 16];
        Pack.intToBigEndian(u1Length, result, 0);
        int off = 0 + 4;
        System.arraycopy(u1Bytes, 0, result, off, u1Length);
        int off2 = off + u1Length;
        Pack.intToBigEndian(u2Length, result, off2);
        int off3 = off2 + 4;
        System.arraycopy(u2Bytes, 0, result, off3, u2Length);
        int off4 = off3 + u2Length;
        Pack.intToBigEndian(eLength, result, off4);
        int off5 = off4 + 4;
        System.arraycopy(eBytes, 0, result, off5, eLength);
        int off6 = off5 + eLength;
        Pack.intToBigEndian(vLength, result, off6);
        int off7 = off6 + 4;
        System.arraycopy(vBytes, 0, result, off7, vLength);
        int i = off7 + vLength;
        return result;
    }
}
