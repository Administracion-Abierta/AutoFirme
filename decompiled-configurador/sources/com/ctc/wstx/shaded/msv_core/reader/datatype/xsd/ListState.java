package com.ctc.wstx.shaded.msv_core.reader.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;
import com.ctc.wstx.shaded.msv_core.reader.IgnoreState;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/reader/datatype/xsd/ListState.class */
public class ListState extends TypeWithOneChildState {
    protected final String newTypeUri;
    protected final String newTypeName;

    @Override // com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.TypeWithOneChildState, com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.XSTypeOwner
    public /* bridge */ /* synthetic */ void onEndChild(XSDatatypeExp x0) {
        super.onEndChild(x0);
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.TypeState, com.ctc.wstx.shaded.msv_core.reader.SimpleState
    public /* bridge */ /* synthetic */ void endSelf() {
        super.endSelf();
    }

    protected ListState(String newTypeUri, String newTypeName) {
        this.newTypeUri = newTypeUri;
        this.newTypeName = newTypeName;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.TypeWithOneChildState
    protected XSDatatypeExp annealType(XSDatatypeExp itemType) throws DatatypeException {
        return XSDatatypeExp.makeList(this.newTypeUri, this.newTypeName, itemType, this.reader);
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.State
    protected void startSelf() {
        super.startSelf();
        String itemType = this.startTag.getAttribute("itemType");
        if (itemType != null) {
            onEndChild(((XSDatatypeResolver) this.reader).resolveXSDatatype(itemType));
        }
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.SimpleState
    protected State createChildState(StartTagInfo tag) {
        if (!this.startTag.namespaceURI.equals(tag.namespaceURI)) {
            return null;
        }
        if (tag.localName.equals("annotation")) {
            return new IgnoreState();
        }
        if (tag.localName.equals("simpleType")) {
            return new SimpleTypeState();
        }
        return null;
    }
}
