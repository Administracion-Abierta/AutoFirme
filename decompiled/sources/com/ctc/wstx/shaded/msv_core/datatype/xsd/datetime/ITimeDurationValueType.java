package com.ctc.wstx.shaded.msv_core.datatype.xsd.datetime;

import java.io.Serializable;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/datatype/xsd/datetime/ITimeDurationValueType.class */
public interface ITimeDurationValueType extends Serializable {
    BigTimeDurationValueType getBigValue();

    int compare(ITimeDurationValueType iTimeDurationValueType);
}
