package nu.xom;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:nu/xom/UnavailableCharacterException.class */
public class UnavailableCharacterException extends XMLException {
    private static final long serialVersionUID = -8280912714497572798L;
    private final char unavailableCharacter;
    private final String encoding;

    public UnavailableCharacterException(char c, String str) {
        super(new StringBuffer().append("Cannot use the character ").append(c).append(" (&#x").append(Integer.toHexString(c).toUpperCase()).append(";) in the ").append(str).append(" encoding.").toString());
        this.unavailableCharacter = c;
        this.encoding = str;
    }

    public char getCharacter() {
        return this.unavailableCharacter;
    }

    public String getEncoding() {
        return this.encoding;
    }
}
