package org.spongycastle.tsp;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.spongycastle.asn1.ASN1InputStream;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.tsp.TimeStampReq;
import org.spongycastle.asn1.x509.Extension;
import org.spongycastle.asn1.x509.Extensions;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/tsp/TimeStampRequest.class */
public class TimeStampRequest {
    private static Set EMPTY_SET = Collections.unmodifiableSet(new HashSet());
    private TimeStampReq req;
    private Extensions extensions;

    public TimeStampRequest(TimeStampReq req) {
        this.req = req;
        this.extensions = req.getExtensions();
    }

    public TimeStampRequest(byte[] req) throws IOException {
        this(new ByteArrayInputStream(req));
    }

    public TimeStampRequest(InputStream in) throws IOException {
        this(loadRequest(in));
    }

    private static TimeStampReq loadRequest(InputStream in) throws IOException {
        try {
            return TimeStampReq.getInstance(new ASN1InputStream(in).readObject());
        } catch (ClassCastException e) {
            throw new IOException("malformed request: " + e);
        } catch (IllegalArgumentException e2) {
            throw new IOException("malformed request: " + e2);
        }
    }

    public int getVersion() {
        return this.req.getVersion().getValue().intValue();
    }

    public ASN1ObjectIdentifier getMessageImprintAlgOID() {
        return this.req.getMessageImprint().getHashAlgorithm().getAlgorithm();
    }

    public byte[] getMessageImprintDigest() {
        return this.req.getMessageImprint().getHashedMessage();
    }

    public ASN1ObjectIdentifier getReqPolicy() {
        if (this.req.getReqPolicy() != null) {
            return this.req.getReqPolicy();
        }
        return null;
    }

    public BigInteger getNonce() {
        if (this.req.getNonce() != null) {
            return this.req.getNonce().getValue();
        }
        return null;
    }

    public boolean getCertReq() {
        if (this.req.getCertReq() != null) {
            return this.req.getCertReq().isTrue();
        }
        return false;
    }

    public void validate(Set algorithms, Set policies, Set extensions) throws TSPException {
        Set algorithms2 = convert(algorithms);
        Set policies2 = convert(policies);
        Set extensions2 = convert(extensions);
        if (!algorithms2.contains(getMessageImprintAlgOID())) {
            throw new TSPValidationException("request contains unknown algorithm", 128);
        }
        if (policies2 != null && getReqPolicy() != null && !policies2.contains(getReqPolicy())) {
            throw new TSPValidationException("request contains unknown policy", 256);
        }
        if (getExtensions() != null && extensions2 != null) {
            Enumeration en = getExtensions().oids();
            while (en.hasMoreElements()) {
                ASN1ObjectIdentifier oid = (ASN1ObjectIdentifier) en.nextElement();
                if (!extensions2.contains(oid)) {
                    throw new TSPValidationException("request contains unknown extension", 8388608);
                }
            }
        }
        int digestLength = TSPUtil.getDigestLength(getMessageImprintAlgOID().getId());
        if (digestLength != getMessageImprintDigest().length) {
            throw new TSPValidationException("imprint digest the wrong length", 4);
        }
    }

    public byte[] getEncoded() throws IOException {
        return this.req.getEncoded();
    }

    Extensions getExtensions() {
        return this.extensions;
    }

    public boolean hasExtensions() {
        return this.extensions != null;
    }

    public Extension getExtension(ASN1ObjectIdentifier oid) {
        if (this.extensions != null) {
            return this.extensions.getExtension(oid);
        }
        return null;
    }

    public List getExtensionOIDs() {
        return TSPUtil.getExtensionOIDs(this.extensions);
    }

    public Set getNonCriticalExtensionOIDs() {
        if (this.extensions == null) {
            return EMPTY_SET;
        }
        return Collections.unmodifiableSet(new HashSet(Arrays.asList(this.extensions.getNonCriticalExtensionOIDs())));
    }

    public Set getCriticalExtensionOIDs() {
        if (this.extensions == null) {
            return EMPTY_SET;
        }
        return Collections.unmodifiableSet(new HashSet(Arrays.asList(this.extensions.getCriticalExtensionOIDs())));
    }

    private Set convert(Set orig) {
        if (orig == null) {
            return orig;
        }
        Set con = new HashSet(orig.size());
        for (Object o : orig) {
            if (o instanceof String) {
                con.add(new ASN1ObjectIdentifier((String) o));
            } else {
                con.add(o);
            }
        }
        return con;
    }
}
