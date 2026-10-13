package org.apache.xml.security.configuration;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/configuration/SecurityHeaderHandlersType.class */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SecurityHeaderHandlersType", namespace = "http://www.xmlsecurity.org/NS/configuration", propOrder = {"handler"})
public class SecurityHeaderHandlersType {

    @XmlElement(name = "Handler", namespace = "http://www.xmlsecurity.org/NS/configuration")
    protected List<HandlerType> handler;

    public List<HandlerType> getHandler() {
        if (this.handler == null) {
            this.handler = new ArrayList();
        }
        return this.handler;
    }
}
