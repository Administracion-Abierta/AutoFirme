package org.mozilla.javascript;

import com.sun.jna.platform.win32.COM.tlb.imp.TlbConst;
import es.gob.afirma.standalone.configurator.common.PreferencesManager;
import java.io.IOException;
import java.io.Reader;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/TokenStream.class */
class TokenStream {
    private static final int EOF_CHAR = -1;
    private static final char BYTE_ORDER_MARK = 65279;
    private boolean dirtyLine;
    String regExpFlags;
    private double number;
    private boolean isBinary;
    private boolean isOldOctal;
    private boolean isOctal;
    private boolean isHex;
    private int quoteChar;
    private int stringBufferTop;
    private int ungetCursor;
    int lineno;
    private String sourceString;
    private Reader sourceReader;
    private char[] sourceBuffer;
    private int sourceEnd;
    int sourceCursor;
    int cursor;
    int tokenBeg;
    int tokenEnd;
    Token.CommentType commentType;
    private boolean xmlIsAttribute;
    private boolean xmlIsTagContent;
    private int xmlOpenTagsCount;
    private Parser parser;
    static final /* synthetic */ boolean $assertionsDisabled;
    private String string = "";
    private char[] stringBuffer = new char[128];
    private ObjToIntMap allStrings = new ObjToIntMap(50);
    private final int[] ungetBuffer = new int[3];
    private boolean hitEOF = false;
    private int lineStart = 0;
    private int lineEndChar = -1;
    private String commentPrefix = "";
    private int commentCursor = -1;

    static {
        $assertionsDisabled = !TokenStream.class.desiredAssertionStatus();
    }

    TokenStream(Parser parser, Reader sourceReader, String sourceString, int lineno) {
        this.parser = parser;
        this.lineno = lineno;
        if (sourceReader != null) {
            if (sourceString != null) {
                Kit.codeBug();
            }
            this.sourceReader = sourceReader;
            this.sourceBuffer = new char[512];
            this.sourceEnd = 0;
        } else {
            if (sourceString == null) {
                Kit.codeBug();
            }
            this.sourceString = sourceString;
            this.sourceEnd = sourceString.length();
        }
        this.cursor = 0;
        this.sourceCursor = 0;
    }

    String tokenToString(int token) {
        return "";
    }

    static boolean isKeyword(String s, int version, boolean isStrict) {
        return 0 != stringToKeyword(s, version, isStrict);
    }

    private static int stringToKeyword(String name, int version, boolean isStrict) {
        if (version < 200) {
            return stringToKeywordForJS(name);
        }
        return stringToKeywordForES(name, isStrict);
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code duplicated, block: B:181:0x081b A[PHI: r65 r67
      0x081b: PHI (r65v1 'id' int) = 
      (r65v0 'id' int)
      (r65v3 'id' int)
      (r65v0 'id' int)
      (r65v4 'id' int)
      (r65v5 'id' int)
      (r65v0 'id' int)
      (r65v6 'id' int)
      (r65v7 'id' int)
      (r65v8 'id' int)
      (r65v0 'id' int)
      (r65v9 'id' int)
      (r65v10 'id' int)
      (r65v11 'id' int)
      (r65v12 'id' int)
      (r65v13 'id' int)
      (r65v0 'id' int)
      (r65v14 'id' int)
      (r65v15 'id' int)
      (r65v16 'id' int)
      (r65v17 'id' int)
      (r65v18 'id' int)
      (r65v19 'id' int)
      (r65v0 'id' int)
      (r65v20 'id' int)
      (r65v21 'id' int)
      (r65v22 'id' int)
      (r65v23 'id' int)
      (r65v24 'id' int)
      (r65v25 'id' int)
      (r65v26 'id' int)
      (r65v27 'id' int)
      (r65v0 'id' int)
      (r65v28 'id' int)
      (r65v29 'id' int)
      (r65v30 'id' int)
      (r65v0 'id' int)
      (r65v31 'id' int)
      (r65v32 'id' int)
      (r65v33 'id' int)
      (r65v0 'id' int)
      (r65v34 'id' int)
      (r65v35 'id' int)
      (r65v0 'id' int)
      (r65v36 'id' int)
      (r65v37 'id' int)
      (r65v38 'id' int)
      (r65v39 'id' int)
      (r65v0 'id' int)
      (r65v40 'id' int)
      (r65v41 'id' int)
      (r65v42 'id' int)
      (r65v0 'id' int)
      (r65v43 'id' int)
      (r65v44 'id' int)
      (r65v0 'id' int)
      (r65v0 'id' int)
      (r65v0 'id' int)
      (r65v0 'id' int)
      (r65v0 'id' int)
      (r65v47 'id' int)
      (r65v48 'id' int)
      (r65v49 'id' int)
      (r65v0 'id' int)
      (r65v0 'id' int)
      (r65v0 'id' int)
      (r65v0 'id' int)
      (r65v0 'id' int)
      (r65v0 'id' int)
      (r65v0 'id' int)
      (r65v0 'id' int)
      (r65v0 'id' int)
      (r65v0 'id' int)
      (r65v54 'id' int)
      (r65v0 'id' int)
      (r65v0 'id' int)
      (r65v0 'id' int)
      (r65v0 'id' int)
      (r65v0 'id' int)
      (r65v0 'id' int)
      (r65v0 'id' int)
      (r65v0 'id' int)
      (r65v0 'id' int)
      (r65v0 'id' int)
      (r65v0 'id' int)
      (r65v0 'id' int)
      (r65v0 'id' int)
      (r65v0 'id' int)
      (r65v0 'id' int)
      (r65v0 'id' int)
      (r65v0 'id' int)
     binds: [B:3:0x011e, B:180:0x080f, B:178:0x0801, B:179:0x0804, B:176:0x07f1, B:172:0x07d3, B:173:0x07d6, B:170:0x07c3, B:167:0x07b0, B:158:0x0733, B:163:0x0792, B:162:0x0787, B:161:0x077b, B:160:0x0770, B:159:0x0764, B:149:0x06aa, B:155:0x071e, B:154:0x0712, B:153:0x0706, B:152:0x06fb, B:151:0x06f0, B:150:0x06e4, B:131:0x0599, B:146:0x0696, B:145:0x068a, B:144:0x067f, B:143:0x0673, B:142:0x0667, B:141:0x065b, B:140:0x064f, B:139:0x0643, B:137:0x0636, B:138:0x0639, B:135:0x0627, B:132:0x060c, B:104:0x0464, B:128:0x0585, B:127:0x057a, B:126:0x056e, B:124:0x055f, B:125:0x0562, B:122:0x054f, B:118:0x0531, B:119:0x0534, B:116:0x0521, B:113:0x0507, B:112:0x04fc, B:110:0x04ee, B:111:0x04f1, B:108:0x04df, B:105:0x04c4, B:53:0x02bd, B:101:0x0450, B:100:0x0445, B:94:0x0425, B:96:0x0430, B:98:0x043b, B:89:0x040c, B:91:0x0417, B:85:0x03ea, B:84:0x03de, B:83:0x03d2, B:77:0x03b1, B:79:0x03bc, B:81:0x03c7, B:72:0x0398, B:74:0x03a3, B:63:0x0360, B:65:0x036b, B:67:0x0376, B:58:0x0347, B:60:0x0352, B:54:0x0324, B:20:0x01b1, B:47:0x029f, B:49:0x02aa, B:42:0x0282, B:44:0x028d, B:37:0x0265, B:39:0x0270, B:32:0x0247, B:34:0x0252, B:27:0x0229, B:29:0x0234, B:22:0x020c, B:24:0x0217, B:15:0x0196, B:17:0x01a1, B:12:0x0188, B:7:0x016f] A[DONT_GENERATE, DONT_INLINE]
      0x081b: PHI (r67v1 'X' java.lang.String) = 
      (r67v0 'X' java.lang.String)
      (r67v2 'X' java.lang.String)
      (r67v0 'X' java.lang.String)
      (r67v3 'X' java.lang.String)
      (r67v4 'X' java.lang.String)
      (r67v0 'X' java.lang.String)
      (r67v5 'X' java.lang.String)
      (r67v6 'X' java.lang.String)
      (r67v7 'X' java.lang.String)
      (r67v0 'X' java.lang.String)
      (r67v8 'X' java.lang.String)
      (r67v9 'X' java.lang.String)
      (r67v10 'X' java.lang.String)
      (r67v11 'X' java.lang.String)
      (r67v12 'X' java.lang.String)
      (r67v0 'X' java.lang.String)
      (r67v13 'X' java.lang.String)
      (r67v14 'X' java.lang.String)
      (r67v15 'X' java.lang.String)
      (r67v16 'X' java.lang.String)
      (r67v17 'X' java.lang.String)
      (r67v18 'X' java.lang.String)
      (r67v0 'X' java.lang.String)
      (r67v19 'X' java.lang.String)
      (r67v20 'X' java.lang.String)
      (r67v21 'X' java.lang.String)
      (r67v22 'X' java.lang.String)
      (r67v23 'X' java.lang.String)
      (r67v24 'X' java.lang.String)
      (r67v25 'X' java.lang.String)
      (r67v26 'X' java.lang.String)
      (r67v0 'X' java.lang.String)
      (r67v27 'X' java.lang.String)
      (r67v28 'X' java.lang.String)
      (r67v29 'X' java.lang.String)
      (r67v0 'X' java.lang.String)
      (r67v30 'X' java.lang.String)
      (r67v31 'X' java.lang.String)
      (r67v32 'X' java.lang.String)
      (r67v0 'X' java.lang.String)
      (r67v33 'X' java.lang.String)
      (r67v34 'X' java.lang.String)
      (r67v0 'X' java.lang.String)
      (r67v35 'X' java.lang.String)
      (r67v36 'X' java.lang.String)
      (r67v37 'X' java.lang.String)
      (r67v38 'X' java.lang.String)
      (r67v0 'X' java.lang.String)
      (r67v39 'X' java.lang.String)
      (r67v40 'X' java.lang.String)
      (r67v41 'X' java.lang.String)
      (r67v0 'X' java.lang.String)
      (r67v42 'X' java.lang.String)
      (r67v43 'X' java.lang.String)
      (r67v0 'X' java.lang.String)
      (r67v0 'X' java.lang.String)
      (r67v0 'X' java.lang.String)
      (r67v0 'X' java.lang.String)
      (r67v0 'X' java.lang.String)
      (r67v44 'X' java.lang.String)
      (r67v45 'X' java.lang.String)
      (r67v46 'X' java.lang.String)
      (r67v0 'X' java.lang.String)
      (r67v0 'X' java.lang.String)
      (r67v0 'X' java.lang.String)
      (r67v0 'X' java.lang.String)
      (r67v0 'X' java.lang.String)
      (r67v0 'X' java.lang.String)
      (r67v0 'X' java.lang.String)
      (r67v0 'X' java.lang.String)
      (r67v0 'X' java.lang.String)
      (r67v0 'X' java.lang.String)
      (r67v47 'X' java.lang.String)
      (r67v0 'X' java.lang.String)
      (r67v0 'X' java.lang.String)
      (r67v0 'X' java.lang.String)
      (r67v0 'X' java.lang.String)
      (r67v0 'X' java.lang.String)
      (r67v0 'X' java.lang.String)
      (r67v0 'X' java.lang.String)
      (r67v0 'X' java.lang.String)
      (r67v0 'X' java.lang.String)
      (r67v0 'X' java.lang.String)
      (r67v0 'X' java.lang.String)
      (r67v0 'X' java.lang.String)
      (r67v0 'X' java.lang.String)
      (r67v0 'X' java.lang.String)
      (r67v0 'X' java.lang.String)
      (r67v0 'X' java.lang.String)
      (r67v0 'X' java.lang.String)
     binds: [B:3:0x011e, B:180:0x080f, B:178:0x0801, B:179:0x0804, B:176:0x07f1, B:172:0x07d3, B:173:0x07d6, B:170:0x07c3, B:167:0x07b0, B:158:0x0733, B:163:0x0792, B:162:0x0787, B:161:0x077b, B:160:0x0770, B:159:0x0764, B:149:0x06aa, B:155:0x071e, B:154:0x0712, B:153:0x0706, B:152:0x06fb, B:151:0x06f0, B:150:0x06e4, B:131:0x0599, B:146:0x0696, B:145:0x068a, B:144:0x067f, B:143:0x0673, B:142:0x0667, B:141:0x065b, B:140:0x064f, B:139:0x0643, B:137:0x0636, B:138:0x0639, B:135:0x0627, B:132:0x060c, B:104:0x0464, B:128:0x0585, B:127:0x057a, B:126:0x056e, B:124:0x055f, B:125:0x0562, B:122:0x054f, B:118:0x0531, B:119:0x0534, B:116:0x0521, B:113:0x0507, B:112:0x04fc, B:110:0x04ee, B:111:0x04f1, B:108:0x04df, B:105:0x04c4, B:53:0x02bd, B:101:0x0450, B:100:0x0445, B:94:0x0425, B:96:0x0430, B:98:0x043b, B:89:0x040c, B:91:0x0417, B:85:0x03ea, B:84:0x03de, B:83:0x03d2, B:77:0x03b1, B:79:0x03bc, B:81:0x03c7, B:72:0x0398, B:74:0x03a3, B:63:0x0360, B:65:0x036b, B:67:0x0376, B:58:0x0347, B:60:0x0352, B:54:0x0324, B:20:0x01b1, B:47:0x029f, B:49:0x02aa, B:42:0x0282, B:44:0x028d, B:37:0x0265, B:39:0x0270, B:32:0x0247, B:34:0x0252, B:27:0x0229, B:29:0x0234, B:22:0x020c, B:24:0x0217, B:15:0x0196, B:17:0x01a1, B:12:0x0188, B:7:0x016f] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARN: Code duplicated, block: B:183:0x0820  */
    /* JADX WARN: Switch 'out' block B:181:0x081b for B:104:0x0464 already processed. Defaulting to fallback option. */
    /* JADX WARN: Switch 'out' block B:181:0x081b for B:131:0x0599 already processed. Defaulting to fallback option. */
    /* JADX WARN: Switch 'out' block B:181:0x081b for B:149:0x06aa already processed. Defaulting to fallback option. */
    /* JADX WARN: Switch 'out' block B:181:0x081b for B:158:0x0733 already processed. Defaulting to fallback option. */
    private static int stringToKeywordForJS(String name) {
        int id = 0;
        String X = null;
        switch (name.length()) {
            case 2:
                int c = name.charAt(1);
                if (c == 102) {
                    if (name.charAt(0) == 'i') {
                        id = 113;
                    } else if (X != null && X != name && !X.equals(name)) {
                        id = 0;
                    }
                } else if (c == 110) {
                    if (name.charAt(0) == 'i') {
                        id = 52;
                    } else if (X != null) {
                        id = 0;
                    }
                } else if (c == 111 && name.charAt(0) == 'd') {
                    id = 119;
                } else if (X != null) {
                    id = 0;
                }
                break;
            case 3:
                switch (name.charAt(0)) {
                    case 'f':
                        if (name.charAt(2) == 'r' && name.charAt(1) == 'o') {
                            id = 120;
                        } else if (X != null) {
                            id = 0;
                        }
                        break;
                    case 'g':
                    case 'h':
                    case 'j':
                    case 'k':
                    case 'm':
                    case 'o':
                    case 'p':
                    case 'q':
                    case 'r':
                    case 's':
                    case 'u':
                    default:
                        if (X != null) {
                            id = 0;
                        }
                        break;
                    case 'i':
                        if (name.charAt(2) == 't' && name.charAt(1) == 'n') {
                            id = 128;
                        } else if (X != null) {
                            id = 0;
                        }
                        break;
                    case 'l':
                        if (name.charAt(2) == 't' && name.charAt(1) == 'e') {
                            id = 154;
                        } else if (X != null) {
                            id = 0;
                        }
                        break;
                    case 'n':
                        if (name.charAt(2) == 'w' && name.charAt(1) == 'e') {
                            id = 30;
                        } else if (X != null) {
                            id = 0;
                        }
                        break;
                    case 't':
                        if (name.charAt(2) == 'y' && name.charAt(1) == 'r') {
                            id = 82;
                        } else if (X != null) {
                            id = 0;
                        }
                        break;
                    case 'v':
                        if (name.charAt(2) == 'r' && name.charAt(1) == 'a') {
                            id = 123;
                        } else if (X != null) {
                            id = 0;
                        }
                        break;
                }
                break;
            case 4:
                switch (name.charAt(0)) {
                    case 'b':
                        X = "byte";
                        id = 128;
                        if (X != null) {
                            id = 0;
                        }
                        break;
                    case 'c':
                        int c2 = name.charAt(3);
                        if (c2 == 101) {
                            if (name.charAt(2) == 's' && name.charAt(1) == 'a') {
                                id = 116;
                            } else if (X != null) {
                                id = 0;
                            }
                        } else if (c2 == 114 && name.charAt(2) == 'a' && name.charAt(1) == 'h') {
                            id = 128;
                        } else if (X != null) {
                            id = 0;
                        }
                        break;
                    case 'd':
                    case 'f':
                    case 'h':
                    case 'i':
                    case 'j':
                    case 'k':
                    case 'm':
                    case 'o':
                    case 'p':
                    case 'q':
                    case 'r':
                    case 's':
                    case 'u':
                    default:
                        if (X != null) {
                            id = 0;
                        }
                        break;
                    case 'e':
                        int c3 = name.charAt(3);
                        if (c3 == 101) {
                            if (name.charAt(2) == 's' && name.charAt(1) == 'l') {
                                id = 114;
                            } else if (X != null) {
                                id = 0;
                            }
                        } else if (c3 == 109 && name.charAt(2) == 'u' && name.charAt(1) == 'n') {
                            id = 128;
                        } else if (X != null) {
                            id = 0;
                        }
                        break;
                    case 'g':
                        X = "goto";
                        id = 128;
                        if (X != null) {
                            id = 0;
                        }
                        break;
                    case 'l':
                        X = "long";
                        id = 128;
                        if (X != null) {
                            id = 0;
                        }
                        break;
                    case 'n':
                        X = "null";
                        id = 42;
                        if (X != null) {
                            id = 0;
                        }
                        break;
                    case 't':
                        int c4 = name.charAt(3);
                        if (c4 == 101) {
                            if (name.charAt(2) == 'u' && name.charAt(1) == 'r') {
                                id = 45;
                            } else if (X != null) {
                                id = 0;
                            }
                        } else if (c4 == 115 && name.charAt(2) == 'i' && name.charAt(1) == 'h') {
                            id = 43;
                        } else if (X != null) {
                            id = 0;
                        }
                        break;
                    case 'v':
                        X = "void";
                        id = 127;
                        if (X != null) {
                            id = 0;
                        }
                        break;
                    case 'w':
                        X = "with";
                        id = 124;
                        if (X != null) {
                            id = 0;
                        }
                        break;
                }
                break;
            case 5:
                switch (name.charAt(2)) {
                    case 'a':
                        X = "class";
                        id = 128;
                        break;
                    case 'e':
                        int c5 = name.charAt(0);
                        if (c5 == 98) {
                            X = "break";
                            id = 121;
                        } else if (c5 == 121) {
                            X = "yield";
                            id = 73;
                        }
                        break;
                    case 'i':
                        X = "while";
                        id = 118;
                        break;
                    case 'l':
                        X = "false";
                        id = 44;
                        break;
                    case 'n':
                        int c6 = name.charAt(0);
                        if (c6 == 99) {
                            X = "const";
                            id = 155;
                        } else if (c6 == 102) {
                            X = "final";
                            id = 128;
                        }
                        break;
                    case 'o':
                        int c7 = name.charAt(0);
                        if (c7 == 102) {
                            X = "float";
                            id = 128;
                        } else if (c7 == 115) {
                            X = "short";
                            id = 128;
                        }
                        break;
                    case 'p':
                        X = "super";
                        id = 128;
                        break;
                    case 'r':
                        X = "throw";
                        id = 50;
                        break;
                    case 't':
                        X = "catch";
                        id = 125;
                        break;
                }
                if (X != null) {
                    id = 0;
                }
                break;
            case 6:
                switch (name.charAt(1)) {
                    case 'a':
                        X = "native";
                        id = 128;
                        break;
                    case 'e':
                        int c8 = name.charAt(0);
                        if (c8 == 100) {
                            X = "delete";
                            id = 31;
                        } else if (c8 == 114) {
                            X = "return";
                            id = 4;
                        }
                        break;
                    case 'h':
                        X = "throws";
                        id = 128;
                        break;
                    case 'm':
                        X = "import";
                        id = 128;
                        break;
                    case 'o':
                        X = "double";
                        id = 128;
                        break;
                    case 't':
                        X = "static";
                        id = 128;
                        break;
                    case 'u':
                        X = "public";
                        id = 128;
                        break;
                    case 'w':
                        X = "switch";
                        id = 115;
                        break;
                    case 'x':
                        X = "export";
                        id = 128;
                        break;
                    case 'y':
                        X = "typeof";
                        id = 32;
                        break;
                }
                if (X != null) {
                    id = 0;
                }
                break;
            case 7:
                switch (name.charAt(1)) {
                    case 'a':
                        X = "package";
                        id = 128;
                        break;
                    case 'e':
                        X = PreferencesManager.VALUE_KEYSTORE_DEFAULT;
                        id = 117;
                        break;
                    case 'i':
                        X = "finally";
                        id = 126;
                        break;
                    case 'o':
                        X = "boolean";
                        id = 128;
                        break;
                    case 'r':
                        X = "private";
                        id = 128;
                        break;
                    case 'x':
                        X = "extends";
                        id = 128;
                        break;
                }
                if (X != null) {
                    id = 0;
                }
                break;
            case 8:
                switch (name.charAt(0)) {
                    case 'a':
                        X = "abstract";
                        id = 128;
                        break;
                    case 'c':
                        X = "continue";
                        id = 122;
                        break;
                    case 'd':
                        X = "debugger";
                        id = 161;
                        break;
                    case 'f':
                        X = "function";
                        id = 110;
                        break;
                    case 'v':
                        X = "volatile";
                        id = 128;
                        break;
                }
                if (X != null) {
                    id = 0;
                }
                break;
            case 9:
                int c9 = name.charAt(0);
                if (c9 == 105) {
                    X = "interface";
                    id = 128;
                } else if (c9 == 112) {
                    X = "protected";
                    id = 128;
                } else if (c9 == 116) {
                    X = "transient";
                    id = 128;
                }
                if (X != null) {
                    id = 0;
                }
                break;
            case 10:
                int c10 = name.charAt(1);
                if (c10 == 109) {
                    X = "implements";
                    id = 128;
                } else if (c10 == 110) {
                    X = "instanceof";
                    id = 53;
                }
                if (X != null) {
                    id = 0;
                }
                break;
            case 11:
            default:
                if (X != null) {
                    id = 0;
                }
                break;
            case 12:
                X = "synchronized";
                id = 128;
                if (X != null) {
                    id = 0;
                }
                break;
        }
        if (id == 0) {
            return 0;
        }
        return id & 255;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code duplicated, block: B:164:0x066c A[PHI: r51 r53
      0x066c: PHI (r51v1 'id' int) = 
      (r51v0 'id' int)
      (r51v0 'id' int)
      (r51v3 'id' int)
      (r51v4 'id' int)
      (r51v0 'id' int)
      (r51v0 'id' int)
      (r51v5 'id' int)
      (r51v6 'id' int)
      (r51v0 'id' int)
      (r51v7 'id' int)
      (r51v8 'id' int)
      (r51v9 'id' int)
      (r51v0 'id' int)
      (r51v10 'id' int)
      (r51v11 'id' int)
      (r51v12 'id' int)
      (r51v13 'id' int)
      (r51v14 'id' int)
      (r51v0 'id' int)
      (r51v15 'id' int)
      (r51v16 'id' int)
      (r51v17 'id' int)
      (r51v18 'id' int)
      (r51v19 'id' int)
      (r51v20 'id' int)
      (r51v0 'id' int)
      (r51v21 'id' int)
      (r51v22 'id' int)
      (r51v0 'id' int)
      (r51v23 'id' int)
      (r51v24 'id' int)
      (r51v25 'id' int)
      (r51v26 'id' int)
      (r51v27 'id' int)
      (r51v28 'id' int)
      (r51v0 'id' int)
      (r51v29 'id' int)
      (r51v30 'id' int)
      (r51v0 'id' int)
      (r51v31 'id' int)
      (r51v32 'id' int)
      (r51v0 'id' int)
      (r51v33 'id' int)
      (r51v34 'id' int)
      (r51v0 'id' int)
      (r51v0 'id' int)
      (r51v0 'id' int)
      (r51v0 'id' int)
      (r51v0 'id' int)
      (r51v37 'id' int)
      (r51v0 'id' int)
      (r51v0 'id' int)
      (r51v0 'id' int)
      (r51v0 'id' int)
      (r51v0 'id' int)
      (r51v0 'id' int)
      (r51v0 'id' int)
      (r51v0 'id' int)
      (r51v0 'id' int)
      (r51v0 'id' int)
      (r51v0 'id' int)
      (r51v0 'id' int)
      (r51v0 'id' int)
      (r51v0 'id' int)
      (r51v0 'id' int)
      (r51v0 'id' int)
      (r51v0 'id' int)
      (r51v0 'id' int)
      (r51v0 'id' int)
      (r51v0 'id' int)
      (r51v0 'id' int)
      (r51v0 'id' int)
      (r51v0 'id' int)
     binds: [B:3:0x00d4, B:162:0x065e, B:163:0x0661, B:160:0x064e, B:152:0x0628, B:154:0x062c, B:155:0x062f, B:150:0x0618, B:141:0x05c3, B:144:0x05f7, B:143:0x05eb, B:142:0x05e0, B:129:0x0546, B:138:0x05ae, B:137:0x05a2, B:134:0x0593, B:133:0x0588, B:132:0x057c, B:110:0x0463, B:126:0x0532, B:125:0x0526, B:124:0x051b, B:123:0x050f, B:120:0x04ff, B:117:0x04ef, B:115:0x04e2, B:116:0x04e5, B:113:0x04d3, B:89:0x035f, B:107:0x044f, B:106:0x0444, B:105:0x0438, B:104:0x042c, B:103:0x0421, B:102:0x0416, B:100:0x0408, B:101:0x040b, B:98:0x03f9, B:94:0x03db, B:95:0x03de, B:92:0x03cb, B:48:0x022f, B:86:0x034b, B:85:0x0340, B:79:0x0320, B:81:0x032b, B:83:0x0336, B:74:0x0307, B:76:0x0312, B:70:0x02e5, B:64:0x02c4, B:66:0x02cf, B:68:0x02da, B:59:0x02ab, B:61:0x02b6, B:50:0x0274, B:52:0x027f, B:54:0x028a, B:20:0x0161, B:42:0x0211, B:44:0x021c, B:37:0x01f4, B:39:0x01ff, B:32:0x01d7, B:34:0x01e2, B:27:0x01b9, B:29:0x01c4, B:22:0x019c, B:24:0x01a7, B:15:0x0146, B:17:0x0151, B:12:0x0138, B:7:0x011f] A[DONT_GENERATE, DONT_INLINE]
      0x066c: PHI (r53v1 'X' java.lang.String) = 
      (r53v0 'X' java.lang.String)
      (r53v0 'X' java.lang.String)
      (r53v2 'X' java.lang.String)
      (r53v3 'X' java.lang.String)
      (r53v0 'X' java.lang.String)
      (r53v0 'X' java.lang.String)
      (r53v4 'X' java.lang.String)
      (r53v5 'X' java.lang.String)
      (r53v0 'X' java.lang.String)
      (r53v6 'X' java.lang.String)
      (r53v7 'X' java.lang.String)
      (r53v8 'X' java.lang.String)
      (r53v0 'X' java.lang.String)
      (r53v9 'X' java.lang.String)
      (r53v10 'X' java.lang.String)
      (r53v11 'X' java.lang.String)
      (r53v12 'X' java.lang.String)
      (r53v13 'X' java.lang.String)
      (r53v0 'X' java.lang.String)
      (r53v14 'X' java.lang.String)
      (r53v15 'X' java.lang.String)
      (r53v16 'X' java.lang.String)
      (r53v17 'X' java.lang.String)
      (r53v18 'X' java.lang.String)
      (r53v19 'X' java.lang.String)
      (r53v0 'X' java.lang.String)
      (r53v20 'X' java.lang.String)
      (r53v21 'X' java.lang.String)
      (r53v0 'X' java.lang.String)
      (r53v22 'X' java.lang.String)
      (r53v23 'X' java.lang.String)
      (r53v24 'X' java.lang.String)
      (r53v25 'X' java.lang.String)
      (r53v26 'X' java.lang.String)
      (r53v27 'X' java.lang.String)
      (r53v0 'X' java.lang.String)
      (r53v28 'X' java.lang.String)
      (r53v29 'X' java.lang.String)
      (r53v0 'X' java.lang.String)
      (r53v30 'X' java.lang.String)
      (r53v31 'X' java.lang.String)
      (r53v0 'X' java.lang.String)
      (r53v32 'X' java.lang.String)
      (r53v33 'X' java.lang.String)
      (r53v0 'X' java.lang.String)
      (r53v0 'X' java.lang.String)
      (r53v0 'X' java.lang.String)
      (r53v0 'X' java.lang.String)
      (r53v0 'X' java.lang.String)
      (r53v34 'X' java.lang.String)
      (r53v0 'X' java.lang.String)
      (r53v0 'X' java.lang.String)
      (r53v0 'X' java.lang.String)
      (r53v0 'X' java.lang.String)
      (r53v0 'X' java.lang.String)
      (r53v0 'X' java.lang.String)
      (r53v0 'X' java.lang.String)
      (r53v0 'X' java.lang.String)
      (r53v0 'X' java.lang.String)
      (r53v0 'X' java.lang.String)
      (r53v0 'X' java.lang.String)
      (r53v0 'X' java.lang.String)
      (r53v0 'X' java.lang.String)
      (r53v0 'X' java.lang.String)
      (r53v0 'X' java.lang.String)
      (r53v0 'X' java.lang.String)
      (r53v0 'X' java.lang.String)
      (r53v0 'X' java.lang.String)
      (r53v0 'X' java.lang.String)
      (r53v0 'X' java.lang.String)
      (r53v0 'X' java.lang.String)
      (r53v0 'X' java.lang.String)
      (r53v0 'X' java.lang.String)
     binds: [B:3:0x00d4, B:162:0x065e, B:163:0x0661, B:160:0x064e, B:152:0x0628, B:154:0x062c, B:155:0x062f, B:150:0x0618, B:141:0x05c3, B:144:0x05f7, B:143:0x05eb, B:142:0x05e0, B:129:0x0546, B:138:0x05ae, B:137:0x05a2, B:134:0x0593, B:133:0x0588, B:132:0x057c, B:110:0x0463, B:126:0x0532, B:125:0x0526, B:124:0x051b, B:123:0x050f, B:120:0x04ff, B:117:0x04ef, B:115:0x04e2, B:116:0x04e5, B:113:0x04d3, B:89:0x035f, B:107:0x044f, B:106:0x0444, B:105:0x0438, B:104:0x042c, B:103:0x0421, B:102:0x0416, B:100:0x0408, B:101:0x040b, B:98:0x03f9, B:94:0x03db, B:95:0x03de, B:92:0x03cb, B:48:0x022f, B:86:0x034b, B:85:0x0340, B:79:0x0320, B:81:0x032b, B:83:0x0336, B:74:0x0307, B:76:0x0312, B:70:0x02e5, B:64:0x02c4, B:66:0x02cf, B:68:0x02da, B:59:0x02ab, B:61:0x02b6, B:50:0x0274, B:52:0x027f, B:54:0x028a, B:20:0x0161, B:42:0x0211, B:44:0x021c, B:37:0x01f4, B:39:0x01ff, B:32:0x01d7, B:34:0x01e2, B:27:0x01b9, B:29:0x01c4, B:22:0x019c, B:24:0x01a7, B:15:0x0146, B:17:0x0151, B:12:0x0138, B:7:0x011f] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARN: Code duplicated, block: B:166:0x0671  */
    /* JADX WARN: Switch 'out' block B:164:0x066c for B:110:0x0463 already processed. Defaulting to fallback option. */
    /* JADX WARN: Switch 'out' block B:164:0x066c for B:129:0x0546 already processed. Defaulting to fallback option. */
    /* JADX WARN: Switch 'out' block B:164:0x066c for B:141:0x05c3 already processed. Defaulting to fallback option. */
    /* JADX WARN: Switch 'out' block B:164:0x066c for B:89:0x035f already processed. Defaulting to fallback option. */
    private static int stringToKeywordForES(String name, boolean isStrict) {
        int id = 0;
        String X = null;
        switch (name.length()) {
            case 2:
                int c = name.charAt(1);
                if (c == 102) {
                    if (name.charAt(0) == 'i') {
                        id = 113;
                    } else if (X != null && X != name && !X.equals(name)) {
                        id = 0;
                    }
                } else if (c == 110) {
                    if (name.charAt(0) == 'i') {
                        id = 52;
                    } else if (X != null) {
                        id = 0;
                    }
                } else if (c == 111 && name.charAt(0) == 'd') {
                    id = 119;
                } else if (X != null) {
                    id = 0;
                }
                break;
            case 3:
                switch (name.charAt(0)) {
                    case 'f':
                        if (name.charAt(2) == 'r' && name.charAt(1) == 'o') {
                            id = 120;
                        } else if (X != null) {
                            id = 0;
                        }
                        break;
                    case 'l':
                        if (name.charAt(2) == 't' && name.charAt(1) == 'e') {
                            id = 154;
                        } else if (X != null) {
                            id = 0;
                        }
                        break;
                    case 'n':
                        if (name.charAt(2) == 'w' && name.charAt(1) == 'e') {
                            id = 30;
                        } else if (X != null) {
                            id = 0;
                        }
                        break;
                    case 't':
                        if (name.charAt(2) == 'y' && name.charAt(1) == 'r') {
                            id = 82;
                        } else if (X != null) {
                            id = 0;
                        }
                        break;
                    case 'v':
                        if (name.charAt(2) == 'r' && name.charAt(1) == 'a') {
                            id = 123;
                        } else if (X != null) {
                            id = 0;
                        }
                        break;
                    default:
                        if (X != null) {
                            id = 0;
                        }
                        break;
                }
                break;
            case 4:
                switch (name.charAt(0)) {
                    case 'c':
                        if (name.charAt(3) == 101 && name.charAt(2) == 's' && name.charAt(1) == 'a') {
                            id = 116;
                        } else if (X != null) {
                            id = 0;
                        }
                        break;
                    case 'e':
                        int c2 = name.charAt(3);
                        if (c2 == 101) {
                            if (name.charAt(2) == 's' && name.charAt(1) == 'l') {
                                id = 114;
                            } else if (X != null) {
                                id = 0;
                            }
                        } else if (c2 == 109 && name.charAt(2) == 'u' && name.charAt(1) == 'n') {
                            id = 128;
                        } else if (X != null) {
                            id = 0;
                        }
                        break;
                    case 'n':
                        X = "null";
                        id = 42;
                        if (X != null) {
                            id = 0;
                        }
                        break;
                    case 't':
                        int c3 = name.charAt(3);
                        if (c3 == 101) {
                            if (name.charAt(2) == 'u' && name.charAt(1) == 'r') {
                                id = 45;
                            } else if (X != null) {
                                id = 0;
                            }
                        } else if (c3 == 115 && name.charAt(2) == 'i' && name.charAt(1) == 'h') {
                            id = 43;
                        } else if (X != null) {
                            id = 0;
                        }
                        break;
                    case 'v':
                        X = "void";
                        id = 127;
                        if (X != null) {
                            id = 0;
                        }
                        break;
                    case 'w':
                        X = "with";
                        id = 124;
                        if (X != null) {
                            id = 0;
                        }
                        break;
                    default:
                        if (X != null) {
                            id = 0;
                        }
                        break;
                }
                break;
            case 5:
                switch (name.charAt(2)) {
                    case 'a':
                        int c4 = name.charAt(0);
                        if (c4 == 99) {
                            X = "class";
                            id = 128;
                        } else if (c4 == 97) {
                            X = "await";
                            id = 128;
                        }
                        break;
                    case 'e':
                        int c5 = name.charAt(0);
                        if (c5 == 98) {
                            X = "break";
                            id = 121;
                        } else if (c5 == 121) {
                            X = "yield";
                            id = 73;
                        }
                        break;
                    case 'i':
                        X = "while";
                        id = 118;
                        break;
                    case 'l':
                        X = "false";
                        id = 44;
                        break;
                    case 'n':
                        X = "const";
                        id = 155;
                        break;
                    case 'p':
                        X = "super";
                        id = 128;
                        break;
                    case 'r':
                        X = "throw";
                        id = 50;
                        break;
                    case 't':
                        X = "catch";
                        id = 125;
                        break;
                }
                if (X != null) {
                    id = 0;
                }
                break;
            case 6:
                switch (name.charAt(1)) {
                    case 'e':
                        int c6 = name.charAt(0);
                        if (c6 == 100) {
                            X = "delete";
                            id = 31;
                        } else if (c6 == 114) {
                            X = "return";
                            id = 4;
                        }
                        break;
                    case 'm':
                        X = "import";
                        id = 128;
                        break;
                    case 't':
                        if (isStrict) {
                            X = "static";
                            id = 128;
                            break;
                        }
                    case 'u':
                        if (isStrict) {
                            X = "public";
                            id = 128;
                            break;
                        }
                    case 'w':
                        X = "switch";
                        id = 115;
                        break;
                    case 'x':
                        X = "export";
                        id = 128;
                        break;
                    case 'y':
                        X = "typeof";
                        id = 32;
                        break;
                }
                if (X != null) {
                    id = 0;
                }
                break;
            case 7:
                switch (name.charAt(1)) {
                    case 'a':
                        if (isStrict) {
                            X = "package";
                            id = 128;
                            break;
                        }
                    case 'e':
                        X = PreferencesManager.VALUE_KEYSTORE_DEFAULT;
                        id = 117;
                        break;
                    case 'i':
                        X = "finally";
                        id = 126;
                        break;
                    case 'r':
                        if (isStrict) {
                            X = "private";
                            id = 128;
                            break;
                        }
                    case 'x':
                        X = "extends";
                        id = 128;
                        break;
                }
                if (X != null) {
                    id = 0;
                }
                break;
            case 8:
                switch (name.charAt(0)) {
                    case 'c':
                        X = "continue";
                        id = 122;
                        break;
                    case 'd':
                        X = "debugger";
                        id = 161;
                        break;
                    case 'f':
                        X = "function";
                        id = 110;
                        break;
                }
                if (X != null) {
                    id = 0;
                }
                break;
            case 9:
                int c7 = name.charAt(0);
                if (c7 == 105 && isStrict) {
                    X = "interface";
                    id = 128;
                } else if (c7 == 112 && isStrict) {
                    X = "protected";
                    id = 128;
                }
                if (X != null) {
                    id = 0;
                }
                break;
            case 10:
                int c8 = name.charAt(1);
                if (c8 == 109 && isStrict) {
                    X = "implements";
                    id = 128;
                } else if (c8 == 110) {
                    X = "instanceof";
                    id = 53;
                }
                if (X != null) {
                    id = 0;
                }
                break;
            default:
                if (X != null) {
                    id = 0;
                }
                break;
        }
        if (id == 0) {
            return 0;
        }
        return id & 255;
    }

    final String getSourceString() {
        return this.sourceString;
    }

    final int getLineno() {
        return this.lineno;
    }

    final String getString() {
        return this.string;
    }

    final char getQuoteChar() {
        return (char) this.quoteChar;
    }

    final double getNumber() {
        return this.number;
    }

    final boolean isNumberBinary() {
        return this.isBinary;
    }

    final boolean isNumberOldOctal() {
        return this.isOldOctal;
    }

    final boolean isNumberOctal() {
        return this.isOctal;
    }

    final boolean isNumberHex() {
        return this.isHex;
    }

    final boolean eof() {
        return this.hitEOF;
    }

    final int getToken() throws IOException {
        int c;
        boolean identifierStart;
        double dval;
        int c2;
        do {
            c = getChar();
            if (c == -1) {
                this.tokenBeg = this.cursor - 1;
                this.tokenEnd = this.cursor;
                return 0;
            }
            if (c == 10) {
                this.dirtyLine = false;
                this.tokenBeg = this.cursor - 1;
                this.tokenEnd = this.cursor;
                return 1;
            }
        } while (isJSSpace(c));
        if (c != 45) {
            this.dirtyLine = true;
        }
        this.tokenBeg = this.cursor - 1;
        this.tokenEnd = this.cursor;
        if (c == 64) {
            return 148;
        }
        boolean isUnicodeEscapeStart = false;
        if (c == 92) {
            c = getChar();
            if (c == 117) {
                identifierStart = true;
                isUnicodeEscapeStart = true;
                this.stringBufferTop = 0;
            } else {
                identifierStart = false;
                ungetChar(c);
                c = 92;
            }
        } else {
            identifierStart = Character.isJavaIdentifierStart((char) c);
            if (identifierStart) {
                this.stringBufferTop = 0;
                addToString(c);
            }
        }
        if (identifierStart) {
            boolean containsEscape = isUnicodeEscapeStart;
            while (true) {
                if (isUnicodeEscapeStart) {
                    int escapeVal = 0;
                    for (int i = 0; i != 4; i++) {
                        escapeVal = Kit.xDigitToInt(getChar(), escapeVal);
                        if (escapeVal < 0) {
                            break;
                        }
                    }
                    if (escapeVal < 0) {
                        this.parser.addError("msg.invalid.escape");
                        return -1;
                    }
                    addToString(escapeVal);
                    isUnicodeEscapeStart = false;
                } else {
                    int c3 = getChar();
                    if (c3 == 92) {
                        int c4 = getChar();
                        if (c4 == 117) {
                            isUnicodeEscapeStart = true;
                            containsEscape = true;
                        } else {
                            this.parser.addError("msg.illegal.character", c4);
                            return -1;
                        }
                    } else if (c3 != -1 && c3 != BYTE_ORDER_MARK && Character.isJavaIdentifierPart((char) c3)) {
                        addToString(c3);
                    } else {
                        ungetChar(c3);
                        String str = getStringFromBuffer();
                        if (!containsEscape) {
                            int result = stringToKeyword(str, this.parser.compilerEnv.getLanguageVersion(), this.parser.inUseStrictDirective());
                            if (result != 0) {
                                if ((result == 154 || result == 73) && this.parser.compilerEnv.getLanguageVersion() < 170) {
                                    this.string = result == 154 ? "let" : "yield";
                                    result = 39;
                                }
                                this.string = (String) this.allStrings.intern(str);
                                if (result != 128) {
                                    return result;
                                }
                                if (this.parser.compilerEnv.getLanguageVersion() >= 200) {
                                    return result;
                                }
                                if (!this.parser.compilerEnv.isReservedKeywordAsIdentifier()) {
                                    return result;
                                }
                            }
                        } else if (isKeyword(str, this.parser.compilerEnv.getLanguageVersion(), this.parser.inUseStrictDirective())) {
                            str = convertLastCharToHex(str);
                        }
                        this.string = (String) this.allStrings.intern(str);
                        return 39;
                    }
                }
            }
        } else {
            if (isDigit(c) || (c == 46 && isDigit(peekChar()))) {
                this.stringBufferTop = 0;
                int base = 10;
                this.isBinary = false;
                this.isOctal = false;
                this.isOldOctal = false;
                this.isHex = false;
                boolean es6 = this.parser.compilerEnv.getLanguageVersion() >= 200;
                if (c == 48) {
                    c = getChar();
                    if (c == 120 || c == 88) {
                        base = 16;
                        this.isHex = true;
                        c = getChar();
                    } else if (es6 && (c == 111 || c == 79)) {
                        base = 8;
                        this.isOctal = true;
                        c = getChar();
                    } else if (es6 && (c == 98 || c == 66)) {
                        base = 2;
                        this.isBinary = true;
                        c = getChar();
                    } else if (isDigit(c)) {
                        base = 8;
                        this.isOldOctal = true;
                    } else {
                        addToString(48);
                    }
                }
                boolean isEmpty = true;
                if (base == 16) {
                    while (0 <= Kit.xDigitToInt(c, 0)) {
                        addToString(c);
                        c = getChar();
                        isEmpty = false;
                    }
                } else {
                    while (48 <= c && c <= 57) {
                        if (base == 8 && c >= 56) {
                            if (this.isOldOctal) {
                                this.parser.addWarning("msg.bad.octal.literal", c == 56 ? TlbConst.TYPELIB_MAJOR_VERSION_WORD : "9");
                                base = 10;
                            } else {
                                this.parser.addError("msg.caught.nfe");
                                return -1;
                            }
                        } else if (base == 2 && c >= 50) {
                            this.parser.addError("msg.caught.nfe");
                            return -1;
                        }
                        addToString(c);
                        c = getChar();
                        isEmpty = false;
                    }
                }
                if (isEmpty && (this.isBinary || this.isOctal || this.isHex)) {
                    this.parser.addError("msg.caught.nfe");
                    return -1;
                }
                boolean isInteger = true;
                if (base == 10 && (c == 46 || c == 101 || c == 69)) {
                    isInteger = false;
                    if (c == 46) {
                        do {
                            addToString(c);
                            c = getChar();
                        } while (isDigit(c));
                    }
                    if (c == 101 || c == 69) {
                        addToString(c);
                        c = getChar();
                        if (c == 43 || c == 45) {
                            addToString(c);
                            c = getChar();
                        }
                        if (!isDigit(c)) {
                            this.parser.addError("msg.missing.exponent");
                            return -1;
                        }
                        do {
                            addToString(c);
                            c = getChar();
                        } while (isDigit(c));
                    }
                }
                ungetChar(c);
                String numString = getStringFromBuffer();
                this.string = numString;
                if (base == 10 && !isInteger) {
                    try {
                        dval = Double.parseDouble(numString);
                    } catch (NumberFormatException e) {
                        this.parser.addError("msg.caught.nfe");
                        return -1;
                    }
                } else {
                    dval = ScriptRuntime.stringPrefixToNumber(numString, 0, base);
                }
                this.number = dval;
                return 40;
            }
            if (c == 34 || c == 39 || c == 96) {
                this.quoteChar = c;
                this.stringBufferTop = 0;
                int c5 = getChar(false);
                while (c5 != this.quoteChar) {
                    if (c5 == 10 || c5 == -1) {
                        ungetChar(c5);
                        this.tokenEnd = this.cursor;
                        this.parser.addError("msg.unterminated.string.lit");
                        return -1;
                    }
                    if (c5 == 92) {
                        c5 = getChar();
                        switch (c5) {
                            case 10:
                                c5 = getChar();
                                continue;
                            case 98:
                                c5 = 8;
                                break;
                            case 102:
                                c5 = 12;
                                break;
                            case 110:
                                c5 = 10;
                                break;
                            case 114:
                                c5 = 13;
                                break;
                            case 116:
                                c5 = 9;
                                break;
                            case 117:
                                int escapeStart = this.stringBufferTop;
                                addToString(117);
                                int escapeVal2 = 0;
                                int i2 = 0;
                                while (true) {
                                    if (i2 != 4) {
                                        c5 = getChar();
                                        escapeVal2 = Kit.xDigitToInt(c5, escapeVal2);
                                        if (escapeVal2 < 0) {
                                        }
                                        addToString(c5);
                                        i2++;
                                    } else {
                                        this.stringBufferTop = escapeStart;
                                        c5 = escapeVal2;
                                    }
                                    break;
                                }
                                break;
                            case 118:
                                c5 = 11;
                                break;
                            case 120:
                                c5 = getChar();
                                int escapeVal3 = Kit.xDigitToInt(c5, 0);
                                if (escapeVal3 < 0) {
                                    addToString(120);
                                } else {
                                    c5 = getChar();
                                    int escapeVal4 = Kit.xDigitToInt(c5, escapeVal3);
                                    if (escapeVal4 < 0) {
                                        addToString(120);
                                        addToString(c5);
                                    } else {
                                        c5 = escapeVal4;
                                    }
                                }
                                break;
                            default:
                                if (48 <= c5 && c5 < 56) {
                                    int val = c5 - 48;
                                    int c6 = getChar();
                                    if (48 <= c6 && c6 < 56) {
                                        val = ((8 * val) + c6) - 48;
                                        c6 = getChar();
                                        if (48 <= c6 && c6 < 56 && val <= 31) {
                                            val = ((8 * val) + c6) - 48;
                                            c6 = getChar();
                                        }
                                    }
                                    ungetChar(c6);
                                    c5 = val;
                                }
                                break;
                        }
                    }
                    addToString(c5);
                    c5 = getChar(false);
                }
                this.string = (String) this.allStrings.intern(getStringFromBuffer());
                return 41;
            }
            switch (c) {
                case 33:
                    if (matchChar(61)) {
                        if (matchChar(61)) {
                            return 47;
                        }
                        return 13;
                    }
                    return 26;
                case 34:
                case 35:
                case 36:
                case 39:
                case 48:
                case 49:
                case 50:
                case 51:
                case 52:
                case 53:
                case 54:
                case 55:
                case 56:
                case 57:
                case 64:
                case 65:
                case 66:
                case 67:
                case 68:
                case 69:
                case 70:
                case 71:
                case 72:
                case 73:
                case 74:
                case 75:
                case 76:
                case 77:
                case 78:
                case 79:
                case 80:
                case 81:
                case 82:
                case 83:
                case 84:
                case 85:
                case 86:
                case 87:
                case 88:
                case 89:
                case 90:
                case 92:
                case 95:
                case 96:
                case 97:
                case 98:
                case 99:
                case 100:
                case 101:
                case 102:
                case 103:
                case 104:
                case 105:
                case 106:
                case 107:
                case 108:
                case 109:
                case 110:
                case 111:
                case 112:
                case 113:
                case 114:
                case 115:
                case 116:
                case 117:
                case 118:
                case 119:
                case 120:
                case 121:
                case 122:
                default:
                    this.parser.addError("msg.illegal.character", c);
                    return -1;
                case 37:
                    if (matchChar(61)) {
                        return 102;
                    }
                    return 25;
                case 38:
                    if (matchChar(38)) {
                        return 106;
                    }
                    if (matchChar(61)) {
                        return 94;
                    }
                    return 11;
                case 40:
                    return 88;
                case 41:
                    return 89;
                case 42:
                    if (matchChar(61)) {
                        return 100;
                    }
                    return 23;
                case 43:
                    if (matchChar(61)) {
                        return 98;
                    }
                    if (matchChar(43)) {
                        return 107;
                    }
                    return 21;
                case 44:
                    return 90;
                case 45:
                    if (matchChar(61)) {
                        c2 = 99;
                    } else if (matchChar(45)) {
                        if (!this.dirtyLine && matchChar(62)) {
                            markCommentStart("--");
                            skipLine();
                            this.commentType = Token.CommentType.HTML;
                            return 162;
                        }
                        c2 = 108;
                    } else {
                        c2 = 22;
                    }
                    this.dirtyLine = true;
                    return c2;
                case 46:
                    if (matchChar(46)) {
                        return 144;
                    }
                    if (matchChar(40)) {
                        return 147;
                    }
                    return 109;
                case 47:
                    markCommentStart();
                    if (matchChar(47)) {
                        this.tokenBeg = this.cursor - 2;
                        skipLine();
                        this.commentType = Token.CommentType.LINE;
                        return 162;
                    }
                    if (matchChar(42)) {
                        boolean lookForSlash = false;
                        this.tokenBeg = this.cursor - 2;
                        if (matchChar(42)) {
                            lookForSlash = true;
                            this.commentType = Token.CommentType.JSDOC;
                        } else {
                            this.commentType = Token.CommentType.BLOCK_COMMENT;
                        }
                        while (true) {
                            int c7 = getChar();
                            if (c7 == -1) {
                                this.tokenEnd = this.cursor - 1;
                                this.parser.addError("msg.unterminated.comment");
                                return 162;
                            }
                            if (c7 == 42) {
                                lookForSlash = true;
                            } else if (c7 == 47) {
                                if (lookForSlash) {
                                    this.tokenEnd = this.cursor;
                                    return 162;
                                }
                            } else {
                                lookForSlash = false;
                                this.tokenEnd = this.cursor;
                            }
                        }
                    } else {
                        if (matchChar(61)) {
                            return 101;
                        }
                        return 24;
                    }
                    break;
                case 58:
                    if (matchChar(58)) {
                        return 145;
                    }
                    return 104;
                case 59:
                    return 83;
                case 60:
                    if (matchChar(33)) {
                        if (matchChar(45)) {
                            if (matchChar(45)) {
                                this.tokenBeg = this.cursor - 4;
                                skipLine();
                                this.commentType = Token.CommentType.HTML;
                                return 162;
                            }
                            ungetCharIgnoreLineEnd(45);
                        }
                        ungetCharIgnoreLineEnd(33);
                    }
                    if (matchChar(60)) {
                        if (matchChar(61)) {
                            return 95;
                        }
                        return 18;
                    }
                    if (matchChar(61)) {
                        return 15;
                    }
                    return 14;
                case 61:
                    if (matchChar(61)) {
                        if (matchChar(61)) {
                            return 46;
                        }
                        return 12;
                    }
                    if (matchChar(62)) {
                        return 165;
                    }
                    return 91;
                case 62:
                    if (matchChar(62)) {
                        if (matchChar(62)) {
                            if (matchChar(61)) {
                                return 97;
                            }
                            return 20;
                        }
                        if (matchChar(61)) {
                            return 96;
                        }
                        return 19;
                    }
                    if (matchChar(61)) {
                        return 17;
                    }
                    return 16;
                case 63:
                    return 103;
                case 91:
                    return 84;
                case 93:
                    return 85;
                case 94:
                    if (matchChar(61)) {
                        return 93;
                    }
                    return 10;
                case 123:
                    return 86;
                case 124:
                    if (matchChar(124)) {
                        return 105;
                    }
                    if (matchChar(61)) {
                        return 92;
                    }
                    return 9;
                case 125:
                    return 87;
                case 126:
                    return 27;
            }
        }
    }

    private static boolean isAlpha(int c) {
        if (c <= 90) {
            return 65 <= c;
        }
        return 97 <= c && c <= 122;
    }

    static boolean isDigit(int c) {
        return 48 <= c && c <= 57;
    }

    static boolean isJSSpace(int c) {
        if (c <= 127) {
            return c == 32 || c == 9 || c == 12 || c == 11;
        }
        return c == 160 || c == BYTE_ORDER_MARK || Character.getType((char) c) == 12;
    }

    private static boolean isJSFormatChar(int c) {
        return c > 127 && Character.getType((char) c) == 16;
    }

    void readRegExp(int startToken) throws IOException {
        int start = this.tokenBeg;
        this.stringBufferTop = 0;
        if (startToken == 101) {
            addToString(61);
        } else {
            if (startToken != 24) {
                Kit.codeBug();
            }
            if (peekChar() == 42) {
                this.tokenEnd = this.cursor - 1;
                this.string = new String(this.stringBuffer, 0, this.stringBufferTop);
                this.parser.reportError("msg.unterminated.re.lit");
                return;
            }
        }
        boolean inCharSet = false;
        while (true) {
            int i = getChar();
            int c = i;
            if (i != 47 || inCharSet) {
                if (c == 10 || c == -1) {
                    ungetChar(c);
                    this.tokenEnd = this.cursor - 1;
                    this.string = new String(this.stringBuffer, 0, this.stringBufferTop);
                    this.parser.reportError("msg.unterminated.re.lit");
                    return;
                }
                if (c == 92) {
                    addToString(c);
                    c = getChar();
                    if (c == 10 || c == -1) {
                        ungetChar(c);
                        this.tokenEnd = this.cursor - 1;
                        this.string = new String(this.stringBuffer, 0, this.stringBufferTop);
                        this.parser.reportError("msg.unterminated.re.lit");
                        return;
                    }
                } else if (c == 91) {
                    inCharSet = true;
                } else if (c == 93) {
                    inCharSet = false;
                }
                addToString(c);
            } else {
                int reEnd = this.stringBufferTop;
                while (true) {
                    if (matchChar(103)) {
                        addToString(103);
                    } else if (matchChar(105)) {
                        addToString(105);
                    } else if (matchChar(109)) {
                        addToString(109);
                    } else if (!matchChar(121)) {
                        break;
                    } else {
                        addToString(121);
                    }
                }
                this.tokenEnd = start + this.stringBufferTop + 2;
                if (isAlpha(peekChar())) {
                    this.parser.reportError("msg.invalid.re.flag");
                }
                this.string = new String(this.stringBuffer, 0, reEnd);
                this.regExpFlags = new String(this.stringBuffer, reEnd, this.stringBufferTop - reEnd);
                return;
            }
        }
    }

    String readAndClearRegExpFlags() {
        String flags = this.regExpFlags;
        this.regExpFlags = null;
        return flags;
    }

    boolean isXMLAttribute() {
        return this.xmlIsAttribute;
    }

    int getFirstXMLToken() throws IOException {
        this.xmlOpenTagsCount = 0;
        this.xmlIsAttribute = false;
        this.xmlIsTagContent = false;
        if (!canUngetChar()) {
            return -1;
        }
        ungetChar(60);
        return getNextXMLToken();
    }

    int getNextXMLToken() throws IOException {
        this.tokenBeg = this.cursor;
        this.stringBufferTop = 0;
        int i = getChar();
        while (true) {
            int c = i;
            if (c != -1) {
                if (this.xmlIsTagContent) {
                    switch (c) {
                        case 9:
                        case 10:
                        case 13:
                        case 32:
                            addToString(c);
                            break;
                        case 34:
                        case 39:
                            addToString(c);
                            if (!readQuotedString(c)) {
                                return -1;
                            }
                            break;
                        case 47:
                            addToString(c);
                            if (peekChar() == 62) {
                                addToString(getChar());
                                this.xmlIsTagContent = false;
                                this.xmlOpenTagsCount--;
                            }
                            break;
                        case 61:
                            addToString(c);
                            this.xmlIsAttribute = true;
                            break;
                        case 62:
                            addToString(c);
                            this.xmlIsTagContent = false;
                            this.xmlIsAttribute = false;
                            break;
                        case 123:
                            ungetChar(c);
                            this.string = getStringFromBuffer();
                            return 146;
                        default:
                            addToString(c);
                            this.xmlIsAttribute = false;
                            break;
                    }
                    if (!this.xmlIsTagContent && this.xmlOpenTagsCount == 0) {
                        this.string = getStringFromBuffer();
                        return 149;
                    }
                } else {
                    switch (c) {
                        case 60:
                            addToString(c);
                            switch (peekChar()) {
                                case 33:
                                    addToString(getChar());
                                    switch (peekChar()) {
                                        case 45:
                                            addToString(getChar());
                                            int c2 = getChar();
                                            if (c2 == 45) {
                                                addToString(c2);
                                                if (!readXmlComment()) {
                                                    return -1;
                                                }
                                            } else {
                                                this.stringBufferTop = 0;
                                                this.string = null;
                                                this.parser.addError("msg.XML.bad.form");
                                                return -1;
                                            }
                                            break;
                                        case 91:
                                            addToString(getChar());
                                            if (getChar() == 67 && getChar() == 68 && getChar() == 65 && getChar() == 84 && getChar() == 65 && getChar() == 91) {
                                                addToString(67);
                                                addToString(68);
                                                addToString(65);
                                                addToString(84);
                                                addToString(65);
                                                addToString(91);
                                                if (!readCDATA()) {
                                                    return -1;
                                                }
                                            } else {
                                                this.stringBufferTop = 0;
                                                this.string = null;
                                                this.parser.addError("msg.XML.bad.form");
                                                return -1;
                                            }
                                            break;
                                        default:
                                            if (!readEntity()) {
                                                return -1;
                                            }
                                            break;
                                            break;
                                    }
                                    break;
                                case 47:
                                    addToString(getChar());
                                    if (this.xmlOpenTagsCount == 0) {
                                        this.stringBufferTop = 0;
                                        this.string = null;
                                        this.parser.addError("msg.XML.bad.form");
                                        return -1;
                                    }
                                    this.xmlIsTagContent = true;
                                    this.xmlOpenTagsCount--;
                                    break;
                                    break;
                                case 63:
                                    addToString(getChar());
                                    if (!readPI()) {
                                        return -1;
                                    }
                                    break;
                                    break;
                                default:
                                    this.xmlIsTagContent = true;
                                    this.xmlOpenTagsCount++;
                                    break;
                            }
                            break;
                        case 123:
                            ungetChar(c);
                            this.string = getStringFromBuffer();
                            return 146;
                        default:
                            addToString(c);
                            break;
                    }
                }
                i = getChar();
            } else {
                this.tokenEnd = this.cursor;
                this.stringBufferTop = 0;
                this.string = null;
                this.parser.addError("msg.XML.bad.form");
                return -1;
            }
        }
    }

    private boolean readQuotedString(int quote) throws IOException {
        int i = getChar();
        while (true) {
            int c = i;
            if (c != -1) {
                addToString(c);
                if (c == quote) {
                    return true;
                }
                i = getChar();
            } else {
                this.stringBufferTop = 0;
                this.string = null;
                this.parser.addError("msg.XML.bad.form");
                return false;
            }
        }
    }

    private boolean readXmlComment() throws IOException {
        int c = getChar();
        while (c != -1) {
            addToString(c);
            if (c == 45 && peekChar() == 45) {
                c = getChar();
                addToString(c);
                if (peekChar() == 62) {
                    int c2 = getChar();
                    addToString(c2);
                    return true;
                }
            } else {
                c = getChar();
            }
        }
        this.stringBufferTop = 0;
        this.string = null;
        this.parser.addError("msg.XML.bad.form");
        return false;
    }

    private boolean readCDATA() throws IOException {
        int c = getChar();
        while (c != -1) {
            addToString(c);
            if (c == 93 && peekChar() == 93) {
                c = getChar();
                addToString(c);
                if (peekChar() == 62) {
                    int c2 = getChar();
                    addToString(c2);
                    return true;
                }
            } else {
                c = getChar();
            }
        }
        this.stringBufferTop = 0;
        this.string = null;
        this.parser.addError("msg.XML.bad.form");
        return false;
    }

    private boolean readEntity() throws IOException {
        int declTags = 1;
        int i = getChar();
        while (true) {
            int c = i;
            if (c != -1) {
                addToString(c);
                switch (c) {
                    case 60:
                        declTags++;
                        break;
                    case 62:
                        declTags--;
                        if (declTags == 0) {
                            return true;
                        }
                        break;
                }
                i = getChar();
            } else {
                this.stringBufferTop = 0;
                this.string = null;
                this.parser.addError("msg.XML.bad.form");
                return false;
            }
        }
    }

    private boolean readPI() throws IOException {
        int i = getChar();
        while (true) {
            int c = i;
            if (c != -1) {
                addToString(c);
                if (c != 63 || peekChar() != 62) {
                    i = getChar();
                } else {
                    addToString(getChar());
                    return true;
                }
            } else {
                this.stringBufferTop = 0;
                this.string = null;
                this.parser.addError("msg.XML.bad.form");
                return false;
            }
        }
    }

    private String getStringFromBuffer() {
        this.tokenEnd = this.cursor;
        return new String(this.stringBuffer, 0, this.stringBufferTop);
    }

    private void addToString(int c) {
        int N = this.stringBufferTop;
        if (N == this.stringBuffer.length) {
            char[] tmp = new char[this.stringBuffer.length * 2];
            System.arraycopy(this.stringBuffer, 0, tmp, 0, N);
            this.stringBuffer = tmp;
        }
        this.stringBuffer[N] = (char) c;
        this.stringBufferTop = N + 1;
    }

    private boolean canUngetChar() {
        return this.ungetCursor == 0 || this.ungetBuffer[this.ungetCursor - 1] != 10;
    }

    private void ungetChar(int c) {
        if (this.ungetCursor != 0 && this.ungetBuffer[this.ungetCursor - 1] == 10) {
            Kit.codeBug();
        }
        int[] iArr = this.ungetBuffer;
        int i = this.ungetCursor;
        this.ungetCursor = i + 1;
        iArr[i] = c;
        this.cursor--;
    }

    private boolean matchChar(int test) throws IOException {
        int c = getCharIgnoreLineEnd();
        if (c == test) {
            this.tokenEnd = this.cursor;
            return true;
        }
        ungetCharIgnoreLineEnd(c);
        return false;
    }

    private int peekChar() throws IOException {
        int c = getChar();
        ungetChar(c);
        return c;
    }

    private int getChar() throws IOException {
        return getChar(true);
    }

    private int getChar(boolean skipFormattingChars) throws IOException {
        int c;
        if (this.ungetCursor != 0) {
            this.cursor++;
            int[] iArr = this.ungetBuffer;
            int i = this.ungetCursor - 1;
            this.ungetCursor = i;
            return iArr[i];
        }
        while (true) {
            if (this.sourceString != null) {
                if (this.sourceCursor == this.sourceEnd) {
                    this.hitEOF = true;
                    return -1;
                }
                this.cursor++;
                String str = this.sourceString;
                int i2 = this.sourceCursor;
                this.sourceCursor = i2 + 1;
                c = str.charAt(i2);
            } else {
                if (this.sourceCursor == this.sourceEnd && !fillSourceBuffer()) {
                    this.hitEOF = true;
                    return -1;
                }
                this.cursor++;
                char[] cArr = this.sourceBuffer;
                int i3 = this.sourceCursor;
                this.sourceCursor = i3 + 1;
                c = cArr[i3];
            }
            if (this.lineEndChar >= 0) {
                if (this.lineEndChar == 13 && c == 10) {
                    this.lineEndChar = 10;
                } else {
                    this.lineEndChar = -1;
                    this.lineStart = this.sourceCursor - 1;
                    this.lineno++;
                }
            }
            if (c <= 127) {
                if (c == 10 || c == 13) {
                    this.lineEndChar = c;
                    c = 10;
                }
            } else {
                if (c == BYTE_ORDER_MARK) {
                    return c;
                }
                if (!skipFormattingChars || !isJSFormatChar(c)) {
                    if (ScriptRuntime.isJSLineTerminator(c)) {
                        this.lineEndChar = c;
                        c = 10;
                    }
                }
            }
            return c;
        }
    }

    private int getCharIgnoreLineEnd() throws IOException {
        int c;
        if (this.ungetCursor != 0) {
            this.cursor++;
            int[] iArr = this.ungetBuffer;
            int i = this.ungetCursor - 1;
            this.ungetCursor = i;
            return iArr[i];
        }
        do {
            if (this.sourceString != null) {
                if (this.sourceCursor == this.sourceEnd) {
                    this.hitEOF = true;
                    return -1;
                }
                this.cursor++;
                String str = this.sourceString;
                int i2 = this.sourceCursor;
                this.sourceCursor = i2 + 1;
                c = str.charAt(i2);
            } else {
                if (this.sourceCursor == this.sourceEnd && !fillSourceBuffer()) {
                    this.hitEOF = true;
                    return -1;
                }
                this.cursor++;
                char[] cArr = this.sourceBuffer;
                int i3 = this.sourceCursor;
                this.sourceCursor = i3 + 1;
                c = cArr[i3];
            }
            if (c <= 127) {
                if (c == 10 || c == 13) {
                    this.lineEndChar = c;
                    c = 10;
                }
            } else if (c == BYTE_ORDER_MARK) {
                return c;
            }
            return c;
        } while (isJSFormatChar(c));
        if (ScriptRuntime.isJSLineTerminator(c)) {
            this.lineEndChar = c;
            c = 10;
        }
        return c;
    }

    private void ungetCharIgnoreLineEnd(int c) {
        int[] iArr = this.ungetBuffer;
        int i = this.ungetCursor;
        this.ungetCursor = i + 1;
        iArr[i] = c;
        this.cursor--;
    }

    private void skipLine() throws IOException {
        int c;
        do {
            c = getChar();
            if (c == -1) {
                break;
            }
        } while (c != 10);
        ungetChar(c);
        this.tokenEnd = this.cursor;
    }

    final int getOffset() {
        int n = this.sourceCursor - this.lineStart;
        if (this.lineEndChar >= 0) {
            n--;
        }
        return n;
    }

    private final int charAt(int index) {
        if (index < 0) {
            return -1;
        }
        if (this.sourceString != null) {
            if (index >= this.sourceEnd) {
                return -1;
            }
            return this.sourceString.charAt(index);
        }
        if (index >= this.sourceEnd) {
            int oldSourceCursor = this.sourceCursor;
            try {
                if (!fillSourceBuffer()) {
                    return -1;
                }
                index -= oldSourceCursor - this.sourceCursor;
            } catch (IOException e) {
                return -1;
            }
        }
        return this.sourceBuffer[index];
    }

    private final String substring(int beginIndex, int endIndex) {
        if (this.sourceString != null) {
            return this.sourceString.substring(beginIndex, endIndex);
        }
        int count = endIndex - beginIndex;
        return new String(this.sourceBuffer, beginIndex, count);
    }

    final String getLine() {
        int lineEnd;
        int lineEnd2 = this.sourceCursor;
        if (this.lineEndChar >= 0) {
            lineEnd = lineEnd2 - 1;
            if (this.lineEndChar == 10 && charAt(lineEnd - 1) == 13) {
                lineEnd--;
            }
        } else {
            int lineLength = lineEnd2 - this.lineStart;
            while (true) {
                int c = charAt(this.lineStart + lineLength);
                if (c == -1 || ScriptRuntime.isJSLineTerminator(c)) {
                    break;
                }
                lineLength++;
            }
            lineEnd = this.lineStart + lineLength;
        }
        return substring(this.lineStart, lineEnd);
    }

    final String getLine(int position, int[] linep) {
        if (!$assertionsDisabled && (position < 0 || position > this.cursor)) {
            throw new AssertionError();
        }
        if (!$assertionsDisabled && linep.length != 2) {
            throw new AssertionError();
        }
        int delta = (this.cursor + this.ungetCursor) - position;
        int cur = this.sourceCursor;
        if (delta > cur) {
            return null;
        }
        int end = 0;
        int lines = 0;
        while (delta > 0) {
            if (!$assertionsDisabled && cur <= 0) {
                throw new AssertionError();
            }
            int c = charAt(cur - 1);
            if (ScriptRuntime.isJSLineTerminator(c)) {
                if (c == 10 && charAt(cur - 2) == 13) {
                    delta--;
                    cur--;
                }
                lines++;
                end = cur - 1;
            }
            delta--;
            cur--;
        }
        int start = 0;
        int offset = 0;
        while (cur > 0) {
            if (!ScriptRuntime.isJSLineTerminator(charAt(cur - 1))) {
                cur--;
                offset++;
            } else {
                start = cur;
                break;
            }
        }
        linep[0] = (this.lineno - lines) + (this.lineEndChar >= 0 ? 1 : 0);
        linep[1] = offset;
        if (lines == 0) {
            return getLine();
        }
        return substring(start, end);
    }

    private boolean fillSourceBuffer() throws IOException {
        if (this.sourceString != null) {
            Kit.codeBug();
        }
        if (this.sourceEnd == this.sourceBuffer.length) {
            if (this.lineStart != 0 && !isMarkingComment()) {
                System.arraycopy(this.sourceBuffer, this.lineStart, this.sourceBuffer, 0, this.sourceEnd - this.lineStart);
                this.sourceEnd -= this.lineStart;
                this.sourceCursor -= this.lineStart;
                this.lineStart = 0;
            } else {
                char[] tmp = new char[this.sourceBuffer.length * 2];
                System.arraycopy(this.sourceBuffer, 0, tmp, 0, this.sourceEnd);
                this.sourceBuffer = tmp;
            }
        }
        int n = this.sourceReader.read(this.sourceBuffer, this.sourceEnd, this.sourceBuffer.length - this.sourceEnd);
        if (n < 0) {
            return false;
        }
        this.sourceEnd += n;
        return true;
    }

    public int getCursor() {
        return this.cursor;
    }

    public int getTokenBeg() {
        return this.tokenBeg;
    }

    public int getTokenEnd() {
        return this.tokenEnd;
    }

    public int getTokenLength() {
        return this.tokenEnd - this.tokenBeg;
    }

    public Token.CommentType getCommentType() {
        return this.commentType;
    }

    private void markCommentStart() {
        markCommentStart("");
    }

    private void markCommentStart(String prefix) {
        if (this.parser.compilerEnv.isRecordingComments() && this.sourceReader != null) {
            this.commentPrefix = prefix;
            this.commentCursor = this.sourceCursor - 1;
        }
    }

    private boolean isMarkingComment() {
        return this.commentCursor != -1;
    }

    final String getAndResetCurrentComment() {
        if (this.sourceString != null) {
            if (isMarkingComment()) {
                Kit.codeBug();
            }
            return this.sourceString.substring(this.tokenBeg, this.tokenEnd);
        }
        if (!isMarkingComment()) {
            Kit.codeBug();
        }
        StringBuilder comment = new StringBuilder(this.commentPrefix);
        comment.append(this.sourceBuffer, this.commentCursor, getTokenLength() - this.commentPrefix.length());
        this.commentCursor = -1;
        return comment.toString();
    }

    private static String convertLastCharToHex(String str) {
        int lastIndex = str.length() - 1;
        StringBuilder buf = new StringBuilder(str.substring(0, lastIndex));
        buf.append("\\u");
        String hexCode = Integer.toHexString(str.charAt(lastIndex));
        for (int i = 0; i < 4 - hexCode.length(); i++) {
            buf.append('0');
        }
        buf.append(hexCode);
        return buf.toString();
    }
}
