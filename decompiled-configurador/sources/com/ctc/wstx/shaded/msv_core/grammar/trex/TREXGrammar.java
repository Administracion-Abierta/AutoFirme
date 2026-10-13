package com.ctc.wstx.shaded.msv_core.grammar.trex;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionPool;
import com.ctc.wstx.shaded.msv_core.grammar.Grammar;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceContainer;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceExp;
import com.ctc.wstx.shaded.msv_core.reader.datatype.DataTypeVocabularyMap;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/grammar/trex/TREXGrammar.class */
public class TREXGrammar extends ReferenceExp implements Grammar {
    public final RefContainer namedPatterns;
    public final ExpressionPool pool;
    protected final TREXGrammar parentGrammar;
    public final DataTypeVocabularyMap dataTypes;
    private static final long serialVersionUID = 1;

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/grammar/trex/TREXGrammar$RefContainer.class */
    public static final class RefContainer extends ReferenceContainer {
        public ReferenceExp getOrCreate(String name) {
            return super._getOrCreate(name);
        }

        @Override // com.ctc.wstx.shaded.msv_core.grammar.ReferenceContainer
        protected ReferenceExp createReference(String name) {
            return new ReferenceExp(name);
        }
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.Grammar
    public Expression getTopLevel() {
        return this.exp;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.Grammar
    public ExpressionPool getPool() {
        return this.pool;
    }

    public final TREXGrammar getParentGrammar() {
        return this.parentGrammar;
    }

    public TREXGrammar(ExpressionPool pool, TREXGrammar parentGrammar) {
        super(null);
        this.namedPatterns = new RefContainer();
        this.dataTypes = new DataTypeVocabularyMap();
        this.pool = pool;
        this.parentGrammar = parentGrammar;
    }

    public TREXGrammar(ExpressionPool pool) {
        this(pool, null);
    }

    public TREXGrammar() {
        this(new ExpressionPool(), null);
    }
}
