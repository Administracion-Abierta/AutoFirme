package org.bouncycastle.jcajce.provider.asymmetric;

import org.apache.xml.security.keys.content.x509.XMLX509Certificate;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/X509.class */
public class X509 {

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/X509$Mappings.class */
    public static class Mappings extends AsymmetricAlgorithmProvider {
        @Override // org.bouncycastle.jcajce.provider.util.AlgorithmProvider
        public void configure(ConfigurableProvider configurableProvider) {
            configurableProvider.addAlgorithm("KeyFactory.X.509", "org.bouncycastle.jcajce.provider.asymmetric.x509.KeyFactory");
            configurableProvider.addAlgorithm("Alg.Alias.KeyFactory.X509", XMLX509Certificate.JCA_CERT_ID);
            configurableProvider.addAlgorithm("CertificateFactory.X.509", "org.bouncycastle.jcajce.provider.asymmetric.x509.CertificateFactory");
            configurableProvider.addAlgorithm("Alg.Alias.CertificateFactory.X509", XMLX509Certificate.JCA_CERT_ID);
        }
    }
}
