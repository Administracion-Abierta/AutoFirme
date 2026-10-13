package org.apache.xml.security.stax.ext;

import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/stax/ext/DocumentContext.class */
public interface DocumentContext {
    String getEncoding();

    String getBaseURI();

    void setIsInEncryptedContent(int i, Object obj);

    void unsetIsInEncryptedContent(Object obj);

    boolean isInEncryptedContent();

    void setIsInSignedContent(int i, Object obj);

    void unsetIsInSignedContent(Object obj);

    boolean isInSignedContent();

    List<XMLSecurityConstants.ContentType> getProtectionOrder();

    Map<Integer, XMLSecurityConstants.ContentType> getContentTypeMap();
}
