package com.ctc.wstx.shaded.msv_core.grammar;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/grammar/ExpressionVisitorBoolean.class */
public interface ExpressionVisitorBoolean {
    boolean onAttribute(AttributeExp attributeExp);

    boolean onChoice(ChoiceExp choiceExp);

    boolean onElement(ElementExp elementExp);

    boolean onOneOrMore(OneOrMoreExp oneOrMoreExp);

    boolean onMixed(MixedExp mixedExp);

    boolean onList(ListExp listExp);

    boolean onRef(ReferenceExp referenceExp);

    boolean onOther(OtherExp otherExp);

    boolean onEpsilon();

    boolean onNullSet();

    boolean onAnyString();

    boolean onSequence(SequenceExp sequenceExp);

    boolean onData(DataExp dataExp);

    boolean onValue(ValueExp valueExp);

    boolean onConcur(ConcurExp concurExp);

    boolean onInterleave(InterleaveExp interleaveExp);
}
