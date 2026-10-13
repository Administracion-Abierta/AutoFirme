package org.apache.xml.security.binding.xmldsig;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlElementRefs;
import jakarta.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;
import org.apache.xml.security.utils.Constants;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/binding/xmldsig/X509DataType.class */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "X509DataType", namespace = "http://www.w3.org/2000/09/xmldsig#", propOrder = {"x509IssuerSerialOrX509SKIOrX509SubjectName"})
public class X509DataType {

    @XmlElementRefs({@XmlElementRef(name = Constants._TAG_X509ISSUERSERIAL, namespace = "http://www.w3.org/2000/09/xmldsig#", type = JAXBElement.class, required = false), @XmlElementRef(name = Constants._TAG_X509SKI, namespace = "http://www.w3.org/2000/09/xmldsig#", type = JAXBElement.class, required = false), @XmlElementRef(name = Constants._TAG_X509SUBJECTNAME, namespace = "http://www.w3.org/2000/09/xmldsig#", type = JAXBElement.class, required = false), @XmlElementRef(name = "X509Certificate", namespace = "http://www.w3.org/2000/09/xmldsig#", type = JAXBElement.class, required = false), @XmlElementRef(name = Constants._TAG_X509CRL, namespace = "http://www.w3.org/2000/09/xmldsig#", type = JAXBElement.class, required = false)})
    @XmlAnyElement(lax = true)
    protected List<Object> x509IssuerSerialOrX509SKIOrX509SubjectName;

    public List<Object> getX509IssuerSerialOrX509SKIOrX509SubjectName() {
        if (this.x509IssuerSerialOrX509SKIOrX509SubjectName == null) {
            this.x509IssuerSerialOrX509SKIOrX509SubjectName = new ArrayList();
        }
        return this.x509IssuerSerialOrX509SKIOrX509SubjectName;
    }
}
