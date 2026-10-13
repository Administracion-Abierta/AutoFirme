package com.ctc.wstx.shaded.msv_core.reader;

import com.ctc.wstx.shaded.msv_core.grammar.AttributeExp;
import com.ctc.wstx.shaded.msv_core.grammar.BinaryExp;
import com.ctc.wstx.shaded.msv_core.grammar.ChoiceExp;
import com.ctc.wstx.shaded.msv_core.grammar.ConcurExp;
import com.ctc.wstx.shaded.msv_core.grammar.DataExp;
import com.ctc.wstx.shaded.msv_core.grammar.ElementExp;
import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid;
import com.ctc.wstx.shaded.msv_core.grammar.InterleaveExp;
import com.ctc.wstx.shaded.msv_core.grammar.ListExp;
import com.ctc.wstx.shaded.msv_core.grammar.MixedExp;
import com.ctc.wstx.shaded.msv_core.grammar.OneOrMoreExp;
import com.ctc.wstx.shaded.msv_core.grammar.OtherExp;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceExp;
import com.ctc.wstx.shaded.msv_core.grammar.SequenceExp;
import com.ctc.wstx.shaded.msv_core.grammar.UnaryExp;
import com.ctc.wstx.shaded.msv_core.grammar.ValueExp;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;
import org.xml.sax.Locator;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/reader/RunAwayExpressionChecker.class */
public class RunAwayExpressionChecker implements ExpressionVisitorVoid {
    protected static final RuntimeException eureka = new RuntimeException();
    private final Set testedExps = new HashSet();
    private Set contentModel = new HashSet();
    private Stack refStack = new Stack();
    private Stack unprocessedElementExps = new Stack();
    private final GrammarReader reader;

    protected RunAwayExpressionChecker(GrammarReader reader) {
        this.reader = reader;
    }

    private void check(Expression exp) {
        try {
            exp.visit(this);
            while (!this.unprocessedElementExps.isEmpty()) {
                this.contentModel.clear();
                this.refStack.clear();
                ((ElementExp) this.unprocessedElementExps.pop()).contentModel.visit(this);
            }
        } catch (RuntimeException e) {
            if (e != eureka) {
                throw e;
            }
        }
    }

    public static void check(GrammarReader reader, Expression exp) {
        new RunAwayExpressionChecker(reader).check(exp);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
    public void onAttribute(AttributeExp exp) {
        enter(exp);
        exp.exp.visit(this);
        leave();
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
    public void onConcur(ConcurExp exp) {
        binaryVisit(exp);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
    public void onInterleave(InterleaveExp exp) {
        binaryVisit(exp);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
    public void onSequence(SequenceExp exp) {
        binaryVisit(exp);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
    public void onChoice(ChoiceExp exp) {
        binaryVisit(exp);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
    public void onOneOrMore(OneOrMoreExp exp) {
        unaryVisit(exp);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
    public void onMixed(MixedExp exp) {
        unaryVisit(exp);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
    public void onList(ListExp exp) {
        unaryVisit(exp);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
    public void onEpsilon() {
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
    public void onNullSet() {
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
    public void onAnyString() {
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
    public void onData(DataExp exp) {
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
    public void onValue(ValueExp exp) {
    }

    protected final void binaryVisit(BinaryExp exp) {
        int cnt = 0;
        while (true) {
            enter(exp);
            cnt++;
            exp.exp2.visit(this);
            if (!(exp.exp1 instanceof BinaryExp)) {
                break;
            } else {
                exp = (BinaryExp) exp.exp1;
            }
        }
        exp.exp1.visit(this);
        while (cnt > 0) {
            leave();
            cnt--;
        }
    }

    protected final void unaryVisit(UnaryExp exp) {
        enter(exp);
        exp.exp.visit(this);
        leave();
    }

    private void enter(Expression exp) {
        if (this.contentModel.contains(exp)) {
            String s = "";
            int sz = this.refStack.size();
            Vector locs = new Vector();
            for (int i = this.refStack.indexOf(exp); i < sz; i++) {
                if (this.refStack.elementAt(i) instanceof ReferenceExp) {
                    ReferenceExp e = (ReferenceExp) this.refStack.elementAt(i);
                    if (e.name != null) {
                        if (s.length() != 0) {
                            s = s + " > ";
                        }
                        s = s + e.name;
                        Locator loc = this.reader.getDeclaredLocationOf(e);
                        if (loc != null) {
                            locs.add(loc);
                        }
                    }
                }
            }
            this.reader.reportError((Locator[]) locs.toArray(new Locator[0]), GrammarReader.ERR_RUNAWAY_EXPRESSION, new Object[]{s});
            throw eureka;
        }
        this.contentModel.add(exp);
        this.refStack.push(exp);
    }

    private void leave() {
        this.contentModel.remove(this.refStack.pop());
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
    public void onRef(ReferenceExp exp) {
        enter(exp);
        if (!this.testedExps.contains(exp)) {
            this.testedExps.add(exp);
            exp.exp.visit(this);
        }
        leave();
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
    public void onOther(OtherExp exp) {
        enter(exp);
        exp.exp.visit(this);
        leave();
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
    public void onElement(ElementExp exp) {
        if (!this.testedExps.add(exp)) {
            return;
        }
        this.unprocessedElementExps.push(exp);
    }
}
