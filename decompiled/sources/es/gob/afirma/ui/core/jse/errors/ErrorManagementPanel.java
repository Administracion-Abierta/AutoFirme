package es.gob.afirma.ui.core.jse.errors;

import com.sun.jna.platform.win32.COM.tlb.imp.TlbBase;
import es.gob.afirma.ui.core.jse.JSEUIMessages;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.Window;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/ui/core/jse/errors/ErrorManagementPanel.class */
public final class ErrorManagementPanel extends JPanel {
    private static final long serialVersionUID = 8384222173067817402L;
    private static final int DETAILS_PREFERRED_WIDTH = 700;
    private static final int DETAILS_PREFERRED_HEIGHT = 200;
    private final Window window;
    private JPanel errorInfoPanel;
    private JPanel buttonsPanel;
    private JButton closeButton;
    private JButton detailsButton;
    private JButton copyErrorButton;
    private JScrollPane scrollPane;
    private JTextArea errorTextArea;
    final ErrorManagementHandler eventsHandler;
    private boolean expandedDetails;

    ErrorManagementPanel(ErrorManagementDialog errorManagementDialog, Throwable th, Object obj, int i) {
        this.window = errorManagementDialog;
        this.eventsHandler = new ErrorManagementHandler(this, errorManagementDialog);
        createUI(th, obj, i);
        this.eventsHandler.registerComponents();
    }

    private void createUI(Throwable th, Object obj, int i) {
        setLayout(new GridBagLayout());
        this.errorInfoPanel = createInfoTextPanel(th, obj, i);
        this.errorTextArea = new JTextArea();
        this.errorTextArea.setEditable(false);
        boolean z = false;
        if (th != null && th.getStackTrace().length != 0) {
            StringWriter stringWriter = new StringWriter();
            th.printStackTrace(new PrintWriter(stringWriter));
            this.errorTextArea.append(stringWriter.toString().replaceAll(TlbBase.TAB, "      "));
            z = true;
        }
        this.scrollPane = new JScrollPane(this.errorTextArea);
        this.scrollPane.getVerticalScrollBar().setValue(this.scrollPane.getVerticalScrollBar().getMinimum());
        this.scrollPane.setPreferredSize(new Dimension(700, (int) Math.min(200.0d, Toolkit.getDefaultToolkit().getScreenSize().getHeight() * 0.7d)));
        SwingUtilities.invokeLater(new Runnable() { // from class: es.gob.afirma.ui.core.jse.errors.ErrorManagementPanel.1
            @Override // java.lang.Runnable
            public void run() {
                ErrorManagementPanel.this.getScrollPane().getVerticalScrollBar().setValue(ErrorManagementPanel.this.getScrollPane().getVerticalScrollBar().getMinimum());
                ErrorManagementPanel.this.getScrollPane().getHorizontalScrollBar().setValue(ErrorManagementPanel.this.getScrollPane().getHorizontalScrollBar().getMinimum());
            }
        });
        this.scrollPane.setVisible(false);
        this.buttonsPanel = createButtonsPanel(z);
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = 2;
        gridBagConstraints.weighty = 0.0d;
        gridBagConstraints.weightx = 1.0d;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new Insets(15, 5, 11, 15);
        add(this.errorInfoPanel, gridBagConstraints);
        gridBagConstraints.gridy++;
        gridBagConstraints.ipady = 140;
        gridBagConstraints.insets = new Insets(0, 15, 11, 15);
        add(this.scrollPane, gridBagConstraints);
        gridBagConstraints.gridy++;
        gridBagConstraints.ipady = 0;
        add(this.buttonsPanel, gridBagConstraints);
    }

    private static JPanel createInfoTextPanel(Throwable th, Object obj, int i) {
        JLabel jLabel = new JLabel();
        if (i == 0) {
            jLabel.setIcon(new ImageIcon(ErrorManagementPanel.class.getResource("error_icon.png")));
        } else if (i == 2) {
            jLabel.setIcon(new ImageIcon(ErrorManagementPanel.class.getResource("warning_icon.png")));
        }
        String str = "<html>" + obj.toString().replace("\n", "<br>") + "</html>";
        JLabel jLabel2 = new JLabel();
        jLabel2.setText(str);
        JPanel jPanel = new JPanel(new FlowLayout(3, 11, 0));
        jPanel.add(jLabel);
        jPanel.add(jLabel2);
        return jPanel;
    }

    private JPanel createButtonsPanel(boolean z) {
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new FlowLayout(4));
        if (z) {
            this.copyErrorButton = new JButton(JSEUIMessages.getString("JSEUIManager.92"));
            this.copyErrorButton.getAccessibleContext().setAccessibleDescription(JSEUIMessages.getString("JSEUIManager.92"));
            this.copyErrorButton.setVisible(false);
            jPanel.add(this.copyErrorButton);
            this.detailsButton = new JButton(JSEUIMessages.getString("JSEUIManager.90"));
            this.detailsButton.getAccessibleContext().setAccessibleDescription(JSEUIMessages.getString("JSEUIManager.90"));
            jPanel.add(this.detailsButton);
        }
        this.closeButton = new JButton(JSEUIMessages.getString("JSEUIManager.1"));
        this.closeButton.getAccessibleContext().setAccessibleDescription(JSEUIMessages.getString("JSEUIManager.1"));
        jPanel.add(this.closeButton);
        return jPanel;
    }

    public Window getWindow() {
        return this.window;
    }

    public JPanel getErrorInfoPanel() {
        return this.errorInfoPanel;
    }

    public JScrollPane getScrollPane() {
        return this.scrollPane;
    }

    public JButton getCloseButton() {
        return this.closeButton;
    }

    public JButton getDetailsButton() {
        return this.detailsButton;
    }

    public boolean isExpandedDetails() {
        return this.expandedDetails;
    }

    public void setExpandedDetails(boolean z) {
        this.expandedDetails = z;
    }

    public JButton getCopyErrorButton() {
        return this.copyErrorButton;
    }

    public JTextArea getErrorTextArea() {
        return this.errorTextArea;
    }
}
