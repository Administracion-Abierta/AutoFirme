package org.apache.xml.security.encryption;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamSource;
import org.apache.xml.security.c14n.InvalidCanonicalizerException;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Node;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/encryption/TransformSerializer.class */
public class TransformSerializer extends AbstractSerializer {
    private final TransformerFactory transformerFactory;

    public TransformSerializer(boolean secureValidation) throws InvalidCanonicalizerException, TransformerConfigurationException {
        this("http://santuario.apache.org/c14n/physical", secureValidation);
    }

    public TransformSerializer(String canonAlg, boolean secureValidation) throws InvalidCanonicalizerException, TransformerConfigurationException {
        super(canonAlg, secureValidation);
        this.transformerFactory = TransformerFactory.newInstance();
        this.transformerFactory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", Boolean.TRUE.booleanValue());
        if (secureValidation) {
            try {
                this.transformerFactory.setAttribute("http://javax.xml.XMLConstants/property/accessExternalDTD", "");
                this.transformerFactory.setAttribute("http://javax.xml.XMLConstants/property/accessExternalStylesheet", "");
            } catch (IllegalArgumentException e) {
            }
        }
    }

    @Override // org.apache.xml.security.encryption.Serializer
    public Node deserialize(byte[] source, Node ctx) throws XMLEncryptionException, IOException {
        byte[] fragment = createContext(source, ctx);
        InputStream is = new ByteArrayInputStream(fragment);
        Throwable th = null;
        try {
            try {
                Node nodeDeserialize = deserialize(ctx, new StreamSource(is));
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

    private Node deserialize(Node ctx, Source source) throws XMLEncryptionException {
        Document contextDocument;
        try {
            if (9 == ctx.getNodeType()) {
                contextDocument = (Document) ctx;
            } else {
                contextDocument = ctx.getOwnerDocument();
            }
            Transformer transformer = this.transformerFactory.newTransformer();
            DOMResult res = new DOMResult();
            Node placeholder = contextDocument.createDocumentFragment();
            res.setNode(placeholder);
            transformer.transform(source, res);
            Node dummyChild = placeholder.getFirstChild();
            Node child = dummyChild.getFirstChild();
            if (child != null && child.getNextSibling() == null) {
                return child;
            }
            DocumentFragment docfrag = contextDocument.createDocumentFragment();
            while (child != null) {
                dummyChild.removeChild(child);
                docfrag.appendChild(child);
                child = dummyChild.getFirstChild();
            }
            return docfrag;
        } catch (Exception e) {
            throw new XMLEncryptionException(e);
        }
    }
}
