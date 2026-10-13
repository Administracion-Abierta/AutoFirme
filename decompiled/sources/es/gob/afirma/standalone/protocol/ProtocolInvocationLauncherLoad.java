package es.gob.afirma.standalone.protocol;

import es.gob.afirma.core.AOCancelledOperationException;
import es.gob.afirma.core.misc.AOUtil;
import es.gob.afirma.core.misc.Base64;
import es.gob.afirma.core.misc.Platform;
import es.gob.afirma.core.misc.protocol.UrlParametersToLoad;
import es.gob.afirma.core.ui.AOUIFactory;
import es.gob.afirma.standalone.DesktopUtil;
import es.gob.afirma.standalone.SimpleAfirma;
import es.gob.afirma.standalone.so.macos.MacUtils;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Logger;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/protocol/ProtocolInvocationLauncherLoad.class */
final class ProtocolInvocationLauncherLoad {
    private static final char LOAD_SEPARATOR = ':';
    private static final char MULTILOAD_SEPARATOR = '|';
    private static final String RESULT_CANCEL = "CANCEL";
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");

    private ProtocolInvocationLauncherLoad() {
    }

    static String processLoad(UrlParametersToLoad urlParametersToLoad, int i, boolean z) throws SocketOperationException {
        if (!ProtocolInvocationLauncher.MAX_PROTOCOL_VERSION_SUPPORTED.support(i)) {
            LOGGER.severe(String.format("Version de protocolo no soportada (%1s). Version actual: %s2. Hay que actualizar la aplicacion.", Integer.valueOf(i), Integer.valueOf(ProtocolInvocationLauncher.MAX_PROTOCOL_VERSION_SUPPORTED.getVersion())));
            ProtocolInvocationLauncherErrorManager.showError("SAF_21");
            if (z) {
                return ProtocolInvocationLauncherErrorManager.getErrorMessage("SAF_21");
            }
            throw new SocketOperationException("SAF_21");
        }
        if (urlParametersToLoad.getMinimumClientVersion() != null && new Version(urlParametersToLoad.getMinimumClientVersion()).greaterThan(SimpleAfirma.getVersion())) {
            ProtocolInvocationLauncherErrorManager.showError("SAF_41");
            if (z) {
                return ProtocolInvocationLauncherErrorManager.getErrorMessage("SAF_41");
            }
            throw new SocketOperationException("SAF_41");
        }
        try {
            if (Platform.OS.MACOSX.equals(Platform.getOS())) {
                MacUtils.focusApplication();
            }
            File[] loadFiles = AOUIFactory.getLoadFiles(urlParametersToLoad.getTitle(), urlParametersToLoad.getFilepath(), null, urlParametersToLoad.getExtensions() != null ? urlParametersToLoad.getExtensions().split(",") : null, urlParametersToLoad.getDescription(), false, urlParametersToLoad.getMultiload(), DesktopUtil.getDefaultDialogsIcon(), null);
            StringBuilder sb = new StringBuilder();
            for (int i2 = 0; i2 < loadFiles.length; i2++) {
                try {
                    FileInputStream fileInputStream = new FileInputStream(loadFiles[i2]);
                    Throwable th = null;
                    try {
                        try {
                            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
                            Throwable th2 = null;
                            try {
                                try {
                                    byte[] dataFromInputStream = AOUtil.getDataFromInputStream(bufferedInputStream);
                                    if (bufferedInputStream != null) {
                                        if (0 != 0) {
                                            try {
                                                bufferedInputStream.close();
                                            } catch (Throwable th3) {
                                                th2.addSuppressed(th3);
                                            }
                                        } else {
                                            bufferedInputStream.close();
                                        }
                                    }
                                    if (fileInputStream != null) {
                                        if (0 != 0) {
                                            try {
                                                fileInputStream.close();
                                            } catch (Throwable th4) {
                                                th.addSuppressed(th4);
                                            }
                                        } else {
                                            fileInputStream.close();
                                        }
                                    }
                                    if (dataFromInputStream == null) {
                                        throw new IOException("La lectura de datos para cargar ha devuelto un nulo");
                                    }
                                    sb.append(loadFiles[i2].getName());
                                    sb.append(':');
                                    sb.append(Base64.encode(dataFromInputStream));
                                    if (i2 < loadFiles.length - 1) {
                                        sb.append('|');
                                    }
                                } catch (Throwable th5) {
                                    th2 = th5;
                                    throw th5;
                                }
                            } catch (Throwable th6) {
                                if (bufferedInputStream != null) {
                                    if (th2 != null) {
                                        try {
                                            bufferedInputStream.close();
                                        } catch (Throwable th7) {
                                            th2.addSuppressed(th7);
                                        }
                                    } else {
                                        bufferedInputStream.close();
                                    }
                                }
                                throw th6;
                            }
                        } catch (Throwable th8) {
                            th = th8;
                            throw th8;
                        }
                    } catch (Throwable th9) {
                        if (fileInputStream != null) {
                            if (th != null) {
                                try {
                                    fileInputStream.close();
                                } catch (Throwable th10) {
                                    th.addSuppressed(th10);
                                }
                            } else {
                                fileInputStream.close();
                            }
                        }
                        throw th9;
                    }
                } catch (Exception e) {
                    LOGGER.severe("Error en la lectura de los datos a cargar: " + e);
                    ProtocolInvocationLauncherErrorManager.showError("SAF_25", e);
                    if (z) {
                        return ProtocolInvocationLauncherErrorManager.getErrorMessage("SAF_25");
                    }
                    throw new SocketOperationException("SAF_25");
                }
            }
            return sb.toString();
        } catch (AOCancelledOperationException e2) {
            LOGGER.info("carga de datos de firma cancelada por el usuario: " + e2);
            if (z) {
                return getResultCancel();
            }
            throw new SocketOperationException(getResultCancel());
        }
    }

    public static String getResultCancel() {
        return "CANCEL";
    }
}
