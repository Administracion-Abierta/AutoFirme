package com.twelvemonkeys.imageio.metadata.xmp;

import com.twelvemonkeys.imageio.metadata.AbstractEntry;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.pdfbox.pdmodel.documentinterchange.taggedpdf.PDListAttributeObject;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/metadata/xmp/XMPEntry.class */
final class XMPEntry extends AbstractEntry {
    private final String fieldName;

    public XMPEntry(String str, Object obj) {
        this(str, null, obj);
    }

    public XMPEntry(String str, String str2, Object obj) {
        super(str, obj);
        this.fieldName = str2;
    }

    @Override // com.twelvemonkeys.imageio.metadata.AbstractEntry
    protected String getNativeIdentifier() {
        String str = (String) getIdentifier();
        String str2 = (this.fieldName == null || !str.endsWith(this.fieldName)) ? null : XMP.DEFAULT_NS_MAPPING.get(str.substring(0, str.length() - this.fieldName.length()));
        return str2 != null ? str2 + ":" + this.fieldName : str;
    }

    @Override // com.twelvemonkeys.imageio.metadata.AbstractEntry, com.twelvemonkeys.imageio.metadata.Entry
    public String getFieldName() {
        return this.fieldName != null ? this.fieldName : XMP.DEFAULT_NS_MAPPING.get(getIdentifier());
    }

    @Override // com.twelvemonkeys.imageio.metadata.AbstractEntry, com.twelvemonkeys.imageio.metadata.Entry
    public String getTypeName() {
        Object value = getValue();
        if (value instanceof List) {
            return PDListAttributeObject.OWNER_LIST;
        }
        if (value instanceof Set) {
            return "Set";
        }
        if (value instanceof Map) {
            return "Map";
        }
        return super.getTypeName();
    }

    @Override // com.twelvemonkeys.imageio.metadata.AbstractEntry
    public String toString() {
        String typeName = getTypeName();
        return String.format("%s: %s%s", getNativeIdentifier(), getValueAsString(), typeName != null ? " (" + typeName + ")" : "");
    }
}
