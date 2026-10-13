package javax.help.event;

import java.util.EventListener;

/* JADX INFO: loaded from: autofirma.jar:javax/help/event/HelpSetListener.class */
public interface HelpSetListener extends EventListener {
    void helpSetAdded(HelpSetEvent helpSetEvent);

    void helpSetRemoved(HelpSetEvent helpSetEvent);
}
