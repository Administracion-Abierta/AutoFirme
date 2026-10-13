package org.bouncycastle.tsp;

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
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.tsp.TimeStampReq;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/tsp/TimeStampRequest.class */
public class TimeStampRequest {
    private static Set EMPTY_SET = Collections.unmodifiableSet(new HashSet());
    private TimeStampReq req;
    private Extensions extensions;

    public TimeStampRequest(TimeStampReq timeStampReq) {
        this.req = timeStampReq;
        this.extensions = timeStampReq.getExtensions();
    }

    public TimeStampRequest(byte[] bArr) throws IOException {
        this(new ByteArrayInputStream(bArr));
    }

    public TimeStampRequest(InputStream inputStream) throws IOException {
        this(loadRequest(inputStream));
    }

    private static TimeStampReq loadRequest(InputStream inputStream) throws IOException {
        try {
            return TimeStampReq.getInstance(new ASN1InputStream(inputStream).readObject());
        } catch (ClassCastException e) {
            throw new IOException("malformed request: " + e);
        } catch (IllegalArgumentException e2) {
            throw new IOException("malformed request: " + e2);
        }
    }

    public int getVersion() {
        return this.req.getVersion().intValueExact();
    }

    public ASN1ObjectIdentifier getMessageImprintAlgOID() {
        return this.req.getMessageImprint().getHashAlgorithm().getAlgorithm();
    }

    public AlgorithmIdentifier getMessageImprintAlgID() {
        return this.req.getMessageImprint().getHashAlgorithm();
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

    public void validate(Set set, Set set2, Set set3) throws TSPException {
        Set setConvert = convert(set);
        Set setConvert2 = convert(set2);
        Set setConvert3 = convert(set3);
        if (!setConvert.contains(getMessageImprintAlgOID())) {
            throw new TSPValidationException("request contains unknown algorithm", 128);
        }
        if (setConvert2 != null && getReqPolicy() != null && !setConvert2.contains(getReqPolicy())) {
            throw new TSPValidationException("request contains unknown policy", 256);
        }
        if (getExtensions() != null && setConvert3 != null) {
            Enumeration enumerationOids = getExtensions().oids();
            while (enumerationOids.hasMoreElements()) {
                if (!setConvert3.contains((ASN1ObjectIdentifier) enumerationOids.nextElement())) {
                    throw new TSPValidationException("request contains unknown extension", 8388608);
                }
            }
        }
        if (TSPUtil.getDigestLength(getMessageImprintAlgOID().getId()) != getMessageImprintDigest().length) {
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

    public Extension getExtension(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        if (this.extensions != null) {
            return this.extensions.getExtension(aSN1ObjectIdentifier);
        }
        return null;
    }

    public List getExtensionOIDs() {
        return TSPUtil.getExtensionOIDs(this.extensions);
    }

    public Set getNonCriticalExtensionOIDs() {
        return this.extensions == null ? EMPTY_SET : Collections.unmodifiableSet(new HashSet(Arrays.asList(this.extensions.getNonCriticalExtensionOIDs())));
    }

    public Set getCriticalExtensionOIDs() {
        return this.extensions == null ? EMPTY_SET : Collections.unmodifiableSet(new HashSet(Arrays.asList(this.extensions.getCriticalExtensionOIDs())));
    }

    private Set convert(Set set) {
        if (set == null) {
            return set;
        }
        HashSet hashSet = new HashSet(set.size());
        for (Object obj : set) {
            if (obj instanceof String) {
                hashSet.add(new ASN1ObjectIdentifier((String) obj));
            } else {
                hashSet.add(obj);
            }
        }
        return hashSet;
    }
}
