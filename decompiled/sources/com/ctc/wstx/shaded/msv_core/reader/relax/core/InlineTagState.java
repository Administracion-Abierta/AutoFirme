package com.ctc.wstx.shaded.msv_core.reader.relax.core;

import com.ctc.wstx.shaded.msv_core.grammar.SimpleNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.relax.TagClause;
import org.bouncycastle.jcajce.util.AnnotatedPrivateKey;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/reader/relax/core/InlineTagState.class */
public class InlineTagState extends ClauseState {
    @Override // com.ctc.wstx.shaded.msv_core.reader.SimpleState
    protected void endSelf() {
        super.endSelf();
        String name = this.startTag.getAttribute("name");
        if (name == null) {
            name = this.parentState.getStartTag().getAttribute(AnnotatedPrivateKey.LABEL);
            if (name == null) {
                name = this.parentState.getStartTag().getAttribute("role");
            }
            if (name == null) {
                name = "<undefined>";
            }
        }
        if (!(this.parentState instanceof ElementRuleBaseState)) {
            throw new Error();
        }
        TagClause c = new TagClause();
        c.nameClass = new SimpleNameClass(getReader().module.targetNamespace, name);
        c.exp = this.exp;
        ((ElementRuleBaseState) this.parentState).onEndInlineClause(c);
    }
}
