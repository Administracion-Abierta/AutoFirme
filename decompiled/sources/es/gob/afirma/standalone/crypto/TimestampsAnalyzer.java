package es.gob.afirma.standalone.crypto;

import com.aowagie.text.exceptions.BadPasswordException;
import com.aowagie.text.pdf.AcroFields;
import com.aowagie.text.pdf.PdfDictionary;
import com.aowagie.text.pdf.PdfName;
import com.aowagie.text.pdf.PdfReader;
import es.gob.afirma.core.signers.AOTimestampInfo;
import es.gob.afirma.core.ui.AOUIFactory;
import es.gob.afirma.signers.cades.AOCAdESSigner;
import es.gob.afirma.signers.cms.AOCMSSigner;
import es.gob.afirma.signers.pades.AOPDFSigner;
import es.gob.afirma.signers.pades.PdfUtil;
import es.gob.afirma.standalone.SimpleAfirmaMessages;
import java.io.ByteArrayInputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;
import org.apache.xml.security.keys.content.x509.XMLX509Certificate;
import org.spongycastle.asn1.cms.Attribute;
import org.spongycastle.asn1.cms.AttributeTable;
import org.spongycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.spongycastle.cert.X509CertificateHolder;
import org.spongycastle.cms.CMSException;
import org.spongycastle.cms.CMSSignedData;
import org.spongycastle.cms.SignerInformation;
import org.spongycastle.tsp.TimeStampToken;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/crypto/TimestampsAnalyzer.class */
public final class TimestampsAnalyzer {
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private static final PdfName PDFNAME_ETSI_RFC3161 = new PdfName("ETSI.RFC3161");
    private static final PdfName PDFNAME_DOCTIMESTAMP = new PdfName("DocTimeStamp");

    public static List<AOTimestampInfo> getTimestamps(byte[] bArr) {
        if (bArr == null) {
            return new ArrayList(0);
        }
        if (new AOPDFSigner().isSign(bArr)) {
            return getPdfTimestamps(bArr);
        }
        try {
            if (new AOCAdESSigner().isSign(bArr) || new AOCMSSigner().isSign(bArr)) {
                return getCmsTimestamps(bArr);
            }
        } catch (Exception e) {
            LOGGER.warning("Error comprobando si la firma es CMS: " + e);
        }
        return new ArrayList(0);
    }

    public static List<AOTimestampInfo> getTimestamps(byte[] bArr, Properties properties) {
        if (bArr == null) {
            return new ArrayList(0);
        }
        if (new AOPDFSigner().isSign(bArr, properties)) {
            return getPdfTimestamps(bArr, properties);
        }
        try {
            if (new AOCAdESSigner().isSign(bArr) || new AOCMSSigner().isSign(bArr)) {
                return getCmsTimestamps(bArr);
            }
        } catch (Exception e) {
            LOGGER.warning("Error comprobando si la firma es CMS: " + e);
        }
        return new ArrayList(0);
    }

    private static List<AOTimestampInfo> getCmsTimestamps(byte[] bArr) {
        try {
            return getCmsTimestamps(new CMSSignedData(bArr));
        } catch (CMSException e) {
            LOGGER.severe("La firma proporcionada no es un SignedData compatible CMS, se devolvera una lista de sellos vacia: " + e);
            return new ArrayList(0);
        }
    }

    private static List<AOTimestampInfo> getCmsTimestamps(CMSSignedData cMSSignedData) {
        Attribute attribute;
        if (cMSSignedData == null) {
            return new ArrayList(0);
        }
        ArrayList arrayList = new ArrayList();
        Iterator<SignerInformation> it = cMSSignedData.getSignerInfos().getSigners().iterator();
        while (it.hasNext()) {
            AttributeTable unsignedAttributes = it.next().getUnsignedAttributes();
            if (unsignedAttributes != null && (attribute = unsignedAttributes.get(PKCSObjectIdentifiers.id_aa_signatureTimeStampToken)) != null) {
                try {
                    CMSSignedData cMSSignedData2 = new CMSSignedData(attribute.getAttrValues().getObjectAt(0).toASN1Primitive().getEncoded());
                    TimeStampToken timeStampToken = new TimeStampToken(cMSSignedData2);
                    Collection<X509CertificateHolder> matches = cMSSignedData2.getCertificates().getMatches(null);
                    if (!matches.isEmpty()) {
                        arrayList.add(new AOTimestampInfo((X509Certificate) CertificateFactory.getInstance(XMLX509Certificate.JCA_CERT_ID).generateCertificate(new ByteArrayInputStream(((X509CertificateHolder) matches.toArray()[0]).toASN1Structure().getEncoded())), timeStampToken.getTimeStampInfo().getGenTime()));
                    }
                } catch (Exception e) {
                    LOGGER.warning("Error extrayendo los sellos de tiempo de la firma CMS, se continuara con la siguiente: " + e);
                }
            }
        }
        return arrayList;
    }

    private static List<AOTimestampInfo> getPdfTimestamps(byte[] bArr) {
        PdfReader pdfReader;
        try {
            pdfReader = new PdfReader(bArr);
        } catch (BadPasswordException e) {
            try {
                pdfReader = new PdfReader(bArr, new String(AOUIFactory.getPassword(SimpleAfirmaMessages.getString("TimestampsAnalyzer.0"), null)).getBytes());
            } catch (BadPasswordException e2) {
                LOGGER.severe("La contrasena del PDF no es valida, se devolvera una lista de sellos vacia: " + e2);
                return new ArrayList(0);
            } catch (Exception e3) {
                LOGGER.severe("No se ha podido leer el PDF con contrasena, se devolvera una lista de sellos vacia: " + e3);
                return new ArrayList(0);
            }
        } catch (Exception e4) {
            LOGGER.severe("No se ha podido leer el PDF, se devolvera una lista de sellos vacia: " + e4);
            return new ArrayList(0);
        }
        try {
            AcroFields acroFields = pdfReader.getAcroFields();
            List<String> signatureNames = acroFields.getSignatureNames();
            ArrayList arrayList = new ArrayList();
            for (String str : signatureNames) {
                PdfDictionary signatureDictionary = acroFields.getSignatureDictionary(str);
                try {
                    CMSSignedData cMSSignedData = new CMSSignedData(signatureDictionary.getAsString(PdfName.CONTENTS).getOriginalBytes());
                    arrayList.addAll(getCmsTimestamps(cMSSignedData));
                    if (PDFNAME_ETSI_RFC3161.equals(signatureDictionary.get(PdfName.SUBFILTER)) || PDFNAME_DOCTIMESTAMP.equals(signatureDictionary.get(PdfName.SUBFILTER))) {
                        try {
                            try {
                                arrayList.add(new AOTimestampInfo(acroFields.verifySignature(str).getSigningCertificate(), new TimeStampToken(cMSSignedData).getTimeStampInfo().getGenTime()));
                            } catch (Exception e5) {
                                LOGGER.warning("El PDF contiene una firma corrupta o con un formato desconocido (" + str + "), se continua con las siguientes si las hubiese: " + e5);
                            }
                        } catch (Exception e6) {
                            LOGGER.warning("El sello encontrado no es compatible, se continua con los siguientes: " + e6);
                        }
                    }
                } catch (CMSException e7) {
                    LOGGER.severe("La firma encontrada no es compatible CMS, se continua con las siguientes: " + e7);
                }
            }
            return arrayList;
        } catch (Exception e8) {
            LOGGER.severe("No se ha podido obtener la informacion de los sellos del PDF, se devolvera una lista de sellos vacia: " + e8);
            return new ArrayList(0);
        }
    }

    private static List<AOTimestampInfo> getPdfTimestamps(byte[] bArr, Properties properties) {
        boolean z;
        PdfReader pdfReader;
        if (properties != null) {
            try {
                z = Boolean.parseBoolean(properties.getProperty("headless"));
            } catch (BadPasswordException e) {
                try {
                    pdfReader = new PdfReader(bArr, new String(AOUIFactory.getPassword(SimpleAfirmaMessages.getString("TimestampsAnalyzer.0"), null)).getBytes());
                } catch (BadPasswordException e2) {
                    LOGGER.severe("La contrasena del PDF no es valida, se devolvera una lista de sellos vacia: " + e2);
                    return new ArrayList(0);
                } catch (Exception e3) {
                    LOGGER.severe("No se ha podido leer el PDF, se devolvera una lista de sellos vacia: " + e3);
                    return new ArrayList(0);
                }
            } catch (Exception e4) {
                LOGGER.severe("No se ha podido leer el PDF, se devolvera una lista de sellos vacia: " + e4);
                return new ArrayList(0);
            }
        } else {
            z = false;
        }
        pdfReader = PdfUtil.getPdfReader(bArr, properties, z);
        try {
            AcroFields acroFields = pdfReader.getAcroFields();
            List<String> signatureNames = acroFields.getSignatureNames();
            ArrayList arrayList = new ArrayList();
            for (String str : signatureNames) {
                PdfDictionary signatureDictionary = acroFields.getSignatureDictionary(str);
                try {
                    CMSSignedData cMSSignedData = new CMSSignedData(signatureDictionary.getAsString(PdfName.CONTENTS).getOriginalBytes());
                    arrayList.addAll(getCmsTimestamps(cMSSignedData));
                    if (PDFNAME_ETSI_RFC3161.equals(signatureDictionary.get(PdfName.SUBFILTER)) || PDFNAME_DOCTIMESTAMP.equals(signatureDictionary.get(PdfName.SUBFILTER))) {
                        try {
                            try {
                                arrayList.add(new AOTimestampInfo(acroFields.verifySignature(str).getSigningCertificate(), new TimeStampToken(cMSSignedData).getTimeStampInfo().getGenTime()));
                            } catch (Exception e5) {
                                LOGGER.warning("El PDF contiene una firma corrupta o con un formato desconocido (" + str + "), se continua con las siguientes si las hubiese: " + e5);
                            }
                        } catch (Exception e6) {
                            LOGGER.warning("El sello encontrado no es compatible, se continua con los siguientes: " + e6);
                        }
                    }
                } catch (CMSException e7) {
                    LOGGER.severe("La firma encontrada no es compatible CMS, se continua con las siguientes: " + e7);
                }
            }
            return arrayList;
        } catch (Exception e8) {
            LOGGER.severe("No se ha podido obtener la informacion de los sellos del PDF, se devolvera una lista de sellos vacia: " + e8);
            return new ArrayList(0);
        }
    }
}
