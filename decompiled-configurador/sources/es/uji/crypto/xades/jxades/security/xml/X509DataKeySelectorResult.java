package es.uji.crypto.xades.jxades.security.xml;

import java.security.Key;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import javax.xml.crypto.KeySelectorResult;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.keyinfo.X509IssuerSerial;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/uji/crypto/xades/jxades/security/xml/X509DataKeySelectorResult.class */
public class X509DataKeySelectorResult implements KeySelectorResult {
    private X509IssuerSerial x509IssuerSerial;
    private X509Certificate x509Certificate;
    private X509CRL x509CRL;
    private String subjectName;
    private byte[] subjectKeyId;
    private List<XMLStructure> unrecognizedObjects;

    public X509DataKeySelectorResult(X509Data data) {
        List content = data.getContent();
        this.unrecognizedObjects = new ArrayList(content.size());
        for (Object obj : content) {
            if (obj instanceof X509IssuerSerial) {
                this.x509IssuerSerial = (X509IssuerSerial) obj;
            } else if (obj instanceof X509Certificate) {
                this.x509Certificate = (X509Certificate) obj;
            } else if (obj instanceof X509CRL) {
                this.x509CRL = (X509CRL) obj;
            } else if (obj instanceof String) {
                this.subjectName = (String) obj;
            } else if (obj instanceof byte[]) {
                this.subjectKeyId = (byte[]) obj;
            } else {
                this.unrecognizedObjects.add((XMLStructure) obj);
            }
        }
    }

    public Key getKey() {
        if (this.x509Certificate != null) {
            return this.x509Certificate.getPublicKey();
        }
        return null;
    }

    public X509IssuerSerial getX509IssuerSerial() {
        return this.x509IssuerSerial;
    }

    public X509Certificate getX509Certificate() {
        return this.x509Certificate;
    }

    public X509CRL getX509CRL() {
        return this.x509CRL;
    }

    public String getSubjectName() {
        return this.subjectName;
    }

    public byte[] getSubjectKeyId() {
        return this.subjectKeyId;
    }

    public List<XMLStructure> getUnrecognizedObjects() {
        return this.unrecognizedObjects;
    }
}
