package es.gob.afirma.signers.pades;

import com.aowagie.text.DocumentException;
import com.aowagie.text.ElementTags;
import com.aowagie.text.ExceptionConverter;
import com.aowagie.text.Font;
import com.aowagie.text.Image;
import com.aowagie.text.Phrase;
import com.aowagie.text.Rectangle;
import com.aowagie.text.pdf.BaseFont;
import com.aowagie.text.pdf.ByteBuffer;
import com.aowagie.text.pdf.ColumnText;
import com.aowagie.text.pdf.PdfSignatureAppearance;
import com.aowagie.text.pdf.PdfStamper;
import com.aowagie.text.pdf.PdfTemplate;
import es.gob.afirma.core.misc.AOUtil;
import es.gob.afirma.core.misc.Platform;
import es.gob.afirma.signers.pades.common.PdfExtraParams;
import java.awt.Color;
import java.io.IOException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/pades/PdfVisibleAreasUtils.class */
final class PdfVisibleAreasUtils {
    private static final int DEFAULT_LAYER_2_FONT_SIZE = 12;
    private static final int UNDEFINED = -1;
    private static final String BLACK = "black";
    private static final String LAYERTEXT_TAG_DELIMITER = "$$";
    static final String LAYERTEXT_TAG_DATE_PREFIX = "$$SIGNDATE";
    private static final String LAYERTEXT_TAG_DATE_DELIMITER = "=";
    static final String LAYERTEXT_TAG_SUBJECTCN = "$$SUBJECTCN$$";
    static final String LAYERTEXT_TAG_PSEUDONYM = "$$PSEUDONYM$$";
    static final String LAYERTEXT_TAG_OU = "$$OU$$";
    static final String LAYERTEXT_TAG_OUS = "$$OUS$$";
    static final String LAYERTEXT_TAG_TITLE = "$$TITLE$$";
    private static final String LAYERTEXT_TAG_SUBJECTDN = "$$SUBJECTDN$$";
    private static final String LAYERTEXT_TAG_ISSUERCN = "$$ISSUERCN$$";
    private static final String LAYERTEXT_TAG_CERTSERIAL = "$$CERTSERIAL$$";
    private static final String LAYERTEXT_TAG_GIVENNAME = "$$GIVENNAME$$";
    private static final String LAYERTEXT_TAG_SURNAME = "$$SURNAME$$";
    private static final String LAYERTEXT_TAG_ORGANIZATION = "$$ORGANIZATION$$";
    static final String LAYERTEXT_TAG_REASON = "$$REASON$$";
    static final String LAYERTEXT_TAG_LOCATION = "$$LOCATION$$";
    private static final String LAYERTEXT_TAG_CONTACT = "$$CONTACT$$";
    private static final float MARGIN = 2.0f;
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private static final Map<String, ColorValues> COLORS = new HashMap(7);

    static {
        COLORS.put(BLACK, new ColorValues(0, 0, 0));
        COLORS.put("white", new ColorValues(255, 255, 255));
        COLORS.put("lightGray", new ColorValues(192, 192, 192));
        COLORS.put("gray", new ColorValues(128, 128, 128));
        COLORS.put("darkGray", new ColorValues(64, 64, 64));
        COLORS.put(ElementTags.RED, new ColorValues(255, 0, 0));
        COLORS.put("pink", new ColorValues(255, 175, 175));
    }

    private PdfVisibleAreasUtils() {
    }

    static Font getFont(int i, int i2, int i3, String str, boolean z) {
        Class<?> cls;
        int i4 = i == -1 ? 0 : i;
        int i5 = i2 == -1 ? 12 : i2;
        int i6 = i3 == -1 ? 0 : i3;
        try {
            BaseFont baseFont = getBaseFont(i, z);
            String lowerCase = str != null ? str.toLowerCase() : BLACK;
            ColorValues colorValues = COLORS.get(lowerCase) != null ? COLORS.get(lowerCase) : COLORS.get(BLACK);
            try {
                if (Platform.getOS() == Platform.OS.ANDROID || Platform.getOS() == Platform.OS.OTHER) {
                    cls = Class.forName("harmony.java.awt.Color");
                } else {
                    cls = Class.forName("java.awt.Color");
                }
                return (Font) Font.class.getConstructor(BaseFont.class, Float.TYPE, Integer.TYPE, cls).newInstance(baseFont, Float.valueOf(i5), Integer.valueOf(i6), cls.getConstructor(Integer.TYPE, Integer.TYPE, Integer.TYPE).newInstance(Integer.valueOf(colorValues.getR()), Integer.valueOf(colorValues.getG()), Integer.valueOf(colorValues.getB())));
            } catch (Exception e) {
                LOGGER.warning("Error estableciendo el color del tipo de letra para la firma visible PDF, se usara el por defecto: " + e);
                return new Font(baseFont, i5, i6, (Color) null);
            }
        } catch (Exception e2) {
            LOGGER.warning("Error construyendo la fuente de letra para la firma visible PDF, se usara la por defecto y el PDF no sera compatible PDF/A: " + e2);
            return new Font(i4, i5, i6, (Color) null);
        }
    }

    private static BaseFont getBaseFont(int i, boolean z) throws IOException, DocumentException {
        BaseFont baseFontLoadInternalFont;
        if (z) {
            try {
                baseFontLoadInternalFont = loadFontToEmbed(i);
            } catch (Throwable th) {
                LOGGER.log(Level.WARNING, "No se ha podido cargar la fuente de letra para incrustar. Puede que el resultado no sea un PDF/A", th);
                baseFontLoadInternalFont = loadInternalFont(i);
            }
        } else {
            baseFontLoadInternalFont = loadInternalFont(i);
        }
        return baseFontLoadInternalFont;
    }

    private static BaseFont loadFontToEmbed(int i) throws IOException, DocumentException {
        BaseFont baseFontCreateFont;
        switch (i) {
            case 0:
            default:
                baseFontCreateFont = BaseFont.createFont("/fonts/courier.ttf", BaseFont.IDENTITY_H, true);
                break;
            case 1:
                baseFontCreateFont = BaseFont.createFont("/fonts/helvetica.ttf", BaseFont.IDENTITY_H, true);
                break;
            case 2:
                baseFontCreateFont = BaseFont.createFont("/fonts/times.ttf", BaseFont.IDENTITY_H, true);
                break;
        }
        baseFontCreateFont.setSubset(false);
        return baseFontCreateFont;
    }

    private static BaseFont loadInternalFont(int i) throws IOException, DocumentException {
        BaseFont baseFontCreateFont;
        switch (i) {
            case 0:
            default:
                baseFontCreateFont = BaseFont.createFont("Courier", "", false);
                break;
            case 1:
                baseFontCreateFont = BaseFont.createFont("Helvetica", "", false);
                break;
            case 2:
                baseFontCreateFont = BaseFont.createFont("Times-Roman", "", false);
                break;
        }
        return baseFontCreateFont;
    }

    static String getLayerText(String str, X509Certificate x509Certificate, Calendar calendar, String str2, String str3, String str4, boolean z, String str5) {
        String str6;
        String strReplace;
        if (str == null) {
            return null;
        }
        String strReplace2 = str;
        if (x509Certificate != null) {
            PdfTextMask pdfTextMaskPrepareMask = prepareMask(z, str5);
            String cn = AOUtil.getCN(x509Certificate);
            if (cn != null && pdfTextMaskPrepareMask != null) {
                cn = obfuscateIds(cn, pdfTextMaskPrepareMask);
            }
            String strReplace3 = strReplace2.replace(LAYERTEXT_TAG_SUBJECTCN, cn).replace(LAYERTEXT_TAG_ISSUERCN, AOUtil.getCN(x509Certificate.getIssuerX500Principal().getName())).replace(LAYERTEXT_TAG_CERTSERIAL, x509Certificate.getSerialNumber().toString());
            String string = x509Certificate.getSubjectX500Principal().toString();
            String rDNvalueFromLdapName = AOUtil.getRDNvalueFromLdapName("OID.2.5.4.65", string);
            if (rDNvalueFromLdapName != null) {
                strReplace3 = strReplace3.replace(LAYERTEXT_TAG_PSEUDONYM, rDNvalueFromLdapName);
            }
            String[] ous = AOUtil.getOUS(string);
            if (ous.length > 0) {
                String strReplace4 = strReplace3.replace(LAYERTEXT_TAG_OU, ous[0]);
                String str7 = ous[0];
                for (int i = 1; i < ous.length; i++) {
                    str7 = str7 + ", " + ous[i];
                }
                strReplace = strReplace4.replace(LAYERTEXT_TAG_OUS, str7);
            } else {
                strReplace = strReplace3.replace(LAYERTEXT_TAG_OU, "").replace(LAYERTEXT_TAG_OUS, "");
            }
            String rDNvalueFromLdapName2 = AOUtil.getRDNvalueFromLdapName("t", string);
            String strReplace5 = strReplace.replace(LAYERTEXT_TAG_TITLE, rDNvalueFromLdapName2 != null ? rDNvalueFromLdapName2 : "");
            if (string != null && pdfTextMaskPrepareMask != null) {
                string = obfuscateIds(string, pdfTextMaskPrepareMask);
            }
            String strReplace6 = strReplace5.replace(LAYERTEXT_TAG_SUBJECTDN, string);
            String rDNvalueFromLdapName3 = AOUtil.getRDNvalueFromLdapName("GIVENNAME", string);
            String strReplace7 = strReplace6.replace(LAYERTEXT_TAG_GIVENNAME, rDNvalueFromLdapName3 != null ? rDNvalueFromLdapName3 : "");
            String rDNvalueFromLdapName4 = AOUtil.getRDNvalueFromLdapName("SURNAME", string);
            String strReplace8 = strReplace7.replace(LAYERTEXT_TAG_SURNAME, rDNvalueFromLdapName4 != null ? rDNvalueFromLdapName4 : "");
            String rDNvalueFromLdapName5 = AOUtil.getRDNvalueFromLdapName("o", string);
            strReplace2 = strReplace8.replace(LAYERTEXT_TAG_ORGANIZATION, rDNvalueFromLdapName5 != null ? rDNvalueFromLdapName5 : "");
        }
        String strReplace9 = strReplace2.replace(LAYERTEXT_TAG_REASON, str2 != null ? str2 : "").replace(LAYERTEXT_TAG_LOCATION, str3 != null ? str3 : "").replace(LAYERTEXT_TAG_CONTACT, str4 != null ? str4 : "");
        while (true) {
            String str8 = strReplace9;
            if (str8.contains(LAYERTEXT_TAG_DATE_PREFIX)) {
                int iIndexOf = str8.indexOf(LAYERTEXT_TAG_DATE_PREFIX);
                String strSubstring = str8.substring(iIndexOf, str8.indexOf(LAYERTEXT_TAG_DELIMITER, iIndexOf + LAYERTEXT_TAG_DATE_PREFIX.length()) + LAYERTEXT_TAG_DELIMITER.length());
                Date time = calendar != null ? calendar.getTime() : new Date();
                if (strSubstring.contains(LAYERTEXT_TAG_DATE_DELIMITER)) {
                    String str9 = strSubstring.replace(LAYERTEXT_TAG_DELIMITER, "").split(LAYERTEXT_TAG_DATE_DELIMITER)[1];
                    try {
                        str6 = new SimpleDateFormat(str9).format(time);
                    } catch (Exception e) {
                        LOGGER.warning("Patron incorrecto para la fecha de firma en la firma visible (" + str9 + "), se usara el por defecto: " + e);
                        str6 = new SimpleDateFormat().format(time);
                    }
                } else {
                    str6 = new SimpleDateFormat().format(time);
                }
                strReplace9 = str8.replace(strSubstring, str6);
            } else {
                return str8;
            }
        }
    }

    private static PdfTextMask prepareMask(boolean z, String str) {
        PdfTextMask param = null;
        if (z) {
            if (str != null) {
                try {
                    param = PdfTextMask.parseParam(str);
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "La mascara de ofuscacion no esta bien definida. Se usara la por defecto", (Throwable) e);
                }
            } else {
                param = new PdfTextMask();
            }
        }
        return param;
    }

    /* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/pades/PdfVisibleAreasUtils$ColorValues.class */
    private static final class ColorValues {
        private final int r;
        private final int g;
        private final int b;

        ColorValues(int i, int i2, int i3) {
            this.r = i;
            this.g = i2;
            this.b = i3;
        }

        int getR() {
            return this.r;
        }

        int getG() {
            return this.g;
        }

        int getB() {
            return this.b;
        }
    }

    static Image buildRotatedSignatureImage(PdfStamper pdfStamper, PdfSignatureAppearance pdfSignatureAppearance, Rectangle rectangle, int i, Image image) throws IOException, DocumentException {
        float width = rectangle.getWidth();
        float height = rectangle.getHeight();
        int i2 = i % 360;
        if (i2 < 0) {
            i2 += 360;
        }
        PdfTemplate pdfTemplateCreateTemplate = PdfTemplate.createTemplate(pdfStamper.getWriter(), (i2 == 0 || i2 == 180) ? width : height, (i2 == 0 || i2 == 180) ? height : width);
        if (image != null) {
            image.setInterpolation(true);
            image.setAbsolutePosition(ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO);
            if (i2 == 90 || i2 == 270) {
                float fMin = Math.min(height / image.getWidth(), width / image.getHeight());
                float width2 = image.getWidth() * fMin;
                float height2 = image.getHeight() * fMin;
                pdfTemplateCreateTemplate.addImage(image, width2, ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO, height2, (height - width2) / MARGIN, (width - height2) / MARGIN);
            } else {
                float fMin2 = Math.min(width / image.getWidth(), height / image.getHeight());
                float width3 = image.getWidth() * fMin2;
                float height3 = image.getHeight() * fMin2;
                pdfTemplateCreateTemplate.addImage(image, width3, ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO, height3, (width - width3) / MARGIN, (height - height3) / MARGIN);
            }
        }
        if (pdfSignatureAppearance.getLayer2Text() != null) {
            Font layer2Font = pdfSignatureAppearance.getLayer2Font();
            if (layer2Font == null) {
                layer2Font = new Font();
            }
            pdfTemplateCreateTemplate.setFontAndSize(layer2Font.getCalculatedBaseFont(false), layer2Font.getSize());
            printText(pdfTemplateCreateTemplate, pdfSignatureAppearance.getLayer2Text(), layer2Font, new Rectangle(pdfTemplateCreateTemplate.getBoundingBox()));
        }
        Image image2 = Image.getInstance(pdfTemplateCreateTemplate);
        image2.setInterpolation(true);
        image2.setRotationDegrees(i2);
        image2.setAbsolutePosition(ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO);
        PdfTemplate pdfTemplateCreateTemplate2 = PdfTemplate.createTemplate(pdfStamper.getWriter(), width, height);
        ByteBuffer internalBuffer = pdfTemplateCreateTemplate.getInternalBuffer();
        Throwable th = null;
        try {
            ByteBuffer internalBuffer2 = pdfTemplateCreateTemplate2.getInternalBuffer();
            Throwable th2 = null;
            try {
                try {
                    internalBuffer2.write(internalBuffer.toByteArray());
                    if (internalBuffer2 != null) {
                        if (0 != 0) {
                            try {
                                internalBuffer2.close();
                            } catch (Throwable th3) {
                                th2.addSuppressed(th3);
                            }
                        } else {
                            internalBuffer2.close();
                        }
                    }
                    if (internalBuffer != null) {
                        if (0 != 0) {
                            try {
                                internalBuffer.close();
                            } catch (Throwable th4) {
                                th.addSuppressed(th4);
                            }
                        } else {
                            internalBuffer.close();
                        }
                    }
                    pdfTemplateCreateTemplate2.reset();
                    pdfTemplateCreateTemplate2.addImage(image2);
                    Image image3 = Image.getInstance(pdfTemplateCreateTemplate2);
                    image3.setInterpolation(true);
                    image3.setAbsolutePosition(ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO);
                    return image3;
                } catch (Throwable th5) {
                    th2 = th5;
                    throw th5;
                }
            } catch (Throwable th6) {
                if (internalBuffer2 != null) {
                    if (th2 != null) {
                        try {
                            internalBuffer2.close();
                        } catch (Throwable th7) {
                            th2.addSuppressed(th7);
                        }
                    } else {
                        internalBuffer2.close();
                    }
                }
                throw th6;
            }
        } catch (Throwable th8) {
            if (internalBuffer != null) {
                if (0 != 0) {
                    try {
                        internalBuffer.close();
                    } catch (Throwable th9) {
                        th.addSuppressed(th9);
                    }
                } else {
                    internalBuffer.close();
                }
            }
            throw th8;
        }
    }

    private static void printText(PdfTemplate pdfTemplate, String str, Font font, Rectangle rectangle) throws DocumentException {
        Rectangle rectangle2 = new Rectangle(MARGIN, MARGIN, rectangle.getWidth() - MARGIN, rectangle.getHeight() - MARGIN);
        float fFitText = fitText(font, str, rectangle2, font.getSize(), 0);
        ColumnText columnText = new ColumnText(pdfTemplate);
        columnText.setRunDirection(0);
        columnText.setSimpleColumn(new Phrase(str, font), rectangle2.getLeft(), rectangle2.getBottom(), rectangle2.getRight(), rectangle2.getTop(), fFitText, 0);
        columnText.go();
    }

    private static float fitText(Font font, String str, Rectangle rectangle, float f, int i) {
        float fAbs = f;
        if (fAbs <= ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
            try {
                int i2 = 0;
                int i3 = 0;
                char[] charArray = str.toCharArray();
                for (int i4 = 0; i4 < charArray.length; i4++) {
                    if (charArray[i4] == '\n') {
                        i3++;
                    } else if (charArray[i4] == '\r') {
                        i2++;
                    }
                }
                fAbs = (Math.abs(rectangle.getHeight()) / (Math.max(i2, i3) + 1)) - 0.001f;
            } catch (Exception e) {
                throw new ExceptionConverter(e);
            }
        }
        font.setSize(fAbs);
        Phrase phrase = new Phrase(str, font);
        ColumnText columnText = new ColumnText(null);
        columnText.setSimpleColumn(phrase, rectangle.getLeft(), rectangle.getBottom(), rectangle.getRight(), rectangle.getTop(), f, 0);
        columnText.setRunDirection(i);
        if ((columnText.go(true) & 1) != 0) {
            return fAbs;
        }
        float f2 = 0.0f;
        float f3 = fAbs;
        float f4 = fAbs;
        for (int i5 = 0; i5 < 50; i5++) {
            f4 = (f2 + f3) / MARGIN;
            ColumnText columnText2 = new ColumnText(null);
            font.setSize(f4);
            columnText2.setSimpleColumn(new Phrase(str, font), rectangle.getLeft(), rectangle.getBottom(), rectangle.getRight(), rectangle.getTop(), f4, 0);
            columnText2.setRunDirection(i);
            if ((columnText2.go(true) & 1) != 0) {
                if (f3 - f2 < f4 * 0.1f) {
                    return f4;
                }
                f2 = f4;
            } else {
                f3 = f4;
            }
        }
        return f4;
    }

    static Rectangle getSignaturePositionOnPage(Properties properties) {
        return PdfUtil.getPositionOnPage(properties, "signature");
    }

    static boolean isVisibleSignature(Properties properties) {
        if (properties == null || properties.getProperty(PdfExtraParams.SIGNATURE_FIELD) != null) {
            return true;
        }
        Rectangle positionOnPage = PdfUtil.getPositionOnPage(properties, "signature");
        if (positionOnPage != null && Math.signum(positionOnPage.getWidth()) != ColumnText.GLOBAL_SPACE_CHAR_RATIO && Math.signum(positionOnPage.getHeight()) != ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
            return true;
        }
        return false;
    }

    static String obfuscateIds(String str, PdfTextMask pdfTextMask) {
        char[] charArray = str.toCharArray();
        int i = 0;
        int i2 = 0;
        boolean z = false;
        for (int i3 = 0; i3 < charArray.length; i3++) {
            if (Character.isLetterOrDigit(charArray[i3])) {
                if (Character.isDigit(charArray[i3])) {
                    i++;
                    if (i == pdfTextMask.getMinLength()) {
                        z = true;
                    }
                } else {
                    i = 0;
                }
            } else {
                if (z) {
                    obfuscate(charArray, i2, i3 - i2, pdfTextMask);
                    z = false;
                }
                i2 = i3 + 1;
            }
        }
        if (z) {
            obfuscate(charArray, i2, charArray.length - i2, pdfTextMask);
        }
        return new String(charArray);
    }

    private static void obfuscate(char[] cArr, int i, int i2, PdfTextMask pdfTextMask) {
        int iCountDigits = countDigits(cArr);
        if (iCountDigits >= countPlainPositions(pdfTextMask.getPositions())) {
            boolean[] positions = pdfTextMask.getPositions();
            if (pdfTextMask.isShiftSupported() && iCountDigits < positions.length) {
                positions = new boolean[iCountDigits];
                boolean[] positions2 = pdfTextMask.getPositions();
                int i3 = 0;
                int length = positions2.length - iCountDigits;
                for (int i4 = 0; i4 < positions2.length; i4++) {
                    if (positions2[i4] || i3 >= length) {
                        positions[i4 - i3] = positions2[i4];
                    } else {
                        i3++;
                    }
                }
            }
            int i5 = 0;
            for (int i6 = 0; i6 < i2; i6++) {
                if (Character.isDigit(cArr[i + i6])) {
                    if (i5 >= positions.length || !positions[i5]) {
                        cArr[i + i6] = pdfTextMask.getObfuscatedChar();
                    }
                    i5++;
                } else {
                    cArr[i + i6] = pdfTextMask.getObfuscatedChar();
                }
            }
            return;
        }
        boolean[] positions3 = pdfTextMask.getPositions();
        int length2 = positions3.length - 1;
        for (int i7 = (i + i2) - 1; i7 >= i; i7--) {
            if (length2 < 0 || !positions3[length2]) {
                cArr[i7] = pdfTextMask.getObfuscatedChar();
            }
            length2--;
        }
    }

    private static int countPlainPositions(boolean[] zArr) {
        int i = 0;
        for (boolean z : zArr) {
            if (z) {
                i++;
            }
        }
        return i;
    }

    private static int countDigits(char[] cArr) {
        int i = 0;
        for (char c : cArr) {
            if (Character.isDigit(c)) {
                i++;
            }
        }
        return i;
    }
}
