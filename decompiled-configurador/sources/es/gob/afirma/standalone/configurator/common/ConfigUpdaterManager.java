package es.gob.afirma.standalone.configurator.common;

import es.gob.afirma.core.misc.AOUtil;
import es.gob.afirma.core.misc.LoggerUtil;
import es.gob.afirma.core.misc.http.DataDownloader;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/standalone/configurator/common/ConfigUpdaterManager.class */
public class ConfigUpdaterManager {
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");

    public static boolean needCheckConfigUpdates() {
        return PreferencesManager.needCheckConfigUpdates();
    }

    public static void savePrefsConfigFile(String str, boolean z) {
        try {
            byte[] fromUrl = readFromUrl(str);
            ConfigDataInfo configDataInfo = new ConfigDataInfo(fromUrl);
            importSystemPreferences(fromUrl);
            PreferencesManager.setConfigFileInfo(str, z, configDataInfo);
            PreferencesManager.setConfigCheckDate();
            PreferencesManager.flushSystemPrefs();
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error al actualizar el registro del fichero de configuracion", (Throwable) e);
        }
    }

    public static void updatePrefsConfigFile() {
        String configFileUrl = PreferencesManager.getConfigFileUrl();
        try {
            ConfigDataInfo configDataInfo = new ConfigDataInfo(readFromUrl(configFileUrl));
            if (PreferencesManager.isNewConfigFile(configDataInfo)) {
                try {
                    LOGGER.info("Se actualiza la configuracion desde: " + LoggerUtil.getTrimStr(LoggerUtil.getCleanUserHomePath(configFileUrl)));
                    importSystemPreferences(configDataInfo.getData());
                    PreferencesManager.setConfigFileInfo(configDataInfo);
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Error al actualizar la configuracion por defecto a nivel de sistema", (Throwable) e);
                }
            }
            try {
                PreferencesManager.setConfigCheckDate();
                PreferencesManager.flushSystemPrefs();
            } catch (Exception e2) {
                LOGGER.log(Level.WARNING, "Error al actualizar el intento de actualizacion", (Throwable) e2);
            }
        } catch (Exception e3) {
            LOGGER.log(Level.WARNING, "Error al actualizar la configuracion por defecto a nivel de sistema", (Throwable) e3);
        }
    }

    private static void importSystemPreferences(byte[] bArr) throws Exception {
        PreferencesManager.clearAllSystemPrefs();
        PreferencesPlistHandler.importSystemPreferencesFromXml(bArr);
    }

    /* JADX WARN: Not initialized variable reg: 7, insn: 0x00c7: MOVE (r0 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r7 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) A[TRY_LEAVE], block:B:44:0x00c7 */
    /* JADX WARN: Not initialized variable reg: 8, insn: 0x00cb: MOVE (r0 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r8 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]), block:B:46:0x00cb */
    private static byte[] readFromUrl(String str) throws IOException {
        byte[] bArrDownloadData;
        InputStream inputStream;
        Throwable th;
        if (str.toLowerCase(Locale.US).startsWith("https:")) {
            try {
                bArrDownloadData = DataDownloader.downloadData(str, false, true);
            } catch (Exception e) {
                throw new IOException("No se ha podido descargar el fichero del enlace remoto", e);
            }
        } else {
            try {
                try {
                    FileInputStream fileInputStream = new FileInputStream(str);
                    Throwable th2 = null;
                    BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
                    Throwable th3 = null;
                    try {
                        try {
                            bArrDownloadData = AOUtil.getDataFromInputStream(bufferedInputStream);
                            if (bufferedInputStream != null) {
                                if (0 != 0) {
                                    try {
                                        bufferedInputStream.close();
                                    } catch (Throwable th4) {
                                        th3.addSuppressed(th4);
                                    }
                                } else {
                                    bufferedInputStream.close();
                                }
                            }
                            if (fileInputStream != null) {
                                if (0 != 0) {
                                    try {
                                        fileInputStream.close();
                                    } catch (Throwable th5) {
                                        th2.addSuppressed(th5);
                                    }
                                } else {
                                    fileInputStream.close();
                                }
                            }
                        } catch (Throwable th6) {
                            th3 = th6;
                            throw th6;
                        }
                    } catch (Throwable th7) {
                        if (bufferedInputStream != null) {
                            if (th3 != null) {
                                try {
                                    bufferedInputStream.close();
                                } catch (Throwable th8) {
                                    th3.addSuppressed(th8);
                                }
                            } else {
                                bufferedInputStream.close();
                            }
                        }
                        throw th7;
                    }
                } catch (Throwable th9) {
                    if (inputStream != null) {
                        if (th != null) {
                            try {
                                inputStream.close();
                            } catch (Throwable th10) {
                                th.addSuppressed(th10);
                            }
                        } else {
                            inputStream.close();
                        }
                    }
                    throw th9;
                }
            } catch (Exception e2) {
                throw new IOException("No se ha podido leer el archivo", e2);
            }
        }
        return bArrDownloadData;
    }
}
