package com.ctc.wstx.shaded.msv_core.grammar.relax;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceExp;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/grammar/relax/AttPoolClause.class */
public class AttPoolClause extends ReferenceExp implements Exportable {
    public boolean exported;
    private static final long serialVersionUID = 1;

    protected AttPoolClause(String role) {
        super(role);
        this.exported = false;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.relax.Exportable
    public boolean isExported() {
        return this.exported;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.Expression
    public Object visit(RELAXExpressionVisitor visitor) {
        return visitor.onAttPool(this);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.Expression
    public Expression visit(RELAXExpressionVisitorExpression visitor) {
        return visitor.onAttPool(this);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.Expression
    public boolean visit(RELAXExpressionVisitorBoolean visitor) {
        return visitor.onAttPool(this);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.Expression
    public void visit(RELAXExpressionVisitorVoid visitor) {
        visitor.onAttPool(this);
    }
}
