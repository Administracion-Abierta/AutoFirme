package com.aowagie.text.pdf;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/FdfReader.class */
class FdfReader extends PdfReader {
    private HashMap fields;
    private String fileSpec;
    private PdfName encoding;

    FdfReader() {
    }

    @Override // com.aowagie.text.pdf.PdfReader
    protected void readPdf() throws IOException {
        this.fields = new LinkedHashMap();
        try {
            this.tokens.checkFdfHeader();
            rebuildXref();
            readDocObj();
            try {
                this.tokens.close();
            } catch (Exception e) {
            }
            readFields();
        } catch (Throwable th) {
            try {
                this.tokens.close();
            } catch (Exception e2) {
            }
            throw th;
        }
    }

    private void kidNode(PdfDictionary merged, String name) {
        PdfArray kids = merged.getAsArray(PdfName.KIDS);
        if (kids == null || kids.isEmpty()) {
            if (name.length() > 0) {
                name = name.substring(1);
            }
            this.fields.put(name, merged);
            return;
        }
        merged.remove(PdfName.KIDS);
        for (int k = 0; k < kids.size(); k++) {
            PdfDictionary dic = new PdfDictionary();
            dic.merge(merged);
            PdfDictionary newDic = kids.getAsDict(k);
            PdfString t = newDic.getAsString(PdfName.T);
            String newName = name;
            if (t != null) {
                newName = newName + "." + t.toUnicodeString();
            }
            dic.merge(newDic);
            dic.remove(PdfName.T);
            kidNode(dic, newName);
        }
    }

    private void readFields() {
        this.catalog = this.trailer.getAsDict(PdfName.ROOT);
        PdfDictionary fdf = this.catalog.getAsDict(PdfName.FDF);
        if (fdf == null) {
            return;
        }
        PdfString fs = fdf.getAsString(PdfName.F);
        if (fs != null) {
            this.fileSpec = fs.toUnicodeString();
        }
        PdfArray fld = fdf.getAsArray(PdfName.FIELDS);
        if (fld == null) {
            return;
        }
        this.encoding = fdf.getAsName(PdfName.ENCODING);
        PdfDictionary merged = new PdfDictionary();
        merged.put(PdfName.KIDS, fld);
        kidNode(merged, "");
    }

    public HashMap getFields() {
        return this.fields;
    }

    String getFieldValue(String name) {
        PdfObject v;
        PdfDictionary field = (PdfDictionary) this.fields.get(name);
        if (field == null || (v = getPdfObject(field.get(PdfName.V))) == null) {
            return null;
        }
        if (v.isName()) {
            return PdfName.decodeName(((PdfName) v).toString());
        }
        if (v.isString()) {
            PdfString vs = (PdfString) v;
            if (this.encoding == null || vs.getEncoding() != null) {
                return vs.toUnicodeString();
            }
            byte[] b = vs.getBytes();
            if (b.length >= 2 && b[0] == -2 && b[1] == -1) {
                return vs.toUnicodeString();
            }
            try {
                if (this.encoding.equals(PdfName.SHIFT_JIS)) {
                    return new String(b, "SJIS");
                }
                if (this.encoding.equals(PdfName.UHC)) {
                    return new String(b, "MS949");
                }
                if (this.encoding.equals(PdfName.GBK)) {
                    return new String(b, "GBK");
                }
                if (this.encoding.equals(PdfName.BIGFIVE)) {
                    return new String(b, "Big5");
                }
                return vs.toUnicodeString();
            } catch (Exception e) {
            }
        } else {
            return null;
        }
    }

    public String getFileSpec() {
        return this.fileSpec;
    }
}
