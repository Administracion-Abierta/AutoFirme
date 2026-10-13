package com.ctc.wstx.shaded.msv_core.datatype.xsd;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/datatype/xsd/GYearMonthType.class */
public class GYearMonthType extends DateTimeBaseType {
    public static final GYearMonthType theInstance = new GYearMonthType();
    private static final long serialVersionUID = 1;

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.DateTimeBaseType, com.ctc.wstx.shaded.msv_core.datatype.xsd.Comparator
    public /* bridge */ /* synthetic */ int compare(Object x0, Object x1) {
        return super.compare(x0, x1);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.DateTimeBaseType, com.ctc.wstx.shaded.msv_core.datatype.DatabindableDatatype
    public /* bridge */ /* synthetic */ Class getJavaObjectType() {
        return super.getJavaObjectType();
    }

    private GYearMonthType() {
        super("gYearMonth");
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.DateTimeBaseType
    protected final String getFormat() {
        return "%Y-%M%z";
    }
}
