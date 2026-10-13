package com.ctc.wstx.shaded.msv_core.grammar.xmlschema;

import com.ctc.wstx.shaded.msv_core.grammar.DifferenceNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.NameClass;
import com.ctc.wstx.shaded.msv_core.grammar.NameClassVisitor;
import com.ctc.wstx.shaded.msv_core.grammar.SimpleNameClass;
import com.ctc.wstx.shaded.msv_core.util.StringPair;
import java.util.HashSet;
import java.util.Set;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/grammar/xmlschema/LaxDefaultNameClass.class */
public class LaxDefaultNameClass extends NameClass {
    private NameClass base;
    protected NameClass equivalentNameClass;
    private final Set names = new HashSet();
    private static final long serialVersionUID = 1;

    public LaxDefaultNameClass(NameClass _base) {
        this.base = _base;
        this.names.add(new StringPair("*", "*"));
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.NameClass
    public Object visit(NameClassVisitor visitor) {
        if (this.equivalentNameClass == null) {
            NameClass nc = this.base;
            StringPair[] items = (StringPair[]) this.names.toArray(new StringPair[0]);
            for (int i = 0; i < items.length; i++) {
                if (items[i].namespaceURI != "*" && items[i].localName != "*") {
                    nc = new DifferenceNameClass(nc, new SimpleNameClass(items[i]));
                }
            }
            this.equivalentNameClass = nc;
        }
        return this.equivalentNameClass.visit(visitor);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.NameClass
    public boolean accepts(String namespaceURI, String localName) {
        return this.base.accepts(namespaceURI, localName) && !this.names.contains(new StringPair(namespaceURI, localName));
    }

    public void addName(String namespaceURI, String localName) {
        this.names.add(new StringPair(namespaceURI, localName));
        this.names.add(new StringPair(namespaceURI, "*"));
        this.names.add(new StringPair("*", localName));
    }
}
