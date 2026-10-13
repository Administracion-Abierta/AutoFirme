package es.gob.afirma.standalone.ui.pdf;

import es.gob.afirma.core.AOCancelledOperationException;
import es.gob.afirma.core.ui.AOUIFactory;
import es.gob.afirma.signers.pades.common.PdfExtraParams;
import es.gob.afirma.standalone.DesktopUtil;
import es.gob.afirma.standalone.LookAndFeelManager;
import es.gob.afirma.standalone.SimpleAfirmaMessages;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/pdf/SignPdfDialog.class */
public final class SignPdfDialog extends JDialog implements PdfLoader.PdfLoaderListener, SignPdfUiPanel.SignPdfUiPanelListener {
    private static final long serialVersionUID = -7987676963743094243L;
    private static final int PREFERRED_WIDTH = 500;
    private static final int PREFERRED_HEIGHT = 800;
    private static final int LOWER_LIMIT_X = 30;
    private static final int LOWER_LIMIT_Y = 45;
    private static final int AREA_LIMIT = 1350;
    private static final String MESSAGE_REFERENCE_NEED_PASSWORD = "pdfpasswordprotected";
    private static final String MESSAGE_REFERENCE_BAD_PASSWORD = "pdfbadpassword";
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private final Frame parent;
    private final SignPdfDialogListener listener;
    private JScrollPane scrollPanel;
    private JPanel activePanel;
    private boolean isPdfSign;
    private boolean isMassiveSign;
    private byte[] pdfData;
    private final boolean customAppearance;
    private final boolean signatureVisible;
    private final boolean stampVisible;
    private List<BufferedImage> pages;
    private List<Dimension> pageSizes;

    /* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/pdf/SignPdfDialog$SignPdfDialogListener.class */
    public interface SignPdfDialogListener {
        void propertiesCreated(Properties properties);
    }

    SignPdfDialogListener getListener() {
        return this.listener;
    }

    private SignPdfDialog(Frame frame, SignPdfDialogListener signPdfDialogListener, boolean z, boolean z2, boolean z3) {
        super(frame);
        this.parent = frame;
        this.listener = signPdfDialogListener;
        this.signatureVisible = z;
        this.stampVisible = z3;
        this.customAppearance = z2;
        createUI();
    }

    private void createUI() {
        setTitle(SignPdfUiMessages.getString("SignPdfDialog.3"));
        setIconImages(DesktopUtil.getIconImages());
        getAccessibleContext().setAccessibleDescription(SignPdfUiMessages.getString("SignPdfDialog.2"));
        setModalityType(Dialog.ModalityType.TOOLKIT_MODAL);
        setLocationRelativeTo(this.parent);
        this.scrollPanel = new JScrollPane();
        this.scrollPanel.setHorizontalScrollBarPolicy(30);
        this.scrollPanel.getHorizontalScrollBar().setUnitIncrement(16);
        this.scrollPanel.setVerticalScrollBarPolicy(20);
        this.scrollPanel.getVerticalScrollBar().setUnitIncrement(16);
        add(this.scrollPanel);
        addWindowListener(new WindowAdapter() { // from class: es.gob.afirma.standalone.ui.pdf.SignPdfDialog.1
            public void windowClosing(WindowEvent windowEvent) {
                SignPdfDialog.this.positionCancelled();
            }
        });
    }

    public static void getVisibleSignatureExtraParams(boolean z, boolean z2, byte[] bArr, Frame frame, boolean z3, boolean z4, boolean z5, SignPdfDialogListener signPdfDialogListener) {
        if (bArr == null || bArr.length < 3) {
            throw new IllegalArgumentException("El PDF a aplicarle la firma visible no puede ser nulo ni vacio");
        }
        if (signPdfDialogListener == null) {
            throw new IllegalArgumentException("La clase a la que notificar la obtencion de propiedades no puede ser nula");
        }
        SignPdfDialog signPdfDialog = new SignPdfDialog(frame, signPdfDialogListener, z3, z4, z5);
        signPdfDialog.setPreferredSize(getPreferredDimensionToSignatureDialog());
        Point centerPoint = GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint();
        signPdfDialog.setLocation(centerPoint.x - (((int) signPdfDialog.getPreferredSize().getWidth()) / 2), centerPoint.y - (((int) signPdfDialog.getPreferredSize().getHeight()) / 2));
        PdfLoader.loadPdf(z, z2, bArr, signPdfDialog);
    }

    public static JDialog getVisibleSignatureDialog(boolean z, boolean z2, byte[] bArr, Frame frame, boolean z3, boolean z4, boolean z5, SignPdfDialogListener signPdfDialogListener) {
        if (bArr == null || bArr.length < 3) {
            throw new IllegalArgumentException("El PDF a aplicarle la firma visible no puede ser nulo ni vacio");
        }
        if (signPdfDialogListener == null) {
            throw new IllegalArgumentException("La clase a la que notificar la obtencion de propiedades no puede ser nula");
        }
        SignPdfDialog signPdfDialog = new SignPdfDialog(frame, signPdfDialogListener, z3, z4, z5);
        signPdfDialog.setPreferredSize(getPreferredDimensionToSignatureDialog());
        Point centerPoint = GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint();
        signPdfDialog.setLocation(centerPoint.x - (((int) signPdfDialog.getPreferredSize().getWidth()) / 2), centerPoint.y - (((int) signPdfDialog.getPreferredSize().getHeight()) / 2));
        PdfLoader.loadPdf(z, z2, bArr, signPdfDialog);
        return signPdfDialog;
    }

    @Override // es.gob.afirma.standalone.ui.pdf.PdfLoader.PdfLoaderListener
    public void pdfLoaded(boolean z, boolean z2, byte[] bArr) throws IOException {
        this.isPdfSign = z;
        this.isMassiveSign = z2;
        this.pdfData = bArr;
        char[] password = null;
        Properties properties = new Properties();
        do {
            try {
                this.pages = Pdf2ImagesConverter.pdf2ImagesUsefulSections(this.pdfData, password, 0);
            } catch (InvalidPasswordException e) {
                try {
                    password = AOUIFactory.getPassword(SimpleAfirmaMessages.getString(password == null ? "pdfpasswordprotected" : "pdfbadpassword"), this.parent);
                } catch (AOCancelledOperationException e2) {
                    throw e;
                }
            } catch (IOException e3) {
                throw e3;
            }
        } while (this.pages == null);
        this.pageSizes = SignPdfUiUtil.getPageSizes(this.pdfData, password);
        if (password != null) {
            properties.setProperty(PdfExtraParams.OWNER_PASSWORD_STRING, new String(password));
        }
        if (this.signatureVisible) {
            setPreferredSize(getPreferredDimensionToSignatureDialog());
            this.activePanel = new SignPdfUiPanel(this.isPdfSign, this.isMassiveSign, this.pages, this.pageSizes, this.pdfData, properties, this);
            this.scrollPanel.setViewportView(this.activePanel);
        } else if (this.stampVisible) {
            setPreferredSize(getPreferredDimensionToStampDialog());
            setTitle(SignPdfUiMessages.getString("SignPdfUiStamp.0"));
            this.activePanel = new SignPdfUiPanelStamp(this.pages, this.pageSizes, this.pdfData, this, properties);
            ((SignPdfUiPanelStamp) this.activePanel).setDialogParent(this);
            this.scrollPanel.setViewportView(this.activePanel);
        }
        pack();
        setVisible(true);
    }

    private static Dimension getPreferredDimensionToSignatureDialog() {
        return new Dimension(500, (int) Math.min(800.0d, LookAndFeelManager.getScreenSize().getHeight() * 0.9d));
    }

    private static Dimension getPreferredDimensionToStampDialog() {
        return new Dimension(500, (int) Math.min(860.0d, LookAndFeelManager.getScreenSize().getHeight() * 0.9d));
    }

    @Override // es.gob.afirma.standalone.ui.pdf.PdfLoader.PdfLoaderListener
    public void pdfLoadedFailed(Throwable th) {
        LOGGER.severe("Error creando la previsualizacion del PDF: " + th);
        if (th instanceof OutOfMemoryError) {
            AOUIFactory.showErrorMessage(SignPdfUiMessages.getString("SignPdfDialog.4"), SignPdfUiMessages.getString("SignPdfDialog.1"), 0, th);
        } else {
            AOUIFactory.showErrorMessage(SignPdfUiMessages.getString("SignPdfDialog.0"), SignPdfUiMessages.getString("SignPdfDialog.1"), 0, th);
        }
        setVisible(false);
        this.listener.propertiesCreated(new Properties());
        dispose();
    }

    @Override // es.gob.afirma.standalone.ui.pdf.SignPdfUiPanel.SignPdfUiPanelListener
    public void nextPanel(Properties properties, BufferedImage bufferedImage) {
        if (this.activePanel instanceof SignPdfUiPanel) {
            if (checkSignatureDimensions(properties)) {
                if (this.customAppearance) {
                    setPreferredSize(getPreferredDimensionToSignatureDialog());
                    this.activePanel = new SignPdfUiPanelPreview(this, properties, bufferedImage);
                    ((SignPdfUiPanelPreview) this.activePanel).setDialogParent(this);
                    if (((SignPdfUiPanelPreview) this.activePanel).checkRubricTransparency()) {
                        AOUIFactory.showMessageDialog(this, SignPdfUiMessages.getString("SignPdfDialog.9"), SignPdfUiMessages.getString("SignPdfDialog.8"), 2, null);
                    }
                    this.scrollPanel.setViewportView(this.activePanel);
                    pack();
                    ((SignPdfUiPanelPreview) this.activePanel).requestFocusInWindow();
                    return;
                }
                positionSelected(properties);
                return;
            }
            return;
        }
        if ((this.activePanel instanceof SignPdfUiPanelPreview) && this.stampVisible) {
            setPreferredSize(getPreferredDimensionToStampDialog());
            this.activePanel = new SignPdfUiPanelStamp(this.pages, this.pageSizes, this.pdfData, this, properties);
            ((SignPdfUiPanelStamp) this.activePanel).setDialogParent(this);
            this.scrollPanel.setViewportView(this.activePanel);
            pack();
            ((SignPdfUiPanelStamp) this.activePanel).requestFocusInWindow();
            return;
        }
        positionSelected(properties);
    }

    private boolean checkSignatureDimensions(Properties properties) {
        boolean z = true;
        int i = Integer.parseInt(properties.getProperty(PdfExtraParams.SIGNATURE_POSITION_ON_PAGE_LOWER_LEFTX));
        int i2 = Integer.parseInt(properties.getProperty(PdfExtraParams.SIGNATURE_POSITION_ON_PAGE_LOWER_LEFTY));
        int i3 = Integer.parseInt(properties.getProperty(PdfExtraParams.SIGNATURE_POSITION_ON_PAGE_UPPER_RIGHTX));
        int i4 = Integer.parseInt(properties.getProperty(PdfExtraParams.SIGNATURE_POSITION_ON_PAGE_UPPER_RIGHTY));
        int iAbs = Math.abs(i3 - i);
        int iAbs2 = Math.abs(i4 - i2);
        boolean z2 = iAbs < 30 || iAbs2 < 45;
        boolean z3 = iAbs * iAbs2 < 1350;
        if (z2 || z3) {
            String string = SignPdfUiMessages.getString("SignPdfDialog.6");
            String string2 = SignPdfUiMessages.getString("SignPdfDialog.7");
            transferFocus();
            AOUIFactory.showErrorMessage(this, string, string2, AOUIFactory.ERROR_MESSAGE, null);
            z = false;
        }
        return z;
    }

    @Override // es.gob.afirma.standalone.ui.pdf.SignPdfUiPanel.SignPdfUiPanelListener
    public void positionSelected(Properties properties) {
        setVisible(false);
        LOGGER.info("Propiedades establecidas mediante GUI: " + properties);
        this.listener.propertiesCreated(properties);
        dispose();
    }

    @Override // es.gob.afirma.standalone.ui.pdf.SignPdfUiPanel.SignPdfUiPanelListener
    public void positionCancelled() {
        setVisible(false);
        this.listener.propertiesCreated(new Properties());
        dispose();
    }

    /* JADX INFO: renamed from: getParent, reason: merged with bridge method [inline-methods] */
    public Frame m769getParent() {
        return this.parent;
    }
}
