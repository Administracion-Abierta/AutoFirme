package org.apache.xml.security.configuration;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/configuration/ObjectFactory.class */
@XmlRegistry
public class ObjectFactory {
    private static final QName _Configuration_QNAME = new QName("http://www.xmlsecurity.org/NS/configuration", "Configuration");

    public ConfigurationType createConfigurationType() {
        return new ConfigurationType();
    }

    public AlgorithmType createAlgorithmType() {
        return new AlgorithmType();
    }

    public TransformAlgorithmType createTransformAlgorithmType() {
        return new TransformAlgorithmType();
    }

    public ResolverType createResolverType() {
        return new ResolverType();
    }

    public PropertyType createPropertyType() {
        return new PropertyType();
    }

    public TransformAlgorithmsType createTransformAlgorithmsType() {
        return new TransformAlgorithmsType();
    }

    public HandlerType createHandlerType() {
        return new HandlerType();
    }

    public SecurityHeaderHandlersType createSecurityHeaderHandlersType() {
        return new SecurityHeaderHandlersType();
    }

    public PropertiesType createPropertiesType() {
        return new PropertiesType();
    }

    public JCEAlgorithmMappingsType createJCEAlgorithmMappingsType() {
        return new JCEAlgorithmMappingsType();
    }

    public ResourceResolversType createResourceResolversType() {
        return new ResourceResolversType();
    }

    @XmlElementDecl(namespace = "http://www.xmlsecurity.org/NS/configuration", name = "Configuration")
    public JAXBElement<ConfigurationType> createConfiguration(ConfigurationType value) {
        return new JAXBElement<>(_Configuration_QNAME, ConfigurationType.class, null, value);
    }
}
