package org.spongycastle.tsp.cms;

import org.spongycastle.tsp.TimeStampToken;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/tsp/cms/ImprintDigestInvalidException.class */
public class ImprintDigestInvalidException extends Exception {
    private TimeStampToken token;

    public ImprintDigestInvalidException(String message, TimeStampToken token) {
        super(message);
        this.token = token;
    }

    public TimeStampToken getTimeStampToken() {
        return this.token;
    }
}
