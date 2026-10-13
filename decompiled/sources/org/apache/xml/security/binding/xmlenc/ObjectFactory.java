package org.apache.xml.security.binding.xmlenc;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;
import java.math.BigInteger;
import javax.xml.namespace.QName;
import org.apache.xml.security.binding.xmldsig.KeyInfoType;
import org.apache.xml.security.utils.EncryptionConstants;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/binding/xmlenc/ObjectFactory.class */
@XmlRegistry
public class ObjectFactory {
    private static final QName _CipherData_QNAME = new QName("http://www.w3.org/2001/04/xmlenc#", EncryptionConstants._TAG_CIPHERDATA);
    private static final QName _CipherValue_QNAME = new QName("http://www.w3.org/2001/04/xmlenc#", EncryptionConstants._TAG_CIPHERVALUE);
    private static final QName _CipherReference_QNAME = new QName("http://www.w3.org/2001/04/xmlenc#", EncryptionConstants._TAG_CIPHERREFERENCE);
    private static final QName _EncryptedData_QNAME = new QName("http://www.w3.org/2001/04/xmlenc#", "EncryptedData");
    private static final QName _EncryptedKey_QNAME = new QName("http://www.w3.org/2001/04/xmlenc#", EncryptionConstants._TAG_ENCRYPTEDKEY);
    private static final QName _AgreementMethod_QNAME = new QName("http://www.w3.org/2001/04/xmlenc#", EncryptionConstants._TAG_AGREEMENTMETHOD);
    private static final QName _EncryptionProperties_QNAME = new QName("http://www.w3.org/2001/04/xmlenc#", EncryptionConstants._TAG_ENCRYPTIONPROPERTIES);
    private static final QName _EncryptionProperty_QNAME = new QName("http://www.w3.org/2001/04/xmlenc#", EncryptionConstants._TAG_ENCRYPTIONPROPERTY);
    private static final QName _ReferenceListDataReference_QNAME = new QName("http://www.w3.org/2001/04/xmlenc#", EncryptionConstants._TAG_DATAREFERENCE);
    private static final QName _ReferenceListKeyReference_QNAME = new QName("http://www.w3.org/2001/04/xmlenc#", EncryptionConstants._TAG_KEYREFERENCE);
    private static final QName _EncryptionMethodTypeKeySize_QNAME = new QName("http://www.w3.org/2001/04/xmlenc#", EncryptionConstants._TAG_KEYSIZE);
    private static final QName _EncryptionMethodTypeOAEPparams_QNAME = new QName("http://www.w3.org/2001/04/xmlenc#", EncryptionConstants._TAG_OAEPPARAMS);
    private static final QName _AgreementMethodTypeKANonce_QNAME = new QName("http://www.w3.org/2001/04/xmlenc#", EncryptionConstants._TAG_KA_NONCE);
    private static final QName _AgreementMethodTypeOriginatorKeyInfo_QNAME = new QName("http://www.w3.org/2001/04/xmlenc#", EncryptionConstants._TAG_ORIGINATORKEYINFO);
    private static final QName _AgreementMethodTypeRecipientKeyInfo_QNAME = new QName("http://www.w3.org/2001/04/xmlenc#", EncryptionConstants._TAG_RECIPIENTKEYINFO);

    public CipherDataType createCipherDataType() {
        return new CipherDataType();
    }

    public CipherValueType createCipherValueType() {
        return new CipherValueType();
    }

    public CipherReferenceType createCipherReferenceType() {
        return new CipherReferenceType();
    }

    public EncryptedDataType createEncryptedDataType() {
        return new EncryptedDataType();
    }

    public EncryptedKeyType createEncryptedKeyType() {
        return new EncryptedKeyType();
    }

    public AgreementMethodType createAgreementMethodType() {
        return new AgreementMethodType();
    }

    public ReferenceList createReferenceList() {
        return new ReferenceList();
    }

    public ReferenceType createReferenceType() {
        return new ReferenceType();
    }

    public EncryptionPropertiesType createEncryptionPropertiesType() {
        return new EncryptionPropertiesType();
    }

    public EncryptionPropertyType createEncryptionPropertyType() {
        return new EncryptionPropertyType();
    }

    public EncryptionMethodType createEncryptionMethodType() {
        return new EncryptionMethodType();
    }

    public TransformsType createTransformsType() {
        return new TransformsType();
    }

    @XmlElementDecl(namespace = "http://www.w3.org/2001/04/xmlenc#", name = EncryptionConstants._TAG_CIPHERDATA)
    public JAXBElement<CipherDataType> createCipherData(CipherDataType value) {
        return new JAXBElement<>(_CipherData_QNAME, CipherDataType.class, null, value);
    }

    @XmlElementDecl(namespace = "http://www.w3.org/2001/04/xmlenc#", name = EncryptionConstants._TAG_CIPHERVALUE)
    public JAXBElement<CipherValueType> createCipherValue(CipherValueType value) {
        return new JAXBElement<>(_CipherValue_QNAME, CipherValueType.class, null, value);
    }

    @XmlElementDecl(namespace = "http://www.w3.org/2001/04/xmlenc#", name = EncryptionConstants._TAG_CIPHERREFERENCE)
    public JAXBElement<CipherReferenceType> createCipherReference(CipherReferenceType value) {
        return new JAXBElement<>(_CipherReference_QNAME, CipherReferenceType.class, null, value);
    }

    @XmlElementDecl(namespace = "http://www.w3.org/2001/04/xmlenc#", name = "EncryptedData")
    public JAXBElement<EncryptedDataType> createEncryptedData(EncryptedDataType value) {
        return new JAXBElement<>(_EncryptedData_QNAME, EncryptedDataType.class, null, value);
    }

    @XmlElementDecl(namespace = "http://www.w3.org/2001/04/xmlenc#", name = EncryptionConstants._TAG_ENCRYPTEDKEY)
    public JAXBElement<EncryptedKeyType> createEncryptedKey(EncryptedKeyType value) {
        return new JAXBElement<>(_EncryptedKey_QNAME, EncryptedKeyType.class, null, value);
    }

    @XmlElementDecl(namespace = "http://www.w3.org/2001/04/xmlenc#", name = EncryptionConstants._TAG_AGREEMENTMETHOD)
    public JAXBElement<AgreementMethodType> createAgreementMethod(AgreementMethodType value) {
        return new JAXBElement<>(_AgreementMethod_QNAME, AgreementMethodType.class, null, value);
    }

    @XmlElementDecl(namespace = "http://www.w3.org/2001/04/xmlenc#", name = EncryptionConstants._TAG_ENCRYPTIONPROPERTIES)
    public JAXBElement<EncryptionPropertiesType> createEncryptionProperties(EncryptionPropertiesType value) {
        return new JAXBElement<>(_EncryptionProperties_QNAME, EncryptionPropertiesType.class, null, value);
    }

    @XmlElementDecl(namespace = "http://www.w3.org/2001/04/xmlenc#", name = EncryptionConstants._TAG_ENCRYPTIONPROPERTY)
    public JAXBElement<EncryptionPropertyType> createEncryptionProperty(EncryptionPropertyType value) {
        return new JAXBElement<>(_EncryptionProperty_QNAME, EncryptionPropertyType.class, null, value);
    }

    @XmlElementDecl(namespace = "http://www.w3.org/2001/04/xmlenc#", name = EncryptionConstants._TAG_DATAREFERENCE, scope = ReferenceList.class)
    public JAXBElement<ReferenceType> createReferenceListDataReference(ReferenceType value) {
        return new JAXBElement<>(_ReferenceListDataReference_QNAME, ReferenceType.class, ReferenceList.class, value);
    }

    @XmlElementDecl(namespace = "http://www.w3.org/2001/04/xmlenc#", name = EncryptionConstants._TAG_KEYREFERENCE, scope = ReferenceList.class)
    public JAXBElement<ReferenceType> createReferenceListKeyReference(ReferenceType value) {
        return new JAXBElement<>(_ReferenceListKeyReference_QNAME, ReferenceType.class, ReferenceList.class, value);
    }

    @XmlElementDecl(namespace = "http://www.w3.org/2001/04/xmlenc#", name = EncryptionConstants._TAG_KEYSIZE, scope = EncryptionMethodType.class)
    public JAXBElement<BigInteger> createEncryptionMethodTypeKeySize(BigInteger value) {
        return new JAXBElement<>(_EncryptionMethodTypeKeySize_QNAME, BigInteger.class, EncryptionMethodType.class, value);
    }

    @XmlElementDecl(namespace = "http://www.w3.org/2001/04/xmlenc#", name = EncryptionConstants._TAG_OAEPPARAMS, scope = EncryptionMethodType.class)
    public JAXBElement<byte[]> createEncryptionMethodTypeOAEPparams(byte[] value) {
        return new JAXBElement<>(_EncryptionMethodTypeOAEPparams_QNAME, byte[].class, EncryptionMethodType.class, value);
    }

    @XmlElementDecl(namespace = "http://www.w3.org/2001/04/xmlenc#", name = EncryptionConstants._TAG_KA_NONCE, scope = AgreementMethodType.class)
    public JAXBElement<byte[]> createAgreementMethodTypeKANonce(byte[] value) {
        return new JAXBElement<>(_AgreementMethodTypeKANonce_QNAME, byte[].class, AgreementMethodType.class, value);
    }

    @XmlElementDecl(namespace = "http://www.w3.org/2001/04/xmlenc#", name = EncryptionConstants._TAG_ORIGINATORKEYINFO, scope = AgreementMethodType.class)
    public JAXBElement<KeyInfoType> createAgreementMethodTypeOriginatorKeyInfo(KeyInfoType value) {
        return new JAXBElement<>(_AgreementMethodTypeOriginatorKeyInfo_QNAME, KeyInfoType.class, AgreementMethodType.class, value);
    }

    @XmlElementDecl(namespace = "http://www.w3.org/2001/04/xmlenc#", name = EncryptionConstants._TAG_RECIPIENTKEYINFO, scope = AgreementMethodType.class)
    public JAXBElement<KeyInfoType> createAgreementMethodTypeRecipientKeyInfo(KeyInfoType value) {
        return new JAXBElement<>(_AgreementMethodTypeRecipientKeyInfo_QNAME, KeyInfoType.class, AgreementMethodType.class, value);
    }
}
