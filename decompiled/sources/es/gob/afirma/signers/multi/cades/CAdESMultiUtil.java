package es.gob.afirma.signers.multi.cades;

import es.gob.afirma.core.SigningLTSException;
import java.io.IOException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1InputStream;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.ASN1Set;
import org.spongycastle.asn1.ASN1TaggedObject;
import org.spongycastle.asn1.BERSet;
import org.spongycastle.asn1.cms.AttributeTable;
import org.spongycastle.asn1.cms.SignedData;
import org.spongycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.spongycastle.cms.CMSException;
import org.spongycastle.cms.CMSSignedData;
import org.spongycastle.cms.SignerInformation;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/multi/cades/CAdESMultiUtil.class */
public final class CAdESMultiUtil {
    private static final ASN1ObjectIdentifier ARCHIVE_TIMESTAMP_V2_OID = new ASN1ObjectIdentifier("1.2.840.113549.1.9.16.2.48");
    private static final ASN1ObjectIdentifier ARCHIVE_TIMESTAMP_V3_OID = new ASN1ObjectIdentifier("0.4.0.1733.2.4");
    private static final ASN1ObjectIdentifier LONG_TERM_VALIDATION_OID = new ASN1ObjectIdentifier("0.4.0.1733.2.2");
    private static final List<ASN1ObjectIdentifier> UNSUPPORTED_ATTRIBUTES = new ArrayList(8);

    static {
        UNSUPPORTED_ATTRIBUTES.add(ARCHIVE_TIMESTAMP_V2_OID);
        UNSUPPORTED_ATTRIBUTES.add(ARCHIVE_TIMESTAMP_V3_OID);
        UNSUPPORTED_ATTRIBUTES.add(LONG_TERM_VALIDATION_OID);
        UNSUPPORTED_ATTRIBUTES.add(PKCSObjectIdentifiers.id_aa_ets_archiveTimestamp);
    }

    private CAdESMultiUtil() {
    }

    public static ASN1Set addCertificates(SignedData signedData, Certificate[] certificateArr) throws IOException, CertificateEncodingException {
        ASN1Set certificates = signedData.getCertificates();
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        Enumeration objects = certificates.getObjects();
        while (objects.hasMoreElements()) {
            aSN1EncodableVector.add((ASN1Encodable) objects.nextElement());
        }
        if (certificateArr.length != 0) {
            for (Certificate certificate : certificateArr) {
                aSN1EncodableVector.add(org.spongycastle.asn1.x509.Certificate.getInstance(ASN1Primitive.fromByteArray(certificate.getEncoded())));
            }
        }
        return new BERSet(aSN1EncodableVector);
    }

    public static void checkUnsupportedAttributes(byte[] bArr) throws SigningLTSException, IOException {
        try {
            checkUnsupportedAttributes(new CMSSignedData(bArr));
        } catch (CMSException e) {
            throw new IOException("La firma proporcionada no es un SignedData compatible CMS: " + e, e);
        }
    }

    private static void checkUnsupportedAttributes(CMSSignedData cMSSignedData) throws SigningLTSException {
        Iterator<SignerInformation> it = cMSSignedData.getSignerInfos().iterator();
        while (it.hasNext()) {
            checkUnsupportedAttributes(it.next());
        }
    }

    private static void checkUnsupportedAttributes(SignerInformation signerInformation) throws SigningLTSException {
        AttributeTable unsignedAttributes = signerInformation.getUnsignedAttributes();
        if (unsignedAttributes != null) {
            for (ASN1ObjectIdentifier aSN1ObjectIdentifier : UNSUPPORTED_ATTRIBUTES) {
                if (unsignedAttributes.get(aSN1ObjectIdentifier) != null) {
                    throw new SigningLTSException("No se soportan multifirmas de firmas con atributos longevos (encontrado OID=" + aSN1ObjectIdentifier + ")");
                }
            }
        }
    }

    static void checkUnsupported(ASN1ObjectIdentifier aSN1ObjectIdentifier) throws SigningLTSException {
        Iterator<ASN1ObjectIdentifier> it = UNSUPPORTED_ATTRIBUTES.iterator();
        while (it.hasNext()) {
            if (it.next().equals(aSN1ObjectIdentifier)) {
                throw new SigningLTSException("No se soportan multifirmas de firmas con atributos longevos (encontrado OID=" + aSN1ObjectIdentifier + ")");
            }
        }
    }

    public static boolean isCounterSignature(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        return PKCSObjectIdentifiers.pkcs_9_at_counterSignature.equals(aSN1ObjectIdentifier);
    }

    static SignedData readData(byte[] bArr) throws IOException {
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
                objects.nextElement();
                return SignedData.getInstance((ASN1Sequence) ((ASN1TaggedObject) objects.nextElement()).getObject());
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
}
