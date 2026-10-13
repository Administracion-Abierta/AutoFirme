package es.gob.afirma.standalone;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/CommandLineParameterException.class */
public final class CommandLineParameterException extends CommandLineException {
    private static final long serialVersionUID = 6635394222787127627L;

    CommandLineParameterException(String str) {
        super(str);
    }

    CommandLineParameterException(Throwable th) {
        super(th);
    }

    CommandLineParameterException(String str, Throwable th) {
        super(str, th);
    }

    CommandLineParameterException(String str, boolean z) {
        super(str, z);
    }

    CommandLineParameterException(Throwable th, boolean z) {
        super(th, z);
    }

    CommandLineParameterException(String str, Throwable th, boolean z) {
        super(str, th, z);
    }
}
