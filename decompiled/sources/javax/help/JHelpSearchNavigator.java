package javax.help;

import java.net.URL;
import javax.help.search.MergingSearchEngine;
import javax.help.search.SearchEngine;

/* JADX INFO: loaded from: autofirma.jar:javax/help/JHelpSearchNavigator.class */
public class JHelpSearchNavigator extends JHelpNavigator {
    private MergingSearchEngine search;
    private static final boolean debug = false;

    public JHelpSearchNavigator(NavigatorView navigatorView) {
        super(navigatorView, null);
    }

    public JHelpSearchNavigator(NavigatorView navigatorView, HelpModel helpModel) {
        super(navigatorView, helpModel);
    }

    public JHelpSearchNavigator(HelpSet helpSet, String str, String str2, URL url) throws InvalidNavigatorViewException {
        super(new SearchView(helpSet, str, str2, createParams(url)));
    }

    @Override // javax.help.JHelpNavigator
    public String getUIClassID() {
        return "HelpSearchNavigatorUI";
    }

    public SearchEngine getSearchEngine() {
        if (this.search == null) {
            this.search = new MergingSearchEngine(getNavigatorView());
        }
        return this.search;
    }

    public void setSearchEngine(SearchEngine searchEngine) {
        this.search = new MergingSearchEngine(searchEngine);
    }

    protected String getDefaultQueryEngine() {
        return HelpUtilities.getDefaultQueryEngine();
    }

    @Override // javax.help.JHelpNavigator
    public boolean canMerge(NavigatorView navigatorView) {
        if ((navigatorView instanceof SearchView) && getNavigatorName().equals(navigatorView.getName())) {
            debug("canMerge: true");
            return true;
        }
        debug("canMerge: false");
        return false;
    }

    @Override // javax.help.JHelpNavigator
    public void merge(NavigatorView navigatorView) {
        debug("JHelpSearchNavigator.merge invoked");
        debug(new StringBuffer().append("  params: ").append(navigatorView.getParameters()).toString());
        if (this.search == null) {
            this.search = (MergingSearchEngine) getSearchEngine();
        }
        this.search.merge(navigatorView);
        debug(new StringBuffer().append("merge: ").append(navigatorView).toString());
        getUI().merge(navigatorView);
    }

    @Override // javax.help.JHelpNavigator
    public void remove(NavigatorView navigatorView) {
        debug("JHelpSearchNavigator.remove invoked");
        debug(new StringBuffer().append("  params: ").append(navigatorView.getParameters()).toString());
        if (this.search == null) {
            this.search = (MergingSearchEngine) getSearchEngine();
        }
        this.search.remove(navigatorView);
        debug(new StringBuffer().append("remove: ").append(navigatorView).toString());
        getUI().remove(navigatorView);
    }

    private static void debug(String str) {
    }
}
