package es.gob.afirma.signvalidation;

import com.aowagie.text.pdf.AcroFields;
import com.aowagie.text.pdf.PdfDictionary;
import com.aowagie.text.pdf.PdfName;
import com.aowagie.text.pdf.PdfReader;
import es.gob.afirma.core.signers.AOSignConstants;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.cms.AttributeTable;
import org.spongycastle.asn1.cms.CMSObjectIdentifiers;
import org.spongycastle.asn1.esf.ESFAttributes;
import org.spongycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.spongycastle.cms.CMSSignedData;
import org.spongycastle.cms.SignerInformation;
import org.spongycastle.cms.SignerInformationStore;
import org.spongycastle.util.CollectionStore;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signvalidation/SignatureFormatDetectorPadesCades.class */
public class SignatureFormatDetectorPadesCades implements ISignatureFormatDetector {
    private static final ASN1ObjectIdentifier ID_ATTRIBUTE_CERTIFICATE_REFERENCES = new ASN1ObjectIdentifier("1.2.840.113549.1.9.16.2.44");
    private static final ASN1ObjectIdentifier ID_ATTRIBUTE_REVOCATION_REFERENCES = new ASN1ObjectIdentifier("1.2.840.113549.1.9.16.2.45");
    private static final ASN1ObjectIdentifier ID_LONG_TERM_VALIDATION = new ASN1ObjectIdentifier("0.4.0.1733.2.2");
    private static final ASN1ObjectIdentifier ID_ARCHIVE_TIME_STAMP_V3 = new ASN1ObjectIdentifier("0.4.0.1733.2.4");
    public static final PdfName DSS_DICTIONARY_NAME = new PdfName("DSS");
    public static final PdfName CADES_SUBFILTER_VALUE = new PdfName(AOSignConstants.PADES_SUBFILTER_BES);
    public static final PdfName TST_SUBFILTER_VALUE = new PdfName("ETSI.RFC3161");
    public static final PdfName DOC_TIME_STAMP_DICTIONARY_NAME = new PdfName("DocTimeStamp");
    static String FORMAT_CADES_BES = ISignatureFormatDetector.FORMAT_CADES_BES;
    static String FORMAT_CMS = "CMS";
    static String FORMAT_CMS_T = ISignatureFormatDetector.FORMAT_CMS_T;

    private static boolean isPAdESBLevel(PDFSignatureDictionary pDFSignatureDictionary) {
        PdfDictionary dictionary = pDFSignatureDictionary.getDictionary();
        if (pDFSignatureDictionary.getDictionary().getAsName(PdfName.SUBFILTER).equals(CADES_SUBFILTER_VALUE) && dictionary.get(PdfName.M) != null) {
            try {
                Iterator<SignerInformation> it = getCMSSignature(pDFSignatureDictionary).getSignerInfos().getSigners().iterator();
                while (it.hasNext()) {
                    if (it.next().getSignedAttributes().get(PKCSObjectIdentifiers.id_aa_signingCertificate) != null) {
                        return true;
                    }
                }
                return false;
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    private static boolean isPAdESBBLevel(PDFSignatureDictionary pDFSignatureDictionary) {
        PdfDictionary dictionary = pDFSignatureDictionary.getDictionary();
        if (pDFSignatureDictionary.getDictionary().getAsName(PdfName.SUBFILTER).equals(CADES_SUBFILTER_VALUE) && dictionary.get(PdfName.M) != null) {
            try {
                Iterator<SignerInformation> it = getCMSSignature(pDFSignatureDictionary).getSignerInfos().getSigners().iterator();
                while (it.hasNext()) {
                    if (it.next().getSignedAttributes().get(PKCSObjectIdentifiers.id_aa_signingCertificateV2) != null) {
                        return true;
                    }
                }
                return false;
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    private static boolean isPAdESTLevel(PDFSignatureDictionary pDFSignatureDictionary, PdfReader pdfReader) {
        try {
            int size = 0;
            CMSSignedData cMSSignature = getCMSSignature(pDFSignatureDictionary);
            if (!cMSSignature.getCertificates().getMatches(null).isEmpty()) {
                SignerInformation signerInformation = (SignerInformation) ((List) cMSSignature.getSignerInfos().getSigners()).get(0);
                if (signerInformation.getUnsignedAttributes() != null && signerInformation.getUnsignedAttributes().getAll(PKCSObjectIdentifiers.id_aa_signatureTimeStampToken) != null) {
                    size = signerInformation.getUnsignedAttributes().getAll(PKCSObjectIdentifiers.id_aa_signatureTimeStampToken).size();
                }
                int iCountDocumentTimeStampDictionaries = countDocumentTimeStampDictionaries(pdfReader);
                if (size == 1 && iCountDocumentTimeStampDictionaries == 0) {
                    return true;
                }
                if (size == 0 && iCountDocumentTimeStampDictionaries == 1) {
                    return true;
                }
                return false;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean isPAdESLTLevel(PDFSignatureDictionary pDFSignatureDictionary, PdfReader pdfReader) {
        if (isPAdESTLevel(pDFSignatureDictionary, pdfReader)) {
            if (pdfReader.getCatalog().get(DSS_DICTIONARY_NAME) != null) {
                return true;
            }
            AcroFields acroFields = pdfReader.getAcroFields();
            List<String> signatureNames = acroFields.getSignatureNames();
            for (int i = 0; i < signatureNames.size(); i++) {
                try {
                    if (new PdfReader(acroFields.extractRevision(signatureNames.get(i))).getCatalog().getAsDict(DSS_DICTIONARY_NAME) != null) {
                        return true;
                    }
                } catch (IOException e) {
                    return false;
                }
            }
            return false;
        }
        return false;
    }

    private static boolean isPAdESLTALevel(PDFSignatureDictionary pDFSignatureDictionary, PdfReader pdfReader) {
        int size = 0;
        try {
            CMSSignedData cMSSignature = getCMSSignature(pDFSignatureDictionary);
            if (!cMSSignature.getCertificates().getMatches(null).isEmpty()) {
                SignerInformation signerInformation = (SignerInformation) ((List) cMSSignature.getSignerInfos().getSigners()).get(0);
                if (signerInformation.getUnsignedAttributes() != null && signerInformation.getUnsignedAttributes().getAll(PKCSObjectIdentifiers.id_aa_signatureTimeStampToken) != null) {
                    size = signerInformation.getUnsignedAttributes().getAll(PKCSObjectIdentifiers.id_aa_signatureTimeStampToken).size();
                }
                int iCountDocumentTimeStampDictionaries = countDocumentTimeStampDictionaries(pdfReader);
                if (size > 1 || iCountDocumentTimeStampDictionaries > 1) {
                    if (pdfReader.getCatalog().get(DSS_DICTIONARY_NAME) != null) {
                        return true;
                    }
                    AcroFields acroFields = pdfReader.getAcroFields();
                    List<String> signatureNames = acroFields.getSignatureNames();
                    for (int i = 0; i < signatureNames.size(); i++) {
                        if (new PdfReader(acroFields.extractRevision(signatureNames.get(i))).getCatalog().getAsDict(DSS_DICTIONARY_NAME) != null) {
                            return true;
                        }
                    }
                    return false;
                }
                return false;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    private static int countDocumentTimeStampDictionaries(PdfReader pdfReader) {
        int i = 0;
        AcroFields acroFields = pdfReader.getAcroFields();
        List<String> signatureNames = acroFields.getSignatureNames();
        for (int i2 = 0; i2 < signatureNames.size(); i2++) {
            PdfDictionary signatureDictionary = acroFields.getSignatureDictionary(signatureNames.get(i2));
            String string = null;
            if (signatureDictionary.get(PdfName.TYPE) != null) {
                string = signatureDictionary.get(PdfName.TYPE).toString();
            }
            if (signatureDictionary.get(PdfName.SUBFILTER).toString().equalsIgnoreCase(TST_SUBFILTER_VALUE.toString()) && (string == null || string.equals(DOC_TIME_STAMP_DICTIONARY_NAME.toString()))) {
                i++;
            }
        }
        return i;
    }

    public static String resolvePDFFormat(byte[] bArr) {
        String str;
        try {
            PdfReader pdfReader = new PdfReader(bArr);
            PDFSignatureDictionary pDFSignatureDictionaryObtainLatestSignatureFromPDF = obtainLatestSignatureFromPDF(pdfReader);
            if (isPAdESBLevel(pDFSignatureDictionaryObtainLatestSignatureFromPDF)) {
                str = ISignatureFormatDetector.FORMAT_PADES_B_LEVEL;
                if (isPAdESLTALevel(pDFSignatureDictionaryObtainLatestSignatureFromPDF, pdfReader)) {
                    str = ISignatureFormatDetector.FORMAT_PADES_LTA_LEVEL;
                } else if (isPAdESLTLevel(pDFSignatureDictionaryObtainLatestSignatureFromPDF, pdfReader)) {
                    str = ISignatureFormatDetector.FORMAT_PADES_LT_LEVEL;
                } else if (isPAdESTLevel(pDFSignatureDictionaryObtainLatestSignatureFromPDF, pdfReader)) {
                    str = ISignatureFormatDetector.FORMAT_PADES_T_LEVEL;
                }
            } else if (isPAdESBBLevel(pDFSignatureDictionaryObtainLatestSignatureFromPDF)) {
                str = ISignatureFormatDetector.FORMAT_PADES_B_B_LEVEL;
                if (isPAdESLTALevel(pDFSignatureDictionaryObtainLatestSignatureFromPDF, pdfReader)) {
                    str = ISignatureFormatDetector.FORMAT_PADES_LTA_LEVEL;
                } else if (isPAdESLTLevel(pDFSignatureDictionaryObtainLatestSignatureFromPDF, pdfReader)) {
                    str = ISignatureFormatDetector.FORMAT_PADES_LT_LEVEL;
                } else if (isPAdESTLevel(pDFSignatureDictionaryObtainLatestSignatureFromPDF, pdfReader)) {
                    str = ISignatureFormatDetector.FORMAT_PADES_T_LEVEL;
                }
            } else {
                return getFormatOfPAdESSignature(pDFSignatureDictionaryObtainLatestSignatureFromPDF, pdfReader);
            }
        } catch (Exception e) {
            str = ISignatureFormatDetector.FORMAT_UNRECOGNIZED;
        }
        return str;
    }

    private static String getFormatOfPAdESSignature(PDFSignatureDictionary pDFSignatureDictionary, PdfReader pdfReader) {
        String str = ISignatureFormatDetector.FORMAT_UNRECOGNIZED;
        if (isPAdESLTV(pdfReader)) {
            str = ISignatureFormatDetector.FORMAT_PADES_LTV;
        } else if (isPAdESEPES(pDFSignatureDictionary)) {
            str = ISignatureFormatDetector.FORMAT_PADES_EPES;
        } else if (isPAdESBES(pDFSignatureDictionary)) {
            str = ISignatureFormatDetector.FORMAT_PADES_BES;
        } else if (isPAdESBasic(pDFSignatureDictionary)) {
            str = ISignatureFormatDetector.FORMAT_PADES_BASIC;
        } else if (isPDF(pDFSignatureDictionary)) {
            str = ISignatureFormatDetector.FORMAT_PDF;
        }
        return str;
    }

    public static boolean isPAdESEPES(PDFSignatureDictionary pDFSignatureDictionary) {
        if (pDFSignatureDictionary.getDictionary().getAsName(PdfName.SUBFILTER).equals(CADES_SUBFILTER_VALUE)) {
            try {
                return isCAdESEPES((List<SignerInformation>) getCMSSignature(pDFSignatureDictionary).getSignerInfos().getSigners());
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    public static boolean isPAdESBES(PDFSignatureDictionary pDFSignatureDictionary) {
        if (pDFSignatureDictionary.getDictionary().getAsName(PdfName.SUBFILTER).equals(CADES_SUBFILTER_VALUE)) {
            try {
                if (!hasSignaturePolicyIdentifier((SignerInformation) ((List) getCMSSignature(pDFSignatureDictionary).getSignerInfos().getSigners()).get(0))) {
                    return true;
                }
                return false;
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    public static boolean isPAdESBasic(PDFSignatureDictionary pDFSignatureDictionary) {
        PdfName pdfName = (PdfName) pDFSignatureDictionary.getDictionary().get(PdfName.SUBFILTER);
        if (pdfName.equals(PdfName.ADBE_PKCS7_DETACHED) || pdfName.equals(PdfName.ADBE_PKCS7_SHA1)) {
            return true;
        }
        return false;
    }

    public static boolean isPDF(PDFSignatureDictionary pDFSignatureDictionary) {
        PdfName pdfName = (PdfName) pDFSignatureDictionary.getDictionary().get(PdfName.SUBFILTER);
        if (!pdfName.equals(PdfName.ADBE_PKCS7_DETACHED) && !pdfName.equals(PdfName.ADBE_PKCS7_SHA1) && !pdfName.equals(CADES_SUBFILTER_VALUE)) {
            return true;
        }
        return false;
    }

    private static boolean isPAdESLTV(PdfReader pdfReader) {
        return pdfReader.getCatalog().get(DSS_DICTIONARY_NAME) != null || countDocumentTimeStampDictionaries(pdfReader) > 0;
    }

    public static CMSSignedData getCMSSignature(PDFSignatureDictionary pDFSignatureDictionary) throws Exception {
        try {
            CMSSignedData cMSSignedData = new CMSSignedData(pDFSignatureDictionary.getDictionary().getAsString(PdfName.CONTENTS).getOriginalBytes());
            if (cMSSignedData.getSignerInfos().getSigners().size() == 0) {
                throw new Exception("No existen firmantes en el documento");
            }
            return cMSSignedData;
        } catch (Exception e) {
            throw e;
        }
    }

    public static PDFSignatureDictionary obtainLatestSignatureFromPDF(PdfReader pdfReader) throws Exception {
        int revision;
        PDFSignatureDictionary pDFSignatureDictionary = null;
        try {
            int i = -1;
            AcroFields acroFields = pdfReader.getAcroFields();
            List<String> signatureNames = acroFields.getSignatureNames();
            for (int i2 = 0; i2 < signatureNames.size(); i2++) {
                String str = signatureNames.get(i2);
                PdfDictionary signatureDictionary = acroFields.getSignatureDictionary(str);
                String string = null;
                if (signatureDictionary.get(PdfName.TYPE) != null) {
                    string = signatureDictionary.get(PdfName.TYPE).toString();
                }
                if (!signatureDictionary.get(PdfName.SUBFILTER).toString().equalsIgnoreCase(new PdfName("ETSI.RFC3161").toString()) && ((string == null || string.equalsIgnoreCase(PdfName.SIG.toString())) && (revision = acroFields.getRevision(str)) > i)) {
                    i = revision;
                    pDFSignatureDictionary = new PDFSignatureDictionary(revision, signatureDictionary, str);
                }
            }
            return pDFSignatureDictionary;
        } catch (Exception e) {
            throw e;
        }
    }

    private static boolean isCAdESEPES(List<SignerInformation> list) {
        if (list != null && list.size() > 0) {
            for (SignerInformation signerInformation : list) {
                if (isCAdESEPES(signerInformation)) {
                    return true;
                }
                SignerInformationStore counterSignatures = signerInformation.getCounterSignatures();
                if (counterSignatures != null && counterSignatures.size() > 0 && isCAdESEPES((List<SignerInformation>) counterSignatures.getSigners())) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    private static boolean isCAdESEPES(SignerInformation signerInformation) {
        AttributeTable signedAttributes = signerInformation.getSignedAttributes();
        if (signedAttributes != null && signedAttributes.get(PKCSObjectIdentifiers.id_aa_ets_sigPolicyId) != null) {
            return true;
        }
        return false;
    }

    public static boolean isASN1Format(byte[] bArr) {
        try {
            CMSSignedData cMSSignedData = new CMSSignedData(bArr);
            cMSSignedData.getSignedContent().getContentType();
            if (cMSSignedData.getSignedContent() != null && !CMSObjectIdentifiers.signedData.equals(cMSSignedData.getSignedContent().getContentType())) {
                return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String resolveASN1Format(CMSSignedData cMSSignedData, SignerInformation signerInformation) {
        String strResolveCAdESNoBaselineFormat = ISignatureFormatDetector.FORMAT_UNRECOGNIZED;
        try {
            if (isCMS(signerInformation)) {
                strResolveCAdESNoBaselineFormat = FORMAT_CMS;
                if (isCAdEST(signerInformation)) {
                    strResolveCAdESNoBaselineFormat = FORMAT_CMS_T;
                }
            } else if (isCAdESEPES(signerInformation)) {
                strResolveCAdESNoBaselineFormat = resolveFormatOfCAdESEPESSignature(cMSSignedData, signerInformation);
            } else if (isCAdESBES(signerInformation)) {
                strResolveCAdESNoBaselineFormat = FORMAT_CADES_BES;
                if (isCAdESBLevel(signerInformation)) {
                    if (isCAdEST(signerInformation)) {
                        if (isCAdESLTLevel(signerInformation, cMSSignedData)) {
                            strResolveCAdESNoBaselineFormat = ISignatureFormatDetector.FORMAT_CADES_LT_LEVEL;
                            if (isCAdESLTALevel(signerInformation)) {
                                strResolveCAdESNoBaselineFormat = ISignatureFormatDetector.FORMAT_CADES_LTA_LEVEL;
                            }
                        } else {
                            strResolveCAdESNoBaselineFormat = resolveCAdESNoBaselineFormat(ISignatureFormatDetector.FORMAT_CADES_T_LEVEL, signerInformation);
                        }
                    } else {
                        strResolveCAdESNoBaselineFormat = resolveCAdESNoBaselineFormat(ISignatureFormatDetector.FORMAT_CADES_B_LEVEL, signerInformation);
                    }
                }
            } else if (isCAdESBBLevel(signerInformation)) {
                if (isCAdEST(signerInformation)) {
                    if (isCAdESLTLevel(signerInformation, cMSSignedData)) {
                        strResolveCAdESNoBaselineFormat = ISignatureFormatDetector.FORMAT_CADES_LT_LEVEL;
                        if (isCAdESLTALevel(signerInformation)) {
                            strResolveCAdESNoBaselineFormat = ISignatureFormatDetector.FORMAT_CADES_LTA_LEVEL;
                        }
                    } else {
                        strResolveCAdESNoBaselineFormat = resolveCAdESNoBaselineFormat(ISignatureFormatDetector.FORMAT_CADES_T_LEVEL, signerInformation);
                    }
                } else {
                    strResolveCAdESNoBaselineFormat = resolveCAdESNoBaselineFormat(ISignatureFormatDetector.FORMAT_CADES_B_B_LEVEL, signerInformation);
                }
            }
        } catch (Exception e) {
            strResolveCAdESNoBaselineFormat = ISignatureFormatDetector.FORMAT_UNRECOGNIZED;
        }
        return strResolveCAdESNoBaselineFormat;
    }

    private static boolean isCMS(SignerInformation signerInformation) {
        AttributeTable signedAttributes = signerInformation.getSignedAttributes();
        return signedAttributes.get(PKCSObjectIdentifiers.id_aa_signingCertificate) == null && signedAttributes.get(PKCSObjectIdentifiers.id_aa_signingCertificateV2) == null && signedAttributes.get(PKCSObjectIdentifiers.id_aa_ets_otherSigCert) == null;
    }

    private static boolean isCAdEST(SignerInformation signerInformation) {
        AttributeTable unsignedAttributes = signerInformation.getUnsignedAttributes();
        if (unsignedAttributes != null && unsignedAttributes.get(PKCSObjectIdentifiers.id_aa_signatureTimeStampToken) != null) {
            return true;
        }
        return false;
    }

    private static String resolveFormatOfCAdESEPESSignature(CMSSignedData cMSSignedData, SignerInformation signerInformation) {
        String strResolveCAdESNoBaselineFormat;
        if (isCAdESBLevel(signerInformation)) {
            strResolveCAdESNoBaselineFormat = ISignatureFormatDetector.FORMAT_CADES_B_LEVEL;
            if (isCAdEST(signerInformation)) {
                strResolveCAdESNoBaselineFormat = ISignatureFormatDetector.FORMAT_CADES_T_LEVEL;
                if (isCAdESLTLevel(signerInformation, cMSSignedData)) {
                    strResolveCAdESNoBaselineFormat = ISignatureFormatDetector.FORMAT_CADES_LT_LEVEL;
                    if (isCAdESLTALevel(signerInformation)) {
                        strResolveCAdESNoBaselineFormat = ISignatureFormatDetector.FORMAT_CADES_LTA_LEVEL;
                    }
                }
            }
        } else {
            strResolveCAdESNoBaselineFormat = resolveCAdESNoBaselineFormat(ISignatureFormatDetector.FORMAT_CADES_EPES, signerInformation);
        }
        return strResolveCAdESNoBaselineFormat;
    }

    private static String resolveCAdESNoBaselineFormat(String str, SignerInformation signerInformation) {
        String str2 = str;
        if (!str2.equals(ISignatureFormatDetector.FORMAT_CADES_T_LEVEL) && isCAdEST(signerInformation)) {
            str2 = ISignatureFormatDetector.FORMAT_CADES_T;
        }
        return str2;
    }

    public static boolean hasSignaturePolicyIdentifier(SignerInformation signerInformation) {
        AttributeTable signedAttributes = signerInformation.getSignedAttributes();
        if (signedAttributes != null && signedAttributes.get(PKCSObjectIdentifiers.id_aa_ets_sigPolicyId) != null) {
            return true;
        }
        return false;
    }

    private static boolean isCAdESBES(SignerInformation signerInformation) {
        AttributeTable signedAttributes = signerInformation.getSignedAttributes();
        return (signedAttributes.get(PKCSObjectIdentifiers.id_aa_signingCertificate) == null && signedAttributes.get(PKCSObjectIdentifiers.id_aa_signingCertificateV2) == null && signedAttributes.get(PKCSObjectIdentifiers.id_aa_ets_otherSigCert) == null) ? false : true;
    }

    private static boolean isCAdESBBLevel(SignerInformation signerInformation) {
        return signerInformation.getSignedAttributes().get(PKCSObjectIdentifiers.id_aa_signingCertificateV2) != null;
    }

    private static boolean isCAdESBLevel(SignerInformation signerInformation) {
        if (signerInformation.getSignedAttributes().get(PKCSObjectIdentifiers.pkcs_9_at_signingTime) != null) {
            return true;
        }
        return false;
    }

    private static boolean isCAdESLTLevel(SignerInformation signerInformation, CMSSignedData cMSSignedData) {
        AttributeTable unsignedAttributes = signerInformation.getUnsignedAttributes();
        CollectionStore collectionStore = (CollectionStore) cMSSignedData.getCRLs();
        if (checkUnsignedAttributesForCAdESLTLevel(unsignedAttributes) && collectionStore != null && collectionStore.iterator().hasNext()) {
            return true;
        }
        return false;
    }

    private static boolean checkUnsignedAttributesForCAdESLTLevel(AttributeTable attributeTable) {
        if (attributeTable != null) {
            if (attributeTable.get(PKCSObjectIdentifiers.id_aa_ets_certificateRefs) != null || attributeTable.get(PKCSObjectIdentifiers.id_aa_ets_revocationRefs) != null || attributeTable.get(ID_ATTRIBUTE_CERTIFICATE_REFERENCES) != null || attributeTable.get(ID_ATTRIBUTE_REVOCATION_REFERENCES) != null || attributeTable.get(PKCSObjectIdentifiers.id_aa_ets_escTimeStamp) != null || attributeTable.get(PKCSObjectIdentifiers.id_aa_ets_certCRLTimestamp) != null || attributeTable.get(PKCSObjectIdentifiers.id_aa_ets_certValues) != null) {
                return false;
            }
            return checkUnsignedAttributesForCAdESLTLevelAux(attributeTable);
        }
        return true;
    }

    private static boolean checkUnsignedAttributesForCAdESLTLevelAux(AttributeTable attributeTable) {
        if (attributeTable.get(PKCSObjectIdentifiers.id_aa_ets_revocationValues) != null || attributeTable.get(ESFAttributes.archiveTimestamp) != null || attributeTable.get(ESFAttributes.archiveTimestampV2) != null || attributeTable.get(ID_LONG_TERM_VALIDATION) != null) {
            return false;
        }
        return true;
    }

    private static boolean isCAdESLTALevel(SignerInformation signerInformation) {
        AttributeTable unsignedAttributes = signerInformation.getUnsignedAttributes();
        if (unsignedAttributes != null && unsignedAttributes.get(ID_ARCHIVE_TIME_STAMP_V3) != null) {
            return true;
        }
        return false;
    }
}
