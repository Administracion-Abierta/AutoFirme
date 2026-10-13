package org.bouncycastle.operator;

import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/operator/PBEMacCalculatorProvider.class */
public interface PBEMacCalculatorProvider {
    MacCalculator get(AlgorithmIdentifier algorithmIdentifier, char[] cArr) throws OperatorCreationException;
}
