package javax.help;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.net.URL;
import java.util.Vector;
import javax.help.event.EventListenerList;
import javax.help.event.HelpModelEvent;
import javax.help.event.HelpModelListener;
import javax.help.event.TextHelpModelEvent;
import javax.help.event.TextHelpModelListener;

/* JADX INFO: loaded from: autofirma.jar:javax/help/DefaultHelpModel.class */
public class DefaultHelpModel implements TextHelpModel, Serializable {
    private HelpSet helpset;
    private Map.ID currentID;
    private URL currentURL;
    private String navID;
    private String title;
    private static boolean debug = false;
    static Class class$javax$help$event$HelpModelListener;
    static Class class$javax$help$event$TextHelpModelListener;
    private Vector highlights = new Vector();
    protected EventListenerList listenerList = new EventListenerList();
    protected EventListenerList textListenerList = new EventListenerList();
    protected PropertyChangeSupport changes = new PropertyChangeSupport(this);

    public DefaultHelpModel(HelpSet helpSet) {
        this.helpset = helpSet;
    }

    @Override // javax.help.HelpModel
    public void setHelpSet(HelpSet helpSet) {
        HelpSet helpSet2 = this.helpset;
        this.helpset = helpSet;
        this.changes.firePropertyChange("helpSet", helpSet2, helpSet);
    }

    @Override // javax.help.HelpModel
    public HelpSet getHelpSet() {
        return this.helpset;
    }

    @Override // javax.help.HelpModel
    public void setCurrentID(Map.ID id) throws InvalidHelpSetContextException {
        setCurrentID(id, (String) null, (JHelpNavigator) null);
    }

    @Override // javax.help.HelpModel
    public void setCurrentID(Map.ID id, String str, JHelpNavigator jHelpNavigator) throws InvalidHelpSetContextException {
        URL uRLFromID;
        if (id == null) {
            id = this.helpset.getHomeID();
        }
        if (id == null || id.equals(this.currentID)) {
            return;
        }
        String str2 = id.id;
        HelpSet helpSet = id.hs;
        if (!this.helpset.contains(helpSet)) {
            throw new InvalidHelpSetContextException("Wrong context", this.helpset, helpSet);
        }
        Map combinedMap = this.helpset.getCombinedMap();
        this.currentID = id;
        try {
            if (helpSet == this.helpset) {
                uRLFromID = combinedMap.getURLFromID(id);
            } else {
                uRLFromID = helpSet.getLocalMap().getURLFromID(id);
            }
            if (this.currentURL != null && this.currentURL.equals(uRLFromID)) {
                String ref = this.currentURL.getRef();
                String ref2 = uRLFromID.getRef();
                if (ref == null && ref2 == null) {
                    return;
                }
                if (ref != null && ref2 != null && ref.compareTo(ref2) == 0) {
                    return;
                }
            }
            this.currentURL = uRLFromID;
        } catch (Exception e) {
            this.currentURL = null;
        }
        this.highlights.setSize(0);
        fireIDChanged(this, this.currentID, this.currentURL, str, jHelpNavigator);
    }

    @Override // javax.help.HelpModel
    public Map.ID getCurrentID() {
        return this.currentID;
    }

    @Override // javax.help.HelpModel
    public void setCurrentURL(URL url) {
        setCurrentURL(url, (String) null, (JHelpNavigator) null);
    }

    @Override // javax.help.HelpModel
    public void setCurrentURL(URL url, String str, JHelpNavigator jHelpNavigator) {
        boolean z = false;
        if (this.currentURL == null) {
            if (this.currentURL != url) {
                this.currentURL = url;
                z = true;
            }
        } else if (!this.currentURL.equals(url)) {
            this.currentURL = url;
            z = true;
        }
        if (this.currentURL == null) {
            if (this.currentID != null) {
                this.currentID = null;
                z = true;
            }
        } else {
            Map.ID iDFromURL = this.helpset.getCombinedMap().getIDFromURL(this.currentURL);
            if (this.currentID == null) {
                if (this.currentID != iDFromURL) {
                    this.currentID = iDFromURL;
                    z = true;
                }
            } else if (!this.currentID.equals(iDFromURL)) {
                this.currentID = iDFromURL;
                z = true;
            }
        }
        if (z) {
            this.highlights.setSize(0);
            fireIDChanged(this, this.currentID, this.currentURL, str, jHelpNavigator);
        }
    }

    @Override // javax.help.HelpModel
    public URL getCurrentURL() {
        return this.currentURL;
    }

    @Override // javax.help.TextHelpModel
    public void addHighlight(int i, int i2) {
        debug(new StringBuffer().append("addHighlight(").append(i).append(", ").append(i2).append(")").toString());
        this.highlights.addElement(new DefaultHighlight(i, i2));
        fireHighlightsChanged(this);
    }

    @Override // javax.help.TextHelpModel
    public void removeAllHighlights() {
        debug("removeAllHighlights");
        this.highlights.setSize(0);
        fireHighlightsChanged(this);
    }

    @Override // javax.help.TextHelpModel
    public void setHighlights(TextHelpModel.Highlight[] highlightArr) {
        this.highlights.setSize(0);
        if (highlightArr == null) {
            return;
        }
        for (int i = 0; i < highlightArr.length; i++) {
            this.highlights.addElement(new DefaultHighlight(highlightArr[i].getStartOffset(), highlightArr[i].getEndOffset()));
        }
        if (this.highlights.size() > 0) {
            fireHighlightsChanged(this);
        }
    }

    @Override // javax.help.TextHelpModel
    public TextHelpModel.Highlight[] getHighlights() {
        DefaultHighlight[] defaultHighlightArr = new DefaultHighlight[this.highlights.size()];
        this.highlights.copyInto(defaultHighlightArr);
        return defaultHighlightArr;
    }

    @Override // javax.help.HelpModel
    public void addHelpModelListener(HelpModelListener helpModelListener) {
        Class clsClass$;
        debug("addHelpModelListener: ");
        debug(new StringBuffer().append("  l:").append(helpModelListener).toString());
        if (debug) {
            try {
                throw new Exception("");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        EventListenerList eventListenerList = this.listenerList;
        if (class$javax$help$event$HelpModelListener == null) {
            clsClass$ = class$("javax.help.event.HelpModelListener");
            class$javax$help$event$HelpModelListener = clsClass$;
        } else {
            clsClass$ = class$javax$help$event$HelpModelListener;
        }
        eventListenerList.add(clsClass$, helpModelListener);
    }

    static Class class$(String str) {
        try {
            return Class.forName(str);
        } catch (ClassNotFoundException e) {
            throw new NoClassDefFoundError(e.getMessage());
        }
    }

    @Override // javax.help.HelpModel
    public void removeHelpModelListener(HelpModelListener helpModelListener) {
        Class clsClass$;
        EventListenerList eventListenerList = this.listenerList;
        if (class$javax$help$event$HelpModelListener == null) {
            clsClass$ = class$("javax.help.event.HelpModelListener");
            class$javax$help$event$HelpModelListener = clsClass$;
        } else {
            clsClass$ = class$javax$help$event$HelpModelListener;
        }
        eventListenerList.remove(clsClass$, helpModelListener);
    }

    @Override // javax.help.TextHelpModel
    public void addTextHelpModelListener(TextHelpModelListener textHelpModelListener) {
        Class clsClass$;
        debug("addTextHelpModelListener: ");
        debug(new StringBuffer().append("  l:").append(textHelpModelListener).toString());
        if (debug) {
            try {
                throw new Exception("");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        EventListenerList eventListenerList = this.textListenerList;
        if (class$javax$help$event$TextHelpModelListener == null) {
            clsClass$ = class$("javax.help.event.TextHelpModelListener");
            class$javax$help$event$TextHelpModelListener = clsClass$;
        } else {
            clsClass$ = class$javax$help$event$TextHelpModelListener;
        }
        eventListenerList.add(clsClass$, textHelpModelListener);
    }

    @Override // javax.help.TextHelpModel
    public void removeTextHelpModelListener(TextHelpModelListener textHelpModelListener) {
        Class clsClass$;
        EventListenerList eventListenerList = this.textListenerList;
        if (class$javax$help$event$TextHelpModelListener == null) {
            clsClass$ = class$("javax.help.event.TextHelpModelListener");
            class$javax$help$event$TextHelpModelListener = clsClass$;
        } else {
            clsClass$ = class$javax$help$event$TextHelpModelListener;
        }
        eventListenerList.remove(clsClass$, textHelpModelListener);
    }

    @Override // javax.help.HelpModel
    public void addPropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        this.changes.addPropertyChangeListener(propertyChangeListener);
    }

    @Override // javax.help.HelpModel
    public void removePropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        this.changes.removePropertyChangeListener(propertyChangeListener);
    }

    @Override // javax.help.TextHelpModel
    public void setDocumentTitle(String str) {
        String str2 = this.title;
        this.title = str;
        this.changes.firePropertyChange("documentTitle", str2, str);
    }

    @Override // javax.help.TextHelpModel
    public String getDocumentTitle() {
        return this.title;
    }

    protected void fireIDChanged(Object obj, Map.ID id, URL url, String str, JHelpNavigator jHelpNavigator) {
        Class clsClass$;
        Object[] listenerList = this.listenerList.getListenerList();
        HelpModelEvent helpModelEvent = null;
        for (int length = listenerList.length - 2; length >= 0; length -= 2) {
            Object obj2 = listenerList[length];
            if (class$javax$help$event$HelpModelListener == null) {
                clsClass$ = class$("javax.help.event.HelpModelListener");
                class$javax$help$event$HelpModelListener = clsClass$;
            } else {
                clsClass$ = class$javax$help$event$HelpModelListener;
            }
            if (obj2 == clsClass$) {
                if (helpModelEvent == null) {
                    helpModelEvent = new HelpModelEvent(obj, id, url, str, jHelpNavigator);
                }
                debug("fireIDChanged: ");
                debug(new StringBuffer().append("  ").append(listenerList[length + 1]).toString());
                debug(new StringBuffer().append("  id=").append(helpModelEvent.getID()).append(" url=").append(helpModelEvent.getURL()).toString());
                ((HelpModelListener) listenerList[length + 1]).idChanged(helpModelEvent);
            }
        }
    }

    protected void fireIDChanged(Object obj, Map.ID id, URL url) {
        fireIDChanged(obj, id, url, (String) null, (JHelpNavigator) null);
    }

    protected void fireHighlightsChanged(Object obj) {
        Class clsClass$;
        Object[] listenerList = this.textListenerList.getListenerList();
        TextHelpModelEvent textHelpModelEvent = null;
        for (int length = listenerList.length - 2; length >= 0; length -= 2) {
            Object obj2 = listenerList[length];
            if (class$javax$help$event$TextHelpModelListener == null) {
                clsClass$ = class$("javax.help.event.TextHelpModelListener");
                class$javax$help$event$TextHelpModelListener = clsClass$;
            } else {
                clsClass$ = class$javax$help$event$TextHelpModelListener;
            }
            if (obj2 == clsClass$) {
                if (textHelpModelEvent == null) {
                    textHelpModelEvent = new TextHelpModelEvent(obj);
                }
                debug("fireHighlightsChanged: ");
                debug(new StringBuffer().append("  ").append(listenerList[length + 1]).toString());
                debug(new StringBuffer().append("  ").append(textHelpModelEvent).toString());
                ((TextHelpModelListener) listenerList[length + 1]).highlightsChanged(textHelpModelEvent);
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:javax/help/DefaultHelpModel$DefaultHighlight.class */
    public static class DefaultHighlight implements TextHelpModel.Highlight {
        public int start;
        public int end;

        public DefaultHighlight(int i, int i2) {
            if (i < 0) {
                throw new IllegalArgumentException("start");
            }
            if (i2 < 0) {
                throw new IllegalArgumentException("end");
            }
            this.start = i;
            this.end = i2;
        }

        @Override // javax.help.TextHelpModel.Highlight
        public int getStartOffset() {
            return this.start;
        }

        @Override // javax.help.TextHelpModel.Highlight
        public int getEndOffset() {
            return this.end;
        }
    }

    private static void debug(String str) {
        if (debug) {
            System.err.println(new StringBuffer().append("DefaultHelpModel: ").append(str).toString());
        }
    }
}
