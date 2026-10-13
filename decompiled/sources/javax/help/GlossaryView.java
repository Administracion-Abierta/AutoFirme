package javax.help;

import java.awt.Component;
import java.util.Hashtable;
import java.util.Locale;

/* JADX INFO: loaded from: autofirma.jar:javax/help/GlossaryView.class */
public class GlossaryView extends IndexView {
    public GlossaryView(HelpSet helpSet, String str, String str2, Hashtable hashtable) {
        super(helpSet, str, str2, helpSet.getLocale(), hashtable);
    }

    public GlossaryView(HelpSet helpSet, String str, String str2, Locale locale, Hashtable hashtable) {
        super(helpSet, str, str2, locale, hashtable);
    }

    @Override // javax.help.IndexView, javax.help.NavigatorView
    public Component createNavigator(HelpModel helpModel) {
        return new JHelpGlossaryNavigator(this, helpModel);
    }
}
