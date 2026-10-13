package org.bouncycastle.pkix;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.bouncycastle.asn1.ASN1IA5String;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.bouncycastle.asn1.x500.style.RFC4519Style;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralSubtree;
import org.bouncycastle.asn1.x509.NameConstraintValidator;
import org.bouncycastle.asn1.x509.NameConstraintValidatorException;
import org.bouncycastle.asn1.x509.OtherName;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Integers;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.encoders.Hex;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pkix/ASN1PKIXNameConstraintValidator.class */
class ASN1PKIXNameConstraintValidator implements NameConstraintValidator {
    private Set excludedSubtreesDN = new HashSet();
    private Set excludedSubtreesDNS = new HashSet();
    private Set excludedSubtreesEmail = new HashSet();
    private Set excludedSubtreesURI = new HashSet();
    private Set excludedSubtreesIP = new HashSet();
    private Set excludedSubtreesOtherName = new HashSet();
    private Set permittedSubtreesDN;
    private Set permittedSubtreesDNS;
    private Set permittedSubtreesEmail;
    private Set permittedSubtreesURI;
    private Set permittedSubtreesIP;
    private Set permittedSubtreesOtherName;

    @Override // org.bouncycastle.asn1.x509.NameConstraintValidator
    public void checkPermitted(GeneralName generalName) throws NameConstraintValidatorException {
        switch (generalName.getTagNo()) {
            case 0:
                checkPermittedOtherName(this.permittedSubtreesOtherName, OtherName.getInstance(generalName.getName()));
                break;
            case 1:
                checkPermittedEmail(this.permittedSubtreesEmail, extractNameAsString(generalName));
                break;
            case 2:
                checkPermittedDNS(this.permittedSubtreesDNS, ASN1IA5String.getInstance(generalName.getName()).getString());
                break;
            case 4:
                checkPermittedDN(X500Name.getInstance(generalName.getName()));
                break;
            case 6:
                checkPermittedURI(this.permittedSubtreesURI, ASN1IA5String.getInstance(generalName.getName()).getString());
                break;
            case 7:
                checkPermittedIP(this.permittedSubtreesIP, ASN1OctetString.getInstance(generalName.getName()).getOctets());
                break;
        }
    }

    @Override // org.bouncycastle.asn1.x509.NameConstraintValidator
    public void checkExcluded(GeneralName generalName) throws NameConstraintValidatorException {
        switch (generalName.getTagNo()) {
            case 0:
                checkExcludedOtherName(this.excludedSubtreesOtherName, OtherName.getInstance(generalName.getName()));
                break;
            case 1:
                checkExcludedEmail(this.excludedSubtreesEmail, extractNameAsString(generalName));
                break;
            case 2:
                checkExcludedDNS(this.excludedSubtreesDNS, ASN1IA5String.getInstance(generalName.getName()).getString());
                break;
            case 4:
                checkExcludedDN(X500Name.getInstance(generalName.getName()));
                break;
            case 6:
                checkExcludedURI(this.excludedSubtreesURI, ASN1IA5String.getInstance(generalName.getName()).getString());
                break;
            case 7:
                checkExcludedIP(this.excludedSubtreesIP, ASN1OctetString.getInstance(generalName.getName()).getOctets());
                break;
        }
    }

    @Override // org.bouncycastle.asn1.x509.NameConstraintValidator
    public void intersectPermittedSubtree(GeneralSubtree generalSubtree) {
        intersectPermittedSubtree(new GeneralSubtree[]{generalSubtree});
    }

    @Override // org.bouncycastle.asn1.x509.NameConstraintValidator
    public void intersectPermittedSubtree(GeneralSubtree[] generalSubtreeArr) {
        HashMap map = new HashMap();
        for (int i = 0; i != generalSubtreeArr.length; i++) {
            GeneralSubtree generalSubtree = generalSubtreeArr[i];
            Integer numValueOf = Integers.valueOf(generalSubtree.getBase().getTagNo());
            if (map.get(numValueOf) == null) {
                map.put(numValueOf, new HashSet());
            }
            ((Set) map.get(numValueOf)).add(generalSubtree);
        }
        for (Map.Entry entry : map.entrySet()) {
            int iIntValue = ((Integer) entry.getKey()).intValue();
            switch (iIntValue) {
                case 0:
                    this.permittedSubtreesOtherName = intersectOtherName(this.permittedSubtreesOtherName, (Set) entry.getValue());
                    break;
                case 1:
                    this.permittedSubtreesEmail = intersectEmail(this.permittedSubtreesEmail, (Set) entry.getValue());
                    break;
                case 2:
                    this.permittedSubtreesDNS = intersectDNS(this.permittedSubtreesDNS, (Set) entry.getValue());
                    break;
                case 3:
                case 5:
                default:
                    throw new IllegalStateException("Unknown tag encountered: " + iIntValue);
                case 4:
                    this.permittedSubtreesDN = intersectDN(this.permittedSubtreesDN, (Set) entry.getValue());
                    break;
                case 6:
                    this.permittedSubtreesURI = intersectURI(this.permittedSubtreesURI, (Set) entry.getValue());
                    break;
                case 7:
                    this.permittedSubtreesIP = intersectIP(this.permittedSubtreesIP, (Set) entry.getValue());
                    break;
            }
        }
    }

    @Override // org.bouncycastle.asn1.x509.NameConstraintValidator
    public void intersectEmptyPermittedSubtree(int i) {
        switch (i) {
            case 0:
                this.permittedSubtreesOtherName = new HashSet();
                return;
            case 1:
                this.permittedSubtreesEmail = new HashSet();
                return;
            case 2:
                this.permittedSubtreesDNS = new HashSet();
                return;
            case 3:
            case 5:
            default:
                throw new IllegalStateException("Unknown tag encountered: " + i);
            case 4:
                this.permittedSubtreesDN = new HashSet();
                return;
            case 6:
                this.permittedSubtreesURI = new HashSet();
                return;
            case 7:
                this.permittedSubtreesIP = new HashSet();
                return;
        }
    }

    @Override // org.bouncycastle.asn1.x509.NameConstraintValidator
    public void addExcludedSubtree(GeneralSubtree generalSubtree) {
        GeneralName base = generalSubtree.getBase();
        switch (base.getTagNo()) {
            case 0:
                this.excludedSubtreesOtherName = unionOtherName(this.excludedSubtreesOtherName, OtherName.getInstance(base.getName()));
                return;
            case 1:
                this.excludedSubtreesEmail = unionEmail(this.excludedSubtreesEmail, extractNameAsString(base));
                return;
            case 2:
                this.excludedSubtreesDNS = unionDNS(this.excludedSubtreesDNS, extractNameAsString(base));
                return;
            case 3:
            case 5:
            default:
                throw new IllegalStateException("Unknown tag encountered: " + base.getTagNo());
            case 4:
                this.excludedSubtreesDN = unionDN(this.excludedSubtreesDN, (ASN1Sequence) base.getName().toASN1Primitive());
                return;
            case 6:
                this.excludedSubtreesURI = unionURI(this.excludedSubtreesURI, extractNameAsString(base));
                return;
            case 7:
                this.excludedSubtreesIP = unionIP(this.excludedSubtreesIP, ASN1OctetString.getInstance(base.getName()).getOctets());
                return;
        }
    }

    public int hashCode() {
        return hashCollection(this.excludedSubtreesDN) + hashCollection(this.excludedSubtreesDNS) + hashCollection(this.excludedSubtreesEmail) + hashCollection(this.excludedSubtreesIP) + hashCollection(this.excludedSubtreesURI) + hashCollection(this.excludedSubtreesOtherName) + hashCollection(this.permittedSubtreesDN) + hashCollection(this.permittedSubtreesDNS) + hashCollection(this.permittedSubtreesEmail) + hashCollection(this.permittedSubtreesIP) + hashCollection(this.permittedSubtreesURI) + hashCollection(this.permittedSubtreesOtherName);
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof ASN1PKIXNameConstraintValidator)) {
            return false;
        }
        ASN1PKIXNameConstraintValidator aSN1PKIXNameConstraintValidator = (ASN1PKIXNameConstraintValidator) obj;
        return collectionsAreEqual(aSN1PKIXNameConstraintValidator.excludedSubtreesDN, this.excludedSubtreesDN) && collectionsAreEqual(aSN1PKIXNameConstraintValidator.excludedSubtreesDNS, this.excludedSubtreesDNS) && collectionsAreEqual(aSN1PKIXNameConstraintValidator.excludedSubtreesEmail, this.excludedSubtreesEmail) && collectionsAreEqual(aSN1PKIXNameConstraintValidator.excludedSubtreesIP, this.excludedSubtreesIP) && collectionsAreEqual(aSN1PKIXNameConstraintValidator.excludedSubtreesURI, this.excludedSubtreesURI) && collectionsAreEqual(aSN1PKIXNameConstraintValidator.excludedSubtreesOtherName, this.excludedSubtreesOtherName) && collectionsAreEqual(aSN1PKIXNameConstraintValidator.permittedSubtreesDN, this.permittedSubtreesDN) && collectionsAreEqual(aSN1PKIXNameConstraintValidator.permittedSubtreesDNS, this.permittedSubtreesDNS) && collectionsAreEqual(aSN1PKIXNameConstraintValidator.permittedSubtreesEmail, this.permittedSubtreesEmail) && collectionsAreEqual(aSN1PKIXNameConstraintValidator.permittedSubtreesIP, this.permittedSubtreesIP) && collectionsAreEqual(aSN1PKIXNameConstraintValidator.permittedSubtreesURI, this.permittedSubtreesURI) && collectionsAreEqual(aSN1PKIXNameConstraintValidator.permittedSubtreesOtherName, this.permittedSubtreesOtherName);
    }

    public void checkPermittedDN(X500Name x500Name) throws NameConstraintValidatorException {
        checkPermittedDN(this.permittedSubtreesDN, ASN1Sequence.getInstance(x500Name.toASN1Primitive()));
    }

    public void checkExcludedDN(X500Name x500Name) throws NameConstraintValidatorException {
        checkExcludedDN(this.excludedSubtreesDN, ASN1Sequence.getInstance(x500Name));
    }

    private static boolean withinDNSubtree(ASN1Sequence aSN1Sequence, ASN1Sequence aSN1Sequence2) {
        if (aSN1Sequence2.size() < 1 || aSN1Sequence2.size() > aSN1Sequence.size()) {
            return false;
        }
        int i = 0;
        RDN rdn = RDN.getInstance(aSN1Sequence2.getObjectAt(0));
        for (int i2 = 0; i2 < aSN1Sequence.size(); i2++) {
            i = i2;
            if (RDN.getInstance(aSN1Sequence.getObjectAt(i2)).equals(rdn)) {
                break;
            }
        }
        if (aSN1Sequence2.size() > aSN1Sequence.size() - i) {
            return false;
        }
        for (int i3 = 0; i3 < aSN1Sequence2.size(); i3++) {
            RDN rdn2 = RDN.getInstance(aSN1Sequence2.getObjectAt(i3));
            RDN rdn3 = RDN.getInstance(aSN1Sequence.getObjectAt(i + i3));
            if (rdn2.size() != rdn3.size() || !rdn2.getFirst().getType().equals((ASN1Primitive) rdn3.getFirst().getType())) {
                return false;
            }
            if (rdn2.size() == 1 && rdn2.getFirst().getType().equals((ASN1Primitive) RFC4519Style.serialNumber)) {
                if (!rdn3.getFirst().getValue().toString().startsWith(rdn2.getFirst().getValue().toString())) {
                    return false;
                }
            } else if (!IETFUtils.rDNAreEqual(rdn2, rdn3)) {
                return false;
            }
        }
        return true;
    }

    private void checkPermittedDN(Set set, ASN1Sequence aSN1Sequence) throws NameConstraintValidatorException {
        if (set == null) {
            return;
        }
        if (set.isEmpty() && aSN1Sequence.size() == 0) {
            return;
        }
        Iterator it = set.iterator();
        while (it.hasNext()) {
            if (withinDNSubtree(aSN1Sequence, (ASN1Sequence) it.next())) {
                return;
            }
        }
        throw new NameConstraintValidatorException("Subject distinguished name is not from a permitted subtree");
    }

    private void checkExcludedDN(Set set, ASN1Sequence aSN1Sequence) throws NameConstraintValidatorException {
        if (set.isEmpty()) {
            return;
        }
        Iterator it = set.iterator();
        while (it.hasNext()) {
            if (withinDNSubtree(aSN1Sequence, (ASN1Sequence) it.next())) {
                throw new NameConstraintValidatorException("Subject distinguished name is from an excluded subtree");
            }
        }
    }

    private Set intersectDN(Set set, Set set2) {
        HashSet hashSet = new HashSet();
        Iterator it = set2.iterator();
        while (it.hasNext()) {
            ASN1Sequence aSN1Sequence = ASN1Sequence.getInstance(((GeneralSubtree) it.next()).getBase().getName().toASN1Primitive());
            if (set != null) {
                Iterator it2 = set.iterator();
                while (it2.hasNext()) {
                    ASN1Sequence aSN1Sequence2 = (ASN1Sequence) it2.next();
                    if (withinDNSubtree(aSN1Sequence, aSN1Sequence2)) {
                        hashSet.add(aSN1Sequence);
                    } else if (withinDNSubtree(aSN1Sequence2, aSN1Sequence)) {
                        hashSet.add(aSN1Sequence2);
                    }
                }
            } else if (aSN1Sequence != null) {
                hashSet.add(aSN1Sequence);
            }
        }
        return hashSet;
    }

    private Set unionDN(Set set, ASN1Sequence aSN1Sequence) {
        if (set.isEmpty()) {
            if (aSN1Sequence == null) {
                return set;
            }
            set.add(aSN1Sequence);
            return set;
        }
        HashSet hashSet = new HashSet();
        Iterator it = set.iterator();
        while (it.hasNext()) {
            ASN1Sequence aSN1Sequence2 = (ASN1Sequence) it.next();
            if (withinDNSubtree(aSN1Sequence, aSN1Sequence2)) {
                hashSet.add(aSN1Sequence2);
            } else if (withinDNSubtree(aSN1Sequence2, aSN1Sequence)) {
                hashSet.add(aSN1Sequence);
            } else {
                hashSet.add(aSN1Sequence2);
                hashSet.add(aSN1Sequence);
            }
        }
        return hashSet;
    }

    private Set intersectOtherName(Set set, Set set2) {
        HashSet hashSet = new HashSet(set);
        hashSet.retainAll(set2);
        return hashSet;
    }

    private Set unionOtherName(Set set, OtherName otherName) {
        HashSet hashSet = new HashSet(set);
        hashSet.add(otherName);
        return hashSet;
    }

    private Set intersectEmail(Set set, Set set2) {
        HashSet hashSet = new HashSet();
        Iterator it = set2.iterator();
        while (it.hasNext()) {
            String strExtractNameAsString = extractNameAsString(((GeneralSubtree) it.next()).getBase());
            if (set != null) {
                Iterator it2 = set.iterator();
                while (it2.hasNext()) {
                    intersectEmail(strExtractNameAsString, (String) it2.next(), hashSet);
                }
            } else if (strExtractNameAsString != null) {
                hashSet.add(strExtractNameAsString);
            }
        }
        return hashSet;
    }

    private Set unionEmail(Set set, String str) {
        if (set.isEmpty()) {
            if (str == null) {
                return set;
            }
            set.add(str);
            return set;
        }
        HashSet hashSet = new HashSet();
        Iterator it = set.iterator();
        while (it.hasNext()) {
            unionEmail((String) it.next(), str, hashSet);
        }
        return hashSet;
    }

    private Set intersectIP(Set set, Set set2) {
        HashSet hashSet = new HashSet();
        Iterator it = set2.iterator();
        while (it.hasNext()) {
            byte[] octets = ASN1OctetString.getInstance(((GeneralSubtree) it.next()).getBase().getName()).getOctets();
            if (set != null) {
                Iterator it2 = set.iterator();
                while (it2.hasNext()) {
                    hashSet.addAll(intersectIPRange((byte[]) it2.next(), octets));
                }
            } else if (octets != null) {
                hashSet.add(octets);
            }
        }
        return hashSet;
    }

    private Set unionIP(Set set, byte[] bArr) {
        if (set.isEmpty()) {
            if (bArr == null) {
                return set;
            }
            set.add(bArr);
            return set;
        }
        HashSet hashSet = new HashSet();
        Iterator it = set.iterator();
        while (it.hasNext()) {
            hashSet.addAll(unionIPRange((byte[]) it.next(), bArr));
        }
        return hashSet;
    }

    private Set unionIPRange(byte[] bArr, byte[] bArr2) {
        HashSet hashSet = new HashSet();
        if (Arrays.areEqual(bArr, bArr2)) {
            hashSet.add(bArr);
        } else {
            hashSet.add(bArr);
            hashSet.add(bArr2);
        }
        return hashSet;
    }

    private Set intersectIPRange(byte[] bArr, byte[] bArr2) {
        if (bArr.length != bArr2.length) {
            return Collections.EMPTY_SET;
        }
        byte[][] bArrExtractIPsAndSubnetMasks = extractIPsAndSubnetMasks(bArr, bArr2);
        byte[] bArr3 = bArrExtractIPsAndSubnetMasks[0];
        byte[] bArr4 = bArrExtractIPsAndSubnetMasks[1];
        byte[] bArr5 = bArrExtractIPsAndSubnetMasks[2];
        byte[] bArr6 = bArrExtractIPsAndSubnetMasks[3];
        byte[][] bArrMinMaxIPs = minMaxIPs(bArr3, bArr4, bArr5, bArr6);
        return compareTo(max(bArrMinMaxIPs[0], bArrMinMaxIPs[2]), min(bArrMinMaxIPs[1], bArrMinMaxIPs[3])) == 1 ? Collections.EMPTY_SET : Collections.singleton(ipWithSubnetMask(or(bArrMinMaxIPs[0], bArrMinMaxIPs[2]), or(bArr4, bArr6)));
    }

    private byte[] ipWithSubnetMask(byte[] bArr, byte[] bArr2) {
        int length = bArr.length;
        byte[] bArr3 = new byte[length * 2];
        System.arraycopy(bArr, 0, bArr3, 0, length);
        System.arraycopy(bArr2, 0, bArr3, length, length);
        return bArr3;
    }

    private byte[][] extractIPsAndSubnetMasks(byte[] bArr, byte[] bArr2) {
        int length = bArr.length / 2;
        byte[] bArr3 = new byte[length];
        byte[] bArr4 = new byte[length];
        System.arraycopy(bArr, 0, bArr3, 0, length);
        System.arraycopy(bArr, length, bArr4, 0, length);
        byte[] bArr5 = new byte[length];
        byte[] bArr6 = new byte[length];
        System.arraycopy(bArr2, 0, bArr5, 0, length);
        System.arraycopy(bArr2, length, bArr6, 0, length);
        return new byte[][]{bArr3, bArr4, bArr5, bArr6};
    }

    private byte[][] minMaxIPs(byte[] bArr, byte[] bArr2, byte[] bArr3, byte[] bArr4) {
        int length = bArr.length;
        byte[] bArr5 = new byte[length];
        byte[] bArr6 = new byte[length];
        byte[] bArr7 = new byte[length];
        byte[] bArr8 = new byte[length];
        for (int i = 0; i < length; i++) {
            bArr5[i] = (byte) (bArr[i] & bArr2[i]);
            bArr6[i] = (byte) ((bArr[i] & bArr2[i]) | (bArr2[i] ^ (-1)));
            bArr7[i] = (byte) (bArr3[i] & bArr4[i]);
            bArr8[i] = (byte) ((bArr3[i] & bArr4[i]) | (bArr4[i] ^ (-1)));
        }
        return new byte[][]{bArr5, bArr6, bArr7, bArr8};
    }

    private void checkPermittedEmail(Set set, String str) throws NameConstraintValidatorException {
        if (set == null) {
            return;
        }
        Iterator it = set.iterator();
        while (it.hasNext()) {
            if (emailIsConstrained(str, (String) it.next())) {
                return;
            }
        }
        if (str.length() != 0 || set.size() != 0) {
            throw new NameConstraintValidatorException("Subject email address is not from a permitted subtree.");
        }
    }

    private void checkPermittedOtherName(Set set, OtherName otherName) throws NameConstraintValidatorException {
        if (set == null) {
            return;
        }
        Iterator it = set.iterator();
        while (it.hasNext()) {
            if (otherNameIsConstrained(otherName, (OtherName) it.next())) {
                return;
            }
        }
        throw new NameConstraintValidatorException("Subject OtherName is not from a permitted subtree.");
    }

    private void checkExcludedOtherName(Set set, OtherName otherName) throws NameConstraintValidatorException {
        if (set.isEmpty()) {
            return;
        }
        Iterator it = set.iterator();
        while (it.hasNext()) {
            if (otherNameIsConstrained(otherName, OtherName.getInstance(it.next()))) {
                throw new NameConstraintValidatorException("OtherName is from an excluded subtree.");
            }
        }
    }

    private void checkExcludedEmail(Set set, String str) throws NameConstraintValidatorException {
        if (set.isEmpty()) {
            return;
        }
        Iterator it = set.iterator();
        while (it.hasNext()) {
            if (emailIsConstrained(str, (String) it.next())) {
                throw new NameConstraintValidatorException("Email address is from an excluded subtree.");
            }
        }
    }

    private void checkPermittedIP(Set set, byte[] bArr) throws NameConstraintValidatorException {
        if (set == null) {
            return;
        }
        Iterator it = set.iterator();
        while (it.hasNext()) {
            if (isIPConstrained(bArr, (byte[]) it.next())) {
                return;
            }
        }
        if (bArr.length != 0 || set.size() != 0) {
            throw new NameConstraintValidatorException("IP is not from a permitted subtree.");
        }
    }

    private void checkExcludedIP(Set set, byte[] bArr) throws NameConstraintValidatorException {
        if (set.isEmpty()) {
            return;
        }
        Iterator it = set.iterator();
        while (it.hasNext()) {
            if (isIPConstrained(bArr, (byte[]) it.next())) {
                throw new NameConstraintValidatorException("IP is from an excluded subtree.");
            }
        }
    }

    private boolean isIPConstrained(byte[] bArr, byte[] bArr2) {
        int length = bArr.length;
        if (length != bArr2.length / 2) {
            return false;
        }
        byte[] bArr3 = new byte[length];
        System.arraycopy(bArr2, length, bArr3, 0, length);
        byte[] bArr4 = new byte[length];
        byte[] bArr5 = new byte[length];
        for (int i = 0; i < length; i++) {
            bArr4[i] = (byte) (bArr2[i] & bArr3[i]);
            bArr5[i] = (byte) (bArr[i] & bArr3[i]);
        }
        return Arrays.areEqual(bArr4, bArr5);
    }

    private boolean otherNameIsConstrained(OtherName otherName, OtherName otherName2) {
        return otherName2.equals(otherName);
    }

    private boolean emailIsConstrained(String str, String str2) {
        String strSubstring = str.substring(str.indexOf(64) + 1);
        if (str2.indexOf(64) != -1) {
            return str.equalsIgnoreCase(str2) || strSubstring.equalsIgnoreCase(str2.substring(1));
        }
        if (str2.charAt(0) != '.') {
            return strSubstring.equalsIgnoreCase(str2);
        }
        return withinDomain(strSubstring, str2);
    }

    private boolean withinDomain(String str, String str2) {
        String strSubstring = str2;
        if (strSubstring.startsWith(".")) {
            strSubstring = strSubstring.substring(1);
        }
        String[] strArrSplit = Strings.split(strSubstring, '.');
        String[] strArrSplit2 = Strings.split(str, '.');
        if (strArrSplit2.length <= strArrSplit.length) {
            return false;
        }
        int length = strArrSplit2.length - strArrSplit.length;
        for (int i = -1; i < strArrSplit.length; i++) {
            if (i == -1) {
                if (strArrSplit2[i + length].equals("")) {
                    return false;
                }
            } else if (!strArrSplit[i].equalsIgnoreCase(strArrSplit2[i + length])) {
                return false;
            }
        }
        return true;
    }

    private void checkPermittedDNS(Set set, String str) throws NameConstraintValidatorException {
        if (set == null) {
            return;
        }
        Iterator it = set.iterator();
        while (it.hasNext()) {
            String str2 = (String) it.next();
            if (withinDomain(str, str2) || str.equalsIgnoreCase(str2)) {
                return;
            }
        }
        if (str.length() != 0 || set.size() != 0) {
            throw new NameConstraintValidatorException("DNS is not from a permitted subtree.");
        }
    }

    private void checkExcludedDNS(Set set, String str) throws NameConstraintValidatorException {
        if (set.isEmpty()) {
            return;
        }
        Iterator it = set.iterator();
        while (it.hasNext()) {
            String str2 = (String) it.next();
            if (withinDomain(str, str2) || str.equalsIgnoreCase(str2)) {
                throw new NameConstraintValidatorException("DNS is from an excluded subtree.");
            }
        }
    }

    private void unionEmail(String str, String str2, Set set) {
        if (str.indexOf(64) != -1) {
            String strSubstring = str.substring(str.indexOf(64) + 1);
            if (str2.indexOf(64) != -1) {
                if (str.equalsIgnoreCase(str2)) {
                    set.add(str);
                    return;
                } else {
                    set.add(str);
                    set.add(str2);
                    return;
                }
            }
            if (str2.startsWith(".")) {
                if (withinDomain(strSubstring, str2)) {
                    set.add(str2);
                    return;
                } else {
                    set.add(str);
                    set.add(str2);
                    return;
                }
            }
            if (strSubstring.equalsIgnoreCase(str2)) {
                set.add(str2);
                return;
            } else {
                set.add(str);
                set.add(str2);
                return;
            }
        }
        if (!str.startsWith(".")) {
            if (str2.indexOf(64) != -1) {
                if (str2.substring(str.indexOf(64) + 1).equalsIgnoreCase(str)) {
                    set.add(str);
                    return;
                } else {
                    set.add(str);
                    set.add(str2);
                    return;
                }
            }
            if (str2.startsWith(".")) {
                if (withinDomain(str, str2)) {
                    set.add(str2);
                    return;
                } else {
                    set.add(str);
                    set.add(str2);
                    return;
                }
            }
            if (str.equalsIgnoreCase(str2)) {
                set.add(str);
                return;
            } else {
                set.add(str);
                set.add(str2);
                return;
            }
        }
        if (str2.indexOf(64) != -1) {
            if (withinDomain(str2.substring(str.indexOf(64) + 1), str)) {
                set.add(str);
                return;
            } else {
                set.add(str);
                set.add(str2);
                return;
            }
        }
        if (!str2.startsWith(".")) {
            if (withinDomain(str2, str)) {
                set.add(str);
                return;
            } else {
                set.add(str);
                set.add(str2);
                return;
            }
        }
        if (withinDomain(str, str2) || str.equalsIgnoreCase(str2)) {
            set.add(str2);
        } else if (withinDomain(str2, str)) {
            set.add(str);
        } else {
            set.add(str);
            set.add(str2);
        }
    }

    private void unionURI(String str, String str2, Set set) {
        if (str.indexOf(64) != -1) {
            String strSubstring = str.substring(str.indexOf(64) + 1);
            if (str2.indexOf(64) != -1) {
                if (str.equalsIgnoreCase(str2)) {
                    set.add(str);
                    return;
                } else {
                    set.add(str);
                    set.add(str2);
                    return;
                }
            }
            if (str2.startsWith(".")) {
                if (withinDomain(strSubstring, str2)) {
                    set.add(str2);
                    return;
                } else {
                    set.add(str);
                    set.add(str2);
                    return;
                }
            }
            if (strSubstring.equalsIgnoreCase(str2)) {
                set.add(str2);
                return;
            } else {
                set.add(str);
                set.add(str2);
                return;
            }
        }
        if (!str.startsWith(".")) {
            if (str2.indexOf(64) != -1) {
                if (str2.substring(str.indexOf(64) + 1).equalsIgnoreCase(str)) {
                    set.add(str);
                    return;
                } else {
                    set.add(str);
                    set.add(str2);
                    return;
                }
            }
            if (str2.startsWith(".")) {
                if (withinDomain(str, str2)) {
                    set.add(str2);
                    return;
                } else {
                    set.add(str);
                    set.add(str2);
                    return;
                }
            }
            if (str.equalsIgnoreCase(str2)) {
                set.add(str);
                return;
            } else {
                set.add(str);
                set.add(str2);
                return;
            }
        }
        if (str2.indexOf(64) != -1) {
            if (withinDomain(str2.substring(str.indexOf(64) + 1), str)) {
                set.add(str);
                return;
            } else {
                set.add(str);
                set.add(str2);
                return;
            }
        }
        if (!str2.startsWith(".")) {
            if (withinDomain(str2, str)) {
                set.add(str);
                return;
            } else {
                set.add(str);
                set.add(str2);
                return;
            }
        }
        if (withinDomain(str, str2) || str.equalsIgnoreCase(str2)) {
            set.add(str2);
        } else if (withinDomain(str2, str)) {
            set.add(str);
        } else {
            set.add(str);
            set.add(str2);
        }
    }

    private Set intersectDNS(Set set, Set set2) {
        HashSet hashSet = new HashSet();
        Iterator it = set2.iterator();
        while (it.hasNext()) {
            String strExtractNameAsString = extractNameAsString(((GeneralSubtree) it.next()).getBase());
            if (set != null) {
                Iterator it2 = set.iterator();
                while (it2.hasNext()) {
                    String str = (String) it2.next();
                    if (withinDomain(str, strExtractNameAsString)) {
                        hashSet.add(str);
                    } else if (withinDomain(strExtractNameAsString, str)) {
                        hashSet.add(strExtractNameAsString);
                    }
                }
            } else if (strExtractNameAsString != null) {
                hashSet.add(strExtractNameAsString);
            }
        }
        return hashSet;
    }

    private Set unionDNS(Set set, String str) {
        if (set.isEmpty()) {
            if (str == null) {
                return set;
            }
            set.add(str);
            return set;
        }
        HashSet hashSet = new HashSet();
        Iterator it = set.iterator();
        while (it.hasNext()) {
            String str2 = (String) it.next();
            if (withinDomain(str2, str)) {
                hashSet.add(str);
            } else if (withinDomain(str, str2)) {
                hashSet.add(str2);
            } else {
                hashSet.add(str2);
                hashSet.add(str);
            }
        }
        return hashSet;
    }

    private void intersectEmail(String str, String str2, Set set) {
        if (str.indexOf(64) != -1) {
            String strSubstring = str.substring(str.indexOf(64) + 1);
            if (str2.indexOf(64) != -1) {
                if (str.equalsIgnoreCase(str2)) {
                    set.add(str);
                    return;
                }
                return;
            } else if (str2.startsWith(".")) {
                if (withinDomain(strSubstring, str2)) {
                    set.add(str);
                    return;
                }
                return;
            } else {
                if (strSubstring.equalsIgnoreCase(str2)) {
                    set.add(str);
                    return;
                }
                return;
            }
        }
        if (!str.startsWith(".")) {
            if (str2.indexOf(64) != -1) {
                if (str2.substring(str2.indexOf(64) + 1).equalsIgnoreCase(str)) {
                    set.add(str2);
                    return;
                }
                return;
            } else if (str2.startsWith(".")) {
                if (withinDomain(str, str2)) {
                    set.add(str);
                    return;
                }
                return;
            } else {
                if (str.equalsIgnoreCase(str2)) {
                    set.add(str);
                    return;
                }
                return;
            }
        }
        if (str2.indexOf(64) != -1) {
            if (withinDomain(str2.substring(str.indexOf(64) + 1), str)) {
                set.add(str2);
            }
        } else if (!str2.startsWith(".")) {
            if (withinDomain(str2, str)) {
                set.add(str2);
            }
        } else if (withinDomain(str, str2) || str.equalsIgnoreCase(str2)) {
            set.add(str);
        } else if (withinDomain(str2, str)) {
            set.add(str2);
        }
    }

    private void checkExcludedURI(Set set, String str) throws NameConstraintValidatorException {
        if (set.isEmpty()) {
            return;
        }
        Iterator it = set.iterator();
        while (it.hasNext()) {
            if (isUriConstrained(str, (String) it.next())) {
                throw new NameConstraintValidatorException("URI is from an excluded subtree.");
            }
        }
    }

    private Set intersectURI(Set set, Set set2) {
        HashSet hashSet = new HashSet();
        Iterator it = set2.iterator();
        while (it.hasNext()) {
            String strExtractNameAsString = extractNameAsString(((GeneralSubtree) it.next()).getBase());
            if (set != null) {
                Iterator it2 = set.iterator();
                while (it2.hasNext()) {
                    intersectURI((String) it2.next(), strExtractNameAsString, hashSet);
                }
            } else if (strExtractNameAsString != null) {
                hashSet.add(strExtractNameAsString);
            }
        }
        return hashSet;
    }

    private Set unionURI(Set set, String str) {
        if (set.isEmpty()) {
            if (str == null) {
                return set;
            }
            set.add(str);
            return set;
        }
        HashSet hashSet = new HashSet();
        Iterator it = set.iterator();
        while (it.hasNext()) {
            unionURI((String) it.next(), str, hashSet);
        }
        return hashSet;
    }

    private void intersectURI(String str, String str2, Set set) {
        if (str.indexOf(64) != -1) {
            String strSubstring = str.substring(str.indexOf(64) + 1);
            if (str2.indexOf(64) != -1) {
                if (str.equalsIgnoreCase(str2)) {
                    set.add(str);
                    return;
                }
                return;
            } else if (str2.startsWith(".")) {
                if (withinDomain(strSubstring, str2)) {
                    set.add(str);
                    return;
                }
                return;
            } else {
                if (strSubstring.equalsIgnoreCase(str2)) {
                    set.add(str);
                    return;
                }
                return;
            }
        }
        if (!str.startsWith(".")) {
            if (str2.indexOf(64) != -1) {
                if (str2.substring(str2.indexOf(64) + 1).equalsIgnoreCase(str)) {
                    set.add(str2);
                    return;
                }
                return;
            } else if (str2.startsWith(".")) {
                if (withinDomain(str, str2)) {
                    set.add(str);
                    return;
                }
                return;
            } else {
                if (str.equalsIgnoreCase(str2)) {
                    set.add(str);
                    return;
                }
                return;
            }
        }
        if (str2.indexOf(64) != -1) {
            if (withinDomain(str2.substring(str.indexOf(64) + 1), str)) {
                set.add(str2);
            }
        } else if (!str2.startsWith(".")) {
            if (withinDomain(str2, str)) {
                set.add(str2);
            }
        } else if (withinDomain(str, str2) || str.equalsIgnoreCase(str2)) {
            set.add(str);
        } else if (withinDomain(str2, str)) {
            set.add(str2);
        }
    }

    private void checkPermittedURI(Set set, String str) throws NameConstraintValidatorException {
        if (set == null) {
            return;
        }
        Iterator it = set.iterator();
        while (it.hasNext()) {
            if (isUriConstrained(str, (String) it.next())) {
                return;
            }
        }
        if (str.length() != 0 || set.size() != 0) {
            throw new NameConstraintValidatorException("URI is not from a permitted subtree.");
        }
    }

    private boolean isUriConstrained(String str, String str2) {
        String strExtractHostFromURL = extractHostFromURL(str);
        if (str2.startsWith(".")) {
            return withinDomain(strExtractHostFromURL, str2);
        }
        return strExtractHostFromURL.equalsIgnoreCase(str2);
    }

    private static String extractHostFromURL(String str) {
        String strSubstring = str.substring(str.indexOf(58) + 1);
        if (strSubstring.indexOf("//") != -1) {
            strSubstring = strSubstring.substring(strSubstring.indexOf("//") + 2);
        }
        if (strSubstring.lastIndexOf(58) != -1) {
            strSubstring = strSubstring.substring(0, strSubstring.lastIndexOf(58));
        }
        String strSubstring2 = strSubstring.substring(strSubstring.indexOf(58) + 1);
        String strSubstring3 = strSubstring2.substring(strSubstring2.indexOf(64) + 1);
        if (strSubstring3.indexOf(47) != -1) {
            strSubstring3 = strSubstring3.substring(0, strSubstring3.indexOf(47));
        }
        return strSubstring3;
    }

    private String extractNameAsString(GeneralName generalName) {
        return ASN1IA5String.getInstance(generalName.getName()).getString();
    }

    private static byte[] max(byte[] bArr, byte[] bArr2) {
        for (int i = 0; i < bArr.length; i++) {
            if ((bArr[i] & 65535) > (bArr2[i] & 65535)) {
                return bArr;
            }
        }
        return bArr2;
    }

    private static byte[] min(byte[] bArr, byte[] bArr2) {
        for (int i = 0; i < bArr.length; i++) {
            if ((bArr[i] & 65535) < (bArr2[i] & 65535)) {
                return bArr;
            }
        }
        return bArr2;
    }

    private static int compareTo(byte[] bArr, byte[] bArr2) {
        if (Arrays.areEqual(bArr, bArr2)) {
            return 0;
        }
        return Arrays.areEqual(max(bArr, bArr2), bArr) ? 1 : -1;
    }

    private static byte[] or(byte[] bArr, byte[] bArr2) {
        byte[] bArr3 = new byte[bArr.length];
        for (int i = 0; i < bArr.length; i++) {
            bArr3[i] = (byte) (bArr[i] | bArr2[i]);
        }
        return bArr3;
    }

    private int hashCollection(Collection collection) {
        if (collection == null) {
            return 0;
        }
        int iHashCode = 0;
        for (Object obj : collection) {
            iHashCode = obj instanceof byte[] ? iHashCode + Arrays.hashCode((byte[]) obj) : iHashCode + obj.hashCode();
        }
        return iHashCode;
    }

    private boolean collectionsAreEqual(Collection collection, Collection collection2) {
        if (collection == collection2) {
            return true;
        }
        if (collection == null || collection2 == null || collection.size() != collection2.size()) {
            return false;
        }
        for (Object obj : collection) {
            Iterator it = collection2.iterator();
            boolean z = false;
            while (it.hasNext()) {
                if (equals(obj, it.next())) {
                    z = true;
                    break;
                }
            }
            if (!z) {
                return false;
            }
        }
        return true;
    }

    private boolean equals(Object obj, Object obj2) {
        if (obj == obj2) {
            return true;
        }
        if (obj == null || obj2 == null) {
            return false;
        }
        return ((obj instanceof byte[]) && (obj2 instanceof byte[])) ? Arrays.areEqual((byte[]) obj, (byte[]) obj2) : obj.equals(obj2);
    }

    private String stringifyIP(byte[] bArr) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bArr.length / 2; i++) {
            if (sb.length() > 0) {
                sb.append(".");
            }
            sb.append(Integer.toString(bArr[i] & 255));
        }
        sb.append("/");
        boolean z = true;
        for (int length = bArr.length / 2; length < bArr.length; length++) {
            if (z) {
                z = false;
            } else {
                sb.append(".");
            }
            sb.append(Integer.toString(bArr[length] & 255));
        }
        return sb.toString();
    }

    private String stringifyIPCollection(Set set) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        Iterator it = set.iterator();
        while (it.hasNext()) {
            if (sb.length() > 1) {
                sb.append(",");
            }
            sb.append(stringifyIP((byte[]) it.next()));
        }
        sb.append("]");
        return sb.toString();
    }

    private String stringifyOtherNameCollection(Set set) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        Iterator it = set.iterator();
        while (it.hasNext()) {
            if (sb.length() > 1) {
                sb.append(",");
            }
            OtherName otherName = OtherName.getInstance(it.next());
            sb.append(otherName.getTypeID().getId());
            sb.append(":");
            try {
                sb.append(Hex.toHexString(otherName.getValue().toASN1Primitive().getEncoded()));
            } catch (IOException e) {
                sb.append(e.toString());
            }
        }
        sb.append("]");
        return sb.toString();
    }

    private final void addLine(StringBuilder sb, String str) {
        sb.append(str).append(Strings.lineSeparator());
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        addLine(sb, "permitted:");
        if (this.permittedSubtreesDN != null) {
            addLine(sb, "DN:");
            addLine(sb, this.permittedSubtreesDN.toString());
        }
        if (this.permittedSubtreesDNS != null) {
            addLine(sb, "DNS:");
            addLine(sb, this.permittedSubtreesDNS.toString());
        }
        if (this.permittedSubtreesEmail != null) {
            addLine(sb, "Email:");
            addLine(sb, this.permittedSubtreesEmail.toString());
        }
        if (this.permittedSubtreesURI != null) {
            addLine(sb, "URI:");
            addLine(sb, this.permittedSubtreesURI.toString());
        }
        if (this.permittedSubtreesIP != null) {
            addLine(sb, "IP:");
            addLine(sb, stringifyIPCollection(this.permittedSubtreesIP));
        }
        if (this.permittedSubtreesOtherName != null) {
            addLine(sb, "OtherName:");
            addLine(sb, stringifyOtherNameCollection(this.permittedSubtreesOtherName));
        }
        addLine(sb, "excluded:");
        if (!this.excludedSubtreesDN.isEmpty()) {
            addLine(sb, "DN:");
            addLine(sb, this.excludedSubtreesDN.toString());
        }
        if (!this.excludedSubtreesDNS.isEmpty()) {
            addLine(sb, "DNS:");
            addLine(sb, this.excludedSubtreesDNS.toString());
        }
        if (!this.excludedSubtreesEmail.isEmpty()) {
            addLine(sb, "Email:");
            addLine(sb, this.excludedSubtreesEmail.toString());
        }
        if (!this.excludedSubtreesURI.isEmpty()) {
            addLine(sb, "URI:");
            addLine(sb, this.excludedSubtreesURI.toString());
        }
        if (!this.excludedSubtreesIP.isEmpty()) {
            addLine(sb, "IP:");
            addLine(sb, stringifyIPCollection(this.excludedSubtreesIP));
        }
        if (!this.excludedSubtreesOtherName.isEmpty()) {
            addLine(sb, "OtherName:");
            addLine(sb, stringifyOtherNameCollection(this.excludedSubtreesOtherName));
        }
        return sb.toString();
    }
}
