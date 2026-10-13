package org.apache.xml.security.transforms.implementations;

import java.io.OutputStream;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.transforms.TransformSpi;
import org.apache.xml.security.transforms.TransformationException;
import org.apache.xml.security.utils.JavaUtils;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/transforms/implementations/TransformBase64Decode.class */
public class TransformBase64Decode extends TransformSpi {
    @Override // org.apache.xml.security.transforms.TransformSpi
    protected String engineGetURI() {
        return "http://www.w3.org/2000/09/xmldsig#base64";
    }

    @Override // org.apache.xml.security.transforms.TransformSpi
    protected XMLSignatureInput enginePerformTransform(XMLSignatureInput input, OutputStream os, Element transformElement, String baseURI, boolean secureValidation) throws Exception {
        if (input.isElement()) {
            Node el = input.getSubNode();
            if (input.getSubNode().getNodeType() == 3) {
                el = el.getParentNode();
            }
            StringBuilder sb = new StringBuilder();
            traverseElement((Element) el, sb);
            if (os == null) {
                byte[] decodedBytes = XMLUtils.decode(sb.toString());
                XMLSignatureInput output = new XMLSignatureInput(decodedBytes);
                output.setSecureValidation(secureValidation);
                return output;
            }
            byte[] bytes = XMLUtils.decode(sb.toString());
            os.write(bytes);
            XMLSignatureInput output2 = new XMLSignatureInput((byte[]) null);
            output2.setSecureValidation(secureValidation);
            output2.setOutputStream(os);
            return output2;
        }
        if (input.isOctetStream() || input.isNodeSet()) {
            if (os == null) {
                byte[] base64Bytes = input.getBytes();
                byte[] decodedBytes2 = XMLUtils.decode(base64Bytes);
                XMLSignatureInput output3 = new XMLSignatureInput(decodedBytes2);
                output3.setSecureValidation(secureValidation);
                return output3;
            }
            if (input.isByteArray() || input.isNodeSet()) {
                byte[] bytes2 = XMLUtils.decode(input.getBytes());
                os.write(bytes2);
            } else {
                byte[] inputBytes = JavaUtils.getBytesFromStream(input.getOctetStreamReal());
                byte[] bytes3 = XMLUtils.decode(inputBytes);
                os.write(bytes3);
            }
            XMLSignatureInput output4 = new XMLSignatureInput((byte[]) null);
            output4.setSecureValidation(secureValidation);
            output4.setOutputStream(os);
            return output4;
        }
        throw new TransformationException("empty", new Object[]{"Unrecognized XMLSignatureInput state"});
    }

    private void traverseElement(Element node, StringBuilder sb) {
        Node firstChild = node.getFirstChild();
        while (true) {
            Node sibling = firstChild;
            if (sibling != null) {
                if (1 == sibling.getNodeType()) {
                    traverseElement((Element) sibling, sb);
                } else if (3 == sibling.getNodeType()) {
                    sb.append(((Text) sibling).getData());
                }
                firstChild = sibling.getNextSibling();
            } else {
                return;
            }
        }
    }
}
