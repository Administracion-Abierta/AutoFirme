package com.ctc.wstx.shaded.msv_core.reader.util;

import com.ctc.wstx.shaded.msv.org_isorelax.verifier.Schema;
import com.ctc.wstx.shaded.msv.org_isorelax.verifier.Verifier;
import com.ctc.wstx.shaded.msv.org_isorelax.verifier.VerifierConfigurationException;
import com.ctc.wstx.shaded.msv.org_isorelax.verifier.VerifierFilter;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionPool;
import com.ctc.wstx.shaded.msv_core.grammar.Grammar;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.XMLSchemaGrammar;
import com.ctc.wstx.shaded.msv_core.reader.Controller;
import com.ctc.wstx.shaded.msv_core.reader.GrammarReader;
import com.ctc.wstx.shaded.msv_core.reader.GrammarReaderController;
import com.ctc.wstx.shaded.msv_core.reader.dtd.DTDReader;
import com.ctc.wstx.shaded.msv_core.reader.relax.core.RELAXCoreReader;
import com.ctc.wstx.shaded.msv_core.reader.trex.classic.TREXGrammarReader;
import com.ctc.wstx.shaded.msv_core.reader.trex.ng.comp.RELAXNGCompReader;
import com.ctc.wstx.shaded.msv_core.reader.xmlschema.XMLSchemaReader;
import com.ctc.wstx.shaded.msv_core.relaxns.reader.RELAXNSReader;
import com.ctc.wstx.shaded.msv_core.util.Util;
import com.ctc.wstx.shaded.msv_core.verifier.jaxp.SAXParserFactoryImpl;
import com.ctc.wstx.shaded.msv_core.verifier.regexp.REDocumentDeclaration;
import com.ctc.wstx.shaded.msv_core.verifier.regexp.xmlschema.XSREDocDecl;
import java.io.IOException;
import java.util.Vector;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/reader/util/GrammarLoader.class */
public class GrammarLoader {
    private SAXParserFactory factory;
    private Controller controller;
    private ExpressionPool pool;
    private boolean strictCheck = false;

    public static REDocumentDeclaration loadVGM(String url, GrammarReaderController controller, SAXParserFactory factory) throws ParserConfigurationException, SAXException, IOException {
        Grammar g = loadSchema(url, controller, factory);
        if (g != null) {
            return wrapByVGM(g);
        }
        return null;
    }

    public static REDocumentDeclaration loadVGM(InputSource source, GrammarReaderController controller, SAXParserFactory factory) throws ParserConfigurationException, SAXException, IOException {
        Grammar g = loadSchema(source, controller, factory);
        if (g != null) {
            return wrapByVGM(g);
        }
        return null;
    }

    private static REDocumentDeclaration wrapByVGM(Grammar g) {
        if (g instanceof XMLSchemaGrammar) {
            return new XSREDocDecl((XMLSchemaGrammar) g);
        }
        return new REDocumentDeclaration(g);
    }

    public static REDocumentDeclaration loadVGM(String url) throws ParserConfigurationException, SAXException, IOException {
        try {
            return loadVGM(url, new ThrowController(), (SAXParserFactory) null);
        } catch (GrammarLoaderException e) {
            throw e.e;
        }
    }

    public static REDocumentDeclaration loadVGM(InputSource source) throws ParserConfigurationException, SAXException, IOException {
        try {
            return loadVGM(source, new ThrowController(), (SAXParserFactory) null);
        } catch (GrammarLoaderException e) {
            throw e.e;
        }
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/reader/util/GrammarLoader$GrammarLoaderException.class */
    private static class GrammarLoaderException extends RuntimeException {
        public final SAXException e;

        GrammarLoaderException(SAXException e) {
            super(e.getMessage());
            this.e = e;
        }
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/reader/util/GrammarLoader$ThrowController.class */
    private static class ThrowController implements GrammarReaderController {
        private ThrowController() {
        }

        @Override // com.ctc.wstx.shaded.msv_core.reader.GrammarReaderController
        public void warning(Locator[] locs, String errorMessage) {
        }

        @Override // com.ctc.wstx.shaded.msv_core.reader.GrammarReaderController
        public void error(Locator[] locs, String errorMessage, Exception nestedException) {
            for (int i = 0; i < locs.length; i++) {
                if (locs[i] != null) {
                    throw new GrammarLoaderException(new SAXParseException(errorMessage, locs[i], nestedException));
                }
            }
            throw new GrammarLoaderException(new SAXException(errorMessage, nestedException));
        }

        @Override // org.xml.sax.EntityResolver
        public InputSource resolveEntity(String p, String s) {
            return null;
        }
    }

    public static Grammar loadSchema(String url, GrammarReaderController controller, SAXParserFactory factory) throws ParserConfigurationException, SAXException, IOException {
        GrammarLoader loader = new GrammarLoader();
        loader.setController(controller);
        loader.setSAXParserFactory(factory);
        return loader.parse(url);
    }

    public static Grammar loadSchema(InputSource source, GrammarReaderController controller, SAXParserFactory factory) throws ParserConfigurationException, SAXException, IOException {
        GrammarLoader loader = new GrammarLoader();
        loader.setController(controller);
        loader.setSAXParserFactory(factory);
        return loader.parse(source);
    }

    public static Grammar loadSchema(String source, GrammarReaderController controller) throws ParserConfigurationException, SAXException, IOException {
        GrammarLoader loader = new GrammarLoader();
        loader.setController(controller);
        return loader.parse(source);
    }

    public static Grammar loadSchema(InputSource source, GrammarReaderController controller) throws ParserConfigurationException, SAXException, IOException {
        GrammarLoader loader = new GrammarLoader();
        loader.setController(controller);
        return loader.parse(source);
    }

    public static Grammar loadSchema(String url) throws ParserConfigurationException, SAXException, IOException {
        try {
            return loadSchema(url, new ThrowController(), (SAXParserFactory) null);
        } catch (GrammarLoaderException e) {
            throw e.e;
        }
    }

    public static Grammar loadSchema(InputSource source) throws ParserConfigurationException, SAXException, IOException {
        try {
            return loadSchema(source, new ThrowController(), (SAXParserFactory) null);
        } catch (GrammarLoaderException e) {
            throw e.e;
        }
    }

    public void setSAXParserFactory(SAXParserFactory factory) {
        this.factory = factory;
    }

    public SAXParserFactory getSAXParserFactory() {
        if (this.factory == null) {
            this.factory = SAXParserFactory.newInstance();
            this.factory.setNamespaceAware(true);
        }
        return this.factory;
    }

    public void setController(GrammarReaderController controller) {
        this.controller = new Controller(controller);
    }

    public Controller getController() {
        if (this.controller == null) {
            this.controller = new Controller(new GrammarReaderController() { // from class: com.ctc.wstx.shaded.msv_core.reader.util.GrammarLoader.1
                @Override // com.ctc.wstx.shaded.msv_core.reader.GrammarReaderController
                public void warning(Locator[] locs, String errorMessage) {
                }

                @Override // com.ctc.wstx.shaded.msv_core.reader.GrammarReaderController
                public void error(Locator[] locs, String errorMessage, Exception nestedException) {
                }

                @Override // org.xml.sax.EntityResolver
                public InputSource resolveEntity(String s, String p) {
                    return null;
                }
            });
        }
        return this.controller;
    }

    public void setPool(ExpressionPool pool) {
        this.pool = pool;
    }

    public ExpressionPool getPool() {
        return this.pool == null ? new ExpressionPool() : this.pool;
    }

    public void setStrictCheck(boolean value) {
        this.strictCheck = value;
    }

    public boolean getStrictCheck() {
        return this.strictCheck;
    }

    public Grammar parse(InputSource source) throws ParserConfigurationException, SAXException, IOException {
        return _loadSchema(source);
    }

    public Grammar parse(String url) throws ParserConfigurationException, SAXException, IOException {
        return _loadSchema(url);
    }

    public REDocumentDeclaration parseVGM(String url) throws ParserConfigurationException, SAXException, IOException {
        Grammar g = _loadSchema(url);
        if (g == null) {
            return null;
        }
        return new REDocumentDeclaration(g);
    }

    public REDocumentDeclaration parseVGM(InputSource source) throws ParserConfigurationException, SAXException, IOException {
        Grammar g = _loadSchema(source);
        if (g == null) {
            return null;
        }
        return new REDocumentDeclaration(g);
    }

    private boolean hasDTDextension(String name) {
        int idx;
        if (name != null && (idx = name.length() - 4) >= 0) {
            return name.substring(idx).equalsIgnoreCase(".dtd");
        }
        return false;
    }

    private Grammar _loadSchema(Object source) throws ParserConfigurationException, SAXException, IOException {
        boolean isDTD = false;
        if ((source instanceof String) && hasDTDextension((String) source)) {
            isDTD = true;
        }
        if ((source instanceof InputSource) && hasDTDextension(((InputSource) source).getSystemId())) {
            isDTD = true;
        }
        if (isDTD) {
            if (source instanceof String) {
                source = Util.getInputSource((String) source);
            }
            return DTDReader.parse((InputSource) source, getController());
        }
        final GrammarReader[] reader = new GrammarReader[1];
        final XMLReader parser = getSAXParserFactory().newSAXParser().getXMLReader();
        parser.setContentHandler(new DefaultHandler() { // from class: com.ctc.wstx.shaded.msv_core.reader.util.GrammarLoader.2
            private Locator locator;
            private Vector prefixes = new Vector();

            @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
            public void setDocumentLocator(Locator loc) {
                this.locator = loc;
            }

            @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
            public void startPrefixMapping(String prefix, String uri) {
                this.prefixes.add(new String[]{prefix, uri});
            }

            private ContentHandler setupPipeline(Schema schema) throws SAXException {
                try {
                    Verifier v = schema.newVerifier();
                    v.setErrorHandler(GrammarLoader.this.getController());
                    v.setEntityResolver(GrammarLoader.this.getController());
                    VerifierFilter filter = v.getVerifierFilter();
                    filter.setContentHandler(reader[0]);
                    return (ContentHandler) filter;
                } catch (VerifierConfigurationException vce) {
                    throw new SAXException(vce);
                }
            }

            @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
            public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
                ContentHandler winner;
                if (localName.equals("module")) {
                    if (GrammarLoader.this.strictCheck) {
                        Schema s = RELAXCoreReader.getRELAXCoreSchema4Schema();
                        reader[0] = new RELAXCoreReader(GrammarLoader.this.getController(), new SAXParserFactoryImpl(GrammarLoader.this.getSAXParserFactory(), s), GrammarLoader.this.getPool());
                        winner = setupPipeline(s);
                    } else {
                        GrammarReader[] grammarReaderArr = reader;
                        RELAXCoreReader rELAXCoreReader = new RELAXCoreReader(GrammarLoader.this.getController(), GrammarLoader.this.getSAXParserFactory(), GrammarLoader.this.getPool());
                        grammarReaderArr[0] = rELAXCoreReader;
                        winner = rELAXCoreReader;
                    }
                } else if (localName.equals("schema")) {
                    if (GrammarLoader.this.strictCheck) {
                        Schema s2 = XMLSchemaReader.getXmlSchemaForXmlSchema();
                        reader[0] = new XMLSchemaReader(GrammarLoader.this.getController(), new SAXParserFactoryImpl(GrammarLoader.this.getSAXParserFactory(), s2), GrammarLoader.this.getPool());
                        winner = setupPipeline(s2);
                    } else {
                        GrammarReader[] grammarReaderArr2 = reader;
                        XMLSchemaReader xMLSchemaReader = new XMLSchemaReader(GrammarLoader.this.getController(), GrammarLoader.this.getSAXParserFactory(), GrammarLoader.this.getPool());
                        grammarReaderArr2[0] = xMLSchemaReader;
                        winner = xMLSchemaReader;
                    }
                } else if (RELAXNSReader.RELAXNamespaceNamespace.equals(namespaceURI)) {
                    GrammarReader[] grammarReaderArr3 = reader;
                    RELAXNSReader rELAXNSReader = new RELAXNSReader(GrammarLoader.this.getController(), GrammarLoader.this.getSAXParserFactory(), GrammarLoader.this.getPool());
                    grammarReaderArr3[0] = rELAXNSReader;
                    winner = rELAXNSReader;
                } else if (!"http://www.thaiopensource.com/trex".equals(namespaceURI) && !namespaceURI.equals("")) {
                    if (GrammarLoader.this.strictCheck) {
                        Schema s3 = RELAXNGCompReader.getRELAXNGSchema4Schema();
                        reader[0] = new RELAXNGCompReader(GrammarLoader.this.getController(), new SAXParserFactoryImpl(GrammarLoader.this.getSAXParserFactory(), s3), GrammarLoader.this.getPool());
                        winner = setupPipeline(s3);
                    } else {
                        GrammarReader[] grammarReaderArr4 = reader;
                        RELAXNGCompReader rELAXNGCompReader = new RELAXNGCompReader(GrammarLoader.this.getController(), GrammarLoader.this.getSAXParserFactory(), GrammarLoader.this.getPool());
                        grammarReaderArr4[0] = rELAXNGCompReader;
                        winner = rELAXNGCompReader;
                    }
                } else {
                    GrammarReader[] grammarReaderArr5 = reader;
                    TREXGrammarReader tREXGrammarReader = new TREXGrammarReader(GrammarLoader.this.getController(), GrammarLoader.this.getSAXParserFactory(), GrammarLoader.this.getPool());
                    grammarReaderArr5[0] = tREXGrammarReader;
                    winner = tREXGrammarReader;
                }
                winner.setDocumentLocator(this.locator);
                winner.startDocument();
                for (int i = 0; i < this.prefixes.size(); i++) {
                    String[] d = (String[]) this.prefixes.get(i);
                    winner.startPrefixMapping(d[0], d[1]);
                }
                winner.startElement(namespaceURI, localName, qName, atts);
                parser.setContentHandler(winner);
            }
        });
        parser.setErrorHandler(getController());
        parser.setEntityResolver(getController());
        if (source instanceof String) {
            parser.parse((String) source);
        } else {
            parser.parse((InputSource) source);
        }
        if (getController().hadError()) {
            return null;
        }
        return reader[0].getResultAsGrammar();
    }
}
