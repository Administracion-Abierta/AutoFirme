package org.spongycastle.cert.crmf.jcajce;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.Provider;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.cert.crmf.CRMFException;
import org.spongycastle.cert.crmf.PKMACValuesCalculator;
import org.spongycastle.jcajce.util.DefaultJcaJceHelper;
import org.spongycastle.jcajce.util.NamedJcaJceHelper;
import org.spongycastle.jcajce.util.ProviderJcaJceHelper;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/cert/crmf/jcajce/JcePKMACValuesCalculator.class */
public class JcePKMACValuesCalculator implements PKMACValuesCalculator {
    private MessageDigest digest;
    private Mac mac;
    private CRMFHelper helper = new CRMFHelper(new DefaultJcaJceHelper());

    public JcePKMACValuesCalculator setProvider(Provider provider) {
        this.helper = new CRMFHelper(new ProviderJcaJceHelper(provider));
        return this;
    }

    public JcePKMACValuesCalculator setProvider(String providerName) {
        this.helper = new CRMFHelper(new NamedJcaJceHelper(providerName));
        return this;
    }

    @Override // org.spongycastle.cert.crmf.PKMACValuesCalculator
    public void setup(AlgorithmIdentifier digAlg, AlgorithmIdentifier macAlg) throws CRMFException {
        this.digest = this.helper.createDigest(digAlg.getAlgorithm());
        this.mac = this.helper.createMac(macAlg.getAlgorithm());
    }

    @Override // org.spongycastle.cert.crmf.PKMACValuesCalculator
    public byte[] calculateDigest(byte[] data) {
        return this.digest.digest(data);
    }

    @Override // org.spongycastle.cert.crmf.PKMACValuesCalculator
    public byte[] calculateMac(byte[] pwd, byte[] data) throws CRMFException {
        try {
            this.mac.init(new SecretKeySpec(pwd, this.mac.getAlgorithm()));
            return this.mac.doFinal(data);
        } catch (GeneralSecurityException e) {
            throw new CRMFException("failure in setup: " + e.getMessage(), e);
        }
    }
}
