package org.spongycastle.cms.jcajce;

import java.io.OutputStream;
import java.security.AlgorithmParameters;
import java.security.Provider;
import java.security.SecureRandom;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.cms.CMSException;
import org.spongycastle.jcajce.io.MacOutputStream;
import org.spongycastle.operator.GenericKey;
import org.spongycastle.operator.MacCalculator;
import org.spongycastle.operator.jcajce.JceGenericKey;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cms/jcajce/JceCMSMacCalculatorBuilder.class */
public class JceCMSMacCalculatorBuilder {
    private final ASN1ObjectIdentifier macOID;
    private final int keySize;
    private EnvelopedDataHelper helper;
    private AlgorithmParameters algorithmParameters;
    private SecureRandom random;

    public JceCMSMacCalculatorBuilder(ASN1ObjectIdentifier macOID) {
        this(macOID, -1);
    }

    public JceCMSMacCalculatorBuilder(ASN1ObjectIdentifier macOID, int keySize) {
        this.helper = new EnvelopedDataHelper(new DefaultJcaJceExtHelper());
        this.macOID = macOID;
        this.keySize = keySize;
    }

    public JceCMSMacCalculatorBuilder setProvider(Provider provider) {
        this.helper = new EnvelopedDataHelper(new ProviderJcaJceExtHelper(provider));
        return this;
    }

    public JceCMSMacCalculatorBuilder setProvider(String providerName) {
        this.helper = new EnvelopedDataHelper(new NamedJcaJceExtHelper(providerName));
        return this;
    }

    public JceCMSMacCalculatorBuilder setSecureRandom(SecureRandom random) {
        this.random = random;
        return this;
    }

    public JceCMSMacCalculatorBuilder setAlgorithmParameters(AlgorithmParameters algorithmParameters) {
        this.algorithmParameters = algorithmParameters;
        return this;
    }

    public MacCalculator build() throws CMSException {
        return new CMSMacCalculator(this.macOID, this.keySize, this.algorithmParameters, this.random);
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cms/jcajce/JceCMSMacCalculatorBuilder$CMSMacCalculator.class */
    private class CMSMacCalculator implements MacCalculator {
        private SecretKey encKey;
        private AlgorithmIdentifier algorithmIdentifier;
        private Mac mac;

        CMSMacCalculator(ASN1ObjectIdentifier macOID, int keySize, AlgorithmParameters params, SecureRandom random) throws CMSException {
            KeyGenerator keyGen = JceCMSMacCalculatorBuilder.this.helper.createKeyGenerator(macOID);
            random = random == null ? new SecureRandom() : random;
            if (keySize < 0) {
                keyGen.init(random);
            } else {
                keyGen.init(keySize, random);
            }
            this.encKey = keyGen.generateKey();
            this.algorithmIdentifier = JceCMSMacCalculatorBuilder.this.helper.getAlgorithmIdentifier(macOID, params == null ? JceCMSMacCalculatorBuilder.this.helper.generateParameters(macOID, this.encKey, random) : params);
            this.mac = JceCMSMacCalculatorBuilder.this.helper.createContentMac(this.encKey, this.algorithmIdentifier);
        }

        @Override // org.spongycastle.operator.MacCalculator
        public AlgorithmIdentifier getAlgorithmIdentifier() {
            return this.algorithmIdentifier;
        }

        @Override // org.spongycastle.operator.MacCalculator
        public OutputStream getOutputStream() {
            return new MacOutputStream(this.mac);
        }

        @Override // org.spongycastle.operator.MacCalculator
        public byte[] getMac() {
            return this.mac.doFinal();
        }

        @Override // org.spongycastle.operator.MacCalculator
        public GenericKey getKey() {
            return new JceGenericKey(this.algorithmIdentifier, this.encKey);
        }
    }
}
