package com.twelvemonkeys.imageio.metadata;

import com.twelvemonkeys.lang.Validate;
import com.twelvemonkeys.util.CollectionUtil;
import java.lang.reflect.Array;
import java.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/metadata/AbstractEntry.class */
public abstract class AbstractEntry implements Entry {
    private final Object identifier;
    private final Object value;

    protected AbstractEntry(Object obj, Object obj2) {
        Validate.notNull(obj, "identifier");
        this.identifier = obj;
        this.value = obj2;
    }

    @Override // com.twelvemonkeys.imageio.metadata.Entry
    public final Object getIdentifier() {
        return this.identifier;
    }

    protected String getNativeIdentifier() {
        return String.valueOf(getIdentifier());
    }

    @Override // com.twelvemonkeys.imageio.metadata.Entry
    public String getFieldName() {
        return null;
    }

    @Override // com.twelvemonkeys.imageio.metadata.Entry
    public Object getValue() {
        return this.value;
    }

    @Override // com.twelvemonkeys.imageio.metadata.Entry
    public String getValueAsString() {
        int iValueCount = valueCount();
        if (iValueCount == 0 && this.value != null && this.value.getClass().isArray() && Array.getLength(this.value) == 0) {
            return "";
        }
        if (iValueCount > 1) {
            if (iValueCount < 16) {
                return arrayToString(this.value);
            }
            String strArrayToString = arrayToString(CollectionUtil.subArray(this.value, 0, 4));
            return String.format("%s ... %s (%d)", strArrayToString.substring(0, strArrayToString.length() - 1), arrayToString(CollectionUtil.subArray(this.value, iValueCount - 4, 4)).substring(1), Integer.valueOf(iValueCount));
        }
        if (this.value != null && this.value.getClass().isArray() && Array.getLength(this.value) == 1) {
            return String.valueOf(Array.get(this.value, 0));
        }
        return String.valueOf(this.value);
    }

    private static String arrayToString(Object obj) {
        Class<?> componentType = obj.getClass().getComponentType();
        if (componentType.isPrimitive()) {
            if (componentType.equals(Boolean.TYPE)) {
                return Arrays.toString((boolean[]) obj);
            }
            if (componentType.equals(Byte.TYPE)) {
                return Arrays.toString((byte[]) obj);
            }
            if (componentType.equals(Character.TYPE)) {
                return new String((char[]) obj);
            }
            if (componentType.equals(Double.TYPE)) {
                return Arrays.toString((double[]) obj);
            }
            if (componentType.equals(Float.TYPE)) {
                return Arrays.toString((float[]) obj);
            }
            if (componentType.equals(Integer.TYPE)) {
                return Arrays.toString((int[]) obj);
            }
            if (componentType.equals(Long.TYPE)) {
                return Arrays.toString((long[]) obj);
            }
            if (componentType.equals(Short.TYPE)) {
                return Arrays.toString((short[]) obj);
            }
            throw new AssertionError("Unknown type: " + componentType);
        }
        return Arrays.toString((Object[]) obj);
    }

    @Override // com.twelvemonkeys.imageio.metadata.Entry
    public String getTypeName() {
        if (this.value == null) {
            return null;
        }
        return this.value.getClass().getSimpleName();
    }

    @Override // com.twelvemonkeys.imageio.metadata.Entry
    public int valueCount() {
        if (this.value != null && this.value.getClass().isArray()) {
            return Array.getLength(this.value);
        }
        return 1;
    }

    public int hashCode() {
        return this.identifier.hashCode() + (this.value != null ? 31 * this.value.hashCode() : 0);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof AbstractEntry)) {
            return false;
        }
        AbstractEntry abstractEntry = (AbstractEntry) obj;
        return this.identifier.equals(abstractEntry.identifier) && ((this.value == null && abstractEntry.value == null) || (this.value != null && valueEquals(abstractEntry)));
    }

    private boolean valueEquals(AbstractEntry abstractEntry) {
        return this.value.getClass().isArray() ? arrayEquals(this.value, abstractEntry.value) : this.value.equals(abstractEntry.value);
    }

    static boolean arrayEquals(Object obj, Object obj2) {
        if (obj == obj2) {
            return true;
        }
        if (obj2 == null || obj == null || obj.getClass() != obj2.getClass()) {
            return false;
        }
        Class<?> componentType = obj.getClass().getComponentType();
        if (componentType.isPrimitive()) {
            if (obj instanceof byte[]) {
                return Arrays.equals((byte[]) obj, (byte[]) obj2);
            }
            if (obj instanceof char[]) {
                return Arrays.equals((char[]) obj, (char[]) obj2);
            }
            if (obj instanceof short[]) {
                return Arrays.equals((short[]) obj, (short[]) obj2);
            }
            if (obj instanceof int[]) {
                return Arrays.equals((int[]) obj, (int[]) obj2);
            }
            if (obj instanceof long[]) {
                return Arrays.equals((long[]) obj, (long[]) obj2);
            }
            if (obj instanceof boolean[]) {
                return Arrays.equals((boolean[]) obj, (boolean[]) obj2);
            }
            if (obj instanceof float[]) {
                return Arrays.equals((float[]) obj, (float[]) obj2);
            }
            if (obj instanceof double[]) {
                return Arrays.equals((double[]) obj, (double[]) obj2);
            }
            throw new AssertionError("Unsupported type:" + componentType);
        }
        return Arrays.equals((Object[]) obj, (Object[]) obj2);
    }

    public String toString() {
        String fieldName = getFieldName();
        String str = fieldName != null ? String.format("/%s", fieldName) : "";
        String typeName = getTypeName();
        return String.format("%s%s: %s%s", getNativeIdentifier(), str, getValueAsString(), typeName != null ? String.format(" (%s)", typeName) : "");
    }
}
