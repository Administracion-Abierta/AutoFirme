package org.apache.xml.security.binding.xmlenc11;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.codec.language.bm.Languages;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/binding/xmlenc11/KeyDerivationMethodType.class */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "KeyDerivationMethodType", namespace = "http://www.w3.org/2009/xmlenc11#", propOrder = {Languages.ANY})
public class KeyDerivationMethodType {

    @XmlAnyElement(lax = true)
    protected List<Object> any;

    @XmlSchemaType(name = "anyURI")
    @XmlAttribute(name = "Algorithm", required = true)
    protected String algorithm;

    public List<Object> getAny() {
        if (this.any == null) {
            this.any = new ArrayList();
        }
        return this.any;
    }

    public String getAlgorithm() {
        return this.algorithm;
    }

    public void setAlgorithm(String value) {
        this.algorithm = value;
    }
}
