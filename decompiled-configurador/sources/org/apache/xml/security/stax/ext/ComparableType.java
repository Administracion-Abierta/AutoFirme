package org.apache.xml.security.stax.ext;

import java.util.Objects;
import org.apache.xml.security.stax.ext.ComparableType;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/stax/ext/ComparableType.class */
public abstract class ComparableType<T extends ComparableType<T>> implements Comparable<T> {
    private final String name;

    public ComparableType(String name) {
        this.name = (String) Objects.requireNonNull(name, "name");
    }

    public String getName() {
        return this.name;
    }

    public String toString() {
        return this.name;
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (getClass().isAssignableFrom(obj.getClass()) && getName().equals(((ComparableType) obj).getName())) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return this.name.hashCode();
    }

    @Override // java.lang.Comparable
    public int compareTo(T o) {
        return getName().compareTo(o.getName());
    }
}
