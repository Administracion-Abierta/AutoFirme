package com.ctc.wstx.shaded.msv_core.grammar.relax;

import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype;
import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionPool;
import com.ctc.wstx.shaded.msv_core.grammar.Grammar;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceContainer;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceExp;
import java.util.HashMap;
import java.util.Map;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/grammar/relax/RELAXModule.class */
public class RELAXModule implements Grammar {
    public final String targetNamespace;
    public Expression topLevel;
    public final ExpressionPool pool;
    private static final long serialVersionUID = 1;
    public final ElementRulesContainer elementRules = new ElementRulesContainer();
    public final HedgeRulesContainer hedgeRules = new HedgeRulesContainer();
    public final TagContainer tags = new TagContainer();
    public final AttPoolContainer attPools = new AttPoolContainer();
    public final DatatypeContainer datatypes = new DatatypeContainer();

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/grammar/relax/RELAXModule$ElementRulesContainer.class */
    public final class ElementRulesContainer extends ReferenceContainer {
        public ElementRulesContainer() {
        }

        public ElementRules getOrCreate(String name) {
            return (ElementRules) super._getOrCreate(name);
        }

        public ElementRules get(String name) {
            return (ElementRules) super._get(name);
        }

        @Override // com.ctc.wstx.shaded.msv_core.grammar.ReferenceContainer
        protected ReferenceExp createReference(String name) {
            return new ElementRules(name, RELAXModule.this);
        }
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/grammar/relax/RELAXModule$HedgeRulesContainer.class */
    public final class HedgeRulesContainer extends ReferenceContainer {
        public HedgeRulesContainer() {
        }

        public HedgeRules getOrCreate(String name) {
            return (HedgeRules) super._getOrCreate(name);
        }

        public HedgeRules get(String name) {
            return (HedgeRules) super._get(name);
        }

        @Override // com.ctc.wstx.shaded.msv_core.grammar.ReferenceContainer
        protected ReferenceExp createReference(String name) {
            return new HedgeRules(name, RELAXModule.this);
        }
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/grammar/relax/RELAXModule$TagContainer.class */
    public final class TagContainer extends ReferenceContainer {
        public TagContainer() {
        }

        public TagClause getOrCreate(String name) {
            return (TagClause) super._getOrCreate(name);
        }

        public TagClause get(String name) {
            return (TagClause) super._get(name);
        }

        @Override // com.ctc.wstx.shaded.msv_core.grammar.ReferenceContainer
        protected ReferenceExp createReference(String name) {
            return new TagClause(name);
        }
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/grammar/relax/RELAXModule$AttPoolContainer.class */
    public final class AttPoolContainer extends ReferenceContainer {
        public AttPoolContainer() {
        }

        public AttPoolClause getOrCreate(String name) {
            return (AttPoolClause) super._getOrCreate(name);
        }

        public AttPoolClause get(String name) {
            return (AttPoolClause) super._get(name);
        }

        @Override // com.ctc.wstx.shaded.msv_core.grammar.ReferenceContainer
        protected ReferenceExp createReference(String name) {
            return new AttPoolClause(name);
        }
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/grammar/relax/RELAXModule$DatatypeContainer.class */
    public class DatatypeContainer {
        private final Map m = new HashMap();

        public DatatypeContainer() {
        }

        public XSDatatype get(String name) {
            return (XSDatatype) this.m.get(name);
        }

        public void add(XSDatatype dt) {
            if (dt.getName() == null) {
                throw new IllegalArgumentException();
            }
            this.m.put(dt.getName(), dt);
        }
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.Grammar
    public Expression getTopLevel() {
        return this.topLevel;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.Grammar
    public ExpressionPool getPool() {
        return this.pool;
    }

    public RELAXModule(ExpressionPool pool, String targetNamespace) {
        if (targetNamespace == null) {
            throw new NullPointerException();
        }
        this.pool = pool;
        this.targetNamespace = targetNamespace;
        this.datatypes.add(EmptyStringType.theInstance);
        this.datatypes.add(NoneType.theInstance);
    }
}
