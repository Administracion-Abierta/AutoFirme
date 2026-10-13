package org.spongycastle.openssl;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.ASN1TaggedObject;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.asn1.DERTaggedObject;
import org.spongycastle.asn1.DERUTF8String;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/openssl/CertificateTrustBlock.class */
public class CertificateTrustBlock {
    private ASN1Sequence uses;
    private ASN1Sequence prohibitions;
    private String alias;

    public CertificateTrustBlock(Set<ASN1ObjectIdentifier> uses) {
        this(null, uses, null);
    }

    public CertificateTrustBlock(String alias, Set<ASN1ObjectIdentifier> uses) {
        this(alias, uses, null);
    }

    public CertificateTrustBlock(String alias, Set<ASN1ObjectIdentifier> uses, Set<ASN1ObjectIdentifier> prohibitions) {
        this.alias = alias;
        this.uses = toSequence(uses);
        this.prohibitions = toSequence(prohibitions);
    }

    CertificateTrustBlock(byte[] encoded) {
        ASN1Sequence seq = ASN1Sequence.getInstance(encoded);
        Enumeration en = seq.getObjects();
        while (en.hasMoreElements()) {
            ASN1Encodable obj = (ASN1Encodable) en.nextElement();
            if (obj instanceof ASN1Sequence) {
                this.uses = ASN1Sequence.getInstance(obj);
            } else if (obj instanceof ASN1TaggedObject) {
                this.prohibitions = ASN1Sequence.getInstance((ASN1TaggedObject) obj, false);
            } else if (obj instanceof DERUTF8String) {
                this.alias = DERUTF8String.getInstance(obj).getString();
            }
        }
    }

    public String getAlias() {
        return this.alias;
    }

    public Set<ASN1ObjectIdentifier> getUses() {
        return toSet(this.uses);
    }

    public Set<ASN1ObjectIdentifier> getProhibitions() {
        return toSet(this.prohibitions);
    }

    private Set<ASN1ObjectIdentifier> toSet(ASN1Sequence seq) {
        if (seq != null) {
            Set<ASN1ObjectIdentifier> oids = new HashSet<>(seq.size());
            Enumeration en = seq.getObjects();
            while (en.hasMoreElements()) {
                oids.add(ASN1ObjectIdentifier.getInstance(en.nextElement()));
            }
            return oids;
        }
        return Collections.EMPTY_SET;
    }

    private ASN1Sequence toSequence(Set<ASN1ObjectIdentifier> oids) {
        if (oids == null || oids.isEmpty()) {
            return null;
        }
        ASN1EncodableVector v = new ASN1EncodableVector();
        Iterator<ASN1ObjectIdentifier> it = oids.iterator();
        while (it.hasNext()) {
            v.add(it.next());
        }
        return new DERSequence(v);
    }

    ASN1Sequence toASN1Sequence() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        if (this.uses != null) {
            v.add(this.uses);
        }
        if (this.prohibitions != null) {
            v.add(new DERTaggedObject(false, 0, this.prohibitions));
        }
        if (this.alias != null) {
            v.add(new DERUTF8String(this.alias));
        }
        return new DERSequence(v);
    }
}
