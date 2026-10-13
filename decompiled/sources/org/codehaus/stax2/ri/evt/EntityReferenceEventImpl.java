package org.codehaus.stax2.ri.evt;

import java.io.IOException;
import java.io.Writer;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EntityDeclaration;
import javax.xml.stream.events.EntityReference;
import org.codehaus.stax2.XMLStreamWriter2;

/* JADX INFO: loaded from: autofirma.jar:org/codehaus/stax2/ri/evt/EntityReferenceEventImpl.class */
public class EntityReferenceEventImpl extends BaseEventImpl implements EntityReference {
    protected final EntityDeclaration mDecl;

    public EntityReferenceEventImpl(Location loc, EntityDeclaration decl) {
        super(loc);
        this.mDecl = decl;
    }

    public EntityReferenceEventImpl(Location loc, String name) {
        super(loc);
        this.mDecl = new EntityDeclarationEventImpl(loc, name);
    }

    public EntityDeclaration getDeclaration() {
        return this.mDecl;
    }

    public String getName() {
        return this.mDecl.getName();
    }

    @Override // org.codehaus.stax2.ri.evt.BaseEventImpl
    public int getEventType() {
        return 9;
    }

    @Override // org.codehaus.stax2.ri.evt.BaseEventImpl
    public boolean isEntityReference() {
        return true;
    }

    @Override // org.codehaus.stax2.ri.evt.BaseEventImpl
    public void writeAsEncodedUnicode(Writer w) throws XMLStreamException {
        try {
            w.write(38);
            w.write(getName());
            w.write(59);
        } catch (IOException ie) {
            throwFromIOE(ie);
        }
    }

    @Override // org.codehaus.stax2.ri.evt.BaseEventImpl, org.codehaus.stax2.evt.XMLEvent2
    public void writeUsing(XMLStreamWriter2 w) throws XMLStreamException {
        w.writeEntityRef(getName());
    }

    @Override // org.codehaus.stax2.ri.evt.BaseEventImpl
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null || !(o instanceof EntityReference)) {
            return false;
        }
        EntityReference other = (EntityReference) o;
        return getName().equals(other.getName());
    }

    @Override // org.codehaus.stax2.ri.evt.BaseEventImpl
    public int hashCode() {
        return getName().hashCode();
    }
}
