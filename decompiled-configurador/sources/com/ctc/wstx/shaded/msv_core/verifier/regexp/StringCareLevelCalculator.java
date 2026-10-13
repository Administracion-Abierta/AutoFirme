package com.ctc.wstx.shaded.msv_core.verifier.regexp;

import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype;
import com.ctc.wstx.shaded.msv_core.grammar.AttributeExp;
import com.ctc.wstx.shaded.msv_core.grammar.BinaryExp;
import com.ctc.wstx.shaded.msv_core.grammar.ChoiceExp;
import com.ctc.wstx.shaded.msv_core.grammar.ConcurExp;
import com.ctc.wstx.shaded.msv_core.grammar.DataExp;
import com.ctc.wstx.shaded.msv_core.grammar.ElementExp;
import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitor;
import com.ctc.wstx.shaded.msv_core.grammar.InterleaveExp;
import com.ctc.wstx.shaded.msv_core.grammar.ListExp;
import com.ctc.wstx.shaded.msv_core.grammar.MixedExp;
import com.ctc.wstx.shaded.msv_core.grammar.OneOrMoreExp;
import com.ctc.wstx.shaded.msv_core.grammar.OtherExp;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceExp;
import com.ctc.wstx.shaded.msv_core.grammar.SequenceExp;
import com.ctc.wstx.shaded.msv_core.grammar.ValueExp;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/verifier/regexp/StringCareLevelCalculator.class */
public class StringCareLevelCalculator implements ExpressionVisitor {
    protected static final StringCareLevelCalculator theInstance = new StringCareLevelCalculator();
    private static final String ANY_STRING = "anyString";
    private static final String NO_STRING = "noString";
    private static final String SOME_STRING = "someString";

    protected StringCareLevelCalculator() {
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitor
    public Object onAttribute(AttributeExp exp) {
        return NO_STRING;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitor
    public Object onElement(ElementExp exp) {
        return NO_STRING;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitor
    public Object onMixed(MixedExp exp) {
        return ANY_STRING;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitor
    public Object onList(ListExp exp) {
        return SOME_STRING;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitor
    public Object onAnyString() {
        return ANY_STRING;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitor
    public Object onData(DataExp exp) {
        if (exp.except == Expression.nullSet && (exp.dt instanceof XSDatatype)) {
            XSDatatype xdt = (XSDatatype) exp.dt;
            if (xdt.isAlwaysValid()) {
                return ANY_STRING;
            }
            return SOME_STRING;
        }
        return SOME_STRING;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitor
    public Object onValue(ValueExp exp) {
        return SOME_STRING;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitor
    public Object onChoice(ChoiceExp exp) {
        return doChoice(exp);
    }

    private Object doChoice(BinaryExp exp) {
        Object lhs = exp.exp1.visit(this);
        Object rhs = exp.exp2.visit(this);
        if (lhs == ANY_STRING && rhs == ANY_STRING) {
            return ANY_STRING;
        }
        return (lhs == NO_STRING && rhs == NO_STRING) ? NO_STRING : SOME_STRING;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitor
    public Object onOneOrMore(OneOrMoreExp exp) {
        return exp.exp.visit(this);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitor
    public Object onRef(ReferenceExp exp) {
        return exp.exp.visit(this);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitor
    public Object onOther(OtherExp exp) {
        return exp.exp.visit(this);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitor
    public Object onEpsilon() {
        return NO_STRING;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitor
    public Object onNullSet() {
        return NO_STRING;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitor
    public Object onSequence(SequenceExp exp) {
        if (!exp.exp1.isEpsilonReducible()) {
            return exp.exp1.visit(this);
        }
        return doChoice(exp);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitor
    public Object onConcur(ConcurExp exp) {
        Object lhs = exp.exp1.visit(this);
        Object rhs = exp.exp2.visit(this);
        if (lhs == ANY_STRING && rhs == ANY_STRING) {
            return ANY_STRING;
        }
        return (lhs == NO_STRING || rhs == NO_STRING) ? NO_STRING : SOME_STRING;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitor
    public Object onInterleave(InterleaveExp p) {
        return doChoice(p);
    }

    public static int calc(Expression exp) {
        Object r = exp.visit(theInstance);
        if (r == ANY_STRING) {
            return 1;
        }
        if (r == NO_STRING) {
            return 0;
        }
        return 2;
    }
}
