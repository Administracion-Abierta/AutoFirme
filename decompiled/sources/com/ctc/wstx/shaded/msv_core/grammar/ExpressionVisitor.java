package com.ctc.wstx.shaded.msv_core.grammar;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/grammar/ExpressionVisitor.class */
public interface ExpressionVisitor {
    Object onAttribute(AttributeExp attributeExp);

    Object onChoice(ChoiceExp choiceExp);

    Object onElement(ElementExp elementExp);

    Object onOneOrMore(OneOrMoreExp oneOrMoreExp);

    Object onMixed(MixedExp mixedExp);

    Object onList(ListExp listExp);

    Object onRef(ReferenceExp referenceExp);

    Object onOther(OtherExp otherExp);

    Object onEpsilon();

    Object onNullSet();

    Object onAnyString();

    Object onSequence(SequenceExp sequenceExp);

    Object onData(DataExp dataExp);

    Object onValue(ValueExp valueExp);

    Object onConcur(ConcurExp concurExp);

    Object onInterleave(InterleaveExp interleaveExp);
}
