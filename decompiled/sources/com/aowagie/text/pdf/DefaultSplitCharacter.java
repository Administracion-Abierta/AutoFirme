package com.aowagie.text.pdf;

import com.aowagie.text.SplitCharacter;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/DefaultSplitCharacter.class */
class DefaultSplitCharacter implements SplitCharacter {
    static final SplitCharacter DEFAULT = new DefaultSplitCharacter();

    DefaultSplitCharacter() {
    }

    @Override // com.aowagie.text.SplitCharacter
    public boolean isSplitCharacter(int start, int current, int end, char[] cc, PdfChunk[] ck) {
        char c = getCurrentCharacter(current, cc, ck);
        if (c <= ' ' || c == '-' || c == 8208) {
            return true;
        }
        if (c < 8194) {
            return false;
        }
        return (c >= 8194 && c <= 8203) || (c >= 11904 && c < 55200) || ((c >= 63744 && c < 64256) || ((c >= 65072 && c < 65104) || (c >= 65377 && c < 65440)));
    }

    private char getCurrentCharacter(int current, char[] cc, PdfChunk[] ck) {
        if (ck == null) {
            return cc[current];
        }
        return (char) ck[Math.min(current, ck.length - 1)].getUnicodeEquivalent(cc[current]);
    }
}
