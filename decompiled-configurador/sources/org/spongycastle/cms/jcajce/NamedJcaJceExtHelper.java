package org.spongycastle.cms.jcajce;

import java.security.PrivateKey;
import javax.crypto.SecretKey;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.jcajce.util.NamedJcaJceHelper;
import org.spongycastle.operator.SymmetricKeyUnwrapper;
import org.spongycastle.operator.jcajce.JceAsymmetricKeyUnwrapper;
import org.spongycastle.operator.jcajce.JceKTSKeyUnwrapper;
import org.spongycastle.operator.jcajce.JceSymmetricKeyUnwrapper;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/cms/jcajce/NamedJcaJceExtHelper.class */
class NamedJcaJceExtHelper extends NamedJcaJceHelper implements JcaJceExtHelper {
    public NamedJcaJceExtHelper(String providerName) {
        super(providerName);
    }

    @Override // org.spongycastle.cms.jcajce.JcaJceExtHelper
    public JceAsymmetricKeyUnwrapper createAsymmetricUnwrapper(AlgorithmIdentifier keyEncryptionAlgorithm, PrivateKey keyEncryptionKey) {
        return new JceAsymmetricKeyUnwrapper(keyEncryptionAlgorithm, keyEncryptionKey).setProvider(this.providerName);
    }

    @Override // org.spongycastle.cms.jcajce.JcaJceExtHelper
    public JceKTSKeyUnwrapper createAsymmetricUnwrapper(AlgorithmIdentifier keyEncryptionAlgorithm, PrivateKey keyEncryptionKey, byte[] partyUInfo, byte[] partyVInfo) {
        return new JceKTSKeyUnwrapper(keyEncryptionAlgorithm, keyEncryptionKey, partyUInfo, partyVInfo).setProvider(this.providerName);
    }

    @Override // org.spongycastle.cms.jcajce.JcaJceExtHelper
    public SymmetricKeyUnwrapper createSymmetricUnwrapper(AlgorithmIdentifier keyEncryptionAlgorithm, SecretKey keyEncryptionKey) {
        return new JceSymmetricKeyUnwrapper(keyEncryptionAlgorithm, keyEncryptionKey).setProvider(this.providerName);
    }
}
