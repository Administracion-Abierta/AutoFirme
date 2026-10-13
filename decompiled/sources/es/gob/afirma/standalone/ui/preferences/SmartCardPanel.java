package es.gob.afirma.standalone.ui.preferences;

import es.gob.afirma.core.misc.Platform;
import es.gob.afirma.core.ui.AOUIFactory;
import es.gob.afirma.standalone.SimpleAfirmaMessages;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/preferences/SmartCardPanel.class */
final class SmartCardPanel extends JPanel {
    private static final long serialVersionUID = -6040435120676908406L;
    static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    final JLabel cardnameLbl = new JLabel(SimpleAfirmaMessages.getString("SmartCardDialog.1"));
    final JLabel controllerPathLbl = new JLabel(SimpleAfirmaMessages.getString("SmartCardDialog.2"));
    final JTextField cardNameTxt = new JTextField();
    final JTextField controllerPathTxt = new JTextField();
    private SecureDomainsHandler eventsHandler;

    SmartCardPanel() {
        createUI();
    }

    private void createUI() {
        setLayout(new GridBagLayout());
        this.cardnameLbl.setLabelFor(this.cardNameTxt);
        this.cardNameTxt.setFocusable(true);
        this.controllerPathLbl.setLabelFor(this.controllerPathTxt);
        this.controllerPathTxt.setEditable(false);
        this.controllerPathTxt.setColumns(52);
        Component jButton = new JButton(SimpleAfirmaMessages.getString("SmartCardDialog.3"));
        jButton.setFocusable(true);
        jButton.addActionListener(new ActionListener() { // from class: es.gob.afirma.standalone.ui.preferences.SmartCardPanel.1
            public void actionPerformed(ActionEvent actionEvent) {
                String[] strArr;
                String str;
                String string = SimpleAfirmaMessages.getString("SmartCardDialog.8");
                if (Platform.OS.WINDOWS.equals(Platform.getOS())) {
                    strArr = new String[]{"dll"};
                    str = string + " (*.dll)";
                } else if (Platform.OS.MACOSX.equals(Platform.getOS())) {
                    strArr = new String[]{"so", "dylib"};
                    str = string + " (*.dylib, *.so)";
                } else {
                    strArr = new String[]{"so"};
                    str = string + " (*.so)";
                }
                SmartCardPanel.this.controllerPathTxt.setText(AOUIFactory.getLoadFiles(SimpleAfirmaMessages.getString("SmartCardDialog.7"), null, null, strArr, str, false, false, null, this)[0].getAbsolutePath());
            }
        });
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.weightx = 3.0d;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        add(this.cardnameLbl, gridBagConstraints);
        gridBagConstraints.gridy++;
        add(this.cardNameTxt, gridBagConstraints);
        gridBagConstraints.gridy++;
        add(this.controllerPathLbl, gridBagConstraints);
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.gridy++;
        gridBagConstraints.weightx = 1.0d;
        add(this.controllerPathTxt, gridBagConstraints);
        gridBagConstraints.weightx = 0.0d;
        gridBagConstraints.gridx = 2;
        add(jButton, gridBagConstraints);
        gridBagConstraints.gridy++;
        gridBagConstraints.weighty = 1.0d;
        add(new JPanel(), gridBagConstraints);
    }

    void saveData() throws ConfigurationException {
        this.eventsHandler.saveViewData();
    }

    public JTextField getCardNameTxt() {
        return this.cardNameTxt;
    }

    public JTextField getControllerPathTxt() {
        return this.controllerPathTxt;
    }
}
