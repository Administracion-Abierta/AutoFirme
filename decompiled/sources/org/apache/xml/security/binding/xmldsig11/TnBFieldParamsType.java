package org.apache.xml.security.binding.xmldsig11;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import java.math.BigInteger;
import org.apache.pdfbox.contentstream.operator.OperatorName;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/binding/xmldsig11/TnBFieldParamsType.class */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TnBFieldParamsType", namespace = "http://www.w3.org/2009/xmldsig11#", propOrder = {OperatorName.NON_STROKING_CMYK})
public class TnBFieldParamsType extends CharTwoFieldParamsType {

    @XmlSchemaType(name = "positiveInteger")
    @XmlElement(name = OperatorName.STROKING_COLOR_CMYK, namespace = "http://www.w3.org/2009/xmldsig11#", required = true)
    protected BigInteger k;

    public BigInteger getK() {
        return this.k;
    }

    public void setK(BigInteger value) {
        this.k = value;
    }
}
