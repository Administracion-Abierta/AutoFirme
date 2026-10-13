package com.ctc.wstx.shaded.msv_core.reader.relax.core;

import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;
import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.SimpleNameClass;
import com.ctc.wstx.shaded.msv_core.reader.ExpressionState;
import com.ctc.wstx.shaded.msv_core.reader.GrammarReader;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.FacetStateParent;
import com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.XSTypeIncubator;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/reader/relax/core/AttributeState.class */
public class AttributeState extends ExpressionState implements FacetStateParent {
    protected XSTypeIncubator incubator;

    @Override // com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.FacetStateParent
    public XSTypeIncubator getIncubator() {
        return this.incubator;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.State
    protected void startSelf() {
        super.startSelf();
        RELAXCoreReader reader = (RELAXCoreReader) this.reader;
        String type = this.startTag.getAttribute("type");
        if (type == null) {
            type = "string";
        }
        this.incubator = reader.resolveXSDatatype(type).createIncubator();
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.ExpressionState
    protected Expression makeExpression() {
        try {
            String name = this.startTag.getAttribute("name");
            String required = this.startTag.getAttribute("required");
            if (name == null) {
                this.reader.reportError(GrammarReader.ERR_MISSING_ATTRIBUTE, "attribute", "name");
                return Expression.epsilon;
            }
            Expression exp = this.reader.pool.createAttribute(new SimpleNameClass("", name), this.incubator.derive(null, null));
            if (!"true".equals(required)) {
                exp = this.reader.pool.createOptional(exp);
            }
            return exp;
        } catch (DatatypeException e) {
            this.reader.reportError(e, GrammarReader.ERR_BAD_TYPE, e.getMessage());
            return Expression.anyString;
        }
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.SimpleState
    protected State createChildState(StartTagInfo tag) {
        return ((RELAXCoreReader) this.reader).createFacetState(this, tag);
    }
}
