package jakarta.xml.bind.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/* JADX INFO: loaded from: autofirma.jar:jakarta/xml/bind/annotation/XmlType.class */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface XmlType {

    /* JADX INFO: loaded from: autofirma.jar:jakarta/xml/bind/annotation/XmlType$DEFAULT.class */
    public static final class DEFAULT {
    }

    String name() default "##default";

    String[] propOrder() default {""};

    String namespace() default "##default";

    Class factoryClass() default DEFAULT.class;

    String factoryMethod() default "";
}
