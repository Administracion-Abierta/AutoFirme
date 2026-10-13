package jakarta.xml.bind.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:jakarta/xml/bind/annotation/XmlSeeAlso.class */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface XmlSeeAlso {
    Class[] value();
}
