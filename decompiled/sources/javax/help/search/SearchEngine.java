package javax.help.search;

import java.net.URL;
import java.security.InvalidParameterException;
import java.util.Hashtable;

/* JADX INFO: loaded from: autofirma.jar:javax/help/search/SearchEngine.class */
public abstract class SearchEngine {
    protected URL base;
    protected Hashtable params;
    private static final boolean debug = false;

    public abstract SearchQuery createQuery() throws IllegalStateException;

    public SearchEngine(URL url, Hashtable hashtable) throws InvalidParameterException {
        this.base = url;
        this.params = hashtable;
    }

    public SearchEngine() {
    }

    private static void debug(String str) {
    }
}
