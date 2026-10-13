package es.gob.afirma.standalone.ui;

import es.gob.afirma.core.ui.AOUIFactory;
import es.gob.afirma.signvalidation.SignValidity;
import es.gob.afirma.standalone.LookAndFeelManager;
import es.gob.afirma.standalone.SimpleAfirmaMessages;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.util.List;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/ValidationInfoPanel.class */
public class ValidationInfoPanel extends JPanel {
    private static final long serialVersionUID = 1627010163673368615L;
    private String accesibleDescription;
    final JLabel signsDetailsLbl = new JLabel();

    public ValidationInfoPanel(SignOperationConfig signOperationConfig, Color color) {
        createUI(signOperationConfig, color);
    }

    private void createUI(SignOperationConfig signOperationConfig, Color color) {
        BufferedImage bufferedImageLoadImage;
        if (!LookAndFeelManager.WINDOWS_HIGH_CONTRAST) {
            setBackground(color);
        }
        String invalidSignatureText = signOperationConfig.getInvalidSignatureText();
        JLabel jLabel = null;
        List<SignValidity> signValidity = signOperationConfig.getSignValidity();
        if (invalidSignatureText != null) {
            if (signValidity.get(0).getValidity() == SignValidity.SIGN_DETAIL_TYPE.UNKNOWN || signValidity.get(0).getValidity() == SignValidity.SIGN_DETAIL_TYPE.PENDING_CONFIRM_BY_USER) {
                bufferedImageLoadImage = ImageLoader.loadImage("unknown_icon.png");
            } else {
                bufferedImageLoadImage = ImageLoader.loadImage("ko_icon.png");
            }
            jLabel = new JLabel(invalidSignatureText, new ImageIcon(bufferedImageLoadImage), 2);
            this.accesibleDescription += invalidSignatureText;
            jLabel.setForeground(Color.RED);
        } else if (signValidity.get(0).getValidity() == SignValidity.SIGN_DETAIL_TYPE.OK) {
            jLabel = new JLabel(SimpleAfirmaMessages.getString("SignPanel.156"), new ImageIcon(ImageLoader.loadImage("ok_icon.png")), 2);
            this.accesibleDescription += SimpleAfirmaMessages.getString("SignPanel.156");
        }
        this.signsDetailsLbl.setText(SimpleAfirmaMessages.getString("SignResultPanel.38"));
        LabelLinkManager labelLinkManager = new LabelLinkManager(this.signsDetailsLbl);
        byte[] bArr = null;
        try {
            FileInputStream fileInputStream = new FileInputStream(signOperationConfig.getDataFile());
            Throwable th = null;
            try {
                try {
                    bArr = new byte[(int) signOperationConfig.getDataFile().length()];
                    fileInputStream.read(bArr);
                    if (fileInputStream != null) {
                        if (0 != 0) {
                            try {
                                fileInputStream.close();
                            } catch (Throwable th2) {
                                th.addSuppressed(th2);
                            }
                        } else {
                            fileInputStream.close();
                        }
                    }
                    labelLinkManager.setLabelLinkListener(new ValidationErrorsLabelLinkImpl(bArr, signValidity));
                    this.signsDetailsLbl.getAccessibleContext().setAccessibleName(SimpleAfirmaMessages.getString("SignDataPanel.46") + SimpleAfirmaMessages.getString("SignResultPanel.38"));
                    setLayout(new BoxLayout(this, 1));
                    add(Box.createRigidArea(new Dimension(0, 4)));
                    add(jLabel);
                    add(Box.createRigidArea(new Dimension(0, 4)));
                    add(this.signsDetailsLbl);
                } catch (Throwable th3) {
                    th = th3;
                    throw th3;
                }
            } catch (Throwable th4) {
                if (fileInputStream != null) {
                    if (th != null) {
                        try {
                            fileInputStream.close();
                        } catch (Throwable th5) {
                            th.addSuppressed(th5);
                        }
                    } else {
                        fileInputStream.close();
                    }
                }
                throw th4;
            }
        } catch (Exception e) {
            AOUIFactory.showErrorMessage(SimpleAfirmaMessages.getString("ValidationInfoDialog.5"), SimpleAfirmaMessages.getString("SimpleAfirma.7"), 0, null);
        }
    }

    public String getAccesibleDescription() {
        return this.accesibleDescription;
    }
}
