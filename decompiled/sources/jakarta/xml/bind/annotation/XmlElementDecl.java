package jakarta.xml.bind.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/* JADX INFO: loaded from: autofirma.jar:jakarta/xml/bind/annotation/XmlElementDecl.class */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface XmlElementDecl {

    /* JADX INFO: loaded from: autofirma.jar:jakarta/xml/bind/annotation/XmlElementDecl$GLOBAL.class */
    public static final class GLOBAL {
    }

    Class scope() default GLOBAL.class;

    String namespace() default "##default";

    String name();

    String substitutionHeadNamespace() default "##default";

    String substitutionHeadName() default "";

    String defaultValue() default "\u0000";
}
