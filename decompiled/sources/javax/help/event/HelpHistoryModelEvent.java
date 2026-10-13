package javax.help.event;

import java.util.EventObject;

/* JADX INFO: loaded from: autofirma.jar:javax/help/event/HelpHistoryModelEvent.class */
public class HelpHistoryModelEvent extends EventObject {
    private boolean next;
    private boolean previous;

    public HelpHistoryModelEvent(Object obj, boolean z, boolean z2) {
        super(obj);
        this.next = z2;
        this.previous = z;
    }

    public boolean isPrevious() {
        return this.previous;
    }

    public boolean isNext() {
        return this.next;
    }
}
