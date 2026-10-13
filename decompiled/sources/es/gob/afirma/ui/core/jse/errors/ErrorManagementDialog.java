package es.gob.afirma.ui.core.jse.errors;

import java.awt.Frame;
import javax.accessibility.Accessible;
import javax.swing.JDialog;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/ui/core/jse/errors/ErrorManagementDialog.class */
public final class ErrorManagementDialog extends JDialog {
    private static final long serialVersionUID = -5699987186099323728L;

    private ErrorManagementDialog(Frame frame, boolean z, Object obj, String str, int i, Throwable th) {
        super(frame, z);
        setTitle(str);
        add(new ErrorManagementPanel(this, th, obj, i));
        if (obj instanceof Accessible) {
            getAccessibleContext().setAccessibleDescription(((Accessible) obj).getAccessibleContext().getAccessibleDescription());
        } else if (obj instanceof String) {
            getAccessibleContext().setAccessibleDescription((String) obj);
        }
        setResizable(false);
        setDefaultCloseOperation(2);
        setEnabled(true);
        setAlwaysOnTop(true);
        resize();
    }

    private ErrorManagementDialog(JDialog jDialog, boolean z, Object obj, String str, int i, Throwable th) {
        super(jDialog, z);
        setTitle(str);
        add(new ErrorManagementPanel(this, th, obj, i));
        if (obj instanceof Accessible) {
            getAccessibleContext().setAccessibleDescription(((Accessible) obj).getAccessibleContext().getAccessibleDescription());
        } else if (obj instanceof String) {
            getAccessibleContext().setAccessibleDescription((String) obj);
        }
        setResizable(false);
        setDefaultCloseOperation(2);
        setEnabled(true);
        setAlwaysOnTop(true);
        resize();
    }

    public static void show(Frame frame, boolean z, Object obj, String str, int i, Throwable th) {
        new ErrorManagementDialog(frame, z, obj, str, i, th).setVisible(true);
    }

    public static void show(JDialog jDialog, boolean z, Object obj, String str, int i, Throwable th) {
        new ErrorManagementDialog(jDialog, z, obj, str, i, th).setVisible(true);
    }

    public void resize() {
        pack();
        setLocationRelativeTo(getParent());
    }
}
