package org.mozilla.javascript;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/Hashtable.class */
public class Hashtable implements Serializable, Iterable<Entry> {
    private static final long serialVersionUID = -7151554912419543747L;
    private final HashMap<Object, Entry> map = new HashMap<>();
    private Entry first = null;
    private Entry last = null;
    static final /* synthetic */ boolean $assertionsDisabled;

    static {
        $assertionsDisabled = !Hashtable.class.desiredAssertionStatus();
    }

    /* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/Hashtable$Entry.class */
    public static final class Entry implements Serializable {
        private static final long serialVersionUID = 4086572107122965503L;
        protected Object key;
        protected Object value;
        protected boolean deleted;
        protected Entry next;
        protected Entry prev;
        private final int hashCode;

        Entry() {
            this.hashCode = 0;
        }

        Entry(Object k, Object value) {
            if ((k instanceof Number) && !(k instanceof Double)) {
                this.key = Double.valueOf(((Number) k).doubleValue());
            } else if (k instanceof ConsString) {
                this.key = k.toString();
            } else {
                this.key = k;
            }
            if (this.key == null || k.equals(ScriptRuntime.negativeZeroObj)) {
                this.hashCode = 0;
            } else {
                this.hashCode = this.key.hashCode();
            }
            this.value = value;
        }

        public Object key() {
            return this.key;
        }

        public Object value() {
            return this.value;
        }

        Object clear() {
            Object ret = this.value;
            this.key = Undefined.instance;
            this.value = Undefined.instance;
            this.deleted = true;
            return ret;
        }

        public int hashCode() {
            return this.hashCode;
        }

        public boolean equals(Object o) {
            if (o == null) {
                return false;
            }
            try {
                return ScriptRuntime.sameZero(this.key, ((Entry) o).key);
            } catch (ClassCastException e) {
                return false;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static Entry makeDummy() {
        Entry d = new Entry();
        d.clear();
        return d;
    }

    public int size() {
        return this.map.size();
    }

    public void put(Object key, Object value) {
        Entry nv = new Entry(key, value);
        Entry ev = this.map.putIfAbsent(nv, nv);
        if (ev == null) {
            if (this.first == null) {
                this.last = nv;
                this.first = nv;
                return;
            } else {
                this.last.next = nv;
                nv.prev = this.last;
                this.last = nv;
                return;
            }
        }
        ev.value = value;
    }

    public Object get(Object key) {
        Entry e = new Entry(key, null);
        Entry v = this.map.get(e);
        if (v == null) {
            return null;
        }
        return v.value;
    }

    public boolean has(Object key) {
        Entry e = new Entry(key, null);
        return this.map.containsKey(e);
    }

    public Object delete(Object key) {
        Entry e = new Entry(key, null);
        Entry v = this.map.remove(e);
        if (v == null) {
            return null;
        }
        if (v == this.first) {
            if (v == this.last) {
                v.clear();
                v.prev = null;
            } else {
                this.first = v.next;
                this.first.prev = null;
                if (this.first.next != null) {
                    this.first.next.prev = this.first;
                }
            }
        } else {
            Entry prev = v.prev;
            prev.next = v.next;
            v.prev = null;
            if (v.next != null) {
                v.next.prev = prev;
            } else {
                if (!$assertionsDisabled && v != this.last) {
                    throw new AssertionError();
                }
                this.last = prev;
            }
        }
        return v.clear();
    }

    public void clear() {
        Iterator<Entry> it = iterator();
        it.forEachRemaining((v0) -> {
            v0.clear();
        });
        if (this.first != null) {
            Entry dummy = makeDummy();
            this.last.next = dummy;
            this.last = dummy;
            this.first = dummy;
        }
        this.map.clear();
    }

    @Override // java.lang.Iterable
    public Iterator<Entry> iterator() {
        return new Iter(this.first);
    }

    /* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/Hashtable$Iter.class */
    private static final class Iter implements Iterator<Entry> {
        private Entry pos;

        Iter(Entry start) {
            Entry dummy = Hashtable.makeDummy();
            dummy.next = start;
            this.pos = dummy;
        }

        private void skipDeleted() {
            while (this.pos.next != null && this.pos.next.deleted) {
                this.pos = this.pos.next;
            }
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            skipDeleted();
            return (this.pos == null || this.pos.next == null) ? false : true;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.Iterator
        public Entry next() {
            skipDeleted();
            if (this.pos == null || this.pos.next == null) {
                throw new NoSuchElementException();
            }
            Entry e = this.pos.next;
            this.pos = this.pos.next;
            return e;
        }
    }
}
