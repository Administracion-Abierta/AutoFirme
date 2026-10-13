package org.apache.xml.security.keys.content.keyvalues;

import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.I18n;
import org.apache.xml.security.utils.SignatureElementProxy;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/keys/content/keyvalues/RSAKeyValue.class */
public class RSAKeyValue extends SignatureElementProxy implements KeyValueContent {
    public RSAKeyValue(Element element, String baseURI) throws XMLSecurityException {
        super(element, baseURI);
    }

    public RSAKeyValue(Document doc, BigInteger modulus, BigInteger exponent) {
        super(doc);
        addReturnToSelf();
        addBigIntegerElement(modulus, Constants._TAG_MODULUS);
        addBigIntegerElement(exponent, Constants._TAG_EXPONENT);
    }

    public RSAKeyValue(Document doc, Key key) throws IllegalArgumentException {
        super(doc);
        addReturnToSelf();
        if (key instanceof RSAPublicKey) {
            addBigIntegerElement(((RSAPublicKey) key).getModulus(), Constants._TAG_MODULUS);
            addBigIntegerElement(((RSAPublicKey) key).getPublicExponent(), Constants._TAG_EXPONENT);
        } else {
            Object[] exArgs = {Constants._TAG_RSAKEYVALUE, key.getClass().getName()};
            throw new IllegalArgumentException(I18n.translate("KeyValue.IllegalArgument", exArgs));
        }
    }

    @Override // org.apache.xml.security.keys.content.keyvalues.KeyValueContent
    public PublicKey getPublicKey() throws XMLSecurityException {
        try {
            KeyFactory rsaFactory = KeyFactory.getInstance("RSA");
            RSAPublicKeySpec rsaKeyspec = new RSAPublicKeySpec(getBigIntegerFromChildElement(Constants._TAG_MODULUS, "http://www.w3.org/2000/09/xmldsig#"), getBigIntegerFromChildElement(Constants._TAG_EXPONENT, "http://www.w3.org/2000/09/xmldsig#"));
            PublicKey pk = rsaFactory.generatePublic(rsaKeyspec);
            return pk;
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            throw new XMLSecurityException(ex);
        }
    }

    @Override // org.apache.xml.security.utils.ElementProxy
    public String getBaseLocalName() {
        return Constants._TAG_RSAKEYVALUE;
    }
}
