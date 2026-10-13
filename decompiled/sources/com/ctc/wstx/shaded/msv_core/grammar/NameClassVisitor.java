package com.ctc.wstx.shaded.msv_core.grammar;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/grammar/NameClassVisitor.class */
public interface NameClassVisitor {
    Object onChoice(ChoiceNameClass choiceNameClass);

    Object onAnyName(AnyNameClass anyNameClass);

    Object onSimple(SimpleNameClass simpleNameClass);

    Object onNsName(NamespaceNameClass namespaceNameClass);

    Object onNot(NotNameClass notNameClass);

    Object onDifference(DifferenceNameClass differenceNameClass);
}
