package org.apache.xml.security.binding.xmldsig;

import com.ctc.wstx.shaded.msv_core.scanner.dtd.DTDParser;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlID;
import jakarta.xml.bind.annotation.XmlMixed;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/binding/xmldsig/SignaturePropertyType.class */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SignaturePropertyType", namespace = "http://www.w3.org/2000/09/xmldsig#", propOrder = {"content"})
public class SignaturePropertyType {

    @XmlMixed
    @XmlAnyElement(lax = true)
    protected List<Object> content;

    @XmlSchemaType(name = "anyURI")
    @XmlAttribute(name = "Target", required = true)
    protected String target;

    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = DTDParser.TYPE_ID)
    @XmlAttribute(name = "Id")
    @XmlID
    protected String id;

    public List<Object> getContent() {
        if (this.content == null) {
            this.content = new ArrayList();
        }
        return this.content;
    }

    public String getTarget() {
        return this.target;
    }

    public void setTarget(String value) {
        this.target = value;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String value) {
        this.id = value;
    }
}
