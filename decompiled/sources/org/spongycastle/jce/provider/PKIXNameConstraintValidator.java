package org.spongycastle.jce.provider;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.spongycastle.asn1.ASN1OctetString;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.DERIA5String;
import org.spongycastle.asn1.x509.GeneralName;
import org.spongycastle.asn1.x509.GeneralSubtree;
import org.spongycastle.util.Arrays;
import org.spongycastle.util.Integers;
import org.spongycastle.util.Strings;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jce/provider/PKIXNameConstraintValidator.class */
public class PKIXNameConstraintValidator {
    private Set excludedSubtreesDN = new HashSet();
    private Set excludedSubtreesDNS = new HashSet();
    private Set excludedSubtreesEmail = new HashSet();
    private Set excludedSubtreesURI = new HashSet();
    private Set excludedSubtreesIP = new HashSet();
    private Set permittedSubtreesDN;
    private Set permittedSubtreesDNS;
    private Set permittedSubtreesEmail;
    private Set permittedSubtreesURI;
    private Set permittedSubtreesIP;

    private static boolean withinDNSubtree(ASN1Sequence dns, ASN1Sequence subtree) {
        if (subtree.size() < 1 || subtree.size() > dns.size()) {
            return false;
        }
        for (int j = subtree.size() - 1; j >= 0; j--) {
            if (!subtree.getObjectAt(j).equals(dns.getObjectAt(j))) {
                return false;
            }
        }
        return true;
    }

    public void checkPermittedDN(ASN1Sequence dns) throws PKIXNameConstraintValidatorException {
        checkPermittedDN(this.permittedSubtreesDN, dns);
    }

    public void checkExcludedDN(ASN1Sequence dns) throws PKIXNameConstraintValidatorException {
        checkExcludedDN(this.excludedSubtreesDN, dns);
    }

    private void checkPermittedDN(Set permitted, ASN1Sequence dns) throws PKIXNameConstraintValidatorException {
        if (permitted == null) {
            return;
        }
        if (permitted.isEmpty() && dns.size() == 0) {
            return;
        }
        Iterator it = permitted.iterator();
        while (it.hasNext()) {
            ASN1Sequence subtree = (ASN1Sequence) it.next();
            if (withinDNSubtree(dns, subtree)) {
                return;
            }
        }
        throw new PKIXNameConstraintValidatorException("Subject distinguished name is not from a permitted subtree");
    }

    private void checkExcludedDN(Set excluded, ASN1Sequence dns) throws PKIXNameConstraintValidatorException {
        if (excluded.isEmpty()) {
            return;
        }
        Iterator it = excluded.iterator();
        while (it.hasNext()) {
            ASN1Sequence subtree = (ASN1Sequence) it.next();
            if (withinDNSubtree(dns, subtree)) {
                throw new PKIXNameConstraintValidatorException("Subject distinguished name is from an excluded subtree");
            }
        }
    }

    private Set intersectDN(Set permitted, Set dns) {
        Set intersect = new HashSet();
        Iterator it = dns.iterator();
        while (it.hasNext()) {
            ASN1Sequence dn = ASN1Sequence.getInstance(((GeneralSubtree) it.next()).getBase().getName().toASN1Primitive());
            if (permitted == null) {
                if (dn != null) {
                    intersect.add(dn);
                }
            } else {
                Iterator _iter = permitted.iterator();
                while (_iter.hasNext()) {
                    ASN1Sequence subtree = (ASN1Sequence) _iter.next();
                    if (withinDNSubtree(dn, subtree)) {
                        intersect.add(dn);
                    } else if (withinDNSubtree(subtree, dn)) {
                        intersect.add(subtree);
                    }
                }
            }
        }
        return intersect;
    }

    private Set unionDN(Set excluded, ASN1Sequence dn) {
        if (excluded.isEmpty()) {
            if (dn == null) {
                return excluded;
            }
            excluded.add(dn);
            return excluded;
        }
        Set intersect = new HashSet();
        Iterator it = excluded.iterator();
        while (it.hasNext()) {
            ASN1Sequence subtree = (ASN1Sequence) it.next();
            if (withinDNSubtree(dn, subtree)) {
                intersect.add(subtree);
            } else if (withinDNSubtree(subtree, dn)) {
                intersect.add(dn);
            } else {
                intersect.add(subtree);
                intersect.add(dn);
            }
        }
        return intersect;
    }

    private Set intersectEmail(Set permitted, Set emails) {
        Set intersect = new HashSet();
        Iterator it = emails.iterator();
        while (it.hasNext()) {
            String email = extractNameAsString(((GeneralSubtree) it.next()).getBase());
            if (permitted == null) {
                if (email != null) {
                    intersect.add(email);
                }
            } else {
                Iterator it2 = permitted.iterator();
                while (it2.hasNext()) {
                    String _permitted = (String) it2.next();
                    intersectEmail(email, _permitted, intersect);
                }
            }
        }
        return intersect;
    }

    private Set unionEmail(Set excluded, String email) {
        if (excluded.isEmpty()) {
            if (email == null) {
                return excluded;
            }
            excluded.add(email);
            return excluded;
        }
        Set union = new HashSet();
        Iterator it = excluded.iterator();
        while (it.hasNext()) {
            String _excluded = (String) it.next();
            unionEmail(_excluded, email, union);
        }
        return union;
    }

    private Set intersectIP(Set permitted, Set ips) {
        Set intersect = new HashSet();
        Iterator it = ips.iterator();
        while (it.hasNext()) {
            byte[] ip = ASN1OctetString.getInstance(((GeneralSubtree) it.next()).getBase().getName()).getOctets();
            if (permitted == null) {
                if (ip != null) {
                    intersect.add(ip);
                }
            } else {
                Iterator it2 = permitted.iterator();
                while (it2.hasNext()) {
                    byte[] _permitted = (byte[]) it2.next();
                    intersect.addAll(intersectIPRange(_permitted, ip));
                }
            }
        }
        return intersect;
    }

    private Set unionIP(Set excluded, byte[] ip) {
        if (excluded.isEmpty()) {
            if (ip == null) {
                return excluded;
            }
            excluded.add(ip);
            return excluded;
        }
        Set union = new HashSet();
        Iterator it = excluded.iterator();
        while (it.hasNext()) {
            byte[] _excluded = (byte[]) it.next();
            union.addAll(unionIPRange(_excluded, ip));
        }
        return union;
    }

    private Set unionIPRange(byte[] ipWithSubmask1, byte[] ipWithSubmask2) {
        Set set = new HashSet();
        if (Arrays.areEqual(ipWithSubmask1, ipWithSubmask2)) {
            set.add(ipWithSubmask1);
        } else {
            set.add(ipWithSubmask1);
            set.add(ipWithSubmask2);
        }
        return set;
    }

    private Set intersectIPRange(byte[] ipWithSubmask1, byte[] ipWithSubmask2) {
        if (ipWithSubmask1.length != ipWithSubmask2.length) {
            return Collections.EMPTY_SET;
        }
        byte[][] temp = extractIPsAndSubnetMasks(ipWithSubmask1, ipWithSubmask2);
        byte[] ip1 = temp[0];
        byte[] subnetmask1 = temp[1];
        byte[] ip2 = temp[2];
        byte[] subnetmask2 = temp[3];
        byte[][] minMax = minMaxIPs(ip1, subnetmask1, ip2, subnetmask2);
        byte[] max = min(minMax[1], minMax[3]);
        byte[] min = max(minMax[0], minMax[2]);
        if (compareTo(min, max) == 1) {
            return Collections.EMPTY_SET;
        }
        byte[] ip = or(minMax[0], minMax[2]);
        byte[] subnetmask = or(subnetmask1, subnetmask2);
        return Collections.singleton(ipWithSubnetMask(ip, subnetmask));
    }

    private byte[] ipWithSubnetMask(byte[] ip, byte[] subnetMask) {
        int ipLength = ip.length;
        byte[] temp = new byte[ipLength * 2];
        System.arraycopy(ip, 0, temp, 0, ipLength);
        System.arraycopy(subnetMask, 0, temp, ipLength, ipLength);
        return temp;
    }

    private byte[][] extractIPsAndSubnetMasks(byte[] ipWithSubmask1, byte[] ipWithSubmask2) {
        int ipLength = ipWithSubmask1.length / 2;
        byte[] ip1 = new byte[ipLength];
        byte[] subnetmask1 = new byte[ipLength];
        System.arraycopy(ipWithSubmask1, 0, ip1, 0, ipLength);
        System.arraycopy(ipWithSubmask1, ipLength, subnetmask1, 0, ipLength);
        byte[] ip2 = new byte[ipLength];
        byte[] subnetmask2 = new byte[ipLength];
        System.arraycopy(ipWithSubmask2, 0, ip2, 0, ipLength);
        System.arraycopy(ipWithSubmask2, ipLength, subnetmask2, 0, ipLength);
        return new byte[][]{ip1, subnetmask1, ip2, subnetmask2};
    }

    private byte[][] minMaxIPs(byte[] ip1, byte[] subnetmask1, byte[] ip2, byte[] subnetmask2) {
        int ipLength = ip1.length;
        byte[] min1 = new byte[ipLength];
        byte[] max1 = new byte[ipLength];
        byte[] min2 = new byte[ipLength];
        byte[] max2 = new byte[ipLength];
        for (int i = 0; i < ipLength; i++) {
            min1[i] = (byte) (ip1[i] & subnetmask1[i]);
            max1[i] = (byte) ((ip1[i] & subnetmask1[i]) | (subnetmask1[i] ^ (-1)));
            min2[i] = (byte) (ip2[i] & subnetmask2[i]);
            max2[i] = (byte) ((ip2[i] & subnetmask2[i]) | (subnetmask2[i] ^ (-1)));
        }
        return new byte[][]{min1, max1, min2, max2};
    }

    private void checkPermittedEmail(Set permitted, String email) throws PKIXNameConstraintValidatorException {
        if (permitted == null) {
            return;
        }
        Iterator it = permitted.iterator();
        while (it.hasNext()) {
            String str = (String) it.next();
            if (emailIsConstrained(email, str)) {
                return;
            }
        }
        if (email.length() == 0 && permitted.size() == 0) {
        } else {
            throw new PKIXNameConstraintValidatorException("Subject email address is not from a permitted subtree.");
        }
    }

    private void checkExcludedEmail(Set excluded, String email) throws PKIXNameConstraintValidatorException {
        if (excluded.isEmpty()) {
            return;
        }
        Iterator it = excluded.iterator();
        while (it.hasNext()) {
            String str = (String) it.next();
            if (emailIsConstrained(email, str)) {
                throw new PKIXNameConstraintValidatorException("Email address is from an excluded subtree.");
            }
        }
    }

    private void checkPermittedIP(Set permitted, byte[] ip) throws PKIXNameConstraintValidatorException {
        if (permitted == null) {
            return;
        }
        Iterator it = permitted.iterator();
        while (it.hasNext()) {
            byte[] ipWithSubnet = (byte[]) it.next();
            if (isIPConstrained(ip, ipWithSubnet)) {
                return;
            }
        }
        if (ip.length == 0 && permitted.size() == 0) {
        } else {
            throw new PKIXNameConstraintValidatorException("IP is not from a permitted subtree.");
        }
    }

    private void checkExcludedIP(Set excluded, byte[] ip) throws PKIXNameConstraintValidatorException {
        if (excluded.isEmpty()) {
            return;
        }
        Iterator it = excluded.iterator();
        while (it.hasNext()) {
            byte[] ipWithSubnet = (byte[]) it.next();
            if (isIPConstrained(ip, ipWithSubnet)) {
                throw new PKIXNameConstraintValidatorException("IP is from an excluded subtree.");
            }
        }
    }

    private boolean isIPConstrained(byte[] ip, byte[] constraint) {
        int ipLength = ip.length;
        if (ipLength != constraint.length / 2) {
            return false;
        }
        byte[] subnetMask = new byte[ipLength];
        System.arraycopy(constraint, ipLength, subnetMask, 0, ipLength);
        byte[] permittedSubnetAddress = new byte[ipLength];
        byte[] ipSubnetAddress = new byte[ipLength];
        for (int i = 0; i < ipLength; i++) {
            permittedSubnetAddress[i] = (byte) (constraint[i] & subnetMask[i]);
            ipSubnetAddress[i] = (byte) (ip[i] & subnetMask[i]);
        }
        return Arrays.areEqual(permittedSubnetAddress, ipSubnetAddress);
    }

    private boolean emailIsConstrained(String email, String constraint) {
        String sub = email.substring(email.indexOf(64) + 1);
        if (constraint.indexOf(64) != -1) {
            if (email.equalsIgnoreCase(constraint) || sub.equalsIgnoreCase(constraint.substring(1))) {
                return true;
            }
            return false;
        }
        if (constraint.charAt(0) != '.') {
            if (sub.equalsIgnoreCase(constraint)) {
                return true;
            }
            return false;
        }
        if (withinDomain(sub, constraint)) {
            return true;
        }
        return false;
    }

    private boolean withinDomain(String testDomain, String domain) {
        String tempDomain = domain;
        if (tempDomain.startsWith(".")) {
            tempDomain = tempDomain.substring(1);
        }
        String[] domainParts = Strings.split(tempDomain, '.');
        String[] testDomainParts = Strings.split(testDomain, '.');
        if (testDomainParts.length <= domainParts.length) {
            return false;
        }
        int d = testDomainParts.length - domainParts.length;
        for (int i = -1; i < domainParts.length; i++) {
            if (i == -1) {
                if (testDomainParts[i + d].equals("")) {
                    return false;
                }
            } else if (!domainParts[i].equalsIgnoreCase(testDomainParts[i + d])) {
                return false;
            }
        }
        return true;
    }

    private void checkPermittedDNS(Set permitted, String dns) throws PKIXNameConstraintValidatorException {
        if (permitted == null) {
            return;
        }
        Iterator it = permitted.iterator();
        while (it.hasNext()) {
            String str = (String) it.next();
            if (withinDomain(dns, str) || dns.equalsIgnoreCase(str)) {
                return;
            }
        }
        if (dns.length() == 0 && permitted.size() == 0) {
        } else {
            throw new PKIXNameConstraintValidatorException("DNS is not from a permitted subtree.");
        }
    }

    private void checkExcludedDNS(Set excluded, String dns) throws PKIXNameConstraintValidatorException {
        if (excluded.isEmpty()) {
            return;
        }
        Iterator it = excluded.iterator();
        while (it.hasNext()) {
            String str = (String) it.next();
            if (withinDomain(dns, str) || dns.equalsIgnoreCase(str)) {
                throw new PKIXNameConstraintValidatorException("DNS is from an excluded subtree.");
            }
        }
    }

    private void unionEmail(String email1, String email2, Set union) {
        if (email1.indexOf(64) != -1) {
            String _sub = email1.substring(email1.indexOf(64) + 1);
            if (email2.indexOf(64) != -1) {
                if (email1.equalsIgnoreCase(email2)) {
                    union.add(email1);
                    return;
                } else {
                    union.add(email1);
                    union.add(email2);
                    return;
                }
            }
            if (email2.startsWith(".")) {
                if (withinDomain(_sub, email2)) {
                    union.add(email2);
                    return;
                } else {
                    union.add(email1);
                    union.add(email2);
                    return;
                }
            }
            if (_sub.equalsIgnoreCase(email2)) {
                union.add(email2);
                return;
            } else {
                union.add(email1);
                union.add(email2);
                return;
            }
        }
        if (email1.startsWith(".")) {
            if (email2.indexOf(64) != -1) {
                String _sub2 = email2.substring(email1.indexOf(64) + 1);
                if (withinDomain(_sub2, email1)) {
                    union.add(email1);
                    return;
                } else {
                    union.add(email1);
                    union.add(email2);
                    return;
                }
            }
            if (email2.startsWith(".")) {
                if (withinDomain(email1, email2) || email1.equalsIgnoreCase(email2)) {
                    union.add(email2);
                    return;
                } else if (withinDomain(email2, email1)) {
                    union.add(email1);
                    return;
                } else {
                    union.add(email1);
                    union.add(email2);
                    return;
                }
            }
            if (withinDomain(email2, email1)) {
                union.add(email1);
                return;
            } else {
                union.add(email1);
                union.add(email2);
                return;
            }
        }
        if (email2.indexOf(64) != -1) {
            String _sub3 = email2.substring(email1.indexOf(64) + 1);
            if (_sub3.equalsIgnoreCase(email1)) {
                union.add(email1);
                return;
            } else {
                union.add(email1);
                union.add(email2);
                return;
            }
        }
        if (email2.startsWith(".")) {
            if (withinDomain(email1, email2)) {
                union.add(email2);
                return;
            } else {
                union.add(email1);
                union.add(email2);
                return;
            }
        }
        if (email1.equalsIgnoreCase(email2)) {
            union.add(email1);
        } else {
            union.add(email1);
            union.add(email2);
        }
    }

    private void unionURI(String email1, String email2, Set union) {
        if (email1.indexOf(64) != -1) {
            String _sub = email1.substring(email1.indexOf(64) + 1);
            if (email2.indexOf(64) != -1) {
                if (email1.equalsIgnoreCase(email2)) {
                    union.add(email1);
                    return;
                } else {
                    union.add(email1);
                    union.add(email2);
                    return;
                }
            }
            if (email2.startsWith(".")) {
                if (withinDomain(_sub, email2)) {
                    union.add(email2);
                    return;
                } else {
                    union.add(email1);
                    union.add(email2);
                    return;
                }
            }
            if (_sub.equalsIgnoreCase(email2)) {
                union.add(email2);
                return;
            } else {
                union.add(email1);
                union.add(email2);
                return;
            }
        }
        if (email1.startsWith(".")) {
            if (email2.indexOf(64) != -1) {
                String _sub2 = email2.substring(email1.indexOf(64) + 1);
                if (withinDomain(_sub2, email1)) {
                    union.add(email1);
                    return;
                } else {
                    union.add(email1);
                    union.add(email2);
                    return;
                }
            }
            if (email2.startsWith(".")) {
                if (withinDomain(email1, email2) || email1.equalsIgnoreCase(email2)) {
                    union.add(email2);
                    return;
                } else if (withinDomain(email2, email1)) {
                    union.add(email1);
                    return;
                } else {
                    union.add(email1);
                    union.add(email2);
                    return;
                }
            }
            if (withinDomain(email2, email1)) {
                union.add(email1);
                return;
            } else {
                union.add(email1);
                union.add(email2);
                return;
            }
        }
        if (email2.indexOf(64) != -1) {
            String _sub3 = email2.substring(email1.indexOf(64) + 1);
            if (_sub3.equalsIgnoreCase(email1)) {
                union.add(email1);
                return;
            } else {
                union.add(email1);
                union.add(email2);
                return;
            }
        }
        if (email2.startsWith(".")) {
            if (withinDomain(email1, email2)) {
                union.add(email2);
                return;
            } else {
                union.add(email1);
                union.add(email2);
                return;
            }
        }
        if (email1.equalsIgnoreCase(email2)) {
            union.add(email1);
        } else {
            union.add(email1);
            union.add(email2);
        }
    }

    private Set intersectDNS(Set permitted, Set dnss) {
        Set intersect = new HashSet();
        Iterator it = dnss.iterator();
        while (it.hasNext()) {
            String dns = extractNameAsString(((GeneralSubtree) it.next()).getBase());
            if (permitted == null) {
                if (dns != null) {
                    intersect.add(dns);
                }
            } else {
                Iterator _iter = permitted.iterator();
                while (_iter.hasNext()) {
                    String _permitted = (String) _iter.next();
                    if (withinDomain(_permitted, dns)) {
                        intersect.add(_permitted);
                    } else if (withinDomain(dns, _permitted)) {
                        intersect.add(dns);
                    }
                }
            }
        }
        return intersect;
    }

    protected Set unionDNS(Set excluded, String dns) {
        if (excluded.isEmpty()) {
            if (dns == null) {
                return excluded;
            }
            excluded.add(dns);
            return excluded;
        }
        Set union = new HashSet();
        Iterator _iter = excluded.iterator();
        while (_iter.hasNext()) {
            String _permitted = (String) _iter.next();
            if (withinDomain(_permitted, dns)) {
                union.add(dns);
            } else if (withinDomain(dns, _permitted)) {
                union.add(_permitted);
            } else {
                union.add(_permitted);
                union.add(dns);
            }
        }
        return union;
    }

    private void intersectEmail(String email1, String email2, Set intersect) {
        if (email1.indexOf(64) != -1) {
            String _sub = email1.substring(email1.indexOf(64) + 1);
            if (email2.indexOf(64) != -1) {
                if (email1.equalsIgnoreCase(email2)) {
                    intersect.add(email1);
                    return;
                }
                return;
            } else if (email2.startsWith(".")) {
                if (withinDomain(_sub, email2)) {
                    intersect.add(email1);
                    return;
                }
                return;
            } else {
                if (_sub.equalsIgnoreCase(email2)) {
                    intersect.add(email1);
                    return;
                }
                return;
            }
        }
        if (email1.startsWith(".")) {
            if (email2.indexOf(64) != -1) {
                String _sub2 = email2.substring(email1.indexOf(64) + 1);
                if (withinDomain(_sub2, email1)) {
                    intersect.add(email2);
                    return;
                }
                return;
            }
            if (email2.startsWith(".")) {
                if (withinDomain(email1, email2) || email1.equalsIgnoreCase(email2)) {
                    intersect.add(email1);
                    return;
                } else {
                    if (withinDomain(email2, email1)) {
                        intersect.add(email2);
                        return;
                    }
                    return;
                }
            }
            if (withinDomain(email2, email1)) {
                intersect.add(email2);
                return;
            }
            return;
        }
        if (email2.indexOf(64) != -1) {
            String _sub3 = email2.substring(email2.indexOf(64) + 1);
            if (_sub3.equalsIgnoreCase(email1)) {
                intersect.add(email2);
                return;
            }
            return;
        }
        if (email2.startsWith(".")) {
            if (withinDomain(email1, email2)) {
                intersect.add(email1);
            }
        } else if (email1.equalsIgnoreCase(email2)) {
            intersect.add(email1);
        }
    }

    private void checkExcludedURI(Set excluded, String uri) throws PKIXNameConstraintValidatorException {
        if (excluded.isEmpty()) {
            return;
        }
        Iterator it = excluded.iterator();
        while (it.hasNext()) {
            String str = (String) it.next();
            if (isUriConstrained(uri, str)) {
                throw new PKIXNameConstraintValidatorException("URI is from an excluded subtree.");
            }
        }
    }

    private Set intersectURI(Set permitted, Set uris) {
        Set intersect = new HashSet();
        Iterator it = uris.iterator();
        while (it.hasNext()) {
            String uri = extractNameAsString(((GeneralSubtree) it.next()).getBase());
            if (permitted == null) {
                if (uri != null) {
                    intersect.add(uri);
                }
            } else {
                Iterator _iter = permitted.iterator();
                while (_iter.hasNext()) {
                    String _permitted = (String) _iter.next();
                    intersectURI(_permitted, uri, intersect);
                }
            }
        }
        return intersect;
    }

    private Set unionURI(Set excluded, String uri) {
        if (excluded.isEmpty()) {
            if (uri == null) {
                return excluded;
            }
            excluded.add(uri);
            return excluded;
        }
        Set union = new HashSet();
        Iterator _iter = excluded.iterator();
        while (_iter.hasNext()) {
            String _excluded = (String) _iter.next();
            unionURI(_excluded, uri, union);
        }
        return union;
    }

    private void intersectURI(String email1, String email2, Set intersect) {
        if (email1.indexOf(64) != -1) {
            String _sub = email1.substring(email1.indexOf(64) + 1);
            if (email2.indexOf(64) != -1) {
                if (email1.equalsIgnoreCase(email2)) {
                    intersect.add(email1);
                    return;
                }
                return;
            } else if (email2.startsWith(".")) {
                if (withinDomain(_sub, email2)) {
                    intersect.add(email1);
                    return;
                }
                return;
            } else {
                if (_sub.equalsIgnoreCase(email2)) {
                    intersect.add(email1);
                    return;
                }
                return;
            }
        }
        if (email1.startsWith(".")) {
            if (email2.indexOf(64) != -1) {
                String _sub2 = email2.substring(email1.indexOf(64) + 1);
                if (withinDomain(_sub2, email1)) {
                    intersect.add(email2);
                    return;
                }
                return;
            }
            if (email2.startsWith(".")) {
                if (withinDomain(email1, email2) || email1.equalsIgnoreCase(email2)) {
                    intersect.add(email1);
                    return;
                } else {
                    if (withinDomain(email2, email1)) {
                        intersect.add(email2);
                        return;
                    }
                    return;
                }
            }
            if (withinDomain(email2, email1)) {
                intersect.add(email2);
                return;
            }
            return;
        }
        if (email2.indexOf(64) != -1) {
            String _sub3 = email2.substring(email2.indexOf(64) + 1);
            if (_sub3.equalsIgnoreCase(email1)) {
                intersect.add(email2);
                return;
            }
            return;
        }
        if (email2.startsWith(".")) {
            if (withinDomain(email1, email2)) {
                intersect.add(email1);
            }
        } else if (email1.equalsIgnoreCase(email2)) {
            intersect.add(email1);
        }
    }

    private void checkPermittedURI(Set permitted, String uri) throws PKIXNameConstraintValidatorException {
        if (permitted == null) {
            return;
        }
        Iterator it = permitted.iterator();
        while (it.hasNext()) {
            String str = (String) it.next();
            if (isUriConstrained(uri, str)) {
                return;
            }
        }
        if (uri.length() == 0 && permitted.size() == 0) {
        } else {
            throw new PKIXNameConstraintValidatorException("URI is not from a permitted subtree.");
        }
    }

    private boolean isUriConstrained(String uri, String constraint) {
        String host = extractHostFromURL(uri);
        if (!constraint.startsWith(".")) {
            if (host.equalsIgnoreCase(constraint)) {
                return true;
            }
            return false;
        }
        if (withinDomain(host, constraint)) {
            return true;
        }
        return false;
    }

    private static String extractHostFromURL(String url) {
        String sub = url.substring(url.indexOf(58) + 1);
        if (sub.indexOf("//") != -1) {
            sub = sub.substring(sub.indexOf("//") + 2);
        }
        if (sub.lastIndexOf(58) != -1) {
            sub = sub.substring(0, sub.lastIndexOf(58));
        }
        String sub2 = sub.substring(sub.indexOf(58) + 1);
        String sub3 = sub2.substring(sub2.indexOf(64) + 1);
        if (sub3.indexOf(47) != -1) {
            sub3 = sub3.substring(0, sub3.indexOf(47));
        }
        return sub3;
    }

    public void checkPermitted(GeneralName name) throws PKIXNameConstraintValidatorException {
        switch (name.getTagNo()) {
            case 1:
                checkPermittedEmail(this.permittedSubtreesEmail, extractNameAsString(name));
                break;
            case 2:
                checkPermittedDNS(this.permittedSubtreesDNS, DERIA5String.getInstance(name.getName()).getString());
                break;
            case 4:
                checkPermittedDN(ASN1Sequence.getInstance(name.getName().toASN1Primitive()));
                break;
            case 6:
                checkPermittedURI(this.permittedSubtreesURI, DERIA5String.getInstance(name.getName()).getString());
                break;
            case 7:
                byte[] ip = ASN1OctetString.getInstance(name.getName()).getOctets();
                checkPermittedIP(this.permittedSubtreesIP, ip);
                break;
        }
    }

    public void checkExcluded(GeneralName name) throws PKIXNameConstraintValidatorException {
        switch (name.getTagNo()) {
            case 1:
                checkExcludedEmail(this.excludedSubtreesEmail, extractNameAsString(name));
                break;
            case 2:
                checkExcludedDNS(this.excludedSubtreesDNS, DERIA5String.getInstance(name.getName()).getString());
                break;
            case 4:
                checkExcludedDN(ASN1Sequence.getInstance(name.getName().toASN1Primitive()));
                break;
            case 6:
                checkExcludedURI(this.excludedSubtreesURI, DERIA5String.getInstance(name.getName()).getString());
                break;
            case 7:
                byte[] ip = ASN1OctetString.getInstance(name.getName()).getOctets();
                checkExcludedIP(this.excludedSubtreesIP, ip);
                break;
        }
    }

    public void intersectPermittedSubtree(GeneralSubtree permitted) {
        intersectPermittedSubtree(new GeneralSubtree[]{permitted});
    }

    public void intersectPermittedSubtree(GeneralSubtree[] permitted) {
        Map subtreesMap = new HashMap();
        for (int i = 0; i != permitted.length; i++) {
            GeneralSubtree subtree = permitted[i];
            Integer tagNo = Integers.valueOf(subtree.getBase().getTagNo());
            if (subtreesMap.get(tagNo) == null) {
                subtreesMap.put(tagNo, new HashSet());
            }
            ((Set) subtreesMap.get(tagNo)).add(subtree);
        }
        for (Map.Entry entry : subtreesMap.entrySet()) {
            switch (((Integer) entry.getKey()).intValue()) {
                case 1:
                    this.permittedSubtreesEmail = intersectEmail(this.permittedSubtreesEmail, (Set) entry.getValue());
                    break;
                case 2:
                    this.permittedSubtreesDNS = intersectDNS(this.permittedSubtreesDNS, (Set) entry.getValue());
                    break;
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

    private String extractNameAsString(GeneralName name) {
        return DERIA5String.getInstance(name.getName()).getString();
    }

    public void intersectEmptyPermittedSubtree(int nameType) {
        switch (nameType) {
            case 1:
                this.permittedSubtreesEmail = new HashSet();
                break;
            case 2:
                this.permittedSubtreesDNS = new HashSet();
                break;
            case 4:
                this.permittedSubtreesDN = new HashSet();
                break;
            case 6:
                this.permittedSubtreesURI = new HashSet();
                break;
            case 7:
                this.permittedSubtreesIP = new HashSet();
                break;
        }
    }

    public void addExcludedSubtree(GeneralSubtree subtree) {
        GeneralName base = subtree.getBase();
        switch (base.getTagNo()) {
            case 1:
                this.excludedSubtreesEmail = unionEmail(this.excludedSubtreesEmail, extractNameAsString(base));
                break;
            case 2:
                this.excludedSubtreesDNS = unionDNS(this.excludedSubtreesDNS, extractNameAsString(base));
                break;
            case 4:
                this.excludedSubtreesDN = unionDN(this.excludedSubtreesDN, (ASN1Sequence) base.getName().toASN1Primitive());
                break;
            case 6:
                this.excludedSubtreesURI = unionURI(this.excludedSubtreesURI, extractNameAsString(base));
                break;
            case 7:
                this.excludedSubtreesIP = unionIP(this.excludedSubtreesIP, ASN1OctetString.getInstance(base.getName()).getOctets());
                break;
        }
    }

    private static byte[] max(byte[] ip1, byte[] ip2) {
        for (int i = 0; i < ip1.length; i++) {
            if ((ip1[i] & 65535) > (ip2[i] & 65535)) {
                return ip1;
            }
        }
        return ip2;
    }

    private static byte[] min(byte[] ip1, byte[] ip2) {
        for (int i = 0; i < ip1.length; i++) {
            if ((ip1[i] & 65535) < (ip2[i] & 65535)) {
                return ip1;
            }
        }
        return ip2;
    }

    private static int compareTo(byte[] ip1, byte[] ip2) {
        if (Arrays.areEqual(ip1, ip2)) {
            return 0;
        }
        if (Arrays.areEqual(max(ip1, ip2), ip1)) {
            return 1;
        }
        return -1;
    }

    private static byte[] or(byte[] ip1, byte[] ip2) {
        byte[] temp = new byte[ip1.length];
        for (int i = 0; i < ip1.length; i++) {
            temp[i] = (byte) (ip1[i] | ip2[i]);
        }
        return temp;
    }

    public int hashCode() {
        return hashCollection(this.excludedSubtreesDN) + hashCollection(this.excludedSubtreesDNS) + hashCollection(this.excludedSubtreesEmail) + hashCollection(this.excludedSubtreesIP) + hashCollection(this.excludedSubtreesURI) + hashCollection(this.permittedSubtreesDN) + hashCollection(this.permittedSubtreesDNS) + hashCollection(this.permittedSubtreesEmail) + hashCollection(this.permittedSubtreesIP) + hashCollection(this.permittedSubtreesURI);
    }

    private int hashCollection(Collection coll) {
        if (coll == null) {
            return 0;
        }
        int hash = 0;
        for (Object o : coll) {
            if (o instanceof byte[]) {
                hash += Arrays.hashCode((byte[]) o);
            } else {
                hash += o.hashCode();
            }
        }
        return hash;
    }

    public boolean equals(Object o) {
        if (!(o instanceof PKIXNameConstraintValidator)) {
            return false;
        }
        PKIXNameConstraintValidator constraintValidator = (PKIXNameConstraintValidator) o;
        return collectionsAreEqual(constraintValidator.excludedSubtreesDN, this.excludedSubtreesDN) && collectionsAreEqual(constraintValidator.excludedSubtreesDNS, this.excludedSubtreesDNS) && collectionsAreEqual(constraintValidator.excludedSubtreesEmail, this.excludedSubtreesEmail) && collectionsAreEqual(constraintValidator.excludedSubtreesIP, this.excludedSubtreesIP) && collectionsAreEqual(constraintValidator.excludedSubtreesURI, this.excludedSubtreesURI) && collectionsAreEqual(constraintValidator.permittedSubtreesDN, this.permittedSubtreesDN) && collectionsAreEqual(constraintValidator.permittedSubtreesDNS, this.permittedSubtreesDNS) && collectionsAreEqual(constraintValidator.permittedSubtreesEmail, this.permittedSubtreesEmail) && collectionsAreEqual(constraintValidator.permittedSubtreesIP, this.permittedSubtreesIP) && collectionsAreEqual(constraintValidator.permittedSubtreesURI, this.permittedSubtreesURI);
    }

    private boolean collectionsAreEqual(Collection coll1, Collection coll2) {
        if (coll1 == coll2) {
            return true;
        }
        if (coll1 == null || coll2 == null || coll1.size() != coll2.size()) {
            return false;
        }
        for (Object a : coll1) {
            boolean found = false;
            for (Object b : coll2) {
                if (equals(a, b)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return false;
            }
        }
        return true;
    }

    private boolean equals(Object o1, Object o2) {
        if (o1 == o2) {
            return true;
        }
        if (o1 == null || o2 == null) {
            return false;
        }
        if ((o1 instanceof byte[]) && (o2 instanceof byte[])) {
            return Arrays.areEqual((byte[]) o1, (byte[]) o2);
        }
        return o1.equals(o2);
    }

    private String stringifyIP(byte[] ip) {
        String temp = "";
        for (int i = 0; i < ip.length / 2; i++) {
            temp = temp + Integer.toString(ip[i] & 255) + ".";
        }
        String temp2 = temp.substring(0, temp.length() - 1) + "/";
        for (int i2 = ip.length / 2; i2 < ip.length; i2++) {
            temp2 = temp2 + Integer.toString(ip[i2] & 255) + ".";
        }
        return temp2.substring(0, temp2.length() - 1);
    }

    private String stringifyIPCollection(Set ips) {
        String temp = "[";
        Iterator it = ips.iterator();
        while (it.hasNext()) {
            temp = temp + stringifyIP((byte[]) it.next()) + ",";
        }
        if (temp.length() > 1) {
            temp = temp.substring(0, temp.length() - 1);
        }
        return temp + "]";
    }

    public String toString() {
        String temp = "permitted:\n";
        if (this.permittedSubtreesDN != null) {
            temp = (temp + "DN:\n") + this.permittedSubtreesDN.toString() + "\n";
        }
        if (this.permittedSubtreesDNS != null) {
            temp = (temp + "DNS:\n") + this.permittedSubtreesDNS.toString() + "\n";
        }
        if (this.permittedSubtreesEmail != null) {
            temp = (temp + "Email:\n") + this.permittedSubtreesEmail.toString() + "\n";
        }
        if (this.permittedSubtreesURI != null) {
            temp = (temp + "URI:\n") + this.permittedSubtreesURI.toString() + "\n";
        }
        if (this.permittedSubtreesIP != null) {
            temp = (temp + "IP:\n") + stringifyIPCollection(this.permittedSubtreesIP) + "\n";
        }
        String temp2 = temp + "excluded:\n";
        if (!this.excludedSubtreesDN.isEmpty()) {
            temp2 = (temp2 + "DN:\n") + this.excludedSubtreesDN.toString() + "\n";
        }
        if (!this.excludedSubtreesDNS.isEmpty()) {
            temp2 = (temp2 + "DNS:\n") + this.excludedSubtreesDNS.toString() + "\n";
        }
        if (!this.excludedSubtreesEmail.isEmpty()) {
            temp2 = (temp2 + "Email:\n") + this.excludedSubtreesEmail.toString() + "\n";
        }
        if (!this.excludedSubtreesURI.isEmpty()) {
            temp2 = (temp2 + "URI:\n") + this.excludedSubtreesURI.toString() + "\n";
        }
        if (!this.excludedSubtreesIP.isEmpty()) {
            temp2 = (temp2 + "IP:\n") + stringifyIPCollection(this.excludedSubtreesIP) + "\n";
        }
        return temp2;
    }
}
