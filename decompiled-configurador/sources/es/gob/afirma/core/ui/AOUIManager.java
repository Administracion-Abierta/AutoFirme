package es.gob.afirma.core.ui;

import java.io.File;
import java.io.IOException;
import java.util.List;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/core/ui/AOUIManager.class */
public interface AOUIManager {
    char[] getPassword(String str, Object obj);

    char[] getPassword(String str, Object obj, String str2, boolean z, Object obj2);

    char[] getDoublePassword(String str, String str2, Object obj, String str3, boolean z, Object obj2);

    Object showInputDialog(Object obj, Object obj2, String str, int i, Object obj3, Object[] objArr, Object obj4);

    String showCertificateSelectionDialog(Object obj, KeyStoreDialogManager keyStoreDialogManager);

    int showConfirmDialog(Object obj, Object obj2, String str, int i, int i2);

    void showMessageDialog(Object obj, Object obj2, String str, int i);

    void showMessageDialog(Object obj, Object obj2, String str, int i, Object obj3);

    void showErrorMessage(Object obj, String str, int i, Throwable th);

    void showErrorMessage(Object obj, Object obj2, String str, int i, Throwable th);

    int getPlainMessageCode();

    int getYesNoOptionCode();

    int getWarningMessageCode();

    int getErrorMessageCode();

    int getYesOptionCode();

    int getNoOptionCode();

    int getOkCancelOptionCode();

    int getOkOptionCode();

    int getInformationMessageCode();

    int getQuestionMessageCode();

    File[] getLoadFiles(String str, String str2, String str3, String[] strArr, String str4, boolean z, boolean z2, Object obj, Object obj2);

    File saveDataToFile(byte[] bArr, String str, String str2, String str3, List<GenericFileFilter> list, Object obj) throws IOException;

    File saveDataToFile(byte[] bArr, String str, String str2, String str3, List<GenericFileFilter> list, GenericFileFilter genericFileFilter, Object obj) throws IOException;
}
