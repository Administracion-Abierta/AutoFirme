package org.apache.xml.security.binding.xmldsig;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import org.apache.xml.security.utils.Constants;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/binding/xmldsig/DSAKeyValueType.class */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DSAKeyValueType", namespace = "http://www.w3.org/2000/09/xmldsig#", propOrder = {"p", "q", "g", "y", "j", "seed", "pgenCounter"})
public class DSAKeyValueType {

    @XmlElement(name = Constants._TAG_P, namespace = "http://www.w3.org/2000/09/xmldsig#")
    protected byte[] p;

    @XmlElement(name = Constants._TAG_Q, namespace = "http://www.w3.org/2000/09/xmldsig#")
    protected byte[] q;

    @XmlElement(name = Constants._TAG_G, namespace = "http://www.w3.org/2000/09/xmldsig#")
    protected byte[] g;

    @XmlElement(name = Constants._TAG_Y, namespace = "http://www.w3.org/2000/09/xmldsig#", required = true)
    protected byte[] y;

    @XmlElement(name = Constants._TAG_J, namespace = "http://www.w3.org/2000/09/xmldsig#")
    protected byte[] j;

    @XmlElement(name = Constants._TAG_SEED, namespace = "http://www.w3.org/2000/09/xmldsig#")
    protected byte[] seed;

    @XmlElement(name = Constants._TAG_PGENCOUNTER, namespace = "http://www.w3.org/2000/09/xmldsig#")
    protected byte[] pgenCounter;

    public byte[] getP() {
        return this.p;
    }

    public void setP(byte[] value) {
        this.p = value;
    }

    public byte[] getQ() {
        return this.q;
    }

    public void setQ(byte[] value) {
        this.q = value;
    }

    public byte[] getG() {
        return this.g;
    }

    public void setG(byte[] value) {
        this.g = value;
    }

    public byte[] getY() {
        return this.y;
    }

    public void setY(byte[] value) {
        this.y = value;
    }

    public byte[] getJ() {
        return this.j;
    }

    public void setJ(byte[] value) {
        this.j = value;
    }

    public byte[] getSeed() {
        return this.seed;
    }

    public void setSeed(byte[] value) {
        this.seed = value;
    }

    public byte[] getPgenCounter() {
        return this.pgenCounter;
    }

    public void setPgenCounter(byte[] value) {
        this.pgenCounter = value;
    }
}
