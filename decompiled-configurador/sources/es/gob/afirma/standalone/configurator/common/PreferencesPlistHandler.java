package es.gob.afirma.standalone.configurator.common;

import es.gob.afirma.core.AOInvalidFormatException;
import es.gob.afirma.core.misc.AOFileUtils;
import es.gob.afirma.core.misc.AOUtil;
import es.gob.afirma.core.prefs.KeyStorePreferencesManager;
import es.gob.afirma.core.signers.AOSimpleSignInfo;
import es.gob.afirma.core.ui.AOUIFactory;
import es.gob.afirma.core.util.tree.AOTreeModel;
import es.gob.afirma.core.util.tree.AOTreeNode;
import es.gob.afirma.signers.xades.AOXAdESSigner;
import es.gob.afirma.standalone.configurator.common.xmlwise.Plist;
import es.gob.afirma.standalone.configurator.common.xmlwise.XmlParseException;
import java.awt.Component;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/standalone/configurator/common/PreferencesPlistHandler.class */
public final class PreferencesPlistHandler {
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private static final String XML_HEAD = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
    private static final String PLIST_OPEN_TAG = "<plist version=\"1.0\">";
    private static final String PLIST_CLOSE_TAG = "</plist>";
    private static final String SMARTCARDS_KEY = "smartcards";

    /* JADX WARN: Not initialized variable reg: 11, insn: 0x00b7: MOVE (r0 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r11 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) A[TRY_LEAVE], block:B:42:0x00b7 */
    /* JADX WARN: Not initialized variable reg: 12, insn: 0x00bc: MOVE (r0 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r12 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]), block:B:44:0x00bc */
    public static void importPreferences(String str, Component component, boolean z) {
        InputStream inputStream;
        Throwable th;
        String string;
        if (str == null) {
            throw new IllegalStateException("El fichero de configuracion no puede ser nulo");
        }
        try {
            try {
                FileInputStream fileInputStream = new FileInputStream(str);
                Throwable th2 = null;
                BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
                Throwable th3 = null;
                try {
                    try {
                        byte[] dataFromInputStream = AOUtil.getDataFromInputStream(bufferedInputStream);
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
                        if (!AOFileUtils.isXML(dataFromInputStream)) {
                            LOGGER.severe("El fichero de preferencias no es XML");
                            AOUIFactory.showErrorMessage(ConfiguratorCommonMessages.getString("PreferencesPlistHandler.9"), ConfiguratorCommonMessages.getString("PreferencesPlistHandler.1"), AOUIFactory.ERROR_MESSAGE, null);
                            return;
                        }
                        try {
                            XAdESValidator.validate(dataFromInputStream, true);
                            try {
                                string = ConfiguratorCommonMessages.getString("PreferencesPlistHandler.5", AOUtil.getCN(((AOSimpleSignInfo) ((AOTreeNode) AOTreeModel.getChild(new AOXAdESSigner().getSignersStructure(dataFromInputStream, true).getRoot(), 0)).getUserObject()).getCerts()[0]));
                            } catch (AOInvalidFormatException e) {
                                LOGGER.log(Level.SEVERE, "No se ha podido extraer la informacion de la firma del fichero de configuracion", (Throwable) e);
                                AOUIFactory.showErrorMessage(ConfiguratorCommonMessages.getString("PreferencesPlistHandler.7"), ConfiguratorCommonMessages.getString("PreferencesPlistHandler.1"), AOUIFactory.ERROR_MESSAGE, e);
                                return;
                            }
                        } catch (AOInvalidFormatException e2) {
                            LOGGER.severe("El fichero de configuracion no esta firmado");
                            string = ConfiguratorCommonMessages.getString("PreferencesPlistHandler.3");
                        } catch (Exception e3) {
                            LOGGER.severe("El fichero de configuracion contiene una firma invalida: " + e3);
                            AOUIFactory.showErrorMessage(ConfiguratorCommonMessages.getString("PreferencesPlistHandler.4"), ConfiguratorCommonMessages.getString("PreferencesPlistHandler.1"), AOUIFactory.ERROR_MESSAGE, null);
                            return;
                        }
                        if (AOUIFactory.NO_OPTION == AOUIFactory.showConfirmDialog(component, ConfiguratorCommonMessages.getString("PreferencesPlistHandler.10", string), ConfiguratorCommonMessages.getString("PreferencesPlistHandler.11"), AOUIFactory.YES_NO_OPTION, AOUIFactory.WARNING_MESSAGE)) {
                            return;
                        }
                        try {
                            importUserPreferencesFromXml(new String(dataFromInputStream), z);
                        } catch (Exception e4) {
                            LOGGER.log(Level.SEVERE, "El fichero de configuracion es invalido", (Throwable) e4);
                            AOUIFactory.showErrorMessage(ConfiguratorCommonMessages.getString("PreferencesPlistHandler.8"), ConfiguratorCommonMessages.getString("PreferencesPlistHandler.1"), AOUIFactory.ERROR_MESSAGE, e4);
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
        } catch (Exception e5) {
            LOGGER.log(Level.SEVERE, "No ha sido posible leer el fichero de configuracion", (Throwable) e5);
            AOUIFactory.showErrorMessage(ConfiguratorCommonMessages.getString("PreferencesPlistHandler.0"), ConfiguratorCommonMessages.getString("PreferencesPlistHandler.1"), AOUIFactory.ERROR_MESSAGE, e5);
        }
    }

    public static void importUserPreferencesFromXml(String str, boolean z) throws InvalidPreferencesFileException {
        Map<String, Object> mapLoadPreferencesFromXml = loadPreferencesFromXml(str, z);
        if (mapLoadPreferencesFromXml.containsKey(SMARTCARDS_KEY)) {
            KeyStorePreferencesManager.putUserSmartCardsMap((Map) mapLoadPreferencesFromXml.get(SMARTCARDS_KEY));
            mapLoadPreferencesFromXml.remove(SMARTCARDS_KEY);
        }
        checkPreferences(mapLoadPreferencesFromXml);
        storeUserPreferences(mapLoadPreferencesFromXml, z);
    }

    public static Map<String, Object> loadPreferencesFromXml(String str, boolean z) throws InvalidPreferencesFileException {
        try {
            return Plist.fromXml(str);
        } catch (XmlParseException e) {
            throw new InvalidPreferencesFileException("Error analizando el fichero XML: " + e, e);
        }
    }

    public static void importSystemPreferencesFromXml(byte[] bArr) throws InvalidPreferencesFileException {
        try {
            Map<String, Object> mapFromXml = Plist.fromXml(bArr);
            if (mapFromXml.containsKey(SMARTCARDS_KEY)) {
                KeyStorePreferencesManager.putSystemSmartCardsMap((Map) mapFromXml.get(SMARTCARDS_KEY));
                mapFromXml.remove(SMARTCARDS_KEY);
            }
            checkPreferences(mapFromXml);
            storeSystemPreferences(mapFromXml);
        } catch (XmlParseException e) {
            throw new InvalidPreferencesFileException("Error analizando el fichero XML: " + e, e);
        }
    }

    public static String exportPreferencesToXml() {
        Map<String, Object> prefsToExport = PreferencesManager.getPrefsToExport();
        Map<String, String> allSmartCardsMap = KeyStorePreferencesManager.getAllSmartCardsMap();
        if (allSmartCardsMap.size() > 0) {
            prefsToExport.put(SMARTCARDS_KEY, allSmartCardsMap);
        }
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><plist version=\"1.0\">" + Plist.objectToXml(prefsToExport).toXml() + PLIST_CLOSE_TAG;
    }

    private static void checkPreferences(Map<String, Object> map) throws InvalidPreferencesFileException {
        for (String str : map.keySet()) {
            Object obj = map.get(str);
            if (obj == null) {
                throw new InvalidPreferencesFileException("El valor de la clave de preferencia " + str + " es nulo");
            }
            if (!(obj instanceof String) && !(obj instanceof Boolean)) {
                throw new InvalidPreferencesFileException("No se soporta el tipo " + obj.getClass().getName() + " en un fichero de preferencias");
            }
        }
    }

    private static void storeUserPreferences(Map<String, Object> map, boolean z) {
        for (String str : map.keySet()) {
            Object obj = map.get(str);
            if (!PreferencesManager.isProtectedPreference(str) || !z) {
                if (obj instanceof Boolean) {
                    PreferencesManager.putBoolean(str, ((Boolean) obj).booleanValue());
                } else {
                    PreferencesManager.put(str, obj.toString());
                }
            }
        }
    }

    private static void storeSystemPreferences(Map<String, Object> map) {
        for (String str : map.keySet()) {
            Object obj = map.get(str);
            if (obj instanceof Boolean) {
                PreferencesManager.putBooleanSystemPref(str, ((Boolean) obj).booleanValue());
            } else {
                PreferencesManager.putSystemPref(str, obj.toString());
            }
        }
        try {
            PreferencesManager.flushSystemPrefs();
        } catch (BackingStoreException e) {
            LOGGER.warning("No se pudo guardar la preferencia del sistema: " + e);
        }
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/standalone/configurator/common/PreferencesPlistHandler$InvalidPreferencesFileException.class */
    public static class InvalidPreferencesFileException extends Exception {
        private static final long serialVersionUID = 1;

        InvalidPreferencesFileException(String str, Throwable th) {
            super(str, th);
        }

        InvalidPreferencesFileException(String str) {
            super(str);
        }
    }
}
