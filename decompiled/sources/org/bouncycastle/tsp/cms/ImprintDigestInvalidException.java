package org.bouncycastle.tsp.cms;

import org.bouncycastle.tsp.TimeStampToken;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/tsp/cms/ImprintDigestInvalidException.class */
public class ImprintDigestInvalidException extends Exception {
    private TimeStampToken token;

    public ImprintDigestInvalidException(String str, TimeStampToken timeStampToken) {
        super(str);
        this.token = timeStampToken;
    }

    public TimeStampToken getTimeStampToken() {
        return this.token;
    }
}
