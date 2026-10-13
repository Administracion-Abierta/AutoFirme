package org.spongycastle.pqc.jcajce.provider;

import org.apache.commons.codec.digest.MessageDigestAlgorithms;
import org.spongycastle.jcajce.provider.config.ConfigurableProvider;
import org.spongycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;
import org.spongycastle.jcajce.provider.util.AsymmetricKeyInfoConverter;
import org.spongycastle.pqc.asn1.PQCObjectIdentifiers;
import org.spongycastle.pqc.jcajce.provider.sphincs.Sphincs256KeyFactorySpi;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pqc/jcajce/provider/SPHINCS.class */
public class SPHINCS {
    private static final String PREFIX = "org.spongycastle.pqc.jcajce.provider.sphincs.";

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pqc/jcajce/provider/SPHINCS$Mappings.class */
    public static class Mappings extends AsymmetricAlgorithmProvider {
        @Override // org.spongycastle.jcajce.provider.util.AlgorithmProvider
        public void configure(ConfigurableProvider provider) {
            provider.addAlgorithm("KeyFactory.SPHINCS256", "org.spongycastle.pqc.jcajce.provider.sphincs.Sphincs256KeyFactorySpi");
            provider.addAlgorithm("KeyPairGenerator.SPHINCS256", "org.spongycastle.pqc.jcajce.provider.sphincs.Sphincs256KeyPairGeneratorSpi");
            addSignatureAlgorithm(provider, "SHA512", "SPHINCS256", "org.spongycastle.pqc.jcajce.provider.sphincs.SignatureSpi$withSha512", PQCObjectIdentifiers.sphincs256_with_SHA512);
            addSignatureAlgorithm(provider, MessageDigestAlgorithms.SHA3_512, "SPHINCS256", "org.spongycastle.pqc.jcajce.provider.sphincs.SignatureSpi$withSha3_512", PQCObjectIdentifiers.sphincs256_with_SHA3_512);
            AsymmetricKeyInfoConverter keyFact = new Sphincs256KeyFactorySpi();
            registerOid(provider, PQCObjectIdentifiers.sphincs256, "SPHINCS256", keyFact);
            registerOidAlgorithmParameters(provider, PQCObjectIdentifiers.sphincs256, "SPHINCS256");
        }
    }
}
