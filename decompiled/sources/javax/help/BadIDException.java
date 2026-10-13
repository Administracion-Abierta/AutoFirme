package javax.help;

/* JADX INFO: loaded from: autofirma.jar:javax/help/BadIDException.class */
public class BadIDException extends IllegalArgumentException {
    private Map map;
    private String id;
    private HelpSet hs;

    public BadIDException(String str, Map map, String str2, HelpSet helpSet) {
        super(str);
        this.map = map;
        this.id = str2;
        this.hs = helpSet;
    }

    public Map getMap() {
        return this.map;
    }

    public String getID() {
        return this.id;
    }

    public HelpSet getHelpSet() {
        return this.hs;
    }
}
