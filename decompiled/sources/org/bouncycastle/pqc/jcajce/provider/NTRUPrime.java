package org.bouncycastle.pqc.jcajce.provider;

import org.bouncycastle.asn1.bc.BCObjectIdentifiers;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;
import org.bouncycastle.pqc.jcajce.provider.ntruprime.NTRULPRimeKeyFactorySpi;
import org.bouncycastle.pqc.jcajce.provider.ntruprime.SNTRUPrimeKeyFactorySpi;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/jcajce/provider/NTRUPrime.class */
public class NTRUPrime {
    private static final String PREFIX = "org.bouncycastle.pqc.jcajce.provider.ntruprime.";

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/jcajce/provider/NTRUPrime$Mappings.class */
    public static class Mappings extends AsymmetricAlgorithmProvider {
        @Override // org.bouncycastle.jcajce.provider.util.AlgorithmProvider
        public void configure(ConfigurableProvider configurableProvider) {
            configurableProvider.addAlgorithm("KeyFactory.NTRULPRIME", "org.bouncycastle.pqc.jcajce.provider.ntruprime.NTRULPRimeKeyFactorySpi");
            configurableProvider.addAlgorithm("KeyPairGenerator.NTRULPRIME", "org.bouncycastle.pqc.jcajce.provider.ntruprime.NTRULPRimeKeyPairGeneratorSpi");
            configurableProvider.addAlgorithm("KeyGenerator.NTRULPRIME", "org.bouncycastle.pqc.jcajce.provider.ntruprime.NTRULPRimeKeyGeneratorSpi");
            NTRULPRimeKeyFactorySpi nTRULPRimeKeyFactorySpi = new NTRULPRimeKeyFactorySpi();
            configurableProvider.addAlgorithm("Cipher.NTRULPRIME", "org.bouncycastle.pqc.jcajce.provider.ntruprime.NTRULPRimeCipherSpi$Base");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher." + BCObjectIdentifiers.pqc_kem_ntrulprime, "NTRU");
            registerOid(configurableProvider, BCObjectIdentifiers.ntrulpr653, "NTRULPRIME", nTRULPRimeKeyFactorySpi);
            registerOid(configurableProvider, BCObjectIdentifiers.ntrulpr761, "NTRULPRIME", nTRULPRimeKeyFactorySpi);
            registerOid(configurableProvider, BCObjectIdentifiers.ntrulpr857, "NTRULPRIME", nTRULPRimeKeyFactorySpi);
            registerOid(configurableProvider, BCObjectIdentifiers.ntrulpr953, "NTRULPRIME", nTRULPRimeKeyFactorySpi);
            registerOid(configurableProvider, BCObjectIdentifiers.ntrulpr1013, "NTRULPRIME", nTRULPRimeKeyFactorySpi);
            registerOid(configurableProvider, BCObjectIdentifiers.ntrulpr1277, "NTRULPRIME", nTRULPRimeKeyFactorySpi);
            configurableProvider.addAlgorithm("KeyFactory.SNTRUPRIME", "org.bouncycastle.pqc.jcajce.provider.ntruprime.SNTRUPrimeKeyFactorySpi");
            configurableProvider.addAlgorithm("KeyPairGenerator.SNTRUPRIME", "org.bouncycastle.pqc.jcajce.provider.ntruprime.SNTRUPrimeKeyPairGeneratorSpi");
            configurableProvider.addAlgorithm("KeyGenerator.SNTRUPRIME", "org.bouncycastle.pqc.jcajce.provider.ntruprime.SNTRUPrimeKeyGeneratorSpi");
            SNTRUPrimeKeyFactorySpi sNTRUPrimeKeyFactorySpi = new SNTRUPrimeKeyFactorySpi();
            configurableProvider.addAlgorithm("Cipher.SNTRUPRIME", "org.bouncycastle.pqc.jcajce.provider.ntruprime.SNTRUPrimeCipherSpi$Base");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher." + BCObjectIdentifiers.pqc_kem_sntruprime, "NTRU");
            registerOid(configurableProvider, BCObjectIdentifiers.sntrup653, "SNTRUPRIME", sNTRUPrimeKeyFactorySpi);
            registerOid(configurableProvider, BCObjectIdentifiers.sntrup761, "SNTRUPRIME", sNTRUPrimeKeyFactorySpi);
            registerOid(configurableProvider, BCObjectIdentifiers.sntrup857, "SNTRUPRIME", sNTRUPrimeKeyFactorySpi);
            registerOid(configurableProvider, BCObjectIdentifiers.sntrup953, "SNTRUPRIME", sNTRUPrimeKeyFactorySpi);
            registerOid(configurableProvider, BCObjectIdentifiers.sntrup1013, "SNTRUPRIME", sNTRUPrimeKeyFactorySpi);
            registerOid(configurableProvider, BCObjectIdentifiers.sntrup1277, "SNTRUPRIME", sNTRUPrimeKeyFactorySpi);
        }
    }
}
