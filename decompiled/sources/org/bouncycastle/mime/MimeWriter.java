package org.bouncycastle.mime;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/mime/MimeWriter.class */
public abstract class MimeWriter {
    protected final Headers headers;

    protected MimeWriter(Headers headers) {
        this.headers = headers;
    }

    public Headers getHeaders() {
        return this.headers;
    }

    public abstract OutputStream getContentStream() throws IOException;

    protected static List<String> mapToLines(Map<String, String> map) {
        ArrayList arrayList = new ArrayList(map.size());
        for (String str : map.keySet()) {
            arrayList.add(str + ": " + map.get(str));
        }
        return arrayList;
    }
}
