package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;
import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;
import com.ctc.wstx.shaded.msv_core.datatype.SerializationContext;
import java.util.StringTokenizer;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/datatype/xsd/ListType.class */
public final class ListType extends ConcreteType implements Discrete {
    public final XSDatatypeImpl itemType;
    private static final long serialVersionUID = 1;

    public ListType(String nsUri, String newTypeName, XSDatatypeImpl itemType) throws DatatypeException {
        super(nsUri, newTypeName);
        if (itemType.isFinal(2)) {
            throw new DatatypeException(localize(XSDatatypeImpl.ERR_INVALID_ITEMTYPE));
        }
        this.itemType = itemType;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype
    public final String displayName() {
        String name = getName();
        return name != null ? name : this.itemType.displayName() + "-list";
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype
    public final int getVariety() {
        return 2;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype
    public final XSDatatype getBaseType() {
        return SimpleURType.theInstance;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl, com.ctc.wstx.shaded.msv.relaxng_datatype.Datatype
    public boolean isContextDependent() {
        return this.itemType.isContextDependent();
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl, com.ctc.wstx.shaded.msv.relaxng_datatype.Datatype
    public int getIdType() {
        switch (this.itemType.getIdType()) {
            case 0:
                return 0;
            case 1:
                return 0;
            case 2:
                return 3;
            case 3:
                return 3;
            default:
                throw new Error();
        }
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.ConcreteType, com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype
    public final boolean isFinal(int derivationType) {
        if (derivationType == 2) {
            return true;
        }
        return this.itemType.isFinal(derivationType);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype
    public final int isFacetApplicable(String facetName) {
        if (facetName.equals(XSDatatype.FACET_LENGTH) || facetName.equals(XSDatatype.FACET_MINLENGTH) || facetName.equals(XSDatatype.FACET_MAXLENGTH) || facetName.equals(XSDatatype.FACET_ENUMERATION) || facetName.equals(XSDatatype.FACET_PATTERN)) {
            return 0;
        }
        return -2;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl
    protected final boolean checkFormat(String content, ValidationContext context) {
        StringTokenizer tokens = new StringTokenizer(content);
        while (tokens.hasMoreTokens()) {
            if (!this.itemType.isValid(tokens.nextToken(), context)) {
                return false;
            }
        }
        return true;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl
    public Object _createValue(String content, ValidationContext context) {
        StringTokenizer tokens = new StringTokenizer(content);
        Object[] values = new Object[tokens.countTokens()];
        int i = 0;
        while (tokens.hasMoreTokens()) {
            int i2 = i;
            i++;
            Object obj_createValue = this.itemType._createValue(tokens.nextToken(), context);
            values[i2] = obj_createValue;
            if (obj_createValue == null) {
                return null;
            }
        }
        return new ListValueType(values);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.DatabindableDatatype
    public Class getJavaObjectType() {
        return Object[].class;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.Discrete
    public final int countLength(Object value) {
        return ((ListValueType) value).values.length;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype
    public String convertToLexicalValue(Object value, SerializationContext context) {
        if (!(value instanceof ListValueType)) {
            throw new IllegalArgumentException();
        }
        ListValueType lv = (ListValueType) value;
        StringBuffer r = new StringBuffer();
        for (int i = 0; i < lv.values.length; i++) {
            if (i != 0) {
                r.append(' ');
            }
            r.append(this.itemType.convertToLexicalValue(lv.values[i], context));
        }
        return r.toString();
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.ConcreteType, com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl
    protected void _checkValid(String content, ValidationContext context) throws DatatypeException {
        StringTokenizer tokens = new StringTokenizer(content);
        while (tokens.hasMoreTokens()) {
            String token = tokens.nextToken();
            this.itemType.checkValid(token, context);
        }
    }
}
