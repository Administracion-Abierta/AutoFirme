package org.mozilla.javascript.serialize;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Undefined;
import org.mozilla.javascript.UniqueTag;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/serialize/ScriptableInputStream.class */
public class ScriptableInputStream extends ObjectInputStream {
    private Scriptable scope;
    private ClassLoader classLoader;

    public ScriptableInputStream(InputStream in, Scriptable scope) throws IOException {
        super(in);
        this.scope = scope;
        enableResolveObject(true);
        Context cx = Context.getCurrentContext();
        if (cx != null) {
            this.classLoader = cx.getApplicationClassLoader();
        }
    }

    @Override // java.io.ObjectInputStream
    protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
        String name = desc.getName();
        if (this.classLoader != null) {
            try {
                return this.classLoader.loadClass(name);
            } catch (ClassNotFoundException e) {
            }
        }
        return super.resolveClass(desc);
    }

    @Override // java.io.ObjectInputStream
    protected Object resolveObject(Object obj) throws IOException {
        if (obj instanceof ScriptableOutputStream.PendingLookup) {
            String name = ((ScriptableOutputStream.PendingLookup) obj).getName();
            obj = ScriptableOutputStream.lookupQualifiedName(this.scope, name);
            if (obj == Scriptable.NOT_FOUND) {
                throw new IOException("Object " + name + " not found upon deserialization.");
            }
        } else if (obj instanceof UniqueTag) {
            obj = ((UniqueTag) obj).readResolve();
        } else if (obj instanceof Undefined) {
            obj = ((Undefined) obj).readResolve();
        }
        return obj;
    }
}
