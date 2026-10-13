package jakarta.xml.bind.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/* JADX INFO: loaded from: autofirma.jar:jakarta/xml/bind/annotation/XmlElementRef.class */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface XmlElementRef {

    /* JADX INFO: loaded from: autofirma.jar:jakarta/xml/bind/annotation/XmlElementRef$DEFAULT.class */
    public static final class DEFAULT {
    }

    Class type() default DEFAULT.class;

    String namespace() default "";

    String name() default "##default";

    boolean required() default true;
}
