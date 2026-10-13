package org.apache.xml.security.keys.content;

import java.security.PublicKey;
import java.security.interfaces.DSAPublicKey;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPublicKey;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.keys.content.keyvalues.DSAKeyValue;
import org.apache.xml.security.keys.content.keyvalues.ECKeyValue;
import org.apache.xml.security.keys.content.keyvalues.RSAKeyValue;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.SignatureElementProxy;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/keys/content/KeyValue.class */
public class KeyValue extends SignatureElementProxy implements KeyInfoContent {
    public KeyValue(Document doc, DSAKeyValue dsaKeyValue) {
        super(doc);
        addReturnToSelf();
        appendSelf(dsaKeyValue);
        addReturnToSelf();
    }

    public KeyValue(Document doc, RSAKeyValue rsaKeyValue) {
        super(doc);
        addReturnToSelf();
        appendSelf(rsaKeyValue);
        addReturnToSelf();
    }

    public KeyValue(Document doc, Element unknownKeyValue) {
        super(doc);
        addReturnToSelf();
        appendSelf(unknownKeyValue);
        addReturnToSelf();
    }

    public KeyValue(Document doc, PublicKey pk) {
        super(doc);
        addReturnToSelf();
        if (pk instanceof DSAPublicKey) {
            DSAKeyValue dsa = new DSAKeyValue(getDocument(), pk);
            appendSelf(dsa);
            addReturnToSelf();
        } else if (pk instanceof RSAPublicKey) {
            RSAKeyValue rsa = new RSAKeyValue(getDocument(), pk);
            appendSelf(rsa);
            addReturnToSelf();
        } else {
            if (pk instanceof ECPublicKey) {
                ECKeyValue ec = new ECKeyValue(getDocument(), pk);
                appendSelf(ec);
                addReturnToSelf();
                return;
            }
            String error = "The given PublicKey type " + pk + " is not supported. Only DSAPublicKey and RSAPublicKey and ECPublicKey types are currently supported";
            throw new IllegalArgumentException(error);
        }
    }

    public static boolean isSupportedKeyType(PublicKey publicKey) {
        return (publicKey instanceof DSAPublicKey) || (publicKey instanceof RSAPublicKey) || (publicKey instanceof ECPublicKey);
    }

    public KeyValue(Element element, String baseURI) throws XMLSecurityException {
        super(element, baseURI);
    }

    public PublicKey getPublicKey() throws XMLSecurityException {
        Element rsa = XMLUtils.selectDsNode(getFirstChild(), Constants._TAG_RSAKEYVALUE, 0);
        if (rsa != null) {
            RSAKeyValue kv = new RSAKeyValue(rsa, this.baseURI);
            return kv.getPublicKey();
        }
        Element dsa = XMLUtils.selectDsNode(getFirstChild(), Constants._TAG_DSAKEYVALUE, 0);
        if (dsa != null) {
            DSAKeyValue kv2 = new DSAKeyValue(dsa, this.baseURI);
            return kv2.getPublicKey();
        }
        return null;
    }

    @Override // org.apache.xml.security.utils.ElementProxy
    public String getBaseLocalName() {
        return Constants._TAG_KEYVALUE;
    }
}
