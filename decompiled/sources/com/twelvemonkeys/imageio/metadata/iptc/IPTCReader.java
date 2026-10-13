package com.twelvemonkeys.imageio.metadata.iptc;

import com.twelvemonkeys.imageio.metadata.Directory;
import com.twelvemonkeys.imageio.metadata.Entry;
import com.twelvemonkeys.imageio.metadata.MetadataReader;
import com.twelvemonkeys.lang.StringUtil;
import com.twelvemonkeys.lang.Validate;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CodingErrorAction;
import java.util.Arrays;
import java.util.LinkedHashMap;
import javax.imageio.IIOException;
import javax.imageio.stream.ImageInputStream;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/metadata/iptc/IPTCReader.class */
public final class IPTCReader extends MetadataReader {
    private static final int ENCODING_UNKNOWN = -1;
    private static final int ENCODING_UNSPECIFIED = 0;
    private static final int ENCODING_UTF_8 = 1779015;
    private int encoding = 0;

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.imageio.IIOException */
    @Override // com.twelvemonkeys.imageio.metadata.MetadataReader
    public Directory read(ImageInputStream imageInputStream) throws IIOException, IOException {
        Validate.notNull(imageInputStream, "input");
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        while (imageInputStream.read() == 28) {
            short s = imageInputStream.readShort();
            int unsignedShort = imageInputStream.readUnsignedShort();
            boolean zIsArray = IPTC.Tags.isArray(s);
            IPTCEntry entry = readEntry(imageInputStream, s, unsignedShort, zIsArray, zIsArray ? (Entry) linkedHashMap.get(Short.valueOf(s)) : null);
            if (entry != null) {
                linkedHashMap.put(Short.valueOf(s), entry);
            }
        }
        return new IPTCDirectory(linkedHashMap.values());
    }

    private IPTCEntry mergeEntries(short s, Object obj, Entry entry) {
        Object[] objArrCopyOf;
        Object[] objArr = entry != null ? (Object[]) entry.getValue() : null;
        if (obj instanceof String) {
            if (objArr == null) {
                objArrCopyOf = new String[]{(String) obj};
            } else {
                String[] strArr = (String[]) objArr;
                objArrCopyOf = Arrays.copyOf(strArr, strArr.length + 1);
                objArrCopyOf[objArrCopyOf.length - 1] = obj;
            }
        } else if (objArr == null) {
            objArrCopyOf = new Object[]{obj};
        } else {
            objArrCopyOf = Arrays.copyOf(objArr, objArr.length + 1);
            objArrCopyOf[objArrCopyOf.length - 1] = obj;
        }
        return new IPTCEntry(s, objArrCopyOf);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.imageio.IIOException */
    private IPTCEntry readEntry(ImageInputStream imageInputStream, short s, int i, boolean z, Entry entry) throws IIOException, IOException {
        Object string;
        switch (s) {
            case IPTC.TAG_CODED_CHARACTER_SET /* 346 */:
                this.encoding = parseEncoding(imageInputStream, i);
                return null;
            case 512:
                string = Integer.valueOf(imageInputStream.readUnsignedShort());
                break;
            default:
                if ((s & 65280) == 512) {
                    if (i < 1) {
                        string = null;
                    } else {
                        string = parseString(imageInputStream, i);
                    }
                } else {
                    byte[] bArr = new byte[i];
                    imageInputStream.readFully(bArr);
                    string = bArr;
                }
                break;
        }
        return z ? mergeEntries(s, string, entry) : new IPTCEntry(s, string);
    }

    private int parseEncoding(ImageInputStream imageInputStream, int i) throws IOException {
        if (i == 3 && ((imageInputStream.readUnsignedByte() << 16) | (imageInputStream.readUnsignedByte() << 8) | imageInputStream.readUnsignedByte()) == ENCODING_UTF_8) {
            return ENCODING_UTF_8;
        }
        return -1;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.imageio.IIOException */
    private String parseString(ImageInputStream imageInputStream, int i) throws IIOException, IOException {
        byte[] bArr = new byte[i];
        imageInputStream.readFully(bArr);
        try {
            return Charset.forName("UTF-8").newDecoder().onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT).decode(ByteBuffer.wrap(bArr)).toString();
        } catch (CharacterCodingException e) {
            if (this.encoding == ENCODING_UTF_8) {
                throw new IIOException("Wrong encoding of IPTC data, explicitly set to UTF-8 in DataSet 1:90", e);
            }
            return StringUtil.decode(bArr, 0, bArr.length, "ISO8859_1");
        }
    }
}
