package es.gob.afirma.signers.cms;

import es.gob.afirma.signers.pkcs7.SCChecker;
import java.io.IOException;
import java.util.Enumeration;
import java.util.logging.Logger;
import org.spongycastle.asn1.ASN1InputStream;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.ASN1Set;
import org.spongycastle.asn1.ASN1TaggedObject;
import org.spongycastle.asn1.cms.Attribute;
import org.spongycastle.asn1.cms.SignedData;
import org.spongycastle.asn1.cms.SignerInfo;
import org.spongycastle.asn1.pkcs.PKCSObjectIdentifiers;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/cms/ValidateCMSSignedData.class */
final class ValidateCMSSignedData {
    private ValidateCMSSignedData() {
    }

    public static boolean isCMSSignedData(byte[] bArr) throws IOException {
        new SCChecker().checkSpongyCastle();
        boolean zVerifySignerInfo = true;
        try {
            ASN1InputStream aSN1InputStream = new ASN1InputStream(bArr);
            Throwable th = null;
            try {
                try {
                    Enumeration objects = ((ASN1Sequence) aSN1InputStream.readObject()).getObjects();
                    if (!((ASN1ObjectIdentifier) objects.nextElement()).equals(PKCSObjectIdentifiers.signedData)) {
                        zVerifySignerInfo = false;
                    } else {
                        ASN1Set signerInfos = SignedData.getInstance((ASN1Sequence) ((ASN1TaggedObject) objects.nextElement()).getObject()).getSignerInfos();
                        for (int i = 0; zVerifySignerInfo && i < signerInfos.size(); i++) {
                            zVerifySignerInfo = verifySignerInfo(SignerInfo.getInstance(signerInfos.getObjectAt(i)));
                        }
                    }
                    if (aSN1InputStream != null) {
                        if (0 != 0) {
                            try {
                                aSN1InputStream.close();
                            } catch (Throwable th2) {
                                th.addSuppressed(th2);
                            }
                        } else {
                            aSN1InputStream.close();
                        }
                    }
                    return zVerifySignerInfo;
                } catch (Throwable th3) {
                    if (aSN1InputStream != null) {
                        if (th != null) {
                            try {
                                aSN1InputStream.close();
                            } catch (Throwable th4) {
                                th.addSuppressed(th4);
                            }
                        } else {
                            aSN1InputStream.close();
                        }
                    }
                    throw th3;
                }
            } catch (Throwable th5) {
                th = th5;
                throw th5;
            }
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean verifySignerInfo(SignerInfo signerInfo) {
        boolean z = true;
        Enumeration objects = signerInfo.getAuthenticatedAttributes().getObjects();
        while (z && objects.hasMoreElements()) {
            if (Attribute.getInstance(objects.nextElement()).getAttrType().equals(PKCSObjectIdentifiers.id_aa_ets_sigPolicyId)) {
                z = false;
                Logger.getLogger("es.gob.afirma").warning("El signerInfo no es del tipo CMS, es del tipo CADES");
            }
        }
        return z;
    }
}
