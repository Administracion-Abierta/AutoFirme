package com.sun.jna;

import java.lang.reflect.Method;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/VarArgsChecker.class */
abstract class VarArgsChecker {
    abstract boolean isVarArgs(Method method);

    abstract int fixedArgs(Method method);

    private VarArgsChecker() {
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/VarArgsChecker$RealVarArgsChecker.class */
    private static final class RealVarArgsChecker extends VarArgsChecker {
        private RealVarArgsChecker() {
            super();
        }

        @Override // com.sun.jna.VarArgsChecker
        boolean isVarArgs(Method m) {
            return m.isVarArgs();
        }

        @Override // com.sun.jna.VarArgsChecker
        int fixedArgs(Method m) {
            if (m.isVarArgs()) {
                return m.getParameterTypes().length - 1;
            }
            return 0;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/VarArgsChecker$NoVarArgsChecker.class */
    private static final class NoVarArgsChecker extends VarArgsChecker {
        private NoVarArgsChecker() {
            super();
        }

        @Override // com.sun.jna.VarArgsChecker
        boolean isVarArgs(Method m) {
            return false;
        }

        @Override // com.sun.jna.VarArgsChecker
        int fixedArgs(Method m) {
            return 0;
        }
    }

    static VarArgsChecker create() {
        try {
            Method isVarArgsMethod = Method.class.getMethod("isVarArgs", new Class[0]);
            if (isVarArgsMethod != null) {
                return new RealVarArgsChecker();
            }
            return new NoVarArgsChecker();
        } catch (NoSuchMethodException e) {
            return new NoVarArgsChecker();
        } catch (SecurityException e2) {
            return new NoVarArgsChecker();
        }
    }
}
