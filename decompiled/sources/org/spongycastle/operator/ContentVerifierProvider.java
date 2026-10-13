package org.spongycastle.operator;

import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.cert.X509CertificateHolder;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/operator/ContentVerifierProvider.class */
public interface ContentVerifierProvider {
    boolean hasAssociatedCertificate();

    X509CertificateHolder getAssociatedCertificate();

    ContentVerifier get(AlgorithmIdentifier algorithmIdentifier) throws OperatorCreationException;
}
