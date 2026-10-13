package nu.xom;

import java.io.IOException;
import java.io.Writer;
import org.spongycastle.asn1.eac.EACTags;
import org.spongycastle.crypto.tls.CipherSuite;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:nu/xom/UnicodeWriter.class */
final class UnicodeWriter extends TextWriter {
    UnicodeWriter(Writer writer, String str) {
        super(writer, str);
    }

    @Override // nu.xom.TextWriter
    boolean needsEscaping(char c) {
        return false;
    }

    @Override // nu.xom.TextWriter
    void writeMarkup(String str) throws IOException {
        if (this.normalize) {
            str = normalize(str);
        }
        int unicodeLengthForMarkup = getUnicodeLengthForMarkup(str);
        if (unicodeLengthForMarkup < 0) {
            int length = str.length();
            for (int i = 0; i < length; i++) {
                writeMarkup(str.charAt(i));
            }
            return;
        }
        this.out.write(str);
        if (unicodeLengthForMarkup > 0) {
            this.column += unicodeLengthForMarkup;
            this.lastCharacterWasSpace = false;
            this.skipFollowingLinefeed = false;
            this.justBroke = false;
        }
    }

    @Override // nu.xom.TextWriter
    void writeName(String str) throws IOException {
        if (this.normalize) {
            str = normalize(str);
        }
        int unicodeLengthForName = getUnicodeLengthForName(str);
        this.out.write(str);
        this.column += unicodeLengthForName;
        this.lastCharacterWasSpace = false;
        this.skipFollowingLinefeed = false;
        this.justBroke = false;
    }

    private static int getUnicodeLengthForMarkup(String str) {
        int i = 0;
        int length = str.length();
        for (int i2 = 0; i2 < length; i2++) {
            char cCharAt = str.charAt(i2);
            if (cCharAt <= ' ') {
                return -1;
            }
            if (cCharAt < 55296 || cCharAt > 56319) {
                i++;
            }
        }
        return i;
    }

    private static int getUnicodeLengthForName(String str) {
        int i = 0;
        int length = str.length();
        for (int i2 = 0; i2 < length; i2++) {
            char cCharAt = str.charAt(i2);
            if (cCharAt < 55296 || cCharAt > 56319) {
                i++;
            }
        }
        return i;
    }

    @Override // nu.xom.TextWriter
    void writeAttributeValue(String str) throws IOException {
        if (this.normalize) {
            str = normalize(str);
        }
        int unicodeLengthForAttributeValue = getUnicodeLengthForAttributeValue(str);
        if (unicodeLengthForAttributeValue < 0) {
            int length = str.length();
            for (int i = 0; i < length; i++) {
                writeAttributeValue(str.charAt(i));
            }
            return;
        }
        this.out.write(str);
        if (unicodeLengthForAttributeValue > 0) {
            this.column += unicodeLengthForAttributeValue;
            this.lastCharacterWasSpace = false;
            this.skipFollowingLinefeed = false;
            this.justBroke = false;
        }
    }

    private static int getUnicodeLengthForAttributeValue(String str) {
        int i = 0;
        int length = str.length();
        for (int i2 = 0; i2 < length; i2++) {
            char cCharAt = str.charAt(i2);
            switch (cCharAt) {
                case '\t':
                    return -1;
                case '\n':
                    return -1;
                case 11:
                case '\f':
                    throw new XMLException("Bad character snuck into document");
                case '\r':
                    return -1;
                case 14:
                case 15:
                case 16:
                case 17:
                case 18:
                case 19:
                case 20:
                case 21:
                case 22:
                case 23:
                case 24:
                case 25:
                case 26:
                case 27:
                case 28:
                case 29:
                case 30:
                case 31:
                    throw new XMLException("Bad character snuck into document");
                case ' ':
                    return -1;
                case '!':
                    i++;
                    break;
                case '\"':
                    return -1;
                case '#':
                    i++;
                    break;
                case EACTags.APPLICATION_EXPIRATION_DATE /* 36 */:
                    i++;
                    break;
                case EACTags.APPLICATION_EFFECTIVE_DATE /* 37 */:
                    i++;
                    break;
                case EACTags.CARD_EFFECTIVE_DATE /* 38 */:
                    return -1;
                case EACTags.INTERCHANGE_CONTROL /* 39 */:
                    i++;
                    break;
                case '(':
                    i++;
                    break;
                case ')':
                    i++;
                    break;
                case '*':
                    i++;
                    break;
                case '+':
                    i++;
                    break;
                case ',':
                    i++;
                    break;
                case '-':
                    i++;
                    break;
                case '.':
                    i++;
                    break;
                case '/':
                    i++;
                    break;
                case '0':
                    i++;
                    break;
                case '1':
                    i++;
                    break;
                case '2':
                    i++;
                    break;
                case '3':
                    i++;
                    break;
                case '4':
                    i++;
                    break;
                case '5':
                    i++;
                    break;
                case '6':
                    i++;
                    break;
                case '7':
                    i++;
                    break;
                case '8':
                    i++;
                    break;
                case '9':
                    i++;
                    break;
                case ':':
                    i++;
                    break;
                case ';':
                    i++;
                    break;
                case '<':
                    return -1;
                case '=':
                    i++;
                    break;
                case CipherSuite.TLS_DH_DSS_WITH_AES_128_CBC_SHA256 /* 62 */:
                    return -1;
                default:
                    if (cCharAt < 55296 || cCharAt > 56319) {
                        i++;
                    }
                    break;
            }
        }
        return i;
    }

    @Override // nu.xom.TextWriter
    void writePCDATA(String str) throws IOException {
        if (this.normalize) {
            str = normalize(str);
        }
        int unicodeLengthForPCDATA = getUnicodeLengthForPCDATA(str);
        if (unicodeLengthForPCDATA < 0) {
            int length = str.length();
            for (int i = 0; i < length; i++) {
                writePCDATA(str.charAt(i));
            }
            return;
        }
        this.out.write(str);
        if (unicodeLengthForPCDATA > 0) {
            this.column += unicodeLengthForPCDATA;
            this.lastCharacterWasSpace = false;
            this.skipFollowingLinefeed = false;
            this.justBroke = false;
        }
    }

    private static int getUnicodeLengthForPCDATA(String str) {
        int i = 0;
        int length = str.length();
        for (int i2 = 0; i2 < length; i2++) {
            char cCharAt = str.charAt(i2);
            switch (cCharAt) {
                case '\t':
                    return -1;
                case '\n':
                    return -1;
                case 11:
                case '\f':
                    throw new XMLException("Bad character snuck into document");
                case '\r':
                    return -1;
                case 14:
                case 15:
                case 16:
                case 17:
                case 18:
                case 19:
                case 20:
                case 21:
                case 22:
                case 23:
                case 24:
                case 25:
                case 26:
                case 27:
                case 28:
                case 29:
                case 30:
                case 31:
                    throw new XMLException("Bad character snuck into document");
                case ' ':
                    return -1;
                case '!':
                    i++;
                    break;
                case '\"':
                    i++;
                    break;
                case '#':
                    i++;
                    break;
                case EACTags.APPLICATION_EXPIRATION_DATE /* 36 */:
                    i++;
                    break;
                case EACTags.APPLICATION_EFFECTIVE_DATE /* 37 */:
                    i++;
                    break;
                case EACTags.CARD_EFFECTIVE_DATE /* 38 */:
                    return -1;
                case EACTags.INTERCHANGE_CONTROL /* 39 */:
                    i++;
                    break;
                case '(':
                    i++;
                    break;
                case ')':
                    i++;
                    break;
                case '*':
                    i++;
                    break;
                case '+':
                    i++;
                    break;
                case ',':
                    i++;
                    break;
                case '-':
                    i++;
                    break;
                case '.':
                    i++;
                    break;
                case '/':
                    i++;
                    break;
                case '0':
                    i++;
                    break;
                case '1':
                    i++;
                    break;
                case '2':
                    i++;
                    break;
                case '3':
                    i++;
                    break;
                case '4':
                    i++;
                    break;
                case '5':
                    i++;
                    break;
                case '6':
                    i++;
                    break;
                case '7':
                    i++;
                    break;
                case '8':
                    i++;
                    break;
                case '9':
                    i++;
                    break;
                case ':':
                    i++;
                    break;
                case ';':
                    i++;
                    break;
                case '<':
                    return -1;
                case '=':
                    i++;
                    break;
                case CipherSuite.TLS_DH_DSS_WITH_AES_128_CBC_SHA256 /* 62 */:
                    return -1;
                default:
                    if (cCharAt < 55296 || cCharAt > 56319) {
                        i++;
                    }
                    break;
            }
        }
        return i;
    }
}
