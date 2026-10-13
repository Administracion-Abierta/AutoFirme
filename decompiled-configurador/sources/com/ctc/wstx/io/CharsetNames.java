package com.ctc.wstx.io;

import com.ctc.wstx.util.StringUtil;
import java.io.OutputStreamWriter;
import java.io.Writer;
import org.spongycastle.asn1.eac.EACTags;
import org.spongycastle.crypto.tls.AlertDescription;
import org.spongycastle.crypto.tls.CipherSuite;
import org.spongycastle.crypto.tls.ExtensionType;
import org.spongycastle.pqc.math.linearalgebra.Matrix;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/io/CharsetNames.class */
public final class CharsetNames {
    public static final String CS_US_ASCII = "US-ASCII";
    public static final String CS_UTF8 = "UTF-8";
    public static final String CS_UTF16 = "UTF-16";
    public static final String CS_UTF16BE = "UTF-16BE";
    public static final String CS_UTF16LE = "UTF-16LE";
    public static final String CS_UTF32 = "UTF-32";
    public static final String CS_UTF32BE = "UTF-32BE";
    public static final String CS_UTF32LE = "UTF-32LE";
    public static final String CS_ISO_LATIN1 = "ISO-8859-1";
    public static final String CS_SHIFT_JIS = "Shift_JIS";
    public static final String CS_EBCDIC_SUBSET = "IBM037";

    public static String normalize(String csName) {
        char d;
        if (csName == null || csName.length() < 3) {
            return csName;
        }
        boolean gotCsPrefix = false;
        char c = csName.charAt(0);
        if ((c == 'c' || c == 'C') && ((d = csName.charAt(1)) == 's' || d == 'S')) {
            csName = csName.substring(2);
            c = csName.charAt(0);
            gotCsPrefix = true;
        }
        switch (c) {
            case 'A':
            case 'a':
                if (StringUtil.equalEncodings(csName, "ASCII")) {
                    return CS_US_ASCII;
                }
                return csName;
            case 'B':
            case 'D':
            case 'F':
            case 'G':
            case 'H':
            case EACTags.DEPRECATED /* 75 */:
            case 'L':
            case EACTags.INTEGRATED_CIRCUIT_MANUFACTURER_ID /* 77 */:
            case 'N':
            case 'O':
            case 'P':
            case EACTags.ANSWER_TO_RESET /* 81 */:
            case 'R':
            case 'T':
            case AlertDescription.inappropriate_fallback /* 86 */:
            case 'W':
            case 'X':
            case 'Y':
            case 'Z':
            case '[':
            case '\\':
            case ']':
            case '^':
            case '_':
            case '`':
            case 'b':
            case AlertDescription.no_renegotiation /* 100 */:
            case 'f':
            case 'g':
            case 'h':
            case CipherSuite.TLS_DHE_RSA_WITH_AES_256_CBC_SHA256 /* 107 */:
            case CipherSuite.TLS_DH_anon_WITH_AES_128_CBC_SHA256 /* 108 */:
            case CipherSuite.TLS_DH_anon_WITH_AES_256_CBC_SHA256 /* 109 */:
            case AlertDescription.unsupported_extension /* 110 */:
            case AlertDescription.certificate_unobtainable /* 111 */:
            case AlertDescription.unrecognized_name /* 112 */:
            case AlertDescription.bad_certificate_status_response /* 113 */:
            case AlertDescription.bad_certificate_hash_value /* 114 */:
            case 't':
            default:
                return csName;
            case 'C':
            case 'c':
                if (StringUtil.encodingStartsWith(csName, "cp")) {
                    return "IBM" + StringUtil.trimEncoding(csName, true).substring(2);
                }
                if (StringUtil.encodingStartsWith(csName, "cs") && StringUtil.encodingStartsWith(csName, "csIBM")) {
                    return StringUtil.trimEncoding(csName, true).substring(2);
                }
                return csName;
            case 'E':
            case ExtensionType.negotiated_ff_dhe_groups /* 101 */:
                if (csName.startsWith("EBCDIC-CP-") || csName.startsWith("ebcdic-cp-")) {
                    String type = StringUtil.trimEncoding(csName, true).substring(8);
                    if (type.equals("US") || type.equals("CA") || type.equals("WT") || type.equals("NL")) {
                        return CS_EBCDIC_SUBSET;
                    }
                    if (type.equals("DK") || type.equals("NO")) {
                        return "IBM277";
                    }
                    if (type.equals("FI") || type.equals("SE")) {
                        return "IBM278";
                    }
                    if (type.equals("ROECE") || type.equals("YU")) {
                        return "IBM870";
                    }
                    if (type.equals("IT")) {
                        return "IBM280";
                    }
                    if (type.equals("ES")) {
                        return "IBM284";
                    }
                    if (type.equals("GB")) {
                        return "IBM285";
                    }
                    if (type.equals("FR")) {
                        return "IBM297";
                    }
                    if (type.equals("AR1")) {
                        return "IBM420";
                    }
                    if (type.equals("AR2")) {
                        return "IBM918";
                    }
                    if (type.equals("HE")) {
                        return "IBM424";
                    }
                    if (type.equals("CH")) {
                        return "IBM500";
                    }
                    return type.equals("IS") ? "IBM871" : CS_EBCDIC_SUBSET;
                }
                return csName;
            case 'I':
            case CipherSuite.TLS_DH_RSA_WITH_AES_256_CBC_SHA256 /* 105 */:
                if (StringUtil.equalEncodings(csName, "ISO-8859-1") || StringUtil.equalEncodings(csName, "ISO-Latin1")) {
                    return "ISO-8859-1";
                }
                if (!StringUtil.encodingStartsWith(csName, "ISO-10646")) {
                    if (StringUtil.encodingStartsWith(csName, "IBM")) {
                        return csName;
                    }
                    return csName;
                }
                int ix = csName.indexOf("10646");
                String suffix = csName.substring(ix + 5);
                if (StringUtil.equalEncodings(suffix, "UCS-Basic")) {
                    return CS_US_ASCII;
                }
                if (StringUtil.equalEncodings(suffix, "Unicode-Latin1")) {
                    return "ISO-8859-1";
                }
                if (StringUtil.equalEncodings(suffix, "UCS-2")) {
                    return CS_UTF16;
                }
                if (StringUtil.equalEncodings(suffix, "UCS-4")) {
                    return CS_UTF32;
                }
                if (StringUtil.equalEncodings(suffix, "UTF-1") || StringUtil.equalEncodings(suffix, "J-1") || StringUtil.equalEncodings(suffix, CS_US_ASCII)) {
                    return CS_US_ASCII;
                }
                return csName;
            case EACTags.CERTIFICATION_AUTHORITY_PUBLIC_KEY /* 74 */:
            case CipherSuite.TLS_DHE_DSS_WITH_AES_256_CBC_SHA256 /* 106 */:
                if (StringUtil.equalEncodings(csName, "JIS_Encoding")) {
                    return CS_SHIFT_JIS;
                }
                return csName;
            case 'S':
            case AlertDescription.unknown_psk_identity /* 115 */:
                if (StringUtil.equalEncodings(csName, CS_SHIFT_JIS)) {
                    return CS_SHIFT_JIS;
                }
                return csName;
            case Matrix.MATRIX_TYPE_RANDOM_UT /* 85 */:
            case 'u':
                if (csName.length() >= 2) {
                    switch (csName.charAt(1)) {
                        case 'C':
                        case 'c':
                            if (StringUtil.equalEncodings(csName, "UCS-2")) {
                                return CS_UTF16;
                            }
                            if (StringUtil.equalEncodings(csName, "UCS-4")) {
                                return CS_UTF32;
                            }
                            return csName;
                        case 'N':
                        case AlertDescription.unsupported_extension /* 110 */:
                            if (gotCsPrefix) {
                                if (StringUtil.equalEncodings(csName, "Unicode")) {
                                    return CS_UTF16;
                                }
                                if (StringUtil.equalEncodings(csName, "UnicodeAscii")) {
                                    return "ISO-8859-1";
                                }
                                if (StringUtil.equalEncodings(csName, "UnicodeAscii")) {
                                    return CS_US_ASCII;
                                }
                                return csName;
                            }
                            return csName;
                        case 'S':
                        case AlertDescription.unknown_psk_identity /* 115 */:
                            if (StringUtil.equalEncodings(csName, CS_US_ASCII)) {
                                return CS_US_ASCII;
                            }
                            return csName;
                        case 'T':
                        case 't':
                            if (StringUtil.equalEncodings(csName, "UTF-8")) {
                                return "UTF-8";
                            }
                            if (StringUtil.equalEncodings(csName, CS_UTF16BE)) {
                                return CS_UTF16BE;
                            }
                            if (StringUtil.equalEncodings(csName, CS_UTF16LE)) {
                                return CS_UTF16LE;
                            }
                            if (StringUtil.equalEncodings(csName, CS_UTF16)) {
                                return CS_UTF16;
                            }
                            if (StringUtil.equalEncodings(csName, CS_UTF32BE)) {
                                return CS_UTF32BE;
                            }
                            if (StringUtil.equalEncodings(csName, CS_UTF32LE)) {
                                return CS_UTF32LE;
                            }
                            if (StringUtil.equalEncodings(csName, CS_UTF32)) {
                                return CS_UTF32;
                            }
                            if (StringUtil.equalEncodings(csName, "UTF")) {
                                return CS_UTF16;
                            }
                            return csName;
                        default:
                            return csName;
                    }
                }
                return csName;
        }
    }

    public static String findEncodingFor(Writer w) {
        if (w instanceof OutputStreamWriter) {
            String enc = ((OutputStreamWriter) w).getEncoding();
            return normalize(enc);
        }
        return null;
    }
}
