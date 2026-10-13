package com.ctc.wstx.shaded.msv_core.grammar.xmlschema;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/grammar/xmlschema/AttributeGroupExp.class */
public class AttributeGroupExp extends RedefinableExp implements AttWildcardExp {
    public AttributeWildcard wildcard;
    private static final long serialVersionUID = 1;

    @Override // com.ctc.wstx.shaded.msv_core.grammar.xmlschema.AttWildcardExp
    public AttributeWildcard getAttributeWildcard() {
        return this.wildcard;
    }

    public void setAttributeWildcard(AttributeWildcard local) {
        this.wildcard = local;
    }

    public AttributeGroupExp(String typeLocalName) {
        super(typeLocalName);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.xmlschema.RedefinableExp
    public RedefinableExp getClone() {
        RedefinableExp exp = new AttributeGroupExp(this.name);
        exp.redefine(this);
        return exp;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.xmlschema.RedefinableExp
    public void redefine(RedefinableExp _rhs) {
        super.redefine(_rhs);
        AttributeGroupExp rhs = (AttributeGroupExp) _rhs;
        if (rhs.wildcard != null) {
            this.wildcard = rhs.wildcard.copy();
        } else {
            this.wildcard = null;
        }
    }
}
