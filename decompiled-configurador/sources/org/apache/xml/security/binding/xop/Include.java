package org.apache.xml.security.binding.xop;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAnyAttribute;
import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;
import org.apache.commons.codec.language.bm.Languages;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/binding/xop/Include.class */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Include", namespace = XMLSecurityConstants.NS_XOP, propOrder = {Languages.ANY})
public class Include {

    @XmlAnyElement(lax = true)
    protected List<Object> any;

    @XmlSchemaType(name = "anyURI")
    @XmlAttribute(name = "href", required = true)
    protected String href;

    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap();

    public List<Object> getAny() {
        if (this.any == null) {
            this.any = new ArrayList();
        }
        return this.any;
    }

    public String getHref() {
        return this.href;
    }

    public void setHref(String value) {
        this.href = value;
    }

    public Map<QName, String> getOtherAttributes() {
        return this.otherAttributes;
    }
}
