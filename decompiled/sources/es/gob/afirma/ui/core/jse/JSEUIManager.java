package es.gob.afirma.ui.core.jse;

import es.gob.afirma.core.AOCancelledOperationException;
import es.gob.afirma.core.ui.AOUIManager;
import es.gob.afirma.core.ui.GenericFileFilter;
import es.gob.afirma.core.ui.KeyStoreDialogManager;
import es.gob.afirma.ui.core.jse.errors.ErrorManagementDialog;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.accessibility.Accessible;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/ui/core/jse/JSEUIManager.class */
public class JSEUIManager implements AOUIManager {
    private static final int ASCII_LOWER_INDEX = 32;
    private static final int ASCII_HIGHER_INDEX = 126;
    public static final String PREFERENCE_DIRECTORY = "currentDir";
    protected static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private static final Preferences PREFERENCES = Preferences.userNodeForPackage(JSEUIManager.class);

    public static String get(String str, String str2) {
        return PREFERENCES.get(str, str2);
    }

    public static void put(String str, String str2) {
        PREFERENCES.put(str, str2);
    }

    @Override // es.gob.afirma.core.ui.AOUIManager
    public final char[] getPassword(String str, Object obj) {
        return getPassword(str, null, null, false, obj);
    }

    @Override // es.gob.afirma.core.ui.AOUIManager
    public final char[] getPassword(String str, Object obj, String str2, boolean z, Object obj2) {
        final JPasswordField jPasswordField = new JPasswordField(10);
        if (str2 != null) {
            jPasswordField.setDocument(new JTextFieldFilter(str2, z));
        }
        JLabel jLabel = new JLabel(str != null ? str : JSEUIMessages.getString("JSEUIManager.24"));
        jLabel.setMinimumSize(new Dimension(jLabel.getFontMetrics(jLabel.getFont()).stringWidth(str), jLabel.getSize().height));
        jLabel.setLabelFor(jPasswordField);
        JPanel jPanel = new JPanel();
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = 2;
        gridBagConstraints.weightx = 1.0d;
        gridBagConstraints.anchor = 10;
        jPanel.setLayout(new GridBagLayout());
        jPanel.add(jLabel, gridBagConstraints);
        gridBagConstraints.gridy = 1;
        jPanel.add(jPasswordField, gridBagConstraints);
        JOptionPane jOptionPane = new JOptionPane(jPanel, 3, 2, obj instanceof Icon ? (Icon) obj : null) { // from class: es.gob.afirma.ui.core.jse.JSEUIManager.1
            private static final long serialVersionUID = -3012522768561175760L;

            public void selectInitialValue() {
                jPasswordField.requestFocusInWindow();
            }
        };
        jOptionPane.createDialog(obj2 instanceof Component ? (Component) obj2 : null, JSEUIMessages.getString("JSEUIManager.24")).setVisible(true);
        Object value = jOptionPane.getValue();
        if (value == null) {
            throw new AOCancelledOperationException("La insercion de contrasena ha sido cancelada por el usuario");
        }
        if (((Integer) value).intValue() == 0) {
            return jPasswordField.getPassword();
        }
        throw new AOCancelledOperationException("La insercion de contrasena ha sido cancelada por el usuario");
    }

    @Override // es.gob.afirma.core.ui.AOUIManager
    public final char[] getDoublePassword(String str, String str2, Object obj, String str3, boolean z, Object obj2) {
        final JPasswordField jPasswordField = new JPasswordField(10);
        if (str3 != null) {
            jPasswordField.setDocument(new JTextFieldFilter(str3, z));
        }
        JLabel jLabel = new JLabel(str != null ? str : JSEUIMessages.getString("JSEUIManager.24"));
        jLabel.setMinimumSize(new Dimension(jLabel.getFontMetrics(jLabel.getFont()).stringWidth(str != null ? str : JSEUIMessages.getString("JSEUIManager.24")), jLabel.getSize().height));
        jLabel.setLabelFor(jPasswordField);
        final JPasswordField jPasswordField2 = new JPasswordField(10);
        if (str3 != null) {
            jPasswordField2.setDocument(new JTextFieldFilter(str3, z));
        }
        JLabel jLabel2 = new JLabel(str2 != null ? str2 : JSEUIMessages.getString("JSEUIManager.2"));
        jLabel2.setMinimumSize(new Dimension(jLabel2.getFontMetrics(jLabel2.getFont()).stringWidth(str2 != null ? str2 : JSEUIMessages.getString("JSEUIManager.2")), jLabel2.getSize().height));
        jLabel2.setLabelFor(jPasswordField2);
        JPanel jPanel = new JPanel();
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = 2;
        gridBagConstraints.weightx = 1.0d;
        gridBagConstraints.anchor = 10;
        jPanel.setLayout(new GridBagLayout());
        gridBagConstraints.gridy = 0;
        jPanel.add(jLabel, gridBagConstraints);
        gridBagConstraints.gridy = 1;
        jPanel.add(jPasswordField, gridBagConstraints);
        gridBagConstraints.gridy = 2;
        jPanel.add(jLabel2, gridBagConstraints);
        gridBagConstraints.gridy = 3;
        jPanel.add(jPasswordField2, gridBagConstraints);
        Icon icon = obj instanceof Icon ? (Icon) obj : null;
        final JButton jButton = new JButton(JSEUIMessages.getString("JSEUIManager.3"));
        jButton.getAccessibleContext().setAccessibleDescription(JSEUIMessages.getString("JSEUIManager.4"));
        jButton.setEnabled(false);
        JButton jButton2 = new JButton(JSEUIMessages.getString("JSEUIManager.5"));
        jButton2.getAccessibleContext().setAccessibleDescription(JSEUIMessages.getString("JSEUIManager.6"));
        DocumentListener documentListener = new DocumentListener() { // from class: es.gob.afirma.ui.core.jse.JSEUIManager.2
            private void something() {
                if (jPasswordField.getPassword() != null && jPasswordField.getPassword().length > 1 && Arrays.equals(jPasswordField.getPassword(), jPasswordField2.getPassword())) {
                    jButton.setEnabled(true);
                } else {
                    jButton.setEnabled(false);
                }
            }

            public void removeUpdate(DocumentEvent documentEvent) {
                something();
            }

            public void insertUpdate(DocumentEvent documentEvent) {
                something();
            }

            public void changedUpdate(DocumentEvent documentEvent) {
                something();
            }
        };
        jPasswordField.getDocument().addDocumentListener(documentListener);
        jPasswordField2.getDocument().addDocumentListener(documentListener);
        final JOptionPane jOptionPane = new JOptionPane(jPanel, 3, 2, icon, new Object[]{jButton, jButton2}, jButton) { // from class: es.gob.afirma.ui.core.jse.JSEUIManager.3
            private static final long serialVersionUID = -3012522768561175760L;

            public void selectInitialValue() {
                jPasswordField.requestFocusInWindow();
            }
        };
        jButton.addActionListener(new ActionListener() { // from class: es.gob.afirma.ui.core.jse.JSEUIManager.4
            public void actionPerformed(ActionEvent actionEvent) {
                jOptionPane.setValue(0);
            }
        });
        jButton2.addActionListener(new ActionListener() { // from class: es.gob.afirma.ui.core.jse.JSEUIManager.5
            public void actionPerformed(ActionEvent actionEvent) {
                jOptionPane.setValue(2);
            }
        });
        jOptionPane.createDialog(obj2 instanceof Component ? (Component) obj2 : null, JSEUIMessages.getString("JSEUIManager.24")).setVisible(true);
        Object value = jOptionPane.getValue();
        if (value == null) {
            return new char[0];
        }
        if (((Integer) value).intValue() == 0) {
            return jPasswordField.getPassword();
        }
        throw new AOCancelledOperationException("La insercion de contrasena ha sido cancelada por el usuario");
    }

    @Override // es.gob.afirma.core.ui.AOUIManager
    public final Object showInputDialog(Object obj, Object obj2, String str, int i, Object obj3, Object[] objArr, Object obj4) {
        Component component = obj instanceof Component ? (Component) obj : null;
        Icon icon = obj3 instanceof Icon ? (Icon) obj3 : null;
        if (objArr == null) {
            return JOptionPane.showInputDialog(component, obj2, str, i);
        }
        return JOptionPane.showInputDialog(component, obj2, str, i, icon, objArr, obj4);
    }

    @Override // es.gob.afirma.core.ui.AOUIManager
    public String showCertificateSelectionDialog(Object obj, KeyStoreDialogManager keyStoreDialogManager) {
        Component component = obj instanceof Component ? (Component) obj : null;
        try {
            Class<?> cls = Class.forName("es.gob.afirma.ui.core.jse.certificateselection.CertificateSelectionDialog");
            Object objInvoke = cls.getMethod("showDialog", new Class[0]).invoke(cls.getConstructor(Component.class, KeyStoreDialogManager.class).newInstance(component, keyStoreDialogManager), new Object[0]);
            if (objInvoke instanceof String) {
                return (String) objInvoke;
            }
            return null;
        } catch (InvocationTargetException e) {
            LOGGER.severe("Se genero un error en el dialogo de seleccion de certificados: " + e);
            if (e.getCause() instanceof RuntimeException) {
                throw ((RuntimeException) e.getCause());
            }
            throw new IllegalStateException("No se encontraron certificados en el almacen o se produjo un error durante la extraccion del certificado seleccionado: " + e, e.getCause());
        } catch (Exception e2) {
            LOGGER.severe("No se encuentra disponible el proyecto del interfaz grafico del dialogo de seleccion: " + e2);
            throw new IllegalStateException("No se encuentra disponible el proyecto del interfaz grafico del dialogo de seleccion u ocurrio un error durante su ejecucion: " + e2, e2);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/ui/core/jse/JSEUIManager$JTextFieldFilter.class */
    private static final class JTextFieldFilter extends PlainDocument {
        private static final long serialVersionUID = -5746396042117084830L;
        private String acceptedChars;
        private boolean beep;

        JTextFieldFilter(String str, boolean z) {
            this.acceptedChars = null;
            this.beep = false;
            this.beep = z;
            this.acceptedChars = str;
        }

        public void insertString(int i, String str, AttributeSet attributeSet) throws BadLocationException {
            if (str == null) {
                return;
            }
            for (int i2 = 0; i2 < str.length(); i2++) {
                if (this.acceptedChars.indexOf(String.valueOf(str.charAt(i2))) == -1) {
                    if (this.beep) {
                        Toolkit.getDefaultToolkit().beep();
                        return;
                    }
                    return;
                }
            }
            super.insertString(i, str, attributeSet);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/ui/core/jse/JSEUIManager$JTextFieldASCIIFilter.class */
    public static final class JTextFieldASCIIFilter extends PlainDocument {
        private static final long serialVersionUID = 1979726487852842735L;
        private boolean beep;

        public JTextFieldASCIIFilter(boolean z) {
            this.beep = false;
            this.beep = z;
        }

        public void insertString(int i, String str, AttributeSet attributeSet) throws BadLocationException {
            if (str == null) {
                return;
            }
            for (int i2 = 0; i2 < str.length(); i2++) {
                if (str.charAt(i2) < ' ' || str.charAt(i2) > '~') {
                    if (this.beep) {
                        Toolkit.getDefaultToolkit().beep();
                        return;
                    }
                    return;
                }
            }
            super.insertString(i, str, attributeSet);
        }
    }

    @Override // es.gob.afirma.core.ui.AOUIManager
    public final int showConfirmDialog(Object obj, Object obj2, String str, int i, int i2) {
        JOptionPane jOptionPane = new JOptionPane(obj2, i2, i);
        JDialog jDialogCreateDialog = jOptionPane.createDialog(obj instanceof Component ? (Component) obj : null, str);
        if (obj2 instanceof Accessible) {
            jDialogCreateDialog.getAccessibleContext().setAccessibleDescription(((Accessible) obj2).getAccessibleContext().getAccessibleDescription());
        } else if (obj2 instanceof String) {
            jDialogCreateDialog.getAccessibleContext().setAccessibleDescription((String) obj2);
        }
        jDialogCreateDialog.setVisible(true);
        if (jOptionPane.getValue() == null) {
            return 2;
        }
        return ((Integer) jOptionPane.getValue()).intValue();
    }

    @Override // es.gob.afirma.core.ui.AOUIManager
    public void showMessageDialog(Object obj, Object obj2, String str, int i) {
        JDialog jDialogCreateDialog = new JOptionPane(obj2, i).createDialog(obj instanceof Component ? (Component) obj : null, str);
        if (obj2 instanceof Accessible) {
            jDialogCreateDialog.getAccessibleContext().setAccessibleDescription(((Accessible) obj2).getAccessibleContext().getAccessibleDescription());
        } else if (obj2 instanceof String) {
            jDialogCreateDialog.getAccessibleContext().setAccessibleDescription((String) obj2);
        }
        jDialogCreateDialog.setVisible(true);
    }

    @Override // es.gob.afirma.core.ui.AOUIManager
    public void showMessageDialog(Object obj, Object obj2, String str, int i, Object obj3) {
        JDialog jDialogCreateDialog = new JOptionPane(obj2, i, -1, obj3 instanceof Icon ? (Icon) obj3 : null).createDialog(obj instanceof Component ? (Component) obj : null, str);
        if (obj2 instanceof JPanel) {
            jDialogCreateDialog.getAccessibleContext().setAccessibleDescription(((JPanel) obj2).getAccessibleContext().getAccessibleDescription());
        } else {
            jDialogCreateDialog.getAccessibleContext().setAccessibleDescription((String) obj2);
        }
        jDialogCreateDialog.setVisible(true);
    }

    @Override // es.gob.afirma.core.ui.AOUIManager
    public final int getPlainMessageCode() {
        return -1;
    }

    @Override // es.gob.afirma.core.ui.AOUIManager
    public final int getYesNoOptionCode() {
        return 0;
    }

    @Override // es.gob.afirma.core.ui.AOUIManager
    public final int getWarningMessageCode() {
        return 2;
    }

    @Override // es.gob.afirma.core.ui.AOUIManager
    public final int getYesOptionCode() {
        return 0;
    }

    @Override // es.gob.afirma.core.ui.AOUIManager
    public final int getNoOptionCode() {
        return 1;
    }

    @Override // es.gob.afirma.core.ui.AOUIManager
    public final int getOkCancelOptionCode() {
        return 2;
    }

    @Override // es.gob.afirma.core.ui.AOUIManager
    public final int getOkOptionCode() {
        return 0;
    }

    @Override // es.gob.afirma.core.ui.AOUIManager
    public final int getInformationMessageCode() {
        return 1;
    }

    @Override // es.gob.afirma.core.ui.AOUIManager
    public final int getErrorMessageCode() {
        return 0;
    }

    @Override // es.gob.afirma.core.ui.AOUIManager
    public final int getQuestionMessageCode() {
        return 3;
    }

    @Override // es.gob.afirma.core.ui.AOUIManager
    public File[] getLoadFiles(String str, String str2, String str3, String[] strArr, String str4, boolean z, boolean z2, final Object obj, Object obj2) {
        JFileChooser jFileChooser;
        File[] selectedFiles;
        Component component = obj2 instanceof Component ? (Component) obj2 : null;
        if (obj instanceof Image) {
            jFileChooser = new JFileChooser() { // from class: es.gob.afirma.ui.core.jse.JSEUIManager.6
                private static final long serialVersionUID = 5631612687512882773L;

                protected JDialog createDialog(Component component2) {
                    JDialog jDialogCreateDialog = super.createDialog(component2);
                    jDialogCreateDialog.setIconImage((Image) obj);
                    return jDialogCreateDialog;
                }
            };
        } else {
            if (obj != null) {
                LOGGER.warning("Se ha proporcionado un icono del tipo " + obj.getClass().getName() + ", pero solo se admite Image");
            }
            jFileChooser = new JFileChooser();
        }
        if (z) {
            jFileChooser.setFileSelectionMode(1);
        }
        configureDefaultDir(jFileChooser, str2, str3);
        jFileChooser.setMultiSelectionEnabled(z2);
        if (str != null && str.length() > 0) {
            jFileChooser.setDialogTitle(str);
        }
        if (strArr != null && strArr.length > 0) {
            jFileChooser.setFileFilter(new FileNameExtensionFilter(str4, strArr));
        }
        if (jFileChooser.showOpenDialog(component) == 0) {
            if (z2) {
                selectedFiles = jFileChooser.getSelectedFiles();
            } else {
                selectedFiles = new File[]{jFileChooser.getSelectedFile()};
            }
            put(PREFERENCE_DIRECTORY, jFileChooser.getCurrentDirectory().getPath());
            return selectedFiles;
        }
        throw new AOCancelledOperationException();
    }

    @Override // es.gob.afirma.core.ui.AOUIManager
    public File saveDataToFile(byte[] bArr, String str, String str2, String str3, List<GenericFileFilter> list, Object obj) throws IOException {
        return saveDataToFile(bArr, str, str2, str3, list, null, obj);
    }

    /* JADX WARN: Code duplicated, block: B:100:0x027a A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:101:0x01a4 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:102:0x01c0 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:103:0x01ba A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:104:0x026a A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:105:0x0234 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:106:0x01fb A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:116:0x008a A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:11:0x0052  */
    /* JADX WARN: Code duplicated, block: B:121:0x006c A[ADDED_TO_REGION, REMOVE, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:14:0x005d  */
    /* JADX WARN: Code duplicated, block: B:17:0x0076  */
    /* JADX WARN: Code duplicated, block: B:31:0x0100  */
    /* JADX WARN: Code duplicated, block: B:33:0x010f  */
    /* JADX WARN: Code duplicated, block: B:48:0x0185  */
    /* JADX WARN: Code duplicated, block: B:60:0x01e2  */
    /* JADX WARN: Code duplicated, block: B:90:0x01e7 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:96:0x01c4 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:99:0x00f8 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:9:0x001e  */
    /*  JADX ERROR: JadxOverflowException in pass: RegionMakerVisitor
        jadx.core.utils.exceptions.JadxOverflowException: Regions count limit reached at block B:60:0x01e2
        	at jadx.core.utils.ErrorsCounter.addError(ErrorsCounter.java:59)
        	at jadx.core.utils.ErrorsCounter.error(ErrorsCounter.java:31)
        	at jadx.core.dex.attributes.nodes.NotificationAttrNode.addError(NotificationAttrNode.java:19)
        */
    @Override // es.gob.afirma.core.ui.AOUIManager
    public java.io.File saveDataToFile(byte[] r8, java.lang.String r9, java.lang.String r10, java.lang.String r11, java.util.List<es.gob.afirma.core.ui.GenericFileFilter> r12, es.gob.afirma.core.ui.GenericFileFilter r13, java.lang.Object r14) throws java.io.IOException {
        /*
            Method dump skipped, instruction units count: 668
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: es.gob.afirma.ui.core.jse.JSEUIManager.saveDataToFile(byte[], java.lang.String, java.lang.String, java.lang.String, java.util.List, es.gob.afirma.core.ui.GenericFileFilter, java.lang.Object):java.io.File");
    }

    private static void configureDefaultDir(JFileChooser jFileChooser, String str, String str2) {
        try {
            if (str2 != null && str != null) {
                jFileChooser.setSelectedFile(new File(str, str2));
            } else {
                String str3 = str != null ? str : get(PREFERENCE_DIRECTORY, null);
                if (str2 != null) {
                    if (str3 != null) {
                        jFileChooser.setSelectedFile(new File(str3, str2));
                    } else {
                        jFileChooser.setSelectedFile(new File(str2));
                    }
                } else if (str3 != null) {
                    jFileChooser.setCurrentDirectory(new File(str3));
                }
            }
        } catch (Exception e) {
            LOGGER.warning("No se pudo seleccionar el directorio por defecto para el guardado: " + e);
            if (str2 != null) {
                try {
                    jFileChooser.setSelectedFile(new File(str2));
                } catch (Exception e2) {
                    LOGGER.warning("No se pudo seleccionar nombre de fichero a secas en el dialogo de guardado: " + e2);
                }
            }
        }
    }

    @Override // es.gob.afirma.core.ui.AOUIManager
    public void showErrorMessage(Object obj, String str, int i, Throwable th) {
        JFrame jFrame = new JFrame("Error");
        jFrame.setBounds(100, 20, 900, 900);
        ErrorManagementDialog.show((Frame) jFrame, true, obj, str, i, th);
    }

    @Override // es.gob.afirma.core.ui.AOUIManager
    public void showErrorMessage(Object obj, Object obj2, String str, int i, Throwable th) {
        if (obj instanceof JDialog) {
            ErrorManagementDialog.show((JDialog) obj, true, obj2, str, i, th);
        } else if (obj instanceof Frame) {
            ErrorManagementDialog.show((Frame) obj, true, obj2, str, i, th);
        } else {
            ErrorManagementDialog.show((Frame) null, true, obj2, str, i, th);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/ui/core/jse/JSEUIManager$CustomFileChooserForSave.class */
    private static final class CustomFileChooserForSave extends JFileChooser {
        private static final long serialVersionUID = -2107199679670180110L;
        private File file = null;

        File getFile() {
            return this.file;
        }

        CustomFileChooserForSave() {
            addPropertyChangeListener("fileFilterChanged", new PropertyChangeListener() { // from class: es.gob.afirma.ui.core.jse.JSEUIManager.CustomFileChooserForSave.1
                @Override // java.beans.PropertyChangeListener
                public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                    if (!(propertyChangeEvent.getOldValue() instanceof FileNameExtensionFilter) || !(propertyChangeEvent.getNewValue() instanceof FileNameExtensionFilter)) {
                        return;
                    }
                    FileNameExtensionFilter fileNameExtensionFilter = (FileNameExtensionFilter) propertyChangeEvent.getOldValue();
                    FileNameExtensionFilter fileNameExtensionFilter2 = (FileNameExtensionFilter) propertyChangeEvent.getNewValue();
                    if (fileNameExtensionFilter.getExtensions() == null || fileNameExtensionFilter.getExtensions().length < 1 || fileNameExtensionFilter2.getExtensions() == null || fileNameExtensionFilter2.getExtensions().length < 1) {
                        return;
                    }
                    String str = fileNameExtensionFilter.getExtensions()[0];
                    String str2 = fileNameExtensionFilter2.getExtensions()[0];
                    String name = CustomFileChooserForSave.this.getFile().getName();
                    if (name.endsWith(str)) {
                        name = name.replace(str, str2);
                    } else if (!name.endsWith(str2)) {
                        name = name + str2;
                    }
                    CustomFileChooserForSave.this.setSelectedFile(new File(name));
                }
            });
        }

        public void setSelectedFile(File file) {
            super.setSelectedFile(file);
            this.file = file;
        }
    }
}
