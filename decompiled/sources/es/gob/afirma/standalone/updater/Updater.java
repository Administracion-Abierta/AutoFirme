package es.gob.afirma.standalone.updater;

import es.gob.afirma.core.misc.Platform;
import es.gob.afirma.core.misc.http.UrlHttpMethod;
import es.gob.afirma.core.ui.AOUIFactory;
import es.gob.afirma.standalone.HttpManager;
import es.gob.afirma.standalone.configurator.common.PreferencesManager;
import java.awt.Desktop;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/updater/Updater.class */
public final class Updater {
    private static final String PREFERENCE_UPDATE_URL_VERSION_WINDOWS = "urlWindows";
    private static final String PREFERENCE_UPDATE_URL_VERSION_LINUX = "urlLinux";
    private static final String PREFERENCE_UPDATE_URL_VERSION_MACOSX = "urlMacosx";
    private static final String PREFERENCE_UPDATE_URL_SITE = "updateSite";
    private static final String KEY_PREFERENCE_PATH = "es/gob/afirma/standalone";
    private static final String PROPERTY_LASTCHECKDATE = "lastCheckDate";
    static final Logger LOGGER;
    private static String version = null;
    private static String currentVersion = null;
    private static String updateSite = null;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");
    private static Properties updaterProperties = null;

    static {
        loadProperties();
        LOGGER = Logger.getLogger("es.gob.afirma");
    }

    private Updater() {
    }

    static String getUpdateSite() {
        return updateSite;
    }

    private static void loadProperties() {
        if (updaterProperties != null) {
            return;
        }
        updaterProperties = new Properties();
        try {
            updaterProperties.load(Updater.class.getResourceAsStream("/properties/updater.properties"));
        } catch (Exception e) {
            LOGGER.severe("No se ha podido cargar el archivo de recursos del actualizador: " + e);
            updaterProperties = new Properties();
        }
    }

    public static String getCurrentVersion() {
        if (currentVersion == null) {
            currentVersion = updaterProperties.getProperty("currentVersion." + Platform.getOS(), "0");
        }
        return currentVersion;
    }

    public static String getCurrentVersionText() {
        return updaterProperties.getProperty("currentVersionText." + Platform.getOS(), "0");
    }

    private static String getLatestVersion() {
        if (updaterProperties == null) {
            return null;
        }
        String property = null;
        if (Platform.getOS() == Platform.OS.WINDOWS) {
            property = updaterProperties.getProperty(PREFERENCE_UPDATE_URL_VERSION_WINDOWS);
        } else if (Platform.getOS() == Platform.OS.LINUX) {
            property = updaterProperties.getProperty(PREFERENCE_UPDATE_URL_VERSION_LINUX);
        } else if (Platform.getOS() == Platform.OS.MACOSX) {
            property = updaterProperties.getProperty(PREFERENCE_UPDATE_URL_VERSION_MACOSX);
        }
        updateSite = updaterProperties.getProperty(PREFERENCE_UPDATE_URL_SITE);
        if (property == null) {
            LOGGER.warning("El archivo de recursos del actualizador no contiene una URL de comprobacion");
            return null;
        }
        try {
            version = new String(new HttpManager().readUrl(property, UrlHttpMethod.GET)).trim();
        } catch (Exception e) {
            LOGGER.warning("No se pudo recuperar cual es la ultima disponible de la aplicacion");
        }
        return version;
    }

    static boolean isNewVersionAvailable() {
        String latestVersion = getLatestVersion();
        if (latestVersion == null) {
            LOGGER.severe("No se puede comprobar si hay versiones nuevas del aplicativo");
            throw new RuntimeException();
        }
        if (getCurrentVersion() == null) {
            LOGGER.severe("No ha podido comprobar la version actual del aplicativo");
            throw new RuntimeException();
        }
        try {
            return Integer.parseInt(latestVersion) > Integer.parseInt(getCurrentVersion());
        } catch (Exception e) {
            LOGGER.warning("No ha podido comparar la version actual del aplicativo con la ultima disponible: " + e);
            throw new RuntimeException();
        }
    }

    public static boolean isOldVersion(String str) {
        try {
            if (Integer.parseInt(str) > Integer.parseInt(getCurrentVersion())) {
                return true;
            }
            return false;
        } catch (Exception e) {
            LOGGER.severe("No se ha podido comprobar si la version actual es menor que la requerida: " + e);
            return false;
        }
    }

    public static void checkForUpdates(Object obj) {
        boolean z = !PreferencesManager.getBoolean(PreferencesManager.PREFERENCE_GENERAL_UPDATECHECK);
        if (!z) {
            Calendar calendar = Calendar.getInstance();
            Calendar lastCheck = getLastCheck();
            z = (lastCheck.get(6) >= calendar.get(6) && lastCheck.get(1) == calendar.get(1)) || lastCheck.get(1) > calendar.get(1);
        }
        if (!z) {
            new Thread(() -> {
                try {
                    boolean zIsNewVersionAvailable = isNewVersionAvailable();
                    updateLastCheck();
                    if (zIsNewVersionAvailable) {
                        if (0 == AOUIFactory.showConfirmDialog(obj, UpdaterMessages.getString("Updater.1"), UpdaterMessages.getString("Updater.2"), 0, 1)) {
                            try {
                                Desktop.getDesktop().browse(new URI(getUpdateSite()));
                                return;
                            } catch (Exception e) {
                                LOGGER.log(Level.SEVERE, "No se ha podido abrir el sitio Web de actualizaciones de Autofirma: " + e, (Throwable) e);
                                AOUIFactory.showErrorMessage(UpdaterMessages.getString("Updater.3"), UpdaterMessages.getString("Updater.4"), 0, e);
                                return;
                            }
                        }
                        return;
                    }
                    LOGGER.info("Se ha encontrado instalada la ultima version disponible de Autofirma");
                } catch (Exception e2) {
                    LOGGER.info("No se ha podido comprobar la disponibilidad de nueva version: " + e2);
                }
            }).start();
        }
    }

    private static void updateLastCheck() {
        saveLastCheck(Calendar.getInstance().getTime());
        try {
            PreferencesManager.flush();
        } catch (BackingStoreException e) {
            LOGGER.warning("No se ha podido almacenar la fecha de la ultima comprobacion de version");
        }
    }

    private static Calendar getLastCheck() {
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(recoverLastCheck());
        } catch (Exception e) {
            LOGGER.warning("No se ha podido recuperar la fecha de la ultima comprobacion de version");
            calendar.set(1, 2000);
        }
        return calendar;
    }

    private static void saveLastCheck(Date date) {
        Preferences.userRoot().node(KEY_PREFERENCE_PATH).put(PROPERTY_LASTCHECKDATE, DATE_FORMAT.format(date));
    }

    private static Date recoverLastCheck() throws ParseException {
        String str = Preferences.userRoot().node(KEY_PREFERENCE_PATH).get(PROPERTY_LASTCHECKDATE, null);
        if (str == null) {
            return null;
        }
        return DATE_FORMAT.parse(str);
    }
}
