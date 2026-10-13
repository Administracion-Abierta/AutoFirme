package es.gob.afirma.standalone.signdetails;

import com.aowagie.text.exceptions.BadPasswordException;
import com.aowagie.text.pdf.AcroFields;
import com.aowagie.text.pdf.PdfArray;
import com.aowagie.text.pdf.PdfDictionary;
import com.aowagie.text.pdf.PdfName;
import com.aowagie.text.pdf.PdfPKCS7;
import com.aowagie.text.pdf.PdfReader;
import com.aowagie.text.pdf.PdfString;
import es.gob.afirma.core.AOInvalidFormatException;
import es.gob.afirma.core.misc.AOUtil;
import es.gob.afirma.core.signers.AdESPolicy;
import es.gob.afirma.core.util.tree.AOTreeModel;
import es.gob.afirma.core.util.tree.AOTreeNode;
import es.gob.afirma.signers.pades.AOPDFSigner;
import es.gob.afirma.signvalidation.SignValidity;
import es.gob.afirma.signvalidation.SignatureFormatDetectorPadesCades;
import es.gob.afirma.signvalidation.ValidatePdfSignature;
import es.gob.afirma.standalone.SimpleAfirmaMessages;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.spongycastle.asn1.cms.Attribute;
import org.spongycastle.asn1.cms.AttributeTable;
import org.spongycastle.asn1.esf.SignaturePolicyId;
import org.spongycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.spongycastle.cms.CMSSignedData;
import org.spongycastle.cms.SignerInformation;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/signdetails/PAdESSignAnalyzer.class */
public class PAdESSignAnalyzer implements SignAnalyzer {
    static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    List<SignDetails> signDetailsList;
    final AOTreeNode signersTree = new AOTreeNode("Datos");
    public static final String PADES = "PAdES";

    public PAdESSignAnalyzer(byte[] bArr) throws Exception {
        try {
            this.signDetailsList = new ArrayList();
            createSignDetails(bArr);
        } catch (Exception e) {
            throw new AOInvalidFormatException("No se ha podido cargar el documento XML de firmas", e);
        }
    }

    @Override // es.gob.afirma.standalone.signdetails.SignAnalyzer
    public AOTreeModel getSignersTree() {
        return new AOTreeModel(this.signersTree);
    }

    @Override // es.gob.afirma.standalone.signdetails.SignAnalyzer
    public List<SignDetails> getAllSignDetails() {
        return this.signDetailsList;
    }

    @Override // es.gob.afirma.standalone.signdetails.SignAnalyzer
    public String getSignFormat() {
        return "PAdES";
    }

    @Override // es.gob.afirma.standalone.signdetails.SignAnalyzer
    public String getDataLocation() {
        return null;
    }

    private void createSignDetails(byte[] bArr) throws Exception {
        try {
            try {
                try {
                    PdfReader pdfReader = new PdfReader(bArr);
                    try {
                        AcroFields acroFields = pdfReader.getAcroFields();
                        int certificationLevel = pdfReader.getCertificationLevel();
                        String strResolvePDFFormat = SignatureFormatDetectorPadesCades.resolvePDFFormat(bArr);
                        for (String str : acroFields.getSignatureNames()) {
                            PdfDictionary signatureDictionary = acroFields.getSignatureDictionary(str);
                            if (!AOPDFSigner.PDFNAME_ETSI_RFC3161.equals(signatureDictionary.get(PdfName.SUBFILTER)) && !AOPDFSigner.PDFNAME_DOCTIMESTAMP.equals(signatureDictionary.get(PdfName.SUBFILTER))) {
                                SignDetails signDetailsBuildSignDetails = buildSignDetails(str, signatureDictionary, acroFields, strResolvePDFFormat, certificationLevel);
                                if (signatureDictionary.getAsString(PdfName.CONTENTS) != null) {
                                    try {
                                        Iterator it = ((List) new CMSSignedData(signatureDictionary.getAsString(PdfName.CONTENTS).getOriginalBytes()).getSignerInfos().getSigners()).iterator();
                                        while (it.hasNext()) {
                                            SignaturePolicy signaturePolicyAnalyzePolicy = analyzePolicy((SignerInformation) it.next());
                                            if (signaturePolicyAnalyzePolicy != null) {
                                                signDetailsBuildSignDetails.setPolicy(signaturePolicyAnalyzePolicy);
                                                break;
                                            }
                                        }
                                    } catch (Exception e) {
                                        LOGGER.log(Level.SEVERE, "No se ha podido obtener la informacion de la politica correctamente", (Throwable) e);
                                    }
                                }
                                this.signDetailsList.add(signDetailsBuildSignDetails);
                            }
                        }
                    } catch (Exception e2) {
                        LOGGER.severe("No se ha podido obtener la informacion de los firmantes del PDF, se devolvera un arbol vacio: " + e2);
                        throw e2;
                    }
                } catch (Exception e3) {
                    LOGGER.severe("No se ha podido leer el PDF, se devolvera un arbol vacio: " + e3);
                    throw e3;
                }
            } catch (BadPasswordException e4) {
                LOGGER.info("El PDF necesita contrasena. Se devolvera el arbol vacio: " + e4);
                throw e4;
            }
        } catch (Exception e5) {
            throw new AOInvalidFormatException("No se ha podido cargar el documento XML de firmas", e5);
        }
    }

    private SignDetails buildSignDetails(String str, PdfDictionary pdfDictionary, AcroFields acroFields, String str2, int i) throws Exception {
        SignDetails signDetails = new SignDetails();
        signDetails.setSignProfile(str2);
        signDetails.setCertificationLevel(i);
        if (pdfDictionary.get(PdfName.REFERENCE) != null) {
            signDetails.setCertificationSign(Boolean.valueOf(((PdfDictionary) ((PdfArray) pdfDictionary.get(PdfName.REFERENCE)).getArrayList().get(0)).get(PdfName.TRANSFORMMETHOD) != null));
        } else {
            signDetails.setCertificationSign(Boolean.FALSE);
        }
        try {
            PdfPKCS7 pdfPKCS7VerifySignature = acroFields.verifySignature(str);
            if (pdfPKCS7VerifySignature.getSignDate() != null) {
                signDetails.setSigningTime(pdfPKCS7VerifySignature.getSignDate().getTime());
            }
            String digestAlgorithm = pdfPKCS7VerifySignature.getDigestAlgorithm();
            if (digestAlgorithm != null) {
                signDetails.setAlgorithm(digestAlgorithm);
            }
            this.signersTree.add(new AOTreeNode(AOUtil.getCN(pdfPKCS7VerifySignature.getSigningCertificate()) + " (" + DateFormat.getDateTimeInstance(2, 3).format(pdfPKCS7VerifySignature.getSignDate().getTime()) + ")"));
            signDetails.setSigner(new CertificateDetails(pdfPKCS7VerifySignature.getSigningCertificate()));
            HashMap map = new HashMap();
            PdfString asString = pdfDictionary.getAsString(PdfName.REASON);
            if (asString != null) {
                map.put(SignDetailsFormatter.SIGN_REASON_METADATA, asString.toString());
            }
            PdfString asString2 = pdfDictionary.getAsString(PdfName.LOCATION);
            if (asString2 != null) {
                map.put(SignDetailsFormatter.LOCATION_METADATA, asString2.toString());
            }
            PdfString asString3 = pdfDictionary.getAsString(PdfName.CONTACTINFO);
            if (asString3 != null) {
                map.put(SignDetailsFormatter.CONTACT_INFO_METADATA, asString3.toString());
            }
            signDetails.setMetadata(map);
            signDetails.setValidityResult(ValidatePdfSignature.validateSign(str, acroFields, str2, false));
            if ("1".equals(Integer.toString(i)) && signDetails.getValidityResult().get(0).getValidity().equals(SignValidity.SIGN_DETAIL_TYPE.OK)) {
                if (acroFields.getRevision(str) != acroFields.getTotalRevisions()) {
                    List<SignValidity> validityResult = signDetails.getValidityResult();
                    validityResult.clear();
                    validityResult.add(new SignValidity(SignValidity.SIGN_DETAIL_TYPE.KO, SignValidity.VALIDITY_ERROR.CERTIFIED_SIGN_REVISION));
                    signDetails.setValidityResult(validityResult);
                    signDetails.setLastRevisionSign(Boolean.FALSE);
                } else {
                    signDetails.setLastRevisionSign(Boolean.TRUE);
                }
            }
            return signDetails;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "El PDF contiene una firma corrupta o con un formato desconocido (" + str + ")", (Throwable) e);
            throw e;
        }
    }

    private static SignaturePolicy analyzePolicy(SignerInformation signerInformation) {
        AttributeTable signedAttributes = signerInformation.getSignedAttributes();
        Attribute attribute = signedAttributes != null ? signedAttributes.get(PKCSObjectIdentifiers.id_aa_ets_sigPolicyId) : null;
        if (attribute != null && attribute.getAttrValues() != null && attribute.getAttrValues().size() > 0) {
            SignaturePolicyId signaturePolicyId = SignaturePolicyId.getInstance(attribute.getAttrValues().getObjectAt(0));
            String string = signaturePolicyId.getSigPolicyId().toString();
            if (string.equals(SignDetails.POLICY_PADES_AGE_1_9.getPolicyIdentifier().substring(8))) {
                return new SignaturePolicy(SimpleAfirmaMessages.getString("PreferencesPanel.73"), SignDetails.POLICY_PADES_AGE_1_9);
            }
            return new SignaturePolicy("", new AdESPolicy(string, signaturePolicyId.getSigPolicyHash().getHashValue().toString().substring(1), signaturePolicyId.getSigPolicyHash().getHashAlgorithm().getAlgorithm().toString(), signaturePolicyId.getSigPolicyQualifiers().getInfoAt(0).getSigQualifier().toString()));
        }
        return null;
    }
}
