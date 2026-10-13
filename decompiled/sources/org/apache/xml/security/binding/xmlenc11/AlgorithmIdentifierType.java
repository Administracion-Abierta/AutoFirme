package org.apache.xml.security.binding.xmlenc11;

import com.aowagie.text.Annotation;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/binding/xmlenc11/AlgorithmIdentifierType.class */
@XmlSeeAlso({MGFType.class, PRFAlgorithmIdentifierType.class})
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AlgorithmIdentifierType", namespace = "http://www.w3.org/2009/xmlenc11#", propOrder = {Annotation.PARAMETERS})
public class AlgorithmIdentifierType {

    @XmlElement(name = "Parameters", namespace = "http://www.w3.org/2009/xmlenc11#")
    protected Object parameters;

    @XmlSchemaType(name = "anyURI")
    @XmlAttribute(name = "Algorithm", required = true)
    protected String algorithm;

    public Object getParameters() {
        return this.parameters;
    }

    public void setParameters(Object value) {
        this.parameters = value;
    }

    public String getAlgorithm() {
        return this.algorithm;
    }

    public void setAlgorithm(String value) {
        this.algorithm = value;
    }
}
