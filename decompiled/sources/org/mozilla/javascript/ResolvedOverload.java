package org.mozilla.javascript;

import java.util.Arrays;

/* JADX INFO: compiled from: NativeJavaMethod.java */
/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/ResolvedOverload.class */
class ResolvedOverload {
    final Class<?>[] types;
    final int index;

    ResolvedOverload(Object[] args, int index) {
        this.index = index;
        this.types = new Class[args.length];
        int l = args.length;
        for (int i = 0; i < l; i++) {
            Object arg = args[i];
            arg = arg instanceof Wrapper ? ((Wrapper) arg).unwrap() : arg;
            this.types[i] = arg == null ? null : arg.getClass();
        }
    }

    boolean matches(Object[] args) {
        if (args.length != this.types.length) {
            return false;
        }
        int l = args.length;
        for (int i = 0; i < l; i++) {
            Object arg = args[i];
            if (arg instanceof Wrapper) {
                arg = ((Wrapper) arg).unwrap();
            }
            if (arg == null) {
                if (this.types[i] != null) {
                    return false;
                }
            } else if (arg.getClass() != this.types[i]) {
                return false;
            }
        }
        return true;
    }

    public boolean equals(Object other) {
        if (!(other instanceof ResolvedOverload)) {
            return false;
        }
        ResolvedOverload ovl = (ResolvedOverload) other;
        return Arrays.equals(this.types, ovl.types) && this.index == ovl.index;
    }

    public int hashCode() {
        return Arrays.hashCode(this.types);
    }
}
