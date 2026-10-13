package com.ctc.wstx.shaded.msv_core.grammar.xmlschema;

import com.ctc.wstx.shaded.msv_core.grammar.AttributeExp;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceExp;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/grammar/xmlschema/AttributeDeclExp.class */
public class AttributeDeclExp extends ReferenceExp {
    public AttributeExp self;
    private static final long serialVersionUID = 1;

    public AttributeDeclExp(String typeLocalName) {
        super(typeLocalName);
    }

    public void set(AttributeExp exp) {
        this.self = exp;
        this.exp = this.self;
    }
}
