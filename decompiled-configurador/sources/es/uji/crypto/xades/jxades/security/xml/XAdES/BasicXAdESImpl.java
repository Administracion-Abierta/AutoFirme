package es.uji.crypto.xades.jxades.security.xml.XAdES;

import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;
import javax.xml.crypto.MarshalException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/uji/crypto/xades/jxades/security/xml/XAdES/BasicXAdESImpl.class */
public class BasicXAdESImpl extends BaseXAdESImpl implements XAdES_BES {
    protected boolean readOnlyMode;
    protected TreeMap<XAdES.Element, Object> data;
    private final Element baseElement;
    private Document baseDocument;
    private QualifyingProperties qualifyingProperties;
    public String xadesPrefix;
    public String xadesNamespace;
    public String xmlSignaturePrefix;
    public String digestMethod;

    public BasicXAdESImpl(Document document, Element baseElement, boolean readOnlyMode, String xadesPrefix, String xadesNamespace, String xmlSignaturePrefix, String digestMethod) {
        this.readOnlyMode = true;
        this.baseDocument = document;
        if (baseElement != null) {
            this.baseDocument = baseElement.getOwnerDocument();
        }
        this.baseElement = baseElement;
        this.readOnlyMode = readOnlyMode;
        this.data = new TreeMap<>();
        this.xadesPrefix = xadesPrefix;
        this.xadesNamespace = xadesNamespace;
        this.xmlSignaturePrefix = xmlSignaturePrefix;
        this.digestMethod = digestMethod;
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.XAdESBase
    public Document getBaseDocument() {
        return this.baseDocument;
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.XAdESBase
    public Element getBaseElement() {
        return this.baseElement;
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.XAdESBase
    public String getDigestMethod() {
        return this.digestMethod;
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.XAdESBase
    public Date getSigningTime() {
        return (Date) this.data.get(XAdES.Element.SIGNING_TIME);
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.XadesWithBasicAttributes
    public SigningCertificate getSigningCertificate() {
        return (SigningCertificate) this.data.get(XAdES.Element.SIGNING_CERTIFICATE);
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.XadesWithBasicAttributes
    public SignatureProductionPlace getSignatureProductionPlace() {
        return (SignatureProductionPlace) this.data.get(XAdES.Element.SIGNATURE_PRODUCTION_PLACE);
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.XadesWithBasicAttributes
    public SignerRole getSignerRole() {
        return (SignerRole) this.data.get(XAdES.Element.SIGNER_ROLE);
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.XAdESBase
    public Signer getSigner() {
        return (Signer) this.data.get(XAdES.Element.SIGNER);
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.XAdESBase
    public List<DataObjectFormat> getDataObjectFormats() {
        return (List) this.data.get(XAdES.Element.DATA_OBJECT_FORMATS);
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.XAdESBase
    public List<CommitmentTypeIndication> getCommitmentTypeIndications() {
        return (List) this.data.get(XAdES.Element.COMMITMENT_TYPE_INDICATIONS);
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.XAdESBase
    public List<AllDataObjectsTimeStamp> getAllDataObjectsTimeStamps() {
        return (List) this.data.get(XAdES.Element.ALL_DATA_OBJECTS_TIMESTAMPS);
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.XAdESBase
    public List<XAdESTimeStamp> getIndividualDataObjectsTimeStamps() {
        return (List) this.data.get(XAdES.Element.INDIVIDUAL_DATA_OBJECTS_TIMESTAMPS);
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.XAdESBase
    public List<CounterSignature> getCounterSignatures() {
        return (List) this.data.get(XAdES.Element.COUNTER_SIGNATURES);
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.XAdESBase
    public void setSigningTime(Date signingTime) {
        if (this.readOnlyMode) {
            throw new UnsupportedOperationException("Set Method is not allowed. Read-only mode.");
        }
        if (signingTime != null) {
            this.data.put(XAdES.Element.SIGNING_TIME, signingTime);
        } else {
            this.data.remove(XAdES.Element.SIGNING_TIME);
        }
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.XadesWithBasicAttributes
    public void setSigningCertificate(X509Certificate certificate) {
        if (this.readOnlyMode) {
            throw new UnsupportedOperationException("Set Method is not allowed. Read-only mode.");
        }
        if (certificate == null) {
            this.data.remove(XAdES.Element.SIGNING_CERTIFICATE);
        } else {
            SigningCertificateImpl sci = new SigningCertificateImpl(certificate, this.digestMethod);
            this.data.put(XAdES.Element.SIGNING_CERTIFICATE, sci);
        }
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.XadesWithBasicAttributes
    public void setSignatureProductionPlace(SignatureProductionPlace productionPlace) {
        if (this.readOnlyMode) {
            throw new UnsupportedOperationException("Set Method is not allowed. Read-only mode.");
        }
        if (productionPlace != null) {
            this.data.put(XAdES.Element.SIGNATURE_PRODUCTION_PLACE, productionPlace);
        } else {
            this.data.remove(XAdES.Element.SIGNATURE_PRODUCTION_PLACE);
        }
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.XadesWithBasicAttributes
    public void setSignerRole(SignerRole signerRole) {
        if (this.readOnlyMode) {
            throw new UnsupportedOperationException("Set Method is not allowed. Read-only mode.");
        }
        if (signerRole != null) {
            this.data.put(XAdES.Element.SIGNER_ROLE, signerRole);
        } else {
            this.data.remove(XAdES.Element.SIGNER_ROLE);
        }
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.XAdESBase
    public void setSigner(Signer signer) {
        if (this.readOnlyMode) {
            throw new UnsupportedOperationException("Set Method is not allowed. Read-only mode.");
        }
        if (signer != null) {
            this.data.put(XAdES.Element.SIGNER, signer);
        } else {
            this.data.remove(XAdES.Element.SIGNER);
        }
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.XAdESBase
    public void setDataObjectFormats(List<DataObjectFormat> dataObjectFormats) {
        if (this.readOnlyMode) {
            throw new UnsupportedOperationException("Set Method is not allowed. Read-only mode.");
        }
        if (dataObjectFormats != null && dataObjectFormats.size() > 0) {
            this.data.put(XAdES.Element.DATA_OBJECT_FORMATS, dataObjectFormats);
        } else {
            this.data.remove(XAdES.Element.DATA_OBJECT_FORMATS);
        }
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.XAdESBase
    public void setCommitmentTypeIndications(List<CommitmentTypeIndication> commitmentTypeIndications) {
        if (this.readOnlyMode) {
            throw new UnsupportedOperationException("Set Method is not allowed. Read-only mode.");
        }
        if (commitmentTypeIndications != null && commitmentTypeIndications.size() > 0) {
            this.data.put(XAdES.Element.COMMITMENT_TYPE_INDICATIONS, commitmentTypeIndications);
        } else {
            this.data.remove(XAdES.Element.COMMITMENT_TYPE_INDICATIONS);
        }
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.XAdESBase
    public void setAllDataObjectsTimeStamps(List<AllDataObjectsTimeStamp> allDataObjectsTimeStamps) {
        if (this.readOnlyMode) {
            throw new UnsupportedOperationException("Set Method is not allowed. Read-only mode.");
        }
        if (allDataObjectsTimeStamps != null && allDataObjectsTimeStamps.size() > 0) {
            this.data.put(XAdES.Element.ALL_DATA_OBJECTS_TIMESTAMPS, allDataObjectsTimeStamps);
        } else {
            this.data.remove(XAdES.Element.ALL_DATA_OBJECTS_TIMESTAMPS);
        }
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.XAdESBase
    public void setIndividualDataObjectsTimeStamps(List<IndividualDataObjectsTimeStamp> individualDataObjectsTimeStamps) {
        if (this.readOnlyMode) {
            throw new UnsupportedOperationException("Set Method is not allowed. Read-only mode.");
        }
        if (individualDataObjectsTimeStamps != null && individualDataObjectsTimeStamps.size() > 0) {
            this.data.put(XAdES.Element.INDIVIDUAL_DATA_OBJECTS_TIMESTAMPS, individualDataObjectsTimeStamps);
        } else {
            this.data.remove(XAdES.Element.INDIVIDUAL_DATA_OBJECTS_TIMESTAMPS);
        }
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.XAdESBase
    public void setCounterSignatures(List<CounterSignature> counterSignatures) {
        if (this.readOnlyMode) {
            throw new UnsupportedOperationException("Set Method is not allowed. Read-only mode.");
        }
        if (counterSignatures != null && counterSignatures.size() > 0) {
            this.data.put(XAdES.Element.COUNTER_SIGNATURES, counterSignatures);
        } else {
            this.data.remove(XAdES.Element.COUNTER_SIGNATURES);
        }
    }

    protected XAdES getXAdESType() {
        return XAdES.BES;
    }

    protected QualifyingProperties getQualifyingProperties() {
        NodeList nl;
        if (this.qualifyingProperties == null && (nl = this.baseElement.getElementsByTagNameNS(this.xadesNamespace, XAdES.Element.QUALIFYING_PROPERTIES.getElementName())) != null && nl.getLength() > 0) {
            this.qualifyingProperties = new QualifyingProperties(nl.item(0), this.xadesPrefix, this.xadesNamespace, this.xmlSignaturePrefix);
        }
        return this.qualifyingProperties;
    }

    protected SignedSignatureProperties getSignedSignatureProperties(QualifyingProperties qp) {
        return qp.getSignedProperties().getSignedSignatureProperties();
    }

    protected SignedDataObjectProperties getSignedDataObjectProperties(QualifyingProperties qp) {
        return qp.getSignedProperties().getSignedDataObjectProperties();
    }

    protected UnsignedSignatureProperties getUnsignedSignatureProperties(QualifyingProperties qp) {
        return qp.getUnsignedProperties().getUnsignedSignatureProperties();
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.MarshalException */
    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.BaseXAdESImpl
    protected void marshalQualifyingProperties(QualifyingProperties qp, String signatureIdPrefix, List referencesIdList) throws MarshalException {
        try {
            for (XAdES.Element key : XAdES.Element.values()) {
                if (XAdES.Element.SIGNING_TIME.equals(key)) {
                    SignedSignatureProperties ssp = getSignedSignatureProperties(qp);
                    ssp.setSigningTime();
                } else {
                    Object value = this.data.get(key);
                    if (value != null) {
                        if (XAdES.Element.SIGNER.equals(key)) {
                            SignedSignatureProperties ssp2 = getSignedSignatureProperties(qp);
                            ssp2.setSigner((Signer) value);
                        } else if (XAdES.Element.SIGNING_CERTIFICATE.equals(key)) {
                            SignedSignatureProperties ssp3 = getSignedSignatureProperties(qp);
                            ssp3.setSigningCertificate((SigningCertificate) value);
                        } else if (XAdES.Element.SIGNATURE_POLICY_IDENTIFIER.equals(key)) {
                            SignedSignatureProperties ssp4 = getSignedSignatureProperties(qp);
                            ssp4.setSignaturePolicyIdentifier((SignaturePolicyIdentifier) value);
                        } else if (XAdES.Element.SIGNATURE_PRODUCTION_PLACE.equals(key)) {
                            SignedSignatureProperties ssp5 = getSignedSignatureProperties(qp);
                            ssp5.setSignatureProductionPlace((SignatureProductionPlace) value);
                        } else if (XAdES.Element.SIGNER_ROLE.equals(key)) {
                            SignedSignatureProperties ssp6 = getSignedSignatureProperties(qp);
                            ssp6.setSignerRole((SignerRole) value);
                        } else if (XAdES.Element.DATA_OBJECT_FORMATS.equals(key)) {
                            SignedDataObjectProperties sdop = getSignedDataObjectProperties(qp);
                            sdop.setDataObjectFormat((ArrayList) value);
                        } else if (XAdES.Element.COMMITMENT_TYPE_INDICATIONS.equals(key)) {
                            SignedDataObjectProperties sdop2 = getSignedDataObjectProperties(qp);
                            sdop2.setCommitmentTypeIndications((ArrayList) value);
                        } else if (XAdES.Element.COMPLETE_CERTIFICATE_REFS.equals(key)) {
                            UnsignedSignatureProperties usp = getUnsignedSignatureProperties(qp);
                            usp.setCompleteCertificateRefs((Collection) value, signatureIdPrefix);
                        }
                    }
                }
            }
        } catch (GeneralSecurityException ex) {
            throw new MarshalException(ex);
        }
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.XAdESBase
    public String getXadesPrefix() {
        return this.xadesPrefix;
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.XAdESBase
    public String getXmlSignaturePrefix() {
        return this.xmlSignaturePrefix;
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.XAdESBase
    public String getXadesNamespace() {
        return this.xadesNamespace;
    }
}
