package com.ctc.wstx.shaded.msv_core.reader;

import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.helpers.AttributesImpl;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/reader/SimpleState.class */
public abstract class SimpleState extends State {
    protected abstract State createChildState(StartTagInfo startTagInfo);

    protected boolean isGrammarElement(StartTagInfo tag) {
        return this.reader.isGrammarElement(tag);
    }

    @Override // org.xml.sax.ContentHandler
    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) {
        StartTagInfo tag = new StartTagInfo(namespaceURI, localName, qName, new AttributesImpl(atts));
        if (isGrammarElement(tag)) {
            State nextState = createChildState(tag);
            if (nextState != null) {
                this.reader.pushState(nextState, this, tag);
                return;
            }
            this.reader.reportError(GrammarReader.ERR_MALPLACED_ELEMENT, tag.qName);
        } else if (this.parentState == null) {
            this.reader.reportError(GrammarReader.ERR_MALPLACED_ELEMENT, tag.qName);
            this.reader.reportError(GrammarReader.WRN_MAYBE_WRONG_NAMESPACE, tag.namespaceURI);
        }
        this.reader.pushState(new IgnoreState(), this, tag);
    }

    @Override // org.xml.sax.ContentHandler
    public final void endElement(String namespaceURI, String localName, String qName) {
        Locator prevLoc = this.reader.getLocator();
        try {
            this.reader.setLocator(this.location);
            endSelf();
            this.reader.setLocator(prevLoc);
            this.reader.popState();
        } catch (Throwable th) {
            this.reader.setLocator(prevLoc);
            throw th;
        }
    }

    @Override // org.xml.sax.ContentHandler
    public final void endDocument() {
        endSelf();
        this.reader.popState();
    }

    protected void endSelf() {
    }
}
