package org.apache.pdfbox.pdmodel;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSObject;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.documentinterchange.markedcontent.PDPropertyList;
import org.apache.pdfbox.pdmodel.documentinterchange.taggedpdf.StandardStructureTypes;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDFontFactory;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace;
import org.apache.pdfbox.pdmodel.graphics.color.PDPattern;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.graphics.optionalcontent.PDOptionalContentGroup;
import org.apache.pdfbox.pdmodel.graphics.pattern.PDAbstractPattern;
import org.apache.pdfbox.pdmodel.graphics.shading.PDShading;
import org.apache.pdfbox.pdmodel.graphics.state.PDExtendedGraphicsState;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/PDResources.class */
public final class PDResources implements COSObjectable {
    private final COSDictionary resources;
    private final ResourceCache cache;
    private final Map<COSName, SoftReference<PDFont>> directFontCache;

    public PDResources() {
        this.directFontCache = new HashMap();
        this.resources = new COSDictionary();
        this.cache = null;
    }

    public PDResources(COSDictionary resourceDictionary) {
        this.directFontCache = new HashMap();
        if (resourceDictionary == null) {
            throw new IllegalArgumentException("resourceDictionary is null");
        }
        this.resources = resourceDictionary;
        this.cache = null;
    }

    public PDResources(COSDictionary resourceDictionary, ResourceCache resourceCache) {
        this.directFontCache = new HashMap();
        if (resourceDictionary == null) {
            throw new IllegalArgumentException("resourceDictionary is null");
        }
        this.resources = resourceDictionary;
        this.cache = resourceCache;
    }

    @Override // org.apache.pdfbox.pdmodel.common.COSObjectable
    public COSDictionary getCOSObject() {
        return this.resources;
    }

    public PDFont getFont(COSName name) throws IOException {
        SoftReference<PDFont> ref;
        PDFont cached;
        COSObject indirect = getIndirect(COSName.FONT, name);
        if (this.cache != null && indirect != null) {
            PDFont cached2 = this.cache.getFont(indirect);
            if (cached2 != null) {
                return cached2;
            }
        } else if (indirect == null && (ref = this.directFontCache.get(name)) != null && (cached = ref.get()) != null) {
            return cached;
        }
        PDFont font = null;
        COSBase base = get(COSName.FONT, name);
        if (base instanceof COSDictionary) {
            font = PDFontFactory.createFont((COSDictionary) base, this.cache);
        }
        if (this.cache != null && indirect != null) {
            this.cache.put(indirect, font);
        } else if (indirect == null) {
            this.directFontCache.put(name, new SoftReference<>(font));
        }
        return font;
    }

    public PDColorSpace getColorSpace(COSName name) throws IOException {
        return getColorSpace(name, false);
    }

    public PDColorSpace getColorSpace(COSName name, boolean wasDefault) throws IOException {
        PDColorSpace colorSpace;
        PDColorSpace cached;
        COSObject indirect = getIndirect(COSName.COLORSPACE, name);
        if (this.cache != null && indirect != null && (cached = this.cache.getColorSpace(indirect)) != null) {
            return cached;
        }
        COSBase object = get(COSName.COLORSPACE, name);
        if (object != null) {
            colorSpace = PDColorSpace.create(object, this, wasDefault);
        } else {
            colorSpace = PDColorSpace.create(name, this, wasDefault);
        }
        if (this.cache != null && indirect != null && !(colorSpace instanceof PDPattern)) {
            this.cache.put(indirect, colorSpace);
        }
        return colorSpace;
    }

    public boolean hasColorSpace(COSName name) {
        return get(COSName.COLORSPACE, name) != null;
    }

    public PDExtendedGraphicsState getExtGState(COSName name) {
        PDExtendedGraphicsState cached;
        COSObject indirect = getIndirect(COSName.EXT_G_STATE, name);
        if (this.cache != null && indirect != null && (cached = this.cache.getExtGState(indirect)) != null) {
            return cached;
        }
        PDExtendedGraphicsState extGState = null;
        COSBase base = get(COSName.EXT_G_STATE, name);
        if (base instanceof COSDictionary) {
            extGState = new PDExtendedGraphicsState((COSDictionary) base);
        }
        if (this.cache != null && indirect != null) {
            this.cache.put(indirect, extGState);
        }
        return extGState;
    }

    public PDShading getShading(COSName name) throws IOException {
        PDShading cached;
        COSObject indirect = getIndirect(COSName.SHADING, name);
        if (this.cache != null && indirect != null && (cached = this.cache.getShading(indirect)) != null) {
            return cached;
        }
        PDShading shading = null;
        COSBase base = get(COSName.SHADING, name);
        if (base instanceof COSDictionary) {
            shading = PDShading.create((COSDictionary) base);
        }
        if (this.cache != null && indirect != null) {
            this.cache.put(indirect, shading);
        }
        return shading;
    }

    public PDAbstractPattern getPattern(COSName name) throws IOException {
        PDAbstractPattern cached;
        COSObject indirect = getIndirect(COSName.PATTERN, name);
        if (this.cache != null && indirect != null && (cached = this.cache.getPattern(indirect)) != null) {
            return cached;
        }
        PDAbstractPattern pattern = null;
        COSBase base = get(COSName.PATTERN, name);
        if (base instanceof COSDictionary) {
            pattern = PDAbstractPattern.create((COSDictionary) base, getResourceCache());
        }
        if (this.cache != null && indirect != null) {
            this.cache.put(indirect, pattern);
        }
        return pattern;
    }

    public PDPropertyList getProperties(COSName name) {
        PDPropertyList cached;
        COSObject indirect = getIndirect(COSName.PROPERTIES, name);
        if (this.cache != null && indirect != null && (cached = this.cache.getProperties(indirect)) != null) {
            return cached;
        }
        PDPropertyList propertyList = null;
        COSBase base = get(COSName.PROPERTIES, name);
        if (base instanceof COSDictionary) {
            propertyList = PDPropertyList.create((COSDictionary) base);
        }
        if (this.cache != null && indirect != null) {
            this.cache.put(indirect, propertyList);
        }
        return propertyList;
    }

    public boolean isImageXObject(COSName name) {
        COSBase value = get(COSName.XOBJECT, name);
        if (value == null) {
            return false;
        }
        if (value instanceof COSObject) {
            value = ((COSObject) value).getObject();
        }
        if (!(value instanceof COSStream)) {
            return false;
        }
        COSStream stream = (COSStream) value;
        return COSName.IMAGE.equals(stream.getCOSName(COSName.SUBTYPE));
    }

    public PDXObject getXObject(COSName name) throws IOException {
        PDXObject xobject;
        PDXObject cached;
        COSObject indirect = getIndirect(COSName.XOBJECT, name);
        if (this.cache != null && indirect != null && (cached = this.cache.getXObject(indirect)) != null) {
            return cached;
        }
        COSBase value = get(COSName.XOBJECT, name);
        if (value == null) {
            xobject = null;
        } else if (value instanceof COSObject) {
            xobject = PDXObject.createXObject(((COSObject) value).getObject(), this);
        } else {
            xobject = PDXObject.createXObject(value, this);
        }
        if (this.cache != null && indirect != null && isAllowedCache(xobject)) {
            this.cache.put(indirect, xobject);
        }
        return xobject;
    }

    private boolean isAllowedCache(PDXObject xobject) {
        if (xobject instanceof PDImageXObject) {
            COSBase colorSpace = xobject.getCOSObject().getDictionaryObject(COSName.COLORSPACE);
            if (colorSpace instanceof COSName) {
                COSName colorSpaceName = (COSName) colorSpace;
                if (colorSpaceName.equals(COSName.DEVICECMYK) && hasColorSpace(COSName.DEFAULT_CMYK)) {
                    return false;
                }
                if (colorSpaceName.equals(COSName.DEVICERGB) && hasColorSpace(COSName.DEFAULT_RGB)) {
                    return false;
                }
                if ((colorSpaceName.equals(COSName.DEVICEGRAY) && hasColorSpace(COSName.DEFAULT_GRAY)) || hasColorSpace(colorSpaceName)) {
                    return false;
                }
                return true;
            }
            return true;
        }
        return true;
    }

    private COSObject getIndirect(COSName kind, COSName name) {
        COSDictionary dict = (COSDictionary) this.resources.getDictionaryObject(kind);
        if (dict == null) {
            return null;
        }
        COSBase base = dict.getItem(name);
        if (base instanceof COSObject) {
            return (COSObject) base;
        }
        return null;
    }

    private COSBase get(COSName kind, COSName name) {
        COSDictionary dict = (COSDictionary) this.resources.getDictionaryObject(kind);
        if (dict == null) {
            return null;
        }
        return dict.getDictionaryObject(name);
    }

    public Iterable<COSName> getColorSpaceNames() {
        return getNames(COSName.COLORSPACE);
    }

    public Iterable<COSName> getXObjectNames() {
        return getNames(COSName.XOBJECT);
    }

    public Iterable<COSName> getFontNames() {
        return getNames(COSName.FONT);
    }

    public Iterable<COSName> getPropertiesNames() {
        return getNames(COSName.PROPERTIES);
    }

    public Iterable<COSName> getShadingNames() {
        return getNames(COSName.SHADING);
    }

    public Iterable<COSName> getPatternNames() {
        return getNames(COSName.PATTERN);
    }

    public Iterable<COSName> getExtGStateNames() {
        return getNames(COSName.EXT_G_STATE);
    }

    private Iterable<COSName> getNames(COSName kind) {
        COSDictionary dict = (COSDictionary) this.resources.getDictionaryObject(kind);
        if (dict == null) {
            return Collections.emptySet();
        }
        return dict.keySet();
    }

    public COSName add(PDFont font) {
        return add(COSName.FONT, "F", font);
    }

    public COSName add(PDColorSpace colorSpace) {
        return add(COSName.COLORSPACE, OperatorName.NON_STROKING_COLORSPACE, colorSpace);
    }

    public COSName add(PDExtendedGraphicsState extGState) {
        return add(COSName.EXT_G_STATE, OperatorName.SET_GRAPHICS_STATE_PARAMS, extGState);
    }

    public COSName add(PDShading shading) {
        return add(COSName.SHADING, OperatorName.SHADING_FILL, shading);
    }

    public COSName add(PDAbstractPattern pattern) {
        return add(COSName.PATTERN, "p", pattern);
    }

    public COSName add(PDPropertyList properties) {
        if (properties instanceof PDOptionalContentGroup) {
            return add(COSName.PROPERTIES, "oc", properties);
        }
        return add(COSName.PROPERTIES, "Prop", properties);
    }

    public COSName add(PDImageXObject image) {
        return add(COSName.XOBJECT, "Im", image);
    }

    public COSName add(PDFormXObject form) {
        return add(COSName.XOBJECT, StandardStructureTypes.FORM, form);
    }

    public COSName add(PDXObject xobject, String prefix) {
        return add(COSName.XOBJECT, prefix, xobject);
    }

    private COSName add(COSName kind, String prefix, COSObjectable object) {
        COSDictionary dict = (COSDictionary) this.resources.getDictionaryObject(kind);
        if (dict != null && dict.containsValue(object.getCOSObject())) {
            return dict.getKeyForValue(object.getCOSObject());
        }
        if (dict != null && COSName.FONT.equals(kind)) {
            for (Map.Entry<COSName, COSBase> entry : dict.entrySet()) {
                if ((entry.getValue() instanceof COSObject) && object.getCOSObject() == ((COSObject) entry.getValue()).getObject()) {
                    return entry.getKey();
                }
            }
        }
        COSName name = createKey(kind, prefix);
        put(kind, name, object);
        return name;
    }

    private COSName createKey(COSName kind, String prefix) {
        String key;
        COSDictionary dict = (COSDictionary) this.resources.getDictionaryObject(kind);
        if (dict == null) {
            return COSName.getPDFName(prefix + 1);
        }
        int n = dict.keySet().size();
        do {
            n++;
            key = prefix + n;
        } while (dict.containsKey(key));
        return COSName.getPDFName(key);
    }

    private void put(COSName kind, COSName name, COSObjectable object) {
        COSDictionary dict = (COSDictionary) this.resources.getDictionaryObject(kind);
        if (dict == null) {
            dict = new COSDictionary();
            this.resources.setItem(kind, (COSBase) dict);
        }
        dict.setItem(name, object);
    }

    public void put(COSName name, PDFont font) {
        put(COSName.FONT, name, font);
    }

    public void put(COSName name, PDColorSpace colorSpace) {
        put(COSName.COLORSPACE, name, colorSpace);
    }

    public void put(COSName name, PDExtendedGraphicsState extGState) {
        put(COSName.EXT_G_STATE, name, extGState);
    }

    public void put(COSName name, PDShading shading) {
        put(COSName.SHADING, name, shading);
    }

    public void put(COSName name, PDAbstractPattern pattern) {
        put(COSName.PATTERN, name, pattern);
    }

    public void put(COSName name, PDPropertyList properties) {
        put(COSName.PROPERTIES, name, properties);
    }

    public void put(COSName name, PDXObject xobject) {
        put(COSName.XOBJECT, name, xobject);
    }

    public ResourceCache getResourceCache() {
        return this.cache;
    }
}
