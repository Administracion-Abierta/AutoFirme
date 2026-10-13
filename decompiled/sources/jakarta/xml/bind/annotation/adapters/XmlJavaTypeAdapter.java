package jakarta.xml.bind.annotation.adapters;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/* JADX INFO: loaded from: autofirma.jar:jakarta/xml/bind/annotation/adapters/XmlJavaTypeAdapter.class */
@Target({ElementType.PACKAGE, ElementType.FIELD, ElementType.METHOD, ElementType.TYPE, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface XmlJavaTypeAdapter {

    /* JADX INFO: loaded from: autofirma.jar:jakarta/xml/bind/annotation/adapters/XmlJavaTypeAdapter$DEFAULT.class */
    public static final class DEFAULT {
    }

    Class<? extends XmlAdapter> value();

    Class type() default DEFAULT.class;
}
