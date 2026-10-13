package javax.help;

import java.net.URL;
import java.util.Hashtable;
import java.util.Locale;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.help.event.HelpModelListener;
import javax.help.plaf.HelpNavigatorUI;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.UIManager;

/* JADX INFO: loaded from: autofirma.jar:javax/help/JHelpNavigator.class */
public class JHelpNavigator extends JComponent implements Accessible {
    protected HelpModel helpModel;
    protected String type;
    private static String jhPackageName;
    private NavigatorView view;
    private TreeItem[] selectedItems;
    private static final boolean debug = false;

    public JHelpNavigator(NavigatorView navigatorView) {
        this.view = navigatorView;
        if (navigatorView != null) {
            setModel(new DefaultHelpModel(navigatorView.getHelpSet()));
        } else {
            setModel(null);
        }
        updateUI();
    }

    public JHelpNavigator(NavigatorView navigatorView, HelpModel helpModel) {
        this.view = navigatorView;
        setModel(helpModel);
        updateUI();
    }

    public TreeItem[] getSelectedItems() {
        if (this.selectedItems == null) {
            return new TreeItem[0];
        }
        return (TreeItem[]) this.selectedItems.clone();
    }

    public void setSelectedItems(TreeItem[] treeItemArr) {
        TreeItem[] treeItemArr2 = this.selectedItems;
        this.selectedItems = treeItemArr;
        firePropertyChange("SelectedItemsChangedProperty", treeItemArr2, this.selectedItems);
    }

    public String getUIClassID() {
        return "HelpNavigatorUI";
    }

    public boolean canMerge(NavigatorView navigatorView) {
        return false;
    }

    public void merge(NavigatorView navigatorView) {
        throw new UnsupportedOperationException();
    }

    public void remove(NavigatorView navigatorView) {
        throw new UnsupportedOperationException();
    }

    public String getNavigatorName() {
        return this.view.getName();
    }

    public NavigatorView getNavigatorView() {
        return this.view;
    }

    public String getNavigatorLabel() {
        return this.view.getLabel();
    }

    public String getNavigatorLabel(Locale locale) {
        return this.view.getLabel(locale);
    }

    public Icon getIcon() {
        return getUI().getIcon();
    }

    public void setModel(HelpModel helpModel) {
        HelpModel helpModel2 = this.helpModel;
        if (helpModel != helpModel2) {
            this.helpModel = helpModel;
            firePropertyChange("helpModel", helpModel2, this.helpModel);
            invalidate();
        }
    }

    public HelpModel getModel() {
        return this.helpModel;
    }

    public void setUI(HelpNavigatorUI helpNavigatorUI) {
        if (((HelpNavigatorUI) this.ui) != helpNavigatorUI) {
            super.setUI(helpNavigatorUI);
        }
    }

    public HelpNavigatorUI getUI() {
        return (HelpNavigatorUI) this.ui;
    }

    public void updateUI() {
        SwingHelpUtilities.installUIDefaults();
        setUI((HelpNavigatorUI) UIManager.getUI(this));
        invalidate();
    }

    static {
        SwingHelpUtilities.installLookAndFeelDefaults();
    }

    public void addHelpModelListener(HelpModelListener helpModelListener) {
        getModel().addHelpModelListener(helpModelListener);
    }

    public void removeHelpModelListener(HelpModelListener helpModelListener) {
        getModel().removeHelpModelListener(helpModelListener);
    }

    protected static Hashtable createParams(URL url) {
        Hashtable hashtable = new Hashtable();
        hashtable.put("data", url.toString());
        return hashtable;
    }

    private static void debug(String str) {
    }

    public AccessibleContext getAccessibleContext() {
        if (this.accessibleContext == null) {
            this.accessibleContext = new AccessibleJHelpNavigator(this);
        }
        return this.accessibleContext;
    }

    /* JADX INFO: loaded from: autofirma.jar:javax/help/JHelpNavigator$AccessibleJHelpNavigator.class */
    protected class AccessibleJHelpNavigator extends JComponent.AccessibleJComponent {
        private final JHelpNavigator this$0;

        protected AccessibleJHelpNavigator(JHelpNavigator jHelpNavigator) {
            super(jHelpNavigator);
            this.this$0 = jHelpNavigator;
        }

        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.PANEL;
        }
    }
}
