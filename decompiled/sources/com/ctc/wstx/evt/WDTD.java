package com.ctc.wstx.evt;

import com.ctc.wstx.dtd.DTDSubset;
import java.util.ArrayList;
import java.util.List;
import javax.xml.stream.Location;
import javax.xml.stream.events.EntityDeclaration;
import javax.xml.stream.events.NotationDeclaration;
import org.codehaus.stax2.ri.evt.DTDEventImpl;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/evt/WDTD.class */
public class WDTD extends DTDEventImpl {
    final DTDSubset mSubset;
    List<EntityDeclaration> mEntities;
    List<NotationDeclaration> mNotations;

    public WDTD(Location loc, String rootName, String sysId, String pubId, String intSubset, DTDSubset dtdSubset) {
        super(loc, rootName, sysId, pubId, intSubset, dtdSubset);
        this.mEntities = null;
        this.mNotations = null;
        this.mSubset = dtdSubset;
    }

    public WDTD(Location loc, String rootName, String sysId, String pubId, String intSubset) {
        this(loc, rootName, sysId, pubId, intSubset, null);
    }

    public WDTD(Location loc, String rootName, String intSubset) {
        this(loc, rootName, null, null, intSubset, null);
    }

    public WDTD(Location loc, String fullText) {
        super(loc, fullText);
        this.mEntities = null;
        this.mNotations = null;
        this.mSubset = null;
    }

    @Override // org.codehaus.stax2.ri.evt.DTDEventImpl
    public List<EntityDeclaration> getEntities() {
        if (this.mEntities == null && this.mSubset != null) {
            this.mEntities = new ArrayList(this.mSubset.getGeneralEntityList());
        }
        return this.mEntities;
    }

    @Override // org.codehaus.stax2.ri.evt.DTDEventImpl
    public List<NotationDeclaration> getNotations() {
        if (this.mNotations == null && this.mSubset != null) {
            this.mNotations = new ArrayList(this.mSubset.getNotationList());
        }
        return this.mNotations;
    }
}
