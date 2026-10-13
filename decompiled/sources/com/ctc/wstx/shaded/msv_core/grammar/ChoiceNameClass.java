package com.ctc.wstx.shaded.msv_core.grammar;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/grammar/ChoiceNameClass.class */
public class ChoiceNameClass extends NameClass {
    public final NameClass nc1;
    public final NameClass nc2;
    private static final long serialVersionUID = 1;

    @Override // com.ctc.wstx.shaded.msv_core.grammar.NameClass
    public boolean accepts(String namespaceURI, String localPart) {
        return this.nc1.accepts(namespaceURI, localPart) || this.nc2.accepts(namespaceURI, localPart);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.NameClass
    public Object visit(NameClassVisitor visitor) {
        return visitor.onChoice(this);
    }

    public ChoiceNameClass(NameClass nc1, NameClass nc2) {
        this.nc1 = nc1;
        this.nc2 = nc2;
    }

    public String toString() {
        return '(' + this.nc1.toString() + '|' + this.nc2.toString() + ')';
    }
}
