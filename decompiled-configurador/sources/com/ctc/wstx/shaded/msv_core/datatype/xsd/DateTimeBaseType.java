package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;
import com.ctc.wstx.shaded.msv_core.datatype.SerializationContext;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.datetime.CalendarFormatter;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.datetime.CalendarParser;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.datetime.IDateTimeValueType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.datetime.PreciseCalendarFormatter;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.datetime.PreciseCalendarParser;
import java.util.Calendar;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/datatype/xsd/DateTimeBaseType.class */
abstract class DateTimeBaseType extends BuiltinAtomicType implements Comparator {
    private static final long serialVersionUID = 1465669066779112677L;

    protected abstract String getFormat();

    protected DateTimeBaseType(String typeName) {
        super(typeName);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype
    public final XSDatatype getBaseType() {
        return SimpleURType.theInstance;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl
    protected final boolean checkFormat(String content, ValidationContext context) {
        try {
            CalendarParser.parse(getFormat(), content);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl
    public final Object _createValue(String content, ValidationContext context) {
        try {
            return PreciseCalendarParser.parse(getFormat(), content);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype
    public final String convertToLexicalValue(Object value, SerializationContext context) {
        if (!(value instanceof IDateTimeValueType)) {
            throw new IllegalArgumentException();
        }
        return PreciseCalendarFormatter.format(getFormat(), (IDateTimeValueType) value);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.ConcreteType, com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl
    public final Object _createJavaObject(String literal, ValidationContext context) {
        return CalendarParser.parse(getFormat(), literal);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.ConcreteType, com.ctc.wstx.shaded.msv_core.datatype.DatabindableDatatype
    public final String serializeJavaObject(Object value, SerializationContext context) {
        if (value instanceof Calendar) {
            return CalendarFormatter.format(getFormat(), (Calendar) value);
        }
        throw new IllegalArgumentException();
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.DatabindableDatatype
    public Class getJavaObjectType() {
        return Calendar.class;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.Comparator
    public int compare(Object lhs, Object rhs) {
        return ((IDateTimeValueType) lhs).compare((IDateTimeValueType) rhs);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype
    public final int isFacetApplicable(String facetName) {
        if (facetName.equals(XSDatatype.FACET_PATTERN) || facetName.equals(XSDatatype.FACET_ENUMERATION) || facetName.equals(XSDatatype.FACET_WHITESPACE) || facetName.equals(XSDatatype.FACET_MAXINCLUSIVE) || facetName.equals(XSDatatype.FACET_MAXEXCLUSIVE) || facetName.equals(XSDatatype.FACET_MININCLUSIVE) || facetName.equals(XSDatatype.FACET_MINEXCLUSIVE)) {
            return 0;
        }
        return -2;
    }
}
