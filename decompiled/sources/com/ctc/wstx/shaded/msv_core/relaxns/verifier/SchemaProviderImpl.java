package com.ctc.wstx.shaded.msv_core.relaxns.verifier;

import com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.IslandSchema;
import com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.IslandVerifier;
import com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.impl.AbstractSchemaProviderImpl;
import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionPool;
import com.ctc.wstx.shaded.msv_core.grammar.Grammar;
import com.ctc.wstx.shaded.msv_core.relaxns.grammar.DeclImpl;
import com.ctc.wstx.shaded.msv_core.relaxns.grammar.RELAXGrammar;
import com.ctc.wstx.shaded.msv_core.verifier.regexp.REDocumentDeclaration;
import java.util.Iterator;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/relaxns/verifier/SchemaProviderImpl.class */
public class SchemaProviderImpl extends AbstractSchemaProviderImpl {
    private final DeclImpl[] topLevel;
    private final Expression topLevelExp;
    private final ExpressionPool pool;

    @Override // com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.SchemaProvider
    public IslandVerifier createTopLevelVerifier() {
        return new TREXIslandVerifier(new RulesAcceptor(new REDocumentDeclaration(this.topLevelExp, this.pool), this.topLevel));
    }

    public static SchemaProviderImpl fromGrammar(Grammar grammar) {
        if (grammar instanceof RELAXGrammar) {
            return new SchemaProviderImpl((RELAXGrammar) grammar);
        }
        RELAXGrammar g = new RELAXGrammar(grammar.getPool());
        g.topLevel = grammar.getTopLevel();
        return new SchemaProviderImpl(g);
    }

    public SchemaProviderImpl(RELAXGrammar grammar) {
        this.pool = grammar.pool;
        this.topLevelExp = grammar.topLevel;
        this.topLevel = new DeclImpl[]{new DeclImpl("##start", grammar.topLevel)};
        for (String namespaceURI : grammar.moduleMap.keySet()) {
            addSchema(namespaceURI, (IslandSchema) grammar.moduleMap.get(namespaceURI));
        }
    }

    public boolean bind(ErrorHandler handler) {
        ErrorHandlerFilter filter = new ErrorHandlerFilter(handler);
        try {
            Iterator itr = this.schemata.values().iterator();
            while (itr.hasNext()) {
                ((IslandSchema) itr.next()).bind(this, filter);
            }
            return !filter.hadError;
        } catch (SAXException e) {
            return false;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/relaxns/verifier/SchemaProviderImpl$ErrorHandlerFilter.class */
    private static class ErrorHandlerFilter implements ErrorHandler {
        private final ErrorHandler core;
        boolean hadError = false;

        ErrorHandlerFilter(ErrorHandler handler) {
            this.core = handler;
        }

        @Override // org.xml.sax.ErrorHandler
        public void fatalError(SAXParseException spe) throws SAXException {
            error(spe);
        }

        @Override // org.xml.sax.ErrorHandler
        public void error(SAXParseException spe) throws SAXException {
            this.core.error(spe);
            this.hadError = true;
        }

        @Override // org.xml.sax.ErrorHandler
        public void warning(SAXParseException spe) throws SAXException {
            this.core.warning(spe);
        }
    }
}
