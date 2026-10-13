package org.spongycastle.crypto.tls;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;
import org.spongycastle.asn1.ASN1Encoding;
import org.spongycastle.asn1.ocsp.ResponderID;
import org.spongycastle.asn1.x509.Extensions;
import org.spongycastle.util.io.Streams;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/tls/OCSPStatusRequest.class */
public class OCSPStatusRequest {
    protected Vector responderIDList;
    protected Extensions requestExtensions;

    public OCSPStatusRequest(Vector responderIDList, Extensions requestExtensions) {
        this.responderIDList = responderIDList;
        this.requestExtensions = requestExtensions;
    }

    public Vector getResponderIDList() {
        return this.responderIDList;
    }

    public Extensions getRequestExtensions() {
        return this.requestExtensions;
    }

    public void encode(OutputStream output) throws IOException {
        if (this.responderIDList == null || this.responderIDList.isEmpty()) {
            TlsUtils.writeUint16(0, output);
        } else {
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            for (int i = 0; i < this.responderIDList.size(); i++) {
                ResponderID responderID = (ResponderID) this.responderIDList.elementAt(i);
                TlsUtils.writeOpaque16(responderID.getEncoded(ASN1Encoding.DER), buf);
            }
            TlsUtils.checkUint16(buf.size());
            TlsUtils.writeUint16(buf.size(), output);
            Streams.writeBufTo(buf, output);
        }
        if (this.requestExtensions == null) {
            TlsUtils.writeUint16(0, output);
            return;
        }
        byte[] derEncoding = this.requestExtensions.getEncoded(ASN1Encoding.DER);
        TlsUtils.checkUint16(derEncoding.length);
        TlsUtils.writeUint16(derEncoding.length, output);
        output.write(derEncoding);
    }

    public static OCSPStatusRequest parse(InputStream input) throws IOException {
        Vector responderIDList = new Vector();
        int length = TlsUtils.readUint16(input);
        if (length > 0) {
            byte[] data = TlsUtils.readFully(length, input);
            ByteArrayInputStream buf = new ByteArrayInputStream(data);
            do {
                byte[] derEncoding = TlsUtils.readOpaque16(buf);
                ResponderID responderID = ResponderID.getInstance(TlsUtils.readDERObject(derEncoding));
                responderIDList.addElement(responderID);
            } while (buf.available() > 0);
        }
        Extensions requestExtensions = null;
        int length2 = TlsUtils.readUint16(input);
        if (length2 > 0) {
            byte[] derEncoding2 = TlsUtils.readFully(length2, input);
            requestExtensions = Extensions.getInstance(TlsUtils.readDERObject(derEncoding2));
        }
        return new OCSPStatusRequest(responderIDList, requestExtensions);
    }
}
