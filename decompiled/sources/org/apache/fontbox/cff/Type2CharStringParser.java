package org.apache.fontbox.cff;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/cff/Type2CharStringParser.class */
public class Type2CharStringParser {
    private int hstemCount = 0;
    private int vstemCount = 0;
    private List<Object> sequence = null;
    private final String fontName;
    private final String glyphName;

    public Type2CharStringParser(String fontName, String glyphName) {
        this.fontName = fontName;
        this.glyphName = glyphName;
    }

    public Type2CharStringParser(String fontName, int cid) {
        this.fontName = fontName;
        this.glyphName = String.format(Locale.US, "%04x", Integer.valueOf(cid));
    }

    public List<Object> parse(byte[] bytes, byte[][] globalSubrIndex, byte[][] localSubrIndex) throws IOException {
        return parse(bytes, globalSubrIndex, localSubrIndex, true);
    }

    private List<Object> parse(byte[] bytes, byte[][] globalSubrIndex, byte[][] localSubrIndex, boolean init) throws IOException {
        int bias;
        int bias2;
        if (init) {
            this.hstemCount = 0;
            this.vstemCount = 0;
            this.sequence = new ArrayList();
        }
        DataInput input = new DataInput(bytes);
        boolean localSubroutineIndexProvided = localSubrIndex != null && localSubrIndex.length > 0;
        boolean globalSubroutineIndexProvided = globalSubrIndex != null && globalSubrIndex.length > 0;
        while (input.hasRemaining()) {
            int b0 = input.readUnsignedByte();
            if (b0 == 10 && localSubroutineIndexProvided) {
                Integer operand = (Integer) this.sequence.remove(this.sequence.size() - 1);
                int nSubrs = localSubrIndex.length;
                if (nSubrs < 1240) {
                    bias2 = 107;
                } else if (nSubrs < 33900) {
                    bias2 = 1131;
                } else {
                    bias2 = 32768;
                }
                int subrNumber = bias2 + operand.intValue();
                if (subrNumber < localSubrIndex.length) {
                    byte[] subrBytes = localSubrIndex[subrNumber];
                    parse(subrBytes, globalSubrIndex, localSubrIndex, false);
                    Object lastItem = this.sequence.get(this.sequence.size() - 1);
                    if ((lastItem instanceof CharStringCommand) && ((CharStringCommand) lastItem).getKey().getValue()[0] == 11) {
                        this.sequence.remove(this.sequence.size() - 1);
                    }
                }
            } else if (b0 == 29 && globalSubroutineIndexProvided) {
                Integer operand2 = (Integer) this.sequence.remove(this.sequence.size() - 1);
                int nSubrs2 = globalSubrIndex.length;
                if (nSubrs2 < 1240) {
                    bias = 107;
                } else if (nSubrs2 < 33900) {
                    bias = 1131;
                } else {
                    bias = 32768;
                }
                int subrNumber2 = bias + operand2.intValue();
                if (subrNumber2 < globalSubrIndex.length) {
                    byte[] subrBytes2 = globalSubrIndex[subrNumber2];
                    parse(subrBytes2, globalSubrIndex, localSubrIndex, false);
                    Object lastItem2 = this.sequence.get(this.sequence.size() - 1);
                    if ((lastItem2 instanceof CharStringCommand) && ((CharStringCommand) lastItem2).getKey().getValue()[0] == 11) {
                        this.sequence.remove(this.sequence.size() - 1);
                    }
                }
            } else if (b0 >= 0 && b0 <= 27) {
                this.sequence.add(readCommand(b0, input));
            } else if (b0 == 28) {
                this.sequence.add(readNumber(b0, input));
            } else if (b0 >= 29 && b0 <= 31) {
                this.sequence.add(readCommand(b0, input));
            } else if (b0 >= 32 && b0 <= 255) {
                this.sequence.add(readNumber(b0, input));
            } else {
                throw new IllegalArgumentException();
            }
        }
        return this.sequence;
    }

    private CharStringCommand readCommand(int b0, DataInput input) throws IOException {
        if (b0 == 1 || b0 == 18) {
            this.hstemCount += peekNumbers().size() / 2;
        } else if (b0 == 3 || b0 == 19 || b0 == 20 || b0 == 23) {
            this.vstemCount += peekNumbers().size() / 2;
        }
        if (b0 == 12) {
            int b1 = input.readUnsignedByte();
            return new CharStringCommand(b0, b1);
        }
        if (b0 == 19 || b0 == 20) {
            int[] value = new int[1 + getMaskLength()];
            value[0] = b0;
            for (int i = 1; i < value.length; i++) {
                value[i] = input.readUnsignedByte();
            }
            return new CharStringCommand(value);
        }
        return new CharStringCommand(b0);
    }

    private Number readNumber(int b0, DataInput input) throws IOException {
        if (b0 == 28) {
            return Integer.valueOf(input.readShort());
        }
        if (b0 >= 32 && b0 <= 246) {
            return Integer.valueOf(b0 - 139);
        }
        if (b0 >= 247 && b0 <= 250) {
            int b1 = input.readUnsignedByte();
            return Integer.valueOf(((b0 - 247) * 256) + b1 + 108);
        }
        if (b0 >= 251 && b0 <= 254) {
            int b2 = input.readUnsignedByte();
            return Integer.valueOf((((-(b0 - 251)) * 256) - b2) - 108);
        }
        if (b0 == 255) {
            short value = input.readShort();
            double fraction = ((double) input.readUnsignedShort()) / 65535.0d;
            return Double.valueOf(((double) value) + fraction);
        }
        throw new IllegalArgumentException();
    }

    private int getMaskLength() {
        int hintCount = this.hstemCount + this.vstemCount;
        int length = hintCount / 8;
        if (hintCount % 8 > 0) {
            length++;
        }
        return length;
    }

    private List<Number> peekNumbers() {
        List<Number> numbers = new ArrayList<>();
        for (int i = this.sequence.size() - 1; i > -1; i--) {
            Object object = this.sequence.get(i);
            if (!(object instanceof Number)) {
                return numbers;
            }
            numbers.add(0, (Number) object);
        }
        return numbers;
    }
}
