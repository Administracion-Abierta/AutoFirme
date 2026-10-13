package es.gob.afirma.standalone.ui;

import es.gob.afirma.core.misc.Platform;
import es.gob.afirma.signvalidation.SignValidity;
import es.gob.afirma.standalone.LookAndFeelManager;
import es.gob.afirma.standalone.SimpleAfirmaMessages;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyListener;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/SignResultPanel.class */
final class SignResultPanel extends JPanel {
    private static final long serialVersionUID = -7982793036430571363L;
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private final JLabel descTextLabel = new JLabel();
    private final JLabel resultTextLabel = new JLabel();
    private final JLabel linkLabel = new JLabel();
    private final JLabel validationLinkLabel = new JLabel();
    private String accessibleDescription = "";

    SignResultPanel(List<SignValidity> list, boolean z, KeyListener keyListener) {
        SwingUtilities.invokeLater(() -> {
            createUI(list, z, keyListener, null);
        });
    }

    SignResultPanel(List<SignValidity> list, boolean z, KeyListener keyListener, byte[] bArr) {
        SwingUtilities.invokeLater(() -> {
            createUI(list, z, keyListener, bArr);
        });
    }

    void createUI(List<SignValidity> list, boolean z, KeyListener keyListener, byte[] bArr) {
        String str;
        String string;
        String string2;
        String string3;
        switch (list.get(0).getValidity()) {
            case KO:
                str = "ko_icon_large.png";
                break;
            case OK:
            case GENERATED:
                str = "ok_icon_large.png";
                break;
            default:
                str = "unknown_icon_large.png";
                break;
        }
        Component scalablePane = new ScalablePane(ImageLoader.loadImage(str), true);
        scalablePane.setBackground(LookAndFeelManager.TRANSPARENT_COLOR);
        scalablePane.setFocusable(false);
        scalablePane.setMinimumSize(new Dimension(120, 120));
        switch (list.get(0).getValidity()) {
            case KO:
                if (z) {
                    this.resultTextLabel.setText(SimpleAfirmaMessages.getString("SignResultPanel.5"));
                    if (list.get(0).getError() != null) {
                        switch (list.get(0).getError()) {
                            case CORRUPTED_SIGN:
                                string3 = SimpleAfirmaMessages.getString("SignResultPanel.14");
                                break;
                            case CERTIFICATE_EXPIRED:
                                string3 = SimpleAfirmaMessages.getString("SignResultPanel.16");
                                break;
                            case CERTIFICATE_NOT_VALID_YET:
                                string3 = SimpleAfirmaMessages.getString("SignResultPanel.17");
                                break;
                            case CERTIFICATE_PROBLEM:
                                string3 = SimpleAfirmaMessages.getString("SignResultPanel.18");
                                break;
                            case NO_MATCH_DATA:
                                string3 = SimpleAfirmaMessages.getString("SignResultPanel.19");
                                break;
                            case CRL_PROBLEM:
                                string3 = SimpleAfirmaMessages.getString("SignResultPanel.20");
                                break;
                            case ALGORITHM_NOT_SUPPORTED:
                                string3 = SimpleAfirmaMessages.getString("SignResultPanel.22");
                                break;
                            case MODIFIED_FORM:
                                string3 = SimpleAfirmaMessages.getString("SignResultPanel.35");
                                break;
                            case MODIFIED_DOCUMENT:
                                string3 = SimpleAfirmaMessages.getString("SignResultPanel.36");
                                break;
                            case OVERLAPPING_SIGNATURE:
                                string3 = SimpleAfirmaMessages.getString("SignResultPanel.37");
                                break;
                            default:
                                string3 = SimpleAfirmaMessages.getString("SignResultPanel.6");
                                LOGGER.warning("No se ha identificado el motivo por el que la firma no es valida: " + list.get(0).getError());
                                break;
                        }
                    } else {
                        string3 = SimpleAfirmaMessages.getString("SignResultPanel.6");
                    }
                    this.descTextLabel.setText("<html><p>" + string3 + "</p></html>");
                    string2 = SimpleAfirmaMessages.getString("SignResultPanel.6");
                    this.accessibleDescription += SimpleAfirmaMessages.getString("SignResultPanel.5") + string3;
                    new LabelLinkManager(this.validationLinkLabel).setLabelLinkListener(new ValidationErrorsLabelLinkImpl(bArr, list));
                    this.validationLinkLabel.setText(SimpleAfirmaMessages.getString("SignResultPanel.38"));
                    this.validationLinkLabel.getAccessibleContext().setAccessibleName(SimpleAfirmaMessages.getString("SignDataPanel.46") + SimpleAfirmaMessages.getString("SignResultPanel.38"));
                } else {
                    this.resultTextLabel.setText(SimpleAfirmaMessages.getString("SignResultPanel.30"));
                    String string4 = SimpleAfirmaMessages.getString("SignResultPanel.31");
                    this.descTextLabel.setText("<html><p>" + string4 + "</p></html>");
                    this.linkLabel.setText(SimpleAfirmaMessages.getString("SignResultPanel.33"));
                    new LabelLinkManager(this.linkLabel).setLabelLinkListener(new URLLabelLinkImpl(SimpleAfirmaMessages.getString("SignResultPanel.33")));
                    if (keyListener != null) {
                        this.linkLabel.addKeyListener(keyListener);
                    }
                    this.linkLabel.getAccessibleContext().setAccessibleName(SimpleAfirmaMessages.getString("SignDataPanel.46") + SimpleAfirmaMessages.getString("SignResultPanel.33"));
                    string2 = SimpleAfirmaMessages.getString("SignResultPanel.32");
                    this.accessibleDescription += SimpleAfirmaMessages.getString("SignResultPanel.30") + string4 + SimpleAfirmaMessages.getString("SignResultPanel.33");
                }
                break;
            case OK:
                if (z) {
                    String string5 = SimpleAfirmaMessages.getString("SignResultPanel.9");
                    this.resultTextLabel.setText(SimpleAfirmaMessages.getString("SignResultPanel.8"));
                    this.descTextLabel.setText("<html><p>" + string5 + "</p></html>");
                    string2 = SimpleAfirmaMessages.getString("SignResultPanel.10");
                    this.accessibleDescription += SimpleAfirmaMessages.getString("SignResultPanel.8") + string5 + SimpleAfirmaMessages.getString("SignResultPanel.33");
                    new LabelLinkManager(this.validationLinkLabel).setLabelLinkListener(new ValidationErrorsLabelLinkImpl(bArr, list));
                    this.validationLinkLabel.setText(SimpleAfirmaMessages.getString("SignResultPanel.38"));
                    this.validationLinkLabel.getAccessibleContext().setAccessibleName(SimpleAfirmaMessages.getString("SignDataPanel.46") + SimpleAfirmaMessages.getString("SignResultPanel.38"));
                } else {
                    String string6 = SimpleAfirmaMessages.getString("SignResultPanel.28");
                    this.resultTextLabel.setText(SimpleAfirmaMessages.getString("SignResultPanel.27"));
                    this.descTextLabel.setText("<html><p>" + string6 + "</p></html>");
                    string2 = SimpleAfirmaMessages.getString("SignResultPanel.29");
                    this.accessibleDescription += SimpleAfirmaMessages.getString("SignResultPanel.27") + SimpleAfirmaMessages.getString("SignResultPanel.28") + SimpleAfirmaMessages.getString("SignResultPanel.33");
                }
                this.linkLabel.setText(SimpleAfirmaMessages.getString("SignResultPanel.33"));
                new LabelLinkManager(this.linkLabel).setLabelLinkListener(new URLLabelLinkImpl(SimpleAfirmaMessages.getString("SignResultPanel.33")));
                if (keyListener != null) {
                    this.linkLabel.addKeyListener(keyListener);
                }
                this.linkLabel.getAccessibleContext().setAccessibleName(SimpleAfirmaMessages.getString("SignDataPanel.46") + SimpleAfirmaMessages.getString("SignResultPanel.33"));
                break;
            case GENERATED:
                this.resultTextLabel.setText(SimpleAfirmaMessages.getString("SignResultPanel.2"));
                this.descTextLabel.setText("<html><p>" + SimpleAfirmaMessages.getString("SignResultPanel.3") + "</p></html>");
                this.linkLabel.setText(SimpleAfirmaMessages.getString("SignResultPanel.33"));
                new LabelLinkManager(this.linkLabel).setLabelLinkListener(new URLLabelLinkImpl(SimpleAfirmaMessages.getString("SignResultPanel.33")));
                if (keyListener != null) {
                    this.linkLabel.addKeyListener(keyListener);
                }
                this.linkLabel.getAccessibleContext().setAccessibleName(SimpleAfirmaMessages.getString("SignDataPanel.46") + SimpleAfirmaMessages.getString("SignResultPanel.33"));
                new LabelLinkManager(this.validationLinkLabel).setLabelLinkListener(new ValidationErrorsLabelLinkImpl(bArr, list));
                this.validationLinkLabel.setText(SimpleAfirmaMessages.getString("SignResultPanel.38"));
                this.validationLinkLabel.getAccessibleContext().setAccessibleName(SimpleAfirmaMessages.getString("SignDataPanel.46") + SimpleAfirmaMessages.getString("SignResultPanel.38"));
                this.accessibleDescription += SimpleAfirmaMessages.getString("SignResultPanel.2") + SimpleAfirmaMessages.getString("SignResultPanel.3") + SimpleAfirmaMessages.getString("SignDataPanel.46") + SimpleAfirmaMessages.getString("SignResultPanel.33");
                string2 = SimpleAfirmaMessages.getString("SignResultPanel.4");
                break;
            default:
                this.resultTextLabel.setText(SimpleAfirmaMessages.getString("SignResultPanel.11"));
                if (list.get(0).getError() != null) {
                    switch (list.get(0).getError()) {
                        case NO_DATA:
                            string = SimpleAfirmaMessages.getString("SignResultPanel.15");
                            break;
                        case CANT_VALIDATE_EXTERNALLY_DETACHED:
                            string = SimpleAfirmaMessages.getString("SignResultPanel.39");
                            break;
                        case SIGN_PROFILE_NOT_CHECKED:
                            string = SimpleAfirmaMessages.getString("SignResultPanel.40");
                            break;
                        case PDF_UNKOWN_VALIDITY:
                            string = SimpleAfirmaMessages.getString("SignResultPanel.24");
                            break;
                        case OOXML_UNKOWN_VALIDITY:
                            string = SimpleAfirmaMessages.getString("SignResultPanel.25");
                            break;
                        case ODF_UNKOWN_VALIDITY:
                            string = SimpleAfirmaMessages.getString("SignResultPanel.26");
                            break;
                        default:
                            string = SimpleAfirmaMessages.getString("SignResultPanel.12");
                            break;
                    }
                } else {
                    string = SimpleAfirmaMessages.getString("SignResultPanel.12");
                }
                this.descTextLabel.setText("<html><p>" + string + "</p></html>");
                string2 = SimpleAfirmaMessages.getString("SignResultPanel.13");
                this.accessibleDescription += SimpleAfirmaMessages.getString("SignResultPanel.11") + string + SimpleAfirmaMessages.getString("SignResultPanel.13");
                new LabelLinkManager(this.validationLinkLabel).setLabelLinkListener(new ValidationErrorsLabelLinkImpl(bArr, list));
                this.validationLinkLabel.setText(SimpleAfirmaMessages.getString("SignResultPanel.38"));
                this.validationLinkLabel.getAccessibleContext().setAccessibleName(SimpleAfirmaMessages.getString("SignDataPanel.46") + SimpleAfirmaMessages.getString("SignResultPanel.38"));
                break;
        }
        scalablePane.setToolTipText(string2);
        this.resultTextLabel.setFont(getFont().deriveFont(0, 26.0f));
        this.resultTextLabel.setLabelFor(this.descTextLabel);
        if (!LookAndFeelManager.WINDOWS_HIGH_CONTRAST && Platform.getOS() == Platform.OS.WINDOWS) {
            setBackground(LookAndFeelManager.SECUNDARY_COLOR);
        }
        setLayout(new GridBagLayout());
        setFocusable(true);
        getAccessibleContext().setAccessibleDescription(this.accessibleDescription);
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = 1;
        gridBagConstraints.weightx = 0.0d;
        gridBagConstraints.weighty = 1.0d;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.insets = new Insets(11, 11, 0, 5);
        add(scalablePane, gridBagConstraints);
        gridBagConstraints.weightx = 1.0d;
        gridBagConstraints.weighty = 0.0d;
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridheight = 1;
        gridBagConstraints.insets = new Insets(11, 6, 0, 11);
        add(this.resultTextLabel, gridBagConstraints);
        gridBagConstraints.weighty = 1.0d;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new Insets(0, 6, 0, 11);
        add(this.descTextLabel, gridBagConstraints);
        gridBagConstraints.weighty = 0.0d;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new Insets(0, 6, 0, 11);
        add(this.linkLabel, gridBagConstraints);
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new Insets(0, 6, 0, 11);
        add(this.validationLinkLabel, gridBagConstraints);
    }
}
