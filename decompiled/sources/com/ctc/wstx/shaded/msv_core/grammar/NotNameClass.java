package com.ctc.wstx.shaded.msv_core.grammar;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/grammar/NotNameClass.class */
public final class NotNameClass extends NameClass {
    public final NameClass child;
    private static final long serialVersionUID = 1;

    @Override // com.ctc.wstx.shaded.msv_core.grammar.NameClass
    public boolean accepts(String namespaceURI, String localName) {
        return !this.child.accepts(namespaceURI, localName);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.NameClass
    public Object visit(NameClassVisitor visitor) {
        return visitor.onNot(this);
    }

    public NotNameClass(NameClass child) {
        this.child = child;
    }

    public String toString() {
        return "~" + this.child.toString();
    }
}
