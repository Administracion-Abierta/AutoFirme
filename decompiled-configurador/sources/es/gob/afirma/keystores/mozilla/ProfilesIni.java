package es.gob.afirma.keystores.mozilla;

import es.gob.afirma.signers.tsp.pkcs7.TsaParams;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/keystores/mozilla/ProfilesIni.class */
class ProfilesIni {
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private static final String PROFILES_ATTR_NAME = "name=";
    private static final String PROFILES_ATTR_RELATIVE = "isrelative=";
    private static final String PROFILES_ATTR_PATH = "path=";
    private static final String PROFILES_ATTR_DEFAULT = "default=";
    private static final String PROFILES_ATTR_VERSION = "version=";
    private static final String PROFILES_ATTR_START_WITH_LAST_PROFILE = "startwithlastprofile=";
    private static final String PROFILES_ATTR_LOCKED = "locked=";
    private final List<FirefoxProfile> profilesList = new ArrayList();
    private GeneralInfo generalInfo;
    private StateInfo stateInfo;

    ProfilesIni(File file) throws IOException {
        this.generalInfo = new GeneralInfo();
        this.stateInfo = null;
        FileInputStream fileInputStream = new FileInputStream(file);
        Throwable th = null;
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream, StandardCharsets.UTF_8));
            Throwable th2 = null;
            while (true) {
                try {
                    try {
                        String line = bufferedReader.readLine();
                        if (line == null) {
                            break;
                        }
                        String strTrim = line.trim();
                        if (strTrim.startsWith("[")) {
                            bufferedReader.mark(256);
                            if (strTrim.toLowerCase().startsWith("[profile")) {
                                try {
                                    this.profilesList.add(readProfile(bufferedReader, file.getParentFile()));
                                } catch (Exception e) {
                                    LOGGER.warning("No se ha podido leer la informacion de uno de los perfiles de Firefox: " + e);
                                }
                            } else if (strTrim.equalsIgnoreCase("[General]")) {
                                try {
                                    this.generalInfo = readGeneralInfo(bufferedReader);
                                } catch (Exception e2) {
                                    LOGGER.warning("No se ha podido leer la seccion general del fichero de perfiles de Firefox: " + e2);
                                }
                            } else if (strTrim.toLowerCase().startsWith("[install")) {
                                try {
                                    StateInfo stateInfo = readStateInfo(bufferedReader);
                                    if (this.stateInfo == null || !this.stateInfo.isLockedDeclared()) {
                                        this.stateInfo = stateInfo;
                                    }
                                } catch (Exception e3) {
                                    LOGGER.warning("No se ha podido leer la seccion " + strTrim + " con el perfil activo de Firefox: " + e3);
                                }
                            }
                        }
                    } catch (Throwable th3) {
                        th2 = th3;
                        throw th3;
                    }
                } catch (Throwable th4) {
                    if (bufferedReader != null) {
                        if (th2 != null) {
                            try {
                                bufferedReader.close();
                            } catch (Throwable th5) {
                                th2.addSuppressed(th5);
                            }
                        } else {
                            bufferedReader.close();
                        }
                    }
                    throw th4;
                }
            }
            if (bufferedReader != null) {
                if (0 != 0) {
                    try {
                        bufferedReader.close();
                    } catch (Throwable th6) {
                        th2.addSuppressed(th6);
                    }
                } else {
                    bufferedReader.close();
                }
            }
            if (fileInputStream != null) {
                if (0 == 0) {
                    fileInputStream.close();
                    return;
                }
                try {
                    fileInputStream.close();
                } catch (Throwable th7) {
                    th.addSuppressed(th7);
                }
            }
        } catch (Throwable th8) {
            if (fileInputStream != null) {
                if (0 != 0) {
                    try {
                        fileInputStream.close();
                    } catch (Throwable th9) {
                        th.addSuppressed(th9);
                    }
                } else {
                    fileInputStream.close();
                }
            }
            throw th8;
        }
    }

    public List<FirefoxProfile> getProfilesList() {
        if (this.profilesList != null) {
            return new ArrayList(this.profilesList);
        }
        return null;
    }

    public GeneralInfo getGeneralInfo() {
        return this.generalInfo;
    }

    public StateInfo getStateInfo() {
        return this.stateInfo;
    }

    private static FirefoxProfile readProfile(BufferedReader bufferedReader, File file) throws IOException {
        String line;
        String path;
        FirefoxProfile firefoxProfile = new FirefoxProfile();
        while (true) {
            line = bufferedReader.readLine();
            if (line == null || line.trim().isEmpty() || line.trim().startsWith("[")) {
                break;
            }
            bufferedReader.mark(256);
            String strTrim = line.trim();
            if (strTrim.toLowerCase().startsWith(PROFILES_ATTR_NAME)) {
                firefoxProfile.setName(strTrim.substring(PROFILES_ATTR_NAME.length()));
            } else if (strTrim.toLowerCase().startsWith(PROFILES_ATTR_RELATIVE)) {
                firefoxProfile.setRelative(strTrim.substring(PROFILES_ATTR_RELATIVE.length()).equals(TsaParams.TS_SIGN));
            } else if (strTrim.toLowerCase().startsWith(PROFILES_ATTR_PATH)) {
                firefoxProfile.setPath(strTrim.substring(PROFILES_ATTR_PATH.length()));
            } else if (strTrim.toLowerCase().startsWith(PROFILES_ATTR_DEFAULT)) {
                firefoxProfile.setDefault(strTrim.substring(PROFILES_ATTR_DEFAULT.length()).equals(TsaParams.TS_SIGN));
            }
        }
        if (line != null && line.trim().startsWith("[")) {
            try {
                bufferedReader.reset();
            } catch (Exception e) {
                LOGGER.warning("Se ha encontrado problemas en la forma del 'profiles.ini' de Mozilla. Es posible que no se seleccione el perfil adecuado: " + e);
            }
        }
        if (firefoxProfile.getName() == null || firefoxProfile.getPath() == null) {
            throw new IllegalStateException("No se ha encontrado la informacion obligatoria del perfil");
        }
        if (firefoxProfile.isRelative()) {
            path = new File(file, firefoxProfile.getPath()).getAbsolutePath();
        } else {
            path = firefoxProfile.getPath();
        }
        firefoxProfile.setAbsolutePath(path);
        firefoxProfile.setLocked(new File(firefoxProfile.getAbsolutePath(), "lock").exists() || Files.isSymbolicLink(new File(firefoxProfile.getAbsolutePath(), "lock").toPath()) || new File(firefoxProfile.getAbsolutePath(), "parent.lock").exists());
        return firefoxProfile;
    }

    private static GeneralInfo readGeneralInfo(BufferedReader bufferedReader) throws IOException, NumberFormatException {
        String line;
        GeneralInfo generalInfo = new GeneralInfo();
        while (true) {
            line = bufferedReader.readLine();
            if (line == null || line.trim().isEmpty() || line.trim().startsWith("[")) {
                break;
            }
            bufferedReader.mark(256);
            String strTrim = line.trim();
            if (strTrim.toLowerCase().startsWith(PROFILES_ATTR_VERSION)) {
                generalInfo.setVersion(Integer.parseInt(strTrim.substring(PROFILES_ATTR_VERSION.length())));
            } else if (strTrim.toLowerCase().startsWith(PROFILES_ATTR_START_WITH_LAST_PROFILE)) {
                generalInfo.setStartWithLastProfile(strTrim.substring(PROFILES_ATTR_START_WITH_LAST_PROFILE.length()).equals(TsaParams.TS_SIGN));
            }
        }
        if (line != null && line.trim().startsWith("[")) {
            bufferedReader.reset();
        }
        return generalInfo;
    }

    private static StateInfo readStateInfo(BufferedReader bufferedReader) throws IOException {
        String line;
        StateInfo stateInfo = new StateInfo();
        while (true) {
            line = bufferedReader.readLine();
            if (line == null || line.trim().isEmpty() || line.trim().startsWith("[")) {
                break;
            }
            bufferedReader.mark(256);
            String strTrim = line.trim();
            if (strTrim.toLowerCase().startsWith(PROFILES_ATTR_DEFAULT)) {
                stateInfo.setDefaultProfilePath(strTrim.substring(PROFILES_ATTR_DEFAULT.length()));
            } else if (strTrim.toLowerCase().startsWith(PROFILES_ATTR_LOCKED)) {
                stateInfo.setLocked(strTrim.substring(PROFILES_ATTR_LOCKED.length()).equals(TsaParams.TS_SIGN));
            }
        }
        if (line != null && line.trim().startsWith("[")) {
            bufferedReader.reset();
        }
        if (stateInfo.getDefaultProfilePath() == null) {
            throw new IllegalStateException("No se ha encontrado la informacion sobre el perfil activo");
        }
        return stateInfo;
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/keystores/mozilla/ProfilesIni$FirefoxProfile.class */
    static final class FirefoxProfile {
        private String name = null;
        private boolean relative = true;
        private String path = null;
        private String absolutePath = null;
        private boolean def = false;
        private boolean locked = false;

        FirefoxProfile() {
        }

        String getName() {
            return this.name;
        }

        void setName(String str) {
            this.name = str;
        }

        boolean isRelative() {
            return this.relative;
        }

        void setRelative(boolean z) {
            this.relative = z;
        }

        String getPath() {
            return this.path;
        }

        void setPath(String str) {
            this.path = str;
        }

        String getAbsolutePath() {
            return this.absolutePath;
        }

        void setAbsolutePath(String str) {
            this.absolutePath = str;
        }

        boolean isDefault() {
            return this.def;
        }

        void setDefault(boolean z) {
            this.def = z;
        }

        boolean isLocked() {
            return this.locked;
        }

        void setLocked(boolean z) {
            this.locked = z;
        }

        public String toString() {
            return "Perfil de Firefox" + (this.locked ? " bloqueado" : " no bloqueado") + (this.def ? " y por defecto " : "") + (this.absolutePath != null ? " situado en: " + this.absolutePath : "");
        }
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/keystores/mozilla/ProfilesIni$GeneralInfo.class */
    static final class GeneralInfo {
        private int version = 1;
        private boolean startWithLastProfile = true;

        GeneralInfo() {
        }

        public int getVersion() {
            return this.version;
        }

        public void setVersion(int i) {
            this.version = i;
        }

        public boolean isStartWithLastProfile() {
            return this.startWithLastProfile;
        }

        public void setStartWithLastProfile(boolean z) {
            this.startWithLastProfile = z;
        }
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/keystores/mozilla/ProfilesIni$StateInfo.class */
    static final class StateInfo {
        private String defaultProfilePath = null;
        private boolean locked = true;
        private boolean lockedDeclared = false;

        StateInfo() {
        }

        public String getDefaultProfilePath() {
            return this.defaultProfilePath;
        }

        public void setDefaultProfilePath(String str) {
            this.defaultProfilePath = str;
        }

        public boolean isLocked() {
            return this.locked;
        }

        public void setLocked(boolean z) {
            this.locked = z;
            this.lockedDeclared = true;
        }

        public boolean isLockedDeclared() {
            return this.lockedDeclared;
        }
    }
}
