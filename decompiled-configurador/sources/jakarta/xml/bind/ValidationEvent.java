package jakarta.xml.bind;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:jakarta/xml/bind/ValidationEvent.class */
public interface ValidationEvent {
    public static final int WARNING = 0;
    public static final int ERROR = 1;
    public static final int FATAL_ERROR = 2;

    int getSeverity();

    String getMessage();

    Throwable getLinkedException();

    ValidationEventLocator getLocator();
}
