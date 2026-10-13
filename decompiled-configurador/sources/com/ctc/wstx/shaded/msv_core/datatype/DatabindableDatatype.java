package com.ctc.wstx.shaded.msv_core.datatype;

import com.ctc.wstx.shaded.msv.relaxng_datatype.Datatype;
import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/datatype/DatabindableDatatype.class */
public interface DatabindableDatatype extends Datatype {
    Object createJavaObject(String str, ValidationContext validationContext);

    String serializeJavaObject(Object obj, SerializationContext serializationContext) throws IllegalArgumentException;

    Class getJavaObjectType();
}
