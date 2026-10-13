package org.apache.xml.security.encryption;

import java.util.Iterator;
import org.w3c.dom.Element;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/encryption/Reference.class */
public interface Reference {
    String getType();

    String getURI();

    void setURI(String str);

    Iterator<Element> getElementRetrievalInformation();

    void addElementRetrievalInformation(Element element);

    void removeElementRetrievalInformation(Element element);
}
