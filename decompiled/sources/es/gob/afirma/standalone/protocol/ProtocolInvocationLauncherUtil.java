package es.gob.afirma.standalone.protocol;

import es.gob.afirma.core.RuntimeConfigNeededException;
import es.gob.afirma.core.misc.protocol.UrlParameters;
import es.gob.afirma.core.signers.AOSignConstants;
import es.gob.afirma.core.signers.AOSignerFactory;
import es.gob.afirma.core.signers.AOTriphaseException;
import es.gob.afirma.standalone.DataAnalizerUtil;
import es.gob.afirma.standalone.crypto.CypherDataManager;
import es.gob.afirma.standalone.plugins.SignOperation;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Logger;
import org.ini4j.Config;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/protocol/ProtocolInvocationLauncherUtil.class */
final class ProtocolInvocationLauncherUtil {
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");

    private ProtocolInvocationLauncherUtil() {
    }

    /* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/protocol/ProtocolInvocationLauncherUtil$InvalidEncryptedDataLengthException.class */
    static final class InvalidEncryptedDataLengthException extends Exception {
        private static final long serialVersionUID = 1;

        InvalidEncryptedDataLengthException(String str) {
            super(str);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/protocol/ProtocolInvocationLauncherUtil$DecryptionException.class */
    static final class DecryptionException extends Exception {
        private static final long serialVersionUID = 1;

        DecryptionException(String str, Throwable th) {
            super(str, th);
        }
    }

    static byte[] getDataFromRetrieveServlet(UrlParameters urlParameters) throws DecryptionException, InvalidEncryptedDataLengthException, IOException {
        LOGGER.info("Intentamos recuperar los datos del servidor con la URL:\n" + (urlParameters.getRetrieveServletUrl().toString() + Config.DEFAULT_GLOBAL_SECTION_NAME + "op=get&v=1_0&id=" + urlParameters.getFileId()));
        byte[] bArrRetrieveData = IntermediateServerUtil.retrieveData(urlParameters.getRetrieveServletUrl().toString(), urlParameters.getFileId());
        if (bArrRetrieveData.length > 8 && new String(Arrays.copyOf(bArrRetrieveData, 8)).toLowerCase().startsWith("err-")) {
            LOGGER.severe("Se recupera un error desde el servidor intermedio: " + new String(bArrRetrieveData));
            throw new InvalidEncryptedDataLengthException("Se recupera un error desde el servidor intermedio: " + new String(bArrRetrieveData));
        }
        try {
            return CypherDataManager.decipherData(bArrRetrieveData, urlParameters.getDesKey());
        } catch (Exception e) {
            LOGGER.severe("Error en el descifrado de los datos: " + e);
            throw new DecryptionException("Error en el descifrado de los datos: " + e, e);
        }
    }

    static Exception getInternalException(AOTriphaseException aOTriphaseException) {
        try {
            Class<?> cls = Class.forName(aOTriphaseException.getServerExceptionClassname(), false, ProtocolInvocationLauncherUtil.class.getClassLoader());
            if (!cls.isAssignableFrom(RuntimeConfigNeededException.class)) {
                LOGGER.warning("La clase indicada para pedir confirmacion no es de tipo RuntimeConfigNeededException (" + aOTriphaseException.getServerExceptionClassname() + "): " + aOTriphaseException);
                return aOTriphaseException;
            }
            try {
                return (Exception) cls.getDeclaredConstructor(String.class).newInstance(aOTriphaseException.getMessage());
            } catch (Exception e) {
                try {
                    return (Exception) cls.getDeclaredConstructor(String.class, Throwable.class).newInstance(aOTriphaseException.getMessage(), null);
                } catch (Exception e2) {
                    try {
                        return (Exception) cls.getDeclaredConstructor(Throwable.class).newInstance((Throwable) null);
                    } catch (Exception e3) {
                        try {
                            return (Exception) cls.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
                        } catch (Exception e4) {
                            LOGGER.warning("No se encontro un constructor para reconstruir la excepcion del servidor");
                            return aOTriphaseException;
                        }
                    }
                }
            }
        } catch (Throwable th) {
            LOGGER.warning("No se pudo identificar la excepcion enviada por el servidor (" + aOTriphaseException.getServerExceptionClassname() + "): " + th);
            return aOTriphaseException;
        }
    }

    static String identifyFormatFromData(byte[] bArr, SignOperation.Operation operation) {
        String signFormat;
        if (SignOperation.Operation.SIGN == operation) {
            if (DataAnalizerUtil.isPDF(bArr)) {
                signFormat = "PAdES";
            } else if (DataAnalizerUtil.isFacturae(bArr)) {
                signFormat = AOSignConstants.SIGN_FORMAT_FACTURAE;
            } else if (DataAnalizerUtil.isXML(bArr)) {
                signFormat = AOSignConstants.SIGN_FORMAT_XADES;
            } else {
                signFormat = "CAdES";
            }
        } else {
            try {
                signFormat = AOSignerFactory.getSignFormat(AOSignerFactory.getSigner(bArr));
            } catch (IOException e) {
                LOGGER.severe("No se han podido analizar los datos para determinar si son una firma: " + e);
                signFormat = null;
            }
        }
        return signFormat;
    }
}
