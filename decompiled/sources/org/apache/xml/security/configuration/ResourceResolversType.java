package org.apache.xml.security.configuration;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/configuration/ResourceResolversType.class */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ResourceResolversType", namespace = "http://www.xmlsecurity.org/NS/configuration", propOrder = {"resolver"})
public class ResourceResolversType {

    @XmlElement(name = "Resolver", namespace = "http://www.xmlsecurity.org/NS/configuration")
    protected List<ResolverType> resolver;

    public List<ResolverType> getResolver() {
        if (this.resolver == null) {
            this.resolver = new ArrayList();
        }
        return this.resolver;
    }
}
