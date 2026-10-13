package org.apache.pdfbox.pdmodel.graphics;

import java.io.IOException;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.ResourceCache;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.pdmodel.graphics.form.PDTransparencyGroup;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/graphics/PDXObject.class */
public class PDXObject implements COSObjectable {
    private final PDStream stream;

    public static PDXObject createXObject(COSBase base, PDResources resources) throws IOException {
        if (base == null) {
            return null;
        }
        if (!(base instanceof COSStream)) {
            throw new IOException("Unexpected object type: " + base.getClass().getName());
        }
        COSStream stream = (COSStream) base;
        String subtype = stream.getNameAsString(COSName.SUBTYPE);
        if (COSName.IMAGE.getName().equals(subtype)) {
            return new PDImageXObject(new PDStream(stream), resources);
        }
        if (COSName.FORM.getName().equals(subtype)) {
            ResourceCache cache = resources != null ? resources.getResourceCache() : null;
            COSDictionary group = (COSDictionary) stream.getDictionaryObject(COSName.GROUP);
            if (group != null && COSName.TRANSPARENCY.equals(group.getCOSName(COSName.S))) {
                return new PDTransparencyGroup(stream, cache);
            }
            return new PDFormXObject(stream, cache);
        }
        if (COSName.PS.getName().equals(subtype)) {
            return new PDPostScriptXObject(stream);
        }
        throw new IOException("Invalid XObject Subtype: " + subtype);
    }

    protected PDXObject(COSStream stream, COSName subtype) {
        this.stream = new PDStream(stream);
        stream.setName(COSName.TYPE, COSName.XOBJECT.getName());
        stream.setName(COSName.SUBTYPE, subtype.getName());
    }

    protected PDXObject(PDStream stream, COSName subtype) {
        this.stream = stream;
        stream.getCOSObject().setName(COSName.TYPE, COSName.XOBJECT.getName());
        stream.getCOSObject().setName(COSName.SUBTYPE, subtype.getName());
    }

    protected PDXObject(PDDocument document, COSName subtype) {
        this.stream = new PDStream(document);
        this.stream.getCOSObject().setName(COSName.TYPE, COSName.XOBJECT.getName());
        this.stream.getCOSObject().setName(COSName.SUBTYPE, subtype.getName());
    }

    @Override // org.apache.pdfbox.pdmodel.common.COSObjectable
    public final COSStream getCOSObject() {
        return this.stream.getCOSObject();
    }

    @Deprecated
    public final COSStream getCOSStream() {
        return this.stream.getCOSObject();
    }

    @Deprecated
    public final PDStream getPDStream() {
        return this.stream;
    }

    public final PDStream getStream() {
        return this.stream;
    }
}
