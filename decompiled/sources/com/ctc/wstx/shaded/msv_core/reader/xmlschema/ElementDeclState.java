package com.ctc.wstx.shaded.msv_core.reader.xmlschema;

import com.ctc.wstx.shaded.msv_core.datatype.xsd.BooleanType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.TokenType;
import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceExp;
import com.ctc.wstx.shaded.msv_core.grammar.SimpleNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.ElementDeclExp;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.ElementDeclExp.XSElementExp;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.IdentityConstraint;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.XMLSchemaSchema;
import com.ctc.wstx.shaded.msv_core.reader.ExpressionWithChildState;
import com.ctc.wstx.shaded.msv_core.reader.GrammarReader;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;
import com.ctc.wstx.shaded.msv_core.util.StringPair;
import java.util.Vector;
import org.xml.sax.Locator;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/reader/xmlschema/ElementDeclState.class */
public class ElementDeclState extends ExpressionWithChildState {
    protected final Vector idcs = new Vector();

    @Override // com.ctc.wstx.shaded.msv_core.reader.ExpressionWithChildState, com.ctc.wstx.shaded.msv_core.reader.SimpleState
    protected State createChildState(StartTagInfo tag) {
        XMLSchemaReader reader = (XMLSchemaReader) this.reader;
        if (this.exp == null) {
            if (tag.localName.equals("simpleType")) {
                return reader.sfactory.simpleType(this, tag);
            }
            if (tag.localName.equals("complexType")) {
                return reader.sfactory.complexTypeDecl(this, tag);
            }
        }
        if (tag.localName.equals("unique")) {
            return reader.sfactory.unique(this, tag);
        }
        if (tag.localName.equals("key")) {
            return reader.sfactory.key(this, tag);
        }
        if (tag.localName.equals("keyref")) {
            return reader.sfactory.keyref(this, tag);
        }
        return null;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.ExpressionWithChildState
    protected Expression initialExpression() {
        String typeQName = this.startTag.getAttribute("type");
        if (typeQName == null) {
            return null;
        }
        return resolveTypeRef(typeQName);
    }

    protected Expression resolveTypeRef(final String typeQName) {
        final XMLSchemaReader reader = (XMLSchemaReader) this.reader;
        final ReferenceExp ref = new ReferenceExp("elementType(" + typeQName + ")");
        final String[] s = reader.splitQName(typeQName);
        if (s == null) {
            reader.reportError(XMLSchemaReader.ERR_UNDECLARED_PREFIX, typeQName);
            ref.exp = Expression.nullSet;
            return ref;
        }
        reader.addBackPatchJob(new GrammarReader.BackPatch() { // from class: com.ctc.wstx.shaded.msv_core.reader.xmlschema.ElementDeclState.1
            @Override // com.ctc.wstx.shaded.msv_core.reader.GrammarReader.BackPatch
            public State getOwnerState() {
                return ElementDeclState.this;
            }

            @Override // com.ctc.wstx.shaded.msv_core.reader.GrammarReader.BackPatch
            public void patch() {
                Expression e = null;
                if (reader.isSchemaNamespace(s[0])) {
                    e = reader.resolveBuiltinSimpleType(s[1]);
                }
                if (e == null) {
                    XMLSchemaSchema g = reader.getOrCreateSchema(s[0]);
                    e = g.simpleTypes.get(s[1]);
                    if (e == null) {
                        e = g.complexTypes.get(s[1]);
                    }
                    if (e == null) {
                        reader.reportError(XMLSchemaReader.ERR_UNDEFINED_ELEMENTTYPE, typeQName);
                        e = Expression.nullSet;
                    }
                }
                ref.exp = e;
            }
        });
        return ref;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.ExpressionWithChildState
    protected Expression castExpression(Expression halfCastedExpression, Expression newChildExpression) {
        if (halfCastedExpression != null) {
            throw new Error();
        }
        return newChildExpression;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.ExpressionWithChildState
    protected Expression defaultExpression() {
        if (this.startTag.containsAttribute("substitutionGroup")) {
            this.reader.reportError(XMLSchemaReader.ERR_UNIMPLEMENTED_FEATURE, "omitting type attribute in <element> element with substitutionGroup attribute");
        }
        XMLSchemaReader reader = (XMLSchemaReader) this.reader;
        reader.reportWarning(XMLSchemaReader.WRN_IMPLICIT_URTYPE_FOR_ELEMENT, null);
        return reader.complexUrType;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.ExpressionWithChildState
    protected Expression annealExpression(Expression contentType) {
        String targetNamespace;
        ElementDeclExp decl;
        XMLSchemaReader reader = (XMLSchemaReader) this.reader;
        String name = this.startTag.getAttribute("name");
        if (name == null) {
            reader.reportError(GrammarReader.ERR_MISSING_ATTRIBUTE, "element", "name");
            return Expression.nullSet;
        }
        if (isGlobal()) {
            targetNamespace = reader.currentSchema.targetNamespace;
        } else {
            targetNamespace = reader.resolveNamespaceOfElementDecl(this.startTag.getAttribute("form"));
        }
        String fixed = this.startTag.getAttribute("fixed");
        if (fixed != null) {
            contentType = reader.pool.createValue(TokenType.theInstance, new StringPair("", "token"), fixed);
        }
        if (isGlobal()) {
            decl = reader.currentSchema.elementDecls.getOrCreate(name);
            if (decl.getElementExp() != null) {
                reader.reportError(new Locator[]{this.location, reader.getDeclaredLocationOf(decl)}, XMLSchemaReader.ERR_DUPLICATE_ELEMENT_DEFINITION, new Object[]{name});
            }
        } else {
            decl = new ElementDeclExp(reader.currentSchema, null);
        }
        reader.setDeclaredLocationOf(decl);
        ElementDeclExp elementDeclExp = decl;
        elementDeclExp.getClass();
        ElementDeclExp.XSElementExp exp = elementDeclExp.new XSElementExp(new SimpleNameClass(targetNamespace, name), contentType);
        decl.setElementExp(exp);
        exp.identityConstraints.addAll(this.idcs);
        String nillable = this.startTag.getAttribute("nillable");
        if (nillable != null) {
            decl.isNillable = nillable.equals("true") || nillable.equals("1");
        }
        String abstract_ = this.startTag.getAttribute("abstract");
        decl.setAbstract("true".equals(abstract_) || "1".equals(abstract_));
        if (abstract_ != null && !BooleanType.theInstance.isValid(abstract_, null)) {
            reader.reportError(GrammarReader.ERR_BAD_ATTRIBUTE_VALUE, "abstract", abstract_);
        }
        String substitutionGroupQName = this.startTag.getAttribute("substitutionGroup");
        if (substitutionGroupQName != null) {
            String[] r = reader.splitQName(substitutionGroupQName);
            if (r == null) {
                reader.reportError(XMLSchemaReader.ERR_UNDECLARED_PREFIX, substitutionGroupQName);
            } else {
                ElementDeclExp head = reader.getOrCreateSchema(r[0]).elementDecls.getOrCreate(r[1]);
                decl.substitutionAffiliation = head;
            }
        }
        String block = this.startTag.getAttribute("block");
        if (block == null) {
            block = reader.blockDefault;
        }
        if (block != null) {
            if (block.indexOf("#all") >= 0) {
                decl.block |= 7;
            }
            if (block.indexOf("extension") >= 0) {
                decl.block |= 2;
            }
            if (block.indexOf("restriction") >= 0) {
                decl.block |= 1;
            }
            if (block.indexOf("substitution") >= 0) {
                decl.block |= 4;
            }
        }
        String finalValue = this.startTag.getAttribute("final");
        if (finalValue == null) {
            finalValue = reader.finalDefault;
        }
        if (finalValue != null) {
            if (finalValue.indexOf("#all") >= 0) {
                decl.finalValue |= 7;
            }
            if (finalValue.indexOf("extension") >= 0) {
                decl.finalValue |= 2;
            }
            if (finalValue.indexOf("restriction") >= 0) {
                decl.finalValue |= 1;
            }
        }
        return annealDeclaration(decl);
    }

    protected Expression annealDeclaration(ElementDeclExp exp) {
        return exp;
    }

    public boolean isGlobal() {
        return this.parentState instanceof GlobalDeclState;
    }

    protected void onIdentityConstraint(IdentityConstraint idc) {
        this.idcs.add(idc);
    }
}
