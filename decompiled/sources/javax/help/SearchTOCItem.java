package javax.help;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Vector;
import javax.help.search.SearchItem;

/* JADX INFO: loaded from: autofirma.jar:javax/help/SearchTOCItem.class */
public class SearchTOCItem extends TOCItem {
    private URL url;
    private Vector sivec;
    private boolean inTOC;
    private double confidence;

    public SearchTOCItem(Map.ID id, Map.ID id2, HelpSet helpSet, Locale locale) {
        super(id, id2, helpSet, locale);
        this.inTOC = true;
        this.url = null;
        this.sivec = new Vector();
        this.confidence = 0.0d;
    }

    public SearchTOCItem(SearchItem searchItem) {
        super(null, null, null, HelpUtilities.localeFromLang(searchItem.getLang()));
        this.inTOC = false;
        setName(searchItem.getTitle());
        try {
            this.url = new URL(searchItem.getBase(), searchItem.getFilename());
        } catch (MalformedURLException e) {
            this.url = null;
        }
        this.sivec = new Vector();
        SearchHit searchHit = new SearchHit(searchItem.getConfidence(), searchItem.getBegin(), searchItem.getEnd());
        this.confidence = searchItem.getConfidence();
        this.sivec.addElement(searchHit);
    }

    public void addSearchHit(SearchHit searchHit) {
        if (this.sivec.isEmpty()) {
            this.sivec.addElement(searchHit);
            this.confidence = searchHit.getConfidence();
            return;
        }
        for (int size = this.sivec.size() - 1; size >= 0; size--) {
            if (((SearchHit) this.sivec.elementAt(size)).getConfidence() <= searchHit.getConfidence()) {
                this.sivec.insertElementAt(searchHit, size + 1);
                return;
            }
            if (size == 0) {
                this.sivec.insertElementAt(searchHit, 0);
                this.confidence = searchHit.getConfidence();
            }
        }
    }

    @Override // javax.help.TreeItem
    public URL getURL() {
        return this.url;
    }

    public double getConfidence() {
        return this.confidence;
    }

    public Enumeration getConfidences() {
        return new Enumeration(this) { // from class: javax.help.SearchTOCItem.1
            int count = 0;
            private final SearchTOCItem this$0;

            @Override // java.util.Enumeration
            public boolean hasMoreElements() {
                return this.count < this.this$0.sivec.size();
            }

            {
                this.this$0 = this;
            }

            @Override // java.util.Enumeration
            public Object nextElement() {
                synchronized (this.this$0.sivec) {
                    if (this.count < this.this$0.sivec.size()) {
                        Vector vector = this.this$0.sivec;
                        int i = this.count;
                        this.count = i + 1;
                        return new Double(((SearchHit) vector.elementAt(i)).getConfidence());
                    }
                    throw new NoSuchElementException("Vector Enumeration");
                }
            }
        };
    }

    public boolean inTOC() {
        return this.inTOC;
    }

    public Enumeration getSearchHits() {
        return this.sivec.elements();
    }

    public int hitCount() {
        return this.sivec.size();
    }
}
