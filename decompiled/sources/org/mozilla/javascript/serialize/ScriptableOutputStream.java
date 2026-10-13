package org.mozilla.javascript.serialize;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import org.apache.xml.security.utils.Constants;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.UniqueTag;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/serialize/ScriptableOutputStream.class */
public class ScriptableOutputStream extends ObjectOutputStream {
    private Scriptable scope;
    private Map<Object, String> table;

    public ScriptableOutputStream(OutputStream out, Scriptable scope) throws IOException {
        super(out);
        this.scope = scope;
        this.table = new HashMap();
        this.table.put(scope, "");
        enableReplaceObject(true);
        excludeStandardObjectNames();
    }

    public void excludeAllIds(Object[] ids) {
        for (Object id : ids) {
            if ((id instanceof String) && (this.scope.get((String) id, this.scope) instanceof Scriptable)) {
                addExcludedName((String) id);
            }
        }
    }

    public void addOptionalExcludedName(String name) {
        Object obj = lookupQualifiedName(this.scope, name);
        if (obj != null && obj != UniqueTag.NOT_FOUND) {
            if (!(obj instanceof Scriptable)) {
                throw new IllegalArgumentException("Object for excluded name " + name + " is not a Scriptable, it is " + obj.getClass().getName());
            }
            this.table.put(obj, name);
        }
    }

    public void addExcludedName(String name) {
        Object obj = lookupQualifiedName(this.scope, name);
        if (!(obj instanceof Scriptable)) {
            throw new IllegalArgumentException("Object for excluded name " + name + " not found.");
        }
        this.table.put(obj, name);
    }

    public boolean hasExcludedName(String name) {
        return this.table.get(name) != null;
    }

    public void removeExcludedName(String name) {
        this.table.remove(name);
    }

    public void excludeStandardObjectNames() {
        String[] names = {Constants._TAG_OBJECT, "Object.prototype", "Function", "Function.prototype", "String", "String.prototype", "Math", "Array", "Array.prototype", "Error", "Error.prototype", "Number", "Number.prototype", "Date", "Date.prototype", "RegExp", "RegExp.prototype", "Script", "Script.prototype", "Continuation", "Continuation.prototype"};
        for (String str : names) {
            addExcludedName(str);
        }
        String[] optionalNames = {"XML", "XML.prototype", "XMLList", "XMLList.prototype"};
        for (String str2 : optionalNames) {
            addOptionalExcludedName(str2);
        }
    }

    static Object lookupQualifiedName(Scriptable scope, String qualifiedName) {
        StringTokenizer st = new StringTokenizer(qualifiedName, ".");
        Object result = scope;
        while (st.hasMoreTokens()) {
            String s = st.nextToken();
            result = ScriptableObject.getProperty((Scriptable) result, s);
            if (result == null || !(result instanceof Scriptable)) {
                break;
            }
        }
        return result;
    }

    /* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/serialize/ScriptableOutputStream$PendingLookup.class */
    static class PendingLookup implements Serializable {
        private static final long serialVersionUID = -2692990309789917727L;
        private String name;

        PendingLookup(String name) {
            this.name = name;
        }

        String getName() {
            return this.name;
        }
    }

    @Override // java.io.ObjectOutputStream
    protected Object replaceObject(Object obj) throws IOException {
        String name = this.table.get(obj);
        if (name == null) {
            return obj;
        }
        return new PendingLookup(name);
    }
}
