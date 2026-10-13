package org.codehaus.stax2.ri.evt;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EntityDeclaration;
import org.codehaus.stax2.XMLStreamWriter2;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/codehaus/stax2/ri/evt/EntityDeclarationEventImpl.class */
public class EntityDeclarationEventImpl extends BaseEventImpl implements EntityDeclaration {
    protected final String mName;

    public EntityDeclarationEventImpl(Location loc, String name) {
        super(loc);
        this.mName = name;
    }

    public String getBaseURI() {
        return "";
    }

    public String getName() {
        return this.mName;
    }

    public String getNotationName() {
        return null;
    }

    public String getPublicId() {
        return null;
    }

    public String getReplacementText() {
        return null;
    }

    public String getSystemId() {
        return null;
    }

    @Override // org.codehaus.stax2.ri.evt.BaseEventImpl
    public int getEventType() {
        return 15;
    }

    @Override // org.codehaus.stax2.ri.evt.BaseEventImpl
    public void writeAsEncodedUnicode(Writer w) throws XMLStreamException {
        try {
            w.write("<!ENTITY ");
            w.write(getName());
            w.write(" \"");
            String content = getReplacementText();
            if (content != null) {
                w.write(content);
            }
            w.write("\">");
        } catch (IOException ie) {
            throwFromIOE(ie);
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.ri.evt.BaseEventImpl, org.codehaus.stax2.evt.XMLEvent2
    public void writeUsing(XMLStreamWriter2 w) throws XMLStreamException {
        StringWriter strw = new StringWriter();
        writeAsEncodedUnicode(strw);
        w.writeRaw(strw.toString());
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
        return this.mName.hashCode();
    }
}
