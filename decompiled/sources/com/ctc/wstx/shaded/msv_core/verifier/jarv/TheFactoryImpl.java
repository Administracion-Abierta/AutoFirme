package com.ctc.wstx.shaded.msv_core.verifier.jarv;

import com.ctc.wstx.shaded.msv.org_isorelax.verifier.VerifierConfigurationException;
import com.ctc.wstx.shaded.msv_core.grammar.Grammar;
import com.ctc.wstx.shaded.msv_core.reader.GrammarReaderController;
import com.ctc.wstx.shaded.msv_core.reader.util.GrammarLoader;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/verifier/jarv/TheFactoryImpl.class */
public class TheFactoryImpl extends FactoryImpl {
    public TheFactoryImpl(SAXParserFactory factory) {
        super(factory);
    }

    public TheFactoryImpl() {
    }

    @Override // com.ctc.wstx.shaded.msv_core.verifier.jarv.FactoryImpl
    protected Grammar parse(InputSource is, GrammarReaderController controller) throws VerifierConfigurationException {
        try {
            return GrammarLoader.loadSchema(is, controller, this.factory);
        } catch (Exception e) {
            throw new VerifierConfigurationException(e);
        }
    }

    protected Grammar parse(String source, GrammarReaderController controller) throws VerifierConfigurationException {
        try {
            return GrammarLoader.loadSchema(source, controller, this.factory);
        } catch (Exception e) {
            throw new VerifierConfigurationException(e);
        }
    }
}
