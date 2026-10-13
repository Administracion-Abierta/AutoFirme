package es.gob.afirma.standalone.ui;

import es.gob.afirma.standalone.LookAndFeelManager;
import es.gob.afirma.standalone.SimpleAfirmaMessages;
import java.awt.Color;
import java.awt.Dimension;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Date;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/DocumentInfoPanel.class */
public class DocumentInfoPanel extends JPanel {
    private static final long serialVersionUID = 1627010163673368615L;
    private String accesibleDescription;
    final JLabel signsDetailsLbl = new JLabel();

    public DocumentInfoPanel(SignOperationConfig signOperationConfig, Color color) {
        createUI(signOperationConfig, color);
    }

    private void createUI(SignOperationConfig signOperationConfig, Color color) {
        if (!LookAndFeelManager.WINDOWS_HIGH_CONTRAST) {
            setBackground(color);
        }
        JLabel jLabel = new JLabel(SimpleAfirmaMessages.getString("SignPanel.46", signOperationConfig.getFileType().getFileDescription()));
        this.accesibleDescription += SimpleAfirmaMessages.getString("SignPanel.46", signOperationConfig.getFileType().getFileDescription());
        Date date = new Date(signOperationConfig.getDataFile().lastModified());
        JLabel jLabel2 = new JLabel(SimpleAfirmaMessages.getString("SignPanel.47", DateFormat.getDateTimeInstance(1, 3).format(date)));
        this.accesibleDescription += SimpleAfirmaMessages.getString("SignPanel.47", DateFormat.getDateTimeInstance(1, 3).format(date));
        String str = NumberFormat.getNumberInstance().format(signOperationConfig.getDataFile().length() / 1024);
        JLabel jLabel3 = new JLabel(SimpleAfirmaMessages.getString("SignPanel.49") + (str.equals("0") ? "<1" : str) + " KB");
        this.accesibleDescription += SimpleAfirmaMessages.getString("SignPanel.49") + (str.equals("0") ? "<1" : str) + " KB";
        setLayout(new BoxLayout(this, 1));
        add(Box.createRigidArea(new Dimension(0, 4)));
        add(jLabel);
        add(Box.createRigidArea(new Dimension(0, 4)));
        add(jLabel2);
        add(Box.createRigidArea(new Dimension(0, 4)));
        add(jLabel3);
    }

    public String getAccesibleDescription() {
        return this.accesibleDescription;
    }
}
