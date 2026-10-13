package org.mozilla.javascript;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/SlotMap.class */
public interface SlotMap extends Iterable<ScriptableObject.Slot> {
    int size();

    boolean isEmpty();

    ScriptableObject.Slot get(Object obj, int i, ScriptableObject.SlotAccess slotAccess);

    ScriptableObject.Slot query(Object obj, int i);

    void addSlot(ScriptableObject.Slot slot);

    void remove(Object obj, int i);
}
