package com.ctc.wstx.shaded.msv_core.relaxns.reader;

import com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.IslandSchema;
import com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.IslandSchemaReader;
import com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.impl.IgnoredSchema;
import com.ctc.wstx.shaded.msv_core.reader.AbortException;
import com.ctc.wstx.shaded.msv_core.reader.GrammarReader;
import com.ctc.wstx.shaded.msv_core.reader.IgnoreState;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.reader.relax.RELAXReader;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;
import java.io.IOException;
import java.util.Vector;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLFilterImpl;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/relaxns/reader/NamespaceState.class */
public class NamespaceState extends State {
    private boolean inlineModuleExpected = false;
    private boolean bailOut = false;
    private String namespace;
    IslandSchemaReader moduleReader;

    @Override // com.ctc.wstx.shaded.msv_core.reader.State
    protected void startSelf() {
        super.startSelf();
        this.namespace = this.startTag.getAttribute("name");
        if (this.namespace == null) {
            this.reader.reportError(GrammarReader.ERR_MISSING_ATTRIBUTE, "namespace", "name");
            return;
        }
        if (getReader().grammar.moduleMap.containsKey(this.namespace)) {
            this.reader.reportError(RELAXNSReader.ERR_NAMESPACE_COLLISION, this.namespace);
            return;
        }
        String validation = this.startTag.getAttribute("validation");
        if ("false".equals(validation)) {
            getReader().grammar.moduleMap.put(this.namespace, new IgnoredSchema());
            return;
        }
        String language = this.startTag.getAttribute("language");
        if (language == null) {
            language = RELAXReader.RELAXCoreNamespace;
        }
        this.moduleReader = getReader().getIslandSchemaReader(language, this.namespace);
        if (this.moduleReader == null) {
            this.reader.reportError(RELAXNSReader.ERR_UNKNOWN_LANGUAGE, language);
            this.bailOut = true;
            return;
        }
        String moduleLocation = this.startTag.getAttribute("moduleLocation");
        if (moduleLocation == null) {
            this.inlineModuleExpected = true;
            return;
        }
        try {
            InputSource is = this.reader.resolveLocation(this, moduleLocation);
            XMLReader parser = this.reader.parserFactory.newSAXParser().getXMLReader();
            parser.setContentHandler(this.moduleReader);
            parser.parse(is);
        } catch (AbortException e) {
        } catch (IOException e2) {
            this.reader.controller.error(e2, getLocation());
        } catch (ParserConfigurationException e3) {
            this.reader.controller.error(e3, getLocation());
        } catch (SAXException e4) {
            this.reader.controller.error(e4, getLocation());
        }
        getSchema(this.moduleReader);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void getSchema(IslandSchemaReader moduleReader) {
        IslandSchema schema = moduleReader.getSchema();
        if (schema == null) {
            this.reader.controller.setErrorFlag();
            schema = new IgnoredSchema();
        }
        getReader().grammar.moduleMap.put(this.namespace, schema);
    }

    @Override // org.xml.sax.ContentHandler
    public void startElement(String namespace, String localName, String qName, Attributes atts) throws SAXException {
        if (this.bailOut) {
            this.reader.pushState(new IgnoreState(), this, new StartTagInfo(namespace, localName, qName, atts, this.reader));
            return;
        }
        if (!this.inlineModuleExpected) {
            this.reader.reportError(GrammarReader.ERR_MALPLACED_ELEMENT, qName);
            this.bailOut = true;
            return;
        }
        this.moduleReader.startDocument();
        this.moduleReader.setDocumentLocator(this.reader.getLocator());
        GrammarReader.PrefixResolver resolver = this.reader.prefixResolver;
        Vector prefixes = new Vector();
        while (resolver instanceof GrammarReader.ChainPrefixResolver) {
            GrammarReader.ChainPrefixResolver ch = (GrammarReader.ChainPrefixResolver) resolver;
            prefixes.add(ch.prefix);
            resolver = ch.previous;
        }
        for (int i = 0; i < prefixes.size(); i++) {
            String p = (String) prefixes.get(i);
            this.moduleReader.startPrefixMapping(p, this.reader.prefixResolver.resolve(p));
        }
        this.moduleReader.startElement(namespace, localName, qName, atts);
        CutInFilter cutInFilter = new CutInFilter();
        cutInFilter.setContentHandler(this.moduleReader);
        this.reader.setContentHandler(cutInFilter);
        this.inlineModuleExpected = false;
    }

    @Override // org.xml.sax.ContentHandler
    public void endElement(String namespace, String localName, String qName) {
        if (this.inlineModuleExpected) {
            this.reader.reportError(RELAXNSReader.ERR_INLINEMODULE_NOT_FOUND);
        }
        this.reader.popState();
    }

    @Override // org.xml.sax.ContentHandler
    public void endDocument() {
        throw new Error();
    }

    /* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/relaxns/reader/NamespaceState$CutInFilter.class */
    private class CutInFilter extends XMLFilterImpl {
        private int depth;

        private CutInFilter() {
            this.depth = 0;
        }

        @Override // org.xml.sax.helpers.XMLFilterImpl, org.xml.sax.ContentHandler
        public void startElement(String a, String b, String c, Attributes d) throws SAXException {
            this.depth++;
            super.startElement(a, b, c, d);
        }

        @Override // org.xml.sax.helpers.XMLFilterImpl, org.xml.sax.ContentHandler
        public void endElement(String a, String b, String c) throws SAXException {
            super.endElement(a, b, c);
            if (this.depth == 0) {
                super.endDocument();
                NamespaceState.this.getReader().setContentHandler(NamespaceState.this);
                NamespaceState.this.getSchema(NamespaceState.this.moduleReader);
                return;
            }
            this.depth--;
        }
    }

    protected RELAXNSReader getReader() {
        return (RELAXNSReader) this.reader;
    }
}
