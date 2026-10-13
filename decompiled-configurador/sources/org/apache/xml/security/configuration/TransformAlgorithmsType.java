package org.apache.xml.security.configuration;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/configuration/TransformAlgorithmsType.class */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TransformAlgorithmsType", namespace = "http://www.xmlsecurity.org/NS/configuration", propOrder = {"transformAlgorithm"})
public class TransformAlgorithmsType {

    @XmlElement(name = "TransformAlgorithm", namespace = "http://www.xmlsecurity.org/NS/configuration")
    protected List<TransformAlgorithmType> transformAlgorithm;

    public List<TransformAlgorithmType> getTransformAlgorithm() {
        if (this.transformAlgorithm == null) {
            this.transformAlgorithm = new ArrayList();
        }
        return this.transformAlgorithm;
    }
}
