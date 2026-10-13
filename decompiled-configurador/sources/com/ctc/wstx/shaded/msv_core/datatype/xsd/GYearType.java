package com.ctc.wstx.shaded.msv_core.datatype.xsd;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/datatype/xsd/GYearType.class */
public class GYearType extends DateTimeBaseType {
    public static final GYearType theInstance = new GYearType();
    private static final long serialVersionUID = 1;

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.DateTimeBaseType, com.ctc.wstx.shaded.msv_core.datatype.xsd.Comparator
    public /* bridge */ /* synthetic */ int compare(Object x0, Object x1) {
        return super.compare(x0, x1);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.DateTimeBaseType, com.ctc.wstx.shaded.msv_core.datatype.DatabindableDatatype
    public /* bridge */ /* synthetic */ Class getJavaObjectType() {
        return super.getJavaObjectType();
    }

    private GYearType() {
        super("gYear");
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.DateTimeBaseType
    protected final String getFormat() {
        return "%Y%z";
    }
}
