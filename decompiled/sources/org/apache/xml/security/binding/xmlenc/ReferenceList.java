package org.apache.xml.security.binding.xmlenc;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlElementRefs;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;
import org.apache.xml.security.utils.EncryptionConstants;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/binding/xmlenc/ReferenceList.class */
@XmlRootElement(name = EncryptionConstants._TAG_REFERENCELIST, namespace = "http://www.w3.org/2001/04/xmlenc#")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"dataReferenceOrKeyReference"})
public class ReferenceList {

    @XmlElementRefs({@XmlElementRef(name = EncryptionConstants._TAG_DATAREFERENCE, namespace = "http://www.w3.org/2001/04/xmlenc#", type = JAXBElement.class, required = false), @XmlElementRef(name = EncryptionConstants._TAG_KEYREFERENCE, namespace = "http://www.w3.org/2001/04/xmlenc#", type = JAXBElement.class, required = false)})
    protected List<JAXBElement<ReferenceType>> dataReferenceOrKeyReference;

    public List<JAXBElement<ReferenceType>> getDataReferenceOrKeyReference() {
        if (this.dataReferenceOrKeyReference == null) {
            this.dataReferenceOrKeyReference = new ArrayList();
        }
        return this.dataReferenceOrKeyReference;
    }
}
