package org.apache.xml.security.binding.xmldsig;

import com.ctc.wstx.shaded.msv_core.scanner.dtd.DTDParser;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlID;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
import java.util.List;
import org.apache.xml.security.utils.Constants;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/binding/xmldsig/SignaturePropertiesType.class */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SignaturePropertiesType", namespace = "http://www.w3.org/2000/09/xmldsig#", propOrder = {"signatureProperty"})
public class SignaturePropertiesType {

    @XmlElement(name = Constants._TAG_SIGNATUREPROPERTY, namespace = "http://www.w3.org/2000/09/xmldsig#", required = true)
    protected List<SignaturePropertyType> signatureProperty;

    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = DTDParser.TYPE_ID)
    @XmlAttribute(name = "Id")
    @XmlID
    protected String id;

    public List<SignaturePropertyType> getSignatureProperty() {
        if (this.signatureProperty == null) {
            this.signatureProperty = new ArrayList();
        }
        return this.signatureProperty;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String value) {
        this.id = value;
    }
}
