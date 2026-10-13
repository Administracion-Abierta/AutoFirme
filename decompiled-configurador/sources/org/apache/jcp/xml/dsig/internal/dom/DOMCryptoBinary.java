package org.apache.jcp.xml.dsig.internal.dom;

import java.math.BigInteger;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dom.DOMCryptoContext;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/jcp/xml/dsig/internal/dom/DOMCryptoBinary.class */
public final class DOMCryptoBinary extends DOMStructure {
    private final BigInteger bigNum;
    private final String value;

    public DOMCryptoBinary(BigInteger bigNum) {
        if (bigNum == null) {
            throw new NullPointerException("bigNum is null");
        }
        this.bigNum = bigNum;
        byte[] bytes = XMLUtils.getBytes(bigNum, bigNum.bitLength());
        this.value = XMLUtils.encodeToString(bytes);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.MarshalException */
    public DOMCryptoBinary(Node cbNode) throws MarshalException {
        this.value = cbNode.getNodeValue();
        try {
            this.bigNum = new BigInteger(1, XMLUtils.decode(((Text) cbNode).getData()));
        } catch (Exception ex) {
            throw new MarshalException(ex);
        }
    }

    public BigInteger getBigNum() {
        return this.bigNum;
    }

    @Override // org.apache.jcp.xml.dsig.internal.dom.DOMStructure
    public void marshal(Node parent, String prefix, DOMCryptoContext context) throws MarshalException {
        parent.appendChild(DOMUtils.getOwnerDocument(parent).createTextNode(this.value));
    }
}
