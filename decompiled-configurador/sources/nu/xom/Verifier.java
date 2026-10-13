package nu.xom;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.StringTokenizer;
import org.spongycastle.asn1.eac.EACTags;
import org.spongycastle.crypto.tls.AlertDescription;
import org.spongycastle.crypto.tls.CipherSuite;
import org.spongycastle.crypto.tls.ExtensionType;
import org.spongycastle.pqc.math.linearalgebra.Matrix;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:nu/xom/Verifier.class */
final class Verifier {
    private static final byte XML_CHARACTER = 1;
    private static final byte NAME_CHARACTER = 2;
    private static final byte NAME_START_CHARACTER = 4;
    private static final byte NCNAME_CHARACTER = 8;
    private static byte[] flags = null;
    private static boolean[] C0Table;
    private static URICache cache;
    private static XMLReader parser;
    static /* synthetic */ Class class$nu$xom$Verifier;

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:nu/xom/Verifier$EmptyReader.class */
    private static class EmptyReader extends Reader {
        private EmptyReader() {
        }

        @Override // java.io.Reader
        public int read(char[] cArr, int i, int i2) throws IOException {
            return -1;
        }

        @Override // java.io.Reader, java.io.Closeable, java.lang.AutoCloseable
        public void close() {
        }
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:nu/xom/Verifier$URICache.class */
    private static final class URICache {
        private static final int LOAD = 6;
        private String[] cache;
        private int position;

        private URICache() {
            this.cache = new String[6];
            this.position = 0;
        }

        synchronized boolean contains(String str) {
            for (int i = 0; i < 6; i++) {
                if (str == this.cache[i]) {
                    return true;
                }
            }
            return false;
        }

        synchronized void put(String str) {
            this.cache[this.position] = str;
            this.position++;
            if (this.position == 6) {
                this.position = 0;
            }
        }
    }

    private Verifier() {
    }

    private static void loadFlags(ClassLoader classLoader) {
        DataInputStream dataInputStream = null;
        try {
            try {
                InputStream resourceAsStream = classLoader.getResourceAsStream("nu/xom/characters.dat");
                if (resourceAsStream == null) {
                    throw new RuntimeException("Broken XOM installation: could not load nu/xom/characters.dat");
                }
                DataInputStream dataInputStream2 = new DataInputStream(resourceAsStream);
                flags = new byte[65536];
                dataInputStream2.readFully(flags);
                if (dataInputStream2 != null) {
                    try {
                        dataInputStream2.close();
                    } catch (IOException e) {
                    }
                }
            } catch (IOException e2) {
                throw new RuntimeException("Broken XOM installation: could not load nu/xom/characters.dat");
            }
        } catch (Throwable th) {
            if (0 != 0) {
                try {
                    dataInputStream.close();
                } catch (IOException e3) {
                    throw th;
                }
            }
            throw th;
        }
    }

    static void checkNCName(String str) {
        if (str == null) {
            throwIllegalNameException(str, "NCNames cannot be null");
        }
        int length = str.length();
        if (length == 0) {
            throwIllegalNameException(str, "NCNames cannot be empty");
        }
        char cCharAt = str.charAt(0);
        if ((flags[cCharAt] & 4) == 0) {
            throwIllegalNameException(str, new StringBuffer().append("NCNames cannot start with the character ").append(Integer.toHexString(cCharAt)).toString());
        }
        for (int i = 1; i < length; i++) {
            char cCharAt2 = str.charAt(i);
            if ((flags[cCharAt2] & 8) == 0) {
                if (cCharAt2 == ':') {
                    throwIllegalNameException(str, "NCNames cannot contain colons");
                } else {
                    throwIllegalNameException(str, new StringBuffer().append("0x").append(Integer.toHexString(cCharAt2)).append(" is not a legal NCName character").toString());
                }
            }
        }
    }

    private static void throwIllegalNameException(String str, String str2) {
        IllegalNameException illegalNameException = new IllegalNameException(str2);
        illegalNameException.setData(str);
        throw illegalNameException;
    }

    private static void throwIllegalCharacterDataException(String str, String str2) {
        IllegalCharacterDataException illegalCharacterDataException = new IllegalCharacterDataException(str2);
        illegalCharacterDataException.setData(str);
        throw illegalCharacterDataException;
    }

    private static void throwMalformedURIException(String str, String str2) {
        MalformedURIException malformedURIException = new MalformedURIException(str2);
        malformedURIException.setData(str);
        throw malformedURIException;
    }

    static void checkPCDATA(String str) {
        if (str == null) {
            throw new IllegalCharacterDataException("Null text");
        }
        char[] charArray = str.toCharArray();
        int i = 0;
        int length = charArray.length;
        while (i < length) {
            char c = charArray[i];
            if (c >= 55296 && c <= 56319) {
                try {
                    char c2 = charArray[i + 1];
                    if (c2 < 56320 || c2 > 57343) {
                        IllegalCharacterDataException illegalCharacterDataException = new IllegalCharacterDataException("Bad surrogate pair");
                        illegalCharacterDataException.setData(str);
                        throw illegalCharacterDataException;
                    }
                    i++;
                } catch (ArrayIndexOutOfBoundsException e) {
                    IllegalCharacterDataException illegalCharacterDataException2 = new IllegalCharacterDataException("Bad Surrogate Pair", e);
                    illegalCharacterDataException2.setData(str);
                    throw illegalCharacterDataException2;
                }
            } else if ((flags[c] & 1) == 0) {
                throwIllegalCharacterDataException(str, new StringBuffer().append("0x").append(Integer.toHexString(c)).append(" is not allowed in XML content").toString());
            }
            i++;
        }
    }

    static void checkURIReference(String str) {
        if (str == null || str.length() == 0) {
            return;
        }
        URIUtil.ParsedURI parsedURI = new URIUtil.ParsedURI(str);
        try {
            if (parsedURI.scheme != null) {
                checkScheme(parsedURI.scheme);
            }
            if (parsedURI.authority != null) {
                checkAuthority(parsedURI.authority);
            }
            checkPath(parsedURI.path);
            if (parsedURI.fragment != null) {
                checkFragment(parsedURI.fragment);
            }
            if (parsedURI.query != null) {
                checkQuery(parsedURI.query);
            }
        } catch (MalformedURIException e) {
            e.setData(str);
            throw e;
        }
    }

    private static void checkQuery(String str) {
        int length = str.length();
        int i = 0;
        while (i < length) {
            char cCharAt = str.charAt(i);
            if (cCharAt == '%') {
                try {
                    if (!isHexDigit(str.charAt(i + 1)) || !isHexDigit(str.charAt(i + 2))) {
                        throwMalformedURIException(str, "Bad percent escape sequence");
                    }
                } catch (StringIndexOutOfBoundsException e) {
                    throwMalformedURIException(str, "Bad percent escape sequence");
                }
                i += 2;
            } else if (!isQueryCharacter(cCharAt)) {
                throw new MalformedURIException(new StringBuffer().append("Illegal query character ").append(cCharAt).toString());
            }
            i++;
        }
    }

    private static boolean isQueryCharacter(char c) {
        switch (c) {
            case '!':
                return true;
            case '\"':
                return false;
            case '#':
                return false;
            case EACTags.APPLICATION_EXPIRATION_DATE /* 36 */:
                return true;
            case EACTags.APPLICATION_EFFECTIVE_DATE /* 37 */:
                return false;
            case EACTags.CARD_EFFECTIVE_DATE /* 38 */:
                return true;
            case EACTags.INTERCHANGE_CONTROL /* 39 */:
                return true;
            case '(':
                return true;
            case ')':
                return true;
            case '*':
                return true;
            case '+':
                return true;
            case ',':
                return true;
            case '-':
                return true;
            case '.':
                return true;
            case '/':
                return true;
            case '0':
                return true;
            case '1':
                return true;
            case '2':
                return true;
            case '3':
                return true;
            case '4':
                return true;
            case '5':
                return true;
            case '6':
                return true;
            case '7':
                return true;
            case '8':
                return true;
            case '9':
                return true;
            case ':':
                return true;
            case ';':
                return true;
            case '<':
                return false;
            case '=':
                return true;
            case CipherSuite.TLS_DH_DSS_WITH_AES_128_CBC_SHA256 /* 62 */:
                return false;
            case '?':
                return true;
            case '@':
                return true;
            case 'A':
                return true;
            case 'B':
                return true;
            case 'C':
                return true;
            case 'D':
                return true;
            case 'E':
                return true;
            case 'F':
                return true;
            case 'G':
                return true;
            case 'H':
                return true;
            case 'I':
                return true;
            case EACTags.CERTIFICATION_AUTHORITY_PUBLIC_KEY /* 74 */:
                return true;
            case EACTags.DEPRECATED /* 75 */:
                return true;
            case 'L':
                return true;
            case EACTags.INTEGRATED_CIRCUIT_MANUFACTURER_ID /* 77 */:
                return true;
            case 'N':
                return true;
            case 'O':
                return true;
            case 'P':
                return true;
            case EACTags.ANSWER_TO_RESET /* 81 */:
                return true;
            case 'R':
                return true;
            case 'S':
                return true;
            case 'T':
                return true;
            case Matrix.MATRIX_TYPE_RANDOM_UT /* 85 */:
                return true;
            case AlertDescription.inappropriate_fallback /* 86 */:
                return true;
            case 'W':
                return true;
            case 'X':
                return true;
            case 'Y':
                return true;
            case 'Z':
                return true;
            case '[':
                return false;
            case '\\':
                return false;
            case ']':
                return false;
            case '^':
                return false;
            case '_':
                return true;
            case '`':
                return false;
            case 'a':
                return true;
            case 'b':
                return true;
            case 'c':
                return true;
            case AlertDescription.no_renegotiation /* 100 */:
                return true;
            case ExtensionType.negotiated_ff_dhe_groups /* 101 */:
                return true;
            case 'f':
                return true;
            case 'g':
                return true;
            case 'h':
                return true;
            case CipherSuite.TLS_DH_RSA_WITH_AES_256_CBC_SHA256 /* 105 */:
                return true;
            case CipherSuite.TLS_DHE_DSS_WITH_AES_256_CBC_SHA256 /* 106 */:
                return true;
            case CipherSuite.TLS_DHE_RSA_WITH_AES_256_CBC_SHA256 /* 107 */:
                return true;
            case CipherSuite.TLS_DH_anon_WITH_AES_128_CBC_SHA256 /* 108 */:
                return true;
            case CipherSuite.TLS_DH_anon_WITH_AES_256_CBC_SHA256 /* 109 */:
                return true;
            case AlertDescription.unsupported_extension /* 110 */:
                return true;
            case AlertDescription.certificate_unobtainable /* 111 */:
                return true;
            case AlertDescription.unrecognized_name /* 112 */:
                return true;
            case AlertDescription.bad_certificate_status_response /* 113 */:
                return true;
            case AlertDescription.bad_certificate_hash_value /* 114 */:
                return true;
            case AlertDescription.unknown_psk_identity /* 115 */:
                return true;
            case 't':
                return true;
            case 'u':
                return true;
            case 'v':
                return true;
            case 'w':
                return true;
            case 'x':
                return true;
            case 'y':
                return true;
            case 'z':
                return true;
            case '{':
                return false;
            case '|':
                return false;
            case '}':
                return false;
            case '~':
                return true;
            default:
                return false;
        }
    }

    private static void checkFragment(String str) {
        checkQuery(str);
    }

    private static void checkPath(String str) {
        int length = str.length();
        char[] charArray = str.toCharArray();
        int i = 0;
        while (i < length) {
            char c = charArray[i];
            if (c == '/') {
                if (i < length - 1 && charArray[i + 1] == '/') {
                    throwMalformedURIException(str, "Double slash (//) in path");
                }
            } else if (c == '%') {
                try {
                    if (!isHexDigit(charArray[i + 1]) || !isHexDigit(charArray[i + 2])) {
                        throwMalformedURIException(str, "Bad percent escape sequence");
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    throwMalformedURIException(str, "Bad percent escape sequence");
                }
                i += 2;
            } else if (!isPathCharacter(c)) {
                throwMalformedURIException(str, new StringBuffer().append("Illegal path character ").append(c).toString());
            }
            i++;
        }
    }

    private static void checkAuthority(String str) {
        int iIndexOf;
        String strSubstring;
        String strSubstring2 = null;
        String strSubstring3 = null;
        int iIndexOf2 = str.indexOf(64);
        if (iIndexOf2 != -1) {
            strSubstring2 = str.substring(0, iIndexOf2);
            str = str.substring(iIndexOf2 + 1);
        }
        if (str.startsWith("[")) {
            iIndexOf = str.indexOf("]:");
            if (iIndexOf != -1) {
                iIndexOf++;
            }
        } else {
            iIndexOf = str.indexOf(58);
        }
        if (iIndexOf != -1) {
            strSubstring = str.substring(0, iIndexOf);
            strSubstring3 = str.substring(iIndexOf + 1);
        } else {
            strSubstring = str;
        }
        if (strSubstring2 != null) {
            checkUserInfo(strSubstring2);
        }
        if (strSubstring3 != null) {
            checkPort(strSubstring3);
        }
        checkHost(strSubstring);
    }

    private static void checkHost(String str) {
        int length = str.length();
        if (length == 0) {
            return;
        }
        char[] charArray = str.toCharArray();
        if (charArray[0] == '[') {
            if (charArray[length - 1] != ']') {
                throw new MalformedURIException("Missing closing ]");
            }
            checkIP6Address(str.substring(1, length - 1));
        } else {
            if (length > 255) {
                throw new MalformedURIException(new StringBuffer().append("Host name too long: ").append(str).toString());
            }
            int i = 0;
            while (i < length) {
                char c = charArray[i];
                if (c == '%') {
                    try {
                        if (!isHexDigit(charArray[i + 1]) || !isHexDigit(charArray[i + 2])) {
                            throwMalformedURIException(str, "Bad percent escape sequence");
                        }
                    } catch (ArrayIndexOutOfBoundsException e) {
                        throwMalformedURIException(str, "Bad percent escape sequence");
                    }
                    i += 2;
                } else if (!isRegNameCharacter(c)) {
                    throwMalformedURIException(str, new StringBuffer().append("Illegal host character ").append(c).toString());
                }
                i++;
            }
        }
    }

    private static boolean isRegNameCharacter(char c) {
        switch (c) {
            case '!':
                return true;
            case '\"':
                return false;
            case '#':
                return false;
            case EACTags.APPLICATION_EXPIRATION_DATE /* 36 */:
                return true;
            case EACTags.APPLICATION_EFFECTIVE_DATE /* 37 */:
                return false;
            case EACTags.CARD_EFFECTIVE_DATE /* 38 */:
                return true;
            case EACTags.INTERCHANGE_CONTROL /* 39 */:
                return true;
            case '(':
                return true;
            case ')':
                return true;
            case '*':
                return true;
            case '+':
                return true;
            case ',':
                return true;
            case '-':
                return true;
            case '.':
                return true;
            case '/':
                return false;
            case '0':
                return true;
            case '1':
                return true;
            case '2':
                return true;
            case '3':
                return true;
            case '4':
                return true;
            case '5':
                return true;
            case '6':
                return true;
            case '7':
                return true;
            case '8':
                return true;
            case '9':
                return true;
            case ':':
                return false;
            case ';':
                return true;
            case '<':
                return false;
            case '=':
                return true;
            case CipherSuite.TLS_DH_DSS_WITH_AES_128_CBC_SHA256 /* 62 */:
                return false;
            case '?':
                return false;
            case '@':
                return false;
            case 'A':
                return true;
            case 'B':
                return true;
            case 'C':
                return true;
            case 'D':
                return true;
            case 'E':
                return true;
            case 'F':
                return true;
            case 'G':
                return true;
            case 'H':
                return true;
            case 'I':
                return true;
            case EACTags.CERTIFICATION_AUTHORITY_PUBLIC_KEY /* 74 */:
                return true;
            case EACTags.DEPRECATED /* 75 */:
                return true;
            case 'L':
                return true;
            case EACTags.INTEGRATED_CIRCUIT_MANUFACTURER_ID /* 77 */:
                return true;
            case 'N':
                return true;
            case 'O':
                return true;
            case 'P':
                return true;
            case EACTags.ANSWER_TO_RESET /* 81 */:
                return true;
            case 'R':
                return true;
            case 'S':
                return true;
            case 'T':
                return true;
            case Matrix.MATRIX_TYPE_RANDOM_UT /* 85 */:
                return true;
            case AlertDescription.inappropriate_fallback /* 86 */:
                return true;
            case 'W':
                return true;
            case 'X':
                return true;
            case 'Y':
                return true;
            case 'Z':
                return true;
            case '[':
                return false;
            case '\\':
                return false;
            case ']':
                return false;
            case '^':
                return false;
            case '_':
                return true;
            case '`':
                return false;
            case 'a':
                return true;
            case 'b':
                return true;
            case 'c':
                return true;
            case AlertDescription.no_renegotiation /* 100 */:
                return true;
            case ExtensionType.negotiated_ff_dhe_groups /* 101 */:
                return true;
            case 'f':
                return true;
            case 'g':
                return true;
            case 'h':
                return true;
            case CipherSuite.TLS_DH_RSA_WITH_AES_256_CBC_SHA256 /* 105 */:
                return true;
            case CipherSuite.TLS_DHE_DSS_WITH_AES_256_CBC_SHA256 /* 106 */:
                return true;
            case CipherSuite.TLS_DHE_RSA_WITH_AES_256_CBC_SHA256 /* 107 */:
                return true;
            case CipherSuite.TLS_DH_anon_WITH_AES_128_CBC_SHA256 /* 108 */:
                return true;
            case CipherSuite.TLS_DH_anon_WITH_AES_256_CBC_SHA256 /* 109 */:
                return true;
            case AlertDescription.unsupported_extension /* 110 */:
                return true;
            case AlertDescription.certificate_unobtainable /* 111 */:
                return true;
            case AlertDescription.unrecognized_name /* 112 */:
                return true;
            case AlertDescription.bad_certificate_status_response /* 113 */:
                return true;
            case AlertDescription.bad_certificate_hash_value /* 114 */:
                return true;
            case AlertDescription.unknown_psk_identity /* 115 */:
                return true;
            case 't':
                return true;
            case 'u':
                return true;
            case 'v':
                return true;
            case 'w':
                return true;
            case 'x':
                return true;
            case 'y':
                return true;
            case 'z':
                return true;
            case '{':
                return false;
            case '|':
                return false;
            case '}':
                return false;
            case '~':
                return true;
            default:
                return false;
        }
    }

    private static void checkPort(String str) {
        for (int length = str.length() - 1; length >= 0; length--) {
            char cCharAt = str.charAt(length);
            if (cCharAt < '0' || cCharAt > '9') {
                throw new MalformedURIException(new StringBuffer().append("Bad port: ").append(str).toString());
            }
        }
    }

    private static void checkUserInfo(String str) {
        int length = str.length();
        int i = 0;
        while (i < length) {
            char cCharAt = str.charAt(i);
            if (cCharAt == '%') {
                try {
                    if (!isHexDigit(str.charAt(i + 1)) || !isHexDigit(str.charAt(i + 2))) {
                        throwMalformedURIException(str, "Bad percent escape sequence");
                    }
                } catch (StringIndexOutOfBoundsException e) {
                    throwMalformedURIException(str, "Bad percent escape sequence");
                }
                i += 2;
            } else if (!isUserInfoCharacter(cCharAt)) {
                throw new MalformedURIException(new StringBuffer().append("Bad user info: ").append(str).toString());
            }
            i++;
        }
    }

    private static void checkScheme(String str) {
        if ("http".equals(str)) {
            return;
        }
        if (str.length() == 0) {
            throw new MalformedURIException("URIs cannot begin with a colon");
        }
        char cCharAt = str.charAt(0);
        if (!isAlpha(cCharAt)) {
            throw new MalformedURIException(new StringBuffer().append("Illegal initial scheme character ").append(cCharAt).toString());
        }
        for (int length = str.length() - 1; length >= 1; length--) {
            char cCharAt2 = str.charAt(length);
            if (!isSchemeCharacter(cCharAt2)) {
                throw new MalformedURIException(new StringBuffer().append("Illegal scheme character ").append(cCharAt2).toString());
            }
        }
    }

    private static void checkIP6Address(String str) {
        StringTokenizer stringTokenizer = new StringTokenizer(str, ":", true);
        int iCountTokens = stringTokenizer.countTokens();
        if (iCountTokens > 15 || iCountTokens < 2) {
            throw new MalformedURIException(new StringBuffer().append("Illegal IP6 host address: ").append(str).toString());
        }
        for (int i = 0; i < iCountTokens; i++) {
            String strNextToken = stringTokenizer.nextToken();
            if (!":".equals(strNextToken)) {
                try {
                    if (Integer.parseInt(strNextToken, 16) < 0) {
                        throw new MalformedURIException(new StringBuffer().append("Illegal IP6 host address: ").append(str).toString());
                    }
                } catch (NumberFormatException e) {
                    if (i == iCountTokens - 1) {
                        checkIP4Address(strNextToken, str);
                    } else {
                        throwMalformedURIException(str, new StringBuffer().append("Illegal IP6 host address: ").append(str).toString());
                    }
                }
            }
        }
        if (str.indexOf("::") != str.lastIndexOf("::")) {
            throw new MalformedURIException(new StringBuffer().append("Illegal IP6 host address: ").append(str).toString());
        }
    }

    private static void checkIP4Address(String str, String str2) {
        StringTokenizer stringTokenizer = new StringTokenizer(str, ".");
        if (stringTokenizer.countTokens() != 4) {
            throw new MalformedURIException(new StringBuffer().append("Illegal IP6 host address: ").append(str2).toString());
        }
        for (int i = 0; i < 4; i++) {
            try {
                int i2 = Integer.parseInt(stringTokenizer.nextToken());
                if (i2 > 255 || i2 < 0) {
                    throw new MalformedURIException(new StringBuffer().append("Illegal IP6 host address: ").append(str2).toString());
                }
            } catch (NumberFormatException e) {
                throw new MalformedURIException(new StringBuffer().append("Illegal IP6 host address: ").append(str2).toString());
            }
        }
    }

    static void checkXMLName(String str) {
        if (str == null) {
            throwIllegalNameException(str, "XML names cannot be null");
        }
        int length = str.length();
        if (length == 0) {
            throwIllegalNameException(str, "XML names cannot be empty");
        }
        char cCharAt = str.charAt(0);
        if ((flags[cCharAt] & 4) == 0) {
            throwIllegalNameException(str, new StringBuffer().append("XML names cannot start with the character ").append(Integer.toHexString(cCharAt)).toString());
        }
        for (int i = 1; i < length; i++) {
            char cCharAt2 = str.charAt(i);
            if ((flags[cCharAt2] & 2) == 0) {
                throwIllegalNameException(str, new StringBuffer().append("0x").append(Integer.toHexString(cCharAt2)).append(" is not a legal name character").toString());
            }
        }
    }

    static boolean isXMLSpaceCharacter(char c) {
        if (c > ' ') {
            return false;
        }
        return C0Table[c];
    }

    private static boolean isHexDigit(char c) {
        switch (c) {
            case '0':
                return true;
            case '1':
                return true;
            case '2':
                return true;
            case '3':
                return true;
            case '4':
                return true;
            case '5':
                return true;
            case '6':
                return true;
            case '7':
                return true;
            case '8':
                return true;
            case '9':
                return true;
            case ':':
                return false;
            case ';':
                return false;
            case '<':
                return false;
            case '=':
                return false;
            case CipherSuite.TLS_DH_DSS_WITH_AES_128_CBC_SHA256 /* 62 */:
                return false;
            case '?':
                return false;
            case '@':
                return false;
            case 'A':
                return true;
            case 'B':
                return true;
            case 'C':
                return true;
            case 'D':
                return true;
            case 'E':
                return true;
            case 'F':
                return true;
            case 'G':
                return false;
            case 'H':
                return false;
            case 'I':
                return false;
            case EACTags.CERTIFICATION_AUTHORITY_PUBLIC_KEY /* 74 */:
                return false;
            case EACTags.DEPRECATED /* 75 */:
                return false;
            case 'L':
                return false;
            case EACTags.INTEGRATED_CIRCUIT_MANUFACTURER_ID /* 77 */:
                return false;
            case 'N':
                return false;
            case 'O':
                return false;
            case 'P':
                return false;
            case EACTags.ANSWER_TO_RESET /* 81 */:
                return false;
            case 'R':
                return false;
            case 'S':
                return false;
            case 'T':
                return false;
            case Matrix.MATRIX_TYPE_RANDOM_UT /* 85 */:
                return false;
            case AlertDescription.inappropriate_fallback /* 86 */:
                return false;
            case 'W':
                return false;
            case 'X':
                return false;
            case 'Y':
                return false;
            case 'Z':
                return false;
            case '[':
                return false;
            case '\\':
                return false;
            case ']':
                return false;
            case '^':
                return false;
            case '_':
                return false;
            case '`':
                return false;
            case 'a':
                return true;
            case 'b':
                return true;
            case 'c':
                return true;
            case AlertDescription.no_renegotiation /* 100 */:
                return true;
            case ExtensionType.negotiated_ff_dhe_groups /* 101 */:
                return true;
            case 'f':
                return true;
            default:
                return false;
        }
    }

    static void checkAbsoluteURIReference(String str) {
        if (cache.contains(str)) {
            return;
        }
        URIUtil.ParsedURI parsedURI = new URIUtil.ParsedURI(str);
        try {
            if (parsedURI.scheme == null) {
                throwMalformedURIException(str, "Missing scheme in absolute URI reference");
            }
            checkScheme(parsedURI.scheme);
            if (parsedURI.authority != null) {
                checkAuthority(parsedURI.authority);
            }
            checkPath(parsedURI.path);
            if (parsedURI.fragment != null) {
                checkFragment(parsedURI.fragment);
            }
            if (parsedURI.query != null) {
                checkQuery(parsedURI.query);
            }
            cache.put(str);
        } catch (MalformedURIException e) {
            e.setData(str);
            throw e;
        }
    }

    static boolean isAlpha(char c) {
        switch (c) {
            case 'A':
                return true;
            case 'B':
                return true;
            case 'C':
                return true;
            case 'D':
                return true;
            case 'E':
                return true;
            case 'F':
                return true;
            case 'G':
                return true;
            case 'H':
                return true;
            case 'I':
                return true;
            case EACTags.CERTIFICATION_AUTHORITY_PUBLIC_KEY /* 74 */:
                return true;
            case EACTags.DEPRECATED /* 75 */:
                return true;
            case 'L':
                return true;
            case EACTags.INTEGRATED_CIRCUIT_MANUFACTURER_ID /* 77 */:
                return true;
            case 'N':
                return true;
            case 'O':
                return true;
            case 'P':
                return true;
            case EACTags.ANSWER_TO_RESET /* 81 */:
                return true;
            case 'R':
                return true;
            case 'S':
                return true;
            case 'T':
                return true;
            case Matrix.MATRIX_TYPE_RANDOM_UT /* 85 */:
                return true;
            case AlertDescription.inappropriate_fallback /* 86 */:
                return true;
            case 'W':
                return true;
            case 'X':
                return true;
            case 'Y':
                return true;
            case 'Z':
                return true;
            case '[':
                return false;
            case '\\':
                return false;
            case ']':
                return false;
            case '^':
                return false;
            case '_':
                return false;
            case '`':
                return false;
            case 'a':
                return true;
            case 'b':
                return true;
            case 'c':
                return true;
            case AlertDescription.no_renegotiation /* 100 */:
                return true;
            case ExtensionType.negotiated_ff_dhe_groups /* 101 */:
                return true;
            case 'f':
                return true;
            case 'g':
                return true;
            case 'h':
                return true;
            case CipherSuite.TLS_DH_RSA_WITH_AES_256_CBC_SHA256 /* 105 */:
                return true;
            case CipherSuite.TLS_DHE_DSS_WITH_AES_256_CBC_SHA256 /* 106 */:
                return true;
            case CipherSuite.TLS_DHE_RSA_WITH_AES_256_CBC_SHA256 /* 107 */:
                return true;
            case CipherSuite.TLS_DH_anon_WITH_AES_128_CBC_SHA256 /* 108 */:
                return true;
            case CipherSuite.TLS_DH_anon_WITH_AES_256_CBC_SHA256 /* 109 */:
                return true;
            case AlertDescription.unsupported_extension /* 110 */:
                return true;
            case AlertDescription.certificate_unobtainable /* 111 */:
                return true;
            case AlertDescription.unrecognized_name /* 112 */:
                return true;
            case AlertDescription.bad_certificate_status_response /* 113 */:
                return true;
            case AlertDescription.bad_certificate_hash_value /* 114 */:
                return true;
            case AlertDescription.unknown_psk_identity /* 115 */:
                return true;
            case 't':
                return true;
            case 'u':
                return true;
            case 'v':
                return true;
            case 'w':
                return true;
            case 'x':
                return true;
            case 'y':
                return true;
            case 'z':
                return true;
            default:
                return false;
        }
    }

    static boolean isSchemeCharacter(char c) {
        switch (c) {
            case '+':
                return true;
            case ',':
                return false;
            case '-':
                return true;
            case '.':
                return true;
            case '/':
                return false;
            case '0':
                return true;
            case '1':
                return true;
            case '2':
                return true;
            case '3':
                return true;
            case '4':
                return true;
            case '5':
                return true;
            case '6':
                return true;
            case '7':
                return true;
            case '8':
                return true;
            case '9':
                return true;
            case ':':
                return false;
            case ';':
                return false;
            case '<':
                return false;
            case '=':
                return false;
            case CipherSuite.TLS_DH_DSS_WITH_AES_128_CBC_SHA256 /* 62 */:
                return false;
            case '?':
                return false;
            case '@':
                return false;
            case 'A':
                return true;
            case 'B':
                return true;
            case 'C':
                return true;
            case 'D':
                return true;
            case 'E':
                return true;
            case 'F':
                return true;
            case 'G':
                return true;
            case 'H':
                return true;
            case 'I':
                return true;
            case EACTags.CERTIFICATION_AUTHORITY_PUBLIC_KEY /* 74 */:
                return true;
            case EACTags.DEPRECATED /* 75 */:
                return true;
            case 'L':
                return true;
            case EACTags.INTEGRATED_CIRCUIT_MANUFACTURER_ID /* 77 */:
                return true;
            case 'N':
                return true;
            case 'O':
                return true;
            case 'P':
                return true;
            case EACTags.ANSWER_TO_RESET /* 81 */:
                return true;
            case 'R':
                return true;
            case 'S':
                return true;
            case 'T':
                return true;
            case Matrix.MATRIX_TYPE_RANDOM_UT /* 85 */:
                return true;
            case AlertDescription.inappropriate_fallback /* 86 */:
                return true;
            case 'W':
                return true;
            case 'X':
                return true;
            case 'Y':
                return true;
            case 'Z':
                return true;
            case '[':
                return false;
            case '\\':
                return false;
            case ']':
                return false;
            case '^':
                return false;
            case '_':
                return false;
            case '`':
                return false;
            case 'a':
                return true;
            case 'b':
                return true;
            case 'c':
                return true;
            case AlertDescription.no_renegotiation /* 100 */:
                return true;
            case ExtensionType.negotiated_ff_dhe_groups /* 101 */:
                return true;
            case 'f':
                return true;
            case 'g':
                return true;
            case 'h':
                return true;
            case CipherSuite.TLS_DH_RSA_WITH_AES_256_CBC_SHA256 /* 105 */:
                return true;
            case CipherSuite.TLS_DHE_DSS_WITH_AES_256_CBC_SHA256 /* 106 */:
                return true;
            case CipherSuite.TLS_DHE_RSA_WITH_AES_256_CBC_SHA256 /* 107 */:
                return true;
            case CipherSuite.TLS_DH_anon_WITH_AES_128_CBC_SHA256 /* 108 */:
                return true;
            case CipherSuite.TLS_DH_anon_WITH_AES_256_CBC_SHA256 /* 109 */:
                return true;
            case AlertDescription.unsupported_extension /* 110 */:
                return true;
            case AlertDescription.certificate_unobtainable /* 111 */:
                return true;
            case AlertDescription.unrecognized_name /* 112 */:
                return true;
            case AlertDescription.bad_certificate_status_response /* 113 */:
                return true;
            case AlertDescription.bad_certificate_hash_value /* 114 */:
                return true;
            case AlertDescription.unknown_psk_identity /* 115 */:
                return true;
            case 't':
                return true;
            case 'u':
                return true;
            case 'v':
                return true;
            case 'w':
                return true;
            case 'x':
                return true;
            case 'y':
                return true;
            case 'z':
                return true;
            default:
                return false;
        }
    }

    private static boolean isPathCharacter(char c) {
        switch (c) {
            case '!':
                return true;
            case '\"':
                return false;
            case '#':
                return false;
            case EACTags.APPLICATION_EXPIRATION_DATE /* 36 */:
                return true;
            case EACTags.APPLICATION_EFFECTIVE_DATE /* 37 */:
                return false;
            case EACTags.CARD_EFFECTIVE_DATE /* 38 */:
                return true;
            case EACTags.INTERCHANGE_CONTROL /* 39 */:
                return true;
            case '(':
                return true;
            case ')':
                return true;
            case '*':
                return true;
            case '+':
                return true;
            case ',':
                return true;
            case '-':
                return true;
            case '.':
                return true;
            case '/':
                return false;
            case '0':
                return true;
            case '1':
                return true;
            case '2':
                return true;
            case '3':
                return true;
            case '4':
                return true;
            case '5':
                return true;
            case '6':
                return true;
            case '7':
                return true;
            case '8':
                return true;
            case '9':
                return true;
            case ':':
                return true;
            case ';':
                return true;
            case '<':
                return false;
            case '=':
                return true;
            case CipherSuite.TLS_DH_DSS_WITH_AES_128_CBC_SHA256 /* 62 */:
                return false;
            case '?':
                return false;
            case '@':
                return true;
            case 'A':
                return true;
            case 'B':
                return true;
            case 'C':
                return true;
            case 'D':
                return true;
            case 'E':
                return true;
            case 'F':
                return true;
            case 'G':
                return true;
            case 'H':
                return true;
            case 'I':
                return true;
            case EACTags.CERTIFICATION_AUTHORITY_PUBLIC_KEY /* 74 */:
                return true;
            case EACTags.DEPRECATED /* 75 */:
                return true;
            case 'L':
                return true;
            case EACTags.INTEGRATED_CIRCUIT_MANUFACTURER_ID /* 77 */:
                return true;
            case 'N':
                return true;
            case 'O':
                return true;
            case 'P':
                return true;
            case EACTags.ANSWER_TO_RESET /* 81 */:
                return true;
            case 'R':
                return true;
            case 'S':
                return true;
            case 'T':
                return true;
            case Matrix.MATRIX_TYPE_RANDOM_UT /* 85 */:
                return true;
            case AlertDescription.inappropriate_fallback /* 86 */:
                return true;
            case 'W':
                return true;
            case 'X':
                return true;
            case 'Y':
                return true;
            case 'Z':
                return true;
            case '[':
                return false;
            case '\\':
                return false;
            case ']':
                return false;
            case '^':
                return false;
            case '_':
                return true;
            case '`':
                return false;
            case 'a':
                return true;
            case 'b':
                return true;
            case 'c':
                return true;
            case AlertDescription.no_renegotiation /* 100 */:
                return true;
            case ExtensionType.negotiated_ff_dhe_groups /* 101 */:
                return true;
            case 'f':
                return true;
            case 'g':
                return true;
            case 'h':
                return true;
            case CipherSuite.TLS_DH_RSA_WITH_AES_256_CBC_SHA256 /* 105 */:
                return true;
            case CipherSuite.TLS_DHE_DSS_WITH_AES_256_CBC_SHA256 /* 106 */:
                return true;
            case CipherSuite.TLS_DHE_RSA_WITH_AES_256_CBC_SHA256 /* 107 */:
                return true;
            case CipherSuite.TLS_DH_anon_WITH_AES_128_CBC_SHA256 /* 108 */:
                return true;
            case CipherSuite.TLS_DH_anon_WITH_AES_256_CBC_SHA256 /* 109 */:
                return true;
            case AlertDescription.unsupported_extension /* 110 */:
                return true;
            case AlertDescription.certificate_unobtainable /* 111 */:
                return true;
            case AlertDescription.unrecognized_name /* 112 */:
                return true;
            case AlertDescription.bad_certificate_status_response /* 113 */:
                return true;
            case AlertDescription.bad_certificate_hash_value /* 114 */:
                return true;
            case AlertDescription.unknown_psk_identity /* 115 */:
                return true;
            case 't':
                return true;
            case 'u':
                return true;
            case 'v':
                return true;
            case 'w':
                return true;
            case 'x':
                return true;
            case 'y':
                return true;
            case 'z':
                return true;
            case '{':
                return false;
            case '|':
                return false;
            case '}':
                return false;
            case '~':
                return true;
            default:
                return false;
        }
    }

    private static boolean isUserInfoCharacter(char c) {
        switch (c) {
            case '!':
                return true;
            case '\"':
                return false;
            case '#':
                return false;
            case EACTags.APPLICATION_EXPIRATION_DATE /* 36 */:
                return true;
            case EACTags.APPLICATION_EFFECTIVE_DATE /* 37 */:
                return false;
            case EACTags.CARD_EFFECTIVE_DATE /* 38 */:
                return true;
            case EACTags.INTERCHANGE_CONTROL /* 39 */:
                return true;
            case '(':
                return true;
            case ')':
                return true;
            case '*':
                return true;
            case '+':
                return true;
            case ',':
                return true;
            case '-':
                return true;
            case '.':
                return true;
            case '/':
                return true;
            case '0':
                return true;
            case '1':
                return true;
            case '2':
                return true;
            case '3':
                return true;
            case '4':
                return true;
            case '5':
                return true;
            case '6':
                return true;
            case '7':
                return true;
            case '8':
                return true;
            case '9':
                return true;
            case ':':
                return true;
            case ';':
                return true;
            case '<':
                return false;
            case '=':
                return true;
            case CipherSuite.TLS_DH_DSS_WITH_AES_128_CBC_SHA256 /* 62 */:
                return false;
            case '?':
                return false;
            case '@':
                return false;
            case 'A':
                return true;
            case 'B':
                return true;
            case 'C':
                return true;
            case 'D':
                return true;
            case 'E':
                return true;
            case 'F':
                return true;
            case 'G':
                return true;
            case 'H':
                return true;
            case 'I':
                return true;
            case EACTags.CERTIFICATION_AUTHORITY_PUBLIC_KEY /* 74 */:
                return true;
            case EACTags.DEPRECATED /* 75 */:
                return true;
            case 'L':
                return true;
            case EACTags.INTEGRATED_CIRCUIT_MANUFACTURER_ID /* 77 */:
                return true;
            case 'N':
                return true;
            case 'O':
                return true;
            case 'P':
                return true;
            case EACTags.ANSWER_TO_RESET /* 81 */:
                return true;
            case 'R':
                return true;
            case 'S':
                return true;
            case 'T':
                return true;
            case Matrix.MATRIX_TYPE_RANDOM_UT /* 85 */:
                return true;
            case AlertDescription.inappropriate_fallback /* 86 */:
                return true;
            case 'W':
                return true;
            case 'X':
                return true;
            case 'Y':
                return true;
            case 'Z':
                return true;
            case '[':
                return false;
            case '\\':
                return false;
            case ']':
                return false;
            case '^':
                return false;
            case '_':
                return true;
            case '`':
                return false;
            case 'a':
                return true;
            case 'b':
                return true;
            case 'c':
                return true;
            case AlertDescription.no_renegotiation /* 100 */:
                return true;
            case ExtensionType.negotiated_ff_dhe_groups /* 101 */:
                return true;
            case 'f':
                return true;
            case 'g':
                return true;
            case 'h':
                return true;
            case CipherSuite.TLS_DH_RSA_WITH_AES_256_CBC_SHA256 /* 105 */:
                return true;
            case CipherSuite.TLS_DHE_DSS_WITH_AES_256_CBC_SHA256 /* 106 */:
                return true;
            case CipherSuite.TLS_DHE_RSA_WITH_AES_256_CBC_SHA256 /* 107 */:
                return true;
            case CipherSuite.TLS_DH_anon_WITH_AES_128_CBC_SHA256 /* 108 */:
                return true;
            case CipherSuite.TLS_DH_anon_WITH_AES_256_CBC_SHA256 /* 109 */:
                return true;
            case AlertDescription.unsupported_extension /* 110 */:
                return true;
            case AlertDescription.certificate_unobtainable /* 111 */:
                return true;
            case AlertDescription.unrecognized_name /* 112 */:
                return true;
            case AlertDescription.bad_certificate_status_response /* 113 */:
                return true;
            case AlertDescription.bad_certificate_hash_value /* 114 */:
                return true;
            case AlertDescription.unknown_psk_identity /* 115 */:
                return true;
            case 't':
                return true;
            case 'u':
                return true;
            case 'v':
                return true;
            case 'w':
                return true;
            case 'x':
                return true;
            case 'y':
                return true;
            case 'z':
                return true;
            case '{':
                return false;
            case '|':
                return false;
            case '}':
                return false;
            case '~':
                return true;
            default:
                return false;
        }
    }

    static void checkAbsoluteURI(String str) {
        URIUtil.ParsedURI parsedURI = new URIUtil.ParsedURI(str);
        try {
            if (parsedURI.scheme == null) {
                throwMalformedURIException(str, "Missing scheme in absolute URI");
            }
            checkScheme(parsedURI.scheme);
            if (parsedURI.authority != null) {
                checkAuthority(parsedURI.authority);
            }
            checkPath(parsedURI.path);
            if (parsedURI.fragment != null) {
                throwMalformedURIException(str, "URIs cannot have fragment identifiers");
            }
            if (parsedURI.query != null) {
                checkQuery(parsedURI.query);
            }
        } catch (MalformedURIException e) {
            e.setData(str);
            throw e;
        }
    }

    static synchronized void checkInternalDTDSubset(String str) {
        if (parser == null) {
            final InputSource inputSource = new InputSource(new EmptyReader());
            parser = Builder.findParser(false);
            parser.setEntityResolver(new EntityResolver() { // from class: nu.xom.Verifier.1
                @Override // org.xml.sax.EntityResolver
                public InputSource resolveEntity(String str2, String str3) {
                    return inputSource;
                }
            });
        }
        try {
            InputSource inputSource2 = new InputSource(new StringReader(new StringBuffer().append("<!DOCTYPE a [").append(str).append("]><a/>").toString()));
            inputSource2.setSystemId("http://www.example.org/");
            parser.parse(inputSource2);
        } catch (IOException e) {
            throw new RuntimeException("BUG: I don't think this can happen");
        } catch (SAXException e2) {
            IllegalDataException illegalDataException = new IllegalDataException(new StringBuffer().append("Malformed internal DTD subset: ").append(e2.getMessage()).toString(), e2);
            illegalDataException.setData(str);
            throw illegalDataException;
        }
    }

    static /* synthetic */ Class class$(String str) {
        try {
            return Class.forName(str);
        } catch (ClassNotFoundException e) {
            throw new NoClassDefFoundError(e.getMessage());
        }
    }

    static {
        Class clsClass$;
        if (class$nu$xom$Verifier == null) {
            clsClass$ = class$("nu.xom.Verifier");
            class$nu$xom$Verifier = clsClass$;
        } else {
            clsClass$ = class$nu$xom$Verifier;
        }
        ClassLoader classLoader = clsClass$.getClassLoader();
        if (classLoader != null) {
            loadFlags(classLoader);
        }
        if (flags == null) {
            loadFlags(Thread.currentThread().getContextClassLoader());
        }
        C0Table = new boolean[33];
        C0Table[10] = true;
        C0Table[13] = true;
        C0Table[9] = true;
        C0Table[32] = true;
        cache = new URICache();
    }
}
