package org.apache.xml.security.binding.xmldsig11;

import com.ctc.wstx.shaded.msv_core.scanner.dtd.DTDParser;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlID;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.XmlValue;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/binding/xmldsig11/DEREncodedKeyValueType.class */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DEREncodedKeyValueType", namespace = "http://www.w3.org/2009/xmldsig11#", propOrder = {"value"})
public class DEREncodedKeyValueType {

    @XmlValue
    protected byte[] value;

    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = DTDParser.TYPE_ID)
    @XmlAttribute(name = "Id")
    @XmlID
    protected String id;

    public byte[] getValue() {
        return this.value;
    }

    public void setValue(byte[] value) {
        this.value = value;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String value) {
        this.id = value;
    }
}
