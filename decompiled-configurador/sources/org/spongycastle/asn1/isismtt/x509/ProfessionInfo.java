package org.spongycastle.asn1.isismtt.x509;

import java.util.Enumeration;
import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.ASN1OctetString;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.ASN1TaggedObject;
import org.spongycastle.asn1.DEROctetString;
import org.spongycastle.asn1.DERPrintableString;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.asn1.DERTaggedObject;
import org.spongycastle.asn1.x500.DirectoryString;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/asn1/isismtt/x509/ProfessionInfo.class */
public class ProfessionInfo extends ASN1Object {
    public static final ASN1ObjectIdentifier Rechtsanwltin = new ASN1ObjectIdentifier(NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern + ".1");
    public static final ASN1ObjectIdentifier Rechtsanwalt = new ASN1ObjectIdentifier(NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern + ".2");
    public static final ASN1ObjectIdentifier Rechtsbeistand = new ASN1ObjectIdentifier(NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern + ".3");
    public static final ASN1ObjectIdentifier Steuerberaterin = new ASN1ObjectIdentifier(NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern + ".4");
    public static final ASN1ObjectIdentifier Steuerberater = new ASN1ObjectIdentifier(NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern + ".5");
    public static final ASN1ObjectIdentifier Steuerbevollmchtigte = new ASN1ObjectIdentifier(NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern + ".6");
    public static final ASN1ObjectIdentifier Steuerbevollmchtigter = new ASN1ObjectIdentifier(NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern + ".7");
    public static final ASN1ObjectIdentifier Notarin = new ASN1ObjectIdentifier(NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern + ".8");
    public static final ASN1ObjectIdentifier Notar = new ASN1ObjectIdentifier(NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern + ".9");
    public static final ASN1ObjectIdentifier Notarvertreterin = new ASN1ObjectIdentifier(NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern + ".10");
    public static final ASN1ObjectIdentifier Notarvertreter = new ASN1ObjectIdentifier(NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern + ".11");
    public static final ASN1ObjectIdentifier Notariatsverwalterin = new ASN1ObjectIdentifier(NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern + ".12");
    public static final ASN1ObjectIdentifier Notariatsverwalter = new ASN1ObjectIdentifier(NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern + ".13");
    public static final ASN1ObjectIdentifier Wirtschaftsprferin = new ASN1ObjectIdentifier(NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern + ".14");
    public static final ASN1ObjectIdentifier Wirtschaftsprfer = new ASN1ObjectIdentifier(NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern + ".15");
    public static final ASN1ObjectIdentifier VereidigteBuchprferin = new ASN1ObjectIdentifier(NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern + ".16");
    public static final ASN1ObjectIdentifier VereidigterBuchprfer = new ASN1ObjectIdentifier(NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern + ".17");
    public static final ASN1ObjectIdentifier Patentanwltin = new ASN1ObjectIdentifier(NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern + ".18");
    public static final ASN1ObjectIdentifier Patentanwalt = new ASN1ObjectIdentifier(NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern + ".19");
    private NamingAuthority namingAuthority;
    private ASN1Sequence professionItems;
    private ASN1Sequence professionOIDs;
    private String registrationNumber;
    private ASN1OctetString addProfessionInfo;

    public static ProfessionInfo getInstance(Object obj) {
        if (obj == null || (obj instanceof ProfessionInfo)) {
            return (ProfessionInfo) obj;
        }
        if (obj instanceof ASN1Sequence) {
            return new ProfessionInfo((ASN1Sequence) obj);
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + obj.getClass().getName());
    }

    private ProfessionInfo(ASN1Sequence seq) {
        if (seq.size() > 5) {
            throw new IllegalArgumentException("Bad sequence size: " + seq.size());
        }
        Enumeration e = seq.getObjects();
        ASN1Encodable o = (ASN1Encodable) e.nextElement();
        if (o instanceof ASN1TaggedObject) {
            if (((ASN1TaggedObject) o).getTagNo() != 0) {
                throw new IllegalArgumentException("Bad tag number: " + ((ASN1TaggedObject) o).getTagNo());
            }
            this.namingAuthority = NamingAuthority.getInstance((ASN1TaggedObject) o, true);
            o = (ASN1Encodable) e.nextElement();
        }
        this.professionItems = ASN1Sequence.getInstance(o);
        if (e.hasMoreElements()) {
            ASN1Encodable o2 = (ASN1Encodable) e.nextElement();
            if (o2 instanceof ASN1Sequence) {
                this.professionOIDs = ASN1Sequence.getInstance(o2);
            } else if (o2 instanceof DERPrintableString) {
                this.registrationNumber = DERPrintableString.getInstance(o2).getString();
            } else if (o2 instanceof ASN1OctetString) {
                this.addProfessionInfo = ASN1OctetString.getInstance(o2);
            } else {
                throw new IllegalArgumentException("Bad object encountered: " + o2.getClass());
            }
        }
        if (e.hasMoreElements()) {
            ASN1Encodable o3 = (ASN1Encodable) e.nextElement();
            if (o3 instanceof DERPrintableString) {
                this.registrationNumber = DERPrintableString.getInstance(o3).getString();
            } else if (o3 instanceof DEROctetString) {
                this.addProfessionInfo = (DEROctetString) o3;
            } else {
                throw new IllegalArgumentException("Bad object encountered: " + o3.getClass());
            }
        }
        if (e.hasMoreElements()) {
            ASN1Encodable o4 = (ASN1Encodable) e.nextElement();
            if (o4 instanceof DEROctetString) {
                this.addProfessionInfo = (DEROctetString) o4;
                return;
            }
            throw new IllegalArgumentException("Bad object encountered: " + o4.getClass());
        }
    }

    public ProfessionInfo(NamingAuthority namingAuthority, DirectoryString[] professionItems, ASN1ObjectIdentifier[] professionOIDs, String registrationNumber, ASN1OctetString addProfessionInfo) {
        this.namingAuthority = namingAuthority;
        ASN1EncodableVector v = new ASN1EncodableVector();
        for (int i = 0; i != professionItems.length; i++) {
            v.add(professionItems[i]);
        }
        this.professionItems = new DERSequence(v);
        if (professionOIDs != null) {
            ASN1EncodableVector v2 = new ASN1EncodableVector();
            for (int i2 = 0; i2 != professionOIDs.length; i2++) {
                v2.add(professionOIDs[i2]);
            }
            this.professionOIDs = new DERSequence(v2);
        }
        this.registrationNumber = registrationNumber;
        this.addProfessionInfo = addProfessionInfo;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector vec = new ASN1EncodableVector();
        if (this.namingAuthority != null) {
            vec.add(new DERTaggedObject(true, 0, this.namingAuthority));
        }
        vec.add(this.professionItems);
        if (this.professionOIDs != null) {
            vec.add(this.professionOIDs);
        }
        if (this.registrationNumber != null) {
            vec.add(new DERPrintableString(this.registrationNumber, true));
        }
        if (this.addProfessionInfo != null) {
            vec.add(this.addProfessionInfo);
        }
        return new DERSequence(vec);
    }

    public ASN1OctetString getAddProfessionInfo() {
        return this.addProfessionInfo;
    }

    public NamingAuthority getNamingAuthority() {
        return this.namingAuthority;
    }

    public DirectoryString[] getProfessionItems() {
        DirectoryString[] items = new DirectoryString[this.professionItems.size()];
        int count = 0;
        Enumeration e = this.professionItems.getObjects();
        while (e.hasMoreElements()) {
            int i = count;
            count++;
            items[i] = DirectoryString.getInstance(e.nextElement());
        }
        return items;
    }

    public ASN1ObjectIdentifier[] getProfessionOIDs() {
        if (this.professionOIDs == null) {
            return new ASN1ObjectIdentifier[0];
        }
        ASN1ObjectIdentifier[] oids = new ASN1ObjectIdentifier[this.professionOIDs.size()];
        int count = 0;
        Enumeration e = this.professionOIDs.getObjects();
        while (e.hasMoreElements()) {
            int i = count;
            count++;
            oids[i] = ASN1ObjectIdentifier.getInstance(e.nextElement());
        }
        return oids;
    }

    public String getRegistrationNumber() {
        return this.registrationNumber;
    }
}
