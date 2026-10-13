package org.apache.xml.security.encryption;

import java.util.Iterator;
import org.w3c.dom.Element;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/encryption/EncryptionProperty.class */
public interface EncryptionProperty {
    String getTarget();

    void setTarget(String str);

    String getId();

    void setId(String str);

    String getAttribute(String str);

    void setAttribute(String str, String str2);

    Iterator<Element> getEncryptionInformation();

    void addEncryptionInformation(Element element);

    void removeEncryptionInformation(Element element);
}
