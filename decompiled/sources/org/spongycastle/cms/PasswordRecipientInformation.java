package org.spongycastle.cms;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.cms.PasswordRecipientInfo;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.util.Integers;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cms/PasswordRecipientInformation.class */
public class PasswordRecipientInformation extends RecipientInformation {
    static Map KEYSIZES = new HashMap();
    static Map BLOCKSIZES = new HashMap();
    private PasswordRecipientInfo info;

    static {
        BLOCKSIZES.put(CMSAlgorithm.DES_EDE3_CBC, Integers.valueOf(8));
        BLOCKSIZES.put(CMSAlgorithm.AES128_CBC, Integers.valueOf(16));
        BLOCKSIZES.put(CMSAlgorithm.AES192_CBC, Integers.valueOf(16));
        BLOCKSIZES.put(CMSAlgorithm.AES256_CBC, Integers.valueOf(16));
        KEYSIZES.put(CMSAlgorithm.DES_EDE3_CBC, Integers.valueOf(192));
        KEYSIZES.put(CMSAlgorithm.AES128_CBC, Integers.valueOf(128));
        KEYSIZES.put(CMSAlgorithm.AES192_CBC, Integers.valueOf(192));
        KEYSIZES.put(CMSAlgorithm.AES256_CBC, Integers.valueOf(256));
    }

    PasswordRecipientInformation(PasswordRecipientInfo info, AlgorithmIdentifier messageAlgorithm, CMSSecureReadable secureReadable, AuthAttributesProvider additionalData) {
        super(info.getKeyEncryptionAlgorithm(), messageAlgorithm, secureReadable, additionalData);
        this.info = info;
        this.rid = new PasswordRecipientId();
    }

    public String getKeyDerivationAlgOID() {
        if (this.info.getKeyDerivationAlgorithm() != null) {
            return this.info.getKeyDerivationAlgorithm().getAlgorithm().getId();
        }
        return null;
    }

    public byte[] getKeyDerivationAlgParams() {
        ASN1Encodable params;
        try {
            if (this.info.getKeyDerivationAlgorithm() != null && (params = this.info.getKeyDerivationAlgorithm().getParameters()) != null) {
                return params.toASN1Primitive().getEncoded();
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException("exception getting encryption parameters " + e);
        }
    }

    public AlgorithmIdentifier getKeyDerivationAlgorithm() {
        return this.info.getKeyDerivationAlgorithm();
    }

    @Override // org.spongycastle.cms.RecipientInformation
    protected RecipientOperator getRecipientOperator(Recipient recipient) throws IOException, CMSException {
        PasswordRecipient pbeRecipient = (PasswordRecipient) recipient;
        AlgorithmIdentifier kekAlg = AlgorithmIdentifier.getInstance(this.info.getKeyEncryptionAlgorithm());
        AlgorithmIdentifier kekAlgParams = AlgorithmIdentifier.getInstance(kekAlg.getParameters());
        int keySize = ((Integer) KEYSIZES.get(kekAlgParams.getAlgorithm())).intValue();
        byte[] derivedKey = pbeRecipient.calculateDerivedKey(pbeRecipient.getPasswordConversionScheme(), getKeyDerivationAlgorithm(), keySize);
        return pbeRecipient.getRecipientOperator(kekAlgParams, this.messageAlgorithm, derivedKey, this.info.getEncryptedKey().getOctets());
    }
}
