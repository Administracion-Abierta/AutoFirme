package org.apache.xml.security.keys.content;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.Signature11ElementProxy;
import org.bouncycastle.jcajce.spec.EdDSAParameterSpec;
import org.bouncycastle.jcajce.spec.XDHParameterSpec;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/keys/content/DEREncodedKeyValue.class */
public class DEREncodedKeyValue extends Signature11ElementProxy implements KeyInfoContent {
    private static final String[] supportedKeyTypes = {"RSA", "DSA", "EC", "DiffieHellman", "DH", "XDH", XDHParameterSpec.X25519, XDHParameterSpec.X448, "EdDSA", EdDSAParameterSpec.Ed25519, EdDSAParameterSpec.Ed448, "RSASSA-PSS"};

    public DEREncodedKeyValue(Element element, String baseURI) throws XMLSecurityException {
        super(element, baseURI);
    }

    public DEREncodedKeyValue(Document doc, PublicKey publicKey) throws XMLSecurityException {
        super(doc);
        addBase64Text(getEncodedDER(publicKey));
    }

    public DEREncodedKeyValue(Document doc, byte[] encodedKey) {
        super(doc);
        addBase64Text(encodedKey);
    }

    public void setId(String id) {
        setLocalIdAttribute("Id", id);
    }

    public String getId() {
        return getLocalAttribute("Id");
    }

    @Override // org.apache.xml.security.utils.ElementProxy
    public String getBaseLocalName() {
        return Constants._TAG_DERENCODEDKEYVALUE;
    }

    public PublicKey getPublicKey() throws XMLSecurityException {
        byte[] encodedKey = getBytesFromTextChild();
        for (String keyType : supportedKeyTypes) {
            try {
                KeyFactory keyFactory = KeyFactory.getInstance(keyType);
                X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encodedKey);
                PublicKey publicKey = keyFactory.generatePublic(keySpec);
                if (publicKey != null) {
                    return publicKey;
                }
            } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            }
        }
        throw new XMLSecurityException("DEREncodedKeyValue.UnsupportedEncodedKey");
    }

    protected byte[] getEncodedDER(PublicKey publicKey) throws XMLSecurityException {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(publicKey.getAlgorithm());
            X509EncodedKeySpec keySpec = (X509EncodedKeySpec) keyFactory.getKeySpec(publicKey, X509EncodedKeySpec.class);
            return keySpec.getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            Object[] exArgs = {publicKey.getAlgorithm(), publicKey.getFormat(), publicKey.getClass().getName()};
            throw new XMLSecurityException(e, "DEREncodedKeyValue.UnsupportedPublicKey", exArgs);
        }
    }
}
