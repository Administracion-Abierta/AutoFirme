package com.ctc.wstx.shaded.msv_core.grammar.xmlschema;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/grammar/xmlschema/GroupDeclExp.class */
public class GroupDeclExp extends RedefinableExp {
    private static final long serialVersionUID = 1;

    public GroupDeclExp(String typeLocalName) {
        super(typeLocalName);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.xmlschema.RedefinableExp
    public RedefinableExp getClone() {
        RedefinableExp exp = new GroupDeclExp(this.name);
        exp.redefine(this);
        return exp;
    }
}
