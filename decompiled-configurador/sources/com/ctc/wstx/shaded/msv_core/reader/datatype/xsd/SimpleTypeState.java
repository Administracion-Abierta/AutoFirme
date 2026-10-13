package com.ctc.wstx.shaded.msv_core.reader.datatype.xsd;

import com.ctc.wstx.shaded.msv_core.reader.GrammarReader;
import com.ctc.wstx.shaded.msv_core.reader.IgnoreState;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;
import java.util.StringTokenizer;
import org.apache.xml.security.transforms.params.XPath2FilterContainer;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/reader/datatype/xsd/SimpleTypeState.class */
public class SimpleTypeState extends TypeWithOneChildState {
    @Override // com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.TypeWithOneChildState, com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.XSTypeOwner
    public /* bridge */ /* synthetic */ void onEndChild(XSDatatypeExp x0) {
        super.onEndChild(x0);
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.TypeState, com.ctc.wstx.shaded.msv_core.reader.SimpleState
    public /* bridge */ /* synthetic */ void endSelf() {
        super.endSelf();
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.SimpleState
    protected State createChildState(StartTagInfo tag) {
        if (!this.startTag.namespaceURI.equals(tag.namespaceURI)) {
            return null;
        }
        String name = this.startTag.getAttribute("name");
        String uri = getTargetNamespaceUri();
        if (tag.localName.equals("annotation")) {
            return new IgnoreState();
        }
        if (tag.localName.equals("restriction")) {
            return new RestrictionState(uri, name);
        }
        if (tag.localName.equals("list")) {
            return new ListState(uri, name);
        }
        if (tag.localName.equals(XPath2FilterContainer.UNION)) {
            return new UnionState(uri, name);
        }
        return null;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.TypeWithOneChildState
    protected XSDatatypeExp annealType(XSDatatypeExp dt) {
        String finalValueStr = this.startTag.getAttribute("final");
        if (finalValueStr != null) {
            int finalValue = getFinalValue(finalValueStr);
            return dt.createFinalizedType(finalValue, this.reader);
        }
        return dt;
    }

    public int getFinalValue(String list) {
        int finalValue = 0;
        StringTokenizer tokens = new StringTokenizer(list);
        while (tokens.hasMoreTokens()) {
            String token = tokens.nextToken();
            if (token.equals("#all")) {
                finalValue |= 7;
            } else if (token.equals("restriction")) {
                finalValue |= 1;
            } else if (token.equals("list")) {
                finalValue |= 2;
            } else if (token.equals(XPath2FilterContainer.UNION)) {
                finalValue |= 4;
            } else {
                this.reader.reportError(GrammarReader.ERR_ILLEGAL_FINAL_VALUE, token);
                return 0;
            }
        }
        return finalValue;
    }
}
