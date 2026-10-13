package com.aowagie.text.pdf;

import com.aowagie.text.Chunk;
import com.aowagie.text.DocumentException;
import com.aowagie.text.ExceptionConverter;
import com.aowagie.text.Font;
import com.aowagie.text.Image;
import com.aowagie.text.Paragraph;
import com.aowagie.text.Phrase;
import com.aowagie.text.Rectangle;
import com.aowagie.text.exceptions.InvalidPageNumberException;
import com.twelvemonkeys.imageio.metadata.tiff.TIFF;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.security.PrivateKey;
import java.security.cert.CRL;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.codec.digest.MessageDigestAlgorithms;
import org.apache.pdfbox.contentstream.operator.OperatorName;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/PdfSignatureAppearance.class */
public class PdfSignatureAppearance {
    public static final int SignatureRenderDescription = 0;
    public static final int SignatureRenderNameAndDescription = 1;
    public static final int SignatureRenderGraphicAndDescription = 2;
    public static final PdfName SELF_SIGNED = PdfName.ADOBE_PPKLITE;
    public static final int NOT_CERTIFIED = 0;
    public static final int CERTIFIED_NO_CHANGES_ALLOWED = 1;
    public static final int CERTIFIED_FORM_FILLING = 2;
    public static final int CERTIFIED_FORM_FILLING_AND_ANNOTATIONS = 3;
    private static final float TOP_SECTION = 0.0f;
    private static final float MARGIN = 2.0f;
    private Rectangle rect;
    private Rectangle pageRect;
    private final PdfTemplate[] app;
    private PdfTemplate frm;
    private final PdfStamperImp writer;
    private String layer2Text;
    private String reason;
    private String location;
    private Calendar signDate;
    private String provider;
    private int page;
    private String fieldName;
    private PrivateKey privKey;
    private Certificate[] certChain;
    private CRL[] crlList;
    private PdfName filter;
    private boolean newField;
    private ByteBuffer sigout;
    private OutputStream originalout;
    private File tempFile;
    private PdfDictionary cryptoDictionary;
    private PdfStamper stamper;
    private boolean preClosed;
    private PdfSigGenericPKCS sigStandard;
    private int[] range;
    private RandomAccessFile raf;
    private byte[] bout;
    private int boutLen;
    private byte[] externalDigest;
    private byte[] externalRSAdata;
    private String digestEncryptionAlgorithm;
    private HashMap exclusionLocations;
    private int render;
    private Image signatureGraphic;
    public static final String questionMark = "% DSUnknown\nq\n1 G\n1 g\n0.1 0 0 0.1 9 0 cm\n0 J 0 j 4 M []0 d\n1 i \n0 g\n313 292 m\n313 404 325 453 432 529 c\n478 561 504 597 504 645 c\n504 736 440 760 391 760 c\n286 760 271 681 265 626 c\n265 625 l\n100 625 l\n100 828 253 898 381 898 c\n451 898 679 878 679 650 c\n679 555 628 499 538 435 c\n488 399 467 376 467 292 c\n313 292 l\nh\n308 214 170 -164 re\nf\n0.44 G\n1.2 w\n1 1 0.4 rg\n287 318 m\n287 430 299 479 406 555 c\n451 587 478 623 478 671 c\n478 762 414 786 365 786 c\n260 786 245 707 239 652 c\n239 651 l\n74 651 l\n74 854 227 924 355 924 c\n425 924 653 904 653 676 c\n653 581 602 525 512 461 c\n462 425 441 402 441 318 c\n287 318 l\nh\n282 240 170 -164 re\nB\nQ\n";
    private String contact;
    private Font layer2Font;
    private String layer4Text;
    private boolean acro6Layers;
    private int runDirection;
    private SignatureEvent signatureEvent;
    private Image image;
    private float imageScale;
    private int certificationLevel;

    /* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/PdfSignatureAppearance$SignatureEvent.class */
    private interface SignatureEvent {
        void getSignatureDictionary(PdfDictionary pdfDictionary);
    }

    PdfSignatureAppearance(PdfStamperImp writer, Calendar globalDate) {
        this.app = new PdfTemplate[5];
        this.page = 1;
        this.newField = true;
        this.preClosed = false;
        this.render = 0;
        this.signatureGraphic = null;
        this.runDirection = 1;
        this.certificationLevel = 0;
        this.writer = writer;
        this.signDate = globalDate != null ? globalDate : new GregorianCalendar();
        this.fieldName = getNewSigName();
    }

    PdfSignatureAppearance(PdfStamperImp writer, Calendar globalDate, List<PRAcroForm.FieldInformation> signatureFieldNames) {
        this.app = new PdfTemplate[5];
        this.page = 1;
        this.newField = true;
        this.preClosed = false;
        this.render = 0;
        this.signatureGraphic = null;
        this.runDirection = 1;
        this.certificationLevel = 0;
        this.writer = writer;
        this.signDate = globalDate != null ? globalDate : new GregorianCalendar();
        this.fieldName = getNewSigName(signatureFieldNames);
    }

    public int getRender() {
        return this.render;
    }

    public void setRender(int render) {
        this.render = render;
    }

    public Image getSignatureGraphic() {
        return this.signatureGraphic;
    }

    public void setSignatureGraphic(Image signatureGraphic) {
        this.signatureGraphic = signatureGraphic;
    }

    public void setLayer2Text(String text) {
        this.layer2Text = text;
    }

    public String getLayer2Text() {
        return this.layer2Text;
    }

    public void setLayer4Text(String text) {
        this.layer4Text = text;
    }

    public String getLayer4Text() {
        return this.layer4Text;
    }

    public Rectangle getRect() {
        return this.rect;
    }

    public boolean isInvisible() {
        return this.rect == null || this.rect.getWidth() == 0.0f || this.rect.getHeight() == 0.0f;
    }

    public void setCrypto(PrivateKey privKey, Certificate[] certChain, CRL[] crlList, PdfName filter) {
        this.privKey = privKey;
        this.certChain = certChain;
        this.crlList = crlList;
        this.filter = filter;
    }

    public void setVisibleSignature(Rectangle pageRect, int page, String fieldName) {
        if (fieldName != null) {
            if (fieldName.indexOf(46) >= 0) {
                throw new IllegalArgumentException("Field names cannot contain a dot.");
            }
            AcroFields af = this.writer.getAcroFields();
            AcroFields.Item item = af.getFieldItem(fieldName);
            if (item != null) {
                throw new IllegalArgumentException("The field " + fieldName + " already exists.");
            }
            this.fieldName = fieldName;
        }
        if (page < 1 || page > this.writer.reader.getNumberOfPages()) {
            throw new InvalidPageNumberException("Invalid page number: " + page);
        }
        this.pageRect = new Rectangle(pageRect);
        this.pageRect.normalize();
        this.rect = new Rectangle(this.pageRect.getWidth(), this.pageRect.getHeight());
        this.page = page;
    }

    public void setVisibleSignature(String fieldName) {
        AcroFields af = this.writer.getAcroFields();
        AcroFields.Item item = af.getFieldItem(fieldName);
        if (item == null) {
            throw new IllegalArgumentException("The field " + fieldName + " does not exist.");
        }
        PdfDictionary merged = item.getMerged(0);
        if (!PdfName.SIG.equals(PdfReader.getPdfObject(merged.get(PdfName.FT)))) {
            throw new IllegalArgumentException("The field " + fieldName + " is not a signature field.");
        }
        this.newField = false;
        this.fieldName = fieldName;
        PdfArray r = merged.getAsArray(PdfName.RECT);
        float llx = r.getAsNumber(0).floatValue();
        float lly = r.getAsNumber(1).floatValue();
        float urx = r.getAsNumber(2).floatValue();
        float ury = r.getAsNumber(3).floatValue();
        this.pageRect = new Rectangle(llx, lly, urx, ury);
        this.pageRect.normalize();
        this.page = item.getPage(0).intValue();
        int rotation = this.writer.reader.getPageRotation(this.page);
        Rectangle pageSize = this.writer.reader.getPageSizeWithRotation(this.page);
        switch (rotation) {
            case 90:
                this.pageRect = new Rectangle(this.pageRect.getBottom(), pageSize.getTop() - this.pageRect.getLeft(), this.pageRect.getTop(), pageSize.getTop() - this.pageRect.getRight());
                break;
            case 180:
                this.pageRect = new Rectangle(pageSize.getRight() - this.pageRect.getLeft(), pageSize.getTop() - this.pageRect.getBottom(), pageSize.getRight() - this.pageRect.getRight(), pageSize.getTop() - this.pageRect.getTop());
                break;
            case TIFF.TAG_IMAGE_DESCRIPTION /* 270 */:
                this.pageRect = new Rectangle(pageSize.getRight() - this.pageRect.getBottom(), this.pageRect.getLeft(), pageSize.getRight() - this.pageRect.getTop(), this.pageRect.getRight());
                break;
        }
        if (rotation != 0) {
            this.pageRect.normalize();
        }
        this.rect = new Rectangle(this.pageRect.getWidth(), this.pageRect.getHeight());
    }

    public PdfTemplate getLayer(int layer) {
        if (layer < 0 || layer >= this.app.length) {
            return null;
        }
        PdfTemplate t = this.app[layer];
        if (t == null) {
            PdfTemplate[] pdfTemplateArr = this.app;
            PdfTemplate pdfTemplate = new PdfTemplate(this.writer);
            pdfTemplateArr[layer] = pdfTemplate;
            t = pdfTemplate;
            t.setBoundingBox(this.rect);
            this.writer.addDirectTemplateSimple(t, new PdfName(OperatorName.ENDPATH + layer));
        }
        return t;
    }

    public PdfTemplate getTopLayer() {
        if (this.frm == null) {
            this.frm = new PdfTemplate(this.writer);
            this.frm.setBoundingBox(this.rect);
            this.writer.addDirectTemplateSimple(this.frm, new PdfName("FRM"));
        }
        return this.frm;
    }

    public PdfTemplate getAppearance() throws DocumentException {
        Font font;
        String text;
        Font font2;
        Rectangle dataRect;
        if (isInvisible()) {
            PdfTemplate t = new PdfTemplate(this.writer);
            t.setBoundingBox(new Rectangle(0.0f, 0.0f));
            this.writer.addDirectTemplateSimple(t, null);
            return t;
        }
        if (this.app[0] == null) {
            PdfTemplate[] pdfTemplateArr = this.app;
            PdfTemplate t2 = new PdfTemplate(this.writer);
            pdfTemplateArr[0] = t2;
            t2.setBoundingBox(new Rectangle(100.0f, 100.0f));
            this.writer.addDirectTemplateSimple(t2, new PdfName("n0"));
            t2.setLiteral("% DSBlank\n");
        }
        if (this.app[1] == null && !this.acro6Layers) {
            PdfTemplate[] pdfTemplateArr2 = this.app;
            PdfTemplate t3 = new PdfTemplate(this.writer);
            pdfTemplateArr2[1] = t3;
            t3.setBoundingBox(new Rectangle(100.0f, 100.0f));
            this.writer.addDirectTemplateSimple(t3, new PdfName("n1"));
            t3.setLiteral(questionMark);
        }
        if (this.app[2] == null) {
            if (this.layer2Text == null) {
                StringBuffer buf = new StringBuffer();
                buf.append("Firmado por ").append(PdfPKCS7.getSubjectFields((X509Certificate) this.certChain[0]).getField("CN")).append('\n');
                SimpleDateFormat sd = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss z");
                buf.append("Fecha: ").append(sd.format(this.signDate.getTime()));
                if (this.reason != null) {
                    buf.append('\n').append("Motivo: ").append(this.reason);
                }
                if (this.location != null) {
                    buf.append('\n').append("Lugar de firma: ").append(this.location);
                }
                text = buf.toString();
            } else {
                text = this.layer2Text;
            }
            PdfTemplate[] pdfTemplateArr3 = this.app;
            PdfTemplate t4 = new PdfTemplate(this.writer);
            pdfTemplateArr3[2] = t4;
            t4.setBoundingBox(this.rect);
            this.writer.addDirectTemplateSimple(t4, new PdfName("n2"));
            if (this.image != null) {
                if (this.imageScale == 0.0f) {
                    t4.addImage(this.image, this.rect.getWidth(), 0.0f, 0.0f, this.rect.getHeight(), 0.0f, 0.0f);
                } else {
                    float usableScale = this.imageScale;
                    if (this.imageScale < 0.0f) {
                        usableScale = Math.min(this.rect.getWidth() / this.image.getWidth(), this.rect.getHeight() / this.image.getHeight());
                    }
                    float w = this.image.getWidth() * usableScale;
                    float h = this.image.getHeight() * usableScale;
                    t4.addImage(this.image, w, 0.0f, 0.0f, h, (this.rect.getWidth() - w) / MARGIN, (this.rect.getHeight() - h) / MARGIN);
                }
            }
            if (this.layer2Font == null) {
                font2 = new Font();
            } else {
                font2 = new Font(this.layer2Font);
            }
            float size = font2.getSize();
            Rectangle signatureRect = null;
            if (this.render == 1 || (this.render == 2 && this.signatureGraphic != null)) {
                signatureRect = new Rectangle(MARGIN, MARGIN, (this.rect.getWidth() / MARGIN) - MARGIN, this.rect.getHeight() - MARGIN);
                dataRect = new Rectangle((this.rect.getWidth() / MARGIN) + 1.0f, MARGIN, this.rect.getWidth() - 1.0f, this.rect.getHeight() - MARGIN);
                if (this.rect.getHeight() > this.rect.getWidth()) {
                    signatureRect = new Rectangle(MARGIN, this.rect.getHeight() / MARGIN, this.rect.getWidth() - MARGIN, this.rect.getHeight());
                    dataRect = new Rectangle(MARGIN, MARGIN, this.rect.getWidth() - MARGIN, (this.rect.getHeight() / MARGIN) - MARGIN);
                }
            } else {
                dataRect = new Rectangle(MARGIN, MARGIN, this.rect.getWidth() - MARGIN, (this.rect.getHeight() * 1.0f) - MARGIN);
            }
            if (this.render == 1) {
                String signedBy = PdfPKCS7.getSubjectFields((X509Certificate) this.certChain[0]).getField("CN");
                Rectangle sr2 = new Rectangle(signatureRect.getWidth() - MARGIN, signatureRect.getHeight() - MARGIN);
                float signedSize = fitText(font2, signedBy, sr2, -1.0f, this.runDirection);
                ColumnText ct2 = new ColumnText(t4);
                ct2.setRunDirection(this.runDirection);
                ct2.setSimpleColumn(new Phrase(signedBy, font2), signatureRect.getLeft(), signatureRect.getBottom(), signatureRect.getRight(), signatureRect.getTop(), signedSize, 0);
                ct2.go();
            } else if (this.render == 2) {
                ColumnText ct3 = new ColumnText(t4);
                ct3.setRunDirection(this.runDirection);
                ct3.setSimpleColumn(signatureRect.getLeft(), signatureRect.getBottom(), signatureRect.getRight(), signatureRect.getTop(), 0.0f, 2);
                Image im = Image.getInstance(this.signatureGraphic);
                im.scaleToFit(signatureRect.getWidth(), signatureRect.getHeight());
                Paragraph p = new Paragraph();
                float y = (-im.getScaledHeight()) + 15.0f;
                p.add(new Chunk(im, 0.0f + ((signatureRect.getWidth() - im.getScaledWidth()) / MARGIN) + ((signatureRect.getWidth() - im.getScaledWidth()) / MARGIN), y - ((signatureRect.getHeight() - im.getScaledHeight()) / MARGIN), false));
                ct3.addElement(p);
                ct3.go();
            }
            if (size <= 0.0f) {
                Rectangle sr = new Rectangle(dataRect.getWidth(), dataRect.getHeight());
                size = fitText(font2, text, sr, 12.0f, this.runDirection);
            }
            ColumnText ct = new ColumnText(t4);
            ct.setRunDirection(this.runDirection);
            ct.setSimpleColumn(new Phrase(text, font2), dataRect.getLeft(), dataRect.getBottom(), dataRect.getRight(), dataRect.getTop(), size, 0);
            ct.go();
        }
        if (this.app[3] == null && !this.acro6Layers) {
            PdfTemplate[] pdfTemplateArr4 = this.app;
            PdfTemplate t5 = new PdfTemplate(this.writer);
            pdfTemplateArr4[3] = t5;
            t5.setBoundingBox(new Rectangle(100.0f, 100.0f));
            this.writer.addDirectTemplateSimple(t5, new PdfName("n3"));
            t5.setLiteral("% DSBlank\n");
        }
        if (this.app[4] == null && !this.acro6Layers) {
            PdfTemplate[] pdfTemplateArr5 = this.app;
            PdfTemplate t6 = new PdfTemplate(this.writer);
            pdfTemplateArr5[4] = t6;
            t6.setBoundingBox(new Rectangle(0.0f, this.rect.getHeight() * 1.0f, this.rect.getRight(), this.rect.getTop()));
            this.writer.addDirectTemplateSimple(t6, new PdfName("n4"));
            if (this.layer2Font == null) {
                font = new Font();
            } else {
                font = new Font(this.layer2Font);
            }
            font.getSize();
            String text2 = "Signature Not Verified";
            if (this.layer4Text != null) {
                text2 = this.layer4Text;
            }
            Rectangle sr3 = new Rectangle(this.rect.getWidth() - 4.0f, (this.rect.getHeight() * 0.0f) - 4.0f);
            float size2 = fitText(font, text2, sr3, 15.0f, this.runDirection);
            ColumnText ct4 = new ColumnText(t6);
            ct4.setRunDirection(this.runDirection);
            ct4.setSimpleColumn(new Phrase(text2, font), MARGIN, 0.0f, this.rect.getWidth() - MARGIN, this.rect.getHeight() - MARGIN, size2, 0);
            ct4.go();
        }
        int rotation = this.writer.reader.getPageRotation(this.page);
        Rectangle rotated = new Rectangle(this.rect);
        for (int n = rotation; n > 0; n -= 90) {
            rotated = rotated.rotate();
        }
        if (this.frm == null) {
            this.frm = new PdfTemplate(this.writer);
            this.frm.setBoundingBox(rotated);
            this.writer.addDirectTemplateSimple(this.frm, new PdfName("FRM"));
            float scale = Math.min(this.rect.getWidth(), this.rect.getHeight()) * 0.9f;
            float x = (this.rect.getWidth() - scale) / MARGIN;
            float y2 = (this.rect.getHeight() - scale) / MARGIN;
            float scale2 = scale / 100.0f;
            if (rotation == 90) {
                this.frm.concatCTM(0.0f, 1.0f, -1.0f, 0.0f, this.rect.getHeight(), 0.0f);
            } else if (rotation == 180) {
                this.frm.concatCTM(-1.0f, 0.0f, 0.0f, -1.0f, this.rect.getWidth(), this.rect.getHeight());
            } else if (rotation == 270) {
                this.frm.concatCTM(0.0f, -1.0f, 1.0f, 0.0f, 0.0f, this.rect.getWidth());
            }
            this.frm.addTemplate(this.app[0], 0.0f, 0.0f);
            if (!this.acro6Layers) {
                this.frm.addTemplate(this.app[1], scale2, 0.0f, 0.0f, scale2, x, y2);
            }
            this.frm.addTemplate(this.app[2], 0.0f, 0.0f);
            if (!this.acro6Layers) {
                this.frm.addTemplate(this.app[3], scale2, 0.0f, 0.0f, scale2, x, y2);
                this.frm.addTemplate(this.app[4], 0.0f, 0.0f);
            }
        }
        PdfTemplate napp = new PdfTemplate(this.writer);
        napp.setBoundingBox(rotated);
        this.writer.addDirectTemplateSimple(napp, null);
        napp.addTemplate(this.frm, 0.0f, 0.0f);
        return napp;
    }

    private static float fitText(Font font, String text, Rectangle rect, float maxFontSize, int runDirection) {
        if (maxFontSize <= 0.0f) {
            try {
                int cr = 0;
                int lf = 0;
                char[] t = text.toCharArray();
                for (char element : t) {
                    if (element == '\n') {
                        lf++;
                    } else if (element == '\r') {
                        cr++;
                    }
                }
                int minLines = Math.max(cr, lf) + 1;
                maxFontSize = (Math.abs(rect.getHeight()) / minLines) - 0.001f;
            } catch (Exception e) {
                throw new ExceptionConverter(e);
            }
        }
        font.setSize(maxFontSize);
        Phrase ph = new Phrase(text, font);
        ColumnText ct = new ColumnText(null);
        ct.setSimpleColumn(ph, rect.getLeft(), rect.getBottom(), rect.getRight(), rect.getTop(), maxFontSize, 0);
        ct.setRunDirection(runDirection);
        int status = ct.go(true);
        if ((status & 1) != 0) {
            return maxFontSize;
        }
        float min = 0.0f;
        float max = maxFontSize;
        float size = maxFontSize;
        for (int k = 0; k < 50; k++) {
            size = (min + max) / MARGIN;
            ColumnText ct2 = new ColumnText(null);
            font.setSize(size);
            ct2.setSimpleColumn(new Phrase(text, font), rect.getLeft(), rect.getBottom(), rect.getRight(), rect.getTop(), size, 0);
            ct2.setRunDirection(runDirection);
            int status2 = ct2.go(true);
            if ((status2 & 1) != 0) {
                if (max - min < size * 0.1f) {
                    return size;
                }
                min = size;
            } else {
                max = size;
            }
        }
        return size;
    }

    public void setExternalDigest(byte[] digest, byte[] RSAdata, String digestEncryptionAlgorithm) {
        this.externalDigest = digest;
        this.externalRSAdata = RSAdata;
        this.digestEncryptionAlgorithm = digestEncryptionAlgorithm;
    }

    public String getReason() {
        return this.reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getLocation() {
        return this.location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getProvider() {
        return this.provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public PrivateKey getPrivKey() {
        return this.privKey;
    }

    public Certificate[] getCertChain() {
        return this.certChain;
    }

    public CRL[] getCrlList() {
        return this.crlList;
    }

    public PdfName getFilter() {
        return this.filter;
    }

    public boolean isNewField() {
        return this.newField;
    }

    public int getPage() {
        return this.page;
    }

    public String getFieldName() {
        return this.fieldName;
    }

    public Rectangle getPageRect() {
        return this.pageRect;
    }

    public Calendar getSignDate() {
        return this.signDate;
    }

    public void setSignDate(Calendar signDate) {
        this.signDate = signDate;
    }

    void setSigout(ByteBuffer sigout) {
        this.sigout = sigout;
    }

    void setOriginalout(OutputStream originalout) {
        this.originalout = originalout;
    }

    public File getTempFile() {
        return this.tempFile;
    }

    void setTempFile(File tempFile) {
        this.tempFile = tempFile;
    }

    public String getNewSigName() {
        return getNewSigName(null);
    }

    public String getNewSigName(List<PRAcroForm.FieldInformation> signatureFieldNames) {
        AcroFields af = this.writer.getAcroFields();
        int step = 0;
        boolean found = false;
        while (!found) {
            step++;
            String n1 = "Signature" + step;
            if (af.getFieldItem(n1) == null) {
                String nameWithDot = n1 + ".";
                found = true;
                for (Object element : af.getFields().keySet()) {
                    String fn = (String) element;
                    if (fn.startsWith(nameWithDot)) {
                        found = false;
                        break;
                    }
                }
                if (found && signatureFieldNames != null) {
                    for (int i = 0; found && i < signatureFieldNames.size(); i++) {
                        PRAcroForm.FieldInformation fieldInfo = signatureFieldNames.get(i);
                        if (fieldInfo.getName() != null && (fieldInfo.getName().equals(n1) || fieldInfo.getName().startsWith(nameWithDot))) {
                            found = false;
                            break;
                        }
                    }
                }
            }
        }
        String name = "Signature" + step;
        return name;
    }

    void preClose(Calendar globalDate) throws IOException, DocumentException {
        preClose(null, globalDate, null);
    }

    public void preClose() throws IOException, DocumentException {
        preClose(null, null, null);
    }

    public void preClose(HashMap exclusionSizes) throws IOException, DocumentException {
        preClose(exclusionSizes, null, null);
    }

    public void preClose(HashMap exclusionSizes, Calendar globalDate) throws IOException, DocumentException {
        preClose(exclusionSizes, globalDate, null);
    }

    public void preClose(HashMap exclusionSizes, Calendar globalDate, List<Integer> pages) throws IOException, DocumentException {
        if (this.preClosed) {
            throw new DocumentException("Document already pre closed.");
        }
        this.preClosed = true;
        AcroFields af = this.writer.getAcroFields();
        String name = getFieldName();
        boolean fieldExists = !isNewField();
        PdfIndirectReference refSig = this.writer.getPdfIndirectReference();
        this.writer.setSigFlags(3);
        if (fieldExists) {
            PdfDictionary widget = af.getFieldItem(name).getWidget(0);
            this.writer.markUsed(widget);
            widget.put(PdfName.P, this.writer.getPageReference(getPage()));
            widget.put(PdfName.V, refSig);
            PdfObject obj = PdfReader.getPdfObjectRelease(widget.get(PdfName.F));
            int flags = 0;
            if (obj != null && obj.isNumber()) {
                flags = ((PdfNumber) obj).intValue();
            }
            widget.put(PdfName.F, new PdfNumber(flags | 128));
            PdfDictionary ap = new PdfDictionary();
            ap.put(PdfName.N, getAppearance().getIndirectReference());
            widget.put(PdfName.AP, ap);
        } else {
            PdfFormField sigField = PdfFormField.createSignature(this.writer);
            sigField.setFieldName(name);
            sigField.put(PdfName.V, refSig);
            sigField.setFlags(132);
            if (!isInvisible()) {
                sigField.setWidget(getPageRect(), null);
            } else {
                sigField.setWidget(new Rectangle(0.0f, 0.0f), null);
            }
            sigField.setAppearance(PdfAnnotation.APPEARANCE_NORMAL, getAppearance());
            if (pages != null && pages.size() >= 1) {
                for (int i = 0; i < pages.size(); i++) {
                    sigField.setPage(pages.get(i).intValue());
                    this.writer.addAnnotation(sigField, pages.get(i).intValue());
                }
            } else {
                int pagen = getPage();
                sigField.setPage(pagen);
                this.writer.addAnnotation(sigField, pagen);
            }
        }
        this.exclusionLocations = new LinkedHashMap();
        if (this.cryptoDictionary == null) {
            if (PdfName.ADOBE_PPKLITE.equals(getFilter())) {
                this.sigStandard = new PdfSigGenericPKCS.PPKLite(getProvider());
            } else if (PdfName.ADOBE_PPKMS.equals(getFilter())) {
                this.sigStandard = new PdfSigGenericPKCS.PPKMS(getProvider());
            } else if (PdfName.VERISIGN_PPKVS.equals(getFilter())) {
                this.sigStandard = new PdfSigGenericPKCS.VeriSign(getProvider());
            } else {
                throw new IllegalArgumentException("Unknown filter: " + getFilter());
            }
            this.sigStandard.setExternalDigest(this.externalDigest, this.externalRSAdata, this.digestEncryptionAlgorithm);
            if (getReason() != null) {
                this.sigStandard.setReason(getReason());
            }
            if (getLocation() != null) {
                this.sigStandard.setLocation(getLocation());
            }
            if (getContact() != null) {
                this.sigStandard.setContact(getContact());
            }
            this.sigStandard.put(PdfName.M, new PdfDate(getSignDate()));
            this.sigStandard.setSignInfo(getPrivKey(), getCertChain(), getCrlList());
            PdfString contents = (PdfString) this.sigStandard.get(PdfName.CONTENTS);
            PdfLiteral lit = new PdfLiteral(((contents.toString().length() + (PdfName.ADOBE_PPKLITE.equals(getFilter()) ? 0 : 64)) * 2) + 2);
            this.exclusionLocations.put(PdfName.CONTENTS, lit);
            this.sigStandard.put(PdfName.CONTENTS, lit);
            PdfLiteral lit2 = new PdfLiteral(80);
            this.exclusionLocations.put(PdfName.BYTERANGE, lit2);
            this.sigStandard.put(PdfName.BYTERANGE, lit2);
            if (this.certificationLevel > 0) {
                addDocMDP(this.sigStandard);
            }
            if (this.signatureEvent != null) {
                this.signatureEvent.getSignatureDictionary(this.sigStandard);
            }
            this.writer.addToBody((PdfObject) this.sigStandard, refSig, false);
        } else {
            PdfLiteral lit3 = new PdfLiteral(80);
            this.exclusionLocations.put(PdfName.BYTERANGE, lit3);
            this.cryptoDictionary.put(PdfName.BYTERANGE, lit3);
            for (Map.Entry entry : exclusionSizes.entrySet()) {
                PdfName key = (PdfName) entry.getKey();
                Integer v = (Integer) entry.getValue();
                PdfLiteral lit4 = new PdfLiteral(v.intValue());
                this.exclusionLocations.put(key, lit4);
                this.cryptoDictionary.put(key, lit4);
            }
            if (this.certificationLevel > 0) {
                addDocMDP(this.cryptoDictionary);
            }
            if (this.signatureEvent != null) {
                this.signatureEvent.getSignatureDictionary(this.cryptoDictionary);
            }
            this.writer.addToBody((PdfObject) this.cryptoDictionary, refSig, false);
        }
        if (this.certificationLevel > 0) {
            PdfDictionary docmdp = new PdfDictionary();
            docmdp.put(new PdfName("DocMDP"), refSig);
            this.writer.reader.getCatalog().put(new PdfName("Perms"), docmdp);
        }
        this.writer.close(this.stamper.getMoreInfo(), globalDate != null ? globalDate : new GregorianCalendar());
        this.range = new int[this.exclusionLocations.size() * 2];
        int byteRangePosition = ((PdfLiteral) this.exclusionLocations.get(PdfName.BYTERANGE)).getPosition();
        this.exclusionLocations.remove(PdfName.BYTERANGE);
        int idx = 1;
        for (PdfLiteral lit5 : this.exclusionLocations.values()) {
            int n = lit5.getPosition();
            int i2 = idx;
            int idx2 = idx + 1;
            this.range[i2] = n;
            idx = idx2 + 1;
            this.range[idx2] = lit5.getPosLength() + n;
        }
        Arrays.sort(this.range, 1, this.range.length - 1);
        for (int k = 3; k < this.range.length - 2; k += 2) {
            int[] iArr = this.range;
            int i3 = k;
            iArr[i3] = iArr[i3] - this.range[k - 1];
        }
        if (this.tempFile == null) {
            this.bout = this.sigout.getBuffer();
            this.boutLen = this.sigout.size();
            this.range[this.range.length - 1] = this.boutLen - this.range[this.range.length - 2];
            ByteBuffer bf = new ByteBuffer();
            bf.append('[');
            for (int element : this.range) {
                bf.append(element).append(' ');
            }
            bf.append(']');
            System.arraycopy(bf.getBuffer(), 0, this.bout, byteRangePosition, bf.size());
            return;
        }
        try {
            this.raf = new RandomAccessFile(this.tempFile, "rw");
            int boutLen = (int) this.raf.length();
            this.range[this.range.length - 1] = boutLen - this.range[this.range.length - 2];
            ByteBuffer bf2 = new ByteBuffer();
            bf2.append('[');
            for (int element2 : this.range) {
                bf2.append(element2).append(' ');
            }
            bf2.append(']');
            this.raf.seek(byteRangePosition);
            this.raf.write(bf2.getBuffer(), 0, bf2.size());
        } catch (IOException e) {
            try {
                this.raf.close();
            } catch (Exception e2) {
            }
            try {
                this.tempFile.delete();
            } catch (Exception e3) {
            }
            throw e;
        }
    }

    public void close(PdfDictionary update) throws IOException, DocumentException {
        try {
            if (!this.preClosed) {
                throw new DocumentException("preClose() must be called first.");
            }
            ByteBuffer bf = new ByteBuffer();
            for (Object element : update.getKeys()) {
                PdfName key = (PdfName) element;
                PdfObject obj = update.get(key);
                PdfLiteral lit = (PdfLiteral) this.exclusionLocations.get(key);
                if (lit == null) {
                    throw new IllegalArgumentException("The key " + key.toString() + " didn't reserve space in preClose().");
                }
                bf.reset();
                obj.toPdf(null, bf);
                if (bf.size() > lit.getPosLength()) {
                    throw new IllegalArgumentException("The key " + key.toString() + " is too big. Is " + bf.size() + ", reserved " + lit.getPosLength());
                }
                if (this.tempFile == null) {
                    System.arraycopy(bf.getBuffer(), 0, this.bout, lit.getPosition(), bf.size());
                } else {
                    this.raf.seek(lit.getPosition());
                    this.raf.write(bf.getBuffer(), 0, bf.size());
                }
            }
            if (update.size() != this.exclusionLocations.size()) {
                throw new IllegalArgumentException("The update dictionary has less keys than required.");
            }
            if (this.tempFile == null) {
                this.originalout.write(this.bout, 0, this.boutLen);
            } else if (this.originalout != null) {
                this.raf.seek(0L);
                int length = (int) this.raf.length();
                byte[] buf = new byte[8192];
                while (length > 0) {
                    int r = this.raf.read(buf, 0, Math.min(buf.length, length));
                    if (r < 0) {
                        throw new EOFException("Unexpected EOF");
                    }
                    this.originalout.write(buf, 0, r);
                    length -= r;
                }
            }
            if (this.tempFile != null) {
                try {
                    this.raf.close();
                } catch (Exception e) {
                }
                if (this.originalout != null) {
                    try {
                        this.tempFile.delete();
                    } catch (Exception e2) {
                    }
                }
            }
            if (this.originalout != null) {
                try {
                    this.originalout.close();
                } catch (Exception e3) {
                }
            }
        } catch (Throwable th) {
            if (this.tempFile != null) {
                try {
                    this.raf.close();
                } catch (Exception e4) {
                }
                if (this.originalout != null) {
                    try {
                        this.tempFile.delete();
                    } catch (Exception e5) {
                    }
                }
            }
            if (this.originalout != null) {
                try {
                    this.originalout.close();
                } catch (Exception e6) {
                }
            }
            throw th;
        }
    }

    private void addDocMDP(PdfDictionary pdfDictionary) {
        PdfDictionary reference = new PdfDictionary();
        PdfDictionary transformParams = new PdfDictionary();
        transformParams.put(PdfName.P, new PdfNumber(this.certificationLevel));
        transformParams.put(PdfName.V, new PdfName("1.2"));
        transformParams.put(PdfName.TYPE, PdfName.TRANSFORMPARAMS);
        reference.put(PdfName.TRANSFORMMETHOD, PdfName.DOCMDP);
        reference.put(PdfName.TYPE, PdfName.SIGREF);
        reference.put(PdfName.TRANSFORMPARAMS, transformParams);
        reference.put(new PdfName("DigestValue"), new PdfString("aa"));
        PdfArray loc = new PdfArray();
        loc.add(new PdfNumber(0));
        loc.add(new PdfNumber(0));
        reference.put(new PdfName("DigestLocation"), loc);
        reference.put(new PdfName("DigestMethod"), new PdfName(MessageDigestAlgorithms.MD5));
        reference.put(PdfName.DATA, this.writer.reader.getTrailer().get(PdfName.ROOT));
        PdfArray types = new PdfArray();
        types.add(reference);
        pdfDictionary.put(PdfName.REFERENCE, types);
    }

    public InputStream getRangeStream() {
        return new RangeStream(this.raf, this.bout, this.range);
    }

    public PdfDictionary getCryptoDictionary() {
        return this.cryptoDictionary;
    }

    public void setCryptoDictionary(PdfDictionary cryptoDictionary) {
        this.cryptoDictionary = cryptoDictionary;
    }

    public PdfStamper getStamper() {
        return this.stamper;
    }

    void setStamper(PdfStamper stamper) {
        this.stamper = stamper;
    }

    public boolean isPreClosed() {
        return this.preClosed;
    }

    public PdfSigGenericPKCS getSigStandard() {
        return this.sigStandard;
    }

    public String getContact() {
        return this.contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public Font getLayer2Font() {
        return this.layer2Font;
    }

    public void setLayer2Font(Font layer2Font) {
        this.layer2Font = layer2Font;
    }

    public boolean isAcro6Layers() {
        return this.acro6Layers;
    }

    public void setAcro6Layers(boolean acro6Layers) {
        this.acro6Layers = acro6Layers;
    }

    public void setRunDirection(int runDirection) {
        if (runDirection < 0 || runDirection > 3) {
            throw new RuntimeException("Invalid run direction: " + runDirection);
        }
        this.runDirection = runDirection;
    }

    public int getRunDirection() {
        return this.runDirection;
    }

    public SignatureEvent getSignatureEvent() {
        return this.signatureEvent;
    }

    public void setSignatureEvent(SignatureEvent signatureEvent) {
        this.signatureEvent = signatureEvent;
    }

    public Image getImage() {
        return this.image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public float getImageScale() {
        return this.imageScale;
    }

    public void setImageScale(float imageScale) {
        this.imageScale = imageScale;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/PdfSignatureAppearance$RangeStream.class */
    private static class RangeStream extends InputStream {
        private final byte[] b;
        private final RandomAccessFile raf;
        private final byte[] bout;
        private final int[] range;
        private int rangePosition;

        private RangeStream(RandomAccessFile raf, byte[] bout, int[] range) {
            this.b = new byte[1];
            this.rangePosition = 0;
            this.raf = raf;
            this.bout = bout;
            this.range = range;
        }

        @Override // java.io.InputStream
        public int read() throws IOException {
            int n = read(this.b);
            if (n != 1) {
                return -1;
            }
            return this.b[0] & 255;
        }

        @Override // java.io.InputStream
        public int read(byte[] b, int off, int len) throws IOException {
            if (b == null) {
                throw new NullPointerException();
            }
            if (off < 0 || off > b.length || len < 0 || off + len > b.length || off + len < 0) {
                throw new IndexOutOfBoundsException();
            }
            if (len == 0) {
                return 0;
            }
            if (this.rangePosition >= this.range[this.range.length - 2] + this.range[this.range.length - 1]) {
                return -1;
            }
            for (int k = 0; k < this.range.length; k += 2) {
                int start = this.range[k];
                int end = start + this.range[k + 1];
                if (this.rangePosition < start) {
                    this.rangePosition = start;
                }
                if (this.rangePosition >= start && this.rangePosition < end) {
                    int lenf = Math.min(len, end - this.rangePosition);
                    if (this.raf == null) {
                        System.arraycopy(this.bout, this.rangePosition, b, off, lenf);
                    } else {
                        this.raf.seek(this.rangePosition);
                        this.raf.readFully(b, off, lenf);
                    }
                    this.rangePosition += lenf;
                    return lenf;
                }
            }
            return -1;
        }
    }

    public int getCertificationLevel() {
        return this.certificationLevel;
    }

    public void setCertificationLevel(int certificationLevel) {
        this.certificationLevel = certificationLevel;
    }
}
