package org.bouncycastle.cms.jcajce;

import java.security.Key;
import java.security.PrivateKey;
import java.security.Provider;
import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.cms.KEMRecipientInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.KEMRecipient;
import org.bouncycastle.operator.OperatorException;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cms/jcajce/JceKEMRecipient.class */
public abstract class JceKEMRecipient implements KEMRecipient {
    private PrivateKey recipientKey;
    protected EnvelopedDataHelper helper = new EnvelopedDataHelper(new DefaultJcaJceExtHelper());
    protected EnvelopedDataHelper contentHelper = this.helper;
    protected Map extraMappings = new HashMap();
    protected boolean validateKeySize = false;
    protected boolean unwrappedKeyMustBeEncodable;

    public JceKEMRecipient(PrivateKey privateKey) {
        this.recipientKey = CMSUtils.cleanPrivateKey(privateKey);
    }

    public JceKEMRecipient setProvider(Provider provider) {
        this.helper = new EnvelopedDataHelper(new ProviderJcaJceExtHelper(provider));
        this.contentHelper = this.helper;
        return this;
    }

    public JceKEMRecipient setProvider(String str) {
        this.helper = new EnvelopedDataHelper(new NamedJcaJceExtHelper(str));
        this.contentHelper = this.helper;
        return this;
    }

    public JceKEMRecipient setAlgorithmMapping(ASN1ObjectIdentifier aSN1ObjectIdentifier, String str) {
        this.extraMappings.put(aSN1ObjectIdentifier, str);
        return this;
    }

    public JceKEMRecipient setContentProvider(Provider provider) {
        this.contentHelper = CMSUtils.createContentHelper(provider);
        return this;
    }

    public JceKEMRecipient setMustProduceEncodableUnwrappedKey(boolean z) {
        this.unwrappedKeyMustBeEncodable = z;
        return this;
    }

    public JceKEMRecipient setContentProvider(String str) {
        this.contentHelper = CMSUtils.createContentHelper(str);
        return this;
    }

    public JceKEMRecipient setKeySizeValidation(boolean z) {
        this.validateKeySize = z;
        return this;
    }

    protected Key extractSecretKey(AlgorithmIdentifier algorithmIdentifier, AlgorithmIdentifier algorithmIdentifier2, byte[] bArr) throws CMSException {
        KEMRecipientInfo.getInstance(algorithmIdentifier.getParameters());
        JceCMSKEMKeyUnwrapper jceCMSKEMKeyUnwrapper = (JceCMSKEMKeyUnwrapper) this.helper.createKEMUnwrapper(algorithmIdentifier, this.recipientKey);
        if (!this.extraMappings.isEmpty()) {
            for (ASN1ObjectIdentifier aSN1ObjectIdentifier : this.extraMappings.keySet()) {
                jceCMSKEMKeyUnwrapper.setAlgorithmMapping(aSN1ObjectIdentifier, (String) this.extraMappings.get(aSN1ObjectIdentifier));
            }
        }
        try {
            Key jceKey = this.helper.getJceKey(algorithmIdentifier2, jceCMSKEMKeyUnwrapper.generateUnwrappedKey(algorithmIdentifier2, bArr));
            if (this.validateKeySize) {
                this.helper.keySizeCheck(algorithmIdentifier2, jceKey);
            }
            return jceKey;
        } catch (OperatorException e) {
            throw new CMSException("exception unwrapping key: " + e.getMessage(), e);
        }
    }
}
