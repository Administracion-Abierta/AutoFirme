package org.mozilla.javascript;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.Iterator;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/NativeCollectionIterator.class */
public class NativeCollectionIterator extends ES6Iterator {
    private static final long serialVersionUID = 7094840979404373443L;
    private String className;
    private Type type;
    private transient Iterator<Hashtable.Entry> iterator;

    /* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/NativeCollectionIterator$Type.class */
    enum Type {
        KEYS,
        VALUES,
        BOTH
    }

    static void init(ScriptableObject scope, String tag, boolean sealed) {
        ES6Iterator.init(scope, sealed, new NativeCollectionIterator(tag), tag);
    }

    public NativeCollectionIterator(String tag) {
        this.iterator = Collections.emptyIterator();
        this.className = tag;
        this.iterator = Collections.emptyIterator();
        this.type = Type.BOTH;
    }

    public NativeCollectionIterator(Scriptable scope, String className, Type type, Iterator<Hashtable.Entry> iterator) {
        super(scope, className);
        this.iterator = Collections.emptyIterator();
        this.className = className;
        this.iterator = iterator;
        this.type = type;
    }

    @Override // org.mozilla.javascript.ScriptableObject, org.mozilla.javascript.Scriptable
    public String getClassName() {
        return this.className;
    }

    @Override // org.mozilla.javascript.ES6Iterator
    protected boolean isDone(Context cx, Scriptable scope) {
        return !this.iterator.hasNext();
    }

    @Override // org.mozilla.javascript.ES6Iterator
    protected Object nextValue(Context cx, Scriptable scope) {
        Hashtable.Entry e = this.iterator.next();
        switch (this.type) {
            case KEYS:
                return e.key;
            case VALUES:
                return e.value;
            case BOTH:
                return cx.newArray(scope, new Object[]{e.key, e.value});
            default:
                throw new AssertionError();
        }
    }

    private void readObject(ObjectInputStream stream) throws ClassNotFoundException, IOException {
        stream.defaultReadObject();
        this.className = (String) stream.readObject();
        this.type = (Type) stream.readObject();
        this.iterator = Collections.emptyIterator();
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        stream.writeObject(this.className);
        stream.writeObject(this.type);
    }
}
