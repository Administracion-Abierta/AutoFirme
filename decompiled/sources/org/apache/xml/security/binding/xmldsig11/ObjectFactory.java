package org.apache.xml.security.binding.xmldsig11;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;
import org.apache.xml.security.utils.Constants;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/binding/xmldsig11/ObjectFactory.class */
@XmlRegistry
public class ObjectFactory {
    private static final QName _ECKeyValue_QNAME = new QName("http://www.w3.org/2009/xmldsig11#", Constants._TAG_ECKEYVALUE);
    private static final QName _Prime_QNAME = new QName("http://www.w3.org/2009/xmldsig11#", "Prime");
    private static final QName _GnB_QNAME = new QName("http://www.w3.org/2009/xmldsig11#", "GnB");
    private static final QName _TnB_QNAME = new QName("http://www.w3.org/2009/xmldsig11#", "TnB");
    private static final QName _PnB_QNAME = new QName("http://www.w3.org/2009/xmldsig11#", "PnB");
    private static final QName _OCSPResponse_QNAME = new QName("http://www.w3.org/2009/xmldsig11#", "OCSPResponse");
    private static final QName _DEREncodedKeyValue_QNAME = new QName("http://www.w3.org/2009/xmldsig11#", Constants._TAG_DERENCODEDKEYVALUE);
    private static final QName _KeyInfoReference_QNAME = new QName("http://www.w3.org/2009/xmldsig11#", Constants._TAG_KEYINFOREFERENCE);
    private static final QName _X509Digest_QNAME = new QName("http://www.w3.org/2009/xmldsig11#", Constants._TAG_X509DIGEST);

    public ECKeyValueType createECKeyValueType() {
        return new ECKeyValueType();
    }

    public PrimeFieldParamsType createPrimeFieldParamsType() {
        return new PrimeFieldParamsType();
    }

    public CharTwoFieldParamsType createCharTwoFieldParamsType() {
        return new CharTwoFieldParamsType();
    }

    public TnBFieldParamsType createTnBFieldParamsType() {
        return new TnBFieldParamsType();
    }

    public PnBFieldParamsType createPnBFieldParamsType() {
        return new PnBFieldParamsType();
    }

    public DEREncodedKeyValueType createDEREncodedKeyValueType() {
        return new DEREncodedKeyValueType();
    }

    public KeyInfoReferenceType createKeyInfoReferenceType() {
        return new KeyInfoReferenceType();
    }

    public X509DigestType createX509DigestType() {
        return new X509DigestType();
    }

    public NamedCurveType createNamedCurveType() {
        return new NamedCurveType();
    }

    public ECParametersType createECParametersType() {
        return new ECParametersType();
    }

    public FieldIDType createFieldIDType() {
        return new FieldIDType();
    }

    public CurveType createCurveType() {
        return new CurveType();
    }

    public ECValidationDataType createECValidationDataType() {
        return new ECValidationDataType();
    }

    @XmlElementDecl(namespace = "http://www.w3.org/2009/xmldsig11#", name = Constants._TAG_ECKEYVALUE)
    public JAXBElement<ECKeyValueType> createECKeyValue(ECKeyValueType value) {
        return new JAXBElement<>(_ECKeyValue_QNAME, ECKeyValueType.class, null, value);
    }

    @XmlElementDecl(namespace = "http://www.w3.org/2009/xmldsig11#", name = "Prime")
    public JAXBElement<PrimeFieldParamsType> createPrime(PrimeFieldParamsType value) {
        return new JAXBElement<>(_Prime_QNAME, PrimeFieldParamsType.class, null, value);
    }

    @XmlElementDecl(namespace = "http://www.w3.org/2009/xmldsig11#", name = "GnB")
    public JAXBElement<CharTwoFieldParamsType> createGnB(CharTwoFieldParamsType value) {
        return new JAXBElement<>(_GnB_QNAME, CharTwoFieldParamsType.class, null, value);
    }

    @XmlElementDecl(namespace = "http://www.w3.org/2009/xmldsig11#", name = "TnB")
    public JAXBElement<TnBFieldParamsType> createTnB(TnBFieldParamsType value) {
        return new JAXBElement<>(_TnB_QNAME, TnBFieldParamsType.class, null, value);
    }

    @XmlElementDecl(namespace = "http://www.w3.org/2009/xmldsig11#", name = "PnB")
    public JAXBElement<PnBFieldParamsType> createPnB(PnBFieldParamsType value) {
        return new JAXBElement<>(_PnB_QNAME, PnBFieldParamsType.class, null, value);
    }

    @XmlElementDecl(namespace = "http://www.w3.org/2009/xmldsig11#", name = "OCSPResponse")
    public JAXBElement<byte[]> createOCSPResponse(byte[] value) {
        return new JAXBElement<>(_OCSPResponse_QNAME, byte[].class, null, value);
    }

    @XmlElementDecl(namespace = "http://www.w3.org/2009/xmldsig11#", name = Constants._TAG_DERENCODEDKEYVALUE)
    public JAXBElement<DEREncodedKeyValueType> createDEREncodedKeyValue(DEREncodedKeyValueType value) {
        return new JAXBElement<>(_DEREncodedKeyValue_QNAME, DEREncodedKeyValueType.class, null, value);
    }

    @XmlElementDecl(namespace = "http://www.w3.org/2009/xmldsig11#", name = Constants._TAG_KEYINFOREFERENCE)
    public JAXBElement<KeyInfoReferenceType> createKeyInfoReference(KeyInfoReferenceType value) {
        return new JAXBElement<>(_KeyInfoReference_QNAME, KeyInfoReferenceType.class, null, value);
    }

    @XmlElementDecl(namespace = "http://www.w3.org/2009/xmldsig11#", name = Constants._TAG_X509DIGEST)
    public JAXBElement<X509DigestType> createX509Digest(X509DigestType value) {
        return new JAXBElement<>(_X509Digest_QNAME, X509DigestType.class, null, value);
    }
}
