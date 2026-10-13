package com.ctc.wstx.shaded.msv_core.grammar.xmlschema;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionPool;
import com.ctc.wstx.shaded.msv_core.grammar.Grammar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/grammar/xmlschema/XMLSchemaGrammar.class */
public class XMLSchemaGrammar implements Grammar {
    protected final ExpressionPool pool;
    public Expression topLevel;
    protected final Map schemata;
    private static final long serialVersionUID = 1;

    public XMLSchemaGrammar() {
        this(new ExpressionPool());
    }

    public XMLSchemaGrammar(ExpressionPool pool) {
        this.schemata = new HashMap();
        this.pool = pool;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.Grammar
    public final ExpressionPool getPool() {
        return this.pool;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.Grammar
    public final Expression getTopLevel() {
        return this.topLevel;
    }

    public XMLSchemaSchema getByNamespace(String targetNamesapce) {
        return (XMLSchemaSchema) this.schemata.get(targetNamesapce);
    }

    public Iterator iterateSchemas() {
        return this.schemata.values().iterator();
    }
}
