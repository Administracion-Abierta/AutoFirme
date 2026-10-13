package org.spongycastle.eac.operator.jcajce;

import es.gob.afirma.core.signers.AOSignConstants;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Signature;
import java.util.Hashtable;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.eac.EACObjectIdentifiers;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/eac/operator/jcajce/EACHelper.class */
abstract class EACHelper {
    private static final Hashtable sigNames = new Hashtable();

    protected abstract Signature createSignature(String str) throws NoSuchAlgorithmException, NoSuchProviderException;

    EACHelper() {
    }

    static {
        sigNames.put(EACObjectIdentifiers.id_TA_RSA_v1_5_SHA_1, AOSignConstants.SIGN_ALGORITHM_SHA1WITHRSA);
        sigNames.put(EACObjectIdentifiers.id_TA_RSA_v1_5_SHA_256, AOSignConstants.SIGN_ALGORITHM_SHA256WITHRSA);
        sigNames.put(EACObjectIdentifiers.id_TA_RSA_PSS_SHA_1, "SHA1withRSAandMGF1");
        sigNames.put(EACObjectIdentifiers.id_TA_RSA_PSS_SHA_256, "SHA256withRSAandMGF1");
        sigNames.put(EACObjectIdentifiers.id_TA_RSA_v1_5_SHA_512, "SHA512withRSA");
        sigNames.put(EACObjectIdentifiers.id_TA_RSA_PSS_SHA_512, "SHA512withRSAandMGF1");
        sigNames.put(EACObjectIdentifiers.id_TA_ECDSA_SHA_1, AOSignConstants.SIGN_ALGORITHM_SHA1WITHECDSA);
        sigNames.put(EACObjectIdentifiers.id_TA_ECDSA_SHA_224, AOSignConstants.SIGN_ALGORITHM_SHA224WITHECDSA);
        sigNames.put(EACObjectIdentifiers.id_TA_ECDSA_SHA_256, AOSignConstants.SIGN_ALGORITHM_SHA256WITHECDSA);
        sigNames.put(EACObjectIdentifiers.id_TA_ECDSA_SHA_384, AOSignConstants.SIGN_ALGORITHM_SHA384WITHECDSA);
        sigNames.put(EACObjectIdentifiers.id_TA_ECDSA_SHA_512, AOSignConstants.SIGN_ALGORITHM_SHA512WITHECDSA);
    }

    public Signature getSignature(ASN1ObjectIdentifier oid) throws NoSuchAlgorithmException, NoSuchProviderException {
        return createSignature((String) sigNames.get(oid));
    }
}
