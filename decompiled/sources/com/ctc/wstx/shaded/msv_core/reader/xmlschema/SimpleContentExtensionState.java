package com.ctc.wstx.shaded.msv_core.reader.xmlschema;

import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype;
import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceExp;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.AttributeWildcard;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.ComplexTypeExp;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.SimpleTypeExp;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.XMLSchemaSchema;
import com.ctc.wstx.shaded.msv_core.reader.GrammarReader;
import com.ctc.wstx.shaded.msv_core.reader.SequenceState;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.XSDatatypeExp;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/reader/xmlschema/SimpleContentExtensionState.class */
public class SimpleContentExtensionState extends SequenceState implements AnyAttributeOwner {
    protected ComplexTypeExp parentDecl;

    protected SimpleContentExtensionState(ComplexTypeExp parentDecl) {
        this.parentDecl = parentDecl;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.xmlschema.AnyAttributeOwner
    public void setAttributeWildcard(AttributeWildcard local) {
        this.parentDecl.wildcard = local;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.ExpressionWithChildState, com.ctc.wstx.shaded.msv_core.reader.SimpleState
    protected State createChildState(StartTagInfo tag) {
        XMLSchemaReader reader = (XMLSchemaReader) this.reader;
        return reader.createAttributeState(this, tag);
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.SequenceState, com.ctc.wstx.shaded.msv_core.reader.ExpressionWithChildState
    protected Expression initialExpression() {
        return Expression.epsilon;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.ExpressionWithChildState
    protected Expression annealExpression(Expression exp) {
        this.parentDecl.derivationMethod = 2;
        return this.reader.pool.createSequence(super.annealExpression(exp), getBody());
    }

    private Expression getBody() {
        XSDatatype dt;
        final XMLSchemaReader reader = (XMLSchemaReader) this.reader;
        final String base = this.startTag.getAttribute("base");
        if (base == null) {
            reader.reportError(GrammarReader.ERR_MISSING_ATTRIBUTE, this.startTag.localName, "base");
            return Expression.nullSet;
        }
        final String[] baseTypeName = reader.splitQName(base);
        if (baseTypeName == null) {
            reader.reportError(XMLSchemaReader.ERR_UNDECLARED_PREFIX, base);
            return Expression.nullSet;
        }
        if (reader.isSchemaNamespace(baseTypeName[0]) && (dt = reader.resolveBuiltinDataType(baseTypeName[1])) != null) {
            XSDatatypeExp dtexp = new XSDatatypeExp(dt, reader.pool);
            this.parentDecl.simpleBaseType = dtexp;
            return dtexp;
        }
        final XMLSchemaSchema schema = reader.grammar.getByNamespace(baseTypeName[0]);
        final ReferenceExp ref = new ReferenceExp(null);
        reader.addBackPatchJob(new GrammarReader.BackPatch() { // from class: com.ctc.wstx.shaded.msv_core.reader.xmlschema.SimpleContentExtensionState.1
            @Override // com.ctc.wstx.shaded.msv_core.reader.GrammarReader.BackPatch
            public State getOwnerState() {
                return SimpleContentExtensionState.this;
            }

            @Override // com.ctc.wstx.shaded.msv_core.reader.GrammarReader.BackPatch
            public void patch() {
                SimpleTypeExp sexp = schema.simpleTypes.get(baseTypeName[1]);
                if (sexp != null) {
                    ref.exp = sexp;
                    SimpleContentExtensionState.this.parentDecl.simpleBaseType = sexp.getType();
                    State._assert(SimpleContentExtensionState.this.parentDecl.simpleBaseType != null);
                } else {
                    ComplexTypeExp cexp = schema.complexTypes.get(baseTypeName[1]);
                    if (cexp != null) {
                        ref.exp = cexp.body;
                        SimpleContentExtensionState.this.parentDecl.complexBaseType = cexp;
                    } else {
                        reader.reportError(XMLSchemaReader.ERR_UNDEFINED_COMPLEX_OR_SIMPLE_TYPE, base);
                    }
                }
            }
        });
        return ref;
    }
}
