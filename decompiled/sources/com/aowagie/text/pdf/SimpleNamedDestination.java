package com.aowagie.text.pdf;

import com.aowagie.text.xml.simpleparser.SimpleXMLDocHandler;
import com.aowagie.text.xml.simpleparser.SimpleXMLParser;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/SimpleNamedDestination.class */
final class SimpleNamedDestination implements SimpleXMLDocHandler {
    private HashMap xmlNames;
    private HashMap xmlLast;

    private SimpleNamedDestination() {
    }

    private static void exportToXML(HashMap names, Writer wrt, String encoding, boolean onlyASCII) throws IOException {
        wrt.write("<?xml version=\"1.0\" encoding=\"");
        wrt.write(SimpleXMLParser.escapeXML(encoding, onlyASCII));
        wrt.write("\"?>\n<Destination>\n");
        for (Map.Entry entry : names.entrySet()) {
            String key = (String) entry.getKey();
            String value = (String) entry.getValue();
            wrt.write("  <Name Page=\"");
            wrt.write(SimpleXMLParser.escapeXML(value, onlyASCII));
            wrt.write("\">");
            wrt.write(SimpleXMLParser.escapeXML(escapeBinaryString(key), onlyASCII));
            wrt.write("</Name>\n");
        }
        wrt.write("</Destination>\n");
        wrt.flush();
    }

    private static PdfArray createDestinationArray(String value, PdfWriter writer) {
        PdfArray ar = new PdfArray();
        StringTokenizer tk = new StringTokenizer(value);
        int n = Integer.parseInt(tk.nextToken());
        ar.add(writer.getPageReference(n));
        if (!tk.hasMoreTokens()) {
            ar.add(PdfName.XYZ);
            ar.add(new float[]{ColumnText.GLOBAL_SPACE_CHAR_RATIO, 10000.0f, ColumnText.GLOBAL_SPACE_CHAR_RATIO});
        } else {
            String fn = tk.nextToken();
            if (fn.startsWith("/")) {
                fn = fn.substring(1);
            }
            ar.add(new PdfName(fn));
            for (int k = 0; k < 4 && tk.hasMoreTokens(); k++) {
                String fn2 = tk.nextToken();
                if (fn2.equals("null")) {
                    ar.add(PdfNull.PDFNULL);
                } else {
                    ar.add(new PdfNumber(fn2));
                }
            }
        }
        return ar;
    }

    static String escapeBinaryString(String s) {
        StringBuffer buf = new StringBuffer();
        char[] cc = s.toCharArray();
        for (char c : cc) {
            if (c < ' ') {
                buf.append('\\');
                String octal = "00" + Integer.toOctalString(c);
                buf.append(octal.substring(octal.length() - 3));
            } else if (c == '\\') {
                buf.append("\\\\");
            } else {
                buf.append(c);
            }
        }
        return buf.toString();
    }

    static String unEscapeBinaryString(String s) {
        char c;
        StringBuffer buf = new StringBuffer();
        char[] cc = s.toCharArray();
        int len = cc.length;
        int k = 0;
        while (k < len) {
            char c2 = cc[k];
            if (c2 == '\\') {
                k++;
                if (k >= len) {
                    buf.append('\\');
                    break;
                }
                char c3 = cc[k];
                if (c3 >= '0' && c3 <= '7') {
                    int n = c3 - '0';
                    int k2 = k + 1;
                    for (int j = 0; j < 2 && k2 < len && (c = cc[k2]) >= '0' && c <= '7'; j++) {
                        k2++;
                        n = ((n * 8) + c) - 48;
                    }
                    k = k2 - 1;
                    buf.append((char) n);
                } else {
                    buf.append(c3);
                }
            } else {
                buf.append(c2);
            }
            k++;
        }
        return buf.toString();
    }

    @Override // com.aowagie.text.xml.simpleparser.SimpleXMLDocHandler
    public void endDocument() {
    }

    @Override // com.aowagie.text.xml.simpleparser.SimpleXMLDocHandler
    public void endElement(String tag) {
        if (tag.equals("Destination")) {
            if (this.xmlLast == null && this.xmlNames != null) {
            } else {
                throw new RuntimeException("Destination end tag out of place.");
            }
        } else {
            if (!tag.equals("Name")) {
                throw new RuntimeException("Invalid end tag - " + tag);
            }
            if (this.xmlLast == null || this.xmlNames == null) {
                throw new RuntimeException("Name end tag out of place.");
            }
            if (!this.xmlLast.containsKey("Page")) {
                throw new RuntimeException("Page attribute missing.");
            }
            this.xmlNames.put(unEscapeBinaryString((String) this.xmlLast.get("Name")), this.xmlLast.get("Page"));
            this.xmlLast = null;
        }
    }

    @Override // com.aowagie.text.xml.simpleparser.SimpleXMLDocHandler
    public void startDocument() {
    }

    @Override // com.aowagie.text.xml.simpleparser.SimpleXMLDocHandler
    public void startElement(String tag, HashMap h) {
        if (this.xmlNames == null) {
            if (tag.equals("Destination")) {
                this.xmlNames = new HashMap();
                return;
            }
            throw new RuntimeException("Root element is not Destination.");
        }
        if (!tag.equals("Name")) {
            throw new RuntimeException("Tag " + tag + " not allowed.");
        }
        if (this.xmlLast != null) {
            throw new RuntimeException("Nested tags are not allowed.");
        }
        this.xmlLast = new HashMap(h);
        this.xmlLast.put("Name", "");
    }

    @Override // com.aowagie.text.xml.simpleparser.SimpleXMLDocHandler
    public void text(String str) {
        if (this.xmlLast == null) {
            return;
        }
        String name = (String) this.xmlLast.get("Name");
        this.xmlLast.put("Name", name + str);
    }
}
