package org.apache.xml.security.binding.xmldsigmore;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;
import jakarta.xml.bind.annotation.adapters.HexBinaryAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.EncryptionConstants;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/binding/xmldsigmore/ObjectFactory.class */
@XmlRegistry
public class ObjectFactory {
    private static final QName _PKCS7SignedData_QNAME = new QName("http://www.w3.org/2001/04/xmldsig-more", "PKCS7signedData");
    private static final QName _RSAPSSParams_QNAME = new QName("http://www.w3.org/2007/05/xmldsig-more#", Constants._TAG_RSAPSSPARAMS);
    private static final QName _Nonce_QNAME = new QName(Constants.XML_DSIG_NS_MORE_21_04, "Nonce");
    private static final QName _Counter_QNAME = new QName(Constants.XML_DSIG_NS_MORE_21_04, "Counter");
    private static final QName _ADD_QNAME = new QName(Constants.XML_DSIG_NS_MORE_21_04, "ADD");
    private static final QName _HKDFParams_QNAME = new QName(Constants.XML_DSIG_NS_MORE_21_04, EncryptionConstants._TAG_HKDFPARAMS);

    public RSAPSSParamsType createRSAPSSParamsType() {
        return new RSAPSSParamsType();
    }

    public MaskGenerationFunctionType createMaskGenerationFunctionType() {
        return new MaskGenerationFunctionType();
    }

    public HKDFParamsType createHKDFParamsType() {
        return new HKDFParamsType();
    }

    @XmlElementDecl(namespace = "http://www.w3.org/2001/04/xmldsig-more", name = "PKCS7signedData")
    public JAXBElement<byte[]> createPKCS7SignedData(byte[] value) {
        return new JAXBElement<>(_PKCS7SignedData_QNAME, byte[].class, null, value);
    }

    @XmlElementDecl(namespace = "http://www.w3.org/2007/05/xmldsig-more#", name = Constants._TAG_RSAPSSPARAMS)
    public JAXBElement<RSAPSSParamsType> createRSAPSSParams(RSAPSSParamsType value) {
        return new JAXBElement<>(_RSAPSSParams_QNAME, RSAPSSParamsType.class, null, value);
    }

    @XmlJavaTypeAdapter(HexBinaryAdapter.class)
    @XmlElementDecl(namespace = Constants.XML_DSIG_NS_MORE_21_04, name = "Nonce")
    public JAXBElement<byte[]> createNonce(byte[] value) {
        return new JAXBElement<>(_Nonce_QNAME, byte[].class, null, value);
    }

    @XmlJavaTypeAdapter(HexBinaryAdapter.class)
    @XmlElementDecl(namespace = Constants.XML_DSIG_NS_MORE_21_04, name = "Counter")
    public JAXBElement<byte[]> createCounter(byte[] value) {
        return new JAXBElement<>(_Counter_QNAME, byte[].class, null, value);
    }

    @XmlElementDecl(namespace = Constants.XML_DSIG_NS_MORE_21_04, name = "ADD")
    public JAXBElement<String> createADD(String value) {
        return new JAXBElement<>(_ADD_QNAME, String.class, null, value);
    }

    @XmlElementDecl(namespace = Constants.XML_DSIG_NS_MORE_21_04, name = EncryptionConstants._TAG_HKDFPARAMS)
    public JAXBElement<HKDFParamsType> createHKDFParams(HKDFParamsType value) {
        return new JAXBElement<>(_HKDFParams_QNAME, HKDFParamsType.class, null, value);
    }
}
