package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;
import com.ctc.wstx.shaded.msv_core.datatype.SerializationContext;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/datatype/xsd/QnameType.class */
public class QnameType extends BuiltinAtomicType implements Discrete {
    public static final QnameType theInstance = new QnameType();
    private static final long serialVersionUID = 1;

    private QnameType() {
        super("QName");
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype
    public final XSDatatype getBaseType() {
        return SimpleURType.theInstance;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl, com.ctc.wstx.shaded.msv.relaxng_datatype.Datatype
    public boolean isContextDependent() {
        return true;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl
    protected boolean checkFormat(String value, ValidationContext context) {
        int first = value.indexOf(58);
        if (first <= 0) {
            return XmlNames.isUnqualifiedName(value);
        }
        int last = value.lastIndexOf(58);
        if (last != first) {
            return false;
        }
        String prefix = value.substring(0, first);
        return XmlNames.isUnqualifiedName(prefix) && XmlNames.isUnqualifiedName(value.substring(first + 1)) && context.resolveNamespacePrefix(prefix) != null;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl
    public Object _createValue(String value, ValidationContext context) {
        String localPart;
        String uri;
        int first = value.indexOf(58);
        if (first <= 0) {
            if (!XmlNames.isUnqualifiedName(value)) {
                return null;
            }
            uri = context.resolveNamespacePrefix("");
            localPart = value;
        } else {
            int last = value.lastIndexOf(58);
            if (last != first) {
                return null;
            }
            String prefix = value.substring(0, first);
            localPart = value.substring(first + 1);
            if (!XmlNames.isUnqualifiedName(prefix) || !XmlNames.isUnqualifiedName(localPart)) {
                return null;
            }
            uri = context.resolveNamespacePrefix(prefix);
        }
        if (uri == null) {
            return null;
        }
        return new QnameValueType(uri, localPart);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype
    public final int isFacetApplicable(String facetName) {
        if (facetName.equals(XSDatatype.FACET_PATTERN) || facetName.equals(XSDatatype.FACET_ENUMERATION) || facetName.equals(XSDatatype.FACET_WHITESPACE) || facetName.equals(XSDatatype.FACET_LENGTH) || facetName.equals(XSDatatype.FACET_MAXLENGTH) || facetName.equals(XSDatatype.FACET_MINLENGTH)) {
            return 0;
        }
        return -2;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.Discrete
    public final int countLength(Object value) {
        QnameValueType v = (QnameValueType) value;
        return UnicodeUtil.countLength(v.namespaceURI) + UnicodeUtil.countLength(v.localPart);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype
    public String convertToLexicalValue(Object o, SerializationContext context) {
        if (!(o instanceof QnameValueType)) {
            throw new UnsupportedOperationException();
        }
        QnameValueType v = (QnameValueType) o;
        return serialize(v.namespaceURI, v.localPart, context);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.ConcreteType, com.ctc.wstx.shaded.msv_core.datatype.DatabindableDatatype
    public String serializeJavaObject(Object value, SerializationContext context) {
        if (!(value instanceof String[])) {
            throw new IllegalArgumentException();
        }
        String[] input = (String[]) value;
        if (input.length != 2) {
            throw new IllegalArgumentException();
        }
        return serialize(input[0], input[1], context);
    }

    private String serialize(String uri, String local, SerializationContext context) {
        String prefix = context.getNamespacePrefix(uri);
        return prefix == null ? local : prefix + ":" + local;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.ConcreteType, com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl
    public Object _createJavaObject(String literal, ValidationContext context) {
        QnameValueType v = (QnameValueType) createValue(literal, context);
        if (v == null) {
            return null;
        }
        return new String[]{v.namespaceURI, v.localPart};
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.DatabindableDatatype
    public Class getJavaObjectType() {
        return String[].class;
    }
}
