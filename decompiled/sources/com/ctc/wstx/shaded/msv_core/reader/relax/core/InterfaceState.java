package com.ctc.wstx.shaded.msv_core.reader.relax.core;

import com.ctc.wstx.shaded.msv_core.grammar.relax.RELAXModule;
import com.ctc.wstx.shaded.msv_core.reader.ChildlessState;
import com.ctc.wstx.shaded.msv_core.reader.GrammarReader;
import com.ctc.wstx.shaded.msv_core.reader.SimpleState;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.reader.relax.RELAXReader;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;
import org.bouncycastle.jcajce.util.AnnotatedPrivateKey;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/reader/relax/core/InterfaceState.class */
public class InterfaceState extends SimpleState {
    @Override // com.ctc.wstx.shaded.msv_core.reader.SimpleState
    protected State createChildState(StartTagInfo tag) {
        if (!tag.namespaceURI.equals(RELAXReader.RELAXCoreNamespace)) {
            return null;
        }
        if (tag.localName.equals("div")) {
            return new InterfaceState();
        }
        RELAXModule module = getReader().module;
        if (tag.localName.equals("export")) {
            String label = tag.getAttribute(AnnotatedPrivateKey.LABEL);
            if (label != null) {
                module.elementRules.getOrCreate(label).exported = true;
            } else {
                this.reader.reportError(GrammarReader.ERR_MISSING_ATTRIBUTE, "export", AnnotatedPrivateKey.LABEL);
            }
            return new ChildlessState();
        }
        return null;
    }

    protected RELAXCoreReader getReader() {
        return (RELAXCoreReader) this.reader;
    }
}
