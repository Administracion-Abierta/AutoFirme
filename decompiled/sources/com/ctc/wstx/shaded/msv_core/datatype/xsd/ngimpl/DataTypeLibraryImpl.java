package com.ctc.wstx.shaded.msv_core.datatype.xsd.ngimpl;

import com.ctc.wstx.shaded.msv.relaxng_datatype.Datatype;
import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeBuilder;
import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;
import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeLibrary;
import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeLibraryFactory;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.DatatypeFactory;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype;
import com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.XSDVocabulary;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/datatype/xsd/ngimpl/DataTypeLibraryImpl.class */
public class DataTypeLibraryImpl implements DatatypeLibrary, DatatypeLibraryFactory {
    @Override // com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeLibrary
    public Datatype createDatatype(String typeName) throws DatatypeException {
        return getType(typeName);
    }

    private XSDatatype getType(String typeName) throws DatatypeException {
        return DatatypeFactory.getTypeByName(typeName);
    }

    @Override // com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeLibrary
    public DatatypeBuilder createDatatypeBuilder(String typeName) throws DatatypeException {
        return new DatatypeBuilderImpl(getType(typeName));
    }

    @Override // com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeLibraryFactory
    public DatatypeLibrary createDatatypeLibrary(String uri) {
        if (uri.equals("http://www.w3.org/2001/XMLSchema") || uri.equals(XSDVocabulary.XMLSchemaNamespace)) {
            return this;
        }
        return null;
    }
}
