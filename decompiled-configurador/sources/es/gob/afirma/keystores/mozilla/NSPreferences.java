package es.gob.afirma.keystores.mozilla;

import es.gob.afirma.core.misc.Platform;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/keystores/mozilla/NSPreferences.class */
final class NSPreferences {
    private static final int MIN_FIREFOX_FILES_ON_PROFILE = 10;
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private static Map<String, String> activeProfilesDir = null;

    private NSPreferences() {
    }

    static String getFireFoxUserProfileDirectory(File file) throws IOException {
        if (file == null) {
            throw new IllegalArgumentException("El fichero de perfiles es nulo y no se podra determinar el directorio del usuario de Firefox");
        }
        if (activeProfilesDir == null) {
            activeProfilesDir = new HashMap();
        }
        String canonicalPath = file.getCanonicalPath();
        if (activeProfilesDir.containsKey(canonicalPath)) {
            return activeProfilesDir.get(canonicalPath);
        }
        if (!file.isFile()) {
            throw new IOException("No se ha encontrado el fichero con los perfiles de Firefox en: " + file);
        }
        String absolutePath = null;
        ProfilesIni profilesIniLoadProfilesIni = loadProfilesIni(file);
        ProfilesIni.FirefoxProfile activeProfile = getActiveProfile(profilesIniLoadProfilesIni);
        if (activeProfile != null && !isDummyProfile(activeProfile)) {
            absolutePath = activeProfile.getAbsolutePath();
            LOGGER.info("Se utilizara el perfil activo de Mozilla: '" + getCleanPath(absolutePath));
        }
        if (absolutePath == null) {
            for (ProfilesIni.FirefoxProfile firefoxProfile : (ProfilesIni.FirefoxProfile[]) profilesIniLoadProfilesIni.getProfilesList().toArray(new ProfilesIni.FirefoxProfile[0])) {
                if (!isDummyProfile(firefoxProfile) && firefoxProfile.isDefault()) {
                    absolutePath = firefoxProfile.getAbsolutePath();
                    LOGGER.info("Se utilizara el perfil por defecto para Mozilla : " + getCleanPath(absolutePath));
                    break;
                }
            }
        }
        if (absolutePath == null) {
            long jLastModified = 0;
            for (ProfilesIni.FirefoxProfile firefoxProfile2 : (ProfilesIni.FirefoxProfile[]) profilesIniLoadProfilesIni.getProfilesList().toArray(new ProfilesIni.FirefoxProfile[0])) {
                if (!isDummyProfile(firefoxProfile2) && new File(firefoxProfile2.getAbsolutePath()).lastModified() > jLastModified) {
                    jLastModified = new File(firefoxProfile2.getAbsolutePath()).lastModified();
                    absolutePath = firefoxProfile2.getAbsolutePath();
                    LOGGER.info("Se usara el ultimo perfil modificado de Mozilla: " + getCleanPath(absolutePath));
                }
            }
        }
        if (absolutePath == null && !profilesIniLoadProfilesIni.getProfilesList().isEmpty()) {
            absolutePath = profilesIniLoadProfilesIni.getProfilesList().get(0).getAbsolutePath();
            LOGGER.info("Se utilizara el primer perfil encontrado de Mozilla: " + getCleanPath(absolutePath));
        }
        activeProfilesDir.put(canonicalPath, absolutePath);
        return absolutePath;
    }

    private static String getCleanPath(String str) {
        return str.replace(Platform.getUserHome(), "USERHOME");
    }

    private static ProfilesIni.FirefoxProfile getActiveProfile(ProfilesIni profilesIni) {
        String defaultProfilePath;
        ProfilesIni.FirefoxProfile firefoxProfile = null;
        if (profilesIni.getGeneralInfo().getVersion() == 1) {
            for (ProfilesIni.FirefoxProfile firefoxProfile2 : profilesIni.getProfilesList()) {
                if (!isDummyProfile(firefoxProfile2) && firefoxProfile2.isLocked()) {
                    firefoxProfile = firefoxProfile2;
                    LOGGER.info("Se toma como perfil activo de Mozilla el primer perfil valido bloqueado");
                    break;
                }
            }
        } else {
            ProfilesIni.StateInfo stateInfo = profilesIni.getStateInfo();
            if (stateInfo != null && (defaultProfilePath = stateInfo.getDefaultProfilePath()) != null) {
                for (ProfilesIni.FirefoxProfile firefoxProfile3 : profilesIni.getProfilesList()) {
                    if (defaultProfilePath.equals(firefoxProfile3.getPath())) {
                        firefoxProfile = firefoxProfile3;
                        LOGGER.info("Se toma como perfil activo de Mozilla el indicado en el 'profiles.ini' v2 o sup.");
                        break;
                    }
                }
            }
        }
        if (firefoxProfile == null) {
            LOGGER.info("No se encontro el perfil activo de Mozilla");
        }
        return firefoxProfile;
    }

    private static boolean isDummyProfile(ProfilesIni.FirefoxProfile firefoxProfile) {
        if (new File(firefoxProfile.getAbsolutePath()).list().length < 10) {
            LOGGER.fine("Se descarta el perfil '" + getCleanPath(firefoxProfile.getAbsolutePath()) + "' por no alcanzar el numero de archivos de un perfil valido");
            return true;
        }
        if (!new File(firefoxProfile.getAbsolutePath(), "key4.db").canRead() && !new File(firefoxProfile.getAbsolutePath(), "key3.db").canRead()) {
            LOGGER.fine("Se descarta el perfil '" + getCleanPath(firefoxProfile.getAbsolutePath()) + "' por no tener un almacen de claves legible");
            return true;
        }
        return false;
    }

    private static ProfilesIni loadProfilesIni(File file) throws IOException {
        try {
            return new ProfilesIni(file);
        } catch (Exception e) {
            throw new IOException("No se pudo cargar el fichero de perfiles de Firefox", e);
        }
    }
}
