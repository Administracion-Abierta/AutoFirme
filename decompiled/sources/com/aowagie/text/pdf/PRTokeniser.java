package com.aowagie.text.pdf;

import java.io.IOException;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/PRTokeniser.class */
public class PRTokeniser {
    static final int TK_NUMBER = 1;
    static final int TK_STRING = 2;
    static final int TK_NAME = 3;
    static final int TK_COMMENT = 4;
    static final int TK_START_ARRAY = 5;
    static final int TK_END_ARRAY = 6;
    static final int TK_START_DIC = 7;
    static final int TK_END_DIC = 8;
    static final int TK_REF = 9;
    static final int TK_OTHER = 10;
    public static final int TK_ENDOFFILE = 11;
    private static final boolean[] delims = {true, true, false, false, false, false, false, false, false, false, true, true, false, true, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, true, false, false, true, true, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, true, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false};
    private static final String EMPTY = "";
    private final RandomAccessFileOrArray file;
    private int type;
    private String stringValue;
    private int reference;
    private int generation;
    private boolean hexString;

    PRTokeniser(String filename) throws IOException {
        this.file = new RandomAccessFileOrArray(filename);
    }

    public PRTokeniser(byte[] pdfIn) {
        this.file = new RandomAccessFileOrArray(pdfIn);
    }

    PRTokeniser(RandomAccessFileOrArray file) {
        this.file = file;
    }

    public void seek(int pos) throws IOException {
        this.file.seek(pos);
    }

    public int getFilePointer() throws IOException {
        return this.file.getFilePointer();
    }

    void close() throws IOException {
        this.file.close();
    }

    int length() throws IOException {
        return this.file.length();
    }

    public int read() throws IOException {
        return this.file.read();
    }

    public RandomAccessFileOrArray getSafeFile() {
        return new RandomAccessFileOrArray(this.file);
    }

    public RandomAccessFileOrArray getFile() {
        return this.file;
    }

    String readString(int size) throws IOException {
        int ch;
        StringBuffer buf = new StringBuffer();
        while (true) {
            int i = size;
            size--;
            if (i <= 0 || (ch = this.file.read()) == -1) {
                break;
            }
            buf.append((char) ch);
        }
        return buf.toString();
    }

    static final boolean isWhitespace(int ch) {
        return ch == 0 || ch == 9 || ch == 10 || ch == 12 || ch == 13 || ch == 32;
    }

    public int getTokenType() {
        return this.type;
    }

    public String getStringValue() {
        return this.stringValue;
    }

    public int getReference() {
        return this.reference;
    }

    public int getGeneration() {
        return this.generation;
    }

    void backOnePosition(int ch) {
        if (ch != -1) {
            this.file.pushBack((byte) ch);
        }
    }

    void throwError(String error) throws IOException {
        throw new IOException(error + " at file pointer " + this.file.getFilePointer());
    }

    char checkPdfHeader() throws IOException {
        this.file.setStartOffset(0);
        String str = readString(1024);
        int idx = str.indexOf("%PDF-");
        if (idx < 0) {
            throw new IOException("PDF header signature not found.");
        }
        this.file.setStartOffset(idx);
        return str.charAt(idx + 7);
    }

    void checkFdfHeader() throws IOException {
        this.file.setStartOffset(0);
        String str = readString(1024);
        int idx = str.indexOf("%FDF-1.2");
        if (idx < 0) {
            throw new IOException("FDF header signature not found.");
        }
        this.file.setStartOffset(idx);
    }

    public int getStartxref() throws IOException {
        int size = Math.min(1024, this.file.length());
        int pos = this.file.length() - size;
        this.file.seek(pos);
        String str = readString(1024);
        int idx = str.lastIndexOf("startxref");
        if (idx < 0) {
            throw new IOException("PDF startxref not found.");
        }
        return pos + idx;
    }

    public static int getHex(int v) {
        if (v >= 48 && v <= 57) {
            return v - 48;
        }
        if (v >= 65 && v <= 70) {
            return (v - 65) + 10;
        }
        if (v >= 97 && v <= 102) {
            return (v - 97) + 10;
        }
        return -1;
    }

    void nextValidToken() throws IOException {
        int level = 0;
        String n1 = null;
        String n2 = null;
        int ptr = 0;
        while (nextToken()) {
            if (this.type != 4) {
                switch (level) {
                    case 0:
                        if (this.type != 1) {
                            return;
                        }
                        ptr = this.file.getFilePointer();
                        n1 = this.stringValue;
                        level++;
                        break;
                        break;
                    case 1:
                        if (this.type != 1) {
                            this.file.seek(ptr);
                            this.type = 1;
                            this.stringValue = n1;
                            return;
                        }
                        n2 = this.stringValue;
                        level++;
                        break;
                    default:
                        if (this.type != 10 || !this.stringValue.equals("R")) {
                            this.file.seek(ptr);
                            this.type = 1;
                            this.stringValue = n1;
                            return;
                        } else {
                            this.type = 9;
                            this.reference = Integer.parseInt(n1);
                            this.generation = Integer.parseInt(n2);
                            return;
                        }
                }
            }
        }
        if (level > 0) {
            this.type = 1;
            this.file.seek(ptr);
            this.stringValue = n1;
            return;
        }
        throwError("Unexpected end of file");
    }

    boolean nextToken() throws IOException {
        int ch;
        int ch2;
        int ch3;
        do {
            ch = this.file.read();
            if (ch == -1) {
                break;
            }
        } while (isWhitespace(ch));
        if (ch == -1) {
            this.type = 11;
            return false;
        }
        StringBuffer outBuf = null;
        this.stringValue = "";
        switch (ch) {
            case 37:
                this.type = 4;
                do {
                    ch3 = this.file.read();
                    if (ch3 == -1 || ch3 == 13) {
                        break;
                    }
                } while (ch3 != 10);
                break;
            case 40:
                outBuf = new StringBuffer();
                this.type = 2;
                this.hexString = false;
                int nesting = 0;
                while (true) {
                    ch2 = this.file.read();
                    if (ch2 != -1) {
                        if (ch2 == 40) {
                            nesting++;
                        } else if (ch2 == 41) {
                            nesting--;
                        } else if (ch2 == 92) {
                            boolean lineBreak = false;
                            ch2 = this.file.read();
                            switch (ch2) {
                                case 10:
                                    lineBreak = true;
                                    break;
                                case 13:
                                    lineBreak = true;
                                    ch2 = this.file.read();
                                    if (ch2 != 10) {
                                        backOnePosition(ch2);
                                    }
                                    break;
                                case 40:
                                case 41:
                                case 92:
                                    break;
                                case 98:
                                    ch2 = 8;
                                    break;
                                case 102:
                                    ch2 = 12;
                                    break;
                                case 110:
                                    ch2 = 10;
                                    break;
                                case 114:
                                    ch2 = 13;
                                    break;
                                case 116:
                                    ch2 = 9;
                                    break;
                                default:
                                    if (ch2 >= 48 && ch2 <= 55) {
                                        int octal = ch2 - 48;
                                        int ch4 = this.file.read();
                                        if (ch4 < 48 || ch4 > 55) {
                                            backOnePosition(ch4);
                                            ch2 = octal;
                                        } else {
                                            int octal2 = ((octal << 3) + ch4) - 48;
                                            int ch5 = this.file.read();
                                            if (ch5 < 48 || ch5 > 55) {
                                                backOnePosition(ch5);
                                                ch2 = octal2;
                                            } else {
                                                ch2 = (((octal2 << 3) + ch5) - 48) & 255;
                                            }
                                        }
                                    }
                                    break;
                            }
                            if (lineBreak) {
                                continue;
                            } else if (ch2 < 0) {
                            }
                        } else if (ch2 == 13) {
                            ch2 = this.file.read();
                            if (ch2 >= 0) {
                                if (ch2 != 10) {
                                    backOnePosition(ch2);
                                    ch2 = 10;
                                }
                            }
                        }
                        if (nesting != -1) {
                            outBuf.append((char) ch2);
                        }
                    }
                }
                if (ch2 == -1) {
                    throwError("Error reading string");
                }
                break;
            case 47:
                outBuf = new StringBuffer();
                this.type = 3;
                while (true) {
                    int ch6 = this.file.read();
                    if (!delims[ch6 + 1]) {
                        if (ch6 == 35) {
                            ch6 = (getHex(this.file.read()) << 4) + getHex(this.file.read());
                        }
                        outBuf.append((char) ch6);
                    } else {
                        backOnePosition(ch6);
                    }
                    break;
                }
                break;
            case 60:
                int v1 = this.file.read();
                if (v1 == 60) {
                    this.type = 7;
                } else {
                    outBuf = new StringBuffer();
                    this.type = 2;
                    this.hexString = true;
                    int v2 = 0;
                    while (true) {
                        if (isWhitespace(v1)) {
                            v1 = this.file.read();
                        } else if (v1 != 62) {
                            v1 = getHex(v1);
                            if (v1 >= 0) {
                                int i = this.file.read();
                                while (true) {
                                    v2 = i;
                                    if (isWhitespace(v2)) {
                                        i = this.file.read();
                                    } else if (v2 == 62) {
                                        outBuf.append((char) (v1 << 4));
                                    } else {
                                        v2 = getHex(v2);
                                        if (v2 >= 0) {
                                            outBuf.append((char) ((v1 << 4) + v2));
                                            v1 = this.file.read();
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (v1 < 0 || v2 < 0) {
                        throwError("Error reading string");
                    }
                }
                break;
            case 62:
                if (this.file.read() != 62) {
                    throwError("'>' not expected");
                }
                this.type = 8;
                break;
            case 91:
                this.type = 5;
                break;
            case 93:
                this.type = 6;
                break;
            default:
                outBuf = new StringBuffer();
                if (ch == 45 || ch == 43 || ch == 46 || (ch >= 48 && ch <= 57)) {
                    this.type = 1;
                    while (true) {
                        outBuf.append((char) ch);
                        ch = this.file.read();
                        if (ch != -1) {
                            if (ch < 48 || ch > 57) {
                                if (ch != 46) {
                                }
                            }
                        }
                    }
                } else {
                    this.type = 10;
                    do {
                        outBuf.append((char) ch);
                        ch = this.file.read();
                    } while (!delims[ch + 1]);
                }
                backOnePosition(ch);
                break;
        }
        if (outBuf != null) {
            this.stringValue = outBuf.toString();
            return true;
        }
        return true;
    }

    int intValue() {
        return Integer.parseInt(this.stringValue);
    }

    /* JADX WARN: Code duplicated, block: B:26:0x0095  */
    /* JADX WARN: Code duplicated, block: B:29:0x009b  */
    /* JADX WARN: Code duplicated, block: B:44:0x00f8  */
    /* JADX WARN: Code duplicated, block: B:55:0x00c4 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:56:0x00c9 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:57:0x00da A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:59:0x0097 A[DONT_GENERATE, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:61:0x0097 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:63:? A[RETURN, SYNTHETIC] */
    boolean readLineSegment(byte[] input) throws IOException {
        boolean eol;
        int i;
        int cur;
        int i2;
        int c = -1;
        boolean eol2 = false;
        int ptr = 0;
        int len = input.length;
        if (0 < len) {
            do {
                i2 = read();
                c = i2;
            } while (isWhitespace(i2));
        }
        while (!eol2 && ptr < len) {
            switch (c) {
                case -1:
                case 10:
                    eol2 = true;
                    break;
                case 13:
                    eol2 = true;
                    int cur2 = getFilePointer();
                    if (read() != 10) {
                        seek(cur2);
                    }
                    break;
                default:
                    int i3 = ptr;
                    ptr++;
                    input[i3] = (byte) c;
                    break;
            }
            if (!eol2 && len > ptr) {
                c = read();
            } else {
                if (ptr >= len) {
                    eol = false;
                    while (!eol) {
                        i = read();
                        c = i;
                        switch (i) {
                            case -1:
                            case 10:
                                eol = true;
                                break;
                            case 13:
                                eol = true;
                                cur = getFilePointer();
                                if (read() != 10) {
                                    seek(cur);
                                }
                                break;
                        }
                    }
                }
                if (c != -1 && ptr == 0) {
                    return false;
                }
                if (ptr + 2 <= len) {
                    input[ptr] = 32;
                    input[ptr + 1] = 88;
                    return true;
                }
                return true;
            }
        }
        if (ptr >= len) {
            eol = false;
            while (!eol) {
                i = read();
                c = i;
                switch (i) {
                    case -1:
                    case 10:
                        eol = true;
                        break;
                    case 13:
                        eol = true;
                        cur = getFilePointer();
                        if (read() != 10) {
                            seek(cur);
                        }
                        break;
                }
            }
        }
        if (c != -1) {
        }
        if (ptr + 2 <= len) {
            input[ptr] = 32;
            input[ptr + 1] = 88;
            return true;
        }
        return true;
    }

    static int[] checkObjectStart(byte[] line) {
        try {
            PRTokeniser tk = new PRTokeniser(line);
            if (!tk.nextToken() || tk.getTokenType() != 1) {
                return null;
            }
            int num = tk.intValue();
            if (!tk.nextToken() || tk.getTokenType() != 1) {
                return null;
            }
            int gen = tk.intValue();
            if (!tk.nextToken() || !tk.getStringValue().equals("obj")) {
                return null;
            }
            return new int[]{num, gen};
        } catch (Exception e) {
            return null;
        }
    }

    public boolean isHexString() {
        return this.hexString;
    }
}
