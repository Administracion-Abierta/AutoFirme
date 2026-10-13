package com.ctc.wstx.shaded.msv_core.reader.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;
import com.ctc.wstx.shaded.msv_core.reader.IgnoreState;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;
import java.util.ArrayList;
import java.util.StringTokenizer;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/reader/datatype/xsd/UnionState.class */
public class UnionState extends TypeState implements XSTypeOwner {
    protected final String newTypeUri;
    protected final String newTypeName;
    private final ArrayList memberTypes = new ArrayList();

    @Override // com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.TypeState, com.ctc.wstx.shaded.msv_core.reader.SimpleState
    public /* bridge */ /* synthetic */ void endSelf() {
        super.endSelf();
    }

    protected UnionState(String newTypeUri, String newTypeName) {
        this.newTypeUri = newTypeUri;
        this.newTypeName = newTypeName;
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

    @Override // com.ctc.wstx.shaded.msv_core.reader.State
    protected void startSelf() {
        super.startSelf();
        String memberTypes = this.startTag.getAttribute("memberTypes");
        if (memberTypes != null) {
            StringTokenizer tokens = new StringTokenizer(memberTypes);
            while (tokens.hasMoreTokens()) {
                onEndChild(((XSDatatypeResolver) this.reader).resolveXSDatatype(tokens.nextToken()));
            }
        }
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.XSTypeOwner
    public void onEndChild(XSDatatypeExp type) {
        this.memberTypes.add(type);
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.TypeState
    protected final XSDatatypeExp makeType() throws DatatypeException {
        return XSDatatypeExp.makeUnion(this.newTypeUri, this.newTypeName, this.memberTypes, this.reader);
    }
}
