package es.gob.afirma.standalone;

import es.gob.afirma.core.misc.AOUtil;
import es.gob.afirma.core.misc.LoggerUtil;
import es.gob.afirma.standalone.updater.Updater;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/HelpResourceManager.class */
public final class HelpResourceManager {
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private static final int MAX_VERSION_LENGTH = 30;
    private static final String INTERNAL_HELP_DIR_NAME = "help/";
    private static final String INDEX_HELP_FILENAME = "help/fileslist.txt";
    private static final String HELP_VERSION_FILENAME = "help.version";

    private HelpResourceManager() {
    }

    static void extractHelpResources(File file) throws IOException {
        extractResources(file);
        FileOutputStream fileOutputStream = new FileOutputStream(new File(file, HELP_VERSION_FILENAME));
        Throwable th = null;
        try {
            fileOutputStream.write(Updater.getCurrentVersionText().getBytes());
            if (fileOutputStream != null) {
                if (0 != 0) {
                    try {
                        fileOutputStream.close();
                        return;
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                        return;
                    }
                }
                fileOutputStream.close();
            }
        } catch (Throwable th3) {
            if (fileOutputStream != null) {
                if (0 != 0) {
                    try {
                        fileOutputStream.close();
                    } catch (Throwable th4) {
                        th.addSuppressed(th4);
                    }
                } else {
                    fileOutputStream.close();
                }
            }
            throw th3;
        }
    }

    /* JADX WARN: Not initialized variable reg: 10, insn: 0x01f4: MOVE (r0 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r10 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) A[TRY_LEAVE], block:B:95:0x01f4 */
    /* JADX WARN: Not initialized variable reg: 11, insn: 0x01f8: MOVE (r0 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r11 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]), block:B:97:0x01f8 */
    private static void extractResources(File file) throws IOException {
        InputStreamReader inputStreamReader;
        Throwable th;
        createHelpDir(file);
        InputStream systemResourceAsStream = ClassLoader.getSystemResourceAsStream(INDEX_HELP_FILENAME);
        Throwable th2 = null;
        try {
            try {
                try {
                    InputStreamReader inputStreamReader2 = new InputStreamReader(systemResourceAsStream);
                    Throwable th3 = null;
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader2);
                    Throwable th4 = null;
                    while (true) {
                        try {
                            String line = bufferedReader.readLine();
                            if (line == null) {
                                break;
                            }
                            if (!line.trim().isEmpty()) {
                                try {
                                    InputStream systemResourceAsStream2 = ClassLoader.getSystemResourceAsStream(INTERNAL_HELP_DIR_NAME + line);
                                    Throwable th5 = null;
                                    try {
                                        try {
                                            File file2 = new File(file, line.replace("/", File.separator));
                                            if (!file2.getParentFile().exists()) {
                                                file2.getParentFile().mkdirs();
                                            }
                                            FileOutputStream fileOutputStream = new FileOutputStream(file2);
                                            Throwable th6 = null;
                                            try {
                                                try {
                                                    fileOutputStream.write(AOUtil.getDataFromInputStream(systemResourceAsStream2));
                                                    if (fileOutputStream != null) {
                                                        if (0 != 0) {
                                                            try {
                                                                fileOutputStream.close();
                                                            } catch (Throwable th7) {
                                                                th6.addSuppressed(th7);
                                                            }
                                                        } else {
                                                            fileOutputStream.close();
                                                        }
                                                    }
                                                    if (systemResourceAsStream2 != null) {
                                                        if (0 != 0) {
                                                            try {
                                                                systemResourceAsStream2.close();
                                                            } catch (Throwable th8) {
                                                                th5.addSuppressed(th8);
                                                            }
                                                        } else {
                                                            systemResourceAsStream2.close();
                                                        }
                                                    }
                                                } catch (Throwable th9) {
                                                    th6 = th9;
                                                    throw th9;
                                                }
                                            } catch (Throwable th10) {
                                                if (fileOutputStream != null) {
                                                    if (th6 != null) {
                                                        try {
                                                            fileOutputStream.close();
                                                        } catch (Throwable th11) {
                                                            th6.addSuppressed(th11);
                                                        }
                                                    } else {
                                                        fileOutputStream.close();
                                                    }
                                                }
                                                throw th10;
                                            }
                                        } catch (Throwable th12) {
                                            th5 = th12;
                                            throw th12;
                                        }
                                    } catch (Throwable th13) {
                                        if (systemResourceAsStream2 != null) {
                                            if (th5 != null) {
                                                try {
                                                    systemResourceAsStream2.close();
                                                } catch (Throwable th14) {
                                                    th5.addSuppressed(th14);
                                                }
                                            } else {
                                                systemResourceAsStream2.close();
                                            }
                                        }
                                        throw th13;
                                    }
                                } catch (Exception e) {
                                    LOGGER.log(Level.WARNING, "No se ha encontrado o no ha podido leerse el fichero de ayuda " + LoggerUtil.getCleanUserHomePath(line), (Throwable) e);
                                }
                            }
                        } catch (Throwable th15) {
                            if (bufferedReader != null) {
                                if (0 != 0) {
                                    try {
                                        bufferedReader.close();
                                    } catch (Throwable th16) {
                                        th4.addSuppressed(th16);
                                    }
                                } else {
                                    bufferedReader.close();
                                }
                            }
                            throw th15;
                        }
                    }
                    if (bufferedReader != null) {
                        if (0 != 0) {
                            try {
                                bufferedReader.close();
                            } catch (Throwable th17) {
                                th4.addSuppressed(th17);
                            }
                        } else {
                            bufferedReader.close();
                        }
                    }
                    if (inputStreamReader2 != null) {
                        if (0 != 0) {
                            try {
                                inputStreamReader2.close();
                            } catch (Throwable th18) {
                                th3.addSuppressed(th18);
                            }
                        } else {
                            inputStreamReader2.close();
                        }
                    }
                    if (systemResourceAsStream != null) {
                        if (0 == 0) {
                            systemResourceAsStream.close();
                            return;
                        }
                        try {
                            systemResourceAsStream.close();
                        } catch (Throwable th19) {
                            th2.addSuppressed(th19);
                        }
                    }
                } catch (Throwable th20) {
                    if (inputStreamReader != null) {
                        if (th != null) {
                            try {
                                inputStreamReader.close();
                            } catch (Throwable th21) {
                                th.addSuppressed(th21);
                            }
                        } else {
                            inputStreamReader.close();
                        }
                    }
                    throw th20;
                }
            } catch (Throwable th22) {
                throw th22;
            }
        } catch (Throwable th23) {
            if (systemResourceAsStream != null) {
                if (0 != 0) {
                    try {
                        systemResourceAsStream.close();
                    } catch (Throwable th24) {
                        th2.addSuppressed(th24);
                    }
                } else {
                    systemResourceAsStream.close();
                }
            }
            throw th23;
        }
    }

    private static void createHelpDir(File file) {
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public static boolean isDifferentHelpFile(File file) {
        if (!file.exists()) {
            return true;
        }
        byte[] bArr = new byte[(int) Math.min(file.length(), 30L)];
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            Throwable th = null;
            try {
                fileInputStream.read(bArr);
                if (fileInputStream != null) {
                    if (0 != 0) {
                        try {
                            fileInputStream.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    } else {
                        fileInputStream.close();
                    }
                }
                String str = new String(bArr, StandardCharsets.UTF_8);
                String currentVersion = Updater.getCurrentVersion();
                return !str.equals(currentVersion.substring(0, Math.min(currentVersion.length(), 30)));
            } catch (Throwable th3) {
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
                throw th3;
            }
        } catch (Exception e) {
            LOGGER.warning("No se pudo leer el fichero con la version de la ayuda. Se generara la nueva ayuda: " + e);
            return true;
        }
    }

    static boolean isLocalHelpUpdated(File file) {
        boolean zEquals;
        try {
            FileInputStream fileInputStream = new FileInputStream(new File(file, HELP_VERSION_FILENAME));
            Throwable th = null;
            try {
                try {
                    zEquals = Updater.getCurrentVersionText().equals(new String(AOUtil.getDataFromInputStream(fileInputStream)));
                    if (fileInputStream != null) {
                        if (0 != 0) {
                            try {
                                fileInputStream.close();
                            } catch (Throwable th2) {
                                th.addSuppressed(th2);
                            }
                        } else {
                            fileInputStream.close();
                        }
                    }
                } catch (Throwable th3) {
                    th = th3;
                    throw th3;
                }
            } catch (Throwable th4) {
                if (fileInputStream != null) {
                    if (th != null) {
                        try {
                            fileInputStream.close();
                        } catch (Throwable th5) {
                            th.addSuppressed(th5);
                        }
                    } else {
                        fileInputStream.close();
                    }
                }
                throw th4;
            }
        } catch (Exception e) {
            if (!(e instanceof FileNotFoundException)) {
                LOGGER.log(Level.WARNING, "No se ha podido leer el fichero de version de la ayuda local", (Throwable) e);
            }
            zEquals = false;
        }
        return zEquals;
    }

    public static String createHelpFileLauncher(String str) throws Exception {
        File file = new File(System.getProperty("java.io.tmpdir"), "local_launcher.html");
        String strReplace = str.replace("\\", "/");
        try {
            PrintWriter printWriter = new PrintWriter(file, "UTF-8");
            Throwable th = null;
            try {
                try {
                    printWriter.println("<meta http-equiv=\"refresh\" content=\"0; url=" + strReplace + "\" />");
                    printWriter.println("<body onload=\"setTimeout(location.href='file://" + strReplace + "', 1000)\">");
                    if (printWriter != null) {
                        if (0 != 0) {
                            try {
                                printWriter.close();
                            } catch (Throwable th2) {
                                th.addSuppressed(th2);
                            }
                        } else {
                            printWriter.close();
                        }
                    }
                } catch (Throwable th3) {
                    th = th3;
                    throw th3;
                }
            } catch (Throwable th4) {
                if (printWriter != null) {
                    if (th != null) {
                        try {
                            printWriter.close();
                        } catch (Throwable th5) {
                            th.addSuppressed(th5);
                        }
                    } else {
                        printWriter.close();
                    }
                }
                throw th4;
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error abriendo archivo a escribir", (Throwable) e);
        }
        return "file:///" + file.getAbsolutePath().replace("\\", "/");
    }
}
