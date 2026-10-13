package com.ctc.wstx.shaded.msv_core.reader.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.Datatype;
import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.DatatypeFactory;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.reader.datatype.DataTypeVocabulary;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;
import java.io.Serializable;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/reader/datatype/xsd/XSDVocabulary.class */
public class XSDVocabulary implements DataTypeVocabulary, Serializable {
    public static final String XMLSchemaNamespace = "http://www.w3.org/2001/XMLSchema-datatypes";
    public static final String XMLSchemaNamespace2 = "http://www.w3.org/2001/XMLSchema";

    @Override // com.ctc.wstx.shaded.msv_core.reader.datatype.DataTypeVocabulary
    public State createTopLevelReaderState(StartTagInfo tag) {
        if (tag.localName.equals("simpleType")) {
            return new SimpleTypeState();
        }
        return null;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.datatype.DataTypeVocabulary
    public Datatype getType(String localTypeName) throws DatatypeException {
        return DatatypeFactory.getTypeByName(localTypeName);
    }
}
