package es.gob.afirma.signvalidation;

import com.aowagie.text.pdf.AcroFields;
import com.aowagie.text.pdf.PdfArray;
import com.aowagie.text.pdf.PdfDictionary;
import com.aowagie.text.pdf.PdfName;
import com.aowagie.text.pdf.PdfPKCS7;
import com.aowagie.text.pdf.PdfReader;
import es.gob.afirma.core.RuntimeConfigNeededException;
import es.gob.afirma.signers.pades.PdfUtil;
import es.gob.afirma.signers.pades.common.PdfExtraParams;
import es.gob.afirma.signers.pades.common.PdfFormModifiedException;
import es.gob.afirma.signers.pades.common.SuspectedPSAException;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signvalidation/ValidatePdfSignature.class */
public final class ValidatePdfSignature extends SignValider {
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private static final PdfName PDFNAME_ETSI_RFC3161 = new PdfName("ETSI.RFC3161");
    private static final PdfName PDFNAME_DOCTIMESTAMP = new PdfName("DocTimeStamp");
    private static final String DEFAULT_PAGES_TO_CHECK_PSA = "10";

    @Override // es.gob.afirma.signvalidation.SignValider
    public List<SignValidity> validate(byte[] bArr) throws RuntimeConfigNeededException, IOException {
        return validate(bArr, true);
    }

    @Override // es.gob.afirma.signvalidation.SignValider
    public List<SignValidity> validate(byte[] bArr, boolean z) throws RuntimeConfigNeededException, IOException {
        Properties properties = new Properties();
        properties.setProperty(PdfExtraParams.CHECK_CERTIFICATES, Boolean.toString(z));
        try {
            return validate(bArr, properties);
        } catch (RuntimeConfigNeededException e) {
            throw new IOException("No se dispone de la informacion necesaria para completar la validacion", e);
        }
    }

    @Override // es.gob.afirma.signvalidation.SignValider
    public List<SignValidity> validate(byte[] bArr, Properties properties) throws RuntimeConfigNeededException, IOException {
        Map<String, String> mapCheckPDFForm;
        Properties properties2 = properties != null ? properties : new Properties();
        ArrayList arrayList = new ArrayList();
        try {
            PdfReader pdfReader = PdfUtil.getPdfReader(bArr, properties2, Boolean.parseBoolean(properties2.getProperty("headless")));
            AcroFields acroFields = pdfReader.getAcroFields();
            List<String> signatureNames = acroFields.getSignatureNames();
            if (signatureNames.size() == 0) {
                arrayList.add(new SignValidity(SignValidity.SIGN_DETAIL_TYPE.KO, SignValidity.VALIDITY_ERROR.NO_SIGN));
                return arrayList;
            }
            String property = properties2.getProperty(PdfExtraParams.ALLOW_SIGN_MODIFIED_FORM);
            boolean z = Boolean.parseBoolean(property);
            boolean z2 = false;
            if (!z && acroFields.getTotalRevisions() > 1 && (mapCheckPDFForm = DataAnalizerUtil.checkPDFForm(pdfReader)) != null && !mapCheckPDFForm.isEmpty()) {
                if (property == null && isRelaxed()) {
                    throw new PdfFormModifiedException("Se han detectado cambios en un formulario posteriores a la primera firma");
                }
                arrayList.add(new SignValidity(SignValidity.SIGN_DETAIL_TYPE.KO, SignValidity.VALIDITY_ERROR.MODIFIED_FORM));
                z2 = true;
            }
            String property2 = properties2.getProperty("allowShadowAttack");
            boolean z3 = Boolean.parseBoolean(property2);
            String property3 = properties2.getProperty(PdfExtraParams.PAGES_TO_CHECK_PSA, DEFAULT_PAGES_TO_CHECK_PSA);
            boolean z4 = false;
            if (z || z2) {
                z4 = true;
            }
            if (!z4 && !z3 && acroFields.getTotalRevisions() > 1 && acroFields.getRevision(signatureNames.get(0)) < acroFields.getTotalRevisions()) {
                InputStream inputStreamExtractRevision = acroFields.extractRevision(signatureNames.get(0));
                Throwable th = null;
                try {
                    SignValidity signValidityCheckPdfShadowAttack = DataAnalizerUtil.checkPdfShadowAttack(bArr, inputStreamExtractRevision, property3);
                    if (signValidityCheckPdfShadowAttack != null) {
                        if (isRelaxed() && signValidityCheckPdfShadowAttack.getValidity() == SignValidity.SIGN_DETAIL_TYPE.PENDING_CONFIRM_BY_USER && property2 == null) {
                            throw new SuspectedPSAException("PDF sospechoso de haber sufrido PDF Shadow Attack");
                        }
                        if (signValidityCheckPdfShadowAttack.getValidity() == SignValidity.SIGN_DETAIL_TYPE.PENDING_CONFIRM_BY_USER) {
                            signValidityCheckPdfShadowAttack = new SignValidity(SignValidity.SIGN_DETAIL_TYPE.KO, signValidityCheckPdfShadowAttack.getError());
                        }
                        arrayList.add(signValidityCheckPdfShadowAttack);
                    }
                    if (inputStreamExtractRevision != null) {
                        if (0 != 0) {
                            try {
                                inputStreamExtractRevision.close();
                            } catch (Throwable th2) {
                                th.addSuppressed(th2);
                            }
                        } else {
                            inputStreamExtractRevision.close();
                        }
                    }
                } catch (Throwable th3) {
                    if (inputStreamExtractRevision != null) {
                        if (0 != 0) {
                            try {
                                inputStreamExtractRevision.close();
                            } catch (Throwable th4) {
                                th.addSuppressed(th4);
                            }
                        } else {
                            inputStreamExtractRevision.close();
                        }
                    }
                    throw th3;
                }
            }
            String strResolvePDFFormat = SignatureFormatDetectorPadesCades.resolvePDFFormat(bArr);
            Iterator<String> it = signatureNames.iterator();
            while (it.hasNext()) {
                for (SignValidity signValidity : validateSign(it.next(), acroFields, strResolvePDFFormat, Boolean.parseBoolean(properties2.getProperty(PdfExtraParams.CHECK_CERTIFICATES)))) {
                    if (!arrayList.contains(signValidity)) {
                        if (SignValidity.SIGN_DETAIL_TYPE.UNKNOWN.equals(signValidity.getValidity())) {
                            arrayList.add(0, signValidity);
                        } else if (signValidity.getValidity() != SignValidity.SIGN_DETAIL_TYPE.OK) {
                            arrayList.add(signValidity);
                        }
                    }
                }
            }
            int revision = 0;
            if (pdfReader.getCertificationLevel() == 1 && arrayList.size() == 0) {
                for (String str : signatureNames) {
                    PdfDictionary signatureDictionary = acroFields.getSignatureDictionary(str);
                    if (signatureDictionary.get(PdfName.REFERENCE) != null && ((PdfDictionary) ((PdfArray) signatureDictionary.get(PdfName.REFERENCE)).getArrayList().get(0)).get(PdfName.TRANSFORMMETHOD) != null) {
                        revision = acroFields.getRevision(str);
                    }
                }
            }
            if (pdfReader.getCertificationLevel() == 1 && arrayList.size() == 0) {
                Iterator<String> it2 = signatureNames.iterator();
                while (it2.hasNext()) {
                    int revision2 = acroFields.getRevision(it2.next());
                    if (revision2 == acroFields.getTotalRevisions() && revision2 <= revision) {
                        arrayList.add(new SignValidity(SignValidity.SIGN_DETAIL_TYPE.OK, null));
                    } else if (revision2 < acroFields.getTotalRevisions() && revision2 <= revision) {
                        arrayList.add(new SignValidity(SignValidity.SIGN_DETAIL_TYPE.OK, null));
                    } else {
                        arrayList.add(new SignValidity(SignValidity.SIGN_DETAIL_TYPE.KO, SignValidity.VALIDITY_ERROR.CERTIFIED_SIGN_REVISION));
                    }
                }
            }
            List<SignValidity> listCheckLongStandingValiditySign = checkLongStandingValiditySign(checkValidityKOPriority(arrayList));
            if (listCheckLongStandingValiditySign.size() == 0) {
                listCheckLongStandingValiditySign.add(new SignValidity(SignValidity.SIGN_DETAIL_TYPE.OK, null));
            }
            return listCheckLongStandingValiditySign;
        } catch (Exception e) {
            arrayList.add(new SignValidity(SignValidity.SIGN_DETAIL_TYPE.KO, SignValidity.VALIDITY_ERROR.NO_SIGN));
            return arrayList;
        }
    }

    public static List<SignValidity> validateSign(String str, AcroFields acroFields, String str2, boolean z) throws RuntimeConfigNeededException, IOException {
        ArrayList arrayList = new ArrayList();
        PdfPKCS7 pdfPKCS7VerifySignature = acroFields.verifySignature(str);
        if (pdfPKCS7VerifySignature.getStrictHashAlgorithm() == null) {
            arrayList.add(new SignValidity(SignValidity.SIGN_DETAIL_TYPE.KO, SignValidity.VALIDITY_ERROR.ALGORITHM_NOT_SUPPORTED));
        }
        PdfDictionary signatureDictionary = acroFields.getSignatureDictionary(str);
        if (!PDFNAME_ETSI_RFC3161.equals(signatureDictionary.get(PdfName.SUBFILTER)) && !PDFNAME_DOCTIMESTAMP.equals(signatureDictionary.get(PdfName.SUBFILTER))) {
            try {
                if (!pdfPKCS7VerifySignature.verify()) {
                    arrayList.add(new SignValidity(SignValidity.SIGN_DETAIL_TYPE.KO, SignValidity.VALIDITY_ERROR.NO_MATCH_DATA));
                }
            } catch (Exception e) {
                LOGGER.warning("Error validando una de las firmas del PDF: " + e);
                arrayList.add(new SignValidity(SignValidity.SIGN_DETAIL_TYPE.KO, SignValidity.VALIDITY_ERROR.CORRUPTED_SIGN, e));
            }
        }
        if (z) {
            try {
                pdfPKCS7VerifySignature.getSigningCertificate().checkValidity();
            } catch (CertificateExpiredException e2) {
                arrayList.add(new SignValidity(SignValidity.SIGN_DETAIL_TYPE.KO, SignValidity.VALIDITY_ERROR.CERTIFICATE_EXPIRED, e2));
            } catch (CertificateNotYetValidException e3) {
                arrayList.add(new SignValidity(SignValidity.SIGN_DETAIL_TYPE.KO, SignValidity.VALIDITY_ERROR.CERTIFICATE_NOT_VALID_YET, e3));
            }
        }
        if (!ISignatureFormatDetector.FORMAT_PADES_BASIC.equals(str2) && !ISignatureFormatDetector.FORMAT_UNRECOGNIZED.equals(str2) && !ISignatureFormatDetector.FORMAT_PADES_BES.equals(str2) && !ISignatureFormatDetector.FORMAT_PADES_B_LEVEL.equals(str2) && !ISignatureFormatDetector.FORMAT_PADES_B_B_LEVEL.equals(str2) && !ISignatureFormatDetector.FORMAT_PADES_EPES.equals(str2)) {
            arrayList.add(new SignValidity(SignValidity.SIGN_DETAIL_TYPE.UNKNOWN, SignValidity.VALIDITY_ERROR.SIGN_PROFILE_NOT_CHECKED));
        }
        if (arrayList.size() == 0) {
            arrayList.add(new SignValidity(SignValidity.SIGN_DETAIL_TYPE.OK, null));
        }
        return arrayList;
    }
}
