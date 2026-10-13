package javax.help.search;

import java.util.Enumeration;
import java.util.EventObject;
import java.util.Vector;

/* JADX INFO: loaded from: autofirma.jar:javax/help/search/SearchEvent.class */
public class SearchEvent extends EventObject {
    private String params;
    private boolean searching;
    private Vector items;

    public SearchEvent(Object obj, String str, boolean z) {
        super(obj);
        if (str == null) {
            throw new IllegalArgumentException("null params");
        }
        this.params = str;
        this.searching = z;
    }

    public SearchEvent(Object obj, String str, boolean z, Vector vector) {
        super(obj);
        if (str == null) {
            throw new IllegalArgumentException("null params");
        }
        this.params = str;
        this.searching = z;
        if (vector == null) {
            throw new IllegalArgumentException("null items");
        }
        this.items = vector;
    }

    public String getParams() {
        return this.params;
    }

    public boolean isSearchCompleted() {
        return this.searching;
    }

    public Enumeration getSearchItems() {
        if (this.items == null) {
            return null;
        }
        return this.items.elements();
    }
}
