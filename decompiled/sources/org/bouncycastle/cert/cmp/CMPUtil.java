package org.bouncycastle.cert.cmp;

import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.operator.DigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cert/cmp/CMPUtil.class */
class CMPUtil {
    CMPUtil() {
    }

    static byte[] calculateCertHash(ASN1Object aSN1Object, AlgorithmIdentifier algorithmIdentifier, DigestCalculatorProvider digestCalculatorProvider, DigestAlgorithmIdentifierFinder digestAlgorithmIdentifierFinder) throws CMPException {
        AlgorithmIdentifier algorithmIdentifierFind = digestAlgorithmIdentifierFinder.find(algorithmIdentifier);
        if (algorithmIdentifierFind == null) {
            throw new CMPException("cannot find digest algorithm from signature algorithm");
        }
        return calculateDigest(aSN1Object, algorithmIdentifierFind, digestCalculatorProvider);
    }

    static byte[] calculateDigest(ASN1Object aSN1Object, AlgorithmIdentifier algorithmIdentifier, DigestCalculatorProvider digestCalculatorProvider) throws CMPException {
        DigestCalculator digestCalculator = getDigestCalculator(algorithmIdentifier, digestCalculatorProvider);
        derEncodeToStream(aSN1Object, digestCalculator.getOutputStream());
        return digestCalculator.getDigest();
    }

    static void derEncodeToStream(ASN1Object aSN1Object, OutputStream outputStream) {
        try {
            aSN1Object.encodeTo(outputStream, "DER");
            outputStream.close();
        } catch (IOException e) {
            throw new CMPRuntimeException("unable to DER encode object: " + e.getMessage(), e);
        }
    }

    static DigestCalculator getDigestCalculator(AlgorithmIdentifier algorithmIdentifier, DigestCalculatorProvider digestCalculatorProvider) throws CMPException {
        try {
            return digestCalculatorProvider.get(algorithmIdentifier);
        } catch (OperatorCreationException e) {
            throw new CMPException("unable to create digester: " + e.getMessage(), e);
        }
    }
}
