package com.ctc.wstx.shaded.msv_core.reader.xmlschema;

import com.ctc.wstx.shaded.msv_core.reader.GrammarReaderController2;
import java.util.Map;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/reader/xmlschema/WSDLGrammarReaderController.class */
public class WSDLGrammarReaderController implements GrammarReaderController2, LSResourceResolver {
    private GrammarReaderController2 nextController;
    private Map<String, EmbeddedSchema> schemas;
    private String baseURI;

    public WSDLGrammarReaderController(GrammarReaderController2 nextController, String baseURI, Map<String, EmbeddedSchema> sources) {
        this.nextController = nextController;
        this.baseURI = baseURI;
        this.schemas = sources;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.GrammarReaderController
    public void error(Locator[] locs, String msg, Exception nestedException) {
        if (this.nextController != null) {
            this.nextController.error(locs, msg, nestedException);
        }
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.GrammarReaderController
    public void warning(Locator[] locs, String errorMessage) {
        if (this.nextController != null) {
            this.nextController.warning(locs, errorMessage);
        }
    }

    @Override // org.xml.sax.EntityResolver
    public InputSource resolveEntity(String publicId, String systemId) {
        return null;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.GrammarReaderController2
    public LSResourceResolver getLSResourceResolver() {
        return this;
    }

    @Override // org.w3c.dom.ls.LSResourceResolver
    public LSInput resolveResource(String type, String namespaceURI, String publicId, String systemId, String baseURI) {
        EmbeddedSchema schema = this.schemas.get(namespaceURI);
        if (schema != null) {
            return new DOMLSInputImpl(this.baseURI, schema.getSystemId(), schema.getSchemaElement());
        }
        return null;
    }
}
