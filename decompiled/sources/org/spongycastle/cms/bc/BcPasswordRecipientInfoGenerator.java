package org.spongycastle.cms.bc;

import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.ASN1OctetString;
import org.spongycastle.asn1.pkcs.PBKDF2Params;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.cms.CMSException;
import org.spongycastle.cms.PasswordRecipientInfoGenerator;
import org.spongycastle.crypto.PBEParametersGenerator;
import org.spongycastle.crypto.Wrapper;
import org.spongycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.spongycastle.crypto.params.KeyParameter;
import org.spongycastle.crypto.params.ParametersWithIV;
import org.spongycastle.operator.GenericKey;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cms/bc/BcPasswordRecipientInfoGenerator.class */
public class BcPasswordRecipientInfoGenerator extends PasswordRecipientInfoGenerator {
    public BcPasswordRecipientInfoGenerator(ASN1ObjectIdentifier kekAlgorithm, char[] password) {
        super(kekAlgorithm, password);
    }

    @Override // org.spongycastle.cms.PasswordRecipientInfoGenerator
    protected byte[] calculateDerivedKey(int schemeID, AlgorithmIdentifier derivationAlgorithm, int keySize) throws CMSException {
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

    @Override // org.spongycastle.cms.PasswordRecipientInfoGenerator
    public byte[] generateEncryptedBytes(AlgorithmIdentifier keyEncryptionAlgorithm, byte[] derivedKey, GenericKey contentEncryptionKey) throws CMSException {
        byte[] contentEncryptionKeySpec = ((KeyParameter) CMSUtils.getBcKey(contentEncryptionKey)).getKey();
        Wrapper keyEncryptionCipher = EnvelopedDataHelper.createRFC3211Wrapper(keyEncryptionAlgorithm.getAlgorithm());
        keyEncryptionCipher.init(true, new ParametersWithIV(new KeyParameter(derivedKey), ASN1OctetString.getInstance(keyEncryptionAlgorithm.getParameters()).getOctets()));
        return keyEncryptionCipher.wrap(contentEncryptionKeySpec, 0, contentEncryptionKeySpec.length);
    }
}
