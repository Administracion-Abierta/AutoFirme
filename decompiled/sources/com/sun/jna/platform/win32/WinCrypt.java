package com.sun.jna.platform.win32;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.StringArray;
import com.sun.jna.Structure;
import com.sun.jna.Union;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APITypeMapper;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinCrypt.class */
public interface WinCrypt {
    public static final int CRYPTPROTECT_PROMPT_ON_UNPROTECT = 1;
    public static final int CRYPTPROTECT_PROMPT_ON_PROTECT = 2;
    public static final int CRYPTPROTECT_PROMPT_RESERVED = 4;
    public static final int CRYPTPROTECT_PROMPT_STRONG = 8;
    public static final int CRYPTPROTECT_PROMPT_REQUIRE_STRONG = 16;
    public static final int CRYPTPROTECT_UI_FORBIDDEN = 1;
    public static final int CRYPTPROTECT_LOCAL_MACHINE = 4;
    public static final int CRYPTPROTECT_CRED_SYNC = 8;
    public static final int CRYPTPROTECT_AUDIT = 16;
    public static final int CRYPTPROTECT_NO_RECOVERY = 32;
    public static final int CRYPTPROTECT_VERIFY_PROTECTION = 64;
    public static final int CRYPTPROTECT_CRED_REGENERATE = 128;
    public static final int CRYPT_E_ASN1_ERROR = -2146881280;
    public static final int CRYPT_E_ASN1_INTERNAL = -2146881279;
    public static final int CRYPT_E_ASN1_EOD = -2146881278;
    public static final int CRYPT_E_ASN1_CORRUPT = -2146881277;
    public static final int CRYPT_E_ASN1_LARGE = -2146881276;
    public static final int CRYPT_E_ASN1_CONSTRAINT = -2146881275;
    public static final int CRYPT_E_ASN1_MEMORY = -2146881274;
    public static final int CRYPT_E_ASN1_OVERFLOW = -2146881273;
    public static final int CRYPT_E_ASN1_BADPDU = -2146881272;
    public static final int CRYPT_E_ASN1_BADARGS = -2146881271;
    public static final int CRYPT_E_ASN1_BADREAL = -2146881270;
    public static final int CRYPT_E_ASN1_BADTAG = -2146881269;
    public static final int CRYPT_E_ASN1_CHOICE = -2146881268;
    public static final int CRYPT_E_ASN1_RULE = -2146881267;
    public static final int CRYPT_E_ASN1_UTF8 = -2146881266;
    public static final int CRYPT_E_ASN1_PDU_TYPE = -2146881229;
    public static final int CRYPT_E_ASN1_NYI = -2146881228;
    public static final int CRYPT_E_ASN1_EXTENDED = -2146881023;
    public static final int CRYPT_E_ASN1_NOEOD = -2146881022;
    public static final int CRYPT_ASN_ENCODING = 1;
    public static final int CRYPT_NDR_ENCODING = 2;
    public static final int X509_ASN_ENCODING = 1;
    public static final int X509_NDR_ENCODING = 2;
    public static final int PKCS_7_ASN_ENCODING = 65536;
    public static final int PKCS_7_NDR_ENCODING = 131072;
    public static final int USAGE_MATCH_TYPE_AND = 0;
    public static final int USAGE_MATCH_TYPE_OR = 1;
    public static final int PP_CLIENT_HWND = 1;
    public static final int CERT_SIMPLE_NAME_STR = 1;
    public static final int CERT_OID_NAME_STR = 2;
    public static final int CERT_X500_NAME_STR = 3;
    public static final int CERT_XML_NAME_STR = 4;
    public static final int CERT_CHAIN_POLICY_BASE = 1;
    public static final String szOID_RSA_SHA1RSA = "1.2.840.113549.1.1.5";
    public static final HCERTCHAINENGINE HCCE_CURRENT_USER = new HCERTCHAINENGINE(Pointer.createConstant(0));
    public static final HCERTCHAINENGINE HCCE_LOCAL_MACHINE = new HCERTCHAINENGINE(Pointer.createConstant(1));
    public static final HCERTCHAINENGINE HCCE_SERIAL_LOCAL_MACHINE = new HCERTCHAINENGINE(Pointer.createConstant(2));
    public static final int CERT_COMPARE_SHIFT = 16;
    public static final int CERT_COMPARE_NAME_STR_W = 8;
    public static final int CERT_INFO_SUBJECT_FLAG = 7;
    public static final int CERT_FIND_SUBJECT_STR_W = 524295;
    public static final int CERT_FIND_SUBJECT_STR = 524295;
    public static final int CRYPT_EXPORTABLE = 1;
    public static final int CRYPT_USER_PROTECTED = 2;
    public static final int CRYPT_MACHINE_KEYSET = 32;
    public static final int CRYPT_USER_KEYSET = 4096;
    public static final int PKCS12_PREFER_CNG_KSP = 256;
    public static final int PKCS12_ALWAYS_CNG_KSP = 512;
    public static final int PKCS12_ALLOW_OVERWRITE_KEY = 16384;
    public static final int PKCS12_NO_PERSIST_KEY = 32768;
    public static final int PKCS12_INCLUDE_EXTENDED_PROPERTIES = 16;
    public static final int CERT_CLOSE_STORE_FORCE_FLAG = 1;
    public static final int CERT_CLOSE_STORE_CHECK_FLAG = 2;
    public static final int CERT_QUERY_CONTENT_CERT = 1;
    public static final int CERT_QUERY_CONTENT_CTL = 2;
    public static final int CERT_QUERY_CONTENT_CRL = 3;
    public static final int CERT_QUERY_CONTENT_SERIALIZED_STORE = 4;
    public static final int CERT_QUERY_CONTENT_SERIALIZED_CERT = 5;
    public static final int CERT_QUERY_CONTENT_SERIALIZED_CTL = 6;
    public static final int CERT_QUERY_CONTENT_SERIALIZED_CRL = 7;
    public static final int CERT_QUERY_CONTENT_PKCS7_SIGNED = 8;
    public static final int CERT_QUERY_CONTENT_PKCS7_UNSIGNED = 9;
    public static final int CERT_QUERY_CONTENT_PKCS7_SIGNED_EMBED = 10;
    public static final int CERT_QUERY_CONTENT_PKCS10 = 11;
    public static final int CERT_QUERY_CONTENT_PFX = 12;
    public static final int CERT_QUERY_CONTENT_CERT_PAIR = 13;
    public static final int CERT_QUERY_CONTENT_PFX_AND_LOAD = 14;
    public static final int CERT_QUERY_CONTENT_FLAG_CERT = 2;
    public static final int CERT_QUERY_CONTENT_FLAG_CTL = 4;
    public static final int CERT_QUERY_CONTENT_FLAG_CRL = 8;
    public static final int CERT_QUERY_CONTENT_FLAG_SERIALIZED_STORE = 16;
    public static final int CERT_QUERY_CONTENT_FLAG_SERIALIZED_CERT = 32;
    public static final int CERT_QUERY_CONTENT_FLAG_SERIALIZED_CTL = 64;
    public static final int CERT_QUERY_CONTENT_FLAG_SERIALIZED_CRL = 128;
    public static final int CERT_QUERY_CONTENT_FLAG_PKCS7_SIGNED = 256;
    public static final int CERT_QUERY_CONTENT_FLAG_PKCS7_UNSIGNED = 512;
    public static final int CERT_QUERY_CONTENT_FLAG_PKCS7_SIGNED_EMBED = 1024;
    public static final int CERT_QUERY_CONTENT_FLAG_PKCS10 = 2048;
    public static final int CERT_QUERY_CONTENT_FLAG_PFX = 4096;
    public static final int CERT_QUERY_CONTENT_FLAG_CERT_PAIR = 8192;
    public static final int CERT_QUERY_CONTENT_FLAG_PFX_AND_LOAD = 16384;
    public static final int CERT_QUERY_CONTENT_FLAG_ALL = 16382;
    public static final int CERT_QUERY_FORMAT_BINARY = 1;
    public static final int CERT_QUERY_FORMAT_BASE64_ENCODED = 2;
    public static final int CERT_QUERY_FORMAT_ASN_ASCII_HEX_ENCODED = 3;
    public static final int CERT_QUERY_FORMAT_FLAG_BINARY = 2;
    public static final int CERT_QUERY_FORMAT_FLAG_BASE64_ENCODED = 4;
    public static final int CERT_QUERY_FORMAT_FLAG_ASN_ASCII_HEX_ENCODED = 8;
    public static final int CERT_QUERY_FORMAT_FLAG_ALL = 14;
    public static final int CERT_QUERY_OBJECT_FILE = 1;
    public static final int CERT_QUERY_OBJECT_BLOB = 2;

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinCrypt$CERT_CHAIN_POLICY_PARA.class */
    @Structure.FieldOrder({"cbSize", "dwFlags", "pvExtraPolicyPara"})
    public static class CERT_CHAIN_POLICY_PARA extends Structure {
        public int cbSize;
        public int dwFlags;
        public Pointer pvExtraPolicyPara;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinCrypt$CERT_CHAIN_POLICY_PARA$ByReference.class */
        public static class ByReference extends CERT_CHAIN_POLICY_PARA implements Structure.ByReference {
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinCrypt$CERT_CHAIN_POLICY_STATUS.class */
    @Structure.FieldOrder({"cbSize", "dwError", "lChainIndex", "lElementIndex", "pvExtraPolicyStatus"})
    public static class CERT_CHAIN_POLICY_STATUS extends Structure {
        public int cbSize;
        public int dwError;
        public int lChainIndex;
        public int lElementIndex;
        public Pointer pvExtraPolicyStatus;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinCrypt$CERT_CHAIN_POLICY_STATUS$ByReference.class */
        public static class ByReference extends CERT_CHAIN_POLICY_STATUS implements Structure.ByReference {
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinCrypt$CERT_CONTEXT.class */
    @Structure.FieldOrder({"dwCertEncodingType", "pbCertEncoded", "cbCertEncoded", "pCertInfo", "hCertStore"})
    public static class CERT_CONTEXT extends Structure {
        public int dwCertEncodingType;
        public Pointer pbCertEncoded;
        public int cbCertEncoded;
        public CERT_INFO.ByReference pCertInfo;
        public HCERTSTORE hCertStore;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinCrypt$CERT_CONTEXT$ByReference.class */
        public static class ByReference extends CERT_CONTEXT implements Structure.ByReference {
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinCrypt$CERT_PUBLIC_KEY_INFO.class */
    @Structure.FieldOrder({"Algorithm", "PublicKey"})
    public static class CERT_PUBLIC_KEY_INFO extends Structure {
        public CRYPT_ALGORITHM_IDENTIFIER Algorithm;
        public CRYPT_BIT_BLOB PublicKey;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinCrypt$CERT_PUBLIC_KEY_INFO$ByReference.class */
        public static class ByReference extends CERT_PUBLIC_KEY_INFO implements Structure.ByReference {
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinCrypt$CERT_TRUST_LIST_INFO.class */
    @Structure.FieldOrder({"cbSize", "pCtlEntry", "pCtlContext"})
    public static class CERT_TRUST_LIST_INFO extends Structure {
        public int cbSize;
        public CTL_ENTRY.ByReference pCtlEntry;
        public CTL_CONTEXT.ByReference pCtlContext;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinCrypt$CERT_TRUST_LIST_INFO$ByReference.class */
        public static class ByReference extends CERT_TRUST_LIST_INFO implements Structure.ByReference {
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinCrypt$CERT_TRUST_STATUS.class */
    @Structure.FieldOrder({"dwErrorStatus", "dwInfoStatus"})
    public static class CERT_TRUST_STATUS extends Structure {
        public int dwErrorStatus;
        public int dwInfoStatus;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinCrypt$CERT_TRUST_STATUS$ByReference.class */
        public static class ByReference extends CERT_TRUST_STATUS implements Structure.ByReference {
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinCrypt$CERT_USAGE_MATCH.class */
    @Structure.FieldOrder({"dwType", "Usage"})
    public static class CERT_USAGE_MATCH extends Structure {
        public int dwType;
        public CTL_USAGE Usage;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinCrypt$CERT_USAGE_MATCH$ByReference.class */
        public static class ByReference extends CERT_USAGE_MATCH implements Structure.ByReference {
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinCrypt$CRL_CONTEXT.class */
    @Structure.FieldOrder({"dwCertEncodingType", "pbCrlEncoded", "cbCrlEncoded", "pCrlInfo", "hCertStore"})
    public static class CRL_CONTEXT extends Structure {
        public int dwCertEncodingType;
        public Pointer pbCrlEncoded;
        public int cbCrlEncoded;
        public CRL_INFO.ByReference pCrlInfo;
        public HCERTSTORE hCertStore;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinCrypt$CRL_CONTEXT$ByReference.class */
        public static class ByReference extends CRL_CONTEXT implements Structure.ByReference {
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinCrypt$CRYPT_BIT_BLOB.class */
    @Structure.FieldOrder({"cbData", "pbData", "cUnusedBits"})
    public static class CRYPT_BIT_BLOB extends Structure {
        public int cbData;
        public Pointer pbData;
        public int cUnusedBits;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinCrypt$CRYPT_BIT_BLOB$ByReference.class */
        public static class ByReference extends CRYPT_BIT_BLOB implements Structure.ByReference {
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinCrypt$CRYPT_KEY_PROV_PARAM.class */
    @Structure.FieldOrder({"dwParam", "pbData", "cbData", "dwFlags"})
    public static class CRYPT_KEY_PROV_PARAM extends Structure {
        public int dwParam;
        public Pointer pbData;
        public int cbData;
        public int dwFlags;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinCrypt$CRYPT_KEY_PROV_PARAM$ByReference.class */
        public static class ByReference extends CRYPT_KEY_PROV_PARAM implements Structure.ByReference {
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinCrypt$CTL_CONTEXT.class */
    @Structure.FieldOrder({"dwMsgAndCertEncodingType", "pbCtlEncoded", "cbCtlEncoded", "pCtlInfo", "hCertStore", "hCryptMsg", "pbCtlContent", "cbCtlContent"})
    public static class CTL_CONTEXT extends Structure {
        public int dwMsgAndCertEncodingType;
        public Pointer pbCtlEncoded;
        public int cbCtlEncoded;
        public CTL_INFO.ByReference pCtlInfo;
        public HCERTSTORE hCertStore;
        public HCRYPTMSG hCryptMsg;
        public Pointer pbCtlContent;
        public int cbCtlContent;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinCrypt$CTL_CONTEXT$ByReference.class */
        public static class ByReference extends CTL_CONTEXT implements Structure.ByReference {
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinCrypt$CryptGetSignerCertificateCallback.class */
    public interface CryptGetSignerCertificateCallback extends StdCallLibrary.StdCallCallback {
        CERT_CONTEXT.ByReference callback(Pointer pointer, int i, CERT_INFO cert_info, HCERTSTORE hcertstore);
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinCrypt$DATA_BLOB.class */
    @Structure.FieldOrder({"cbData", "pbData"})
    public static class DATA_BLOB extends Structure {
        public int cbData;
        public Pointer pbData;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinCrypt$DATA_BLOB$ByReference.class */
        public static class ByReference extends DATA_BLOB implements Structure.ByReference {
        }

        public DATA_BLOB() {
        }

        public DATA_BLOB(Pointer memory) {
            super(memory);
            read();
        }

        public DATA_BLOB(byte[] data) {
            if (data.length > 0) {
                this.pbData = new Memory(data.length);
                this.pbData.write(0L, data, 0, data.length);
                this.cbData = data.length;
            } else {
                this.pbData = new Memory(1L);
                this.cbData = 0;
            }
        }

        public DATA_BLOB(String s) {
            this(Native.toByteArray(s));
        }

        public byte[] getData() {
            if (this.pbData == null) {
                return null;
            }
            return this.pbData.getByteArray(0L, this.cbData);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinCrypt$CTL_ENTRY.class */
    @Structure.FieldOrder({"SubjectIdentifier", "cAttribute", "rgAttribute"})
    public static class CTL_ENTRY extends Structure {
        public DATA_BLOB SubjectIdentifier;
        public int cAttribute;
        public Pointer rgAttribute;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinCrypt$CTL_ENTRY$ByReference.class */
        public static class ByReference extends CTL_ENTRY implements Structure.ByReference {
        }

        public CRYPT_ATTRIBUTE[] getRgAttribute() {
            if (this.cAttribute == 0) {
                return new CRYPT_ATTRIBUTE[0];
            }
            CRYPT_ATTRIBUTE[] result = (CRYPT_ATTRIBUTE[]) ((CRYPT_ATTRIBUTE) Structure.newInstance(CRYPT_ATTRIBUTE.class, this.rgAttribute)).toArray(this.cAttribute);
            result[0].read();
            return result;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinCrypt$CERT_REVOCATION_CRL_INFO.class */
    @Structure.FieldOrder({"cbSize", "pBaseCRLContext", "pDeltaCRLContext", "pCrlEntry", "fDeltaCrlEntry"})
    public static class CERT_REVOCATION_CRL_INFO extends Structure {
        public int cbSize;
        public CRL_CONTEXT.ByReference pBaseCRLContext;
        public CRL_CONTEXT.ByReference pDeltaCRLContext;
        public CRL_ENTRY.ByReference pCrlEntry;
        public boolean fDeltaCrlEntry;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinCrypt$CERT_REVOCATION_CRL_INFO$ByReference.class */
        public static class ByReference extends CERT_REVOCATION_CRL_INFO implements Structure.ByReference {
        }

        public CERT_REVOCATION_CRL_INFO() {
            super(W32APITypeMapper.DEFAULT);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinCrypt$CERT_REVOCATION_INFO.class */
    @Structure.FieldOrder({"cbSize", "dwRevocationResult", "pszRevocationOid", "pvOidSpecificInfo", "fHasFreshnessTime", "dwFreshnessTime", "pCrlInfo"})
    public static class CERT_REVOCATION_INFO extends Structure {
        public int cbSize;
        public int dwRevocationResult;
        public String pszRevocationOid;
        public Pointer pvOidSpecificInfo;
        public boolean fHasFreshnessTime;
        public int dwFreshnessTime;
        public CERT_REVOCATION_CRL_INFO.ByReference pCrlInfo;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinCrypt$CERT_REVOCATION_INFO$ByReference.class */
        public static class ByReference extends CERT_REVOCATION_INFO implements Structure.ByReference {
        }

        public CERT_REVOCATION_INFO() {
            super(W32APITypeMapper.ASCII);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinCrypt$CERT_CHAIN_ELEMENT.class */
    @Structure.FieldOrder({"cbSize", "pCertContext", "TrustStatus", "pRevocationInfo", "pIssuanceUsage", "pApplicationUsage", "pwszExtendedErrorInfo"})
    public static class CERT_CHAIN_ELEMENT extends Structure {
        public int cbSize;
        public CERT_CONTEXT.ByReference pCertContext;
        public CERT_TRUST_STATUS TrustStatus;
        public CERT_REVOCATION_INFO.ByReference pRevocationInfo;
        public CTL_USAGE.ByReference pIssuanceUsage;
        public CTL_USAGE.ByReference pApplicationUsage;
        public String pwszExtendedErrorInfo;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinCrypt$CERT_CHAIN_ELEMENT$ByReference.class */
        public static class ByReference extends CERT_CHAIN_ELEMENT implements Structure.ByReference {
        }

        public CERT_CHAIN_ELEMENT() {
            super(W32APITypeMapper.UNICODE);
        }

        public CERT_CHAIN_ELEMENT(Pointer p) {
            super(p, 0, W32APITypeMapper.UNICODE);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinCrypt$CTL_INFO.class */
    @Structure.FieldOrder({"dwVersion", "SubjectUsage", "ListIdentifier", "SequenceNumber", "ThisUpdate", "NextUpdate", "SubjectAlgorithm", "cCTLEntry", "rgCTLEntry", "cExtension", "rgExtension"})
    public static class CTL_INFO extends Structure {
        public int dwVersion;
        public CTL_USAGE SubjectUsage;
        public DATA_BLOB ListIdentifier;
        public DATA_BLOB SequenceNumber;
        public WinBase.FILETIME ThisUpdate;
        public WinBase.FILETIME NextUpdate;
        public CRYPT_ALGORITHM_IDENTIFIER SubjectAlgorithm;
        public int cCTLEntry;
        public Pointer rgCTLEntry;
        public int cExtension;
        public Pointer rgExtension;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinCrypt$CTL_INFO$ByReference.class */
        public static class ByReference extends CTL_INFO implements Structure.ByReference {
        }

        public CTL_ENTRY[] getRgCTLEntry() {
            if (this.cCTLEntry == 0) {
                return new CTL_ENTRY[0];
            }
            CTL_ENTRY[] result = (CTL_ENTRY[]) ((CTL_ENTRY) Structure.newInstance(CTL_ENTRY.class, this.rgCTLEntry)).toArray(this.cCTLEntry);
            result[0].read();
            return result;
        }

        public CERT_EXTENSION[] getRgExtension() {
            if (this.cExtension == 0) {
                return new CERT_EXTENSION[0];
            }
            CERT_EXTENSION[] result = (CERT_EXTENSION[]) ((CERT_EXTENSION) Structure.newInstance(CERT_EXTENSION.class, this.rgExtension)).toArray(this.cExtension);
            result[0].read();
            return result;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinCrypt$CTL_USAGE.class */
    @Structure.FieldOrder({"cUsageIdentifier", "rgpszUsageIdentifier"})
    public static class CTL_USAGE extends Structure {
        public int cUsageIdentifier;
        public Pointer rgpszUsageIdentifier;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinCrypt$CTL_USAGE$ByReference.class */
        public static class ByReference extends CTL_USAGE implements Structure.ByReference {
        }

        public String[] getRgpszUsageIdentier() {
            if (this.cUsageIdentifier == 0) {
                return new String[0];
            }
            return this.rgpszUsageIdentifier.getStringArray(0L, this.cUsageIdentifier);
        }

        public void setRgpszUsageIdentier(String[] array) {
            if (array == null || array.length == 0) {
                this.cUsageIdentifier = 0;
                this.rgpszUsageIdentifier = null;
            } else {
                this.cUsageIdentifier = array.length;
                this.rgpszUsageIdentifier = new StringArray(array);
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinCrypt$CERT_CHAIN_PARA.class */
    @Structure.FieldOrder({"cbSize", "RequestedUsage", "RequestedIssuancePolicy", "dwUrlRetrievalTimeout", "fCheckRevocationFreshnessTime", "dwRevocationFreshnessTime", "pftCacheResync", "pStrongSignPara", "dwStrongSignFlags"})
    public static class CERT_CHAIN_PARA extends Structure {
        public int cbSize;
        public CERT_USAGE_MATCH RequestedUsage;
        public CERT_USAGE_MATCH RequestedIssuancePolicy;
        public int dwUrlRetrievalTimeout;
        public boolean fCheckRevocationFreshnessTime;
        public int dwRevocationFreshnessTime;
        public WinBase.FILETIME.ByReference pftCacheResync;
        public CERT_STRONG_SIGN_PARA.ByReference pStrongSignPara;
        public int dwStrongSignFlags;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinCrypt$CERT_CHAIN_PARA$ByReference.class */
        public static class ByReference extends CERT_CHAIN_PARA implements Structure.ByReference {
        }

        public CERT_CHAIN_PARA() {
            super(W32APITypeMapper.DEFAULT);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinCrypt$CERT_STRONG_SIGN_PARA.class */
    @Structure.FieldOrder({"cbSize", "dwInfoChoice", "DUMMYUNIONNAME"})
    public static class CERT_STRONG_SIGN_PARA extends Structure {
        public int cbSize;
        public int dwInfoChoice;
        public DUMMYUNION DUMMYUNIONNAME;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinCrypt$CERT_STRONG_SIGN_PARA$ByReference.class */
        public static class ByReference extends CERT_CHAIN_PARA implements Structure.ByReference {
        }

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinCrypt$CERT_STRONG_SIGN_PARA$DUMMYUNION.class */
        public class DUMMYUNION extends Union {
            Pointer pvInfo;
            CERT_STRONG_SIGN_SERIALIZED_INFO.ByReference pSerializedInfo;
            WTypes.LPSTR pszOID;

            public DUMMYUNION() {
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinCrypt$CERT_STRONG_SIGN_SERIALIZED_INFO.class */
    @Structure.FieldOrder({"dwFlags", "pwszCNGSignHashAlgids", "pwszCNGPubKeyMinBitLengths"})
    public static class CERT_STRONG_SIGN_SERIALIZED_INFO extends Structure {
        public int dwFlags;
        public String pwszCNGSignHashAlgids;
        public String pwszCNGPubKeyMinBitLengths;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinCrypt$CERT_STRONG_SIGN_SERIALIZED_INFO$ByReference.class */
        public static class ByReference extends CERT_CHAIN_PARA implements Structure.ByReference {
        }

        public CERT_STRONG_SIGN_SERIALIZED_INFO() {
            super(W32APITypeMapper.UNICODE);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinCrypt$CERT_SIMPLE_CHAIN.class */
    @Structure.FieldOrder({"cbSize", "TrustStatus", "cElement", "rgpElement", "pTrustListInfo", "fHasRevocationFreshnessTime", "dwRevocationFreshnessTime"})
    public static class CERT_SIMPLE_CHAIN extends Structure {
        public int cbSize;
        public CERT_TRUST_STATUS TrustStatus;
        public int cElement;
        public Pointer rgpElement;
        public CERT_TRUST_LIST_INFO.ByReference pTrustListInfo;
        public boolean fHasRevocationFreshnessTime;
        public int dwRevocationFreshnessTime;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinCrypt$CERT_SIMPLE_CHAIN$ByReference.class */
        public static class ByReference extends CERT_SIMPLE_CHAIN implements Structure.ByReference {
        }

        public CERT_SIMPLE_CHAIN() {
            super(W32APITypeMapper.DEFAULT);
        }

        public CERT_CHAIN_ELEMENT[] getRgpElement() {
            CERT_CHAIN_ELEMENT[] elements = new CERT_CHAIN_ELEMENT[this.cElement];
            for (int i = 0; i < elements.length; i++) {
                elements[i] = (CERT_CHAIN_ELEMENT) Structure.newInstance(CERT_CHAIN_ELEMENT.class, this.rgpElement.getPointer(i * Native.POINTER_SIZE));
                elements[i].read();
            }
            return elements;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinCrypt$CERT_CHAIN_CONTEXT.class */
    @Structure.FieldOrder({"cbSize", "TrustStatus", "cChain", "rgpChain", "cLowerQualityChainContext", "rgpLowerQualityChainContext", "fHasRevocationFreshnessTime", "dwRevocationFreshnessTime", "dwCreateFlags", "ChainId"})
    public static class CERT_CHAIN_CONTEXT extends Structure {
        public int cbSize;
        public CERT_TRUST_STATUS TrustStatus;
        public int cChain;
        public Pointer rgpChain;
        public int cLowerQualityChainContext;
        public Pointer rgpLowerQualityChainContext;
        public boolean fHasRevocationFreshnessTime;
        public int dwRevocationFreshnessTime;
        public int dwCreateFlags;
        public Guid.GUID ChainId;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinCrypt$CERT_CHAIN_CONTEXT$ByReference.class */
        public static class ByReference extends CERT_CHAIN_CONTEXT implements Structure.ByReference {
        }

        public CERT_SIMPLE_CHAIN[] getRgpChain() {
            CERT_SIMPLE_CHAIN[] elements = new CERT_SIMPLE_CHAIN[this.cChain];
            for (int i = 0; i < elements.length; i++) {
                elements[i] = (CERT_SIMPLE_CHAIN) Structure.newInstance(CERT_SIMPLE_CHAIN.class, this.rgpChain.getPointer(i * Native.POINTER_SIZE));
                elements[i].read();
            }
            return elements;
        }

        public CERT_CHAIN_CONTEXT[] getRgpLowerQualityChainContext() {
            CERT_CHAIN_CONTEXT[] elements = new CERT_CHAIN_CONTEXT[this.cLowerQualityChainContext];
            for (int i = 0; i < elements.length; i++) {
                elements[i] = (CERT_CHAIN_CONTEXT) Structure.newInstance(CERT_CHAIN_CONTEXT.class, this.rgpLowerQualityChainContext.getPointer(i * Native.POINTER_SIZE));
                elements[i].read();
            }
            return elements;
        }

        public CERT_CHAIN_CONTEXT() {
            super(W32APITypeMapper.DEFAULT);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinCrypt$CERT_EXTENSION.class */
    @Structure.FieldOrder({"pszObjId", "fCritical", "Value"})
    public static class CERT_EXTENSION extends Structure {
        public String pszObjId;
        public boolean fCritical;
        public DATA_BLOB Value;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinCrypt$CERT_EXTENSION$ByReference.class */
        public static class ByReference extends CERT_EXTENSION implements Structure.ByReference {
        }

        public CERT_EXTENSION() {
            super(W32APITypeMapper.ASCII);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinCrypt$CERT_EXTENSIONS.class */
    @Structure.FieldOrder({"cExtension", "rgExtension"})
    public static class CERT_EXTENSIONS extends Structure {
        public int cExtension;
        public Pointer rgExtension;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinCrypt$CERT_EXTENSIONS$ByReference.class */
        public static class ByReference extends CERT_EXTENSIONS implements Structure.ByReference {
        }

        public CERT_EXTENSION[] getRgExtension() {
            if (this.cExtension == 0) {
                return new CERT_EXTENSION[0];
            }
            CERT_EXTENSION[] ces = (CERT_EXTENSION[]) ((CERT_EXTENSION) Structure.newInstance(CERT_EXTENSION.class, this.rgExtension)).toArray(this.cExtension);
            ces[0].read();
            return ces;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinCrypt$CERT_INFO.class */
    @Structure.FieldOrder({"dwVersion", "SerialNumber", "SignatureAlgorithm", "Issuer", "NotBefore", "NotAfter", "Subject", "SubjectPublicKeyInfo", "IssuerUniqueId", "SubjectUniqueId", "cExtension", "rgExtension"})
    public static class CERT_INFO extends Structure {
        public int dwVersion;
        public DATA_BLOB SerialNumber;
        public CRYPT_ALGORITHM_IDENTIFIER SignatureAlgorithm;
        public DATA_BLOB Issuer;
        public WinBase.FILETIME NotBefore;
        public WinBase.FILETIME NotAfter;
        public DATA_BLOB Subject;
        public CERT_PUBLIC_KEY_INFO SubjectPublicKeyInfo;
        public CRYPT_BIT_BLOB IssuerUniqueId;
        public CRYPT_BIT_BLOB SubjectUniqueId;
        public int cExtension;
        public Pointer rgExtension;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinCrypt$CERT_INFO$ByReference.class */
        public static class ByReference extends CERT_INFO implements Structure.ByReference {
        }

        public CERT_EXTENSION[] getRgExtension() {
            if (this.cExtension == 0) {
                return new CERT_EXTENSION[0];
            }
            CERT_EXTENSION[] ces = (CERT_EXTENSION[]) ((CERT_EXTENSION) Structure.newInstance(CERT_EXTENSION.class, this.rgExtension)).toArray(this.cExtension);
            ces[0].read();
            return ces;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinCrypt$CRL_ENTRY.class */
    @Structure.FieldOrder({"SerialNumber", "RevocationDate", "cExtension", "rgExtension"})
    public static class CRL_ENTRY extends Structure {
        public DATA_BLOB SerialNumber;
        public WinBase.FILETIME RevocationDate;
        public int cExtension;
        public Pointer rgExtension;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinCrypt$CRL_ENTRY$ByReference.class */
        public static class ByReference extends CRL_ENTRY implements Structure.ByReference {
        }

        public CERT_EXTENSION[] getRgExtension() {
            if (this.cExtension == 0) {
                return new CERT_EXTENSION[0];
            }
            CERT_EXTENSION[] result = (CERT_EXTENSION[]) ((CERT_EXTENSION) Structure.newInstance(CERT_EXTENSION.class, this.rgExtension)).toArray(this.cExtension);
            result[0].read();
            return result;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinCrypt$CRL_INFO.class */
    @Structure.FieldOrder({"dwVersion", "SignatureAlgorithm", "Issuer", "ThisUpdate", "NextUpdate", "cCRLEntry", "rgCRLEntry", "cExtension", "rgExtension"})
    public static class CRL_INFO extends Structure {
        public int dwVersion;
        public CRYPT_ALGORITHM_IDENTIFIER SignatureAlgorithm;
        public DATA_BLOB Issuer;
        public WinBase.FILETIME ThisUpdate;
        public WinBase.FILETIME NextUpdate;
        public int cCRLEntry;
        public Pointer rgCRLEntry;
        public int cExtension;
        public Pointer rgExtension;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinCrypt$CRL_INFO$ByReference.class */
        public static class ByReference extends CRL_INFO implements Structure.ByReference {
        }

        public CRL_ENTRY[] getRgCRLEntry() {
            if (this.cCRLEntry == 0) {
                return new CRL_ENTRY[0];
            }
            CRL_ENTRY[] result = (CRL_ENTRY[]) ((CRL_ENTRY) Structure.newInstance(CRL_ENTRY.class, this.rgCRLEntry)).toArray(this.cCRLEntry);
            result[0].read();
            return result;
        }

        public CERT_EXTENSION[] getRgExtension() {
            if (this.cExtension == 0) {
                return new CERT_EXTENSION[0];
            }
            CERT_EXTENSION[] result = (CERT_EXTENSION[]) ((CERT_EXTENSION) Structure.newInstance(CERT_EXTENSION.class, this.rgExtension)).toArray(this.cExtension);
            result[0].read();
            return result;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinCrypt$CRYPT_ALGORITHM_IDENTIFIER.class */
    @Structure.FieldOrder({"pszObjId", "Parameters"})
    public static class CRYPT_ALGORITHM_IDENTIFIER extends Structure {
        public String pszObjId;
        public DATA_BLOB Parameters;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinCrypt$CRYPT_ALGORITHM_IDENTIFIER$ByReference.class */
        public static class ByReference extends CRYPT_ALGORITHM_IDENTIFIER implements Structure.ByReference {
        }

        public CRYPT_ALGORITHM_IDENTIFIER() {
            super(W32APITypeMapper.ASCII);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinCrypt$CRYPT_ATTRIBUTE.class */
    @Structure.FieldOrder({"pszObjId", "cValue", "rgValue"})
    public static class CRYPT_ATTRIBUTE extends Structure {
        public String pszObjId;
        public int cValue;
        public DATA_BLOB.ByReference rgValue;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinCrypt$CRYPT_ATTRIBUTE$ByReference.class */
        public static class ByReference extends CRYPT_ATTRIBUTE implements Structure.ByReference {
        }

        public DATA_BLOB[] getRgValue() {
            return (DATA_BLOB[]) this.rgValue.toArray(this.cValue);
        }

        public CRYPT_ATTRIBUTE() {
            super(W32APITypeMapper.ASCII);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinCrypt$CRYPT_KEY_PROV_INFO.class */
    @Structure.FieldOrder({"pwszContainerName", "pwszProvName", "dwProvType", "dwFlags", "cProvParam", "rgProvParam", "dwKeySpec"})
    public static class CRYPT_KEY_PROV_INFO extends Structure {
        public String pwszContainerName;
        public String pwszProvName;
        public int dwProvType;
        public int dwFlags;
        public int cProvParam;
        public Pointer rgProvParam;
        public int dwKeySpec;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinCrypt$CRYPT_KEY_PROV_INFO$ByReference.class */
        public static class ByReference extends CRYPT_KEY_PROV_INFO implements Structure.ByReference {
        }

        public CRYPT_KEY_PROV_INFO() {
            super(W32APITypeMapper.UNICODE);
        }

        public CRYPT_KEY_PROV_PARAM[] getRgProvParam() {
            CRYPT_KEY_PROV_PARAM[] elements = new CRYPT_KEY_PROV_PARAM[this.cProvParam];
            for (int i = 0; i < elements.length; i++) {
                elements[i] = (CRYPT_KEY_PROV_PARAM) Structure.newInstance(CRYPT_KEY_PROV_PARAM.class, this.rgProvParam.getPointer(i * Native.POINTER_SIZE));
                elements[i].read();
            }
            return elements;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinCrypt$CRYPT_SIGN_MESSAGE_PARA.class */
    @Structure.FieldOrder({"cbSize", "dwMsgEncodingType", "pSigningCert", "HashAlgorithm", "pvHashAuxInfo", "cMsgCert", "rgpMsgCert", "cMsgCrl", "rgpMsgCrl", "cAuthAttr", "rgAuthAttr", "cUnauthAttr", "rgUnauthAttr", "dwFlags", "dwInnerContentType", "HashEncryptionAlgorithm", "pvHashEncryptionAuxInfo"})
    public static class CRYPT_SIGN_MESSAGE_PARA extends Structure {
        public int cbSize;
        public int dwMsgEncodingType;
        public CERT_CONTEXT.ByReference pSigningCert;
        public CRYPT_ALGORITHM_IDENTIFIER HashAlgorithm;
        public Pointer pvHashAuxInfo;
        public int cMsgCert;
        public int cMsgCrl;
        public int cAuthAttr;
        public int cUnauthAttr;
        public int dwFlags;
        public int dwInnerContentType;
        public CRYPT_ALGORITHM_IDENTIFIER HashEncryptionAlgorithm;
        public Pointer pvHashEncryptionAuxInfo;
        public Pointer rgpMsgCert = null;
        public Pointer rgpMsgCrl = null;
        public Pointer rgAuthAttr = null;
        public Pointer rgUnauthAttr = null;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinCrypt$CRYPT_SIGN_MESSAGE_PARA$ByReference.class */
        public static class ByReference extends CRYPT_SIGN_MESSAGE_PARA implements Structure.ByReference {
        }

        public CERT_CONTEXT[] getRgpMsgCert() {
            CERT_CONTEXT[] elements = new CERT_CONTEXT[this.cMsgCrl];
            for (int i = 0; i < elements.length; i++) {
                elements[i] = (CERT_CONTEXT) Structure.newInstance(CERT_CONTEXT.class, this.rgpMsgCert.getPointer(i * Native.POINTER_SIZE));
                elements[i].read();
            }
            return elements;
        }

        public CRL_CONTEXT[] getRgpMsgCrl() {
            CRL_CONTEXT[] elements = new CRL_CONTEXT[this.cMsgCrl];
            for (int i = 0; i < elements.length; i++) {
                elements[i] = (CRL_CONTEXT) Structure.newInstance(CRL_CONTEXT.class, this.rgpMsgCrl.getPointer(i * Native.POINTER_SIZE));
                elements[i].read();
            }
            return elements;
        }

        public CRYPT_ATTRIBUTE[] getRgAuthAttr() {
            if (this.cAuthAttr == 0) {
                return new CRYPT_ATTRIBUTE[0];
            }
            return (CRYPT_ATTRIBUTE[]) ((CRYPT_ATTRIBUTE) Structure.newInstance(CRYPT_ATTRIBUTE.class, this.rgAuthAttr)).toArray(this.cAuthAttr);
        }

        public CRYPT_ATTRIBUTE[] getRgUnauthAttr() {
            if (this.cUnauthAttr == 0) {
                return new CRYPT_ATTRIBUTE[0];
            }
            return (CRYPT_ATTRIBUTE[]) ((CRYPT_ATTRIBUTE) Structure.newInstance(CRYPT_ATTRIBUTE.class, this.rgUnauthAttr)).toArray(this.cUnauthAttr);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinCrypt$CRYPT_VERIFY_MESSAGE_PARA.class */
    @Structure.FieldOrder({"cbSize", "dwMsgAndCertEncodingType", "hCryptProv", "pfnGetSignerCertificate", "pvGetArg", "pStrongSignPara"})
    public static class CRYPT_VERIFY_MESSAGE_PARA extends Structure {
        public int cbSize;
        public int dwMsgAndCertEncodingType;
        public HCRYPTPROV_LEGACY hCryptProv;
        public CryptGetSignerCertificateCallback pfnGetSignerCertificate;
        public Pointer pvGetArg;
        public CERT_STRONG_SIGN_PARA.ByReference pStrongSignPara;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinCrypt$CRYPT_VERIFY_MESSAGE_PARA$ByReference.class */
        public static class ByReference extends CRYPT_SIGN_MESSAGE_PARA implements Structure.ByReference {
        }

        @Override // com.sun.jna.Structure
        public void write() {
            this.cbSize = size();
            super.write();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinCrypt$HCERTCHAINENGINE.class */
    public static class HCERTCHAINENGINE extends WinNT.HANDLE {
        public HCERTCHAINENGINE() {
        }

        public HCERTCHAINENGINE(Pointer p) {
            super(p);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinCrypt$HCERTSTORE.class */
    public static class HCERTSTORE extends WinNT.HANDLE {
        public HCERTSTORE() {
        }

        public HCERTSTORE(Pointer p) {
            super(p);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinCrypt$HCRYPTMSG.class */
    public static class HCRYPTMSG extends WinNT.HANDLE {
        public HCRYPTMSG() {
        }

        public HCRYPTMSG(Pointer p) {
            super(p);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinCrypt$HCRYPTPROV_LEGACY.class */
    public static class HCRYPTPROV_LEGACY extends BaseTSD.ULONG_PTR {
        public HCRYPTPROV_LEGACY() {
        }

        public HCRYPTPROV_LEGACY(long value) {
            super(value);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinCrypt$CRYPTPROTECT_PROMPTSTRUCT.class */
    @Structure.FieldOrder({"cbSize", "dwPromptFlags", "hwndApp", "szPrompt"})
    public static class CRYPTPROTECT_PROMPTSTRUCT extends Structure {
        public int cbSize;
        public int dwPromptFlags;
        public WinDef.HWND hwndApp;
        public String szPrompt;

        public CRYPTPROTECT_PROMPTSTRUCT() {
            super(W32APITypeMapper.DEFAULT);
        }

        public CRYPTPROTECT_PROMPTSTRUCT(Pointer memory) {
            super(memory, 0, W32APITypeMapper.DEFAULT);
            read();
        }
    }
}
