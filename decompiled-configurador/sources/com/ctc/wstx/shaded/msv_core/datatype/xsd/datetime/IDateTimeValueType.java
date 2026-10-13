package com.ctc.wstx.shaded.msv_core.datatype.xsd.datetime;

import java.io.Serializable;
import java.util.Calendar;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/datatype/xsd/datetime/IDateTimeValueType.class */
public interface IDateTimeValueType extends Serializable {
    BigDateTimeValueType getBigValue();

    IDateTimeValueType add(ITimeDurationValueType iTimeDurationValueType);

    IDateTimeValueType normalize();

    int compare(IDateTimeValueType iDateTimeValueType);

    Calendar toCalendar();
}
