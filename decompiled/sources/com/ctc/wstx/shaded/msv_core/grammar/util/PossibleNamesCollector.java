package com.ctc.wstx.shaded.msv_core.grammar.util;

import com.ctc.wstx.shaded.msv_core.grammar.AnyNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.ChoiceNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.DifferenceNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.NameClass;
import com.ctc.wstx.shaded.msv_core.grammar.NameClassVisitor;
import com.ctc.wstx.shaded.msv_core.grammar.NamespaceNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.NotNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.SimpleNameClass;
import com.ctc.wstx.shaded.msv_core.util.StringPair;
import java.util.HashSet;
import java.util.Set;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/grammar/util/PossibleNamesCollector.class */
public class PossibleNamesCollector implements NameClassVisitor {
    public static final String MAGIC = "\u0000";
    private static final StringPair pairForAny = new StringPair(MAGIC, MAGIC);
    private Set names = new HashSet();

    public static Set calc(NameClass nc) {
        PossibleNamesCollector col = new PossibleNamesCollector();
        nc.visit(col);
        return col.names;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.NameClassVisitor
    public Object onChoice(ChoiceNameClass nc) {
        nc.nc1.visit(this);
        nc.nc2.visit(this);
        return null;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.NameClassVisitor
    public Object onAnyName(AnyNameClass nc) {
        this.names.add(pairForAny);
        return null;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.NameClassVisitor
    public Object onSimple(SimpleNameClass nc) {
        this.names.add(new StringPair(nc.namespaceURI, nc.localName));
        return null;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.NameClassVisitor
    public Object onNsName(NamespaceNameClass nc) {
        this.names.add(new StringPair(nc.namespaceURI, MAGIC));
        return null;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.NameClassVisitor
    public Object onNot(NotNameClass nc) {
        this.names.add(pairForAny);
        nc.child.visit(this);
        return null;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.NameClassVisitor
    public Object onDifference(DifferenceNameClass nc) {
        nc.nc1.visit(this);
        nc.nc2.visit(this);
        return null;
    }
}
