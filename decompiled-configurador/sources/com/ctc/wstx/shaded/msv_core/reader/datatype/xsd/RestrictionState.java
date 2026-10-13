package com.ctc.wstx.shaded.msv_core.reader.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.StringType;
import com.ctc.wstx.shaded.msv_core.reader.GrammarReader;
import com.ctc.wstx.shaded.msv_core.reader.IgnoreState;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/reader/datatype/xsd/RestrictionState.class */
public class RestrictionState extends TypeWithOneChildState implements FacetStateParent {
    protected final String newTypeUri;
    protected final String newTypeName;
    protected XSTypeIncubator incubator;

    @Override // com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.TypeState, com.ctc.wstx.shaded.msv_core.reader.SimpleState
    public /* bridge */ /* synthetic */ void endSelf() {
        super.endSelf();
    }

    protected RestrictionState(String newTypeUri, String newTypeName) {
        this.newTypeUri = newTypeUri;
        this.newTypeName = newTypeName;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.FacetStateParent
    public final XSTypeIncubator getIncubator() {
        return this.incubator;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.TypeWithOneChildState
    protected XSDatatypeExp annealType(XSDatatypeExp baseType) throws DatatypeException {
        return this.incubator.derive(this.newTypeUri, this.newTypeName);
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.TypeWithOneChildState, com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.XSTypeOwner
    public void onEndChild(XSDatatypeExp child) {
        super.onEndChild(child);
        createTypeIncubator();
    }

    private void createTypeIncubator() {
        this.incubator = this.type.createIncubator();
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.State
    protected void startSelf() {
        super.startSelf();
        String base = this.startTag.getAttribute("base");
        if (base != null) {
            onEndChild(((XSDatatypeResolver) this.reader).resolveXSDatatype(base));
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
        if (FacetState.facetNames.contains(tag.localName)) {
            if (this.incubator == null) {
                this.reader.reportError(GrammarReader.ERR_MISSING_ATTRIBUTE, "restriction", "base");
                onEndChild(new XSDatatypeExp(StringType.theInstance, this.reader.pool));
            }
            return new FacetState();
        }
        return null;
    }
}
