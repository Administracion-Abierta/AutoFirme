package com.ctc.wstx.shaded.msv_core.relaxns.verifier;

import com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.AttributesDecl;
import com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.AttributesVerifier;
import com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.ElementDecl;
import com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.IslandSchema;
import com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.IslandVerifier;
import com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.SchemaProvider;
import com.ctc.wstx.shaded.msv_core.grammar.AttributeExp;
import com.ctc.wstx.shaded.msv_core.grammar.ElementExp;
import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionCloner;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionPool;
import com.ctc.wstx.shaded.msv_core.grammar.Grammar;
import com.ctc.wstx.shaded.msv_core.grammar.OtherExp;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceContainer;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceExp;
import com.ctc.wstx.shaded.msv_core.relaxns.grammar.DeclImpl;
import com.ctc.wstx.shaded.msv_core.relaxns.grammar.ExternalAttributeExp;
import com.ctc.wstx.shaded.msv_core.relaxns.grammar.ExternalElementExp;
import com.ctc.wstx.shaded.msv_core.verifier.regexp.REDocumentDeclaration;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/relaxns/verifier/IslandSchemaImpl.class */
public abstract class IslandSchemaImpl implements IslandSchema, Serializable {
    protected final Map elementDecls = new HashMap();
    protected final Map attributesDecls = new HashMap();

    protected abstract Grammar getGrammar();

    @Override // com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.IslandSchema
    public IslandVerifier createNewVerifier(String namespace, ElementDecl[] rules) {
        DeclImpl[] ri = new DeclImpl[rules.length];
        System.arraycopy(rules, 0, ri, 0, rules.length);
        return new TREXIslandVerifier(new RulesAcceptor(new REDocumentDeclaration(getGrammar()), ri));
    }

    @Override // com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.IslandSchema
    public ElementDecl getElementDeclByName(String name) {
        return (ElementDecl) this.elementDecls.get(name);
    }

    @Override // com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.IslandSchema
    public Iterator iterateElementDecls() {
        return this.elementDecls.values().iterator();
    }

    @Override // com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.IslandSchema
    public ElementDecl[] getElementDecls() {
        DeclImpl[] declImplArr = new DeclImpl[this.elementDecls.size()];
        this.elementDecls.values().toArray(declImplArr);
        return declImplArr;
    }

    @Override // com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.IslandSchema
    public AttributesDecl getAttributesDeclByName(String name) {
        return (AttributesDecl) this.attributesDecls.get(name);
    }

    @Override // com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.IslandSchema
    public Iterator iterateAttributesDecls() {
        return this.attributesDecls.values().iterator();
    }

    @Override // com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.IslandSchema
    public AttributesDecl[] getAttributesDecls() {
        DeclImpl[] declImplArr = new DeclImpl[this.attributesDecls.size()];
        this.attributesDecls.values().toArray(declImplArr);
        return declImplArr;
    }

    @Override // com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.IslandSchema
    public AttributesVerifier createNewAttributesVerifier(String namespaceURI, AttributesDecl[] decls) {
        throw new Error("not implemented");
    }

    protected void bind(ReferenceContainer con, Binder binder) {
        ReferenceExp[] exps = con.getAll();
        for (int i = 0; i < exps.length; i++) {
            exps[i].exp = exps[i].exp.visit(binder);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/relaxns/verifier/IslandSchemaImpl$Binder.class */
    public static class Binder extends ExpressionCloner {
        protected final SchemaProvider provider;
        protected final ErrorHandler errorHandler;
        private final Set boundElements;
        public static final String ERR_UNEXPORTED_ELEMENT_DECL = "IslandSchemaImpl.UnexportedElementDecl";
        public static final String ERR_UNDEFINED_NAMESPACE = "IslandSchemaImpl.UndefinedNamespace";
        public static final String ERR_UNEXPORTED_ATTRIBUTE_DECL = "IslandSchemaImpl.UnexportedAttributeDecl";
        public static final String ERR_UNSUPPROTED_ATTRIBUTES_IMPORT = "IslandSchemaImpl.UnsupportedAttributesImport";

        public Binder(SchemaProvider provider, ErrorHandler errorHandler, ExpressionPool pool) {
            super(pool);
            this.boundElements = new HashSet();
            this.provider = provider;
            this.errorHandler = errorHandler;
        }

        @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorExpression
        public Expression onAttribute(AttributeExp exp) {
            return exp;
        }

        @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorExpression
        public Expression onRef(ReferenceExp exp) {
            return exp.exp.visit(this);
        }

        @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorExpression
        public Expression onOther(OtherExp exp) {
            try {
                if (exp instanceof ExternalAttributeExp) {
                    ExternalAttributeExp eexp = (ExternalAttributeExp) exp;
                    IslandSchema is = this.provider.getSchemaByNamespace(eexp.namespaceURI);
                    if (is == null) {
                        this.errorHandler.error(new SAXParseException(localize(ERR_UNDEFINED_NAMESPACE, eexp.namespaceURI), eexp.source));
                        return exp;
                    }
                    AttributesDecl rule = is.getAttributesDeclByName(eexp.role);
                    if (rule == null) {
                        this.errorHandler.error(new SAXParseException(localize(ERR_UNEXPORTED_ATTRIBUTE_DECL, eexp.role), eexp.source));
                        return exp;
                    }
                    if (!(rule instanceof DeclImpl)) {
                        this.errorHandler.error(new SAXParseException(localize(ERR_UNSUPPROTED_ATTRIBUTES_IMPORT), eexp.source));
                        return exp;
                    }
                    return ((DeclImpl) rule).exp;
                }
                return exp.exp.visit(this);
            } catch (SAXException e) {
                return exp;
            }
        }

        @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorExpression
        public Expression onElement(ElementExp exp) {
            try {
                if (!(exp instanceof ExternalElementExp)) {
                    if (this.boundElements.contains(exp)) {
                        return exp;
                    }
                    this.boundElements.add(exp);
                    exp.contentModel = exp.contentModel.visit(this);
                    return exp;
                }
                ExternalElementExp eexp = (ExternalElementExp) exp;
                IslandSchema is = this.provider.getSchemaByNamespace(eexp.namespaceURI);
                if (is == null) {
                    this.errorHandler.error(new SAXParseException(localize(ERR_UNDEFINED_NAMESPACE, eexp.namespaceURI), eexp.source));
                    return exp;
                }
                eexp.rule = is.getElementDeclByName(eexp.ruleName);
                if (eexp.rule == null) {
                    this.errorHandler.error(new SAXParseException(localize(ERR_UNEXPORTED_ELEMENT_DECL, eexp.ruleName), eexp.source));
                    return exp;
                }
                if (eexp.rule instanceof DeclImpl) {
                    return ((DeclImpl) eexp.rule).exp;
                }
                return exp;
            } catch (SAXException e) {
                return exp;
            }
        }

        public String localize(String propertyName, Object[] args) {
            String format = ResourceBundle.getBundle("com.ctc.wstx.shaded.msv_core.relaxns.verifier.Messages").getString(propertyName);
            return MessageFormat.format(format, args);
        }

        public String localize(String prop) {
            return localize(prop, (Object[]) null);
        }

        public String localize(String prop, Object arg1) {
            return localize(prop, new Object[]{arg1});
        }

        public String localize(String prop, Object arg1, Object arg2) {
            return localize(prop, new Object[]{arg1, arg2});
        }
    }
}
