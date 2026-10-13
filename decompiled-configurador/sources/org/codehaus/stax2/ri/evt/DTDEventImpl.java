package org.codehaus.stax2.ri.evt;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.DTD;
import javax.xml.stream.events.EntityDeclaration;
import javax.xml.stream.events.NotationDeclaration;
import org.codehaus.stax2.XMLStreamWriter2;
import org.codehaus.stax2.evt.DTD2;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/codehaus/stax2/ri/evt/DTDEventImpl.class */
public class DTDEventImpl extends BaseEventImpl implements DTD2 {
    protected final String mRootName;
    protected final String mSystemId;
    protected final String mPublicId;
    protected final String mInternalSubset;
    protected final Object mDTD;
    protected String mFullText;

    public DTDEventImpl(Location loc, String rootName, String sysId, String pubId, String intSubset, Object dtd) {
        super(loc);
        this.mFullText = null;
        this.mRootName = rootName;
        this.mSystemId = sysId;
        this.mPublicId = pubId;
        this.mInternalSubset = intSubset;
        this.mFullText = null;
        this.mDTD = dtd;
    }

    public DTDEventImpl(Location loc, String rootName, String intSubset) {
        this(loc, rootName, null, null, intSubset, null);
    }

    public DTDEventImpl(Location loc, String fullText) {
        this(loc, null, null, null, null, null);
        this.mFullText = fullText;
    }

    public String getDocumentTypeDeclaration() {
        try {
            return doGetDocumentTypeDeclaration();
        } catch (XMLStreamException sex) {
            throw new RuntimeException("Internal error: " + sex);
        }
    }

    public List<EntityDeclaration> getEntities() {
        return null;
    }

    public List<NotationDeclaration> getNotations() {
        return null;
    }

    public Object getProcessedDTD() {
        return this.mDTD;
    }

    @Override // org.codehaus.stax2.ri.evt.BaseEventImpl
    public int getEventType() {
        return 11;
    }

    @Override // org.codehaus.stax2.ri.evt.BaseEventImpl
    public void writeAsEncodedUnicode(Writer w) throws XMLStreamException {
        try {
            if (this.mFullText != null) {
                w.write(this.mFullText);
                return;
            }
            w.write("<!DOCTYPE");
            if (this.mRootName != null) {
                w.write(32);
                w.write(this.mRootName);
            }
            if (this.mSystemId != null) {
                if (this.mPublicId != null) {
                    w.write(" PUBLIC \"");
                    w.write(this.mPublicId);
                    w.write(34);
                } else {
                    w.write(" SYSTEM");
                }
                w.write(" \"");
                w.write(this.mSystemId);
                w.write(34);
            }
            if (this.mInternalSubset != null) {
                w.write(" [");
                w.write(this.mInternalSubset);
                w.write(93);
            }
            w.write(">");
        } catch (IOException ie) {
            throwFromIOE(ie);
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.ri.evt.BaseEventImpl, org.codehaus.stax2.evt.XMLEvent2
    public void writeUsing(XMLStreamWriter2 w) throws XMLStreamException {
        if (this.mRootName != null) {
            w.writeDTD(this.mRootName, this.mSystemId, this.mPublicId, this.mInternalSubset);
        } else {
            w.writeDTD(doGetDocumentTypeDeclaration());
        }
    }

    @Override // org.codehaus.stax2.evt.DTD2
    public String getRootName() {
        return this.mRootName;
    }

    @Override // org.codehaus.stax2.evt.DTD2
    public String getSystemId() {
        return this.mSystemId;
    }

    @Override // org.codehaus.stax2.evt.DTD2
    public String getPublicId() {
        return this.mPublicId;
    }

    @Override // org.codehaus.stax2.evt.DTD2
    public String getInternalSubset() {
        return this.mInternalSubset;
    }

    @Override // org.codehaus.stax2.ri.evt.BaseEventImpl
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null || !(o instanceof DTD)) {
            return false;
        }
        DTD other = (DTD) o;
        return stringsWithNullsEqual(getDocumentTypeDeclaration(), other.getDocumentTypeDeclaration());
    }

    @Override // org.codehaus.stax2.ri.evt.BaseEventImpl
    public int hashCode() {
        int hash = 0;
        if (this.mRootName != null) {
            hash = 0 ^ this.mRootName.hashCode();
        }
        if (this.mSystemId != null) {
            hash ^= this.mSystemId.hashCode();
        }
        if (this.mPublicId != null) {
            hash ^= this.mPublicId.hashCode();
        }
        if (this.mInternalSubset != null) {
            hash ^= this.mInternalSubset.hashCode();
        }
        if (this.mDTD != null) {
            hash ^= this.mDTD.hashCode();
        }
        if (hash == 0 && this.mFullText != null) {
            hash ^= this.mFullText.hashCode();
        }
        return hash;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected String doGetDocumentTypeDeclaration() throws XMLStreamException {
        if (this.mFullText == null) {
            int len = 60;
            if (this.mInternalSubset != null) {
                len = 60 + this.mInternalSubset.length() + 4;
            }
            StringWriter sw = new StringWriter(len);
            writeAsEncodedUnicode(sw);
            this.mFullText = sw.toString();
        }
        return this.mFullText;
    }
}
