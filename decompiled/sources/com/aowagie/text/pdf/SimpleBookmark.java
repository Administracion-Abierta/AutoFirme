package com.aowagie.text.pdf;

import com.aowagie.text.html.Markup;
import com.aowagie.text.xml.simpleparser.SimpleXMLDocHandler;
import com.aowagie.text.xml.simpleparser.SimpleXMLParser;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.StringTokenizer;
import org.apache.pdfbox.pdmodel.interactive.action.PDAction;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionGoTo;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionLaunch;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionNamed;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionRemoteGoTo;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/SimpleBookmark.class */
final class SimpleBookmark implements SimpleXMLDocHandler {
    private ArrayList topList;
    private final Stack attr = new Stack();

    private SimpleBookmark() {
    }

    private static List bookmarkDepth(PdfReader reader, PdfDictionary outline, IntHashtable pages) {
        ArrayList list = new ArrayList();
        while (outline != null) {
            HashMap map = new HashMap();
            PdfString title = (PdfString) PdfReader.getPdfObjectRelease(outline.get(PdfName.TITLE));
            map.put("Title", title.toUnicodeString());
            PdfArray color = (PdfArray) PdfReader.getPdfObjectRelease(outline.get(PdfName.C));
            if (color != null && color.size() == 3) {
                ByteBuffer out = new ByteBuffer();
                out.append(color.getAsNumber(0).floatValue()).append(' ');
                out.append(color.getAsNumber(1).floatValue()).append(' ');
                out.append(color.getAsNumber(2).floatValue());
                map.put("Color", PdfEncodings.convertToString(out.toByteArray(), null));
            }
            PdfNumber style = (PdfNumber) PdfReader.getPdfObjectRelease(outline.get(PdfName.F));
            if (style != null) {
                int f = style.intValue();
                String s = "";
                if ((f & 1) != 0) {
                    s = s + "italic ";
                }
                if ((f & 2) != 0) {
                    s = s + "bold ";
                }
                String s2 = s.trim();
                if (s2.length() != 0) {
                    map.put("Style", s2);
                }
            }
            PdfNumber count = (PdfNumber) PdfReader.getPdfObjectRelease(outline.get(PdfName.COUNT));
            if (count != null && count.intValue() < 0) {
                map.put("Open", "false");
            }
            try {
                PdfObject dest = PdfReader.getPdfObjectRelease(outline.get(PdfName.DEST));
                if (dest != null) {
                    mapGotoBookmark(map, dest, pages);
                } else {
                    PdfDictionary action = (PdfDictionary) PdfReader.getPdfObjectRelease(outline.get(PdfName.A));
                    if (action != null) {
                        if (PdfName.GOTO.equals(PdfReader.getPdfObjectRelease(action.get(PdfName.S)))) {
                            PdfObject dest2 = PdfReader.getPdfObjectRelease(action.get(PdfName.D));
                            if (dest2 != null) {
                                mapGotoBookmark(map, dest2, pages);
                            }
                        } else if (PdfName.URI.equals(PdfReader.getPdfObjectRelease(action.get(PdfName.S)))) {
                            map.put(PDAction.TYPE, "URI");
                            map.put("URI", ((PdfString) PdfReader.getPdfObjectRelease(action.get(PdfName.URI))).toUnicodeString());
                        } else if (PdfName.GOTOR.equals(PdfReader.getPdfObjectRelease(action.get(PdfName.S)))) {
                            PdfObject dest3 = PdfReader.getPdfObjectRelease(action.get(PdfName.D));
                            if (dest3 != null) {
                                if (dest3.isString()) {
                                    map.put(PDActionNamed.SUB_TYPE, dest3.toString());
                                } else if (dest3.isName()) {
                                    map.put("NamedN", PdfName.decodeName(dest3.toString()));
                                } else if (dest3.isArray()) {
                                    PdfArray arr = (PdfArray) dest3;
                                    StringBuffer s3 = new StringBuffer();
                                    s3.append(arr.getPdfObject(0).toString());
                                    s3.append(' ').append(arr.getPdfObject(1).toString());
                                    for (int k = 2; k < arr.size(); k++) {
                                        s3.append(' ').append(arr.getPdfObject(k).toString());
                                    }
                                    map.put("Page", s3.toString());
                                }
                            }
                            map.put(PDAction.TYPE, PDActionRemoteGoTo.SUB_TYPE);
                            PdfObject file = PdfReader.getPdfObjectRelease(action.get(PdfName.F));
                            if (file != null) {
                                if (file.isString()) {
                                    map.put("File", ((PdfString) file).toUnicodeString());
                                } else if (file.isDictionary()) {
                                    PdfObject file2 = PdfReader.getPdfObject(((PdfDictionary) file).get(PdfName.F));
                                    if (file2.isString()) {
                                        map.put("File", ((PdfString) file2).toUnicodeString());
                                    }
                                }
                            }
                            PdfObject newWindow = PdfReader.getPdfObjectRelease(action.get(PdfName.NEWWINDOW));
                            if (newWindow != null) {
                                map.put("NewWindow", newWindow.toString());
                            }
                        } else if (PdfName.LAUNCH.equals(PdfReader.getPdfObjectRelease(action.get(PdfName.S)))) {
                            map.put(PDAction.TYPE, PDActionLaunch.SUB_TYPE);
                            PdfObject file3 = PdfReader.getPdfObjectRelease(action.get(PdfName.F));
                            if (file3 == null) {
                                file3 = PdfReader.getPdfObjectRelease(action.get(PdfName.WIN));
                            }
                            if (file3 != null) {
                                if (file3.isString()) {
                                    map.put("File", ((PdfString) file3).toUnicodeString());
                                } else if (file3.isDictionary()) {
                                    PdfObject file4 = PdfReader.getPdfObjectRelease(((PdfDictionary) file3).get(PdfName.F));
                                    if (file4.isString()) {
                                        map.put("File", ((PdfString) file4).toUnicodeString());
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
            }
            PdfDictionary first = (PdfDictionary) PdfReader.getPdfObjectRelease(outline.get(PdfName.FIRST));
            if (first != null) {
                map.put("Kids", bookmarkDepth(reader, first, pages));
            }
            list.add(map);
            outline = (PdfDictionary) PdfReader.getPdfObjectRelease(outline.get(PdfName.NEXT));
        }
        return list;
    }

    private static void mapGotoBookmark(HashMap map, PdfObject dest, IntHashtable pages) {
        if (dest.isString()) {
            map.put(PDActionNamed.SUB_TYPE, dest.toString());
        } else if (dest.isName()) {
            map.put(PDActionNamed.SUB_TYPE, PdfName.decodeName(dest.toString()));
        } else if (dest.isArray()) {
            map.put("Page", makeBookmarkParam((PdfArray) dest, pages));
        }
        map.put(PDAction.TYPE, PDActionGoTo.SUB_TYPE);
    }

    private static String makeBookmarkParam(PdfArray dest, IntHashtable pages) {
        StringBuffer s = new StringBuffer();
        PdfObject obj = dest.getPdfObject(0);
        if (obj.isNumber()) {
            s.append(((PdfNumber) obj).intValue() + 1);
        } else {
            s.append(pages.get(getNumber((PdfIndirectReference) obj)));
        }
        s.append(' ').append(dest.getPdfObject(1).toString().substring(1));
        for (int k = 2; k < dest.size(); k++) {
            s.append(' ').append(dest.getPdfObject(k).toString());
        }
        return s.toString();
    }

    private static int getNumber(PdfIndirectReference indirect) {
        PdfDictionary pdfObj = (PdfDictionary) PdfReader.getPdfObjectRelease(indirect);
        if (pdfObj.contains(PdfName.TYPE) && pdfObj.get(PdfName.TYPE).equals(PdfName.PAGES) && pdfObj.contains(PdfName.KIDS)) {
            PdfArray kids = (PdfArray) pdfObj.get(PdfName.KIDS);
            indirect = (PdfIndirectReference) kids.getPdfObject(0);
        }
        return indirect.getNumber();
    }

    private static void createOutlineAction(PdfDictionary pdfDictionary, HashMap map, PdfWriter writer, boolean namedAsNames) {
        String file;
        try {
            String action = (String) map.get(PDAction.TYPE);
            if (PDActionGoTo.SUB_TYPE.equals(action)) {
                String p = (String) map.get(PDActionNamed.SUB_TYPE);
                if (p != null) {
                    if (namedAsNames) {
                        pdfDictionary.put(PdfName.DEST, new PdfName(p));
                    } else {
                        pdfDictionary.put(PdfName.DEST, new PdfString(p, null));
                    }
                } else {
                    String p2 = (String) map.get("Page");
                    if (p2 != null) {
                        PdfArray ar = new PdfArray();
                        StringTokenizer tk = new StringTokenizer(p2);
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
                        pdfDictionary.put(PdfName.DEST, ar);
                    }
                }
            } else if (PDActionRemoteGoTo.SUB_TYPE.equals(action)) {
                PdfDictionary dic = new PdfDictionary();
                String p3 = (String) map.get(PDActionNamed.SUB_TYPE);
                if (p3 != null) {
                    dic.put(PdfName.D, new PdfString(p3, null));
                } else {
                    String p4 = (String) map.get("NamedN");
                    if (p4 != null) {
                        dic.put(PdfName.D, new PdfName(p4));
                    } else {
                        String p5 = (String) map.get("Page");
                        if (p5 != null) {
                            PdfArray ar2 = new PdfArray();
                            StringTokenizer tk2 = new StringTokenizer(p5);
                            ar2.add(new PdfNumber(tk2.nextToken()));
                            if (!tk2.hasMoreTokens()) {
                                ar2.add(PdfName.XYZ);
                                ar2.add(new float[]{ColumnText.GLOBAL_SPACE_CHAR_RATIO, 10000.0f, ColumnText.GLOBAL_SPACE_CHAR_RATIO});
                            } else {
                                String fn3 = tk2.nextToken();
                                if (fn3.startsWith("/")) {
                                    fn3 = fn3.substring(1);
                                }
                                ar2.add(new PdfName(fn3));
                                for (int k2 = 0; k2 < 4 && tk2.hasMoreTokens(); k2++) {
                                    String fn4 = tk2.nextToken();
                                    if (fn4.equals("null")) {
                                        ar2.add(PdfNull.PDFNULL);
                                    } else {
                                        ar2.add(new PdfNumber(fn4));
                                    }
                                }
                            }
                            dic.put(PdfName.D, ar2);
                        }
                    }
                }
                String file2 = (String) map.get("File");
                if (dic.size() > 0 && file2 != null) {
                    dic.put(PdfName.S, PdfName.GOTOR);
                    dic.put(PdfName.F, new PdfString(file2));
                    String nw = (String) map.get("NewWindow");
                    if (nw != null) {
                        if (nw.equals("true")) {
                            dic.put(PdfName.NEWWINDOW, PdfBoolean.PDFTRUE);
                        } else if (nw.equals("false")) {
                            dic.put(PdfName.NEWWINDOW, PdfBoolean.PDFFALSE);
                        }
                    }
                    pdfDictionary.put(PdfName.A, dic);
                }
            } else if ("URI".equals(action)) {
                String uri = (String) map.get("URI");
                if (uri != null) {
                    PdfDictionary dic2 = new PdfDictionary();
                    dic2.put(PdfName.S, PdfName.URI);
                    dic2.put(PdfName.URI, new PdfString(uri));
                    pdfDictionary.put(PdfName.A, dic2);
                }
            } else if (PDActionLaunch.SUB_TYPE.equals(action) && (file = (String) map.get("File")) != null) {
                PdfDictionary dic3 = new PdfDictionary();
                dic3.put(PdfName.S, PdfName.LAUNCH);
                dic3.put(PdfName.F, new PdfString(file));
                pdfDictionary.put(PdfName.A, dic3);
            }
        } catch (Exception e) {
        }
    }

    static Object[] iterateOutlines(PdfWriter pdfWriter, PdfIndirectReference parent, List kids, boolean namedAsNames) throws IOException {
        PdfIndirectReference[] refs = new PdfIndirectReference[kids.size()];
        for (int k = 0; k < refs.length; k++) {
            refs[k] = pdfWriter.getPdfIndirectReference();
        }
        int ptr = 0;
        int count = 0;
        Iterator it = kids.listIterator();
        while (it.hasNext()) {
            HashMap map = (HashMap) it.next();
            Object[] lower = null;
            List subKid = (List) map.get("Kids");
            if (subKid != null && !subKid.isEmpty()) {
                lower = iterateOutlines(pdfWriter, refs[ptr], subKid, namedAsNames);
            }
            PdfDictionary outline = new PdfDictionary();
            count++;
            if (lower != null) {
                outline.put(PdfName.FIRST, (PdfIndirectReference) lower[0]);
                outline.put(PdfName.LAST, (PdfIndirectReference) lower[1]);
                int n = ((Integer) lower[2]).intValue();
                if ("false".equals(map.get("Open"))) {
                    outline.put(PdfName.COUNT, new PdfNumber(-n));
                } else {
                    outline.put(PdfName.COUNT, new PdfNumber(n));
                    count += n;
                }
            }
            outline.put(PdfName.PARENT, parent);
            if (ptr > 0) {
                outline.put(PdfName.PREV, refs[ptr - 1]);
            }
            if (ptr < refs.length - 1) {
                outline.put(PdfName.NEXT, refs[ptr + 1]);
            }
            outline.put(PdfName.TITLE, new PdfString((String) map.get("Title"), PdfObject.TEXT_UNICODE));
            String color = (String) map.get("Color");
            if (color != null) {
                try {
                    PdfArray arr = new PdfArray();
                    StringTokenizer tk = new StringTokenizer(color);
                    for (int k2 = 0; k2 < 3; k2++) {
                        float f = Float.parseFloat(tk.nextToken());
                        if (f < ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
                            f = 0.0f;
                        }
                        if (f > 1.0f) {
                            f = 1.0f;
                        }
                        arr.add(new PdfNumber(f));
                    }
                    outline.put(PdfName.C, arr);
                } catch (Exception e) {
                }
            }
            String style = (String) map.get("Style");
            if (style != null) {
                String style2 = style.toLowerCase();
                int bits = 0;
                if (style2.indexOf(Markup.CSS_VALUE_ITALIC) >= 0) {
                    bits = 0 | 1;
                }
                if (style2.indexOf(Markup.CSS_VALUE_BOLD) >= 0) {
                    bits |= 2;
                }
                if (bits != 0) {
                    outline.put(PdfName.F, new PdfNumber(bits));
                }
            }
            createOutlineAction(outline, map, pdfWriter, namedAsNames);
            pdfWriter.addToBody(outline, refs[ptr]);
            ptr++;
        }
        return new Object[]{refs[0], refs[refs.length - 1], Integer.valueOf(count)};
    }

    private static void exportToXMLNode(List list, Writer out, int indent, boolean onlyASCII) throws IOException {
        String dep = "";
        for (int k = 0; k < indent; k++) {
            dep = dep + "  ";
        }
        Iterator it = list.iterator();
        while (it.hasNext()) {
            HashMap map = (HashMap) it.next();
            String title = null;
            out.write(dep);
            out.write("<Title ");
            List kids = null;
            for (Map.Entry entry : map.entrySet()) {
                String key = (String) entry.getKey();
                if (key.equals("Title")) {
                    title = (String) entry.getValue();
                } else if (key.equals("Kids")) {
                    kids = (List) entry.getValue();
                } else {
                    out.write(key);
                    out.write("=\"");
                    String value = (String) entry.getValue();
                    if (key.equals(PDActionNamed.SUB_TYPE) || key.equals("NamedN")) {
                        value = SimpleNamedDestination.escapeBinaryString(value);
                    }
                    out.write(SimpleXMLParser.escapeXML(value, onlyASCII));
                    out.write("\" ");
                }
            }
            out.write(">");
            if (title == null) {
                title = "";
            }
            out.write(SimpleXMLParser.escapeXML(title, onlyASCII));
            if (kids != null) {
                out.write("\n");
                exportToXMLNode(kids, out, indent + 1, onlyASCII);
                out.write(dep);
            }
            out.write("</Title>\n");
        }
    }

    private static void exportToXML(List list, Writer wrt, String encoding, boolean onlyASCII) throws IOException {
        wrt.write("<?xml version=\"1.0\" encoding=\"");
        wrt.write(SimpleXMLParser.escapeXML(encoding, onlyASCII));
        wrt.write("\"?>\n<Bookmark>\n");
        exportToXMLNode(list, wrt, 1, onlyASCII);
        wrt.write("</Bookmark>\n");
        wrt.flush();
    }

    @Override // com.aowagie.text.xml.simpleparser.SimpleXMLDocHandler
    public void endDocument() {
    }

    @Override // com.aowagie.text.xml.simpleparser.SimpleXMLDocHandler
    public void endElement(String tag) {
        if (tag.equals("Bookmark")) {
            if (this.attr.isEmpty()) {
                return;
            } else {
                throw new RuntimeException("Bookmark end tag out of place.");
            }
        }
        if (!tag.equals("Title")) {
            throw new RuntimeException("Invalid end tag - " + tag);
        }
        HashMap attributes = (HashMap) this.attr.pop();
        String title = (String) attributes.get("Title");
        attributes.put("Title", title.trim());
        String named = (String) attributes.get(PDActionNamed.SUB_TYPE);
        if (named != null) {
            attributes.put(PDActionNamed.SUB_TYPE, SimpleNamedDestination.unEscapeBinaryString(named));
        }
        String named2 = (String) attributes.get("NamedN");
        if (named2 != null) {
            attributes.put("NamedN", SimpleNamedDestination.unEscapeBinaryString(named2));
        }
        if (this.attr.isEmpty()) {
            this.topList.add(attributes);
            return;
        }
        HashMap parent = (HashMap) this.attr.peek();
        List kids = (List) parent.get("Kids");
        if (kids == null) {
            kids = new ArrayList();
            parent.put("Kids", kids);
        }
        kids.add(attributes);
    }

    @Override // com.aowagie.text.xml.simpleparser.SimpleXMLDocHandler
    public void startDocument() {
    }

    @Override // com.aowagie.text.xml.simpleparser.SimpleXMLDocHandler
    public void startElement(String tag, HashMap h) {
        if (this.topList == null) {
            if (tag.equals("Bookmark")) {
                this.topList = new ArrayList();
                return;
            }
            throw new RuntimeException("Root element is not Bookmark: " + tag);
        }
        if (!tag.equals("Title")) {
            throw new RuntimeException("Tag " + tag + " not allowed.");
        }
        HashMap attributes = new HashMap(h);
        attributes.put("Title", "");
        attributes.remove("Kids");
        this.attr.push(attributes);
    }

    @Override // com.aowagie.text.xml.simpleparser.SimpleXMLDocHandler
    public void text(String str) {
        if (this.attr.isEmpty()) {
            return;
        }
        HashMap attributes = (HashMap) this.attr.peek();
        String title = (String) attributes.get("Title");
        attributes.put("Title", title + str);
    }
}
