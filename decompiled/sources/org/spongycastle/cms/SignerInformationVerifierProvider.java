package org.spongycastle.cms;

import org.spongycastle.operator.OperatorCreationException;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cms/SignerInformationVerifierProvider.class */
public interface SignerInformationVerifierProvider {
    SignerInformationVerifier get(SignerId signerId) throws OperatorCreationException;
}
