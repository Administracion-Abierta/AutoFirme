package com.aowagie.text.xml.simpleparser;

import com.ctc.wstx.io.CharsetNames;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Stack;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/xml/simpleparser/SimpleXMLParser.class */
public final class SimpleXMLParser {
    private static final int UNKNOWN = 0;
    private static final int TEXT = 1;
    private static final int TAG_ENCOUNTERED = 2;
    private static final int EXAMIN_TAG = 3;
    private static final int TAG_EXAMINED = 4;
    private static final int IN_CLOSETAG = 5;
    private static final int SINGLE_TAG = 6;
    private static final int CDATA = 7;
    private static final int COMMENT = 8;
    private static final int PI = 9;
    private static final int ENTITY = 10;
    private static final int QUOTE = 11;
    private static final int ATTRIBUTE_KEY = 12;
    private static final int ATTRIBUTE_EQUAL = 13;
    private static final int ATTRIBUTE_VALUE = 14;
    private int state;
    private final boolean html;
    private final SimpleXMLDocHandler doc;
    private final SimpleXMLDocHandlerComment comment;
    private int character = 0;
    private int previousCharacter = -1;
    private int lines = 1;
    private int columns = 0;
    private boolean eol = false;
    private boolean nowhite = false;
    private final StringBuffer text = new StringBuffer();
    private final StringBuffer entity = new StringBuffer();
    private String tag = null;
    private HashMap attributes = null;
    private int nested = 0;
    private int quoteCharacter = 34;
    private String attributekey = null;
    private String attributevalue = null;
    private final Stack stack = new Stack();

    private SimpleXMLParser(SimpleXMLDocHandler doc, SimpleXMLDocHandlerComment comment, boolean html) {
        this.doc = doc;
        this.comment = comment;
        this.html = html;
        this.state = html ? 1 : 0;
    }

    private void go(Reader r) throws IOException {
        BufferedReader reader;
        if (r instanceof BufferedReader) {
            reader = (BufferedReader) r;
        } else {
            reader = new BufferedReader(r);
        }
        this.doc.startDocument();
        while (true) {
            if (this.previousCharacter == -1) {
                this.character = reader.read();
            } else {
                this.character = this.previousCharacter;
                this.previousCharacter = -1;
            }
            if (this.character == -1) {
                if (this.html) {
                    if (this.html && this.state == 1) {
                        flush();
                    }
                    this.doc.endDocument();
                    return;
                }
                throwException("Missing end tag");
                return;
            }
            if (this.character == 10 && this.eol) {
                this.eol = false;
            } else {
                if (this.eol) {
                    this.eol = false;
                } else if (this.character == 10) {
                    this.lines++;
                    this.columns = 0;
                } else if (this.character == 13) {
                    this.eol = true;
                    this.character = 10;
                    this.lines++;
                    this.columns = 0;
                } else {
                    this.columns++;
                }
                switch (this.state) {
                    case 0:
                        if (this.character == 60) {
                            saveState(1);
                            this.state = 2;
                        }
                        break;
                    case 1:
                        if (this.character == 60) {
                            flush();
                            saveState(this.state);
                            this.state = 2;
                        } else if (this.character == 38) {
                            saveState(this.state);
                            this.entity.setLength(0);
                            this.state = 10;
                        } else if (Character.isWhitespace((char) this.character)) {
                            if (this.nowhite) {
                                this.text.append((char) this.character);
                            }
                            this.nowhite = false;
                        } else {
                            this.text.append((char) this.character);
                            this.nowhite = true;
                        }
                        break;
                    case 2:
                        initTag();
                        if (this.character == 47) {
                            this.state = 5;
                        } else if (this.character == 63) {
                            restoreState();
                            this.state = 9;
                        } else {
                            this.text.append((char) this.character);
                            this.state = 3;
                        }
                        break;
                    case 3:
                        if (this.character == 62) {
                            doTag();
                            processTag(true);
                            initTag();
                            this.state = restoreState();
                        } else if (this.character == 47) {
                            this.state = 6;
                        } else if (this.character == 45 && this.text.toString().equals("!-")) {
                            flush();
                            this.state = 8;
                        } else if (this.character == 91 && this.text.toString().equals("![CDATA")) {
                            flush();
                            this.state = 7;
                        } else if (this.character == 69 && this.text.toString().equals("!DOCTYP")) {
                            flush();
                            this.state = 9;
                        } else if (Character.isWhitespace((char) this.character)) {
                            doTag();
                            this.state = 4;
                        } else {
                            this.text.append((char) this.character);
                        }
                        break;
                    case 4:
                        if (this.character == 62) {
                            processTag(true);
                            initTag();
                            this.state = restoreState();
                        } else if (this.character == 47) {
                            this.state = 6;
                        } else if (!Character.isWhitespace((char) this.character)) {
                            this.text.append((char) this.character);
                            this.state = 12;
                        }
                        break;
                    case 5:
                        if (this.character == 62) {
                            doTag();
                            processTag(false);
                            if (!this.html && this.nested == 0) {
                                return;
                            }
                            this.state = restoreState();
                        } else if (!Character.isWhitespace((char) this.character)) {
                            this.text.append((char) this.character);
                        }
                        break;
                    case 6:
                        if (this.character != 62) {
                            throwException("Expected > for tag: <" + this.tag + "/>");
                        }
                        doTag();
                        processTag(true);
                        processTag(false);
                        initTag();
                        if (!this.html && this.nested == 0) {
                            this.doc.endDocument();
                            return;
                        }
                        this.state = restoreState();
                        break;
                    case 7:
                        if (this.character == 62 && this.text.toString().endsWith("]]")) {
                            this.text.setLength(this.text.length() - 2);
                            flush();
                            this.state = restoreState();
                        } else {
                            this.text.append((char) this.character);
                        }
                        break;
                    case 8:
                        if (this.character == 62 && this.text.toString().endsWith("--")) {
                            this.text.setLength(this.text.length() - 2);
                            flush();
                            this.state = restoreState();
                        } else {
                            this.text.append((char) this.character);
                        }
                        break;
                    case 9:
                        if (this.character == 62) {
                            this.state = restoreState();
                            if (this.state == 1) {
                                this.state = 0;
                            }
                        }
                        break;
                    case 10:
                        if (this.character == 59) {
                            this.state = restoreState();
                            String cent = this.entity.toString();
                            this.entity.setLength(0);
                            char ce = EntitiesToUnicode.decodeEntity(cent);
                            if (ce == 0) {
                                this.text.append('&').append(cent).append(';');
                            } else {
                                this.text.append(ce);
                            }
                        } else if ((this.character != 35 && ((this.character < 48 || this.character > 57) && ((this.character < 97 || this.character > 122) && (this.character < 65 || this.character > 90)))) || this.entity.length() >= 7) {
                            this.state = restoreState();
                            this.previousCharacter = this.character;
                            this.text.append('&').append(this.entity.toString());
                            this.entity.setLength(0);
                        } else {
                            this.entity.append((char) this.character);
                        }
                        break;
                    case 11:
                        if (this.html && this.quoteCharacter == 32 && this.character == 62) {
                            flush();
                            processTag(true);
                            initTag();
                            this.state = restoreState();
                        } else if (this.html && this.quoteCharacter == 32 && Character.isWhitespace((char) this.character)) {
                            flush();
                            this.state = 4;
                        } else if (this.html && this.quoteCharacter == 32) {
                            this.text.append((char) this.character);
                        } else if (this.character == this.quoteCharacter) {
                            flush();
                            this.state = 4;
                        } else if (" \r\n\t".indexOf(this.character) >= 0) {
                            this.text.append(' ');
                        } else if (this.character == 38) {
                            saveState(this.state);
                            this.state = 10;
                            this.entity.setLength(0);
                        } else {
                            this.text.append((char) this.character);
                        }
                        break;
                    case 12:
                        if (Character.isWhitespace((char) this.character)) {
                            flush();
                            this.state = 13;
                        } else if (this.character == 61) {
                            flush();
                            this.state = 14;
                        } else if (this.html && this.character == 62) {
                            this.text.setLength(0);
                            processTag(true);
                            initTag();
                            this.state = restoreState();
                        } else {
                            this.text.append((char) this.character);
                        }
                        break;
                    case 13:
                        if (this.character == 61) {
                            this.state = 14;
                        } else if (!Character.isWhitespace((char) this.character)) {
                            if (this.html && this.character == 62) {
                                this.text.setLength(0);
                                processTag(true);
                                initTag();
                                this.state = restoreState();
                            } else if (this.html && this.character == 47) {
                                flush();
                                this.state = 6;
                            } else if (this.html) {
                                flush();
                                this.text.append((char) this.character);
                                this.state = 12;
                            } else {
                                throwException("Error in attribute processing.");
                            }
                        }
                        break;
                    case 14:
                        if (this.character == 34 || this.character == 39) {
                            this.quoteCharacter = this.character;
                            this.state = 11;
                        } else if (!Character.isWhitespace((char) this.character)) {
                            if (this.html && this.character == 62) {
                                flush();
                                processTag(true);
                                initTag();
                                this.state = restoreState();
                            } else if (this.html) {
                                this.text.append((char) this.character);
                                this.quoteCharacter = 32;
                                this.state = 11;
                            } else {
                                throwException("Error in attribute processing");
                            }
                        }
                        break;
                }
            }
        }
    }

    private int restoreState() {
        if (!this.stack.empty()) {
            return ((Integer) this.stack.pop()).intValue();
        }
        return 0;
    }

    private void saveState(int s) {
        this.stack.push(Integer.valueOf(s));
    }

    private void flush() {
        switch (this.state) {
            case 1:
            case 7:
                if (this.text.length() > 0) {
                    this.doc.text(this.text.toString());
                }
                break;
            case 8:
                if (this.comment != null) {
                    this.comment.comment(this.text.toString());
                }
                break;
            case 11:
            case 14:
                this.attributevalue = this.text.toString();
                this.attributes.put(this.attributekey, this.attributevalue);
                break;
            case 12:
                this.attributekey = this.text.toString();
                if (this.html) {
                    this.attributekey = this.attributekey.toLowerCase();
                }
                break;
        }
        this.text.setLength(0);
    }

    private void initTag() {
        this.tag = null;
        this.attributes = new HashMap();
    }

    private void doTag() {
        if (this.tag == null) {
            this.tag = this.text.toString();
        }
        if (this.html) {
            this.tag = this.tag.toLowerCase();
        }
        this.text.setLength(0);
    }

    private void processTag(boolean start) {
        if (start) {
            this.nested++;
            this.doc.startElement(this.tag, this.attributes);
        } else {
            this.nested--;
            this.doc.endElement(this.tag);
        }
    }

    private void throwException(String s) throws IOException {
        throw new IOException(s + " near line " + this.lines + ", column " + this.columns);
    }

    private static void parse(SimpleXMLDocHandler doc, SimpleXMLDocHandlerComment comment, Reader r, boolean html) throws IOException {
        SimpleXMLParser parser = new SimpleXMLParser(doc, comment, html);
        parser.go(r);
    }

    public static void parse(SimpleXMLDocHandler doc, InputStream in) throws IOException {
        String decl;
        byte[] b4 = new byte[4];
        int count = in.read(b4);
        if (count != 4) {
            throw new IOException("Insufficient length.");
        }
        String encoding = getEncodingName(b4);
        String decl2 = null;
        if (encoding.equals("UTF-8")) {
            StringBuffer sb = new StringBuffer();
            while (true) {
                int c = in.read();
                if (c == -1 || c == 62) {
                    break;
                } else {
                    sb.append((char) c);
                }
            }
            decl2 = sb.toString();
        } else if (encoding.equals("CP037")) {
            ByteArrayOutputStream bi = new ByteArrayOutputStream();
            while (true) {
                int c2 = in.read();
                if (c2 == -1 || c2 == 110) {
                    break;
                } else {
                    bi.write(c2);
                }
            }
            decl2 = new String(bi.toByteArray(), "CP037");
        }
        if (decl2 != null && (decl = getDeclaredEncoding(decl2)) != null) {
            encoding = decl;
        }
        parse(doc, new InputStreamReader(in, IanaEncodings.getJavaEncoding(encoding)));
    }

    private static String getDeclaredEncoding(String decl) {
        int idx;
        int idx1;
        int idx2;
        int idx3;
        if (decl == null || (idx = decl.indexOf("encoding")) < 0 || (idx1 = decl.indexOf(34, idx)) == (idx2 = decl.indexOf(39, idx))) {
            return null;
        }
        if ((idx1 < 0 && idx2 > 0) || (idx2 > 0 && idx2 < idx1)) {
            int idx4 = decl.indexOf(39, idx2 + 1);
            if (idx4 < 0) {
                return null;
            }
            return decl.substring(idx2 + 1, idx4);
        }
        if (((idx2 >= 0 || idx1 <= 0) && (idx1 <= 0 || idx1 >= idx2)) || (idx3 = decl.indexOf(34, idx1 + 1)) < 0) {
            return null;
        }
        return decl.substring(idx1 + 1, idx3);
    }

    private static void parse(SimpleXMLDocHandler doc, Reader r) throws IOException {
        parse(doc, null, r, false);
    }

    public static String escapeXML(String s, boolean onlyASCII) {
        char[] cc = s.toCharArray();
        StringBuffer sb = new StringBuffer();
        for (char c : cc) {
            switch (c) {
                case '\"':
                    sb.append("&quot;");
                    break;
                case '&':
                    sb.append("&amp;");
                    break;
                case '\'':
                    sb.append("&apos;");
                    break;
                case '<':
                    sb.append("&lt;");
                    break;
                case '>':
                    sb.append("&gt;");
                    break;
                default:
                    if (c == '\t' || c == '\n' || c == '\r' || ((c >= ' ' && c <= 55295) || ((c >= 57344 && c <= 65533) || (c >= 0 && c <= 65535)))) {
                        if (onlyASCII && c > 127) {
                            sb.append("&#").append((int) c).append(';');
                        } else {
                            sb.append(c);
                        }
                    }
                    break;
            }
        }
        return sb.toString();
    }

    private static String getEncodingName(byte[] b4) {
        int b0 = b4[0] & 255;
        int b1 = b4[1] & 255;
        if (b0 == 254 && b1 == 255) {
            return CharsetNames.CS_UTF16BE;
        }
        if (b0 == 255 && b1 == 254) {
            return CharsetNames.CS_UTF16LE;
        }
        int b2 = b4[2] & 255;
        if (b0 == 239 && b1 == 187 && b2 == 191) {
            return StandardCharsets.UTF_8.name();
        }
        int b3 = b4[3] & 255;
        if (b0 == 0 && b1 == 0 && b2 == 0 && b3 == 60) {
            return "ISO-10646-UCS-4";
        }
        if (b0 == 60 && b1 == 0 && b2 == 0 && b3 == 0) {
            return "ISO-10646-UCS-4";
        }
        if (b0 == 0 && b1 == 0 && b2 == 60 && b3 == 0) {
            return "ISO-10646-UCS-4";
        }
        if (b0 == 0 && b1 == 60 && b2 == 0 && b3 == 0) {
            return "ISO-10646-UCS-4";
        }
        if (b0 == 0 && b1 == 60 && b2 == 0 && b3 == 63) {
            return CharsetNames.CS_UTF16BE;
        }
        if (b0 == 60 && b1 == 0 && b2 == 63 && b3 == 0) {
            return CharsetNames.CS_UTF16LE;
        }
        if (b0 == 76 && b1 == 111 && b2 == 167 && b3 == 148) {
            return "CP037";
        }
        return StandardCharsets.UTF_8.name();
    }
}
