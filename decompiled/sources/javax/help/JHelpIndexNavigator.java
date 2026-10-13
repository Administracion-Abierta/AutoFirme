package javax.help;

import java.net.URL;

/* JADX INFO: loaded from: autofirma.jar:javax/help/JHelpIndexNavigator.class */
public class JHelpIndexNavigator extends JHelpNavigator {
    private static final boolean debug = false;

    public JHelpIndexNavigator(NavigatorView navigatorView) {
        super(navigatorView, null);
    }

    public JHelpIndexNavigator(NavigatorView navigatorView, HelpModel helpModel) {
        super(navigatorView, helpModel);
    }

    public JHelpIndexNavigator(HelpSet helpSet, String str, String str2, URL url) throws InvalidNavigatorViewException {
        super(new IndexView(helpSet, str, str2, createParams(url)));
    }

    @Override // javax.help.JHelpNavigator
    public String getUIClassID() {
        return "HelpIndexNavigatorUI";
    }

    @Override // javax.help.JHelpNavigator
    public boolean canMerge(NavigatorView navigatorView) {
        if ((navigatorView instanceof IndexView) && getNavigatorName().equals(navigatorView.getName())) {
            return true;
        }
        return false;
    }

    @Override // javax.help.JHelpNavigator
    public void merge(NavigatorView navigatorView) {
        debug(new StringBuffer().append("merge of: ").append(navigatorView).toString());
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
