package es.uji.crypto.xades.jxades.security.xml;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/uji/crypto/xades/jxades/security/xml/InvalidSignature.class */
public enum InvalidSignature {
    WRONG_XML_SIGNATURE("Wrong XML Signature"),
    INAPPROPRIATE_XML_CONTEXT("Inappropriate XML Context"),
    NOT_COMPATIBLE_VALIDATE_CONTEXT("Not compatible Validate Context"),
    NULL_VALIDATE_CONTEXT("Null/Empty Validate Context"),
    BAD_SIGNATURE_VALUE("Bad Signature Value"),
    BAD_REFERENCE("Bad Reference"),
    UNEXPECTED_EXCEPTION("Unexpected Exception");

    private String description;

    InvalidSignature(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }
}
