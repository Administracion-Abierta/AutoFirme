package nu.xom.jaxen;

import java.io.PrintStream;
import java.io.PrintWriter;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:nu/xom/jaxen/JaxenRuntimeException.class */
public class JaxenRuntimeException extends RuntimeException {
    private static final long serialVersionUID = -930309761511911193L;
    private Throwable cause;
    private boolean causeSet;

    public JaxenRuntimeException(Throwable th) {
        super(th.getMessage());
        this.causeSet = false;
        initCause(th);
    }

    public JaxenRuntimeException(String str) {
        super(str);
        this.causeSet = false;
    }

    @Override // java.lang.Throwable
    public Throwable getCause() {
        return this.cause;
    }

    @Override // java.lang.Throwable
    public Throwable initCause(Throwable th) {
        if (this.causeSet) {
            throw new IllegalStateException("Cause cannot be reset");
        }
        if (th == this) {
            throw new IllegalArgumentException("Exception cannot be its own cause");
        }
        this.causeSet = true;
        this.cause = th;
        return this;
    }

    @Override // java.lang.Throwable
    public void printStackTrace(PrintStream printStream) {
        super.printStackTrace(printStream);
        if (JaxenException.javaVersion >= 1.4d || getCause() == null) {
            return;
        }
        printStream.print("Caused by: ");
        getCause().printStackTrace(printStream);
    }

    @Override // java.lang.Throwable
    public void printStackTrace(PrintWriter printWriter) {
        super.printStackTrace(printWriter);
        if (JaxenException.javaVersion >= 1.4d || getCause() == null) {
            return;
        }
        printWriter.print("Caused by: ");
        getCause().printStackTrace(printWriter);
    }
}
