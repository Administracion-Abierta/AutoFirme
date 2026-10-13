package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;
import java.io.ObjectStreamException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/datatype/xsd/BuiltinAtomicType.class */
public abstract class BuiltinAtomicType extends ConcreteType {
    private static final long serialVersionUID = 1;

    protected BuiltinAtomicType(String typeName, WhiteSpaceProcessor whiteSpace) {
        super("http://www.w3.org/2001/XMLSchema", typeName, whiteSpace);
    }

    protected BuiltinAtomicType(String typeName) {
        this(typeName, WhiteSpaceProcessor.theCollapse);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype
    public final int getVariety() {
        return 1;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype
    public final String displayName() {
        return getName();
    }

    protected Object readResolve() throws ObjectStreamException {
        String name = getName();
        if (name != null) {
            try {
                return DatatypeFactory.getTypeByName(name);
            } catch (DatatypeException e) {
            }
        }
        return this;
    }
}
