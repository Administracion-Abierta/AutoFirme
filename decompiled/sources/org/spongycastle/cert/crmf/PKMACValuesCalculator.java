package org.spongycastle.cert.crmf;

import org.spongycastle.asn1.x509.AlgorithmIdentifier;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cert/crmf/PKMACValuesCalculator.class */
public interface PKMACValuesCalculator {
    void setup(AlgorithmIdentifier algorithmIdentifier, AlgorithmIdentifier algorithmIdentifier2) throws CRMFException;

    byte[] calculateDigest(byte[] bArr) throws CRMFException;

    byte[] calculateMac(byte[] bArr, byte[] bArr2) throws CRMFException;
}
