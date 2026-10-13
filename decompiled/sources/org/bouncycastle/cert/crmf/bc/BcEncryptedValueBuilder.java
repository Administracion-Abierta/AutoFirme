package org.bouncycastle.cert.crmf.bc;

import java.io.IOException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import org.bouncycastle.asn1.crmf.EncryptedValue;
import org.bouncycastle.cert.crmf.CRMFException;
import org.bouncycastle.cert.crmf.EncryptedValueBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.util.PrivateKeyInfoFactory;
import org.bouncycastle.operator.KeyWrapper;
import org.bouncycastle.operator.OutputEncryptor;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cert/crmf/bc/BcEncryptedValueBuilder.class */
public class BcEncryptedValueBuilder extends EncryptedValueBuilder {
    public BcEncryptedValueBuilder(KeyWrapper keyWrapper, OutputEncryptor outputEncryptor) {
        super(keyWrapper, outputEncryptor);
    }

    public EncryptedValue build(X509Certificate x509Certificate) throws CRMFException, CertificateEncodingException {
        return build(new JcaX509CertificateHolder(x509Certificate));
    }

    public EncryptedValue build(AsymmetricKeyParameter asymmetricKeyParameter) throws CRMFException, IOException {
        return build(PrivateKeyInfoFactory.createPrivateKeyInfo(asymmetricKeyParameter));
    }
}
