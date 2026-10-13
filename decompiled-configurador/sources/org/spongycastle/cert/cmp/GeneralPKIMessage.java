package org.spongycastle.cert.cmp;

import java.io.IOException;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.cmp.PKIBody;
import org.spongycastle.asn1.cmp.PKIHeader;
import org.spongycastle.asn1.cmp.PKIMessage;
import org.spongycastle.cert.CertIOException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/cert/cmp/GeneralPKIMessage.class */
public class GeneralPKIMessage {
    private final PKIMessage pkiMessage;

    private static PKIMessage parseBytes(byte[] encoding) throws IOException {
        try {
            return PKIMessage.getInstance(ASN1Primitive.fromByteArray(encoding));
        } catch (ClassCastException e) {
            throw new CertIOException("malformed data: " + e.getMessage(), e);
        } catch (IllegalArgumentException e2) {
            throw new CertIOException("malformed data: " + e2.getMessage(), e2);
        }
    }

    public GeneralPKIMessage(byte[] encoding) throws IOException {
        this(parseBytes(encoding));
    }

    public GeneralPKIMessage(PKIMessage pkiMessage) {
        this.pkiMessage = pkiMessage;
    }

    public PKIHeader getHeader() {
        return this.pkiMessage.getHeader();
    }

    public PKIBody getBody() {
        return this.pkiMessage.getBody();
    }

    public boolean hasProtection() {
        return this.pkiMessage.getHeader().getProtectionAlg() != null;
    }

    public PKIMessage toASN1Structure() {
        return this.pkiMessage;
    }
}
