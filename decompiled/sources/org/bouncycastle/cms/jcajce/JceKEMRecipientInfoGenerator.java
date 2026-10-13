package org.bouncycastle.cms.jcajce;

import java.security.Provider;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.cms.KEMRecipientInfoGenerator;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cms/jcajce/JceKEMRecipientInfoGenerator.class */
public class JceKEMRecipientInfoGenerator extends KEMRecipientInfoGenerator {
    public JceKEMRecipientInfoGenerator(X509Certificate x509Certificate, ASN1ObjectIdentifier aSN1ObjectIdentifier) throws CertificateEncodingException {
        super(new IssuerAndSerialNumber(new JcaX509CertificateHolder(x509Certificate).toASN1Structure()), new JceCMSKEMKeyWrapper(x509Certificate.getPublicKey(), aSN1ObjectIdentifier));
    }

    public JceKEMRecipientInfoGenerator(byte[] bArr, PublicKey publicKey, ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        super(bArr, new JceCMSKEMKeyWrapper(publicKey, aSN1ObjectIdentifier));
    }

    public JceKEMRecipientInfoGenerator setProvider(String str) {
        ((JceCMSKEMKeyWrapper) this.wrapper).setProvider(str);
        return this;
    }

    public JceKEMRecipientInfoGenerator setProvider(Provider provider) {
        ((JceCMSKEMKeyWrapper) this.wrapper).setProvider(provider);
        return this;
    }

    public JceKEMRecipientInfoGenerator setSecureRandom(SecureRandom secureRandom) {
        ((JceCMSKEMKeyWrapper) this.wrapper).setSecureRandom(secureRandom);
        return this;
    }

    public JceKEMRecipientInfoGenerator setKDF(AlgorithmIdentifier algorithmIdentifier) {
        ((JceCMSKEMKeyWrapper) this.wrapper).setKDF(algorithmIdentifier);
        return this;
    }

    public JceKEMRecipientInfoGenerator setAlgorithmMapping(ASN1ObjectIdentifier aSN1ObjectIdentifier, String str) {
        ((JceCMSKEMKeyWrapper) this.wrapper).setAlgorithmMapping(aSN1ObjectIdentifier, str);
        return this;
    }
}
