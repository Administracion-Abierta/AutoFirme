package com.sun.java.help.search;

import java.util.Locale;
import javax.help.search.SearchEngine;
import javax.help.search.SearchQuery;

/* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/search/DefaultSearchQuery.class */
public class DefaultSearchQuery extends SearchQuery implements Runnable {
    private Thread thread;
    private DefaultSearchEngine dhs;
    private static final boolean debugFlag = false;

    public DefaultSearchQuery(SearchEngine searchEngine) {
        super(searchEngine);
        this.thread = null;
        if (searchEngine instanceof DefaultSearchEngine) {
            this.dhs = (DefaultSearchEngine) searchEngine;
        }
    }

    @Override // javax.help.search.SearchQuery
    public void start(String str, Locale locale) throws IllegalStateException, IllegalArgumentException {
        debug("Starting Search");
        if (isActive()) {
            throw new IllegalStateException();
        }
        super.start(str, locale);
        this.thread = new Thread(this, "QueryThread");
        this.thread.start();
    }

    @Override // javax.help.search.SearchQuery
    public void stop() throws IllegalStateException, IllegalArgumentException {
        debug("Stop Search");
    }

    @Override // javax.help.search.SearchQuery
    public boolean isActive() {
        if (this.thread == null) {
            return false;
        }
        return this.thread.isAlive();
    }

    @Override // java.lang.Runnable
    public void run() throws IllegalArgumentException {
        try {
            this.dhs.getQueryEngine().processQuery(this.searchparams, this.l, this);
            fireSearchFinished();
            this.thread = null;
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException();
        }
    }

    private static void debug(String str) {
    }
}
