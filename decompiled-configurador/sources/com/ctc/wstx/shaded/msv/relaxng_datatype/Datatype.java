package com.ctc.wstx.shaded.msv.relaxng_datatype;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv/relaxng_datatype/Datatype.class */
public interface Datatype {
    public static final int ID_TYPE_NULL = 0;
    public static final int ID_TYPE_ID = 1;
    public static final int ID_TYPE_IDREF = 2;
    public static final int ID_TYPE_IDREFS = 3;

    boolean isValid(String str, ValidationContext validationContext);

    void checkValid(String str, ValidationContext validationContext) throws DatatypeException;

    DatatypeStreamingValidator createStreamingValidator(ValidationContext validationContext);

    Object createValue(String str, ValidationContext validationContext);

    boolean sameValue(Object obj, Object obj2);

    int valueHashCode(Object obj);

    int getIdType();

    boolean isContextDependent();
}
