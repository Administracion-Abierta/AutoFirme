package org.bouncycastle.crypto.digests;

import com.sun.jna.platform.unix.Reboot;
import org.bouncycastle.crypto.CryptoServiceProperties;
import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.util.Memoable;
import org.bouncycastle.util.Pack;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/digests/RIPEMD320Digest.class */
public class RIPEMD320Digest extends GeneralDigest {
    private static final int DIGEST_LENGTH = 40;
    private int H0;
    private int H1;
    private int H2;
    private int H3;
    private int H4;
    private int H5;
    private int H6;
    private int H7;
    private int H8;
    private int H9;
    private int[] X;
    private int xOff;

    public RIPEMD320Digest() {
        this(CryptoServicePurpose.ANY);
    }

    public RIPEMD320Digest(CryptoServicePurpose cryptoServicePurpose) {
        super(cryptoServicePurpose);
        this.X = new int[16];
        CryptoServicesRegistrar.checkConstraints(Utils.getDefaultProperties(this, 128, cryptoServicePurpose));
        reset();
    }

    public RIPEMD320Digest(RIPEMD320Digest rIPEMD320Digest) {
        super(rIPEMD320Digest.purpose);
        this.X = new int[16];
        CryptoServicesRegistrar.checkConstraints(Utils.getDefaultProperties(this, 128, this.purpose));
        doCopy(rIPEMD320Digest);
    }

    private void doCopy(RIPEMD320Digest rIPEMD320Digest) {
        super.copyIn(rIPEMD320Digest);
        this.H0 = rIPEMD320Digest.H0;
        this.H1 = rIPEMD320Digest.H1;
        this.H2 = rIPEMD320Digest.H2;
        this.H3 = rIPEMD320Digest.H3;
        this.H4 = rIPEMD320Digest.H4;
        this.H5 = rIPEMD320Digest.H5;
        this.H6 = rIPEMD320Digest.H6;
        this.H7 = rIPEMD320Digest.H7;
        this.H8 = rIPEMD320Digest.H8;
        this.H9 = rIPEMD320Digest.H9;
        System.arraycopy(rIPEMD320Digest.X, 0, this.X, 0, rIPEMD320Digest.X.length);
        this.xOff = rIPEMD320Digest.xOff;
    }

    @Override // org.bouncycastle.crypto.Digest
    public String getAlgorithmName() {
        return "RIPEMD320";
    }

    @Override // org.bouncycastle.crypto.Digest
    public int getDigestSize() {
        return 40;
    }

    @Override // org.bouncycastle.crypto.digests.GeneralDigest
    protected void processWord(byte[] bArr, int i) {
        int[] iArr = this.X;
        int i2 = this.xOff;
        this.xOff = i2 + 1;
        iArr[i2] = Pack.littleEndianToInt(bArr, i);
        if (this.xOff == 16) {
            processBlock();
        }
    }

    @Override // org.bouncycastle.crypto.digests.GeneralDigest
    protected void processLength(long j) {
        if (this.xOff > 14) {
            processBlock();
        }
        this.X[14] = (int) (j & (-1));
        this.X[15] = (int) (j >>> 32);
    }

    @Override // org.bouncycastle.crypto.Digest
    public int doFinal(byte[] bArr, int i) {
        finish();
        Pack.intToLittleEndian(this.H0, bArr, i);
        Pack.intToLittleEndian(this.H1, bArr, i + 4);
        Pack.intToLittleEndian(this.H2, bArr, i + 8);
        Pack.intToLittleEndian(this.H3, bArr, i + 12);
        Pack.intToLittleEndian(this.H4, bArr, i + 16);
        Pack.intToLittleEndian(this.H5, bArr, i + 20);
        Pack.intToLittleEndian(this.H6, bArr, i + 24);
        Pack.intToLittleEndian(this.H7, bArr, i + 28);
        Pack.intToLittleEndian(this.H8, bArr, i + 32);
        Pack.intToLittleEndian(this.H9, bArr, i + 36);
        reset();
        return 40;
    }

    @Override // org.bouncycastle.crypto.digests.GeneralDigest, org.bouncycastle.crypto.Digest
    public void reset() {
        super.reset();
        this.H0 = 1732584193;
        this.H1 = -271733879;
        this.H2 = -1732584194;
        this.H3 = 271733878;
        this.H4 = -1009589776;
        this.H5 = 1985229328;
        this.H6 = -19088744;
        this.H7 = Reboot.RB_ENABLE_CAD;
        this.H8 = Reboot.RB_AUTOBOOT;
        this.H9 = 1009589775;
        this.xOff = 0;
        for (int i = 0; i != this.X.length; i++) {
            this.X[i] = 0;
        }
    }

    private int RL(int i, int i2) {
        return (i << i2) | (i >>> (32 - i2));
    }

    private int f1(int i, int i2, int i3) {
        return (i ^ i2) ^ i3;
    }

    private int f2(int i, int i2, int i3) {
        return (i & i2) | ((i ^ (-1)) & i3);
    }

    private int f3(int i, int i2, int i3) {
        return (i | (i2 ^ (-1))) ^ i3;
    }

    private int f4(int i, int i2, int i3) {
        return (i & i3) | (i2 & (i3 ^ (-1)));
    }

    private int f5(int i, int i2, int i3) {
        return i ^ (i2 | (i3 ^ (-1)));
    }

    @Override // org.bouncycastle.crypto.digests.GeneralDigest
    protected void processBlock() {
        int i = this.H0;
        int i2 = this.H1;
        int i3 = this.H2;
        int i4 = this.H3;
        int i5 = this.H4;
        int i6 = this.H5;
        int i7 = this.H6;
        int i8 = this.H7;
        int i9 = this.H8;
        int i10 = this.H9;
        int iRL = RL(i + f1(i2, i3, i4) + this.X[0], 11) + i5;
        int iRL2 = RL(i3, 10);
        int iRL3 = RL(i5 + f1(iRL, i2, iRL2) + this.X[1], 14) + i4;
        int iRL4 = RL(i2, 10);
        int iRL5 = RL(i4 + f1(iRL3, iRL, iRL4) + this.X[2], 15) + iRL2;
        int iRL6 = RL(iRL, 10);
        int iRL7 = RL(iRL2 + f1(iRL5, iRL3, iRL6) + this.X[3], 12) + iRL4;
        int iRL8 = RL(iRL3, 10);
        int iRL9 = RL(iRL4 + f1(iRL7, iRL5, iRL8) + this.X[4], 5) + iRL6;
        int iRL10 = RL(iRL5, 10);
        int iRL11 = RL(iRL6 + f1(iRL9, iRL7, iRL10) + this.X[5], 8) + iRL8;
        int iRL12 = RL(iRL7, 10);
        int iRL13 = RL(iRL8 + f1(iRL11, iRL9, iRL12) + this.X[6], 7) + iRL10;
        int iRL14 = RL(iRL9, 10);
        int iRL15 = RL(iRL10 + f1(iRL13, iRL11, iRL14) + this.X[7], 9) + iRL12;
        int iRL16 = RL(iRL11, 10);
        int iRL17 = RL(iRL12 + f1(iRL15, iRL13, iRL16) + this.X[8], 11) + iRL14;
        int iRL18 = RL(iRL13, 10);
        int iRL19 = RL(iRL14 + f1(iRL17, iRL15, iRL18) + this.X[9], 13) + iRL16;
        int iRL20 = RL(iRL15, 10);
        int iRL21 = RL(iRL16 + f1(iRL19, iRL17, iRL20) + this.X[10], 14) + iRL18;
        int iRL22 = RL(iRL17, 10);
        int iRL23 = RL(iRL18 + f1(iRL21, iRL19, iRL22) + this.X[11], 15) + iRL20;
        int iRL24 = RL(iRL19, 10);
        int iRL25 = RL(iRL20 + f1(iRL23, iRL21, iRL24) + this.X[12], 6) + iRL22;
        int iRL26 = RL(iRL21, 10);
        int iRL27 = RL(iRL22 + f1(iRL25, iRL23, iRL26) + this.X[13], 7) + iRL24;
        int iRL28 = RL(iRL23, 10);
        int iRL29 = RL(iRL24 + f1(iRL27, iRL25, iRL28) + this.X[14], 9) + iRL26;
        int iRL30 = RL(iRL25, 10);
        int iRL31 = RL(iRL26 + f1(iRL29, iRL27, iRL30) + this.X[15], 8) + iRL28;
        int iRL32 = RL(iRL27, 10);
        int iRL33 = RL(i6 + f5(i7, i8, i9) + this.X[5] + 1352829926, 8) + i10;
        int iRL34 = RL(i8, 10);
        int iRL35 = RL(i10 + f5(iRL33, i7, iRL34) + this.X[14] + 1352829926, 9) + i9;
        int iRL36 = RL(i7, 10);
        int iRL37 = RL(i9 + f5(iRL35, iRL33, iRL36) + this.X[7] + 1352829926, 9) + iRL34;
        int iRL38 = RL(iRL33, 10);
        int iRL39 = RL(iRL34 + f5(iRL37, iRL35, iRL38) + this.X[0] + 1352829926, 11) + iRL36;
        int iRL40 = RL(iRL35, 10);
        int iRL41 = RL(iRL36 + f5(iRL39, iRL37, iRL40) + this.X[9] + 1352829926, 13) + iRL38;
        int iRL42 = RL(iRL37, 10);
        int iRL43 = RL(iRL38 + f5(iRL41, iRL39, iRL42) + this.X[2] + 1352829926, 15) + iRL40;
        int iRL44 = RL(iRL39, 10);
        int iRL45 = RL(iRL40 + f5(iRL43, iRL41, iRL44) + this.X[11] + 1352829926, 15) + iRL42;
        int iRL46 = RL(iRL41, 10);
        int iRL47 = RL(iRL42 + f5(iRL45, iRL43, iRL46) + this.X[4] + 1352829926, 5) + iRL44;
        int iRL48 = RL(iRL43, 10);
        int iRL49 = RL(iRL44 + f5(iRL47, iRL45, iRL48) + this.X[13] + 1352829926, 7) + iRL46;
        int iRL50 = RL(iRL45, 10);
        int iRL51 = RL(iRL46 + f5(iRL49, iRL47, iRL50) + this.X[6] + 1352829926, 7) + iRL48;
        int iRL52 = RL(iRL47, 10);
        int iRL53 = RL(iRL48 + f5(iRL51, iRL49, iRL52) + this.X[15] + 1352829926, 8) + iRL50;
        int iRL54 = RL(iRL49, 10);
        int iRL55 = RL(iRL50 + f5(iRL53, iRL51, iRL54) + this.X[8] + 1352829926, 11) + iRL52;
        int iRL56 = RL(iRL51, 10);
        int iRL57 = RL(iRL52 + f5(iRL55, iRL53, iRL56) + this.X[1] + 1352829926, 14) + iRL54;
        int iRL58 = RL(iRL53, 10);
        int iRL59 = RL(iRL54 + f5(iRL57, iRL55, iRL58) + this.X[10] + 1352829926, 14) + iRL56;
        int iRL60 = RL(iRL55, 10);
        int iRL61 = RL(iRL56 + f5(iRL59, iRL57, iRL60) + this.X[3] + 1352829926, 12) + iRL58;
        int iRL62 = RL(iRL57, 10);
        int iRL63 = RL(iRL58 + f5(iRL61, iRL59, iRL62) + this.X[12] + 1352829926, 6) + iRL60;
        int iRL64 = RL(iRL59, 10);
        int iRL65 = RL(iRL28 + f2(iRL63, iRL29, iRL32) + this.X[7] + 1518500249, 7) + iRL30;
        int iRL66 = RL(iRL29, 10);
        int iRL67 = RL(iRL30 + f2(iRL65, iRL63, iRL66) + this.X[4] + 1518500249, 6) + iRL32;
        int iRL68 = RL(iRL63, 10);
        int iRL69 = RL(iRL32 + f2(iRL67, iRL65, iRL68) + this.X[13] + 1518500249, 8) + iRL66;
        int iRL70 = RL(iRL65, 10);
        int iRL71 = RL(iRL66 + f2(iRL69, iRL67, iRL70) + this.X[1] + 1518500249, 13) + iRL68;
        int iRL72 = RL(iRL67, 10);
        int iRL73 = RL(iRL68 + f2(iRL71, iRL69, iRL72) + this.X[10] + 1518500249, 11) + iRL70;
        int iRL74 = RL(iRL69, 10);
        int iRL75 = RL(iRL70 + f2(iRL73, iRL71, iRL74) + this.X[6] + 1518500249, 9) + iRL72;
        int iRL76 = RL(iRL71, 10);
        int iRL77 = RL(iRL72 + f2(iRL75, iRL73, iRL76) + this.X[15] + 1518500249, 7) + iRL74;
        int iRL78 = RL(iRL73, 10);
        int iRL79 = RL(iRL74 + f2(iRL77, iRL75, iRL78) + this.X[3] + 1518500249, 15) + iRL76;
        int iRL80 = RL(iRL75, 10);
        int iRL81 = RL(iRL76 + f2(iRL79, iRL77, iRL80) + this.X[12] + 1518500249, 7) + iRL78;
        int iRL82 = RL(iRL77, 10);
        int iRL83 = RL(iRL78 + f2(iRL81, iRL79, iRL82) + this.X[0] + 1518500249, 12) + iRL80;
        int iRL84 = RL(iRL79, 10);
        int iRL85 = RL(iRL80 + f2(iRL83, iRL81, iRL84) + this.X[9] + 1518500249, 15) + iRL82;
        int iRL86 = RL(iRL81, 10);
        int iRL87 = RL(iRL82 + f2(iRL85, iRL83, iRL86) + this.X[5] + 1518500249, 9) + iRL84;
        int iRL88 = RL(iRL83, 10);
        int iRL89 = RL(iRL84 + f2(iRL87, iRL85, iRL88) + this.X[2] + 1518500249, 11) + iRL86;
        int iRL90 = RL(iRL85, 10);
        int iRL91 = RL(iRL86 + f2(iRL89, iRL87, iRL90) + this.X[14] + 1518500249, 7) + iRL88;
        int iRL92 = RL(iRL87, 10);
        int iRL93 = RL(iRL88 + f2(iRL91, iRL89, iRL92) + this.X[11] + 1518500249, 13) + iRL90;
        int iRL94 = RL(iRL89, 10);
        int iRL95 = RL(iRL90 + f2(iRL93, iRL91, iRL94) + this.X[8] + 1518500249, 12) + iRL92;
        int iRL96 = RL(iRL91, 10);
        int iRL97 = RL(iRL60 + f4(iRL31, iRL61, iRL64) + this.X[6] + 1548603684, 9) + iRL62;
        int iRL98 = RL(iRL61, 10);
        int iRL99 = RL(iRL62 + f4(iRL97, iRL31, iRL98) + this.X[11] + 1548603684, 13) + iRL64;
        int iRL100 = RL(iRL31, 10);
        int iRL101 = RL(iRL64 + f4(iRL99, iRL97, iRL100) + this.X[3] + 1548603684, 15) + iRL98;
        int iRL102 = RL(iRL97, 10);
        int iRL103 = RL(iRL98 + f4(iRL101, iRL99, iRL102) + this.X[7] + 1548603684, 7) + iRL100;
        int iRL104 = RL(iRL99, 10);
        int iRL105 = RL(iRL100 + f4(iRL103, iRL101, iRL104) + this.X[0] + 1548603684, 12) + iRL102;
        int iRL106 = RL(iRL101, 10);
        int iRL107 = RL(iRL102 + f4(iRL105, iRL103, iRL106) + this.X[13] + 1548603684, 8) + iRL104;
        int iRL108 = RL(iRL103, 10);
        int iRL109 = RL(iRL104 + f4(iRL107, iRL105, iRL108) + this.X[5] + 1548603684, 9) + iRL106;
        int iRL110 = RL(iRL105, 10);
        int iRL111 = RL(iRL106 + f4(iRL109, iRL107, iRL110) + this.X[10] + 1548603684, 11) + iRL108;
        int iRL112 = RL(iRL107, 10);
        int iRL113 = RL(iRL108 + f4(iRL111, iRL109, iRL112) + this.X[14] + 1548603684, 7) + iRL110;
        int iRL114 = RL(iRL109, 10);
        int iRL115 = RL(iRL110 + f4(iRL113, iRL111, iRL114) + this.X[15] + 1548603684, 7) + iRL112;
        int iRL116 = RL(iRL111, 10);
        int iRL117 = RL(iRL112 + f4(iRL115, iRL113, iRL116) + this.X[8] + 1548603684, 12) + iRL114;
        int iRL118 = RL(iRL113, 10);
        int iRL119 = RL(iRL114 + f4(iRL117, iRL115, iRL118) + this.X[12] + 1548603684, 7) + iRL116;
        int iRL120 = RL(iRL115, 10);
        int iRL121 = RL(iRL116 + f4(iRL119, iRL117, iRL120) + this.X[4] + 1548603684, 6) + iRL118;
        int iRL122 = RL(iRL117, 10);
        int iRL123 = RL(iRL118 + f4(iRL121, iRL119, iRL122) + this.X[9] + 1548603684, 15) + iRL120;
        int iRL124 = RL(iRL119, 10);
        int iRL125 = RL(iRL120 + f4(iRL123, iRL121, iRL124) + this.X[1] + 1548603684, 13) + iRL122;
        int iRL126 = RL(iRL121, 10);
        int iRL127 = RL(iRL122 + f4(iRL125, iRL123, iRL126) + this.X[2] + 1548603684, 11) + iRL124;
        int iRL128 = RL(iRL123, 10);
        int iRL129 = RL(iRL92 + f3(iRL95, iRL93, iRL128) + this.X[3] + 1859775393, 11) + iRL94;
        int iRL130 = RL(iRL93, 10);
        int iRL131 = RL(iRL94 + f3(iRL129, iRL95, iRL130) + this.X[10] + 1859775393, 13) + iRL128;
        int iRL132 = RL(iRL95, 10);
        int iRL133 = RL(iRL128 + f3(iRL131, iRL129, iRL132) + this.X[14] + 1859775393, 6) + iRL130;
        int iRL134 = RL(iRL129, 10);
        int iRL135 = RL(iRL130 + f3(iRL133, iRL131, iRL134) + this.X[4] + 1859775393, 7) + iRL132;
        int iRL136 = RL(iRL131, 10);
        int iRL137 = RL(iRL132 + f3(iRL135, iRL133, iRL136) + this.X[9] + 1859775393, 14) + iRL134;
        int iRL138 = RL(iRL133, 10);
        int iRL139 = RL(iRL134 + f3(iRL137, iRL135, iRL138) + this.X[15] + 1859775393, 9) + iRL136;
        int iRL140 = RL(iRL135, 10);
        int iRL141 = RL(iRL136 + f3(iRL139, iRL137, iRL140) + this.X[8] + 1859775393, 13) + iRL138;
        int iRL142 = RL(iRL137, 10);
        int iRL143 = RL(iRL138 + f3(iRL141, iRL139, iRL142) + this.X[1] + 1859775393, 15) + iRL140;
        int iRL144 = RL(iRL139, 10);
        int iRL145 = RL(iRL140 + f3(iRL143, iRL141, iRL144) + this.X[2] + 1859775393, 14) + iRL142;
        int iRL146 = RL(iRL141, 10);
        int iRL147 = RL(iRL142 + f3(iRL145, iRL143, iRL146) + this.X[7] + 1859775393, 8) + iRL144;
        int iRL148 = RL(iRL143, 10);
        int iRL149 = RL(iRL144 + f3(iRL147, iRL145, iRL148) + this.X[0] + 1859775393, 13) + iRL146;
        int iRL150 = RL(iRL145, 10);
        int iRL151 = RL(iRL146 + f3(iRL149, iRL147, iRL150) + this.X[6] + 1859775393, 6) + iRL148;
        int iRL152 = RL(iRL147, 10);
        int iRL153 = RL(iRL148 + f3(iRL151, iRL149, iRL152) + this.X[13] + 1859775393, 5) + iRL150;
        int iRL154 = RL(iRL149, 10);
        int iRL155 = RL(iRL150 + f3(iRL153, iRL151, iRL154) + this.X[11] + 1859775393, 12) + iRL152;
        int iRL156 = RL(iRL151, 10);
        int iRL157 = RL(iRL152 + f3(iRL155, iRL153, iRL156) + this.X[5] + 1859775393, 7) + iRL154;
        int iRL158 = RL(iRL153, 10);
        int iRL159 = RL(iRL154 + f3(iRL157, iRL155, iRL158) + this.X[12] + 1859775393, 5) + iRL156;
        int iRL160 = RL(iRL155, 10);
        int iRL161 = RL(iRL124 + f3(iRL127, iRL125, iRL96) + this.X[15] + 1836072691, 9) + iRL126;
        int iRL162 = RL(iRL125, 10);
        int iRL163 = RL(iRL126 + f3(iRL161, iRL127, iRL162) + this.X[5] + 1836072691, 7) + iRL96;
        int iRL164 = RL(iRL127, 10);
        int iRL165 = RL(iRL96 + f3(iRL163, iRL161, iRL164) + this.X[1] + 1836072691, 15) + iRL162;
        int iRL166 = RL(iRL161, 10);
        int iRL167 = RL(iRL162 + f3(iRL165, iRL163, iRL166) + this.X[3] + 1836072691, 11) + iRL164;
        int iRL168 = RL(iRL163, 10);
        int iRL169 = RL(iRL164 + f3(iRL167, iRL165, iRL168) + this.X[7] + 1836072691, 8) + iRL166;
        int iRL170 = RL(iRL165, 10);
        int iRL171 = RL(iRL166 + f3(iRL169, iRL167, iRL170) + this.X[14] + 1836072691, 6) + iRL168;
        int iRL172 = RL(iRL167, 10);
        int iRL173 = RL(iRL168 + f3(iRL171, iRL169, iRL172) + this.X[6] + 1836072691, 6) + iRL170;
        int iRL174 = RL(iRL169, 10);
        int iRL175 = RL(iRL170 + f3(iRL173, iRL171, iRL174) + this.X[9] + 1836072691, 14) + iRL172;
        int iRL176 = RL(iRL171, 10);
        int iRL177 = RL(iRL172 + f3(iRL175, iRL173, iRL176) + this.X[11] + 1836072691, 12) + iRL174;
        int iRL178 = RL(iRL173, 10);
        int iRL179 = RL(iRL174 + f3(iRL177, iRL175, iRL178) + this.X[8] + 1836072691, 13) + iRL176;
        int iRL180 = RL(iRL175, 10);
        int iRL181 = RL(iRL176 + f3(iRL179, iRL177, iRL180) + this.X[12] + 1836072691, 5) + iRL178;
        int iRL182 = RL(iRL177, 10);
        int iRL183 = RL(iRL178 + f3(iRL181, iRL179, iRL182) + this.X[2] + 1836072691, 14) + iRL180;
        int iRL184 = RL(iRL179, 10);
        int iRL185 = RL(iRL180 + f3(iRL183, iRL181, iRL184) + this.X[10] + 1836072691, 13) + iRL182;
        int iRL186 = RL(iRL181, 10);
        int iRL187 = RL(iRL182 + f3(iRL185, iRL183, iRL186) + this.X[0] + 1836072691, 13) + iRL184;
        int iRL188 = RL(iRL183, 10);
        int iRL189 = RL(iRL184 + f3(iRL187, iRL185, iRL188) + this.X[4] + 1836072691, 7) + iRL186;
        int iRL190 = RL(iRL185, 10);
        int iRL191 = RL(iRL186 + f3(iRL189, iRL187, iRL190) + this.X[13] + 1836072691, 5) + iRL188;
        int iRL192 = RL(iRL187, 10);
        int iRL193 = RL(((iRL188 + f4(iRL159, iRL157, iRL160)) + this.X[1]) - 1894007588, 11) + iRL158;
        int iRL194 = RL(iRL157, 10);
        int iRL195 = RL(((iRL158 + f4(iRL193, iRL159, iRL194)) + this.X[9]) - 1894007588, 12) + iRL160;
        int iRL196 = RL(iRL159, 10);
        int iRL197 = RL(((iRL160 + f4(iRL195, iRL193, iRL196)) + this.X[11]) - 1894007588, 14) + iRL194;
        int iRL198 = RL(iRL193, 10);
        int iRL199 = RL(((iRL194 + f4(iRL197, iRL195, iRL198)) + this.X[10]) - 1894007588, 15) + iRL196;
        int iRL200 = RL(iRL195, 10);
        int iRL201 = RL(((iRL196 + f4(iRL199, iRL197, iRL200)) + this.X[0]) - 1894007588, 14) + iRL198;
        int iRL202 = RL(iRL197, 10);
        int iRL203 = RL(((iRL198 + f4(iRL201, iRL199, iRL202)) + this.X[8]) - 1894007588, 15) + iRL200;
        int iRL204 = RL(iRL199, 10);
        int iRL205 = RL(((iRL200 + f4(iRL203, iRL201, iRL204)) + this.X[12]) - 1894007588, 9) + iRL202;
        int iRL206 = RL(iRL201, 10);
        int iRL207 = RL(((iRL202 + f4(iRL205, iRL203, iRL206)) + this.X[4]) - 1894007588, 8) + iRL204;
        int iRL208 = RL(iRL203, 10);
        int iRL209 = RL(((iRL204 + f4(iRL207, iRL205, iRL208)) + this.X[13]) - 1894007588, 9) + iRL206;
        int iRL210 = RL(iRL205, 10);
        int iRL211 = RL(((iRL206 + f4(iRL209, iRL207, iRL210)) + this.X[3]) - 1894007588, 14) + iRL208;
        int iRL212 = RL(iRL207, 10);
        int iRL213 = RL(((iRL208 + f4(iRL211, iRL209, iRL212)) + this.X[7]) - 1894007588, 5) + iRL210;
        int iRL214 = RL(iRL209, 10);
        int iRL215 = RL(((iRL210 + f4(iRL213, iRL211, iRL214)) + this.X[15]) - 1894007588, 6) + iRL212;
        int iRL216 = RL(iRL211, 10);
        int iRL217 = RL(((iRL212 + f4(iRL215, iRL213, iRL216)) + this.X[14]) - 1894007588, 8) + iRL214;
        int iRL218 = RL(iRL213, 10);
        int iRL219 = RL(((iRL214 + f4(iRL217, iRL215, iRL218)) + this.X[5]) - 1894007588, 6) + iRL216;
        int iRL220 = RL(iRL215, 10);
        int iRL221 = RL(((iRL216 + f4(iRL219, iRL217, iRL220)) + this.X[6]) - 1894007588, 5) + iRL218;
        int iRL222 = RL(iRL217, 10);
        int iRL223 = RL(((iRL218 + f4(iRL221, iRL219, iRL222)) + this.X[2]) - 1894007588, 12) + iRL220;
        int iRL224 = RL(iRL219, 10);
        int iRL225 = RL(iRL156 + f2(iRL191, iRL189, iRL192) + this.X[8] + 2053994217, 15) + iRL190;
        int iRL226 = RL(iRL189, 10);
        int iRL227 = RL(iRL190 + f2(iRL225, iRL191, iRL226) + this.X[6] + 2053994217, 5) + iRL192;
        int iRL228 = RL(iRL191, 10);
        int iRL229 = RL(iRL192 + f2(iRL227, iRL225, iRL228) + this.X[4] + 2053994217, 8) + iRL226;
        int iRL230 = RL(iRL225, 10);
        int iRL231 = RL(iRL226 + f2(iRL229, iRL227, iRL230) + this.X[1] + 2053994217, 11) + iRL228;
        int iRL232 = RL(iRL227, 10);
        int iRL233 = RL(iRL228 + f2(iRL231, iRL229, iRL232) + this.X[3] + 2053994217, 14) + iRL230;
        int iRL234 = RL(iRL229, 10);
        int iRL235 = RL(iRL230 + f2(iRL233, iRL231, iRL234) + this.X[11] + 2053994217, 14) + iRL232;
        int iRL236 = RL(iRL231, 10);
        int iRL237 = RL(iRL232 + f2(iRL235, iRL233, iRL236) + this.X[15] + 2053994217, 6) + iRL234;
        int iRL238 = RL(iRL233, 10);
        int iRL239 = RL(iRL234 + f2(iRL237, iRL235, iRL238) + this.X[0] + 2053994217, 14) + iRL236;
        int iRL240 = RL(iRL235, 10);
        int iRL241 = RL(iRL236 + f2(iRL239, iRL237, iRL240) + this.X[5] + 2053994217, 6) + iRL238;
        int iRL242 = RL(iRL237, 10);
        int iRL243 = RL(iRL238 + f2(iRL241, iRL239, iRL242) + this.X[12] + 2053994217, 9) + iRL240;
        int iRL244 = RL(iRL239, 10);
        int iRL245 = RL(iRL240 + f2(iRL243, iRL241, iRL244) + this.X[2] + 2053994217, 12) + iRL242;
        int iRL246 = RL(iRL241, 10);
        int iRL247 = RL(iRL242 + f2(iRL245, iRL243, iRL246) + this.X[13] + 2053994217, 9) + iRL244;
        int iRL248 = RL(iRL243, 10);
        int iRL249 = RL(iRL244 + f2(iRL247, iRL245, iRL248) + this.X[9] + 2053994217, 12) + iRL246;
        int iRL250 = RL(iRL245, 10);
        int iRL251 = RL(iRL246 + f2(iRL249, iRL247, iRL250) + this.X[7] + 2053994217, 5) + iRL248;
        int iRL252 = RL(iRL247, 10);
        int iRL253 = RL(iRL248 + f2(iRL251, iRL249, iRL252) + this.X[10] + 2053994217, 15) + iRL250;
        int iRL254 = RL(iRL249, 10);
        int iRL255 = RL(iRL250 + f2(iRL253, iRL251, iRL254) + this.X[14] + 2053994217, 8) + iRL252;
        int iRL256 = RL(iRL251, 10);
        int iRL257 = RL(((iRL220 + f5(iRL223, iRL253, iRL224)) + this.X[4]) - 1454113458, 9) + iRL222;
        int iRL258 = RL(iRL253, 10);
        int iRL259 = RL(((iRL222 + f5(iRL257, iRL223, iRL258)) + this.X[0]) - 1454113458, 15) + iRL224;
        int iRL260 = RL(iRL223, 10);
        int iRL261 = RL(((iRL224 + f5(iRL259, iRL257, iRL260)) + this.X[5]) - 1454113458, 5) + iRL258;
        int iRL262 = RL(iRL257, 10);
        int iRL263 = RL(((iRL258 + f5(iRL261, iRL259, iRL262)) + this.X[9]) - 1454113458, 11) + iRL260;
        int iRL264 = RL(iRL259, 10);
        int iRL265 = RL(((iRL260 + f5(iRL263, iRL261, iRL264)) + this.X[7]) - 1454113458, 6) + iRL262;
        int iRL266 = RL(iRL261, 10);
        int iRL267 = RL(((iRL262 + f5(iRL265, iRL263, iRL266)) + this.X[12]) - 1454113458, 8) + iRL264;
        int iRL268 = RL(iRL263, 10);
        int iRL269 = RL(((iRL264 + f5(iRL267, iRL265, iRL268)) + this.X[2]) - 1454113458, 13) + iRL266;
        int iRL270 = RL(iRL265, 10);
        int iRL271 = RL(((iRL266 + f5(iRL269, iRL267, iRL270)) + this.X[10]) - 1454113458, 12) + iRL268;
        int iRL272 = RL(iRL267, 10);
        int iRL273 = RL(((iRL268 + f5(iRL271, iRL269, iRL272)) + this.X[14]) - 1454113458, 5) + iRL270;
        int iRL274 = RL(iRL269, 10);
        int iRL275 = RL(((iRL270 + f5(iRL273, iRL271, iRL274)) + this.X[1]) - 1454113458, 12) + iRL272;
        int iRL276 = RL(iRL271, 10);
        int iRL277 = RL(((iRL272 + f5(iRL275, iRL273, iRL276)) + this.X[3]) - 1454113458, 13) + iRL274;
        int iRL278 = RL(iRL273, 10);
        int iRL279 = RL(((iRL274 + f5(iRL277, iRL275, iRL278)) + this.X[8]) - 1454113458, 14) + iRL276;
        int iRL280 = RL(iRL275, 10);
        int iRL281 = RL(((iRL276 + f5(iRL279, iRL277, iRL280)) + this.X[11]) - 1454113458, 11) + iRL278;
        int iRL282 = RL(iRL277, 10);
        int iRL283 = RL(((iRL278 + f5(iRL281, iRL279, iRL282)) + this.X[6]) - 1454113458, 8) + iRL280;
        int iRL284 = RL(iRL279, 10);
        int iRL285 = RL(((iRL280 + f5(iRL283, iRL281, iRL284)) + this.X[15]) - 1454113458, 5) + iRL282;
        int iRL286 = RL(iRL281, 10);
        int iRL287 = RL(((iRL282 + f5(iRL285, iRL283, iRL286)) + this.X[13]) - 1454113458, 6) + iRL284;
        int iRL288 = RL(iRL283, 10);
        int iRL289 = RL(iRL252 + f1(iRL255, iRL221, iRL256) + this.X[12], 8) + iRL254;
        int iRL290 = RL(iRL221, 10);
        int iRL291 = RL(iRL254 + f1(iRL289, iRL255, iRL290) + this.X[15], 5) + iRL256;
        int iRL292 = RL(iRL255, 10);
        int iRL293 = RL(iRL256 + f1(iRL291, iRL289, iRL292) + this.X[10], 12) + iRL290;
        int iRL294 = RL(iRL289, 10);
        int iRL295 = RL(iRL290 + f1(iRL293, iRL291, iRL294) + this.X[4], 9) + iRL292;
        int iRL296 = RL(iRL291, 10);
        int iRL297 = RL(iRL292 + f1(iRL295, iRL293, iRL296) + this.X[1], 12) + iRL294;
        int iRL298 = RL(iRL293, 10);
        int iRL299 = RL(iRL294 + f1(iRL297, iRL295, iRL298) + this.X[5], 5) + iRL296;
        int iRL300 = RL(iRL295, 10);
        int iRL301 = RL(iRL296 + f1(iRL299, iRL297, iRL300) + this.X[8], 14) + iRL298;
        int iRL302 = RL(iRL297, 10);
        int iRL303 = RL(iRL298 + f1(iRL301, iRL299, iRL302) + this.X[7], 6) + iRL300;
        int iRL304 = RL(iRL299, 10);
        int iRL305 = RL(iRL300 + f1(iRL303, iRL301, iRL304) + this.X[6], 8) + iRL302;
        int iRL306 = RL(iRL301, 10);
        int iRL307 = RL(iRL302 + f1(iRL305, iRL303, iRL306) + this.X[2], 13) + iRL304;
        int iRL308 = RL(iRL303, 10);
        int iRL309 = RL(iRL304 + f1(iRL307, iRL305, iRL308) + this.X[13], 6) + iRL306;
        int iRL310 = RL(iRL305, 10);
        int iRL311 = RL(iRL306 + f1(iRL309, iRL307, iRL310) + this.X[14], 5) + iRL308;
        int iRL312 = RL(iRL307, 10);
        int iRL313 = RL(iRL308 + f1(iRL311, iRL309, iRL312) + this.X[0], 15) + iRL310;
        int iRL314 = RL(iRL309, 10);
        int iRL315 = RL(iRL310 + f1(iRL313, iRL311, iRL314) + this.X[3], 13) + iRL312;
        int iRL316 = RL(iRL311, 10);
        int iRL317 = RL(iRL312 + f1(iRL315, iRL313, iRL316) + this.X[9], 11) + iRL314;
        int iRL318 = RL(iRL313, 10);
        int iRL319 = RL(iRL314 + f1(iRL317, iRL315, iRL318) + this.X[11], 11) + iRL316;
        int iRL320 = RL(iRL315, 10);
        this.H0 += iRL284;
        this.H1 += iRL287;
        this.H2 += iRL285;
        this.H3 += iRL288;
        this.H4 += iRL318;
        this.H5 += iRL316;
        this.H6 += iRL319;
        this.H7 += iRL317;
        this.H8 += iRL320;
        this.H9 += iRL286;
        this.xOff = 0;
        for (int i11 = 0; i11 != this.X.length; i11++) {
            this.X[i11] = 0;
        }
    }

    @Override // org.bouncycastle.util.Memoable
    public Memoable copy() {
        return new RIPEMD320Digest(this);
    }

    @Override // org.bouncycastle.util.Memoable
    public void reset(Memoable memoable) {
        doCopy((RIPEMD320Digest) memoable);
    }

    @Override // org.bouncycastle.crypto.digests.GeneralDigest
    protected CryptoServiceProperties cryptoServiceProperties() {
        return Utils.getDefaultProperties(this, this.purpose);
    }
}
