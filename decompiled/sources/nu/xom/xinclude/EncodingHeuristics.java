package nu.xom.xinclude;

import com.aowagie.text.pdf.PdfObject;
import com.sun.jna.platform.win32.WinError;
import java.io.IOException;
import java.io.InputStream;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/xinclude/EncodingHeuristics.class */
class EncodingHeuristics {
    private EncodingHeuristics() {
    }

    public static String readEncodingFromStream(InputStream inputStream) throws IOException {
        int i;
        inputStream.mark(1024);
        try {
            int i2 = inputStream.read();
            int i3 = inputStream.read();
            if (i2 == 254 && i3 == 255) {
                return PdfObject.TEXT_UNICODE;
            }
            if (i2 == 255 && i3 == 254) {
                return "UnicodeLittle";
            }
            int i4 = inputStream.read();
            if (i2 == 239 && i3 == 187 && i4 == 191) {
                return "UTF-8";
            }
            int i5 = inputStream.read();
            if (i2 == 0 && i3 == 0 && i4 == 254 && i5 == 255) {
                return "UTF32BE";
            }
            if (i2 == 0 && i3 == 0 && i4 == 255 && i5 == 254) {
                return "UTF32LE";
            }
            if (i2 == 0 && i3 == 0 && i4 == 0 && i5 == 60) {
                inputStream.reset();
                return "UTF32BE";
            }
            if (i2 == 60 && i3 == 0 && i4 == 0 && i5 == 0) {
                inputStream.reset();
                return "UTF32LE";
            }
            if (i2 == 0 && i3 == 60 && i4 == 0 && i5 == 63) {
                inputStream.reset();
                return "UnicodeBigUnmarked";
            }
            if (i2 == 60 && i3 == 0 && i4 == 63 && i5 == 0) {
                inputStream.reset();
                return "UnicodeLittleUnmarked";
            }
            if (i2 == 60 && i3 == 63 && i4 == 120 && i5 == 109) {
                byte[] bArr = new byte[1024];
                bArr[0] = (byte) i2;
                bArr[1] = (byte) i3;
                bArr[2] = (byte) i4;
                bArr[3] = (byte) i5;
                String strFindEncodingDeclaration = findEncodingDeclaration(new String(bArr, 0, inputStream.read(bArr, 4, WinError.ERROR_KEY_HAS_CHILDREN) + 4, "8859_1"));
                inputStream.reset();
                return strFindEncodingDeclaration;
            }
            if (i2 != 76 || i3 != 111 || i4 != 167 || i5 != 148) {
                inputStream.reset();
                return "UTF-8";
            }
            byte[] bArr2 = new byte[WinError.ERROR_REGISTRY_IO_FAILED];
            for (int i6 = 0; i6 < bArr2.length && (i = inputStream.read()) != -1; i6++) {
                bArr2[i6] = (byte) i;
            }
            inputStream.reset();
            return findEncodingDeclaration(new String(bArr2, "Cp037"));
        } catch (IOException e) {
            inputStream.reset();
            return "UTF-8";
        } catch (RuntimeException e2) {
            inputStream.reset();
            return "UTF-8";
        }
    }

    private static String findEncodingDeclaration(String str) throws IOException {
        char cCharAt;
        char cCharAt2;
        int iIndexOf = str.indexOf("encoding") + 8;
        while (true) {
            int i = iIndexOf;
            iIndexOf++;
            cCharAt = str.charAt(i);
            if (cCharAt != ' ' && cCharAt != '\t' && cCharAt != '\r' && cCharAt != '\n') {
                break;
            }
        }
        if (cCharAt != '=') {
            throw new IOException("Couldn't determine encoding");
        }
        while (true) {
            int i2 = iIndexOf;
            iIndexOf++;
            cCharAt2 = str.charAt(i2);
            if (cCharAt2 != ' ' && cCharAt2 != '\t' && cCharAt2 != '\r' && cCharAt2 != '\n') {
                break;
            }
        }
        if (cCharAt2 != '\'' && cCharAt2 != '\"') {
            return "UTF-8";
        }
        StringBuffer stringBuffer = new StringBuffer();
        while (true) {
            int i3 = iIndexOf;
            iIndexOf++;
            char cCharAt3 = str.charAt(i3);
            if (cCharAt3 == cCharAt2) {
                return stringBuffer.toString();
            }
            stringBuffer.append(cCharAt3);
        }
    }
}
