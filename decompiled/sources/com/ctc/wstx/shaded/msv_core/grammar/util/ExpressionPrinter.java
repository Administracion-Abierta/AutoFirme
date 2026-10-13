package com.ctc.wstx.shaded.msv_core.grammar.util;

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
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceContainer;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceExp;
import com.ctc.wstx.shaded.msv_core.grammar.SequenceExp;
import com.ctc.wstx.shaded.msv_core.grammar.ValueExp;
import java.util.Iterator;
import org.ini4j.Config;
import org.ini4j.Registry;
import org.slf4j.Marker;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/grammar/util/ExpressionPrinter.class */
public class ExpressionPrinter implements ExpressionVisitor {
    public static final int FRAGMENT = 1;
    public static final int CONTENTMODEL = 2;
    public static final ExpressionPrinter fragmentInstance = new ExpressionPrinter(1);
    public static final ExpressionPrinter contentModelInstance = new ExpressionPrinter(2);
    public static final ExpressionPrinter smallestInstance = new ExpressionPrinter(3);
    protected final int mode;

    public static String printFragment(Expression exp) {
        return (String) exp.visit(fragmentInstance);
    }

    public static String printContentModel(Expression exp) {
        return (String) exp.visit(contentModelInstance);
    }

    public static String printSmallest(Expression exp) {
        return (String) exp.visit(smallestInstance);
    }

    protected ExpressionPrinter(int mode) {
        this.mode = mode;
    }

    public String printRefContainer(ReferenceContainer cont) {
        String r = "";
        Iterator itr = cont.iterator();
        while (itr.hasNext()) {
            ReferenceExp exp = (ReferenceExp) itr.next();
            r = r + exp.name + "  : " + exp.exp.visit(this) + "\n";
        }
        return r;
    }

    protected static boolean isComplex(Expression exp) {
        return exp instanceof BinaryExp;
    }

    protected String printBinary(BinaryExp exp, String op) {
        String r;
        String r2;
        if (exp.exp1.getClass() == exp.getClass() || !isComplex(exp.exp1)) {
            r = (String) exp.exp1.visit(this);
        } else {
            r = "(" + exp.exp1.visit(this) + ")";
        }
        String r3 = r + op;
        if (!isComplex(exp.exp2)) {
            r2 = r3 + exp.exp2.visit(this);
        } else {
            r2 = r3 + "(" + exp.exp2.visit(this) + ")";
        }
        return r2;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitor
    public Object onAttribute(AttributeExp exp) {
        return Registry.Key.DEFAULT_NAME + exp.nameClass.toString() + "<" + exp.exp.visit(this) + ">";
    }

    private Object optional(Expression exp) {
        if (!(exp instanceof OneOrMoreExp)) {
            return isComplex(exp) ? "(" + exp.visit(this) + ")?" : exp.visit(this) + Config.DEFAULT_GLOBAL_SECTION_NAME;
        }
        OneOrMoreExp ome = (OneOrMoreExp) exp;
        return isComplex(ome.exp) ? "(" + ome.exp.visit(this) + ")*" : ome.exp.visit(this) + "*";
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitor
    public Object onChoice(ChoiceExp exp) {
        if (exp.exp1 == Expression.epsilon) {
            return optional(exp.exp2);
        }
        return exp.exp2 == Expression.epsilon ? optional(exp.exp1) : printBinary(exp, "|");
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitor
    public Object onConcur(ConcurExp exp) {
        return printBinary(exp, "&");
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitor
    public Object onInterleave(InterleaveExp exp) {
        return printBinary(exp, "^");
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitor
    public Object onElement(ElementExp exp) {
        if ((this.mode & 2) != 0) {
            return exp.getNameClass().toString();
        }
        return exp.getNameClass().toString() + "<" + exp.contentModel.visit(this) + ">";
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitor
    public Object onOneOrMore(OneOrMoreExp exp) {
        return isComplex(exp.exp) ? "(" + exp.exp.visit(this) + ")+" : exp.exp.visit(this) + Marker.ANY_NON_NULL_MARKER;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitor
    public Object onMixed(MixedExp exp) {
        return "mixed[" + exp.exp.visit(this) + "]";
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitor
    public Object onList(ListExp exp) {
        return "list[" + exp.exp.visit(this) + "]";
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitor
    public Object onEpsilon() {
        return "#epsilon";
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitor
    public Object onNullSet() {
        return "#nullSet";
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitor
    public Object onAnyString() {
        return "<anyString>";
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitor
    public Object onSequence(SequenceExp exp) {
        return printBinary(exp, ",");
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitor
    public Object onData(DataExp exp) {
        return "$" + exp.name.localName;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitor
    public Object onValue(ValueExp exp) {
        return "$$" + exp.value;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitor
    public Object onOther(OtherExp exp) {
        return exp.printName() + "[" + exp.exp.visit(this) + "]";
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitor
    public Object onRef(ReferenceExp exp) {
        return (this.mode & 1) != 0 ? "{%" + exp.name + "}" : "(" + exp.exp.visit(this) + ")";
    }
}
