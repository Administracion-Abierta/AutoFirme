package org.bouncycastle.its.operator;

import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.its.ITSCertificate;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/its/operator/ITSContentSigner.class */
public interface ITSContentSigner {
    OutputStream getOutputStream();

    byte[] getSignature();

    ITSCertificate getAssociatedCertificate();

    byte[] getAssociatedCertificateDigest();

    AlgorithmIdentifier getDigestAlgorithm();

    ASN1ObjectIdentifier getCurveID();

    boolean isForSelfSigning();
}
