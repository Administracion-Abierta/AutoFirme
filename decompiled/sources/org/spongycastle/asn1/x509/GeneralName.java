package org.spongycastle.asn1.x509;

import java.io.IOException;
import java.util.StringTokenizer;
import org.spongycastle.asn1.ASN1Choice;
import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.ASN1OctetString;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.ASN1TaggedObject;
import org.spongycastle.asn1.DERIA5String;
import org.spongycastle.asn1.DEROctetString;
import org.spongycastle.asn1.DERTaggedObject;
import org.spongycastle.asn1.x500.X500Name;
import org.spongycastle.util.IPAddress;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/asn1/x509/GeneralName.class */
public class GeneralName extends ASN1Object implements ASN1Choice {
    public static final int otherName = 0;
    public static final int rfc822Name = 1;
    public static final int dNSName = 2;
    public static final int x400Address = 3;
    public static final int directoryName = 4;
    public static final int ediPartyName = 5;
    public static final int uniformResourceIdentifier = 6;
    public static final int iPAddress = 7;
    public static final int registeredID = 8;
    private ASN1Encodable obj;
    private int tag;

    public GeneralName(X509Name dirName) {
        this.obj = X500Name.getInstance(dirName);
        this.tag = 4;
    }

    public GeneralName(X500Name dirName) {
        this.obj = dirName;
        this.tag = 4;
    }

    public GeneralName(int tag, ASN1Encodable name) {
        this.obj = name;
        this.tag = tag;
    }

    public GeneralName(int tag, String name) {
        this.tag = tag;
        if (tag == 1 || tag == 2 || tag == 6) {
            this.obj = new DERIA5String(name);
            return;
        }
        if (tag == 8) {
            this.obj = new ASN1ObjectIdentifier(name);
            return;
        }
        if (tag == 4) {
            this.obj = new X500Name(name);
        } else {
            if (tag == 7) {
                byte[] enc = toGeneralNameEncoding(name);
                if (enc != null) {
                    this.obj = new DEROctetString(enc);
                    return;
                }
                throw new IllegalArgumentException("IP Address is invalid");
            }
            throw new IllegalArgumentException("can't process String for tag: " + tag);
        }
    }

    public static GeneralName getInstance(Object obj) {
        if (obj == null || (obj instanceof GeneralName)) {
            return (GeneralName) obj;
        }
        if (obj instanceof ASN1TaggedObject) {
            ASN1TaggedObject tagObj = (ASN1TaggedObject) obj;
            int tag = tagObj.getTagNo();
            switch (tag) {
                case 0:
                    return new GeneralName(tag, ASN1Sequence.getInstance(tagObj, false));
                case 1:
                    return new GeneralName(tag, DERIA5String.getInstance(tagObj, false));
                case 2:
                    return new GeneralName(tag, DERIA5String.getInstance(tagObj, false));
                case 3:
                    throw new IllegalArgumentException("unknown tag: " + tag);
                case 4:
                    return new GeneralName(tag, X500Name.getInstance(tagObj, true));
                case 5:
                    return new GeneralName(tag, ASN1Sequence.getInstance(tagObj, false));
                case 6:
                    return new GeneralName(tag, DERIA5String.getInstance(tagObj, false));
                case 7:
                    return new GeneralName(tag, ASN1OctetString.getInstance(tagObj, false));
                case 8:
                    return new GeneralName(tag, ASN1ObjectIdentifier.getInstance(tagObj, false));
            }
        }
        if (obj instanceof byte[]) {
            try {
                return getInstance(ASN1Primitive.fromByteArray((byte[]) obj));
            } catch (IOException e) {
                throw new IllegalArgumentException("unable to parse encoded general name");
            }
        }
        throw new IllegalArgumentException("unknown object in getInstance: " + obj.getClass().getName());
    }

    public static GeneralName getInstance(ASN1TaggedObject tagObj, boolean explicit) {
        return getInstance(ASN1TaggedObject.getInstance(tagObj, true));
    }

    public int getTagNo() {
        return this.tag;
    }

    public ASN1Encodable getName() {
        return this.obj;
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.tag);
        buf.append(": ");
        switch (this.tag) {
            case 1:
            case 2:
            case 6:
                buf.append(DERIA5String.getInstance(this.obj).getString());
                break;
            case 3:
            case 5:
            default:
                buf.append(this.obj.toString());
                break;
            case 4:
                buf.append(X500Name.getInstance(this.obj).toString());
                break;
        }
        return buf.toString();
    }

    private byte[] toGeneralNameEncoding(String ip) {
        int[] parsedIp;
        if (IPAddress.isValidIPv6WithNetmask(ip) || IPAddress.isValidIPv6(ip)) {
            int slashIndex = ip.indexOf(47);
            if (slashIndex < 0) {
                byte[] addr = new byte[16];
                int[] parsedIp2 = parseIPv6(ip);
                copyInts(parsedIp2, addr, 0);
                return addr;
            }
            byte[] addr2 = new byte[32];
            int[] parsedIp3 = parseIPv6(ip.substring(0, slashIndex));
            copyInts(parsedIp3, addr2, 0);
            String mask = ip.substring(slashIndex + 1);
            if (mask.indexOf(58) > 0) {
                parsedIp = parseIPv6(mask);
            } else {
                parsedIp = parseMask(mask);
            }
            copyInts(parsedIp, addr2, 16);
            return addr2;
        }
        if (IPAddress.isValidIPv4WithNetmask(ip) || IPAddress.isValidIPv4(ip)) {
            int slashIndex2 = ip.indexOf(47);
            if (slashIndex2 < 0) {
                byte[] addr3 = new byte[4];
                parseIPv4(ip, addr3, 0);
                return addr3;
            }
            byte[] addr4 = new byte[8];
            parseIPv4(ip.substring(0, slashIndex2), addr4, 0);
            String mask2 = ip.substring(slashIndex2 + 1);
            if (mask2.indexOf(46) > 0) {
                parseIPv4(mask2, addr4, 4);
            } else {
                parseIPv4Mask(mask2, addr4, 4);
            }
            return addr4;
        }
        return null;
    }

    private void parseIPv4Mask(String mask, byte[] addr, int offset) {
        int maskVal = Integer.parseInt(mask);
        for (int i = 0; i != maskVal; i++) {
            int i2 = (i / 8) + offset;
            addr[i2] = (byte) (addr[i2] | (1 << (7 - (i % 8))));
        }
    }

    private void parseIPv4(String ip, byte[] addr, int offset) {
        StringTokenizer sTok = new StringTokenizer(ip, "./");
        int index = 0;
        while (sTok.hasMoreTokens()) {
            int i = index;
            index++;
            addr[offset + i] = (byte) Integer.parseInt(sTok.nextToken());
        }
    }

    private int[] parseMask(String mask) {
        int[] res = new int[8];
        int maskVal = Integer.parseInt(mask);
        for (int i = 0; i != maskVal; i++) {
            int i2 = i / 16;
            res[i2] = res[i2] | (1 << (15 - (i % 16)));
        }
        return res;
    }

    private void copyInts(int[] parsedIp, byte[] addr, int offSet) {
        for (int i = 0; i != parsedIp.length; i++) {
            addr[(i * 2) + offSet] = (byte) (parsedIp[i] >> 8);
            addr[(i * 2) + 1 + offSet] = (byte) parsedIp[i];
        }
    }

    private int[] parseIPv6(String ip) {
        StringTokenizer sTok = new StringTokenizer(ip, ":", true);
        int index = 0;
        int[] val = new int[8];
        if (ip.charAt(0) == ':' && ip.charAt(1) == ':') {
            sTok.nextToken();
        }
        int doubleColon = -1;
        while (sTok.hasMoreTokens()) {
            String e = sTok.nextToken();
            if (e.equals(":")) {
                doubleColon = index;
                int i = index;
                index++;
                val[i] = 0;
            } else if (e.indexOf(46) < 0) {
                int i2 = index;
                index++;
                val[i2] = Integer.parseInt(e, 16);
                if (sTok.hasMoreTokens()) {
                    sTok.nextToken();
                }
            } else {
                StringTokenizer eTok = new StringTokenizer(e, ".");
                int i3 = index;
                int index2 = index + 1;
                val[i3] = (Integer.parseInt(eTok.nextToken()) << 8) | Integer.parseInt(eTok.nextToken());
                index = index2 + 1;
                val[index2] = (Integer.parseInt(eTok.nextToken()) << 8) | Integer.parseInt(eTok.nextToken());
            }
        }
        if (index != val.length) {
            System.arraycopy(val, doubleColon, val, val.length - (index - doubleColon), index - doubleColon);
            for (int i4 = doubleColon; i4 != val.length - (index - doubleColon); i4++) {
                val[i4] = 0;
            }
        }
        return val;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        if (this.tag == 4) {
            return new DERTaggedObject(true, this.tag, this.obj);
        }
        return new DERTaggedObject(false, this.tag, this.obj);
    }
}
