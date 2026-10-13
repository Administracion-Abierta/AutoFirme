package com.ctc.wstx.msv;

import com.ctc.wstx.shaded.msv_core.grammar.ExpressionPool;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.XMLSchemaGrammar;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.XMLSchemaSchema;
import com.ctc.wstx.shaded.msv_core.reader.GrammarReaderController;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.reader.xmlschema.EmbeddedSchema;
import com.ctc.wstx.shaded.msv_core.reader.xmlschema.MultiSchemaReader;
import com.ctc.wstx.shaded.msv_core.reader.xmlschema.SchemaState;
import com.ctc.wstx.shaded.msv_core.reader.xmlschema.WSDLGrammarReaderController;
import com.ctc.wstx.shaded.msv_core.reader.xmlschema.XMLSchemaReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import org.codehaus.stax2.validation.XMLValidationSchema;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.Locator;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/msv/W3CMultiSchemaFactory.class */
public class W3CMultiSchemaFactory {
    private final SAXParserFactory parserFactory = SAXParserFactory.newInstance();

    public W3CMultiSchemaFactory() {
        this.parserFactory.setNamespaceAware(true);
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/msv/W3CMultiSchemaFactory$RecursiveAllowedXMLSchemaReader.class */
    static class RecursiveAllowedXMLSchemaReader extends XMLSchemaReader {
        Set<String> sysIds;

        RecursiveAllowedXMLSchemaReader(GrammarReaderController controller, SAXParserFactory parserFactory) {
            super(controller, parserFactory, new XMLSchemaReader.StateFactory() { // from class: com.ctc.wstx.msv.W3CMultiSchemaFactory.RecursiveAllowedXMLSchemaReader.1
                @Override // com.ctc.wstx.shaded.msv_core.reader.xmlschema.XMLSchemaReader.StateFactory
                public State schemaHead(String expectedNamespace) {
                    return new SchemaState(expectedNamespace) { // from class: com.ctc.wstx.msv.W3CMultiSchemaFactory.RecursiveAllowedXMLSchemaReader.1.1
                        private XMLSchemaSchema old;

                        @Override // com.ctc.wstx.shaded.msv_core.reader.xmlschema.SchemaState, com.ctc.wstx.shaded.msv_core.reader.xmlschema.SchemaIncludedState, com.ctc.wstx.shaded.msv_core.reader.SimpleState
                        protected void endSelf() {
                            super.endSelf();
                            RecursiveAllowedXMLSchemaReader r = (RecursiveAllowedXMLSchemaReader) this.reader;
                            r.currentSchema = this.old;
                        }

                        @Override // com.ctc.wstx.shaded.msv_core.reader.xmlschema.SchemaState, com.ctc.wstx.shaded.msv_core.reader.xmlschema.SchemaIncludedState
                        protected void onTargetNamespaceResolved(String targetNs, boolean ignoreContents) {
                            RecursiveAllowedXMLSchemaReader r = (RecursiveAllowedXMLSchemaReader) this.reader;
                            this.old = r.currentSchema;
                            r.currentSchema = r.getOrCreateSchema(targetNs);
                            if (!ignoreContents && !r.isSchemaDefined(r.currentSchema)) {
                                r.markSchemaAsDefined(r.currentSchema);
                            }
                        }
                    };
                }
            }, new ExpressionPool());
            this.sysIds = new TreeSet();
        }

        @Override // com.ctc.wstx.shaded.msv_core.reader.GrammarReader
        public void setLocator(Locator locator) {
            if (locator == null && getLocator() != null && getLocator().getSystemId() != null) {
                this.sysIds.add(getLocator().getSystemId());
            }
            super.setLocator(locator);
        }

        @Override // com.ctc.wstx.shaded.msv_core.reader.GrammarReader
        public void switchSource(Source source, State newState) {
            String url = source.getSystemId();
            if (url != null && this.sysIds.contains(url)) {
                return;
            }
            super.switchSource(source, newState);
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public XMLValidationSchema createSchema(String baseURI, Map<String, Source> schemaSources) throws XMLStreamException {
        Map<String, EmbeddedSchema> embeddedSources = new HashMap<>();
        for (Map.Entry<String, Source> source : schemaSources.entrySet()) {
            if (source.getValue() instanceof DOMSource) {
                Node nd = ((DOMSource) source.getValue()).getNode();
                Element el = null;
                if (nd instanceof Element) {
                    el = (Element) nd;
                } else if (nd instanceof Document) {
                    el = ((Document) nd).getDocumentElement();
                }
                embeddedSources.put(source.getKey(), new EmbeddedSchema(source.getValue().getSystemId(), el));
            }
        }
        WSDLGrammarReaderController ctrl = new WSDLGrammarReaderController(null, baseURI, embeddedSources);
        RecursiveAllowedXMLSchemaReader xmlSchemaReader = new RecursiveAllowedXMLSchemaReader(ctrl, this.parserFactory);
        MultiSchemaReader multiSchemaReader = new MultiSchemaReader(xmlSchemaReader);
        Iterator<Source> it = schemaSources.values().iterator();
        while (it.hasNext()) {
            multiSchemaReader.parse(it.next());
        }
        XMLSchemaGrammar grammar = multiSchemaReader.getResult();
        if (grammar == null) {
            throw new XMLStreamException("Failed to load schemas");
        }
        return new W3CSchema(grammar);
    }
}
