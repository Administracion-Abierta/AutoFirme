package es.gob.afirma.signers.pkcs7;

import es.gob.afirma.core.AOInvalidFormatException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.logging.Logger;
import org.spongycastle.asn1.ASN1InputStream;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.ASN1Set;
import org.spongycastle.asn1.ASN1TaggedObject;
import org.spongycastle.asn1.DEROctetString;
import org.spongycastle.asn1.DERSet;
import org.spongycastle.asn1.cms.CMSAttributes;
import org.spongycastle.asn1.cms.ContentInfo;
import org.spongycastle.asn1.cms.SignedData;
import org.spongycastle.asn1.cms.SignerInfo;
import org.spongycastle.asn1.pkcs.PKCSObjectIdentifiers;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/pkcs7/ObtainContentSignedData.class */
public final class ObtainContentSignedData {
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");

    private ObtainContentSignedData() {
    }

    public static byte[] obtainData(byte[] bArr) throws AOInvalidFormatException {
        byte[] octets = null;
        try {
            ASN1InputStream aSN1InputStream = new ASN1InputStream(bArr);
            Throwable th = null;
            try {
                try {
                    ASN1Sequence aSN1Sequence = (ASN1Sequence) aSN1InputStream.readObject();
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
                    Enumeration objects = aSN1Sequence.getObjects();
                    ASN1ObjectIdentifier aSN1ObjectIdentifier = (ASN1ObjectIdentifier) objects.nextElement();
                    ASN1TaggedObject aSN1TaggedObject = (ASN1TaggedObject) objects.nextElement();
                    if (aSN1ObjectIdentifier.equals(PKCSObjectIdentifiers.signedData)) {
                        ContentInfo encapContentInfo = SignedData.getInstance(aSN1TaggedObject.getObject()).getEncapContentInfo();
                        if (encapContentInfo.getContent() != null) {
                            octets = ((DEROctetString) encapContentInfo.getContent()).getOctets();
                        } else {
                            LOGGER.info("No existe contenido en esta firma. Se devolvera null");
                        }
                    } else {
                        LOGGER.warning("No se puede obtener el contenido de esta firma.");
                    }
                    return octets;
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
            throw new AOInvalidFormatException("Error al parsear la firma ASN.1: " + e, e);
        }
    }

    public static byte[] obtainMessageDigest(byte[] bArr, String str) throws IOException {
        ASN1InputStream aSN1InputStream = new ASN1InputStream(bArr);
        Throwable th = null;
        try {
            ASN1Sequence aSN1Sequence = (ASN1Sequence) aSN1InputStream.readObject();
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
            Enumeration objects = aSN1Sequence.getObjects();
            if (!((ASN1ObjectIdentifier) objects.nextElement()).equals(PKCSObjectIdentifiers.signedData)) {
                LOGGER.warning("No se puede obtener el contenido de esta firma.");
                return null;
            }
            ASN1Set signerInfos = SignedData.getInstance(((ASN1TaggedObject) objects.nextElement()).getObject()).getSignerInfos();
            byte[] octets = null;
            for (int i = 0; i < signerInfos.size(); i++) {
                SignerInfo signerInfo = SignerInfo.getInstance(signerInfos.getObjectAt(i));
                if (signerInfo.getDigestAlgorithm().getAlgorithm().toString().equals(AOAlgorithmID.getOID(str))) {
                    ASN1Set authenticatedAttributes = signerInfo.getAuthenticatedAttributes();
                    for (int i2 = 0; i2 < authenticatedAttributes.size(); i2++) {
                        ASN1Sequence aSN1Sequence2 = (ASN1Sequence) authenticatedAttributes.getObjectAt(i2);
                        if (CMSAttributes.messageDigest.getId().equals(((ASN1ObjectIdentifier) aSN1Sequence2.getObjectAt(0)).toString())) {
                            octets = ((DEROctetString) ((DERSet) aSN1Sequence2.getObjectAt(1)).getObjectAt(0)).getOctets();
                            break;
                        }
                    }
                    if (octets != null) {
                        break;
                    }
                }
            }
            if (octets == null) {
                LOGGER.warning("No se ha encontrado en la firma una huella digital generada con el algoritmo: " + str);
            }
            return octets;
        } catch (Throwable th3) {
            if (aSN1InputStream != null) {
                if (0 != 0) {
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
    }
}
