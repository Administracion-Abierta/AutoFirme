package es.gob.afirma.standalone;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/CommandLineException.class */
public class CommandLineException extends Exception {
    private static final long serialVersionUID = 3756189019018723691L;
    private boolean usingGui;

    CommandLineException(String str) {
        super(str);
        this.usingGui = false;
    }

    CommandLineException(Throwable th) {
        super(th);
        this.usingGui = false;
    }

    CommandLineException(String str, Throwable th) {
        super(str, th);
        this.usingGui = false;
    }

    CommandLineException(String str, boolean z) {
        super(str);
        this.usingGui = false;
        this.usingGui = z;
    }

    CommandLineException(Throwable th, boolean z) {
        super(th);
        this.usingGui = false;
        this.usingGui = z;
    }

    CommandLineException(String str, Throwable th, boolean z) {
        super(str, th);
        this.usingGui = false;
        this.usingGui = z;
    }

    public boolean isUsingGui() {
        return this.usingGui;
    }
}
