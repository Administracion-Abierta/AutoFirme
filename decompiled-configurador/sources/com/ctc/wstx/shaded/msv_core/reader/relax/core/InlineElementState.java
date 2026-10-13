package com.ctc.wstx.shaded.msv_core.reader.relax.core;

import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;
import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.SimpleNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.relax.AttPoolClause;
import com.ctc.wstx.shaded.msv_core.grammar.relax.ElementRule;
import com.ctc.wstx.shaded.msv_core.grammar.relax.TagClause;
import com.ctc.wstx.shaded.msv_core.reader.ExpressionState;
import com.ctc.wstx.shaded.msv_core.reader.GrammarReader;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.FacetStateParent;
import com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.XSTypeIncubator;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/reader/relax/core/InlineElementState.class */
public class InlineElementState extends ExpressionState implements FacetStateParent {
    protected XSTypeIncubator incubator;

    @Override // com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.FacetStateParent
    public XSTypeIncubator getIncubator() {
        return this.incubator;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.SimpleState
    protected State createChildState(StartTagInfo tag) {
        if (this.incubator != null) {
            return ((RELAXCoreReader) this.reader).createFacetState(this, tag);
        }
        return null;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.State
    protected void startSelf() {
        super.startSelf();
        RELAXCoreReader reader = (RELAXCoreReader) this.reader;
        String type = this.startTag.getAttribute("type");
        String label = this.startTag.getAttribute("label");
        if (type != null && label != null) {
            reader.reportError(GrammarReader.ERR_CONFLICTING_ATTRIBUTES, "type", "label");
        }
        if (type == null && label == null) {
            reader.reportError(GrammarReader.ERR_MISSING_ATTRIBUTE_2, "element", "type", "label");
            type = "string";
        }
        if (label != null) {
            this.incubator = null;
        } else {
            this.incubator = reader.resolveXSDatatype(type).createIncubator();
        }
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.ExpressionState
    protected Expression makeExpression() {
        Expression contentModel;
        try {
            RELAXCoreReader reader = (RELAXCoreReader) this.reader;
            String name = this.startTag.getAttribute("name");
            if (name == null) {
                reader.reportError(GrammarReader.ERR_MISSING_ATTRIBUTE, "element", "name");
                return Expression.nullSet;
            }
            if (this.incubator != null) {
                contentModel = this.incubator.derive(null, null);
            } else {
                String label = this.startTag.getAttribute("label");
                if (label == null) {
                    throw new Error();
                }
                contentModel = reader.module.hedgeRules.getOrCreate(label);
                reader.backwardReference.memorizeLink(contentModel);
            }
            TagClause c = new TagClause();
            c.nameClass = new SimpleNameClass(reader.module.targetNamespace, name);
            String role = this.startTag.getAttribute("role");
            if (role == null) {
                c.exp = Expression.epsilon;
            } else {
                AttPoolClause att = reader.module.attPools.getOrCreate(role);
                c.exp = att;
                reader.backwardReference.memorizeLink(att);
            }
            return new ElementRule(reader.pool, c, contentModel);
        } catch (DatatypeException e) {
            this.reader.reportError(e, GrammarReader.ERR_BAD_TYPE, e.getMessage());
            return Expression.nullSet;
        }
    }
}
