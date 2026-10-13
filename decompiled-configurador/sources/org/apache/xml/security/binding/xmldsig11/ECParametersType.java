package org.apache.xml.security.binding.xmldsig11;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import java.math.BigInteger;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/binding/xmldsig11/ECParametersType.class */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ECParametersType", namespace = "http://www.w3.org/2009/xmldsig11#", propOrder = {"fieldID", "curve", "base", "order", "coFactor", "validationData"})
public class ECParametersType {

    @XmlElement(name = "FieldID", namespace = "http://www.w3.org/2009/xmldsig11#", required = true)
    protected FieldIDType fieldID;

    @XmlElement(name = "Curve", namespace = "http://www.w3.org/2009/xmldsig11#", required = true)
    protected CurveType curve;

    @XmlElement(name = "Base", namespace = "http://www.w3.org/2009/xmldsig11#", required = true)
    protected byte[] base;

    @XmlElement(name = "Order", namespace = "http://www.w3.org/2009/xmldsig11#", required = true)
    protected byte[] order;

    @XmlElement(name = "CoFactor", namespace = "http://www.w3.org/2009/xmldsig11#")
    protected BigInteger coFactor;

    @XmlElement(name = "ValidationData", namespace = "http://www.w3.org/2009/xmldsig11#")
    protected ECValidationDataType validationData;

    public FieldIDType getFieldID() {
        return this.fieldID;
    }

    public void setFieldID(FieldIDType value) {
        this.fieldID = value;
    }

    public CurveType getCurve() {
        return this.curve;
    }

    public void setCurve(CurveType value) {
        this.curve = value;
    }

    public byte[] getBase() {
        return this.base;
    }

    public void setBase(byte[] value) {
        this.base = value;
    }

    public byte[] getOrder() {
        return this.order;
    }

    public void setOrder(byte[] value) {
        this.order = value;
    }

    public BigInteger getCoFactor() {
        return this.coFactor;
    }

    public void setCoFactor(BigInteger value) {
        this.coFactor = value;
    }

    public ECValidationDataType getValidationData() {
        return this.validationData;
    }

    public void setValidationData(ECValidationDataType value) {
        this.validationData = value;
    }
}
