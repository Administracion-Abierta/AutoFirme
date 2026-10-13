package es.uji.crypto.xades.jxades.security.xml.XAdES;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/* JADX INFO: loaded from: autofirma.jar:es/uji/crypto/xades/jxades/security/xml/XAdES/ExtendedXAdESImpl.class */
public class ExtendedXAdESImpl extends CompleteValidationXAdESImpl implements XAdES_X {
    public ExtendedXAdESImpl(Document document, Element baseElement, boolean readOnlyMode, String xadesPrefix, String xadesNamespace, String xmlSignaturePrefix, String digestMethod) {
        super(document, baseElement, readOnlyMode, xadesPrefix, xadesNamespace, xmlSignaturePrefix, digestMethod);
    }
}
