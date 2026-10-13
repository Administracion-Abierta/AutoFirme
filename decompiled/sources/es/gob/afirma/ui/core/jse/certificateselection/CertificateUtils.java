package es.gob.afirma.ui.core.jse.certificateselection;

import es.gob.afirma.core.AOCancelledOperationException;
import es.gob.afirma.core.misc.AOUtil;
import es.gob.afirma.core.misc.Platform;
import es.gob.afirma.core.ui.AOUIFactory;
import es.gob.afirma.core.ui.GenericFileFilter;
import es.gob.afirma.ui.core.jse.JSEUIManager;
import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.Date;
import java.util.logging.Logger;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.apache.pdfbox.pdmodel.interactive.action.PDWindowsLaunchParams;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/ui/core/jse/certificateselection/CertificateUtils.class */
public final class CertificateUtils {
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private static final String CERTIFICATE_DEFAULT_EXTENSION = ".cer";

    private CertificateUtils() {
    }

    public static void openCert(Component component, X509Certificate x509Certificate) {
        Class<?> cls;
        if (Platform.getOS() == Platform.OS.MACOSX) {
            try {
                File fileSaveTemp = saveTemp(x509Certificate.getEncoded(), CERTIFICATE_DEFAULT_EXTENSION);
                fileSaveTemp.deleteOnExit();
                runMacOsScript("qlmanage -p " + fileSaveTemp.getAbsolutePath().replace(" ", "\\ "));
                return;
            } catch (Exception e) {
                LOGGER.warning("No ha sido posible abrir el certificado mediante QuickLook: " + e);
            }
        } else {
            try {
                cls = Class.forName("java.awt.Desktop");
            } catch (ClassNotFoundException e2) {
                cls = null;
            }
            if (cls != null) {
                LOGGER.info("Se visualizan los datos del certificado con numero de serie: " + AOUtil.hexify(x509Certificate.getSerialNumber().toByteArray(), false));
                try {
                    cls.getDeclaredMethod(PDWindowsLaunchParams.OPERATION_OPEN, File.class).invoke(cls.getDeclaredMethod("getDesktop", (Class[]) null).invoke(null, (Object[]) null), saveTemp(x509Certificate.getEncoded(), CERTIFICATE_DEFAULT_EXTENSION));
                    return;
                } catch (Exception e3) {
                    LOGGER.warning("No ha sido posible abrir el certificado: " + e3);
                }
            }
            if (Platform.getOS() == Platform.OS.WINDOWS) {
                LOGGER.info("Se visualizan mediante una llamada al sistema los datos del certificado con numero de serie: " + AOUtil.hexify(x509Certificate.getSerialNumber().toByteArray(), false));
                try {
                    new ProcessBuilder("cmd", "/C", "start", OperatorName.SHOW_TEXT_LINE_AND_SPACE + CertificateSelectionDialogMessages.getString("CertificateUtils.0") + OperatorName.SHOW_TEXT_LINE_AND_SPACE, OperatorName.SHOW_TEXT_LINE_AND_SPACE + saveTemp(x509Certificate.getEncoded(), CERTIFICATE_DEFAULT_EXTENSION).getAbsolutePath() + OperatorName.SHOW_TEXT_LINE_AND_SPACE).start();
                    return;
                } catch (Exception e4) {
                    LOGGER.warning("No ha sido posible abrir el certificado mediante llamada por consola: " + e4);
                }
            }
        }
        try {
            LOGGER.info("Se guarda en disco el certificado con numero de serie: " + AOUtil.hexify(x509Certificate.getSerialNumber().toByteArray(), false));
            AOUIFactory.getSaveDataToFile(x509Certificate.getEncoded(), CertificateSelectionDialogMessages.getString("CertificateUtils.1"), null, CertificateSelectionDialogMessages.getString("CertificateUtils.5") + CERTIFICATE_DEFAULT_EXTENSION, Collections.singletonList(new GenericFileFilter(new String[]{CERTIFICATE_DEFAULT_EXTENSION}, CertificateSelectionDialogMessages.getString("CertificateUtils.3"))), component);
        } catch (AOCancelledOperationException e5) {
        } catch (IOException e6) {
            new JSEUIManager().showConfirmDialog(component, CertificateSelectionDialogMessages.getString("CertificateUtils.2"), CertificateSelectionDialogMessages.getString("CertificateUtils.3"), -1, 0);
        } catch (CertificateEncodingException e7) {
            new JSEUIManager().showConfirmDialog(component, CertificateSelectionDialogMessages.getString("CertificateUtils.4"), CertificateSelectionDialogMessages.getString("CertificateUtils.3"), -1, 0);
        }
    }

    /* JADX WARN: Bottom block not found for handler: all -> 0x0039 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private static boolean saveFile(java.io.File r4, byte[] r5) throws java.io.IOException {
        /*
            java.io.FileOutputStream r0 = new java.io.FileOutputStream
            r1 = r0
            r2 = r4
            r1.<init>(r2)
            r6 = r0
            r0 = 0
            r7 = r0
            r0 = r6
            r1 = r5
            r0.write(r1)     // Catch: java.lang.Throwable -> L31 java.lang.Throwable -> L39
            r0 = r6
            if (r0 == 0) goto L5c
            r0 = r7
            if (r0 == 0) goto L2a
            r0 = r6
            r0.close()     // Catch: java.lang.Throwable -> L1f
            goto L5c
        L1f:
            r8 = move-exception
            r0 = r7
            r1 = r8
            r0.addSuppressed(r1)
            goto L5c
        L2a:
            r0 = r6
            r0.close()
            goto L5c
        L31:
            r8 = move-exception
            r0 = r8
            r7 = r0
            r0 = r8
            throw r0     // Catch: java.lang.Throwable -> L39
        L39:
            r9 = move-exception
            r0 = r6
            if (r0 == 0) goto L59
            r0 = r7
            if (r0 == 0) goto L55
            r0 = r6
            r0.close()     // Catch: java.lang.Throwable -> L4a
            goto L59
        L4a:
            r10 = move-exception
            r0 = r7
            r1 = r10
            r0.addSuppressed(r1)
            goto L59
        L55:
            r0 = r6
            r0.close()
        L59:
            r0 = r9
            throw r0
        L5c:
            r0 = 1
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: es.gob.afirma.ui.core.jse.certificateselection.CertificateUtils.saveFile(java.io.File, byte[]):boolean");
    }

    private static File saveTemp(byte[] bArr, String str) throws IOException {
        File fileCreateTempFile = File.createTempFile("afirma", str);
        fileCreateTempFile.deleteOnExit();
        if (saveFile(fileCreateTempFile, bArr)) {
            return fileCreateTempFile;
        }
        return null;
    }

    static boolean isExpired(X509Certificate x509Certificate) {
        long time = new Date().getTime();
        return time >= x509Certificate.getNotAfter().getTime() || time <= x509Certificate.getNotBefore().getTime();
    }

    /* JADX WARN: Bottom block not found for handler: all -> 0x0164 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private static java.lang.String runMacOsScript(java.lang.String r6) throws java.lang.InterruptedException, java.io.IOException {
        /*
            Method dump skipped, instruction units count: 417
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: es.gob.afirma.ui.core.jse.certificateselection.CertificateUtils.runMacOsScript(java.lang.String):java.lang.String");
    }
}
