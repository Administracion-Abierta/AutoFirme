package org.bouncycastle.its.operator;

import org.bouncycastle.its.ITSCertificate;
import org.bouncycastle.operator.ContentVerifier;
import org.bouncycastle.operator.OperatorCreationException;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/its/operator/ITSContentVerifierProvider.class */
public interface ITSContentVerifierProvider {
    boolean hasAssociatedCertificate();

    ITSCertificate getAssociatedCertificate();

    ContentVerifier get(int i) throws OperatorCreationException;
}
