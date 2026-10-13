package javax.help;

import java.net.URL;
import java.util.Hashtable;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.help.event.HelpModelListener;
import javax.help.event.TextHelpModelListener;
import javax.help.plaf.HelpContentViewerUI;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.UIManager;
import javax.swing.text.EditorKit;

/* JADX INFO: loaded from: autofirma.jar:javax/help/JHelpContentViewer.class */
public class JHelpContentViewer extends JComponent implements Accessible {
    protected TextHelpModel model;
    private Hashtable kitRegistry;
    private boolean synch = true;
    private boolean debug = false;

    public JHelpContentViewer(HelpSet helpSet) {
        setModel(new DefaultHelpModel(helpSet));
        updateUI();
    }

    public JHelpContentViewer() {
        setModel(new DefaultHelpModel(null));
        updateUI();
    }

    public JHelpContentViewer(TextHelpModel textHelpModel) {
        setModel(textHelpModel);
        updateUI();
    }

    public void setModel(TextHelpModel textHelpModel) {
        debug(new StringBuffer().append("setModel: ").append(textHelpModel).toString());
        TextHelpModel textHelpModel2 = this.model;
        if (textHelpModel != textHelpModel2) {
            this.model = textHelpModel;
            firePropertyChange("helpModel", textHelpModel2, this.model);
            invalidate();
        }
    }

    public TextHelpModel getModel() {
        return this.model;
    }

    public void setUI(HelpContentViewerUI helpContentViewerUI) {
        debug("setUI");
        if (((HelpContentViewerUI) this.ui) != helpContentViewerUI) {
            super.setUI(helpContentViewerUI);
            repaint();
        }
    }

    public HelpContentViewerUI getUI() {
        return (HelpContentViewerUI) this.ui;
    }

    public void updateUI() {
        SwingHelpUtilities.installUIDefaults();
        setUI((HelpContentViewerUI) UIManager.getUI(this));
        invalidate();
    }

    public String getUIClassID() {
        return "HelpContentViewerUI";
    }

    static {
        SwingHelpUtilities.installLookAndFeelDefaults();
    }

    public void setCurrentID(Map.ID id) throws InvalidHelpSetContextException {
        this.model.setCurrentID(id);
    }

    public void setCurrentID(String str) throws BadIDException {
        try {
            this.model.setCurrentID(Map.ID.create(str, getModel().getHelpSet()));
        } catch (InvalidHelpSetContextException e) {
        }
    }

    public void setCurrentURL(URL url) {
        this.model.setCurrentURL(url);
    }

    public URL getCurrentURL() {
        return this.model.getCurrentURL();
    }

    public String getDocumentTitle() {
        return this.model.getDocumentTitle();
    }

    public void addHighlight(int i, int i2) {
        this.model.addHighlight(i, i2);
    }

    public void removeAllHighlights() {
        this.model.removeAllHighlights();
    }

    public void setSynch(boolean z) {
        this.synch = z;
    }

    public boolean getSynch() {
        return this.synch;
    }

    public EditorKit createEditorKitForContentType(String str) {
        Class<?> cls;
        EditorKit editorKit = null;
        if (this.kitRegistry == null) {
            this.kitRegistry = new Hashtable();
        } else {
            editorKit = (EditorKit) this.kitRegistry.get(str);
        }
        if (editorKit == null) {
            HelpSet helpSet = this.model.getHelpSet();
            String str2 = (String) helpSet.getKeyData(HelpSet.kitTypeRegistry, str);
            if (str2 == null) {
                return null;
            }
            ClassLoader loader = (ClassLoader) helpSet.getKeyData(HelpSet.kitLoaderRegistry, str);
            if (loader == null) {
                loader = helpSet.getLoader();
            }
            try {
                if (loader != null) {
                    cls = loader.loadClass(str2);
                } else {
                    cls = Class.forName(str2);
                }
                editorKit = (EditorKit) cls.newInstance();
                this.kitRegistry.put(str, editorKit);
            } catch (Throwable th) {
                th.printStackTrace();
                editorKit = null;
            }
        }
        if (editorKit != null) {
            return (EditorKit) editorKit.clone();
        }
        return JEditorPane.createEditorKitForContentType(str);
    }

    public void addTextHelpModelListener(TextHelpModelListener textHelpModelListener) {
        getModel().addTextHelpModelListener(textHelpModelListener);
    }

    public void removeHelpModelListener(TextHelpModelListener textHelpModelListener) {
        getModel().removeTextHelpModelListener(textHelpModelListener);
    }

    public void addHelpModelListener(HelpModelListener helpModelListener) {
        getModel().addHelpModelListener(helpModelListener);
    }

    public void removeHelpModelListener(HelpModelListener helpModelListener) {
        getModel().removeHelpModelListener(helpModelListener);
    }

    public void clear() {
        firePropertyChange("clear", " ", "xyz");
    }

    public void reload() {
        firePropertyChange("reload", " ", "xyz");
    }

    private void debug(String str) {
        if (this.debug) {
            System.err.println(new StringBuffer().append("JHelpContentViewer: ").append(str).toString());
        }
    }

    public AccessibleContext getAccessibleContext() {
        if (this.accessibleContext == null) {
            this.accessibleContext = new AccessibleJHelpContentViewer(this);
        }
        return this.accessibleContext;
    }

    /* JADX INFO: loaded from: autofirma.jar:javax/help/JHelpContentViewer$AccessibleJHelpContentViewer.class */
    protected class AccessibleJHelpContentViewer extends JComponent.AccessibleJComponent {
        private final JHelpContentViewer this$0;

        protected AccessibleJHelpContentViewer(JHelpContentViewer jHelpContentViewer) {
            super(jHelpContentViewer);
            this.this$0 = jHelpContentViewer;
        }

        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.PANEL;
        }
    }
}
