package org.spongycastle.cms.bc;

import org.spongycastle.asn1.ASN1OctetString;
import org.spongycastle.asn1.pkcs.PBKDF2Params;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.cms.CMSException;
import org.spongycastle.cms.PasswordRecipient;
import org.spongycastle.crypto.InvalidCipherTextException;
import org.spongycastle.crypto.PBEParametersGenerator;
import org.spongycastle.crypto.Wrapper;
import org.spongycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.spongycastle.crypto.params.KeyParameter;
import org.spongycastle.crypto.params.ParametersWithIV;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cms/bc/BcPasswordRecipient.class */
public abstract class BcPasswordRecipient implements PasswordRecipient {
    private final char[] password;
    private int schemeID = 1;

    BcPasswordRecipient(char[] password) {
        this.password = password;
    }

    public BcPasswordRecipient setPasswordConversionScheme(int schemeID) {
        this.schemeID = schemeID;
        return this;
    }

    protected KeyParameter extractSecretKey(AlgorithmIdentifier keyEncryptionAlgorithm, AlgorithmIdentifier contentEncryptionAlgorithm, byte[] derivedKey, byte[] encryptedContentEncryptionKey) throws CMSException {
        Wrapper keyEncryptionCipher = EnvelopedDataHelper.createRFC3211Wrapper(keyEncryptionAlgorithm.getAlgorithm());
        keyEncryptionCipher.init(false, new ParametersWithIV(new KeyParameter(derivedKey), ASN1OctetString.getInstance(keyEncryptionAlgorithm.getParameters()).getOctets()));
        try {
            return new KeyParameter(keyEncryptionCipher.unwrap(encryptedContentEncryptionKey, 0, encryptedContentEncryptionKey.length));
        } catch (InvalidCipherTextException e) {
            throw new CMSException("unable to unwrap key: " + e.getMessage(), e);
        }
    }

    @Override // org.spongycastle.cms.PasswordRecipient
    public byte[] calculateDerivedKey(int schemeID, AlgorithmIdentifier derivationAlgorithm, int keySize) throws CMSException {
        PBKDF2Params params = PBKDF2Params.getInstance(derivationAlgorithm.getParameters());
        byte[] encodedPassword = schemeID == 0 ? PBEParametersGenerator.PKCS5PasswordToBytes(this.password) : PBEParametersGenerator.PKCS5PasswordToUTF8Bytes(this.password);
        try {
            PKCS5S2ParametersGenerator gen = new PKCS5S2ParametersGenerator(EnvelopedDataHelper.getPRF(params.getPrf()));
            gen.init(encodedPassword, params.getSalt(), params.getIterationCount().intValue());
            return ((KeyParameter) gen.generateDerivedParameters(keySize)).getKey();
        } catch (Exception e) {
            throw new CMSException("exception creating derived key: " + e.getMessage(), e);
        }
    }

    @Override // org.spongycastle.cms.PasswordRecipient
    public int getPasswordConversionScheme() {
        return this.schemeID;
    }

    @Override // org.spongycastle.cms.PasswordRecipient
    public char[] getPassword() {
        return this.password;
    }
}
