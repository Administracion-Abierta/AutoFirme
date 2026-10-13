package com.ctc.wstx.shaded.msv_core.grammar.relax;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.NameClass;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceExp;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/grammar/relax/TagClause.class */
public class TagClause extends ReferenceExp {
    public NameClass nameClass;
    private static final long serialVersionUID = 1;

    protected TagClause(String role) {
        super(role);
    }

    public TagClause() {
        super(null);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.Expression
    public Object visit(RELAXExpressionVisitor visitor) {
        return visitor.onTag(this);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.Expression
    public Expression visit(RELAXExpressionVisitorExpression visitor) {
        return visitor.onTag(this);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.Expression
    public boolean visit(RELAXExpressionVisitorBoolean visitor) {
        return visitor.onTag(this);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.Expression
    public void visit(RELAXExpressionVisitorVoid visitor) {
        visitor.onTag(this);
    }
}
