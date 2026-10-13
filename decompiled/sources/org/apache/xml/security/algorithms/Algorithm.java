package org.apache.xml.security.algorithms;

import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.utils.SignatureElementProxy;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/algorithms/Algorithm.class */
public abstract class Algorithm extends SignatureElementProxy {
    public Algorithm(Document doc, String algorithmURI) {
        super(doc);
        setAlgorithmURI(algorithmURI);
    }

    public Algorithm(Element element, String baseURI) throws XMLSecurityException {
        super(element, baseURI);
    }

    public String getAlgorithmURI() {
        return getLocalAttribute("Algorithm");
    }

    protected void setAlgorithmURI(String algorithmURI) {
        if (algorithmURI != null) {
            setLocalAttribute("Algorithm", algorithmURI);
        }
    }
}
