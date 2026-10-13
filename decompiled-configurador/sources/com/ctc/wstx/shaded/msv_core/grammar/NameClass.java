package com.ctc.wstx.shaded.msv_core.grammar;

import com.ctc.wstx.shaded.msv_core.grammar.util.NameClassCollisionChecker;
import com.ctc.wstx.shaded.msv_core.grammar.util.NameClassComparator;
import com.ctc.wstx.shaded.msv_core.grammar.util.NameClassSimplifier;
import com.ctc.wstx.shaded.msv_core.util.StringPair;
import java.io.Serializable;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/grammar/NameClass.class */
public abstract class NameClass implements Serializable {
    public static final String NAMESPACE_WILDCARD = "*";
    public static final String LOCALNAME_WILDCARD = "*";
    public static final NameClass ALL = new AnyNameClass();
    public static final NameClass NONE = new NotNameClass(ALL);
    private static final long serialVersionUID = 1;

    public abstract boolean accepts(String str, String str2);

    public abstract Object visit(NameClassVisitor nameClassVisitor);

    public final boolean accepts(StringPair name) {
        return accepts(name.namespaceURI, name.localName);
    }

    public final boolean includes(NameClass rhs) {
        boolean r = new NameClassComparator() { // from class: com.ctc.wstx.shaded.msv_core.grammar.NameClass.1
            @Override // com.ctc.wstx.shaded.msv_core.grammar.util.NameClassComparator
            protected void probe(String uri, String local) {
                if (!this.nc1.accepts(uri, local) && this.nc2.accepts(uri, local)) {
                    throw this.eureka;
                }
            }
        }.check(this, rhs);
        return !r;
    }

    public boolean isNull() {
        return !new NameClassCollisionChecker().check(this, ALL);
    }

    public final boolean isEqualTo(NameClass rhs) {
        boolean r = new NameClassComparator() { // from class: com.ctc.wstx.shaded.msv_core.grammar.NameClass.2
            @Override // com.ctc.wstx.shaded.msv_core.grammar.util.NameClassComparator
            protected void probe(String uri, String local) {
                boolean a = this.nc1.accepts(uri, local);
                boolean b = this.nc2.accepts(uri, local);
                if ((a && !b) || (!a && b)) {
                    throw this.eureka;
                }
            }
        }.check(this, rhs);
        return !r;
    }

    public NameClass simplify() {
        return NameClassSimplifier.simplify(this);
    }

    public static NameClass intersection(NameClass lhs, NameClass rhs) {
        return NameClassSimplifier.simplify(new DifferenceNameClass(lhs, new NotNameClass(rhs)));
    }

    public static NameClass union(NameClass lhs, NameClass rhs) {
        return NameClassSimplifier.simplify(new ChoiceNameClass(lhs, rhs));
    }
}
