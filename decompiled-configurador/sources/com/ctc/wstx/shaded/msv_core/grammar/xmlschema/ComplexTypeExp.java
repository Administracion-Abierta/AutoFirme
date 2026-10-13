package com.ctc.wstx.shaded.msv_core.grammar.xmlschema;

import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype;
import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceExp;
import com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.XSDatatypeExp;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/grammar/xmlschema/ComplexTypeExp.class */
public class ComplexTypeExp extends XMLSchemaTypeExp {
    public final ReferenceExp body;
    public final ReferenceExp attWildcard;
    public final XMLSchemaSchema parent;
    public AttributeWildcard wildcard;
    public ComplexTypeExp complexBaseType;
    public XSDatatypeExp simpleBaseType;
    public int derivationMethod;
    public int finalValue;
    public int block;
    private static final long serialVersionUID = 1;

    public ComplexTypeExp(XMLSchemaSchema schema, String localName) {
        super(localName);
        this.body = new ReferenceExp(null);
        this.attWildcard = new ReferenceExp(null, Expression.epsilon);
        this.derivationMethod = -1;
        this.finalValue = 0;
        this.block = 0;
        this.parent = schema;
        setAbstract(false);
    }

    public AttributeWildcard getAttributeWildcard() {
        return this.wildcard;
    }

    public void setAttributeWildcard(AttributeWildcard local) {
        this.wildcard = local;
    }

    public final String getTargetNamespace() {
        return this.parent.targetNamespace;
    }

    public boolean isAbstract() {
        return this.exp == Expression.nullSet;
    }

    public void setAbstract(boolean isAbstract) {
        if (!isAbstract) {
            this.exp = this.parent.pool.createSequence(this.body, this.attWildcard);
        } else {
            this.exp = Expression.nullSet;
        }
    }

    public boolean isDerivedTypeOf(ComplexTypeExp baseType, int constraint) {
        ComplexTypeExp complexTypeExp = this;
        while (true) {
            ComplexTypeExp derived = complexTypeExp;
            if (derived != null) {
                if (derived == baseType) {
                    return true;
                }
                if ((derived.derivationMethod & constraint) != 0) {
                    return false;
                }
                complexTypeExp = derived.complexBaseType;
            } else {
                return false;
            }
        }
    }

    public boolean isDerivedTypeOf(XSDatatype baseType, int constraint) {
        ComplexTypeExp complexTypeExp = this;
        while (true) {
            ComplexTypeExp derived = complexTypeExp;
            if (derived.complexBaseType == null) {
                if (derived.simpleBaseType != null) {
                    return derived.simpleBaseType.getCreatedType().isDerivedTypeOf(baseType, (constraint & 1) == 0);
                }
                return false;
            }
            if ((derived.derivationMethod & constraint) != 0) {
                return false;
            }
            complexTypeExp = derived.complexBaseType;
        }
    }

    public boolean isDerivedTypeOf(XMLSchemaTypeExp exp, int constraint) {
        if (exp instanceof ComplexTypeExp) {
            return isDerivedTypeOf((ComplexTypeExp) exp, constraint);
        }
        return isDerivedTypeOf(((SimpleTypeExp) exp).getDatatype(), constraint);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.xmlschema.XMLSchemaTypeExp
    public int getBlock() {
        return this.block;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.xmlschema.RedefinableExp
    public RedefinableExp getClone() {
        ComplexTypeExp exp = new ComplexTypeExp(this.parent, this.name);
        exp.redefine(this);
        return exp;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.xmlschema.RedefinableExp
    public void redefine(RedefinableExp _rhs) {
        super.redefine(_rhs);
        ComplexTypeExp rhs = (ComplexTypeExp) _rhs;
        this.body.exp = rhs.body.exp;
        this.attWildcard.exp = rhs.attWildcard.exp;
        this.complexBaseType = rhs.complexBaseType;
        this.simpleBaseType = rhs.simpleBaseType;
        this.derivationMethod = rhs.derivationMethod;
        this.finalValue = rhs.finalValue;
        this.block = rhs.block;
        if (rhs.wildcard == null) {
            this.wildcard = null;
        } else {
            this.wildcard = rhs.wildcard.copy();
        }
        if (this.parent != rhs.parent) {
            throw new IllegalArgumentException();
        }
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ReferenceExp
    public boolean isDefined() {
        return this.body.isDefined();
    }
}
