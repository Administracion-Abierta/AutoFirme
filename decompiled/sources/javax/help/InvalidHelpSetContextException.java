package javax.help;

/* JADX INFO: loaded from: autofirma.jar:javax/help/InvalidHelpSetContextException.class */
public class InvalidHelpSetContextException extends Exception {
    private HelpSet context;
    private HelpSet hs;

    public InvalidHelpSetContextException(String str, HelpSet helpSet, HelpSet helpSet2) {
        super(str);
        this.context = helpSet;
        this.hs = helpSet2;
    }

    public HelpSet getContext() {
        return this.context;
    }

    public HelpSet getHelpSet() {
        return this.hs;
    }
}
