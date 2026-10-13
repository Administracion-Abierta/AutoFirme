package es.gob.afirma.keystores.mozilla;

import es.gob.afirma.core.misc.BoundedBufferedReader;
import es.gob.afirma.keystores.mozilla.shared.SharedNssUtil;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/keystores/mozilla/Pkcs11Txt.class */
public final class Pkcs11Txt {
    private static final String NAME_SEARCH_TOKEN = "name=\"";
    private static final String LIBRARY_SEARCH_TOKEN = "library=";
    private static final String PKCS11TXT_FILENAME = "pkcs11.txt";

    private Pkcs11Txt() {
    }

    public static List<AOSecMod.ModuleName> getModules() throws IOException {
        File file = new File(SharedNssUtil.getSharedUserProfileDirectory() + File.separator + PKCS11TXT_FILENAME);
        if (!file.isFile()) {
            return new ArrayList(0);
        }
        if (!file.canRead()) {
            Logger.getLogger("es.gob.afirma").warning("No hay permisos de lectura para 'pkcs11.txt' en " + file.getAbsolutePath());
            return new ArrayList(0);
        }
        return getModules(file);
    }

    static List<AOSecMod.ModuleName> getModules(File file) throws IOException {
        FileReader fileReader = new FileReader(file);
        Throwable th = null;
        try {
            List<AOSecMod.ModuleName> modules = getModules(fileReader);
            if (fileReader != null) {
                if (0 != 0) {
                    try {
                        fileReader.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                } else {
                    fileReader.close();
                }
            }
            return modules;
        } catch (Throwable th3) {
            if (fileReader != null) {
                if (0 != 0) {
                    try {
                        fileReader.close();
                    } catch (Throwable th4) {
                        th.addSuppressed(th4);
                    }
                } else {
                    fileReader.close();
                }
            }
            throw th3;
        }
    }

    static List<AOSecMod.ModuleName> getModules(Reader reader) throws IOException {
        ArrayList arrayList = new ArrayList();
        BoundedBufferedReader boundedBufferedReader = new BoundedBufferedReader(reader, 512, 4096);
        Throwable th = null;
        String strTrim = null;
        String strTrim2 = null;
        while (true) {
            try {
                try {
                    String line = boundedBufferedReader.readLine();
                    if (line == null) {
                        break;
                    }
                    if (line.trim().isEmpty()) {
                        strTrim = null;
                        strTrim2 = null;
                    } else {
                        int iIndexOf = -1;
                        if (line.startsWith(LIBRARY_SEARCH_TOKEN)) {
                            iIndexOf = LIBRARY_SEARCH_TOKEN.length();
                        } else if (line.indexOf(" library=") != -1) {
                            iIndexOf = line.indexOf(" library=") + " library=".length();
                        }
                        if (iIndexOf != -1) {
                            int iIndexOf2 = line.indexOf(" name=\"", iIndexOf);
                            if (iIndexOf2 != -1) {
                                strTrim = line.substring(iIndexOf, iIndexOf2).trim();
                                int length = iIndexOf2 + " name=\"".length();
                                strTrim2 = line.substring(length, line.indexOf(34, length));
                            } else {
                                strTrim = line.substring(iIndexOf).trim();
                            }
                        } else if (line.startsWith("name=")) {
                            strTrim2 = line.substring("name=".length()).trim();
                        }
                        if (strTrim != null && strTrim2 != null && !strTrim.isEmpty() && !strTrim2.isEmpty()) {
                            arrayList.add(new AOSecMod.ModuleName(strTrim, strTrim2));
                            strTrim = null;
                            strTrim2 = null;
                        }
                    }
                } catch (Throwable th2) {
                    th = th2;
                    throw th2;
                }
            } catch (Throwable th3) {
                if (boundedBufferedReader != null) {
                    if (th != null) {
                        try {
                            boundedBufferedReader.close();
                        } catch (Throwable th4) {
                            th.addSuppressed(th4);
                        }
                    } else {
                        boundedBufferedReader.close();
                    }
                }
                throw th3;
            }
        }
        if (boundedBufferedReader != null) {
            if (0 != 0) {
                try {
                    boundedBufferedReader.close();
                } catch (Throwable th5) {
                    th.addSuppressed(th5);
                }
            } else {
                boundedBufferedReader.close();
            }
        }
        return arrayList;
    }
}
