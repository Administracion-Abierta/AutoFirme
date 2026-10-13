package org.apache.xml.security.binding.xmldsigmore;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import org.apache.xml.security.binding.xmldsig.DigestMethodType;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/binding/xmldsigmore/MaskGenerationFunctionType.class */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MaskGenerationFunctionType", namespace = "http://www.w3.org/2007/05/xmldsig-more#", propOrder = {"digestMethod"})
public class MaskGenerationFunctionType {

    @XmlElement(name = "DigestMethod", namespace = "http://www.w3.org/2000/09/xmldsig#")
    protected DigestMethodType digestMethod;

    @XmlSchemaType(name = "anyURI")
    @XmlAttribute(name = "Algorithm")
    protected String algorithm;

    public DigestMethodType getDigestMethod() {
        return this.digestMethod;
    }

    public void setDigestMethod(DigestMethodType value) {
        this.digestMethod = value;
    }

    public String getAlgorithm() {
        if (this.algorithm == null) {
            return "http://www.w3.org/2007/05/xmldsig-more#MGF1";
        }
        return this.algorithm;
    }

    public void setAlgorithm(String value) {
        this.algorithm = value;
    }
}
