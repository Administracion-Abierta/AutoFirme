package org.spongycastle.asn1.x509;

import org.spongycastle.asn1.ASN1Choice;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1String;
import org.spongycastle.asn1.ASN1TaggedObject;
import org.spongycastle.asn1.DERBMPString;
import org.spongycastle.asn1.DERIA5String;
import org.spongycastle.asn1.DERUTF8String;
import org.spongycastle.asn1.DERVisibleString;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/asn1/x509/DisplayText.class */
public class DisplayText extends ASN1Object implements ASN1Choice {
    public static final int CONTENT_TYPE_IA5STRING = 0;
    public static final int CONTENT_TYPE_BMPSTRING = 1;
    public static final int CONTENT_TYPE_UTF8STRING = 2;
    public static final int CONTENT_TYPE_VISIBLESTRING = 3;
    public static final int DISPLAY_TEXT_MAXIMUM_SIZE = 200;
    int contentType;
    ASN1String contents;

    public DisplayText(int type, String text) {
        text = text.length() > 200 ? text.substring(0, DISPLAY_TEXT_MAXIMUM_SIZE) : text;
        this.contentType = type;
        switch (type) {
            case 0:
                this.contents = new DERIA5String(text);
                break;
            case 1:
                this.contents = new DERBMPString(text);
                break;
            case 2:
                this.contents = new DERUTF8String(text);
                break;
            case 3:
                this.contents = new DERVisibleString(text);
                break;
            default:
                this.contents = new DERUTF8String(text);
                break;
        }
    }

    public DisplayText(String text) {
        text = text.length() > 200 ? text.substring(0, DISPLAY_TEXT_MAXIMUM_SIZE) : text;
        this.contentType = 2;
        this.contents = new DERUTF8String(text);
    }

    private DisplayText(ASN1String de) {
        this.contents = de;
        if (de instanceof DERUTF8String) {
            this.contentType = 2;
            return;
        }
        if (de instanceof DERBMPString) {
            this.contentType = 1;
        } else if (de instanceof DERIA5String) {
            this.contentType = 0;
        } else {
            if (de instanceof DERVisibleString) {
                this.contentType = 3;
                return;
            }
            throw new IllegalArgumentException("unknown STRING type in DisplayText");
        }
    }

    public static DisplayText getInstance(Object obj) {
        if (obj instanceof ASN1String) {
            return new DisplayText((ASN1String) obj);
        }
        if (obj == null || (obj instanceof DisplayText)) {
            return (DisplayText) obj;
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + obj.getClass().getName());
    }

    public static DisplayText getInstance(ASN1TaggedObject obj, boolean explicit) {
        return getInstance(obj.getObject());
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return (ASN1Primitive) this.contents;
    }

    public String getString() {
        return this.contents.getString();
    }
}
