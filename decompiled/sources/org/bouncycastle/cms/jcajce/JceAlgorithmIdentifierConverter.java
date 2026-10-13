package org.bouncycastle.cms.jcajce;

import java.security.AlgorithmParameters;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.SecureRandom;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSException;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cms/jcajce/JceAlgorithmIdentifierConverter.class */
public class JceAlgorithmIdentifierConverter {
    private EnvelopedDataHelper helper = new EnvelopedDataHelper(new DefaultJcaJceExtHelper());
    private SecureRandom random;

    public JceAlgorithmIdentifierConverter setProvider(Provider provider) {
        this.helper = new EnvelopedDataHelper(new ProviderJcaJceExtHelper(provider));
        return this;
    }

    public JceAlgorithmIdentifierConverter setProvider(String str) {
        this.helper = new EnvelopedDataHelper(new NamedJcaJceExtHelper(str));
        return this;
    }

    public AlgorithmParameters getAlgorithmParameters(AlgorithmIdentifier algorithmIdentifier) throws CMSException {
        if (algorithmIdentifier.getParameters() == null) {
            return null;
        }
        try {
            AlgorithmParameters algorithmParametersCreateAlgorithmParameters = this.helper.createAlgorithmParameters(algorithmIdentifier.getAlgorithm());
            CMSUtils.loadParameters(algorithmParametersCreateAlgorithmParameters, algorithmIdentifier.getParameters());
            return algorithmParametersCreateAlgorithmParameters;
        } catch (NoSuchAlgorithmException e) {
            throw new CMSException("can't find parameters for algorithm", e);
        } catch (NoSuchProviderException e2) {
            throw new CMSException("can't find provider for algorithm", e2);
        }
    }
}
