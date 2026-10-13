package com.ctc.wstx.shaded.msv_core.relaxns.verifier;

import com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.Dispatcher;
import com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.ElementDecl;
import com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.IslandSchema;
import com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.IslandVerifier;
import com.ctc.wstx.shaded.msv_core.relaxns.grammar.relax.AnyOtherElementExp;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/relaxns/verifier/AnyOtherElementVerifier.class */
public class AnyOtherElementVerifier extends DefaultHandler implements IslandVerifier {
    private final AnyOtherElementExp[] exps;
    protected Dispatcher dispatcher;
    protected Locator locator;
    public static final String ERR_UNEXPECTED_NAMESPACE = "AnyOtherElementVerifier.UnexpectedNamespace";

    public AnyOtherElementVerifier(AnyOtherElementExp[] exps) {
        this.exps = exps;
    }

    @Override // com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.IslandVerifier
    public void setDispatcher(Dispatcher disp) {
        this.dispatcher = disp;
    }

    @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
        IslandSchema is = this.dispatcher.getSchemaProvider().getSchemaByNamespace(namespaceURI);
        if (is != null) {
            IslandVerifier iv = is.createNewVerifier(namespaceURI, is.getElementDecls());
            this.dispatcher.switchVerifier(iv);
            iv.startElement(namespaceURI, localName, qName, atts);
            return;
        }
        boolean atLeastOneIsValid = false;
        for (int i = 0; i < this.exps.length; i++) {
            if (this.exps[i] != null) {
                if (this.exps[i].getNameClass().accepts(namespaceURI, localName)) {
                    atLeastOneIsValid = true;
                } else {
                    this.exps[i] = null;
                }
            }
        }
        if (!atLeastOneIsValid) {
            this.dispatcher.getErrorHandler().error(new SAXParseException(Localizer.localize(ERR_UNEXPECTED_NAMESPACE, new Object[]{namespaceURI}), this.locator));
        }
    }

    @Override // com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.IslandVerifier
    public void endChildIsland(String namespaceURI, ElementDecl[] rules) {
    }

    @Override // com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.IslandVerifier
    public ElementDecl[] endIsland() {
        int len = 0;
        for (int i = 0; i < this.exps.length; i++) {
            if (this.exps[i] != null) {
                len++;
            }
        }
        ElementDecl[] r = new ElementDecl[len];
        int j = 0;
        for (int i2 = 0; i2 < this.exps.length; i2++) {
            if (this.exps[i2] != null) {
                int i3 = j;
                j++;
                r[i3] = this.exps[i2];
            }
        }
        return r;
    }

    @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
    public void setDocumentLocator(Locator loc) {
        this.locator = loc;
    }
}
