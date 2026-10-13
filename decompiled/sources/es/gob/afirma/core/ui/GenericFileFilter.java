package es.gob.afirma.core.ui;

import java.util.Arrays;
import java.util.Objects;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/core/ui/GenericFileFilter.class */
public final class GenericFileFilter {
    private final String[] exts;
    private final String description;

    public GenericFileFilter(String[] strArr, String str) {
        this.exts = strArr != null ? (String[]) strArr.clone() : null;
        this.description = str;
    }

    public String[] getExtensions() {
        if (this.exts != null) {
            return (String[]) this.exts.clone();
        }
        return null;
    }

    public String getDescription() {
        return this.description;
    }

    public boolean equals(Object obj) {
        if (obj instanceof GenericFileFilter) {
            return Arrays.equals(this.exts, ((GenericFileFilter) obj).getExtensions()) && Objects.equals(((GenericFileFilter) obj).getDescription(), this.description);
        }
        return super.equals(obj);
    }

    public int hashCode() {
        return super.hashCode();
    }
}
