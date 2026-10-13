package com.ctc.wstx.shaded.msv_core.verifier.regexp;

import com.ctc.wstx.shaded.msv_core.driver.textui.Debug;
import com.ctc.wstx.shaded.msv_core.grammar.AttributeExp;
import com.ctc.wstx.shaded.msv_core.grammar.ChoiceExp;
import com.ctc.wstx.shaded.msv_core.grammar.ConcurExp;
import com.ctc.wstx.shaded.msv_core.grammar.DataExp;
import com.ctc.wstx.shaded.msv_core.grammar.ElementExp;
import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionPool;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid;
import com.ctc.wstx.shaded.msv_core.grammar.InterleaveExp;
import com.ctc.wstx.shaded.msv_core.grammar.ListExp;
import com.ctc.wstx.shaded.msv_core.grammar.MixedExp;
import com.ctc.wstx.shaded.msv_core.grammar.OneOrMoreExp;
import com.ctc.wstx.shaded.msv_core.grammar.OtherExp;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceExp;
import com.ctc.wstx.shaded.msv_core.grammar.SequenceExp;
import com.ctc.wstx.shaded.msv_core.grammar.ValueExp;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;
import com.ctc.wstx.shaded.msv_core.util.StringPair;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/verifier/regexp/CombinedChildContentExpCreator.class */
public class CombinedChildContentExpCreator implements ExpressionVisitorVoid {
    protected final ExpressionPool pool;
    private StartTagInfo tagInfo;
    private ElementExp[] result = new ElementExp[4];
    private int numElements;
    private boolean checkTagName;
    private Expression content;
    private Expression continuation;
    private boolean foundConcur;

    /* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/verifier/regexp/CombinedChildContentExpCreator$ExpressionPair.class */
    public static class ExpressionPair {
        public final Expression content;
        public final Expression continuation;

        public ExpressionPair(Expression content, Expression continuation) {
            this.content = content;
            this.continuation = continuation;
        }
    }

    protected CombinedChildContentExpCreator(ExpressionPool pool) {
        this.pool = pool;
    }

    public ExpressionPair get(Expression combinedPattern, StartTagInfo info, boolean checkTagName) {
        if (Debug.debug) {
            for (int i = 0; i < this.result.length; i++) {
                this.result[i] = null;
            }
        }
        this.numElements = 0;
        return continueGet(combinedPattern, info, checkTagName);
    }

    public final ExpressionPair continueGet(Expression combinedPattern, StartTagInfo info, boolean checkTagName) {
        this.foundConcur = false;
        this.tagInfo = info;
        this.checkTagName = checkTagName;
        combinedPattern.visit(this);
        if (this.numElements != 1) {
            this.continuation = null;
        }
        return new ExpressionPair(this.content, this.continuation);
    }

    public ExpressionPair get(Expression combinedPattern, StartTagInfo info) {
        StringPair sp = null;
        if (combinedPattern.verifierTag != null) {
            OptimizationTag ot = (OptimizationTag) combinedPattern.verifierTag;
            sp = new StringPair(info.namespaceURI, info.localName);
            OptimizationTag.OwnerAndCont cache = (OptimizationTag.OwnerAndCont) ot.transitions.get(sp);
            if (cache != null) {
                this.numElements = 1;
                this.result[0] = cache.owner;
                return new ExpressionPair(cache.owner.contentModel.getExpandedExp(this.pool), cache.continuation);
            }
        }
        ExpressionPair r = get(combinedPattern, info, true);
        if (this.numElements == 1) {
            OptimizationTag ot2 = (OptimizationTag) combinedPattern.verifierTag;
            if (ot2 == null) {
                OptimizationTag optimizationTag = new OptimizationTag();
                ot2 = optimizationTag;
                combinedPattern.verifierTag = optimizationTag;
            }
            if (sp == null) {
                sp = new StringPair(info.namespaceURI, info.localName);
            }
            ot2.transitions.put(sp, new OptimizationTag.OwnerAndCont(this.result[0], r.continuation));
        }
        return r;
    }

    public final ElementExp[] getMatchedElements() {
        return this.result;
    }

    public final int numMatchedElements() {
        return this.numElements;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
    public void onConcur(ConcurExp exp) {
        this.foundConcur = true;
        exp.exp1.visit(this);
        Expression content1 = this.content;
        Expression continuation1 = this.continuation;
        exp.exp2.visit(this);
        this.content = this.pool.createConcur(this.content, content1);
        this.continuation = this.pool.createConcur(this.continuation, continuation1);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
    public void onInterleave(InterleaveExp exp) {
        exp.exp1.visit(this);
        if (this.content == Expression.nullSet) {
            exp.exp2.visit(this);
            this.continuation = this.pool.createInterleave(this.continuation, exp.exp1);
            return;
        }
        Expression content1 = this.content;
        Expression continuation1 = this.continuation;
        exp.exp2.visit(this);
        if (this.content == Expression.nullSet) {
            this.content = content1;
            this.continuation = this.pool.createInterleave(continuation1, exp.exp2);
        } else {
            this.content = this.pool.createChoice(this.content, content1);
            this.continuation = this.pool.createInterleave(continuation1, exp.exp2);
        }
    }

    public final boolean isComplex() {
        return this.foundConcur;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
    public void onElement(ElementExp exp) {
        if (this.checkTagName && !exp.getNameClass().accepts(this.tagInfo.namespaceURI, this.tagInfo.localName)) {
            Expression expression = Expression.nullSet;
            this.continuation = expression;
            this.content = expression;
            return;
        }
        for (int i = 0; i < this.numElements; i++) {
            if (this.result[i] == exp) {
                this.content = exp.contentModel.getExpandedExp(this.pool);
                this.continuation = Expression.epsilon;
                return;
            }
        }
        if (this.numElements == this.result.length) {
            ElementExp[] buf = new ElementExp[this.result.length * 2];
            System.arraycopy(this.result, 0, buf, 0, this.result.length);
            this.result = buf;
        }
        ElementExp[] elementExpArr = this.result;
        int i2 = this.numElements;
        this.numElements = i2 + 1;
        elementExpArr[i2] = exp;
        this.content = exp.contentModel.getExpandedExp(this.pool);
        this.continuation = Expression.epsilon;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
    public void onOneOrMore(OneOrMoreExp exp) {
        exp.exp.visit(this);
        this.continuation = this.pool.createSequence(this.continuation, this.pool.createZeroOrMore(exp.exp));
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
    public void onMixed(MixedExp exp) {
        exp.exp.visit(this);
        this.continuation = this.pool.createMixed(this.continuation);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
    public void onAttribute(AttributeExp exp) {
        Expression expression = Expression.nullSet;
        this.continuation = expression;
        this.content = expression;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
    public void onEpsilon() {
        Expression expression = Expression.nullSet;
        this.continuation = expression;
        this.content = expression;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
    public void onNullSet() {
        Expression expression = Expression.nullSet;
        this.continuation = expression;
        this.content = expression;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
    public void onAnyString() {
        Expression expression = Expression.nullSet;
        this.continuation = expression;
        this.content = expression;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
    public void onData(DataExp exp) {
        Expression expression = Expression.nullSet;
        this.continuation = expression;
        this.content = expression;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
    public void onValue(ValueExp exp) {
        Expression expression = Expression.nullSet;
        this.continuation = expression;
        this.content = expression;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
    public void onList(ListExp exp) {
        Expression expression = Expression.nullSet;
        this.continuation = expression;
        this.content = expression;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
    public void onRef(ReferenceExp exp) {
        exp.exp.visit(this);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
    public void onOther(OtherExp exp) {
        exp.exp.visit(this);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
    public void onChoice(ChoiceExp exp) {
        exp.exp1.visit(this);
        Expression content1 = this.content;
        Expression continuation1 = this.continuation;
        exp.exp2.visit(this);
        this.content = this.pool.createChoice(this.content, content1);
        this.continuation = this.pool.createChoice(this.continuation, continuation1);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
    public void onSequence(SequenceExp exp) {
        exp.exp1.visit(this);
        this.continuation = this.pool.createSequence(this.continuation, exp.exp2);
        if (!exp.exp1.isEpsilonReducible()) {
            return;
        }
        Expression content1 = this.content;
        Expression continuation1 = this.continuation;
        exp.exp2.visit(this);
        if (this.content == Expression.nullSet) {
            this.content = content1;
            this.continuation = continuation1;
        } else {
            if (content1 == Expression.nullSet) {
                return;
            }
            this.content = this.pool.createChoice(this.content, content1);
            this.continuation = this.pool.createChoice(continuation1, this.continuation);
        }
    }
}
