package es.gob.afirma.signers.xades.asic;

import es.gob.afirma.core.AOException;
import es.gob.afirma.core.AOInvalidFormatException;
import es.gob.afirma.core.signers.AOSignConstants;
import es.gob.afirma.core.signers.AOSignInfo;
import es.gob.afirma.core.signers.AOSigner;
import es.gob.afirma.core.signers.CounterSignTarget;
import es.gob.afirma.core.signers.asic.ASiCUtil;
import es.gob.afirma.core.util.tree.AOTreeModel;
import es.gob.afirma.signers.xades.AOXAdESSigner;
import es.gob.afirma.signers.xades.XAdESExtraParams;
import es.gob.afirma.signers.xml.XmlDSigProviderHelper;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Logger;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/xades/asic/AOXAdESASiCSSigner.class */
public final class AOXAdESASiCSSigner implements AOSigner {
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");

    static {
        XmlDSigProviderHelper.configureXmlDSigProvider();
    }

    @Override // es.gob.afirma.core.signers.AOSimpleSigner
    public byte[] sign(byte[] bArr, String str, PrivateKey privateKey, Certificate[] certificateArr, Properties properties) throws IOException, AOException {
        Properties aSiCProperties = setASiCProperties(properties, bArr);
        aSiCProperties.put(XAdESExtraParams.KEEP_KEYINFO_UNSIGNED, Boolean.TRUE.toString());
        aSiCProperties.put(XAdESExtraParams.FORMAT, AOSignConstants.SIGN_FORMAT_XADES_EXTERNALLY_DETACHED);
        try {
            return ASiCUtil.createSContainer(new AOXAdESSigner().sign(hash(bArr, aSiCProperties.getProperty("precalculatedHashAlgorithm", aSiCProperties.getProperty(XAdESExtraParams.REFERENCES_DIGEST_METHOD, "http://www.w3.org/2001/04/xmlenc#sha512"))), str, privateKey, certificateArr, aSiCProperties), bArr, ASiCUtil.ENTRY_NAME_XML_SIGNATURE, aSiCProperties.getProperty(XAdESASiCExtraParams.ASICS_FILENAME));
        } catch (Exception e) {
            throw new AOException("No se reconoce el algoritmo de huella digital", e);
        }
    }

    private static byte[] hash(byte[] bArr, String str) throws NoSuchAlgorithmException {
        return MessageDigest.getInstance(AOSignConstants.getDigestAlgorithmName(str)).digest(bArr);
    }

    @Override // es.gob.afirma.core.signers.AOCoSigner
    public byte[] cosign(byte[] bArr, byte[] bArr2, String str, PrivateKey privateKey, Certificate[] certificateArr, Properties properties) throws IOException, AOException {
        byte[] aSiCSData = ASiCUtil.getASiCSData(bArr2);
        byte[] aSiCSXMLSignature = ASiCUtil.getASiCSXMLSignature(bArr2);
        Properties aSiCProperties = setASiCProperties(properties, aSiCSData);
        aSiCProperties.put(XAdESExtraParams.KEEP_KEYINFO_UNSIGNED, Boolean.TRUE.toString());
        HashMap map = new HashMap();
        map.put("dataobject.xml", aSiCSData);
        AOXAdESSigner aOXAdESSigner = new AOXAdESSigner();
        aOXAdESSigner.setUriDereferencer(new UriAndDataDereferencer(map));
        return ASiCUtil.createSContainer(aOXAdESSigner.cosign(aSiCSData, aSiCSXMLSignature, str, privateKey, certificateArr, aSiCProperties), aSiCSData, ASiCUtil.ENTRY_NAME_XML_SIGNATURE, ASiCUtil.getASiCSDataFilename(bArr2));
    }

    @Override // es.gob.afirma.core.signers.AOCoSigner
    public byte[] cosign(byte[] bArr, String str, PrivateKey privateKey, Certificate[] certificateArr, Properties properties) throws IOException, AOException {
        return cosign(null, bArr, str, privateKey, certificateArr, properties);
    }

    @Override // es.gob.afirma.core.signers.AOCounterSigner
    public byte[] countersign(byte[] bArr, String str, CounterSignTarget counterSignTarget, Object[] objArr, PrivateKey privateKey, Certificate[] certificateArr, Properties properties) throws IOException, AOException {
        byte[] aSiCSData = ASiCUtil.getASiCSData(bArr);
        byte[] aSiCSXMLSignature = ASiCUtil.getASiCSXMLSignature(bArr);
        Properties aSiCProperties = setASiCProperties(properties, aSiCSData);
        aSiCProperties.put(XAdESExtraParams.KEEP_KEYINFO_UNSIGNED, Boolean.TRUE.toString());
        return ASiCUtil.createSContainer(new AOXAdESSigner().countersign(aSiCSXMLSignature, str, counterSignTarget, objArr, privateKey, certificateArr, aSiCProperties), aSiCSData, ASiCUtil.ENTRY_NAME_XML_SIGNATURE, ASiCUtil.getASiCSDataFilename(bArr));
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public AOTreeModel getSignersStructure(byte[] bArr, boolean z) throws IOException, AOInvalidFormatException {
        return getSignersStructure(bArr, null, z);
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public AOTreeModel getSignersStructure(byte[] bArr, Properties properties, boolean z) throws IOException, AOInvalidFormatException {
        return new AOXAdESSigner().getSignersStructure(ASiCUtil.getASiCSXMLSignature(bArr), z);
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public boolean isSign(byte[] bArr) throws IOException {
        return isSign(bArr, null);
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public boolean isSign(byte[] bArr, Properties properties) throws IOException {
        try {
            return new AOXAdESSigner().isSign(ASiCUtil.getASiCSXMLSignature(bArr));
        } catch (Exception e) {
            return false;
        }
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public boolean isValidDataFile(byte[] bArr) {
        if (bArr == null) {
            LOGGER.warning("Se han introducido datos nulos para su comprobacion");
            return false;
        }
        return true;
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public String getSignedName(String str, String str2) {
        return str + (str2 != null ? str2 : "") + ".asics";
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public byte[] getData(byte[] bArr) throws IOException, AOException {
        return getData(bArr, null);
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public byte[] getData(byte[] bArr, Properties properties) throws IOException {
        return ASiCUtil.getASiCSData(bArr);
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public AOSignInfo getSignInfo(byte[] bArr) throws IOException, AOException {
        return getSignInfo(bArr, null);
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public AOSignInfo getSignInfo(byte[] bArr, Properties properties) throws IOException, AOException {
        return new AOXAdESSigner().getSignInfo(ASiCUtil.getASiCSXMLSignature(bArr));
    }

    public static Properties setASiCProperties(Properties properties, byte[] bArr) {
        String aSiCSDefaultDataFilename;
        Properties properties2 = properties != null ? properties : new Properties();
        if (properties2.getProperty(XAdESASiCExtraParams.ASICS_FILENAME) != null) {
            aSiCSDefaultDataFilename = properties2.getProperty(XAdESASiCExtraParams.ASICS_FILENAME);
        } else {
            aSiCSDefaultDataFilename = ASiCUtil.getASiCSDefaultDataFilename(bArr);
        }
        properties2.put(XAdESExtraParams.ADD_KEY_INFO_KEY_NAME, Boolean.TRUE.toString());
        properties2.put("uri", aSiCSDefaultDataFilename);
        properties2.put(XAdESExtraParams.ROOT_XML_NODE_NAME, "asic:XAdESSignatures");
        properties2.put(XAdESExtraParams.ROOT_XML_NODE_NAMESPACE, "http://uri.etsi.org/02918/v1.2.1#");
        properties2.put(XAdESExtraParams.ROOT_XML_NODE_NAMESPACE_PREFIX, "xmlns:asic");
        return properties2;
    }
}
