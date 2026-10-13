package com.ctc.wstx.shaded.msv_core.verifier.regexp.xmlschema;

import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.DatatypeFactory;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.QnameType;
import com.ctc.wstx.shaded.msv_core.driver.textui.Debug;
import com.ctc.wstx.shaded.msv_core.grammar.ElementExp;
import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.ComplexTypeExp;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.ElementDeclExp;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.SimpleTypeExp;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.XMLSchemaSchema;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.XMLSchemaTypeExp;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;
import com.ctc.wstx.shaded.msv_core.util.StringRef;
import com.ctc.wstx.shaded.msv_core.verifier.Acceptor;
import com.ctc.wstx.shaded.msv_core.verifier.regexp.AttributeToken;
import com.ctc.wstx.shaded.msv_core.verifier.regexp.SimpleAcceptor;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/verifier/regexp/xmlschema/XSAcceptor.class */
public class XSAcceptor extends SimpleAcceptor {
    public static final String XSINamespace = "http://www.w3.org/2001/XMLSchema-instance";
    public static final String XMLSchemaNamespace = "http://www.w3.org/2001/XMLSchema";
    private final XSREDocDecl _docDecl;

    public XSAcceptor(XSREDocDecl docDecl, Expression combined, ElementExp owner, Expression continuation) {
        super(docDecl, combined, owner, continuation);
        this._docDecl = docDecl;
    }

    @Override // com.ctc.wstx.shaded.msv_core.verifier.regexp.SimpleAcceptor, com.ctc.wstx.shaded.msv_core.verifier.Acceptor
    public Acceptor createClone() {
        return new XSAcceptor(this._docDecl, getExpression(), this.owner, this.continuation);
    }

    @Override // com.ctc.wstx.shaded.msv_core.verifier.regexp.ContentModelAcceptor, com.ctc.wstx.shaded.msv_core.verifier.regexp.ExpressionAcceptor
    protected Acceptor createAcceptor(Expression combined, Expression continuation, ElementExp[] primitives, int numPrimitives) {
        if (primitives == null || numPrimitives <= 1) {
            return new XSAcceptor((XSREDocDecl) this.docDecl, combined, primitives == null ? null : primitives[0], continuation);
        }
        return new XSAcceptor((XSREDocDecl) this.docDecl, primitives[0].contentModel.getExpandedExp(this.docDecl.pool), primitives[0], null);
    }

    @Override // com.ctc.wstx.shaded.msv_core.verifier.regexp.ExpressionAcceptor
    protected boolean onAttribute(AttributeToken token, StringRef refErr) {
        if (token.namespaceURI.equals("http://www.w3.org/2001/XMLSchema-instance")) {
            token.match(this._docDecl.xsiAttExp);
            return true;
        }
        return super.onAttribute(token, refErr);
    }

    @Override // com.ctc.wstx.shaded.msv_core.verifier.regexp.ExpressionAcceptor, com.ctc.wstx.shaded.msv_core.verifier.Acceptor
    public Acceptor createChildAcceptor(StartTagInfo sti, StringRef refErr) {
        Expression contentModel;
        String type = sti.getAttribute("http://www.w3.org/2001/XMLSchema-instance", "type");
        String nil = sti.getAttribute("http://www.w3.org/2001/XMLSchema-instance", "nil");
        if (type == null && nil == null) {
            return super.createChildAcceptor(sti, refErr);
        }
        this._docDecl.getCCCEC().get(getExpression(), sti, true);
        switch (this._docDecl.getCCCEC().numMatchedElements()) {
            case 0:
                return super.createChildAcceptor(sti, refErr);
            case 1:
                ElementExp element = this._docDecl.getCCCEC().getMatchedElements()[0];
                if (!(element instanceof ElementDeclExp.XSElementExp)) {
                    return super.createChildAcceptor(sti, refErr);
                }
                ElementDeclExp.XSElementExp xe = (ElementDeclExp.XSElementExp) element;
                if (nil != null) {
                    if (!xe.parent.isNillable) {
                        if (refErr == null) {
                            return null;
                        }
                        refErr.str = this._docDecl.localizeMessage(XSREDocDecl.ERR_NON_NILLABLE_ELEMENT, sti.qName);
                        return new XSAcceptor(this._docDecl, Expression.epsilon, xe, null);
                    }
                    if (nil.trim().equals("true")) {
                        if (Debug.debug) {
                            System.out.println("xsi:nil is found");
                        }
                        return new XSAcceptor(this._docDecl, Expression.epsilon, xe, null);
                    }
                }
                if (type == null) {
                    return super.createChildAcceptor(sti, refErr);
                }
                String[] typeName = (String[]) QnameType.theInstance.createJavaObject(type, sti.context);
                if (typeName == null) {
                    return onTypeResolutionFailure(sti, type, refErr);
                }
                if (typeName[0].equals("http://www.w3.org/2001/XMLSchema")) {
                    try {
                        contentModel = this._docDecl.grammar.getPool().createData(DatatypeFactory.getTypeByName(typeName[1]));
                    } catch (DatatypeException e) {
                        return onTypeResolutionFailure(sti, type, refErr);
                    }
                    break;
                } else {
                    XMLSchemaSchema schema = this._docDecl.grammar.getByNamespace(typeName[0]);
                    if (schema == null) {
                        return onTypeResolutionFailure(sti, type, refErr);
                    }
                    XMLSchemaTypeExp currentType = xe.parent.getTypeDefinition();
                    ComplexTypeExp cexp = schema.complexTypes.get(typeName[1]);
                    if (cexp != null) {
                        if (cexp.isDerivedTypeOf(currentType, xe.parent.block | currentType.getBlock())) {
                            contentModel = cexp;
                        } else {
                            return onNotSubstitutableType(sti, type, refErr);
                        }
                    } else {
                        SimpleTypeExp sexp = schema.simpleTypes.get(typeName[1]);
                        if (sexp == null) {
                            return onTypeResolutionFailure(sti, type, refErr);
                        }
                        if (!(currentType instanceof SimpleTypeExp)) {
                            return onNotSubstitutableType(sti, type, refErr);
                        }
                        SimpleTypeExp curT = (SimpleTypeExp) currentType;
                        if (sexp.getDatatype().isDerivedTypeOf(curT.getDatatype(), !xe.parent.isRestrictionBlocked())) {
                            contentModel = sexp;
                        } else {
                            return onNotSubstitutableType(sti, type, refErr);
                        }
                    }
                }
                return new XSAcceptor(this._docDecl, contentModel, xe, null);
            default:
                return super.createChildAcceptor(sti, refErr);
        }
    }

    private Acceptor onNotSubstitutableType(StartTagInfo sti, String type, StringRef refErr) {
        if (refErr == null) {
            return null;
        }
        refErr.str = this._docDecl.localizeMessage(XSREDocDecl.ERR_NOT_SUBSTITUTABLE_TYPE, type);
        return super.createChildAcceptor(sti, refErr);
    }

    private Acceptor onTypeResolutionFailure(StartTagInfo sti, String type, StringRef refErr) {
        if (refErr == null) {
            return null;
        }
        refErr.str = this._docDecl.localizeMessage(XSREDocDecl.ERR_UNDEFINED_TYPE, type);
        return super.createChildAcceptor(sti, refErr);
    }
}
