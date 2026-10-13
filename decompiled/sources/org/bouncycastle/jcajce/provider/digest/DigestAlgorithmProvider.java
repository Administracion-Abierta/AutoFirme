package org.bouncycastle.jcajce.provider.digest;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AlgorithmProvider;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/digest/DigestAlgorithmProvider.class */
abstract class DigestAlgorithmProvider extends AlgorithmProvider {
    DigestAlgorithmProvider() {
    }

    protected void addHMACAlgorithm(ConfigurableProvider configurableProvider, String str, String str2, String str3) {
        String str4 = "HMAC" + str;
        configurableProvider.addAlgorithm("Mac." + str4, str2);
        configurableProvider.addAlgorithm("Alg.Alias.Mac.HMAC-" + str, str4);
        configurableProvider.addAlgorithm("Alg.Alias.Mac.HMAC/" + str, str4);
        configurableProvider.addAlgorithm("KeyGenerator." + str4, str3);
        configurableProvider.addAlgorithm("Alg.Alias.KeyGenerator.HMAC-" + str, str4);
        configurableProvider.addAlgorithm("Alg.Alias.KeyGenerator.HMAC/" + str, str4);
    }

    protected void addKMACAlgorithm(ConfigurableProvider configurableProvider, String str, String str2, String str3) {
        String str4 = "KMAC" + str;
        configurableProvider.addAlgorithm("Mac." + str4, str2);
        configurableProvider.addAlgorithm("KeyGenerator." + str4, str3);
        configurableProvider.addAlgorithm("Alg.Alias.KeyGenerator.KMAC" + str, str4);
    }

    protected void addHMACAlias(ConfigurableProvider configurableProvider, String str, ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        String str2 = "HMAC" + str;
        configurableProvider.addAlgorithm("Alg.Alias.Mac." + aSN1ObjectIdentifier, str2);
        configurableProvider.addAlgorithm("Alg.Alias.KeyGenerator." + aSN1ObjectIdentifier, str2);
    }
}
