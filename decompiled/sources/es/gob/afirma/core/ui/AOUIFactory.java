package es.gob.afirma.core.ui;

import es.gob.afirma.core.misc.Platform;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/core/ui/AOUIFactory.class */
public final class AOUIFactory {
    public static final int PLAIN_MESSAGE;
    public static final int YES_NO_OPTION;
    public static final int WARNING_MESSAGE;
    public static final int YES_OPTION;
    public static final int NO_OPTION;
    public static final int OK_CANCEL_OPTION;
    public static final int OK_OPTION;
    public static final int INFORMATION_MESSAGE;
    public static final int QUESTION_MESSAGE;
    public static final int ERROR_MESSAGE;
    private static AOUIManager uiManager;

    private AOUIFactory() {
    }

    static {
        String str;
        try {
            if (Platform.OS.ANDROID.equals(Platform.getOS())) {
                throw new UnsupportedOperationException("No se soporta GUI en Android");
            }
            if (Platform.OS.MACOSX.equals(Platform.getOS())) {
                str = "es.gob.afirma.ui.core.jse.AWTUIManager";
            } else {
                str = "es.gob.afirma.ui.core.jse.JSEUIManager";
            }
            try {
                uiManager = (AOUIManager) Class.forName(str).getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
            } catch (Exception e) {
                Logger.getLogger("es.gob.afirma").severe("No se ha podido instanciar la implementacion de gestor de interfaces graficas: " + e);
            }
            PLAIN_MESSAGE = uiManager.getPlainMessageCode();
            YES_NO_OPTION = uiManager.getYesNoOptionCode();
            WARNING_MESSAGE = uiManager.getWarningMessageCode();
            YES_OPTION = uiManager.getYesOptionCode();
            NO_OPTION = uiManager.getNoOptionCode();
            OK_CANCEL_OPTION = uiManager.getOkCancelOptionCode();
            OK_OPTION = uiManager.getOkOptionCode();
            INFORMATION_MESSAGE = uiManager.getInformationMessageCode();
            QUESTION_MESSAGE = uiManager.getQuestionMessageCode();
            ERROR_MESSAGE = uiManager.getErrorMessageCode();
        } catch (Exception e2) {
            throw new UnsupportedOperationException("No se ha podido instanciar el gestor de interfaces graficas: " + e2, e2);
        }
    }

    public static void setUIManager(AOUIManager aOUIManager) {
        if (aOUIManager != null) {
            uiManager = aOUIManager;
        }
    }

    public static char[] getPassword(String str, Object obj) {
        return uiManager.getPassword(str, obj);
    }

    public static char[] getPassword(String str, Object obj, String str2, boolean z, Object obj2) {
        return uiManager.getPassword(str, obj, str2, z, obj2);
    }

    public static char[] getDoublePassword(String str, String str2, Object obj, String str3, boolean z, Object obj2) {
        return uiManager.getDoublePassword(str, str2, obj, str3, z, obj2);
    }

    public static int showConfirmDialog(Object obj, Object obj2, String str, int i, int i2) {
        return uiManager.showConfirmDialog(obj, obj2, str, i, i2);
    }

    public static void showMessageDialog(Object obj, Object obj2, String str, int i) {
        uiManager.showMessageDialog(obj, obj2, str, i);
    }

    public static void showMessageDialog(Object obj, Object obj2, String str, int i, Object obj3) {
        uiManager.showMessageDialog(obj, obj2, str, i, obj3);
    }

    public static void showErrorMessage(Object obj, String str, int i, Throwable th) {
        uiManager.showErrorMessage(obj, str, i, th);
    }

    public static void showErrorMessage(Object obj, Object obj2, String str, int i, Throwable th) {
        uiManager.showErrorMessage(obj, obj2, str, i, th);
    }

    public static Object showInputDialog(Object obj, Object obj2, String str, int i, Object obj3, Object[] objArr, Object obj4) {
        return uiManager.showInputDialog(obj, obj2, str, i, obj3, objArr, obj4);
    }

    public static String showCertificateSelectionDialog(Object obj, KeyStoreDialogManager keyStoreDialogManager) {
        return uiManager.showCertificateSelectionDialog(obj, keyStoreDialogManager);
    }

    public static File[] getLoadFiles(String str, String str2, String str3, String[] strArr, String str4, boolean z, boolean z2, Object obj, Object obj2) {
        return uiManager.getLoadFiles(str, str2, str3, strArr, str4, z, z2, obj, obj2);
    }

    public static File getSaveDataToFile(byte[] bArr, String str, String str2, String str3, List<GenericFileFilter> list, Object obj) throws IOException {
        return uiManager.saveDataToFile(bArr, str, str2, str3, list, obj);
    }

    public static File getSaveDataToFile(byte[] bArr, String str, String str2, String str3, List<GenericFileFilter> list, GenericFileFilter genericFileFilter, Object obj) throws IOException {
        return uiManager.saveDataToFile(bArr, str, str2, str3, list, genericFileFilter, obj);
    }
}
