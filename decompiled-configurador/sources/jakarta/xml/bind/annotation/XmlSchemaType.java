package jakarta.xml.bind.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:jakarta/xml/bind/annotation/XmlSchemaType.class */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PACKAGE})
@Retention(RetentionPolicy.RUNTIME)
public @interface XmlSchemaType {

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:jakarta/xml/bind/annotation/XmlSchemaType$DEFAULT.class */
    public static final class DEFAULT {
    }

    String name();

    String namespace() default "http://www.w3.org/2001/XMLSchema";

    Class type() default DEFAULT.class;
}
