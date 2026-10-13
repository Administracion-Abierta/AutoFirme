package org.apache.xml.security.binding.xmlenc;

import com.ctc.wstx.shaded.msv_core.scanner.dtd.DTDParser;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlID;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
import java.util.List;
import org.apache.xml.security.utils.EncryptionConstants;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/binding/xmlenc/EncryptionPropertiesType.class */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EncryptionPropertiesType", namespace = "http://www.w3.org/2001/04/xmlenc#", propOrder = {"encryptionProperty"})
public class EncryptionPropertiesType {

    @XmlElement(name = EncryptionConstants._TAG_ENCRYPTIONPROPERTY, namespace = "http://www.w3.org/2001/04/xmlenc#", required = true)
    protected List<EncryptionPropertyType> encryptionProperty;

    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = DTDParser.TYPE_ID)
    @XmlAttribute(name = "Id")
    @XmlID
    protected String id;

    public List<EncryptionPropertyType> getEncryptionProperty() {
        if (this.encryptionProperty == null) {
            this.encryptionProperty = new ArrayList();
        }
        return this.encryptionProperty;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String value) {
        this.id = value;
    }
}
