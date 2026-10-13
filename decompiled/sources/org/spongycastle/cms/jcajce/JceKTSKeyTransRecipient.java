package org.spongycastle.cms.jcajce;

import java.io.IOException;
import java.security.Key;
import java.security.PrivateKey;
import java.security.Provider;
import java.util.HashMap;
import java.util.Map;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.DEROctetString;
import org.spongycastle.asn1.cms.IssuerAndSerialNumber;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.cms.CMSException;
import org.spongycastle.cms.KeyTransRecipient;
import org.spongycastle.cms.KeyTransRecipientId;
import org.spongycastle.operator.OperatorException;
import org.spongycastle.operator.jcajce.JceKTSKeyUnwrapper;
import org.spongycastle.util.encoders.Hex;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cms/jcajce/JceKTSKeyTransRecipient.class */
public abstract class JceKTSKeyTransRecipient implements KeyTransRecipient {
    private static final byte[] ANONYMOUS_SENDER = Hex.decode("0c14416e6f6e796d6f75732053656e64657220202020");
    private final byte[] partyVInfo;
    private PrivateKey recipientKey;
    protected EnvelopedDataHelper helper = new EnvelopedDataHelper(new DefaultJcaJceExtHelper());
    protected EnvelopedDataHelper contentHelper = this.helper;
    protected Map extraMappings = new HashMap();
    protected boolean validateKeySize = false;
    protected boolean unwrappedKeyMustBeEncodable;

    public JceKTSKeyTransRecipient(PrivateKey recipientKey, byte[] partyVInfo) {
        this.recipientKey = recipientKey;
        this.partyVInfo = partyVInfo;
    }

    public JceKTSKeyTransRecipient setProvider(Provider provider) {
        this.helper = new EnvelopedDataHelper(new ProviderJcaJceExtHelper(provider));
        this.contentHelper = this.helper;
        return this;
    }

    public JceKTSKeyTransRecipient setProvider(String providerName) {
        this.helper = new EnvelopedDataHelper(new NamedJcaJceExtHelper(providerName));
        this.contentHelper = this.helper;
        return this;
    }

    public JceKTSKeyTransRecipient setAlgorithmMapping(ASN1ObjectIdentifier algorithm, String algorithmName) {
        this.extraMappings.put(algorithm, algorithmName);
        return this;
    }

    public JceKTSKeyTransRecipient setContentProvider(Provider provider) {
        this.contentHelper = CMSUtils.createContentHelper(provider);
        return this;
    }

    public JceKTSKeyTransRecipient setContentProvider(String providerName) {
        this.contentHelper = CMSUtils.createContentHelper(providerName);
        return this;
    }

    public JceKTSKeyTransRecipient setKeySizeValidation(boolean doValidate) {
        this.validateKeySize = doValidate;
        return this;
    }

    protected Key extractSecretKey(AlgorithmIdentifier keyEncryptionAlgorithm, AlgorithmIdentifier encryptedKeyAlgorithm, byte[] encryptedEncryptionKey) throws CMSException {
        JceKTSKeyUnwrapper unwrapper = this.helper.createAsymmetricUnwrapper(keyEncryptionAlgorithm, this.recipientKey, ANONYMOUS_SENDER, this.partyVInfo);
        try {
            Key key = this.helper.getJceKey(encryptedKeyAlgorithm.getAlgorithm(), unwrapper.generateUnwrappedKey(encryptedKeyAlgorithm, encryptedEncryptionKey));
            if (this.validateKeySize) {
                this.helper.keySizeCheck(encryptedKeyAlgorithm, key);
            }
            return key;
        } catch (OperatorException e) {
            throw new CMSException("exception unwrapping key: " + e.getMessage(), e);
        }
    }

    protected static byte[] getPartyVInfoFromRID(KeyTransRecipientId recipientId) throws IOException {
        if (recipientId.getSerialNumber() != null) {
            return new IssuerAndSerialNumber(recipientId.getIssuer(), recipientId.getSerialNumber()).getEncoded("DER");
        }
        return new DEROctetString(recipientId.getSubjectKeyIdentifier()).getEncoded();
    }
}
