package org.apache.xml.security.binding.excc14n;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/binding/excc14n/InclusiveNamespaces.class */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = org.apache.xml.security.transforms.params.InclusiveNamespaces._TAG_EC_INCLUSIVENAMESPACES, namespace = "http://www.w3.org/2001/10/xml-exc-c14n#")
public class InclusiveNamespaces {

    @XmlAttribute(name = org.apache.xml.security.transforms.params.InclusiveNamespaces._ATT_EC_PREFIXLIST)
    protected List<String> prefixList;

    public List<String> getPrefixList() {
        if (this.prefixList == null) {
            this.prefixList = new ArrayList();
        }
        return this.prefixList;
    }
}
