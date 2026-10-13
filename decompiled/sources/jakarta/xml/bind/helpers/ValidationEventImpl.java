package jakarta.xml.bind.helpers;

import jakarta.xml.bind.ValidationEvent;
import jakarta.xml.bind.ValidationEventLocator;
import java.text.MessageFormat;

/* JADX INFO: loaded from: autofirma.jar:jakarta/xml/bind/helpers/ValidationEventImpl.class */
public class ValidationEventImpl implements ValidationEvent {
    private int severity;
    private String message;
    private Throwable linkedException;
    private ValidationEventLocator locator;

    public ValidationEventImpl(int _severity, String _message, ValidationEventLocator _locator) {
        this(_severity, _message, _locator, null);
    }

    public ValidationEventImpl(int _severity, String _message, ValidationEventLocator _locator, Throwable _linkedException) {
        setSeverity(_severity);
        this.message = _message;
        this.locator = _locator;
        this.linkedException = _linkedException;
    }

    @Override // jakarta.xml.bind.ValidationEvent
    public int getSeverity() {
        return this.severity;
    }

    public void setSeverity(int _severity) {
        if (_severity != 0 && _severity != 1 && _severity != 2) {
            throw new IllegalArgumentException(Messages.format("ValidationEventImpl.IllegalSeverity"));
        }
        this.severity = _severity;
    }

    @Override // jakarta.xml.bind.ValidationEvent
    public String getMessage() {
        return this.message;
    }

    public void setMessage(String _message) {
        this.message = _message;
    }

    @Override // jakarta.xml.bind.ValidationEvent
    public Throwable getLinkedException() {
        return this.linkedException;
    }

    public void setLinkedException(Throwable _linkedException) {
        this.linkedException = _linkedException;
    }

    @Override // jakarta.xml.bind.ValidationEvent
    public ValidationEventLocator getLocator() {
        return this.locator;
    }

    public void setLocator(ValidationEventLocator _locator) {
        this.locator = _locator;
    }

    public String toString() {
        String s;
        switch (getSeverity()) {
            case 0:
                s = "WARNING";
                break;
            case 1:
                s = "ERROR";
                break;
            case 2:
                s = "FATAL_ERROR";
                break;
            default:
                s = String.valueOf(getSeverity());
                break;
        }
        return MessageFormat.format("[severity={0},message={1},locator={2}]", s, getMessage(), getLocator());
    }
}
