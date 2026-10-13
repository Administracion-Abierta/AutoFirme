package org.apache.xml.security.binding.xmlenc11;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;
import org.apache.xml.security.utils.EncryptionConstants;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/binding/xmlenc11/ObjectFactory.class */
@XmlRegistry
public class ObjectFactory {
    private static final QName _ConcatKDFParams_QNAME = new QName("http://www.w3.org/2009/xmlenc11#", EncryptionConstants._TAG_CONCATKDFPARAMS);
    private static final QName _DerivedKey_QNAME = new QName("http://www.w3.org/2009/xmlenc11#", "DerivedKey");
    private static final QName _KeyDerivationMethod_QNAME = new QName("http://www.w3.org/2009/xmlenc11#", EncryptionConstants._TAG_KEYDERIVATIONMETHOD);
    private static final QName _PBKDF2Params_QNAME = new QName("http://www.w3.org/2009/xmlenc11#", "PBKDF2-params");
    private static final QName _MGF_QNAME = new QName("http://www.w3.org/2009/xmlenc11#", EncryptionConstants._TAG_MGF);

    public PBKDF2ParameterType createPBKDF2ParameterType() {
        return new PBKDF2ParameterType();
    }

    public ConcatKDFParamsType createConcatKDFParamsType() {
        return new ConcatKDFParamsType();
    }

    public DerivedKeyType createDerivedKeyType() {
        return new DerivedKeyType();
    }

    public KeyDerivationMethodType createKeyDerivationMethodType() {
        return new KeyDerivationMethodType();
    }

    public MGFType createMGFType() {
        return new MGFType();
    }

    public AlgorithmIdentifierType createAlgorithmIdentifierType() {
        return new AlgorithmIdentifierType();
    }

    public PRFAlgorithmIdentifierType createPRFAlgorithmIdentifierType() {
        return new PRFAlgorithmIdentifierType();
    }

    public PBKDF2ParameterType.Salt createPBKDF2ParameterTypeSalt() {
        return new PBKDF2ParameterType.Salt();
    }

    @XmlElementDecl(namespace = "http://www.w3.org/2009/xmlenc11#", name = EncryptionConstants._TAG_CONCATKDFPARAMS)
    public JAXBElement<ConcatKDFParamsType> createConcatKDFParams(ConcatKDFParamsType value) {
        return new JAXBElement<>(_ConcatKDFParams_QNAME, ConcatKDFParamsType.class, null, value);
    }

    @XmlElementDecl(namespace = "http://www.w3.org/2009/xmlenc11#", name = "DerivedKey")
    public JAXBElement<DerivedKeyType> createDerivedKey(DerivedKeyType value) {
        return new JAXBElement<>(_DerivedKey_QNAME, DerivedKeyType.class, null, value);
    }

    @XmlElementDecl(namespace = "http://www.w3.org/2009/xmlenc11#", name = EncryptionConstants._TAG_KEYDERIVATIONMETHOD)
    public JAXBElement<KeyDerivationMethodType> createKeyDerivationMethod(KeyDerivationMethodType value) {
        return new JAXBElement<>(_KeyDerivationMethod_QNAME, KeyDerivationMethodType.class, null, value);
    }

    @XmlElementDecl(namespace = "http://www.w3.org/2009/xmlenc11#", name = "PBKDF2-params")
    public JAXBElement<PBKDF2ParameterType> createPBKDF2Params(PBKDF2ParameterType value) {
        return new JAXBElement<>(_PBKDF2Params_QNAME, PBKDF2ParameterType.class, null, value);
    }

    @XmlElementDecl(namespace = "http://www.w3.org/2009/xmlenc11#", name = EncryptionConstants._TAG_MGF)
    public JAXBElement<MGFType> createMGF(MGFType value) {
        return new JAXBElement<>(_MGF_QNAME, MGFType.class, null, value);
    }
}
