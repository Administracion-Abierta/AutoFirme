package jakarta.xml.bind;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import org.spongycastle.asn1.eac.EACTags;
import org.spongycastle.crypto.tls.AlertDescription;
import org.spongycastle.crypto.tls.CipherSuite;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:jakarta/xml/bind/DatatypeConverterImpl.class */
final class DatatypeConverterImpl implements DatatypeConverterInterface {
    public static final DatatypeConverterInterface theInstance;
    private static final char[] hexCode;
    private static final byte[] decodeMap;
    private static final byte PADDING = 127;
    private static final char[] encodeMap;
    private static final DatatypeFactory datatypeFactory;
    static final /* synthetic */ boolean $assertionsDisabled;

    static {
        $assertionsDisabled = !DatatypeConverterImpl.class.desiredAssertionStatus();
        theInstance = new DatatypeConverterImpl();
        hexCode = "0123456789ABCDEF".toCharArray();
        decodeMap = initDecodeMap();
        encodeMap = initEncodeMap();
        try {
            datatypeFactory = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {
            throw new Error(e);
        }
    }

    protected DatatypeConverterImpl() {
    }

    @Override // jakarta.xml.bind.DatatypeConverterInterface
    public String parseString(String lexicalXSDString) {
        return lexicalXSDString;
    }

    @Override // jakarta.xml.bind.DatatypeConverterInterface
    public BigInteger parseInteger(String lexicalXSDInteger) {
        return _parseInteger(lexicalXSDInteger);
    }

    public static BigInteger _parseInteger(CharSequence s) {
        return new BigInteger(removeOptionalPlus(WhiteSpaceProcessor.trim(s)).toString());
    }

    @Override // jakarta.xml.bind.DatatypeConverterInterface
    public String printInteger(BigInteger val) {
        return _printInteger(val);
    }

    public static String _printInteger(BigInteger val) {
        return val.toString();
    }

    @Override // jakarta.xml.bind.DatatypeConverterInterface
    public int parseInt(String s) {
        return _parseInt(s);
    }

    public static int _parseInt(CharSequence s) {
        int len = s.length();
        int sign = 1;
        int r = 0;
        for (int i = 0; i < len; i++) {
            char ch = s.charAt(i);
            if (!WhiteSpaceProcessor.isWhiteSpace(ch)) {
                if ('0' <= ch && ch <= '9') {
                    r = (r * 10) + (ch - '0');
                } else if (ch == '-') {
                    sign = -1;
                } else if (ch != '+') {
                    throw new NumberFormatException("Not a number: " + ((Object) s));
                }
            }
        }
        return r * sign;
    }

    @Override // jakarta.xml.bind.DatatypeConverterInterface
    public long parseLong(String lexicalXSLong) {
        return _parseLong(lexicalXSLong);
    }

    public static long _parseLong(CharSequence s) {
        return Long.parseLong(removeOptionalPlus(WhiteSpaceProcessor.trim(s)).toString());
    }

    @Override // jakarta.xml.bind.DatatypeConverterInterface
    public short parseShort(String lexicalXSDShort) {
        return _parseShort(lexicalXSDShort);
    }

    public static short _parseShort(CharSequence s) {
        return (short) _parseInt(s);
    }

    @Override // jakarta.xml.bind.DatatypeConverterInterface
    public String printShort(short val) {
        return _printShort(val);
    }

    public static String _printShort(short val) {
        return String.valueOf((int) val);
    }

    @Override // jakarta.xml.bind.DatatypeConverterInterface
    public BigDecimal parseDecimal(String content) {
        return _parseDecimal(content);
    }

    public static BigDecimal _parseDecimal(CharSequence content) {
        CharSequence content2 = WhiteSpaceProcessor.trim(content);
        if (content2.length() <= 0) {
            return null;
        }
        return new BigDecimal(content2.toString());
    }

    @Override // jakarta.xml.bind.DatatypeConverterInterface
    public float parseFloat(String lexicalXSDFloat) {
        return _parseFloat(lexicalXSDFloat);
    }

    public static float _parseFloat(CharSequence _val) {
        String s = WhiteSpaceProcessor.trim(_val).toString();
        if (s.equals("NaN")) {
            return Float.NaN;
        }
        if (s.equals("INF")) {
            return Float.POSITIVE_INFINITY;
        }
        if (s.equals("-INF")) {
            return Float.NEGATIVE_INFINITY;
        }
        if (s.length() == 0 || !isDigitOrPeriodOrSign(s.charAt(0)) || !isDigitOrPeriodOrSign(s.charAt(s.length() - 1))) {
            throw new NumberFormatException();
        }
        return Float.parseFloat(s);
    }

    @Override // jakarta.xml.bind.DatatypeConverterInterface
    public String printFloat(float v) {
        return _printFloat(v);
    }

    public static String _printFloat(float v) {
        if (Float.isNaN(v)) {
            return "NaN";
        }
        if (v == Float.POSITIVE_INFINITY) {
            return "INF";
        }
        if (v == Float.NEGATIVE_INFINITY) {
            return "-INF";
        }
        return String.valueOf(v);
    }

    @Override // jakarta.xml.bind.DatatypeConverterInterface
    public double parseDouble(String lexicalXSDDouble) {
        return _parseDouble(lexicalXSDDouble);
    }

    public static double _parseDouble(CharSequence _val) {
        String val = WhiteSpaceProcessor.trim(_val).toString();
        if (val.equals("NaN")) {
            return Double.NaN;
        }
        if (val.equals("INF")) {
            return Double.POSITIVE_INFINITY;
        }
        if (val.equals("-INF")) {
            return Double.NEGATIVE_INFINITY;
        }
        if (val.length() == 0 || !isDigitOrPeriodOrSign(val.charAt(0)) || !isDigitOrPeriodOrSign(val.charAt(val.length() - 1))) {
            throw new NumberFormatException(val);
        }
        return Double.parseDouble(val);
    }

    @Override // jakarta.xml.bind.DatatypeConverterInterface
    public boolean parseBoolean(String lexicalXSDBoolean) {
        Boolean b = _parseBoolean(lexicalXSDBoolean);
        if (b == null) {
            return false;
        }
        return b.booleanValue();
    }

    /* JADX WARN: Code duplicated, block: B:27:0x009e  */
    /* JADX WARN: Code duplicated, block: B:28:0x00a4  */
    /* JADX WARN: Code duplicated, block: B:39:0x00d7  */
    /* JADX WARN: Code duplicated, block: B:40:0x00dd  */
    public static Boolean _parseBoolean(CharSequence literal) {
        char ch;
        if (literal == null) {
            return null;
        }
        int i = 0;
        int len = literal.length();
        boolean value = false;
        if (literal.length() <= 0) {
            return null;
        }
        do {
            int i2 = i;
            i++;
            ch = literal.charAt(i2);
            if (!WhiteSpaceProcessor.isWhiteSpace(ch)) {
                break;
            }
        } while (i < len);
        int strIndex = 0;
        switch (ch) {
            case '0':
                value = false;
                break;
            case '1':
                value = true;
                break;
            case 'f':
                do {
                    int i3 = i;
                    i++;
                    char ch2 = literal.charAt(i3);
                    int i4 = strIndex;
                    strIndex++;
                    if ("alse".charAt(i4) != ch2 || i >= len) {
                    }
                    if (strIndex == 4) {
                        value = false;
                    } else {
                        return false;
                    }
                    break;
                } while (strIndex < 4);
                if (strIndex == 4) {
                    value = false;
                } else {
                    return false;
                }
                break;
            case 't':
                do {
                    int i5 = i;
                    i++;
                    char ch3 = literal.charAt(i5);
                    int i6 = strIndex;
                    strIndex++;
                    if ("rue".charAt(i6) != ch3 || i >= len) {
                    }
                    if (strIndex == 3) {
                        value = true;
                    } else {
                        return false;
                    }
                    break;
                } while (strIndex < 3);
                if (strIndex == 3) {
                    value = true;
                } else {
                    return false;
                }
                break;
        }
        if (i < len) {
            do {
                int i7 = i;
                i++;
                if (WhiteSpaceProcessor.isWhiteSpace(literal.charAt(i7))) {
                }
            } while (i < len);
        }
        if (i == len) {
            return Boolean.valueOf(value);
        }
        return null;
    }

    @Override // jakarta.xml.bind.DatatypeConverterInterface
    public String printBoolean(boolean val) {
        return val ? "true" : "false";
    }

    public static String _printBoolean(boolean val) {
        return val ? "true" : "false";
    }

    @Override // jakarta.xml.bind.DatatypeConverterInterface
    public byte parseByte(String lexicalXSDByte) {
        return _parseByte(lexicalXSDByte);
    }

    public static byte _parseByte(CharSequence literal) {
        return (byte) _parseInt(literal);
    }

    @Override // jakarta.xml.bind.DatatypeConverterInterface
    public String printByte(byte val) {
        return _printByte(val);
    }

    public static String _printByte(byte val) {
        return String.valueOf((int) val);
    }

    @Override // jakarta.xml.bind.DatatypeConverterInterface
    public QName parseQName(String lexicalXSDQName, NamespaceContext nsc) {
        return _parseQName(lexicalXSDQName, nsc);
    }

    public static QName _parseQName(CharSequence text, NamespaceContext nsc) {
        String prefix;
        String localPart;
        String uri;
        int length = text.length();
        int start = 0;
        while (start < length && WhiteSpaceProcessor.isWhiteSpace(text.charAt(start))) {
            start++;
        }
        int end = length;
        while (end > start && WhiteSpaceProcessor.isWhiteSpace(text.charAt(end - 1))) {
            end--;
        }
        if (end == start) {
            throw new IllegalArgumentException("input is empty");
        }
        int idx = start + 1;
        while (idx < end && text.charAt(idx) != ':') {
            idx++;
        }
        if (idx == end) {
            uri = nsc.getNamespaceURI("");
            localPart = text.subSequence(start, end).toString();
            prefix = "";
        } else {
            prefix = text.subSequence(start, idx).toString();
            localPart = text.subSequence(idx + 1, end).toString();
            uri = nsc.getNamespaceURI(prefix);
            if (uri == null || uri.length() == 0) {
                throw new IllegalArgumentException("prefix " + prefix + " is not bound to a namespace");
            }
        }
        return new QName(uri, localPart, prefix);
    }

    @Override // jakarta.xml.bind.DatatypeConverterInterface
    public Calendar parseDateTime(String lexicalXSDDateTime) {
        return _parseDateTime(lexicalXSDDateTime);
    }

    public static GregorianCalendar _parseDateTime(CharSequence s) {
        String val = WhiteSpaceProcessor.trim(s).toString();
        return datatypeFactory.newXMLGregorianCalendar(val).toGregorianCalendar();
    }

    @Override // jakarta.xml.bind.DatatypeConverterInterface
    public String printDateTime(Calendar val) {
        return _printDateTime(val);
    }

    public static String _printDateTime(Calendar val) {
        return CalendarFormatter.doFormat("%Y-%M-%DT%h:%m:%s%z", val);
    }

    @Override // jakarta.xml.bind.DatatypeConverterInterface
    public byte[] parseBase64Binary(String lexicalXSDBase64Binary) {
        return _parseBase64Binary(lexicalXSDBase64Binary);
    }

    @Override // jakarta.xml.bind.DatatypeConverterInterface
    public byte[] parseHexBinary(String s) {
        int len = s.length();
        if (len % 2 != 0) {
            throw new IllegalArgumentException("hexBinary needs to be even-length: " + s);
        }
        byte[] out = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            int h = hexToBin(s.charAt(i));
            int l = hexToBin(s.charAt(i + 1));
            if (h == -1 || l == -1) {
                throw new IllegalArgumentException("contains illegal character for hexBinary: " + s);
            }
            out[i / 2] = (byte) ((h * 16) + l);
        }
        return out;
    }

    private static int hexToBin(char ch) {
        if ('0' <= ch && ch <= '9') {
            return ch - '0';
        }
        if ('A' <= ch && ch <= 'F') {
            return (ch - 'A') + 10;
        }
        if ('a' <= ch && ch <= 'f') {
            return (ch - 'a') + 10;
        }
        return -1;
    }

    @Override // jakarta.xml.bind.DatatypeConverterInterface
    public String printHexBinary(byte[] data) {
        StringBuilder r = new StringBuilder(data.length * 2);
        for (byte b : data) {
            r.append(hexCode[(b >> 4) & 15]);
            r.append(hexCode[b & 15]);
        }
        return r.toString();
    }

    @Override // jakarta.xml.bind.DatatypeConverterInterface
    public long parseUnsignedInt(String lexicalXSDUnsignedInt) {
        return _parseLong(lexicalXSDUnsignedInt);
    }

    @Override // jakarta.xml.bind.DatatypeConverterInterface
    public String printUnsignedInt(long val) {
        return _printLong(val);
    }

    @Override // jakarta.xml.bind.DatatypeConverterInterface
    public int parseUnsignedShort(String lexicalXSDUnsignedShort) {
        return _parseInt(lexicalXSDUnsignedShort);
    }

    @Override // jakarta.xml.bind.DatatypeConverterInterface
    public Calendar parseTime(String lexicalXSDTime) {
        return datatypeFactory.newXMLGregorianCalendar(lexicalXSDTime).toGregorianCalendar();
    }

    @Override // jakarta.xml.bind.DatatypeConverterInterface
    public String printTime(Calendar val) {
        return CalendarFormatter.doFormat("%h:%m:%s%z", val);
    }

    @Override // jakarta.xml.bind.DatatypeConverterInterface
    public Calendar parseDate(String lexicalXSDDate) {
        return datatypeFactory.newXMLGregorianCalendar(lexicalXSDDate).toGregorianCalendar();
    }

    @Override // jakarta.xml.bind.DatatypeConverterInterface
    public String printDate(Calendar val) {
        return _printDate(val);
    }

    public static String _printDate(Calendar val) {
        return CalendarFormatter.doFormat("%Y-%M-%D%z", val);
    }

    @Override // jakarta.xml.bind.DatatypeConverterInterface
    public String parseAnySimpleType(String lexicalXSDAnySimpleType) {
        return lexicalXSDAnySimpleType;
    }

    @Override // jakarta.xml.bind.DatatypeConverterInterface
    public String printString(String val) {
        return val;
    }

    @Override // jakarta.xml.bind.DatatypeConverterInterface
    public String printInt(int val) {
        return _printInt(val);
    }

    public static String _printInt(int val) {
        return String.valueOf(val);
    }

    @Override // jakarta.xml.bind.DatatypeConverterInterface
    public String printLong(long val) {
        return _printLong(val);
    }

    public static String _printLong(long val) {
        return String.valueOf(val);
    }

    @Override // jakarta.xml.bind.DatatypeConverterInterface
    public String printDecimal(BigDecimal val) {
        return _printDecimal(val);
    }

    public static String _printDecimal(BigDecimal val) {
        return val.toPlainString();
    }

    @Override // jakarta.xml.bind.DatatypeConverterInterface
    public String printDouble(double v) {
        return _printDouble(v);
    }

    public static String _printDouble(double v) {
        if (Double.isNaN(v)) {
            return "NaN";
        }
        if (v == Double.POSITIVE_INFINITY) {
            return "INF";
        }
        if (v == Double.NEGATIVE_INFINITY) {
            return "-INF";
        }
        return String.valueOf(v);
    }

    @Override // jakarta.xml.bind.DatatypeConverterInterface
    public String printQName(QName val, NamespaceContext nsc) {
        return _printQName(val, nsc);
    }

    public static String _printQName(QName val, NamespaceContext nsc) {
        String qname;
        String prefix = nsc.getPrefix(val.getNamespaceURI());
        String localPart = val.getLocalPart();
        if (prefix == null || prefix.length() == 0) {
            qname = localPart;
        } else {
            qname = prefix + ':' + localPart;
        }
        return qname;
    }

    @Override // jakarta.xml.bind.DatatypeConverterInterface
    public String printBase64Binary(byte[] val) {
        return _printBase64Binary(val);
    }

    @Override // jakarta.xml.bind.DatatypeConverterInterface
    public String printUnsignedShort(int val) {
        return String.valueOf(val);
    }

    @Override // jakarta.xml.bind.DatatypeConverterInterface
    public String printAnySimpleType(String val) {
        return val;
    }

    public static String installHook(String s) {
        DatatypeConverter.setDatatypeConverter(theInstance);
        return s;
    }

    private static byte[] initDecodeMap() {
        byte[] map = new byte[128];
        for (int i = 0; i < 128; i++) {
            map[i] = -1;
        }
        for (int i2 = 65; i2 <= 90; i2++) {
            map[i2] = (byte) (i2 - 65);
        }
        for (int i3 = 97; i3 <= 122; i3++) {
            map[i3] = (byte) ((i3 - 97) + 26);
        }
        for (int i4 = 48; i4 <= 57; i4++) {
            map[i4] = (byte) ((i4 - 48) + 52);
        }
        map[43] = 62;
        map[47] = 63;
        map[61] = 127;
        return map;
    }

    private static int guessLength(String text) {
        int len = text.length();
        int j = len - 1;
        while (j >= 0) {
            byte code = decodeMap[text.charAt(j)];
            if (code == 127) {
                j--;
            } else {
                if (code != -1) {
                    break;
                }
                return (text.length() / 4) * 3;
            }
        }
        int padSize = len - (j + 1);
        if (padSize > 2) {
            return (text.length() / 4) * 3;
        }
        return ((text.length() / 4) * 3) - padSize;
    }

    public static byte[] _parseBase64Binary(String text) {
        int buflen = guessLength(text);
        byte[] out = new byte[buflen];
        int o = 0;
        int len = text.length();
        byte[] quadruplet = new byte[4];
        int q = 0;
        for (int i = 0; i < len; i++) {
            char ch = text.charAt(i);
            byte v = decodeMap[ch];
            if (v != -1) {
                int i2 = q;
                q++;
                quadruplet[i2] = v;
            }
            if (q == 4) {
                int i3 = o;
                o++;
                out[i3] = (byte) ((quadruplet[0] << 2) | (quadruplet[1] >> 4));
                if (quadruplet[2] != 127) {
                    o++;
                    out[o] = (byte) ((quadruplet[1] << 4) | (quadruplet[2] >> 2));
                }
                if (quadruplet[3] != 127) {
                    int i4 = o;
                    o++;
                    out[i4] = (byte) ((quadruplet[2] << 6) | quadruplet[3]);
                }
                q = 0;
            }
        }
        if (buflen == o) {
            return out;
        }
        byte[] nb = new byte[o];
        System.arraycopy(out, 0, nb, 0, o);
        return nb;
    }

    private static char[] initEncodeMap() {
        char[] map = new char[64];
        for (int i = 0; i < 26; i++) {
            map[i] = (char) (65 + i);
        }
        for (int i2 = 26; i2 < 52; i2++) {
            map[i2] = (char) (97 + (i2 - 26));
        }
        for (int i3 = 52; i3 < 62; i3++) {
            map[i3] = (char) (48 + (i3 - 52));
        }
        map[62] = '+';
        map[63] = '/';
        return map;
    }

    public static char encode(int i) {
        return encodeMap[i & 63];
    }

    public static byte encodeByte(int i) {
        return (byte) encodeMap[i & 63];
    }

    public static String _printBase64Binary(byte[] input) {
        return _printBase64Binary(input, 0, input.length);
    }

    public static String _printBase64Binary(byte[] input, int offset, int len) {
        char[] buf = new char[((len + 2) / 3) * 4];
        int ptr = _printBase64Binary(input, offset, len, buf, 0);
        if ($assertionsDisabled || ptr == buf.length) {
            return new String(buf);
        }
        throw new AssertionError();
    }

    public static int _printBase64Binary(byte[] input, int offset, int len, char[] buf, int ptr) {
        int remaining = len;
        int i = offset;
        while (remaining >= 3) {
            int i2 = ptr;
            int ptr2 = ptr + 1;
            buf[i2] = encode(input[i] >> 2);
            int ptr3 = ptr2 + 1;
            buf[ptr2] = encode(((input[i] & 3) << 4) | ((input[i + 1] >> 4) & 15));
            int ptr4 = ptr3 + 1;
            buf[ptr3] = encode(((input[i + 1] & 15) << 2) | ((input[i + 2] >> 6) & 3));
            ptr = ptr4 + 1;
            buf[ptr4] = encode(input[i + 2] & 63);
            remaining -= 3;
            i += 3;
        }
        if (remaining == 1) {
            int i3 = ptr;
            int ptr5 = ptr + 1;
            buf[i3] = encode(input[i] >> 2);
            int ptr6 = ptr5 + 1;
            buf[ptr5] = encode((input[i] & 3) << 4);
            int ptr7 = ptr6 + 1;
            buf[ptr6] = '=';
            ptr = ptr7 + 1;
            buf[ptr7] = '=';
        }
        if (remaining == 2) {
            int i4 = ptr;
            int ptr8 = ptr + 1;
            buf[i4] = encode(input[i] >> 2);
            int ptr9 = ptr8 + 1;
            buf[ptr8] = encode(((input[i] & 3) << 4) | ((input[i + 1] >> 4) & 15));
            int ptr10 = ptr9 + 1;
            buf[ptr9] = encode((input[i + 1] & 15) << 2);
            ptr = ptr10 + 1;
            buf[ptr10] = '=';
        }
        return ptr;
    }

    public static int _printBase64Binary(byte[] input, int offset, int len, byte[] out, int ptr) {
        int remaining = len;
        int i = offset;
        while (remaining >= 3) {
            int i2 = ptr;
            int ptr2 = ptr + 1;
            out[i2] = encodeByte(input[i] >> 2);
            int ptr3 = ptr2 + 1;
            out[ptr2] = encodeByte(((input[i] & 3) << 4) | ((input[i + 1] >> 4) & 15));
            int ptr4 = ptr3 + 1;
            out[ptr3] = encodeByte(((input[i + 1] & 15) << 2) | ((input[i + 2] >> 6) & 3));
            ptr = ptr4 + 1;
            out[ptr4] = encodeByte(input[i + 2] & 63);
            remaining -= 3;
            i += 3;
        }
        if (remaining == 1) {
            int i3 = ptr;
            int ptr5 = ptr + 1;
            out[i3] = encodeByte(input[i] >> 2);
            int ptr6 = ptr5 + 1;
            out[ptr5] = encodeByte((input[i] & 3) << 4);
            int ptr7 = ptr6 + 1;
            out[ptr6] = 61;
            ptr = ptr7 + 1;
            out[ptr7] = 61;
        }
        if (remaining == 2) {
            int i4 = ptr;
            int ptr8 = ptr + 1;
            out[i4] = encodeByte(input[i] >> 2);
            int ptr9 = ptr8 + 1;
            out[ptr8] = encodeByte(((input[i] & 3) << 4) | ((input[i + 1] >> 4) & 15));
            int ptr10 = ptr9 + 1;
            out[ptr9] = encodeByte((input[i + 1] & 15) << 2);
            ptr = ptr10 + 1;
            out[ptr10] = 61;
        }
        return ptr;
    }

    private static CharSequence removeOptionalPlus(CharSequence s) {
        int len = s.length();
        if (len <= 1 || s.charAt(0) != '+') {
            return s;
        }
        CharSequence s2 = s.subSequence(1, len);
        char ch = s2.charAt(0);
        if ('0' <= ch && ch <= '9') {
            return s2;
        }
        if ('.' == ch) {
            return s2;
        }
        throw new NumberFormatException();
    }

    private static boolean isDigitOrPeriodOrSign(char ch) {
        if (('0' <= ch && ch <= '9') || ch == '+' || ch == '-' || ch == '.') {
            return true;
        }
        return false;
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:jakarta/xml/bind/DatatypeConverterImpl$CalendarFormatter.class */
    private static final class CalendarFormatter {
        private CalendarFormatter() {
        }

        public static String doFormat(String format, Calendar cal) throws IllegalArgumentException {
            int fidx = 0;
            int flen = format.length();
            StringBuilder buf = new StringBuilder();
            while (fidx < flen) {
                int i = fidx;
                fidx++;
                char fch = format.charAt(i);
                if (fch != '%') {
                    buf.append(fch);
                } else {
                    fidx++;
                    switch (format.charAt(fidx)) {
                        case 'D':
                            formatDays(cal, buf);
                            break;
                        case EACTags.INTEGRATED_CIRCUIT_MANUFACTURER_ID /* 77 */:
                            formatMonth(cal, buf);
                            break;
                        case 'Y':
                            formatYear(cal, buf);
                            break;
                        case 'h':
                            formatHours(cal, buf);
                            break;
                        case CipherSuite.TLS_DH_anon_WITH_AES_256_CBC_SHA256 /* 109 */:
                            formatMinutes(cal, buf);
                            break;
                        case AlertDescription.unknown_psk_identity /* 115 */:
                            formatSeconds(cal, buf);
                            break;
                        case 'z':
                            formatTimeZone(cal, buf);
                            break;
                        default:
                            throw new InternalError();
                    }
                }
            }
            return buf.toString();
        }

        private static void formatYear(Calendar cal, StringBuilder buf) {
            String string;
            String s;
            int year = cal.get(1);
            if (year <= 0) {
                string = Integer.toString(1 - year);
            } else {
                string = Integer.toString(year);
            }
            while (true) {
                s = string;
                if (s.length() >= 4) {
                    break;
                } else {
                    string = '0' + s;
                }
            }
            if (year <= 0) {
                s = '-' + s;
            }
            buf.append(s);
        }

        private static void formatMonth(Calendar cal, StringBuilder buf) {
            formatTwoDigits(cal.get(2) + 1, buf);
        }

        private static void formatDays(Calendar cal, StringBuilder buf) {
            formatTwoDigits(cal.get(5), buf);
        }

        private static void formatHours(Calendar cal, StringBuilder buf) {
            formatTwoDigits(cal.get(11), buf);
        }

        private static void formatMinutes(Calendar cal, StringBuilder buf) {
            formatTwoDigits(cal.get(12), buf);
        }

        private static void formatSeconds(Calendar cal, StringBuilder buf) {
            int n;
            formatTwoDigits(cal.get(13), buf);
            if (cal.isSet(14) && (n = cal.get(14)) != 0) {
                String string = Integer.toString(n);
                while (true) {
                    String ms = string;
                    if (ms.length() < 3) {
                        string = '0' + ms;
                    } else {
                        buf.append('.');
                        buf.append(ms);
                        return;
                    }
                }
            }
        }

        private static void formatTimeZone(Calendar cal, StringBuilder buf) {
            TimeZone tz = cal.getTimeZone();
            if (tz == null) {
                return;
            }
            int offset = tz.getOffset(cal.getTime().getTime());
            if (offset == 0) {
                buf.append('Z');
                return;
            }
            if (offset >= 0) {
                buf.append('+');
            } else {
                buf.append('-');
                offset *= -1;
            }
            int offset2 = offset / 60000;
            formatTwoDigits(offset2 / 60, buf);
            buf.append(':');
            formatTwoDigits(offset2 % 60, buf);
        }

        private static void formatTwoDigits(int n, StringBuilder buf) {
            if (n < 10) {
                buf.append('0');
            }
            buf.append(n);
        }
    }
}
