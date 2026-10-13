package org.apache.xml.security.binding.xmldsig11;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;
import java.math.BigInteger;
import org.apache.pdfbox.contentstream.operator.OperatorName;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/binding/xmldsig11/CharTwoFieldParamsType.class */
@XmlSeeAlso({TnBFieldParamsType.class, PnBFieldParamsType.class})
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CharTwoFieldParamsType", namespace = "http://www.w3.org/2009/xmldsig11#", propOrder = {OperatorName.MOVE_TO})
public class CharTwoFieldParamsType {

    @XmlSchemaType(name = "positiveInteger")
    @XmlElement(name = OperatorName.SET_LINE_MITERLIMIT, namespace = "http://www.w3.org/2009/xmldsig11#", required = true)
    protected BigInteger m;

    public BigInteger getM() {
        return this.m;
    }

    public void setM(BigInteger value) {
        this.m = value;
    }
}
