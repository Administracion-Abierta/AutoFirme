package com.ctc.wstx.shaded.msv_core.verifier.identity;

import com.ctc.wstx.shaded.msv_core.driver.textui.Debug;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.KeyConstraint;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.KeyRefConstraint;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.LocatorImpl;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/verifier/identity/FieldsMatcher.class */
public class FieldsMatcher extends MatcherBundle {
    protected final Locator startTag;
    protected final SelectorMatcher selector;

    protected FieldsMatcher(SelectorMatcher selector, String namespaceURI, String localName) throws SAXException {
        super(selector.owner);
        this.selector = selector;
        if (this.owner.getLocator() == null) {
            this.startTag = null;
        } else {
            this.startTag = new LocatorImpl(this.owner.getLocator());
        }
        this.children = new Matcher[selector.idConst.fields.length];
        for (int i = 0; i < selector.idConst.fields.length; i++) {
            this.children[i] = new FieldMatcher(this, selector.idConst.fields[i], namespaceURI, localName);
        }
    }

    @Override // com.ctc.wstx.shaded.msv_core.verifier.identity.MatcherBundle
    protected void onRemoved() throws SAXException {
        Object[] values = new Object[this.children.length];
        for (int i = 0; i < this.children.length; i++) {
            Object obj = ((FieldMatcher) this.children[i]).value;
            values[i] = obj;
            if (obj == null) {
                if (!(this.selector.idConst instanceof KeyConstraint)) {
                    return;
                }
                this.owner.reportError(this.startTag, null, IDConstraintChecker.ERR_UNMATCHED_KEY_FIELD, new Object[]{this.selector.idConst.namespaceURI, this.selector.idConst.localName, new Integer(i + 1)});
                return;
            }
        }
        if (Debug.debug) {
            System.out.println("fields collected for " + this.selector.idConst.localName);
        }
        KeyValue kv = new KeyValue(values, this.startTag);
        if (this.owner.addKeyValue(this.selector, kv) || (this.selector.idConst instanceof KeyRefConstraint)) {
            return;
        }
        Object[] items = this.owner.getKeyValues(this.selector);
        int i2 = 0;
        while (i2 < values.length && !items[i2].equals(kv)) {
            i2++;
        }
        this.owner.reportError(this.startTag, null, IDConstraintChecker.ERR_NOT_UNIQUE, new Object[]{this.selector.idConst.namespaceURI, this.selector.idConst.localName});
        this.owner.reportError(((KeyValue) items[i2]).locator, null, IDConstraintChecker.ERR_NOT_UNIQUE_DIAG, new Object[]{this.selector.idConst.namespaceURI, this.selector.idConst.localName});
    }
}
