package es.gob.afirma.ui.core.jse.certificateselection;

import es.gob.afirma.core.keystores.NameCertificateBean;
import es.gob.afirma.core.ui.KeyStoreDialogManager;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/ui/core/jse/certificateselection/CertificateSelectionDialog.class */
public final class CertificateSelectionDialog extends MouseAdapter {
    private final CertificateSelectionPanel csd;
    private final JOptionPane optionPane;
    private final Component parent;
    private final KeyStoreDialogManager ksdm;
    private String currentKeyStoreTypeName;
    private final boolean disableSelection;
    JDialog certDialog;
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private static final Comparator<NameCertificateBean> CERT_NAME_COMPARATOR = new Comparator<NameCertificateBean>() { // from class: es.gob.afirma.ui.core.jse.certificateselection.CertificateSelectionDialog.1
        @Override // java.util.Comparator
        public int compare(NameCertificateBean nameCertificateBean, NameCertificateBean nameCertificateBean2) {
            if (nameCertificateBean == null && nameCertificateBean2 == null) {
                return 0;
            }
            if (nameCertificateBean == null) {
                return 1;
            }
            if (nameCertificateBean2 == null) {
                return -1;
            }
            return nameCertificateBean.getName().compareToIgnoreCase(nameCertificateBean2.getName());
        }
    };

    public CertificateSelectionDialog(Component component, KeyStoreDialogManager keyStoreDialogManager) {
        this(component, keyStoreDialogManager, null, null, true, false);
    }

    public CertificateSelectionDialog(Component component, KeyStoreDialogManager keyStoreDialogManager, String str, String str2, boolean z, boolean z2) {
        this.certDialog = null;
        this.parent = component;
        this.ksdm = keyStoreDialogManager;
        this.currentKeyStoreTypeName = this.ksdm.getKeyStoreName();
        NameCertificateBean[] nameCertificates = this.ksdm.getNameCertificates();
        Arrays.sort(nameCertificates, CERT_NAME_COMPARATOR);
        this.csd = new CertificateSelectionPanel(nameCertificates, this, str, str2, z, keyStoreDialogManager.isExternalStoresOpeningAllowed(), this.ksdm.getAvailablesKeyStores());
        this.optionPane = nameCertificates.length > 1 ? new CertOptionPane(this.csd) : new JOptionPane();
        this.csd.addCertificateListMouseListener(this);
        this.optionPane.setMessage(this.csd);
        this.optionPane.setMessageType(-1);
        this.optionPane.setOptionType(z2 ? -1 : 2);
        this.disableSelection = z2;
    }

    public String showDialog() throws CertificatesNotFoundException {
        return showDialog(true);
    }

    public String showDialog(boolean z) throws CertificatesNotFoundException {
        String string = CertificateSelectionDialogMessages.getString("CertificateSelectionDialog.0", this.currentKeyStoreTypeName);
        LOGGER.info("Abrimos el dialogo de certificados para el almacen: " + this.currentKeyStoreTypeName);
        if (this.ksdm.getLibName() != null) {
            string = string + " : " + this.ksdm.getLibName();
        }
        this.certDialog = this.optionPane.createDialog(this.parent, string);
        this.certDialog.setBackground(Color.WHITE);
        this.certDialog.setModal(true);
        this.certDialog.setAlwaysOnTop(z);
        CertificateSelectionDispatcherListener certificateSelectionDispatcherListener = new CertificateSelectionDispatcherListener(this.optionPane, this);
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(certificateSelectionDispatcherListener);
        Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
        if (defaultToolkit != null) {
            int height = (int) defaultToolkit.getScreenSize().getHeight();
            if (this.certDialog.getSize().getHeight() > height) {
                this.certDialog.setSize(new Dimension((int) this.certDialog.getSize().getWidth(), height));
            }
        }
        this.certDialog.setVisible(true);
        KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(certificateSelectionDispatcherListener);
        if (this.csd.getShowedCertsCount() == 0) {
            throw new CertificatesNotFoundException("No habia certificados validos en el almacen del usuario");
        }
        if (this.optionPane.getValue() == null || ((Integer) this.optionPane.getValue()).intValue() != 0) {
            this.certDialog.dispose();
            return null;
        }
        String selectedCertificateAlias = this.csd.getSelectedCertificateAlias();
        this.csd.savePreferredCertificateView();
        this.certDialog.dispose();
        this.certDialog = null;
        return selectedCertificateAlias;
    }

    public void mouseClicked(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() == 2 && this.optionPane != null && !this.disableSelection) {
            this.optionPane.setValue(0);
        }
    }

    public void refreshKeystore() {
        try {
            this.ksdm.refresh();
            refreshDialog();
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error en la orden de actualizacion del almacen: " + e, (Throwable) e);
        }
    }

    private void refreshDialog() {
        NameCertificateBean[] nameCertificates = this.ksdm.getNameCertificates();
        Arrays.sort(nameCertificates, CERT_NAME_COMPARATOR);
        refreshDialog(nameCertificates);
    }

    private void refreshDialog(NameCertificateBean[] nameCertificateBeanArr) {
        this.certDialog.setAlwaysOnTop(false);
        try {
            this.csd.refresh(nameCertificateBeanArr);
        } catch (Exception e) {
            LOGGER.warning("No se pudo actualizar el dialogo de seleccion: " + e);
        }
        this.certDialog.pack();
    }

    public void refreshDialog(NameCertificateBean[] nameCertificateBeanArr, CertificateLineView certificateLineView) {
        this.csd.setCertLineView(certificateLineView);
        refreshDialog(nameCertificateBeanArr);
    }

    public void changeKeyStore(int i, String str, String str2) {
        boolean zChangeKeyStoreManager;
        this.certDialog.setAlwaysOnTop(false);
        if (str2 == null || str2.isEmpty()) {
            zChangeKeyStoreManager = this.ksdm.changeKeyStoreManager(i, this.parent);
        } else {
            zChangeKeyStoreManager = this.ksdm.changeKeyStoreManagerToPKCS11(this.parent, str, str2);
        }
        if (zChangeKeyStoreManager) {
            refreshDialog();
            this.currentKeyStoreTypeName = this.ksdm.getKeyStoreName();
            this.certDialog.setTitle(CertificateSelectionDialogMessages.getString("CertificateSelectionDialog.0", this.currentKeyStoreTypeName != null ? this.currentKeyStoreTypeName : str));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/ui/core/jse/certificateselection/CertificateSelectionDialog$CertOptionPane.class */
    private static final class CertOptionPane extends JOptionPane {
        private static final long serialVersionUID = 1;
        private final CertificateSelectionPanel selectionPanel;

        CertOptionPane(CertificateSelectionPanel certificateSelectionPanel) {
            this.selectionPanel = certificateSelectionPanel;
        }

        public void selectInitialValue() {
            this.selectionPanel.selectCertificateList();
        }
    }
}
