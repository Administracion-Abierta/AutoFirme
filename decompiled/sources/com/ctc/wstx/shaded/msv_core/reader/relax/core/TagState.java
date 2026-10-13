package com.ctc.wstx.shaded.msv_core.reader.relax.core;

import com.ctc.wstx.shaded.msv_core.grammar.SimpleNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.relax.TagClause;
import com.ctc.wstx.shaded.msv_core.reader.GrammarReader;
import org.xml.sax.Locator;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/reader/relax/core/TagState.class */
public class TagState extends ClauseState {
    @Override // com.ctc.wstx.shaded.msv_core.reader.SimpleState
    protected void endSelf() {
        super.endSelf();
        String name = this.startTag.getAttribute("name");
        String role = this.startTag.getAttribute("role");
        if (role == null) {
            role = name;
        }
        if (name == null) {
            this.reader.reportError(GrammarReader.ERR_MISSING_ATTRIBUTE, "tag", "name");
            return;
        }
        TagClause c = getReader().module.tags.getOrCreate(role);
        if (c.nameClass != null) {
            this.reader.reportError(new Locator[]{getReader().getDeclaredLocationOf(c), this.location}, RELAXCoreReader.ERR_MULTIPLE_TAG_DECLARATIONS, new Object[]{role});
        }
        c.nameClass = new SimpleNameClass(getReader().module.targetNamespace, name);
        c.exp = this.exp;
        getReader().setDeclaredLocationOf(c);
    }
}
