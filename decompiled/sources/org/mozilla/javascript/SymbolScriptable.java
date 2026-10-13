package org.mozilla.javascript;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/SymbolScriptable.class */
public interface SymbolScriptable {
    Object get(Symbol symbol, Scriptable scriptable);

    boolean has(Symbol symbol, Scriptable scriptable);

    void put(Symbol symbol, Scriptable scriptable, Object obj);

    void delete(Symbol symbol);
}
