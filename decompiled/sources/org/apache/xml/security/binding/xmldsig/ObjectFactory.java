package org.apache.xml.security.binding.xmldsig;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;
import java.math.BigInteger;
import javax.xml.namespace.QName;
import org.apache.xml.security.utils.Constants;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/binding/xmldsig/ObjectFactory.class */
@XmlRegistry
public class ObjectFactory {
    private static final QName _Signature_QNAME = new QName("http://www.w3.org/2000/09/xmldsig#", "Signature");
    private static final QName _SignatureValue_QNAME = new QName("http://www.w3.org/2000/09/xmldsig#", "SignatureValue");
    private static final QName _SignedInfo_QNAME = new QName("http://www.w3.org/2000/09/xmldsig#", "SignedInfo");
    private static final QName _CanonicalizationMethod_QNAME = new QName("http://www.w3.org/2000/09/xmldsig#", Constants._TAG_CANONICALIZATIONMETHOD);
    private static final QName _SignatureMethod_QNAME = new QName("http://www.w3.org/2000/09/xmldsig#", "SignatureMethod");
    private static final QName _Reference_QNAME = new QName("http://www.w3.org/2000/09/xmldsig#", "Reference");
    private static final QName _Transforms_QNAME = new QName("http://www.w3.org/2000/09/xmldsig#", "Transforms");
    private static final QName _Transform_QNAME = new QName("http://www.w3.org/2000/09/xmldsig#", Constants._TAG_TRANSFORM);
    private static final QName _DigestMethod_QNAME = new QName("http://www.w3.org/2000/09/xmldsig#", "DigestMethod");
    private static final QName _DigestValue_QNAME = new QName("http://www.w3.org/2000/09/xmldsig#", "DigestValue");
    private static final QName _KeyInfo_QNAME = new QName("http://www.w3.org/2000/09/xmldsig#", Constants._TAG_KEYINFO);
    private static final QName _KeyName_QNAME = new QName("http://www.w3.org/2000/09/xmldsig#", Constants._TAG_KEYNAME);
    private static final QName _MgmtData_QNAME = new QName("http://www.w3.org/2000/09/xmldsig#", Constants._TAG_MGMTDATA);
    private static final QName _KeyValue_QNAME = new QName("http://www.w3.org/2000/09/xmldsig#", Constants._TAG_KEYVALUE);
    private static final QName _RetrievalMethod_QNAME = new QName("http://www.w3.org/2000/09/xmldsig#", Constants._TAG_RETRIEVALMETHOD);
    private static final QName _X509Data_QNAME = new QName("http://www.w3.org/2000/09/xmldsig#", Constants._TAG_X509DATA);
    private static final QName _PGPData_QNAME = new QName("http://www.w3.org/2000/09/xmldsig#", Constants._TAG_PGPDATA);
    private static final QName _SPKIData_QNAME = new QName("http://www.w3.org/2000/09/xmldsig#", Constants._TAG_SPKIDATA);
    private static final QName _Object_QNAME = new QName("http://www.w3.org/2000/09/xmldsig#", Constants._TAG_OBJECT);
    private static final QName _Manifest_QNAME = new QName("http://www.w3.org/2000/09/xmldsig#", Constants._TAG_MANIFEST);
    private static final QName _SignatureProperties_QNAME = new QName("http://www.w3.org/2000/09/xmldsig#", Constants._TAG_SIGNATUREPROPERTIES);
    private static final QName _SignatureProperty_QNAME = new QName("http://www.w3.org/2000/09/xmldsig#", Constants._TAG_SIGNATUREPROPERTY);
    private static final QName _DSAKeyValue_QNAME = new QName("http://www.w3.org/2000/09/xmldsig#", Constants._TAG_DSAKEYVALUE);
    private static final QName _RSAKeyValue_QNAME = new QName("http://www.w3.org/2000/09/xmldsig#", Constants._TAG_RSAKEYVALUE);
    private static final QName _SPKIDataTypeSPKISexp_QNAME = new QName("http://www.w3.org/2000/09/xmldsig#", Constants._TAG_SPKISEXP);
    private static final QName _PGPDataTypePGPKeyID_QNAME = new QName("http://www.w3.org/2000/09/xmldsig#", Constants._TAG_PGPKEYID);
    private static final QName _PGPDataTypePGPKeyPacket_QNAME = new QName("http://www.w3.org/2000/09/xmldsig#", Constants._TAG_PGPKEYPACKET);
    private static final QName _X509DataTypeX509IssuerSerial_QNAME = new QName("http://www.w3.org/2000/09/xmldsig#", Constants._TAG_X509ISSUERSERIAL);
    private static final QName _X509DataTypeX509SKI_QNAME = new QName("http://www.w3.org/2000/09/xmldsig#", Constants._TAG_X509SKI);
    private static final QName _X509DataTypeX509SubjectName_QNAME = new QName("http://www.w3.org/2000/09/xmldsig#", Constants._TAG_X509SUBJECTNAME);
    private static final QName _X509DataTypeX509Certificate_QNAME = new QName("http://www.w3.org/2000/09/xmldsig#", "X509Certificate");
    private static final QName _X509DataTypeX509CRL_QNAME = new QName("http://www.w3.org/2000/09/xmldsig#", Constants._TAG_X509CRL);
    private static final QName _TransformTypeXPath_QNAME = new QName("http://www.w3.org/2000/09/xmldsig#", "XPath");
    private static final QName _SignatureMethodTypeHMACOutputLength_QNAME = new QName("http://www.w3.org/2000/09/xmldsig#", Constants._TAG_HMACOUTPUTLENGTH);

    public SignatureType createSignatureType() {
        return new SignatureType();
    }

    public SignatureValueType createSignatureValueType() {
        return new SignatureValueType();
    }

    public SignedInfoType createSignedInfoType() {
        return new SignedInfoType();
    }

    public CanonicalizationMethodType createCanonicalizationMethodType() {
        return new CanonicalizationMethodType();
    }

    public SignatureMethodType createSignatureMethodType() {
        return new SignatureMethodType();
    }

    public ReferenceType createReferenceType() {
        return new ReferenceType();
    }

    public TransformsType createTransformsType() {
        return new TransformsType();
    }

    public TransformType createTransformType() {
        return new TransformType();
    }

    public DigestMethodType createDigestMethodType() {
        return new DigestMethodType();
    }

    public KeyInfoType createKeyInfoType() {
        return new KeyInfoType();
    }

    public KeyValueType createKeyValueType() {
        return new KeyValueType();
    }

    public RetrievalMethodType createRetrievalMethodType() {
        return new RetrievalMethodType();
    }

    public X509DataType createX509DataType() {
        return new X509DataType();
    }

    public PGPDataType createPGPDataType() {
        return new PGPDataType();
    }

    public SPKIDataType createSPKIDataType() {
        return new SPKIDataType();
    }

    public ObjectType createObjectType() {
        return new ObjectType();
    }

    public ManifestType createManifestType() {
        return new ManifestType();
    }

    public SignaturePropertiesType createSignaturePropertiesType() {
        return new SignaturePropertiesType();
    }

    public SignaturePropertyType createSignaturePropertyType() {
        return new SignaturePropertyType();
    }

    public DSAKeyValueType createDSAKeyValueType() {
        return new DSAKeyValueType();
    }

    public RSAKeyValueType createRSAKeyValueType() {
        return new RSAKeyValueType();
    }

    public X509IssuerSerialType createX509IssuerSerialType() {
        return new X509IssuerSerialType();
    }

    @XmlElementDecl(namespace = "http://www.w3.org/2000/09/xmldsig#", name = "Signature")
    public JAXBElement<SignatureType> createSignature(SignatureType value) {
        return new JAXBElement<>(_Signature_QNAME, SignatureType.class, null, value);
    }

    @XmlElementDecl(namespace = "http://www.w3.org/2000/09/xmldsig#", name = "SignatureValue")
    public JAXBElement<SignatureValueType> createSignatureValue(SignatureValueType value) {
        return new JAXBElement<>(_SignatureValue_QNAME, SignatureValueType.class, null, value);
    }

    @XmlElementDecl(namespace = "http://www.w3.org/2000/09/xmldsig#", name = "SignedInfo")
    public JAXBElement<SignedInfoType> createSignedInfo(SignedInfoType value) {
        return new JAXBElement<>(_SignedInfo_QNAME, SignedInfoType.class, null, value);
    }

    @XmlElementDecl(namespace = "http://www.w3.org/2000/09/xmldsig#", name = Constants._TAG_CANONICALIZATIONMETHOD)
    public JAXBElement<CanonicalizationMethodType> createCanonicalizationMethod(CanonicalizationMethodType value) {
        return new JAXBElement<>(_CanonicalizationMethod_QNAME, CanonicalizationMethodType.class, null, value);
    }

    @XmlElementDecl(namespace = "http://www.w3.org/2000/09/xmldsig#", name = "SignatureMethod")
    public JAXBElement<SignatureMethodType> createSignatureMethod(SignatureMethodType value) {
        return new JAXBElement<>(_SignatureMethod_QNAME, SignatureMethodType.class, null, value);
    }

    @XmlElementDecl(namespace = "http://www.w3.org/2000/09/xmldsig#", name = "Reference")
    public JAXBElement<ReferenceType> createReference(ReferenceType value) {
        return new JAXBElement<>(_Reference_QNAME, ReferenceType.class, null, value);
    }

    @XmlElementDecl(namespace = "http://www.w3.org/2000/09/xmldsig#", name = "Transforms")
    public JAXBElement<TransformsType> createTransforms(TransformsType value) {
        return new JAXBElement<>(_Transforms_QNAME, TransformsType.class, null, value);
    }

    @XmlElementDecl(namespace = "http://www.w3.org/2000/09/xmldsig#", name = Constants._TAG_TRANSFORM)
    public JAXBElement<TransformType> createTransform(TransformType value) {
        return new JAXBElement<>(_Transform_QNAME, TransformType.class, null, value);
    }

    @XmlElementDecl(namespace = "http://www.w3.org/2000/09/xmldsig#", name = "DigestMethod")
    public JAXBElement<DigestMethodType> createDigestMethod(DigestMethodType value) {
        return new JAXBElement<>(_DigestMethod_QNAME, DigestMethodType.class, null, value);
    }

    @XmlElementDecl(namespace = "http://www.w3.org/2000/09/xmldsig#", name = "DigestValue")
    public JAXBElement<byte[]> createDigestValue(byte[] value) {
        return new JAXBElement<>(_DigestValue_QNAME, byte[].class, null, value);
    }

    @XmlElementDecl(namespace = "http://www.w3.org/2000/09/xmldsig#", name = Constants._TAG_KEYINFO)
    public JAXBElement<KeyInfoType> createKeyInfo(KeyInfoType value) {
        return new JAXBElement<>(_KeyInfo_QNAME, KeyInfoType.class, null, value);
    }

    @XmlElementDecl(namespace = "http://www.w3.org/2000/09/xmldsig#", name = Constants._TAG_KEYNAME)
    public JAXBElement<String> createKeyName(String value) {
        return new JAXBElement<>(_KeyName_QNAME, String.class, null, value);
    }

    @XmlElementDecl(namespace = "http://www.w3.org/2000/09/xmldsig#", name = Constants._TAG_MGMTDATA)
    public JAXBElement<String> createMgmtData(String value) {
        return new JAXBElement<>(_MgmtData_QNAME, String.class, null, value);
    }

    @XmlElementDecl(namespace = "http://www.w3.org/2000/09/xmldsig#", name = Constants._TAG_KEYVALUE)
    public JAXBElement<KeyValueType> createKeyValue(KeyValueType value) {
        return new JAXBElement<>(_KeyValue_QNAME, KeyValueType.class, null, value);
    }

    @XmlElementDecl(namespace = "http://www.w3.org/2000/09/xmldsig#", name = Constants._TAG_RETRIEVALMETHOD)
    public JAXBElement<RetrievalMethodType> createRetrievalMethod(RetrievalMethodType value) {
        return new JAXBElement<>(_RetrievalMethod_QNAME, RetrievalMethodType.class, null, value);
    }

    @XmlElementDecl(namespace = "http://www.w3.org/2000/09/xmldsig#", name = Constants._TAG_X509DATA)
    public JAXBElement<X509DataType> createX509Data(X509DataType value) {
        return new JAXBElement<>(_X509Data_QNAME, X509DataType.class, null, value);
    }

    @XmlElementDecl(namespace = "http://www.w3.org/2000/09/xmldsig#", name = Constants._TAG_PGPDATA)
    public JAXBElement<PGPDataType> createPGPData(PGPDataType value) {
        return new JAXBElement<>(_PGPData_QNAME, PGPDataType.class, null, value);
    }

    @XmlElementDecl(namespace = "http://www.w3.org/2000/09/xmldsig#", name = Constants._TAG_SPKIDATA)
    public JAXBElement<SPKIDataType> createSPKIData(SPKIDataType value) {
        return new JAXBElement<>(_SPKIData_QNAME, SPKIDataType.class, null, value);
    }

    @XmlElementDecl(namespace = "http://www.w3.org/2000/09/xmldsig#", name = Constants._TAG_OBJECT)
    public JAXBElement<ObjectType> createObject(ObjectType value) {
        return new JAXBElement<>(_Object_QNAME, ObjectType.class, null, value);
    }

    @XmlElementDecl(namespace = "http://www.w3.org/2000/09/xmldsig#", name = Constants._TAG_MANIFEST)
    public JAXBElement<ManifestType> createManifest(ManifestType value) {
        return new JAXBElement<>(_Manifest_QNAME, ManifestType.class, null, value);
    }

    @XmlElementDecl(namespace = "http://www.w3.org/2000/09/xmldsig#", name = Constants._TAG_SIGNATUREPROPERTIES)
    public JAXBElement<SignaturePropertiesType> createSignatureProperties(SignaturePropertiesType value) {
        return new JAXBElement<>(_SignatureProperties_QNAME, SignaturePropertiesType.class, null, value);
    }

    @XmlElementDecl(namespace = "http://www.w3.org/2000/09/xmldsig#", name = Constants._TAG_SIGNATUREPROPERTY)
    public JAXBElement<SignaturePropertyType> createSignatureProperty(SignaturePropertyType value) {
        return new JAXBElement<>(_SignatureProperty_QNAME, SignaturePropertyType.class, null, value);
    }

    @XmlElementDecl(namespace = "http://www.w3.org/2000/09/xmldsig#", name = Constants._TAG_DSAKEYVALUE)
    public JAXBElement<DSAKeyValueType> createDSAKeyValue(DSAKeyValueType value) {
        return new JAXBElement<>(_DSAKeyValue_QNAME, DSAKeyValueType.class, null, value);
    }

    @XmlElementDecl(namespace = "http://www.w3.org/2000/09/xmldsig#", name = Constants._TAG_RSAKEYVALUE)
    public JAXBElement<RSAKeyValueType> createRSAKeyValue(RSAKeyValueType value) {
        return new JAXBElement<>(_RSAKeyValue_QNAME, RSAKeyValueType.class, null, value);
    }

    @XmlElementDecl(namespace = "http://www.w3.org/2000/09/xmldsig#", name = Constants._TAG_SPKISEXP, scope = SPKIDataType.class)
    public JAXBElement<byte[]> createSPKIDataTypeSPKISexp(byte[] value) {
        return new JAXBElement<>(_SPKIDataTypeSPKISexp_QNAME, byte[].class, SPKIDataType.class, value);
    }

    @XmlElementDecl(namespace = "http://www.w3.org/2000/09/xmldsig#", name = Constants._TAG_PGPKEYID, scope = PGPDataType.class)
    public JAXBElement<byte[]> createPGPDataTypePGPKeyID(byte[] value) {
        return new JAXBElement<>(_PGPDataTypePGPKeyID_QNAME, byte[].class, PGPDataType.class, value);
    }

    @XmlElementDecl(namespace = "http://www.w3.org/2000/09/xmldsig#", name = Constants._TAG_PGPKEYPACKET, scope = PGPDataType.class)
    public JAXBElement<byte[]> createPGPDataTypePGPKeyPacket(byte[] value) {
        return new JAXBElement<>(_PGPDataTypePGPKeyPacket_QNAME, byte[].class, PGPDataType.class, value);
    }

    @XmlElementDecl(namespace = "http://www.w3.org/2000/09/xmldsig#", name = Constants._TAG_X509ISSUERSERIAL, scope = X509DataType.class)
    public JAXBElement<X509IssuerSerialType> createX509DataTypeX509IssuerSerial(X509IssuerSerialType value) {
        return new JAXBElement<>(_X509DataTypeX509IssuerSerial_QNAME, X509IssuerSerialType.class, X509DataType.class, value);
    }

    @XmlElementDecl(namespace = "http://www.w3.org/2000/09/xmldsig#", name = Constants._TAG_X509SKI, scope = X509DataType.class)
    public JAXBElement<byte[]> createX509DataTypeX509SKI(byte[] value) {
        return new JAXBElement<>(_X509DataTypeX509SKI_QNAME, byte[].class, X509DataType.class, value);
    }

    @XmlElementDecl(namespace = "http://www.w3.org/2000/09/xmldsig#", name = Constants._TAG_X509SUBJECTNAME, scope = X509DataType.class)
    public JAXBElement<String> createX509DataTypeX509SubjectName(String value) {
        return new JAXBElement<>(_X509DataTypeX509SubjectName_QNAME, String.class, X509DataType.class, value);
    }

    @XmlElementDecl(namespace = "http://www.w3.org/2000/09/xmldsig#", name = "X509Certificate", scope = X509DataType.class)
    public JAXBElement<byte[]> createX509DataTypeX509Certificate(byte[] value) {
        return new JAXBElement<>(_X509DataTypeX509Certificate_QNAME, byte[].class, X509DataType.class, value);
    }

    @XmlElementDecl(namespace = "http://www.w3.org/2000/09/xmldsig#", name = Constants._TAG_X509CRL, scope = X509DataType.class)
    public JAXBElement<byte[]> createX509DataTypeX509CRL(byte[] value) {
        return new JAXBElement<>(_X509DataTypeX509CRL_QNAME, byte[].class, X509DataType.class, value);
    }

    @XmlElementDecl(namespace = "http://www.w3.org/2000/09/xmldsig#", name = "XPath", scope = TransformType.class)
    public JAXBElement<String> createTransformTypeXPath(String value) {
        return new JAXBElement<>(_TransformTypeXPath_QNAME, String.class, TransformType.class, value);
    }

    @XmlElementDecl(namespace = "http://www.w3.org/2000/09/xmldsig#", name = Constants._TAG_HMACOUTPUTLENGTH, scope = SignatureMethodType.class)
    public JAXBElement<BigInteger> createSignatureMethodTypeHMACOutputLength(BigInteger value) {
        return new JAXBElement<>(_SignatureMethodTypeHMACOutputLength_QNAME, BigInteger.class, SignatureMethodType.class, value);
    }
}
