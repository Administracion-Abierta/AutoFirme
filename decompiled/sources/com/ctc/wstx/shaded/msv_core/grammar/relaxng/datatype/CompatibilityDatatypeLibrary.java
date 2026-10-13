package com.ctc.wstx.shaded.msv_core.grammar.relaxng.datatype;

import com.ctc.wstx.shaded.msv.relaxng_datatype.Datatype;
import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeBuilder;
import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;
import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeLibrary;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.DatatypeFactory;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.IDREFType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.IDType;
import com.ctc.wstx.shaded.msv_core.scanner.dtd.DTDParser;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/grammar/relaxng/datatype/CompatibilityDatatypeLibrary.class */
public class CompatibilityDatatypeLibrary implements DatatypeLibrary {
    public static final String namespaceURI = "http://relaxng.org/ns/compatibility/datatypes/1.0";

    @Override // com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeLibrary
    public Datatype createDatatype(String name) throws DatatypeException {
        if (name.equals("ID")) {
            return IDType.theInstance;
        }
        if (name.equals(DTDParser.TYPE_IDREF)) {
            return IDREFType.theInstance;
        }
        if (name.equals(DTDParser.TYPE_IDREFS)) {
            return DatatypeFactory.getTypeByName(DTDParser.TYPE_IDREFS);
        }
        throw new DatatypeException("undefined built-in type:" + name);
    }

    @Override // com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeLibrary
    public DatatypeBuilder createDatatypeBuilder(String name) throws DatatypeException {
        return new DatatypeBuilderImpl(createDatatype(name));
    }
}
