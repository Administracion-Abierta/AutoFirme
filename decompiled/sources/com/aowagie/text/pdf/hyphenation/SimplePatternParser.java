package com.aowagie.text.pdf.hyphenation;

import com.aowagie.text.html.HtmlTags;
import com.aowagie.text.xml.simpleparser.SimpleXMLDocHandler;
import com.ctc.wstx.cfg.XmlConsts;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;
import org.apache.fontbox.ttf.PostScriptTable;
import org.apache.pdfbox.pdmodel.common.PDPageLabelRange;
import org.mozilla.javascript.ES6Iterator;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/hyphenation/SimplePatternParser.class */
class SimplePatternParser implements SimpleXMLDocHandler, PatternConsumer {
    private int currElement;
    private PatternConsumer consumer;
    private ArrayList exception;
    private static final int ELEM_CLASSES = 1;
    private static final int ELEM_EXCEPTIONS = 2;
    private static final int ELEM_PATTERNS = 3;
    private static final int ELEM_HYPHEN = 4;
    private final StringBuffer token = new StringBuffer();
    private char hyphenChar = '-';

    /* JADX WARN: Bottom block not found for handler: all -> 0x001f */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void parse(java.io.InputStream r5, com.aowagie.text.pdf.hyphenation.PatternConsumer r6) {
        /*
            r4 = this;
            r0 = r4
            r1 = r6
            r0.consumer = r1
            r0 = r4
            r1 = r5
            com.aowagie.text.xml.simpleparser.SimpleXMLParser.parse(r0, r1)     // Catch: java.io.IOException -> L15 java.lang.Throwable -> L1f
            r0 = r5
            r0.close()     // Catch: java.lang.Exception -> L11
            goto L2d
        L11:
            r7 = move-exception
            goto L2d
        L15:
            r7 = move-exception
            com.aowagie.text.ExceptionConverter r0 = new com.aowagie.text.ExceptionConverter     // Catch: java.lang.Throwable -> L1f
            r1 = r0
            r2 = r7
            r1.<init>(r2)     // Catch: java.lang.Throwable -> L1f
            throw r0     // Catch: java.lang.Throwable -> L1f
        L1f:
            r8 = move-exception
            r0 = r5
            r0.close()     // Catch: java.lang.Exception -> L28
            goto L2a
        L28:
            r9 = move-exception
        L2a:
            r0 = r8
            throw r0
        L2d:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.aowagie.text.pdf.hyphenation.SimplePatternParser.parse(java.io.InputStream, com.aowagie.text.pdf.hyphenation.PatternConsumer):void");
    }

    private static String getPattern(String word) {
        StringBuffer pat = new StringBuffer();
        int len = word.length();
        for (int i = 0; i < len; i++) {
            if (!Character.isDigit(word.charAt(i))) {
                pat.append(word.charAt(i));
            }
        }
        return pat.toString();
    }

    private ArrayList normalizeException(ArrayList ex) {
        ArrayList res = new ArrayList();
        for (int i = 0; i < ex.size(); i++) {
            Object item = ex.get(i);
            if (item instanceof String) {
                String str = (String) item;
                StringBuffer buf = new StringBuffer();
                for (int j = 0; j < str.length(); j++) {
                    char c = str.charAt(j);
                    if (c != this.hyphenChar) {
                        buf.append(c);
                    } else {
                        res.add(buf.toString());
                        buf.setLength(0);
                        char[] h = {this.hyphenChar};
                        res.add(new Hyphen(new String(h), null, null));
                    }
                }
                if (buf.length() > 0) {
                    res.add(buf.toString());
                }
            } else {
                res.add(item);
            }
        }
        return res;
    }

    private String getExceptionWord(ArrayList ex) {
        StringBuffer res = new StringBuffer();
        for (int i = 0; i < ex.size(); i++) {
            Object item = ex.get(i);
            if (item instanceof String) {
                res.append((String) item);
            } else if (((Hyphen) item).noBreak != null) {
                res.append(((Hyphen) item).noBreak);
            }
        }
        return res.toString();
    }

    private static String getInterletterValues(String pat) {
        StringBuffer il = new StringBuffer();
        String word = pat + PDPageLabelRange.STYLE_LETTERS_LOWER;
        int len = word.length();
        int i = 0;
        while (i < len) {
            char c = word.charAt(i);
            if (Character.isDigit(c)) {
                il.append(c);
                i++;
            } else {
                il.append('0');
            }
            i++;
        }
        return il.toString();
    }

    @Override // com.aowagie.text.xml.simpleparser.SimpleXMLDocHandler
    public void endDocument() {
    }

    @Override // com.aowagie.text.xml.simpleparser.SimpleXMLDocHandler
    public void endElement(String tag) {
        if (this.token.length() > 0) {
            String word = this.token.toString();
            switch (this.currElement) {
                case 1:
                    this.consumer.addClass(word);
                    break;
                case 2:
                    this.exception.add(word);
                    this.exception = normalizeException(this.exception);
                    this.consumer.addException(getExceptionWord(this.exception), (ArrayList) this.exception.clone());
                    break;
                case 3:
                    this.consumer.addPattern(getPattern(word), getInterletterValues(word));
                    break;
            }
            if (this.currElement != 4) {
                this.token.setLength(0);
            }
        }
        if (this.currElement == 4) {
            this.currElement = 2;
        } else {
            this.currElement = 0;
        }
    }

    @Override // com.aowagie.text.xml.simpleparser.SimpleXMLDocHandler
    public void startDocument() {
    }

    @Override // com.aowagie.text.xml.simpleparser.SimpleXMLDocHandler
    public void startElement(String tag, HashMap h) {
        if (tag.equals("hyphen-char")) {
            String hh = (String) h.get(ES6Iterator.VALUE_PROPERTY);
            if (hh != null && hh.length() == 1) {
                this.hyphenChar = hh.charAt(0);
            }
        } else if (tag.equals("classes")) {
            this.currElement = 1;
        } else if (tag.equals("patterns")) {
            this.currElement = 3;
        } else if (tag.equals("exceptions")) {
            this.currElement = 2;
            this.exception = new ArrayList();
        } else if (tag.equals("hyphen")) {
            if (this.token.length() > 0) {
                this.exception.add(this.token.toString());
            }
            this.exception.add(new Hyphen((String) h.get(HtmlTags.PRE), (String) h.get(XmlConsts.XML_SA_NO), (String) h.get(PostScriptTable.TAG)));
            this.currElement = 4;
        }
        this.token.setLength(0);
    }

    @Override // com.aowagie.text.xml.simpleparser.SimpleXMLDocHandler
    public void text(String str) {
        StringTokenizer tk = new StringTokenizer(str);
        while (tk.hasMoreTokens()) {
            String word = tk.nextToken();
            switch (this.currElement) {
                case 1:
                    this.consumer.addClass(word);
                    break;
                case 2:
                    this.exception.add(word);
                    this.exception = normalizeException(this.exception);
                    this.consumer.addException(getExceptionWord(this.exception), (ArrayList) this.exception.clone());
                    this.exception.clear();
                    break;
                case 3:
                    this.consumer.addPattern(getPattern(word), getInterletterValues(word));
                    break;
            }
        }
    }

    @Override // com.aowagie.text.pdf.hyphenation.PatternConsumer
    public void addClass(String c) {
        System.out.println("class: " + c);
    }

    @Override // com.aowagie.text.pdf.hyphenation.PatternConsumer
    public void addException(String w, ArrayList e) {
        System.out.println("exception: " + w + " : " + e.toString());
    }

    @Override // com.aowagie.text.pdf.hyphenation.PatternConsumer
    public void addPattern(String p, String v) {
        System.out.println("pattern: " + p + " : " + v);
    }

    public static void main(String[] args) throws Exception {
        try {
            if (args.length > 0) {
                SimplePatternParser simplePatternParser = new SimplePatternParser();
                simplePatternParser.parse(new FileInputStream(args[0]), simplePatternParser);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
