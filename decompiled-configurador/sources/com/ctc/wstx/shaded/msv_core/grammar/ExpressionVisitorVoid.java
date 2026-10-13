package com.ctc.wstx.shaded.msv_core.grammar;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/grammar/ExpressionVisitorVoid.class */
public interface ExpressionVisitorVoid {
    void onAttribute(AttributeExp attributeExp);

    void onChoice(ChoiceExp choiceExp);

    void onElement(ElementExp elementExp);

    void onOneOrMore(OneOrMoreExp oneOrMoreExp);

    void onMixed(MixedExp mixedExp);

    void onList(ListExp listExp);

    void onRef(ReferenceExp referenceExp);

    void onOther(OtherExp otherExp);

    void onEpsilon();

    void onNullSet();

    void onAnyString();

    void onSequence(SequenceExp sequenceExp);

    void onData(DataExp dataExp);

    void onValue(ValueExp valueExp);

    void onConcur(ConcurExp concurExp);

    void onInterleave(InterleaveExp interleaveExp);
}
