package jakarta.xml.bind.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/* JADX INFO: loaded from: autofirma.jar:jakarta/xml/bind/annotation/XmlSchema.class */
@Target({ElementType.PACKAGE})
@Retention(RetentionPolicy.RUNTIME)
public @interface XmlSchema {
    public static final String NO_LOCATION = "##generate";

    XmlNs[] xmlns() default {};

    String namespace() default "";

    XmlNsForm elementFormDefault() default XmlNsForm.UNSET;

    XmlNsForm attributeFormDefault() default XmlNsForm.UNSET;

    String location() default "##generate";
}
