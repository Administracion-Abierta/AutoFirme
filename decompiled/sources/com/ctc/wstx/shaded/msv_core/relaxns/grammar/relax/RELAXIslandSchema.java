package com.ctc.wstx.shaded.msv_core.relaxns.grammar.relax;

import com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.ElementDecl;
import com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.IslandSchema;
import com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.SchemaProvider;
import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.Grammar;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceExp;
import com.ctc.wstx.shaded.msv_core.grammar.relax.AttPoolClause;
import com.ctc.wstx.shaded.msv_core.grammar.relax.ElementRules;
import com.ctc.wstx.shaded.msv_core.grammar.relax.HedgeRules;
import com.ctc.wstx.shaded.msv_core.grammar.relax.RELAXModule;
import com.ctc.wstx.shaded.msv_core.relaxns.grammar.DeclImpl;
import com.ctc.wstx.shaded.msv_core.relaxns.grammar.ExternalElementExp;
import com.ctc.wstx.shaded.msv_core.relaxns.verifier.IslandSchemaImpl;
import java.util.Iterator;
import java.util.Set;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/relaxns/grammar/relax/RELAXIslandSchema.class */
public class RELAXIslandSchema extends IslandSchemaImpl {
    protected final RELAXModule module;
    protected Set pendingAnyOtherElements;

    public RELAXIslandSchema(RELAXModule module, Set pendingAnyOtherElements) {
        this.module = module;
        this.pendingAnyOtherElements = pendingAnyOtherElements;
        ReferenceExp[] refs = module.elementRules.getAll();
        for (int i = 0; i < refs.length; i++) {
            if (((ElementRules) refs[i]).exported) {
                this.elementDecls.put(refs[i].name, new DeclImpl(refs[i]));
            }
        }
        ReferenceExp[] refs2 = module.hedgeRules.getAll();
        for (int i2 = 0; i2 < refs2.length; i2++) {
            if (((HedgeRules) refs2[i2]).exported) {
                this.elementDecls.put(refs2[i2].name, new DeclImpl(refs2[i2]));
            }
        }
        ExportedAttPoolGenerator expGen = new ExportedAttPoolGenerator(module.pool);
        ReferenceExp[] refs3 = module.attPools.getAll();
        for (int i3 = 0; i3 < refs3.length; i3++) {
            if (((AttPoolClause) refs3[i3]).exported) {
                this.attributesDecls.put(refs3[i3].name, new DeclImpl(refs3[i3].name, expGen.create(module, refs3[i3].exp)));
            }
        }
    }

    @Override // com.ctc.wstx.shaded.msv_core.relaxns.verifier.IslandSchemaImpl
    protected Grammar getGrammar() {
        return this.module;
    }

    @Override // com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.IslandSchema
    public void bind(SchemaProvider provider, ErrorHandler handler) throws SAXException {
        Expression pseudoContentModel = createChoiceOfAllExportedRules(provider);
        Iterator itr = this.pendingAnyOtherElements.iterator();
        while (itr.hasNext()) {
            ((AnyOtherElementExp) itr.next()).wrapUp(this.module, pseudoContentModel, provider, handler);
        }
        this.pendingAnyOtherElements = null;
        IslandSchemaImpl.Binder binder = new IslandSchemaImpl.Binder(provider, handler, this.module.pool);
        bind(this.module.elementRules, binder);
        bind(this.module.hedgeRules, binder);
        bind(this.module.attPools, binder);
        bind(this.module.tags, binder);
    }

    private Expression createChoiceOfAllExportedRules(SchemaProvider provider) {
        Expression exp = Expression.nullSet;
        Iterator itr = provider.iterateNamespace();
        while (itr.hasNext()) {
            String namespace = (String) itr.next();
            IslandSchema is = provider.getSchemaByNamespace(namespace);
            ElementDecl[] rules = is.getElementDecls();
            for (ElementDecl elementDecl : rules) {
                exp = this.module.pool.createChoice(exp, new ExternalElementExp(this.module.pool, namespace, elementDecl.getName(), null));
            }
        }
        return exp;
    }
}
