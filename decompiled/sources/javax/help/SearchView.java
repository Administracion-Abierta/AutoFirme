package javax.help;

import java.awt.Component;
import java.util.Hashtable;
import java.util.Locale;

/* JADX INFO: loaded from: autofirma.jar:javax/help/SearchView.class */
public class SearchView extends NavigatorView {
    public SearchView(HelpSet helpSet, String str, String str2, Hashtable hashtable) {
        super(helpSet, str, str2, helpSet.getLocale(), hashtable);
    }

    public SearchView(HelpSet helpSet, String str, String str2, Locale locale, Hashtable hashtable) {
        super(helpSet, str, str2, locale, hashtable);
    }

    @Override // javax.help.NavigatorView
    public Component createNavigator(HelpModel helpModel) {
        return new JHelpSearchNavigator(this, helpModel);
    }
}
