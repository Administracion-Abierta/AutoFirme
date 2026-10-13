package nu.xom.jaxen.expr;

import java.util.HashSet;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/jaxen/expr/IdentitySet.class */
final class IdentitySet {
    private HashSet contents = new HashSet();

    /* JADX INFO: loaded from: autofirma.jar:nu/xom/jaxen/expr/IdentitySet$IdentityWrapper.class */
    private static class IdentityWrapper {
        private Object object;

        IdentityWrapper(Object obj) {
            this.object = obj;
        }

        public boolean equals(Object obj) {
            return this.object == ((IdentityWrapper) obj).object;
        }

        public int hashCode() {
            return System.identityHashCode(this.object);
        }
    }

    IdentitySet() {
    }

    void add(Object obj) {
        this.contents.add(new IdentityWrapper(obj));
    }

    public boolean contains(Object obj) {
        return this.contents.contains(new IdentityWrapper(obj));
    }
}
