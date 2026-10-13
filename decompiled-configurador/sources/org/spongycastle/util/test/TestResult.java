package org.spongycastle.util.test;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/util/test/TestResult.class */
public interface TestResult {
    boolean isSuccessful();

    Throwable getException();

    String toString();
}
