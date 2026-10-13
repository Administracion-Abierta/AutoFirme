package org.apache.xml.security.binding.xmlenc11;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.HexBinaryAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.apache.xml.security.binding.xmldsig.DigestMethodType;
import org.apache.xml.security.utils.EncryptionConstants;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/binding/xmlenc11/ConcatKDFParamsType.class */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ConcatKDFParamsType", namespace = "http://www.w3.org/2009/xmlenc11#", propOrder = {"digestMethod"})
public class ConcatKDFParamsType {

    @XmlElement(name = "DigestMethod", namespace = "http://www.w3.org/2000/09/xmldsig#", required = true)
    protected DigestMethodType digestMethod;

    @XmlJavaTypeAdapter(HexBinaryAdapter.class)
    @XmlSchemaType(name = "hexBinary")
    @XmlAttribute(name = EncryptionConstants._ATT_ALGORITHM_ID)
    protected byte[] algorithmID;

    @XmlJavaTypeAdapter(HexBinaryAdapter.class)
    @XmlSchemaType(name = "hexBinary")
    @XmlAttribute(name = EncryptionConstants._ATT_PARTYUINFO)
    protected byte[] partyUInfo;

    @XmlJavaTypeAdapter(HexBinaryAdapter.class)
    @XmlSchemaType(name = "hexBinary")
    @XmlAttribute(name = EncryptionConstants._ATT_PARTYVINFO)
    protected byte[] partyVInfo;

    @XmlJavaTypeAdapter(HexBinaryAdapter.class)
    @XmlSchemaType(name = "hexBinary")
    @XmlAttribute(name = EncryptionConstants._ATT_SUPPPUBINFO)
    protected byte[] suppPubInfo;

    @XmlJavaTypeAdapter(HexBinaryAdapter.class)
    @XmlSchemaType(name = "hexBinary")
    @XmlAttribute(name = EncryptionConstants._ATT_SUPPPRIVINFO)
    protected byte[] suppPrivInfo;

    public DigestMethodType getDigestMethod() {
        return this.digestMethod;
    }

    public void setDigestMethod(DigestMethodType value) {
        this.digestMethod = value;
    }

    public byte[] getAlgorithmID() {
        return this.algorithmID;
    }

    public void setAlgorithmID(byte[] value) {
        this.algorithmID = value;
    }

    public byte[] getPartyUInfo() {
        return this.partyUInfo;
    }

    public void setPartyUInfo(byte[] value) {
        this.partyUInfo = value;
    }

    public byte[] getPartyVInfo() {
        return this.partyVInfo;
    }

    public void setPartyVInfo(byte[] value) {
        this.partyVInfo = value;
    }

    public byte[] getSuppPubInfo() {
        return this.suppPubInfo;
    }

    public void setSuppPubInfo(byte[] value) {
        this.suppPubInfo = value;
    }

    public byte[] getSuppPrivInfo() {
        return this.suppPrivInfo;
    }

    public void setSuppPrivInfo(byte[] value) {
        this.suppPrivInfo = value;
    }
}
