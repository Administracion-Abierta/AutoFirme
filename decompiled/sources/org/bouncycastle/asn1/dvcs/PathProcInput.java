package org.bouncycastle.asn1.dvcs;

import java.util.Arrays;
import org.bouncycastle.asn1.ASN1Boolean;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.PolicyInformation;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/dvcs/PathProcInput.class */
public class PathProcInput extends ASN1Object {
    private PolicyInformation[] acceptablePolicySet;
    private boolean inhibitPolicyMapping;
    private boolean explicitPolicyReqd;
    private boolean inhibitAnyPolicy;

    public PathProcInput(PolicyInformation[] policyInformationArr) {
        this.inhibitPolicyMapping = false;
        this.explicitPolicyReqd = false;
        this.inhibitAnyPolicy = false;
        this.acceptablePolicySet = copy(policyInformationArr);
    }

    public PathProcInput(PolicyInformation[] policyInformationArr, boolean z, boolean z2, boolean z3) {
        this.inhibitPolicyMapping = false;
        this.explicitPolicyReqd = false;
        this.inhibitAnyPolicy = false;
        this.acceptablePolicySet = copy(policyInformationArr);
        this.inhibitPolicyMapping = z;
        this.explicitPolicyReqd = z2;
        this.inhibitAnyPolicy = z3;
    }

    private static PolicyInformation[] fromSequence(ASN1Sequence aSN1Sequence) {
        PolicyInformation[] policyInformationArr = new PolicyInformation[aSN1Sequence.size()];
        for (int i = 0; i != policyInformationArr.length; i++) {
            policyInformationArr[i] = PolicyInformation.getInstance(aSN1Sequence.getObjectAt(i));
        }
        return policyInformationArr;
    }

    public static PathProcInput getInstance(Object obj) {
        if (obj instanceof PathProcInput) {
            return (PathProcInput) obj;
        }
        if (obj == null) {
            return null;
        }
        ASN1Sequence aSN1Sequence = ASN1Sequence.getInstance(obj);
        PathProcInput pathProcInput = new PathProcInput(fromSequence(ASN1Sequence.getInstance(aSN1Sequence.getObjectAt(0))));
        for (int i = 1; i < aSN1Sequence.size(); i++) {
            ASN1Encodable objectAt = aSN1Sequence.getObjectAt(i);
            if (objectAt instanceof ASN1Boolean) {
                pathProcInput.setInhibitPolicyMapping(ASN1Boolean.getInstance(objectAt).isTrue());
            } else if (objectAt instanceof ASN1TaggedObject) {
                ASN1TaggedObject aSN1TaggedObject = ASN1TaggedObject.getInstance(objectAt);
                switch (aSN1TaggedObject.getTagNo()) {
                    case 0:
                        pathProcInput.setExplicitPolicyReqd(ASN1Boolean.getInstance(aSN1TaggedObject, false).isTrue());
                        break;
                    case 1:
                        pathProcInput.setInhibitAnyPolicy(ASN1Boolean.getInstance(aSN1TaggedObject, false).isTrue());
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown tag encountered: " + aSN1TaggedObject.getTagNo());
                }
            } else {
                continue;
            }
        }
        return pathProcInput;
    }

    public static PathProcInput getInstance(ASN1TaggedObject aSN1TaggedObject, boolean z) {
        return getInstance(ASN1Sequence.getInstance(aSN1TaggedObject, z));
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector(4);
        ASN1EncodableVector aSN1EncodableVector2 = new ASN1EncodableVector(this.acceptablePolicySet.length);
        for (int i = 0; i != this.acceptablePolicySet.length; i++) {
            aSN1EncodableVector2.add(this.acceptablePolicySet[i]);
        }
        aSN1EncodableVector.add(new DERSequence(aSN1EncodableVector2));
        if (this.inhibitPolicyMapping) {
            aSN1EncodableVector.add(ASN1Boolean.getInstance(this.inhibitPolicyMapping));
        }
        if (this.explicitPolicyReqd) {
            aSN1EncodableVector.add(new DERTaggedObject(false, 0, (ASN1Encodable) ASN1Boolean.getInstance(this.explicitPolicyReqd)));
        }
        if (this.inhibitAnyPolicy) {
            aSN1EncodableVector.add(new DERTaggedObject(false, 1, (ASN1Encodable) ASN1Boolean.getInstance(this.inhibitAnyPolicy)));
        }
        return new DERSequence(aSN1EncodableVector);
    }

    public String toString() {
        return "PathProcInput: {\nacceptablePolicySet: " + Arrays.asList(this.acceptablePolicySet) + "\ninhibitPolicyMapping: " + this.inhibitPolicyMapping + "\nexplicitPolicyReqd: " + this.explicitPolicyReqd + "\ninhibitAnyPolicy: " + this.inhibitAnyPolicy + "\n}\n";
    }

    public PolicyInformation[] getAcceptablePolicySet() {
        return copy(this.acceptablePolicySet);
    }

    public boolean isInhibitPolicyMapping() {
        return this.inhibitPolicyMapping;
    }

    private void setInhibitPolicyMapping(boolean z) {
        this.inhibitPolicyMapping = z;
    }

    public boolean isExplicitPolicyReqd() {
        return this.explicitPolicyReqd;
    }

    private void setExplicitPolicyReqd(boolean z) {
        this.explicitPolicyReqd = z;
    }

    public boolean isInhibitAnyPolicy() {
        return this.inhibitAnyPolicy;
    }

    private void setInhibitAnyPolicy(boolean z) {
        this.inhibitAnyPolicy = z;
    }

    private PolicyInformation[] copy(PolicyInformation[] policyInformationArr) {
        PolicyInformation[] policyInformationArr2 = new PolicyInformation[policyInformationArr.length];
        System.arraycopy(policyInformationArr, 0, policyInformationArr2, 0, policyInformationArr2.length);
        return policyInformationArr2;
    }
}
