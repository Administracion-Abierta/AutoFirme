package es.gob.afirma.standalone.signdetails;

import es.gob.afirma.core.AOInvalidFormatException;
import es.gob.afirma.core.misc.MimeHelper;
import es.gob.afirma.core.signers.AOSignConstants;
import es.gob.afirma.core.signers.AdESPolicy;
import es.gob.afirma.core.util.tree.AOTreeModel;
import es.gob.afirma.signers.cades.CAdESAttributes;
import es.gob.afirma.signvalidation.CertHolderBySignerIdSelector;
import es.gob.afirma.signvalidation.SignValidity;
import es.gob.afirma.signvalidation.SignatureFormatDetectorPadesCades;
import es.gob.afirma.signvalidation.ValidateBinarySignature;
import es.gob.afirma.standalone.SimpleAfirmaMessages;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;
import org.apache.xml.security.keys.content.x509.XMLX509Certificate;
import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.ASN1GeneralizedTime;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.ASN1UTCTime;
import org.spongycastle.asn1.cms.Attribute;
import org.spongycastle.asn1.cms.AttributeTable;
import org.spongycastle.asn1.cms.CMSAttributes;
import org.spongycastle.asn1.esf.SignaturePolicyId;
import org.spongycastle.asn1.esf.SignerAttribute;
import org.spongycastle.asn1.esf.SignerLocation;
import org.spongycastle.asn1.ess.ContentHints;
import org.spongycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.spongycastle.cert.X509CertificateHolder;
import org.spongycastle.cms.CMSException;
import org.spongycastle.cms.CMSSignedData;
import org.spongycastle.cms.CMSSignerDigestMismatchException;
import org.spongycastle.cms.DefaultCMSSignatureAlgorithmNameGenerator;
import org.spongycastle.cms.SignerInformation;
import org.spongycastle.cms.SignerInformationStore;
import org.spongycastle.cms.SignerInformationVerifier;
import org.spongycastle.jce.provider.BouncyCastleProvider;
import org.spongycastle.operator.DefaultSignatureAlgorithmIdentifierFinder;
import org.spongycastle.operator.OperatorCreationException;
import org.spongycastle.operator.bc.BcDigestCalculatorProvider;
import org.spongycastle.operator.jcajce.JcaContentVerifierProviderBuilder;
import org.spongycastle.util.Store;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/signdetails/CAdESSignAnalyzer.class */
public class CAdESSignAnalyzer implements SignAnalyzer {
    static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private List<SignDetails> signDetailsList;
    AOTreeModel signersTree;
    private CMSSignedData cmsSignedData;
    private Properties oidMimetypeProp;
    private static final String CADES = "CAdES";

    public CAdESSignAnalyzer(byte[] bArr) throws Exception {
        try {
            this.signDetailsList = new ArrayList();
            this.signersTree = SignAnalyzer.getSignInfo(bArr).getSignsTree();
            loadOidMimetypeProperties();
            createSignDetails(bArr);
        } catch (Exception e) {
            throw new AOInvalidFormatException("No se ha podido cargar el documento XML de firmas", e);
        }
    }

    @Override // es.gob.afirma.standalone.signdetails.SignAnalyzer
    public AOTreeModel getSignersTree() {
        return this.signersTree;
    }

    @Override // es.gob.afirma.standalone.signdetails.SignAnalyzer
    public List<SignDetails> getAllSignDetails() {
        return this.signDetailsList;
    }

    @Override // es.gob.afirma.standalone.signdetails.SignAnalyzer
    public String getSignFormat() {
        return "CAdES";
    }

    @Override // es.gob.afirma.standalone.signdetails.SignAnalyzer
    public String getDataLocation() {
        if (this.cmsSignedData.getSignedContent() != null && this.cmsSignedData.getSignedContent().getContent() != null) {
            return SimpleAfirmaMessages.getString("ValidationInfoDialog.21");
        }
        return SimpleAfirmaMessages.getString("ValidationInfoDialog.22");
    }

    private void createSignDetails(byte[] bArr) throws Exception {
        this.cmsSignedData = new CMSSignedData(bArr);
        Iterator<SignerInformation> it = this.cmsSignedData.getSignerInfos().getSigners().iterator();
        while (it.hasNext()) {
            buildSignDetails(it.next());
        }
    }

    private void buildSignDetails(SignerInformation signerInformation) throws Exception {
        SignDetails signDetails = new SignDetails();
        AttributeTable signedAttributes = signerInformation.getSignedAttributes();
        String id = null;
        Attribute attribute = signedAttributes.get(CMSAttributes.contentHint);
        if (attribute != null && attribute.getAttributeValues() != null && attribute.getAttributeValues().length >= 1) {
            id = ContentHints.getInstance(attribute.getAttributeValues()[0]).getContentType().getId();
        }
        String strResolveASN1Format = SignatureFormatDetectorPadesCades.resolveASN1Format(this.cmsSignedData, signerInformation);
        signDetails.setSignProfile(strResolveASN1Format);
        if (id != null) {
            Object obj = this.oidMimetypeProp.get(id);
            if (obj != null) {
                signDetails.getDataObjectFormats().add(new DataObjectFormat((String) obj));
            } else {
                signDetails.getDataObjectFormats().add(new DataObjectFormat(id));
            }
        }
        Attribute attribute2 = signedAttributes.get(CMSAttributes.signingTime);
        if (attribute2 != null) {
            signDetails.setSigningTime(parseSigningTime(attribute2));
        }
        analyzePolicy(signerInformation, signDetails);
        Attribute attribute3 = signerInformation.getSignedAttributes().get(PKCSObjectIdentifiers.id_aa_ets_signerAttr);
        if (attribute3 == null) {
            attribute3 = signerInformation.getSignedAttributes().get(new ASN1ObjectIdentifier(CAdESAttributes.OID_id_aa_ets_signerAttrV2));
        }
        if (attribute3 != null) {
            org.spongycastle.asn1.x509.Attribute[] attributeArr = (org.spongycastle.asn1.x509.Attribute[]) SignerAttribute.getInstance(attribute3.getAttrValues().getObjectAt(0)).getValues()[0];
            for (int i = 0; i < attributeArr.length; i++) {
                signDetails.getMetadata().put("claimedRole" + i, attributeArr[i].getAttrValues().getObjectAt(0).toString());
            }
        }
        Attribute attribute4 = signerInformation.getSignedAttributes().get(PKCSObjectIdentifiers.id_aa_ets_signerLocation);
        if (attribute4 != null) {
            SignerLocation signerLocation = SignerLocation.getInstance(attribute4.getAttrValues().getObjectAt(0));
            if (signerLocation.getCountry() != null) {
                signDetails.getMetadata().put(SimpleAfirmaMessages.getString("ValidationInfoDialog.9"), signerLocation.getCountry().toString());
            }
            if (signerLocation.getPostal() != null && signerLocation.getPostal().length > 0) {
                signDetails.getMetadata().put(SimpleAfirmaMessages.getString("ValidationInfoDialog.8"), signerLocation.getPostal()[0].toString());
            }
            if (signerLocation.getLocality() != null) {
                signDetails.getMetadata().put(SimpleAfirmaMessages.getString("ValidationInfoDialog.23"), signerLocation.getLocality().toString());
            }
        }
        Store<X509CertificateHolder> certificates = this.cmsSignedData.getCertificates();
        CertificateFactory certificateFactory = CertificateFactory.getInstance(XMLX509Certificate.JCA_CERT_ID);
        X509Certificate x509Certificate = (X509Certificate) certificateFactory.generateCertificate(new ByteArrayInputStream(certificates.getMatches(new CertHolderBySignerIdSelector(signerInformation.getSID())).iterator().next().getEncoded()));
        signDetails.setSigner(new CertificateDetails(x509Certificate));
        checkCorrectSignerInformation(signDetails, x509Certificate, signerInformation);
        signDetails.setAlgorithm(AOSignConstants.composeSignatureAlgorithmName(signerInformation.getDigestAlgorithmID().getAlgorithm().toString(), x509Certificate.getPublicKey().getAlgorithm()));
        Iterator<SignValidity> it = ValidateBinarySignature.verifySign(signerInformation, certificates, certificateFactory, false, strResolveASN1Format, this.cmsSignedData.getSignedContent() != null).iterator();
        while (it.hasNext()) {
            signDetails.getValidityResult().add(it.next());
        }
        this.signDetailsList.add(signDetails);
        SignerInformationStore counterSignatures = signerInformation.getCounterSignatures();
        if (counterSignatures != null && counterSignatures.size() > 0) {
            Iterator it2 = ((List) counterSignatures.getSigners()).iterator();
            while (it2.hasNext()) {
                buildSignDetails((SignerInformation) it2.next());
            }
        }
    }

    private void loadOidMimetypeProperties() throws IOException {
        this.oidMimetypeProp = new Properties();
        InputStream resourceAsStream = MimeHelper.class.getClassLoader().getResourceAsStream("resources/mimetypes_oids.properties");
        Throwable th = null;
        try {
            try {
                if (resourceAsStream == null) {
                    throw new IOException("No se ha encontrado el fichero de recursos para la relacion entre OID y MimeType");
                }
                this.oidMimetypeProp.load(resourceAsStream);
                if (resourceAsStream != null) {
                    if (0 != 0) {
                        try {
                            resourceAsStream.close();
                            return;
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                            return;
                        }
                    }
                    resourceAsStream.close();
                }
            } catch (Throwable th3) {
                throw th3;
            }
        } catch (Throwable th4) {
            if (resourceAsStream != null) {
                if (0 != 0) {
                    try {
                        resourceAsStream.close();
                    } catch (Throwable th5) {
                        th.addSuppressed(th5);
                    }
                } else {
                    resourceAsStream.close();
                }
            }
            throw th4;
        }
    }

    private static void analyzePolicy(SignerInformation signerInformation, SignDetails signDetails) {
        Attribute attribute = signerInformation.getSignedAttributes().get(PKCSObjectIdentifiers.id_aa_ets_sigPolicyId);
        if (attribute != null && attribute.getAttrValues() != null && attribute.getAttrValues().size() > 0) {
            SignaturePolicyId signaturePolicyId = SignaturePolicyId.getInstance(attribute.getAttrValues().getObjectAt(0));
            String string = signaturePolicyId.getSigPolicyId().toString();
            if (string.equals(SignDetails.POLICY_CADES_AGE_1_9.getPolicyIdentifier().substring(8))) {
                signDetails.setPolicy(new SignaturePolicy(SimpleAfirmaMessages.getString("PreferencesPanel.73"), SignDetails.POLICY_CADES_AGE_1_9));
                return;
            }
            if (string.equals(SignDetails.POLICY_CADES_AGE_1_8.getPolicyIdentifier().substring(8))) {
                signDetails.setPolicy(new SignaturePolicy(SimpleAfirmaMessages.getString("PreferencesPanel.25"), SignDetails.POLICY_CADES_AGE_1_8));
                return;
            }
            String string2 = signaturePolicyId.getSigPolicyHash().getHashValue().toString();
            signDetails.setPolicy(new SignaturePolicy("", new AdESPolicy(string, string2.substring(1), signaturePolicyId.getSigPolicyHash().getHashAlgorithm().getAlgorithm().toString(), signaturePolicyId.getSigPolicyQualifiers().getInfoAt(0).getSigQualifier().toString())));
        }
    }

    private static Date parseSigningTime(Attribute attribute) {
        Date date = null;
        ASN1Encodable objectAt = attribute.getAttrValues().getObjectAt(0);
        if (objectAt == null) {
            LOGGER.severe("El objeto no contiene una fecha");
        } else if (objectAt instanceof ASN1GeneralizedTime) {
            try {
                date = ((ASN1GeneralizedTime) objectAt).getDate();
            } catch (ParseException e) {
                LOGGER.severe("No es posible convertir la fecha: " + e);
            }
        } else if (objectAt instanceof ASN1UTCTime) {
            try {
                date = ((ASN1UTCTime) objectAt).getDate();
            } catch (ParseException e2) {
                LOGGER.severe("No es posible convertir la fecha: " + e2);
            }
        } else {
            LOGGER.severe("Formato de fecha deconocido: " + objectAt.getClass().getName());
        }
        return date;
    }

    private void checkCorrectSignerInformation(SignDetails signDetails, X509Certificate x509Certificate, SignerInformation signerInformation) {
        if (signDetails.getSigner().isCorrectValidation()) {
            String string = null;
            if (signerInformation != null) {
                try {
                    if (!signerInformation.verify(new SignerInformationVerifier(new DefaultCMSSignatureAlgorithmNameGenerator(), new DefaultSignatureAlgorithmIdentifierFinder(), new JcaContentVerifierProviderBuilder().setProvider(new BouncyCastleProvider()).build(x509Certificate), new BcDigestCalculatorProvider()))) {
                        throw new CMSException("No se ha podido verificar la informacion del firmante");
                    }
                } catch (CMSSignerDigestMismatchException e) {
                } catch (CMSException e2) {
                    string = (e2.getCause() == null || !(e2.getCause() instanceof OperatorCreationException) || e2.getCause().getCause() == null || !(e2.getCause().getCause() instanceof InvalidKeyException)) ? SimpleAfirmaMessages.getString("ValidationInfoDialog.4") : SimpleAfirmaMessages.getString("ValidationInfoDialog.5");
                } catch (Exception e3) {
                    string = SimpleAfirmaMessages.getString("ValidationInfoDialog.4");
                }
            }
            if (string != null) {
                signDetails.getSigner().getValidityResult().put("Validacion", string);
            }
        }
    }
}
