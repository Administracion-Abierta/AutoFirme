package es.gob.afirma.standalone.ui;

import es.gob.afirma.core.signers.AOSignConstants;
import es.gob.afirma.standalone.SimpleAfirmaMessages;
import es.gob.afirma.standalone.configurator.common.PreferencesManager;
import es.gob.afirma.standalone.ui.preferences.FormatItem;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/ChangeFormatPanel.class */
public class ChangeFormatPanel extends JPanel {
    private static final long serialVersionUID = 8338298450770919501L;
    private FormatItem XADES;
    private FormatItem CADES;
    private FormatItem PADES;
    private FormatItem OOXML;
    private FormatItem FACTURAE;
    private FormatItem ODF;
    private FormatItem[] pdfFilesArray;
    private FormatItem[] ooxmlFilesArray;
    private FormatItem[] facturaeFilesArray;
    private FormatItem[] xmlFilesArray;
    private FormatItem[] binFilesArray;
    private FormatItem[] odfFilesArray;
    private final JComboBox<FormatItem> usedCombo = new JComboBox<>();
    private static String accessibleDescription = "";

    public ChangeFormatPanel(SignOperationConfig signOperationConfig) {
        initFormats();
        accessibleDescription = SimpleAfirmaMessages.getString("ChangeFormatDialog.0");
        createPanel(signOperationConfig);
    }

    private void createPanel(SignOperationConfig signOperationConfig) {
        setLayout(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = 2;
        gridBagConstraints.anchor = 21;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridx = 0;
        add(new JLabel(SimpleAfirmaMessages.getString("ChangeFormatDialog.1")));
        gridBagConstraints.gridy++;
        String str = null;
        FormatItem[] formatItemArr = null;
        if (FileType.XML.equals(signOperationConfig.getFileType()) || FileType.SIGN_XADES.equals(signOperationConfig.getFileType())) {
            formatItemArr = this.xmlFilesArray;
            str = PreferencesManager.get(PreferencesManager.PREFERENCE_GENERAL_DEFAULT_FORMAT_XML, PreferencesManager.PreferencesSource.DEFAULT);
        } else if (FileType.PDF.equals(signOperationConfig.getFileType())) {
            formatItemArr = this.pdfFilesArray;
            str = PreferencesManager.get(PreferencesManager.PREFERENCE_GENERAL_DEFAULT_FORMAT_PDF, PreferencesManager.PreferencesSource.DEFAULT);
        } else if (FileType.OOXML.equals(signOperationConfig.getFileType())) {
            formatItemArr = this.ooxmlFilesArray;
            str = PreferencesManager.get(PreferencesManager.PREFERENCE_GENERAL_DEFAULT_FORMAT_OOXML, PreferencesManager.PreferencesSource.DEFAULT);
        } else if (FileType.FACTURAE.equals(signOperationConfig.getFileType())) {
            formatItemArr = this.facturaeFilesArray;
            str = PreferencesManager.get(PreferencesManager.PREFERENCE_GENERAL_DEFAULT_FORMAT_FACTURAE, PreferencesManager.PreferencesSource.DEFAULT);
        } else if (FileType.BINARY.equals(signOperationConfig.getFileType()) || FileType.SIGN_CADES.equals(signOperationConfig.getFileType())) {
            formatItemArr = this.binFilesArray;
            str = PreferencesManager.get(PreferencesManager.PREFERENCE_GENERAL_DEFAULT_FORMAT_BIN, PreferencesManager.PreferencesSource.DEFAULT);
        } else if (FileType.ODF.equals(signOperationConfig.getFileType())) {
            formatItemArr = this.odfFilesArray;
            str = PreferencesManager.get(PreferencesManager.PREFERENCE_GENERAL_DEFAULT_FORMAT_ODF, PreferencesManager.PreferencesSource.DEFAULT);
        }
        createUsedCombo(str, formatItemArr);
        for (int i = 0; i < this.usedCombo.getItemCount(); i++) {
            if (signOperationConfig.getSignatureFormatName().contains(((FormatItem) this.usedCombo.getItemAt(i)).getName())) {
                this.usedCombo.setSelectedIndex(i);
            }
        }
        add(this.usedCombo, gridBagConstraints);
    }

    private void initFormats() {
        this.XADES = new FormatItem(AOSignConstants.SIGN_FORMAT_XADES);
        this.CADES = new FormatItem("CAdES");
        this.PADES = new FormatItem("PAdES");
        this.OOXML = new FormatItem(AOSignConstants.SIGN_FORMAT_OOXML);
        this.FACTURAE = new FormatItem(AOSignConstants.SIGN_FORMAT_FACTURAE);
        this.ODF = new FormatItem(AOSignConstants.SIGN_FORMAT_ODF);
        this.pdfFilesArray = new FormatItem[]{this.PADES, this.CADES, this.XADES};
        this.ooxmlFilesArray = new FormatItem[]{this.OOXML, this.CADES, this.XADES};
        this.facturaeFilesArray = new FormatItem[]{this.FACTURAE, this.XADES, this.CADES};
        this.xmlFilesArray = new FormatItem[]{this.XADES, this.CADES};
        this.binFilesArray = new FormatItem[]{this.CADES, this.XADES};
        this.odfFilesArray = new FormatItem[]{this.ODF, this.CADES, this.XADES};
    }

    private void createUsedCombo(String str, FormatItem[] formatItemArr) {
        for (FormatItem formatItem : formatItemArr) {
            if (str.equals(formatItem.getName())) {
                formatItem.setRecommended(true);
            }
            this.usedCombo.addItem(formatItem);
        }
    }

    public static String getAccessibleDescription() {
        return accessibleDescription;
    }

    public JComboBox<FormatItem> getUsedCombo() {
        return this.usedCombo;
    }
}
