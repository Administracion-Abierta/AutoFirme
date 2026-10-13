package org.apache.xml.security.stax.config;

import java.util.List;
import java.util.Properties;
import org.apache.xml.security.configuration.PropertiesType;
import org.apache.xml.security.configuration.PropertyType;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/stax/config/ConfigurationProperties.class */
public final class ConfigurationProperties {
    private static Properties properties;
    private static Class<?> callingClass;

    private ConfigurationProperties() {
    }

    protected static synchronized void init(PropertiesType propertiesType, Class<?> callingClass2) throws Exception {
        properties = new Properties();
        List<PropertyType> handlerList = propertiesType.getProperty();
        for (PropertyType propertyType : handlerList) {
            properties.setProperty(propertyType.getNAME(), propertyType.getVAL());
        }
        callingClass = callingClass2;
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    public static Class<?> getCallingClass() {
        return callingClass;
    }
}
