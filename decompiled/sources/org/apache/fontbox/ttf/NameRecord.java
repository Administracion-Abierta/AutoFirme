package org.apache.fontbox.ttf;

import java.io.IOException;

/* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/ttf/NameRecord.class */
public class NameRecord {
    public static final int PLATFORM_UNICODE = 0;
    public static final int PLATFORM_MACINTOSH = 1;
    public static final int PLATFORM_ISO = 2;
    public static final int PLATFORM_WINDOWS = 3;
    public static final int ENCODING_UNICODE_1_0 = 0;
    public static final int ENCODING_UNICODE_1_1 = 1;
    public static final int ENCODING_UNICODE_2_0_BMP = 3;
    public static final int ENCODING_UNICODE_2_0_FULL = 4;

    @Deprecated
    public static final int LANGUGAE_UNICODE = 0;
    public static final int LANGUAGE_UNICODE = 0;
    public static final int ENCODING_WINDOWS_SYMBOL = 0;
    public static final int ENCODING_WINDOWS_UNICODE_BMP = 1;
    public static final int ENCODING_WINDOWS_UNICODE_UCS4 = 10;

    @Deprecated
    public static final int LANGUGAE_WINDOWS_EN_US = 1033;
    public static final int LANGUAGE_WINDOWS_EN_US = 1033;
    public static final int ENCODING_MACINTOSH_ROMAN = 0;

    @Deprecated
    public static final int LANGUGAE_MACINTOSH_ENGLISH = 0;
    public static final int LANGUAGE_MACINTOSH_ENGLISH = 0;
    public static final int NAME_COPYRIGHT = 0;
    public static final int NAME_FONT_FAMILY_NAME = 1;
    public static final int NAME_FONT_SUB_FAMILY_NAME = 2;
    public static final int NAME_UNIQUE_FONT_ID = 3;
    public static final int NAME_FULL_FONT_NAME = 4;
    public static final int NAME_VERSION = 5;
    public static final int NAME_POSTSCRIPT_NAME = 6;
    public static final int NAME_TRADEMARK = 7;
    private int platformId;
    private int platformEncodingId;
    private int languageId;
    private int nameId;
    private int stringLength;
    private int stringOffset;
    private String string;

    public int getStringLength() {
        return this.stringLength;
    }

    public void setStringLength(int stringLengthValue) {
        this.stringLength = stringLengthValue;
    }

    public int getStringOffset() {
        return this.stringOffset;
    }

    public void setStringOffset(int stringOffsetValue) {
        this.stringOffset = stringOffsetValue;
    }

    public int getLanguageId() {
        return this.languageId;
    }

    public void setLanguageId(int languageIdValue) {
        this.languageId = languageIdValue;
    }

    public int getNameId() {
        return this.nameId;
    }

    public void setNameId(int nameIdValue) {
        this.nameId = nameIdValue;
    }

    public int getPlatformEncodingId() {
        return this.platformEncodingId;
    }

    public void setPlatformEncodingId(int platformEncodingIdValue) {
        this.platformEncodingId = platformEncodingIdValue;
    }

    public int getPlatformId() {
        return this.platformId;
    }

    public void setPlatformId(int platformIdValue) {
        this.platformId = platformIdValue;
    }

    void initData(TrueTypeFont ttf, TTFDataStream data) throws IOException {
        this.platformId = data.readUnsignedShort();
        this.platformEncodingId = data.readUnsignedShort();
        this.languageId = data.readUnsignedShort();
        this.nameId = data.readUnsignedShort();
        this.stringLength = data.readUnsignedShort();
        this.stringOffset = data.readUnsignedShort();
    }

    public String toString() {
        return "platform=" + this.platformId + " pEncoding=" + this.platformEncodingId + " language=" + this.languageId + " name=" + this.nameId + " " + this.string;
    }

    public String getString() {
        return this.string;
    }

    public void setString(String stringValue) {
        this.string = stringValue;
    }
}
