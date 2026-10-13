package org.bouncycastle.crypto.hpke;

import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Pack;
import org.bouncycastle.util.Strings;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/hpke/HPKE.class */
public class HPKE {
    public static final byte mode_base = 0;
    public static final byte mode_psk = 1;
    public static final byte mode_auth = 2;
    public static final byte mode_auth_psk = 3;
    public static final short kem_P256_SHA256 = 16;
    public static final short kem_P384_SHA348 = 17;
    public static final short kem_P521_SHA512 = 18;
    public static final short kem_X25519_SHA256 = 32;
    public static final short kem_X448_SHA512 = 33;
    public static final short kdf_HKDF_SHA256 = 1;
    public static final short kdf_HKDF_SHA384 = 2;
    public static final short kdf_HKDF_SHA512 = 3;
    public static final short aead_AES_GCM128 = 1;
    public static final short aead_AES_GCM256 = 2;
    public static final short aead_CHACHA20_POLY1305 = 3;
    public static final short aead_EXPORT_ONLY = -1;
    private final byte[] default_psk = null;
    private final byte[] default_psk_id = null;
    private final byte mode;
    private final short kemId;
    private final short kdfId;
    private final short aeadId;
    private final KEM kem;
    private final HKDF hkdf;
    private final int encSize;
    short Nk;

    public HPKE(byte b, short s, short s2, short s3) {
        this.mode = b;
        this.kemId = s;
        this.kdfId = s2;
        this.aeadId = s3;
        this.hkdf = new HKDF(s2);
        this.kem = new DHKEM(s);
        if (s3 == 1) {
            this.Nk = (short) 16;
        } else {
            this.Nk = (short) 32;
        }
        this.encSize = this.kem.getEncryptionSize();
    }

    public HPKE(byte b, short s, short s2, short s3, KEM kem, int i) {
        this.mode = b;
        this.kemId = s;
        this.kdfId = s2;
        this.aeadId = s3;
        this.hkdf = new HKDF(s2);
        this.kem = kem;
        if (s3 == 1) {
            this.Nk = (short) 16;
        } else {
            this.Nk = (short) 32;
        }
        this.encSize = i;
    }

    public int getEncSize() {
        return this.encSize;
    }

    public short getAeadId() {
        return this.aeadId;
    }

    private void VerifyPSKInputs(byte b, byte[] bArr, byte[] bArr2) {
        boolean z = !Arrays.areEqual(bArr, this.default_psk);
        if (z != (!Arrays.areEqual(bArr2, this.default_psk_id))) {
            throw new IllegalArgumentException("Inconsistent PSK inputs");
        }
        if (z && b % 2 == 0) {
            throw new IllegalArgumentException("PSK input provided when not needed");
        }
        if (!z && b % 2 == 1) {
            throw new IllegalArgumentException("Missing required PSK input");
        }
    }

    private HPKEContext keySchedule(byte b, byte[] bArr, byte[] bArr2, byte[] bArr3, byte[] bArr4) {
        VerifyPSKInputs(b, bArr3, bArr4);
        byte[] bArrConcatenate = Arrays.concatenate(Strings.toByteArray("HPKE"), Pack.shortToBigEndian(this.kemId), Pack.shortToBigEndian(this.kdfId), Pack.shortToBigEndian(this.aeadId));
        byte[] bArrConcatenate2 = Arrays.concatenate(new byte[]{b}, this.hkdf.LabeledExtract(null, bArrConcatenate, "psk_id_hash", bArr4), this.hkdf.LabeledExtract(null, bArrConcatenate, "info_hash", bArr2));
        byte[] bArrLabeledExtract = this.hkdf.LabeledExtract(bArr, bArrConcatenate, "secret", bArr3);
        return new HPKEContext(new AEAD(this.aeadId, this.hkdf.LabeledExpand(bArrLabeledExtract, bArrConcatenate, "key", bArrConcatenate2, this.Nk), this.hkdf.LabeledExpand(bArrLabeledExtract, bArrConcatenate, "base_nonce", bArrConcatenate2, 12)), this.hkdf, this.hkdf.LabeledExpand(bArrLabeledExtract, bArrConcatenate, "exp", bArrConcatenate2, this.hkdf.getHashSize()), bArrConcatenate);
    }

    public AsymmetricCipherKeyPair generatePrivateKey() {
        return this.kem.GeneratePrivateKey();
    }

    public byte[] serializePublicKey(AsymmetricKeyParameter asymmetricKeyParameter) {
        return this.kem.SerializePublicKey(asymmetricKeyParameter);
    }

    public byte[] serializePrivateKey(AsymmetricKeyParameter asymmetricKeyParameter) {
        return this.kem.SerializePrivateKey(asymmetricKeyParameter);
    }

    public AsymmetricKeyParameter deserializePublicKey(byte[] bArr) {
        return this.kem.DeserializePublicKey(bArr);
    }

    public AsymmetricCipherKeyPair deserializePrivateKey(byte[] bArr, byte[] bArr2) {
        return this.kem.DeserializePrivateKey(bArr, bArr2);
    }

    public AsymmetricCipherKeyPair deriveKeyPair(byte[] bArr) {
        return this.kem.DeriveKeyPair(bArr);
    }

    public byte[][] sendExport(AsymmetricKeyParameter asymmetricKeyParameter, byte[] bArr, byte[] bArr2, int i, byte[] bArr3, byte[] bArr4, AsymmetricCipherKeyPair asymmetricCipherKeyPair) {
        HPKEContextWithEncapsulation hPKEContextWithEncapsulationSetupPSKS;
        byte[][] bArr5 = new byte[2][];
        switch (this.mode) {
            case 0:
                hPKEContextWithEncapsulationSetupPSKS = setupBaseS(asymmetricKeyParameter, bArr);
                break;
            case 1:
                hPKEContextWithEncapsulationSetupPSKS = SetupPSKS(asymmetricKeyParameter, bArr, bArr3, bArr4);
                break;
            case 2:
                hPKEContextWithEncapsulationSetupPSKS = setupAuthS(asymmetricKeyParameter, bArr, asymmetricCipherKeyPair);
                break;
            case 3:
                hPKEContextWithEncapsulationSetupPSKS = setupAuthPSKS(asymmetricKeyParameter, bArr, bArr3, bArr4, asymmetricCipherKeyPair);
                break;
            default:
                throw new IllegalStateException("Unknown mode");
        }
        bArr5[0] = hPKEContextWithEncapsulationSetupPSKS.encapsulation;
        bArr5[1] = hPKEContextWithEncapsulationSetupPSKS.export(bArr2, i);
        return bArr5;
    }

    public byte[] receiveExport(byte[] bArr, AsymmetricCipherKeyPair asymmetricCipherKeyPair, byte[] bArr2, byte[] bArr3, int i, byte[] bArr4, byte[] bArr5, AsymmetricKeyParameter asymmetricKeyParameter) {
        HPKEContext hPKEContext;
        switch (this.mode) {
            case 0:
                hPKEContext = setupBaseR(bArr, asymmetricCipherKeyPair, bArr2);
                break;
            case 1:
                hPKEContext = setupPSKR(bArr, asymmetricCipherKeyPair, bArr2, bArr4, bArr5);
                break;
            case 2:
                hPKEContext = setupAuthR(bArr, asymmetricCipherKeyPair, bArr2, asymmetricKeyParameter);
                break;
            case 3:
                hPKEContext = setupAuthPSKR(bArr, asymmetricCipherKeyPair, bArr2, bArr4, bArr5, asymmetricKeyParameter);
                break;
            default:
                throw new IllegalStateException("Unknown mode");
        }
        return hPKEContext.export(bArr3, i);
    }

    public byte[][] seal(AsymmetricKeyParameter asymmetricKeyParameter, byte[] bArr, byte[] bArr2, byte[] bArr3, byte[] bArr4, byte[] bArr5, AsymmetricCipherKeyPair asymmetricCipherKeyPair) throws InvalidCipherTextException {
        HPKEContextWithEncapsulation hPKEContextWithEncapsulationSetupPSKS;
        byte[][] bArr6 = new byte[2][];
        switch (this.mode) {
            case 0:
                hPKEContextWithEncapsulationSetupPSKS = setupBaseS(asymmetricKeyParameter, bArr);
                break;
            case 1:
                hPKEContextWithEncapsulationSetupPSKS = SetupPSKS(asymmetricKeyParameter, bArr, bArr4, bArr5);
                break;
            case 2:
                hPKEContextWithEncapsulationSetupPSKS = setupAuthS(asymmetricKeyParameter, bArr, asymmetricCipherKeyPair);
                break;
            case 3:
                hPKEContextWithEncapsulationSetupPSKS = setupAuthPSKS(asymmetricKeyParameter, bArr, bArr4, bArr5, asymmetricCipherKeyPair);
                break;
            default:
                throw new IllegalStateException("Unknown mode");
        }
        bArr6[0] = hPKEContextWithEncapsulationSetupPSKS.seal(bArr2, bArr3);
        bArr6[1] = hPKEContextWithEncapsulationSetupPSKS.getEncapsulation();
        return bArr6;
    }

    public byte[] open(byte[] bArr, AsymmetricCipherKeyPair asymmetricCipherKeyPair, byte[] bArr2, byte[] bArr3, byte[] bArr4, byte[] bArr5, byte[] bArr6, AsymmetricKeyParameter asymmetricKeyParameter) throws InvalidCipherTextException {
        HPKEContext hPKEContext;
        switch (this.mode) {
            case 0:
                hPKEContext = setupBaseR(bArr, asymmetricCipherKeyPair, bArr2);
                break;
            case 1:
                hPKEContext = setupPSKR(bArr, asymmetricCipherKeyPair, bArr2, bArr5, bArr6);
                break;
            case 2:
                hPKEContext = setupAuthR(bArr, asymmetricCipherKeyPair, bArr2, asymmetricKeyParameter);
                break;
            case 3:
                hPKEContext = setupAuthPSKR(bArr, asymmetricCipherKeyPair, bArr2, bArr5, bArr6, asymmetricKeyParameter);
                break;
            default:
                throw new IllegalStateException("Unknown mode");
        }
        return hPKEContext.open(bArr3, bArr4);
    }

    public HPKEContextWithEncapsulation setupBaseS(AsymmetricKeyParameter asymmetricKeyParameter, byte[] bArr) {
        byte[][] bArrEncap = this.kem.Encap(asymmetricKeyParameter);
        return new HPKEContextWithEncapsulation(keySchedule((byte) 0, bArrEncap[0], bArr, this.default_psk, this.default_psk_id), bArrEncap[1]);
    }

    public HPKEContextWithEncapsulation setupBaseS(AsymmetricKeyParameter asymmetricKeyParameter, byte[] bArr, AsymmetricCipherKeyPair asymmetricCipherKeyPair) {
        byte[][] bArrEncap = this.kem.Encap(asymmetricKeyParameter, asymmetricCipherKeyPair);
        return new HPKEContextWithEncapsulation(keySchedule((byte) 0, bArrEncap[0], bArr, this.default_psk, this.default_psk_id), bArrEncap[1]);
    }

    public HPKEContext setupBaseR(byte[] bArr, AsymmetricCipherKeyPair asymmetricCipherKeyPair, byte[] bArr2) {
        return keySchedule((byte) 0, this.kem.Decap(bArr, asymmetricCipherKeyPair), bArr2, this.default_psk, this.default_psk_id);
    }

    public HPKEContextWithEncapsulation SetupPSKS(AsymmetricKeyParameter asymmetricKeyParameter, byte[] bArr, byte[] bArr2, byte[] bArr3) {
        byte[][] bArrEncap = this.kem.Encap(asymmetricKeyParameter);
        return new HPKEContextWithEncapsulation(keySchedule((byte) 1, bArrEncap[0], bArr, bArr2, bArr3), bArrEncap[1]);
    }

    public HPKEContext setupPSKR(byte[] bArr, AsymmetricCipherKeyPair asymmetricCipherKeyPair, byte[] bArr2, byte[] bArr3, byte[] bArr4) {
        return keySchedule((byte) 1, this.kem.Decap(bArr, asymmetricCipherKeyPair), bArr2, bArr3, bArr4);
    }

    public HPKEContextWithEncapsulation setupAuthS(AsymmetricKeyParameter asymmetricKeyParameter, byte[] bArr, AsymmetricCipherKeyPair asymmetricCipherKeyPair) {
        byte[][] bArrAuthEncap = this.kem.AuthEncap(asymmetricKeyParameter, asymmetricCipherKeyPair);
        return new HPKEContextWithEncapsulation(keySchedule((byte) 2, bArrAuthEncap[0], bArr, this.default_psk, this.default_psk_id), bArrAuthEncap[1]);
    }

    public HPKEContext setupAuthR(byte[] bArr, AsymmetricCipherKeyPair asymmetricCipherKeyPair, byte[] bArr2, AsymmetricKeyParameter asymmetricKeyParameter) {
        return keySchedule((byte) 2, this.kem.AuthDecap(bArr, asymmetricCipherKeyPair, asymmetricKeyParameter), bArr2, this.default_psk, this.default_psk_id);
    }

    public HPKEContextWithEncapsulation setupAuthPSKS(AsymmetricKeyParameter asymmetricKeyParameter, byte[] bArr, byte[] bArr2, byte[] bArr3, AsymmetricCipherKeyPair asymmetricCipherKeyPair) {
        byte[][] bArrAuthEncap = this.kem.AuthEncap(asymmetricKeyParameter, asymmetricCipherKeyPair);
        return new HPKEContextWithEncapsulation(keySchedule((byte) 3, bArrAuthEncap[0], bArr, bArr2, bArr3), bArrAuthEncap[1]);
    }

    public HPKEContext setupAuthPSKR(byte[] bArr, AsymmetricCipherKeyPair asymmetricCipherKeyPair, byte[] bArr2, byte[] bArr3, byte[] bArr4, AsymmetricKeyParameter asymmetricKeyParameter) {
        return keySchedule((byte) 3, this.kem.AuthDecap(bArr, asymmetricCipherKeyPair, asymmetricKeyParameter), bArr2, bArr3, bArr4);
    }
}
