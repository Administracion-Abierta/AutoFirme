package com.twelvemonkeys.imageio.metadata.iptc;

import com.twelvemonkeys.imageio.metadata.Directory;
import com.twelvemonkeys.imageio.metadata.Entry;
import com.twelvemonkeys.imageio.metadata.MetadataWriter;
import com.twelvemonkeys.lang.Validate;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import javax.imageio.stream.ImageOutputStream;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/metadata/iptc/IPTCWriter.class */
public final class IPTCWriter extends MetadataWriter {
    @Override // com.twelvemonkeys.imageio.metadata.MetadataWriter
    public boolean write(Directory directory, ImageOutputStream imageOutputStream) throws IOException {
        Validate.notNull(directory, "directory");
        Validate.notNull(imageOutputStream, "stream");
        for (Entry entry : directory) {
            int iIntValue = ((Integer) entry.getIdentifier()).intValue();
            Object value = entry.getValue();
            if (IPTC.Tags.isArray((short) iIntValue)) {
                for (Object obj : (Object[]) value) {
                    imageOutputStream.write(28);
                    imageOutputStream.writeShort(iIntValue);
                    writeValue(imageOutputStream, obj);
                }
            } else {
                imageOutputStream.write(28);
                imageOutputStream.writeShort(iIntValue);
                writeValue(imageOutputStream, value);
            }
        }
        return false;
    }

    private void writeValue(ImageOutputStream imageOutputStream, Object obj) throws IOException {
        if (obj instanceof String) {
            byte[] bytes = ((String) obj).getBytes(StandardCharsets.UTF_8);
            imageOutputStream.writeShort(bytes.length);
            imageOutputStream.write(bytes);
        } else if (obj instanceof byte[]) {
            byte[] bArr = (byte[]) obj;
            imageOutputStream.writeShort(bArr.length);
            imageOutputStream.write(bArr);
        } else if (obj instanceof Integer) {
            imageOutputStream.writeShort(2);
            imageOutputStream.writeShort(((Integer) obj).intValue());
        }
    }
}
