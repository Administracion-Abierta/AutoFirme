package javax.help;

import java.util.Locale;

/* JADX INFO: loaded from: autofirma.jar:javax/help/TOCItem.class */
public class TOCItem extends TreeItem {
    private Map.ID imageID;

    public TOCItem(Map.ID id, Map.ID id2, HelpSet helpSet, Locale locale) {
        super(id, helpSet, locale);
        this.imageID = id2;
    }

    public TOCItem(Map.ID id, Map.ID id2, Locale locale) {
        super(id, locale);
        if (id != null) {
            setHelpSet(id.hs);
        }
        this.imageID = id2;
    }

    public TOCItem() {
        super(null, null, null);
        this.imageID = null;
    }

    public Map.ID getImageID() {
        return this.imageID;
    }
}
