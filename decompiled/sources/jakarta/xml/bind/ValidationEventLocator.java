package jakarta.xml.bind;

import java.net.URL;
import org.w3c.dom.Node;

/* JADX INFO: loaded from: autofirma.jar:jakarta/xml/bind/ValidationEventLocator.class */
public interface ValidationEventLocator {
    URL getURL();

    int getOffset();

    int getLineNumber();

    int getColumnNumber();

    Object getObject();

    Node getNode();
}
