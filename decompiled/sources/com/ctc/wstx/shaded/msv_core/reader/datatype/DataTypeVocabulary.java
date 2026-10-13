package com.ctc.wstx.shaded.msv_core.reader.datatype;

import com.ctc.wstx.shaded.msv.relaxng_datatype.Datatype;
import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/reader/datatype/DataTypeVocabulary.class */
public interface DataTypeVocabulary {
    State createTopLevelReaderState(StartTagInfo startTagInfo);

    Datatype getType(String str) throws DatatypeException;
}
