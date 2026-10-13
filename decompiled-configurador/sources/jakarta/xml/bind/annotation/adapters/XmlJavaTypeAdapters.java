package jakarta.xml.bind.annotation.adapters;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:jakarta/xml/bind/annotation/adapters/XmlJavaTypeAdapters.class */
@Target({ElementType.PACKAGE})
@Retention(RetentionPolicy.RUNTIME)
public @interface XmlJavaTypeAdapters {
    XmlJavaTypeAdapter[] value();
}
