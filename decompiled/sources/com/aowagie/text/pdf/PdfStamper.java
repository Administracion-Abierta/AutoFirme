package com.aowagie.text.pdf;

import com.aowagie.text.DocWriter;
import com.aowagie.text.DocumentException;
import com.aowagie.text.ExceptionConverter;
import com.aowagie.text.Rectangle;
import com.aowagie.text.pdf.interfaces.PdfEncryptionSettings;
import com.aowagie.text.pdf.interfaces.PdfViewerPreferences;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/PdfStamper.class */
public class PdfStamper implements PdfViewerPreferences, PdfEncryptionSettings {
    private final PdfStamperImp stamper;
    private Map moreInfo;
    private boolean hasSignature;
    private PdfSignatureAppearance sigApp;

    public PdfStamper(PdfReader reader, OutputStream os, Calendar globalDate) throws IOException, DocumentException {
        this.stamper = new PdfStamperImp(reader, os, (char) 0, false, globalDate);
    }

    public PdfStamper(PdfReader reader, OutputStream os) throws IOException, DocumentException {
        this.stamper = new PdfStamperImp(reader, os, (char) 0, false, new GregorianCalendar());
    }

    private PdfStamper(PdfReader reader, OutputStream os, char pdfVersion, boolean append, Calendar globalDate) throws IOException, DocumentException {
        this.stamper = new PdfStamperImp(reader, os, pdfVersion, append, globalDate);
    }

    public Map getMoreInfo() {
        return this.moreInfo;
    }

    public void setMoreInfo(Map moreInfo) {
        this.moreInfo = moreInfo;
    }

    public void insertPage(int pageNumber, Rectangle mediabox) {
        this.stamper.insertPage(pageNumber, mediabox);
    }

    public PdfSignatureAppearance getSignatureAppearance() {
        return this.sigApp;
    }

    public void close() throws IOException, DocumentException {
        close(new GregorianCalendar());
    }

    public void close(Calendar globalDate) throws IOException, DocumentException {
        if (!this.hasSignature) {
            this.stamper.close(this.moreInfo, globalDate);
            return;
        }
        this.sigApp.preClose(globalDate);
        PdfSigGenericPKCS sig = this.sigApp.getSigStandard();
        PdfLiteral lit = (PdfLiteral) sig.get(PdfName.CONTENTS);
        int totalBuf = (lit.getPosLength() - 2) / 2;
        byte[] buf = new byte[8192];
        InputStream inp = this.sigApp.getRangeStream();
        while (true) {
            try {
                int n = inp.read(buf);
                if (n > 0) {
                    sig.getSigner().update(buf, 0, n);
                } else {
                    byte[] buf2 = new byte[totalBuf];
                    byte[] bsig = sig.getSignerContents();
                    System.arraycopy(bsig, 0, buf2, 0, bsig.length);
                    PdfString str = new PdfString(buf2);
                    str.setHexWriting(true);
                    PdfDictionary dic = new PdfDictionary();
                    dic.put(PdfName.CONTENTS, str);
                    this.sigApp.close(dic);
                    this.stamper.reader.close();
                    return;
                }
            } catch (SignatureException se) {
                throw new ExceptionConverter(se);
            }
        }
    }

    public boolean isRotateContents() {
        return this.stamper.isRotateContents();
    }

    public void setRotateContents(boolean rotateContents) {
        this.stamper.setRotateContents(rotateContents);
    }

    void setEncryption(byte[] userPassword, byte[] ownerPassword, int permissions, boolean strength128Bits) throws DocumentException {
        if (this.stamper.isAppend()) {
            throw new DocumentException("Append mode does not support changing the encryption status.");
        }
        if (this.stamper.isContentWritten()) {
            throw new DocumentException("Content was already written to the output.");
        }
        this.stamper.setEncryption(userPassword, ownerPassword, permissions, strength128Bits ? 1 : 0);
    }

    @Override // com.aowagie.text.pdf.interfaces.PdfEncryptionSettings
    public void setEncryption(byte[] userPassword, byte[] ownerPassword, int permissions, int encryptionType) throws DocumentException {
        if (this.stamper.isAppend()) {
            throw new DocumentException("Append mode does not support changing the encryption status.");
        }
        if (this.stamper.isContentWritten()) {
            throw new DocumentException("Content was already written to the output.");
        }
        this.stamper.setEncryption(userPassword, ownerPassword, permissions, encryptionType);
    }

    void setEncryption(boolean strength, String userPassword, String ownerPassword, int permissions) throws DocumentException {
        setEncryption(DocWriter.getISOBytes(userPassword), DocWriter.getISOBytes(ownerPassword), permissions, strength);
    }

    void setEncryption(int encryptionType, String userPassword, String ownerPassword, int permissions) throws DocumentException {
        setEncryption(DocWriter.getISOBytes(userPassword), DocWriter.getISOBytes(ownerPassword), permissions, encryptionType);
    }

    @Override // com.aowagie.text.pdf.interfaces.PdfEncryptionSettings
    public void setEncryption(Certificate[] certs, int[] permissions, int encryptionType) throws DocumentException {
        if (this.stamper.isAppend()) {
            throw new DocumentException("Append mode does not support changing the encryption status.");
        }
        if (this.stamper.isContentWritten()) {
            throw new DocumentException("Content was already written to the output.");
        }
        this.stamper.setEncryption(certs, permissions, encryptionType);
    }

    public PdfWriter getWriter() {
        return this.stamper;
    }

    public PdfReader getReader() {
        return this.stamper.reader;
    }

    public AcroFields getAcroFields() {
        return this.stamper.getAcroFields();
    }

    public void setFormFlattening(boolean flat) {
        this.stamper.setFormFlattening(flat);
    }

    public void setFreeTextFlattening(boolean flat) {
        this.stamper.setFreeTextFlattening(flat);
    }

    public void addAnnotation(PdfAnnotation annot, int page) {
        this.stamper.addAnnotation(annot, page);
    }

    public void setOutlines(List outlines) {
        this.stamper.setOutlines(outlines);
    }

    private void addFileAttachment(String description, PdfFileSpecification fs) throws IOException {
        this.stamper.addFileAttachment(description, fs);
    }

    @Override // com.aowagie.text.pdf.interfaces.PdfViewerPreferences
    public void setViewerPreferences(int preferences) {
        this.stamper.setViewerPreferences(preferences);
    }

    @Override // com.aowagie.text.pdf.interfaces.PdfViewerPreferences
    public void addViewerPreference(PdfName key, PdfObject value) {
        this.stamper.addViewerPreference(key, value);
    }

    public void setXmpMetadata(byte[] xmp) {
        this.stamper.setXmpMetadata(xmp);
    }

    public boolean isFullCompression() {
        return this.stamper.isFullCompression();
    }

    public void setFullCompression() {
        if (this.stamper.isAppend()) {
            return;
        }
        this.stamper.setFullCompression();
    }

    public static PdfStamper createSignature(PdfReader reader, OutputStream os, char pdfVersion, File tempFile, boolean append) throws IOException, DocumentException {
        return createSignature(reader, os, pdfVersion, tempFile, append, null);
    }

    public static PdfStamper createSignature(PdfReader reader, OutputStream os, char pdfVersion, File tempFile, boolean append, Calendar globalDate) throws IOException, DocumentException {
        PdfStamper stp;
        Calendar gDate = globalDate != null ? globalDate : new GregorianCalendar();
        if (tempFile == null) {
            ByteBuffer bout = new ByteBuffer();
            stp = new PdfStamper(reader, bout, pdfVersion, append, gDate);
            List<PRAcroForm.FieldInformation> formFieldNames = getFieldsWithSignatureName(reader.getAcroForm());
            stp.sigApp = new PdfSignatureAppearance(stp.stamper, gDate, formFieldNames);
            stp.sigApp.setSigout(bout);
        } else {
            if (tempFile.isDirectory()) {
                tempFile = File.createTempFile("pdf", null, tempFile);
            }
            FileOutputStream fout = new FileOutputStream(tempFile);
            stp = new PdfStamper(reader, fout, pdfVersion, append, globalDate);
            stp.sigApp = new PdfSignatureAppearance(stp.stamper, globalDate);
            stp.sigApp.setTempFile(tempFile);
        }
        stp.sigApp.setOriginalout(os);
        stp.sigApp.setStamper(stp);
        stp.hasSignature = true;
        PdfDictionary catalog = reader.getCatalog();
        PdfDictionary acroForm = (PdfDictionary) PdfReader.getPdfObject(catalog.get(PdfName.ACROFORM), catalog);
        if (acroForm != null) {
            acroForm.remove(PdfName.NEEDAPPEARANCES);
            stp.stamper.markUsed(acroForm);
        }
        return stp;
    }

    private static List<PRAcroForm.FieldInformation> getFieldsWithSignatureName(PRAcroForm acroForm) {
        List<PRAcroForm.FieldInformation> resultFormFields = new ArrayList<>();
        if (acroForm != null) {
            List<?> formFields = acroForm.getFields();
            for (Object field : formFields) {
                PRAcroForm.FieldInformation fieldInfo = (PRAcroForm.FieldInformation) field;
                if (fieldInfo.getName() != null && fieldInfo.getName().startsWith("Signature")) {
                    resultFormFields.add(fieldInfo);
                }
            }
        }
        return resultFormFields;
    }

    public PdfContentByte getOverContent(int pageNum) {
        return this.stamper.getOverContent(pageNum);
    }

    public Map getPdfLayers() {
        return this.stamper.getPdfLayers();
    }
}
