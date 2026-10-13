package es.gob.afirma.signers.ooxml;

import es.gob.afirma.core.AOException;
import es.gob.afirma.core.AOFormatFileException;
import es.gob.afirma.core.AOInvalidFormatException;
import es.gob.afirma.core.misc.MimeHelper;
import es.gob.afirma.core.misc.OfficeAnalizer;
import es.gob.afirma.core.signers.AOSignConstants;
import es.gob.afirma.core.signers.AOSignInfo;
import es.gob.afirma.core.signers.AOSigner;
import es.gob.afirma.core.signers.CounterSignTarget;
import es.gob.afirma.core.util.tree.AOTreeModel;
import es.gob.afirma.core.util.tree.AOTreeNode;
import es.gob.afirma.signers.ooxml.relprovider.OOXMLProvider;
import es.gob.afirma.signers.xades.AOXAdESSigner;
import es.gob.afirma.signers.xml.XmlDSigProviderHelper;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/ooxml/AOOOXMLSigner.class */
public final class AOOOXMLSigner implements AOSigner {
    static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private static final String EXTENSION_DOCX = ".docx";
    private static final String EXTENSION_XLSX = ".xlsx";
    private static final String EXTENSION_PPTX = ".pptx";
    private static final String EXTENSION_PPSX = ".ppsx";
    private static final String EXTENSION_OOXML = ".ooxml";
    private static final int THRESHOLD_FILE_SIZE = 1000000000;

    static {
        XmlDSigProviderHelper.configureXmlDSigProvider();
    }

    public AOOOXMLSigner() {
        if (System.getProperty("java.version").startsWith("1.6")) {
            throw new UnsupportedJreVersionException();
        }
        try {
            if (Security.getProvider(OOXMLProvider.RELATIONSHIP_TRANSFORM_PROVIDER_NAME) == null) {
                Security.addProvider(new OOXMLProvider());
            }
        } catch (Throwable th) {
            LOGGER.log(Level.WARNING, "Error en la instalacion del proveedor OOXML: " + th, th);
        }
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public byte[] getData(byte[] bArr) throws IOException, AOException {
        return getData(bArr, null);
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public byte[] getData(byte[] bArr, Properties properties) throws AOException {
        if (!isSign(bArr)) {
            throw new AOInvalidFormatException("El documento introducido no contiene una firma valida");
        }
        return bArr;
    }

    private static boolean isOOXMLFile(byte[] bArr) throws IOException {
        if (!isZipData(bArr)) {
            return false;
        }
        boolean z = true;
        try {
            List<ZipEntry> entryList = OOXMLUtil.getEntryList(bArr);
            if (!OOXMLUtil.hasEntry(entryList, "[Content_Types].xml") || ((!OOXMLUtil.hasEntry(entryList, "_rels/.rels") && !OOXMLUtil.hasEntry(entryList, "_rels\\.rels")) || ((!OOXMLUtil.hasEntry(entryList, "docProps/app.xml") && !OOXMLUtil.hasEntry(entryList, "docProps\\app.xml")) || (!OOXMLUtil.hasEntry(entryList, "docProps/core.xml") && !OOXMLUtil.hasEntry(entryList, "docProps\\core.xml"))))) {
                z = false;
            }
        } catch (Exception e) {
            z = false;
        }
        return z;
    }

    private static boolean isZipData(byte[] bArr) throws IOException {
        return new MimeHelper(bArr).isZipData();
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public AOSignInfo getSignInfo(byte[] bArr) throws IOException, AOException {
        return getSignInfo(bArr, null);
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public AOSignInfo getSignInfo(byte[] bArr, Properties properties) throws AOException {
        if (bArr == null) {
            throw new IllegalArgumentException("No se han introducido datos para analizar");
        }
        if (!isSign(bArr)) {
            throw new AOFormatFileException("Los datos introducidos no se corresponden con documento OOXML");
        }
        return new AOSignInfo(AOSignConstants.SIGN_FORMAT_OOXML);
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public String getSignedName(String str, String str2) {
        String str3 = str2 != null ? str2 : "";
        if (str == null) {
            return str3 + EXTENSION_OOXML;
        }
        String lowerCase = str.toLowerCase();
        if (lowerCase.endsWith(EXTENSION_DOCX)) {
            return str.substring(0, str.length() - EXTENSION_DOCX.length()) + str3 + EXTENSION_DOCX;
        }
        if (lowerCase.endsWith(EXTENSION_XLSX)) {
            return str.substring(0, str.length() - EXTENSION_XLSX.length()) + str3 + EXTENSION_XLSX;
        }
        if (lowerCase.endsWith(EXTENSION_PPTX)) {
            return str.substring(0, str.length() - EXTENSION_PPTX.length()) + str3 + EXTENSION_PPTX;
        }
        if (lowerCase.endsWith(EXTENSION_PPSX)) {
            return str.substring(0, str.length() - EXTENSION_PPSX.length()) + str3 + EXTENSION_PPSX;
        }
        return str + str3 + EXTENSION_OOXML;
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public AOTreeModel getSignersStructure(byte[] bArr, boolean z) throws IOException {
        return getSignersStructure(bArr, null, z);
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public AOTreeModel getSignersStructure(byte[] bArr, Properties properties, boolean z) throws IOException {
        if (bArr == null) {
            throw new IllegalArgumentException("Los datos de firma introducidos son nulos");
        }
        if (bArr.length >= THRESHOLD_FILE_SIZE) {
            throw new IOException("El archivo tiene un tamano superior al permitido.");
        }
        if (!isSign(bArr)) {
            LOGGER.severe("La firma indicada no es de tipo OOXML");
            return null;
        }
        AOXAdESSigner aOXAdESSigner = new AOXAdESSigner();
        AOTreeNode aOTreeNode = new AOTreeNode("Datos");
        try {
            for (byte[] bArr2 : OOXMLUtil.getOOXMLSignatures(bArr)) {
                aOTreeNode.add(((AOTreeNode) aOXAdESSigner.getSignersStructure(bArr2, z).getRoot()).getChildAt(0));
            }
            return new AOTreeModel(aOTreeNode);
        } catch (Exception e) {
            LOGGER.severe("La estructura de una de las firmas elementales no es valida: " + e);
            return null;
        }
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public boolean isSign(byte[] bArr) {
        return isSign(bArr, null);
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public boolean isSign(byte[] bArr, Properties properties) {
        if (bArr == null) {
            LOGGER.warning("Se ha introducido una firma nula para su comprobacion");
            return false;
        }
        try {
            return isOOXMLFile(bArr) && OOXMLUtil.countOOXMLSignatures(bArr) > 0;
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
        try {
            return isOOXMLFile(bArr);
        } catch (Exception e) {
            return false;
        }
    }

    @Override // es.gob.afirma.core.signers.AOSimpleSigner
    public byte[] sign(byte[] bArr, String str, PrivateKey privateKey, Certificate[] certificateArr, Properties properties) throws IOException, AOException {
        if (bArr.length >= THRESHOLD_FILE_SIZE) {
            throw new IOException("El archivo tiene un tamano superior al permitido.");
        }
        if (!OfficeAnalizer.isOOXMLDocument(bArr)) {
            throw new AOFormatFileException("Los datos introducidos no se corresponden con un documento OOXML");
        }
        if (certificateArr == null || certificateArr.length < 1) {
            throw new IllegalArgumentException("Debe proporcionarse a menos el certificado del firmante");
        }
        return signOOXML(bArr, str, privateKey, new X509Certificate[]{(X509Certificate) certificateArr[0]}, properties != null ? properties : new Properties());
    }

    @Override // es.gob.afirma.core.signers.AOCoSigner
    public byte[] cosign(byte[] bArr, String str, PrivateKey privateKey, Certificate[] certificateArr, Properties properties) throws IOException, AOException {
        return sign(bArr, str, privateKey, certificateArr, properties);
    }

    @Override // es.gob.afirma.core.signers.AOCoSigner
    public byte[] cosign(byte[] bArr, byte[] bArr2, String str, PrivateKey privateKey, Certificate[] certificateArr, Properties properties) throws IOException, AOException {
        return cosign(bArr2, str, privateKey, certificateArr, properties);
    }

    @Override // es.gob.afirma.core.signers.AOCounterSigner
    public byte[] countersign(byte[] bArr, String str, CounterSignTarget counterSignTarget, Object[] objArr, PrivateKey privateKey, Certificate[] certificateArr, Properties properties) {
        throw new UnsupportedOperationException("No es posible realizar contrafirmas de ficheros OOXML");
    }

    private static byte[] signOOXML(byte[] bArr, String str, PrivateKey privateKey, X509Certificate[] x509CertificateArr, Properties properties) throws AOException {
        if (privateKey == null) {
            throw new IllegalArgumentException("No se ha proporcionado una clave valida");
        }
        try {
            return OOXMLZipHelper.outputSignedOfficeOpenXMLDocument(bArr, OOXMLXAdESSigner.getSignedXML(bArr, str, privateKey, x509CertificateArr, properties));
        } catch (AOException e) {
            throw e;
        } catch (Exception e2) {
            throw new AOException("Error durante la firma OOXML: " + e2, e2);
        }
    }
}
