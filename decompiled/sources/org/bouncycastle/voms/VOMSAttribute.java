package org.bouncycastle.voms;

import java.util.ArrayList;
import java.util.List;
import org.bouncycastle.asn1.ASN1IA5String;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.x509.Attribute;
import org.bouncycastle.asn1.x509.IetfAttrSyntax;
import org.bouncycastle.cert.X509AttributeCertificateHolder;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/voms/VOMSAttribute.class */
public class VOMSAttribute {
    public static final String VOMS_ATTR_OID = "1.3.6.1.4.1.8005.100.100.4";
    private X509AttributeCertificateHolder myAC;
    private String myHostPort;
    private String myVo;
    private List myStringList = new ArrayList();
    private List myFQANs = new ArrayList();

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/voms/VOMSAttribute$FQAN.class */
    public static class FQAN {
        String fqan;
        String group;
        String role;
        String capability;

        public FQAN(String str) {
            this.fqan = str;
        }

        public FQAN(String str, String str2, String str3) {
            this.group = str;
            this.role = str2;
            this.capability = str3;
        }

        public String getFQAN() {
            if (this.fqan != null) {
                return this.fqan;
            }
            this.fqan = this.group + "/Role=" + (this.role != null ? this.role : "") + (this.capability != null ? "/Capability=" + this.capability : "");
            return this.fqan;
        }

        protected void split() {
            this.fqan.length();
            int iIndexOf = this.fqan.indexOf("/Role=");
            if (iIndexOf < 0) {
                return;
            }
            this.group = this.fqan.substring(0, iIndexOf);
            int iIndexOf2 = this.fqan.indexOf("/Capability=", iIndexOf + 6);
            String strSubstring = iIndexOf2 < 0 ? this.fqan.substring(iIndexOf + 6) : this.fqan.substring(iIndexOf + 6, iIndexOf2);
            this.role = strSubstring.length() == 0 ? null : strSubstring;
            String strSubstring2 = iIndexOf2 < 0 ? null : this.fqan.substring(iIndexOf2 + 12);
            this.capability = (strSubstring2 == null || strSubstring2.length() == 0) ? null : strSubstring2;
        }

        public String getGroup() {
            if (this.group == null && this.fqan != null) {
                split();
            }
            return this.group;
        }

        public String getRole() {
            if (this.group == null && this.fqan != null) {
                split();
            }
            return this.role;
        }

        public String getCapability() {
            if (this.group == null && this.fqan != null) {
                split();
            }
            return this.capability;
        }

        public String toString() {
            return getFQAN();
        }
    }

    public VOMSAttribute(X509AttributeCertificateHolder x509AttributeCertificateHolder) {
        if (x509AttributeCertificateHolder == null) {
            throw new IllegalArgumentException("VOMSAttribute: AttributeCertificate is NULL");
        }
        this.myAC = x509AttributeCertificateHolder;
        Attribute[] attributes = x509AttributeCertificateHolder.getAttributes(new ASN1ObjectIdentifier("1.3.6.1.4.1.8005.100.100.4"));
        if (attributes == null) {
            return;
        }
        for (int i = 0; i != attributes.length; i++) {
            try {
                IetfAttrSyntax ietfAttrSyntax = IetfAttrSyntax.getInstance(attributes[i].getAttributeValues()[0]);
                String string = ((ASN1IA5String) ietfAttrSyntax.getPolicyAuthority().getNames()[0].getName()).getString();
                int iIndexOf = string.indexOf("://");
                if (iIndexOf < 0 || iIndexOf == string.length() - 1) {
                    throw new IllegalArgumentException("Bad encoding of VOMS policyAuthority : [" + string + "]");
                }
                this.myVo = string.substring(0, iIndexOf);
                this.myHostPort = string.substring(iIndexOf + 3);
                if (ietfAttrSyntax.getValueType() != 1) {
                    throw new IllegalArgumentException("VOMS attribute values are not encoded as octet strings, policyAuthority = " + string);
                }
                ASN1OctetString[] aSN1OctetStringArr = (ASN1OctetString[]) ietfAttrSyntax.getValues();
                for (int i2 = 0; i2 != aSN1OctetStringArr.length; i2++) {
                    String str = new String(aSN1OctetStringArr[i2].getOctets());
                    FQAN fqan = new FQAN(str);
                    if (!this.myStringList.contains(str) && str.startsWith("/" + this.myVo + "/")) {
                        this.myStringList.add(str);
                        this.myFQANs.add(fqan);
                    }
                }
            } catch (IllegalArgumentException e) {
                throw e;
            } catch (Exception e2) {
                throw new IllegalArgumentException("Badly encoded VOMS extension in AC issued by " + x509AttributeCertificateHolder.getIssuer());
            }
        }
    }

    public X509AttributeCertificateHolder getAC() {
        return this.myAC;
    }

    public List getFullyQualifiedAttributes() {
        return this.myStringList;
    }

    public List getListOfFQAN() {
        return this.myFQANs;
    }

    public String getHostPort() {
        return this.myHostPort;
    }

    public String getVO() {
        return this.myVo;
    }

    public String toString() {
        return "VO      :" + this.myVo + "\nHostPort:" + this.myHostPort + "\nFQANs   :" + this.myFQANs;
    }
}
