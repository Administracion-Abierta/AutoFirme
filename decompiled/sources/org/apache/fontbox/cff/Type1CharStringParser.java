package org.apache.fontbox.cff;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/cff/Type1CharStringParser.class */
public class Type1CharStringParser {
    private static final Log LOG = LogFactory.getLog(Type1CharStringParser.class);
    static final int RETURN = 11;
    static final int CALLSUBR = 10;
    static final int TWO_BYTE = 12;
    static final int CALLOTHERSUBR = 16;
    static final int POP = 17;
    private final String fontName;
    private final String glyphName;

    public Type1CharStringParser(String fontName, String glyphName) {
        this.fontName = fontName;
        this.glyphName = glyphName;
    }

    public List<Object> parse(byte[] bytes, List<byte[]> subrs) throws IOException {
        return parse(bytes, subrs, new ArrayList());
    }

    private List<Object> parse(byte[] bytes, List<byte[]> subrs, List<Object> sequence) throws IOException {
        DataInput input = new DataInput(bytes);
        while (input.hasRemaining()) {
            int b0 = input.readUnsignedByte();
            if (b0 == 10) {
                Object obj = sequence.remove(sequence.size() - 1);
                if (!(obj instanceof Integer)) {
                    LOG.warn("Parameter " + obj + " for CALLSUBR is ignored, integer expected in glyph '" + this.glyphName + "' of font " + this.fontName);
                } else {
                    Integer operand = (Integer) obj;
                    if (operand.intValue() >= 0 && operand.intValue() < subrs.size()) {
                        byte[] subrBytes = subrs.get(operand.intValue());
                        parse(subrBytes, subrs, sequence);
                        Object lastItem = sequence.get(sequence.size() - 1);
                        if ((lastItem instanceof CharStringCommand) && ((CharStringCommand) lastItem).getKey().getValue()[0] == 11) {
                            sequence.remove(sequence.size() - 1);
                        }
                    } else {
                        LOG.warn("CALLSUBR is ignored, operand: " + operand + ", subrs.size(): " + subrs.size() + " in glyph '" + this.glyphName + "' of font " + this.fontName);
                        while (sequence.get(sequence.size() - 1) instanceof Integer) {
                            sequence.remove(sequence.size() - 1);
                        }
                    }
                }
            } else if (b0 == 12 && input.peekUnsignedByte(0) == 16) {
                input.readByte();
                Integer othersubrNum = (Integer) sequence.remove(sequence.size() - 1);
                Integer numArgs = (Integer) sequence.remove(sequence.size() - 1);
                Deque<Integer> results = new ArrayDeque<>();
                switch (othersubrNum.intValue()) {
                    case 0:
                        results.push(removeInteger(sequence));
                        results.push(removeInteger(sequence));
                        sequence.remove(sequence.size() - 1);
                        sequence.add(0);
                        sequence.add(new CharStringCommand(12, 16));
                        break;
                    case 1:
                        sequence.add(1);
                        sequence.add(new CharStringCommand(12, 16));
                        break;
                    case 2:
                    default:
                        for (int i = 0; i < numArgs.intValue(); i++) {
                            results.push(removeInteger(sequence));
                        }
                        break;
                    case 3:
                        results.push(removeInteger(sequence));
                        break;
                }
                while (input.peekUnsignedByte(0) == 12 && input.peekUnsignedByte(1) == 17) {
                    input.readByte();
                    input.readByte();
                    sequence.add(results.pop());
                }
                if (results.size() > 0) {
                    LOG.warn("Value left on the PostScript stack in glyph " + this.glyphName + " of font " + this.fontName);
                }
            } else if (b0 >= 0 && b0 <= 31) {
                sequence.add(readCommand(input, b0));
            } else if (b0 >= 32 && b0 <= 255) {
                sequence.add(readNumber(input, b0));
            } else {
                throw new IllegalArgumentException();
            }
        }
        return sequence;
    }

    private static Integer removeInteger(List<Object> sequence) throws IOException {
        Object item = sequence.remove(sequence.size() - 1);
        if (item instanceof Integer) {
            return (Integer) item;
        }
        CharStringCommand command = (CharStringCommand) item;
        if (command.getKey().getValue()[0] == 12 && command.getKey().getValue()[1] == 12) {
            int a = ((Integer) sequence.remove(sequence.size() - 1)).intValue();
            int b = ((Integer) sequence.remove(sequence.size() - 1)).intValue();
            return Integer.valueOf(b / a);
        }
        throw new IOException("Unexpected char string command: " + command.getKey());
    }

    private CharStringCommand readCommand(DataInput input, int b0) throws IOException {
        if (b0 == 12) {
            int b1 = input.readUnsignedByte();
            return new CharStringCommand(b0, b1);
        }
        return new CharStringCommand(b0);
    }

    private Integer readNumber(DataInput input, int b0) throws IOException {
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
            return Integer.valueOf(input.readInt());
        }
        throw new IllegalArgumentException();
    }
}
