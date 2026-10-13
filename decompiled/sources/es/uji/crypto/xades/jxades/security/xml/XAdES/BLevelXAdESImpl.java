package es.uji.crypto.xades.jxades.security.xml.XAdES;

import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;
import javax.xml.crypto.MarshalException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/* JADX INFO: loaded from: autofirma.jar:es/uji/crypto/xades/jxades/security/xml/XAdES/BLevelXAdESImpl.class */
public class BLevelXAdESImpl extends BaseXAdESImpl implements XAdES_B_Level {
    protected boolean readOnlyMode;
    protected TreeMap<XAdES.Element, Object> data;
    private final Element baseElement;
    private Document baseDocument;
    private QualifyingProperties qualifyingProperties;
    public String xadesPrefix;
    public String xadesNamespace;
    public String xmlSignaturePrefix;
    public String digestMethod;

    public BLevelXAdESImpl(Document document, Element baseElement, boolean readOnlyMode, String xadesPrefix, String xadesNamespace, String xmlSignaturePrefix, String digestMethod) {
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

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.XadesWithBaselineAttributes
    public SigningCertificateV2 getSigningCertificateV2() {
        return (SigningCertificateV2) this.data.get(XAdES.Element.SIGNING_CERTIFICATE_V2);
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.XadesWithBaselineAttributes
    public SignatureProductionPlaceV2 getSignatureProductionPlaceV2() {
        return (SignatureProductionPlaceV2) this.data.get(XAdES.Element.SIGNATURE_PRODUCTION_PLACE_V2);
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.XadesWithBaselineAttributes
    public SignerRoleV2 getSignerRoleV2() {
        return (SignerRoleV2) this.data.get(XAdES.Element.SIGNER_ROLE_V2);
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

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.XadesWithBaselineAttributes
    public void setSigningCertificateV2(X509Certificate signingCertificate, SigningCertificateV2Info additionalInfo) {
        if (this.readOnlyMode) {
            throw new UnsupportedOperationException("Set Method is not allowed. Read-only mode.");
        }
        if (signingCertificate == null) {
            this.data.remove(XAdES.Element.SIGNING_CERTIFICATE_V2);
            return;
        }
        SigningCertificateV2Impl sci = new SigningCertificateV2Impl(signingCertificate, this.digestMethod);
        sci.setIssuerSerialV2(additionalInfo != null ? additionalInfo.getIssuerSerialV2() : null);
        this.data.put(XAdES.Element.SIGNING_CERTIFICATE_V2, sci);
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.XadesWithBaselineAttributes
    public void setSignatureProductionPlaceV2(SignatureProductionPlaceV2 productionPlace) {
        if (this.readOnlyMode) {
            throw new UnsupportedOperationException("Set Method is not allowed. Read-only mode.");
        }
        if (productionPlace != null) {
            this.data.put(XAdES.Element.SIGNATURE_PRODUCTION_PLACE_V2, productionPlace);
        } else {
            this.data.remove(XAdES.Element.SIGNATURE_PRODUCTION_PLACE_V2);
        }
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.XadesWithBaselineAttributes
    public void setSignerRoleV2(SignerRoleV2 signerRole) {
        if (this.readOnlyMode) {
            throw new UnsupportedOperationException("Set Method is not allowed. Read-only mode.");
        }
        if (signerRole != null) {
            this.data.put(XAdES.Element.SIGNER_ROLE_V2, signerRole);
        } else {
            this.data.remove(XAdES.Element.SIGNER_ROLE_V2);
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
        return XAdES.B_LEVEL;
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
                        } else if (XAdES.Element.SIGNING_CERTIFICATE_V2.equals(key)) {
                            SignedSignatureProperties ssp3 = getSignedSignatureProperties(qp);
                            ssp3.setSigningCertificateV2((SigningCertificateV2) value);
                        } else if (XAdES.Element.SIGNATURE_POLICY_IDENTIFIER.equals(key)) {
                            SignedSignatureProperties ssp4 = getSignedSignatureProperties(qp);
                            ssp4.setSignaturePolicyIdentifier((SignaturePolicyIdentifier) value);
                        } else if (XAdES.Element.SIGNATURE_PRODUCTION_PLACE_V2.equals(key)) {
                            SignedSignatureProperties ssp5 = getSignedSignatureProperties(qp);
                            ssp5.setSignatureProductionPlaceV2((SignatureProductionPlaceV2) value);
                        } else if (XAdES.Element.SIGNER_ROLE_V2.equals(key)) {
                            SignedSignatureProperties ssp6 = getSignedSignatureProperties(qp);
                            ssp6.setSignerRoleV2((SignerRoleV2) value);
                        } else if (XAdES.Element.DATA_OBJECT_FORMATS.equals(key)) {
                            SignedDataObjectProperties sdop = getSignedDataObjectProperties(qp);
                            sdop.setDataObjectFormat((ArrayList) value);
                        } else if (XAdES.Element.COMMITMENT_TYPE_INDICATIONS.equals(key)) {
                            SignedDataObjectProperties sdop2 = getSignedDataObjectProperties(qp);
                            sdop2.setCommitmentTypeIndications((ArrayList) value);
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

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.XadesWithExplicitPolicy
    public void setSignaturePolicyIdentifier(SignaturePolicyIdentifier signaturePolicyIdentifier) {
        if (this.readOnlyMode) {
            throw new UnsupportedOperationException("Set Method is not allowed. Read-only mode.");
        }
        if (signaturePolicyIdentifier != null) {
            this.data.put(XAdES.Element.SIGNATURE_POLICY_IDENTIFIER, signaturePolicyIdentifier);
        } else {
            this.data.remove(XAdES.Element.SIGNATURE_POLICY_IDENTIFIER);
        }
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.XadesWithExplicitPolicy
    public SignaturePolicyIdentifier getSignaturePolicyIdentifier() {
        return (SignaturePolicyIdentifier) this.data.get(XAdES.Element.SIGNATURE_POLICY_IDENTIFIER);
    }
}
