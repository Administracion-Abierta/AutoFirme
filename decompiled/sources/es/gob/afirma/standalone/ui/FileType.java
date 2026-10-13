package es.gob.afirma.standalone.ui;

import es.gob.afirma.standalone.SimpleAfirmaMessages;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JComponent;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/FileType.class */
enum FileType {
    PDF("icon_pdf_large.png", SimpleAfirmaMessages.getString("SignPanel.0"), SimpleAfirmaMessages.getString("SignPanel.9")),
    BINARY("icon_binary_large.png", SimpleAfirmaMessages.getString("SignPanel.12"), SimpleAfirmaMessages.getString("SignPanel.11")),
    XML("icon_xml_large.png", SimpleAfirmaMessages.getString("SignPanel.8"), SimpleAfirmaMessages.getString("SignPanel.10")),
    FACTURAE("icon_facturae_large.png", SimpleAfirmaMessages.getString("SignPanel.17"), SimpleAfirmaMessages.getString("SignPanel.20")),
    SIGN_CADES("icon_sign_large.png", SimpleAfirmaMessages.getString("SignPanel.6") + " (CAdES)", SimpleAfirmaMessages.getString("SignPanel.39") + " (CAdES)"),
    SIGN_XADES("icon_sign_large.png", SimpleAfirmaMessages.getString("SignPanel.6") + " (XAdES)", SimpleAfirmaMessages.getString("SignPanel.39") + " (XAdES)"),
    OOXML("icon_office_win_large.png", SimpleAfirmaMessages.getString("SignPanel.38"), SimpleAfirmaMessages.getString("SignPanel.37")),
    ODF("icon_openoffice_large.png", SimpleAfirmaMessages.getString("SignPanel.32"), SimpleAfirmaMessages.getString("SignPanel.31"));

    private final String fileDescription;
    private final Component fileTypeScalableIcon;

    FileType(String str, String str2, String str3) {
        this.fileDescription = str3;
        this.fileTypeScalableIcon = createScalablePane(str, str2);
    }

    private static JComponent createScalablePane(String str, String str2) {
        ScalablePane scalablePane = new ScalablePane(ImageLoader.loadImage(str), true);
        scalablePane.setFocusable(false);
        scalablePane.setToolTipText(str2);
        scalablePane.setBackground(new Color(255, 255, 255, 0));
        return scalablePane;
    }

    Component getIcon() {
        return this.fileTypeScalableIcon;
    }

    String getFileDescription() {
        return this.fileDescription;
    }
}
