package es.gob.afirma.keystores.jmulticard.ui;

import com.ctc.wstx.cfg.XmlConsts;
import java.awt.HeadlessException;
import java.io.Console;
import java.util.Locale;
import java.util.logging.Logger;
import org.apache.pdfbox.contentstream.operator.OperatorName;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/keystores/jmulticard/ui/DialogBuilder.class */
public final class DialogBuilder {
    private static boolean headless = false;

    static {
        setHeadLess(Boolean.getBoolean("java.awt.headless"));
    }

    public static void setHeadLess(boolean z) {
        headless = z;
    }

    private DialogBuilder() {
    }

    public static void showSignatureConfirmDialog(CustomAuthorizeCallback customAuthorizeCallback) {
        if (!headless) {
            try {
                customAuthorizeCallback.setAuthorized(ConfirmSmartcardDialog.showConfirmDialog(PasswordCallbackManager.getDialogOwner(), true, Messages.getString("CustomDialog.confirmDialog.prompt"), Messages.getString("CustomDialog.confirmDialog.title"), 0, "/images/dnie.png") == 0);
            } catch (HeadlessException e) {
                Logger.getLogger("es.gob.afirma").info("No hay entorno grafico, se revierte a consola: " + e);
                Console console = System.console();
                if (console == null) {
                    throw new NoConsoleException("No hay consola para solicitar el PIN");
                }
                customAuthorizeCallback.setAuthorized(getConsoleConfirm(console, customAuthorizeCallback) == 0);
            }
        }
    }

    private static int getConsoleConfirm(Console console, CustomAuthorizeCallback customAuthorizeCallback) {
        console.printf(Messages.getString("CustomDialog.confirmDialog.prompt"), new Object[0]);
        String lowerCase = console.readLine().replace("\n", "").replace("\r", "").trim().toLowerCase(Locale.getDefault());
        if ("si".equals(lowerCase) || OperatorName.CLOSE_AND_STROKE.equals(lowerCase) || "sí".equals(lowerCase) || OperatorName.CURVE_TO_REPLICATE_FINAL_POINT.equals(lowerCase) || XmlConsts.XML_SA_YES.equals(lowerCase)) {
            return 0;
        }
        if (XmlConsts.XML_SA_NO.equals(lowerCase) || OperatorName.ENDPATH.equals(lowerCase)) {
            return 1;
        }
        return getConsoleConfirm(console, customAuthorizeCallback);
    }
}
