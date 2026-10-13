package org.apache.xml.security.encryption;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.xml.security.c14n.InvalidCanonicalizerException;
import org.apache.xml.security.parser.XMLParserException;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/encryption/DocumentSerializer.class */
public class DocumentSerializer extends AbstractSerializer {
    public DocumentSerializer(boolean secureValidation) throws InvalidCanonicalizerException {
        this("http://santuario.apache.org/c14n/physical", secureValidation);
    }

    public DocumentSerializer(String canonAlg, boolean secureValidation) throws InvalidCanonicalizerException {
        super(canonAlg, secureValidation);
    }

    @Override // org.apache.xml.security.encryption.Serializer
    public Node deserialize(byte[] source, Node ctx) throws XMLEncryptionException, IOException {
        byte[] fragment = createContext(source, ctx);
        InputStream is = new ByteArrayInputStream(fragment);
        Throwable th = null;
        try {
            try {
                Node nodeDeserialize = deserialize(ctx, is);
                if (0 != 0) {
                    try {
                        is.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                } else {
                    is.close();
                }
                return nodeDeserialize;
            } catch (Throwable th3) {
                th = th3;
                throw th3;
            }
        } catch (Throwable th4) {
            if (th != null) {
                try {
                    is.close();
                } catch (Throwable th5) {
                    th.addSuppressed(th5);
                }
            } else {
                is.close();
            }
            throw th4;
        }
    }

    private Node deserialize(Node ctx, InputStream inputStream) throws XMLEncryptionException {
        Document contextDocument;
        try {
            Document d = XMLUtils.read(inputStream, this.secureValidation);
            if (9 == ctx.getNodeType()) {
                contextDocument = (Document) ctx;
            } else {
                contextDocument = ctx.getOwnerDocument();
            }
            Element fragElt = (Element) contextDocument.importNode(d.getDocumentElement(), true);
            DocumentFragment result = contextDocument.createDocumentFragment();
            for (Node child = fragElt.getFirstChild(); child != null; child = fragElt.getFirstChild()) {
                fragElt.removeChild(child);
                result.appendChild(child);
            }
            return result;
        } catch (XMLParserException pce) {
            throw new XMLEncryptionException(pce);
        }
    }
}
