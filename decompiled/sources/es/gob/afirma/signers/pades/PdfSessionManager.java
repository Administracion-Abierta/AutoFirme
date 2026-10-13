package es.gob.afirma.signers.pades;

import com.aowagie.text.DocumentException;
import com.aowagie.text.Font;
import com.aowagie.text.Image;
import com.aowagie.text.Rectangle;
import com.aowagie.text.exceptions.BadPasswordException;
import com.aowagie.text.exceptions.InvalidPageNumberException;
import com.aowagie.text.pdf.PdfDate;
import com.aowagie.text.pdf.PdfName;
import com.aowagie.text.pdf.PdfPKCS7;
import com.aowagie.text.pdf.PdfReader;
import com.aowagie.text.pdf.PdfSignature;
import com.aowagie.text.pdf.PdfSignatureAppearance;
import com.aowagie.text.pdf.PdfStamper;
import com.aowagie.text.pdf.PdfStamperImp;
import es.gob.afirma.core.AOException;
import es.gob.afirma.core.misc.AOUtil;
import es.gob.afirma.core.signers.AOSignConstants;
import es.gob.afirma.signers.pades.common.BadPdfPasswordException;
import es.gob.afirma.signers.pades.common.PdfExtraParams;
import es.gob.afirma.signers.pades.common.PdfHasUnregisteredSignaturesException;
import es.gob.afirma.signers.pades.common.PdfIsPasswordProtectedException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/pades/PdfSessionManager.class */
public final class PdfSessionManager {
    static final int LAST_PAGE = -1;
    static final int NEW_PAGE = 0;
    private static final int UNDEFINED = -1;
    private static final int CSIZE = 27000;
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private static final int PDF_MAX_VERSION = 7;
    private static final int PDF_MIN_VERSION = 2;
    private static final String DEFAULT_SIGNATURE_ROTATION = "0";

    private PdfSessionManager() {
    }

    public static PdfTriPhaseSession getSessionData(byte[] bArr, Certificate[] certificateArr, Calendar calendar, Properties properties, boolean z) throws IOException, AOException {
        byte[] bArrAddSignHistoryToXmp;
        int i;
        int i2;
        char c;
        int i3;
        int i4;
        int i5;
        Properties properties2 = properties != null ? properties : new Properties();
        boolean z2 = Boolean.parseBoolean(properties2.getProperty(PdfExtraParams.DO_NOT_USE_CERTCHAIN_ON_POSTSIGN));
        int i6 = Integer.parseInt(properties2.getProperty(PdfExtraParams.SIGNATURE_ROTATION, "0"));
        Image image = PdfPreProcessor.getImage(properties2.getProperty(PdfExtraParams.SIGNATURE_RUBRIC_IMAGE), z);
        String property = properties2.getProperty(PdfExtraParams.SIGN_REASON);
        String property2 = properties2.getProperty(PdfExtraParams.SIGNATURE_FIELD);
        String property3 = properties2.getProperty("signatureProductionCity");
        String property4 = properties2.getProperty(PdfExtraParams.SIGNER_CONTACT);
        try {
            bArrAddSignHistoryToXmp = XmpHelper.addSignHistoryToXmp(bArr, calendar, properties2);
        } catch (BadPasswordException e) {
            if (properties2.containsKey(PdfExtraParams.OWNER_PASSWORD_STRING) || properties2.containsKey(PdfExtraParams.USER_PASSWORD_STRING)) {
                throw new BadPdfPasswordException("La contrasena del PDF es incorrecta", e);
            }
            throw new PdfIsPasswordProtectedException("El PDF esta protegido contra lectura", e);
        } catch (Exception e2) {
            LOGGER.warning("No ha podido registrarse la firma en el historico XMP: " + e2);
            bArrAddSignHistoryToXmp = bArr;
        }
        PdfReader pdfReader = PdfUtil.getPdfReader(bArrAddSignHistoryToXmp, properties2, Boolean.parseBoolean(properties2.getProperty("headless")));
        String property5 = properties2.getProperty(PdfExtraParams.SIGNATURE_SUBFILTER);
        String property6 = properties2.getProperty("policyIdentifier");
        String property7 = properties2.getProperty("profile");
        if (property6 != null || AOSignConstants.SIGN_PROFILE_BASELINE.equals(property7)) {
            property5 = AOSignConstants.PADES_SUBFILTER_BES;
            properties2.setProperty(PdfExtraParams.SIGNATURE_SUBFILTER, AOSignConstants.PADES_SUBFILTER_BES);
        }
        try {
            i = properties2.getProperty(PdfExtraParams.CERTIFICATION_LEVEL) != null ? Integer.parseInt(properties2.getProperty(PdfExtraParams.CERTIFICATION_LEVEL).trim()) : -1;
        } catch (Exception e3) {
            LOGGER.warning("Se ha indicado un nivel de certificacion no valido ('" + properties2.getProperty(PdfExtraParams.CERTIFICATION_LEVEL) + "'): " + e3);
            i = -1;
        }
        try {
            i2 = properties2.getProperty(PdfExtraParams.PDF_VERSION) != null ? Integer.parseInt(properties2.getProperty(PdfExtraParams.PDF_VERSION).trim()) : 7;
        } catch (Exception e4) {
            LOGGER.warning("Error en el establecimiento de la version PDF, se usara 7: " + e4);
            i2 = 7;
        }
        if (i2 != -1 && (i2 < 2 || i2 > 7)) {
            LOGGER.warning("Se ha establecido un valor invalido para version, se ignorara: " + i2);
            i2 = -1;
        }
        boolean zIsPdfA1 = PdfUtil.isPdfA1(pdfReader.getMetadata());
        if (zIsPdfA1) {
            LOGGER.info("Detectado PDF-A1, no se comprimira el PDF");
        }
        String layerText = null;
        String layerText2 = null;
        Font font = null;
        if (PdfVisibleAreasUtils.isVisibleSignature(properties2)) {
            boolean z3 = true;
            if (properties2.containsKey(PdfExtraParams.OBFUSCATE_CERT_DATA)) {
                z3 = Boolean.parseBoolean(properties2.getProperty(PdfExtraParams.OBFUSCATE_CERT_DATA));
            } else if (AOUtil.isPseudonymCert((X509Certificate) certificateArr[0])) {
                z3 = false;
            }
            String property8 = properties2.getProperty(PdfExtraParams.OBFUSCATION_MASK);
            layerText = PdfVisibleAreasUtils.getLayerText(properties2.getProperty(PdfExtraParams.LAYER4_TEXT), (certificateArr == null || certificateArr.length <= 0) ? null : (X509Certificate) certificateArr[0], calendar, property, property3, property4, z3, property8);
            String property9 = properties2.getProperty(PdfExtraParams.LAYER2_TEXT);
            if (property9 == null && !properties2.containsKey(PdfExtraParams.SIGNATURE_RUBRIC_IMAGE)) {
                property9 = getDefaultLayer2Text(property != null, property3 != null);
            }
            layerText2 = PdfVisibleAreasUtils.getLayerText(property9, (certificateArr == null || certificateArr.length <= 0) ? null : (X509Certificate) certificateArr[0], calendar, property, property3, property4, z3, property8);
            try {
                i3 = properties2.getProperty(PdfExtraParams.LAYER2_FONTFAMILY) != null ? Integer.parseInt(properties2.getProperty(PdfExtraParams.LAYER2_FONTFAMILY).trim()) : -1;
            } catch (Exception e5) {
                LOGGER.warning("Se ha indicado un tipo de letra no valido para la capa 2 del PDF ('" + properties2.getProperty(PdfExtraParams.LAYER2_FONTFAMILY) + "'): " + e5);
                i3 = -1;
            }
            try {
                i4 = properties2.getProperty(PdfExtraParams.LAYER2_FONTSIZE) != null ? Integer.parseInt(properties2.getProperty(PdfExtraParams.LAYER2_FONTSIZE).trim()) : -1;
            } catch (Exception e6) {
                LOGGER.warning("Se ha indicado un tamano de letra no valido para la capa 2 del PDF ('" + properties2.getProperty(PdfExtraParams.LAYER2_FONTSIZE) + "'): " + e6);
                i4 = -1;
            }
            try {
                i5 = properties2.getProperty(PdfExtraParams.LAYER2_FONTSTYLE) != null ? Integer.parseInt(properties2.getProperty(PdfExtraParams.LAYER2_FONTSTYLE).trim()) : -1;
            } catch (Exception e7) {
                LOGGER.warning("Se ha indicado un estilo de letra no valido para la capa 2 del PDF ('" + properties2.getProperty(PdfExtraParams.LAYER2_FONTSTYLE) + "'): " + e7);
                i5 = -1;
            }
            font = PdfVisibleAreasUtils.getFont(i3, i4, i5, properties2.getProperty(PdfExtraParams.LAYER2_FONTCOLOR), zIsPdfA1);
        }
        PdfUtil.checkPdfCertification(pdfReader.getCertificationLevel(), properties2);
        if (PdfUtil.pdfHasUnregisteredSignatures(pdfReader)) {
            String property10 = properties2.getProperty(PdfExtraParams.ALLOW_COSIGNING_UNREGISTERED_SIGNATURES);
            if (property10 == null || property10.trim().isEmpty()) {
                throw new PdfHasUnregisteredSignaturesException("El PDF contiene firmas sin registrar");
            }
            if (!Boolean.parseBoolean(property10)) {
                throw new AOException("El PDF contiene firmas sin registrar y estas no estan permitidas");
            }
        }
        pdfReader.removeUsageRights();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        if (i2 == -1) {
            c = 0;
        } else {
            try {
                c = Integer.toString(i2).toCharArray()[0];
            } catch (DocumentException e8) {
                LOGGER.severe("Error al crear la firma para estampar: " + e8);
                throw new AOException("Error al crear la firma para estampar", e8);
            } catch (BadPasswordException e9) {
                if (properties2.containsKey(PdfExtraParams.OWNER_PASSWORD_STRING) || properties2.containsKey(PdfExtraParams.USER_PASSWORD_STRING)) {
                    throw new BadPdfPasswordException("La contrasena del PDF es incorrecta", e9);
                }
                throw new PdfIsPasswordProtectedException("El PDF esta protegido contra modificaciones", e9);
            }
        }
        PdfStamper pdfStamperCreateSignature = PdfStamper.createSignature(pdfReader, byteArrayOutputStream, c, null, PdfUtil.getAppendMode(properties2, pdfReader), calendar);
        List<Integer> pages = null;
        Rectangle signaturePositionOnPage = PdfVisibleAreasUtils.getSignaturePositionOnPage(properties2);
        if (signaturePositionOnPage != null) {
            int numberOfPages = pdfReader.getNumberOfPages();
            pages = PdfUtil.getPages(properties2, numberOfPages);
            if (pages.contains(0) && property2 == null) {
                pdfStamperCreateSignature.insertPage(numberOfPages + 1, pdfReader.getPageSizeWithRotation(1));
                pages.remove((Object) 0);
                pages.add(Integer.valueOf(numberOfPages + 1));
            }
            PdfUtil.correctPositionSignature(pdfReader, pages, signaturePositionOnPage);
        }
        PdfSignatureAppearance signatureAppearance = pdfStamperCreateSignature.getSignatureAppearance();
        if (i2 > 2 && !zIsPdfA1 && !"false".equalsIgnoreCase(properties2.getProperty(PdfExtraParams.COMPRESS_PDF))) {
            pdfStamperCreateSignature.setFullCompression();
        }
        if (Boolean.parseBoolean(properties2.getProperty(PdfExtraParams.INCLUDE_QUESTION_MARK))) {
            signatureAppearance.setAcro6Layers(false);
            signatureAppearance.setLayer4Text(PdfSignatureAppearance.questionMark);
        } else {
            signatureAppearance.setAcro6Layers(true);
        }
        PdfUtil.enableLtv(pdfStamperCreateSignature);
        PdfPreProcessor.attachFile(properties2, pdfStamperCreateSignature, z);
        PdfPreProcessor.addImage(properties2, pdfStamperCreateSignature, pdfReader, z);
        signatureAppearance.setRender(0);
        if (property != null) {
            signatureAppearance.setReason(property);
        }
        signatureAppearance.setSignDate(calendar);
        signatureAppearance.setCrypto(null, z2 ? null : certificateArr, null, null);
        if (property3 != null) {
            signatureAppearance.setLocation(property3);
        }
        if (property4 != null) {
            signatureAppearance.setContact(property4);
        }
        if (image != null) {
            signatureAppearance.setLayer2Text("");
            signatureAppearance.setLayer4Text("");
        }
        if (PdfVisibleAreasUtils.isVisibleSignature(properties2)) {
            if (layerText2 != null) {
                signatureAppearance.setLayer2Text(layerText2);
            }
            if (font != null) {
                signatureAppearance.setLayer2Font(font);
            }
            if (layerText != null) {
                signatureAppearance.setLayer4Text(layerText);
            }
            if (signaturePositionOnPage != null && property2 == null) {
                try {
                    if (i6 == 0) {
                        if (image != null) {
                            signatureAppearance.setImage(image);
                            signatureAppearance.setImageScale(-1.0f);
                        }
                    } else {
                        Image imageBuildRotatedSignatureImage = PdfVisibleAreasUtils.buildRotatedSignatureImage(pdfStamperCreateSignature, signatureAppearance, signaturePositionOnPage, i6, image);
                        signatureAppearance.setLayer2Text("");
                        signatureAppearance.setImage(imageBuildRotatedSignatureImage);
                    }
                    signatureAppearance.setVisibleSignature(signaturePositionOnPage, pages != null ? pages.get(0).intValue() : pdfReader.getNumberOfPages(), null);
                } catch (DocumentException e10) {
                    throw new IOException("Error en la insercion de la firma rotada: " + e10, e10);
                } catch (InvalidPageNumberException e11) {
                    LOGGER.warning("Numero de pagina incorrecto. La firma no sera visible: " + e11);
                }
            } else if (property2 != null) {
                signatureAppearance.setVisibleSignature(property2);
            }
        }
        PdfSignature pdfSignature = new PdfSignature(PdfName.ADOBE_PPKLITE, (property5 == null || property5.isEmpty()) ? new PdfName(AOSignConstants.PADES_SUBFILTER_BES) : new PdfName(property5));
        if (signatureAppearance.getSignDate() != null) {
            pdfSignature.setDate(new PdfDate(signatureAppearance.getSignDate()));
        }
        if (certificateArr != null && certificateArr.length > 0 && !z2) {
            pdfSignature.setName(PdfPKCS7.getSubjectFields((X509Certificate) certificateArr[0]).getField("CN"));
        }
        if (signatureAppearance.getReason() != null) {
            pdfSignature.setReason(signatureAppearance.getReason());
        }
        if (signatureAppearance.getLocation() != null) {
            pdfSignature.setLocation(signatureAppearance.getLocation());
        }
        if (signatureAppearance.getContact() != null) {
            pdfSignature.setContact(signatureAppearance.getContact());
        }
        signatureAppearance.setCryptoDictionary(pdfSignature);
        if (i != -1) {
            signatureAppearance.setCertificationLevel(i);
        }
        if (i2 != -1) {
            pdfStamperCreateSignature.getWriter().setPdfVersion(Integer.toString(i2).toCharArray()[0]);
        }
        try {
            signatureAppearance.preClose(reserveSignSizes(properties2), calendar, pages);
            return new PdfTriPhaseSession(signatureAppearance, byteArrayOutputStream, new String(((PdfStamperImp) pdfStamperCreateSignature.getWriter()).getFileID().getBytes()));
        } catch (DocumentException e12) {
            LOGGER.severe("Error al estampar la firma: " + e12);
            throw new AOException("Error al estampar la firma", e12);
        }
    }

    private static String getDefaultLayer2Text(boolean z, boolean z2) {
        StringBuilder sb = new StringBuilder();
        sb.append("Firmado por ").append("$$SUBJECTCN$$").append('\n').append("Fecha: ").append("$$SIGNDATE").append("=dd/MM/yyyy HH:mm:ss z$$");
        if (z) {
            sb.append('\n').append("Motivo: ").append("$$REASON$$");
        }
        if (z2) {
            sb.append('\n').append("Lugar de firma: ").append("$$LOCATION$$");
        }
        return sb.toString();
    }

    public static HashMap<PdfName, Integer> reserveSignSizes(Properties properties) {
        int reservedSignatureSized = getReservedSignatureSized(properties);
        HashMap<PdfName, Integer> map = new HashMap<>();
        map.put(PdfName.CONTENTS, Integer.valueOf((reservedSignatureSized * 2) + 2));
        return map;
    }

    public static int getReservedSignatureSized(Properties properties) {
        int i = CSIZE;
        String property = properties.getProperty(PdfExtraParams.SIGN_RESERVED_SIZE);
        if (property != null && !property.isEmpty()) {
            try {
                i = Integer.parseInt(property);
            } catch (NumberFormatException e) {
                LOGGER.warning("El valor de la propiedad signReservedSize no es valido, se le asignara el valor por defecto (27000):" + e);
            }
        }
        return i;
    }
}
