package es.gob.afirma.standalone.ui.preferences;

import es.gob.afirma.standalone.SimpleAfirmaMessages;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/preferences/SecureDomainsPanel.class */
final class SecureDomainsPanel extends JPanel {
    private static final long serialVersionUID = -6040435120676908406L;
    private static final int PREFERRED_WIDTH = 420;
    private static final int PREFERRED_HEIGHT = 210;
    static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private JTextArea secureDomainsListTA = new JTextArea(10, 30);
    private final SecureDomainsHandler eventsHandler = new SecureDomainsHandler(this);

    SecureDomainsPanel() {
        createUI();
        new Thread(() -> {
            this.eventsHandler.loadViewData();
        }).start();
    }

    private void createUI() {
        setLayout(new GridBagLayout());
        setMinimumSize(new Dimension(PREFERRED_WIDTH, 210));
        getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("SecureDomainsDialog.2"));
        Component jScrollPane = new JScrollPane(this.secureDomainsListTA);
        Component jLabel = new JLabel(SimpleAfirmaMessages.getString("SecureDomainsDialog.2"));
        jLabel.getAccessibleContext().setAccessibleDescription("SecureDomainsDialog.2");
        jLabel.setFocusable(true);
        Component jLabel2 = new JLabel(SimpleAfirmaMessages.getString("SecureDomainsDialog.3"));
        jLabel2.getAccessibleContext().setAccessibleDescription("SecureDomainsDialog.3");
        jLabel2.setFocusable(true);
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = 2;
        gridBagConstraints.weightx = 1.0d;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        add(jLabel, gridBagConstraints);
        gridBagConstraints.gridy++;
        gridBagConstraints.insets = new Insets(10, 0, 10, 0);
        add(jScrollPane, gridBagConstraints);
        gridBagConstraints.insets = new Insets(0, 0, 0, 0);
        gridBagConstraints.gridy++;
        add(jLabel2, gridBagConstraints);
    }

    void saveData() throws ConfigurationException {
        this.eventsHandler.saveViewData();
    }

    public JTextArea getSecureDomainsListTA() {
        return this.secureDomainsListTA;
    }

    public void setSecureDomainsListTA(JTextArea jTextArea) {
        this.secureDomainsListTA = jTextArea;
    }
}
