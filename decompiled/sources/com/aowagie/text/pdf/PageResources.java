package com.aowagie.text.pdf;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/PageResources.class */
class PageResources {
    private HashMap forbiddenNames;
    private PdfDictionary originalResources;
    private HashMap usedNames;
    private final PdfDictionary fontDictionary = new PdfDictionary();
    private final PdfDictionary xObjectDictionary = new PdfDictionary();
    private final PdfDictionary colorDictionary = new PdfDictionary();
    private final PdfDictionary patternDictionary = new PdfDictionary();
    private final PdfDictionary shadingDictionary = new PdfDictionary();
    private final PdfDictionary extGStateDictionary = new PdfDictionary();
    private final PdfDictionary propertyDictionary = new PdfDictionary();
    private int[] namePtr = {0};

    PageResources() {
    }

    void setOriginalResources(PdfDictionary resources, int[] newNamePtr) {
        if (newNamePtr != null) {
            this.namePtr = newNamePtr;
        }
        this.forbiddenNames = new LinkedHashMap();
        this.usedNames = new LinkedHashMap();
        if (resources == null) {
            return;
        }
        this.originalResources = new PdfDictionary();
        this.originalResources.merge(resources);
        for (Object element : resources.getKeys()) {
            PdfName key = (PdfName) element;
            PdfObject sub = PdfReader.getPdfObject(resources.get(key));
            if (sub != null && sub.isDictionary()) {
                PdfDictionary dic = (PdfDictionary) sub;
                Iterator<PdfName> it = dic.getKeys().iterator();
                while (it.hasNext()) {
                    this.forbiddenNames.put(it.next(), null);
                }
                PdfDictionary dic2 = new PdfDictionary();
                dic2.merge(dic);
                this.originalResources.put(key, dic2);
            }
        }
    }

    private PdfName translateName(PdfName name) {
        PdfName translated = name;
        if (this.forbiddenNames != null) {
            translated = (PdfName) this.usedNames.get(name);
            if (translated == null) {
                do {
                    StringBuilder sbAppend = new StringBuilder().append("Xi");
                    int[] iArr = this.namePtr;
                    int i = iArr[0];
                    iArr[0] = i + 1;
                    translated = new PdfName(sbAppend.append(i).toString());
                } while (this.forbiddenNames.containsKey(translated));
                this.usedNames.put(name, translated);
            }
        }
        return translated;
    }

    PdfName addFont(PdfName name, PdfIndirectReference reference) {
        PdfName name2 = translateName(name);
        this.fontDictionary.put(name2, reference);
        return name2;
    }

    PdfName addXObject(PdfName name, PdfIndirectReference reference) {
        PdfName name2 = translateName(name);
        this.xObjectDictionary.put(name2, reference);
        return name2;
    }

    PdfName addColor(PdfName name, PdfIndirectReference reference) {
        PdfName name2 = translateName(name);
        this.colorDictionary.put(name2, reference);
        return name2;
    }

    void addDefaultColor(PdfDictionary dic) {
        this.colorDictionary.merge(dic);
    }

    void addDefaultColorDiff(PdfDictionary dic) {
        this.colorDictionary.mergeDifferent(dic);
    }

    PdfName addShading(PdfName name, PdfIndirectReference reference) {
        PdfName name2 = translateName(name);
        this.shadingDictionary.put(name2, reference);
        return name2;
    }

    PdfName addPattern(PdfName name, PdfIndirectReference reference) {
        PdfName name2 = translateName(name);
        this.patternDictionary.put(name2, reference);
        return name2;
    }

    PdfName addExtGState(PdfName name, PdfIndirectReference reference) {
        PdfName name2 = translateName(name);
        this.extGStateDictionary.put(name2, reference);
        return name2;
    }

    PdfName addProperty(PdfName name, PdfIndirectReference reference) {
        PdfName name2 = translateName(name);
        this.propertyDictionary.put(name2, reference);
        return name2;
    }

    PdfDictionary getResources() {
        PdfResources resources = new PdfResources();
        if (this.originalResources != null) {
            resources.putAll(this.originalResources);
        }
        resources.put(PdfName.PROCSET, new PdfLiteral("[/PDF /Text /ImageB /ImageC /ImageI]"));
        resources.add(PdfName.FONT, this.fontDictionary);
        resources.add(PdfName.XOBJECT, this.xObjectDictionary);
        resources.add(PdfName.COLORSPACE, this.colorDictionary);
        resources.add(PdfName.PATTERN, this.patternDictionary);
        resources.add(PdfName.SHADING, this.shadingDictionary);
        resources.add(PdfName.EXTGSTATE, this.extGStateDictionary);
        resources.add(PdfName.PROPERTIES, this.propertyDictionary);
        return resources;
    }

    boolean hasResources() {
        return this.fontDictionary.size() > 0 || this.xObjectDictionary.size() > 0 || this.colorDictionary.size() > 0 || this.patternDictionary.size() > 0 || this.shadingDictionary.size() > 0 || this.extGStateDictionary.size() > 0 || this.propertyDictionary.size() > 0;
    }
}
