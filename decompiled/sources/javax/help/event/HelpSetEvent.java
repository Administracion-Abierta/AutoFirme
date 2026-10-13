package javax.help.event;

import java.util.EventObject;
import javax.help.HelpSet;

/* JADX INFO: loaded from: autofirma.jar:javax/help/event/HelpSetEvent.class */
public class HelpSetEvent extends EventObject {
    public static final int HELPSET_ADDED = 0;
    public static final int HELPSET_REMOVED = 1;
    private HelpSet helpset;
    private int action;

    public HelpSetEvent(Object obj, HelpSet helpSet, int i) {
        super(obj);
        this.helpset = helpSet;
        if (helpSet == null) {
            throw new NullPointerException("helpset");
        }
        this.action = i;
        if (i < 0 || i > 1) {
            throw new IllegalArgumentException("invalid action");
        }
    }

    public HelpSet getHelpSet() {
        return this.helpset;
    }

    public int getAction() {
        return this.action;
    }

    @Override // java.util.EventObject
    public String toString() {
        if (this.action == 0) {
            return new StringBuffer().append("HelpSetEvent(").append(this.source).append(", ").append(this.helpset).append("; added").toString();
        }
        return new StringBuffer().append("HelpSetEvent(").append(this.source).append(", ").append(this.helpset).append("; removed").toString();
    }
}
