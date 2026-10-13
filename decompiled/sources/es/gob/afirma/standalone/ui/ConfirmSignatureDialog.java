package es.gob.afirma.standalone.ui;

import es.gob.afirma.standalone.DesktopUtil;
import es.gob.afirma.standalone.SimpleAfirmaMessages;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.mozilla.javascript.ES6Iterator;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/ConfirmSignatureDialog.class */
public class ConfirmSignatureDialog {
    private final JDialog dialog;
    private JCheckBox confirmCb;
    private Boolean result = null;
    private final JOptionPane optionPane = new JOptionPane();

    public ConfirmSignatureDialog(Component component, int i) {
        this.optionPane.setMessageType(-1);
        this.optionPane.setMessage(createPanel(i));
        this.optionPane.setOptions(new String[]{SimpleAfirmaMessages.getString("ConfirmSignatureDialog.6"), SimpleAfirmaMessages.getString("ConfirmSignatureDialog.7")});
        this.optionPane.setOptionType(2);
        this.dialog = this.optionPane.createDialog(getParentComponent(component), SimpleAfirmaMessages.getString("ConfirmSignatureDialog.0"));
        this.dialog.setIconImages(DesktopUtil.getIconImages());
        this.optionPane.addPropertyChangeListener(propertyChangeEvent -> {
            boolean z = propertyChangeEvent.getSource() == this.optionPane;
            boolean zEquals = propertyChangeEvent.getPropertyName().equals(ES6Iterator.VALUE_PROPERTY);
            if (z && zEquals && SimpleAfirmaMessages.getString("ConfirmSignatureDialog.6").equals(propertyChangeEvent.getNewValue())) {
                this.result = Boolean.valueOf(this.confirmCb.isSelected());
            }
        });
    }

    private Component createPanel(int i) {
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BoxLayout(jPanel, 1));
        if (i == 1) {
            jPanel.add(new JLabel(SimpleAfirmaMessages.getString("ConfirmSignatureDialog.1")));
        } else {
            jPanel.add(new JLabel(SimpleAfirmaMessages.getString("ConfirmSignatureDialog.2", Integer.toString(i))));
        }
        jPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        jPanel.add(new JLabel(SimpleAfirmaMessages.getString("ConfirmSignatureDialog.4")));
        jPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        this.confirmCb = new JCheckBox(SimpleAfirmaMessages.getString("ConfirmSignatureDialog.5"));
        jPanel.add(this.confirmCb);
        return jPanel;
    }

    private static Component getParentComponent(Component component) {
        Component component2;
        Component parent = component;
        while (true) {
            component2 = parent;
            if (component2 == null || component2.getParent() == null || component2 == component2.getParent()) {
                break;
            }
            parent = component2.getParent();
        }
        return component2;
    }

    public void setVisible(boolean z) {
        this.dialog.setVisible(z);
    }

    public Boolean getResult() {
        return this.result;
    }
}
