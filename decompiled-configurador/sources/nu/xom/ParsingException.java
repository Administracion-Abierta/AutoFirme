package nu.xom;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:nu/xom/ParsingException.class */
public class ParsingException extends Exception {
    private static final long serialVersionUID = -5185450480590174490L;
    private Throwable cause;
    private int lineNumber;
    private int columnNumber;
    private String uri;
    private boolean causeSet;

    public ParsingException(String str, Throwable th) {
        super(str);
        this.lineNumber = -1;
        this.columnNumber = -1;
        this.causeSet = false;
        initCause(th);
    }

    public ParsingException(String str, String str2, Throwable th) {
        super(str);
        this.lineNumber = -1;
        this.columnNumber = -1;
        this.causeSet = false;
        this.uri = str2;
        initCause(th);
    }

    public ParsingException(String str, int i, int i2) {
        this(str, null, i, i2, null);
    }

    public ParsingException(String str, String str2, int i, int i2) {
        this(str, str2, i, i2, null);
    }

    public ParsingException(String str, String str2, int i, int i2, Throwable th) {
        super(str);
        this.lineNumber = -1;
        this.columnNumber = -1;
        this.causeSet = false;
        this.lineNumber = i;
        this.columnNumber = i2;
        this.uri = str2;
        initCause(th);
    }

    public ParsingException(String str, int i, int i2, Throwable th) {
        super(str);
        this.lineNumber = -1;
        this.columnNumber = -1;
        this.causeSet = false;
        this.lineNumber = i;
        this.columnNumber = i2;
        initCause(th);
    }

    public ParsingException(String str) {
        super(str);
        this.lineNumber = -1;
        this.columnNumber = -1;
        this.causeSet = false;
    }

    public int getLineNumber() {
        return this.lineNumber;
    }

    public int getColumnNumber() {
        return this.columnNumber;
    }

    public String getURI() {
        return this.uri;
    }

    @Override // java.lang.Throwable
    public final Throwable initCause(Throwable th) {
        if (this.causeSet) {
            throw new IllegalStateException("Can't overwrite cause");
        }
        if (th == this) {
            throw new IllegalArgumentException("Self-causation not permitted");
        }
        this.cause = th;
        this.causeSet = true;
        return this;
    }

    @Override // java.lang.Throwable
    public Throwable getCause() {
        return this.cause;
    }

    @Override // java.lang.Throwable
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer(super.toString());
        if (this.lineNumber >= 0) {
            stringBuffer.append(" at line ");
            stringBuffer.append(this.lineNumber);
            stringBuffer.append(", column ");
            stringBuffer.append(this.columnNumber);
        }
        if (this.uri != null) {
            stringBuffer.append(" in ");
            stringBuffer.append(this.uri);
        }
        return stringBuffer.toString();
    }
}
