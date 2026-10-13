package com.ctc.wstx.evt;

import javax.xml.stream.Location;
import javax.xml.stream.events.EntityDeclaration;
import javax.xml.stream.events.EntityReference;
import org.codehaus.stax2.ri.evt.EntityReferenceEventImpl;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/evt/WEntityReference.class */
public class WEntityReference extends EntityReferenceEventImpl implements EntityReference {
    final String mName;

    public WEntityReference(Location loc, EntityDeclaration decl) {
        super(loc, decl);
        this.mName = null;
    }

    public WEntityReference(Location loc, String name) {
        super(loc, (EntityDeclaration) null);
        this.mName = name;
    }

    @Override // org.codehaus.stax2.ri.evt.EntityReferenceEventImpl
    public String getName() {
        if (this.mName != null) {
            return this.mName;
        }
        return super.getName();
    }
}
