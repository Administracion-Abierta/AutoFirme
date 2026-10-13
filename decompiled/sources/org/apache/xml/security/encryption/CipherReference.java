package org.apache.xml.security.encryption;

import org.w3c.dom.Attr;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/encryption/CipherReference.class */
public interface CipherReference {
    String getURI();

    Attr getURIAsAttr();

    Transforms getTransforms();

    void setTransforms(Transforms transforms);
}
