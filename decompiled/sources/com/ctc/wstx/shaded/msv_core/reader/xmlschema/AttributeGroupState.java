package com.ctc.wstx.shaded.msv_core.reader.xmlschema;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceContainer;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.AttributeGroupExp;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.AttributeWildcard;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.XMLSchemaSchema;
import com.ctc.wstx.shaded.msv_core.reader.GrammarReader;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;
import org.xml.sax.Locator;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/reader/xmlschema/AttributeGroupState.class */
public class AttributeGroupState extends RedefinableDeclState implements AnyAttributeOwner {
    private AttributeWildcard wildcard;

    @Override // com.ctc.wstx.shaded.msv_core.reader.ExpressionWithChildState, com.ctc.wstx.shaded.msv_core.reader.SimpleState
    protected State createChildState(StartTagInfo tag) {
        XMLSchemaReader reader = (XMLSchemaReader) this.reader;
        return reader.createAttributeState(this, tag);
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.xmlschema.RedefinableDeclState
    protected ReferenceContainer getContainer() {
        return ((XMLSchemaReader) this.reader).currentSchema.attributeGroups;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.ExpressionWithChildState
    protected Expression initialExpression() {
        XMLSchemaReader reader = (XMLSchemaReader) this.reader;
        String refQName = this.startTag.getAttribute("ref");
        if (refQName == null) {
            return Expression.epsilon;
        }
        Expression exp = reader.resolveQNameRef(this.startTag, "ref", new XMLSchemaReader.RefResolver() { // from class: com.ctc.wstx.shaded.msv_core.reader.xmlschema.AttributeGroupState.1
            @Override // com.ctc.wstx.shaded.msv_core.reader.xmlschema.XMLSchemaReader.RefResolver
            public ReferenceContainer get(XMLSchemaSchema g) {
                return g.attributeGroups;
            }
        });
        return exp == null ? Expression.epsilon : exp;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.xmlschema.AnyAttributeOwner
    public void setAttributeWildcard(AttributeWildcard local) {
        this.wildcard = local;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.ExpressionWithChildState
    protected Expression castExpression(Expression halfCastedExpression, Expression newChildExpression) {
        if (this.startTag.containsAttribute("ref")) {
            this.reader.reportError(GrammarReader.ERR_MORE_THAN_ONE_CHILD_EXPRESSION);
        }
        if (halfCastedExpression == null) {
            return newChildExpression;
        }
        return this.reader.pool.createSequence(newChildExpression, halfCastedExpression);
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.ExpressionWithChildState
    protected Expression annealExpression(Expression contentType) {
        AttributeGroupExp exp;
        XMLSchemaReader reader = (XMLSchemaReader) this.reader;
        if (!isGlobal()) {
            return contentType;
        }
        String name = this.startTag.getAttribute("name");
        if (name == null) {
            reader.reportError(GrammarReader.ERR_MISSING_ATTRIBUTE, "attributeGroup", "name");
            return Expression.epsilon;
        }
        if (isRedefine()) {
            exp = (AttributeGroupExp) this.oldDecl;
        } else {
            exp = reader.currentSchema.attributeGroups.getOrCreate(name);
            if (exp.exp != null) {
                reader.reportError(new Locator[]{this.location, reader.getDeclaredLocationOf(exp)}, XMLSchemaReader.ERR_DUPLICATE_ATTRIBUTE_GROUP_DEFINITION, new Object[]{name});
            }
        }
        reader.setDeclaredLocationOf(exp);
        exp.exp = contentType;
        exp.wildcard = this.wildcard;
        return exp;
    }
}
