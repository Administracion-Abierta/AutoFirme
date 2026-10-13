package es.gob.afirma.standalone.ui;

import es.gob.afirma.core.misc.Platform;
import es.gob.afirma.standalone.DesktopUtil;
import es.gob.afirma.standalone.SimpleAfirmaMessages;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/DataFileInfoDialog.class */
public class DataFileInfoDialog {
    private static final String DEFAULT_DESCRIPTION = "binary";
    private final JOptionPane optionPane = new JOptionPane();
    private final DataFileInfo dataInfo;
    final Component parent;
    private static String accessibleDescription = "";

    public DataFileInfoDialog(DataFileInfo dataFileInfo, Component component) {
        this.dataInfo = dataFileInfo;
        this.parent = component;
    }

    public Options show() {
        Options[] optionsArr;
        accessibleDescription = "";
        this.optionPane.setMessageType(-1);
        this.optionPane.setMessage(createInfoPanel(this.dataInfo));
        boolean z = (this.dataInfo.getExtension() == null || this.dataInfo.getExtension().trim().isEmpty() || this.dataInfo.isExecutable()) ? false : true;
        if (Platform.OS.WINDOWS.equals(Platform.getOS())) {
            if (z) {
                optionsArr = new Options[]{Options.OPEN, Options.SAVE, Options.CANCEL};
            } else {
                optionsArr = new Options[]{Options.SAVE, Options.CANCEL};
            }
        } else if (z) {
            optionsArr = new Options[]{Options.CANCEL, Options.OPEN, Options.SAVE};
        } else {
            optionsArr = new Options[]{Options.CANCEL, Options.SAVE};
        }
        this.optionPane.setOptions(optionsArr);
        JDialog jDialogCreateDialog = this.optionPane.createDialog(this.parent, SimpleAfirmaMessages.getString("DataFileInfoDialog.3"));
        jDialogCreateDialog.getAccessibleContext().setAccessibleDescription(accessibleDescription);
        jDialogCreateDialog.setIconImages(DesktopUtil.getIconImages());
        jDialogCreateDialog.setVisible(true);
        return (Options) this.optionPane.getValue();
    }

    private static JPanel createInfoPanel(DataFileInfo dataFileInfo) {
        String string;
        JPanel jPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        JPanel jPanel2 = new JPanel();
        BufferedImage icon = dataFileInfo.getIcon();
        if (icon != null) {
            jPanel2.add(new JLabel(new ImageIcon(icon)));
        }
        gridBagConstraints.fill = 3;
        gridBagConstraints.weighty = 1.0d;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        jPanel.add(jPanel2, gridBagConstraints);
        JPanel jPanel3 = new JPanel(new GridBagLayout());
        GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
        gridBagConstraints2.fill = 2;
        gridBagConstraints2.insets = new Insets(3, 3, 3, 3);
        gridBagConstraints2.gridx = 0;
        gridBagConstraints2.gridy = 0;
        jPanel3.add(new JLabel(SimpleAfirmaMessages.getString("DataFileInfoDialog.4")), gridBagConstraints2);
        gridBagConstraints2.insets = new Insets(3, 8, 3, 3);
        gridBagConstraints2.gridx++;
        String string2 = (dataFileInfo.getDescription() == null || "binary".equals(dataFileInfo.getDescription())) ? SimpleAfirmaMessages.getString("DataFileInfoDialog.5") : dataFileInfo.getDescription();
        jPanel3.add(new JLabel(string2), gridBagConstraints2);
        accessibleDescription += SimpleAfirmaMessages.getString("DataFileInfoDialog.4") + string2;
        gridBagConstraints2.insets = new Insets(3, 3, 3, 3);
        gridBagConstraints2.gridx = 0;
        gridBagConstraints2.gridy++;
        jPanel3.add(new JLabel(SimpleAfirmaMessages.getString("DataFileInfoDialog.6")), gridBagConstraints2);
        gridBagConstraints2.insets = new Insets(3, 8, 3, 3);
        gridBagConstraints2.gridx++;
        if (dataFileInfo.getExtension() != null && !dataFileInfo.getExtension().trim().isEmpty()) {
            string = dataFileInfo.getExtension();
        } else {
            string = SimpleAfirmaMessages.getString("DataFileInfoDialog.7");
        }
        String str = string;
        jPanel3.add(new JLabel(str), gridBagConstraints2);
        accessibleDescription += SimpleAfirmaMessages.getString("DataFileInfoDialog.6") + str;
        gridBagConstraints2.insets = new Insets(3, 3, 3, 3);
        gridBagConstraints2.gridx = 0;
        gridBagConstraints2.gridy++;
        jPanel3.add(new JLabel(SimpleAfirmaMessages.getString("DataFileInfoDialog.8")), gridBagConstraints2);
        gridBagConstraints2.insets = new Insets(3, 8, 3, 3);
        gridBagConstraints2.gridx++;
        String fileSize = formatFileSize(dataFileInfo.getSize());
        jPanel3.add(new JLabel(fileSize), gridBagConstraints2);
        accessibleDescription += SimpleAfirmaMessages.getString("DataFileInfoDialog.8") + fileSize;
        gridBagConstraints.insets = new Insets(0, 6, 0, 0);
        gridBagConstraints.weightx = 0.0d;
        gridBagConstraints.gridx++;
        jPanel.add(jPanel3, gridBagConstraints);
        return jPanel;
    }

    private static String formatFileSize(int i) {
        if (i < 1024) {
            return NumberFormat.getInstance().format(i) + " bytes";
        }
        int i2 = i / 1024;
        if (i2 < 1024) {
            return NumberFormat.getInstance().format(i2) + " KB";
        }
        double d = ((double) i2) / 1024.0d;
        double dPow = Math.pow(10.0d, 1.0d);
        return new DecimalFormat("###,###.# MB").format(Math.round(d * dPow) / dPow);
    }

    /* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/DataFileInfoDialog$Options.class */
    public enum Options {
        SAVE(SimpleAfirmaMessages.getString("DataFileInfoDialog.0")),
        OPEN(SimpleAfirmaMessages.getString("DataFileInfoDialog.1")),
        CANCEL(SimpleAfirmaMessages.getString("DataFileInfoDialog.2"));

        private String text;

        Options(String str) {
            this.text = str;
        }

        @Override // java.lang.Enum
        public String toString() {
            return this.text;
        }
    }
}
