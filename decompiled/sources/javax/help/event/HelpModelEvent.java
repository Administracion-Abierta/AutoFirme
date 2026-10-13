package javax.help.event;

import java.net.URL;
import java.util.EventObject;
import javax.help.JHelpNavigator;
import javax.help.Map;

/* JADX INFO: loaded from: autofirma.jar:javax/help/event/HelpModelEvent.class */
public class HelpModelEvent extends EventObject {
    private Map.ID id;
    private URL url;
    private String historyName;
    private JHelpNavigator navigator;
    private int pos0;
    private int pos1;

    public HelpModelEvent(Object obj, Map.ID id, URL url) {
        this(obj, id, url, (String) null, (JHelpNavigator) null);
    }

    public HelpModelEvent(Object obj, Map.ID id, URL url, String str, JHelpNavigator jHelpNavigator) {
        super(obj);
        if (id == null && url == null) {
            throw new IllegalArgumentException("ID or URL must not be null");
        }
        this.id = id;
        this.url = url;
        this.historyName = str;
        this.navigator = jHelpNavigator;
    }

    public HelpModelEvent(Object obj, int i, int i2) {
        super(obj);
        this.pos0 = i;
        this.pos1 = i2;
    }

    public Map.ID getID() {
        return this.id;
    }

    public URL getURL() {
        return this.url;
    }

    public String getHistoryName() {
        return this.historyName;
    }

    public JHelpNavigator getNavigator() {
        return this.navigator;
    }

    public int getPos0() {
        return this.pos0;
    }

    public int getPos1() {
        return this.pos1;
    }
}
