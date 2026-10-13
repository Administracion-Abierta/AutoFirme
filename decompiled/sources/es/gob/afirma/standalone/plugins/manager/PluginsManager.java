package es.gob.afirma.standalone.plugins.manager;

import es.gob.afirma.core.misc.AOFileUtils;
import es.gob.afirma.core.misc.LoggerUtil;
import es.gob.afirma.standalone.plugins.AfirmaPlugin;
import es.gob.afirma.standalone.plugins.MinimalPluginInfo;
import es.gob.afirma.standalone.plugins.Permission;
import es.gob.afirma.standalone.plugins.PluginControlledException;
import es.gob.afirma.standalone.plugins.PluginInfo;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/plugins/manager/PluginsManager.class */
public class PluginsManager {
    private static final String PLUGIN_RELATION_FILENAME = "installed";
    private static final String RELATIONS_SEPARATOR = ":";
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private List<AfirmaPlugin> pluginsLoadedList = null;
    private File pluginsDir;

    public PluginsManager(File file) {
        this.pluginsDir = null;
        this.pluginsDir = file;
    }

    public List<AfirmaPlugin> getPluginsLoadedList() throws PluginException {
        return getPluginsLoadedList(false);
    }

    public List<AfirmaPlugin> getPluginsLoadedList(boolean z) throws PluginException {
        if (this.pluginsLoadedList == null || z) {
            this.pluginsLoadedList = loadPlugins();
        }
        return new ArrayList(this.pluginsLoadedList);
    }

    private List<AfirmaPlugin> loadPlugins() throws PluginException {
        ArrayList arrayList = new ArrayList();
        File file = new File(this.pluginsDir, PLUGIN_RELATION_FILENAME);
        if (file.isFile() && file.canRead()) {
            try {
                for (MinimalPluginInfo minimalPluginInfo : loadPluginsList(file)) {
                    try {
                        arrayList.add(loadPlugin(minimalPluginInfo));
                    } catch (PluginException e) {
                        LOGGER.log(Level.WARNING, String.format("No se ha podido cargar la informacion del plugin %s. Se eliminara del listado", minimalPluginInfo.getInternalName()), (Throwable) e);
                    }
                }
            } catch (Exception e2) {
                throw new PluginException("Error al cargar el listado de plugins", e2);
            }
        }
        return arrayList;
    }

    /* JADX WARN: Not initialized variable reg: 10, insn: 0x0178: MOVE (r0 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r10 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]), block:B:79:0x0178 */
    /* JADX WARN: Not initialized variable reg: 9, insn: 0x0174: MOVE (r0 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r9 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) A[TRY_LEAVE], block:B:77:0x0174 */
    private static MinimalPluginInfo[] loadPluginsList(File file) throws IOException {
        FileInputStream fileInputStream;
        Throwable th;
        String line;
        if (!file.isFile() || !file.canRead()) {
            return new MinimalPluginInfo[0];
        }
        ArrayList arrayList = new ArrayList();
        try {
            try {
                FileInputStream fileInputStream2 = new FileInputStream(file);
                Throwable th2 = null;
                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream2);
                Throwable th3 = null;
                try {
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    Throwable th4 = null;
                    do {
                        try {
                            try {
                                line = bufferedReader.readLine();
                                if (line != null && !line.trim().isEmpty()) {
                                    try {
                                        String[] strArrSplit = line.trim().split(RELATIONS_SEPARATOR);
                                        arrayList.add(new MinimalPluginInfo(strArrSplit[0], Integer.parseInt(strArrSplit[1])));
                                    } catch (Exception e) {
                                        LOGGER.warning(String.format("No se puede cargar el plugin correspondiente a la linea %s. Se descartara.", line));
                                    }
                                }
                            } catch (Throwable th5) {
                                th4 = th5;
                                throw th5;
                            }
                        } catch (Throwable th6) {
                            if (bufferedReader != null) {
                                if (th4 != null) {
                                    try {
                                        bufferedReader.close();
                                    } catch (Throwable th7) {
                                        th4.addSuppressed(th7);
                                    }
                                } else {
                                    bufferedReader.close();
                                }
                            }
                            throw th6;
                        }
                    } while (line != null);
                    if (bufferedReader != null) {
                        if (0 != 0) {
                            try {
                                bufferedReader.close();
                            } catch (Throwable th8) {
                                th4.addSuppressed(th8);
                            }
                        } else {
                            bufferedReader.close();
                        }
                    }
                    if (inputStreamReader != null) {
                        if (0 != 0) {
                            try {
                                inputStreamReader.close();
                            } catch (Throwable th9) {
                                th3.addSuppressed(th9);
                            }
                        } else {
                            inputStreamReader.close();
                        }
                    }
                    if (fileInputStream2 != null) {
                        if (0 != 0) {
                            try {
                                fileInputStream2.close();
                            } catch (Throwable th10) {
                                th2.addSuppressed(th10);
                            }
                        } else {
                            fileInputStream2.close();
                        }
                    }
                    return (MinimalPluginInfo[]) arrayList.toArray(new MinimalPluginInfo[arrayList.size()]);
                } catch (Throwable th11) {
                    if (inputStreamReader != null) {
                        if (0 != 0) {
                            try {
                                inputStreamReader.close();
                            } catch (Throwable th12) {
                                th3.addSuppressed(th12);
                            }
                        } else {
                            inputStreamReader.close();
                        }
                    }
                    throw th11;
                }
            } catch (Exception e2) {
                throw new IOException("No se pudo leer el listado de plugins instalados", e2);
            }
        } catch (Throwable th13) {
            if (fileInputStream != null) {
                if (th != null) {
                    try {
                        fileInputStream.close();
                    } catch (Throwable th14) {
                        th.addSuppressed(th14);
                    }
                } else {
                    fileInputStream.close();
                }
            }
            throw th13;
        }
    }

    private AfirmaPlugin loadPlugin(MinimalPluginInfo minimalPluginInfo) throws PluginException {
        File file = new File(this.pluginsDir, minimalPluginInfo.getInternalName());
        if (!file.isDirectory()) {
            throw new PluginException("No se ha encontrado el plugin " + minimalPluginInfo.getInternalName());
        }
        File[] fileArrListFiles = file.listFiles(file2 -> {
            return file2.isFile() && file2.canRead();
        });
        if (fileArrListFiles == null || fileArrListFiles.length == 0) {
            throw new PluginException("No se han encontrado los ficheros del plugin " + minimalPluginInfo.getInternalName());
        }
        return loadPluginFromFiles(fileArrListFiles);
    }

    /* JADX WARN: Bottom block not found for handler: all -> 0x009b */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private static void savePluginsList(java.io.File r4, java.util.List<es.gob.afirma.standalone.plugins.AfirmaPlugin> r5) throws java.io.IOException {
        /*
            Method dump skipped, instruction units count: 205
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: es.gob.afirma.standalone.plugins.manager.PluginsManager.savePluginsList(java.io.File, java.util.List):void");
    }

    public static AfirmaPlugin loadPluginFromFiles(File[] fileArr) throws PluginException {
        try {
            return PluginLoader.loadPlugin(fileArr);
        } catch (PluginException e) {
            LOGGER.log(Level.SEVERE, "El plugin importado no es valido", (Throwable) e);
            throw e;
        } catch (IOException e2) {
            LOGGER.log(Level.SEVERE, "Error en la carga de un conjunto de ficheros de plugin", (Throwable) e2);
            throw new PluginException("Error en la carga de un conjunto de ficheros de plugin", e2);
        }
    }

    public static void closePlugin(AfirmaPlugin afirmaPlugin) {
        ClassLoader classLoader = afirmaPlugin.getClass().getClassLoader();
        if (classLoader instanceof URLClassLoader) {
            try {
                ((URLClassLoader) classLoader).close();
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "No se pudo descargar un plugin de memoria", (Throwable) e);
            }
        }
    }

    public AfirmaPlugin installPlugin(File file, String str) throws PluginControlledException, IOException, PluginException {
        File fileCopyPluginToDirectory = copyPluginToDirectory(file, str, this.pluginsDir);
        try {
            AfirmaPlugin afirmaPluginLoadPluginFromFiles = loadPluginFromFiles(new File[]{fileCopyPluginToDirectory});
            if (PermissionChecker.check(afirmaPluginLoadPluginFromFiles.getInfo(), Permission.INSTALL)) {
                try {
                    afirmaPluginLoadPluginFromFiles.install();
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Ocurrio un error al instalar el plugin. Lo desinstalamos", (Throwable) e);
                    try {
                        uninstallPlugin(afirmaPluginLoadPluginFromFiles);
                    } catch (Exception e2) {
                        LOGGER.warning("No se han podido eliminar los ficheros importados del plugin");
                    }
                    if (e instanceof PluginControlledException) {
                        throw ((PluginControlledException) e);
                    }
                    throw new PluginException("Ocurrio un error al instalar el plugin", e);
                }
            }
            addPluginToRelationsFile(afirmaPluginLoadPluginFromFiles.getInfo());
            if (this.pluginsLoadedList == null) {
                this.pluginsLoadedList = new ArrayList();
            }
            this.pluginsLoadedList.add(afirmaPluginLoadPluginFromFiles);
            return afirmaPluginLoadPluginFromFiles;
        } catch (Exception e3) {
            deleteDirectoryAndContent(fileCopyPluginToDirectory.getParentFile());
            throw new PluginException("No se pudo cargar el plugin recien importado", e3);
        }
    }

    private static File copyPluginToDirectory(File file, String str, File file2) throws PluginInstalledException, IOException {
        if (!file2.isDirectory()) {
            try {
                if (!file2.getParentFile().exists()) {
                    createDirWithPermissions(file2.getParentFile());
                }
                createDirWithPermissions(file2);
            } catch (Exception e) {
                throw new IOException("No se ha podido crear el directorio interno de plugins: " + LoggerUtil.getCleanUserHomePath(file2.getAbsolutePath()), e);
            }
        }
        File file3 = new File(file2, str);
        if (file3.exists()) {
            throw new PluginInstalledException("El plugin seleccionado ya se encuentra instalado");
        }
        try {
            createDirWithPermissions(file3);
            File file4 = new File(file3, file.getName());
            FileOutputStream fileOutputStream = new FileOutputStream(file4);
            Throwable th = null;
            try {
                try {
                    Files.copy(file.toPath(), fileOutputStream);
                    if (fileOutputStream != null) {
                        if (0 != 0) {
                            try {
                                fileOutputStream.close();
                            } catch (Throwable th2) {
                                th.addSuppressed(th2);
                            }
                        } else {
                            fileOutputStream.close();
                        }
                    }
                    AOFileUtils.setAllPermissions(file4);
                    return file4;
                } catch (Throwable th3) {
                    th = th3;
                    throw th3;
                }
            } catch (Throwable th4) {
                if (fileOutputStream != null) {
                    if (th != null) {
                        try {
                            fileOutputStream.close();
                        } catch (Throwable th5) {
                            th.addSuppressed(th5);
                        }
                    } else {
                        fileOutputStream.close();
                    }
                }
                throw th4;
            }
        } catch (Exception e2) {
            throw new IOException("No se ha podido crear el directorio para la copia del plugin: " + LoggerUtil.getCleanUserHomePath(file3.getAbsolutePath()), e2);
        }
    }

    /* JADX WARN: Bottom block not found for handler: all -> 0x0085 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void addPluginToRelationsFile(es.gob.afirma.standalone.plugins.MinimalPluginInfo r8) throws java.io.IOException {
        /*
            r7 = this;
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r1 = r0
            r1.<init>()
            r1 = r8
            java.lang.String r1 = r1.getInternalName()
            java.lang.StringBuilder r0 = r0.append(r1)
            java.lang.String r1 = ":"
            java.lang.StringBuilder r0 = r0.append(r1)
            r1 = r8
            int r1 = r1.getVersionCode()
            java.lang.StringBuilder r0 = r0.append(r1)
            java.lang.String r1 = "\n"
            java.lang.StringBuilder r0 = r0.append(r1)
            java.lang.String r0 = r0.toString()
            java.nio.charset.Charset r1 = java.nio.charset.StandardCharsets.UTF_8
            byte[] r0 = r0.getBytes(r1)
            r9 = r0
            java.io.File r0 = new java.io.File
            r1 = r0
            r2 = r7
            java.io.File r2 = r2.pluginsDir
            java.lang.String r3 = "installed"
            r1.<init>(r2, r3)
            r10 = r0
            r0 = r10
            boolean r0 = r0.isFile()
            if (r0 != 0) goto Lb4
            java.io.FileOutputStream r0 = new java.io.FileOutputStream
            r1 = r0
            r2 = r10
            r1.<init>(r2)
            r11 = r0
            r0 = 0
            r12 = r0
            r0 = r11
            r1 = r9
            r0.write(r1)     // Catch: java.lang.Throwable -> L7c java.lang.Throwable -> L85
            r0 = r11
            r0.flush()     // Catch: java.lang.Throwable -> L7c java.lang.Throwable -> L85
            r0 = r11
            if (r0 == 0) goto Lad
            r0 = r12
            if (r0 == 0) goto L74
            r0 = r11
            r0.close()     // Catch: java.lang.Throwable -> L68
            goto Lad
        L68:
            r13 = move-exception
            r0 = r12
            r1 = r13
            r0.addSuppressed(r1)
            goto Lad
        L74:
            r0 = r11
            r0.close()
            goto Lad
        L7c:
            r13 = move-exception
            r0 = r13
            r12 = r0
            r0 = r13
            throw r0     // Catch: java.lang.Throwable -> L85
        L85:
            r14 = move-exception
            r0 = r11
            if (r0 == 0) goto Laa
            r0 = r12
            if (r0 == 0) goto La5
            r0 = r11
            r0.close()     // Catch: java.lang.Throwable -> L99
            goto Laa
        L99:
            r15 = move-exception
            r0 = r12
            r1 = r15
            r0.addSuppressed(r1)
            goto Laa
        La5:
            r0 = r11
            r0.close()
        Laa:
            r0 = r14
            throw r0
        Lad:
            r0 = r10
            es.gob.afirma.core.misc.AOFileUtils.setAllPermissions(r0)
            goto Lc7
        Lb4:
            r0 = r10
            java.nio.file.Path r0 = r0.toPath()
            r1 = r9
            r2 = 1
            java.nio.file.OpenOption[] r2 = new java.nio.file.OpenOption[r2]
            r3 = r2
            r4 = 0
            java.nio.file.StandardOpenOption r5 = java.nio.file.StandardOpenOption.APPEND
            r3[r4] = r5
            java.nio.file.Path r0 = java.nio.file.Files.write(r0, r1, r2)
        Lc7:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: es.gob.afirma.standalone.plugins.manager.PluginsManager.addPluginToRelationsFile(es.gob.afirma.standalone.plugins.MinimalPluginInfo):void");
    }

    public void uninstallPlugin(AfirmaPlugin afirmaPlugin) throws IOException {
        PluginInfo info = afirmaPlugin.getInfo();
        LOGGER.info("Se desinstala el plugin: " + info.getName());
        if (PermissionChecker.check(info, Permission.INSTALL)) {
            LOGGER.info("Se ejecuta la funcion de desinstalacion del plugin");
            try {
                afirmaPlugin.uninstall();
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "El proceso de desinstalacion interno del plugin devolvio un error", (Throwable) e);
            }
        }
        String internalName = info.getInternalName();
        this.pluginsLoadedList.remove(afirmaPlugin);
        ((URLClassLoader) afirmaPlugin.getClass().getClassLoader()).close();
        if (!this.pluginsDir.isDirectory()) {
            LOGGER.warning("No existe el directorio de plugins, asi que no deberia haber ninguno instalado");
            return;
        }
        File file = new File(this.pluginsDir, internalName);
        if (!file.exists()) {
            LOGGER.warning("El plugin seleccionado, no se encuentra instalado");
        } else {
            deleteDirectoryAndContent(file);
            savePluginsList(new File(this.pluginsDir, PLUGIN_RELATION_FILENAME), this.pluginsLoadedList);
        }
    }

    public void forceRemove(String str) throws IOException {
        File file = new File(this.pluginsDir, str);
        if (!file.exists()) {
            LOGGER.warning("El plugin seleccionado, no se encuentra instalado");
        } else {
            deleteDirectoryAndContent(file);
        }
    }

    private static void deleteDirectoryAndContent(File file) throws IOException {
        for (File file2 : file.listFiles()) {
            if (file2.isFile()) {
                Files.delete(file2.toPath());
            } else {
                deleteDirectoryAndContent(file2);
            }
        }
        Files.delete(file.toPath());
    }

    private static void createDirWithPermissions(File file) throws IOException {
        if (!file.mkdirs()) {
            throw new IOException("No se pudo crear el directorio: " + LoggerUtil.getCleanUserHomePath(file.getAbsolutePath()));
        }
        AOFileUtils.setAllPermissions(file);
    }
}
