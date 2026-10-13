package org.apache.pdfbox.cos;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.util.Charsets;
import org.apache.pdfbox.util.Hex;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/cos/COSString.class */
public final class COSString extends COSBase {
    private byte[] bytes;
    private boolean forceHexForm;
    private static final Log LOG = LogFactory.getLog(COSString.class);
    public static final boolean FORCE_PARSING = Boolean.getBoolean("org.apache.pdfbox.forceParsing");

    public COSString(byte[] bytes) {
        setValue(bytes);
    }

    public COSString(String text) {
        boolean isOnlyPDFDocEncoding = true;
        for (char c : text.toCharArray()) {
            if (!PDFDocEncoding.containsChar(c)) {
                isOnlyPDFDocEncoding = false;
                break;
            }
        }
        if (isOnlyPDFDocEncoding) {
            this.bytes = PDFDocEncoding.getBytes(text);
            return;
        }
        byte[] data = text.getBytes(Charsets.UTF_16BE);
        this.bytes = new byte[data.length + 2];
        this.bytes[0] = -2;
        this.bytes[1] = -1;
        System.arraycopy(data, 0, this.bytes, 2, data.length);
    }

    public static COSString parseHex(String hex) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        StringBuilder hexBuffer = new StringBuilder(hex.trim());
        if (hexBuffer.length() % 2 != 0) {
            hexBuffer.append('0');
        }
        int length = hexBuffer.length();
        for (int i = 0; i < length; i += 2) {
            try {
                bytes.write(Integer.parseInt(hexBuffer.substring(i, i + 2), 16));
            } catch (NumberFormatException e) {
                if (FORCE_PARSING) {
                    LOG.warn("Encountered a malformed hex string");
                    bytes.write(63);
                } else {
                    throw new IOException("Invalid hex string: " + hex, e);
                }
            }
        }
        return new COSString(bytes.toByteArray());
    }

    public void setValue(byte[] value) {
        this.bytes = (byte[]) value.clone();
    }

    public void setForceHexForm(boolean value) {
        this.forceHexForm = value;
    }

    public boolean getForceHexForm() {
        return this.forceHexForm;
    }

    public String getString() {
        if (this.bytes.length >= 2) {
            if ((this.bytes[0] & 255) == 254 && (this.bytes[1] & 255) == 255) {
                return new String(this.bytes, 2, this.bytes.length - 2, Charsets.UTF_16BE);
            }
            if ((this.bytes[0] & 255) == 255 && (this.bytes[1] & 255) == 254) {
                return new String(this.bytes, 2, this.bytes.length - 2, Charsets.UTF_16LE);
            }
        }
        return PDFDocEncoding.toString(this.bytes);
    }

    public String getASCII() {
        return new String(this.bytes, Charsets.US_ASCII);
    }

    public byte[] getBytes() {
        return this.bytes;
    }

    public String toHexString() {
        return Hex.getString(this.bytes);
    }

    @Override // org.apache.pdfbox.cos.COSBase
    public Object accept(ICOSVisitor visitor) throws IOException {
        return visitor.visitFromString(this);
    }

    public boolean equals(Object obj) {
        if (obj instanceof COSString) {
            COSString strObj = (COSString) obj;
            return getString().equals(strObj.getString()) && this.forceHexForm == strObj.forceHexForm;
        }
        return false;
    }

    public int hashCode() {
        int result = Arrays.hashCode(this.bytes);
        return result + (this.forceHexForm ? 17 : 0);
    }

    public String toString() {
        return "COSString{" + getString() + "}";
    }
}
