package org.mozilla.javascript;

import java.util.Iterator;
import java.util.LinkedHashMap;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/HashSlotMap.class */
public class HashSlotMap implements SlotMap {
    private final LinkedHashMap<Object, ScriptableObject.Slot> map = new LinkedHashMap<>();

    @Override // org.mozilla.javascript.SlotMap
    public int size() {
        return this.map.size();
    }

    @Override // org.mozilla.javascript.SlotMap
    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    @Override // org.mozilla.javascript.SlotMap
    public ScriptableObject.Slot query(Object key, int index) {
        Object name = key == null ? String.valueOf(index) : key;
        return this.map.get(name);
    }

    @Override // org.mozilla.javascript.SlotMap
    public ScriptableObject.Slot get(Object key, int index, ScriptableObject.SlotAccess accessType) {
        Object name = key == null ? String.valueOf(index) : key;
        ScriptableObject.Slot slot = this.map.get(name);
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
        return createSlot(key, index, name, accessType);
    }

    private ScriptableObject.Slot createSlot(Object key, int index, Object name, ScriptableObject.SlotAccess accessType) {
        ScriptableObject.Slot newSlot;
        ScriptableObject.Slot slot = this.map.get(name);
        if (slot != null) {
            if (accessType == ScriptableObject.SlotAccess.MODIFY_GETTER_SETTER && !(slot instanceof ScriptableObject.GetterSlot)) {
                newSlot = new ScriptableObject.GetterSlot(name, slot.indexOrHash, slot.getAttributes());
            } else if (accessType == ScriptableObject.SlotAccess.CONVERT_ACCESSOR_TO_DATA && (slot instanceof ScriptableObject.GetterSlot)) {
                newSlot = new ScriptableObject.Slot(name, slot.indexOrHash, slot.getAttributes());
            } else {
                if (accessType == ScriptableObject.SlotAccess.MODIFY_CONST) {
                    return null;
                }
                return slot;
            }
            newSlot.value = slot.value;
            this.map.put(name, newSlot);
            return newSlot;
        }
        ScriptableObject.Slot newSlot2 = accessType == ScriptableObject.SlotAccess.MODIFY_GETTER_SETTER ? new ScriptableObject.GetterSlot(key, index, 0) : new ScriptableObject.Slot(key, index, 0);
        if (accessType == ScriptableObject.SlotAccess.MODIFY_CONST) {
            newSlot2.setAttributes(13);
        }
        addSlot(newSlot2);
        return newSlot2;
    }

    @Override // org.mozilla.javascript.SlotMap
    public void addSlot(ScriptableObject.Slot newSlot) {
        Object name = newSlot.name == null ? String.valueOf(newSlot.indexOrHash) : newSlot.name;
        this.map.put(name, newSlot);
    }

    @Override // org.mozilla.javascript.SlotMap
    public void remove(Object key, int index) {
        Object name = key == null ? String.valueOf(index) : key;
        ScriptableObject.Slot slot = this.map.get(name);
        if (slot != null) {
            if ((slot.getAttributes() & 4) != 0) {
                Context cx = Context.getContext();
                if (cx.isStrictMode()) {
                    throw ScriptRuntime.typeError1("msg.delete.property.with.configurable.false", key);
                }
                return;
            }
            this.map.remove(name);
        }
    }

    @Override // java.lang.Iterable
    public Iterator<ScriptableObject.Slot> iterator() {
        return this.map.values().iterator();
    }
}
