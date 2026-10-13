package es.gob.afirma.signers.xades;

import es.gob.afirma.core.AOException;
import es.gob.afirma.core.AOInvalidFormatException;
import es.gob.afirma.core.signers.AOSignConstants;
import es.gob.afirma.core.signers.AOSignInfo;
import es.gob.afirma.core.signers.AOSigner;
import es.gob.afirma.core.signers.AdESPolicy;
import es.gob.afirma.core.signers.CounterSignTarget;
import es.gob.afirma.core.util.tree.AOTreeModel;
import es.gob.afirma.signers.xml.Utils;
import es.gob.afirma.signers.xml.XmlDSigProviderHelper;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/xades/AOFacturaESigner.class */
public final class AOFacturaESigner implements AOSigner {
    public static final AdESPolicy POLICY_FACTURAE_31 = new AdESPolicy("http://www.facturae.es/politica_de_firma_formato_facturae/politica_de_firma_formato_facturae_v3_1.pdf", "Ohixl6upD6av8N7pEvDABhEL6hM=", AOSignConstants.DIGEST_ALGORITHM_SHA1, null);
    public static final AdESPolicy POLICY_FACTURAE_30 = new AdESPolicy("http://www.facturae.es/politica de firma formato facturae/politica de firma formato facturae v3_0.pdf", "xmfh8D/Ec/hHeE1IB4zPd61zHIY=", AOSignConstants.DIGEST_ALGORITHM_SHA1, null);
    private static final AOXAdESSigner XADES_SIGNER = new AOXAdESSigner();
    private static final Set<String> ALLOWED_PARAMS = new HashSet(5);
    private static final Properties EXTRA_PARAMS;

    static {
        XmlDSigProviderHelper.configureXmlDSigProvider();
        ALLOWED_PARAMS.add("signatureProductionCity");
        ALLOWED_PARAMS.add(XAdESExtraParams.SIGNATURE_PRODUCTION_PROVINCE);
        ALLOWED_PARAMS.add("signatureProductionPostalCode");
        ALLOWED_PARAMS.add("signatureProductionCountry");
        ALLOWED_PARAMS.add(XAdESExtraParams.XADES_NAMESPACE);
        ALLOWED_PARAMS.add(XAdESExtraParams.SIGNED_PROPERTIES_TYPE_URL);
        ALLOWED_PARAMS.add("policyIdentifier");
        ALLOWED_PARAMS.add("policyIdentifierHash");
        ALLOWED_PARAMS.add("policyIdentifierHashAlgorithm");
        ALLOWED_PARAMS.add(XAdESExtraParams.POLICY_DESCRIPTION);
        ALLOWED_PARAMS.add("policyQualifier");
        ALLOWED_PARAMS.add("signerClaimedRoles");
        ALLOWED_PARAMS.add("SignatureId");
        EXTRA_PARAMS = new Properties();
        EXTRA_PARAMS.setProperty(XAdESExtraParams.FORMAT, AOSignConstants.SIGN_FORMAT_XADES_ENVELOPED);
        EXTRA_PARAMS.setProperty(XAdESExtraParams.FACTURAE_SIGN, "true");
    }

    @Override // es.gob.afirma.core.signers.AOSimpleSigner
    public byte[] sign(byte[] bArr, String str, PrivateKey privateKey, Certificate[] certificateArr, Properties properties) throws IOException, AOException {
        if (!isValidDataFile(bArr)) {
            throw new InvalidEFacturaDataException();
        }
        if (isSign(bArr)) {
            throw new EFacturaAlreadySignedException();
        }
        return XADES_SIGNER.sign(bArr, str, privateKey, certificateArr, getFacturaEExtraParams(properties));
    }

    @Override // es.gob.afirma.core.signers.AOCoSigner
    public byte[] cosign(byte[] bArr, byte[] bArr2, String str, PrivateKey privateKey, Certificate[] certificateArr, Properties properties) {
        throw new UnsupportedOperationException("No se soporta la cofirma de facturas");
    }

    @Override // es.gob.afirma.core.signers.AOCoSigner
    public byte[] cosign(byte[] bArr, String str, PrivateKey privateKey, Certificate[] certificateArr, Properties properties) {
        throw new UnsupportedOperationException("No se soporta la cofirma de facturas");
    }

    @Override // es.gob.afirma.core.signers.AOCounterSigner
    public byte[] countersign(byte[] bArr, String str, CounterSignTarget counterSignTarget, Object[] objArr, PrivateKey privateKey, Certificate[] certificateArr, Properties properties) {
        throw new UnsupportedOperationException("No se soporta la contrafirma de facturas");
    }

    public static Properties getFacturaEExtraParams(Properties properties) {
        Properties properties2 = (Properties) EXTRA_PARAMS.clone();
        if (properties != null) {
            String lowerCase = properties.getProperty("signerClaimedRoles", "emisor").toLowerCase();
            if (!"emisor".equals(lowerCase) && !"receptor".equals(lowerCase) && !"tercero".equals(lowerCase) && !"supplier".equals(lowerCase) && !"customer".equals(lowerCase) && !"third party".equals(lowerCase)) {
                throw new IllegalArgumentException("El papel '" + lowerCase + "' no es valido para una factura electronica");
            }
            properties.put("signerClaimedRoles", lowerCase);
            String property = properties.getProperty("policyIdentifier");
            if (property != null) {
                AdESPolicy adESPolicy = new AdESPolicy(property, properties.getProperty("policyIdentifierHash"), properties.getProperty("policyIdentifierHashAlgorithm"), properties.getProperty("policyQualifier"));
                if (!POLICY_FACTURAE_31.equals(adESPolicy) && !POLICY_FACTURAE_30.equals(adESPolicy)) {
                    throw new IllegalArgumentException("La politica no esta soportada (solo se soporta FacturaE 3.0 y 3.1): " + adESPolicy);
                }
            } else {
                properties.remove("policyQualifier");
                properties.putAll(POLICY_FACTURAE_31.asExtraParams());
            }
            for (Object obj : properties.keySet()) {
                if (ALLOWED_PARAMS.contains(obj)) {
                    properties2.put(obj, properties.get(obj));
                }
            }
        }
        return properties2;
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public AOTreeModel getSignersStructure(byte[] bArr, Properties properties, boolean z) throws IOException, AOInvalidFormatException {
        return XADES_SIGNER.getSignersStructure(bArr, z);
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public AOTreeModel getSignersStructure(byte[] bArr, boolean z) throws IOException, AOInvalidFormatException {
        return getSignersStructure(bArr, null, z);
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public boolean isSign(byte[] bArr) throws IOException {
        return isSign(bArr, null);
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public boolean isSign(byte[] bArr, Properties properties) throws IOException {
        if (bArr == null || bArr.length == 0) {
            return false;
        }
        try {
            return isSign(Utils.getNewDocumentBuilder().parse(new ByteArrayInputStream(bArr)));
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isSign(Document document) {
        return isValidDataFile(document) && AOXAdESSigner.isSign(document);
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public boolean isValidDataFile(byte[] bArr) {
        if (bArr == null || bArr.length == 0) {
            return false;
        }
        try {
            return isValidDataFile(Utils.getNewDocumentBuilder().parse(new ByteArrayInputStream(bArr)));
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isValidDataFile(Document document) {
        if (document == null) {
            return false;
        }
        try {
            Element documentElement = document.getDocumentElement();
            if (!"Facturae".equals(documentElement.getLocalName())) {
                return false;
            }
            HashSet hashSet = new HashSet(3);
            hashSet.add("FileHeader");
            hashSet.add("Parties");
            hashSet.add("Invoices");
            NodeList childNodes = documentElement.getChildNodes();
            for (int i = 0; i < childNodes.getLength(); i++) {
                String localName = childNodes.item(i).getLocalName();
                if (hashSet.contains(localName)) {
                    hashSet.remove(localName);
                }
            }
            if (hashSet.size() > 0) {
                return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public String getSignedName(String str, String str2) {
        return XADES_SIGNER.getSignedName(str, str2);
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public byte[] getData(byte[] bArr) throws IOException, AOException {
        return getData(bArr, null);
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public byte[] getData(byte[] bArr, Properties properties) throws IOException, AOException {
        return XADES_SIGNER.getData(bArr);
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public AOSignInfo getSignInfo(byte[] bArr) throws IOException, AOException {
        return getSignInfo(bArr, null);
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public AOSignInfo getSignInfo(byte[] bArr, Properties properties) {
        AOSignInfo aOSignInfo = new AOSignInfo(AOSignConstants.SIGN_FORMAT_FACTURAE);
        aOSignInfo.setVariant(null);
        return aOSignInfo;
    }
}
