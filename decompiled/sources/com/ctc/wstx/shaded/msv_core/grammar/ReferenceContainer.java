package com.ctc.wstx.shaded.msv_core.grammar;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/grammar/ReferenceContainer.class */
public abstract class ReferenceContainer implements Serializable {
    protected final Map impl = new HashMap();
    private static final long serialVersionUID = 1;

    protected abstract ReferenceExp createReference(String str);

    public final ReferenceExp _getOrCreate(String name) {
        Object o = this.impl.get(name);
        if (o != null) {
            return (ReferenceExp) o;
        }
        ReferenceExp exp = createReference(name);
        this.impl.put(name, exp);
        return exp;
    }

    public void redefine(String name, ReferenceExp newExp) {
        if (this.impl.put(name, newExp) == null) {
            throw new IllegalArgumentException();
        }
    }

    public final ReferenceExp _get(String name) {
        Object o = this.impl.get(name);
        if (o != null) {
            return (ReferenceExp) o;
        }
        return null;
    }

    public final Iterator iterator() {
        return this.impl.values().iterator();
    }

    public final ReferenceExp[] getAll() {
        ReferenceExp[] r = new ReferenceExp[size()];
        this.impl.values().toArray(r);
        return r;
    }

    public final ReferenceExp remove(String name) {
        return (ReferenceExp) this.impl.remove(name);
    }

    public final int size() {
        return this.impl.size();
    }
}
