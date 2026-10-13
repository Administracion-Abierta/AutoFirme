package org.bouncycastle.cert.crmf.jcajce;

import java.security.PrivateKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import org.bouncycastle.asn1.crmf.EncryptedValue;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.cert.crmf.CRMFException;
import org.bouncycastle.cert.crmf.EncryptedValueBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.operator.KeyWrapper;
import org.bouncycastle.operator.OutputEncryptor;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cert/crmf/jcajce/JcaEncryptedValueBuilder.class */
public class JcaEncryptedValueBuilder extends EncryptedValueBuilder {
    public JcaEncryptedValueBuilder(KeyWrapper keyWrapper, OutputEncryptor outputEncryptor) {
        super(keyWrapper, outputEncryptor);
    }

    public EncryptedValue build(X509Certificate x509Certificate) throws CRMFException, CertificateEncodingException {
        return build(new JcaX509CertificateHolder(x509Certificate));
    }

    public EncryptedValue build(PrivateKey privateKey) throws CRMFException, CertificateEncodingException {
        return build(PrivateKeyInfo.getInstance(privateKey.getEncoded()));
    }
}
