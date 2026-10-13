package jakarta.xml.bind.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/* JADX INFO: loaded from: autofirma.jar:jakarta/xml/bind/annotation/XmlElement.class */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface XmlElement {

    /* JADX INFO: loaded from: autofirma.jar:jakarta/xml/bind/annotation/XmlElement$DEFAULT.class */
    public static final class DEFAULT {
    }

    String name() default "##default";

    boolean nillable() default false;

    boolean required() default false;

    String namespace() default "##default";

    String defaultValue() default "\u0000";

    Class type() default DEFAULT.class;
}
