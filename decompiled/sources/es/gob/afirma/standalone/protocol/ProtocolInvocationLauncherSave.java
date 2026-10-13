package es.gob.afirma.standalone.protocol;

import es.gob.afirma.core.AOCancelledOperationException;
import es.gob.afirma.core.misc.Platform;
import es.gob.afirma.core.misc.protocol.UrlParametersToSave;
import es.gob.afirma.core.ui.AOUIFactory;
import es.gob.afirma.core.ui.GenericFileFilter;
import es.gob.afirma.standalone.SimpleAfirma;
import es.gob.afirma.standalone.so.macos.MacUtils;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/protocol/ProtocolInvocationLauncherSave.class */
final class ProtocolInvocationLauncherSave {
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private static final String RESULT_OK = "OK";
    private static final String RESULT_CANCEL = "CANCEL";

    private ProtocolInvocationLauncherSave() {
    }

    static String processSave(UrlParametersToSave urlParametersToSave, int i, boolean z) throws SocketOperationException {
        if (!ProtocolInvocationLauncher.MAX_PROTOCOL_VERSION_SUPPORTED.support(i)) {
            LOGGER.severe(String.format("Version de protocolo no soportada (%1s). Version actual: %s2. Hay que actualizar la aplicacion.", Integer.valueOf(i), Integer.valueOf(ProtocolInvocationLauncher.MAX_PROTOCOL_VERSION_SUPPORTED.getVersion())));
            ProtocolInvocationLauncherErrorManager.showError("SAF_21");
            if (!z) {
                throw new SocketOperationException("SAF_21");
            }
            return ProtocolInvocationLauncherErrorManager.getErrorMessage("SAF_21");
        }
        if (urlParametersToSave.getMinimumClientVersion() != null && new Version(urlParametersToSave.getMinimumClientVersion()).greaterThan(SimpleAfirma.getVersion())) {
            ProtocolInvocationLauncherErrorManager.showError("SAF_41");
            if (!z) {
                throw new SocketOperationException("SAF_41");
            }
            return ProtocolInvocationLauncherErrorManager.getErrorMessage("SAF_41");
        }
        try {
            if (Platform.OS.MACOSX.equals(Platform.getOS())) {
                MacUtils.focusApplication();
            }
            AOUIFactory.getSaveDataToFile(urlParametersToSave.getData(), urlParametersToSave.getTitle(), null, urlParametersToSave.getFileName(), Collections.singletonList(new GenericFileFilter(urlParametersToSave.getExtensions() != null ? new String[]{urlParametersToSave.getExtensions()} : null, urlParametersToSave.getFileTypeDescription())), null);
            if (urlParametersToSave.getStorageServletUrl() != null) {
                Thread activeWaitingThread = ProtocolInvocationLauncher.getActiveWaitingThread();
                if (activeWaitingThread != null) {
                    activeWaitingThread.interrupt();
                }
                synchronized (IntermediateServerUtil.getUniqueSemaphoreInstance()) {
                    try {
                        LOGGER.info("Enviamos el resultado de la operacion de guardado al servidor intermedio");
                        IntermediateServerUtil.sendData(RESULT_OK, urlParametersToSave.getStorageServletUrl().toString(), urlParametersToSave.getId());
                    } catch (Exception e) {
                        LOGGER.log(Level.SEVERE, "Error al enviar los datos al servidor", (Throwable) e);
                        ProtocolInvocationLauncherErrorManager.showError("SAF_11", e);
                        return ProtocolInvocationLauncherErrorManager.getErrorMessage("SAF_11");
                    }
                }
                return RESULT_OK;
            }
            LOGGER.info("Se omite el envio por red del resultado por no haberse proporcionado una URL de destino");
            return RESULT_OK;
        } catch (AOCancelledOperationException e2) {
            LOGGER.severe("Operacion cancelada por el usuario: " + e2);
            if (!z) {
                throw new SocketOperationException("CANCEL");
            }
            return "CANCEL";
        } catch (Exception e3) {
            LOGGER.severe("Error en el guardado de datos: " + e3);
            ProtocolInvocationLauncherErrorManager.showError("SAF_05", e3);
            if (!z) {
                throw new SocketOperationException("SAF_05");
            }
            return ProtocolInvocationLauncherErrorManager.getErrorMessage("SAF_05");
        }
    }

    public static String getResultOk() {
        return RESULT_OK;
    }

    public static String getResultCancel() {
        return "CANCEL";
    }
}
