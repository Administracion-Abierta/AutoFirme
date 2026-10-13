package org.spongycastle.util.test;

import java.io.PrintStream;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/util/test/SimpleTest.class */
public abstract class SimpleTest implements Test {
    @Override // org.spongycastle.util.test.Test
    public abstract String getName();

    public abstract void performTest() throws Exception;

    private TestResult success() {
        return SimpleTestResult.successful(this, "Okay");
    }

    protected void fail(String message) {
        throw new TestFailedException(SimpleTestResult.failed(this, message));
    }

    protected void isTrue(boolean value) {
        if (!value) {
            throw new TestFailedException(SimpleTestResult.failed(this, "no message"));
        }
    }

    protected void isTrue(String message, boolean value) {
        if (!value) {
            throw new TestFailedException(SimpleTestResult.failed(this, message));
        }
    }

    protected void isEquals(Object a, Object b) {
        if (!a.equals(b)) {
            throw new TestFailedException(SimpleTestResult.failed(this, "no message"));
        }
    }

    protected void isEquals(int a, int b) {
        if (a != b) {
            throw new TestFailedException(SimpleTestResult.failed(this, "no message"));
        }
    }

    protected void isEquals(long a, long b) {
        if (a != b) {
            throw new TestFailedException(SimpleTestResult.failed(this, "no message"));
        }
    }

    protected void isEquals(String message, boolean a, boolean b) {
        if (a != b) {
            throw new TestFailedException(SimpleTestResult.failed(this, message));
        }
    }

    protected void isEquals(String message, long a, long b) {
        if (a != b) {
            throw new TestFailedException(SimpleTestResult.failed(this, message));
        }
    }

    protected void isEquals(String message, Object a, Object b) {
        if (a == null && b == null) {
            return;
        }
        if (a == null) {
            throw new TestFailedException(SimpleTestResult.failed(this, message));
        }
        if (b == null) {
            throw new TestFailedException(SimpleTestResult.failed(this, message));
        }
        if (!a.equals(b)) {
            throw new TestFailedException(SimpleTestResult.failed(this, message));
        }
    }

    protected boolean areEqual(byte[][] left, byte[][] right) {
        if (left == null && right == null) {
            return true;
        }
        if (left == null || right == null || left.length != right.length) {
            return false;
        }
        for (int t = 0; t < left.length; t++) {
            if (!areEqual(left[t], right[t])) {
                return false;
            }
        }
        return true;
    }

    protected void fail(String message, Throwable throwable) {
        throw new TestFailedException(SimpleTestResult.failed(this, message, throwable));
    }

    protected void fail(String message, Object expected, Object found) {
        throw new TestFailedException(SimpleTestResult.failed(this, message, expected, found));
    }

    protected boolean areEqual(byte[] a, byte[] b) {
        return Arrays.areEqual(a, b);
    }

    @Override // org.spongycastle.util.test.Test
    public TestResult perform() {
        try {
            performTest();
            return success();
        } catch (TestFailedException e) {
            return e.getResult();
        } catch (Exception e2) {
            return SimpleTestResult.failed(this, "Exception: " + e2, e2);
        }
    }

    protected static void runTest(Test test) {
        runTest(test, System.out);
    }

    protected static void runTest(Test test, PrintStream out) {
        TestResult result = test.perform();
        out.println(result.toString());
        if (result.getException() != null) {
            result.getException().printStackTrace(out);
        }
    }
}
