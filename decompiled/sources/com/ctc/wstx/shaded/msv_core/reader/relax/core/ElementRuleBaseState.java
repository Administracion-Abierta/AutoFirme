package com.ctc.wstx.shaded.msv_core.reader.relax.core;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.NameClass;
import com.ctc.wstx.shaded.msv_core.grammar.relax.ElementRule;
import com.ctc.wstx.shaded.msv_core.grammar.relax.ElementRules;
import com.ctc.wstx.shaded.msv_core.grammar.relax.TagClause;
import com.ctc.wstx.shaded.msv_core.reader.GrammarReader;
import com.ctc.wstx.shaded.msv_core.reader.SimpleState;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;
import org.bouncycastle.jcajce.util.AnnotatedPrivateKey;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/reader/relax/core/ElementRuleBaseState.class */
abstract class ElementRuleBaseState extends SimpleState {
    protected TagClause clause;

    protected abstract Expression getContentModel();

    ElementRuleBaseState() {
    }

    protected RELAXCoreReader getReader() {
        return (RELAXCoreReader) this.reader;
    }

    protected void onEndInlineClause(TagClause inlineTag) {
        if (this.clause != null) {
            this.reader.reportError(RELAXCoreReader.ERR_MORE_THAN_ONE_INLINE_TAG);
        }
        this.clause = inlineTag;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.SimpleState
    protected void endSelf() {
        String role = this.startTag.getAttribute("role");
        String label = this.startTag.getAttribute(AnnotatedPrivateKey.LABEL);
        if (role == null && label == null) {
            this.reader.reportError(GrammarReader.ERR_MISSING_ATTRIBUTE_2, "elementRule", "role", AnnotatedPrivateKey.LABEL);
            label = "<undefined>";
        }
        if (label == null) {
            label = role;
        }
        if (this.clause == null) {
            if (role == null) {
                this.reader.reportError(GrammarReader.ERR_MISSING_ATTRIBUTE, "elementRule", "role");
                this.clause = new TagClause();
                this.clause.nameClass = NameClass.ALL;
                this.clause.exp = Expression.nullSet;
            } else {
                this.clause = getReader().module.tags.getOrCreate(role);
            }
        }
        ElementRules er = getReader().module.elementRules.getOrCreate(label);
        getReader().setDeclaredLocationOf(er);
        er.addElementRule(this.reader.pool, new ElementRule(this.reader.pool, this.clause, getContentModel()));
        super.endSelf();
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.SimpleState
    protected State createChildState(StartTagInfo tag) {
        if (tag.localName.equals("tag")) {
            return getReader().getStateFactory().tagInline(this, tag);
        }
        return null;
    }
}
