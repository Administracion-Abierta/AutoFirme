package org.apache.xml.security.stax.securityEvent;

import java.util.ArrayList;
import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/stax/securityEvent/AbstractElementSecurityEvent.class */
public abstract class AbstractElementSecurityEvent extends SecurityEvent {
    private List<QName> elementPath;
    private XMLSecEvent xmlSecEvent;

    public AbstractElementSecurityEvent(SecurityEventConstants.Event securityEventType) {
        super(securityEventType);
    }

    public List<QName> getElementPath() {
        return this.elementPath;
    }

    public void setElementPath(List<QName> elementPath) {
        this.elementPath = new ArrayList(elementPath);
    }

    public XMLSecEvent getXmlSecEvent() {
        return this.xmlSecEvent;
    }

    public void setXmlSecEvent(XMLSecEvent xmlSecEvent) {
        this.xmlSecEvent = xmlSecEvent;
    }
}
