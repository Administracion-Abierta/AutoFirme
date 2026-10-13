package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;
import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeStreamingValidator;
import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;
import com.ctc.wstx.shaded.msv.relaxng_datatype.helpers.StreamingValidatorImpl;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.Vector;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/datatype/xsd/XSDatatypeImpl.class */
public abstract class XSDatatypeImpl implements XSDatatype {
    private final String namespaceUri;
    private final String typeName;
    public final WhiteSpaceProcessor whiteSpace;
    protected static final ValidationContext serializedValueChecker = new ValidationContext() { // from class: com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl.1
        @Override // com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext
        public boolean isNotation(String s) {
            return true;
        }

        @Override // com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext
        public boolean isUnparsedEntity(String s) {
            return true;
        }

        @Override // com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext
        public String resolveNamespacePrefix(String ns) {
            return "abc";
        }

        @Override // com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext
        public String getBaseUri() {
            return null;
        }
    };
    public static final String ERR_INAPPROPRIATE_FOR_TYPE = "DataTypeErrorDiagnosis.InappropriateForType";
    public static final String ERR_TOO_MUCH_PRECISION = "DataTypeErrorDiagnosis.TooMuchPrecision";
    public static final String ERR_TOO_MUCH_SCALE = "DataTypeErrorDiagnosis.TooMuchScale";
    public static final String ERR_ENUMERATION = "DataTypeErrorDiagnosis.Enumeration";
    public static final String ERR_ENUMERATION_WITH_ARG = "DataTypeErrorDiagnosis.Enumeration.Arg";
    public static final String ERR_OUT_OF_RANGE = "DataTypeErrorDiagnosis.OutOfRange";
    public static final String ERR_LENGTH = "DataTypeErrorDiagnosis.Length";
    public static final String ERR_MINLENGTH = "DataTypeErrorDiagnosis.MinLength";
    public static final String ERR_MAXLENGTH = "DataTypeErrorDiagnosis.MaxLength";
    public static final String ERR_PATTERN_1 = "DataTypeErrorDiagnosis.Pattern.1";
    public static final String ERR_PATTERN_MANY = "DataTypeErrorDiagnosis.Pattern.Many";
    public static final String ERR_INVALID_ITEMTYPE = "BadTypeException.InvalidItemType";
    public static final String ERR_INVALID_MEMBER_TYPE = "BadTypeException.InvalidMemberType";
    public static final String ERR_INVALID_BASE_TYPE = "BadTypeException.InvalidBaseType";
    public static final String ERR_INVALID_WHITESPACE_VALUE = "WhiteSpaceProcessor.InvalidWhiteSpaceValue";
    public static final String ERR_PARSE_ERROR = "PatternFacet.ParseError";
    public static final String ERR_INVALID_VALUE_FOR_THIS_TYPE = "EnumerationFacet.InvalidValueForThisType";
    public static final String ERR_FACET_MUST_BE_NON_NEGATIVE_INTEGER = "BadTypeException.FacetMustBeNonNegativeInteger";
    public static final String ERR_FACET_MUST_BE_POSITIVE_INTEGER = "BadTypeException.FacetMustBePositiveInteger";
    public static final String ERR_OVERRIDING_FIXED_FACET = "BadTypeException.OverridingFixedFacet";
    public static final String ERR_INCONSISTENT_FACETS_1 = "InconsistentFacets.1";
    public static final String ERR_INCONSISTENT_FACETS_2 = "InconsistentFacets.2";
    public static final String ERR_X_AND_Y_ARE_EXCLUSIVE = "XAndYAreExclusive";
    public static final String ERR_LOOSENED_FACET = "LoosenedFacet";
    public static final String ERR_SCALE_IS_GREATER_THAN_PRECISION = "PrecisionScaleFacet.ScaleIsGraterThanPrecision";
    public static final String ERR_DUPLICATE_FACET = "BadTypeException.DuplicateFacet";
    public static final String ERR_NOT_APPLICABLE_FACET = "BadTypeException.NotApplicableFacet";
    public static final String ERR_EMPTY_UNION = "BadTypeException.EmptyUnion";
    private static final long serialVersionUID = 1;

    protected abstract Object _createValue(String str, ValidationContext validationContext);

    protected abstract void _checkValid(String str, ValidationContext validationContext) throws DatatypeException;

    protected abstract Object _createJavaObject(String str, ValidationContext validationContext);

    protected abstract boolean checkFormat(String str, ValidationContext validationContext);

    public abstract ConcreteType getConcreteType();

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype
    public String getNamespaceUri() {
        return this.namespaceUri;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype
    public String getName() {
        return this.typeName;
    }

    protected XSDatatypeImpl(String uri, String typeName, WhiteSpaceProcessor whiteSpace) {
        this.namespaceUri = uri;
        this.typeName = typeName;
        this.whiteSpace = whiteSpace;
    }

    @Override // com.ctc.wstx.shaded.msv.relaxng_datatype.Datatype
    public final Object createValue(String lexicalValue, ValidationContext context) {
        return _createValue(this.whiteSpace.process(lexicalValue), context);
    }

    @Override // com.ctc.wstx.shaded.msv.relaxng_datatype.Datatype
    public final void checkValid(String content, ValidationContext context) throws DatatypeException {
        _checkValid(this.whiteSpace.process(content), context);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.DatabindableDatatype
    public final Object createJavaObject(String literal, ValidationContext context) {
        return _createJavaObject(this.whiteSpace.process(literal), context);
    }

    @Override // com.ctc.wstx.shaded.msv.relaxng_datatype.Datatype
    public final boolean isValid(String literal, ValidationContext context) {
        String literal2 = this.whiteSpace.process(literal);
        if (needValueCheck()) {
            return _createValue(literal2, context) != null;
        }
        return checkFormat(literal2, context);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype
    public boolean isAlwaysValid() {
        return false;
    }

    @Override // com.ctc.wstx.shaded.msv.relaxng_datatype.Datatype
    public DatatypeStreamingValidator createStreamingValidator(ValidationContext context) {
        return new StreamingValidatorImpl(this, context);
    }

    protected boolean needValueCheck() {
        return false;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype
    public DataTypeWithFacet getFacetObject(String facetName) {
        XSDatatype dt = getBaseType();
        if (dt != null) {
            return dt.getFacetObject(facetName);
        }
        return null;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype
    public String[] getApplicableFacetNames() {
        Vector vec = new Vector();
        String[] facetNames = {XSDatatype.FACET_ENUMERATION, XSDatatype.FACET_FRACTIONDIGITS, XSDatatype.FACET_TOTALDIGITS, XSDatatype.FACET_LENGTH, XSDatatype.FACET_MINLENGTH, XSDatatype.FACET_MAXLENGTH, XSDatatype.FACET_MAXEXCLUSIVE, XSDatatype.FACET_MINEXCLUSIVE, XSDatatype.FACET_MAXINCLUSIVE, XSDatatype.FACET_MININCLUSIVE, XSDatatype.FACET_PATTERN, XSDatatype.FACET_WHITESPACE};
        for (int i = 0; i < facetNames.length; i++) {
            if (isFacetApplicable(facetNames[i]) == 0) {
                vec.add(facetNames[i]);
            }
        }
        return (String[]) vec.toArray(new String[vec.size()]);
    }

    @Override // com.ctc.wstx.shaded.msv.relaxng_datatype.Datatype
    public final boolean sameValue(Object o1, Object o2) {
        if (o1 == null || o2 == null) {
            return false;
        }
        return o1.equals(o2);
    }

    @Override // com.ctc.wstx.shaded.msv.relaxng_datatype.Datatype
    public final int valueHashCode(Object o) {
        return o.hashCode();
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype
    public final boolean isDerivedTypeOf(XSDatatype baseType, boolean restrictionAllowed) {
        return isDerivedTypeOf(baseType, this, restrictionAllowed);
    }

    public static boolean isDerivedTypeOf(XSDatatype base, XSDatatype derived, boolean restrictionAllowed) {
        XSDatatype t;
        if (base == derived) {
            return true;
        }
        if (!restrictionAllowed) {
            return false;
        }
        if (base == SimpleURType.theInstance) {
            return true;
        }
        if (base.getVariety() == 3) {
            XSDatatype baseType = base;
            while (true) {
                t = baseType;
                if (t instanceof UnionType) {
                    break;
                }
                baseType = t.getBaseType();
            }
            XSDatatypeImpl[] memberTypes = ((UnionType) t).memberTypes;
            for (XSDatatypeImpl xSDatatypeImpl : memberTypes) {
                if (isDerivedTypeOf(xSDatatypeImpl, derived, restrictionAllowed)) {
                    return true;
                }
            }
        }
        while (derived != SimpleURType.theInstance) {
            if (base == derived) {
                return true;
            }
            derived = derived.getBaseType();
        }
        return false;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype
    public XSDatatype getAncestorBuiltinType() {
        XSDatatype baseType = this;
        while (true) {
            XSDatatype dt = baseType;
            if (!"http://www.w3.org/2001/XMLSchema".equals(dt.getNamespaceUri())) {
                baseType = dt.getBaseType();
            } else {
                return dt;
            }
        }
    }

    public int getIdType() {
        return 0;
    }

    public boolean isContextDependent() {
        return false;
    }

    public static String localize(String prop, Object[] args) {
        return MessageFormat.format(ResourceBundle.getBundle("com.ctc.wstx.shaded.msv_core.datatype.xsd.Messages").getString(prop), args);
    }

    public static String localize(String prop) {
        return localize(prop, (Object[]) null);
    }

    public static String localize(String prop, Object arg1) {
        return localize(prop, new Object[]{arg1});
    }

    public static String localize(String prop, Object arg1, Object arg2) {
        return localize(prop, new Object[]{arg1, arg2});
    }

    public static String localize(String prop, Object arg1, Object arg2, Object arg3) {
        return localize(prop, new Object[]{arg1, arg2, arg3});
    }
}
