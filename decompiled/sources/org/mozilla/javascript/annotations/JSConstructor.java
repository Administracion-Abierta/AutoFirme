package org.mozilla.javascript.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/annotations/JSConstructor.class */
@Target({ElementType.CONSTRUCTOR, ElementType.METHOD})
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface JSConstructor {
}
