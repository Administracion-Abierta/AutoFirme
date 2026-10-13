package org.apache.xml.security.encryption;

import java.io.IOException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/encryption/Serializer.class */
public interface Serializer {
    byte[] serializeToByteArray(Element element) throws Exception;

    byte[] serializeToByteArray(NodeList nodeList) throws Exception;

    Node deserialize(byte[] bArr, Node node) throws XMLEncryptionException, IOException;
}
