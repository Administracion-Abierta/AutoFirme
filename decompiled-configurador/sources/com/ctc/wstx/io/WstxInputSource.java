package com.ctc.wstx.io;

import java.io.IOException;
import java.net.URL;
import javax.xml.stream.XMLStreamException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/io/WstxInputSource.class */
public abstract class WstxInputSource {
    protected final WstxInputSource mParent;
    protected final String mFromEntity;
    protected int mScopeId = 0;
    protected int mEntityDepth;

    public abstract void overrideSource(URL url);

    public abstract boolean fromInternalEntity();

    public abstract URL getSource() throws IOException;

    public abstract String getPublicId();

    public abstract String getSystemId();

    protected abstract WstxInputLocation getLocation();

    public abstract WstxInputLocation getLocation(long j, int i, int i2);

    protected abstract void doInitInputLocation(WstxInputData wstxInputData);

    public abstract int readInto(WstxInputData wstxInputData) throws XMLStreamException, IOException;

    public abstract boolean readMore(WstxInputData wstxInputData, int i) throws XMLStreamException, IOException;

    public abstract void saveContext(WstxInputData wstxInputData);

    public abstract void restoreContext(WstxInputData wstxInputData);

    public abstract void close() throws IOException;

    public abstract void closeCompletely() throws IOException;

    protected WstxInputSource(WstxInputSource parent, String fromEntity) {
        this.mParent = parent;
        this.mFromEntity = fromEntity;
    }

    public final WstxInputSource getParent() {
        return this.mParent;
    }

    public boolean isOrIsExpandedFrom(String entityId) {
        if (entityId != null) {
            WstxInputSource wstxInputSource = this;
            while (true) {
                WstxInputSource curr = wstxInputSource;
                if (curr != null) {
                    if (entityId == curr.mFromEntity) {
                        return true;
                    }
                    wstxInputSource = curr.mParent;
                } else {
                    return false;
                }
            }
        } else {
            return false;
        }
    }

    public String getEntityId() {
        return this.mFromEntity;
    }

    public int getScopeId() {
        return this.mScopeId;
    }

    public int getEntityDepth() {
        return this.mEntityDepth;
    }

    public final void initInputLocation(WstxInputData reader, int currScopeId, int entityDepth) {
        this.mScopeId = currScopeId;
        this.mEntityDepth = entityDepth;
        doInitInputLocation(reader);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(80);
        sb.append("<WstxInputSource [class ");
        sb.append(getClass().toString());
        sb.append("]; systemId: ");
        sb.append(getSystemId());
        sb.append(", source: ");
        try {
            URL url = getSource();
            sb.append(url.toString());
        } catch (IOException e) {
            sb.append("[ERROR: " + e.getMessage() + "]");
        }
        sb.append('>');
        return sb.toString();
    }
}
