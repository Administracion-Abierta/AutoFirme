package com.ctc.wstx.shaded.msv_core.relaxns.reader;

import com.ctc.wstx.shaded.msv_core.reader.GrammarReader;
import com.ctc.wstx.shaded.msv_core.reader.SimpleState;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;
import org.ini4j.Config;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/relaxns/reader/GrammarState.class */
public class GrammarState extends SimpleState {
    protected RELAXNSReader getReader() {
        return (RELAXNSReader) this.reader;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.SimpleState
    protected State createChildState(StartTagInfo tag) {
        if (tag.localName.equals("namespace")) {
            return new NamespaceState();
        }
        if (tag.localName.equals("topLevel")) {
            return new TopLevelState();
        }
        if (tag.localName.equals(Config.PROP_INCLUDE)) {
            return new IncludeGrammarState();
        }
        return null;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.State
    protected void startSelf() {
        super.startSelf();
        String nsVersion = this.startTag.getAttribute("relaxNamespaceVersion");
        if (nsVersion == null) {
            this.reader.reportWarning(GrammarReader.ERR_MISSING_ATTRIBUTE, "module", "relaxNamespaceVersion");
        } else if (!"1.0".equals(nsVersion)) {
            this.reader.reportWarning(RELAXNSReader.WRN_ILLEGAL_RELAXNAMESPACE_VERSION, nsVersion);
        }
    }
}
