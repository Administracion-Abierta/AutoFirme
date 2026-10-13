package com.ctc.wstx.shaded.msv_core.verifier.jarv;

import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeLibraryFactory;
import com.ctc.wstx.shaded.msv_core.grammar.Grammar;
import com.ctc.wstx.shaded.msv_core.reader.GrammarReaderController;
import com.ctc.wstx.shaded.msv_core.reader.trex.ng.RELAXNGReader;
import org.xml.sax.InputSource;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/verifier/jarv/RELAXNGFactoryImpl.class */
public class RELAXNGFactoryImpl extends FactoryImpl {
    private DatatypeLibraryFactory datatypeLibraryFactory = null;
    private static final String PROP_NAME = "datatypeLibraryFactory";

    @Override // com.ctc.wstx.shaded.msv_core.verifier.jarv.FactoryImpl
    protected Grammar parse(InputSource is, GrammarReaderController controller) {
        RELAXNGReader reader = new RELAXNGReader(controller, this.factory);
        if (this.datatypeLibraryFactory != null) {
            reader.setDatatypeLibraryFactory(this.datatypeLibraryFactory);
        }
        reader.parse(is);
        return reader.getResult();
    }

    @Override // com.ctc.wstx.shaded.msv.org_isorelax.verifier.VerifierFactory
    public Object getProperty(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (name.equals(PROP_NAME)) {
            return this.datatypeLibraryFactory;
        }
        return super.getProperty(name);
    }

    @Override // com.ctc.wstx.shaded.msv.org_isorelax.verifier.VerifierFactory
    public void setProperty(String name, Object value) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (name.equals(PROP_NAME)) {
            this.datatypeLibraryFactory = (DatatypeLibraryFactory) value;
        } else {
            super.setProperty(name, value);
        }
    }
}
