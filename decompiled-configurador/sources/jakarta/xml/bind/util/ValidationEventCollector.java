package jakarta.xml.bind.util;

import jakarta.xml.bind.ValidationEvent;
import jakarta.xml.bind.ValidationEventHandler;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:jakarta/xml/bind/util/ValidationEventCollector.class */
public class ValidationEventCollector implements ValidationEventHandler {
    private final List<ValidationEvent> events = new ArrayList();

    public ValidationEvent[] getEvents() {
        return (ValidationEvent[]) this.events.toArray(new ValidationEvent[this.events.size()]);
    }

    public void reset() {
        this.events.clear();
    }

    public boolean hasEvents() {
        return !this.events.isEmpty();
    }

    @Override // jakarta.xml.bind.ValidationEventHandler
    public boolean handleEvent(ValidationEvent event) {
        this.events.add(event);
        boolean retVal = true;
        switch (event.getSeverity()) {
            case 0:
                retVal = true;
                break;
            case 1:
                retVal = true;
                break;
            case 2:
                retVal = false;
                break;
            default:
                _assert(false, Messages.format("ValidationEventCollector.UnrecognizedSeverity", Integer.valueOf(event.getSeverity())));
                break;
        }
        return retVal;
    }

    private static void _assert(boolean b, String msg) {
        if (!b) {
            throw new InternalError(msg);
        }
    }
}
