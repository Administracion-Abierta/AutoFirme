package org.spongycastle.asn1.dvcs;

import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.ASN1TaggedObject;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.asn1.x509.GeneralName;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/asn1/dvcs/DVCSRequest.class */
public class DVCSRequest extends ASN1Object {
    private DVCSRequestInformation requestInformation;
    private Data data;
    private GeneralName transactionIdentifier;

    public DVCSRequest(DVCSRequestInformation requestInformation, Data data) {
        this(requestInformation, data, null);
    }

    public DVCSRequest(DVCSRequestInformation requestInformation, Data data, GeneralName transactionIdentifier) {
        this.requestInformation = requestInformation;
        this.data = data;
        this.transactionIdentifier = transactionIdentifier;
    }

    private DVCSRequest(ASN1Sequence seq) {
        this.requestInformation = DVCSRequestInformation.getInstance(seq.getObjectAt(0));
        this.data = Data.getInstance(seq.getObjectAt(1));
        if (seq.size() > 2) {
            this.transactionIdentifier = GeneralName.getInstance(seq.getObjectAt(2));
        }
    }

    public static DVCSRequest getInstance(Object obj) {
        if (obj instanceof DVCSRequest) {
            return (DVCSRequest) obj;
        }
        if (obj != null) {
            return new DVCSRequest(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public static DVCSRequest getInstance(ASN1TaggedObject obj, boolean explicit) {
        return getInstance(ASN1Sequence.getInstance(obj, explicit));
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(this.requestInformation);
        v.add(this.data);
        if (this.transactionIdentifier != null) {
            v.add(this.transactionIdentifier);
        }
        return new DERSequence(v);
    }

    public String toString() {
        return "DVCSRequest {\nrequestInformation: " + this.requestInformation + "\ndata: " + this.data + "\n" + (this.transactionIdentifier != null ? "transactionIdentifier: " + this.transactionIdentifier + "\n" : "") + "}\n";
    }

    public Data getData() {
        return this.data;
    }

    public DVCSRequestInformation getRequestInformation() {
        return this.requestInformation;
    }

    public GeneralName getTransactionIdentifier() {
        return this.transactionIdentifier;
    }
}
