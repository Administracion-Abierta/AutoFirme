package org.spongycastle.dvcs;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.spongycastle.asn1.dvcs.CertEtcToken;
import org.spongycastle.asn1.dvcs.DVCSRequestInformationBuilder;
import org.spongycastle.asn1.dvcs.DVCSTime;
import org.spongycastle.asn1.dvcs.Data;
import org.spongycastle.asn1.dvcs.ServiceType;
import org.spongycastle.asn1.dvcs.TargetEtcChain;
import org.spongycastle.asn1.x509.Extension;
import org.spongycastle.cert.X509CertificateHolder;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/dvcs/VPKCRequestBuilder.class */
public class VPKCRequestBuilder extends DVCSRequestBuilder {
    private List chains;

    public VPKCRequestBuilder() {
        super(new DVCSRequestInformationBuilder(ServiceType.VPKC));
        this.chains = new ArrayList();
    }

    public void addTargetChain(X509CertificateHolder cert) {
        this.chains.add(new TargetEtcChain(new CertEtcToken(0, cert.toASN1Structure())));
    }

    public void addTargetChain(Extension extension) {
        this.chains.add(new TargetEtcChain(new CertEtcToken(extension)));
    }

    public void addTargetChain(TargetChain targetChain) {
        this.chains.add(targetChain.toASN1Structure());
    }

    public void setRequestTime(Date requestTime) {
        this.requestInformationBuilder.setRequestTime(new DVCSTime(requestTime));
    }

    public DVCSRequest build() throws DVCSException {
        Data data = new Data((TargetEtcChain[]) this.chains.toArray(new TargetEtcChain[this.chains.size()]));
        return createDVCRequest(data);
    }
}
