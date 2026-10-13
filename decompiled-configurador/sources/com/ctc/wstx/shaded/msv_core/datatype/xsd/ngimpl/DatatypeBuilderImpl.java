package com.ctc.wstx.shaded.msv_core.datatype.xsd.ngimpl;

import com.ctc.wstx.shaded.msv.relaxng_datatype.Datatype;
import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeBuilder;
import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;
import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.TypeIncubator;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/datatype/xsd/ngimpl/DatatypeBuilderImpl.class */
class DatatypeBuilderImpl implements DatatypeBuilder {
    private TypeIncubator incubator;

    DatatypeBuilderImpl(XSDatatype baseType) {
        this.incubator = new TypeIncubator(baseType);
    }

    @Override // com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeBuilder
    public void addParameter(String name, String value, ValidationContext context) throws DatatypeException {
        if (name.equals(XSDatatype.FACET_ENUMERATION)) {
            throw new DatatypeException(XSDatatypeImpl.localize(XSDatatypeImpl.ERR_NOT_APPLICABLE_FACET, name));
        }
        this.incubator.addFacet(name, value, false, context);
        if (name.equals(XSDatatype.FACET_PATTERN)) {
            this.incubator = new TypeIncubator(this.incubator.derive(null, null));
        }
    }

    @Override // com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeBuilder
    public Datatype createDatatype() throws DatatypeException {
        return this.incubator.derive(null, null);
    }
}
