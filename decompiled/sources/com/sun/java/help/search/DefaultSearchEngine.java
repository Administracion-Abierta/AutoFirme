package com.sun.java.help.search;

import java.net.URL;
import java.security.InvalidParameterException;
import java.util.Hashtable;
import javax.help.search.SearchEngine;
import javax.help.search.SearchQuery;

/* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/search/DefaultSearchEngine.class */
public class DefaultSearchEngine extends SearchEngine {
    private String urldata;
    protected QueryEngine qe;
    private static final boolean debugFlag = false;

    public DefaultSearchEngine(URL url, Hashtable hashtable) throws InvalidParameterException {
        super(url, hashtable);
        debug("Loading Search Database");
        debug(new StringBuffer().append("  base: ").append(url).toString());
        debug(new StringBuffer().append("  params: ").append(hashtable).toString());
        try {
            this.urldata = (String) hashtable.get("data");
            this.qe = new QueryEngine(this.urldata, url);
        } catch (Exception e) {
            throw new InvalidParameterException();
        }
    }

    @Override // javax.help.search.SearchEngine
    public SearchQuery createQuery() {
        return new DefaultSearchQuery(this);
    }

    protected QueryEngine getQueryEngine() {
        return this.qe;
    }

    private static void debug(String str) {
    }
}
