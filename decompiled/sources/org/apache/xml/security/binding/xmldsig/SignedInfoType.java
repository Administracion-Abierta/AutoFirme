package org.apache.xml.security.binding.xmldsig;

import com.aowagie.text.ElementTags;
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

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/binding/xmldsig/SignedInfoType.class */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SignedInfoType", namespace = "http://www.w3.org/2000/09/xmldsig#", propOrder = {"canonicalizationMethod", "signatureMethod", ElementTags.REFERENCE})
public class SignedInfoType {

    @XmlElement(name = Constants._TAG_CANONICALIZATIONMETHOD, namespace = "http://www.w3.org/2000/09/xmldsig#", required = true)
    protected CanonicalizationMethodType canonicalizationMethod;

    @XmlElement(name = "SignatureMethod", namespace = "http://www.w3.org/2000/09/xmldsig#", required = true)
    protected SignatureMethodType signatureMethod;

    @XmlElement(name = "Reference", namespace = "http://www.w3.org/2000/09/xmldsig#", required = true)
    protected List<ReferenceType> reference;

    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "ID")
    @XmlAttribute(name = "Id")
    @XmlID
    protected String id;

    public CanonicalizationMethodType getCanonicalizationMethod() {
        return this.canonicalizationMethod;
    }

    public void setCanonicalizationMethod(CanonicalizationMethodType value) {
        this.canonicalizationMethod = value;
    }

    public SignatureMethodType getSignatureMethod() {
        return this.signatureMethod;
    }

    public void setSignatureMethod(SignatureMethodType value) {
        this.signatureMethod = value;
    }

    public List<ReferenceType> getReference() {
        if (this.reference == null) {
            this.reference = new ArrayList();
        }
        return this.reference;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String value) {
        this.id = value;
    }
}
