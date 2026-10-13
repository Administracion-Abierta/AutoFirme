package org.spongycastle.dvcs;

import org.spongycastle.asn1.x509.DigestInfo;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/dvcs/MessageImprint.class */
public class MessageImprint {
    private final DigestInfo messageImprint;

    public MessageImprint(DigestInfo messageImprint) {
        this.messageImprint = messageImprint;
    }

    public DigestInfo toASN1Structure() {
        return this.messageImprint;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof MessageImprint) {
            return this.messageImprint.equals(((MessageImprint) o).messageImprint);
        }
        return false;
    }

    public int hashCode() {
        return this.messageImprint.hashCode();
    }
}
