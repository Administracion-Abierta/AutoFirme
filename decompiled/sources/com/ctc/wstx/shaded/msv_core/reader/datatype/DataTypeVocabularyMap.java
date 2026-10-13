package com.ctc.wstx.shaded.msv_core.reader.datatype;

import com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.XSDVocabulary;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/reader/datatype/DataTypeVocabularyMap.class */
public class DataTypeVocabularyMap implements Serializable {
    private final Map impl = new HashMap();

    public DataTypeVocabulary get(String namespaceURI) {
        DataTypeVocabulary v = (DataTypeVocabulary) this.impl.get(namespaceURI);
        if (v != null) {
            return v;
        }
        if (namespaceURI.equals(XSDVocabulary.XMLSchemaNamespace)) {
            v = new XSDVocabulary();
            this.impl.put(XSDVocabulary.XMLSchemaNamespace, v);
            this.impl.put("http://www.w3.org/2001/XMLSchema", v);
        }
        return v;
    }

    public void put(String namespaceURI, DataTypeVocabulary voc) {
        this.impl.put(namespaceURI, voc);
    }
}
