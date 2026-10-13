package com.ctc.wstx.shaded.msv_core.verifier;

import com.ctc.wstx.shaded.msv.relaxng_datatype.Datatype;
import com.ctc.wstx.shaded.msv_core.grammar.IDContextProvider2;
import com.ctc.wstx.shaded.msv_core.verifier.regexp.StringToken;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.LocatorImpl;
import org.xml.sax.helpers.NamespaceSupport;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/verifier/AbstractVerifier.class */
public abstract class AbstractVerifier implements ContentHandler, DTDHandler, IDContextProvider2 {
    protected static final Locator dummyLocator = new LocatorImpl();
    protected Locator locator = dummyLocator;
    protected boolean performIDcheck = true;
    protected final Map ids = new HashMap();
    protected final Set idrefs = new HashSet();
    private boolean contextPushed = false;
    protected final NamespaceSupport namespaceSupport = new NamespaceSupport();
    private final Set unparsedEntities = new HashSet();
    private final Set notations = new HashSet();

    protected abstract void onDuplicateId(String str);

    public final Locator getLocator() {
        return this.locator;
    }

    @Override // org.xml.sax.ContentHandler
    public void setDocumentLocator(Locator loc) {
        this.locator = loc;
    }

    @Override // org.xml.sax.ContentHandler
    public void skippedEntity(String p) {
    }

    @Override // org.xml.sax.ContentHandler
    public void processingInstruction(String name, String data) {
    }

    @Override // org.xml.sax.ContentHandler
    public void startPrefixMapping(String prefix, String uri) {
        if (!this.contextPushed) {
            this.namespaceSupport.pushContext();
            this.contextPushed = true;
        }
        this.namespaceSupport.declarePrefix(prefix, uri);
    }

    @Override // org.xml.sax.ContentHandler
    public void endPrefixMapping(String prefix) {
    }

    @Override // org.xml.sax.ContentHandler
    public void startElement(String namespaceUri, String localName, String qName, Attributes atts) throws SAXException {
        if (!this.contextPushed) {
            this.namespaceSupport.pushContext();
        }
        this.contextPushed = false;
    }

    @Override // org.xml.sax.ContentHandler
    public void endElement(String namespaceUri, String localName, String qName) throws SAXException {
        this.namespaceSupport.popContext();
    }

    protected void init() {
        this.ids.clear();
        this.idrefs.clear();
    }

    @Override // org.xml.sax.DTDHandler
    public void notationDecl(String name, String publicId, String systemId) {
        this.notations.add(name);
    }

    @Override // org.xml.sax.DTDHandler
    public void unparsedEntityDecl(String name, String publicId, String systemId, String notationName) {
        this.unparsedEntities.add(name);
    }

    @Override // com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext
    public String resolveNamespacePrefix(String prefix) {
        String uri = this.namespaceSupport.getURI(prefix);
        return (uri == null && prefix.length() == 0) ? "" : uri;
    }

    public boolean isUnparsedEntity(String entityName) {
        return this.unparsedEntities.contains(entityName);
    }

    @Override // com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext
    public boolean isNotation(String notationName) {
        return this.notations.contains(notationName);
    }

    @Override // com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext
    public String getBaseUri() {
        return null;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.IDContextProvider2
    public void onID(Datatype dt, StringToken token) {
        if (!this.performIDcheck) {
            return;
        }
        int idType = dt.getIdType();
        if (idType == 1) {
            String literal = token.literal.trim();
            StringToken existing = (StringToken) this.ids.get(literal);
            if (existing == null) {
                this.ids.put(literal, token);
                return;
            } else {
                if (existing != token) {
                    onDuplicateId(literal);
                    return;
                }
                return;
            }
        }
        if (idType == 2) {
            this.idrefs.add(token.literal.trim());
        } else {
            if (idType == 3) {
                StringTokenizer tokens = new StringTokenizer(token.literal);
                while (tokens.hasMoreTokens()) {
                    this.idrefs.add(tokens.nextToken());
                }
                return;
            }
            throw new Error();
        }
    }
}
