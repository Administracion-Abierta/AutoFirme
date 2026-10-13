package es.gob.afirma.signers.pades;

import com.aowagie.text.Rectangle;
import com.aowagie.text.exceptions.BadPasswordException;
import com.aowagie.text.pdf.AcroFields;
import com.aowagie.text.pdf.PdfArray;
import com.aowagie.text.pdf.PdfDeveloperExtension;
import com.aowagie.text.pdf.PdfDictionary;
import com.aowagie.text.pdf.PdfName;
import com.aowagie.text.pdf.PdfObject;
import com.aowagie.text.pdf.PdfReader;
import com.aowagie.text.pdf.PdfStamper;
import com.aowagie.text.pdf.PdfString;
import com.aowagie.text.pdf.PdfWriter;
import com.sun.jna.platform.win32.COM.tlb.imp.TlbBase;
import es.gob.afirma.core.AOException;
import es.gob.afirma.signers.pades.common.BadPdfPasswordException;
import es.gob.afirma.signers.pades.common.PdfExtraParams;
import es.gob.afirma.signers.pades.common.PdfIsCertifiedException;
import es.gob.afirma.signers.pades.common.PdfIsPasswordProtectedException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.xml.security.keys.content.x509.XMLX509Certificate;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/pades/PdfUtil.class */
public final class PdfUtil {
    private static final String FILTER_ADOBE_PKCS7_DETACHED = "/adbe.pkcs7.detached";
    private static final String RANGE_INDICATOR = "-";
    private static final String RANGE_SEPARATOR = ",";
    private static final String APPEND_PAGE = "append";
    private static final int NEW_PAGE = 0;
    private static final String ALL_PAGES = "all";
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private static final Set<String> SUPPORTED_SUBFILTERS = new HashSet();

    static {
        SUPPORTED_SUBFILTERS.add("/ETSI.RFC3161");
        SUPPORTED_SUBFILTERS.add(FILTER_ADOBE_PKCS7_DETACHED);
        SUPPORTED_SUBFILTERS.add("/ETSI.CAdES.detached");
        SUPPORTED_SUBFILTERS.add("/adbe.pkcs7.sha1");
    }

    private PdfUtil() {
    }

    static boolean isPdfA1(byte[] bArr) {
        if (bArr == null) {
            return false;
        }
        return new String(bArr).replace("\n", "").replace("\r", "").replace(TlbBase.TAB, "").replace(" ", "").contains("<pdfaid:part>1</pdfaid:part>");
    }

    static boolean isPdfAx(byte[] bArr) {
        if (bArr == null) {
            return false;
        }
        return new String(bArr).replace("\n", "").replace("\r", "").replace(TlbBase.TAB, "").replace(" ", "").contains("<pdfaid:part>");
    }

    static GregorianCalendar getSignTime(String str) {
        if (str == null) {
            return new GregorianCalendar();
        }
        try {
            Date date = new SimpleDateFormat("yyyy:MM:dd:HH:mm:ss").parse(str);
            GregorianCalendar gregorianCalendar = new GregorianCalendar();
            gregorianCalendar.setTime(date);
            return gregorianCalendar;
        } catch (Exception e) {
            LOGGER.severe("La fecha indicada ('" + str + "') como momento de firma para PAdES no sigue el patron 'yyyy:MM:dd:HH:mm:ss': " + e);
            return new GregorianCalendar();
        }
    }

    public static PdfReader getPdfReader(byte[] bArr, Properties properties, boolean z) throws BadPdfPasswordException, InvalidPdfException, IOException, PdfIsPasswordProtectedException {
        PdfReader pdfReader;
        Properties properties2 = properties != null ? properties : new Properties();
        String property = properties2.getProperty(PdfExtraParams.OWNER_PASSWORD_STRING);
        String property2 = properties2.getProperty(PdfExtraParams.USER_PASSWORD_STRING);
        try {
            if (property != null) {
                pdfReader = new PdfReader(bArr, property.getBytes());
            } else if (property2 != null) {
                pdfReader = new PdfReader(bArr, property2.getBytes());
            } else {
                pdfReader = new PdfReader(bArr);
            }
            return pdfReader;
        } catch (BadPasswordException e) {
            if (property != null || property2 != null) {
                throw new BadPdfPasswordException("Se ha indicado una contrasena incorrecta para el PDF", e);
            }
            throw new PdfIsPasswordProtectedException("El PDF esta protegido por contrasena para lectura", e);
        } catch (IOException e2) {
            throw new InvalidPdfException(e2);
        }
    }

    static void checkPdfCertification(int i, Properties properties) throws AOException {
        String property = properties.getProperty(PdfExtraParams.ALLOW_SIGNING_CERTIFIED_PDFS);
        if (property != null && Boolean.parseBoolean(property)) {
            return;
        }
        if (!(i != 1)) {
            if (property == null) {
                throw new PdfIsCertifiedException("El PDF esta certificado");
            }
            throw new AOException("El PDF esta certificado y se configuro que no se admitia su firma");
        }
    }

    static void enableLtv(PdfStamper pdfStamper) {
        pdfStamper.getWriter().addDeveloperExtension(new PdfDeveloperExtension(new PdfName("ESIC"), PdfWriter.PDF_VERSION_1_7, 1));
    }

    static boolean getAppendMode(Properties properties, PdfReader pdfReader) {
        return properties.getProperty(PdfExtraParams.OWNER_PASSWORD_STRING) != null || properties.getProperty(PdfExtraParams.USER_PASSWORD_STRING) != null || Boolean.parseBoolean(properties.getProperty(PdfExtraParams.ALWAYS_CREATE_REVISION)) || pdfReader.getAcroFields().getSignatureNames().size() > 0;
    }

    static boolean pdfHasUnregisteredSignatures(byte[] bArr, Properties properties) throws InvalidPdfException, BadPdfPasswordException, IOException, PdfIsPasswordProtectedException {
        Properties properties2 = properties != null ? properties : new Properties();
        return pdfHasUnregisteredSignatures(getPdfReader(bArr, properties2, Boolean.parseBoolean(properties2.getProperty("headless"))));
    }

    static String getFirstSupportedSignSubFilter(byte[] bArr, Properties properties) throws BadPdfPasswordException, InvalidPdfException, IOException, PdfIsPasswordProtectedException {
        if (bArr == null) {
            throw new IllegalArgumentException("El PDF de entrada no puede ser nulo");
        }
        Properties properties2 = properties != null ? properties : new Properties();
        PdfReader pdfReader = getPdfReader(bArr, properties2, Boolean.parseBoolean(properties2.getProperty("headless")));
        for (int i = 0; i < pdfReader.getXrefSize(); i++) {
            PdfObject pdfObject = pdfReader.getPdfObject(i);
            if (pdfObject != null && pdfObject.isDictionary()) {
                PdfDictionary pdfDictionary = (PdfDictionary) pdfObject;
                if (PdfName.SIG.equals(pdfDictionary.get(PdfName.TYPE))) {
                    String string = pdfDictionary.get(PdfName.SUBFILTER) != null ? pdfDictionary.get(PdfName.SUBFILTER).toString() : null;
                    if (SUPPORTED_SUBFILTERS.contains(string)) {
                        return string;
                    }
                } else {
                    continue;
                }
            }
        }
        LOGGER.info("No se ha encontrado ningun filtro de firma soportado, se devolvera null");
        return null;
    }

    static boolean pdfHasUnregisteredSignatures(PdfReader pdfReader) {
        byte[] originalBytes;
        boolean z = false;
        for (int i = 0; i < pdfReader.getXrefSize(); i++) {
            PdfObject pdfObject = pdfReader.getPdfObject(i);
            if (pdfObject != null && pdfObject.isDictionary()) {
                PdfDictionary pdfDictionary = (PdfDictionary) pdfObject;
                if (PdfName.SIG.equals(pdfDictionary.get(PdfName.TYPE))) {
                    String string = pdfDictionary.get(PdfName.SUBFILTER) != null ? pdfDictionary.get(PdfName.SUBFILTER).toString() : null;
                    if (string == null || !SUPPORTED_SUBFILTERS.contains(string)) {
                        z = true;
                        try {
                            PdfObject pdfObject2 = pdfDictionary.get(PdfName.CERT);
                            if (pdfObject2 instanceof PdfString) {
                                originalBytes = ((PdfString) pdfObject2).getOriginalBytes();
                            } else {
                                originalBytes = ((PdfString) ((PdfArray) pdfDictionary.get(PdfName.CERT)).getArrayList().get(0)).getOriginalBytes();
                            }
                            LOGGER.info("Encontrada firma no registrada, hecha con certificado emitido por: " + ((X509Certificate) CertificateFactory.getInstance(XMLX509Certificate.JCA_CERT_ID).generateCertificate(new ByteArrayInputStream(originalBytes))).getIssuerX500Principal().toString());
                        } catch (Exception e) {
                            LOGGER.warning("No se ha podido comprobar la identidad de una firma no registrada con el subfiltro: " + string + ": " + e);
                        }
                    }
                }
            }
        }
        return z;
    }

    /* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/pades/PdfUtil$SignatureField.class */
    public static final class SignatureField {
        private final int signaturePositionOnPageLowerLeftX;
        private final int signaturePositionOnPageLowerLeftY;
        private final int signaturePositionOnPageUpperRightX;
        private final int signaturePositionOnPageUpperRightY;
        private final int page;
        private final String name;

        public SignatureField(int i, int i2, int i3, int i4, int i5, String str) {
            if (str == null) {
                throw new IllegalArgumentException("El campo de firma debe tener un nombre no nulo");
            }
            this.signaturePositionOnPageLowerLeftX = i2;
            this.signaturePositionOnPageLowerLeftY = i3;
            this.signaturePositionOnPageUpperRightX = i4;
            this.signaturePositionOnPageUpperRightY = i5;
            this.page = i;
            this.name = str;
        }

        public String toString() {
            return this.name;
        }

        public String getName() {
            return this.name;
        }

        public int getPage() {
            return this.page;
        }

        public int getSignaturePositionOnPageUpperRightX() {
            return this.signaturePositionOnPageUpperRightX;
        }

        public int getSignaturePositionOnPageUpperRightY() {
            return this.signaturePositionOnPageUpperRightY;
        }

        public int getSignaturePositionOnPageLowerLeftY() {
            return this.signaturePositionOnPageLowerLeftY;
        }

        public int getSignaturePositionOnPageLowerLeftX() {
            return this.signaturePositionOnPageLowerLeftX;
        }
    }

    public static List<SignatureField> getPdfEmptySignatureFields(byte[] bArr) {
        if (bArr == null) {
            return new ArrayList(0);
        }
        try {
            AcroFields acroFields = new PdfReader(bArr).getAcroFields();
            if (acroFields != null) {
                List<String> blankSignatureNames = acroFields.getBlankSignatureNames();
                ArrayList arrayList = new ArrayList();
                if (blankSignatureNames != null) {
                    for (String str : blankSignatureNames) {
                        float[] fieldPositions = acroFields.getFieldPositions(str);
                        if (fieldPositions != null && fieldPositions.length >= 5) {
                            arrayList.add(new SignatureField(Math.round(fieldPositions[0]), Math.round(fieldPositions[1]), Math.round(fieldPositions[2]), Math.round(fieldPositions[3]), Math.round(fieldPositions[4]), str));
                        }
                    }
                    return arrayList;
                }
            }
            return new ArrayList(0);
        } catch (Exception e) {
            LOGGER.severe("Error leyendo el PDF de entrada: " + e);
            return new ArrayList(0);
        }
    }

    public static Rectangle getPositionOnPage(Properties properties, String str) {
        if (properties == null || str == null) {
            LOGGER.severe("Se ha pedido una posicion para un elemento grafico nulo");
            return null;
        }
        if (properties.getProperty(str + "PositionOnPageLowerLeftX") != null && properties.getProperty(str + "PositionOnPageLowerLeftY") != null && properties.getProperty(str + "PositionOnPageUpperRightX") != null && properties.getProperty(str + "PositionOnPageUpperRightY") != null) {
            try {
                return new Rectangle(Integer.parseInt(properties.getProperty(str + "PositionOnPageLowerLeftX").trim()), Integer.parseInt(properties.getProperty(str + "PositionOnPageLowerLeftY").trim()), Integer.parseInt(properties.getProperty(str + "PositionOnPageUpperRightX").trim()), Integer.parseInt(properties.getProperty(str + "PositionOnPageUpperRightY").trim()));
            } catch (Exception e) {
                LOGGER.severe("Se ha indicado una posicion invalida para el elemento grafico '" + str + "': " + e);
                return null;
            }
        }
        return null;
    }

    public static void getPagesRange(String str, int i, List<Integer> list) throws IncorrectPageException {
        int iNormalizePage;
        String strReplace = str.trim().replace(" ", "");
        try {
            iNormalizePage = normalizePage(strReplace, i);
        } catch (NumberFormatException e) {
            iNormalizePage = -1;
        }
        if (iNormalizePage > 0) {
            addPageToResult(iNormalizePage, i, list);
        } else {
            addRangeToResult(strReplace, i, list);
        }
    }

    private static int normalizePage(String str, int i) {
        int i2 = Integer.parseInt(str);
        if (i2 < 0) {
            i2 = i2 + i + 1;
        }
        if (i2 <= 0) {
            i2 = 1;
        }
        if (i2 > i) {
            i2 = i;
        }
        return i2;
    }

    private static void addPageToResult(int i, int i2, List<Integer> list) {
        if (i <= i2 && !list.contains(Integer.valueOf(i))) {
            list.add(Integer.valueOf(i));
        }
    }

    private static void addRangeToResult(String str, int i, List<Integer> list) {
        try {
            int iIndexOf = str.startsWith(RANGE_INDICATOR) ? str.indexOf(RANGE_INDICATOR, 1) : str.indexOf(RANGE_INDICATOR);
            int iNormalizePage = normalizePage(str.substring(0, iIndexOf), i);
            int iNormalizePage2 = normalizePage(str.substring(iIndexOf + RANGE_INDICATOR.length()), i);
            if (iNormalizePage2 < iNormalizePage) {
                throw new IncorrectPageException("Se ha indicado un rango incorrecto: " + (str.length() > 12 ? str.substring(0, 12) + "..." : str));
            }
            for (int i2 = iNormalizePage; i2 <= iNormalizePage2; i2++) {
                addPageToResult(i2, i, list);
            }
        } catch (Exception e) {
            throw new IncorrectPageException("La cadena introducida no se corresponde con un rango de paginas: " + (str.length() > 12 ? str.substring(0, 12) + "..." : str));
        }
    }

    public static void correctPositionSignature(PdfReader pdfReader, List<Integer> list, Rectangle rectangle) {
        for (Integer num : (Integer[]) list.toArray(new Integer[0])) {
            Rectangle pageSizeWithRotation = pdfReader.getPageSizeWithRotation(num.intValue());
            if (pageSizeWithRotation.getWidth() <= rectangle.getLeft() || pageSizeWithRotation.getHeight() <= rectangle.getBottom()) {
                list.remove(num);
            }
        }
        if (list.isEmpty()) {
            throw new InvalidSignaturePositionException("La posicion proporcionada no se encuentra en el rango de ninguna de las paginas a estampar del documento");
        }
        Rectangle pageSizeWithRotation2 = pdfReader.getPageSizeWithRotation(list.get(0).intValue());
        if (rectangle.getTop() > pageSizeWithRotation2.getTop()) {
            rectangle.setTop(pageSizeWithRotation2.getTop());
        }
        if (rectangle.getRight() > pageSizeWithRotation2.getRight()) {
            rectangle.setRight(pageSizeWithRotation2.getRight());
        }
    }

    public static boolean checkPagesRangeInputFormat(String str) {
        if (!str.isEmpty() && !RANGE_SEPARATOR.equals(String.valueOf(str.charAt(str.length() - 1)))) {
            if (str.length() == 1 && RANGE_SEPARATOR.equals(str)) {
                return false;
            }
            for (int i = 0; i < str.length(); i++) {
                if ("0123456789-,".contains(String.valueOf(str.charAt(i)))) {
                    if (i + 2 >= str.length() || !RANGE_INDICATOR.equals(String.valueOf(str.charAt(i))) || !RANGE_INDICATOR.equals(String.valueOf(str.charAt(i + 1))) || !RANGE_INDICATOR.equals(String.valueOf(str.charAt(i + 2)))) {
                        if (i + 1 < str.length() && RANGE_SEPARATOR.equals(String.valueOf(str.charAt(i))) && RANGE_SEPARATOR.equals(String.valueOf(str.charAt(i + 1)))) {
                            return false;
                        }
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            }
            for (String str2 : str.split(RANGE_SEPARATOR)) {
                if (!RANGE_INDICATOR.equals(String.valueOf(str2.charAt(str2.length() - 1)))) {
                    if (str2.length() < 2 || !RANGE_INDICATOR.equals(String.valueOf(str2.charAt(0))) || !RANGE_INDICATOR.equals(String.valueOf(str2.charAt(1)))) {
                        if (str2.length() == 1 && RANGE_INDICATOR.equals(str2)) {
                            return false;
                        }
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public static List<Integer> getPages(Properties properties, int i) {
        String[] strArrSplit;
        if (properties.containsKey(PdfExtraParams.SIGNATURE_PAGES)) {
            strArrSplit = properties.getProperty(PdfExtraParams.SIGNATURE_PAGES).split(RANGE_SEPARATOR);
        } else {
            strArrSplit = properties.containsKey(PdfExtraParams.SIGNATURE_PAGE) ? properties.getProperty(PdfExtraParams.SIGNATURE_PAGE).split(RANGE_SEPARATOR) : new String[0];
        }
        String[] strArr = strArrSplit;
        ArrayList arrayList = new ArrayList();
        if (strArr.length == 0) {
            arrayList.add(Integer.valueOf(i));
        } else if (APPEND_PAGE.equalsIgnoreCase(strArr[0].trim())) {
            arrayList.add(0);
        } else if ("all".equalsIgnoreCase(strArr[0].trim())) {
            for (int i2 = 1; i2 <= i; i2++) {
                arrayList.add(Integer.valueOf(i2));
            }
        } else {
            for (String str : strArr) {
                try {
                    getPagesRange(str, i, arrayList);
                } catch (IncorrectPageException e) {
                    LOGGER.log(Level.WARNING, "Se ha indicado un numero o rango de paginas invalido. Se ignorara.", (Throwable) e);
                }
            }
        }
        Collections.sort(arrayList);
        if (arrayList.isEmpty()) {
            arrayList.add(Integer.valueOf(i));
        }
        return arrayList;
    }
}
