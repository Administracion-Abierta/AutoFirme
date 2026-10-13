package javax.help;

import java.net.URL;
import java.util.Enumeration;
import java.util.Vector;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.help.event.HelpSetEvent;
import javax.help.event.HelpSetListener;
import javax.help.plaf.HelpUI;
import javax.swing.JComponent;
import javax.swing.UIManager;

/* JADX INFO: loaded from: autofirma.jar:javax/help/JHelp.class */
public class JHelp extends JComponent implements HelpSetListener, Accessible {
    protected TextHelpModel helpModel;
    protected HelpHistoryModel historyModel;
    protected Vector navigators;
    protected boolean navDisplayed;
    protected boolean toolbarDisplayed;
    protected HelpSet.Presentation hsPres;
    protected JHelpContentViewer contentViewer;
    private boolean debug;

    public JHelp(HelpSet helpSet) {
        this(new DefaultHelpModel(helpSet), null, helpSet == null ? null : helpSet.getDefaultPresentation());
    }

    public JHelp() {
        this((TextHelpModel) null);
    }

    public JHelp(TextHelpModel textHelpModel) {
        this(textHelpModel, null, null);
    }

    public JHelp(TextHelpModel textHelpModel, HelpHistoryModel helpHistoryModel, HelpSet.Presentation presentation) {
        this.navDisplayed = true;
        this.toolbarDisplayed = true;
        this.debug = false;
        if (helpHistoryModel == null) {
            this.historyModel = new DefaultHelpHistoryModel(this);
        } else {
            this.historyModel = helpHistoryModel;
        }
        this.hsPres = presentation;
        this.navigators = new Vector();
        this.navDisplayed = true;
        this.contentViewer = new JHelpContentViewer(textHelpModel);
        setModel(textHelpModel);
        if (textHelpModel != null) {
            setupNavigators();
        }
        updateUI();
    }

    protected void setupNavigators() {
        HelpSet helpSet = this.helpModel.getHelpSet();
        if (helpSet == null) {
            return;
        }
        NavigatorView[] navigatorViews = helpSet.getNavigatorViews();
        debug(new StringBuffer().append("views: ").append(navigatorViews).toString());
        for (int i = 0; i < navigatorViews.length; i++) {
            debug(new StringBuffer().append("  processing info: ").append(navigatorViews[i]).toString());
            JHelpNavigator jHelpNavigatorCreateNavigator = navigatorViews[i].createNavigator(this.helpModel);
            if (jHelpNavigatorCreateNavigator == null) {
                debug("no JHelpNavigator for given info");
            } else {
                debug("  adding the navigator");
                this.navigators.addElement(jHelpNavigatorCreateNavigator);
            }
        }
    }

    public void setModel(TextHelpModel textHelpModel) {
        HelpSet helpSet;
        TextHelpModel textHelpModel2 = this.helpModel;
        if (textHelpModel != textHelpModel2) {
            if (textHelpModel2 != null) {
                textHelpModel2.getHelpSet().removeHelpSetListener(this);
            }
            this.helpModel = textHelpModel;
            if (textHelpModel != null && (helpSet = textHelpModel.getHelpSet()) != null) {
                helpSet.addHelpSetListener(this);
            }
            firePropertyChange("helpModel", textHelpModel2, this.helpModel);
            this.contentViewer.setModel(textHelpModel);
            getHistoryModel().setHelpModel(textHelpModel);
            HelpUI ui = getUI();
            if (ui == null) {
                return;
            }
            Enumeration helpNavigators = getHelpNavigators();
            while (helpNavigators.hasMoreElements()) {
                ui.removeNavigator((JHelpNavigator) helpNavigators.nextElement());
            }
            this.navigators.removeAllElements();
            setupNavigators();
            updateUI();
        }
    }

    public TextHelpModel getModel() {
        return this.helpModel;
    }

    public HelpHistoryModel getHistoryModel() {
        return this.historyModel;
    }

    public void setHelpSetPresentation(HelpSet.Presentation presentation) {
        this.hsPres = presentation;
    }

    public HelpSet.Presentation getHelpSetPresentation() {
        return this.hsPres;
    }

    public void setHelpSetSpec(String str) {
        HelpSet helpSet;
        try {
            helpSet = new HelpSet(getClass().getClassLoader(), new URL(str));
        } catch (Exception e) {
            System.err.println(new StringBuffer().append("Trouble setting HelpSetSpec to spec |").append(str).append("|").toString());
            System.err.println(new StringBuffer().append("  ex: ").append(e).toString());
            helpSet = null;
        }
        this.contentViewer.setModel(new DefaultHelpModel(helpSet));
        setModel(this.contentViewer.getModel());
        updateUI();
    }

    public URL getHelpSetURL() {
        HelpSet helpSet = this.contentViewer.getModel().getHelpSet();
        if (helpSet == null) {
            return null;
        }
        return helpSet.getHelpSetURL();
    }

    public void setUI(HelpUI helpUI) {
        if (((HelpUI) this.ui) != helpUI) {
            super.setUI(helpUI);
        }
    }

    public HelpUI getUI() {
        return (HelpUI) this.ui;
    }

    public void updateUI() {
        SwingHelpUtilities.installUIDefaults();
        setUI((HelpUI) UIManager.getUI(this));
        invalidate();
    }

    public String getUIClassID() {
        return "HelpUI";
    }

    private JHelpNavigator findNavigator(String str) {
        debug(new StringBuffer().append("findNavigator(").append(str).append(")").toString());
        Enumeration helpNavigators = getHelpNavigators();
        while (helpNavigators.hasMoreElements()) {
            JHelpNavigator jHelpNavigator = (JHelpNavigator) helpNavigators.nextElement();
            debug(new StringBuffer().append("  nav: ").append(jHelpNavigator).toString());
            debug(new StringBuffer().append("  nav.getName: ").append(jHelpNavigator.getNavigatorName()).toString());
            if (jHelpNavigator.getNavigatorName().equals(str)) {
                return jHelpNavigator;
            }
        }
        return null;
    }

    @Override // javax.help.event.HelpSetListener
    public void helpSetAdded(HelpSetEvent helpSetEvent) {
        debug(new StringBuffer().append("helpSetAdded(").append(helpSetEvent).append(")").toString());
        addHelpSet(helpSetEvent.getHelpSet());
    }

    private void addHelpSet(HelpSet helpSet) {
        debug(new StringBuffer().append("helpset :").append(helpSet).toString());
        NavigatorView[] navigatorViews = helpSet.getNavigatorViews();
        int i = 0;
        Enumeration helpNavigators = getHelpNavigators();
        while (helpNavigators.hasMoreElements()) {
            i++;
            helpNavigators.nextElement();
        }
        if (i == 0) {
            debug("master helpset without navigators");
            setModel(new DefaultHelpModel(helpSet));
            setupNavigators();
            return;
        }
        for (int i2 = 0; i2 < navigatorViews.length; i2++) {
            String name = navigatorViews[i2].getName();
            debug(new StringBuffer().append("addHelpSet: looking for navigator for ").append(name).toString());
            JHelpNavigator jHelpNavigatorFindNavigator = findNavigator(name);
            if (jHelpNavigatorFindNavigator != null) {
                debug("   found");
                if (jHelpNavigatorFindNavigator.canMerge(navigatorViews[i2])) {
                    debug("  canMerge: true; merging...");
                    jHelpNavigatorFindNavigator.merge(navigatorViews[i2]);
                } else {
                    debug("  canMerge: false");
                }
            } else {
                debug("   not found");
            }
        }
    }

    @Override // javax.help.event.HelpSetListener
    public void helpSetRemoved(HelpSetEvent helpSetEvent) {
        debug(new StringBuffer().append("helpSetRemoved(").append(helpSetEvent).append(")").toString());
        removeHelpSet(helpSetEvent.getHelpSet());
    }

    private void removeHelpSet(HelpSet helpSet) {
        NavigatorView[] navigatorViews = helpSet.getNavigatorViews();
        for (int i = 0; i < navigatorViews.length; i++) {
            String name = navigatorViews[i].getName();
            debug(new StringBuffer().append("removeHelpSet: looking for navigator for ").append(name).toString());
            JHelpNavigator jHelpNavigatorFindNavigator = findNavigator(name);
            if (jHelpNavigatorFindNavigator != null) {
                debug("   found");
                if (jHelpNavigatorFindNavigator.canMerge(navigatorViews[i])) {
                    debug("  canMerge: true; removing...");
                    jHelpNavigatorFindNavigator.remove(navigatorViews[i]);
                } else {
                    debug("  canMerge: false");
                }
            } else {
                debug("   not found");
            }
        }
        getHistoryModel().removeHelpSet(helpSet);
    }

    public void setCurrentID(Map.ID id) throws InvalidHelpSetContextException {
        if (this.helpModel != null) {
            this.helpModel.setCurrentID(id);
        }
    }

    public void setCurrentID(Map.ID id, String str, JHelpNavigator jHelpNavigator) throws InvalidHelpSetContextException {
        if (this.helpModel != null) {
            this.helpModel.setCurrentID(id, str, jHelpNavigator);
        }
    }

    public void setCurrentID(String str) throws BadIDException {
        try {
            this.helpModel.setCurrentID(Map.ID.create(str, getModel().getHelpSet()));
        } catch (InvalidHelpSetContextException e) {
        }
    }

    public void setCurrentURL(URL url) {
        this.helpModel.setCurrentURL(url);
    }

    public void setCurrentURL(URL url, String str, JHelpNavigator jHelpNavigator) {
        this.helpModel.setCurrentURL(url, str, jHelpNavigator);
    }

    public TreeItem[] getSelectedItems() {
        return getCurrentNavigator().getSelectedItems();
    }

    public void addHelpNavigator(JHelpNavigator jHelpNavigator) {
        debug(new StringBuffer().append("addHelpNavigator(").append(jHelpNavigator).append(")").toString());
        this.navigators.addElement(jHelpNavigator);
        getUI().addNavigator(jHelpNavigator);
        jHelpNavigator.setModel(getModel());
    }

    public void removeHelpNavigator(JHelpNavigator jHelpNavigator) {
        debug(new StringBuffer().append("removeHelpNavigator(").append(jHelpNavigator).append(")").toString());
        if (jHelpNavigator == null) {
            throw new NullPointerException("navigator");
        }
        this.navigators.removeElement(jHelpNavigator);
        getUI().removeNavigator(jHelpNavigator);
    }

    public Enumeration getHelpNavigators() {
        return this.navigators.elements();
    }

    public void setCurrentNavigator(JHelpNavigator jHelpNavigator) {
        getUI().setCurrentNavigator(jHelpNavigator);
    }

    public JHelpNavigator getCurrentNavigator() {
        return getUI().getCurrentNavigator();
    }

    public void setNavigatorDisplayed(boolean z) {
        if (this.navDisplayed != z) {
            this.navDisplayed = z;
            firePropertyChange("navigatorDisplayed", !z, z);
        }
    }

    public boolean isNavigatorDisplayed() {
        return this.navDisplayed;
    }

    public void setToolbarDisplayed(boolean z) {
        if (this.toolbarDisplayed != z) {
            this.toolbarDisplayed = z;
            firePropertyChange("toolbarDisplayed", !z, z);
        }
    }

    public boolean isToolbarDisplayed() {
        return this.toolbarDisplayed;
    }

    public JHelpContentViewer getContentViewer() {
        return this.contentViewer;
    }

    private void debug(String str) {
        if (this.debug) {
            System.err.println(new StringBuffer().append("JHelp: ").append(str).toString());
        }
    }

    static {
        SwingHelpUtilities.installLookAndFeelDefaults();
    }

    public AccessibleContext getAccessibleContext() {
        if (this.accessibleContext == null) {
            this.accessibleContext = new AccessibleJHelp(this);
        }
        return this.accessibleContext;
    }

    /* JADX INFO: loaded from: autofirma.jar:javax/help/JHelp$AccessibleJHelp.class */
    protected class AccessibleJHelp extends JComponent.AccessibleJComponent {
        private final JHelp this$0;

        protected AccessibleJHelp(JHelp jHelp) {
            super(jHelp);
            this.this$0 = jHelp;
        }

        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.PANEL;
        }
    }
}
