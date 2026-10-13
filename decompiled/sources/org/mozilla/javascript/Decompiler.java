package org.mozilla.javascript;

import es.gob.afirma.standalone.configurator.common.PreferencesManager;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/Decompiler.class */
public class Decompiler {
    public static final int ONLY_BODY_FLAG = 1;
    public static final int TO_SOURCE_FLAG = 2;
    public static final int INITIAL_INDENT_PROP = 1;
    public static final int INDENT_GAP_PROP = 2;
    public static final int CASE_GAP_PROP = 3;
    private static final int FUNCTION_END = 168;
    private char[] sourceBuffer = new char[128];
    private int sourceTop;
    private static final boolean printSource = false;

    String getEncodedSource() {
        return sourceToString(0);
    }

    int getCurrentOffset() {
        return this.sourceTop;
    }

    int markFunctionStart(int functionType) {
        int savedOffset = getCurrentOffset();
        if (functionType != 4) {
            addToken(110);
            append((char) functionType);
        }
        return savedOffset;
    }

    int markFunctionEnd(int functionStart) {
        int offset = getCurrentOffset();
        append((char) 168);
        return offset;
    }

    void addToken(int token) {
        if (0 > token || token > 167) {
            throw new IllegalArgumentException();
        }
        append((char) token);
    }

    void addEOL(int token) {
        if (0 > token || token > 167) {
            throw new IllegalArgumentException();
        }
        append((char) token);
        append((char) 1);
    }

    void addName(String str) {
        addToken(39);
        appendString(str);
    }

    void addString(String str) {
        addToken(41);
        appendString(str);
    }

    void addRegexp(String regexp, String flags) {
        addToken(48);
        appendString('/' + regexp + '/' + flags);
    }

    void addNumber(double n) {
        addToken(40);
        long lbits = (long) n;
        if (lbits != n) {
            long lbits2 = Double.doubleToLongBits(n);
            append('D');
            append((char) (lbits2 >> 48));
            append((char) (lbits2 >> 32));
            append((char) (lbits2 >> 16));
            append((char) lbits2);
            return;
        }
        if (lbits < 0) {
            Kit.codeBug();
        }
        if (lbits <= 65535) {
            append('S');
            append((char) lbits);
            return;
        }
        append('J');
        append((char) (lbits >> 48));
        append((char) (lbits >> 32));
        append((char) (lbits >> 16));
        append((char) lbits);
    }

    private void appendString(String str) {
        int L = str.length();
        int lengthEncodingSize = 1;
        if (L >= 32768) {
            lengthEncodingSize = 2;
        }
        int nextTop = this.sourceTop + lengthEncodingSize + L;
        if (nextTop > this.sourceBuffer.length) {
            increaseSourceCapacity(nextTop);
        }
        if (L >= 32768) {
            this.sourceBuffer[this.sourceTop] = (char) (32768 | (L >>> 16));
            this.sourceTop++;
        }
        this.sourceBuffer[this.sourceTop] = (char) L;
        this.sourceTop++;
        str.getChars(0, L, this.sourceBuffer, this.sourceTop);
        this.sourceTop = nextTop;
    }

    private void append(char c) {
        if (this.sourceTop == this.sourceBuffer.length) {
            increaseSourceCapacity(this.sourceTop + 1);
        }
        this.sourceBuffer[this.sourceTop] = c;
        this.sourceTop++;
    }

    private void increaseSourceCapacity(int minimalCapacity) {
        if (minimalCapacity <= this.sourceBuffer.length) {
            Kit.codeBug();
        }
        int newCapacity = this.sourceBuffer.length * 2;
        if (newCapacity < minimalCapacity) {
            newCapacity = minimalCapacity;
        }
        char[] tmp = new char[newCapacity];
        System.arraycopy(this.sourceBuffer, 0, tmp, 0, this.sourceTop);
        this.sourceBuffer = tmp;
    }

    private String sourceToString(int offset) {
        if (offset < 0 || this.sourceTop < offset) {
            Kit.codeBug();
        }
        return new String(this.sourceBuffer, offset, this.sourceTop - offset);
    }

    public static String decompile(String source, int flags, UintMap properties) {
        int topFunctionType;
        int length = source.length();
        if (length == 0) {
            return "";
        }
        int indent = properties.getInt(1, 0);
        if (indent < 0) {
            throw new IllegalArgumentException();
        }
        int indentGap = properties.getInt(2, 4);
        if (indentGap < 0) {
            throw new IllegalArgumentException();
        }
        int caseGap = properties.getInt(3, 2);
        if (caseGap < 0) {
            throw new IllegalArgumentException();
        }
        StringBuilder result = new StringBuilder();
        boolean justFunctionBody = 0 != (flags & 1);
        boolean toSource = 0 != (flags & 2);
        int braceNesting = 0;
        boolean afterFirstEOL = false;
        int i = 0;
        if (source.charAt(0) == 137) {
            i = 0 + 1;
            topFunctionType = -1;
        } else {
            topFunctionType = source.charAt(0 + 1);
        }
        if (!toSource) {
            result.append('\n');
            for (int j = 0; j < indent; j++) {
                result.append(' ');
            }
        } else if (topFunctionType == 2) {
            result.append('(');
        }
        while (i < length) {
            switch (source.charAt(i)) {
                case 1:
                    if (!toSource) {
                        boolean newLine = true;
                        if (!afterFirstEOL) {
                            afterFirstEOL = true;
                            if (justFunctionBody) {
                                result.setLength(0);
                                indent -= indentGap;
                                newLine = false;
                            }
                        }
                        if (newLine) {
                            result.append('\n');
                        }
                        if (i + 1 < length) {
                            int less = 0;
                            int nextToken = source.charAt(i + 1);
                            if (nextToken == 116 || nextToken == 117) {
                                less = indentGap - caseGap;
                            } else if (nextToken == 87) {
                                less = indentGap;
                            } else if (nextToken == 39) {
                                int afterName = getSourceStringEnd(source, i + 2);
                                if (source.charAt(afterName) == 'h') {
                                    less = indentGap;
                                }
                            }
                            while (less < indent) {
                                result.append(' ');
                                less++;
                            }
                        }
                    }
                    break;
                case 2:
                case 3:
                case 5:
                case 6:
                case 7:
                case '\b':
                case '!':
                case '\"':
                case '#':
                case '$':
                case '%':
                case '&':
                case '1':
                case '3':
                case '6':
                case '7':
                case '8':
                case '9':
                case ':':
                case ';':
                case '<':
                case '=':
                case '>':
                case '?':
                case '@':
                case 'A':
                case 'B':
                case 'D':
                case 'E':
                case 'F':
                case 'G':
                case 'H':
                case 'J':
                case 'K':
                case 'L':
                case 'M':
                case 'N':
                case 'O':
                case 'P':
                case 'Q':
                case 'o':
                case 'p':
                case 128:
                case 129:
                case 130:
                case 131:
                case 132:
                case 133:
                case 134:
                case 135:
                case 136:
                case 137:
                case 138:
                case 139:
                case 140:
                case 141:
                case 142:
                case 143:
                case 146:
                case 149:
                case 150:
                case 151:
                case 156:
                case 157:
                case 158:
                case 159:
                case 160:
                case 162:
                case 163:
                case 167:
                default:
                    throw new RuntimeException("Token: " + Token.name(source.charAt(i)));
                case 4:
                    result.append("return");
                    if (83 != getNext(source, length, i)) {
                        result.append(' ');
                    }
                    break;
                case '\t':
                    result.append(" | ");
                    break;
                case '\n':
                    result.append(" ^ ");
                    break;
                case 11:
                    result.append(" & ");
                    break;
                case '\f':
                    result.append(" == ");
                    break;
                case '\r':
                    result.append(" != ");
                    break;
                case 14:
                    result.append(" < ");
                    break;
                case 15:
                    result.append(" <= ");
                    break;
                case 16:
                    result.append(" > ");
                    break;
                case 17:
                    result.append(" >= ");
                    break;
                case 18:
                    result.append(" << ");
                    break;
                case 19:
                    result.append(" >> ");
                    break;
                case 20:
                    result.append(" >>> ");
                    break;
                case 21:
                    result.append(" + ");
                    break;
                case 22:
                    result.append(" - ");
                    break;
                case 23:
                    result.append(" * ");
                    break;
                case 24:
                    result.append(" / ");
                    break;
                case 25:
                    result.append(" % ");
                    break;
                case 26:
                    result.append('!');
                    break;
                case 27:
                    result.append('~');
                    break;
                case 28:
                    result.append('+');
                    break;
                case 29:
                    result.append('-');
                    break;
                case 30:
                    result.append("new ");
                    break;
                case 31:
                    result.append("delete ");
                    break;
                case ' ':
                    result.append("typeof ");
                    break;
                case '\'':
                case '0':
                    i = printSourceString(source, i + 1, false, result);
                    break;
                case '(':
                    i = printSourceNumber(source, i + 1, result);
                    break;
                case ')':
                    i = printSourceString(source, i + 1, true, result);
                    break;
                case '*':
                    result.append("null");
                    break;
                case '+':
                    result.append("this");
                    break;
                case ',':
                    result.append("false");
                    break;
                case '-':
                    result.append("true");
                    break;
                case '.':
                    result.append(" === ");
                    break;
                case '/':
                    result.append(" !== ");
                    break;
                case '2':
                    result.append("throw ");
                    break;
                case '4':
                    result.append(" in ");
                    break;
                case '5':
                    result.append(" instanceof ");
                    break;
                case 'C':
                    result.append(": ");
                    break;
                case 'I':
                    result.append("yield ");
                    break;
                case 'R':
                    result.append("try ");
                    break;
                case 'S':
                    result.append(';');
                    if (1 != getNext(source, length, i)) {
                        result.append(' ');
                    }
                    break;
                case 'T':
                    result.append('[');
                    break;
                case 'U':
                    result.append(']');
                    break;
                case 'V':
                    braceNesting++;
                    if (1 == getNext(source, length, i)) {
                        indent += indentGap;
                    }
                    result.append('{');
                    break;
                case 'W':
                    braceNesting--;
                    if (!justFunctionBody || braceNesting != 0) {
                        result.append('}');
                        switch (getNext(source, length, i)) {
                            case 1:
                            case 168:
                                indent -= indentGap;
                                break;
                            case 114:
                            case 118:
                                indent -= indentGap;
                                result.append(' ');
                                break;
                        }
                    }
                    break;
                case 'X':
                    result.append('(');
                    break;
                case 'Y':
                    result.append(')');
                    if (86 == getNext(source, length, i)) {
                        result.append(' ');
                    }
                    break;
                case 'Z':
                    result.append(", ");
                    break;
                case '[':
                    result.append(" = ");
                    break;
                case '\\':
                    result.append(" |= ");
                    break;
                case ']':
                    result.append(" ^= ");
                    break;
                case '^':
                    result.append(" &= ");
                    break;
                case '_':
                    result.append(" <<= ");
                    break;
                case '`':
                    result.append(" >>= ");
                    break;
                case 'a':
                    result.append(" >>>= ");
                    break;
                case 'b':
                    result.append(" += ");
                    break;
                case 'c':
                    result.append(" -= ");
                    break;
                case 'd':
                    result.append(" *= ");
                    break;
                case 'e':
                    result.append(" /= ");
                    break;
                case 'f':
                    result.append(" %= ");
                    break;
                case 'g':
                    result.append(" ? ");
                    break;
                case 'h':
                    if (1 == getNext(source, length, i)) {
                        result.append(':');
                    } else {
                        result.append(" : ");
                    }
                    break;
                case 'i':
                    result.append(" || ");
                    break;
                case 'j':
                    result.append(" && ");
                    break;
                case 'k':
                    result.append("++");
                    break;
                case 'l':
                    result.append("--");
                    break;
                case 'm':
                    result.append('.');
                    break;
                case 'n':
                    i++;
                    result.append("function ");
                    break;
                case 'q':
                    result.append("if ");
                    break;
                case 'r':
                    result.append("else ");
                    break;
                case 's':
                    result.append("switch ");
                    break;
                case 't':
                    result.append("case ");
                    break;
                case 'u':
                    result.append(PreferencesManager.VALUE_KEYSTORE_DEFAULT);
                    break;
                case 'v':
                    result.append("while ");
                    break;
                case 'w':
                    result.append("do ");
                    break;
                case 'x':
                    result.append("for ");
                    break;
                case 'y':
                    result.append("break");
                    if (39 == getNext(source, length, i)) {
                        result.append(' ');
                    }
                    break;
                case 'z':
                    result.append("continue");
                    if (39 == getNext(source, length, i)) {
                        result.append(' ');
                    }
                    break;
                case '{':
                    result.append("var ");
                    break;
                case '|':
                    result.append("with ");
                    break;
                case '}':
                    result.append("catch ");
                    break;
                case '~':
                    result.append("finally ");
                    break;
                case 127:
                    result.append("void ");
                    break;
                case 144:
                    result.append("..");
                    break;
                case 145:
                    result.append("::");
                    break;
                case 147:
                    result.append(".(");
                    break;
                case 148:
                    result.append('@');
                    break;
                case 152:
                case 153:
                case 164:
                    if (source.charAt(i) == 152) {
                        result.append("get ");
                    } else if (source.charAt(i) == 153) {
                        result.append("set ");
                    }
                    i = printSourceString(source, i + 1 + 1, false, result) + 1;
                    break;
                case 154:
                    result.append("let ");
                    break;
                case 155:
                    result.append("const ");
                    break;
                case 161:
                    result.append("debugger;\n");
                    break;
                case 165:
                    result.append(" => ");
                    break;
                case 166:
                    result.append("yield *");
                    break;
                case 168:
                    break;
            }
            i++;
        }
        if (!toSource) {
            if (!justFunctionBody) {
                result.append('\n');
            }
        } else if (topFunctionType == 2) {
            result.append(')');
        }
        return result.toString();
    }

    private static int getNext(String source, int length, int i) {
        if (i + 1 < length) {
            return source.charAt(i + 1);
        }
        return 0;
    }

    private static int getSourceStringEnd(String source, int offset) {
        return printSourceString(source, offset, false, null);
    }

    private static int printSourceString(String source, int offset, boolean asQuotedString, StringBuilder sb) {
        int length = source.charAt(offset);
        int offset2 = offset + 1;
        if ((32768 & length) != 0) {
            length = ((32767 & length) << 16) | source.charAt(offset2);
            offset2++;
        }
        if (sb != null) {
            String str = source.substring(offset2, offset2 + length);
            if (!asQuotedString) {
                sb.append(str);
            } else {
                sb.append('\"');
                sb.append(ScriptRuntime.escapeString(str));
                sb.append('\"');
            }
        }
        return offset2 + length;
    }

    private static int printSourceNumber(String source, int offset, StringBuilder sb) {
        int offset2;
        double number = 0.0d;
        char type = source.charAt(offset);
        int offset3 = offset + 1;
        if (type == 'S') {
            if (sb != null) {
                int ival = source.charAt(offset3);
                number = ival;
            }
            offset2 = offset3 + 1;
        } else if (type == 'J' || type == 'D') {
            if (sb != null) {
                long lbits = (((long) source.charAt(offset3)) << 48) | (((long) source.charAt(offset3 + 1)) << 32) | (((long) source.charAt(offset3 + 2)) << 16) | ((long) source.charAt(offset3 + 3));
                if (type == 'J') {
                    number = lbits;
                } else {
                    number = Double.longBitsToDouble(lbits);
                }
            }
            offset2 = offset3 + 4;
        } else {
            throw new RuntimeException();
        }
        if (sb != null) {
            sb.append(ScriptRuntime.numberToString(number, 10));
        }
        return offset2;
    }
}
