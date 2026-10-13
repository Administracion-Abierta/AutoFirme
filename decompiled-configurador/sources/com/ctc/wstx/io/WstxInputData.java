package com.ctc.wstx.io;

import com.ctc.wstx.util.XmlChars;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/io/WstxInputData.class */
public class WstxInputData {
    public static final char CHAR_NULL = 0;
    public static final char INT_NULL = 0;
    public static final char CHAR_SPACE = ' ';
    public static final char INT_SPACE = ' ';
    public static final int MAX_UNICODE_CHAR = 1114111;
    private static final int VALID_CHAR_COUNT = 256;
    private static final byte NAME_CHAR_INVALID_B = 0;
    private static final byte NAME_CHAR_ALL_VALID_B = 1;
    private static final byte NAME_CHAR_VALID_NONFIRST_B = -1;
    private static final byte[] sCharValidity = new byte[256];
    private static final int VALID_PUBID_CHAR_COUNT = 128;
    private static final byte[] sPubidValidity;
    private static final byte PUBID_CHAR_VALID_B = 1;
    protected char[] mInputBuffer;
    protected boolean mXml11 = false;
    protected int mInputPtr = 0;
    protected int mInputEnd = 0;
    protected long mCurrInputProcessed = 0;
    protected int mCurrInputRow = 1;
    protected int mCurrInputRowStart = 0;

    static {
        sCharValidity[95] = 1;
        for (int i = 0; i <= 25; i++) {
            sCharValidity[65 + i] = 1;
            sCharValidity[97 + i] = 1;
        }
        for (int i2 = 192; i2 < 256; i2++) {
            sCharValidity[i2] = 1;
        }
        sCharValidity[215] = 0;
        sCharValidity[247] = 0;
        sCharValidity[45] = -1;
        sCharValidity[46] = -1;
        sCharValidity[183] = -1;
        for (int i3 = 48; i3 <= 57; i3++) {
            sCharValidity[i3] = -1;
        }
        sPubidValidity = new byte[128];
        for (int i4 = 0; i4 <= 25; i4++) {
            sPubidValidity[65 + i4] = 1;
            sPubidValidity[97 + i4] = 1;
        }
        for (int i5 = 48; i5 <= 57; i5++) {
            sPubidValidity[i5] = 1;
        }
        sPubidValidity[10] = 1;
        sPubidValidity[13] = 1;
        sPubidValidity[32] = 1;
        sPubidValidity[45] = 1;
        sPubidValidity[39] = 1;
        sPubidValidity[40] = 1;
        sPubidValidity[41] = 1;
        sPubidValidity[43] = 1;
        sPubidValidity[44] = 1;
        sPubidValidity[46] = 1;
        sPubidValidity[47] = 1;
        sPubidValidity[58] = 1;
        sPubidValidity[61] = 1;
        sPubidValidity[63] = 1;
        sPubidValidity[59] = 1;
        sPubidValidity[33] = 1;
        sPubidValidity[42] = 1;
        sPubidValidity[35] = 1;
        sPubidValidity[64] = 1;
        sPubidValidity[36] = 1;
        sPubidValidity[95] = 1;
        sPubidValidity[37] = 1;
    }

    protected WstxInputData() {
    }

    public void copyBufferStateFrom(WstxInputData src) {
        this.mInputBuffer = src.mInputBuffer;
        this.mInputPtr = src.mInputPtr;
        this.mInputEnd = src.mInputEnd;
        this.mCurrInputProcessed = src.mCurrInputProcessed;
        this.mCurrInputRow = src.mCurrInputRow;
        this.mCurrInputRowStart = src.mCurrInputRowStart;
    }

    protected final boolean isNameStartChar(char c) {
        if (c > 'z') {
            return this.mXml11 ? XmlChars.is11NameStartChar(c) : XmlChars.is10NameStartChar(c);
        }
        if (c >= 'a') {
            return true;
        }
        if (c < 'A') {
            return false;
        }
        return c <= 'Z' || c == '_';
    }

    protected final boolean isNameChar(char c) {
        if (c > 'z') {
            return this.mXml11 ? XmlChars.is11NameChar(c) : XmlChars.is10NameChar(c);
        }
        if (c >= 'a') {
            return true;
        }
        if (c > 'Z') {
            return c == '_';
        }
        if (c >= 'A') {
            return true;
        }
        return (c >= '0' && c <= '9') || c == '.' || c == '-';
    }

    public static final boolean isNameStartChar(char c, boolean nsAware, boolean xml11) {
        if (c > 'z') {
            return xml11 ? XmlChars.is11NameStartChar(c) : XmlChars.is10NameStartChar(c);
        }
        if (c >= 'a') {
            return true;
        }
        if (c >= 'A') {
            return c <= 'Z' || c == '_';
        }
        if (c == ':' && !nsAware) {
            return true;
        }
        return false;
    }

    public static final boolean isNameChar(char c, boolean nsAware, boolean xml11) {
        if (c > 'z') {
            return xml11 ? XmlChars.is11NameChar(c) : XmlChars.is10NameChar(c);
        }
        if (c >= 'a') {
            return true;
        }
        if (c > 'Z') {
            return c == '_';
        }
        if (c >= 'A') {
            return true;
        }
        return (c >= '0' && c <= '9') || c == '.' || c == '-' || (c == ':' && !nsAware);
    }

    public static final int findIllegalNameChar(String name, boolean nsAware, boolean xml11) {
        int len = name.length();
        if (len < 1) {
            return -1;
        }
        char c = name.charAt(0);
        if (c <= 'z') {
            if (c < 'a') {
                if (c < 'A') {
                    if (c != ':' || nsAware) {
                        return 0;
                    }
                } else if (c > 'Z' && c != '_') {
                    return 0;
                }
            }
        } else if (xml11) {
            if (!XmlChars.is11NameStartChar(c)) {
                return 0;
            }
        } else if (!XmlChars.is10NameStartChar(c)) {
            return 0;
        }
        for (int i = 1; i < len; i++) {
            char c2 = name.charAt(i);
            if (c2 <= 'z') {
                if (c2 >= 'a') {
                    continue;
                } else if (c2 <= 'Z') {
                    if (c2 < 'A' && ((c2 < '0' || c2 > '9') && c2 != '.' && c2 != '-' && (c2 != ':' || nsAware))) {
                        return i;
                    }
                } else if (c2 != '_') {
                    return i;
                }
            } else if (xml11) {
                if (!XmlChars.is11NameChar(c2)) {
                    return i;
                }
            } else {
                if (!XmlChars.is10NameChar(c2)) {
                    return i;
                }
            }
        }
        return -1;
    }

    public static final int findIllegalNmtokenChar(String nmtoken, boolean nsAware, boolean xml11) {
        int len = nmtoken.length();
        for (int i = 1; i < len; i++) {
            char c = nmtoken.charAt(i);
            if (c <= 'z') {
                if (c >= 'a') {
                    continue;
                } else if (c <= 'Z') {
                    if (c < 'A' && ((c < '0' || c > '9') && c != '.' && c != '-' && (c != ':' || nsAware))) {
                        return i;
                    }
                } else if (c != '_') {
                    return i;
                }
            } else if (xml11) {
                if (!XmlChars.is11NameChar(c)) {
                    return i;
                }
            } else {
                if (!XmlChars.is10NameChar(c)) {
                    return i;
                }
            }
        }
        return -1;
    }

    public static final boolean isSpaceChar(char c) {
        return c <= ' ';
    }

    public static String getCharDesc(char c) {
        if (Character.isISOControl(c)) {
            return "(CTRL-CHAR, code " + ((int) c) + ")";
        }
        return c > 255 ? "'" + c + "' (code " + ((int) c) + " / 0x" + Integer.toHexString(c) + ")" : "'" + c + "' (code " + ((int) c) + ")";
    }
}
