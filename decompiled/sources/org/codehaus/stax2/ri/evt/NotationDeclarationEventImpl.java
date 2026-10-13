package org.codehaus.stax2.ri.evt;

import java.io.IOException;
import java.io.Writer;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import org.codehaus.stax2.XMLStreamWriter2;
import org.codehaus.stax2.evt.NotationDeclaration2;

/* JADX INFO: loaded from: autofirma.jar:org/codehaus/stax2/ri/evt/NotationDeclarationEventImpl.class */
public class NotationDeclarationEventImpl extends BaseEventImpl implements NotationDeclaration2 {
    final String mName;
    final String mPublicId;
    final String mSystemId;

    public NotationDeclarationEventImpl(Location loc, String name, String pubId, String sysId) {
        super(loc);
        this.mName = name;
        this.mPublicId = pubId;
        this.mSystemId = sysId;
    }

    public String getName() {
        return this.mName;
    }

    public String getPublicId() {
        return this.mPublicId;
    }

    public String getSystemId() {
        return this.mSystemId;
    }

    public String getBaseURI() {
        return "";
    }

    @Override // org.codehaus.stax2.ri.evt.BaseEventImpl
    public int getEventType() {
        return 14;
    }

    @Override // org.codehaus.stax2.ri.evt.BaseEventImpl
    public void writeAsEncodedUnicode(Writer w) throws XMLStreamException {
        try {
            w.write("<!NOTATION ");
            w.write(this.mName);
            if (this.mPublicId != null) {
                w.write("PUBLIC \"");
                w.write(this.mPublicId);
                w.write(34);
            } else {
                w.write("SYSTEM");
            }
            if (this.mSystemId != null) {
                w.write(" \"");
                w.write(this.mSystemId);
                w.write(34);
            }
            w.write(62);
        } catch (IOException ie) {
            throwFromIOE(ie);
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.ri.evt.BaseEventImpl, org.codehaus.stax2.evt.XMLEvent2
    public void writeUsing(XMLStreamWriter2 w) throws XMLStreamException {
        throw new XMLStreamException("Can not write notation declarations using an XMLStreamWriter");
    }

    @Override // org.codehaus.stax2.ri.evt.BaseEventImpl
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null || !(o instanceof NotationDeclaration2)) {
            return false;
        }
        NotationDeclaration2 other = (NotationDeclaration2) o;
        return stringsWithNullsEqual(getName(), other.getName()) && stringsWithNullsEqual(getPublicId(), other.getPublicId()) && stringsWithNullsEqual(getSystemId(), other.getSystemId()) && stringsWithNullsEqual(getBaseURI(), other.getBaseURI());
    }

    @Override // org.codehaus.stax2.ri.evt.BaseEventImpl
    public int hashCode() {
        int hash = 0;
        if (this.mName != null) {
            hash = 0 ^ this.mName.hashCode();
        }
        if (this.mPublicId != null) {
            hash ^= this.mPublicId.hashCode();
        }
        if (this.mSystemId != null) {
            hash ^= this.mSystemId.hashCode();
        }
        return hash;
    }
}
