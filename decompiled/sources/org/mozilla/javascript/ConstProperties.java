package org.mozilla.javascript;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/ConstProperties.class */
public interface ConstProperties {
    void putConst(String str, Scriptable scriptable, Object obj);

    void defineConst(String str, Scriptable scriptable);

    boolean isConst(String str);
}
