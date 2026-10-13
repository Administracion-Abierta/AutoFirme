package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv_core.datatype.DatabindableDatatype;
import com.ctc.wstx.shaded.msv_core.datatype.SerializationContext;
import java.io.Serializable;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/datatype/xsd/XSDatatype.class */
public interface XSDatatype extends Serializable, DatabindableDatatype {
    public static final int VARIETY_ATOMIC = 1;
    public static final int VARIETY_LIST = 2;
    public static final int VARIETY_UNION = 3;
    public static final int DERIVATION_BY_RESTRICTION = 1;
    public static final int DERIVATION_BY_LIST = 2;
    public static final int DERIVATION_BY_UNION = 4;
    public static final int APPLICABLE = 0;
    public static final int FIXED = -1;
    public static final int NOT_ALLOWED = -2;
    public static final String FACET_LENGTH = "length";
    public static final String FACET_MINLENGTH = "minLength";
    public static final String FACET_MAXLENGTH = "maxLength";
    public static final String FACET_PATTERN = "pattern";
    public static final String FACET_ENUMERATION = "enumeration";
    public static final String FACET_TOTALDIGITS = "totalDigits";
    public static final String FACET_FRACTIONDIGITS = "fractionDigits";
    public static final String FACET_MININCLUSIVE = "minInclusive";
    public static final String FACET_MAXINCLUSIVE = "maxInclusive";
    public static final String FACET_MINEXCLUSIVE = "minExclusive";
    public static final String FACET_MAXEXCLUSIVE = "maxExclusive";
    public static final String FACET_WHITESPACE = "whiteSpace";
    public static final String XMLSCHEMA_NSURI = "http://www.w3.org/2001/XMLSchema";

    String getName();

    String getNamespaceUri();

    String displayName();

    String convertToLexicalValue(Object obj, SerializationContext serializationContext) throws IllegalArgumentException;

    int getVariety();

    boolean isFinal(int i);

    int isFacetApplicable(String str);

    String[] getApplicableFacetNames();

    DataTypeWithFacet getFacetObject(String str);

    XSDatatype getBaseType();

    XSDatatype getAncestorBuiltinType();

    boolean isDerivedTypeOf(XSDatatype xSDatatype, boolean z);

    boolean isAlwaysValid();
}
