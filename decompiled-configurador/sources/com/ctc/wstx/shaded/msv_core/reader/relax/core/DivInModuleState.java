package com.ctc.wstx.shaded.msv_core.reader.relax.core;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.reader.ExpressionOwner;
import com.ctc.wstx.shaded.msv_core.reader.GrammarReader;
import com.ctc.wstx.shaded.msv_core.reader.SimpleState;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.XSDatatypeExp;
import com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.XSTypeOwner;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/reader/relax/core/DivInModuleState.class */
public class DivInModuleState extends SimpleState implements ExpressionOwner, XSTypeOwner {
    protected RELAXCoreReader getReader() {
        return (RELAXCoreReader) this.reader;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.SimpleState
    protected State createChildState(StartTagInfo tag) {
        if (tag.localName.equals("div")) {
            return getReader().getStateFactory().divInModule(this, tag);
        }
        if (tag.localName.equals("hedgeRule")) {
            return getReader().getStateFactory().hedgeRule(this, tag);
        }
        if (tag.localName.equals("tag")) {
            return getReader().getStateFactory().tag(this, tag);
        }
        if (tag.localName.equals("attPool")) {
            return getReader().getStateFactory().attPool(this, tag);
        }
        if (tag.localName.equals("include")) {
            return getReader().getStateFactory().include(this, tag);
        }
        if (tag.localName.equals("interface")) {
            return getReader().getStateFactory().interface_(this, tag);
        }
        if (tag.localName.equals("elementRule")) {
            return getReader().getStateFactory().elementRule(this, tag);
        }
        if (tag.localName.equals("simpleType")) {
            return getReader().getStateFactory().simpleType(this, tag);
        }
        return null;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.ExpressionOwner
    public void onEndChild(Expression exp) {
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.XSTypeOwner
    public String getTargetNamespaceUri() {
        return "";
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.XSTypeOwner
    public void onEndChild(XSDatatypeExp type) {
        String typeName = type.name;
        if (typeName == null) {
            this.reader.reportError(GrammarReader.ERR_MISSING_ATTRIBUTE, "simpleType", "name");
        } else {
            getReader().addUserDefinedType(type);
        }
    }
}
