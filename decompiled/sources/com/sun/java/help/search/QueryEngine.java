package com.sun.java.help.search;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.BreakIterator;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Vector;
import javax.help.HelpUtilities;
import javax.help.search.SearchQuery;

/* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/search/QueryEngine.class */
public class QueryEngine {
    private SearchEnvironment _env;
    static Class class$com$sun$java$help$search$QueryEngine;

    public QueryEngine(String str, URL url) throws Exception {
        this._env = new SearchEnvironment(str, url);
    }

    public void processQuery(String str, Locale locale, SearchQuery searchQuery) throws Exception {
        int type;
        Vector vector = new Vector();
        LiteMorph morphForLocale = getMorphForLocale(locale);
        int i = -1;
        try {
            BreakIterator wordInstance = BreakIterator.getWordInstance(locale);
            wordInstance.setText(str);
            int iFirst = wordInstance.first();
            for (int next = wordInstance.next(); next != -1; next = wordInstance.next()) {
                String lowerCase = new String(str.substring(iFirst, next)).trim().toLowerCase(locale);
                if (lowerCase.length() > 1) {
                    i++;
                    int iFetch = this._env.fetch(lowerCase);
                    if (iFetch > 0) {
                        vector.addElement(new SearchIds(this, i, iFetch, 0.0d));
                    }
                    if (morphForLocale != null) {
                        for (String str2 : morphForLocale.variantsOf(lowerCase)) {
                            int iFetch2 = this._env.fetch(str2);
                            if (iFetch2 > 0) {
                                vector.addElement(new SearchIds(this, i, iFetch2, 0.1d));
                            }
                        }
                    }
                } else if (lowerCase.length() == 1 && ((type = Character.getType(lowerCase.charAt(0))) == 9 || type == 10 || type == 2 || type == 5 || type == 11 || type == 3 || type == 0 || type == 1)) {
                    i++;
                    int iFetch3 = this._env.fetch(lowerCase);
                    if (iFetch3 > 0) {
                        vector.addElement(new SearchIds(this, i, iFetch3, 0.0d));
                    }
                    if (morphForLocale != null) {
                        for (String str3 : morphForLocale.variantsOf(lowerCase)) {
                            int iFetch4 = this._env.fetch(str3);
                            if (iFetch4 > 0) {
                                vector.addElement(new SearchIds(this, i, iFetch4, 0.1d));
                            }
                        }
                    }
                }
                iFirst = next;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        int size = vector.size();
        Search search = new Search(this._env, i + 1);
        IntegerArray integerArray = new IntegerArray();
        for (int i2 = 0; i2 < size; i2++) {
            SearchIds searchIds = (SearchIds) vector.elementAt(i2);
            search.addTerm(searchIds.col, searchIds.concept, searchIds.score, 0);
            integerArray.clear();
            this._env.getChildren(searchIds.concept, integerArray);
            if (integerArray.cardinality() > 0) {
                for (int i3 = 0; i3 < integerArray.cardinality(); i3++) {
                    search.addTerm(searchIds.col, integerArray.at(i3), searchIds.score + 0.1d, 0);
                    this._env.getChildren(integerArray.at(i3), integerArray);
                }
            }
        }
        search.startSearch(searchQuery);
    }

    private LiteMorph getMorphForLocale(Locale locale) {
        Class clsClass$;
        Class<?> clsLoadClass;
        if (locale == null) {
            locale = Locale.ENGLISH;
        }
        Enumeration candidates = HelpUtilities.getCandidates(locale);
        if (class$com$sun$java$help$search$QueryEngine == null) {
            clsClass$ = class$("com.sun.java.help.search.QueryEngine");
            class$com$sun$java$help$search$QueryEngine = clsClass$;
        } else {
            clsClass$ = class$com$sun$java$help$search$QueryEngine;
        }
        ClassLoader classLoader = clsClass$.getClassLoader();
        while (candidates.hasMoreElements()) {
            String str = new String(new StringBuffer().append("com.sun.java.help.search.LiteMorph").append((String) candidates.nextElement()).toString());
            try {
                if (classLoader == null) {
                    clsLoadClass = Class.forName(str);
                } else {
                    clsLoadClass = classLoader.loadClass(str);
                }
                return (LiteMorph) clsLoadClass.getMethod("getMorph", (Class[]) null).invoke(null, (Object[]) null);
            } catch (Exception e) {
            }
        }
        return null;
    }

    static Class class$(String str) {
        try {
            return Class.forName(str);
        } catch (ClassNotFoundException e) {
            throw new NoClassDefFoundError(e.getMessage());
        }
    }

    public static void main(String[] strArr) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in), 1);
            QueryEngine queryEngine = new QueryEngine(new String(strArr[0]), null);
            System.out.println("initialized; enter query");
            while (true) {
                String line = bufferedReader.readLine();
                if (!line.equals(".")) {
                    long jCurrentTimeMillis = System.currentTimeMillis();
                    queryEngine.processQuery(line, Locale.getDefault(), null);
                    System.out.println(new StringBuffer().append(System.currentTimeMillis() - jCurrentTimeMillis).append(" msec search").toString());
                    System.out.println("enter next query or . to quit");
                } else {
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/search/QueryEngine$SearchIds.class */
    private class SearchIds {
        public int col;
        public int concept;
        public double score;
        private final QueryEngine this$0;

        public SearchIds(QueryEngine queryEngine, int i, int i2, double d) {
            this.this$0 = queryEngine;
            this.col = i;
            this.concept = i2;
            this.score = d;
        }

        public String toString() {
            return new StringBuffer().append("col=").append(this.col).append(" concept=").append(this.concept).append(" score=").append(this.score).toString();
        }
    }
}
