package com.ctc.wstx.shaded.msv_core.grammar;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/grammar/ExpressionVisitorExpression.class */
public interface ExpressionVisitorExpression {
    Expression onAttribute(AttributeExp attributeExp);

    Expression onChoice(ChoiceExp choiceExp);

    Expression onElement(ElementExp elementExp);

    Expression onOneOrMore(OneOrMoreExp oneOrMoreExp);

    Expression onMixed(MixedExp mixedExp);

    Expression onList(ListExp listExp);

    Expression onRef(ReferenceExp referenceExp);

    Expression onOther(OtherExp otherExp);

    Expression onEpsilon();

    Expression onNullSet();

    Expression onAnyString();

    Expression onSequence(SequenceExp sequenceExp);

    Expression onData(DataExp dataExp);

    Expression onValue(ValueExp valueExp);

    Expression onConcur(ConcurExp concurExp);

    Expression onInterleave(InterleaveExp interleaveExp);
}
