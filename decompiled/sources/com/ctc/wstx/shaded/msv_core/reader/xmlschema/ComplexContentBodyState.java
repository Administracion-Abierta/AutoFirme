package com.ctc.wstx.shaded.msv_core.reader.xmlschema;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.AttributeWildcard;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.ComplexTypeExp;
import com.ctc.wstx.shaded.msv_core.reader.GrammarReader;
import com.ctc.wstx.shaded.msv_core.reader.SequenceState;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/reader/xmlschema/ComplexContentBodyState.class */
public class ComplexContentBodyState extends SequenceState implements AnyAttributeOwner {
    protected ComplexTypeExp parentDecl;
    protected boolean extension;

    protected ComplexContentBodyState(ComplexTypeExp parentDecl, boolean extension) {
        this.parentDecl = parentDecl;
        this.extension = extension;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.ExpressionWithChildState, com.ctc.wstx.shaded.msv_core.reader.SimpleState
    protected State createChildState(StartTagInfo tag) {
        State s;
        XMLSchemaReader reader = (XMLSchemaReader) this.reader;
        return (this.exp != Expression.epsilon || (s = reader.createModelGroupState(this, tag)) == null) ? reader.createAttributeState(this, tag) : s;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.xmlschema.AnyAttributeOwner
    public void setAttributeWildcard(AttributeWildcard local) {
        this.parentDecl.wildcard = local;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.SequenceState, com.ctc.wstx.shaded.msv_core.reader.ExpressionWithChildState
    protected Expression initialExpression() {
        return Expression.epsilon;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.ExpressionWithChildState
    protected Expression annealExpression(Expression exp) {
        XMLSchemaReader reader = (XMLSchemaReader) this.reader;
        Expression exp2 = super.annealExpression(exp);
        String refQName = this.startTag.getAttribute("base");
        if (refQName == null) {
            reader.reportError(GrammarReader.ERR_MISSING_ATTRIBUTE, this.startTag.qName, "base");
            return exp2;
        }
        String[] r = reader.splitQName(refQName);
        if (r == null) {
            reader.reportError(XMLSchemaReader.ERR_UNDECLARED_PREFIX, refQName);
            return exp2;
        }
        if (reader.isSchemaNamespace(r[0]) && r[1].equals("anyType")) {
            return exp2;
        }
        ComplexTypeExp baseType = reader.getOrCreateSchema(r[0]).complexTypes.getOrCreate(r[1]);
        if (baseType == null) {
            return exp2;
        }
        reader.backwardReference.memorizeLink(baseType);
        this.parentDecl.derivationMethod = this.extension ? 2 : 1;
        this.parentDecl.complexBaseType = baseType;
        return combineToBaseType(baseType, exp2);
    }

    protected Expression combineToBaseType(ComplexTypeExp baseType, Expression addedExp) {
        if (this.extension) {
            return this.reader.pool.createSequence(baseType.body, addedExp);
        }
        return addedExp;
    }
}
