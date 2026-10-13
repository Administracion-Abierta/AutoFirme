package es.uji.crypto.xades.jxades.util;

import java.io.IOException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/uji/crypto/xades/jxades/util/ObjectId.class */
public class ObjectId implements Comparable<ObjectId> {
    protected final int[] components;
    private final int hashCode;
    private String toString;

    public ObjectId(int[] components) {
        if (components == null || components.length <= 0) {
            throw new IllegalArgumentException("Components[] parameter can not be NULL or empty.");
        }
        this.components = (int[]) components.clone();
        int componentLen = components.length;
        int hc = componentLen;
        for (int i : components) {
            hc += i * 37;
        }
        this.hashCode = hc;
    }

    public ObjectId(String componentsString) throws IOException {
        this(parseObjectIdentifierString(componentsString));
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        return (other instanceof ObjectId) && compareTo((ObjectId) other) == 0;
    }

    @Override // java.lang.Comparable
    public int compareTo(ObjectId other) {
        int size;
        if (this.components.length < other.components.length) {
            size = this.components.length;
        } else {
            size = other.components.length;
        }
        for (int i = 0; i < size; i++) {
            int result = this.components[i] - other.components[i];
            if (result != 0) {
                return result;
            }
        }
        return this.components.length - other.components.length;
    }

    public int[] getComponents() {
        return (int[]) this.components.clone();
    }

    public String toString() {
        if (this.toString == null) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < this.components.length; i++) {
                if (i > 0) {
                    sb.append('.');
                }
                sb.append(this.components[i]);
            }
            this.toString = sb.toString();
        }
        return this.toString;
    }

    public int hashCode() {
        return this.hashCode;
    }

    private static int[] parseObjectIdentifierString(String objId) throws IOException {
        try {
            String[] strValues = objId.split("\\.");
            int[] intValues = new int[strValues.length];
            for (int i = 0; i < strValues.length; i++) {
                intValues[i] = Integer.parseInt(strValues[i].trim());
            }
            return intValues;
        } catch (Exception ex) {
            throw new IOException("Invalid ObjectId format: " + objId + " - " + ex.toString());
        }
    }
}
