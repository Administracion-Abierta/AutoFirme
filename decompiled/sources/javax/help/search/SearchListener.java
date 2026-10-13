package javax.help.search;

import java.util.EventListener;

/* JADX INFO: loaded from: autofirma.jar:javax/help/search/SearchListener.class */
public interface SearchListener extends EventListener {
    void itemsFound(SearchEvent searchEvent);

    void searchStarted(SearchEvent searchEvent);

    void searchFinished(SearchEvent searchEvent);
}
