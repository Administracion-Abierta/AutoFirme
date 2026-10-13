package es.gob.afirma.standalone.signdetails;

import es.gob.afirma.core.util.tree.AOTreeModel;
import es.gob.afirma.core.util.tree.AOTreeNode;
import es.gob.afirma.signers.xades.AOFacturaESigner;
import es.gob.afirma.signvalidation.SignValidity;
import es.gob.afirma.standalone.SimpleAfirmaMessages;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/signdetails/SignDetailsFormatter.class */
public class SignDetailsFormatter {
    private static String signFormat;
    public static final String STREET_ADDRESS_METADATA = SimpleAfirmaMessages.getString("ValidationInfoDialog.11");
    public static final String POSTAL_CODE_METADATA = SimpleAfirmaMessages.getString("ValidationInfoDialog.8");
    public static final String CITY_METADATA = SimpleAfirmaMessages.getString("ValidationInfoDialog.6");
    public static final String PROVINCE_METADATA = SimpleAfirmaMessages.getString("ValidationInfoDialog.7");
    public static final String COUNTRY_METADATA = SimpleAfirmaMessages.getString("ValidationInfoDialog.9");
    public static final String SIGN_REASON_METADATA = SimpleAfirmaMessages.getString("ValidationInfoDialog.18");
    public static final String LOCATION_METADATA = SimpleAfirmaMessages.getString("ValidationInfoDialog.19");
    public static final String CONTACT_INFO_METADATA = SimpleAfirmaMessages.getString("ValidationInfoDialog.20");
    public static final String LOCALITY_METADATA = SimpleAfirmaMessages.getString("ValidationInfoDialog.23");
    static Integer signerIndex = new Integer(1);
    public static final Map<String, String> MIMETYPE_MAPPER = new HashMap();

    static {
        MIMETYPE_MAPPER.put("data", SimpleAfirmaMessages.getString("ValidationInfoDialog.24"));
        MIMETYPE_MAPPER.put("application/pdf", SimpleAfirmaMessages.getString("ValidationInfoDialog.25"));
        MIMETYPE_MAPPER.put("text/xml", SimpleAfirmaMessages.getString("ValidationInfoDialog.26"));
        MIMETYPE_MAPPER.put("plain/xml", SimpleAfirmaMessages.getString("ValidationInfoDialog.26"));
        MIMETYPE_MAPPER.put("application-xml", SimpleAfirmaMessages.getString("ValidationInfoDialog.26"));
        MIMETYPE_MAPPER.put("application/octet-stream", SimpleAfirmaMessages.getString("ValidationInfoDialog.24"));
        MIMETYPE_MAPPER.put("application/postscript", SimpleAfirmaMessages.getString("ValidationInfoDialog.27"));
        MIMETYPE_MAPPER.put("text/html", SimpleAfirmaMessages.getString("ValidationInfoDialog.28"));
        MIMETYPE_MAPPER.put("image/tiff", SimpleAfirmaMessages.getString("ValidationInfoDialog.29"));
        MIMETYPE_MAPPER.put("image/gif", SimpleAfirmaMessages.getString("ValidationInfoDialog.30"));
        MIMETYPE_MAPPER.put("image/jpg", SimpleAfirmaMessages.getString("ValidationInfoDialog.39"));
        MIMETYPE_MAPPER.put("image/jpeg", SimpleAfirmaMessages.getString("ValidationInfoDialog.31"));
        MIMETYPE_MAPPER.put("image/png", SimpleAfirmaMessages.getString("ValidationInfoDialog.32"));
        MIMETYPE_MAPPER.put("video/mpeg", SimpleAfirmaMessages.getString("ValidationInfoDialog.33"));
        MIMETYPE_MAPPER.put("text/sgml", SimpleAfirmaMessages.getString("ValidationInfoDialog.34"));
        MIMETYPE_MAPPER.put("application/msword", SimpleAfirmaMessages.getString("ValidationInfoDialog.35"));
        MIMETYPE_MAPPER.put("application/vnd.ms-excel", SimpleAfirmaMessages.getString("ValidationInfoDialog.36"));
        MIMETYPE_MAPPER.put("application/vnd.ms-project", SimpleAfirmaMessages.getString("ValidationInfoDialog.37"));
        MIMETYPE_MAPPER.put("application/vnd.ms-powerpoint", SimpleAfirmaMessages.getString("ValidationInfoDialog.38"));
        MIMETYPE_MAPPER.put("application/vnd.ms-works", SimpleAfirmaMessages.getString("ValidationInfoDialog.39"));
    }

    public static String formatToHTML(SignAnalyzer signAnalyzer, List<SignValidity> list) {
        String str = "";
        List<SignDetails> allSignDetails = signAnalyzer.getAllSignDetails();
        if (signAnalyzer.getSignFormat() != null) {
            signFormat = signAnalyzer.getSignFormat();
            str = str + "<p><b>Formato</b>: " + signAnalyzer.getSignFormat() + "</p>";
        }
        String dataLocation = signAnalyzer.getDataLocation();
        if (dataLocation != null) {
            str = str + "<p><b>Localizaci&oacute;n de los datos: </b>" + dataLocation + "</p>";
        }
        if (allSignDetails.size() > 0 && allSignDetails.get(0).getDataObjectFormats() != null && allSignDetails.get(0).getDataObjectFormats().size() > 0) {
            String str2 = (str + "<div><p><b>Informaci&oacute;n sobre datos firmados:</b></p>") + "<ul style=\"margin-bottom: 0;\">";
            for (int i = 0; i < allSignDetails.get(0).getDataObjectFormats().size(); i++) {
                if (allSignDetails.get(0).getDataObjectFormats().get(i).getEncoding() != null && !allSignDetails.get(0).getDataObjectFormats().get(i).getEncoding().isEmpty()) {
                    str2 = str2 + "<li>Encoding: " + allSignDetails.get(0).getDataObjectFormats().get(i).getEncoding() + "</li>";
                }
                if (allSignDetails.get(0).getDataObjectFormats().get(i).getDescription() != null && !allSignDetails.get(0).getDataObjectFormats().get(i).getDescription().isEmpty()) {
                    str2 = str2 + "<li>Descripcion: " + allSignDetails.get(0).getDataObjectFormats().get(i).getDescription() + "</li>";
                }
                String mimeType = allSignDetails.get(0).getDataObjectFormats().get(i).getMimeType();
                if (MIMETYPE_MAPPER.containsKey(mimeType)) {
                    mimeType = MIMETYPE_MAPPER.get(mimeType);
                }
                str2 = str2 + "<li>Tipo de certificado: " + mimeType + "</li>";
            }
            str = str2 + "</ul></div>";
        }
        AOTreeModel signersTree = signAnalyzer.getSignersTree();
        if (signersTree != null) {
            str = ((str + "<div style=\"margin-bottom: 0;\"><p><b>&Aacute;rbol de firmantes:</b><ul style=\"margin-bottom: 0;\">") + parseSignersTree((AOTreeNode) signersTree.getRoot())) + "</ul></p></div>";
        }
        signerIndex = new Integer(1);
        String str3 = str + "<p><b>Resultado de la validaci&oacute;n</b>: " + list.get(0).validityTypeToString() + "</p>";
        boolean z = false;
        for (int i2 = 0; i2 < list.size(); i2++) {
            if (list.get(i2).getError() != null && (SignValidity.VALIDITY_ERROR.MODIFIED_FORM.equals(list.get(i2).getError()) || SignValidity.VALIDITY_ERROR.MODIFIED_DOCUMENT.equals(list.get(i2).getError()) || SignValidity.VALIDITY_ERROR.OVERLAPPING_SIGNATURE.equals(list.get(i2).getError()) || SignValidity.VALIDITY_ERROR.SUSPECTED_SIGNATURE.equals(list.get(i2).getError()) || SignValidity.VALIDITY_ERROR.CANT_VALIDATE_EXTERNALLY_DETACHED.equals(list.get(i2).getError()) || SignValidity.VALIDITY_ERROR.NO_DATA.equals(list.get(i2).getError()))) {
                if (!z) {
                    str3 = str3 + "<ul>";
                    z = true;
                }
                str3 = str3 + "<li>" + list.get(i2) + "</li>";
            }
        }
        for (int i3 = 0; i3 < allSignDetails.size(); i3++) {
            for (int i4 = 0; i4 < allSignDetails.get(i3).getValidityResult().size(); i4++) {
                if (allSignDetails.get(i3).getValidityResult().get(i4).getError() != null && !allSignDetails.get(i3).getValidityResult().get(i4).getError().equals(SignValidity.VALIDITY_ERROR.CERTIFICATE_PROBLEM) && !allSignDetails.get(i3).getValidityResult().get(i4).getError().equals(SignValidity.VALIDITY_ERROR.CERTIFICATE_EXPIRED) && !allSignDetails.get(i3).getValidityResult().get(i4).getError().equals(SignValidity.VALIDITY_ERROR.CANT_VALIDATE_CERT) && !allSignDetails.get(i3).getValidityResult().get(i4).getError().equals(SignValidity.VALIDITY_ERROR.CERTIFICATE_NOT_VALID_YET)) {
                    if (!z) {
                        str3 = str3 + "<ul>";
                        z = true;
                    }
                    str3 = str3 + "<li>Firma " + (i3 + 1) + ": " + allSignDetails.get(i3).getValidityResult().get(i4) + "</li>";
                }
            }
            CertificateDetails signer = allSignDetails.get(i3).getSigner();
            if (signer != null) {
                String str4 = (String) signer.getValidityResult().get("Validacion");
                if (!SimpleAfirmaMessages.getString("ValidationInfoDialog.40").equals(str4)) {
                    if (!z) {
                        str3 = str3 + "<ul>";
                        z = true;
                    }
                    str3 = str3 + "<li>Firma " + (i3 + 1) + ": " + str4 + "</li>";
                }
            }
        }
        if (z) {
            str3 = str3 + "</ul>";
        }
        return str3 + parseSignsToHTML(allSignDetails);
    }

    private static String parseSignsToHTML(List<SignDetails> list) {
        String str;
        String name;
        String str2 = "";
        for (int i = 0; i < list.size(); i++) {
            SignDetails signDetails = list.get(i);
            String str3 = str2 + "<h1>Firma " + (i + 1);
            if (signDetails.getSigner() != null && (name = signDetails.getSigner().getName()) != null && !name.isEmpty()) {
                str3 = str3 + " : " + name;
            }
            String str4 = (str3 + "</h1>") + "<div style=\"border:5px outset black;padding-left: 25px;padding-bottom: 10px;\">";
            if (!FacturaESignAnalyzer.FACTURAE.equals(signFormat)) {
                str4 = str4 + "<p><b>Perfil de firma</b>: " + signDetails.getSignProfile() + "</p>";
            }
            if ("PAdES".equals(signFormat) && signDetails.getCertificationLevel() > -1 && signDetails.getCertificationSign().booleanValue()) {
                switch (signDetails.getCertificationLevel()) {
                    case 1:
                        str4 = str4 + "<p><b>PDF certificado</b>: " + SimpleAfirmaMessages.getString("ValidationInfoDialog.41") + "</p>";
                        break;
                    case 2:
                        str4 = str4 + "<p><b>PDF certificado</b>: " + SimpleAfirmaMessages.getString("ValidationInfoDialog.42") + "</p>";
                        break;
                    case 3:
                        str4 = str4 + "<p><b>PDF certificado</b>: " + SimpleAfirmaMessages.getString("ValidationInfoDialog.43") + "</p>";
                        break;
                }
            }
            if (signDetails.getAlgorithm() != null && !signDetails.getAlgorithm().isEmpty()) {
                str4 = str4 + "<p><b>Algoritmo de firma</b>: " + signDetails.getAlgorithm() + "</p>";
            }
            if (signDetails.getSigningTime() != null) {
                str4 = str4 + "<p><b>Fecha y hora de firma</b>: " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(signDetails.getSigningTime()) + "</p>";
            }
            if (signDetails.getValidityResult() != null && !signDetails.getValidityResult().get(0).getValidity().equals(SignValidity.SIGN_DETAIL_TYPE.OK)) {
                String str5 = str4 + "<div><br><b>Resultado de la validacion</b> :<ul style=\"margin-bottom: 0;\">";
                Iterator<SignValidity> it = signDetails.getValidityResult().iterator();
                while (it.hasNext()) {
                    str5 = str5 + "<li>" + it.next() + "</li>";
                }
                str4 = str5 + "</ul></div>";
            }
            if (signDetails.getPolicy() != null) {
                String str6 = str4 + "<div><p><b>Pol&iacute;tica de firma: </b>";
                if (AOFacturaESigner.POLICY_FACTURAE_30.getPolicyIdentifier().equals(signDetails.getPolicy().getPolicy().getPolicyIdentifier()) || AOFacturaESigner.POLICY_FACTURAE_31.getPolicyIdentifier().equals(signDetails.getPolicy().getPolicy().getPolicyIdentifier()) || SignDetails.POLICY_XADES_AGE_1_9.getPolicyIdentifier().equals(signDetails.getPolicy().getPolicy().getPolicyIdentifier()) || SignDetails.POLICY_XADES_AGE_1_8.getPolicyIdentifier().equals(signDetails.getPolicy().getPolicy().getPolicyIdentifier()) || SignDetails.POLICY_CADES_AGE_1_9.getPolicyIdentifier().equals(signDetails.getPolicy().getPolicy().getPolicyIdentifier()) || SignDetails.POLICY_CADES_AGE_1_8.getPolicyIdentifier().equals(signDetails.getPolicy().getPolicy().getPolicyIdentifier()) || SignDetails.POLICY_PADES_AGE_1_9.getPolicyIdentifier().equals(signDetails.getPolicy().getPolicy().getPolicyIdentifier())) {
                    str = str6 + signDetails.getPolicy().getName() + "</p>";
                } else {
                    String str7 = str6 + "<ul style=\"margin-bottom: 0;\">";
                    String name2 = signDetails.getPolicy().getName();
                    if (name2 != null && !name2.isEmpty()) {
                        str7 = str7 + "<li>Descripci&oacute;n: " + name2 + "</li>";
                    }
                    String policyIdentifier = signDetails.getPolicy().getPolicy().getPolicyIdentifier();
                    if (policyIdentifier != null && !policyIdentifier.isEmpty()) {
                        str7 = str7 + "<li>Identificador: " + policyIdentifier + "</li>";
                    }
                    String policyIdentifierHash = signDetails.getPolicy().getPolicy().getPolicyIdentifierHash();
                    if (policyIdentifierHash != null && !policyIdentifierHash.isEmpty()) {
                        str7 = str7 + "<li>Hash: " + policyIdentifierHash + "</li>";
                    }
                    String policyIdentifierHashAlgorithm = signDetails.getPolicy().getPolicy().getPolicyIdentifierHashAlgorithm();
                    if (policyIdentifierHashAlgorithm != null && !policyIdentifierHashAlgorithm.isEmpty()) {
                        str7 = str7 + "<li>Algoritmo: " + policyIdentifierHashAlgorithm + "</li>";
                    }
                    String string = signDetails.getPolicy().getPolicy().getPolicyQualifier().toString();
                    if (string != null && !string.isEmpty()) {
                        str7 = str7 + "<li>Calificador: " + string + "</li>";
                    }
                    str = str7 + "</ul></p>";
                }
                str4 = str + "</div>";
            }
            if (signDetails.getMetadata() != null && signDetails.getMetadata().size() > 0) {
                String str8 = str4 + "<div><p><b>Metadatos </b>:</p><ul style=\"margin-bottom: 0;\">";
                String str9 = "";
                String str10 = "";
                for (String str11 : signDetails.getMetadata().keySet()) {
                    if (str11.startsWith("claimedRole")) {
                        str9 = str9 + signDetails.getMetadata().get(str11) + ",";
                    }
                }
                if (signDetails.getMetadata().containsKey(STREET_ADDRESS_METADATA)) {
                    str10 = str10 + "<li>" + STREET_ADDRESS_METADATA + ": " + signDetails.getMetadata().get(STREET_ADDRESS_METADATA) + "</li>";
                }
                if (signDetails.getMetadata().containsKey(POSTAL_CODE_METADATA)) {
                    str10 = str10 + "<li>" + POSTAL_CODE_METADATA + ": " + signDetails.getMetadata().get(POSTAL_CODE_METADATA) + "</li>";
                }
                if (signDetails.getMetadata().containsKey(CITY_METADATA)) {
                    str10 = str10 + "<li>" + CITY_METADATA + ": " + signDetails.getMetadata().get(CITY_METADATA) + "</li>";
                }
                if (signDetails.getMetadata().containsKey(LOCALITY_METADATA)) {
                    str10 = str10 + "<li>" + LOCALITY_METADATA + ": " + signDetails.getMetadata().get(LOCALITY_METADATA) + "</li>";
                }
                if (signDetails.getMetadata().containsKey(PROVINCE_METADATA)) {
                    str10 = str10 + "<li>" + PROVINCE_METADATA + ": " + signDetails.getMetadata().get(PROVINCE_METADATA) + "</li>";
                }
                if (signDetails.getMetadata().containsKey(COUNTRY_METADATA)) {
                    str10 = str10 + "<li>" + COUNTRY_METADATA + ": " + signDetails.getMetadata().get(COUNTRY_METADATA) + "</li>";
                }
                if (signDetails.getMetadata().containsKey(SIGN_REASON_METADATA)) {
                    str10 = str10 + "<li>" + SIGN_REASON_METADATA + ": " + signDetails.getMetadata().get(SIGN_REASON_METADATA) + "</li>";
                }
                if (signDetails.getMetadata().containsKey(LOCATION_METADATA)) {
                    str10 = str10 + "<li>" + LOCATION_METADATA + ": " + signDetails.getMetadata().get(LOCATION_METADATA) + "</li>";
                }
                if (signDetails.getMetadata().containsKey(CONTACT_INFO_METADATA)) {
                    str10 = str10 + "<li>" + CONTACT_INFO_METADATA + ": " + signDetails.getMetadata().get(CONTACT_INFO_METADATA) + "</li>";
                }
                if (!str9.isEmpty()) {
                    str8 = str8 + "<li>Roles: " + str9.substring(0, str9.length() - 1) + "</li>";
                }
                str4 = (str8 + str10) + "</ul></div>";
            }
            if (signDetails.getSigner() != null) {
                str4 = str4 + parseCertificatesToHTML(signDetails.getSigner());
            }
            str2 = str4 + "</ul></div>";
        }
        return str2;
    }

    private static String parseCertificatesToHTML(CertificateDetails certificateDetails) {
        String str = "<br><b>Certificado:</b><ul><li>Nombre: " + certificateDetails.getName() + "</li><li>Emisor: " + certificateDetails.getIssuerName() + "</li><li>Fecha de caducidad: " + certificateDetails.getExpirationDate() + "</li>";
        if (certificateDetails.getValidityResult() != null) {
            String str2 = str + "<li>Resultado de la validacion :<ul>";
            for (Object obj : certificateDetails.getValidityResult().keySet()) {
                str2 = str2 + "<li>" + obj + ": " + certificateDetails.getValidityResult().getProperty((String) obj) + "</li>";
            }
            str = str2 + "</ul></li>";
        }
        return str;
    }

    private static String parseSignersTree(AOTreeNode aOTreeNode) {
        String str = "";
        for (int i = 0; i < AOTreeModel.getChildCount(aOTreeNode); i++) {
            AOTreeNode aOTreeNode2 = (AOTreeNode) AOTreeModel.getChild(aOTreeNode, i);
            str = str + "<li>Firma " + signerIndex + ": " + aOTreeNode2.toString() + "</li>";
            Integer num = signerIndex;
            signerIndex = Integer.valueOf(signerIndex.intValue() + 1);
            if (aOTreeNode2.getChildCount() > 0) {
                str = ((str + "<ul style=\"margin-bottom: 0;\">") + parseSignersTree(aOTreeNode2)) + "</ul>";
            }
        }
        return str;
    }
}
