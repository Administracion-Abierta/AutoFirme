package com.twelvemonkeys.imageio.metadata.psd;

import com.twelvemonkeys.imageio.metadata.Directory;
import com.twelvemonkeys.imageio.metadata.MetadataReader;
import com.twelvemonkeys.imageio.stream.SubImageInputStream;
import com.twelvemonkeys.lang.StringUtil;
import com.twelvemonkeys.lang.Validate;
import java.io.DataInput;
import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.IIOException;
import javax.imageio.stream.ImageInputStream;
import org.apache.pdfbox.contentstream.operator.OperatorName;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/metadata/psd/PSDReader.class */
public final class PSDReader extends MetadataReader {
    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.imageio.IIOException */
    @Override // com.twelvemonkeys.imageio.metadata.MetadataReader
    public Directory read(ImageInputStream imageInputStream) throws IIOException, IOException {
        Validate.notNull(imageInputStream, "input");
        ArrayList arrayList = new ArrayList();
        while (true) {
            try {
                int i = imageInputStream.readInt();
                switch (i) {
                    case PSD.RESOURCE_TYPE /* 943868237 */:
                    case PSD.RESOURCE_TYPE_LIGHTROOM /* 1097287783 */:
                    case PSD.RESOURCE_TYPE_DCSR /* 1145262930 */:
                    case PSD.RESOURCE_TYPE_IMAGEREADY /* 1298486113 */:
                    case PSD.RESOURCE_TYPE_PHOTODELUXE /* 1346917716 */:
                        short s = imageInputStream.readShort();
                        PSDResource pSDResource = new PSDResource(s, imageInputStream);
                        arrayList.add(new PSDEntry(s, pSDResource.name(), pSDResource.data()));
                        break;
                    default:
                        throw new IIOException(String.format("Wrong image resource type, expected '8BIM': '%08x'", Integer.valueOf(i)));
                }
            } catch (EOFException e) {
                return new PSDDirectory(arrayList);
            }
            return new PSDDirectory(arrayList);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/metadata/psd/PSDReader$PSDResource.class */
    protected static class PSDResource {
        final short id;
        final String name;
        final long size;
        byte[] data;

        static String readPascalString(DataInput dataInput) throws IOException {
            int unsignedByte = dataInput.readUnsignedByte();
            if (unsignedByte == 0) {
                return "";
            }
            byte[] bArr = new byte[unsignedByte];
            dataInput.readFully(bArr);
            return StringUtil.decode(bArr, 0, bArr.length, "ASCII");
        }

        PSDResource(short s, ImageInputStream imageInputStream) throws IOException {
            this.id = s;
            this.name = readPascalString(imageInputStream);
            if ((this.name.length() + 1) % 2 != 0) {
                imageInputStream.readByte();
            }
            this.size = imageInputStream.readUnsignedInt();
            long streamPosition = imageInputStream.getStreamPosition();
            readData(new SubImageInputStream(imageInputStream, this.size));
            if (imageInputStream.getStreamPosition() != streamPosition + this.size) {
                imageInputStream.seek(streamPosition + this.size);
            }
            if (this.size % 2 != 0) {
                imageInputStream.read();
            }
        }

        protected void readData(ImageInputStream imageInputStream) throws IOException {
            this.data = new byte[(int) this.size];
            imageInputStream.readFully(this.data);
        }

        public final int id() {
            return this.id;
        }

        public final byte[] data() {
            return this.data;
        }

        public String name() {
            return this.name;
        }

        public String toString() {
            StringBuilder stringBuilder = toStringBuilder();
            stringBuilder.append(", data length: ");
            stringBuilder.append(this.size);
            stringBuilder.append("]");
            return stringBuilder.toString();
        }

        protected StringBuilder toStringBuilder() {
            StringBuilder sb = new StringBuilder(getClass().getSimpleName());
            sb.append("[ID: 0x");
            sb.append(Integer.toHexString(this.id));
            if (this.name != null && this.name.trim().length() != 0) {
                sb.append(", name: \"");
                sb.append(this.name);
                sb.append(OperatorName.SHOW_TEXT_LINE_AND_SPACE);
            }
            return sb;
        }
    }
}
