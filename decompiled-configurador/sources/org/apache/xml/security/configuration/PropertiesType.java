package org.apache.xml.security.configuration;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/configuration/PropertiesType.class */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PropertiesType", namespace = "http://www.xmlsecurity.org/NS/configuration", propOrder = {"property"})
public class PropertiesType {

    @XmlElement(name = "Property", namespace = "http://www.xmlsecurity.org/NS/configuration")
    protected List<PropertyType> property;

    public List<PropertyType> getProperty() {
        if (this.property == null) {
            this.property = new ArrayList();
        }
        return this.property;
    }
}
