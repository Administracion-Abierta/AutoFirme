package org.apache.fontbox.type1;

/* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/type1/Token.class */
class Token {
    static final Kind STRING = Kind.STRING;
    static final Kind NAME = Kind.NAME;
    static final Kind LITERAL = Kind.LITERAL;
    static final Kind REAL = Kind.REAL;
    static final Kind INTEGER = Kind.INTEGER;
    static final Kind START_ARRAY = Kind.START_ARRAY;
    static final Kind END_ARRAY = Kind.END_ARRAY;
    static final Kind START_PROC = Kind.START_PROC;
    static final Kind END_PROC = Kind.END_PROC;
    static final Kind CHARSTRING = Kind.CHARSTRING;
    static final Kind START_DICT = Kind.START_DICT;
    static final Kind END_DICT = Kind.END_DICT;
    private String text;
    private byte[] data;
    private final Kind kind;

    /* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/type1/Token$Kind.class */
    enum Kind {
        NONE,
        STRING,
        NAME,
        LITERAL,
        REAL,
        INTEGER,
        START_ARRAY,
        END_ARRAY,
        START_PROC,
        END_PROC,
        START_DICT,
        END_DICT,
        CHARSTRING
    }

    Token(String text, Kind type) {
        this.text = text;
        this.kind = type;
    }

    Token(char character, Kind type) {
        this.text = Character.toString(character);
        this.kind = type;
    }

    Token(byte[] data, Kind type) {
        this.data = data;
        this.kind = type;
    }

    public String getText() {
        return this.text;
    }

    public Kind getKind() {
        return this.kind;
    }

    public int intValue() {
        return (int) Float.parseFloat(this.text);
    }

    public float floatValue() {
        return Float.parseFloat(this.text);
    }

    public boolean booleanValue() {
        return this.text.equals("true");
    }

    public byte[] getData() {
        return this.data;
    }

    public String toString() {
        if (this.kind == CHARSTRING) {
            return "Token[kind=CHARSTRING, data=" + this.data.length + " bytes]";
        }
        return "Token[kind=" + this.kind + ", text=" + this.text + "]";
    }
}
