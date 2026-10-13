package javax.help.search;

import java.util.Enumeration;
import java.util.Locale;
import java.util.Vector;
import javax.help.event.EventListenerList;

/* JADX INFO: loaded from: autofirma.jar:javax/help/search/SearchQuery.class */
public abstract class SearchQuery {
    protected EventListenerList listenerList = new EventListenerList();
    protected SearchEngine hs;
    protected String searchparams;
    protected Locale l;
    private static final boolean debug = false;
    static Class class$javax$help$search$SearchListener;

    public abstract boolean isActive();

    public SearchQuery(SearchEngine searchEngine) {
        this.hs = searchEngine;
    }

    public void addSearchListener(SearchListener searchListener) {
        Class clsClass$;
        EventListenerList eventListenerList = this.listenerList;
        if (class$javax$help$search$SearchListener == null) {
            clsClass$ = class$("javax.help.search.SearchListener");
            class$javax$help$search$SearchListener = clsClass$;
        } else {
            clsClass$ = class$javax$help$search$SearchListener;
        }
        eventListenerList.add(clsClass$, searchListener);
    }

    static Class class$(String str) {
        try {
            return Class.forName(str);
        } catch (ClassNotFoundException e) {
            throw new NoClassDefFoundError(e.getMessage());
        }
    }

    public void removeSearchListener(SearchListener searchListener) {
        Class clsClass$;
        EventListenerList eventListenerList = this.listenerList;
        if (class$javax$help$search$SearchListener == null) {
            clsClass$ = class$("javax.help.search.SearchListener");
            class$javax$help$search$SearchListener = clsClass$;
        } else {
            clsClass$ = class$javax$help$search$SearchListener;
        }
        eventListenerList.remove(clsClass$, searchListener);
    }

    public void start(String str, Locale locale) throws IllegalStateException, IllegalArgumentException {
        this.searchparams = str;
        this.l = locale;
        fireSearchStarted();
    }

    public void stop() throws IllegalStateException {
        fireSearchFinished();
    }

    public SearchEngine getSearchEngine() {
        return this.hs;
    }

    public void itemsFound(boolean z, Vector vector) {
        fireItemsFound(z, vector);
    }

    protected void fireItemsFound(boolean z, Vector vector) {
        Class clsClass$;
        debug("fireItemsFound");
        debug(new StringBuffer().append("  params: ").append(this.searchparams).toString());
        debug(new StringBuffer().append("  insearch: ").append(z).toString());
        debug(new StringBuffer().append("  docs: ").append(vector).toString());
        Object[] listenerList = this.listenerList.getListenerList();
        SearchEvent searchEvent = null;
        for (int length = listenerList.length - 2; length >= 0; length -= 2) {
            Object obj = listenerList[length];
            if (class$javax$help$search$SearchListener == null) {
                clsClass$ = class$("javax.help.search.SearchListener");
                class$javax$help$search$SearchListener = clsClass$;
            } else {
                clsClass$ = class$javax$help$search$SearchListener;
            }
            if (obj == clsClass$) {
                if (searchEvent == null) {
                    searchEvent = new SearchEvent(this, this.searchparams, z, vector);
                }
                ((SearchListener) listenerList[length + 1]).itemsFound(searchEvent);
            }
        }
    }

    protected void fireItemsFound(SearchEvent searchEvent) {
        Class clsClass$;
        Object[] listenerList = this.listenerList.getListenerList();
        Vector vector = new Vector();
        Enumeration searchItems = searchEvent.getSearchItems();
        while (searchItems.hasMoreElements()) {
            vector.addElement((SearchItem) searchItems.nextElement());
        }
        SearchEvent searchEvent2 = new SearchEvent(this, searchEvent.getParams(), searchEvent.isSearchCompleted(), vector);
        for (int length = listenerList.length - 2; length >= 0; length -= 2) {
            Object obj = listenerList[length];
            if (class$javax$help$search$SearchListener == null) {
                clsClass$ = class$("javax.help.search.SearchListener");
                class$javax$help$search$SearchListener = clsClass$;
            } else {
                clsClass$ = class$javax$help$search$SearchListener;
            }
            if (obj == clsClass$) {
                ((SearchListener) listenerList[length + 1]).itemsFound(searchEvent2);
            }
        }
    }

    protected void fireSearchStarted() {
        Class clsClass$;
        debug("fireSearchStarted");
        Object[] listenerList = this.listenerList.getListenerList();
        SearchEvent searchEvent = null;
        for (int length = listenerList.length - 2; length >= 0; length -= 2) {
            Object obj = listenerList[length];
            if (class$javax$help$search$SearchListener == null) {
                clsClass$ = class$("javax.help.search.SearchListener");
                class$javax$help$search$SearchListener = clsClass$;
            } else {
                clsClass$ = class$javax$help$search$SearchListener;
            }
            if (obj == clsClass$) {
                if (searchEvent == null) {
                    searchEvent = new SearchEvent(this, this.searchparams, true);
                }
                ((SearchListener) listenerList[length + 1]).searchStarted(searchEvent);
            }
        }
    }

    protected void fireSearchFinished() {
        Class clsClass$;
        debug("fireSearchFinished");
        Object[] listenerList = this.listenerList.getListenerList();
        SearchEvent searchEvent = null;
        for (int length = listenerList.length - 2; length >= 0; length -= 2) {
            Object obj = listenerList[length];
            if (class$javax$help$search$SearchListener == null) {
                clsClass$ = class$("javax.help.search.SearchListener");
                class$javax$help$search$SearchListener = clsClass$;
            } else {
                clsClass$ = class$javax$help$search$SearchListener;
            }
            if (obj == clsClass$) {
                if (searchEvent == null) {
                    searchEvent = new SearchEvent(this, this.searchparams, false);
                }
                ((SearchListener) listenerList[length + 1]).searchFinished(searchEvent);
            }
        }
    }

    private static void debug(String str) {
    }
}
