package es.gob.afirma.massive;

import es.gob.afirma.core.AOException;
import es.gob.afirma.core.AOFormatFileException;
import es.gob.afirma.core.misc.AOUtil;
import es.gob.afirma.core.misc.LoggerUtil;
import es.gob.afirma.core.misc.MimeHelper;
import es.gob.afirma.core.signers.AOSignConstants;
import es.gob.afirma.core.signers.AOSigner;
import es.gob.afirma.core.signers.AOSignerFactory;
import es.gob.afirma.core.signers.CounterSignTarget;
import es.gob.afirma.standalone.CommandLineParameters;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/massive/MassiveSignatureHelper.class */
public final class MassiveSignatureHelper {
    private static final String CADES_SIGNER = "es.gob.afirma.signers.cades.AOCAdESSigner";
    private static final String XADES_SIGNER = "es.gob.afirma.signers.xades.AOXAdESSigner";
    private static final String XMLDSIG_SIGNER = "es.gob.afirma.signers.xmldsig.AOXMLDSigSigner";
    private static final String REG_FIELD_SEPARATOR = " - ";
    private static final String MODE = "mode";
    private static final String FORMAT = "format";
    private static final String MIME_TYPE = "mimeType";
    private MassiveSignConfiguration massiveConfiguration;
    private List<String> log = null;
    private AOSigner defaultSigner;
    private AOSigner massiveSignSigner;
    private boolean enabled;
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private static final AOException SIGN_DATA_NOT_GENERATED = new AOException("No se generaron datos de firma");
    private static MessageDigest md = null;

    public MassiveSignatureHelper(MassiveSignConfiguration massiveSignConfiguration) throws AOException {
        this.massiveConfiguration = null;
        this.defaultSigner = null;
        this.massiveSignSigner = null;
        this.enabled = false;
        if (massiveSignConfiguration == null) {
            throw new IllegalArgumentException("La configuracion de firma masiva no puede ser nula");
        }
        if (massiveSignConfiguration.getMassiveOperation() == null) {
            throw new IllegalArgumentException("La configuracion indicada no tiene establecida ninguna operacion masiva");
        }
        this.massiveConfiguration = massiveSignConfiguration;
        this.massiveConfiguration.setSignatureFormat(this.massiveConfiguration.getDefaultFormat());
        this.enabled = true;
        this.defaultSigner = AOSignerFactory.getSigner(this.massiveConfiguration.getDefaultFormat());
        if (this.defaultSigner == null) {
            throw new AOException("Formato de firma no soportado: " + this.massiveConfiguration.getDefaultFormat());
        }
        this.massiveSignSigner = this.defaultSigner;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void disable() {
        this.massiveConfiguration = null;
        this.defaultSigner = null;
        this.enabled = false;
    }

    public void setMassiveOperation(MassiveType massiveType) {
        this.massiveConfiguration.setMassiveOperation(massiveType != null ? massiveType : MassiveType.SIGN);
    }

    public void setSignatureFormat(String str) {
        if (this.massiveConfiguration.getSignatureFormat().equals(str)) {
            return;
        }
        this.massiveConfiguration.setSignatureFormat(str);
        this.massiveSignSigner = AOSignerFactory.getSigner(this.massiveConfiguration.getSignatureFormat());
        if (this.massiveSignSigner == null) {
            LOGGER.warning("No hay disponible un manejador de firma para el formato " + str + ", se utilizara el formato de firma por defecto: " + this.massiveConfiguration.getDefaultFormat());
            this.massiveConfiguration.setSignatureFormat(this.massiveConfiguration.getDefaultFormat());
            this.massiveSignSigner = this.defaultSigner;
        }
    }

    public byte[] signData(byte[] bArr) {
        byte[] bArrCountersignLeafs;
        if (bArr == null) {
            LOGGER.severe("No se han introducido datos para firmar");
            addLog(MassiveSignMessages.getString("MassiveSignatureHelper.0"));
            return null;
        }
        Properties properties = (Properties) this.massiveConfiguration.getExtraParams().clone();
        properties.setProperty("headless", Boolean.toString(true));
        try {
            if (this.massiveConfiguration.getMassiveOperation().equals(MassiveType.SIGN)) {
                bArrCountersignLeafs = signDataFromData(this.massiveSignSigner, bArr, null, properties);
            } else if (this.massiveConfiguration.getMassiveOperation().equals(MassiveType.COSIGN)) {
                bArrCountersignLeafs = cosign(this.defaultSigner, bArr, properties);
            } else if (this.massiveConfiguration.getMassiveOperation().equals(MassiveType.COUNTERSIGN_ALL)) {
                bArrCountersignLeafs = countersignTree(this.defaultSigner, bArr, properties);
            } else {
                bArrCountersignLeafs = countersignLeafs(this.defaultSigner, bArr, properties);
            }
            addLog(MassiveSignMessages.getString("MassiveSignatureHelper.3"));
            return bArrCountersignLeafs;
        } catch (AOFormatFileException e) {
            LOGGER.severe("Los datos introducidos no tienen un formato valido: " + e);
            addLog(MassiveSignMessages.getString("MassiveSignatureHelper.1"));
            return null;
        } catch (Exception e2) {
            LOGGER.severe("Error durante la operacion " + this.massiveConfiguration.getMassiveOperation() + " sobre los datos introducidos: " + e2.getMessage());
            addLog(MassiveSignMessages.getString("MassiveSignatureHelper.2") + REG_FIELD_SEPARATOR + this.massiveConfiguration.getMassiveOperation() + REG_FIELD_SEPARATOR + e2.getMessage());
            return null;
        } catch (OutOfMemoryError e3) {
            LOGGER.severe("Error de falta de memoria durante la firma: " + e3);
            addLog(MassiveSignMessages.getString("MassiveSignatureHelper.16"));
            return null;
        }
    }

    public byte[] signHash(byte[] bArr) {
        if (bArr == null) {
            LOGGER.severe("No se ha introducido un hash para firmar");
            addLog(MassiveSignMessages.getString("MassiveSignatureHelper.4"));
            return null;
        }
        Properties properties = (Properties) this.massiveConfiguration.getExtraParams().clone();
        properties.setProperty("headless", Boolean.toString(true));
        try {
            if (this.massiveConfiguration.getMassiveOperation().equals(MassiveType.SIGN)) {
                checkvalidSignFormat(this.massiveConfiguration);
                byte[] bArrSignDataFromHash = signDataFromHash(this.massiveSignSigner, bArr, properties);
                addLog("Operación sobre hash: Correcta");
                return bArrSignDataFromHash;
            }
            if (this.massiveConfiguration.getMassiveOperation().equals(MassiveType.COSIGN)) {
                throw new UnsupportedOperationException("La cofirma de un hash no es una operacion valida");
            }
            throw new UnsupportedOperationException("La contrafirma de un hash no es una operacion valida");
        } catch (Exception e) {
            LOGGER.severe("Error al operar sobre el hash indicado (" + CommandLineParameters.MASSIVE_OP_SIGN + "): " + e);
            addLog(MassiveSignMessages.getString("MassiveSignatureHelper.5") + REG_FIELD_SEPARATOR + CommandLineParameters.MASSIVE_OP_SIGN + REG_FIELD_SEPARATOR + e.getMessage());
            return null;
        } catch (OutOfMemoryError e2) {
            LOGGER.severe("Error de falta de memoria durante la firma: " + e2);
            addLog(MassiveSignMessages.getString("MassiveSignatureHelper.16"));
            return null;
        }
    }

    private static void checkvalidSignFormat(MassiveSignConfiguration massiveSignConfiguration) {
        if ("PAdES".equals(massiveSignConfiguration.getSignatureFormat()) || AOSignConstants.SIGN_FORMAT_PDF.equals(massiveSignConfiguration.getSignatureFormat())) {
            throw new UnsupportedOperationException("El formato de firma PDF/PAdES no puede realizarse sobre un hash");
        }
        if (AOSignConstants.SIGN_FORMAT_ODF.equals(massiveSignConfiguration.getSignatureFormat()) || AOSignConstants.SIGN_FORMAT_ODF_ALT1.equals(massiveSignConfiguration.getSignatureFormat())) {
            throw new UnsupportedOperationException("El formato de firma ODF no puede realizarse sobre un hash");
        }
        if (AOSignConstants.SIGN_FORMAT_OOXML.equals(massiveSignConfiguration.getSignatureFormat()) || AOSignConstants.SIGN_FORMAT_OOXML_ALT1.equals(massiveSignConfiguration.getSignatureFormat())) {
            throw new UnsupportedOperationException("El formato de firma OOXML no puede realizarse sobre un hash");
        }
        if (AOSignConstants.SIGN_FORMAT_FACTURAE.equals(massiveSignConfiguration.getSignatureFormat()) || AOSignConstants.SIGN_FORMAT_FACTURAE_ALT1.equals(massiveSignConfiguration.getSignatureFormat())) {
            throw new UnsupportedOperationException("El formato de firma OOXML no puede realizarse sobre un hash");
        }
    }

    public byte[] signFile(String str) {
        byte[] bArrCountersignLeafs;
        if (str == null) {
            LOGGER.severe("No se ha introducido un fichero para firmar");
            addLog(MassiveSignMessages.getString("MassiveSignatureHelper.6"));
            return null;
        }
        try {
            URI uriCreateURI = AOUtil.createURI(str);
            try {
                InputStream inputStreamLoadFile = AOUtil.loadFile(uriCreateURI);
                Throwable th = null;
                try {
                    byte[] dataFromInputStream = AOUtil.getDataFromInputStream(inputStreamLoadFile);
                    if (inputStreamLoadFile != null) {
                        if (0 != 0) {
                            try {
                                inputStreamLoadFile.close();
                            } catch (Throwable th2) {
                                th.addSuppressed(th2);
                            }
                        } else {
                            inputStreamLoadFile.close();
                        }
                    }
                    if (dataFromInputStream == null) {
                        LOGGER.severe("El fichero '" + str + "' esta vacio");
                        addLog(MassiveSignMessages.getString("MassiveSignatureHelper.12") + REG_FIELD_SEPARATOR + str);
                        return null;
                    }
                    Properties properties = (Properties) this.massiveConfiguration.getExtraParams().clone();
                    properties.setProperty("headless", Boolean.toString(true));
                    try {
                        if (this.massiveConfiguration.getMassiveOperation().equals(MassiveType.SIGN)) {
                            bArrCountersignLeafs = signDataFromData(this.massiveSignSigner, dataFromInputStream, uriCreateURI, properties);
                        } else if (this.massiveConfiguration.getMassiveOperation().equals(MassiveType.COSIGN)) {
                            bArrCountersignLeafs = cosign(this.defaultSigner, dataFromInputStream, properties);
                        } else if (this.massiveConfiguration.getMassiveOperation().equals(MassiveType.COUNTERSIGN_ALL)) {
                            bArrCountersignLeafs = countersignTree(this.defaultSigner, dataFromInputStream, properties);
                        } else {
                            bArrCountersignLeafs = countersignLeafs(this.defaultSigner, dataFromInputStream, properties);
                        }
                        addLog(MassiveSignMessages.getString("MassiveSignatureHelper.15"));
                        return bArrCountersignLeafs;
                    } catch (AOFormatFileException e) {
                        LOGGER.severe("El fichero '" + LoggerUtil.getCleanUserHomePath(str) + "' no tiene un formato valido: " + e.getMessage());
                        addLog(MassiveSignMessages.getString("MassiveSignatureHelper.13") + REG_FIELD_SEPARATOR + LoggerUtil.getCleanUserHomePath(str));
                        return null;
                    } catch (Exception e2) {
                        LOGGER.severe("Error al realizar la operacion " + this.massiveConfiguration.getMassiveOperation() + " sobre el fichero '" + LoggerUtil.getCleanUserHomePath(str) + "': " + e2.getMessage());
                        addLog(MassiveSignMessages.getString("MassiveSignatureHelper.14") + REG_FIELD_SEPARATOR + e2.getMessage());
                        return null;
                    } catch (OutOfMemoryError e3) {
                        LOGGER.severe("Error de falta de memoria durante la firma: " + e3);
                        addLog(MassiveSignMessages.getString("MassiveSignatureHelper.16"));
                        return null;
                    }
                } catch (Throwable th3) {
                    if (inputStreamLoadFile != null) {
                        if (0 != 0) {
                            try {
                                inputStreamLoadFile.close();
                            } catch (Throwable th4) {
                                th.addSuppressed(th4);
                            }
                        } else {
                            inputStreamLoadFile.close();
                        }
                    }
                    throw th3;
                }
            } catch (FileNotFoundException e4) {
                LOGGER.severe("No ha sido posible encontrar el fichero '" + LoggerUtil.getCleanUserHomePath(str) + "': " + e4);
                addLog(MassiveSignMessages.getString("MassiveSignatureHelper.8") + REG_FIELD_SEPARATOR + LoggerUtil.getCleanUserHomePath(str));
                return null;
            } catch (Exception e5) {
                LOGGER.severe("No es posible acceder al contenido del fichero '" + LoggerUtil.getCleanUserHomePath(str) + "': " + e5);
                addLog(MassiveSignMessages.getString("MassiveSignatureHelper.9") + REG_FIELD_SEPARATOR + LoggerUtil.getCleanUserHomePath(str));
                return null;
            }
        } catch (Exception e6) {
            LOGGER.severe("La URI '" + str + "' no posee un formato valido: " + e6);
            addLog(MassiveSignMessages.getString("MassiveSignatureHelper.7") + REG_FIELD_SEPARATOR + str);
            return null;
        }
    }

    private byte[] signDataFromData(AOSigner aOSigner, byte[] bArr, URI uri, Properties properties) throws IOException, AOException {
        String lowerCase;
        String strTransformMimeTypeToOid;
        if (!properties.containsKey("mode")) {
            properties.setProperty("mode", this.massiveConfiguration.getMode());
        }
        if (!properties.containsKey("format")) {
            properties.setProperty("format", this.massiveConfiguration.getSignatureFormat());
        }
        if (uri != null) {
            properties.setProperty("uri", uri.toString());
        }
        byte[] bArrDigest = bArr;
        String digestAlgorithmName = AOSignConstants.getDigestAlgorithmName(this.massiveConfiguration.getAlgorithm());
        String name = aOSigner.getClass().getName();
        if (CADES_SIGNER.equals(name) || XADES_SIGNER.equals(name) || XMLDSIG_SIGNER.equals(name)) {
            if (properties.containsKey("mimeType")) {
                lowerCase = properties.getProperty("mimeType");
            } else if ((XADES_SIGNER.equals(name) || XMLDSIG_SIGNER.equals(name)) && "explicit".equalsIgnoreCase(properties.getProperty("mode"))) {
                bArrDigest = digest(bArrDigest, digestAlgorithmName);
                lowerCase = ("hash/" + digestAlgorithmName).toLowerCase();
            } else {
                lowerCase = new MimeHelper(bArrDigest).getMimeType();
            }
            properties.setProperty("mimeType", lowerCase);
            if (!properties.containsKey("contentTypeOid") && (strTransformMimeTypeToOid = MimeHelper.transformMimeTypeToOid(lowerCase)) != null) {
                properties.setProperty("contentTypeOid", strTransformMimeTypeToOid);
            }
        }
        byte[] bArrSign = aOSigner.sign(bArrDigest, this.massiveConfiguration.getAlgorithm(), this.massiveConfiguration.getKeyEntry().getPrivateKey(), this.massiveConfiguration.getKeyEntry().getCertificateChain(), properties);
        if (bArrSign == null) {
            throw SIGN_DATA_NOT_GENERATED;
        }
        return bArrSign;
    }

    private byte[] signDataFromHash(AOSigner aOSigner, byte[] bArr, Properties properties) throws IOException, AOException {
        if (!properties.containsKey("mode")) {
            properties.setProperty("mode", this.massiveConfiguration.getMode());
        }
        if (!properties.containsKey("format")) {
            properties.setProperty("format", this.massiveConfiguration.getSignatureFormat());
        }
        properties.setProperty("precalculatedHashAlgorithm", AOSignConstants.getDigestAlgorithmName(this.massiveConfiguration.getAlgorithm()));
        if (aOSigner.getClass().getName().equals(XADES_SIGNER) || aOSigner.getClass().getName().equals(XMLDSIG_SIGNER)) {
            properties.setProperty("mimeType", "hash/" + AOSignConstants.getDigestAlgorithmName(this.massiveConfiguration.getAlgorithm()).toLowerCase());
        }
        byte[] bArrSign = aOSigner.sign(bArr, this.massiveConfiguration.getAlgorithm(), this.massiveConfiguration.getKeyEntry().getPrivateKey(), this.massiveConfiguration.getKeyEntry().getCertificateChain(), properties);
        if (bArrSign == null) {
            throw SIGN_DATA_NOT_GENERATED;
        }
        return bArrSign;
    }

    private byte[] cosign(AOSigner aOSigner, byte[] bArr, Properties properties) throws IOException, AOException {
        if (!properties.containsKey("mode")) {
            properties.setProperty("mode", this.massiveConfiguration.getMode());
        }
        byte[] bArrCosign = getValidSigner(aOSigner, bArr).cosign(bArr, this.massiveConfiguration.getAlgorithm(), this.massiveConfiguration.getKeyEntry().getPrivateKey(), this.massiveConfiguration.getKeyEntry().getCertificateChain(), properties);
        if (bArrCosign == null) {
            throw SIGN_DATA_NOT_GENERATED;
        }
        return bArrCosign;
    }

    private byte[] countersignTree(AOSigner aOSigner, byte[] bArr, Properties properties) throws IOException, AOException {
        return countersignOperation(aOSigner, bArr, CounterSignTarget.TREE, properties);
    }

    private byte[] countersignLeafs(AOSigner aOSigner, byte[] bArr, Properties properties) throws IOException, AOException {
        return countersignOperation(aOSigner, bArr, CounterSignTarget.LEAFS, properties);
    }

    private byte[] countersignOperation(AOSigner aOSigner, byte[] bArr, CounterSignTarget counterSignTarget, Properties properties) throws IOException, AOException {
        byte[] bArrCountersign = getValidSigner(aOSigner, bArr).countersign(bArr, this.massiveConfiguration.getAlgorithm(), counterSignTarget, null, this.massiveConfiguration.getKeyEntry().getPrivateKey(), this.massiveConfiguration.getKeyEntry().getCertificateChain(), properties);
        if (bArrCountersign == null) {
            throw SIGN_DATA_NOT_GENERATED;
        }
        return bArrCountersign;
    }

    private AOSigner getValidSigner(AOSigner aOSigner, byte[] bArr) throws IOException, AOException {
        AOSigner specificSigner = aOSigner;
        if (!this.massiveConfiguration.isOriginalFormat()) {
            if (!aOSigner.isSign(bArr)) {
                throw new AOException("La firma introducida no se corresponde con el formato de firma especificado");
            }
        } else {
            specificSigner = getSpecificSigner(bArr);
            if (specificSigner == null) {
                specificSigner = AOSignerFactory.getSigner(bArr);
                if (specificSigner == null) {
                    throw new AOException("La firma introducida no se corresponde con ningún formato soportado");
                }
            }
        }
        return specificSigner;
    }

    private static AOSigner getSpecificSigner(byte[] bArr) throws IOException {
        for (String str : new String[]{AOSignConstants.SIGN_FORMAT_PDF, AOSignConstants.SIGN_FORMAT_ODF, AOSignConstants.SIGN_FORMAT_OOXML}) {
            AOSigner signer = AOSignerFactory.getSigner(str);
            if (signer != null && signer.isValidDataFile(bArr)) {
                return signer;
            }
        }
        return null;
    }

    private void addLog(String str) {
        if (this.log == null) {
            this.log = new ArrayList();
        }
        this.log.add(str);
    }

    public String getCurrentLogEntry() {
        String str = "";
        if (this.log != null) {
            str = this.log.get(this.log.size() - 1);
        }
        return str;
    }

    public String getDefaultSignatureFormat() {
        if (this.massiveConfiguration != null) {
            return this.massiveConfiguration.getDefaultFormat();
        }
        return null;
    }

    public String getAllLogEntries() {
        StringBuilder sb = new StringBuilder();
        if (this.log != null) {
            Iterator<String> it = this.log.iterator();
            while (it.hasNext()) {
                sb.append(it.next()).append("\r\n");
            }
        }
        return sb.toString().trim();
    }

    private static byte[] digest(byte[] bArr, String str) {
        if (md == null) {
            try {
                md = MessageDigest.getInstance(str);
            } catch (NoSuchAlgorithmException e) {
                LOGGER.severe("Se ha utilizado internamente un algoritmo de huella digital no soportado: " + e);
                throw new IllegalArgumentException("Algoritmo no soportado", e);
            }
        }
        return md.digest(bArr);
    }
}
