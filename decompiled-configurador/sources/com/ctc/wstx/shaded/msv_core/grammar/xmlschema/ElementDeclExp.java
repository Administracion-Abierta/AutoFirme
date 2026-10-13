package com.ctc.wstx.shaded.msv_core.grammar.xmlschema;

import com.ctc.wstx.shaded.msv_core.grammar.ChoiceExp;
import com.ctc.wstx.shaded.msv_core.grammar.ElementExp;
import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.NameClass;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceExp;
import com.ctc.wstx.shaded.msv_core.grammar.SimpleNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.util.ExpressionWalker;
import java.util.Vector;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/grammar/xmlschema/ElementDeclExp.class */
public class ElementDeclExp extends ReferenceExp {
    public ElementDeclExp substitutionAffiliation;
    public final ReferenceExp body;
    private XSElementExp element;
    public final ReferenceExp substitutions;
    public final XMLSchemaSchema parent;
    public boolean isNillable;
    public static final int RESTRICTION = 1;
    public static final int EXTENSION = 2;
    public static final int SUBSTITUTION = 4;
    public static final int ALL = 7;
    public int finalValue;
    public int block;
    private static final long serialVersionUID = 1;

    public ElementDeclExp(XMLSchemaSchema schema, String typeLocalName) {
        super(typeLocalName);
        this.body = new ReferenceExp(null);
        this.finalValue = 0;
        this.block = 0;
        this.parent = schema;
        this.substitutions = new ReferenceExp(typeLocalName + ":substitutions");
        this.substitutions.exp = Expression.nullSet;
    }

    public void setElementExp(XSElementExp exp) {
        this.element = exp;
        this.body.exp = exp;
    }

    public XSElementExp getElementExp() {
        return this.element;
    }

    public Expression getContentModel() {
        return this.element.contentModel;
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/grammar/xmlschema/ElementDeclExp$XSElementExp.class */
    public class XSElementExp extends ElementExp {
        public final SimpleNameClass elementName;
        public final Vector identityConstraints;
        public final ElementDeclExp parent;

        @Override // com.ctc.wstx.shaded.msv_core.grammar.ElementExp, com.ctc.wstx.shaded.msv_core.grammar.NameClassAndExpression
        public final NameClass getNameClass() {
            return this.elementName;
        }

        public XSElementExp(SimpleNameClass elementName, Expression contentModel) {
            super(contentModel, false);
            this.identityConstraints = new Vector();
            this.elementName = elementName;
            this.parent = ElementDeclExp.this;
        }
    }

    public boolean isGlobal() {
        return this.parent.elementDecls.get(this.name) == this;
    }

    public final String getTargetNamespace() {
        return this.parent.targetNamespace;
    }

    public boolean isAbstract() {
        if (this.exp instanceof ChoiceExp) {
            ChoiceExp cexp = (ChoiceExp) this.exp;
            if (cexp.exp1 != this.body && cexp.exp2 != this.body) {
                throw new Error();
            }
            return true;
        }
        if (this.exp != this.substitutions) {
            throw new Error();
        }
        return false;
    }

    public void setAbstract(boolean isAbstract) {
        if (!isAbstract) {
            this.exp = this.parent.pool.createChoice(this.substitutions, this.body);
        } else {
            this.exp = this.substitutions;
        }
    }

    public boolean isSubstitutionBlocked() {
        return (this.block & 4) != 0;
    }

    public boolean isRestrictionBlocked() {
        return (this.block & 1) != 0;
    }

    public XMLSchemaTypeExp getTypeDefinition() {
        final RuntimeException eureka = new RuntimeException();
        final XMLSchemaTypeExp[] result = new XMLSchemaTypeExp[1];
        try {
            getContentModel().visit(new ExpressionWalker() { // from class: com.ctc.wstx.shaded.msv_core.grammar.xmlschema.ElementDeclExp.1
                @Override // com.ctc.wstx.shaded.msv_core.grammar.util.ExpressionWalker, com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
                public void onElement(ElementExp exp) {
                }

                @Override // com.ctc.wstx.shaded.msv_core.grammar.util.ExpressionWalker, com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
                public void onRef(ReferenceExp exp) {
                    if (exp instanceof XMLSchemaTypeExp) {
                        result[0] = (XMLSchemaTypeExp) exp;
                        throw eureka;
                    }
                    super.onRef(exp);
                }
            });
            throw new Error();
        } catch (RuntimeException e) {
            if (e == eureka) {
                return result[0];
            }
            throw e;
        }
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ReferenceExp
    public boolean isDefined() {
        return super.isDefined() && this.element != null;
    }
}
