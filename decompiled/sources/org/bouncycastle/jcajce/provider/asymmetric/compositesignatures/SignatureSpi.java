package org.bouncycastle.jcajce.provider.asymmetric.compositesignatures;

import java.io.IOException;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.internal.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.jcajce.CompositePrivateKey;
import org.bouncycastle.jcajce.CompositePublicKey;
import org.bouncycastle.jcajce.spec.ContextParameterSpec;
import org.bouncycastle.jcajce.util.BCJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.Exceptions;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/compositesignatures/SignatureSpi.class */
public class SignatureSpi extends java.security.SignatureSpi {
    private static final Map<String, String> canonicalNames = new HashMap();
    private static final String ML_DSA_44 = "ML-DSA-44";
    private static final String ML_DSA_65 = "ML-DSA-65";
    private static final String ML_DSA_87 = "ML-DSA-87";
    private Key compositeKey;
    private final ASN1ObjectIdentifier algorithm;
    private final Signature[] componentSignatures;
    private final byte[] domain;
    private final Digest preHashDigest;
    private final byte[] hashOID;
    private final JcaJceHelper helper;
    private ContextParameterSpec contextSpec;
    private AlgorithmParameters engineParams;
    private boolean unprimed;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/compositesignatures/SignatureSpi$HashMLDSA44_ECDSA_P256_SHA256.class */
    public static final class HashMLDSA44_ECDSA_P256_SHA256 extends SignatureSpi {
        public HashMLDSA44_ECDSA_P256_SHA256() {
            super(MiscObjectIdentifiers.id_HashMLDSA44_ECDSA_P256_SHA256, new SHA256Digest(), NISTObjectIdentifiers.id_sha256);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/compositesignatures/SignatureSpi$HashMLDSA44_Ed25519_SHA512.class */
    public static final class HashMLDSA44_Ed25519_SHA512 extends SignatureSpi {
        public HashMLDSA44_Ed25519_SHA512() {
            super(MiscObjectIdentifiers.id_HashMLDSA44_Ed25519_SHA512, new SHA512Digest(), NISTObjectIdentifiers.id_sha512);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/compositesignatures/SignatureSpi$HashMLDSA44_RSA2048_PKCS15_SHA256.class */
    public static final class HashMLDSA44_RSA2048_PKCS15_SHA256 extends SignatureSpi {
        public HashMLDSA44_RSA2048_PKCS15_SHA256() {
            super(MiscObjectIdentifiers.id_HashMLDSA44_RSA2048_PKCS15_SHA256, new SHA256Digest(), NISTObjectIdentifiers.id_sha256);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/compositesignatures/SignatureSpi$HashMLDSA44_RSA2048_PSS_SHA256.class */
    public static final class HashMLDSA44_RSA2048_PSS_SHA256 extends SignatureSpi {
        public HashMLDSA44_RSA2048_PSS_SHA256() {
            super(MiscObjectIdentifiers.id_HashMLDSA44_RSA2048_PSS_SHA256, new SHA256Digest(), NISTObjectIdentifiers.id_sha256);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/compositesignatures/SignatureSpi$HashMLDSA65_ECDSA_P384_SHA512.class */
    public static final class HashMLDSA65_ECDSA_P384_SHA512 extends SignatureSpi {
        public HashMLDSA65_ECDSA_P384_SHA512() {
            super(MiscObjectIdentifiers.id_HashMLDSA65_ECDSA_P384_SHA512, new SHA512Digest(), NISTObjectIdentifiers.id_sha512);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/compositesignatures/SignatureSpi$HashMLDSA65_ECDSA_brainpoolP256r1_SHA512.class */
    public static final class HashMLDSA65_ECDSA_brainpoolP256r1_SHA512 extends SignatureSpi {
        public HashMLDSA65_ECDSA_brainpoolP256r1_SHA512() {
            super(MiscObjectIdentifiers.id_HashMLDSA65_ECDSA_brainpoolP256r1_SHA512, new SHA512Digest(), NISTObjectIdentifiers.id_sha512);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/compositesignatures/SignatureSpi$HashMLDSA65_Ed25519_SHA512.class */
    public static final class HashMLDSA65_Ed25519_SHA512 extends SignatureSpi {
        public HashMLDSA65_Ed25519_SHA512() {
            super(MiscObjectIdentifiers.id_HashMLDSA65_Ed25519_SHA512, new SHA512Digest(), NISTObjectIdentifiers.id_sha512);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/compositesignatures/SignatureSpi$HashMLDSA65_RSA3072_PKCS15_SHA512.class */
    public static final class HashMLDSA65_RSA3072_PKCS15_SHA512 extends SignatureSpi {
        public HashMLDSA65_RSA3072_PKCS15_SHA512() {
            super(MiscObjectIdentifiers.id_HashMLDSA65_RSA3072_PKCS15_SHA512, new SHA512Digest(), NISTObjectIdentifiers.id_sha512);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/compositesignatures/SignatureSpi$HashMLDSA65_RSA3072_PSS_SHA512.class */
    public static final class HashMLDSA65_RSA3072_PSS_SHA512 extends SignatureSpi {
        public HashMLDSA65_RSA3072_PSS_SHA512() {
            super(MiscObjectIdentifiers.id_HashMLDSA65_RSA3072_PSS_SHA512, new SHA512Digest(), NISTObjectIdentifiers.id_sha512);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/compositesignatures/SignatureSpi$HashMLDSA65_RSA4096_PKCS15_SHA512.class */
    public static final class HashMLDSA65_RSA4096_PKCS15_SHA512 extends SignatureSpi {
        public HashMLDSA65_RSA4096_PKCS15_SHA512() {
            super(MiscObjectIdentifiers.id_HashMLDSA65_RSA4096_PKCS15_SHA512, new SHA512Digest(), NISTObjectIdentifiers.id_sha512);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/compositesignatures/SignatureSpi$HashMLDSA65_RSA4096_PSS_SHA512.class */
    public static final class HashMLDSA65_RSA4096_PSS_SHA512 extends SignatureSpi {
        public HashMLDSA65_RSA4096_PSS_SHA512() {
            super(MiscObjectIdentifiers.id_HashMLDSA65_RSA4096_PSS_SHA512, new SHA512Digest(), NISTObjectIdentifiers.id_sha512);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/compositesignatures/SignatureSpi$HashMLDSA87_ECDSA_P384_SHA512.class */
    public static final class HashMLDSA87_ECDSA_P384_SHA512 extends SignatureSpi {
        public HashMLDSA87_ECDSA_P384_SHA512() {
            super(MiscObjectIdentifiers.id_HashMLDSA87_ECDSA_P384_SHA512, new SHA512Digest(), NISTObjectIdentifiers.id_sha512);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/compositesignatures/SignatureSpi$HashMLDSA87_ECDSA_brainpoolP384r1_SHA512.class */
    public static final class HashMLDSA87_ECDSA_brainpoolP384r1_SHA512 extends SignatureSpi {
        public HashMLDSA87_ECDSA_brainpoolP384r1_SHA512() {
            super(MiscObjectIdentifiers.id_HashMLDSA87_ECDSA_brainpoolP384r1_SHA512, new SHA512Digest(), NISTObjectIdentifiers.id_sha512);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/compositesignatures/SignatureSpi$HashMLDSA87_Ed448_SHA512.class */
    public static final class HashMLDSA87_Ed448_SHA512 extends SignatureSpi {
        public HashMLDSA87_Ed448_SHA512() {
            super(MiscObjectIdentifiers.id_HashMLDSA87_Ed448_SHA512, new SHA512Digest(), NISTObjectIdentifiers.id_sha512);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/compositesignatures/SignatureSpi$MLDSA44_ECDSA_P256_SHA256.class */
    public static final class MLDSA44_ECDSA_P256_SHA256 extends SignatureSpi {
        public MLDSA44_ECDSA_P256_SHA256() {
            super(MiscObjectIdentifiers.id_MLDSA44_ECDSA_P256_SHA256);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/compositesignatures/SignatureSpi$MLDSA44_Ed25519_SHA512.class */
    public static final class MLDSA44_Ed25519_SHA512 extends SignatureSpi {
        public MLDSA44_Ed25519_SHA512() {
            super(MiscObjectIdentifiers.id_MLDSA44_Ed25519_SHA512);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/compositesignatures/SignatureSpi$MLDSA44_RSA2048_PKCS15_SHA256.class */
    public static final class MLDSA44_RSA2048_PKCS15_SHA256 extends SignatureSpi {
        public MLDSA44_RSA2048_PKCS15_SHA256() {
            super(MiscObjectIdentifiers.id_MLDSA44_RSA2048_PKCS15_SHA256);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/compositesignatures/SignatureSpi$MLDSA44_RSA2048_PSS_SHA256.class */
    public static final class MLDSA44_RSA2048_PSS_SHA256 extends SignatureSpi {
        public MLDSA44_RSA2048_PSS_SHA256() {
            super(MiscObjectIdentifiers.id_MLDSA44_RSA2048_PSS_SHA256);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/compositesignatures/SignatureSpi$MLDSA65_ECDSA_P384_SHA384.class */
    public static final class MLDSA65_ECDSA_P384_SHA384 extends SignatureSpi {
        public MLDSA65_ECDSA_P384_SHA384() {
            super(MiscObjectIdentifiers.id_MLDSA65_ECDSA_P384_SHA384);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/compositesignatures/SignatureSpi$MLDSA65_ECDSA_brainpoolP256r1_SHA256.class */
    public static final class MLDSA65_ECDSA_brainpoolP256r1_SHA256 extends SignatureSpi {
        public MLDSA65_ECDSA_brainpoolP256r1_SHA256() {
            super(MiscObjectIdentifiers.id_MLDSA65_ECDSA_brainpoolP256r1_SHA256);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/compositesignatures/SignatureSpi$MLDSA65_Ed25519_SHA512.class */
    public static final class MLDSA65_Ed25519_SHA512 extends SignatureSpi {
        public MLDSA65_Ed25519_SHA512() {
            super(MiscObjectIdentifiers.id_MLDSA65_Ed25519_SHA512);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/compositesignatures/SignatureSpi$MLDSA65_RSA3072_PKCS15_SHA256.class */
    public static final class MLDSA65_RSA3072_PKCS15_SHA256 extends SignatureSpi {
        public MLDSA65_RSA3072_PKCS15_SHA256() {
            super(MiscObjectIdentifiers.id_MLDSA65_RSA3072_PKCS15_SHA256);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/compositesignatures/SignatureSpi$MLDSA65_RSA3072_PSS_SHA256.class */
    public static final class MLDSA65_RSA3072_PSS_SHA256 extends SignatureSpi {
        public MLDSA65_RSA3072_PSS_SHA256() {
            super(MiscObjectIdentifiers.id_MLDSA65_RSA3072_PSS_SHA256);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/compositesignatures/SignatureSpi$MLDSA65_RSA4096_PKCS15_SHA384.class */
    public static final class MLDSA65_RSA4096_PKCS15_SHA384 extends SignatureSpi {
        public MLDSA65_RSA4096_PKCS15_SHA384() {
            super(MiscObjectIdentifiers.id_MLDSA65_RSA4096_PKCS15_SHA384);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/compositesignatures/SignatureSpi$MLDSA65_RSA4096_PSS_SHA384.class */
    public static final class MLDSA65_RSA4096_PSS_SHA384 extends SignatureSpi {
        public MLDSA65_RSA4096_PSS_SHA384() {
            super(MiscObjectIdentifiers.id_MLDSA65_RSA4096_PSS_SHA384);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/compositesignatures/SignatureSpi$MLDSA87_ECDSA_P384_SHA384.class */
    public static final class MLDSA87_ECDSA_P384_SHA384 extends SignatureSpi {
        public MLDSA87_ECDSA_P384_SHA384() {
            super(MiscObjectIdentifiers.id_MLDSA87_ECDSA_P384_SHA384);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/compositesignatures/SignatureSpi$MLDSA87_ECDSA_brainpoolP384r1_SHA384.class */
    public static final class MLDSA87_ECDSA_brainpoolP384r1_SHA384 extends SignatureSpi {
        public MLDSA87_ECDSA_brainpoolP384r1_SHA384() {
            super(MiscObjectIdentifiers.id_MLDSA87_ECDSA_brainpoolP384r1_SHA384);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/compositesignatures/SignatureSpi$MLDSA87_Ed448_SHA512.class */
    public static final class MLDSA87_Ed448_SHA512 extends SignatureSpi {
        public MLDSA87_Ed448_SHA512() {
            super(MiscObjectIdentifiers.id_MLDSA87_Ed448_SHA512);
        }
    }

    SignatureSpi(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        this(aSN1ObjectIdentifier, null, null);
    }

    SignatureSpi(ASN1ObjectIdentifier aSN1ObjectIdentifier, Digest digest, ASN1ObjectIdentifier aSN1ObjectIdentifier2) {
        this.helper = new BCJcaJceHelper();
        this.engineParams = null;
        this.unprimed = true;
        this.algorithm = aSN1ObjectIdentifier;
        this.preHashDigest = digest;
        String[] pairing = CompositeIndex.getPairing(aSN1ObjectIdentifier);
        if (digest != null) {
            try {
                this.hashOID = aSN1ObjectIdentifier2.getEncoded();
            } catch (IOException e) {
                throw new IllegalStateException("unable to encode domain value");
            }
        } else {
            this.hashOID = null;
        }
        try {
            this.domain = aSN1ObjectIdentifier.getEncoded();
            this.componentSignatures = new Signature[pairing.length];
            for (int i = 0; i != this.componentSignatures.length; i++) {
                try {
                    this.componentSignatures[i] = Signature.getInstance(pairing[i], BouncyCastleProvider.PROVIDER_NAME);
                } catch (GeneralSecurityException e2) {
                    throw Exceptions.illegalStateException(e2.getMessage(), e2);
                }
            }
        } catch (IOException e3) {
            throw new IllegalStateException("unable to encode domain value");
        }
    }

    @Override // java.security.SignatureSpi
    protected void engineInitVerify(PublicKey publicKey) throws InvalidKeyException {
        if (!(publicKey instanceof CompositePublicKey)) {
            throw new InvalidKeyException("Public key is not composite.");
        }
        this.compositeKey = publicKey;
        if (!((CompositePublicKey) this.compositeKey).getAlgorithmIdentifier().equals((ASN1Primitive) this.algorithm)) {
            throw new InvalidKeyException("Provided composite public key cannot be used with the composite signature algorithm.");
        }
        sigInitVerify();
    }

    private void sigInitVerify() throws InvalidKeyException {
        CompositePublicKey compositePublicKey = (CompositePublicKey) this.compositeKey;
        for (int i = 0; i < this.componentSignatures.length; i++) {
            this.componentSignatures[i].initVerify(compositePublicKey.getPublicKeys().get(i));
        }
        this.unprimed = true;
    }

    @Override // java.security.SignatureSpi
    protected void engineInitSign(PrivateKey privateKey) throws InvalidKeyException {
        if (!(privateKey instanceof CompositePrivateKey)) {
            throw new InvalidKeyException("Private key is not composite.");
        }
        this.compositeKey = privateKey;
        if (!((CompositePrivateKey) privateKey).getAlgorithmIdentifier().equals((ASN1Primitive) this.algorithm)) {
            throw new InvalidKeyException("Provided composite private key cannot be used with the composite signature algorithm.");
        }
        sigInitSign();
    }

    private void sigInitSign() throws InvalidKeyException {
        CompositePrivateKey compositePrivateKey = (CompositePrivateKey) this.compositeKey;
        for (int i = 0; i < this.componentSignatures.length; i++) {
            this.componentSignatures[i].initSign(compositePrivateKey.getPrivateKeys().get(i));
        }
        this.unprimed = true;
    }

    private void baseSigInit() throws SignatureException {
        try {
            this.componentSignatures[0].setParameter(new ContextParameterSpec(this.domain));
            if (this.preHashDigest == null) {
                for (int i = 0; i < this.componentSignatures.length; i++) {
                    Signature signature = this.componentSignatures[i];
                    signature.update(this.domain);
                    if (this.contextSpec == null) {
                        signature.update((byte) 0);
                    } else {
                        byte[] context = this.contextSpec.getContext();
                        signature.update((byte) context.length);
                        signature.update(context);
                    }
                }
            }
            this.unprimed = false;
        } catch (InvalidAlgorithmParameterException e) {
            throw new IllegalStateException("unable to set context on ML-DSA");
        }
    }

    @Override // java.security.SignatureSpi
    protected void engineUpdate(byte b) throws SignatureException {
        if (this.unprimed) {
            baseSigInit();
        }
        if (this.preHashDigest != null) {
            this.preHashDigest.update(b);
            return;
        }
        for (int i = 0; i < this.componentSignatures.length; i++) {
            this.componentSignatures[i].update(b);
        }
    }

    @Override // java.security.SignatureSpi
    protected void engineUpdate(byte[] bArr, int i, int i2) throws SignatureException {
        if (this.unprimed) {
            baseSigInit();
        }
        if (this.preHashDigest != null) {
            this.preHashDigest.update(bArr, i, i2);
            return;
        }
        for (int i3 = 0; i3 < this.componentSignatures.length; i3++) {
            this.componentSignatures[i3].update(bArr, i, i2);
        }
    }

    @Override // java.security.SignatureSpi
    protected byte[] engineSign() throws SignatureException {
        if (this.preHashDigest != null) {
            processPreHashedMessage();
        }
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        for (int i = 0; i < this.componentSignatures.length; i++) {
            try {
                aSN1EncodableVector.add(new DERBitString(this.componentSignatures[i].sign()));
            } catch (IOException e) {
                throw new SignatureException(e.getMessage());
            }
        }
        return new DERSequence(aSN1EncodableVector).getEncoded("DER");
    }

    private void processPreHashedMessage() throws SignatureException {
        byte[] bArr = new byte[this.preHashDigest.getDigestSize()];
        this.preHashDigest.doFinal(bArr, 0);
        for (int i = 0; i < this.componentSignatures.length; i++) {
            Signature signature = this.componentSignatures[i];
            signature.update(this.domain, 0, this.domain.length);
            if (this.contextSpec == null) {
                signature.update((byte) 0);
            } else {
                byte[] context = this.contextSpec.getContext();
                signature.update((byte) context.length);
                signature.update(context);
            }
            signature.update(this.hashOID, 0, this.hashOID.length);
            signature.update(bArr, 0, bArr.length);
        }
    }

    @Override // java.security.SignatureSpi
    protected boolean engineVerify(byte[] bArr) throws SignatureException {
        ASN1Sequence dERSequence = DERSequence.getInstance(bArr);
        if (dERSequence.size() != this.componentSignatures.length) {
            return false;
        }
        if (this.preHashDigest != null && this.preHashDigest != null) {
            processPreHashedMessage();
        }
        boolean z = false;
        for (int i = 0; i < this.componentSignatures.length; i++) {
            if (!this.componentSignatures[i].verify(ASN1BitString.getInstance(dERSequence.getObjectAt(i)).getOctets())) {
                z = true;
            }
        }
        return !z;
    }

    @Override // java.security.SignatureSpi
    protected void engineSetParameter(AlgorithmParameterSpec algorithmParameterSpec) throws InvalidAlgorithmParameterException {
        if (!this.unprimed) {
            throw new InvalidAlgorithmParameterException("attempt to set parameter after update");
        }
        if (!(algorithmParameterSpec instanceof ContextParameterSpec)) {
            throw new InvalidAlgorithmParameterException("unknown parameterSpec passed to composite signature");
        }
        this.contextSpec = (ContextParameterSpec) algorithmParameterSpec;
        try {
            if (this.compositeKey instanceof PublicKey) {
                sigInitVerify();
            } else {
                sigInitSign();
            }
        } catch (InvalidKeyException e) {
            throw new InvalidAlgorithmParameterException("keys invalid on reset: " + e.getMessage(), e);
        }
    }

    private void setSigParameter(Signature signature, String str, List<String> list, List<AlgorithmParameterSpec> list2) throws InvalidAlgorithmParameterException {
        for (int i = 0; i != list.size(); i++) {
            getCanonicalName(list.get(i));
            if (list.get(i).equals(str)) {
                signature.setParameter(list2.get(i));
            }
        }
    }

    private String getCanonicalName(String str) {
        String str2 = canonicalNames.get(str);
        return str2 != null ? str2 : str;
    }

    @Override // java.security.SignatureSpi
    protected void engineSetParameter(String str, Object obj) throws InvalidParameterException {
        throw new UnsupportedOperationException("engineSetParameter unsupported");
    }

    @Override // java.security.SignatureSpi
    protected Object engineGetParameter(String str) throws InvalidParameterException {
        throw new UnsupportedOperationException("engineGetParameter unsupported");
    }

    @Override // java.security.SignatureSpi
    protected final AlgorithmParameters engineGetParameters() {
        if (this.engineParams == null && this.contextSpec != null) {
            try {
                this.engineParams = this.helper.createAlgorithmParameters("CONTEXT");
                this.engineParams.init(this.contextSpec);
            } catch (Exception e) {
                throw Exceptions.illegalStateException(e.toString(), e);
            }
        }
        return this.engineParams;
    }

    static {
        canonicalNames.put("MLDSA44", ML_DSA_44);
        canonicalNames.put("MLDSA65", ML_DSA_65);
        canonicalNames.put("MLDSA87", ML_DSA_87);
        canonicalNames.put(NISTObjectIdentifiers.id_ml_dsa_44.getId(), ML_DSA_44);
        canonicalNames.put(NISTObjectIdentifiers.id_ml_dsa_65.getId(), ML_DSA_65);
        canonicalNames.put(NISTObjectIdentifiers.id_ml_dsa_87.getId(), ML_DSA_87);
    }
}
