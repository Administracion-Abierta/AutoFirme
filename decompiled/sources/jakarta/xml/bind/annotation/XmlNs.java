package jakarta.xml.bind.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/* JADX INFO: loaded from: autofirma.jar:jakarta/xml/bind/annotation/XmlNs.class */
@Target({})
@Retention(RetentionPolicy.RUNTIME)
public @interface XmlNs {
    String prefix();

    String namespaceURI();
}
