package org.bouncycastle.cert.crmf;

import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cert/crmf/PKMACValuesCalculator.class */
public interface PKMACValuesCalculator {
    void setup(AlgorithmIdentifier algorithmIdentifier, AlgorithmIdentifier algorithmIdentifier2) throws CRMFException;

    byte[] calculateDigest(byte[] bArr) throws CRMFException;

    byte[] calculateMac(byte[] bArr, byte[] bArr2) throws CRMFException;
}
