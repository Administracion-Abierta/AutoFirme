package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;
import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;
import com.ctc.wstx.shaded.msv_core.datatype.SerializationContext;
import org.apache.xml.security.transforms.params.XPath2FilterContainer;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/datatype/xsd/UnionType.class */
public final class UnionType extends ConcreteType {
    public final XSDatatypeImpl[] memberTypes;
    private static final long serialVersionUID = 1;

    public UnionType(String nsUri, String newTypeName, XSDatatype[] memberTypes) throws DatatypeException {
        super(nsUri, newTypeName);
        if (memberTypes.length == 0) {
            throw new DatatypeException(localize(XSDatatypeImpl.ERR_EMPTY_UNION));
        }
        XSDatatypeImpl[] m = new XSDatatypeImpl[memberTypes.length];
        System.arraycopy(memberTypes, 0, m, 0, memberTypes.length);
        for (int i = 0; i < m.length; i++) {
            if (m[i].isFinal(4)) {
                throw new DatatypeException(localize(XSDatatypeImpl.ERR_INVALID_MEMBER_TYPE, m[i].displayName()));
            }
        }
        this.memberTypes = m;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype
    public final XSDatatype getBaseType() {
        return SimpleURType.theInstance;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype
    public final String displayName() {
        String name = getName();
        return name != null ? name : XPath2FilterContainer.UNION;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl, com.ctc.wstx.shaded.msv.relaxng_datatype.Datatype
    public boolean isContextDependent() {
        for (int i = 0; i < this.memberTypes.length; i++) {
            if (this.memberTypes[i].isContextDependent()) {
                return true;
            }
        }
        return false;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype
    public final int getVariety() {
        return 3;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype
    public final int isFacetApplicable(String facetName) {
        if (facetName.equals(XSDatatype.FACET_PATTERN) || facetName.equals(XSDatatype.FACET_ENUMERATION)) {
            return 0;
        }
        return -2;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl
    protected final boolean checkFormat(String content, ValidationContext context) {
        for (int i = 0; i < this.memberTypes.length; i++) {
            if (this.memberTypes[i].checkFormat(content, context)) {
                return true;
            }
        }
        return false;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl
    public Object _createValue(String content, ValidationContext context) {
        for (int i = 0; i < this.memberTypes.length; i++) {
            Object o = this.memberTypes[i]._createValue(content, context);
            if (o != null) {
                return o;
            }
        }
        return null;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.DatabindableDatatype
    public Class getJavaObjectType() {
        return Object.class;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype
    public String convertToLexicalValue(Object o, SerializationContext context) {
        for (int i = 0; i < this.memberTypes.length; i++) {
            try {
                return this.memberTypes[i].convertToLexicalValue(o, context);
            } catch (Exception e) {
            }
        }
        throw new IllegalArgumentException();
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.ConcreteType, com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl
    protected void _checkValid(String content, ValidationContext context) throws DatatypeException {
        if (!checkFormat(content, context)) {
            throw new DatatypeException();
        }
    }
}
