package es.gob.afirma.standalone.ui;

import es.gob.afirma.standalone.SimpleAfirmaMessages;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Window;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/ErrorPane.class */
public class ErrorPane {
    private static JPanel mainPanel;
    private static JLabel detailsLabel;

    public static void showError(Component component, Object obj) {
        showError(component, obj, null);
    }

    public static void showError(Component component, Object obj, Throwable th) {
        JDialog jDialog;
        if (component instanceof Window) {
            jDialog = new JDialog((Window) component, Dialog.ModalityType.APPLICATION_MODAL);
        } else if (component instanceof Frame) {
            jDialog = new JDialog((Frame) component, true);
        } else if (component instanceof Dialog) {
            jDialog = new JDialog((Dialog) component, true);
        } else {
            jDialog = new JDialog();
        }
        if (th == null) {
            jDialog.setContentPane(new JOptionPane(obj, 0, -1));
            jDialog.pack();
            jDialog.setVisible(true);
            return;
        }
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = 2;
        gridBagConstraints.weightx = 1.0d;
        gridBagConstraints.gridy = 0;
        mainPanel = new JPanel(new GridBagLayout());
        if (obj instanceof Component) {
            mainPanel.add((Component) obj, gridBagConstraints);
        } else {
            mainPanel.add(new JLabel(obj.toString()), gridBagConstraints);
        }
        detailsLabel = new JLabel(SimpleAfirmaMessages.getString("ErrorPane.1"), new ImageIcon(ErrorPane.class.getResource("/resources/default_cert_ico.png")), 2);
        JPanel jPanel = new JPanel();
        jPanel.add(detailsLabel);
        gridBagConstraints.gridy = 1;
        mainPanel.add(jPanel, gridBagConstraints);
        detailsLabel.addMouseListener(new DetailsLinkListener(jPanel, detailsLabel, getTrace(th)));
        JOptionPane.showMessageDialog(component, mainPanel);
    }

    private static String getTrace(Throwable th) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PrintWriter printWriter = new PrintWriter(byteArrayOutputStream);
        Throwable th2 = null;
        try {
            th.printStackTrace(printWriter);
            if (printWriter != null) {
                if (0 != 0) {
                    try {
                        printWriter.close();
                    } catch (Throwable th3) {
                        th2.addSuppressed(th3);
                    }
                } else {
                    printWriter.close();
                }
            }
            return new String(byteArrayOutputStream.toByteArray());
        } catch (Throwable th4) {
            if (printWriter != null) {
                if (0 != 0) {
                    try {
                        printWriter.close();
                    } catch (Throwable th5) {
                        th2.addSuppressed(th5);
                    }
                } else {
                    printWriter.close();
                }
            }
            throw th4;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/ErrorPane$DetailsLinkListener.class */
    private static class DetailsLinkListener implements MouseListener {
        private final JPanel parentPanel;
        private final JLabel showDetailsLabel;
        private String trace;
        private boolean isShowingDetails = false;
        private JTextArea stackTraceArea = null;

        public DetailsLinkListener(JPanel jPanel, JLabel jLabel, String str) {
            this.trace = null;
            this.parentPanel = jPanel;
            this.showDetailsLabel = jLabel;
            this.trace = str;
        }

        private void showDetails() {
            if (this.stackTraceArea == null) {
                this.stackTraceArea = new JTextArea();
                this.stackTraceArea.setEditable(false);
            }
            this.stackTraceArea.setText(this.trace);
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.fill = 2;
            gridBagConstraints.weightx = 1.0d;
            gridBagConstraints.gridy = 2;
            this.parentPanel.add(this.stackTraceArea, gridBagConstraints);
            this.showDetailsLabel.setIcon(new ImageIcon(ErrorPane.class.getResource("/resources/dnie_cert_ico.png")));
            this.isShowingDetails = true;
        }

        private void hideDetails() {
            this.parentPanel.remove(this.stackTraceArea);
            this.showDetailsLabel.setIcon(new ImageIcon(ErrorPane.class.getResource("/resources/default_cert_ico.png")));
            this.isShowingDetails = false;
        }

        public void mouseClicked(MouseEvent mouseEvent) {
            if (this.isShowingDetails) {
                hideDetails();
            } else {
                showDetails();
            }
        }

        public void mouseEntered(MouseEvent mouseEvent) {
        }

        public void mouseExited(MouseEvent mouseEvent) {
        }

        public void mousePressed(MouseEvent mouseEvent) {
        }

        public void mouseReleased(MouseEvent mouseEvent) {
        }
    }
}
