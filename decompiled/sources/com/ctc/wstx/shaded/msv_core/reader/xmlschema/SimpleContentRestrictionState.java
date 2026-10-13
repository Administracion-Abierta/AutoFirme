package com.ctc.wstx.shaded.msv_core.reader.xmlschema;

import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.StringType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype;
import com.ctc.wstx.shaded.msv_core.grammar.AttributeExp;
import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceExp;
import com.ctc.wstx.shaded.msv_core.grammar.util.ExpressionWalker;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.AttributeWildcard;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.ComplexTypeExp;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.SimpleTypeExp;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.XMLSchemaSchema;
import com.ctc.wstx.shaded.msv_core.reader.GrammarReader;
import com.ctc.wstx.shaded.msv_core.reader.SequenceState;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.FacetStateParent;
import com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.XSDatatypeExp;
import com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.XSTypeIncubator;
import com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.XSTypeOwner;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/reader/xmlschema/SimpleContentRestrictionState.class */
public class SimpleContentRestrictionState extends SequenceState implements FacetStateParent, XSTypeOwner, AnyAttributeOwner {
    protected ComplexTypeExp parentDecl;
    protected XSTypeIncubator incubator;
    private String base;
    private String[] baseTypeName;
    private XMLSchemaSchema baseSchema;

    protected SimpleContentRestrictionState(ComplexTypeExp parentDecl) {
        this.parentDecl = parentDecl;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.xmlschema.AnyAttributeOwner
    public void setAttributeWildcard(AttributeWildcard local) {
        this.parentDecl.wildcard = local;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.FacetStateParent
    public XSTypeIncubator getIncubator() {
        XSDatatype dt;
        if (this.incubator == null) {
            XMLSchemaReader reader = (XMLSchemaReader) this.reader;
            if (this.baseTypeName == null) {
                this.incubator = new XSDatatypeExp(StringType.theInstance, reader.pool).createIncubator();
            } else {
                if (reader.isSchemaNamespace(this.baseTypeName[0]) && (dt = reader.resolveBuiltinDataType(this.baseTypeName[1])) != null) {
                    this.incubator = new XSDatatypeExp(dt, reader.pool).createIncubator();
                }
                if (this.incubator == null) {
                    this.incubator = new XSDatatypeExp(this.baseTypeName[0], this.baseTypeName[1], reader, new BaseContentTypeRenderer()).createIncubator();
                }
            }
        }
        return this.incubator;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/reader/xmlschema/SimpleContentRestrictionState$BaseContentTypeRenderer.class */
    private class BaseContentTypeRenderer implements XSDatatypeExp.Renderer {
        private BaseContentTypeRenderer() {
        }

        @Override // com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.XSDatatypeExp.Renderer
        public XSDatatype render(XSDatatypeExp.RenderingContext context) {
            XMLSchemaReader reader = (XMLSchemaReader) SimpleContentRestrictionState.this.reader;
            SimpleTypeExp sexp = SimpleContentRestrictionState.this.baseSchema.simpleTypes.get(SimpleContentRestrictionState.this.baseTypeName[1]);
            if (sexp == null) {
                ComplexTypeExp cexp = SimpleContentRestrictionState.this.baseSchema.complexTypes.get(SimpleContentRestrictionState.this.baseTypeName[1]);
                if (cexp != null) {
                    final XSDatatypeExp[] dexp = new XSDatatypeExp[1];
                    cexp.body.visit(new ExpressionWalker() { // from class: com.ctc.wstx.shaded.msv_core.reader.xmlschema.SimpleContentRestrictionState.BaseContentTypeRenderer.1
                        @Override // com.ctc.wstx.shaded.msv_core.grammar.util.ExpressionWalker, com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
                        public void onAttribute(AttributeExp exp) {
                        }

                        @Override // com.ctc.wstx.shaded.msv_core.grammar.util.ExpressionWalker, com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
                        public void onRef(ReferenceExp exp) {
                            if (exp instanceof XSDatatypeExp) {
                                dexp[0] = (XSDatatypeExp) exp;
                            } else {
                                super.onRef(exp);
                            }
                        }
                    });
                    if (dexp[0] == null) {
                        reader.reportError(XMLSchemaReader.ERR_INVALID_BASETYPE_FOR_SIMPLECONTENT, SimpleContentRestrictionState.this.base);
                        return StringType.theInstance;
                    }
                    return dexp[0].getType(context);
                }
                return StringType.theInstance;
            }
            return sexp.getType().getType(context);
        }
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.XSTypeOwner
    public String getTargetNamespaceUri() {
        XMLSchemaReader reader = (XMLSchemaReader) this.reader;
        return reader.currentSchema.targetNamespace;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.XSTypeOwner
    public void onEndChild(XSDatatypeExp child) {
        if (this.incubator != null) {
            throw new Error();
        }
        this.incubator = child.createIncubator();
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.ExpressionWithChildState, com.ctc.wstx.shaded.msv_core.reader.SimpleState
    protected State createChildState(StartTagInfo tag) {
        XMLSchemaReader reader = (XMLSchemaReader) this.reader;
        if (this.incubator == null && tag.localName.equals("simpleType")) {
            return reader.sfactory.simpleType(this, tag);
        }
        State s = reader.createAttributeState(this, tag);
        return s != null ? s : reader.createFacetState(this, tag);
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.SequenceState, com.ctc.wstx.shaded.msv_core.reader.ExpressionWithChildState
    protected Expression initialExpression() {
        return Expression.epsilon;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.ExpressionWithChildState, com.ctc.wstx.shaded.msv_core.reader.State
    protected void startSelf() {
        super.startSelf();
        XMLSchemaReader reader = (XMLSchemaReader) this.reader;
        this.base = this.startTag.getAttribute("base");
        if (this.base == null) {
            reader.reportError(GrammarReader.ERR_MISSING_ATTRIBUTE, this.startTag.localName, "base");
            return;
        }
        this.baseTypeName = reader.splitQName(this.base);
        if (this.baseTypeName == null) {
            reader.reportError(XMLSchemaReader.ERR_UNDECLARED_PREFIX, this.base);
        } else {
            this.baseSchema = reader.grammar.getByNamespace(this.baseTypeName[0]);
        }
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.ExpressionWithChildState
    protected Expression annealExpression(Expression exp) {
        XSDatatype dt;
        final XMLSchemaReader reader = (XMLSchemaReader) this.reader;
        this.parentDecl.derivationMethod = 1;
        try {
            XSDatatypeExp p = getIncubator().derive(null, null);
            Expression exp2 = reader.pool.createSequence(super.annealExpression(exp), p);
            if (reader.isSchemaNamespace(this.baseTypeName[0]) && (dt = reader.resolveBuiltinDataType(this.baseTypeName[1])) != null) {
                this.parentDecl.simpleBaseType = new XSDatatypeExp(dt, reader.pool);
                return exp2;
            }
            reader.addBackPatchJob(new GrammarReader.BackPatch() { // from class: com.ctc.wstx.shaded.msv_core.reader.xmlschema.SimpleContentRestrictionState.1
                @Override // com.ctc.wstx.shaded.msv_core.reader.GrammarReader.BackPatch
                public State getOwnerState() {
                    return SimpleContentRestrictionState.this;
                }

                @Override // com.ctc.wstx.shaded.msv_core.reader.GrammarReader.BackPatch
                public void patch() {
                    SimpleTypeExp sexp = SimpleContentRestrictionState.this.baseSchema.simpleTypes.get(SimpleContentRestrictionState.this.baseTypeName[1]);
                    if (sexp == null) {
                        ComplexTypeExp cexp = SimpleContentRestrictionState.this.baseSchema.complexTypes.get(SimpleContentRestrictionState.this.baseTypeName[1]);
                        if (cexp == null) {
                            reader.reportError(XMLSchemaReader.ERR_UNDEFINED_COMPLEX_OR_SIMPLE_TYPE, SimpleContentRestrictionState.this.base);
                            return;
                        } else {
                            SimpleContentRestrictionState.this.parentDecl.complexBaseType = cexp;
                            return;
                        }
                    }
                    SimpleContentRestrictionState.this.parentDecl.simpleBaseType = sexp.getType();
                    State._assert(SimpleContentRestrictionState.this.parentDecl.simpleBaseType != null);
                }
            });
            return exp2;
        } catch (DatatypeException e) {
            reader.reportError(e, GrammarReader.ERR_BAD_TYPE, e.getMessage());
            return Expression.nullSet;
        }
    }
}
