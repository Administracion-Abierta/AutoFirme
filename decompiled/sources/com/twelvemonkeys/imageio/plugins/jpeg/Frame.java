package com.twelvemonkeys.imageio.plugins.jpeg;

import com.twelvemonkeys.imageio.stream.SubImageInputStream;
import java.io.DataInput;
import java.io.IOException;
import java.util.Arrays;
import javax.imageio.IIOException;
import javax.imageio.stream.ImageInputStream;
import org.apache.pdfbox.contentstream.operator.OperatorName;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/plugins/jpeg/Frame.class */
final class Frame extends Segment {
    final int samplePrecision;
    final int lines;
    final int samplesPerLine;
    final Component[] components;

    private Frame(int i, int i2, int i3, int i4, Component[] componentArr) {
        super(i);
        this.samplePrecision = i2;
        this.lines = i3;
        this.samplesPerLine = i4;
        this.components = componentArr;
    }

    int process() {
        return this.marker & 63;
    }

    int componentsInFrame() {
        return this.components.length;
    }

    Component getComponent(int i) {
        for (Component component : this.components) {
            if (component.id == i) {
                return component;
            }
        }
        throw new IllegalArgumentException(String.format("No such component id: %d", Integer.valueOf(i)));
    }

    public String toString() {
        return String.format("SOF%d[%04x, precision: %d, lines: %d, samples/line: %d, components: %s]", Integer.valueOf(process()), Integer.valueOf(this.marker), Integer.valueOf(this.samplePrecision), Integer.valueOf(this.lines), Integer.valueOf(this.samplesPerLine), Arrays.toString(this.components));
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.imageio.IIOException */
    static Frame read(int i, DataInput dataInput, int i2) throws IOException, IIOException {
        int unsignedByte = dataInput.readUnsignedByte();
        int unsignedShort = dataInput.readUnsignedShort();
        int unsignedShort2 = dataInput.readUnsignedShort();
        int unsignedByte2 = dataInput.readUnsignedByte();
        int i3 = 8 + (unsignedByte2 * 3);
        if (i2 != i3) {
            throw new IIOException(String.format("Unexpected SOF length: %d != %d", Integer.valueOf(i2), Integer.valueOf(i3)));
        }
        Component[] componentArr = new Component[unsignedByte2];
        for (int i4 = 0; i4 < unsignedByte2; i4++) {
            int unsignedByte3 = dataInput.readUnsignedByte();
            int unsignedByte4 = dataInput.readUnsignedByte();
            componentArr[i4] = new Component(unsignedByte3, (unsignedByte4 & 240) >> 4, unsignedByte4 & 15, dataInput.readUnsignedByte());
        }
        return new Frame(i, unsignedByte, unsignedShort, unsignedShort2, componentArr);
    }

    static Frame read(int i, ImageInputStream imageInputStream) throws IOException {
        int unsignedShort = imageInputStream.readUnsignedShort();
        return read(i, new SubImageInputStream(imageInputStream, unsignedShort), unsignedShort);
    }

    /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/plugins/jpeg/Frame$Component.class */
    public static final class Component {
        final int id;
        final int hSub;
        final int vSub;
        final int qtSel;

        Component(int i, int i2, int i3, int i4) {
            this.id = i;
            this.hSub = i2;
            this.vSub = i3;
            this.qtSel = i4;
        }

        public String toString() {
            return String.format("id: %s, sub: %d/%d, sel: %d", ((this.id < 97 || this.id > 122) && (this.id < 65 || this.id > 90)) ? Integer.valueOf(this.id) : OperatorName.SHOW_TEXT_LINE + ((char) this.id) + OperatorName.SHOW_TEXT_LINE, Integer.valueOf(this.hSub), Integer.valueOf(this.vSub), Integer.valueOf(this.qtSel));
        }
    }
}
