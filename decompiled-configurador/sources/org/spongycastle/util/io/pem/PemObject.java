package org.spongycastle.util.io.pem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/util/io/pem/PemObject.class */
public class PemObject implements PemObjectGenerator {
    private static final List EMPTY_LIST = Collections.unmodifiableList(new ArrayList());
    private String type;
    private List headers;
    private byte[] content;

    public PemObject(String type, byte[] content) {
        this(type, EMPTY_LIST, content);
    }

    public PemObject(String type, List headers, byte[] content) {
        this.type = type;
        this.headers = Collections.unmodifiableList(headers);
        this.content = content;
    }

    public String getType() {
        return this.type;
    }

    public List getHeaders() {
        return this.headers;
    }

    public byte[] getContent() {
        return this.content;
    }

    @Override // org.spongycastle.util.io.pem.PemObjectGenerator
    public PemObject generate() throws PemGenerationException {
        return this;
    }
}
