package org.apache.xml.security.keys.content.keyvalues;

import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.DSAParams;
import java.security.interfaces.DSAPublicKey;
import java.security.spec.DSAPublicKeySpec;
import java.security.spec.InvalidKeySpecException;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.I18n;
import org.apache.xml.security.utils.SignatureElementProxy;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/keys/content/keyvalues/DSAKeyValue.class */
public class DSAKeyValue extends SignatureElementProxy implements KeyValueContent {
    public DSAKeyValue(Element element, String baseURI) throws XMLSecurityException {
        super(element, baseURI);
    }

    public DSAKeyValue(Document doc, BigInteger P, BigInteger Q, BigInteger G, BigInteger Y) {
        super(doc);
        addReturnToSelf();
        addBigIntegerElement(P, Constants._TAG_P);
        addBigIntegerElement(Q, Constants._TAG_Q);
        addBigIntegerElement(G, Constants._TAG_G);
        addBigIntegerElement(Y, Constants._TAG_Y);
    }

    public DSAKeyValue(Document doc, Key key) throws IllegalArgumentException {
        super(doc);
        addReturnToSelf();
        if (key instanceof DSAPublicKey) {
            DSAParams params = ((DSAPublicKey) key).getParams();
            addBigIntegerElement(params.getP(), Constants._TAG_P);
            addBigIntegerElement(params.getQ(), Constants._TAG_Q);
            addBigIntegerElement(params.getG(), Constants._TAG_G);
            addBigIntegerElement(((DSAPublicKey) key).getY(), Constants._TAG_Y);
            return;
        }
        Object[] exArgs = {Constants._TAG_DSAKEYVALUE, key.getClass().getName()};
        throw new IllegalArgumentException(I18n.translate("KeyValue.IllegalArgument", exArgs));
    }

    @Override // org.apache.xml.security.keys.content.keyvalues.KeyValueContent
    public PublicKey getPublicKey() throws XMLSecurityException {
        try {
            DSAPublicKeySpec pkspec = new DSAPublicKeySpec(getBigIntegerFromChildElement(Constants._TAG_Y, "http://www.w3.org/2000/09/xmldsig#"), getBigIntegerFromChildElement(Constants._TAG_P, "http://www.w3.org/2000/09/xmldsig#"), getBigIntegerFromChildElement(Constants._TAG_Q, "http://www.w3.org/2000/09/xmldsig#"), getBigIntegerFromChildElement(Constants._TAG_G, "http://www.w3.org/2000/09/xmldsig#"));
            KeyFactory dsaFactory = KeyFactory.getInstance("DSA");
            return dsaFactory.generatePublic(pkspec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            throw new XMLSecurityException(ex);
        }
    }

    @Override // org.apache.xml.security.utils.ElementProxy
    public String getBaseLocalName() {
        return Constants._TAG_DSAKEYVALUE;
    }
}
