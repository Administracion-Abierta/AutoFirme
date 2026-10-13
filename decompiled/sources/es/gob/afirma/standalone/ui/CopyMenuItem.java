package es.gob.afirma.standalone.ui;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/CopyMenuItem.class */
final class CopyMenuItem extends JPopupMenu implements ClipboardOwner {
    private static final long serialVersionUID = 1750985678317829383L;
    private final JTextComponent textComponent;

    JTextComponent getTextComponent() {
        return this.textComponent;
    }

    private void createUI(String str) {
        JMenuItem jMenuItem = new JMenuItem(str);
        jMenuItem.addActionListener(actionEvent -> {
            if (getTextComponent() != null) {
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(getTextComponent().getText()), this);
            }
        });
        add(jMenuItem);
    }

    CopyMenuItem(JTextComponent jTextComponent, String str) {
        this.textComponent = jTextComponent;
        createUI(str);
    }

    CopyMenuItem(JLabel jLabel, String str) {
        this.textComponent = new JTextField(jLabel.getText());
        createUI(str);
    }

    public void lostOwnership(Clipboard clipboard, Transferable transferable) {
    }
}
