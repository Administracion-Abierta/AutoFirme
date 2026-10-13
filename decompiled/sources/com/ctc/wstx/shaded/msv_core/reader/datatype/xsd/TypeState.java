package com.ctc.wstx.shaded.msv_core.reader.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.StringType;
import com.ctc.wstx.shaded.msv_core.reader.ExpressionOwner;
import com.ctc.wstx.shaded.msv_core.reader.GrammarReader;
import com.ctc.wstx.shaded.msv_core.reader.IgnoreState;
import com.ctc.wstx.shaded.msv_core.reader.SimpleState;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.reader.datatype.TypeOwner;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/reader/datatype/xsd/TypeState.class */
abstract class TypeState extends SimpleState {
    protected abstract XSDatatypeExp makeType() throws DatatypeException;

    TypeState() {
    }

    private XSTypeOwner getParent() {
        if (this.parentState instanceof XSTypeOwner) {
            return (XSTypeOwner) this.parentState;
        }
        return null;
    }

    public final String getTargetNamespaceUri() {
        XSTypeOwner parent = getParent();
        if (parent != null) {
            return getParent().getTargetNamespaceUri();
        }
        return "";
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.SimpleState
    public void endSelf() {
        super.endSelf();
        XSDatatypeExp type = _makeType();
        if (this.parentState instanceof XSTypeOwner) {
            ((XSTypeOwner) this.parentState).onEndChild(type);
        } else if (this.parentState instanceof TypeOwner) {
            ((TypeOwner) this.parentState).onEndChildType(type, type.name);
        } else {
            if (this.parentState instanceof ExpressionOwner) {
                ((ExpressionOwner) this.parentState).onEndChild(type);
                return;
            }
            throw new Error(this.parentState.getClass().getName() + " doesn't implement any of TypeOwner");
        }
    }

    XSDatatypeExp _makeType() {
        try {
            return makeType();
        } catch (DatatypeException be) {
            this.reader.reportError(be, GrammarReader.ERR_BAD_TYPE);
            return new XSDatatypeExp(StringType.theInstance, this.reader.pool);
        }
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.SimpleState, org.xml.sax.ContentHandler
    public final void startElement(String namespaceURI, String localName, String qName, Attributes atts) {
        StartTagInfo tag = new StartTagInfo(namespaceURI, localName, qName, new AttributesImpl(atts));
        State nextState = createChildState(tag);
        if (nextState != null) {
            this.reader.pushState(nextState, this, tag);
        } else {
            this.reader.reportError(GrammarReader.ERR_MALPLACED_ELEMENT, tag.qName);
            this.reader.pushState(new IgnoreState(), this, tag);
        }
    }
}
