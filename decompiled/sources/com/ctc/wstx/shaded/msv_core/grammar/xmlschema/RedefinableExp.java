package com.ctc.wstx.shaded.msv_core.grammar.xmlschema;

import com.ctc.wstx.shaded.msv_core.grammar.ReferenceExp;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/grammar/xmlschema/RedefinableExp.class */
public abstract class RedefinableExp extends ReferenceExp {
    private static final long serialVersionUID = 1;

    public abstract RedefinableExp getClone();

    public RedefinableExp(String typeLocalName) {
        super(typeLocalName);
    }

    public void redefine(RedefinableExp rhs) {
        if (getClass() != rhs.getClass() || !this.name.equals(rhs.name)) {
            throw new IllegalArgumentException();
        }
        this.exp = rhs.exp;
    }
}
