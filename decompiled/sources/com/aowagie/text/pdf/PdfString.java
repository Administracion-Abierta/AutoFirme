package com.aowagie.text.pdf;

import es.gob.afirma.signvalidation.ISignatureFormatDetector;
import java.io.IOException;
import java.io.OutputStream;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/PdfString.class */
public class PdfString extends PdfObject {
    protected String value;
    private String originalValue;
    private String encoding;
    private int objNum;
    private int objGen;
    private boolean hexWriting;

    public PdfString() {
        super(3);
        this.value = "";
        this.originalValue = null;
        this.encoding = ISignatureFormatDetector.FORMAT_PDF;
        this.objNum = 0;
        this.objGen = 0;
        this.hexWriting = false;
    }

    public PdfString(String value) {
        super(3);
        this.value = "";
        this.originalValue = null;
        this.encoding = ISignatureFormatDetector.FORMAT_PDF;
        this.objNum = 0;
        this.objGen = 0;
        this.hexWriting = false;
        this.value = value;
    }

    public PdfString(String value, String encoding) {
        super(3);
        this.value = "";
        this.originalValue = null;
        this.encoding = ISignatureFormatDetector.FORMAT_PDF;
        this.objNum = 0;
        this.objGen = 0;
        this.hexWriting = false;
        this.value = value;
        this.encoding = encoding;
    }

    public PdfString(byte[] bytes) {
        super(3);
        this.value = "";
        this.originalValue = null;
        this.encoding = ISignatureFormatDetector.FORMAT_PDF;
        this.objNum = 0;
        this.objGen = 0;
        this.hexWriting = false;
        this.value = PdfEncodings.convertToString(bytes, null);
        this.encoding = "";
    }

    @Override // com.aowagie.text.pdf.PdfObject
    public void toPdf(PdfWriter writer, OutputStream os) throws IOException {
        byte[] b = getBytes();
        PdfEncryption crypto = null;
        if (writer != null) {
            crypto = writer.getEncryption();
        }
        if (crypto != null && !crypto.isEmbeddedFilesOnly()) {
            b = crypto.encryptByteArray(b);
        }
        if (this.hexWriting) {
            ByteBuffer buf = new ByteBuffer();
            buf.append('<');
            for (byte b2 : b) {
                buf.appendHex(b2);
            }
            buf.append('>');
            os.write(buf.toByteArray());
            return;
        }
        os.write(PdfContentByte.escapeString(b));
    }

    @Override // com.aowagie.text.pdf.PdfObject
    public String toString() {
        return this.value;
    }

    @Override // com.aowagie.text.pdf.PdfObject
    public byte[] getBytes() {
        if (this.bytes == null) {
            if (this.encoding != null && this.encoding.equals(PdfObject.TEXT_UNICODE) && PdfEncodings.isPdfDocEncoding(this.value)) {
                this.bytes = PdfEncodings.convertToBytes(this.value, ISignatureFormatDetector.FORMAT_PDF);
            } else {
                this.bytes = PdfEncodings.convertToBytes(this.value, this.encoding);
            }
        }
        return this.bytes;
    }

    public String toUnicodeString() {
        if (this.encoding != null && this.encoding.length() != 0) {
            return this.value;
        }
        getBytes();
        if (this.bytes.length >= 2 && this.bytes[0] == -2 && this.bytes[1] == -1) {
            return PdfEncodings.convertToString(this.bytes, PdfObject.TEXT_UNICODE);
        }
        return PdfEncodings.convertToString(this.bytes, ISignatureFormatDetector.FORMAT_PDF);
    }

    public String getEncoding() {
        return this.encoding;
    }

    void setObjNum(int objNum, int objGen) {
        this.objNum = objNum;
        this.objGen = objGen;
    }

    void decrypt(PdfReader reader) {
        PdfEncryption decrypt = reader.getDecrypt();
        if (decrypt != null) {
            this.originalValue = this.value;
            decrypt.setHashKey(this.objNum, this.objGen);
            this.bytes = PdfEncodings.convertToBytes(this.value, (String) null);
            this.bytes = decrypt.decryptByteArray(this.bytes);
            this.value = PdfEncodings.convertToString(this.bytes, null);
        }
    }

    public byte[] getOriginalBytes() {
        if (this.originalValue == null) {
            return getBytes();
        }
        return PdfEncodings.convertToBytes(this.originalValue, (String) null);
    }

    public PdfString setHexWriting(boolean hexWriting) {
        this.hexWriting = hexWriting;
        return this;
    }

    public boolean isHexWriting() {
        return this.hexWriting;
    }
}
