package org.bouncycastle.jcajce.provider.asymmetric;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;
import org.apache.xml.security.keys.content.x509.XMLX509Certificate;
import org.bouncycastle.asn1.bc.BCObjectIdentifiers;
import org.bouncycastle.asn1.bc.ExternalValue;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.jcajce.ExternalPublicKey;
import org.bouncycastle.jcajce.provider.asymmetric.util.BaseKeyFactorySpi;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricKeyInfoConverter;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/EXTERNAL.class */
public class EXTERNAL {
    private static final String PREFIX = "org.bouncycastle.jcajce.provider.asymmetric.EXTERNAL";
    private static final Map<String, String> externalAttributes = new HashMap();
    private static AsymmetricKeyInfoConverter baseConverter;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/EXTERNAL$ExternalKeyInfoConverter.class */
    private static class ExternalKeyInfoConverter implements AsymmetricKeyInfoConverter {
        private final ConfigurableProvider provider;

        public ExternalKeyInfoConverter(ConfigurableProvider configurableProvider) {
            this.provider = configurableProvider;
        }

        @Override // org.bouncycastle.jcajce.provider.util.AsymmetricKeyInfoConverter
        public PrivateKey generatePrivate(PrivateKeyInfo privateKeyInfo) throws IOException {
            throw new UnsupportedOperationException("no support for private key");
        }

        @Override // org.bouncycastle.jcajce.provider.util.AsymmetricKeyInfoConverter
        public PublicKey generatePublic(SubjectPublicKeyInfo subjectPublicKeyInfo) throws IOException {
            return new ExternalPublicKey(ExternalValue.getInstance(subjectPublicKeyInfo.parsePublicKey()));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/EXTERNAL$KeyFactory.class */
    public static class KeyFactory extends BaseKeyFactorySpi {
        @Override // java.security.KeyFactorySpi
        protected Key engineTranslateKey(Key key) throws InvalidKeyException {
            try {
                if (key instanceof PrivateKey) {
                    return generatePrivate(PrivateKeyInfo.getInstance(key.getEncoded()));
                }
                if (key instanceof PublicKey) {
                    return generatePublic(SubjectPublicKeyInfo.getInstance(key.getEncoded()));
                }
                throw new InvalidKeyException("key not recognized");
            } catch (IOException e) {
                throw new InvalidKeyException("key could not be parsed: " + e.getMessage());
            }
        }

        @Override // org.bouncycastle.jcajce.provider.util.AsymmetricKeyInfoConverter
        public PrivateKey generatePrivate(PrivateKeyInfo privateKeyInfo) throws IOException {
            return EXTERNAL.baseConverter.generatePrivate(privateKeyInfo);
        }

        @Override // org.bouncycastle.jcajce.provider.util.AsymmetricKeyInfoConverter
        public PublicKey generatePublic(SubjectPublicKeyInfo subjectPublicKeyInfo) throws IOException {
            return EXTERNAL.baseConverter.generatePublic(subjectPublicKeyInfo);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/EXTERNAL$Mappings.class */
    public static class Mappings extends AsymmetricAlgorithmProvider {
        @Override // org.bouncycastle.jcajce.provider.util.AlgorithmProvider
        public void configure(ConfigurableProvider configurableProvider) {
            configurableProvider.addAlgorithm("KeyFactory.EXTERNAL", "org.bouncycastle.jcajce.provider.asymmetric.EXTERNAL$KeyFactory");
            configurableProvider.addAlgorithm("KeyFactory." + BCObjectIdentifiers.external_value, "org.bouncycastle.jcajce.provider.asymmetric.EXTERNAL$KeyFactory");
            configurableProvider.addAlgorithm("KeyFactory.OID." + BCObjectIdentifiers.external_value, "org.bouncycastle.jcajce.provider.asymmetric.EXTERNAL$KeyFactory");
            AsymmetricKeyInfoConverter unused = EXTERNAL.baseConverter = new ExternalKeyInfoConverter(configurableProvider);
            configurableProvider.addKeyInfoConverter(BCObjectIdentifiers.external_value, EXTERNAL.baseConverter);
        }
    }

    static {
        externalAttributes.put("SupportedKeyClasses", "org.bouncycastle.jcajce.ExternalPublicKey");
        externalAttributes.put("SupportedKeyFormats", XMLX509Certificate.JCA_CERT_ID);
    }
}
