package org.apache.jcp.xml.dsig.internal.dom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dom.DOMCryptoContext;
import javax.xml.crypto.dsig.keyinfo.PGPData;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/jcp/xml/dsig/internal/dom/DOMPGPData.class */
public final class DOMPGPData extends DOMStructure implements PGPData {
    private final byte[] keyId;
    private final byte[] keyPacket;
    private final List<XMLStructure> externalElements;

    public DOMPGPData(byte[] keyPacket, List<? extends XMLStructure> other) {
        if (keyPacket == null) {
            throw new NullPointerException("keyPacket cannot be null");
        }
        if (other == null || other.isEmpty()) {
            this.externalElements = Collections.emptyList();
        } else {
            this.externalElements = Collections.unmodifiableList(new ArrayList(other));
            int size = this.externalElements.size();
            for (int i = 0; i < size; i++) {
                if (!(this.externalElements.get(i) instanceof XMLStructure)) {
                    throw new ClassCastException("other[" + i + "] is not a valid PGPData type");
                }
            }
        }
        this.keyPacket = (byte[]) keyPacket.clone();
        checkKeyPacket(keyPacket);
        this.keyId = null;
    }

    public DOMPGPData(byte[] keyId, byte[] keyPacket, List<? extends XMLStructure> other) {
        if (keyId == null) {
            throw new NullPointerException("keyId cannot be null");
        }
        if (keyId.length != 8) {
            throw new IllegalArgumentException("keyId must be 8 bytes long");
        }
        if (other == null || other.isEmpty()) {
            this.externalElements = Collections.emptyList();
        } else {
            this.externalElements = Collections.unmodifiableList(new ArrayList(other));
            int size = this.externalElements.size();
            for (int i = 0; i < size; i++) {
                if (!(this.externalElements.get(i) instanceof XMLStructure)) {
                    throw new ClassCastException("other[" + i + "] is not a valid PGPData type");
                }
            }
        }
        this.keyId = (byte[]) keyId.clone();
        this.keyPacket = keyPacket == null ? null : (byte[]) keyPacket.clone();
        if (keyPacket != null) {
            checkKeyPacket(keyPacket);
        }
    }

    public DOMPGPData(Element pdElem) throws MarshalException {
        byte[] pgpKeyId = null;
        byte[] pgpKeyPacket = null;
        List<XMLStructure> other = new ArrayList<>();
        Node firstChild = pdElem.getFirstChild();
        while (true) {
            Node firstChild2 = firstChild;
            if (firstChild2 != null) {
                if (firstChild2.getNodeType() == 1) {
                    Element childElem = (Element) firstChild2;
                    String localName = childElem.getLocalName();
                    String namespace = childElem.getNamespaceURI();
                    if (Constants._TAG_PGPKEYID.equals(localName) && "http://www.w3.org/2000/09/xmldsig#".equals(namespace)) {
                        String content = XMLUtils.getFullTextChildrenFromNode(childElem);
                        pgpKeyId = XMLUtils.decode(content);
                    } else if (Constants._TAG_PGPKEYPACKET.equals(localName) && "http://www.w3.org/2000/09/xmldsig#".equals(namespace)) {
                        String content2 = XMLUtils.getFullTextChildrenFromNode(childElem);
                        pgpKeyPacket = XMLUtils.decode(content2);
                    } else {
                        other.add(new javax.xml.crypto.dom.DOMStructure(childElem));
                    }
                }
                firstChild = firstChild2.getNextSibling();
            } else {
                this.keyId = pgpKeyId;
                this.keyPacket = pgpKeyPacket;
                this.externalElements = Collections.unmodifiableList(other);
                return;
            }
        }
    }

    public byte[] getKeyId() {
        if (this.keyId == null) {
            return null;
        }
        return (byte[]) this.keyId.clone();
    }

    public byte[] getKeyPacket() {
        if (this.keyPacket == null) {
            return null;
        }
        return (byte[]) this.keyPacket.clone();
    }

    public List<XMLStructure> getExternalElements() {
        return this.externalElements;
    }

    @Override // org.apache.jcp.xml.dsig.internal.dom.DOMStructure
    public void marshal(Node node, String dsPrefix, DOMCryptoContext context) throws MarshalException {
        Document ownerDoc = DOMUtils.getOwnerDocument(node);
        Element pdElem = DOMUtils.createElement(ownerDoc, Constants._TAG_PGPDATA, "http://www.w3.org/2000/09/xmldsig#", dsPrefix);
        if (this.keyId != null) {
            Element keyIdElem = DOMUtils.createElement(ownerDoc, Constants._TAG_PGPKEYID, "http://www.w3.org/2000/09/xmldsig#", dsPrefix);
            keyIdElem.appendChild(ownerDoc.createTextNode(XMLUtils.encodeToString(this.keyId)));
            pdElem.appendChild(keyIdElem);
        }
        if (this.keyPacket != null) {
            Element keyPktElem = DOMUtils.createElement(ownerDoc, Constants._TAG_PGPKEYPACKET, "http://www.w3.org/2000/09/xmldsig#", dsPrefix);
            keyPktElem.appendChild(ownerDoc.createTextNode(XMLUtils.encodeToString(this.keyPacket)));
            pdElem.appendChild(keyPktElem);
        }
        Iterator<XMLStructure> it = this.externalElements.iterator();
        while (it.hasNext()) {
            DOMUtils.appendChild(pdElem, ((XMLStructure) it.next()).getNode());
        }
        node.appendChild(pdElem);
    }

    private void checkKeyPacket(byte[] keyPacket) {
        if (keyPacket.length < 3) {
            throw new IllegalArgumentException("keypacket must be at least 3 bytes long");
        }
        byte b = keyPacket[0];
        if ((b & 128) != 128) {
            throw new IllegalArgumentException("keypacket tag is invalid: bit 7 is not set");
        }
        if ((b & 64) != 64) {
            throw new IllegalArgumentException("old keypacket tag format is unsupported");
        }
        if ((b & 6) != 6 && (b & 14) != 14 && (b & 5) != 5 && (b & 7) != 7) {
            throw new IllegalArgumentException("keypacket tag is invalid: must be 6, 14, 5, or 7");
        }
    }
}
