package org.bouncycastle.jcajce.provider.asymmetric;

import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.jcajce.provider.asymmetric.compositesignatures.CompositeIndex;
import org.bouncycastle.jcajce.provider.asymmetric.compositesignatures.KeyFactorySpi;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/CompositeSignatures.class */
public class CompositeSignatures {
    private static final String PREFIX = "org.bouncycastle.jcajce.provider.asymmetric.compositesignatures.";
    private static final Map<String, String> compositesAttributes = new HashMap();

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/CompositeSignatures$Mappings.class */
    public static class Mappings extends AsymmetricAlgorithmProvider {
        @Override // org.bouncycastle.jcajce.provider.util.AlgorithmProvider
        public void configure(ConfigurableProvider configurableProvider) {
            for (ASN1ObjectIdentifier aSN1ObjectIdentifier : CompositeIndex.getSupportedIdentifiers()) {
                String algorithmName = CompositeIndex.getAlgorithmName(aSN1ObjectIdentifier);
                String strReplace = algorithmName.replace('-', '_');
                configurableProvider.addAlgorithm("Alg.Alias.KeyFactory", aSN1ObjectIdentifier, "COMPOSITE");
                configurableProvider.addAlgorithm("Alg.Alias.KeyFactory." + algorithmName, "COMPOSITE");
                configurableProvider.addAlgorithm("KeyPairGenerator." + algorithmName, "org.bouncycastle.jcajce.provider.asymmetric.compositesignatures.KeyPairGeneratorSpi$" + strReplace);
                configurableProvider.addAlgorithm("Alg.Alias.KeyPairGenerator", aSN1ObjectIdentifier, algorithmName);
                configurableProvider.addAlgorithm("Signature." + algorithmName, "org.bouncycastle.jcajce.provider.asymmetric.compositesignatures.SignatureSpi$" + strReplace);
                configurableProvider.addAlgorithm("Alg.Alias.Signature", aSN1ObjectIdentifier, algorithmName);
                configurableProvider.addKeyInfoConverter(aSN1ObjectIdentifier, new KeyFactorySpi());
            }
        }
    }

    static {
        compositesAttributes.put("SupportedKeyClasses", "org.bouncycastle.jcajce.CompositePublicKey|org.bouncycastle.jcajce.CompositePrivateKey");
        compositesAttributes.put("SupportedKeyFormats", "PKCS#8|X.509");
    }
}
