package javax.help.tagext;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Vector;
import javax.help.HelpBroker;
import javax.help.HelpSet;
import javax.help.Map;
import javax.help.NavigatorView;
import javax.help.SearchHit;
import javax.help.SearchTOCItem;
import javax.help.SearchView;
import javax.help.search.MergingSearchEngine;
import javax.help.search.SearchEvent;
import javax.help.search.SearchItem;
import javax.help.search.SearchListener;
import javax.help.search.SearchQuery;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

/* JADX INFO: loaded from: autofirma.jar:javax/help/tagext/SearchTOCItemTag.class */
public class SearchTOCItemTag extends BodyTagSupport implements SearchListener {
    private Enumeration treeEnum;
    private Vector nodes;
    private SearchView view;
    private HelpBroker hb;
    private String query;
    private MergingSearchEngine helpsearch;
    private SearchQuery searchquery;
    private boolean searchFinished;
    private static final boolean debug = false;

    public void setSearchView(SearchView searchView) {
        this.view = searchView;
    }

    public void setHelpBroker(HelpBroker helpBroker) {
        this.hb = helpBroker;
    }

    public void setQuery(String str) {
        this.query = str;
    }

    public synchronized int doStartTag() {
        if (this.helpsearch == null) {
            this.helpsearch = new MergingSearchEngine(this.view);
            this.searchquery = this.helpsearch.createQuery();
            this.searchquery.addSearchListener(this);
            addSubHelpSets(this.view.getHelpSet());
        }
        if (this.searchquery.isActive()) {
            this.searchquery.stop();
        }
        this.searchquery.start(this.query, Locale.getDefault());
        if (!this.searchFinished) {
            try {
                wait();
            } catch (InterruptedException e) {
            }
        }
        if (this.treeEnum.hasMoreElements()) {
            setNodeAttributes((SearchTOCItem) this.treeEnum.nextElement());
            return 2;
        }
        return 0;
    }

    private void addSubHelpSets(HelpSet helpSet) {
        Enumeration helpSets = helpSet.getHelpSets();
        while (helpSets.hasMoreElements()) {
            HelpSet helpSet2 = (HelpSet) helpSets.nextElement();
            if (helpSet2 != null) {
                NavigatorView[] navigatorViews = helpSet2.getNavigatorViews();
                for (int i = 0; i < navigatorViews.length; i++) {
                    if (navigatorViews[i] instanceof SearchView) {
                        this.helpsearch.merge(navigatorViews[i]);
                    }
                }
                addSubHelpSets(helpSet2);
            }
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.servlet.jsp.JspTagException */
    public int doAfterBody() throws JspException, JspTagException {
        BodyContent bodyContent = getBodyContent();
        try {
            bodyContent.writeOut(getPreviousOut());
            bodyContent.clearBody();
            if (this.treeEnum.hasMoreElements()) {
                setNodeAttributes((SearchTOCItem) this.treeEnum.nextElement());
                return 2;
            }
            return 0;
        } catch (IOException e) {
            throw new JspTagException(new StringBuffer().append("SearchTOCItemTag: ").append(e.getMessage()).toString());
        }
    }

    private void setNodeAttributes(SearchTOCItem searchTOCItem) {
        this.pageContext.setAttribute("name", searchTOCItem.getName());
        this.pageContext.setAttribute("helpID", getMapID(searchTOCItem));
        this.pageContext.setAttribute("confidence", Double.toString(searchTOCItem.getConfidence()));
        this.pageContext.setAttribute("hits", Integer.toString(searchTOCItem.hitCount()));
        this.pageContext.setAttribute("contentURL", searchTOCItem.getURL().toExternalForm());
        this.pageContext.setAttribute("hitBoundries", getSearchHits(searchTOCItem));
    }

    private String getMapID(SearchTOCItem searchTOCItem) {
        Map.ID iDFromURL = this.hb.getHelpSet().getCombinedMap().getIDFromURL(searchTOCItem.getURL());
        if (iDFromURL == null) {
            return "";
        }
        return iDFromURL.id;
    }

    private String getSearchHits(SearchTOCItem searchTOCItem) {
        String string = "{ ";
        Enumeration searchHits = searchTOCItem.getSearchHits();
        while (searchHits.hasMoreElements()) {
            SearchHit searchHit = (SearchHit) searchHits.nextElement();
            string = new StringBuffer().append(string).append("{").append(searchHit.getBegin()).append(",").append(searchHit.getEnd()).append("}").toString();
            if (searchHits.hasMoreElements()) {
                string = new StringBuffer().append(string).append(", ").toString();
            }
        }
        return new StringBuffer().append(string).append(" }").toString();
    }

    @Override // javax.help.search.SearchListener
    public synchronized void itemsFound(SearchEvent searchEvent) {
        Enumeration searchItems = searchEvent.getSearchItems();
        while (searchItems.hasMoreElements()) {
            SearchItem searchItem = (SearchItem) searchItems.nextElement();
            try {
                URL url = new URL(searchItem.getBase(), searchItem.getFilename());
                boolean z = false;
                Enumeration enumerationElements = this.nodes.elements();
                while (enumerationElements.hasMoreElements()) {
                    SearchTOCItem searchTOCItem = (SearchTOCItem) enumerationElements.nextElement();
                    URL url2 = searchTOCItem.getURL();
                    if (url2 != null && url != null && url.sameFile(url2)) {
                        searchTOCItem.addSearchHit(new SearchHit(searchItem.getConfidence(), searchItem.getBegin(), searchItem.getEnd()));
                        z = true;
                        break;
                    }
                }
                if (!z) {
                    this.nodes.addElement(new SearchTOCItem(searchItem));
                }
            } catch (MalformedURLException e) {
                debug(new StringBuffer().append("Failed to create URL from ").append(searchItem.getBase()).append("|").append(searchItem.getFilename()).toString());
            }
        }
    }

    @Override // javax.help.search.SearchListener
    public synchronized void searchStarted(SearchEvent searchEvent) {
        this.nodes = new Vector();
        this.searchFinished = false;
    }

    @Override // javax.help.search.SearchListener
    public synchronized void searchFinished(SearchEvent searchEvent) {
        this.searchFinished = true;
        this.treeEnum = this.nodes.elements();
        notifyAll();
    }

    private static void debug(String str) {
    }
}
