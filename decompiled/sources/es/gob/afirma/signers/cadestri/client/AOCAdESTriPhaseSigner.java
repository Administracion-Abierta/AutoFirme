package es.gob.afirma.signers.cadestri.client;

import es.gob.afirma.core.AOException;
import es.gob.afirma.core.AOInvalidFormatException;
import es.gob.afirma.core.SigningLTSException;
import es.gob.afirma.core.misc.Base64;
import es.gob.afirma.core.misc.http.UrlHttpManager;
import es.gob.afirma.core.misc.http.UrlHttpManagerFactory;
import es.gob.afirma.core.signers.AOPkcs1Signer;
import es.gob.afirma.core.signers.AOSignInfo;
import es.gob.afirma.core.signers.AOSigner;
import es.gob.afirma.core.signers.AOTriphaseException;
import es.gob.afirma.core.signers.CounterSignTarget;
import es.gob.afirma.core.signers.TriphaseData;
import es.gob.afirma.core.signers.TriphaseDataSigner;
import es.gob.afirma.core.util.tree.AOTreeModel;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.util.Arrays;
import java.util.Properties;
import java.util.logging.Logger;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/cadestri/client/AOCAdESTriPhaseSigner.class */
public class AOCAdESTriPhaseSigner implements AOSigner {
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private static final String CRYPTO_OPERATION_SIGN = "sign";
    private static final String CRYPTO_OPERATION_COSIGN = "cosign";
    private static final String CRYPTO_OPERATION_COUNTERSIGN = "countersign";
    private static final String PROPERTY_NAME_CS_TARGET = "target";
    private static final String ERROR_PREFIX = "ERR-";
    private static final String CONFIG_NEEDED_ERROR_PREFIX = "ERR-21:";
    private static final String SUCCESS = "OK";

    @Override // es.gob.afirma.core.signers.AOSimpleSigner
    public byte[] sign(byte[] bArr, String str, PrivateKey privateKey, Certificate[] certificateArr, Properties properties) throws AOException {
        return triPhaseOperation("CAdES", "sign", bArr, str, privateKey, certificateArr, properties);
    }

    @Override // es.gob.afirma.core.signers.AOCoSigner
    public byte[] cosign(byte[] bArr, byte[] bArr2, String str, PrivateKey privateKey, Certificate[] certificateArr, Properties properties) throws AOException {
        return cosign(bArr2, str, privateKey, certificateArr, properties);
    }

    @Override // es.gob.afirma.core.signers.AOCoSigner
    public byte[] cosign(byte[] bArr, String str, PrivateKey privateKey, Certificate[] certificateArr, Properties properties) throws AOException {
        return triPhaseOperation("CAdES", "cosign", bArr, str, privateKey, certificateArr, properties);
    }

    @Override // es.gob.afirma.core.signers.AOCounterSigner
    public byte[] countersign(byte[] bArr, String str, CounterSignTarget counterSignTarget, Object[] objArr, PrivateKey privateKey, Certificate[] certificateArr, Properties properties) throws AOException {
        if (counterSignTarget == null) {
            throw new IllegalArgumentException("No se han indicado los nodos objetivo de la contrafirma");
        }
        if (counterSignTarget != CounterSignTarget.TREE && counterSignTarget != CounterSignTarget.LEAFS) {
            throw new IllegalArgumentException("El objetivo indicado para la contrafirma no esta soportado: " + counterSignTarget);
        }
        properties.setProperty(PROPERTY_NAME_CS_TARGET, counterSignTarget.toString());
        return triPhaseOperation("CAdES", "countersign", bArr, str, privateKey, certificateArr, properties);
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public AOTreeModel getSignersStructure(byte[] bArr, boolean z) {
        throw new UnsupportedOperationException("No se soporta la obtencion de estructura de firmas en firma trifasica");
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public AOTreeModel getSignersStructure(byte[] bArr, Properties properties, boolean z) {
        throw new UnsupportedOperationException("No se soporta la obtencion de estructura de firmas en firma trifasica");
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public boolean isSign(byte[] bArr, Properties properties) {
        throw new UnsupportedOperationException("No se soporta comprobacion de si es firma en modo trifasico");
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public boolean isSign(byte[] bArr) {
        throw new UnsupportedOperationException("No se soporta comprobacion de si es firma en modo trifasico");
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
        return str + (str2 != null ? str2 : "") + ".csig";
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public byte[] getData(byte[] bArr) {
        throw new UnsupportedOperationException("No se soporta ela obtencion de datos en firma trifasica");
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public byte[] getData(byte[] bArr, Properties properties) throws AOInvalidFormatException {
        throw new UnsupportedOperationException("No se soporta ela obtencion de datos en firma trifasica");
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public AOSignInfo getSignInfo(byte[] bArr) {
        throw new UnsupportedOperationException("No se soporta la obtencion de informacion de la firma en modo trifasico");
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public AOSignInfo getSignInfo(byte[] bArr, Properties properties) throws AOException {
        throw new UnsupportedOperationException("No se soporta la obtencion de informacion de la firma en modo trifasico");
    }

    protected static byte[] triPhaseOperation(String str, String str2, byte[] bArr, String str3, PrivateKey privateKey, Certificate[] certificateArr, Properties properties) throws AOException {
        if (properties == null) {
            throw new IllegalArgumentException("Se necesitan parametros adicionales");
        }
        if (privateKey == null) {
            throw new IllegalArgumentException("Es necesario proporcionar la clave privada de firma");
        }
        if (certificateArr == null || certificateArr.length == 0) {
            throw new IllegalArgumentException("Es necesario proporcionar el certificado de firma");
        }
        if (bArr == null) {
            throw new IllegalArgumentException("No se ha proporcionado el identificador de documento a firmar");
        }
        try {
            URL url = new URL(properties.getProperty("serverUrl"));
            String strEncode = Base64.encode(bArr, true);
            UrlHttpManager installedManager = UrlHttpManagerFactory.getInstalledManager();
            try {
                byte[] bArrPreSign = PreSigner.preSign(str, str3, certificateArr, str2, strEncode, installedManager, url, properties);
                if (bArrPreSign.length > 8 && new String(Arrays.copyOf(bArrPreSign, 8), StandardCharsets.UTF_8).startsWith(ERROR_PREFIX)) {
                    String str4 = new String(bArrPreSign, StandardCharsets.UTF_8);
                    LOGGER.warning("Error durante la prefirma: " + str4);
                    throw buildInternalException(str4, properties);
                }
                try {
                    try {
                        byte[] bArrPostSign = PostSigner.postSign(str, str3, certificateArr, str2, strEncode, properties, true, installedManager, url, Base64.encode(TriphaseDataSigner.doSign(new AOPkcs1Signer(), str3, privateKey, certificateArr, TriphaseData.parser(Base64.decode(bArrPreSign, 0, bArrPreSign.length, true)), properties).toString().getBytes(), true));
                        if (bArrPostSign.length > 8 && new String(Arrays.copyOf(bArrPostSign, 8), StandardCharsets.UTF_8).startsWith(CONFIG_NEEDED_ERROR_PREFIX)) {
                            String str5 = new String(bArrPostSign, StandardCharsets.UTF_8);
                            LOGGER.warning("Error durante la postfirma: " + str5);
                            throw buildInternalException(str5, properties);
                        }
                        String strTrim = new String(bArrPostSign).trim();
                        if (!strTrim.startsWith(SUCCESS)) {
                            throw new AOException("La firma trifasica no ha finalizado correctamente: " + new String(bArrPostSign));
                        }
                        try {
                            return Base64.decode(strTrim.substring("OK NEWID=".length()), true);
                        } catch (IOException e) {
                            LOGGER.warning("El resultado de NEWID del servidor no estaba en Base64: " + e);
                            throw new AOException("El resultado devuelto por el servidor no es correcto", e);
                        }
                    } catch (IOException e2) {
                        throw new AOException("Error en la postfirma: " + e2, e2);
                    } catch (CertificateEncodingException e3) {
                        throw new AOException("Error en el tratamiento del certificado del firmante en la postfirma: " + e3, e3);
                    }
                } catch (Exception e4) {
                    LOGGER.severe("Error al analizar la prefirma enviada por el servidor: " + e4);
                    throw new AOException("Error al analizar la prefirma enviada por el servidor", e4);
                }
            } catch (IOException e5) {
                throw new AOException("Error en la llamada de prefirma al servidor: " + e5, e5);
            } catch (CertificateEncodingException e6) {
                throw new AOException("Error decodificando el certificado del firmante: " + e6, e6);
            }
        } catch (Exception e7) {
            throw new IllegalArgumentException("No se ha proporcionado una URL valida para el servidor de firma: " + properties.getProperty("serverUrl"), e7);
        }
    }

    private static AOException buildInternalException(String str, Properties properties) {
        AOException aOException = null;
        int iIndexOf = str.indexOf(":");
        if (str.startsWith(CONFIG_NEEDED_ERROR_PREFIX)) {
            int iIndexOf2 = str.indexOf(":", iIndexOf + 1);
            String strSubstring = str.substring(iIndexOf + 1, iIndexOf2);
            String strSubstring2 = str.substring(iIndexOf2 + 1);
            if (SigningLTSException.REQUESTOR_MSG_CODE.equals(strSubstring)) {
                aOException = new SigningLTSException(strSubstring2);
            }
        }
        if (aOException == null) {
            int iIndexOf3 = str.indexOf(":", iIndexOf + 1);
            if (iIndexOf3 > 0) {
                aOException = AOTriphaseException.parseException(str.substring(iIndexOf3 + 1).trim());
            } else {
                aOException = new AOException(str);
            }
        }
        return aOException;
    }
}
