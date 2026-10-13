package org.mozilla.javascript;

import java.util.Iterator;
import java.util.concurrent.locks.StampedLock;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/ThreadSafeSlotMapContainer.class */
class ThreadSafeSlotMapContainer extends SlotMapContainer {
    private final StampedLock lock;
    static final /* synthetic */ boolean $assertionsDisabled;

    static {
        $assertionsDisabled = !ThreadSafeSlotMapContainer.class.desiredAssertionStatus();
    }

    ThreadSafeSlotMapContainer(int initialSize) {
        super(initialSize);
        this.lock = new StampedLock();
    }

    @Override // org.mozilla.javascript.SlotMapContainer, org.mozilla.javascript.SlotMap
    public int size() {
        long stamp = this.lock.tryOptimisticRead();
        int s = this.map.size();
        if (this.lock.validate(stamp)) {
            return s;
        }
        long stamp2 = this.lock.readLock();
        try {
            return this.map.size();
        } finally {
            this.lock.unlockRead(stamp2);
        }
    }

    @Override // org.mozilla.javascript.SlotMapContainer
    public int dirtySize() {
        if ($assertionsDisabled || this.lock.isReadLocked()) {
            return this.map.size();
        }
        throw new AssertionError();
    }

    @Override // org.mozilla.javascript.SlotMapContainer, org.mozilla.javascript.SlotMap
    public boolean isEmpty() {
        long stamp = this.lock.tryOptimisticRead();
        boolean e = this.map.isEmpty();
        if (this.lock.validate(stamp)) {
            return e;
        }
        long stamp2 = this.lock.readLock();
        try {
            return this.map.isEmpty();
        } finally {
            this.lock.unlockRead(stamp2);
        }
    }

    @Override // org.mozilla.javascript.SlotMapContainer, org.mozilla.javascript.SlotMap
    public ScriptableObject.Slot get(Object key, int index, ScriptableObject.SlotAccess accessType) {
        long stamp = this.lock.writeLock();
        try {
            if (accessType != ScriptableObject.SlotAccess.QUERY) {
                checkMapSize();
            }
            return this.map.get(key, index, accessType);
        } finally {
            this.lock.unlockWrite(stamp);
        }
    }

    @Override // org.mozilla.javascript.SlotMapContainer, org.mozilla.javascript.SlotMap
    public ScriptableObject.Slot query(Object key, int index) {
        long stamp = this.lock.tryOptimisticRead();
        ScriptableObject.Slot s = this.map.query(key, index);
        if (this.lock.validate(stamp)) {
            return s;
        }
        long stamp2 = this.lock.readLock();
        try {
            return this.map.query(key, index);
        } finally {
            this.lock.unlockRead(stamp2);
        }
    }

    @Override // org.mozilla.javascript.SlotMapContainer, org.mozilla.javascript.SlotMap
    public void addSlot(ScriptableObject.Slot newSlot) {
        long stamp = this.lock.writeLock();
        try {
            checkMapSize();
            this.map.addSlot(newSlot);
        } finally {
            this.lock.unlockWrite(stamp);
        }
    }

    @Override // org.mozilla.javascript.SlotMapContainer, org.mozilla.javascript.SlotMap
    public void remove(Object key, int index) {
        long stamp = this.lock.writeLock();
        try {
            this.map.remove(key, index);
        } finally {
            this.lock.unlockWrite(stamp);
        }
    }

    @Override // org.mozilla.javascript.SlotMapContainer
    public long readLock() {
        return this.lock.readLock();
    }

    @Override // org.mozilla.javascript.SlotMapContainer
    public void unlockRead(long stamp) {
        this.lock.unlockRead(stamp);
    }

    @Override // org.mozilla.javascript.SlotMapContainer, java.lang.Iterable
    public Iterator<ScriptableObject.Slot> iterator() {
        if ($assertionsDisabled || this.lock.isReadLocked()) {
            return this.map.iterator();
        }
        throw new AssertionError();
    }

    @Override // org.mozilla.javascript.SlotMapContainer
    protected void checkMapSize() {
        if (!$assertionsDisabled && !this.lock.isWriteLocked()) {
            throw new AssertionError();
        }
        super.checkMapSize();
    }
}
