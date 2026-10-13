package com.ctc.wstx.shaded.msv_core.verifier.jarv;

import com.ctc.wstx.shaded.msv.org_isorelax.verifier.Schema;
import com.ctc.wstx.shaded.msv.org_isorelax.verifier.VerifierConfigurationException;
import com.ctc.wstx.shaded.msv.org_isorelax.verifier.VerifierFactory;
import com.ctc.wstx.shaded.msv_core.grammar.Grammar;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.XMLSchemaGrammar;
import com.ctc.wstx.shaded.msv_core.reader.GrammarReaderController;
import com.ctc.wstx.shaded.msv_core.verifier.IVerifier;
import com.ctc.wstx.shaded.msv_core.verifier.Verifier;
import com.ctc.wstx.shaded.msv_core.verifier.identity.IDConstraintChecker;
import com.ctc.wstx.shaded.msv_core.verifier.regexp.REDocumentDeclaration;
import com.ctc.wstx.shaded.msv_core.verifier.util.ErrorHandlerImpl;
import java.io.IOException;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/verifier/jarv/FactoryImpl.class */
public abstract class FactoryImpl extends VerifierFactory {
    protected final SAXParserFactory factory;
    private boolean usePanicMode;
    private EntityResolver resolver;

    protected abstract Grammar parse(InputSource inputSource, GrammarReaderController grammarReaderController) throws VerifierConfigurationException, SAXException;

    protected FactoryImpl(SAXParserFactory factory) {
        this.usePanicMode = true;
        this.factory = factory;
    }

    protected FactoryImpl() {
        this.usePanicMode = true;
        this.factory = SAXParserFactory.newInstance();
        this.factory.setNamespaceAware(true);
    }

    @Override // com.ctc.wstx.shaded.msv.org_isorelax.verifier.VerifierFactory
    public void setFeature(String feature, boolean v) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (feature.equals(Const.PANIC_MODE_FEATURE)) {
            this.usePanicMode = v;
        } else {
            super.setFeature(feature, v);
        }
    }

    @Override // com.ctc.wstx.shaded.msv.org_isorelax.verifier.VerifierFactory
    public boolean isFeature(String feature) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (feature.equals(Const.PANIC_MODE_FEATURE)) {
            return this.usePanicMode;
        }
        return super.isFeature(feature);
    }

    @Override // com.ctc.wstx.shaded.msv.org_isorelax.verifier.VerifierFactory
    public void setEntityResolver(EntityResolver _resolver) {
        this.resolver = _resolver;
    }

    @Override // com.ctc.wstx.shaded.msv.org_isorelax.verifier.VerifierFactory
    public EntityResolver getEntityResolver() {
        return this.resolver;
    }

    @Override // com.ctc.wstx.shaded.msv.org_isorelax.verifier.VerifierFactory
    public Schema compileSchema(InputSource source) throws VerifierConfigurationException, SAXException {
        try {
            Grammar g = parse(source, new ThrowController());
            if (g == null) {
                throw new VerifierConfigurationException("unable to parse the schema");
            }
            return new SchemaImpl(g, this.factory, this.usePanicMode);
        } catch (WrapperException we) {
            throw we.e;
        } catch (Exception pce) {
            throw new VerifierConfigurationException(pce);
        }
    }

    static IVerifier createVerifier(Grammar g) {
        if (g instanceof XMLSchemaGrammar) {
            return new IDConstraintChecker((XMLSchemaGrammar) g, new ErrorHandlerImpl());
        }
        return new Verifier(new REDocumentDeclaration(g), new ErrorHandlerImpl());
    }

    /* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/verifier/jarv/FactoryImpl$WrapperException.class */
    private static class WrapperException extends RuntimeException {
        public final SAXException e;

        WrapperException(SAXException e) {
            super(e.getMessage());
            this.e = e;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/verifier/jarv/FactoryImpl$ThrowController.class */
    private class ThrowController implements GrammarReaderController {
        private ThrowController() {
        }

        @Override // com.ctc.wstx.shaded.msv_core.reader.GrammarReaderController
        public void warning(Locator[] locs, String errorMessage) {
        }

        @Override // com.ctc.wstx.shaded.msv_core.reader.GrammarReaderController
        public void error(Locator[] locs, String errorMessage, Exception nestedException) {
            for (int i = 0; i < locs.length; i++) {
                if (locs[i] != null) {
                    throw new WrapperException(new SAXParseException(errorMessage, locs[i], nestedException));
                }
            }
            throw new WrapperException(new SAXException(errorMessage, nestedException));
        }

        @Override // org.xml.sax.EntityResolver
        public InputSource resolveEntity(String p, String s) throws SAXException, IOException {
            if (FactoryImpl.this.resolver == null) {
                return null;
            }
            return FactoryImpl.this.resolver.resolveEntity(p, s);
        }
    }
}
