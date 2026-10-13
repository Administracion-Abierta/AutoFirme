package com.twelvemonkeys.imageio.plugins.jpeg;

import com.twelvemonkeys.imageio.metadata.jpeg.JPEG;
import com.twelvemonkeys.imageio.stream.SubImageInputStream;
import java.io.DataInput;
import java.io.IOException;
import java.util.Arrays;
import javax.imageio.IIOException;
import javax.imageio.stream.ImageInputStream;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/plugins/jpeg/Scan.class */
final class Scan extends Segment {
    final int spectralSelStart;
    final int spectralSelEnd;
    final int approxHigh;
    final int approxLow;
    final Component[] components;

    Scan(Component[] componentArr, int i, int i2, int i3, int i4) {
        super(JPEG.SOS);
        this.components = componentArr;
        this.spectralSelStart = i;
        this.spectralSelEnd = i2;
        this.approxHigh = i3;
        this.approxLow = i4;
    }

    public String toString() {
        return String.format("SOS[spectralSelStart: %d, spectralSelEnd: %d, approxHigh: %d, approxLow: %d, components: %s]", Integer.valueOf(this.spectralSelStart), Integer.valueOf(this.spectralSelEnd), Integer.valueOf(this.approxHigh), Integer.valueOf(this.approxLow), Arrays.toString(this.components));
    }

    public static Scan read(ImageInputStream imageInputStream) throws IOException {
        int unsignedShort = imageInputStream.readUnsignedShort();
        return read(new SubImageInputStream(imageInputStream, unsignedShort), unsignedShort);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.imageio.IIOException */
    public static Scan read(DataInput dataInput, int i) throws IOException, IIOException {
        int unsignedByte = dataInput.readUnsignedByte();
        int i2 = 6 + (unsignedByte * 2);
        if (i2 != i) {
            throw new IIOException(String.format("Unexpected SOS length: %d != %d", Integer.valueOf(i), Integer.valueOf(i2)));
        }
        Component[] componentArr = new Component[unsignedByte];
        for (int i3 = 0; i3 < unsignedByte; i3++) {
            int unsignedByte2 = dataInput.readUnsignedByte();
            int unsignedByte3 = dataInput.readUnsignedByte();
            componentArr[i3] = new Component(unsignedByte2, unsignedByte3 & 15, unsignedByte3 >> 4);
        }
        int unsignedByte4 = dataInput.readUnsignedByte();
        int unsignedByte5 = dataInput.readUnsignedByte();
        int unsignedByte6 = dataInput.readUnsignedByte();
        return new Scan(componentArr, unsignedByte4, unsignedByte5, unsignedByte6 >> 4, unsignedByte6 & 15);
    }

    /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/plugins/jpeg/Scan$Component.class */
    public static final class Component {
        final int scanCompSel;
        final int acTabSel;
        final int dcTabSel;

        Component(int i, int i2, int i3) {
            this.scanCompSel = i;
            this.acTabSel = i2;
            this.dcTabSel = i3;
        }

        public String toString() {
            return String.format("scanCompSel: %d, acTabSel: %d, dcTabSel: %d", Integer.valueOf(this.scanCompSel), Integer.valueOf(this.acTabSel), Integer.valueOf(this.dcTabSel));
        }
    }
}
