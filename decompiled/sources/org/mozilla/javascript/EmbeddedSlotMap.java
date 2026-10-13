package org.mozilla.javascript;

import java.util.Iterator;
import java.util.NoSuchElementException;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/EmbeddedSlotMap.class */
public class EmbeddedSlotMap implements SlotMap {
    private ScriptableObject.Slot[] slots;
    private ScriptableObject.Slot firstAdded;
    private ScriptableObject.Slot lastAdded;
    private int count;
    private static final int INITIAL_SLOT_SIZE = 4;

    /* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/EmbeddedSlotMap$Iter.class */
    private static final class Iter implements Iterator<ScriptableObject.Slot> {
        private ScriptableObject.Slot next;

        Iter(ScriptableObject.Slot slot) {
            this.next = slot;
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return this.next != null;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.Iterator
        public ScriptableObject.Slot next() {
            ScriptableObject.Slot ret = this.next;
            if (ret == null) {
                throw new NoSuchElementException();
            }
            this.next = this.next.orderedNext;
            return ret;
        }
    }

    @Override // org.mozilla.javascript.SlotMap
    public int size() {
        return this.count;
    }

    @Override // org.mozilla.javascript.SlotMap
    public boolean isEmpty() {
        return this.count == 0;
    }

    @Override // java.lang.Iterable
    public Iterator<ScriptableObject.Slot> iterator() {
        return new Iter(this.firstAdded);
    }

    @Override // org.mozilla.javascript.SlotMap
    public ScriptableObject.Slot query(Object key, int index) {
        if (this.slots == null) {
            return null;
        }
        int indexOrHash = key != null ? key.hashCode() : index;
        int slotIndex = getSlotIndex(this.slots.length, indexOrHash);
        ScriptableObject.Slot slot = this.slots[slotIndex];
        while (true) {
            ScriptableObject.Slot slot2 = slot;
            if (slot2 != null) {
                Object skey = slot2.name;
                if (indexOrHash != slot2.indexOrHash || (skey != key && (key == null || !key.equals(skey)))) {
                    slot = slot2.next;
                } else {
                    return slot2;
                }
            } else {
                return null;
            }
        }
    }

    @Override // org.mozilla.javascript.SlotMap
    public ScriptableObject.Slot get(Object key, int index, ScriptableObject.SlotAccess accessType) {
        if (this.slots == null && accessType == ScriptableObject.SlotAccess.QUERY) {
            return null;
        }
        int indexOrHash = key != null ? key.hashCode() : index;
        ScriptableObject.Slot slot = null;
        if (this.slots != null) {
            int slotIndex = getSlotIndex(this.slots.length, indexOrHash);
            ScriptableObject.Slot slot2 = this.slots[slotIndex];
            while (true) {
                slot = slot2;
                if (slot == null) {
                    break;
                }
                Object skey = slot.name;
                if (indexOrHash == slot.indexOrHash && (skey == key || (key != null && key.equals(skey)))) {
                    break;
                }
                slot2 = slot.next;
            }
            switch (accessType) {
                case QUERY:
                    return slot;
                case MODIFY:
                case MODIFY_CONST:
                    if (slot != null) {
                        return slot;
                    }
                    break;
                case MODIFY_GETTER_SETTER:
                    if (slot instanceof ScriptableObject.GetterSlot) {
                        return slot;
                    }
                    break;
                case CONVERT_ACCESSOR_TO_DATA:
                    if (!(slot instanceof ScriptableObject.GetterSlot)) {
                        return slot;
                    }
                    break;
            }
        }
        return createSlot(key, indexOrHash, accessType, slot);
    }

    private ScriptableObject.Slot createSlot(Object key, int indexOrHash, ScriptableObject.SlotAccess accessType, ScriptableObject.Slot existingSlot) {
        ScriptableObject.Slot slot;
        ScriptableObject.Slot newSlot;
        ScriptableObject.Slot ps;
        if (this.count == 0) {
            this.slots = new ScriptableObject.Slot[4];
        } else if (existingSlot != null) {
            int insertPos = getSlotIndex(this.slots.length, indexOrHash);
            ScriptableObject.Slot prev = this.slots[insertPos];
            ScriptableObject.Slot slot2 = prev;
            while (true) {
                slot = slot2;
                if (slot == null || (slot.indexOrHash == indexOrHash && (slot.name == key || (key != null && key.equals(slot.name))))) {
                    break;
                }
                prev = slot;
                slot2 = slot.next;
            }
            if (slot != null) {
                if (accessType == ScriptableObject.SlotAccess.MODIFY_GETTER_SETTER && !(slot instanceof ScriptableObject.GetterSlot)) {
                    newSlot = new ScriptableObject.GetterSlot(key, indexOrHash, slot.getAttributes());
                } else if (accessType == ScriptableObject.SlotAccess.CONVERT_ACCESSOR_TO_DATA && (slot instanceof ScriptableObject.GetterSlot)) {
                    newSlot = new ScriptableObject.Slot(key, indexOrHash, slot.getAttributes());
                } else {
                    if (accessType == ScriptableObject.SlotAccess.MODIFY_CONST) {
                        return null;
                    }
                    return slot;
                }
                newSlot.value = slot.value;
                newSlot.next = slot.next;
                if (slot == this.firstAdded) {
                    this.firstAdded = newSlot;
                } else {
                    ScriptableObject.Slot slot3 = this.firstAdded;
                    while (true) {
                        ps = slot3;
                        if (ps == null || ps.orderedNext == slot) {
                            break;
                        }
                        slot3 = ps.orderedNext;
                    }
                    if (ps != null) {
                        ps.orderedNext = newSlot;
                    }
                }
                newSlot.orderedNext = slot.orderedNext;
                if (slot == this.lastAdded) {
                    this.lastAdded = newSlot;
                }
                if (prev == slot) {
                    this.slots[insertPos] = newSlot;
                } else {
                    prev.next = newSlot;
                }
                return newSlot;
            }
        }
        if (4 * (this.count + 1) > 3 * this.slots.length) {
            ScriptableObject.Slot[] newSlots = new ScriptableObject.Slot[this.slots.length * 2];
            copyTable(this.slots, newSlots);
            this.slots = newSlots;
        }
        ScriptableObject.Slot newSlot2 = accessType == ScriptableObject.SlotAccess.MODIFY_GETTER_SETTER ? new ScriptableObject.GetterSlot(key, indexOrHash, 0) : new ScriptableObject.Slot(key, indexOrHash, 0);
        if (accessType == ScriptableObject.SlotAccess.MODIFY_CONST) {
            newSlot2.setAttributes(13);
        }
        insertNewSlot(newSlot2);
        return newSlot2;
    }

    @Override // org.mozilla.javascript.SlotMap
    public void addSlot(ScriptableObject.Slot newSlot) {
        if (this.slots == null) {
            this.slots = new ScriptableObject.Slot[4];
        }
        insertNewSlot(newSlot);
    }

    private void insertNewSlot(ScriptableObject.Slot newSlot) {
        this.count++;
        if (this.lastAdded != null) {
            this.lastAdded.orderedNext = newSlot;
        }
        if (this.firstAdded == null) {
            this.firstAdded = newSlot;
        }
        this.lastAdded = newSlot;
        addKnownAbsentSlot(this.slots, newSlot);
    }

    @Override // org.mozilla.javascript.SlotMap
    public void remove(Object key, int index) {
        ScriptableObject.Slot slot;
        ScriptableObject.Slot prev;
        int indexOrHash = key != null ? key.hashCode() : index;
        if (this.count != 0) {
            int slotIndex = getSlotIndex(this.slots.length, indexOrHash);
            ScriptableObject.Slot prev2 = this.slots[slotIndex];
            ScriptableObject.Slot slot2 = prev2;
            while (true) {
                slot = slot2;
                if (slot == null || (slot.indexOrHash == indexOrHash && (slot.name == key || (key != null && key.equals(slot.name))))) {
                    break;
                }
                prev2 = slot;
                slot2 = slot.next;
            }
            if (slot != null) {
                if ((slot.getAttributes() & 4) != 0) {
                    Context cx = Context.getContext();
                    if (cx.isStrictMode()) {
                        throw ScriptRuntime.typeError1("msg.delete.property.with.configurable.false", key);
                    }
                    return;
                }
                this.count--;
                if (prev2 == slot) {
                    this.slots[slotIndex] = slot.next;
                } else {
                    prev2.next = slot.next;
                }
                if (slot == this.firstAdded) {
                    prev = null;
                    this.firstAdded = slot.orderedNext;
                } else {
                    ScriptableObject.Slot slot3 = this.firstAdded;
                    while (true) {
                        prev = slot3;
                        if (prev.orderedNext == slot) {
                            break;
                        } else {
                            slot3 = prev.orderedNext;
                        }
                    }
                    prev.orderedNext = slot.orderedNext;
                }
                if (slot == this.lastAdded) {
                    this.lastAdded = prev;
                }
            }
        }
    }

    private static void copyTable(ScriptableObject.Slot[] oldSlots, ScriptableObject.Slot[] newSlots) {
        for (ScriptableObject.Slot slot : oldSlots) {
            while (true) {
                ScriptableObject.Slot slot2 = slot;
                if (slot2 != null) {
                    ScriptableObject.Slot nextSlot = slot2.next;
                    slot2.next = null;
                    addKnownAbsentSlot(newSlots, slot2);
                    slot = nextSlot;
                }
            }
        }
    }

    private static void addKnownAbsentSlot(ScriptableObject.Slot[] addSlots, ScriptableObject.Slot slot) {
        int insertPos = getSlotIndex(addSlots.length, slot.indexOrHash);
        ScriptableObject.Slot old = addSlots[insertPos];
        addSlots[insertPos] = slot;
        slot.next = old;
    }

    private static int getSlotIndex(int tableSize, int indexOrHash) {
        return indexOrHash & (tableSize - 1);
    }
}
