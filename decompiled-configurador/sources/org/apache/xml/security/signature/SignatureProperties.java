package org.apache.xml.security.signature;

import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.SignatureElementProxy;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/signature/SignatureProperties.class */
public class SignatureProperties extends SignatureElementProxy {
    public SignatureProperties(Document doc) {
        super(doc);
        addReturnToSelf();
    }

    public SignatureProperties(Element element, String baseURI) throws XMLSecurityException {
        super(element, baseURI);
        Attr attr = element.getAttributeNodeNS(null, "Id");
        if (attr != null) {
            element.setIdAttributeNode(attr, true);
        }
        Element[] propertyElems = XMLUtils.selectDsNodes(getFirstChild(), Constants._TAG_SIGNATUREPROPERTY);
        for (Element propertyElem : propertyElems) {
            Attr propertyAttr = propertyElem.getAttributeNodeNS(null, "Id");
            if (propertyAttr != null) {
                propertyElem.setIdAttributeNode(propertyAttr, true);
            }
        }
    }

    public int getLength() {
        Element[] propertyElems = XMLUtils.selectDsNodes(getFirstChild(), Constants._TAG_SIGNATUREPROPERTY);
        return propertyElems.length;
    }

    public SignatureProperty item(int i) throws XMLSignatureException {
        try {
            Element propertyElem = XMLUtils.selectDsNode(getFirstChild(), Constants._TAG_SIGNATUREPROPERTY, i);
            if (propertyElem == null) {
                return null;
            }
            return new SignatureProperty(propertyElem, this.baseURI);
        } catch (XMLSecurityException ex) {
            throw new XMLSignatureException(ex);
        }
    }

    public void setId(String Id) {
        if (Id != null) {
            setLocalIdAttribute("Id", Id);
        }
    }

    public String getId() {
        return getLocalAttribute("Id");
    }

    public void addSignatureProperty(SignatureProperty sp) {
        appendSelf(sp);
        addReturnToSelf();
    }

    @Override // org.apache.xml.security.utils.ElementProxy
    public String getBaseLocalName() {
        return Constants._TAG_SIGNATUREPROPERTIES;
    }
}
