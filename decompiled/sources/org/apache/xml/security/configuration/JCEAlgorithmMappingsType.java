package org.apache.xml.security.configuration;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/configuration/JCEAlgorithmMappingsType.class */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "JCEAlgorithmMappingsType", namespace = "http://www.xmlsecurity.org/NS/configuration", propOrder = {"algorithm"})
public class JCEAlgorithmMappingsType {

    @XmlElement(name = "Algorithm", namespace = "http://www.xmlsecurity.org/NS/configuration")
    protected List<AlgorithmType> algorithm;

    public List<AlgorithmType> getAlgorithm() {
        if (this.algorithm == null) {
            this.algorithm = new ArrayList();
        }
        return this.algorithm;
    }
}
