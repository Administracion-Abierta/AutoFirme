package org.codehaus.stax2.ri.evt;

import java.io.IOException;
import java.io.Writer;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Comment;
import org.codehaus.stax2.XMLStreamWriter2;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/codehaus/stax2/ri/evt/CommentEventImpl.class */
public class CommentEventImpl extends BaseEventImpl implements Comment {
    final String mContent;

    public CommentEventImpl(Location loc, String content) {
        super(loc);
        this.mContent = content;
    }

    public String getText() {
        return this.mContent;
    }

    @Override // org.codehaus.stax2.ri.evt.BaseEventImpl
    public int getEventType() {
        return 5;
    }

    @Override // org.codehaus.stax2.ri.evt.BaseEventImpl
    public void writeAsEncodedUnicode(Writer w) throws XMLStreamException {
        try {
            w.write("<!--");
            w.write(this.mContent);
            w.write("-->");
        } catch (IOException ie) {
            throwFromIOE(ie);
        }
    }

    @Override // org.codehaus.stax2.ri.evt.BaseEventImpl, org.codehaus.stax2.evt.XMLEvent2
    public void writeUsing(XMLStreamWriter2 w) throws XMLStreamException {
        w.writeComment(this.mContent);
    }

    @Override // org.codehaus.stax2.ri.evt.BaseEventImpl
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null || !(o instanceof Comment)) {
            return false;
        }
        Comment other = (Comment) o;
        return this.mContent.equals(other.getText());
    }

    @Override // org.codehaus.stax2.ri.evt.BaseEventImpl
    public int hashCode() {
        return this.mContent.hashCode();
    }
}
