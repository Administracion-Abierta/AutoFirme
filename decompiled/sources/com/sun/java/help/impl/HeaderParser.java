package com.sun.java.help.impl;

/* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/impl/HeaderParser.class */
public class HeaderParser {
    String raw;
    String[][] tab = new String[10][2];

    public HeaderParser(String str) {
        this.raw = str;
        parse();
    }

    private void parse() {
        if (this.raw != null) {
            this.raw = this.raw.trim();
            char[] charArray = this.raw.toCharArray();
            int i = 0;
            int i2 = 0;
            int i3 = 0;
            boolean z = true;
            boolean z2 = false;
            int length = charArray.length;
            while (i2 < length) {
                char c = charArray[i2];
                if (c == '=') {
                    this.tab[i3][0] = new String(charArray, i, i2 - i).toLowerCase();
                    z = false;
                    i2++;
                    i = i2;
                } else if (c == '\"') {
                    if (z2) {
                        int i4 = i3;
                        i3++;
                        this.tab[i4][1] = new String(charArray, i, i2 - i);
                        z2 = false;
                        while (true) {
                            i2++;
                            if (i2 >= length || (charArray[i2] != ' ' && charArray[i2] != ',')) {
                                break;
                            }
                        }
                        z = true;
                        i = i2;
                    } else {
                        z2 = true;
                        i2++;
                        i = i2;
                    }
                } else if (c == ' ' || c == ',') {
                    if (z2) {
                        i2++;
                    } else {
                        if (z) {
                            int i5 = i3;
                            i3++;
                            this.tab[i5][0] = new String(charArray, i, i2 - i).toLowerCase();
                        } else {
                            int i6 = i3;
                            i3++;
                            this.tab[i6][1] = new String(charArray, i, i2 - i);
                        }
                        while (i2 < length && (charArray[i2] == ' ' || charArray[i2] == ',')) {
                            i2++;
                        }
                        z = true;
                        i = i2;
                    }
                } else {
                    i2++;
                }
            }
            int i7 = i2 - 1;
            if (i7 > i) {
                if (!z) {
                    if (charArray[i7] == '\"') {
                        int i8 = i3;
                        int i9 = i3 + 1;
                        this.tab[i8][1] = new String(charArray, i, i7 - i);
                        return;
                    } else {
                        int i10 = i3;
                        int i11 = i3 + 1;
                        this.tab[i10][1] = new String(charArray, i, (i7 - i) + 1);
                        return;
                    }
                }
                this.tab[i3][0] = new String(charArray, i, (i7 - i) + 1).toLowerCase();
                return;
            }
            if (i7 == i) {
                if (!z) {
                    if (charArray[i7] == '\"') {
                        int i12 = i3;
                        int i13 = i3 + 1;
                        this.tab[i12][1] = String.valueOf(charArray[i7 - 1]);
                        return;
                    } else {
                        int i14 = i3;
                        int i15 = i3 + 1;
                        this.tab[i14][1] = String.valueOf(charArray[i7]);
                        return;
                    }
                }
                this.tab[i3][0] = String.valueOf(charArray[i7]).toLowerCase();
            }
        }
    }

    public String findKey(int i) {
        if (i < 0 || i > 10) {
            return null;
        }
        return this.tab[i][0];
    }

    public String findValue(int i) {
        if (i < 0 || i > 10) {
            return null;
        }
        return this.tab[i][1];
    }

    public String findValue(String str) {
        return findValue(str, null);
    }

    public String findValue(String str, String str2) {
        if (str == null) {
            return str2;
        }
        str.toLowerCase();
        for (int i = 0; i < 10; i++) {
            if (this.tab[i][0] == null) {
                return str2;
            }
            if (str.equals(this.tab[i][0])) {
                return this.tab[i][1];
            }
        }
        return str2;
    }

    public int findInt(String str, int i) {
        try {
            return Integer.parseInt(findValue(str, String.valueOf(i)));
        } catch (Throwable th) {
            return i;
        }
    }
}
