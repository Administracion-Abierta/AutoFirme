package es.gob.afirma.signers.cades;

import es.gob.afirma.core.signers.AOSignConstants;
import es.gob.afirma.signers.pkcs7.DigestedData;
import es.gob.afirma.signers.pkcs7.SignedAndEnvelopedData;
import java.io.IOException;
import java.util.Enumeration;
import java.util.logging.Logger;
import org.spongycastle.asn1.ASN1InputStream;
import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.ASN1Set;
import org.spongycastle.asn1.ASN1TaggedObject;
import org.spongycastle.asn1.DEROctetString;
import org.spongycastle.asn1.cms.Attribute;
import org.spongycastle.asn1.cms.EncryptedContentInfo;
import org.spongycastle.asn1.cms.EnvelopedData;
import org.spongycastle.asn1.cms.SignedData;
import org.spongycastle.asn1.cms.SignerInfo;
import org.spongycastle.asn1.pkcs.PKCSObjectIdentifiers;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/cades/CAdESValidator.class */
public final class CAdESValidator {
    private static final Logger LOGGER = Logger.getLogger("es.gob.afima");

    private CAdESValidator() {
    }

    private static Enumeration<?> getCAdESObjects(byte[] bArr) throws IOException {
        ASN1InputStream aSN1InputStream = new ASN1InputStream(bArr);
        Throwable th = null;
        try {
            try {
                Enumeration<?> objects = ((ASN1Sequence) aSN1InputStream.readObject()).getObjects();
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
                return objects;
            } catch (Throwable th3) {
                th = th3;
                throw th3;
            }
        } catch (Throwable th4) {
            if (aSN1InputStream != null) {
                if (th != null) {
                    try {
                        aSN1InputStream.close();
                    } catch (Throwable th5) {
                        th.addSuppressed(th5);
                    }
                } else {
                    aSN1InputStream.close();
                }
            }
            throw th4;
        }
    }

    static boolean isCAdESData(byte[] bArr) throws IOException {
        Enumeration<?> cAdESObjects = getCAdESObjects(bArr);
        if (!((ASN1ObjectIdentifier) cAdESObjects.nextElement()).equals(PKCSObjectIdentifiers.data)) {
            return false;
        }
        try {
            new DEROctetString(((ASN1TaggedObject) cAdESObjects.nextElement()).getObject());
            return true;
        } catch (Exception e) {
            LOGGER.fine("Los datos proporcionados no son de tipo Data: " + e);
            return false;
        }
    }

    public static boolean isCAdESSignedData(byte[] bArr, boolean z) {
        try {
            Enumeration<?> cAdESObjects = getCAdESObjects(bArr);
            if (!((ASN1ObjectIdentifier) cAdESObjects.nextElement()).equals(PKCSObjectIdentifiers.signedData)) {
                LOGGER.fine("Los datos proporcionados no son de tipo SignedData de CAdES (no esta declarado el OID de SignedData)");
                return false;
            }
            ASN1Set signerInfos = SignedData.getInstance((ASN1Sequence) ((ASN1TaggedObject) cAdESObjects.nextElement()).getObject()).getSignerInfos();
            if (z) {
                for (int i = 0; i < signerInfos.size(); i++) {
                    if (!verifySignerInfo(SignerInfo.getInstance(signerInfos.getObjectAt(i)))) {
                        LOGGER.fine("Los datos proporcionados no son de tipo SignedData de CAdES (al menos un SignerInfo no se ha declarado de tipo CAdES)");
                        return false;
                    }
                }
            }
            return true;
        } catch (Exception e) {
            LOGGER.fine("Los datos proporcionados no son de tipo SignedData de CAdES: " + e);
            return false;
        }
    }

    private static boolean verifySignerInfo(SignerInfo signerInfo) {
        boolean z = false;
        Enumeration objects = signerInfo.getAuthenticatedAttributes().getObjects();
        while (objects.hasMoreElements()) {
            ASN1Sequence aSN1Sequence = (ASN1Sequence) objects.nextElement();
            Attribute attribute = new Attribute((ASN1ObjectIdentifier) aSN1Sequence.getObjectAt(0), (ASN1Set) aSN1Sequence.getObjectAt(1));
            if (attribute.getAttrType().equals(PKCSObjectIdentifiers.id_aa_signingCertificate) || attribute.getAttrType().equals(PKCSObjectIdentifiers.id_aa_signingCertificateV2)) {
                z = true;
            }
        }
        return z;
    }

    static boolean isCAdESDigestedData(byte[] bArr) throws IOException {
        boolean z = false;
        Enumeration<?> cAdESObjects = getCAdESObjects(bArr);
        if (((ASN1ObjectIdentifier) cAdESObjects.nextElement()).equals(PKCSObjectIdentifiers.digestedData)) {
            z = true;
        }
        try {
            new DigestedData((ASN1Sequence) ((ASN1TaggedObject) cAdESObjects.nextElement()).getObject());
            return z;
        } catch (Exception e) {
            LOGGER.fine("Los datos proporcionados no son de tipo DigestedData: " + e);
            return false;
        }
    }

    static boolean isCAdESEncryptedData(byte[] bArr) throws IOException {
        boolean z = false;
        Enumeration<?> cAdESObjects = getCAdESObjects(bArr);
        if (((ASN1ObjectIdentifier) cAdESObjects.nextElement()).equals(PKCSObjectIdentifiers.encryptedData)) {
            z = true;
        }
        ASN1Sequence aSN1Sequence = (ASN1Sequence) ((ASN1TaggedObject) cAdESObjects.nextElement()).getObject();
        try {
            ASN1Integer.getInstance(aSN1Sequence.getObjectAt(0));
            EncryptedContentInfo.getInstance(aSN1Sequence.getObjectAt(1));
            if (aSN1Sequence.size() == 3) {
                aSN1Sequence.getObjectAt(2);
            }
            return z;
        } catch (Exception e) {
            LOGGER.fine("Los datos proporcionados no son de tipo EncryptedData: " + e);
            return false;
        }
    }

    static boolean isCAdESEnvelopedData(byte[] bArr) throws IOException {
        boolean z = false;
        Enumeration<?> cAdESObjects = getCAdESObjects(bArr);
        if (((ASN1ObjectIdentifier) cAdESObjects.nextElement()).equals(PKCSObjectIdentifiers.envelopedData)) {
            z = true;
        }
        try {
            EnvelopedData.getInstance(((ASN1TaggedObject) cAdESObjects.nextElement()).getObject());
            return z;
        } catch (Exception e) {
            LOGGER.fine("Los datos proporcionados no son de tipo EnvelopedData: " + e);
            return false;
        }
    }

    static boolean isCAdESSignedAndEnvelopedData(byte[] bArr) throws IOException {
        boolean zVerifySignerInfo = false;
        Enumeration<?> cAdESObjects = getCAdESObjects(bArr);
        if (((ASN1ObjectIdentifier) cAdESObjects.nextElement()).equals(PKCSObjectIdentifiers.signedData)) {
            zVerifySignerInfo = true;
        }
        try {
            ASN1Set signerInfos = new SignedAndEnvelopedData((ASN1Sequence) ((ASN1TaggedObject) cAdESObjects.nextElement()).getObject()).getSignerInfos();
            for (int i = 0; i < signerInfos.size(); i++) {
                zVerifySignerInfo = verifySignerInfo(SignerInfo.getInstance(signerInfos.getObjectAt(i)));
            }
            return zVerifySignerInfo;
        } catch (Exception e) {
            LOGGER.fine("Los datos proporcionados no son de tipo SignedAndEnvelopedData: " + e);
            return false;
        }
    }

    public static boolean isCAdESValid(byte[] bArr, String str, boolean z) throws IOException {
        if (str.equals(AOSignConstants.CMS_CONTENTTYPE_DATA)) {
            return isCAdESData(bArr);
        }
        if (str.equals(AOSignConstants.CMS_CONTENTTYPE_SIGNEDDATA)) {
            return isCAdESSignedData(bArr, z);
        }
        if (str.equals(AOSignConstants.CMS_CONTENTTYPE_DIGESTEDDATA)) {
            return isCAdESDigestedData(bArr);
        }
        if (str.equals("EncryptedData")) {
            return isCAdESEncryptedData(bArr);
        }
        if (str.equals("EnvelopedData")) {
            return isCAdESEnvelopedData(bArr);
        }
        if (str.equals(AOSignConstants.CMS_CONTENTTYPE_SIGNEDANDENVELOPEDDATA)) {
            return isCAdESSignedAndEnvelopedData(bArr);
        }
        LOGGER.fine("Tipo de contenido CADES no reconocido");
        return false;
    }

    public static boolean isCAdESValid(byte[] bArr, boolean z) throws IOException {
        if (bArr == null) {
            LOGGER.warning("Se han introducido datos nulos para su comprobacion");
            return false;
        }
        boolean zIsCAdESData = isCAdESData(bArr);
        if (!zIsCAdESData) {
            zIsCAdESData = isCAdESSignedData(bArr, z);
        }
        if (!zIsCAdESData) {
            zIsCAdESData = isCAdESDigestedData(bArr);
        }
        if (!zIsCAdESData) {
            zIsCAdESData = isCAdESEncryptedData(bArr);
        }
        if (!zIsCAdESData) {
            zIsCAdESData = isCAdESEnvelopedData(bArr);
        }
        if (!zIsCAdESData) {
            zIsCAdESData = isCAdESSignedAndEnvelopedData(bArr);
        }
        return zIsCAdESData;
    }
}
