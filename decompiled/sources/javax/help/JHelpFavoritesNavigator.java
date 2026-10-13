package javax.help;

import java.net.URL;
import javax.swing.Action;

/* JADX INFO: loaded from: autofirma.jar:javax/help/JHelpFavoritesNavigator.class */
public class JHelpFavoritesNavigator extends JHelpNavigator {
    private static final boolean debug = false;

    public JHelpFavoritesNavigator(NavigatorView navigatorView) {
        super(navigatorView, null);
    }

    public JHelpFavoritesNavigator(NavigatorView navigatorView, HelpModel helpModel) {
        super(navigatorView, helpModel);
    }

    public JHelpFavoritesNavigator(HelpSet helpSet, String str, String str2, URL url) throws InvalidNavigatorViewException {
        super(new FavoritesView(helpSet, str, str2, createParams(url)));
    }

    @Override // javax.help.JHelpNavigator
    public String getUIClassID() {
        return "HelpFavoritesNavigatorUI";
    }

    @Override // javax.help.JHelpNavigator
    public boolean canMerge(NavigatorView navigatorView) {
        return false;
    }

    public void expandID(String str) {
        firePropertyChange("expand", " ", str);
    }

    public void collapseID(String str) {
        firePropertyChange("collapse", " ", str);
    }

    public Action getAddAction() {
        return getUI().getAddAction();
    }

    private static void debug(String str) {
    }
}
