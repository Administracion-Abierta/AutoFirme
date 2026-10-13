package org.glassfish.json;

import javax.json.stream.JsonLocation;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/glassfish/json/JsonLocationImpl.class */
class JsonLocationImpl implements JsonLocation {
    static final JsonLocation UNKNOWN = new JsonLocationImpl(-1, -1, -1);
    private final long columnNo;
    private final long lineNo;
    private final long offset;

    JsonLocationImpl(long lineNo, long columnNo, long streamOffset) {
        this.lineNo = lineNo;
        this.columnNo = columnNo;
        this.offset = streamOffset;
    }

    @Override // javax.json.stream.JsonLocation
    public long getLineNumber() {
        return this.lineNo;
    }

    @Override // javax.json.stream.JsonLocation
    public long getColumnNumber() {
        return this.columnNo;
    }

    @Override // javax.json.stream.JsonLocation
    public long getStreamOffset() {
        return this.offset;
    }

    public String toString() {
        return "(line no=" + this.lineNo + ", column no=" + this.columnNo + ", offset=" + this.offset + ")";
    }
}
