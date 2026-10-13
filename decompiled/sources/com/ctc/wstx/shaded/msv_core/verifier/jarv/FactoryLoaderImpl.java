package com.ctc.wstx.shaded.msv_core.verifier.jarv;

import com.ctc.wstx.shaded.msv.org_isorelax.verifier.VerifierFactory;
import com.ctc.wstx.shaded.msv.org_isorelax.verifier.VerifierFactoryLoader;
import com.ctc.wstx.shaded.msv_core.reader.relax.RELAXReader;
import com.ctc.wstx.shaded.msv_core.reader.trex.ng.RELAXNGReader;
import com.ctc.wstx.shaded.msv_core.reader.xmlschema.XMLSchemaReader;
import com.ctc.wstx.shaded.msv_core.relaxns.reader.RELAXNSReader;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/verifier/jarv/FactoryLoaderImpl.class */
public class FactoryLoaderImpl implements VerifierFactoryLoader {
    @Override // com.ctc.wstx.shaded.msv.org_isorelax.verifier.VerifierFactoryLoader
    public VerifierFactory createFactory(String language) {
        if (language.equals(RELAXNGReader.RELAXNGNamespace)) {
            return new RELAXNGFactoryImpl();
        }
        if (language.equals(RELAXReader.RELAXCoreNamespace)) {
            return new RELAXCoreFactoryImpl();
        }
        if (language.equals("http://www.thaiopensource.com/trex")) {
            return new TREXFactoryImpl();
        }
        if (language.equals("http://www.w3.org/2001/XMLSchema") || language.equals(XMLSchemaReader.XMLSchemaNamespace_old)) {
            return new XSFactoryImpl();
        }
        if (language.equals(RELAXNSReader.RELAXNamespaceNamespace)) {
            return new TheFactoryImpl();
        }
        if (language.equals("http://www.w3.org/XML/1998/namespace")) {
            return new DTDFactoryImpl();
        }
        if (language.equals("relax")) {
            return new TheFactoryImpl();
        }
        if (language.toUpperCase().equals("DTD")) {
            return new DTDFactoryImpl();
        }
        return null;
    }
}
