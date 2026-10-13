package org.apache.xml.security.configuration;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/configuration/ConfigurationType.class */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ConfigurationType", namespace = "http://www.xmlsecurity.org/NS/configuration", propOrder = {"properties", "securityHeaderHandlers", "transformAlgorithms", "jceAlgorithmMappings", "resourceResolvers"})
public class ConfigurationType {

    @XmlElement(name = "Properties", namespace = "http://www.xmlsecurity.org/NS/configuration", required = true)
    protected PropertiesType properties;

    @XmlElement(name = "SecurityHeaderHandlers", namespace = "http://www.xmlsecurity.org/NS/configuration", required = true)
    protected SecurityHeaderHandlersType securityHeaderHandlers;

    @XmlElement(name = "TransformAlgorithms", namespace = "http://www.xmlsecurity.org/NS/configuration", required = true)
    protected TransformAlgorithmsType transformAlgorithms;

    @XmlElement(name = "JCEAlgorithmMappings", namespace = "http://www.xmlsecurity.org/NS/configuration", required = true)
    protected JCEAlgorithmMappingsType jceAlgorithmMappings;

    @XmlElement(name = "ResourceResolvers", namespace = "http://www.xmlsecurity.org/NS/configuration", required = true)
    protected ResourceResolversType resourceResolvers;

    @XmlAttribute(name = "target")
    protected String target;

    public PropertiesType getProperties() {
        return this.properties;
    }

    public void setProperties(PropertiesType value) {
        this.properties = value;
    }

    public SecurityHeaderHandlersType getSecurityHeaderHandlers() {
        return this.securityHeaderHandlers;
    }

    public void setSecurityHeaderHandlers(SecurityHeaderHandlersType value) {
        this.securityHeaderHandlers = value;
    }

    public TransformAlgorithmsType getTransformAlgorithms() {
        return this.transformAlgorithms;
    }

    public void setTransformAlgorithms(TransformAlgorithmsType value) {
        this.transformAlgorithms = value;
    }

    public JCEAlgorithmMappingsType getJCEAlgorithmMappings() {
        return this.jceAlgorithmMappings;
    }

    public void setJCEAlgorithmMappings(JCEAlgorithmMappingsType value) {
        this.jceAlgorithmMappings = value;
    }

    public ResourceResolversType getResourceResolvers() {
        return this.resourceResolvers;
    }

    public void setResourceResolvers(ResourceResolversType value) {
        this.resourceResolvers = value;
    }

    public String getTarget() {
        return this.target;
    }

    public void setTarget(String value) {
        this.target = value;
    }
}
