package com.ctc.wstx.shaded.msv_core.reader.relax.core;

import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;
import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.reader.GrammarReader;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.FacetStateParent;
import com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.XSTypeIncubator;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/reader/relax/core/ElementRuleWithTypeState.class */
public class ElementRuleWithTypeState extends ElementRuleBaseState implements FacetStateParent {
    protected XSTypeIncubator incubator;

    @Override // com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.FacetStateParent
    public XSTypeIncubator getIncubator() {
        return this.incubator;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.State
    protected void startSelf() {
        super.startSelf();
        RELAXCoreReader reader = (RELAXCoreReader) this.reader;
        this.incubator = reader.resolveXSDatatype(this.startTag.getAttribute("type")).createIncubator();
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.relax.core.ElementRuleBaseState
    protected Expression getContentModel() {
        try {
            return this.incubator.derive(null, null);
        } catch (DatatypeException e) {
            this.reader.reportError(e, GrammarReader.ERR_BAD_TYPE, e.getMessage());
            return Expression.anyString;
        }
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.relax.core.ElementRuleBaseState, com.ctc.wstx.shaded.msv_core.reader.SimpleState
    protected State createChildState(StartTagInfo tag) {
        State next = getReader().createFacetState(this, tag);
        return next != null ? next : super.createChildState(tag);
    }
}
