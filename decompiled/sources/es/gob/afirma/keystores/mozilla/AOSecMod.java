package es.gob.afirma.keystores.mozilla;

import es.gob.afirma.core.AOException;
import es.gob.afirma.core.misc.AOUtil;
import es.gob.afirma.core.misc.Platform;
import es.gob.afirma.keystores.KeyStoreUtilities;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/keystores/mozilla/AOSecMod.class */
public final class AOSecMod {
    private static final int FIRST_ASCII_PRINTABLE_CODE = 32;
    private static final int LAST_ASCII_PRINTABLE_CODE = 126;
    private static List<ModuleName> modules = null;

    private AOSecMod() {
    }

    private static String cleanModuleName(String str) {
        if (str == null) {
            return "";
        }
        char[] charArray = str.toCharArray();
        int i = 0;
        for (int i2 = 0; i2 < charArray.length; i2++) {
            if (charArray[i2] < ' ' || charArray[i2] > '~') {
                i = i2 + 1;
            }
        }
        return str.substring(i);
    }

    private static ModuleName processNames(byte[] bArr, int i) {
        String strSearchPathForFile;
        int i2 = getShort(bArr, i + 0);
        String strCleanModuleName = cleanModuleName(new String(bArr, i + 2, i2));
        int i3 = i + i2 + 2;
        String str = new String(bArr, i3 + 2, getShort(bArr, i3));
        if ((isWindowsLib(str) || isUnixLib(str)) && (strSearchPathForFile = KeyStoreUtilities.searchPathForFile(new String[]{str})) != null) {
            return new ModuleName(strSearchPathForFile, strCleanModuleName);
        }
        throw new UnsupportedOperationException("Intento fallido: " + str);
    }

    private static boolean isUnixLib(String str) {
        return (str == null || Platform.OS.WINDOWS.equals(Platform.getOS()) || (!str.endsWith(".so") && !str.contains(".so.") && !str.endsWith(".dylib"))) ? false : true;
    }

    private static boolean isWindowsLib(String str) {
        return str != null && Platform.OS.WINDOWS.equals(Platform.getOS()) && (str.endsWith(".DLL") || str.endsWith(".dll"));
    }

    static List<ModuleName> getModules(String str) throws AOException {
        if (str == null || str.isEmpty()) {
            throw new IllegalArgumentException("El directorio del perfil de Mozilla no puede ser nulo");
        }
        if (modules == null) {
            String strReplace = str.replace("\\ ", " ");
            if (!strReplace.endsWith("/")) {
                strReplace = strReplace + "/";
            }
            File file = new File(strReplace, "secmod.db");
            if (!file.exists()) {
                throw new AOException("El directorio del perfil de Mozilla proporcionado no contiene una base de datos de modulos (secmod.db)");
            }
            try {
                InputStream inputStreamLoadFile = AOUtil.loadFile(AOUtil.createURI(file.getAbsolutePath()));
                Throwable th = null;
                try {
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
                        modules = new ArrayList();
                        HashSet hashSet = new HashSet();
                        for (int i = 0; i < dataFromInputStream.length; i++) {
                            try {
                                ModuleName moduleNameProcessNames = processNames(dataFromInputStream, i);
                                if (!hashSet.contains(moduleNameProcessNames.getLib())) {
                                    hashSet.add(moduleNameProcessNames.getLib());
                                    modules.add(moduleNameProcessNames);
                                }
                            } catch (IndexOutOfBoundsException e) {
                            } catch (UnsupportedOperationException e2) {
                            } catch (Exception e3) {
                                Logger.getLogger("es.gob.afirma").warning("Se omite un modulo PKCS#11 de Mozilla: " + e3);
                            }
                        }
                    } catch (Throwable th3) {
                        th = th3;
                        throw th3;
                    }
                } catch (Throwable th4) {
                    if (inputStreamLoadFile != null) {
                        if (th != null) {
                            try {
                                inputStreamLoadFile.close();
                            } catch (Throwable th5) {
                                th.addSuppressed(th5);
                            }
                        } else {
                            inputStreamLoadFile.close();
                        }
                    }
                    throw th4;
                }
            } catch (Exception e4) {
                throw new AOException("Error leyendo la base de datos de modulos (secmod.db)", e4);
            }
        }
        if (modules != null) {
            return new ArrayList(modules);
        }
        return null;
    }

    /* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/keystores/mozilla/AOSecMod$ModuleName.class */
    public static final class ModuleName {
        private final String lib;
        private final String description;

        public ModuleName(String str, String str2) {
            this.lib = str;
            this.description = str2;
        }

        String getLib() {
            return this.lib;
        }

        String getDescription() {
            return this.description;
        }

        public String toString() {
            return this.description + " (EXTERNAL, " + this.lib + ", slot 0)";
        }
    }

    private static int getShort(byte[] bArr, int i) {
        return (bArr[i + 0] << 8) | bArr[i + 1];
    }
}
