package org.spongycastle.util.io.pem;

import java.io.IOException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/util/io/pem/PemObjectParser.class */
public interface PemObjectParser {
    Object parseObject(PemObject pemObject) throws IOException;
}
