package org.spongycastle.cms;

import java.security.SecureRandom;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.ASN1OctetString;
import org.spongycastle.asn1.DEROctetString;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.asn1.cms.PasswordRecipientInfo;
import org.spongycastle.asn1.cms.RecipientInfo;
import org.spongycastle.asn1.pkcs.PBKDF2Params;
import org.spongycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.operator.GenericKey;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cms/PasswordRecipientInfoGenerator.class */
public abstract class PasswordRecipientInfoGenerator implements RecipientInfoGenerator {
    protected char[] password;
    private AlgorithmIdentifier keyDerivationAlgorithm;
    private ASN1ObjectIdentifier kekAlgorithm;
    private SecureRandom random;
    private int schemeID;
    private int keySize;
    private int blockSize;
    private PasswordRecipient.PRF prf;
    private byte[] salt;
    private int iterationCount;

    protected abstract byte[] calculateDerivedKey(int i, AlgorithmIdentifier algorithmIdentifier, int i2) throws CMSException;

    protected abstract byte[] generateEncryptedBytes(AlgorithmIdentifier algorithmIdentifier, byte[] bArr, GenericKey genericKey) throws CMSException;

    protected PasswordRecipientInfoGenerator(ASN1ObjectIdentifier kekAlgorithm, char[] password) {
        this(kekAlgorithm, password, getKeySize(kekAlgorithm), ((Integer) PasswordRecipientInformation.BLOCKSIZES.get(kekAlgorithm)).intValue());
    }

    protected PasswordRecipientInfoGenerator(ASN1ObjectIdentifier kekAlgorithm, char[] password, int keySize, int blockSize) {
        this.password = password;
        this.schemeID = 1;
        this.kekAlgorithm = kekAlgorithm;
        this.keySize = keySize;
        this.blockSize = blockSize;
        this.prf = PasswordRecipient.PRF.HMacSHA1;
        this.iterationCount = 1024;
    }

    private static int getKeySize(ASN1ObjectIdentifier kekAlgorithm) {
        Integer size = (Integer) PasswordRecipientInformation.KEYSIZES.get(kekAlgorithm);
        if (size == null) {
            throw new IllegalArgumentException("cannot find key size for algorithm: " + kekAlgorithm);
        }
        return size.intValue();
    }

    public PasswordRecipientInfoGenerator setPasswordConversionScheme(int schemeID) {
        this.schemeID = schemeID;
        return this;
    }

    public PasswordRecipientInfoGenerator setPRF(PasswordRecipient.PRF prf) {
        this.prf = prf;
        return this;
    }

    public PasswordRecipientInfoGenerator setSaltAndIterationCount(byte[] salt, int iterationCount) {
        this.salt = Arrays.clone(salt);
        this.iterationCount = iterationCount;
        return this;
    }

    public PasswordRecipientInfoGenerator setSecureRandom(SecureRandom random) {
        this.random = random;
        return this;
    }

    @Override // org.spongycastle.cms.RecipientInfoGenerator
    public RecipientInfo generate(GenericKey contentEncryptionKey) throws CMSException {
        byte[] iv = new byte[this.blockSize];
        if (this.random == null) {
            this.random = new SecureRandom();
        }
        this.random.nextBytes(iv);
        if (this.salt == null) {
            this.salt = new byte[20];
            this.random.nextBytes(this.salt);
        }
        this.keyDerivationAlgorithm = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_PBKDF2, new PBKDF2Params(this.salt, this.iterationCount, this.prf.prfAlgID));
        byte[] derivedKey = calculateDerivedKey(this.schemeID, this.keyDerivationAlgorithm, this.keySize);
        AlgorithmIdentifier kekAlgorithmId = new AlgorithmIdentifier(this.kekAlgorithm, new DEROctetString(iv));
        byte[] encryptedKeyBytes = generateEncryptedBytes(kekAlgorithmId, derivedKey, contentEncryptionKey);
        ASN1OctetString encryptedKey = new DEROctetString(encryptedKeyBytes);
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(this.kekAlgorithm);
        v.add(new DEROctetString(iv));
        AlgorithmIdentifier keyEncryptionAlgorithm = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_alg_PWRI_KEK, new DERSequence(v));
        return new RecipientInfo(new PasswordRecipientInfo(this.keyDerivationAlgorithm, keyEncryptionAlgorithm, encryptedKey));
    }
}
