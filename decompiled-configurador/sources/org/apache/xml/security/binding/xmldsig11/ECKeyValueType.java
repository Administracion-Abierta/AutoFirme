package org.apache.xml.security.binding.xmldsig11;

import com.ctc.wstx.shaded.msv_core.scanner.dtd.DTDParser;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlID;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/binding/xmldsig11/ECKeyValueType.class */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ECKeyValueType", namespace = "http://www.w3.org/2009/xmldsig11#", propOrder = {"ecParameters", "namedCurve", "publicKey"})
public class ECKeyValueType {

    @XmlElement(name = "ECParameters", namespace = "http://www.w3.org/2009/xmldsig11#")
    protected ECParametersType ecParameters;

    @XmlElement(name = "NamedCurve", namespace = "http://www.w3.org/2009/xmldsig11#")
    protected NamedCurveType namedCurve;

    @XmlElement(name = "PublicKey", namespace = "http://www.w3.org/2009/xmldsig11#", required = true)
    protected byte[] publicKey;

    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = DTDParser.TYPE_ID)
    @XmlAttribute(name = "Id")
    @XmlID
    protected String id;

    public ECParametersType getECParameters() {
        return this.ecParameters;
    }

    public void setECParameters(ECParametersType value) {
        this.ecParameters = value;
    }

    public NamedCurveType getNamedCurve() {
        return this.namedCurve;
    }

    public void setNamedCurve(NamedCurveType value) {
        this.namedCurve = value;
    }

    public byte[] getPublicKey() {
        return this.publicKey;
    }

    public void setPublicKey(byte[] value) {
        this.publicKey = value;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String value) {
        this.id = value;
    }
}
