package com.ctc.wstx.shaded.msv_core.reader.xmlschema;

import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.TokenType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype;
import com.ctc.wstx.shaded.msv_core.grammar.AttributeExp;
import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.NameClass;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceContainer;
import com.ctc.wstx.shaded.msv_core.grammar.SimpleNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.relax.NoneType;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.AttributeDeclExp;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.XMLSchemaSchema;
import com.ctc.wstx.shaded.msv_core.reader.ExpressionWithChildState;
import com.ctc.wstx.shaded.msv_core.reader.GrammarReader;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.XSDatatypeExp;
import com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.XSTypeIncubator;
import com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.XSTypeOwner;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;
import com.ctc.wstx.shaded.msv_core.util.StringPair;
import es.gob.afirma.signers.pades.common.PdfExtraParams;
import org.xml.sax.Locator;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/reader/xmlschema/AttributeState.class */
public class AttributeState extends ExpressionWithChildState implements XSTypeOwner {
    @Override // com.ctc.wstx.shaded.msv_core.reader.ExpressionWithChildState, com.ctc.wstx.shaded.msv_core.reader.SimpleState
    protected State createChildState(StartTagInfo tag) {
        if (tag.localName.equals("simpleType")) {
            return ((XMLSchemaReader) this.reader).sfactory.simpleType(this, tag);
        }
        return super.createChildState(tag);
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.ExpressionWithChildState
    protected Expression initialExpression() {
        XMLSchemaReader reader = (XMLSchemaReader) this.reader;
        if (this.startTag.containsAttribute("ref")) {
            if (isGlobal()) {
                reader.reportError(GrammarReader.ERR_DISALLOWED_ATTRIBUTE, this.startTag.qName, "ref");
                return Expression.epsilon;
            }
            Expression exp = reader.resolveQNameRef(this.startTag, "ref", new XMLSchemaReader.RefResolver() { // from class: com.ctc.wstx.shaded.msv_core.reader.xmlschema.AttributeState.1
                @Override // com.ctc.wstx.shaded.msv_core.reader.xmlschema.XMLSchemaReader.RefResolver
                public ReferenceContainer get(XMLSchemaSchema g) {
                    return g.attributeDecls;
                }
            });
            return exp == null ? Expression.epsilon : exp;
        }
        String typeAttr = this.startTag.getAttribute("type");
        if (typeAttr == null) {
            return null;
        }
        return reader.resolveXSDatatype(typeAttr);
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.ExpressionWithChildState
    protected Expression defaultExpression() {
        return Expression.anyString;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.ExpressionWithChildState
    protected Expression castExpression(Expression halfCastedExpression, Expression newChildExpression) {
        if (halfCastedExpression != null) {
            this.reader.reportError(GrammarReader.ERR_MORE_THAN_ONE_CHILD_EXPRESSION);
        }
        return newChildExpression;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.ExpressionWithChildState
    protected Expression annealExpression(Expression contentType) {
        Expression exp;
        XMLSchemaReader reader = (XMLSchemaReader) this.reader;
        String fixed = this.startTag.getAttribute("fixed");
        String name = this.startTag.getAttribute("name");
        String use = this.startTag.getAttribute("use");
        if (this.startTag.containsAttribute("ref")) {
            if (fixed != null) {
                reader.reportWarning(XMLSchemaReader.ERR_UNIMPLEMENTED_FEATURE, "<attribute> element with both 'ref' and 'fixed' attributes");
            }
            exp = contentType;
        } else {
            if (name == null) {
                reader.reportError(GrammarReader.ERR_MISSING_ATTRIBUTE, "attribute", "name");
                return Expression.nullSet;
            }
            String targetNamespace = isGlobal() ? reader.currentSchema.targetNamespace : reader.resolveNamespaceOfAttributeDecl(this.startTag.getAttribute("form"));
            if (fixed != null) {
                if (contentType instanceof XSDatatypeExp) {
                    XSDatatypeExp baseType = (XSDatatypeExp) contentType;
                    try {
                        XSTypeIncubator inc = baseType.createIncubator();
                        inc.addFacet(XSDatatype.FACET_ENUMERATION, fixed, false, reader);
                        contentType = inc.derive(null, null);
                    } catch (DatatypeException e) {
                        reader.reportError(e, GrammarReader.ERR_BAD_TYPE, e.getMessage());
                        return Expression.nullSet;
                    }
                } else {
                    contentType = reader.pool.createValue(TokenType.theInstance, new StringPair("", "token"), fixed);
                }
            }
            if ("prohibited".equals(use)) {
                contentType = reader.pool.createData(NoneType.theInstance);
            }
            exp = createAttribute(new SimpleNameClass(targetNamespace, name), contentType);
        }
        if (isGlobal()) {
            AttributeDeclExp decl = reader.currentSchema.attributeDecls.getOrCreate(name);
            if (decl.exp != null) {
                reader.reportError(new Locator[]{this.location, reader.getDeclaredLocationOf(decl)}, XMLSchemaReader.ERR_DUPLICATE_ATTRIBUTE_DEFINITION, new Object[]{name});
            }
            reader.setDeclaredLocationOf(decl);
            if (exp instanceof AttributeExp) {
                decl.set((AttributeExp) exp);
            } else if (!reader.controller.hadError()) {
                throw new Error();
            }
        } else if (PdfExtraParams.VISIBLE_SIGNATURE_VALUE_OPTIONAL.equals(use) || use == null || "prohibited".equals(use)) {
            exp = reader.pool.createOptional(exp);
        } else if (!"required".equals(use)) {
            reader.reportError(GrammarReader.ERR_BAD_ATTRIBUTE_VALUE, "use", use);
        }
        return exp;
    }

    protected Expression createAttribute(NameClass nc, Expression exp) {
        return this.reader.pool.createAttribute(nc, exp);
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.XSTypeOwner
    public String getTargetNamespaceUri() {
        XMLSchemaReader reader = (XMLSchemaReader) this.reader;
        return reader.currentSchema.targetNamespace;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.XSTypeOwner
    public void onEndChild(XSDatatypeExp type) {
        super.onEndChild((Expression) type);
    }

    protected boolean isGlobal() {
        return this.parentState instanceof GlobalDeclState;
    }
}
