package org.apache.xml.security.stax.config;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.UnmarshallerHandler;
import java.net.URI;
import java.net.URL;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.apache.xml.security.configuration.ConfigurationType;
import org.apache.xml.security.configuration.ObjectFactory;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.ext.XMLSecurityConfigurationException;
import org.apache.xml.security.utils.ClassLoaderUtils;
import org.apache.xml.security.utils.I18n;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/stax/config/Init.class */
public class Init {
    private static URI initialized;

    public static synchronized void init(URI uri, Class<?> callingClass) throws XMLSecurityException {
        if (initialized == null || (uri != null && !uri.equals(initialized))) {
            try {
                JAXBContext jaxbContext = JAXBContext.newInstance((Class<?>[]) new Class[]{ObjectFactory.class});
                Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
                SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
                schemaFactory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", Boolean.TRUE.booleanValue());
                Schema schema = schemaFactory.newSchema(ClassLoaderUtils.getResource("schemas/security-config.xsd", Init.class));
                unmarshaller.setSchema(schema);
                UnmarshallerHandler unmarshallerHandler = unmarshaller.getUnmarshallerHandler();
                SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
                saxParserFactory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", Boolean.TRUE.booleanValue());
                saxParserFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
                saxParserFactory.setXIncludeAware(false);
                saxParserFactory.setNamespaceAware(true);
                SAXParser saxParser = saxParserFactory.newSAXParser();
                if (uri == null) {
                    URL resource = ClassLoaderUtils.getResource("security-config.xml", Init.class);
                    if (resource == null) {
                        I18n.init("en", "US");
                        throw new XMLSecurityConfigurationException("empty", "security-config.xml not found in classpath");
                    }
                    uri = resource.toURI();
                }
                saxParser.parse(uri.toURL().toExternalForm(), new XIncludeHandler(unmarshallerHandler));
                JAXBElement<ConfigurationType> configurationTypeJAXBElement = (JAXBElement) unmarshallerHandler.getResult();
                ConfigurationProperties.init(configurationTypeJAXBElement.getValue().getProperties(), callingClass);
                SecurityHeaderHandlerMapper.init(configurationTypeJAXBElement.getValue().getSecurityHeaderHandlers(), callingClass);
                JCEAlgorithmMapper.init(configurationTypeJAXBElement.getValue().getJCEAlgorithmMappings());
                TransformerAlgorithmMapper.init(configurationTypeJAXBElement.getValue().getTransformAlgorithms(), callingClass);
                ResourceResolverMapper.init(configurationTypeJAXBElement.getValue().getResourceResolvers(), callingClass);
                I18n.init(ConfigurationProperties.getProperty("DefaultLanguageCode"), ConfigurationProperties.getProperty("DefaultCountryCode"));
                initialized = uri;
            } catch (Exception e) {
                I18n.init("en", "US");
                throw new XMLSecurityConfigurationException(e);
            }
        }
    }
}
