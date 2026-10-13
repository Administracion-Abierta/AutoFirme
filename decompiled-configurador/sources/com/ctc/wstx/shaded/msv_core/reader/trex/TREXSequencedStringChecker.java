package com.ctc.wstx.shaded.msv_core.reader.trex;

import com.ctc.wstx.shaded.msv_core.grammar.AttributeExp;
import com.ctc.wstx.shaded.msv_core.grammar.ChoiceExp;
import com.ctc.wstx.shaded.msv_core.grammar.ConcurExp;
import com.ctc.wstx.shaded.msv_core.grammar.DataExp;
import com.ctc.wstx.shaded.msv_core.grammar.ElementExp;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitor;
import com.ctc.wstx.shaded.msv_core.grammar.InterleaveExp;
import com.ctc.wstx.shaded.msv_core.grammar.ListExp;
import com.ctc.wstx.shaded.msv_core.grammar.MixedExp;
import com.ctc.wstx.shaded.msv_core.grammar.OneOrMoreExp;
import com.ctc.wstx.shaded.msv_core.grammar.OtherExp;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceExp;
import com.ctc.wstx.shaded.msv_core.grammar.SequenceExp;
import com.ctc.wstx.shaded.msv_core.grammar.ValueExp;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/reader/trex/TREXSequencedStringChecker.class */
public class TREXSequencedStringChecker implements ExpressionVisitor {
    private final boolean rejectTextInInterleave;
    private static final Integer[] intPool = {new Integer(0), new Integer(1), new Integer(2), new Integer(3), new Integer(4), new Integer(5), new Integer(6), new Integer(7)};
    private static final int HAS_ELEMENT = 4;
    private static final int HAS_ANYSTRING = 2;
    private static final int HAS_DATA = 1;
    private final TREXBaseReader reader;
    private final Set checkedExps = new HashSet();
    private final Map checkedRefExps = new HashMap();

    public TREXSequencedStringChecker(TREXBaseReader reader, boolean _rejectTextInInterleave) {
        this.reader = reader;
        this.rejectTextInInterleave = _rejectTextInInterleave;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitor
    public Object onRef(ReferenceExp exp) {
        Object r = this.checkedRefExps.get(exp);
        if (r != null) {
            return r;
        }
        Map map = this.checkedRefExps;
        Object r2 = exp.exp.visit(this);
        map.put(exp, r2);
        return r2;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitor
    public Object onOther(OtherExp exp) {
        return exp.exp.visit(this);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitor
    public Object onInterleave(InterleaveExp exp) {
        Object l = exp.exp1.visit(this);
        Object r = exp.exp2.visit(this);
        if (isError(l, r)) {
            this.reader.reportError(TREXBaseReader.ERR_INTERLEAVED_STRING);
            return intPool[0];
        }
        if (this.rejectTextInInterleave && (toInt(l) & 2) != 0 && (toInt(r) & 2) != 0) {
            this.reader.reportError(TREXBaseReader.ERR_INTERLEAVED_ANYSTRING);
            return intPool[0];
        }
        return merge(l, r);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitor
    public Object onSequence(SequenceExp exp) {
        Object l = exp.exp1.visit(this);
        Object r = exp.exp2.visit(this);
        if (isError(l, r)) {
            this.reader.reportError(TREXBaseReader.ERR_SEQUENCED_STRING);
            return intPool[0];
        }
        return merge(l, r);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitor
    public Object onEpsilon() {
        return intPool[0];
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitor
    public Object onNullSet() {
        return intPool[0];
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitor
    public Object onData(DataExp exp) {
        return intPool[1];
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitor
    public Object onValue(ValueExp exp) {
        return intPool[1];
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitor
    public Object onList(ListExp exp) {
        return intPool[1];
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitor
    public Object onAnyString() {
        return intPool[2];
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitor
    public Object onAttribute(AttributeExp exp) {
        if (this.checkedExps.add(exp)) {
            exp.exp.visit(this);
        }
        return intPool[0];
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitor
    public Object onElement(ElementExp exp) {
        if (this.checkedExps.add(exp)) {
            exp.contentModel.visit(this);
        }
        return intPool[4];
    }

    private static final int toInt(Object o) {
        return ((Integer) o).intValue();
    }

    private static Object merge(Object o1, Object o2) {
        return intPool[toInt(o1) | toInt(o2)];
    }

    private static boolean isError(Object o1, Object o2) {
        return (((toInt(o1) & 1) == 0 || toInt(o2) == 0) && ((toInt(o2) & 1) == 0 || toInt(o1) == 0)) ? false : true;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitor
    public Object onChoice(ChoiceExp exp) {
        return merge(exp.exp1.visit(this), exp.exp2.visit(this));
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitor
    public Object onConcur(ConcurExp exp) {
        return merge(exp.exp1.visit(this), exp.exp2.visit(this));
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitor
    public Object onOneOrMore(OneOrMoreExp exp) {
        Object o = exp.exp.visit(this);
        if ((toInt(o) & 1) != 0) {
            this.reader.reportError(TREXBaseReader.ERR_REPEATED_STRING);
            return intPool[0];
        }
        return o;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitor
    public Object onMixed(MixedExp exp) {
        Object o = exp.exp.visit(this);
        if (this.rejectTextInInterleave && (toInt(o) & 2) != 0) {
            this.reader.reportError(TREXBaseReader.ERR_INTERLEAVED_ANYSTRING);
            return intPool[0];
        }
        return merge(o, intPool[2]);
    }
}
