package org.spongycastle.crypto.tls;

import org.spongycastle.asn1.eac.EACTags;
import org.spongycastle.pqc.math.linearalgebra.Matrix;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/tls/AlertDescription.class */
public class AlertDescription {
    public static final short close_notify = 0;
    public static final short unexpected_message = 10;
    public static final short bad_record_mac = 20;
    public static final short decryption_failed = 21;
    public static final short record_overflow = 22;
    public static final short decompression_failure = 30;
    public static final short handshake_failure = 40;
    public static final short no_certificate = 41;
    public static final short bad_certificate = 42;
    public static final short unsupported_certificate = 43;
    public static final short certificate_revoked = 44;
    public static final short certificate_expired = 45;
    public static final short certificate_unknown = 46;
    public static final short illegal_parameter = 47;
    public static final short unknown_ca = 48;
    public static final short access_denied = 49;
    public static final short decode_error = 50;
    public static final short decrypt_error = 51;
    public static final short export_restriction = 60;
    public static final short protocol_version = 70;
    public static final short insufficient_security = 71;
    public static final short internal_error = 80;
    public static final short user_canceled = 90;
    public static final short no_renegotiation = 100;
    public static final short unsupported_extension = 110;
    public static final short certificate_unobtainable = 111;
    public static final short unrecognized_name = 112;
    public static final short bad_certificate_status_response = 113;
    public static final short bad_certificate_hash_value = 114;
    public static final short unknown_psk_identity = 115;
    public static final short inappropriate_fallback = 86;

    public static String getName(short alertDescription) {
        switch (alertDescription) {
            case 0:
                return "close_notify";
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 23:
            case 24:
            case 25:
            case 26:
            case 27:
            case 28:
            case 29:
            case 31:
            case 32:
            case 33:
            case 34:
            case 35:
            case EACTags.APPLICATION_EXPIRATION_DATE /* 36 */:
            case EACTags.APPLICATION_EFFECTIVE_DATE /* 37 */:
            case EACTags.CARD_EFFECTIVE_DATE /* 38 */:
            case EACTags.INTERCHANGE_CONTROL /* 39 */:
            case 52:
            case 53:
            case 54:
            case 55:
            case 56:
            case 57:
            case 58:
            case 59:
            case 61:
            case CipherSuite.TLS_DH_DSS_WITH_AES_128_CBC_SHA256 /* 62 */:
            case 63:
            case 64:
            case 65:
            case 66:
            case 67:
            case 68:
            case 69:
            case 72:
            case 73:
            case EACTags.CERTIFICATION_AUTHORITY_PUBLIC_KEY /* 74 */:
            case EACTags.DEPRECATED /* 75 */:
            case 76:
            case EACTags.INTEGRATED_CIRCUIT_MANUFACTURER_ID /* 77 */:
            case 78:
            case 79:
            case EACTags.ANSWER_TO_RESET /* 81 */:
            case 82:
            case 83:
            case 84:
            case Matrix.MATRIX_TYPE_RANDOM_UT /* 85 */:
            case 87:
            case 88:
            case 89:
            case 91:
            case 92:
            case 93:
            case 94:
            case 95:
            case 96:
            case 97:
            case 98:
            case 99:
            case ExtensionType.negotiated_ff_dhe_groups /* 101 */:
            case 102:
            case 103:
            case 104:
            case CipherSuite.TLS_DH_RSA_WITH_AES_256_CBC_SHA256 /* 105 */:
            case CipherSuite.TLS_DHE_DSS_WITH_AES_256_CBC_SHA256 /* 106 */:
            case CipherSuite.TLS_DHE_RSA_WITH_AES_256_CBC_SHA256 /* 107 */:
            case CipherSuite.TLS_DH_anon_WITH_AES_128_CBC_SHA256 /* 108 */:
            case CipherSuite.TLS_DH_anon_WITH_AES_256_CBC_SHA256 /* 109 */:
            default:
                return "UNKNOWN";
            case 10:
                return "unexpected_message";
            case 20:
                return "bad_record_mac";
            case 21:
                return "decryption_failed";
            case 22:
                return "record_overflow";
            case 30:
                return "decompression_failure";
            case 40:
                return "handshake_failure";
            case 41:
                return "no_certificate";
            case 42:
                return "bad_certificate";
            case 43:
                return "unsupported_certificate";
            case 44:
                return "certificate_revoked";
            case 45:
                return "certificate_expired";
            case 46:
                return "certificate_unknown";
            case 47:
                return "illegal_parameter";
            case 48:
                return "unknown_ca";
            case 49:
                return "access_denied";
            case 50:
                return "decode_error";
            case 51:
                return "decrypt_error";
            case 60:
                return "export_restriction";
            case 70:
                return "protocol_version";
            case 71:
                return "insufficient_security";
            case 80:
                return "internal_error";
            case inappropriate_fallback /* 86 */:
                return "inappropriate_fallback";
            case 90:
                return "user_canceled";
            case no_renegotiation /* 100 */:
                return "no_renegotiation";
            case unsupported_extension /* 110 */:
                return "unsupported_extension";
            case certificate_unobtainable /* 111 */:
                return "certificate_unobtainable";
            case unrecognized_name /* 112 */:
                return "unrecognized_name";
            case bad_certificate_status_response /* 113 */:
                return "bad_certificate_status_response";
            case bad_certificate_hash_value /* 114 */:
                return "bad_certificate_hash_value";
            case unknown_psk_identity /* 115 */:
                return "unknown_psk_identity";
        }
    }

    public static String getText(short alertDescription) {
        return getName(alertDescription) + "(" + ((int) alertDescription) + ")";
    }
}
