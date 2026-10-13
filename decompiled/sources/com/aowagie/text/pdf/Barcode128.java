package com.aowagie.text.pdf;

import com.aowagie.text.ExceptionConverter;
import com.aowagie.text.Rectangle;
import com.github.markusbernhardt.proxy.search.wpad.dhcp.DHCPOptions;
import com.sun.jna.platform.win32.WinError;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Image;
import java.awt.image.MemoryImageSource;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/Barcode128.class */
public class Barcode128 extends Barcode {
    public static final char CODE_AB_TO_C = 'c';
    public static final char CODE_AC_TO_B = 'd';
    public static final char CODE_BC_TO_A = 'e';
    public static final char FNC1_INDEX = 'f';
    public static final char START_A = 'g';
    public static final char START_B = 'h';
    public static final char START_C = 'i';
    public static final char FNC1 = 202;
    public static final char DEL = 195;
    public static final char FNC3 = 196;
    public static final char FNC2 = 197;
    public static final char SHIFT = 198;
    public static final char CODE_C = 199;
    public static final char CODE_A = 200;
    public static final char FNC4 = 200;
    public static final char STARTA = 203;
    public static final char STARTB = 204;
    public static final char STARTC = 205;
    private static final byte[][] BARS = {new byte[]{2, 1, 2, 2, 2, 2}, new byte[]{2, 2, 2, 1, 2, 2}, new byte[]{2, 2, 2, 2, 2, 1}, new byte[]{1, 2, 1, 2, 2, 3}, new byte[]{1, 2, 1, 3, 2, 2}, new byte[]{1, 3, 1, 2, 2, 2}, new byte[]{1, 2, 2, 2, 1, 3}, new byte[]{1, 2, 2, 3, 1, 2}, new byte[]{1, 3, 2, 2, 1, 2}, new byte[]{2, 2, 1, 2, 1, 3}, new byte[]{2, 2, 1, 3, 1, 2}, new byte[]{2, 3, 1, 2, 1, 2}, new byte[]{1, 1, 2, 2, 3, 2}, new byte[]{1, 2, 2, 1, 3, 2}, new byte[]{1, 2, 2, 2, 3, 1}, new byte[]{1, 1, 3, 2, 2, 2}, new byte[]{1, 2, 3, 1, 2, 2}, new byte[]{1, 2, 3, 2, 2, 1}, new byte[]{2, 2, 3, 2, 1, 1}, new byte[]{2, 2, 1, 1, 3, 2}, new byte[]{2, 2, 1, 2, 3, 1}, new byte[]{2, 1, 3, 2, 1, 2}, new byte[]{2, 2, 3, 1, 1, 2}, new byte[]{3, 1, 2, 1, 3, 1}, new byte[]{3, 1, 1, 2, 2, 2}, new byte[]{3, 2, 1, 1, 2, 2}, new byte[]{3, 2, 1, 2, 2, 1}, new byte[]{3, 1, 2, 2, 1, 2}, new byte[]{3, 2, 2, 1, 1, 2}, new byte[]{3, 2, 2, 2, 1, 1}, new byte[]{2, 1, 2, 1, 2, 3}, new byte[]{2, 1, 2, 3, 2, 1}, new byte[]{2, 3, 2, 1, 2, 1}, new byte[]{1, 1, 1, 3, 2, 3}, new byte[]{1, 3, 1, 1, 2, 3}, new byte[]{1, 3, 1, 3, 2, 1}, new byte[]{1, 1, 2, 3, 1, 3}, new byte[]{1, 3, 2, 1, 1, 3}, new byte[]{1, 3, 2, 3, 1, 1}, new byte[]{2, 1, 1, 3, 1, 3}, new byte[]{2, 3, 1, 1, 1, 3}, new byte[]{2, 3, 1, 3, 1, 1}, new byte[]{1, 1, 2, 1, 3, 3}, new byte[]{1, 1, 2, 3, 3, 1}, new byte[]{1, 3, 2, 1, 3, 1}, new byte[]{1, 1, 3, 1, 2, 3}, new byte[]{1, 1, 3, 3, 2, 1}, new byte[]{1, 3, 3, 1, 2, 1}, new byte[]{3, 1, 3, 1, 2, 1}, new byte[]{2, 1, 1, 3, 3, 1}, new byte[]{2, 3, 1, 1, 3, 1}, new byte[]{2, 1, 3, 1, 1, 3}, new byte[]{2, 1, 3, 3, 1, 1}, new byte[]{2, 1, 3, 1, 3, 1}, new byte[]{3, 1, 1, 1, 2, 3}, new byte[]{3, 1, 1, 3, 2, 1}, new byte[]{3, 3, 1, 1, 2, 1}, new byte[]{3, 1, 2, 1, 1, 3}, new byte[]{3, 1, 2, 3, 1, 1}, new byte[]{3, 3, 2, 1, 1, 1}, new byte[]{3, 1, 4, 1, 1, 1}, new byte[]{2, 2, 1, 4, 1, 1}, new byte[]{4, 3, 1, 1, 1, 1}, new byte[]{1, 1, 1, 2, 2, 4}, new byte[]{1, 1, 1, 4, 2, 2}, new byte[]{1, 2, 1, 1, 2, 4}, new byte[]{1, 2, 1, 4, 2, 1}, new byte[]{1, 4, 1, 1, 2, 2}, new byte[]{1, 4, 1, 2, 2, 1}, new byte[]{1, 1, 2, 2, 1, 4}, new byte[]{1, 1, 2, 4, 1, 2}, new byte[]{1, 2, 2, 1, 1, 4}, new byte[]{1, 2, 2, 4, 1, 1}, new byte[]{1, 4, 2, 1, 1, 2}, new byte[]{1, 4, 2, 2, 1, 1}, new byte[]{2, 4, 1, 2, 1, 1}, new byte[]{2, 2, 1, 1, 1, 4}, new byte[]{4, 1, 3, 1, 1, 1}, new byte[]{2, 4, 1, 1, 1, 2}, new byte[]{1, 3, 4, 1, 1, 1}, new byte[]{1, 1, 1, 2, 4, 2}, new byte[]{1, 2, 1, 1, 4, 2}, new byte[]{1, 2, 1, 2, 4, 1}, new byte[]{1, 1, 4, 2, 1, 2}, new byte[]{1, 2, 4, 1, 1, 2}, new byte[]{1, 2, 4, 2, 1, 1}, new byte[]{4, 1, 1, 2, 1, 2}, new byte[]{4, 2, 1, 1, 1, 2}, new byte[]{4, 2, 1, 2, 1, 1}, new byte[]{2, 1, 2, 1, 4, 1}, new byte[]{2, 1, 4, 1, 2, 1}, new byte[]{4, 1, 2, 1, 2, 1}, new byte[]{1, 1, 1, 1, 4, 3}, new byte[]{1, 1, 1, 3, 4, 1}, new byte[]{1, 3, 1, 1, 4, 1}, new byte[]{1, 1, 4, 1, 1, 3}, new byte[]{1, 1, 4, 3, 1, 1}, new byte[]{4, 1, 1, 1, 1, 3}, new byte[]{4, 1, 1, 3, 1, 1}, new byte[]{1, 1, 3, 1, 4, 1}, new byte[]{1, 1, 4, 1, 3, 1}, new byte[]{3, 1, 1, 1, 4, 1}, new byte[]{4, 1, 1, 1, 3, 1}, new byte[]{2, 1, 1, 4, 1, 2}, new byte[]{2, 1, 1, 2, 1, 4}, new byte[]{2, 1, 1, 2, 3, 2}};
    private static final byte[] BARS_STOP = {2, 3, 3, 1, 1, 1, 2};
    private static final IntHashtable ais = new IntHashtable();

    static {
        ais.put(0, 20);
        ais.put(1, 16);
        ais.put(2, 16);
        ais.put(10, -1);
        ais.put(11, 9);
        ais.put(12, 8);
        ais.put(13, 8);
        ais.put(15, 8);
        ais.put(17, 8);
        ais.put(20, 4);
        ais.put(21, -1);
        ais.put(22, -1);
        ais.put(23, -1);
        ais.put(240, -1);
        ais.put(241, -1);
        ais.put(250, -1);
        ais.put(251, -1);
        ais.put(DHCPOptions.OPTION_PROXY_AUTODISCOVERY, -1);
        ais.put(30, -1);
        for (int k = 3100; k < 3700; k++) {
            ais.put(k, 10);
        }
        ais.put(37, -1);
        for (int k2 = 3900; k2 < 3940; k2++) {
            ais.put(k2, -1);
        }
        ais.put(400, -1);
        ais.put(WinError.ERROR_THREAD_MODE_NOT_BACKGROUND, -1);
        ais.put(WinError.ERROR_PROCESS_MODE_ALREADY_BACKGROUND, 20);
        ais.put(WinError.ERROR_PROCESS_MODE_NOT_BACKGROUND, -1);
        for (int k3 = 410; k3 < 416; k3++) {
            ais.put(k3, 16);
        }
        ais.put(420, -1);
        ais.put(421, -1);
        ais.put(422, 6);
        ais.put(423, -1);
        ais.put(424, 6);
        ais.put(425, 6);
        ais.put(426, 6);
        ais.put(WinError.ERROR_CTX_WINSTATION_NAME_INVALID, 17);
        ais.put(WinError.ERROR_CTX_INVALID_PD, -1);
        for (int k4 = 7030; k4 < 7040; k4++) {
            ais.put(k4, -1);
        }
        ais.put(WinError.FRS_ERR_INVALID_API_SEQUENCE, 18);
        ais.put(WinError.FRS_ERR_STARTING_SERVICE, -1);
        ais.put(WinError.FRS_ERR_STOPPING_SERVICE, -1);
        ais.put(WinError.FRS_ERR_INTERNAL_API, -1);
        ais.put(WinError.FRS_ERR_INTERNAL, 10);
        ais.put(WinError.FRS_ERR_SERVICE_COMM, 22);
        ais.put(WinError.FRS_ERR_INSUFFICIENT_PRIV, -1);
        ais.put(WinError.FRS_ERR_AUTHENTICATION, -1);
        ais.put(8018, 22);
        ais.put(8020, -1);
        ais.put(8100, 10);
        ais.put(8101, 14);
        ais.put(8102, 6);
        for (int k5 = 90; k5 < 100; k5++) {
            ais.put(k5, -1);
        }
    }

    public Barcode128() {
        try {
            this.x = 0.8f;
            this.font = BaseFont.createFont("Helvetica", "winansi", false);
            this.size = 8.0f;
            this.baseline = this.size;
            this.barHeight = this.size * 3.0f;
            this.textAlignment = 1;
            this.codeType = 9;
        } catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }

    public static String removeFNC1(String code) {
        int len = code.length();
        StringBuffer buf = new StringBuffer(len);
        for (int k = 0; k < len; k++) {
            char c = code.charAt(k);
            if (c >= ' ' && c <= '~') {
                buf.append(c);
            }
        }
        return buf.toString();
    }

    public static String getHumanReadableUCCEAN(String code) {
        StringBuffer buf = new StringBuffer();
        String fnc1 = String.valueOf((char) 202);
        while (true) {
            try {
                if (code.startsWith(fnc1)) {
                    code = code.substring(1);
                } else {
                    int n = 0;
                    int idlen = 0;
                    for (int k = 2; k < 5 && code.length() >= k; k++) {
                        int i = ais.get(Integer.parseInt(code.substring(0, k)));
                        n = i;
                        if (i != 0) {
                            idlen = k;
                            break;
                        }
                    }
                    if (idlen == 0) {
                        break;
                    }
                    buf.append('(').append(code.substring(0, idlen)).append(')');
                    code = code.substring(idlen);
                    if (n > 0) {
                        int n2 = n - idlen;
                        if (code.length() <= n2) {
                            break;
                        }
                        buf.append(removeFNC1(code.substring(0, n2)));
                        code = code.substring(n2);
                    } else {
                        int idx = code.indexOf(202);
                        if (idx < 0) {
                            break;
                        }
                        buf.append(code.substring(0, idx));
                        code = code.substring(idx + 1);
                    }
                }
            } catch (Exception e) {
            }
        }
        buf.append(removeFNC1(code));
        return buf.toString();
    }

    static boolean isNextDigits(String text, int textIndex, int numDigits) {
        int len = text.length();
        while (textIndex < len && numDigits > 0) {
            if (text.charAt(textIndex) == 202) {
                textIndex++;
            } else {
                int n = Math.min(2, numDigits);
                if (textIndex + n <= len) {
                    while (true) {
                        int i = n;
                        n--;
                        if (i <= 0) {
                            break;
                        }
                        int i2 = textIndex;
                        textIndex++;
                        char c = text.charAt(i2);
                        if (c < '0' || c > '9') {
                            return false;
                        }
                        numDigits--;
                    }
                } else {
                    return false;
                }
            }
        }
        return numDigits == 0;
    }

    static String getPackedRawDigits(String text, int textIndex, int numDigits) {
        String out = "";
        while (numDigits > 0) {
            if (text.charAt(textIndex) == 202) {
                out = out + 'f';
                textIndex++;
            } else {
                numDigits -= 2;
                int i = textIndex;
                int textIndex2 = textIndex + 1;
                int c1 = text.charAt(i) - '0';
                textIndex = textIndex2 + 1;
                int c2 = text.charAt(textIndex2) - '0';
                out = out + ((char) ((c1 * 10) + c2));
            }
        }
        return ((char) (textIndex - textIndex)) + out;
    }

    public static String getRawText(String text, boolean ucc) {
        String out;
        int index;
        int tLen = text.length();
        if (tLen == 0) {
            String out2 = "h";
            if (ucc) {
                out2 = out2 + 'f';
            }
            return out2;
        }
        for (int k = 0; k < tLen; k++) {
            int c = text.charAt(k);
            if (c > 127 && c != 202) {
                throw new RuntimeException("There are illegal characters for barcode 128 in '" + text + "'.");
            }
        }
        int c2 = text.charAt(0);
        char currentCode = 'h';
        if (isNextDigits(text, 0, 2)) {
            currentCode = 'i';
            String out3 = "i";
            if (ucc) {
                out3 = out3 + 'f';
            }
            String out4 = getPackedRawDigits(text, 0, 2);
            index = 0 + out4.charAt(0);
            out = out3 + out4.substring(1);
        } else if (c2 >= 32) {
            String out5 = "h";
            if (ucc) {
                out5 = out5 + 'f';
            }
            if (c2 == 202) {
                out = out5 + 'f';
            } else {
                out = out5 + ((char) (c2 - 32));
            }
            index = 0 + 1;
        } else {
            currentCode = 'g';
            String out6 = "g";
            if (ucc) {
                out6 = out6 + 'f';
            }
            out = out6 + ((char) (c2 + 64));
            index = 0 + 1;
        }
        while (index < tLen) {
            switch (currentCode) {
                case 'g':
                    if (isNextDigits(text, index, 4)) {
                        currentCode = 'i';
                        String out7 = getPackedRawDigits(text, index, 4);
                        index += out7.charAt(0);
                        out = (out + 'c') + out7.substring(1);
                    } else {
                        int i = index;
                        index++;
                        int c3 = text.charAt(i);
                        if (c3 == 202) {
                            out = out + 'f';
                        } else if (c3 > 95) {
                            currentCode = 'h';
                            out = (out + 'd') + ((char) (c3 - 32));
                        } else if (c3 < 32) {
                            out = out + ((char) (c3 + 64));
                        } else {
                            out = out + ((char) (c3 - 32));
                        }
                    }
                    break;
                case 'h':
                    if (isNextDigits(text, index, 4)) {
                        currentCode = 'i';
                        String out8 = getPackedRawDigits(text, index, 4);
                        index += out8.charAt(0);
                        out = (out + 'c') + out8.substring(1);
                    } else {
                        int i2 = index;
                        index++;
                        int c4 = text.charAt(i2);
                        if (c4 == 202) {
                            out = out + 'f';
                        } else if (c4 < 32) {
                            currentCode = 'g';
                            out = (out + 'e') + ((char) (c4 + 64));
                        } else {
                            out = out + ((char) (c4 - 32));
                        }
                    }
                    break;
                case 'i':
                    if (isNextDigits(text, index, 2)) {
                        String out9 = getPackedRawDigits(text, index, 2);
                        index += out9.charAt(0);
                        out = out + out9.substring(1);
                    } else {
                        int i3 = index;
                        index++;
                        int c5 = text.charAt(i3);
                        if (c5 == 202) {
                            out = out + 'f';
                        } else if (c5 < 32) {
                            currentCode = 'g';
                            out = (out + 'e') + ((char) (c5 + 64));
                        } else {
                            currentCode = 'h';
                            out = (out + 'd') + ((char) (c5 - 32));
                        }
                    }
                    break;
            }
        }
        return out;
    }

    public static byte[] getBarsCode128Raw(String text) {
        int idx = text.indexOf(65535);
        if (idx >= 0) {
            text = text.substring(0, idx);
        }
        int chk = text.charAt(0);
        for (int k = 1; k < text.length(); k++) {
            chk += k * text.charAt(k);
        }
        String text2 = text + ((char) (chk % 103));
        byte[] bars = new byte[((text2.length() + 1) * 6) + 7];
        int k2 = 0;
        while (k2 < text2.length()) {
            System.arraycopy(BARS[text2.charAt(k2)], 0, bars, k2 * 6, 6);
            k2++;
        }
        System.arraycopy(BARS_STOP, 0, bars, k2 * 6, 7);
        return bars;
    }

    @Override // com.aowagie.text.pdf.Barcode
    public Rectangle getBarcodeSize() {
        String fullCode;
        String fullCode2;
        float fontX = 0.0f;
        float fontY = 0.0f;
        if (this.font != null) {
            if (this.baseline > ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
                fontY = this.baseline - this.font.getFontDescriptor(3, this.size);
            } else {
                fontY = (-this.baseline) + this.size;
            }
            if (this.codeType == 11) {
                int idx = this.code.indexOf(65535);
                if (idx < 0) {
                    fullCode2 = "";
                } else {
                    fullCode2 = this.code.substring(idx + 1);
                }
            } else if (this.codeType == 10) {
                fullCode2 = getHumanReadableUCCEAN(this.code);
            } else {
                fullCode2 = removeFNC1(this.code);
            }
            fontX = this.font.getWidthPoint(this.altText != null ? this.altText : fullCode2, this.size);
        }
        if (this.codeType == 11) {
            int idx2 = this.code.indexOf(65535);
            if (idx2 >= 0) {
                fullCode = this.code.substring(0, idx2);
            } else {
                fullCode = this.code;
            }
        } else {
            fullCode = getRawText(this.code, this.codeType == 10);
        }
        int len = fullCode.length();
        float fullWidth = ((len + 2) * 11 * this.x) + (2.0f * this.x);
        float fullWidth2 = Math.max(fullWidth, fontX);
        float fullHeight = this.barHeight + fontY;
        return new Rectangle(fullWidth2, fullHeight);
    }

    @Override // com.aowagie.text.pdf.Barcode
    public Rectangle placeBarcode(PdfContentByte cb, Color barColor, Color textColor) {
        String fullCode;
        String bCode;
        if (this.codeType == 11) {
            int idx = this.code.indexOf(65535);
            if (idx < 0) {
                fullCode = "";
            } else {
                fullCode = this.code.substring(idx + 1);
            }
        } else if (this.codeType == 10) {
            fullCode = getHumanReadableUCCEAN(this.code);
        } else {
            fullCode = removeFNC1(this.code);
        }
        float fontX = 0.0f;
        if (this.font != null) {
            BaseFont baseFont = this.font;
            String str = this.altText != null ? this.altText : fullCode;
            fullCode = str;
            fontX = baseFont.getWidthPoint(str, this.size);
        }
        if (this.codeType == 11) {
            int idx2 = this.code.indexOf(65535);
            if (idx2 >= 0) {
                bCode = this.code.substring(0, idx2);
            } else {
                bCode = this.code;
            }
        } else {
            bCode = getRawText(this.code, this.codeType == 10);
        }
        int len = bCode.length();
        float fullWidth = ((len + 2) * 11 * this.x) + (2.0f * this.x);
        float barStartX = 0.0f;
        float textStartX = 0.0f;
        switch (this.textAlignment) {
            case 0:
                break;
            case 2:
                if (fontX > fullWidth) {
                    barStartX = fontX - fullWidth;
                } else {
                    textStartX = fullWidth - fontX;
                }
                break;
            default:
                if (fontX > fullWidth) {
                    barStartX = (fontX - fullWidth) / 2.0f;
                } else {
                    textStartX = (fullWidth - fontX) / 2.0f;
                }
                break;
        }
        float barStartY = 0.0f;
        float textStartY = 0.0f;
        if (this.font != null) {
            if (this.baseline <= ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
                textStartY = this.barHeight - this.baseline;
            } else {
                textStartY = -this.font.getFontDescriptor(3, this.size);
                barStartY = textStartY + this.baseline;
            }
        }
        byte[] bars = getBarsCode128Raw(bCode);
        boolean print = true;
        if (barColor != null) {
            cb.setColorFill(barColor);
        }
        for (byte bar : bars) {
            float w = bar * this.x;
            if (print) {
                cb.rectangle(barStartX, barStartY, w - this.inkSpreading, this.barHeight);
            }
            print = !print;
            barStartX += w;
        }
        cb.fill();
        if (this.font != null) {
            if (textColor != null) {
                cb.setColorFill(textColor);
            }
            cb.beginText();
            cb.setFontAndSize(this.font, this.size);
            cb.setTextMatrix(textStartX, textStartY);
            cb.showText(fullCode);
            cb.endText();
        }
        return getBarcodeSize();
    }

    @Override // com.aowagie.text.pdf.Barcode
    public Image createAwtImage(Color foreground, Color background) {
        String bCode;
        int f = foreground.getRGB();
        int g = background.getRGB();
        Canvas canvas = new Canvas();
        if (this.codeType == 11) {
            int idx = this.code.indexOf(65535);
            if (idx >= 0) {
                bCode = this.code.substring(0, idx);
            } else {
                bCode = this.code;
            }
        } else {
            bCode = getRawText(this.code, this.codeType == 10);
        }
        int len = bCode.length();
        int fullWidth = ((len + 2) * 11) + 2;
        byte[] bars = getBarsCode128Raw(bCode);
        boolean print = true;
        int ptr = 0;
        int height = (int) this.barHeight;
        int[] pix = new int[fullWidth * height];
        for (byte w : bars) {
            int c = g;
            if (print) {
                c = f;
            }
            print = !print;
            for (int j = 0; j < w; j++) {
                int i = ptr;
                ptr++;
                pix[i] = c;
            }
        }
        int i2 = fullWidth;
        while (true) {
            int k = i2;
            if (k < pix.length) {
                System.arraycopy(pix, 0, pix, k, fullWidth);
                i2 = k + fullWidth;
            } else {
                Image img = canvas.createImage(new MemoryImageSource(fullWidth, height, pix, 0, fullWidth));
                return img;
            }
        }
    }

    @Override // com.aowagie.text.pdf.Barcode
    public void setCode(String code) {
        if (getCodeType() == 10 && code.startsWith("(")) {
            int idx = 0;
            String ret = "";
            while (idx >= 0) {
                int end = code.indexOf(41, idx);
                if (end < 0) {
                    throw new IllegalArgumentException("Badly formed UCC string: " + code);
                }
                String sai = code.substring(idx + 1, end);
                if (sai.length() < 2) {
                    throw new IllegalArgumentException("AI too short: (" + sai + ")");
                }
                int ai = Integer.parseInt(sai);
                int len = ais.get(ai);
                if (len == 0) {
                    throw new IllegalArgumentException("AI not found: (" + sai + ")");
                }
                String sai2 = String.valueOf(ai);
                if (sai2.length() == 1) {
                    sai2 = "0" + sai2;
                }
                idx = code.indexOf(40, end);
                int next = idx < 0 ? code.length() : idx;
                ret = ret + sai2 + code.substring(end + 1, next);
                if (len < 0) {
                    if (idx >= 0) {
                        ret = ret + (char) 202;
                    }
                } else if (((next - end) - 1) + sai2.length() != len) {
                    throw new IllegalArgumentException("Invalid AI length: (" + sai2 + ")");
                }
            }
            super.setCode(ret);
            return;
        }
        super.setCode(code);
    }
}
