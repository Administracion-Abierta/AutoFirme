package com.ctc.wstx.shaded.msv_core.relaxns.reader.relax;

import com.ctc.wstx.shaded.msv_core.grammar.relax.RELAXModule;
import com.ctc.wstx.shaded.msv_core.reader.ChildlessState;
import com.ctc.wstx.shaded.msv_core.reader.GrammarReader;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.reader.relax.RELAXReader;
import com.ctc.wstx.shaded.msv_core.reader.relax.core.InterfaceState;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;
import org.bouncycastle.jcajce.util.AnnotatedPrivateKey;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/relaxns/reader/relax/InterfaceStateEx.class */
class InterfaceStateEx extends InterfaceState {
    InterfaceStateEx() {
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.relax.core.InterfaceState, com.ctc.wstx.shaded.msv_core.reader.SimpleState
    protected State createChildState(StartTagInfo tag) {
        String role;
        RELAXCoreIslandSchemaReader reader = (RELAXCoreIslandSchemaReader) this.reader;
        if (!tag.namespaceURI.equals(RELAXReader.RELAXCoreNamespace)) {
            return null;
        }
        if (tag.localName.equals("div")) {
            return new InterfaceStateEx();
        }
        RELAXModule module = reader.getModule();
        if (tag.localName.equals("export") && (role = tag.getAttribute("role")) != null) {
            module.attPools.getOrCreate(role).exported = true;
            return new ChildlessState();
        }
        if (tag.localName.equals("hedgeExport")) {
            String label = tag.getAttribute(AnnotatedPrivateKey.LABEL);
            if (label == null) {
                reader.reportError(GrammarReader.ERR_MISSING_ATTRIBUTE, "hedgeExport", AnnotatedPrivateKey.LABEL);
            } else {
                module.hedgeRules.getOrCreate(label).exported = true;
            }
            return new ChildlessState();
        }
        return super.createChildState(tag);
    }
}
