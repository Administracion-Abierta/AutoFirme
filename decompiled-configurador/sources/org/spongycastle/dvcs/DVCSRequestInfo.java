package org.spongycastle.dvcs;

import java.math.BigInteger;
import java.util.Date;
import org.spongycastle.asn1.dvcs.DVCSRequestInformation;
import org.spongycastle.asn1.dvcs.DVCSTime;
import org.spongycastle.asn1.x509.GeneralNames;
import org.spongycastle.asn1.x509.PolicyInformation;
import org.spongycastle.tsp.TimeStampToken;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/dvcs/DVCSRequestInfo.class */
public class DVCSRequestInfo {
    private DVCSRequestInformation data;

    public DVCSRequestInfo(byte[] in) {
        this(DVCSRequestInformation.getInstance(in));
    }

    public DVCSRequestInfo(DVCSRequestInformation data) {
        this.data = data;
    }

    public DVCSRequestInformation toASN1Structure() {
        return this.data;
    }

    public int getVersion() {
        return this.data.getVersion();
    }

    public int getServiceType() {
        return this.data.getService().getValue().intValue();
    }

    public BigInteger getNonce() {
        return this.data.getNonce();
    }

    public Date getRequestTime() throws DVCSParsingException {
        DVCSTime time = this.data.getRequestTime();
        if (time == null) {
            return null;
        }
        try {
            if (time.getGenTime() != null) {
                return time.getGenTime().getDate();
            }
            TimeStampToken token = new TimeStampToken(time.getTimeStampToken());
            return token.getTimeStampInfo().getGenTime();
        } catch (Exception e) {
            throw new DVCSParsingException("unable to extract time: " + e.getMessage(), e);
        }
    }

    public GeneralNames getRequester() {
        return this.data.getRequester();
    }

    public PolicyInformation getRequestPolicy() {
        if (this.data.getRequestPolicy() != null) {
            return this.data.getRequestPolicy();
        }
        return null;
    }

    public GeneralNames getDVCSNames() {
        return this.data.getDVCS();
    }

    public GeneralNames getDataLocations() {
        return this.data.getDataLocations();
    }

    public static boolean validate(DVCSRequestInfo requestInfo, DVCSRequestInfo responseInfo) {
        DVCSRequestInformation clientInfo = requestInfo.data;
        DVCSRequestInformation serverInfo = responseInfo.data;
        if (clientInfo.getVersion() != serverInfo.getVersion() || !clientEqualsServer(clientInfo.getService(), serverInfo.getService()) || !clientEqualsServer(clientInfo.getRequestTime(), serverInfo.getRequestTime()) || !clientEqualsServer(clientInfo.getRequestPolicy(), serverInfo.getRequestPolicy()) || !clientEqualsServer(clientInfo.getExtensions(), serverInfo.getExtensions())) {
            return false;
        }
        if (clientInfo.getNonce() != null) {
            if (serverInfo.getNonce() == null) {
                return false;
            }
            byte[] clientNonce = clientInfo.getNonce().toByteArray();
            byte[] serverNonce = serverInfo.getNonce().toByteArray();
            if (serverNonce.length < clientNonce.length || !Arrays.areEqual(clientNonce, Arrays.copyOfRange(serverNonce, 0, clientNonce.length))) {
                return false;
            }
            return true;
        }
        return true;
    }

    private static boolean clientEqualsServer(Object client, Object server) {
        return (client == null && server == null) || (client != null && client.equals(server));
    }
}
