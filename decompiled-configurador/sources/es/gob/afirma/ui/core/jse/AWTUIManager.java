package es.gob.afirma.ui.core.jse;

import es.gob.afirma.core.AOCancelledOperationException;
import es.gob.afirma.core.misc.Platform;
import es.gob.afirma.core.ui.GenericFileFilter;
import es.gob.afirma.core.ui.KeyStoreDialogManager;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/ui/core/jse/AWTUIManager.class */
public final class AWTUIManager extends JSEUIManager {
    @Override // es.gob.afirma.ui.core.jse.JSEUIManager, es.gob.afirma.core.ui.AOUIManager
    public File saveDataToFile(byte[] bArr, String str, String str2, String str3, List<GenericFileFilter> list, Object obj) throws IOException {
        return saveDataToFile(bArr, str, str2, str3, list, null, obj);
    }

    @Override // es.gob.afirma.ui.core.jse.JSEUIManager, es.gob.afirma.core.ui.AOUIManager
    public File saveDataToFile(byte[] bArr, String str, String str2, String str3, List<GenericFileFilter> list, GenericFileFilter genericFileFilter, Object obj) throws IOException {
        FileDialog fileDialog;
        if (obj instanceof Frame) {
            fileDialog = new FileDialog((Frame) obj, str, 1);
        } else if (obj instanceof Dialog) {
            fileDialog = new FileDialog((Dialog) obj, str, 1);
        } else {
            fileDialog = new FileDialog((Frame) null, str, 1);
        }
        if (str3 != null && str2 != null && Platform.OS.WINDOWS.equals(Platform.getOS())) {
            fileDialog.setFile(new File(str2, str3).getAbsolutePath());
        } else {
            if (str3 != null) {
                fileDialog.setFile(str3);
            }
            if (str2 == null) {
                fileDialog.setDirectory(get(JSEUIManager.PREFERENCE_DIRECTORY, str2));
            }
        }
        ArrayList arrayList = new ArrayList();
        if (list != null) {
            Iterator<GenericFileFilter> it = list.iterator();
            while (it.hasNext()) {
                String[] extensions = it.next().getExtensions();
                if (extensions != null) {
                    arrayList.addAll(Arrays.asList(extensions));
                }
            }
        }
        final String[] strArr = (String[]) arrayList.toArray(new String[arrayList.size()]);
        if (strArr != null) {
            fileDialog.setFilenameFilter(new FilenameFilter() { // from class: es.gob.afirma.ui.core.jse.AWTUIManager.1
                @Override // java.io.FilenameFilter
                public boolean accept(File file, String str4) {
                    for (String str5 : strArr) {
                        if (str4.endsWith(str5)) {
                            return true;
                        }
                    }
                    return false;
                }
            });
        }
        fileDialog.setVisible(true);
        if (fileDialog.getFile() == null) {
            throw new AOCancelledOperationException();
        }
        File file = new File(fileDialog.getDirectory() + fileDialog.getFile());
        if (bArr != null) {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            Throwable th = null;
            try {
                fileOutputStream.write(bArr);
                fileOutputStream.flush();
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
        return file;
    }

    @Override // es.gob.afirma.ui.core.jse.JSEUIManager, es.gob.afirma.core.ui.AOUIManager
    public File[] getLoadFiles(String str, String str2, String str3, final String[] strArr, String str4, boolean z, boolean z2, Object obj, Object obj2) {
        File[] files;
        FileDialog fileDialog = new FileDialog(obj2 instanceof Frame ? (Frame) obj2 : null, str);
        fileDialog.setMode(0);
        if (str2 == null) {
            fileDialog.setDirectory(get(JSEUIManager.PREFERENCE_DIRECTORY, str2));
        }
        if (z2) {
            fileDialog.setMultipleMode(z2);
        }
        if (str3 != null) {
            fileDialog.setFile(str3);
        }
        if (strArr != null && strArr.length > 0) {
            if (Platform.OS.WINDOWS.equals(Platform.getOS())) {
                String str5 = strArr[0];
                if (!str5.startsWith("*.")) {
                    if (str5.startsWith(".")) {
                        str5 = "*" + str5;
                    } else {
                        str5 = "*." + str5;
                    }
                }
                fileDialog.setFile(str5);
            } else {
                fileDialog.setFilenameFilter(new FilenameFilter() { // from class: es.gob.afirma.ui.core.jse.AWTUIManager.2
                    @Override // java.io.FilenameFilter
                    public boolean accept(File file, String str6) {
                        for (String str7 : strArr) {
                            if (str6.endsWith(str7)) {
                                return true;
                            }
                        }
                        return false;
                    }
                });
            }
        }
        if (z) {
            fileDialog.setFile("*.");
            System.setProperty("apple.awt.fileDialogForDirectories", "true");
        }
        fileDialog.setVisible(true);
        if (fileDialog.getFile() == null) {
            throw new AOCancelledOperationException();
        }
        if (z2) {
            files = fileDialog.getFiles();
            if (files == null) {
                files = new File[0];
            }
        } else {
            String file = fileDialog.getFile();
            files = file != null ? new File[]{new File(fileDialog.getDirectory(), file)} : new File[0];
        }
        System.setProperty("apple.awt.fileDialogForDirectories", "false");
        put(JSEUIManager.PREFERENCE_DIRECTORY, fileDialog.getDirectory());
        return files;
    }

    @Override // es.gob.afirma.ui.core.jse.JSEUIManager, es.gob.afirma.core.ui.AOUIManager
    public String showCertificateSelectionDialog(Object obj, KeyStoreDialogManager keyStoreDialogManager) {
        Component component = obj instanceof Component ? (Component) obj : null;
        try {
            Class<?> cls = Class.forName("es.gob.afirma.ui.core.jse.certificateselection.CertificateSelectionDialog");
            Object objInvoke = cls.getMethod("showDialog", Boolean.TYPE).invoke(cls.getConstructor(Component.class, KeyStoreDialogManager.class).newInstance(component, keyStoreDialogManager), Boolean.FALSE);
            focusApplication();
            if (objInvoke instanceof String) {
                return (String) objInvoke;
            }
            return null;
        } catch (InvocationTargetException e) {
            LOGGER.severe("Se genero un error en el dialogo de seleccion de certificados: " + e);
            if (e.getCause() instanceof RuntimeException) {
                throw ((RuntimeException) e.getCause());
            }
            throw new IllegalStateException("No se encontraron certificados en el almacen o se produjo un error durante la extraccion del certificado seleccionado: " + e, e.getCause());
        } catch (Exception e2) {
            LOGGER.severe("No se encuentra disponible el proyecto del interfaz grafico del dialogo de seleccion: " + e2);
            throw new IllegalStateException("No se encuentra disponible el proyecto del interfaz grafico del dialogo de seleccion u ocurrio un error durante su ejecucion: " + e2, e2);
        }
    }

    private static void focusApplication() {
        if (Platform.OS.MACOSX.equals(Platform.getOS())) {
            try {
                runAppleScript("tell me to activate");
            } catch (Exception e) {
                LOGGER.warning("Fallo cogiendo el foco en macOS: " + e);
            }
        }
    }

    /* JADX WARN: Bottom block not found for handler: all -> 0x0093 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private static java.lang.String runAppleScript(java.lang.String r6) throws java.lang.InterruptedException, java.io.IOException {
        /*
            Method dump skipped, instruction units count: 382
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: es.gob.afirma.ui.core.jse.AWTUIManager.runAppleScript(java.lang.String):java.lang.String");
    }
}
