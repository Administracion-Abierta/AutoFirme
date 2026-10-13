package com.ctc.wstx.shaded.msv_core.verifier.jarv;

import com.ctc.wstx.shaded.msv.org_isorelax.verifier.Schema;
import com.ctc.wstx.shaded.msv.org_isorelax.verifier.Verifier;
import com.ctc.wstx.shaded.msv.org_isorelax.verifier.VerifierConfigurationException;
import com.ctc.wstx.shaded.msv_core.grammar.Grammar;
import com.ctc.wstx.shaded.msv_core.verifier.IVerifier;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/verifier/jarv/SchemaImpl.class */
public class SchemaImpl implements Schema {
    protected final Grammar grammar;
    protected final SAXParserFactory factory;
    private boolean usePanicMode;

    protected SchemaImpl(Grammar grammar, SAXParserFactory factory, boolean _usePanicMode) {
        this.grammar = grammar;
        this.factory = factory;
        this.usePanicMode = _usePanicMode;
    }

    public SchemaImpl(Grammar grammar) {
        this.grammar = grammar;
        this.factory = SAXParserFactory.newInstance();
        this.factory.setNamespaceAware(true);
        this.usePanicMode = false;
    }

    @Override // com.ctc.wstx.shaded.msv.org_isorelax.verifier.Schema
    public Verifier newVerifier() throws VerifierConfigurationException {
        IVerifier core = FactoryImpl.createVerifier(this.grammar);
        core.setPanicMode(this.usePanicMode);
        return new VerifierImpl(core, createXMLReader());
    }

    private synchronized XMLReader createXMLReader() throws VerifierConfigurationException {
        try {
            return this.factory.newSAXParser().getXMLReader();
        } catch (ParserConfigurationException e) {
            throw new VerifierConfigurationException(e);
        } catch (SAXException e2) {
            throw new VerifierConfigurationException(e2);
        }
    }
}
