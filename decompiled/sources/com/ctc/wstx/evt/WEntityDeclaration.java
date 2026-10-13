package com.ctc.wstx.evt;

import com.ctc.wstx.exc.WstxIOException;
import java.io.IOException;
import java.io.Writer;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EntityDeclaration;
import org.codehaus.stax2.XMLStreamWriter2;
import org.codehaus.stax2.ri.evt.BaseEventImpl;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/evt/WEntityDeclaration.class */
public abstract class WEntityDeclaration extends BaseEventImpl implements EntityDeclaration {
    public abstract String getBaseURI();

    public abstract String getName();

    public abstract String getNotationName();

    public abstract String getPublicId();

    public abstract String getReplacementText();

    public abstract String getSystemId();

    public abstract void writeEnc(Writer writer) throws IOException;

    public WEntityDeclaration(Location loc) {
        super(loc);
    }

    @Override // org.codehaus.stax2.ri.evt.BaseEventImpl
    public int getEventType() {
        return 15;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.ri.evt.BaseEventImpl
    public void writeAsEncodedUnicode(Writer w) throws XMLStreamException {
        try {
            writeEnc(w);
        } catch (IOException ie) {
            throw new WstxIOException(ie);
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.ri.evt.BaseEventImpl, org.codehaus.stax2.evt.XMLEvent2
    public void writeUsing(XMLStreamWriter2 w) throws XMLStreamException {
        throw new XMLStreamException("Can not write entity declarations using an XMLStreamWriter");
    }

    @Override // org.codehaus.stax2.ri.evt.BaseEventImpl
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null || !(o instanceof EntityDeclaration)) {
            return false;
        }
        EntityDeclaration other = (EntityDeclaration) o;
        return stringsWithNullsEqual(getName(), other.getName()) && stringsWithNullsEqual(getBaseURI(), other.getBaseURI()) && stringsWithNullsEqual(getNotationName(), other.getNotationName()) && stringsWithNullsEqual(getPublicId(), other.getPublicId()) && stringsWithNullsEqual(getReplacementText(), other.getReplacementText()) && stringsWithNullsEqual(getSystemId(), other.getSystemId());
    }

    @Override // org.codehaus.stax2.ri.evt.BaseEventImpl
    public int hashCode() {
        return getName().hashCode();
    }
}
