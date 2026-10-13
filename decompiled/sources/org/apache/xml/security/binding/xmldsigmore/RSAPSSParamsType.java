package org.apache.xml.security.binding.xmldsigmore;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import org.apache.xml.security.binding.xmldsig.DigestMethodType;
import org.apache.xml.security.utils.Constants;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/binding/xmldsigmore/RSAPSSParamsType.class */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RSAPSSParamsType", namespace = "http://www.w3.org/2007/05/xmldsig-more#", propOrder = {"digestMethod", "maskGenerationFunction", "saltLength", "trailerField"})
public class RSAPSSParamsType {

    @XmlElement(name = "DigestMethod", namespace = "http://www.w3.org/2000/09/xmldsig#")
    protected DigestMethodType digestMethod;

    @XmlElement(name = "MaskGenerationFunction", namespace = "http://www.w3.org/2007/05/xmldsig-more#")
    protected MaskGenerationFunctionType maskGenerationFunction;

    @XmlElement(name = Constants._TAG_SALTLENGTH, namespace = "http://www.w3.org/2007/05/xmldsig-more#")
    protected Integer saltLength;

    @XmlElement(name = Constants._TAG_TRAILERFIELD, namespace = "http://www.w3.org/2007/05/xmldsig-more#")
    protected Integer trailerField;

    public DigestMethodType getDigestMethod() {
        return this.digestMethod;
    }

    public void setDigestMethod(DigestMethodType value) {
        this.digestMethod = value;
    }

    public MaskGenerationFunctionType getMaskGenerationFunction() {
        return this.maskGenerationFunction;
    }

    public void setMaskGenerationFunction(MaskGenerationFunctionType value) {
        this.maskGenerationFunction = value;
    }

    public Integer getSaltLength() {
        return this.saltLength;
    }

    public void setSaltLength(Integer value) {
        this.saltLength = value;
    }

    public Integer getTrailerField() {
        return this.trailerField;
    }

    public void setTrailerField(Integer value) {
        this.trailerField = value;
    }
}
