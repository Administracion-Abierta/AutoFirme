package org.bouncycastle.pqc.legacy.crypto.gmss;

import java.security.SecureRandom;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.pqc.crypto.MessageSigner;
import org.bouncycastle.pqc.legacy.crypto.gmss.util.GMSSRandom;
import org.bouncycastle.pqc.legacy.crypto.gmss.util.GMSSUtil;
import org.bouncycastle.pqc.legacy.crypto.gmss.util.WinternitzOTSVerify;
import org.bouncycastle.pqc.legacy.crypto.gmss.util.WinternitzOTSignature;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/legacy/crypto/gmss/GMSSSigner.class */
public class GMSSSigner implements MessageSigner {
    private GMSSUtil gmssUtil = new GMSSUtil();
    private byte[] pubKeyBytes;
    private Digest messDigestTrees;
    private int mdLength;
    private int numLayer;
    private Digest messDigestOTS;
    private WinternitzOTSignature ots;
    private GMSSDigestProvider digestProvider;
    private int[] index;
    private byte[][][] currentAuthPaths;
    private byte[][] subtreeRootSig;
    private GMSSParameters gmssPS;
    private GMSSRandom gmssRandom;
    GMSSKeyParameters key;
    private SecureRandom random;

    public GMSSSigner(GMSSDigestProvider gMSSDigestProvider) {
        this.digestProvider = gMSSDigestProvider;
        this.messDigestTrees = gMSSDigestProvider.get();
        this.messDigestOTS = this.messDigestTrees;
        this.mdLength = this.messDigestTrees.getDigestSize();
        this.gmssRandom = new GMSSRandom(this.messDigestTrees);
    }

    @Override // org.bouncycastle.pqc.crypto.MessageSigner
    public void init(boolean z, CipherParameters cipherParameters) {
        if (!z) {
            this.key = (GMSSPublicKeyParameters) cipherParameters;
            initVerify();
        } else if (!(cipherParameters instanceof ParametersWithRandom)) {
            this.random = CryptoServicesRegistrar.getSecureRandom();
            this.key = (GMSSPrivateKeyParameters) cipherParameters;
            initSign();
        } else {
            ParametersWithRandom parametersWithRandom = (ParametersWithRandom) cipherParameters;
            this.random = parametersWithRandom.getRandom();
            this.key = (GMSSPrivateKeyParameters) parametersWithRandom.getParameters();
            initSign();
        }
    }

    private void initSign() {
        this.messDigestTrees.reset();
        GMSSPrivateKeyParameters gMSSPrivateKeyParameters = (GMSSPrivateKeyParameters) this.key;
        if (gMSSPrivateKeyParameters.isUsed()) {
            throw new IllegalStateException("Private key already used");
        }
        if (gMSSPrivateKeyParameters.getIndex(0) >= gMSSPrivateKeyParameters.getNumLeafs(0)) {
            throw new IllegalStateException("No more signatures can be generated");
        }
        this.gmssPS = gMSSPrivateKeyParameters.getParameters();
        this.numLayer = this.gmssPS.getNumOfLayers();
        byte[] bArr = gMSSPrivateKeyParameters.getCurrentSeeds()[this.numLayer - 1];
        byte[] bArr2 = new byte[this.mdLength];
        byte[] bArr3 = new byte[this.mdLength];
        System.arraycopy(bArr, 0, bArr3, 0, this.mdLength);
        this.ots = new WinternitzOTSignature(this.gmssRandom.nextSeed(bArr3), this.digestProvider.get(), this.gmssPS.getWinternitzParameter()[this.numLayer - 1]);
        byte[][][] currentAuthPaths = gMSSPrivateKeyParameters.getCurrentAuthPaths();
        this.currentAuthPaths = new byte[this.numLayer][][];
        for (int i = 0; i < this.numLayer; i++) {
            this.currentAuthPaths[i] = new byte[currentAuthPaths[i].length][this.mdLength];
            for (int i2 = 0; i2 < currentAuthPaths[i].length; i2++) {
                System.arraycopy(currentAuthPaths[i][i2], 0, this.currentAuthPaths[i][i2], 0, this.mdLength);
            }
        }
        this.index = new int[this.numLayer];
        System.arraycopy(gMSSPrivateKeyParameters.getIndex(), 0, this.index, 0, this.numLayer);
        this.subtreeRootSig = new byte[this.numLayer - 1][];
        for (int i3 = 0; i3 < this.numLayer - 1; i3++) {
            byte[] subtreeRootSig = gMSSPrivateKeyParameters.getSubtreeRootSig(i3);
            this.subtreeRootSig[i3] = new byte[subtreeRootSig.length];
            System.arraycopy(subtreeRootSig, 0, this.subtreeRootSig[i3], 0, subtreeRootSig.length);
        }
        gMSSPrivateKeyParameters.markUsed();
    }

    @Override // org.bouncycastle.pqc.crypto.MessageSigner
    public byte[] generateSignature(byte[] bArr) {
        byte[] bArr2 = new byte[this.mdLength];
        byte[] signature = this.ots.getSignature(bArr);
        byte[] bArrConcatenateArray = this.gmssUtil.concatenateArray(this.currentAuthPaths[this.numLayer - 1]);
        byte[] bArrIntToBytesLittleEndian = this.gmssUtil.intToBytesLittleEndian(this.index[this.numLayer - 1]);
        byte[] bArr3 = new byte[bArrIntToBytesLittleEndian.length + signature.length + bArrConcatenateArray.length];
        System.arraycopy(bArrIntToBytesLittleEndian, 0, bArr3, 0, bArrIntToBytesLittleEndian.length);
        System.arraycopy(signature, 0, bArr3, bArrIntToBytesLittleEndian.length, signature.length);
        System.arraycopy(bArrConcatenateArray, 0, bArr3, bArrIntToBytesLittleEndian.length + signature.length, bArrConcatenateArray.length);
        byte[] bArr4 = new byte[0];
        for (int i = (this.numLayer - 1) - 1; i >= 0; i--) {
            byte[] bArrConcatenateArray2 = this.gmssUtil.concatenateArray(this.currentAuthPaths[i]);
            byte[] bArrIntToBytesLittleEndian2 = this.gmssUtil.intToBytesLittleEndian(this.index[i]);
            byte[] bArr5 = new byte[bArr4.length];
            System.arraycopy(bArr4, 0, bArr5, 0, bArr4.length);
            bArr4 = new byte[bArr5.length + bArrIntToBytesLittleEndian2.length + this.subtreeRootSig[i].length + bArrConcatenateArray2.length];
            System.arraycopy(bArr5, 0, bArr4, 0, bArr5.length);
            System.arraycopy(bArrIntToBytesLittleEndian2, 0, bArr4, bArr5.length, bArrIntToBytesLittleEndian2.length);
            System.arraycopy(this.subtreeRootSig[i], 0, bArr4, bArr5.length + bArrIntToBytesLittleEndian2.length, this.subtreeRootSig[i].length);
            System.arraycopy(bArrConcatenateArray2, 0, bArr4, bArr5.length + bArrIntToBytesLittleEndian2.length + this.subtreeRootSig[i].length, bArrConcatenateArray2.length);
        }
        byte[] bArr6 = new byte[bArr3.length + bArr4.length];
        System.arraycopy(bArr3, 0, bArr6, 0, bArr3.length);
        System.arraycopy(bArr4, 0, bArr6, bArr3.length, bArr4.length);
        return bArr6;
    }

    private void initVerify() {
        this.messDigestTrees.reset();
        GMSSPublicKeyParameters gMSSPublicKeyParameters = (GMSSPublicKeyParameters) this.key;
        this.pubKeyBytes = gMSSPublicKeyParameters.getPublicKey();
        this.gmssPS = gMSSPublicKeyParameters.getParameters();
        this.numLayer = this.gmssPS.getNumOfLayers();
    }

    @Override // org.bouncycastle.pqc.crypto.MessageSigner
    public boolean verifySignature(byte[] bArr, byte[] bArr2) {
        int i;
        this.messDigestOTS.reset();
        byte[] bArr3 = bArr;
        int i2 = 0;
        for (int i3 = this.numLayer - 1; i3 >= 0; i3--) {
            WinternitzOTSVerify winternitzOTSVerify = new WinternitzOTSVerify(this.digestProvider.get(), this.gmssPS.getWinternitzParameter()[i3]);
            int signatureLength = winternitzOTSVerify.getSignatureLength();
            int iBytesToIntLittleEndian = this.gmssUtil.bytesToIntLittleEndian(bArr2, i2);
            int i4 = i2 + 4;
            byte[] bArr4 = new byte[signatureLength];
            System.arraycopy(bArr2, i4, bArr4, 0, signatureLength);
            i2 = i4 + signatureLength;
            byte[] bArrVerify = winternitzOTSVerify.Verify(bArr3, bArr4);
            if (bArrVerify == null) {
                System.err.println("OTS Public Key is null in GMSSSignature.verify");
                return false;
            }
            byte[][] bArr5 = new byte[this.gmssPS.getHeightOfTrees()[i3]][this.mdLength];
            for (byte[] bArr6 : bArr5) {
                System.arraycopy(bArr2, i2, bArr6, 0, this.mdLength);
                i2 += this.mdLength;
            }
            byte[] bArr7 = new byte[this.mdLength];
            bArr3 = bArrVerify;
            int length = (1 << bArr5.length) + iBytesToIntLittleEndian;
            for (int i5 = 0; i5 < bArr5.length; i5++) {
                byte[] bArr8 = new byte[this.mdLength << 1];
                if (length % 2 == 0) {
                    System.arraycopy(bArr3, 0, bArr8, 0, this.mdLength);
                    System.arraycopy(bArr5[i5], 0, bArr8, this.mdLength, this.mdLength);
                    i = length;
                } else {
                    System.arraycopy(bArr5[i5], 0, bArr8, 0, this.mdLength);
                    System.arraycopy(bArr3, 0, bArr8, this.mdLength, bArr3.length);
                    i = length - 1;
                }
                length = i / 2;
                this.messDigestTrees.update(bArr8, 0, bArr8.length);
                bArr3 = new byte[this.messDigestTrees.getDigestSize()];
                this.messDigestTrees.doFinal(bArr3, 0);
            }
        }
        return Arrays.areEqual(this.pubKeyBytes, bArr3);
    }
}
