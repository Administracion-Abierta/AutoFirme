package org.apache.jcp.xml.dsig.internal.dom;

import javax.xml.crypto.Data;
import org.apache.xml.security.signature.XMLSignatureInput;

/* JADX INFO: loaded from: autofirma.jar:org/apache/jcp/xml/dsig/internal/dom/ApacheData.class */
public interface ApacheData extends Data {
    XMLSignatureInput getXMLSignatureInput();
}
