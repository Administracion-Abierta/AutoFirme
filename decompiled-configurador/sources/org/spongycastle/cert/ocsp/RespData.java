package org.spongycastle.cert.ocsp;

import java.util.Date;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.ocsp.ResponseData;
import org.spongycastle.asn1.ocsp.SingleResponse;
import org.spongycastle.asn1.x509.Extensions;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/cert/ocsp/RespData.class */
public class RespData {
    private ResponseData data;

    public RespData(ResponseData data) {
        this.data = data;
    }

    public int getVersion() {
        return this.data.getVersion().getValue().intValue() + 1;
    }

    public RespID getResponderId() {
        return new RespID(this.data.getResponderID());
    }

    public Date getProducedAt() {
        return OCSPUtils.extractDate(this.data.getProducedAt());
    }

    public SingleResp[] getResponses() {
        ASN1Sequence s = this.data.getResponses();
        SingleResp[] rs = new SingleResp[s.size()];
        for (int i = 0; i != rs.length; i++) {
            rs[i] = new SingleResp(SingleResponse.getInstance(s.getObjectAt(i)));
        }
        return rs;
    }

    public Extensions getResponseExtensions() {
        return this.data.getResponseExtensions();
    }
}
