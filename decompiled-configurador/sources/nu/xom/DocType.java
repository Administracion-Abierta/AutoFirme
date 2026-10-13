package nu.xom;

import org.spongycastle.asn1.eac.EACTags;
import org.spongycastle.crypto.tls.AlertDescription;
import org.spongycastle.crypto.tls.CipherSuite;
import org.spongycastle.crypto.tls.ExtensionType;
import org.spongycastle.pqc.math.linearalgebra.Matrix;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:nu/xom/DocType.class */
public class DocType extends Node {
    private String rootName;
    private String systemID;
    private String publicID;
    private String internalDTDSubset;

    public DocType(String str, String str2, String str3) {
        this.internalDTDSubset = "";
        _setRootElementName(str);
        _setSystemID(str3);
        _setPublicID(str2);
    }

    public DocType(String str, String str2) {
        this(str, null, str2);
    }

    public DocType(String str) {
        this(str, null, null);
    }

    public DocType(DocType docType) {
        this.internalDTDSubset = "";
        this.internalDTDSubset = docType.internalDTDSubset;
        this.publicID = docType.publicID;
        this.systemID = docType.systemID;
        this.rootName = docType.rootName;
    }

    private DocType() {
        this.internalDTDSubset = "";
    }

    static DocType build(String str, String str2, String str3) {
        DocType docType = new DocType();
        docType.publicID = str2;
        docType.systemID = str3;
        docType.rootName = str;
        return docType;
    }

    public final String getRootElementName() {
        return this.rootName;
    }

    public void setRootElementName(String str) {
        _setRootElementName(str);
    }

    private void _setRootElementName(String str) {
        Verifier.checkXMLName(str);
        this.rootName = str;
    }

    public final String getInternalDTDSubset() {
        return this.internalDTDSubset;
    }

    public final void setInternalDTDSubset(String str) {
        if (str == null || str.length() <= 0) {
            this.internalDTDSubset = "";
        } else {
            Verifier.checkInternalDTDSubset(str);
            fastSetInternalDTDSubset(str);
        }
    }

    final void fastSetInternalDTDSubset(String str) {
        this.internalDTDSubset = str;
    }

    public final String getPublicID() {
        return this.publicID;
    }

    public void setPublicID(String str) {
        _setPublicID(str);
    }

    private void _setPublicID(String str) {
        int length;
        if (this.systemID == null && str != null) {
            throw new WellformednessException("Cannot have a public ID without a system ID");
        }
        if (str != null && (length = str.length()) != 0) {
            if (Verifier.isXMLSpaceCharacter(str.charAt(0))) {
                throw new IllegalDataException("Initial white space in public IDs is not round trippable.");
            }
            if (Verifier.isXMLSpaceCharacter(str.charAt(length - 1))) {
                throw new IllegalDataException("Trailing white space in public IDs is not round trippable.");
            }
            for (int i = 0; i < length; i++) {
                char cCharAt = str.charAt(i);
                if (!isXMLPublicIDCharacter(cCharAt)) {
                    throw new IllegalDataException(new StringBuffer().append("The character 0x").append(Integer.toHexString(cCharAt)).append(" is not allowed in public IDs").toString());
                }
                if (cCharAt == ' ' && str.charAt(i - 1) == ' ') {
                    throw new IllegalDataException("Multiple consecutive spaces in public IDs are not round trippable.");
                }
            }
        }
        this.publicID = str;
    }

    public final String getSystemID() {
        return this.systemID;
    }

    public void setSystemID(String str) {
        _setSystemID(str);
    }

    private void _setSystemID(String str) {
        if (str == null && this.publicID != null) {
            throw new WellformednessException("Cannot remove system ID without removing public ID first");
        }
        if (str != null) {
            Verifier.checkURIReference(str);
            if (str.indexOf(35) != -1) {
                MalformedURIException malformedURIException = new MalformedURIException("System literals cannot contain fragment identifiers");
                malformedURIException.setData(str);
                throw malformedURIException;
            }
        }
        this.systemID = str;
    }

    @Override // nu.xom.Node
    public final String getValue() {
        return "";
    }

    @Override // nu.xom.Node
    public final Node getChild(int i) {
        throw new IndexOutOfBoundsException("Document type declarations do not have children");
    }

    @Override // nu.xom.Node
    public final int getChildCount() {
        return 0;
    }

    public final String toString() {
        return new StringBuffer().append("[").append(getClass().getName()).append(": ").append(this.rootName).append("]").toString();
    }

    @Override // nu.xom.Node
    public Node copy() {
        return new DocType(this);
    }

    @Override // nu.xom.Node
    public final String toXML() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("<!DOCTYPE ");
        stringBuffer.append(this.rootName);
        if (this.publicID != null) {
            stringBuffer.append(" PUBLIC \"");
            stringBuffer.append(this.publicID);
            stringBuffer.append("\" \"");
            stringBuffer.append(this.systemID);
            stringBuffer.append('\"');
        } else if (this.systemID != null) {
            stringBuffer.append(" SYSTEM \"");
            stringBuffer.append(this.systemID);
            stringBuffer.append('\"');
        }
        if (this.internalDTDSubset.length() != 0) {
            stringBuffer.append(" [\n");
            stringBuffer.append(this.internalDTDSubset);
            stringBuffer.append(']');
        }
        stringBuffer.append(">");
        return stringBuffer.toString();
    }

    @Override // nu.xom.Node
    boolean isDocType() {
        return true;
    }

    private static boolean isXMLPublicIDCharacter(char c) {
        switch (c) {
            case ' ':
                return true;
            case '!':
                return true;
            case '\"':
                return false;
            case '#':
                return true;
            case EACTags.APPLICATION_EXPIRATION_DATE /* 36 */:
                return true;
            case EACTags.APPLICATION_EFFECTIVE_DATE /* 37 */:
                return true;
            case EACTags.CARD_EFFECTIVE_DATE /* 38 */:
                return false;
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
            default:
                return false;
        }
    }
}
