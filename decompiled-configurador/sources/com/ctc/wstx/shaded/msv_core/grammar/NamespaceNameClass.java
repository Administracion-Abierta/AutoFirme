package com.ctc.wstx.shaded.msv_core.grammar;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/grammar/NamespaceNameClass.class */
public class NamespaceNameClass extends NameClass {
    public final String namespaceURI;
    private static final long serialVersionUID = 1;

    @Override // com.ctc.wstx.shaded.msv_core.grammar.NameClass
    public boolean accepts(String namespaceURI, String localName) {
        if ("*" == namespaceURI) {
            return true;
        }
        return this.namespaceURI.equals(namespaceURI);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.NameClass
    public Object visit(NameClassVisitor visitor) {
        return visitor.onNsName(this);
    }

    public NamespaceNameClass(String namespaceURI) {
        this.namespaceURI = namespaceURI;
    }

    public String toString() {
        return this.namespaceURI + ":*";
    }
}
