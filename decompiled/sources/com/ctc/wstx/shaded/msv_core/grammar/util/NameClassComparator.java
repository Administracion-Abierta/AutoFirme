package com.ctc.wstx.shaded.msv_core.grammar.util;

import com.ctc.wstx.shaded.msv_core.grammar.AnyNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.ChoiceNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.DifferenceNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.NameClass;
import com.ctc.wstx.shaded.msv_core.grammar.NameClassVisitor;
import com.ctc.wstx.shaded.msv_core.grammar.NamespaceNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.NotNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.SimpleNameClass;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/grammar/util/NameClassComparator.class */
public abstract class NameClassComparator implements NameClassVisitor {
    protected NameClass nc1;
    protected NameClass nc2;
    protected final RuntimeException eureka = new RuntimeException();
    private final String MAGIC = PossibleNamesCollector.MAGIC;

    protected abstract void probe(String str, String str2);

    public boolean check(NameClass _new, NameClass _old) {
        try {
            this.nc1 = _new;
            this.nc2 = _old;
            _old.visit(this);
            _new.visit(this);
            return false;
        } catch (RuntimeException e) {
            if (e == this.eureka) {
                return true;
            }
            throw e;
        }
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.NameClassVisitor
    public Object onAnyName(AnyNameClass nc) {
        probe(PossibleNamesCollector.MAGIC, PossibleNamesCollector.MAGIC);
        return null;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.NameClassVisitor
    public Object onNsName(NamespaceNameClass nc) {
        probe(nc.namespaceURI, PossibleNamesCollector.MAGIC);
        return null;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.NameClassVisitor
    public Object onSimple(SimpleNameClass nc) {
        probe(nc.namespaceURI, nc.localName);
        return null;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.NameClassVisitor
    public Object onNot(NotNameClass nc) {
        nc.child.visit(this);
        return null;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.NameClassVisitor
    public Object onDifference(DifferenceNameClass nc) {
        nc.nc1.visit(this);
        nc.nc2.visit(this);
        return null;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.NameClassVisitor
    public Object onChoice(ChoiceNameClass nc) {
        nc.nc1.visit(this);
        nc.nc2.visit(this);
        return null;
    }
}
