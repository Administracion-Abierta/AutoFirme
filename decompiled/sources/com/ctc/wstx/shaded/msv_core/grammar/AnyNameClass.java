package com.ctc.wstx.shaded.msv_core.grammar;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/grammar/AnyNameClass.class */
public final class AnyNameClass extends NameClass {
    public static final NameClass theInstance = new AnyNameClass();
    private static final long serialVersionUID = 1;

    @Override // com.ctc.wstx.shaded.msv_core.grammar.NameClass
    public boolean accepts(String namespaceURI, String localName) {
        return true;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.NameClass
    public Object visit(NameClassVisitor visitor) {
        return visitor.onAnyName(this);
    }

    protected AnyNameClass() {
    }

    public String toString() {
        return "*:*";
    }

    private Object readResolve() {
        return theInstance;
    }
}
