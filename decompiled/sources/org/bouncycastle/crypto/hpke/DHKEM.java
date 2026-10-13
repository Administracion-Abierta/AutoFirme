package org.bouncycastle.crypto.hpke;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.BasicAgreement;
import org.bouncycastle.crypto.agreement.ECDHCBasicAgreement;
import org.bouncycastle.crypto.agreement.XDHBasicAgreement;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.crypto.generators.X25519KeyPairGenerator;
import org.bouncycastle.crypto.generators.X448KeyPairGenerator;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECKeyGenerationParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.X25519KeyGenerationParameters;
import org.bouncycastle.crypto.params.X25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.X25519PublicKeyParameters;
import org.bouncycastle.crypto.params.X448KeyGenerationParameters;
import org.bouncycastle.crypto.params.X448PrivateKeyParameters;
import org.bouncycastle.crypto.params.X448PublicKeyParameters;
import org.bouncycastle.math.ec.FixedPointCombMultiplier;
import org.bouncycastle.math.ec.WNafUtil;
import org.bouncycastle.math.ec.custom.sec.SecP256R1Curve;
import org.bouncycastle.math.ec.custom.sec.SecP384R1Curve;
import org.bouncycastle.math.ec.custom.sec.SecP521R1Curve;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Pack;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.encoders.Hex;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/hpke/DHKEM.class */
class DHKEM extends KEM {
    private AsymmetricCipherKeyPairGenerator kpGen;
    private BasicAgreement agreement;
    private final short kemId;
    private HKDF hkdf;
    private byte bitmask;
    private int Nsk;
    private int Nsecret;
    private int Nenc;
    ECDomainParameters domainParams;

    protected DHKEM(short s) {
        this.kemId = s;
        switch (s) {
            case 16:
                this.hkdf = new HKDF((short) 1);
                SecP256R1Curve secP256R1Curve = new SecP256R1Curve();
                this.domainParams = new ECDomainParameters(secP256R1Curve, secP256R1Curve.createPoint(new BigInteger(1, Hex.decode("6b17d1f2e12c4247f8bce6e563a440f277037d812deb33a0f4a13945d898c296")), new BigInteger(1, Hex.decode("4fe342e2fe1a7f9b8ee7eb4a7c0f9e162bce33576b315ececbb6406837bf51f5"))), secP256R1Curve.getOrder(), secP256R1Curve.getCofactor(), Hex.decode("c49d360886e704936a6678e1139d26b7819f7e90"));
                this.agreement = new ECDHCBasicAgreement();
                this.bitmask = (byte) -1;
                this.Nsk = 32;
                this.Nsecret = 32;
                this.Nenc = 65;
                this.kpGen = new ECKeyPairGenerator();
                this.kpGen.init(new ECKeyGenerationParameters(this.domainParams, new SecureRandom()));
                return;
            case 17:
                this.hkdf = new HKDF((short) 2);
                SecP384R1Curve secP384R1Curve = new SecP384R1Curve();
                this.domainParams = new ECDomainParameters(secP384R1Curve, secP384R1Curve.createPoint(new BigInteger(1, Hex.decode("aa87ca22be8b05378eb1c71ef320ad746e1d3b628ba79b9859f741e082542a385502f25dbf55296c3a545e3872760ab7")), new BigInteger(1, Hex.decode("3617de4a96262c6f5d9e98bf9292dc29f8f41dbd289a147ce9da3113b5f0b8c00a60b1ce1d7e819d7a431d7c90ea0e5f"))), secP384R1Curve.getOrder(), secP384R1Curve.getCofactor(), Hex.decode("a335926aa319a27a1d00896a6773a4827acdac73"));
                this.agreement = new ECDHCBasicAgreement();
                this.bitmask = (byte) -1;
                this.Nsk = 48;
                this.Nsecret = 48;
                this.Nenc = 97;
                this.kpGen = new ECKeyPairGenerator();
                this.kpGen.init(new ECKeyGenerationParameters(this.domainParams, new SecureRandom()));
                return;
            case 18:
                this.hkdf = new HKDF((short) 3);
                SecP521R1Curve secP521R1Curve = new SecP521R1Curve();
                this.domainParams = new ECDomainParameters(secP521R1Curve, secP521R1Curve.createPoint(new BigInteger("c6858e06b70404e9cd9e3ecb662395b4429c648139053fb521f828af606b4d3dbaa14b5e77efe75928fe1dc127a2ffa8de3348b3c1856a429bf97e7e31c2e5bd66", 16), new BigInteger("11839296a789a3bc0045c8a5fb42c7d1bd998f54449579b446817afbd17273e662c97ee72995ef42640c550b9013fad0761353c7086a272c24088be94769fd16650", 16)), secP521R1Curve.getOrder(), secP521R1Curve.getCofactor(), Hex.decode("d09e8800291cb85396cc6717393284aaa0da64ba"));
                this.agreement = new ECDHCBasicAgreement();
                this.bitmask = (byte) 1;
                this.Nsk = 66;
                this.Nsecret = 64;
                this.Nenc = 133;
                this.kpGen = new ECKeyPairGenerator();
                this.kpGen.init(new ECKeyGenerationParameters(this.domainParams, new SecureRandom()));
                return;
            case 32:
                this.hkdf = new HKDF((short) 1);
                this.agreement = new XDHBasicAgreement();
                this.Nsecret = 32;
                this.Nsk = 32;
                this.Nenc = 32;
                this.kpGen = new X25519KeyPairGenerator();
                this.kpGen.init(new X25519KeyGenerationParameters(new SecureRandom()));
                return;
            case 33:
                this.hkdf = new HKDF((short) 3);
                this.agreement = new XDHBasicAgreement();
                this.Nsecret = 64;
                this.Nsk = 56;
                this.Nenc = 56;
                this.kpGen = new X448KeyPairGenerator();
                this.kpGen.init(new X448KeyGenerationParameters(new SecureRandom()));
                return;
            default:
                throw new IllegalArgumentException("invalid kem id");
        }
    }

    @Override // org.bouncycastle.crypto.hpke.KEM
    public byte[] SerializePublicKey(AsymmetricKeyParameter asymmetricKeyParameter) {
        switch (this.kemId) {
            case 16:
            case 17:
            case 18:
                return ((ECPublicKeyParameters) asymmetricKeyParameter).getQ().getEncoded(false);
            case 32:
                return ((X25519PublicKeyParameters) asymmetricKeyParameter).getEncoded();
            case 33:
                return ((X448PublicKeyParameters) asymmetricKeyParameter).getEncoded();
            default:
                throw new IllegalStateException("invalid kem id");
        }
    }

    @Override // org.bouncycastle.crypto.hpke.KEM
    public byte[] SerializePrivateKey(AsymmetricKeyParameter asymmetricKeyParameter) {
        switch (this.kemId) {
            case 16:
            case 17:
            case 18:
                return formatBigIntegerBytes(((ECPrivateKeyParameters) asymmetricKeyParameter).getD().toByteArray(), this.Nsk);
            case 32:
                return ((X25519PrivateKeyParameters) asymmetricKeyParameter).getEncoded();
            case 33:
                return ((X448PrivateKeyParameters) asymmetricKeyParameter).getEncoded();
            default:
                throw new IllegalStateException("invalid kem id");
        }
    }

    @Override // org.bouncycastle.crypto.hpke.KEM
    public AsymmetricKeyParameter DeserializePublicKey(byte[] bArr) {
        switch (this.kemId) {
            case 16:
            case 17:
            case 18:
                return new ECPublicKeyParameters(this.domainParams.getCurve().decodePoint(bArr), this.domainParams);
            case 32:
                return new X25519PublicKeyParameters(bArr);
            case 33:
                return new X448PublicKeyParameters(bArr);
            default:
                throw new IllegalStateException("invalid kem id");
        }
    }

    @Override // org.bouncycastle.crypto.hpke.KEM
    public AsymmetricCipherKeyPair DeserializePrivateKey(byte[] bArr, byte[] bArr2) {
        AsymmetricKeyParameter asymmetricKeyParameterGeneratePublicKey = null;
        if (bArr2 != null) {
            asymmetricKeyParameterGeneratePublicKey = DeserializePublicKey(bArr2);
        }
        switch (this.kemId) {
            case 16:
            case 17:
            case 18:
                ECPrivateKeyParameters eCPrivateKeyParameters = new ECPrivateKeyParameters(new BigInteger(1, bArr), this.domainParams);
                if (asymmetricKeyParameterGeneratePublicKey == null) {
                    asymmetricKeyParameterGeneratePublicKey = new ECPublicKeyParameters(new FixedPointCombMultiplier().multiply(this.domainParams.getG(), eCPrivateKeyParameters.getD()), this.domainParams);
                }
                return new AsymmetricCipherKeyPair(asymmetricKeyParameterGeneratePublicKey, (AsymmetricKeyParameter) eCPrivateKeyParameters);
            case 32:
                X25519PrivateKeyParameters x25519PrivateKeyParameters = new X25519PrivateKeyParameters(bArr);
                if (asymmetricKeyParameterGeneratePublicKey == null) {
                    asymmetricKeyParameterGeneratePublicKey = x25519PrivateKeyParameters.generatePublicKey();
                }
                return new AsymmetricCipherKeyPair(asymmetricKeyParameterGeneratePublicKey, (AsymmetricKeyParameter) x25519PrivateKeyParameters);
            case 33:
                X448PrivateKeyParameters x448PrivateKeyParameters = new X448PrivateKeyParameters(bArr);
                if (asymmetricKeyParameterGeneratePublicKey == null) {
                    asymmetricKeyParameterGeneratePublicKey = x448PrivateKeyParameters.generatePublicKey();
                }
                return new AsymmetricCipherKeyPair(asymmetricKeyParameterGeneratePublicKey, (AsymmetricKeyParameter) x448PrivateKeyParameters);
            default:
                throw new IllegalStateException("invalid kem id");
        }
    }

    @Override // org.bouncycastle.crypto.hpke.KEM
    int getEncryptionSize() {
        return this.Nenc;
    }

    private boolean ValidateSk(BigInteger bigInteger) {
        BigInteger n = this.domainParams.getN();
        return bigInteger.compareTo(BigInteger.valueOf(1L)) >= 0 && bigInteger.compareTo(n) < 0 && WNafUtil.getNafWeight(bigInteger) >= (n.bitLength() >>> 2);
    }

    @Override // org.bouncycastle.crypto.hpke.KEM
    public AsymmetricCipherKeyPair GeneratePrivateKey() {
        return this.kpGen.generateKeyPair();
    }

    @Override // org.bouncycastle.crypto.hpke.KEM
    public AsymmetricCipherKeyPair DeriveKeyPair(byte[] bArr) {
        byte[] bArrConcatenate = Arrays.concatenate(Strings.toByteArray("KEM"), Pack.shortToBigEndian(this.kemId));
        switch (this.kemId) {
            case 16:
            case 17:
            case 18:
                byte[] bArrLabeledExtract = this.hkdf.LabeledExtract(null, bArrConcatenate, "dkp_prk", bArr);
                byte[] bArr2 = new byte[1];
                for (int i = 0; i <= 255; i++) {
                    bArr2[0] = (byte) i;
                    byte[] bArrLabeledExpand = this.hkdf.LabeledExpand(bArrLabeledExtract, bArrConcatenate, "candidate", bArr2, this.Nsk);
                    bArrLabeledExpand[0] = (byte) (bArrLabeledExpand[0] & this.bitmask);
                    BigInteger bigInteger = new BigInteger(1, bArrLabeledExpand);
                    if (ValidateSk(bigInteger)) {
                        return new AsymmetricCipherKeyPair((AsymmetricKeyParameter) new ECPublicKeyParameters(new FixedPointCombMultiplier().multiply(this.domainParams.getG(), bigInteger), this.domainParams), (AsymmetricKeyParameter) new ECPrivateKeyParameters(bigInteger, this.domainParams));
                    }
                }
                throw new IllegalStateException("DeriveKeyPairError");
            case 32:
                X25519PrivateKeyParameters x25519PrivateKeyParameters = new X25519PrivateKeyParameters(this.hkdf.LabeledExpand(this.hkdf.LabeledExtract(null, bArrConcatenate, "dkp_prk", bArr), bArrConcatenate, "sk", null, this.Nsk));
                return new AsymmetricCipherKeyPair((AsymmetricKeyParameter) x25519PrivateKeyParameters.generatePublicKey(), (AsymmetricKeyParameter) x25519PrivateKeyParameters);
            case 33:
                X448PrivateKeyParameters x448PrivateKeyParameters = new X448PrivateKeyParameters(this.hkdf.LabeledExpand(this.hkdf.LabeledExtract(null, bArrConcatenate, "dkp_prk", bArr), bArrConcatenate, "sk", null, this.Nsk));
                return new AsymmetricCipherKeyPair((AsymmetricKeyParameter) x448PrivateKeyParameters.generatePublicKey(), (AsymmetricKeyParameter) x448PrivateKeyParameters);
            default:
                throw new IllegalStateException("invalid kem id");
        }
    }

    @Override // org.bouncycastle.crypto.hpke.KEM
    protected byte[][] Encap(AsymmetricKeyParameter asymmetricKeyParameter) {
        return Encap(asymmetricKeyParameter, this.kpGen.generateKeyPair());
    }

    @Override // org.bouncycastle.crypto.hpke.KEM
    protected byte[][] Encap(AsymmetricKeyParameter asymmetricKeyParameter, AsymmetricCipherKeyPair asymmetricCipherKeyPair) {
        this.agreement.init(asymmetricCipherKeyPair.getPrivate());
        byte[] bigIntegerBytes = formatBigIntegerBytes(this.agreement.calculateAgreement(asymmetricKeyParameter).toByteArray(), this.agreement.getFieldSize());
        byte[] bArrSerializePublicKey = SerializePublicKey(asymmetricCipherKeyPair.getPublic());
        return new byte[][]{ExtractAndExpand(bigIntegerBytes, Arrays.concatenate(bArrSerializePublicKey, SerializePublicKey(asymmetricKeyParameter))), bArrSerializePublicKey};
    }

    @Override // org.bouncycastle.crypto.hpke.KEM
    protected byte[] Decap(byte[] bArr, AsymmetricCipherKeyPair asymmetricCipherKeyPair) {
        AsymmetricKeyParameter asymmetricKeyParameterDeserializePublicKey = DeserializePublicKey(bArr);
        this.agreement.init(asymmetricCipherKeyPair.getPrivate());
        return ExtractAndExpand(formatBigIntegerBytes(this.agreement.calculateAgreement(asymmetricKeyParameterDeserializePublicKey).toByteArray(), this.agreement.getFieldSize()), Arrays.concatenate(bArr, SerializePublicKey(asymmetricCipherKeyPair.getPublic())));
    }

    @Override // org.bouncycastle.crypto.hpke.KEM
    protected byte[][] AuthEncap(AsymmetricKeyParameter asymmetricKeyParameter, AsymmetricCipherKeyPair asymmetricCipherKeyPair) {
        AsymmetricCipherKeyPair asymmetricCipherKeyPairGenerateKeyPair = this.kpGen.generateKeyPair();
        this.agreement.init(asymmetricCipherKeyPairGenerateKeyPair.getPrivate());
        byte[] bigIntegerBytes = formatBigIntegerBytes(this.agreement.calculateAgreement(asymmetricKeyParameter).toByteArray(), this.agreement.getFieldSize());
        this.agreement.init(asymmetricCipherKeyPair.getPrivate());
        byte[] bArrConcatenate = Arrays.concatenate(bigIntegerBytes, formatBigIntegerBytes(this.agreement.calculateAgreement(asymmetricKeyParameter).toByteArray(), this.agreement.getFieldSize()));
        byte[] bArrSerializePublicKey = SerializePublicKey(asymmetricCipherKeyPairGenerateKeyPair.getPublic());
        return new byte[][]{ExtractAndExpand(bArrConcatenate, Arrays.concatenate(bArrSerializePublicKey, SerializePublicKey(asymmetricKeyParameter), SerializePublicKey(asymmetricCipherKeyPair.getPublic()))), bArrSerializePublicKey};
    }

    @Override // org.bouncycastle.crypto.hpke.KEM
    protected byte[] AuthDecap(byte[] bArr, AsymmetricCipherKeyPair asymmetricCipherKeyPair, AsymmetricKeyParameter asymmetricKeyParameter) {
        AsymmetricKeyParameter asymmetricKeyParameterDeserializePublicKey = DeserializePublicKey(bArr);
        this.agreement.init(asymmetricCipherKeyPair.getPrivate());
        byte[] bigIntegerBytes = formatBigIntegerBytes(this.agreement.calculateAgreement(asymmetricKeyParameterDeserializePublicKey).toByteArray(), this.agreement.getFieldSize());
        this.agreement.init(asymmetricCipherKeyPair.getPrivate());
        return ExtractAndExpand(Arrays.concatenate(bigIntegerBytes, formatBigIntegerBytes(this.agreement.calculateAgreement(asymmetricKeyParameter).toByteArray(), this.agreement.getFieldSize())), Arrays.concatenate(bArr, SerializePublicKey(asymmetricCipherKeyPair.getPublic()), SerializePublicKey(asymmetricKeyParameter)));
    }

    private byte[] ExtractAndExpand(byte[] bArr, byte[] bArr2) {
        byte[] bArrConcatenate = Arrays.concatenate(Strings.toByteArray("KEM"), Pack.shortToBigEndian(this.kemId));
        return this.hkdf.LabeledExpand(this.hkdf.LabeledExtract(null, bArrConcatenate, "eae_prk", bArr), bArrConcatenate, "shared_secret", bArr2, this.Nsecret);
    }

    private byte[] formatBigIntegerBytes(byte[] bArr, int i) {
        byte[] bArr2 = new byte[i];
        if (bArr.length <= i) {
            System.arraycopy(bArr, 0, bArr2, i - bArr.length, bArr.length);
        } else {
            System.arraycopy(bArr, bArr.length - i, bArr2, 0, i);
        }
        return bArr2;
    }
}
