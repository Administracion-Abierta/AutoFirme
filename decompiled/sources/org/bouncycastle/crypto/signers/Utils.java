package org.bouncycastle.crypto.signers;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServiceProperties;
import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.constraints.ConstraintUtils;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.params.DSAKeyParameters;
import org.bouncycastle.crypto.params.ECKeyParameters;
import org.bouncycastle.crypto.params.GOST3410KeyParameters;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/signers/Utils.class */
class Utils {
    Utils() {
    }

    static CryptoServiceProperties getDefaultProperties(String str, DSAKeyParameters dSAKeyParameters, boolean z) {
        return new DefaultServiceProperties(str, ConstraintUtils.bitsOfSecurityFor(dSAKeyParameters.getParameters().getP()), dSAKeyParameters, getPurpose(z));
    }

    static CryptoServiceProperties getDefaultProperties(String str, GOST3410KeyParameters gOST3410KeyParameters, boolean z) {
        return new DefaultServiceProperties(str, ConstraintUtils.bitsOfSecurityFor(gOST3410KeyParameters.getParameters().getP()), gOST3410KeyParameters, getPurpose(z));
    }

    static CryptoServiceProperties getDefaultProperties(String str, ECKeyParameters eCKeyParameters, boolean z) {
        return new DefaultServiceProperties(str, ConstraintUtils.bitsOfSecurityFor(eCKeyParameters.getParameters().getCurve()), eCKeyParameters, getPurpose(z));
    }

    static CryptoServiceProperties getDefaultProperties(String str, int i, CipherParameters cipherParameters, boolean z) {
        return new DefaultServiceProperties(str, i, cipherParameters, getPurpose(z));
    }

    static CryptoServicePurpose getPurpose(boolean z) {
        return z ? CryptoServicePurpose.SIGNING : CryptoServicePurpose.VERIFYING;
    }
}
