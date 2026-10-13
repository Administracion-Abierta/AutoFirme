package org.codehaus.stax2.ri.evt;

import java.io.IOException;
import java.io.Writer;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.ProcessingInstruction;
import org.codehaus.stax2.XMLStreamWriter2;

/* JADX INFO: loaded from: autofirma.jar:org/codehaus/stax2/ri/evt/ProcInstrEventImpl.class */
public class ProcInstrEventImpl extends BaseEventImpl implements ProcessingInstruction {
    final String mTarget;
    final String mData;

    public ProcInstrEventImpl(Location loc, String target, String data) {
        super(loc);
        this.mTarget = target;
        this.mData = data;
    }

    public String getData() {
        return this.mData;
    }

    public String getTarget() {
        return this.mTarget;
    }

    @Override // org.codehaus.stax2.ri.evt.BaseEventImpl
    public int getEventType() {
        return 3;
    }

    @Override // org.codehaus.stax2.ri.evt.BaseEventImpl
    public boolean isProcessingInstruction() {
        return true;
    }

    @Override // org.codehaus.stax2.ri.evt.BaseEventImpl
    public void writeAsEncodedUnicode(Writer w) throws XMLStreamException {
        try {
            w.write("<?");
            w.write(this.mTarget);
            if (this.mData != null && this.mData.length() > 0) {
                w.write(this.mData);
            }
            w.write("?>");
        } catch (IOException ie) {
            throwFromIOE(ie);
        }
    }

    @Override // org.codehaus.stax2.ri.evt.BaseEventImpl, org.codehaus.stax2.evt.XMLEvent2
    public void writeUsing(XMLStreamWriter2 w) throws XMLStreamException {
        if (this.mData != null && this.mData.length() > 0) {
            w.writeProcessingInstruction(this.mTarget, this.mData);
        } else {
            w.writeProcessingInstruction(this.mTarget);
        }
    }

    @Override // org.codehaus.stax2.ri.evt.BaseEventImpl
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null || !(o instanceof ProcessingInstruction)) {
            return false;
        }
        ProcessingInstruction other = (ProcessingInstruction) o;
        return this.mTarget.equals(other.getTarget()) && stringsWithNullsEqual(this.mData, other.getData());
    }

    @Override // org.codehaus.stax2.ri.evt.BaseEventImpl
    public int hashCode() {
        int hash = this.mTarget.hashCode();
        if (this.mData != null) {
            hash ^= this.mData.hashCode();
        }
        return hash;
    }
}
