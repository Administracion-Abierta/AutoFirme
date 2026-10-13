package jakarta.xml.bind.helpers;

import jakarta.xml.bind.NotIdentifiableEvent;
import jakarta.xml.bind.ValidationEventLocator;

/* JADX INFO: loaded from: autofirma.jar:jakarta/xml/bind/helpers/NotIdentifiableEventImpl.class */
public class NotIdentifiableEventImpl extends ValidationEventImpl implements NotIdentifiableEvent {
    public NotIdentifiableEventImpl(int _severity, String _message, ValidationEventLocator _locator) {
        super(_severity, _message, _locator);
    }

    public NotIdentifiableEventImpl(int _severity, String _message, ValidationEventLocator _locator, Throwable _linkedException) {
        super(_severity, _message, _locator, _linkedException);
    }
}
