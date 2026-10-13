package javax.help;

import java.util.Locale;

/* JADX INFO: loaded from: autofirma.jar:javax/help/IndexItem.class */
public class IndexItem extends TreeItem {
    public IndexItem(Map.ID id, HelpSet helpSet, Locale locale) {
        super(id, helpSet, locale);
    }

    public IndexItem(Map.ID id, Locale locale) {
        super(id, locale);
        if (id != null) {
            setHelpSet(id.hs);
        }
    }

    public IndexItem() {
        super(null, null, null);
    }
}
