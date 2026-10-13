package com.ctc.wstx.shaded.msv_core.grammar;

import com.ctc.wstx.shaded.msv_core.util.StringPair;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/grammar/SimpleNameClass.class */
public final class SimpleNameClass extends NameClass {
    public final String namespaceURI;
    public final String localName;
    private static final long serialVersionUID = 1;

    @Override // com.ctc.wstx.shaded.msv_core.grammar.NameClass
    public boolean accepts(String namespaceURI, String localName) {
        return (this.namespaceURI.equals(namespaceURI) || "*" == namespaceURI) && (this.localName.equals(localName) || "*" == localName);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.NameClass
    public Object visit(NameClassVisitor visitor) {
        return visitor.onSimple(this);
    }

    public SimpleNameClass(StringPair name) {
        this(name.namespaceURI, name.localName);
    }

    public SimpleNameClass(String namespaceURI, String localName) {
        this.namespaceURI = namespaceURI;
        this.localName = localName;
    }

    public StringPair toStringPair() {
        return new StringPair(this.namespaceURI, this.localName);
    }

    public String toString() {
        return this.namespaceURI.length() == 0 ? this.localName : this.localName;
    }
}
