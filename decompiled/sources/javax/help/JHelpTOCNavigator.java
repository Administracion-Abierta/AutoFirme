package javax.help;

import java.net.URL;

/* JADX INFO: loaded from: autofirma.jar:javax/help/JHelpTOCNavigator.class */
public class JHelpTOCNavigator extends JHelpNavigator {
    private static final boolean debug = false;

    public JHelpTOCNavigator(NavigatorView navigatorView) {
        super(navigatorView, null);
    }

    public JHelpTOCNavigator(NavigatorView navigatorView, HelpModel helpModel) {
        super(navigatorView, helpModel);
    }

    public JHelpTOCNavigator(HelpSet helpSet, String str, String str2, URL url) throws InvalidNavigatorViewException {
        super(new TOCView(helpSet, str, str2, createParams(url)));
    }

    @Override // javax.help.JHelpNavigator
    public String getUIClassID() {
        return "HelpTOCNavigatorUI";
    }

    @Override // javax.help.JHelpNavigator
    public boolean canMerge(NavigatorView navigatorView) {
        if ((navigatorView instanceof TOCView) && getNavigatorName().equals(navigatorView.getName())) {
            debug("canMerge: true");
            return true;
        }
        debug("canMerge: false");
        return false;
    }

    @Override // javax.help.JHelpNavigator
    public void merge(NavigatorView navigatorView) {
        debug(new StringBuffer().append("merge: ").append(navigatorView).toString());
        getUI().merge(navigatorView);
    }

    @Override // javax.help.JHelpNavigator
    public void remove(NavigatorView navigatorView) {
        getUI().remove(navigatorView);
    }

    public void expandID(String str) {
        firePropertyChange("expand", " ", str);
    }

    public void collapseID(String str) {
        firePropertyChange("collapse", " ", str);
    }

    private static void debug(String str) {
    }
}
