package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;
import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;
import com.ctc.wstx.shaded.msv_core.datatype.SerializationContext;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/datatype/xsd/ConcreteType.class */
public abstract class ConcreteType extends XSDatatypeImpl {
    private static final long serialVersionUID = 1;

    protected ConcreteType(String nsUri, String typeName, WhiteSpaceProcessor whiteSpace) {
        super(nsUri, typeName, whiteSpace);
    }

    protected ConcreteType(String nsUri, String typeName) {
        this(nsUri, typeName, WhiteSpaceProcessor.theCollapse);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl
    public final ConcreteType getConcreteType() {
        return this;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype
    public boolean isFinal(int derivationType) {
        return false;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl
    protected void _checkValid(String content, ValidationContext context) throws DatatypeException {
        if (!checkFormat(content, context)) {
            throw new DatatypeException(-1, localize(XSDatatypeImpl.ERR_INAPPROPRIATE_FOR_TYPE, content, getName()));
        }
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl
    public Object _createJavaObject(String literal, ValidationContext context) {
        return _createValue(literal, context);
    }

    public String serializeJavaObject(Object value, SerializationContext context) {
        String literal = convertToLexicalValue(value, context);
        if (!isValid(literal, serializedValueChecker)) {
            return null;
        }
        return literal;
    }
}
