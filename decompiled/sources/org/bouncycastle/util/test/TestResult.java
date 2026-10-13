package org.bouncycastle.util.test;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/util/test/TestResult.class */
public interface TestResult {
    boolean isSuccessful();

    Throwable getException();

    String toString();
}
