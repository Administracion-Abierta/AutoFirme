package com.ctc.wstx.shaded.msv_core.reader.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;
import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.TypeIncubator;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype;
import com.ctc.wstx.shaded.msv_core.reader.GrammarReader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/reader/datatype/xsd/LazyTypeIncubator.class */
class LazyTypeIncubator implements XSTypeIncubator {
    private final XSDatatypeExp baseType;
    private final GrammarReader reader;
    private final List facets = new LinkedList();

    public LazyTypeIncubator(XSDatatypeExp base, GrammarReader reader) {
        this.baseType = base;
        this.reader = reader;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.XSTypeIncubator
    public void addFacet(String name, String strValue, boolean fixed, ValidationContext context) {
        this.facets.add(new Facet(name, strValue, fixed, context));
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.XSTypeIncubator
    public XSDatatypeExp derive(final String nsUri, final String localName) throws DatatypeException {
        final int facetSize = this.facets.size();
        return facetSize == 0 ? this.baseType : new XSDatatypeExp(nsUri, localName, this.reader, new XSDatatypeExp.Renderer() { // from class: com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.LazyTypeIncubator.1
            @Override // com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.XSDatatypeExp.Renderer
            public XSDatatype render(XSDatatypeExp.RenderingContext context) throws DatatypeException {
                TypeIncubator ti = new TypeIncubator(LazyTypeIncubator.this.baseType.getType(context));
                Iterator itr = LazyTypeIncubator.this.facets.iterator();
                for (int i = 0; i < facetSize; i++) {
                    Facet f = (Facet) itr.next();
                    ti.addFacet(f.name, f.value, f.fixed, f.context);
                }
                return ti.derive(nsUri, localName);
            }
        });
    }

    /* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/reader/datatype/xsd/LazyTypeIncubator$Facet.class */
    private class Facet {
        String name;
        String value;
        boolean fixed;
        ValidationContext context;

        public Facet(String name, String value, boolean fixed, ValidationContext context) {
            this.name = name;
            this.value = value;
            this.fixed = fixed;
            this.context = context;
        }
    }
}
