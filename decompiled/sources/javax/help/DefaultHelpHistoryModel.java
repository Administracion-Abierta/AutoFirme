package javax.help;

import java.net.URL;
import java.util.Vector;
import javax.help.event.EventListenerList;
import javax.help.event.HelpHistoryModelEvent;
import javax.help.event.HelpHistoryModelListener;
import javax.help.event.HelpModelEvent;

/* JADX INFO: loaded from: autofirma.jar:javax/help/DefaultHelpHistoryModel.class */
public class DefaultHelpHistoryModel implements HelpHistoryModel {
    protected HelpModel helpModel;
    protected JHelp help;
    private static boolean debug = false;
    static Class class$javax$help$event$HelpHistoryModelListener;
    protected Vector history = new Vector();
    protected int historyIndex = -1;
    protected EventListenerList listenerList = new EventListenerList();

    public DefaultHelpHistoryModel(JHelp jHelp) {
        this.help = jHelp;
        if (jHelp != null) {
            setHelpModel(jHelp.getModel());
        }
    }

    @Override // javax.help.HelpHistoryModel
    public void setHelpModel(HelpModel helpModel) {
        if (helpModel == this.helpModel) {
            return;
        }
        discard();
        if (this.helpModel != null) {
            this.helpModel.removeHelpModelListener(this);
        }
        if (helpModel != null) {
            helpModel.addHelpModelListener(this);
        }
        this.helpModel = helpModel;
    }

    @Override // javax.help.HelpHistoryModel
    public void removeHelpHistoryModelListener(HelpHistoryModelListener helpHistoryModelListener) {
        Class clsClass$;
        EventListenerList eventListenerList = this.listenerList;
        if (class$javax$help$event$HelpHistoryModelListener == null) {
            clsClass$ = class$("javax.help.event.HelpHistoryModelListener");
            class$javax$help$event$HelpHistoryModelListener = clsClass$;
        } else {
            clsClass$ = class$javax$help$event$HelpHistoryModelListener;
        }
        eventListenerList.remove(clsClass$, helpHistoryModelListener);
    }

    static Class class$(String str) {
        try {
            return Class.forName(str);
        } catch (ClassNotFoundException e) {
            throw new NoClassDefFoundError(e.getMessage());
        }
    }

    @Override // javax.help.HelpHistoryModel
    public void addHelpHistoryModelListener(HelpHistoryModelListener helpHistoryModelListener) {
        Class clsClass$;
        EventListenerList eventListenerList = this.listenerList;
        if (class$javax$help$event$HelpHistoryModelListener == null) {
            clsClass$ = class$("javax.help.event.HelpHistoryModelListener");
            class$javax$help$event$HelpHistoryModelListener = clsClass$;
        } else {
            clsClass$ = class$javax$help$event$HelpHistoryModelListener;
        }
        eventListenerList.add(clsClass$, helpHistoryModelListener);
    }

    @Override // javax.help.HelpHistoryModel
    public void discard() {
        this.history.setSize(0);
        this.historyIndex = -1;
        fireHistoryChanged(this, false, false);
    }

    @Override // javax.help.HelpHistoryModel
    public Vector getHistory() {
        return this.history;
    }

    public void removeLastEntry() {
        if (this.history.size() > 0) {
            this.history.removeElementAt(this.history.size() - 1);
        }
    }

    @Override // javax.help.HelpHistoryModel
    public int getIndex() {
        return this.historyIndex;
    }

    @Override // javax.help.HelpHistoryModel
    public void goForward() {
        setHistoryEntry(this.historyIndex + 1);
    }

    @Override // javax.help.HelpHistoryModel
    public void goBack() {
        setHistoryEntry(this.historyIndex - 1);
    }

    @Override // javax.help.HelpHistoryModel
    public Vector getForwardHistory() {
        Vector vector = (Vector) this.history.clone();
        Vector vector2 = new Vector();
        int size = vector.size();
        debug(new StringBuffer().append(" forward size : ").append(size).toString());
        for (int i = this.historyIndex + 1; i < size; i++) {
            vector2.addElement(vector.elementAt(i));
        }
        return vector2;
    }

    @Override // javax.help.HelpHistoryModel
    public Vector getBackwardHistory() {
        Vector vector = new Vector();
        Vector vector2 = (Vector) this.history.clone();
        debug(new StringBuffer().append(" backward size : ").append(vector2.size()).toString());
        debug(new StringBuffer().append(" backward index : ").append(this.historyIndex).toString());
        if (vector2 != null) {
            for (int i = 0; i < this.historyIndex; i++) {
                vector.addElement(vector2.elementAt(i));
            }
        }
        return vector;
    }

    @Override // javax.help.HelpHistoryModel
    public void setHistoryEntry(int i) {
        debug(new StringBuffer().append("setHistoryEntry(").append(i).append(")").toString());
        if (this.helpModel == null) {
            return;
        }
        if (i < 0 || i >= this.history.size()) {
            discard();
            return;
        }
        HelpModelEvent helpModelEvent = (HelpModelEvent) this.history.elementAt(i);
        this.historyIndex = i - 1;
        Map.ID id = helpModelEvent.getID();
        URL url = helpModelEvent.getURL();
        JHelpNavigator navigator = helpModelEvent.getNavigator();
        if (id != null) {
            try {
                debug(new StringBuffer().append("  setCurrentID").append(id).toString());
                this.helpModel.setCurrentID(id, helpModelEvent.getHistoryName(), helpModelEvent.getNavigator());
                if (navigator != null) {
                    this.help.setCurrentNavigator(navigator);
                    return;
                }
                return;
            } catch (Exception e) {
            }
        }
        if (url != null) {
            try {
                debug(new StringBuffer().append("  setCurrentURL").append(url).toString());
                this.helpModel.setCurrentURL(url, helpModelEvent.getHistoryName(), helpModelEvent.getNavigator());
                if (navigator != null) {
                    this.help.setCurrentNavigator(navigator);
                    return;
                }
                return;
            } catch (Exception e2) {
            }
        }
        discard();
    }

    protected void fireHistoryChanged(Object obj, boolean z, boolean z2) {
        Class clsClass$;
        Object[] listenerList = this.listenerList.getListenerList();
        HelpHistoryModelEvent helpHistoryModelEvent = null;
        for (int length = listenerList.length - 2; length >= 0; length -= 2) {
            Object obj2 = listenerList[length];
            if (class$javax$help$event$HelpHistoryModelListener == null) {
                clsClass$ = class$("javax.help.event.HelpHistoryModelListener");
                class$javax$help$event$HelpHistoryModelListener = clsClass$;
            } else {
                clsClass$ = class$javax$help$event$HelpHistoryModelListener;
            }
            if (obj2 == clsClass$) {
                if (helpHistoryModelEvent == null) {
                    helpHistoryModelEvent = new HelpHistoryModelEvent(obj, z, z2);
                }
                debug("fireHistoryChanged: ");
                debug(new StringBuffer().append("  ").append(listenerList[length + 1]).toString());
                debug(new StringBuffer().append("  previous=").append(helpHistoryModelEvent.isPrevious()).append(" next=").append(helpHistoryModelEvent.isNext()).toString());
                ((HelpHistoryModelListener) listenerList[length + 1]).historyChanged(helpHistoryModelEvent);
            }
        }
    }

    @Override // javax.help.event.HelpModelListener
    public void idChanged(HelpModelEvent helpModelEvent) {
        debug(new StringBuffer().append("idChanged(").append(helpModelEvent).append(")").toString());
        debug(new StringBuffer().append("  historyIndex==").append(this.historyIndex).toString());
        debug(new StringBuffer().append("  history.size==").append(this.history.size()).toString());
        if (this.historyIndex == this.history.size() - 1) {
            this.history.addElement(helpModelEvent);
            this.historyIndex++;
            fireHistoryChanged(this, this.historyIndex > 0, this.historyIndex < this.history.size() - 1);
            return;
        }
        if (this.historyIndex >= -1 && this.historyIndex < this.history.size() - 1) {
            this.historyIndex++;
            HelpModelEvent helpModelEvent2 = (HelpModelEvent) this.history.elementAt(this.historyIndex);
            if (helpModelEvent2 == null) {
                discard();
                return;
            }
            if (helpModelEvent2.getID() != null && helpModelEvent.getID() != null && helpModelEvent2.getID().equals(helpModelEvent.getID())) {
                fireHistoryChanged(this, this.historyIndex > 0, this.historyIndex < this.history.size() - 1);
                return;
            }
            if (helpModelEvent2.getURL() != null && helpModelEvent.getURL() != null && helpModelEvent2.getURL().sameFile(helpModelEvent.getURL())) {
                fireHistoryChanged(this, this.historyIndex > 0, this.historyIndex < this.history.size() - 1);
                return;
            }
            this.history.setSize(this.historyIndex);
            this.history.addElement(helpModelEvent);
            fireHistoryChanged(this, this.historyIndex > 0, this.historyIndex < this.history.size() - 1);
        }
    }

    @Override // javax.help.HelpHistoryModel
    public void removeHelpSet(HelpSet helpSet) {
        Map.ID iDFromURL;
        this.history.elements();
        debug(new StringBuffer().append(" size before ").append(this.history.size()).toString());
        if (debug) {
            System.err.println("before : ");
            for (int i = 0; i < this.history.size(); i++) {
                System.err.println(((HelpModelEvent) this.history.elementAt(i)).getID());
            }
        }
        int size = this.history.size();
        Vector vector = new Vector();
        int i2 = this.historyIndex;
        for (int i3 = 0; i3 < size; i3++) {
            HelpModelEvent helpModelEvent = (HelpModelEvent) this.history.elementAt(i3);
            Map.ID id = helpModelEvent.getID();
            debug(new StringBuffer().append(" update id ").append(id).toString());
            URL url = helpModelEvent.getURL();
            debug(new StringBuffer().append(" update url ").append(url).toString());
            if (id != null && id.hs != helpSet) {
                debug(new StringBuffer().append(" remain - ").append(id).toString());
                vector.addElement(this.history.elementAt(i3));
            } else if (url != null && (iDFromURL = helpSet.getCombinedMap().getIDFromURL(url)) == null) {
                debug(new StringBuffer().append(" remain > ").append(iDFromURL).toString());
                vector.addElement(this.history.elementAt(i3));
            }
        }
        this.history = vector;
        this.historyIndex = this.history.size() - 1;
        debug(new StringBuffer().append(" size after ").append(this.history.size()).toString());
        if (debug) {
            System.err.println("after : ");
            for (int i4 = 0; i4 < this.history.size(); i4++) {
                System.err.println(((HelpModelEvent) this.history.elementAt(i4)).getID());
            }
        }
        setHistoryEntry(this.historyIndex);
    }

    protected HelpModel getModel() {
        return this.helpModel;
    }

    private static void debug(String str) {
        if (debug) {
            System.out.println(new StringBuffer().append("BasicHelpUI: ").append(str).toString());
        }
    }
}
