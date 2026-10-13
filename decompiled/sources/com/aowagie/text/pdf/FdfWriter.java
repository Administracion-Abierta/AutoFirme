package com.aowagie.text.pdf;

import com.aowagie.text.DocWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/FdfWriter.class */
public class FdfWriter {
    private static final byte[] HEADER_FDF = DocWriter.getISOBytes("%FDF-1.2\n%âãÏÓ\n");
    HashMap fields = new HashMap();
    private String file;

    public void writeTo(OutputStream os) throws IOException {
        Wrt wrt = new Wrt(os, this);
        wrt.writeTo();
    }

    boolean setField(String field, PdfObject value) {
        HashMap map = this.fields;
        StringTokenizer tk = new StringTokenizer(field, ".");
        if (!tk.hasMoreTokens()) {
            return false;
        }
        while (true) {
            String s = tk.nextToken();
            Object obj = map.get(s);
            if (tk.hasMoreTokens()) {
                if (obj == null) {
                    HashMap map2 = new HashMap();
                    map.put(s, map2);
                    map = map2;
                } else if (obj instanceof HashMap) {
                    map = (HashMap) obj;
                } else {
                    return false;
                }
            } else {
                if (!(obj instanceof HashMap)) {
                    map.put(s, value);
                    return true;
                }
                return false;
            }
        }
    }

    void iterateFields(HashMap values, HashMap map, String name) {
        for (Map.Entry entry : map.entrySet()) {
            String s = (String) entry.getKey();
            Object obj = entry.getValue();
            if (obj instanceof HashMap) {
                iterateFields(values, (HashMap) obj, name + "." + s);
            } else {
                values.put((name + "." + s).substring(1), obj);
            }
        }
    }

    public boolean removeField(String field) {
        HashMap map = this.fields;
        StringTokenizer tk = new StringTokenizer(field, ".");
        if (!tk.hasMoreTokens()) {
            return false;
        }
        ArrayList hist = new ArrayList();
        while (true) {
            String s = tk.nextToken();
            Object obj = map.get(s);
            if (obj == null) {
                return false;
            }
            hist.add(map);
            hist.add(s);
            if (tk.hasMoreTokens()) {
                if (obj instanceof HashMap) {
                    map = (HashMap) obj;
                } else {
                    return false;
                }
            } else {
                if (obj instanceof HashMap) {
                    return false;
                }
                for (int k = hist.size() - 2; k >= 0; k -= 2) {
                    HashMap map2 = (HashMap) hist.get(k);
                    map2.remove((String) hist.get(k + 1));
                    if (!map2.isEmpty()) {
                        return true;
                    }
                }
                return true;
            }
        }
    }

    public HashMap getFields() {
        HashMap values = new HashMap();
        iterateFields(values, this.fields, "");
        return values;
    }

    public String getField(String field) {
        HashMap map = this.fields;
        StringTokenizer tk = new StringTokenizer(field, ".");
        if (!tk.hasMoreTokens()) {
            return null;
        }
        while (true) {
            String s = tk.nextToken();
            Object obj = map.get(s);
            if (obj == null) {
                return null;
            }
            if (tk.hasMoreTokens()) {
                if (obj instanceof HashMap) {
                    map = (HashMap) obj;
                } else {
                    return null;
                }
            } else {
                if (obj instanceof HashMap) {
                    return null;
                }
                if (((PdfObject) obj).isString()) {
                    return ((PdfString) obj).toUnicodeString();
                }
                return PdfName.decodeName(obj.toString());
            }
        }
    }

    public boolean setFieldAsName(String field, String value) {
        return setField(field, new PdfName(value));
    }

    public boolean setFieldAsString(String field, String value) {
        return setField(field, new PdfString(value, PdfObject.TEXT_UNICODE));
    }

    public boolean setFieldAsAction(String field, PdfAction action) {
        return setField(field, action);
    }

    public void setFields(FdfReader fdf) {
        HashMap map = fdf.getFields();
        for (Map.Entry entry : map.entrySet()) {
            String key = (String) entry.getKey();
            PdfDictionary dic = (PdfDictionary) entry.getValue();
            PdfObject v = dic.get(PdfName.V);
            if (v != null) {
                setField(key, v);
            }
            PdfObject v2 = dic.get(PdfName.A);
            if (v2 != null) {
                setField(key, v2);
            }
        }
    }

    public void setFields(PdfReader pdf) {
        setFields(pdf.getAcroFields());
    }

    public void setFields(AcroFields af) {
        PdfObject ft;
        for (Map.Entry<String, AcroFields.Item> entry : af.getFields().entrySet()) {
            String fn = entry.getKey();
            AcroFields.Item item = entry.getValue();
            PdfDictionary dic = item.getMerged(0);
            PdfObject v = PdfReader.getPdfObjectRelease(dic.get(PdfName.V));
            if (v != null && (ft = PdfReader.getPdfObjectRelease(dic.get(PdfName.FT))) != null && !PdfName.SIG.equals(ft)) {
                setField(fn, v);
            }
        }
    }

    public String getFile() {
        return this.file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/FdfWriter$Wrt.class */
    static class Wrt extends PdfWriter {
        private final FdfWriter fdf;

        Wrt(OutputStream os, FdfWriter fdf) throws IOException {
            super(new PdfDocument(), os);
            this.fdf = fdf;
            this.os.write(FdfWriter.HEADER_FDF);
            this.body = new PdfWriter.PdfBody(this);
        }

        void writeTo() throws IOException {
            PdfDictionary dic = new PdfDictionary();
            dic.put(PdfName.FIELDS, calculate(this.fdf.fields));
            if (this.fdf.file != null) {
                dic.put(PdfName.F, new PdfString(this.fdf.file, PdfObject.TEXT_UNICODE));
            }
            PdfDictionary fd = new PdfDictionary();
            fd.put(PdfName.FDF, dic);
            PdfIndirectReference ref = addToBody(fd).getIndirectReference();
            this.os.write(getISOBytes("trailer\n"));
            PdfDictionary trailer = new PdfDictionary();
            trailer.put(PdfName.ROOT, ref);
            trailer.toPdf(null, this.os);
            this.os.write(getISOBytes("\n%%EOF\n"));
            this.os.close();
        }

        PdfArray calculate(HashMap map) throws IOException {
            PdfArray ar = new PdfArray();
            for (Map.Entry entry : map.entrySet()) {
                String key = (String) entry.getKey();
                Object v = entry.getValue();
                PdfDictionary dic = new PdfDictionary();
                dic.put(PdfName.T, new PdfString(key, PdfObject.TEXT_UNICODE));
                if (v instanceof HashMap) {
                    dic.put(PdfName.KIDS, calculate((HashMap) v));
                } else if (v instanceof PdfAction) {
                    dic.put(PdfName.A, (PdfAction) v);
                } else {
                    dic.put(PdfName.V, (PdfObject) v);
                }
                ar.add(dic);
            }
            return ar;
        }
    }
}
