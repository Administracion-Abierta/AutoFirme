package javax.help.search;

import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Vector;
import javax.help.HelpSet;
import javax.help.HelpUtilities;
import javax.help.NavigatorView;

/* JADX INFO: loaded from: autofirma.jar:javax/help/search/MergingSearchEngine.class */
public class MergingSearchEngine extends SearchEngine {
    private Vector engines;
    private Hashtable enginePerView = new Hashtable();
    private boolean stopQuery = false;
    private static final boolean debug = false;
    static Class class$java$net$URL;
    static Class class$java$util$Hashtable;

    public MergingSearchEngine(NavigatorView navigatorView) {
        if (navigatorView == null) {
            throw new IllegalArgumentException("view must not be null");
        }
        this.engines = new Vector();
        this.engines.addElement(makeEngine(navigatorView));
    }

    public MergingSearchEngine(SearchEngine searchEngine) {
        if (searchEngine == null) {
            throw new IllegalArgumentException("engine must not be null");
        }
        this.engines = new Vector();
        this.engines.addElement(searchEngine);
    }

    @Override // javax.help.search.SearchEngine
    public SearchQuery createQuery() {
        return new MergingSearchQuery(this, this);
    }

    public void merge(NavigatorView navigatorView) {
        if (navigatorView == null) {
            throw new IllegalArgumentException("view must not be null");
        }
        SearchEngine searchEngineMakeEngine = makeEngine(navigatorView);
        if (searchEngineMakeEngine == null) {
            throw new IllegalArgumentException("view is invalid");
        }
        this.engines.addElement(searchEngineMakeEngine);
        this.enginePerView.put(navigatorView, searchEngineMakeEngine);
    }

    public void remove(NavigatorView navigatorView) {
        if (navigatorView == null) {
            throw new IllegalArgumentException("view is either null or invalid");
        }
        SearchEngine searchEngine = (SearchEngine) this.enginePerView.get(navigatorView);
        if (searchEngine != null) {
            this.engines.removeElement(searchEngine);
            this.enginePerView.remove(searchEngine);
            return;
        }
        throw new IllegalArgumentException("view is either null or invalid");
    }

    public Enumeration getEngines() {
        return this.engines.elements();
    }

    private SearchEngine makeEngine(NavigatorView navigatorView) {
        Class<?> clsClass$;
        Class<?> clsClass$2;
        Class<?> clsLoadClass;
        Hashtable parameters = navigatorView.getParameters();
        if (parameters == null) {
            return null;
        }
        if (parameters != null && !parameters.containsKey("data")) {
            return null;
        }
        String defaultQueryEngine = (String) parameters.get("engine");
        HelpSet helpSet = navigatorView.getHelpSet();
        URL helpSetURL = helpSet.getHelpSetURL();
        ClassLoader loader = helpSet.getLoader();
        if (defaultQueryEngine == null) {
            defaultQueryEngine = HelpUtilities.getDefaultQueryEngine();
            parameters.put("engine", defaultQueryEngine);
        }
        SearchEngine searchEngine = null;
        Class<?>[] clsArr = new Class[2];
        if (class$java$net$URL == null) {
            clsClass$ = class$("java.net.URL");
            class$java$net$URL = clsClass$;
        } else {
            clsClass$ = class$java$net$URL;
        }
        clsArr[0] = clsClass$;
        if (class$java$util$Hashtable == null) {
            clsClass$2 = class$("java.util.Hashtable");
            class$java$util$Hashtable = clsClass$2;
        } else {
            clsClass$2 = class$java$util$Hashtable;
        }
        clsArr[1] = clsClass$2;
        Object[] objArr = {helpSetURL, parameters};
        debug("makeEngine");
        debug(new StringBuffer().append("  base: ").append(helpSetURL).toString());
        debug(new StringBuffer().append("  params: ").append(parameters).toString());
        try {
            if (loader == null) {
                clsLoadClass = Class.forName(defaultQueryEngine);
            } else {
                clsLoadClass = loader.loadClass(defaultQueryEngine);
            }
            try {
                try {
                    searchEngine = (SearchEngine) clsLoadClass.getConstructor(clsArr).newInstance(objArr);
                } catch (InvocationTargetException e) {
                    System.err.println(new StringBuffer().append("Exception while creating engine named ").append(defaultQueryEngine).append(" for view: ").append(navigatorView).toString());
                    e.printStackTrace();
                } catch (Throwable th) {
                    throw new Error(new StringBuffer().append("Could not create engine named ").append(defaultQueryEngine).append(" for view: ").append(navigatorView).toString());
                }
                return searchEngine;
            } catch (Throwable th2) {
                throw new Error(new StringBuffer().append("Could not find constructor for ").append(defaultQueryEngine).append(". For view: ").append(navigatorView).toString());
            }
        } catch (Throwable th3) {
            throw new Error(new StringBuffer().append("Could not load engine named ").append(defaultQueryEngine).append(" for view: ").append(navigatorView).toString());
        }
    }

    static Class class$(String str) {
        try {
            return Class.forName(str);
        } catch (ClassNotFoundException e) {
            throw new NoClassDefFoundError(e.getMessage());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:javax/help/search/MergingSearchEngine$MergingSearchQuery.class */
    private class MergingSearchQuery extends SearchQuery implements SearchListener {
        private MergingSearchEngine mhs;
        private Vector queries;
        private String searchparams;
        private final MergingSearchEngine this$0;

        public MergingSearchQuery(MergingSearchEngine mergingSearchEngine, SearchEngine searchEngine) {
            super(searchEngine);
            this.this$0 = mergingSearchEngine;
            if (searchEngine instanceof MergingSearchEngine) {
                this.mhs = (MergingSearchEngine) searchEngine;
            }
        }

        @Override // javax.help.search.SearchQuery
        public synchronized void start(String str, Locale locale) throws IllegalStateException, IllegalArgumentException {
            MergingSearchEngine mergingSearchEngine = this.this$0;
            MergingSearchEngine.debug("startSearch()");
            if (!isActive()) {
                this.this$0.stopQuery = false;
                super.start(str, locale);
                this.queries = new Vector();
                Enumeration engines = this.mhs.getEngines();
                while (engines.hasMoreElements()) {
                    SearchEngine searchEngine = (SearchEngine) engines.nextElement();
                    if (searchEngine != null) {
                        this.queries.addElement(searchEngine.createQuery());
                    }
                }
                Enumeration enumerationElements = this.queries.elements();
                while (enumerationElements.hasMoreElements()) {
                    SearchQuery searchQuery = (SearchQuery) enumerationElements.nextElement();
                    searchQuery.addSearchListener(this);
                    searchQuery.start(str, locale);
                }
                return;
            }
            throw new IllegalStateException();
        }

        @Override // javax.help.search.SearchQuery
        public synchronized void stop() throws IllegalStateException {
            if (this.queries != null) {
                this.this$0.stopQuery = true;
                boolean z = true;
                while (z) {
                    z = false;
                    if (this.queries != null) {
                        Enumeration enumerationElements = this.queries.elements();
                        while (enumerationElements.hasMoreElements()) {
                            if (((SearchQuery) enumerationElements.nextElement()).isActive()) {
                                MergingSearchEngine.debug("queries are active waiting to stop");
                                z = true;
                            }
                        }
                        if (z) {
                            try {
                                wait(250L);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                this.queries = null;
            }
        }

        @Override // javax.help.search.SearchQuery
        public boolean isActive() {
            if (this.queries == null) {
                return false;
            }
            Enumeration enumerationElements = this.queries.elements();
            while (enumerationElements.hasMoreElements()) {
                if (((SearchQuery) enumerationElements.nextElement()).isActive()) {
                    return true;
                }
            }
            return false;
        }

        @Override // javax.help.search.SearchQuery
        public SearchEngine getSearchEngine() {
            return this.mhs;
        }

        @Override // javax.help.search.SearchListener
        public synchronized void itemsFound(SearchEvent searchEvent) {
            SearchQuery searchQuery = (SearchQuery) searchEvent.getSource();
            if (!this.this$0.stopQuery && this.queries != null) {
                Enumeration enumerationElements = this.queries.elements();
                while (enumerationElements.hasMoreElements()) {
                    if (((SearchQuery) enumerationElements.nextElement()) == searchQuery) {
                        fireItemsFound(searchEvent);
                    }
                }
            }
        }

        @Override // javax.help.search.SearchListener
        public void searchStarted(SearchEvent searchEvent) {
        }

        @Override // javax.help.search.SearchListener
        public synchronized void searchFinished(SearchEvent searchEvent) {
            SearchQuery searchQuery = (SearchQuery) searchEvent.getSource();
            if (this.queries != null) {
                Enumeration enumerationElements = this.queries.elements();
                while (enumerationElements.hasMoreElements()) {
                    SearchQuery searchQuery2 = (SearchQuery) enumerationElements.nextElement();
                    if (searchQuery2 == searchQuery) {
                        searchQuery.removeSearchListener(this);
                        this.queries.removeElement(searchQuery2);
                    }
                }
                if (this.queries.isEmpty()) {
                    this.queries = null;
                    if (!this.this$0.stopQuery) {
                        fireSearchFinished();
                    }
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void debug(String str) {
    }
}
