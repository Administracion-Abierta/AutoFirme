package com.ctc.wstx.shaded.msv_core.relaxns.reader;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.reader.ExpressionOwner;
import com.ctc.wstx.shaded.msv_core.reader.SimpleState;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.relaxns.verifier.IslandSchemaImpl;
import com.ctc.wstx.shaded.msv_core.relaxns.verifier.SchemaProviderImpl;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/relaxns/reader/RootGrammarState.class */
public class RootGrammarState extends SimpleState implements ExpressionOwner {
    @Override // com.ctc.wstx.shaded.msv_core.reader.SimpleState
    protected State createChildState(StartTagInfo tag) {
        if (tag.localName.equals("grammar")) {
            return new GrammarState();
        }
        return null;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.SimpleState
    protected void endSelf() {
        RELAXNSReader reader = (RELAXNSReader) this.reader;
        SchemaProviderImpl schemaProvider = new SchemaProviderImpl(reader.grammar);
        reader.schemaProvider = schemaProvider;
        if (!reader.controller.hadError()) {
            if (!schemaProvider.bind(reader.controller)) {
                reader.controller.setErrorFlag();
            }
            if (reader.grammar.topLevel != null) {
                reader.grammar.topLevel = reader.grammar.topLevel.visit(new IslandSchemaImpl.Binder(schemaProvider, reader.controller, reader.pool));
            }
        }
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.ExpressionOwner
    public final void onEndChild(Expression exp) {
    }
}
