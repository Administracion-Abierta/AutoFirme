package org.apache.fontbox.ttf;

import java.util.List;

/* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/ttf/SubstitutingCmapLookup.class */
public class SubstitutingCmapLookup implements CmapLookup {
    private final CmapSubtable cmap;
    private final GlyphSubstitutionTable gsub;
    private final List<String> enabledFeatures;

    public SubstitutingCmapLookup(CmapSubtable cmap, GlyphSubstitutionTable gsub, List<String> enabledFeatures) {
        this.cmap = cmap;
        this.gsub = gsub;
        this.enabledFeatures = enabledFeatures;
    }

    @Override // org.apache.fontbox.ttf.CmapLookup
    public int getGlyphId(int characterCode) {
        int gid = this.cmap.getGlyphId(characterCode);
        String[] scriptTags = OpenTypeScript.getScriptTags(characterCode);
        return this.gsub.getSubstitution(gid, scriptTags, this.enabledFeatures);
    }

    @Override // org.apache.fontbox.ttf.CmapLookup
    public List<Integer> getCharCodes(int gid) {
        return this.cmap.getCharCodes(this.gsub.getUnsubstitution(gid));
    }
}
