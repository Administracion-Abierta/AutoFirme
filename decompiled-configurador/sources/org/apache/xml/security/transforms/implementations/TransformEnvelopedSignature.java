package org.apache.xml.security.transforms.implementations;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.xml.security.parser.XMLParserException;
import org.apache.xml.security.signature.NodeFilter;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.transforms.TransformSpi;
import org.apache.xml.security.transforms.TransformationException;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/transforms/implementations/TransformEnvelopedSignature.class */
public class TransformEnvelopedSignature extends TransformSpi {
    @Override // org.apache.xml.security.transforms.TransformSpi
    protected String engineGetURI() {
        return "http://www.w3.org/2000/09/xmldsig#enveloped-signature";
    }

    @Override // org.apache.xml.security.transforms.TransformSpi
    protected XMLSignatureInput enginePerformTransform(XMLSignatureInput input, OutputStream os, Element transformElement, String baseURI, boolean secureValidation) throws TransformationException {
        Node signatureElement = searchSignatureElement(transformElement);
        input.setExcludeNode(signatureElement);
        try {
            input.addNodeFilter(new EnvelopedNodeFilter(signatureElement));
            return input;
        } catch (IOException | XMLParserException ex) {
            throw new TransformationException(ex);
        }
    }

    private static Node searchSignatureElement(Node signatureElement) throws TransformationException {
        boolean found = false;
        while (signatureElement != null && signatureElement.getNodeType() != 9) {
            Element el = (Element) signatureElement;
            if (el.getNamespaceURI().equals("http://www.w3.org/2000/09/xmldsig#") && el.getLocalName().equals("Signature")) {
                found = true;
                break;
            }
            signatureElement = signatureElement.getParentNode();
        }
        if (!found) {
            throw new TransformationException("transform.envelopedSignatureTransformNotInSignatureElement");
        }
        return signatureElement;
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/transforms/implementations/TransformEnvelopedSignature$EnvelopedNodeFilter.class */
    static class EnvelopedNodeFilter implements NodeFilter {
        private final Node exclude;

        EnvelopedNodeFilter(Node n) {
            this.exclude = n;
        }

        @Override // org.apache.xml.security.signature.NodeFilter
        public int isNodeIncludeDO(Node n, int level) {
            if (n == this.exclude) {
                return -1;
            }
            return 1;
        }

        @Override // org.apache.xml.security.signature.NodeFilter
        public int isNodeInclude(Node n) {
            if (n == this.exclude || XMLUtils.isDescendantOrSelf(this.exclude, n)) {
                return -1;
            }
            return 1;
        }
    }
}
