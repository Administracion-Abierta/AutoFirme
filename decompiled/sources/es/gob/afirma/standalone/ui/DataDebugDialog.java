package es.gob.afirma.standalone.ui;

import es.gob.afirma.core.misc.AOUtil;
import es.gob.afirma.core.misc.MimeHelper;
import es.gob.afirma.core.ui.AOUIFactory;
import es.gob.afirma.standalone.SimpleAfirmaMessages;
import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/DataDebugDialog.class */
public final class DataDebugDialog extends JDialog {
    static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private static final long serialVersionUID = 5993877132056846401L;
    private static final int DIALOG_WIDTH = 800;
    private static final int DIALOG_HEIGHT = 600;
    private byte[] data;

    public DataDebugDialog(byte[] bArr) {
        if (bArr == null || bArr.length < 1) {
            throw new IllegalArgumentException("Los datos a firmar no pueden ser nulos ni vacios");
        }
        this.data = (byte[]) bArr.clone();
        final JTextArea jTextArea = new JTextArea();
        JScrollPane jScrollPane = new JScrollPane(jTextArea);
        fillDisplay(jTextArea, bArr);
        jTextArea.setEditable(false);
        jTextArea.setFont(new Font("monospaced", 0, 12));
        JButton jButton = new JButton(SimpleAfirmaMessages.getString("DataDebugDialog.8"));
        jButton.addActionListener(actionEvent -> {
            setVisible(false);
        });
        jButton.setMnemonic('C');
        jButton.getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("DataDebugDialog.9"));
        jTextArea.addKeyListener(new KeyAdapter() { // from class: es.gob.afirma.standalone.ui.DataDebugDialog.1
            public void keyPressed(KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == 82 && keyEvent.isControlDown()) {
                    try {
                        FileInputStream fileInputStream = new FileInputStream(AOUIFactory.getLoadFiles(SimpleAfirmaMessages.getString("DataDebugDialog.7"), null, null, null, SimpleAfirmaMessages.getString("DataDebugDialog.6"), false, false, null, this)[0]);
                        Throwable th = null;
                        try {
                            try {
                                DataDebugDialog.this.setData(AOUtil.getDataFromInputStream(fileInputStream));
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
                                DataDebugDialog.fillDisplay(jTextArea, DataDebugDialog.this.getData());
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
                    } catch (IOException e) {
                        DataDebugDialog.LOGGER.warning("Ha fallado la carga de los datos a firmar: " + e);
                        jTextArea.setText((String) null);
                        jTextArea.setText(SimpleAfirmaMessages.getString("DataDebugDialog.5"));
                    }
                }
            }
        });
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BorderLayout());
        jPanel.setSize(800, 600);
        jPanel.add(jScrollPane, "Center");
        jPanel.add(jButton, "Last");
        setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        setTitle(SimpleAfirmaMessages.getString("DataDebugDialog.4"));
        setSize(800, 600);
        setResizable(true);
        Point centerPoint = GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint();
        setLocation(centerPoint.x - 400, centerPoint.y - 300);
        add(jPanel);
        setVisible(true);
    }

    static void fillDisplay(JTextArea jTextArea, byte[] bArr) {
        String string;
        String mimeType;
        jTextArea.setText((String) null);
        MimeHelper mimeHelper = new MimeHelper(bArr);
        try {
            string = mimeHelper.getDescription();
        } catch (IOException e) {
            LOGGER.warning("No se ha podido determinar el tipo de los datos a firmar, se usara binario: " + e);
            string = SimpleAfirmaMessages.getString("DataDebugDialog.0");
        }
        jTextArea.append(SimpleAfirmaMessages.getString("DataDebugDialog.1", string) + '\n');
        jTextArea.append(SimpleAfirmaMessages.getString("DataDebugDialog.2", Integer.toString(bArr.length)) + '\n');
        try {
            mimeType = mimeHelper.getMimeType();
        } catch (IOException e2) {
            LOGGER.warning("No se ha podido determinar el tipo de los datos a firmar, se mostraran en Base64: " + e2);
            mimeType = "application/octet-stream";
        }
        jTextArea.append(SimpleAfirmaMessages.getString("DataDebugDialog.3") + '\n');
        if (mimeType.contains("text") || mimeType.contains("xml")) {
            jTextArea.append(new String(bArr));
        } else {
            jTextArea.append(new String(Base64.getMimeEncoder().encode(bArr)));
        }
    }

    public byte[] getData() {
        return this.data;
    }

    void setData(byte[] bArr) {
        this.data = bArr;
    }
}
