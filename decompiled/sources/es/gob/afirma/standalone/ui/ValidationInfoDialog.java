package es.gob.afirma.standalone.ui;

import es.gob.afirma.core.signers.AOSigner;
import es.gob.afirma.core.signers.AOSignerFactory;
import es.gob.afirma.core.ui.AOUIFactory;
import es.gob.afirma.signers.xades.AOXAdESSigner;
import es.gob.afirma.signvalidation.SignValidity;
import es.gob.afirma.standalone.DataAnalizerUtil;
import es.gob.afirma.standalone.DesktopUtil;
import es.gob.afirma.standalone.SimpleAfirmaMessages;
import es.gob.afirma.standalone.signdetails.CAdESSignAnalyzer;
import es.gob.afirma.standalone.signdetails.FacturaESignAnalyzer;
import es.gob.afirma.standalone.signdetails.PAdESSignAnalyzer;
import es.gob.afirma.standalone.signdetails.SignAnalyzer;
import es.gob.afirma.standalone.signdetails.SignDetailsFormatter;
import es.gob.afirma.standalone.signdetails.XAdESSignAnalyzer;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.text.DefaultCaret;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/ValidationInfoDialog.class */
public class ValidationInfoDialog extends JDialog {
    static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private static final long serialVersionUID = -6677845022747809410L;
    private static final int DEFAULT_WINDOW_WIDTH = 780;
    private static final int DEFAULT_WINDOW_HEIGHT = 550;
    private JScrollPane scrollPane;
    private final JEditorPane validationInfo;
    private final List<SignValidity> generalValidation;

    public ValidationInfoDialog(JFrame jFrame, byte[] bArr, List<SignValidity> list) {
        super(jFrame, true);
        this.validationInfo = new JEditorPane();
        this.generalValidation = list;
        createUI(bArr);
    }

    void createUI(byte[] bArr) {
        setTitle(SimpleAfirmaMessages.getString("ValidationInfoDialog.0"));
        setIconImages(DesktopUtil.getIconImages());
        setResizable(true);
        setPreferredSize(new Dimension(780, 550));
        setDefaultCloseOperation(2);
        setLayout(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = 1;
        gridBagConstraints.weightx = 1.0d;
        gridBagConstraints.weighty = 0.0d;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new Insets(11, 11, 0, 11);
        add(new JLabel(SimpleAfirmaMessages.getString("ValidationInfoDialog.1")), gridBagConstraints);
        gridBagConstraints.insets = new Insets(5, 11, 0, 11);
        gridBagConstraints.gridy++;
        gridBagConstraints.weighty = 2.0d;
        SignAnalyzer signAnalyzer = null;
        try {
            signAnalyzer = getSignAnalyzer(bArr);
        } catch (Exception e) {
            AOUIFactory.showErrorMessage(SimpleAfirmaMessages.getString("ValidationInfoDialog.17"), SimpleAfirmaMessages.getString("SimpleAfirma.7"), 0, null);
        }
        String toHTML = SignDetailsFormatter.formatToHTML(signAnalyzer, this.generalValidation);
        this.validationInfo.setContentType("text/html");
        this.validationInfo.setText(toHTML);
        this.validationInfo.setEditable(false);
        DefaultCaret defaultCaret = new DefaultCaret();
        defaultCaret.setBlinkRate(500);
        defaultCaret.setUpdatePolicy(1);
        this.validationInfo.setCaret(defaultCaret);
        this.scrollPane = new JScrollPane(this.validationInfo);
        this.scrollPane.getVerticalScrollBar().setValue(this.scrollPane.getVerticalScrollBar().getMinimum());
        Component jPanel = new JPanel();
        jPanel.setLayout(new GridBagLayout());
        GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
        gridBagConstraints2.fill = 1;
        gridBagConstraints2.weightx = 1.0d;
        gridBagConstraints2.weighty = 2.0d;
        gridBagConstraints2.gridx = 0;
        gridBagConstraints2.gridy = 0;
        jPanel.add(this.scrollPane, gridBagConstraints2);
        add(jPanel, gridBagConstraints);
        Component jButton = new JButton(SimpleAfirmaMessages.getString("TrustedCertificatesDialog.28"));
        jButton.addActionListener(actionEvent -> {
            dispose();
        });
        gridBagConstraints.weighty = 0.0d;
        gridBagConstraints.fill = 0;
        gridBagConstraints.anchor = 14;
        gridBagConstraints.insets = new Insets(11, 11, 11, 11);
        gridBagConstraints.gridy++;
        add(jButton, gridBagConstraints);
        pack();
        setLocationRelativeTo(SwingUtilities.getWindowAncestor(this));
    }

    private static SignAnalyzer getSignAnalyzer(byte[] bArr) throws Exception {
        if (DataAnalizerUtil.isPDF(bArr)) {
            return new PAdESSignAnalyzer(bArr);
        }
        if (DataAnalizerUtil.isFacturae(bArr)) {
            return new FacturaESignAnalyzer(bArr);
        }
        try {
            AOSigner signer = AOSignerFactory.getSigner(bArr);
            if (signer == null) {
                return null;
            }
            if (signer instanceof AOXAdESSigner) {
                return new XAdESSignAnalyzer(bArr);
            }
            return new CAdESSignAnalyzer(bArr);
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Error al leer la informacion de la firma", (Throwable) e);
            throw e;
        }
    }
}
