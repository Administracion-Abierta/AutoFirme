package es.gob.afirma.signvalidation;

import com.aowagie.text.pdf.AcroFields;
import com.aowagie.text.pdf.PRAcroForm;
import com.aowagie.text.pdf.PRIndirectReference;
import com.aowagie.text.pdf.PdfArray;
import com.aowagie.text.pdf.PdfDictionary;
import com.aowagie.text.pdf.PdfName;
import com.aowagie.text.pdf.PdfObject;
import com.aowagie.text.pdf.PdfReader;
import es.gob.afirma.core.misc.AOFileUtils;
import es.gob.afirma.core.misc.Base64;
import es.gob.afirma.signers.cades.AOCAdESSigner;
import es.gob.afirma.signers.cms.AOCMSSigner;
import es.gob.afirma.signers.odf.AOODFSigner;
import es.gob.afirma.signers.ooxml.AOOOXMLSigner;
import es.gob.afirma.signers.pades.AOPDFSigner;
import es.gob.afirma.signers.pades.common.PdfExtraParams;
import es.gob.afirma.signers.xades.AOFacturaESigner;
import es.gob.afirma.signers.xades.AOXAdESSigner;
import es.gob.afirma.signers.xmldsig.AOXMLDSigSigner;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationWidget;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.xml.security.keys.content.x509.XMLX509Certificate;
import org.slf4j.Marker;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signvalidation/DataAnalizerUtil.class */
public final class DataAnalizerUtil {
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");

    private DataAnalizerUtil() {
    }

    public static X509Certificate isCertificate(byte[] bArr) throws CertificateException {
        if (bArr == null || bArr.length < 1) {
            throw new CertificateException("Los datos eran nulos o vacios");
        }
        CertificateFactory certificateFactory = CertificateFactory.getInstance(XMLX509Certificate.JCA_CERT_ID);
        try {
            return (X509Certificate) certificateFactory.generateCertificate(new ByteArrayInputStream(bArr));
        } catch (Exception e) {
            try {
                return (X509Certificate) certificateFactory.generateCertificate(new ByteArrayInputStream(Base64.decode(new String(bArr).replace("%0A", "").replace("%2F", "/").replace("%2B", Marker.ANY_NON_NULL_MARKER).replace("%3D", "="))));
            } catch (Exception e2) {
                throw new CertificateException("Los datos proporcionados no son un certificado X.509");
            }
        }
    }

    public static boolean isXML(byte[] bArr) {
        return AOFileUtils.isXML(bArr);
    }

    public static boolean isSignedXML(byte[] bArr) {
        try {
            return new AOXAdESSigner().isSign(bArr) || new AOXMLDSigSigner().isSign(bArr);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isPDF(byte[] bArr) {
        try {
            return new AOPDFSigner().isValidDataFile(bArr);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isSignedPDF(byte[] bArr) {
        try {
            return new AOPDFSigner().isSign(bArr);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isBinary(byte[] bArr) {
        try {
            return new AOCMSSigner().isValidDataFile(bArr);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isSignedBinary(byte[] bArr) {
        try {
            return new AOCMSSigner().isSign(bArr) || new AOCAdESSigner().isSign(bArr);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isFacturae(byte[] bArr) {
        try {
            return new AOFacturaESigner().isValidDataFile(bArr);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isSignedFacturae(byte[] bArr) {
        try {
            return new AOFacturaESigner().isSign(bArr);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isODF(byte[] bArr) {
        try {
            return new AOODFSigner().isValidDataFile(bArr);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Ocurrio un error al analizar los datos: " + e);
            return false;
        }
    }

    public static boolean isSignedODF(byte[] bArr) {
        try {
            return new AOODFSigner().isSign(bArr);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Ocurrio un error al analizar los datos: " + e);
            return false;
        }
    }

    public static boolean isOOXML(byte[] bArr) {
        try {
            return new AOOOXMLSigner().isValidDataFile(bArr);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Ocurrio un error al analizar los datos: " + e);
            return false;
        }
    }

    public static boolean isSignedOOXML(byte[] bArr) {
        try {
            return new AOOOXMLSigner().isSign(bArr);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Ocurrio un error al analizar los datos: " + e);
            return false;
        }
    }

    /* JADX WARN: Not initialized variable reg: 10, insn: 0x0166: MOVE (r0 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r10 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) A[TRY_LEAVE], block:B:64:0x0166 */
    /* JADX WARN: Not initialized variable reg: 11, insn: 0x016b: MOVE (r0 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r11 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]), block:B:66:0x016b */
    /* JADX WARN: Not initialized variable reg: 8, insn: 0x01ba: MOVE (r0 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r8 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) A[TRY_LEAVE], block:B:86:0x01ba */
    /* JADX WARN: Not initialized variable reg: 9, insn: 0x01be: MOVE (r0 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r9 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]), block:B:88:0x01be */
    public static SignValidity checkPdfShadowAttack(byte[] bArr, InputStream inputStream, String str) throws IOException {
        AutoCloseable autoCloseable;
        Throwable th;
        AutoCloseable autoCloseable2;
        Throwable th2;
        try {
            try {
                PDDocument pDDocumentLoad = PDDocument.load(bArr);
                Throwable th3 = null;
                try {
                    try {
                        PDDocument pDDocumentLoad2 = PDDocument.load(inputStream);
                        Throwable th4 = null;
                        PDFRenderer pDFRenderer = new PDFRenderer(pDDocumentLoad);
                        PDFRenderer pDFRenderer2 = new PDFRenderer(pDDocumentLoad2);
                        int numberOfPages = PdfExtraParams.PAGES_TO_CHECK_PSA_VALUE_ALL.equalsIgnoreCase(str) ? pDDocumentLoad.getNumberOfPages() : Integer.parseInt(str);
                        for (int i = 0; i < numberOfPages; i++) {
                            if (checkSignatureOverlaping(pDDocumentLoad.getPage(i).getAnnotations())) {
                                SignValidity signValidity = new SignValidity(SignValidity.SIGN_DETAIL_TYPE.PENDING_CONFIRM_BY_USER, SignValidity.VALIDITY_ERROR.OVERLAPPING_SIGNATURE);
                                if (pDDocumentLoad2 != null) {
                                    if (0 != 0) {
                                        try {
                                            pDDocumentLoad2.close();
                                        } catch (Throwable th5) {
                                            th4.addSuppressed(th5);
                                        }
                                    } else {
                                        pDDocumentLoad2.close();
                                    }
                                }
                                if (pDDocumentLoad != null) {
                                    if (0 != 0) {
                                        try {
                                            pDDocumentLoad.close();
                                        } catch (Throwable th6) {
                                            th3.addSuppressed(th6);
                                        }
                                    } else {
                                        pDDocumentLoad.close();
                                    }
                                }
                                return signValidity;
                            }
                            if (!checkImagesChanges(pDFRenderer.renderImageWithDPI(i, 40.0f, ImageType.GRAY), pDFRenderer2.renderImageWithDPI(i, 40.0f, ImageType.GRAY))) {
                                SignValidity signValidity2 = new SignValidity(SignValidity.SIGN_DETAIL_TYPE.PENDING_CONFIRM_BY_USER, SignValidity.VALIDITY_ERROR.MODIFIED_DOCUMENT);
                                if (pDDocumentLoad2 != null) {
                                    if (0 != 0) {
                                        try {
                                            pDDocumentLoad2.close();
                                        } catch (Throwable th7) {
                                            th4.addSuppressed(th7);
                                        }
                                    } else {
                                        pDDocumentLoad2.close();
                                    }
                                }
                                if (pDDocumentLoad != null) {
                                    if (0 != 0) {
                                        try {
                                            pDDocumentLoad.close();
                                        } catch (Throwable th8) {
                                            th3.addSuppressed(th8);
                                        }
                                    } else {
                                        pDDocumentLoad.close();
                                    }
                                }
                                return signValidity2;
                            }
                            LOGGER.log(Level.WARNING, "Error al cargar el fichero, se salta al siguiente");
                            return null;
                        }
                        if (pDDocumentLoad2 != null) {
                            if (0 != 0) {
                                try {
                                    pDDocumentLoad2.close();
                                } catch (Throwable th9) {
                                    th4.addSuppressed(th9);
                                }
                            } else {
                                pDDocumentLoad2.close();
                            }
                        }
                        if (pDDocumentLoad != null) {
                            if (0 != 0) {
                                try {
                                    pDDocumentLoad.close();
                                } catch (Throwable th10) {
                                    th3.addSuppressed(th10);
                                }
                            } else {
                                pDDocumentLoad.close();
                            }
                        }
                        return null;
                    } catch (Throwable th11) {
                        if (autoCloseable2 != null) {
                            if (th2 != null) {
                                try {
                                    autoCloseable2.close();
                                } catch (Throwable th12) {
                                    th2.addSuppressed(th12);
                                }
                            } else {
                                autoCloseable2.close();
                            }
                        }
                        throw th11;
                    }
                } catch (Throwable th13) {
                    throw th13;
                }
            } catch (Throwable th14) {
                if (autoCloseable != null) {
                    if (th != null) {
                        try {
                            autoCloseable.close();
                        } catch (Throwable th15) {
                            th.addSuppressed(th15);
                        }
                    } else {
                        autoCloseable.close();
                    }
                }
                throw th14;
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error al cargar el fichero, se salta al siguiente");
            return null;
        }
    }

    public static boolean checkImagesChanges(BufferedImage bufferedImage, BufferedImage bufferedImage2) {
        return imageDimensionsEqual(bufferedImage, bufferedImage2) && drawSubtractionImage(bufferedImage, bufferedImage2, null) == 0;
    }

    private static boolean imageDimensionsEqual(BufferedImage bufferedImage, BufferedImage bufferedImage2) {
        if (bufferedImage.getWidth() != bufferedImage2.getWidth() || bufferedImage.getHeight() != bufferedImage2.getHeight()) {
            LOGGER.log(Level.WARNING, "Las imagenes no tienen el mismo tamano");
            return false;
        }
        return true;
    }

    private static int drawSubtractionImage(BufferedImage bufferedImage, BufferedImage bufferedImage2, BufferedImage bufferedImage3) {
        int i = 0;
        for (int i2 = 0; i2 < bufferedImage.getHeight() && i2 < bufferedImage2.getHeight(); i2++) {
            for (int i3 = 0; i3 < bufferedImage.getWidth() && i3 < bufferedImage2.getWidth(); i3++) {
                int rgb = bufferedImage.getRGB(i3, i2);
                int rgb2 = bufferedImage2.getRGB(i3, i2);
                int iAbs = Math.abs(((rgb >> 16) & 255) - ((rgb2 >> 16) & 255)) + Math.abs(((rgb >> 8) & 255) - ((rgb2 >> 8) & 255)) + Math.abs((rgb & 255) - (rgb2 & 255));
                if (iAbs > 0) {
                    i++;
                }
                if (bufferedImage3 != null) {
                    int i4 = iAbs / 3;
                    bufferedImage3.setRGB(i3, i2, (i4 << 16) | (i4 << 8) | i4);
                }
            }
        }
        return i;
    }

    private static boolean checkSignatureOverlaping(List<PDAnnotation> list) {
        int i = 0;
        while (i < list.size() - 1) {
            if (list.get(i) instanceof PDAnnotationWidget) {
                Rectangle2D javaRectangle = toJavaRectangle(list.get(i).getRectangle());
                if (javaRectangle.getWidth() != 0.0d && javaRectangle.getHeight() != 0.0d) {
                    i++;
                    if (checkIsOverlapping(javaRectangle, list, i)) {
                        return true;
                    }
                }
            }
            i++;
        }
        return false;
    }

    private static boolean checkIsOverlapping(Rectangle2D rectangle2D, List<PDAnnotation> list, int i) {
        for (int i2 = i; i2 < list.size(); i2++) {
            Rectangle2D javaRectangle = toJavaRectangle(list.get(i2).getRectangle());
            if (rectangle2D.getMinX() > javaRectangle.getMaxX() || javaRectangle.getMinX() > rectangle2D.getMaxX() || rectangle2D.getMinY() > javaRectangle.getMaxY() || javaRectangle.getMinY() > rectangle2D.getMaxY()) {
                return false;
            }
        }
        return true;
    }

    private static Rectangle2D toJavaRectangle(PDRectangle pDRectangle) {
        return new Rectangle2D.Float(pDRectangle.getLowerLeftX(), pDRectangle.getLowerLeftY(), pDRectangle.getUpperRightX() - pDRectangle.getLowerLeftX(), pDRectangle.getUpperRightY() - pDRectangle.getLowerLeftY());
    }

    public static Map<String, String> checkPDFForm(PdfReader pdfReader) throws IOException {
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        Map<String, PdfObject> fieldValues = getFieldValues(pdfReader);
        AcroFields acroFields = pdfReader.getAcroFields();
        List<String> signatureNames = acroFields.getSignatureNames();
        InputStream inputStreamExtractRevision = acroFields.extractRevision(signatureNames.get(signatureNames.size() - 1));
        Throwable th = null;
        try {
            try {
                PdfReader pdfReader2 = new PdfReader(inputStreamExtractRevision);
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
                Map<String, PdfObject> fieldValues2 = getFieldValues(pdfReader2);
                for (String str : fieldValues2.keySet()) {
                    PdfObject pdfObject = fieldValues2.get(str);
                    PdfObject pdfObjectRemove = fieldValues.remove(str);
                    if (pdfObject != null) {
                        String string = pdfObject.toString();
                        if (pdfObjectRemove != null) {
                            String string2 = pdfObjectRemove.toString();
                            if (!string.equals(string2)) {
                                linkedHashMap.put(str, "El campo " + str + " ha sido modificado tras firma: " + string + " / " + string2);
                            }
                        } else {
                            linkedHashMap.put(str, "El campo " + str + " ha sido modificado tras firma: " + string + " / null");
                        }
                    } else if (pdfObjectRemove != null) {
                        linkedHashMap.put(str, "El campo " + str + " ha sido modificado tras firma: null / " + pdfObjectRemove.toString());
                    }
                }
                if (linkedHashMap.isEmpty()) {
                    return null;
                }
                return linkedHashMap;
            } catch (Throwable th3) {
                th = th3;
                throw th3;
            }
        } catch (Throwable th4) {
            if (inputStreamExtractRevision != null) {
                if (th != null) {
                    try {
                        inputStreamExtractRevision.close();
                    } catch (Throwable th5) {
                        th.addSuppressed(th5);
                    }
                } else {
                    inputStreamExtractRevision.close();
                }
            }
            throw th4;
        }
    }

    private static Map<String, PdfObject> getFieldValues(PdfReader pdfReader) {
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        ArrayList fields = pdfReader.getAcroForm().getFields();
        for (int i = 0; i < fields.size(); i++) {
            PRAcroForm.FieldInformation fieldInformation = (PRAcroForm.FieldInformation) fields.get(i);
            if (fieldInformation.getInfo() != null) {
                String string = null;
                if (fieldInformation.getInfo().get(PdfName.FT) != null) {
                    string = fieldInformation.getInfo().get(PdfName.FT).toString();
                }
                if (string == null || !string.equals("/Sig")) {
                    linkedHashMap.put(fieldInformation.getName(), fieldInformation.getInfo().get(PdfName.V));
                }
            }
        }
        for (int i2 = 1; i2 <= pdfReader.getNumberOfPages(); i2++) {
            PdfArray asArray = pdfReader.getPageN(i2).getAsArray(PdfName.ANNOTS);
            if (asArray != null) {
                for (int i3 = 0; i3 < asArray.size(); i3++) {
                    PdfDictionary pdfDictionary = null;
                    PdfObject pdfObject = asArray.getPdfObject(i3);
                    if (pdfObject instanceof PRIndirectReference) {
                        pdfDictionary = (PdfDictionary) pdfReader.getPdfObject(((PRIndirectReference) pdfObject).getNumber());
                    } else if (pdfObject instanceof PdfDictionary) {
                        pdfDictionary = (PdfDictionary) pdfObject;
                    }
                    if (pdfDictionary != null && PdfName.ANNOT.equals(pdfDictionary.get(PdfName.TYPE)) && PdfName.WIDGET.equals(pdfDictionary.get(PdfName.SUBTYPE)) && !PdfName.SIG.equals(pdfDictionary.get(PdfName.FT))) {
                        PdfObject pdfObject2 = pdfDictionary.get(PdfName.T);
                        String string2 = pdfObject2 != null ? pdfObject2.toString() : "P" + i2 + "A" + i3;
                        if (!linkedHashMap.containsKey(string2)) {
                            linkedHashMap.put(string2, pdfDictionary.get(PdfName.V));
                        }
                    }
                }
            }
        }
        return linkedHashMap;
    }
}
