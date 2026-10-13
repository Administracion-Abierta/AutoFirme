package org.apache.xml.security.binding.xmldsig;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;
import org.apache.xml.security.utils.Constants;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/binding/xmldsig/TransformsType.class */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TransformsType", namespace = "http://www.w3.org/2000/09/xmldsig#", propOrder = {"transform"})
public class TransformsType {

    @XmlElement(name = Constants._TAG_TRANSFORM, namespace = "http://www.w3.org/2000/09/xmldsig#", required = true)
    protected List<TransformType> transform;

    public List<TransformType> getTransform() {
        if (this.transform == null) {
            this.transform = new ArrayList();
        }
        return this.transform;
    }
}
