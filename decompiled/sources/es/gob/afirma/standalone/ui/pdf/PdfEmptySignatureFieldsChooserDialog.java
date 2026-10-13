package es.gob.afirma.standalone.ui.pdf;

import es.gob.afirma.core.AOCancelledOperationException;
import es.gob.afirma.core.ui.AOUIFactory;
import es.gob.afirma.signers.pades.PdfUtil;
import es.gob.afirma.signers.pades.common.PdfExtraParams;
import es.gob.afirma.standalone.DesktopUtil;
import es.gob.afirma.standalone.SimpleAfirmaMessages;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/pdf/PdfEmptySignatureFieldsChooserDialog.class */
public final class PdfEmptySignatureFieldsChooserDialog extends JDialog implements SignPdfUiPanel.SignPdfUiPanelListener, PdfLoader.PdfLoaderListener {
    private static final long serialVersionUID = 4904673758490090781L;
    private static final int PREFERRED_WIDTH = 500;
    private static final int PREFERRED_HEIGHT = 615;
    private static final String MESSAGE_REFERENCE_NEED_PASSWORD = "pdfpasswordprotected";
    private static final String MESSAGE_REFERENCE_BAD_PASSWORD = "pdfbadpassword";
    private final SignPdfDialog.SignPdfDialogListener listener;
    private final byte[] pdf;
    private final boolean isSign;
    private JPanel activePanel;
    private final Properties extraParams;
    private final boolean stampVisible;
    private final PdfUtil.SignatureField field;

    SignPdfDialog.SignPdfDialogListener getListener() {
        return this.listener;
    }

    PdfUtil.SignatureField getField() {
        return this.field;
    }

    public static void startPdfEmptySignatureFieldsChooserDialog(byte[] bArr, boolean z, Frame frame, PdfUtil.SignatureField signatureField, SignPdfDialog.SignPdfDialogListener signPdfDialogListener, boolean z2) {
        PdfEmptySignatureFieldsChooserDialog pdfEmptySignatureFieldsChooserDialog = new PdfEmptySignatureFieldsChooserDialog(bArr, z, frame, signatureField, signPdfDialogListener, z2);
        pdfEmptySignatureFieldsChooserDialog.setSize(500, 615);
        pdfEmptySignatureFieldsChooserDialog.setLocationRelativeTo(frame);
    }

    private PdfEmptySignatureFieldsChooserDialog(byte[] bArr, boolean z, Frame frame, PdfUtil.SignatureField signatureField, SignPdfDialog.SignPdfDialogListener signPdfDialogListener, boolean z2) {
        super(frame);
        this.listener = signPdfDialogListener;
        this.pdf = bArr;
        this.isSign = z;
        this.field = signatureField;
        this.stampVisible = z2;
        setTitle(SignPdfUiMessages.getString("SignPdfDialog.3"));
        setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        this.extraParams = new Properties();
        this.extraParams.put(PdfExtraParams.SIGNATURE_FIELD, getField().getName());
        this.extraParams.put(PdfExtraParams.SIGNATURE_PAGE, Integer.toString(getField().getPage()));
        this.extraParams.put(PdfExtraParams.SIGNATURE_POSITION_ON_PAGE_LOWER_LEFTX, Integer.toString(getField().getSignaturePositionOnPageLowerLeftX()));
        this.extraParams.put(PdfExtraParams.SIGNATURE_POSITION_ON_PAGE_LOWER_LEFTY, Integer.toString(getField().getSignaturePositionOnPageLowerLeftY()));
        this.extraParams.put(PdfExtraParams.SIGNATURE_POSITION_ON_PAGE_UPPER_RIGHTX, Integer.toString(getField().getSignaturePositionOnPageUpperRightX()));
        this.extraParams.put(PdfExtraParams.SIGNATURE_POSITION_ON_PAGE_UPPER_RIGHTY, Integer.toString(getField().getSignaturePositionOnPageUpperRightY()));
        SwingUtilities.invokeLater(() -> {
            createUI();
        });
    }

    void createUI() {
        getContentPane().setLayout(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = 1;
        gridBagConstraints.weightx = 1.0d;
        setIconImages(DesktopUtil.getIconImages());
        getAccessibleContext().setAccessibleDescription(SignPdfUiMessages.getString("SignPdfFieldChooser.1"));
        addWindowListener(new WindowAdapter() { // from class: es.gob.afirma.standalone.ui.pdf.PdfEmptySignatureFieldsChooserDialog.1
            public void windowClosing(WindowEvent windowEvent) {
                PdfEmptySignatureFieldsChooserDialog.this.positionCancelled();
            }
        });
        try {
            BufferedImage fragmentImage = getFragmentImage(getField());
            if (fragmentImage != null) {
                nextPanel(this.extraParams, fragmentImage);
                return;
            }
            throw new IOException("Error creando la imagen para previsualizar");
        } catch (Exception e) {
            Logger.getLogger("es.gob.afirma").severe("No ha sido posible cargar la previsualizacion del acrocampo: " + e);
            AOUIFactory.showErrorMessage(SignPdfUiMessages.getString("SignPdfFieldChooser.2"), SignPdfUiMessages.getString("SignPdfFieldChooser.3"), 0, e);
            setVisible(false);
            getListener().propertiesCreated(new Properties());
            dispose();
        }
    }

    BufferedImage getFragmentImage(PdfUtil.SignatureField signatureField) throws IOException {
        int page = signatureField.getPage() - 1;
        char[] password = null;
        List<BufferedImage> listPdf2ImagesUsefulSections = null;
        do {
            try {
                listPdf2ImagesUsefulSections = Pdf2ImagesConverter.pdf2ImagesUsefulSections(this.pdf, password, page);
            } catch (InvalidPasswordException e) {
                try {
                    password = AOUIFactory.getPassword(SimpleAfirmaMessages.getString(password == null ? "pdfpasswordprotected" : "pdfbadpassword"), this);
                } catch (AOCancelledOperationException e2) {
                    throw e;
                }
            } catch (IOException e3) {
                throw e3;
            }
        } while (listPdf2ImagesUsefulSections == null);
        BufferedImage bufferedImage = listPdf2ImagesUsefulSections.get(page);
        int signaturePositionOnPageUpperRightX = signatureField.getSignaturePositionOnPageUpperRightX();
        int signaturePositionOnPageUpperRightY = signatureField.getSignaturePositionOnPageUpperRightY();
        int signaturePositionOnPageLowerLeftX = signatureField.getSignaturePositionOnPageLowerLeftX();
        return bufferedImage.getSubimage(signaturePositionOnPageLowerLeftX, signaturePositionOnPageUpperRightY, signaturePositionOnPageUpperRightX - signaturePositionOnPageLowerLeftX, signaturePositionOnPageUpperRightY - signatureField.getSignaturePositionOnPageLowerLeftY());
    }

    @Override // es.gob.afirma.standalone.ui.pdf.SignPdfUiPanel.SignPdfUiPanelListener
    public void nextPanel(Properties properties, BufferedImage bufferedImage) {
        if (this.activePanel == null) {
            getContentPane().removeAll();
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.fill = 1;
            gridBagConstraints.weightx = 1.0d;
            gridBagConstraints.weighty = 1.0d;
            gridBagConstraints.insets = new Insets(0, 0, 0, 0);
            this.activePanel = new SignPdfUiPanelPreview(this, properties, bufferedImage);
            ((SignPdfUiPanelPreview) this.activePanel).setDialogParent(this);
            getContentPane().add(this.activePanel, gridBagConstraints);
            setVisible(true);
            ((SignPdfUiPanelPreview) this.activePanel).requestFocusInWindow();
            return;
        }
        if ((this.activePanel instanceof SignPdfUiPanelPreview) && this.stampVisible) {
            PdfLoader.loadPdf(this.isSign, false, this.pdf, this);
        } else {
            positionSelected(properties);
        }
    }

    @Override // es.gob.afirma.standalone.ui.pdf.SignPdfUiPanel.SignPdfUiPanelListener
    public void positionSelected(Properties properties) {
        setVisible(false);
        this.listener.propertiesCreated(properties);
        dispose();
    }

    @Override // es.gob.afirma.standalone.ui.pdf.SignPdfUiPanel.SignPdfUiPanelListener
    public void positionCancelled() {
        setVisible(false);
        this.listener.propertiesCreated(new Properties());
        dispose();
    }

    public static PdfUtil.SignatureField selectField(List<PdfUtil.SignatureField> list) throws AOCancelledOperationException {
        String string = SignPdfUiMessages.getString("SignPdfFieldChooser.7");
        JComboBox jComboBox = new JComboBox(list.toArray());
        JLabel jLabel = new JLabel(string);
        JPanel jPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = 2;
        gridBagConstraints.weightx = 1.0d;
        gridBagConstraints.gridy = 0;
        jPanel.add(jLabel, gridBagConstraints);
        gridBagConstraints.gridy++;
        jPanel.add(jComboBox, gridBagConstraints);
        gridBagConstraints.fill = 1;
        gridBagConstraints.weighty = 1.0d;
        jPanel.add(new JLabel(), gridBagConstraints);
        String[] strArr = {SignPdfUiMessages.getString("SignPdfFieldChooser.4"), SignPdfUiMessages.getString("SignPdfFieldChooser.6"), SignPdfUiMessages.getString("SignPdfFieldChooser.5")};
        int iShowOptionDialog = JOptionPane.showOptionDialog((Component) null, jPanel, SimpleAfirmaMessages.getString("SignPanelSignTask.1"), -1, 3, (Icon) null, strArr, strArr[0]);
        if (iShowOptionDialog == 2) {
            throw new AOCancelledOperationException();
        }
        if (iShowOptionDialog == 0) {
            return (PdfUtil.SignatureField) jComboBox.getSelectedItem();
        }
        return null;
    }

    @Override // es.gob.afirma.standalone.ui.pdf.PdfLoader.PdfLoaderListener
    public void pdfLoaded(boolean z, boolean z2, byte[] bArr) throws IOException {
        getContentPane().remove(this.activePanel);
        char[] password = null;
        List<BufferedImage> listPdf2ImagesUsefulSections = null;
        Properties properties = new Properties();
        do {
            try {
                listPdf2ImagesUsefulSections = Pdf2ImagesConverter.pdf2ImagesUsefulSections(bArr, password, 0);
            } catch (InvalidPasswordException e) {
                try {
                    password = AOUIFactory.getPassword(SimpleAfirmaMessages.getString(password == null ? "pdfpasswordprotected" : "pdfbadpassword"), this);
                } catch (AOCancelledOperationException e2) {
                    throw e;
                }
            } catch (IOException e3) {
                throw e3;
            }
        } while (listPdf2ImagesUsefulSections == null);
        List<Dimension> pageSizes = SignPdfUiUtil.getPageSizes(bArr, password);
        if (password != null) {
            properties.setProperty(PdfExtraParams.OWNER_PASSWORD_STRING, new String(password));
        }
        this.activePanel = new SignPdfUiPanelStamp(listPdf2ImagesUsefulSections, pageSizes, bArr, this, new Properties());
        getContentPane().add(this.activePanel);
    }

    @Override // es.gob.afirma.standalone.ui.pdf.PdfLoader.PdfLoaderListener
    public void pdfLoadedFailed(Throwable th) {
        Logger.getLogger("es.gob.afirma").severe("Error creando la previsualizacion del PDF: " + th);
        if (th instanceof OutOfMemoryError) {
            AOUIFactory.showErrorMessage(SignPdfUiMessages.getString("SignPdfDialog.4"), SignPdfUiMessages.getString("SignPdfDialog.1"), 0, th);
        } else {
            AOUIFactory.showErrorMessage(SignPdfUiMessages.getString("SignPdfDialog.0"), SignPdfUiMessages.getString("SignPdfDialog.1"), 0, th);
        }
        setVisible(false);
        this.listener.propertiesCreated(new Properties());
        dispose();
    }
}
