package org.bouncycastle.jcajce.provider.keystore;

import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;
import org.bouncycastle.util.Properties;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/keystore/PKCS12.class */
public class PKCS12 {
    private static final String PREFIX = "org.bouncycastle.jcajce.provider.keystore.pkcs12.";

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/keystore/PKCS12$Mappings.class */
    public static class Mappings extends AsymmetricAlgorithmProvider {
        @Override // org.bouncycastle.jcajce.provider.util.AlgorithmProvider
        public void configure(ConfigurableProvider configurableProvider) {
            String propertyValue = Properties.getPropertyValue("org.bouncycastle.pkcs12.default");
            if (propertyValue != null) {
                configurableProvider.addAlgorithm("Alg.Alias.KeyStore.PKCS12", propertyValue);
                configurableProvider.addAlgorithm("Alg.Alias.KeyStore.BCPKCS12", propertyValue);
                configurableProvider.addAlgorithm("Alg.Alias.KeyStore.PKCS12-DEF", propertyValue.substring(0, 5) + "-DEF" + propertyValue.substring(6));
            } else {
                configurableProvider.addAlgorithm("KeyStore.PKCS12", "org.bouncycastle.jcajce.provider.keystore.pkcs12.PKCS12KeyStoreSpi$BCPKCS12KeyStore");
                configurableProvider.addAlgorithm("KeyStore.BCPKCS12", "org.bouncycastle.jcajce.provider.keystore.pkcs12.PKCS12KeyStoreSpi$BCPKCS12KeyStore");
                configurableProvider.addAlgorithm("KeyStore.PKCS12-DEF", "org.bouncycastle.jcajce.provider.keystore.pkcs12.PKCS12KeyStoreSpi$DefPKCS12KeyStore");
            }
            configurableProvider.addAlgorithm("KeyStore.PKCS12-3DES-40RC2", "org.bouncycastle.jcajce.provider.keystore.pkcs12.PKCS12KeyStoreSpi$BCPKCS12KeyStore");
            configurableProvider.addAlgorithm("KeyStore.PKCS12-3DES-3DES", "org.bouncycastle.jcajce.provider.keystore.pkcs12.PKCS12KeyStoreSpi$BCPKCS12KeyStore3DES");
            configurableProvider.addAlgorithm("KeyStore.PKCS12-AES256-AES128", "org.bouncycastle.jcajce.provider.keystore.pkcs12.PKCS12KeyStoreSpi$DefPKCS12KeyStoreAES256");
            configurableProvider.addAlgorithm("KeyStore.PKCS12-AES256-AES128-GCM", "org.bouncycastle.jcajce.provider.keystore.pkcs12.PKCS12KeyStoreSpi$DefPKCS12KeyStoreAES256GCM");
            configurableProvider.addAlgorithm("KeyStore.PKCS12-DEF-3DES-40RC2", "org.bouncycastle.jcajce.provider.keystore.pkcs12.PKCS12KeyStoreSpi$DefPKCS12KeyStore");
            configurableProvider.addAlgorithm("KeyStore.PKCS12-DEF-3DES-3DES", "org.bouncycastle.jcajce.provider.keystore.pkcs12.PKCS12KeyStoreSpi$DefPKCS12KeyStore3DES");
            configurableProvider.addAlgorithm("KeyStore.PKCS12-DEF-AES256-AES128", "org.bouncycastle.jcajce.provider.keystore.pkcs12.PKCS12KeyStoreSpi$DefPKCS12KeyStoreAES256");
            configurableProvider.addAlgorithm("KeyStore.PKCS12-DEF-AES256-AES128-GCM", "org.bouncycastle.jcajce.provider.keystore.pkcs12.PKCS12KeyStoreSpi$DefPKCS12KeyStoreAES256GCM");
        }
    }
}
