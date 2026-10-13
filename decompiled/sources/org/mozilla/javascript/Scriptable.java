package org.mozilla.javascript;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/Scriptable.class */
public interface Scriptable {
    public static final Object NOT_FOUND = UniqueTag.NOT_FOUND;

    String getClassName();

    Object get(String str, Scriptable scriptable);

    Object get(int i, Scriptable scriptable);

    boolean has(String str, Scriptable scriptable);

    boolean has(int i, Scriptable scriptable);

    void put(String str, Scriptable scriptable, Object obj);

    void put(int i, Scriptable scriptable, Object obj);

    void delete(String str);

    void delete(int i);

    Scriptable getPrototype();

    void setPrototype(Scriptable scriptable);

    Scriptable getParentScope();

    void setParentScope(Scriptable scriptable);

    Object[] getIds();

    Object getDefaultValue(Class<?> cls);

    boolean hasInstance(Scriptable scriptable);
}
