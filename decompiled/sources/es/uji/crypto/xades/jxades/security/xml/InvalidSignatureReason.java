package es.uji.crypto.xades.jxades.security.xml;

import es.uji.crypto.xades.jxades.util.ComparableBean;
import es.uji.crypto.xades.jxades.util.SystemUtils;
import es.uji.crypto.xades.jxades.util.UniversalIndexKey;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureException;
import org.apache.pdfbox.contentstream.operator.OperatorName;

/* JADX INFO: loaded from: autofirma.jar:es/uji/crypto/xades/jxades/security/xml/InvalidSignatureReason.class */
public class InvalidSignatureReason implements ComparableBean {
    private InvalidSignature invalidSignature;
    private String reason;
    private Comparable<UniversalIndexKey> key;

    public InvalidSignatureReason() {
    }

    public InvalidSignatureReason(MarshalException ex) {
        this.invalidSignature = InvalidSignature.WRONG_XML_SIGNATURE;
        this.reason = "Wrong XML signature: " + SystemUtils.getCauseMessages(ex);
    }

    public InvalidSignatureReason(InvalidSignature invalidSignature, ClassCastException ex) {
        this.invalidSignature = invalidSignature;
        if (InvalidSignature.NOT_COMPATIBLE_VALIDATE_CONTEXT.equals(invalidSignature)) {
            this.reason = "Not compatible validate context: " + SystemUtils.getCauseMessages(ex);
        } else {
            this.reason = "Inappropriate XML structure: " + SystemUtils.getCauseMessages(ex);
        }
    }

    public InvalidSignatureReason(String source, NullPointerException ex) {
        this.invalidSignature = InvalidSignature.NULL_VALIDATE_CONTEXT;
        this.reason = "NULL " + source + " validate context: " + SystemUtils.getCauseMessages(ex);
    }

    public InvalidSignatureReason(String source, XMLSignatureException ex) {
        this.invalidSignature = InvalidSignature.UNEXPECTED_EXCEPTION;
        this.reason = "Unexpected exception occurs in " + source + " while validating the signature: " + SystemUtils.getCauseMessages(ex);
    }

    public InvalidSignatureReason(XMLSignature.SignatureValue signatureValue) {
        this.invalidSignature = InvalidSignature.BAD_SIGNATURE_VALUE;
        StringBuilder sb = new StringBuilder();
        sb.append("Bad signature value");
        String id = signatureValue.getId();
        if (id != null) {
            String id2 = id.trim();
            if (id2.length() > 0) {
                sb.append(" with Id '").append(id2).append(OperatorName.SHOW_TEXT_LINE);
            }
        }
        this.reason = sb.toString();
    }

    public InvalidSignatureReason(Reference reference) {
        this.invalidSignature = InvalidSignature.BAD_REFERENCE;
        StringBuilder sb = new StringBuilder();
        sb.append("Bad reference");
        String id = reference.getId();
        if (id != null) {
            String strTrim = id.trim();
            id = strTrim;
            if (strTrim.length() > 0) {
                sb.append(" with Id '").append(id).append(OperatorName.SHOW_TEXT_LINE);
            }
        }
        String uri = reference.getURI();
        if (uri != null) {
            String uri2 = uri.trim();
            if (uri2.length() > 0) {
                if (id != null && id.length() > 0) {
                    sb.append(" and URI = '");
                } else {
                    sb.append(" with URI = '");
                }
                sb.append(uri2).append(OperatorName.SHOW_TEXT_LINE);
            }
        }
        this.reason = sb.toString();
    }

    public InvalidSignature getInvalidSignature() {
        return this.invalidSignature;
    }

    public String getReason() {
        return this.reason;
    }

    @Override // es.uji.crypto.xades.jxades.util.ComparableBean
    public Comparable<UniversalIndexKey> getIndexKey() {
        if (this.key == null) {
            this.key = new UniversalIndexKey(this.invalidSignature.getDescription(), this.reason);
        }
        return this.key;
    }
}
