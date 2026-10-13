package org.apache.xml.security.transforms.implementations;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.c14n.implementations.Canonicalizer20010315Excl;
import org.apache.xml.security.c14n.implementations.Canonicalizer20010315ExclOmitComments;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.transforms.TransformSpi;
import org.apache.xml.security.transforms.params.InclusiveNamespaces;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/transforms/implementations/TransformC14NExclusive.class */
public class TransformC14NExclusive extends TransformSpi {
    @Override // org.apache.xml.security.transforms.TransformSpi
    protected String engineGetURI() {
        return "http://www.w3.org/2001/10/xml-exc-c14n#";
    }

    @Override // org.apache.xml.security.transforms.TransformSpi
    protected XMLSignatureInput enginePerformTransform(XMLSignatureInput input, OutputStream os, Element transformElement, String baseURI, boolean secureValidation) throws CanonicalizationException {
        try {
            String inclusiveNamespaces = null;
            if (length(transformElement, "http://www.w3.org/2001/10/xml-exc-c14n#", InclusiveNamespaces._TAG_EC_INCLUSIVENAMESPACES) == 1) {
                Element inclusiveElement = XMLUtils.selectNode(transformElement.getFirstChild(), "http://www.w3.org/2001/10/xml-exc-c14n#", InclusiveNamespaces._TAG_EC_INCLUSIVENAMESPACES, 0);
                inclusiveNamespaces = new InclusiveNamespaces(inclusiveElement, baseURI).getInclusiveNamespaces();
            }
            Canonicalizer20010315Excl c14n = getCanonicalizer();
            if (os == null && (input.isOctetStream() || input.isElement() || input.isNodeSet())) {
                try {
                    ByteArrayOutputStream writer = new ByteArrayOutputStream();
                    Throwable th = null;
                    try {
                        try {
                            c14n.engineCanonicalize(input, inclusiveNamespaces, writer, secureValidation);
                            writer.flush();
                            XMLSignatureInput output = new XMLSignatureInput(writer.toByteArray());
                            output.setSecureValidation(secureValidation);
                            if (0 != 0) {
                                try {
                                    writer.close();
                                } catch (Throwable th2) {
                                    th.addSuppressed(th2);
                                }
                            } else {
                                writer.close();
                            }
                            return output;
                        } catch (Throwable th3) {
                            th = th3;
                            throw th3;
                        }
                    } catch (Throwable th4) {
                        if (th != null) {
                            try {
                                writer.close();
                            } catch (Throwable th5) {
                                th.addSuppressed(th5);
                            }
                        } else {
                            writer.close();
                        }
                        throw th4;
                    }
                } catch (IOException ex) {
                    throw new CanonicalizationException("empty", new Object[]{ex.getMessage()});
                }
            }
            c14n.engineCanonicalize(input, inclusiveNamespaces, os, secureValidation);
            XMLSignatureInput output2 = new XMLSignatureInput((byte[]) null);
            output2.setSecureValidation(secureValidation);
            output2.setOutputStream(os);
            return output2;
        } catch (XMLSecurityException ex2) {
            throw new CanonicalizationException(ex2);
        }
    }

    protected Canonicalizer20010315Excl getCanonicalizer() {
        return new Canonicalizer20010315ExclOmitComments();
    }

    private int length(Element element, String namespace, String localname) {
        int number = 0;
        Node firstChild = element.getFirstChild();
        while (true) {
            Node sibling = firstChild;
            if (sibling != null) {
                if (localname.equals(sibling.getLocalName()) && namespace.equals(sibling.getNamespaceURI())) {
                    number++;
                }
                firstChild = sibling.getNextSibling();
            } else {
                return number;
            }
        }
    }
}
