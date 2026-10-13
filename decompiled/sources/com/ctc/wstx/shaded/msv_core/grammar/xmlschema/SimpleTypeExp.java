package com.ctc.wstx.shaded.msv_core.grammar.xmlschema;

import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype;
import com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.XSDatatypeExp;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/grammar/xmlschema/SimpleTypeExp.class */
public class SimpleTypeExp extends XMLSchemaTypeExp {
    protected XSDatatypeExp type;
    private static final long serialVersionUID = 1;

    SimpleTypeExp(String typeName) {
        super(typeName);
    }

    public void set(XSDatatypeExp exp) {
        this.type = exp;
        this.exp = exp;
    }

    public XSDatatypeExp getType() {
        return this.type;
    }

    public XSDatatype getDatatype() {
        return this.type.getCreatedType();
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.xmlschema.XMLSchemaTypeExp
    public int getBlock() {
        return 0;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.xmlschema.RedefinableExp
    public RedefinableExp getClone() {
        SimpleTypeExp exp = new SimpleTypeExp(this.name);
        exp.redefine(this);
        return exp;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.xmlschema.RedefinableExp
    public void redefine(RedefinableExp _rhs) {
        super.redefine(_rhs);
        SimpleTypeExp rhs = (SimpleTypeExp) _rhs;
        if (this.type == null) {
            this.type = rhs.getType().getClone();
        } else {
            if (rhs.getType() == null) {
                throw new InternalError();
            }
            this.type.redefine(rhs.getType());
        }
    }
}
