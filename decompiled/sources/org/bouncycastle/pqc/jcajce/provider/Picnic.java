package org.bouncycastle.pqc.jcajce.provider;

import org.apache.commons.codec.digest.MessageDigestAlgorithms;
import org.bouncycastle.asn1.bc.BCObjectIdentifiers;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;
import org.bouncycastle.pqc.jcajce.provider.picnic.PicnicKeyFactorySpi;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/jcajce/provider/Picnic.class */
public class Picnic {
    private static final String PREFIX = "org.bouncycastle.pqc.jcajce.provider.picnic.";

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/jcajce/provider/Picnic$Mappings.class */
    public static class Mappings extends AsymmetricAlgorithmProvider {
        @Override // org.bouncycastle.jcajce.provider.util.AlgorithmProvider
        public void configure(ConfigurableProvider configurableProvider) {
            configurableProvider.addAlgorithm("KeyFactory.PICNIC", "org.bouncycastle.pqc.jcajce.provider.picnic.PicnicKeyFactorySpi");
            configurableProvider.addAlgorithm("KeyPairGenerator.PICNIC", "org.bouncycastle.pqc.jcajce.provider.picnic.PicnicKeyPairGeneratorSpi");
            addSignatureAlgorithm(configurableProvider, "PICNIC", "org.bouncycastle.pqc.jcajce.provider.picnic.SignatureSpi$Base", BCObjectIdentifiers.picnic_signature);
            addSignatureAlgorithm(configurableProvider, "SHAKE256", "PICNIC", "org.bouncycastle.pqc.jcajce.provider.picnic.SignatureSpi$withShake256", BCObjectIdentifiers.picnic_with_shake256);
            addSignatureAlgorithm(configurableProvider, "SHA512", "PICNIC", "org.bouncycastle.pqc.jcajce.provider.picnic.SignatureSpi$withSha512", BCObjectIdentifiers.picnic_with_sha512);
            addSignatureAlgorithm(configurableProvider, MessageDigestAlgorithms.SHA3_512, "PICNIC", "org.bouncycastle.pqc.jcajce.provider.picnic.SignatureSpi$withSha3512", BCObjectIdentifiers.picnic_with_sha3_512);
            PicnicKeyFactorySpi picnicKeyFactorySpi = new PicnicKeyFactorySpi();
            registerOid(configurableProvider, BCObjectIdentifiers.picnic_key, "Picnic", picnicKeyFactorySpi);
            registerOid(configurableProvider, BCObjectIdentifiers.picnicl1fs, "Picnic", picnicKeyFactorySpi);
            registerOid(configurableProvider, BCObjectIdentifiers.picnicl1ur, "Picnic", picnicKeyFactorySpi);
            registerOid(configurableProvider, BCObjectIdentifiers.picnicl3fs, "Picnic", picnicKeyFactorySpi);
            registerOid(configurableProvider, BCObjectIdentifiers.picnicl3ur, "Picnic", picnicKeyFactorySpi);
            registerOid(configurableProvider, BCObjectIdentifiers.picnicl5fs, "Picnic", picnicKeyFactorySpi);
            registerOid(configurableProvider, BCObjectIdentifiers.picnicl5ur, "Picnic", picnicKeyFactorySpi);
            registerOid(configurableProvider, BCObjectIdentifiers.picnic3l1, "Picnic", picnicKeyFactorySpi);
            registerOid(configurableProvider, BCObjectIdentifiers.picnic3l3, "Picnic", picnicKeyFactorySpi);
            registerOid(configurableProvider, BCObjectIdentifiers.picnic3l5, "Picnic", picnicKeyFactorySpi);
            registerOid(configurableProvider, BCObjectIdentifiers.picnicl1full, "Picnic", picnicKeyFactorySpi);
            registerOid(configurableProvider, BCObjectIdentifiers.picnicl3full, "Picnic", picnicKeyFactorySpi);
            registerOid(configurableProvider, BCObjectIdentifiers.picnicl5full, "Picnic", picnicKeyFactorySpi);
        }
    }
}
