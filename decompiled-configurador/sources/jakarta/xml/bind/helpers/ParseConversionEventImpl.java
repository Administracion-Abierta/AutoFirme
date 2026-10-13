package jakarta.xml.bind.helpers;

import jakarta.xml.bind.ParseConversionEvent;
import jakarta.xml.bind.ValidationEventLocator;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:jakarta/xml/bind/helpers/ParseConversionEventImpl.class */
public class ParseConversionEventImpl extends ValidationEventImpl implements ParseConversionEvent {
    public ParseConversionEventImpl(int _severity, String _message, ValidationEventLocator _locator) {
        super(_severity, _message, _locator);
    }

    public ParseConversionEventImpl(int _severity, String _message, ValidationEventLocator _locator, Throwable _linkedException) {
        super(_severity, _message, _locator, _linkedException);
    }
}
