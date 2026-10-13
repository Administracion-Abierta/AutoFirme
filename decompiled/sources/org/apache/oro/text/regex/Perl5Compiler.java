package org.apache.oro.text.regex;

import java.util.HashMap;
import org.apache.pdfbox.pdmodel.interactive.action.PDWindowsLaunchParams;

/* JADX INFO: loaded from: autofirma.jar:org/apache/oro/text/regex/Perl5Compiler.class */
public final class Perl5Compiler implements PatternCompiler {
    private static final int __WORSTCASE = 0;
    private static final int __NONNULL = 1;
    private static final int __SIMPLE = 2;
    private static final int __SPSTART = 4;
    private static final int __TRYAGAIN = 8;
    private static final char __CASE_INSENSITIVE = 1;
    private static final char __GLOBAL = 2;
    private static final char __KEEP = 4;
    private static final char __MULTILINE = '\b';
    private static final char __SINGLELINE = 16;
    private static final char __EXTENDED = ' ';
    private static final char __READ_ONLY = 32768;
    private static final String __HEX_DIGIT = "0123456789abcdef0123456789ABCDEFx";
    private CharStringPointer __input;
    private boolean __sawBackreference;
    private final char[] __modifierFlags = {0};
    private int __numParentheses;
    private int __programSize;
    private int __cost;
    private char[] __program;
    private static final HashMap<String, Character> __hashPOSIX = new HashMap<>();
    public static final int DEFAULT_MASK = 0;
    public static final int CASE_INSENSITIVE_MASK = 1;
    public static final int MULTILINE_MASK = 8;
    public static final int SINGLELINE_MASK = 16;
    public static final int EXTENDED_MASK = 32;
    public static final int READ_ONLY_MASK = 32768;

    static {
        __hashPOSIX.put("alnum", '2');
        __hashPOSIX.put("word", (char) 18);
        __hashPOSIX.put("alpha", '&');
        __hashPOSIX.put("blank", '\'');
        __hashPOSIX.put("cntrl", '(');
        __hashPOSIX.put("digit", (char) 24);
        __hashPOSIX.put("graph", ')');
        __hashPOSIX.put("lower", '*');
        __hashPOSIX.put(PDWindowsLaunchParams.OPERATION_PRINT, '+');
        __hashPOSIX.put("punct", ',');
        __hashPOSIX.put("space", (char) 22);
        __hashPOSIX.put("upper", '-');
        __hashPOSIX.put("xdigit", '.');
        __hashPOSIX.put("ascii", '3');
    }

    public static final String quotemeta(char[] expression) {
        StringBuffer buffer = new StringBuffer(2 * expression.length);
        for (int ch = 0; ch < expression.length; ch++) {
            if (!OpCode._isWordCharacter(expression[ch])) {
                buffer.append('\\');
            }
            buffer.append(expression[ch]);
        }
        return buffer.toString();
    }

    public static final String quotemeta(String expression) {
        return quotemeta(expression.toCharArray());
    }

    private static boolean __isSimpleRepetitionOp(char ch) {
        return ch == '*' || ch == '+' || ch == '?';
    }

    private static boolean __isComplexRepetitionOp(char[] ch, int offset) {
        if (offset >= ch.length || offset < 0) {
            return false;
        }
        return ch[offset] == '*' || ch[offset] == '+' || ch[offset] == '?' || (ch[offset] == '{' && __parseRepetition(ch, offset));
    }

    private static boolean __parseRepetition(char[] str, int off) {
        if (str[off] != '{') {
            return false;
        }
        int offset = off + 1;
        if (offset >= str.length || !Character.isDigit(str[offset])) {
            return false;
        }
        while (offset < str.length && Character.isDigit(str[offset])) {
            offset++;
        }
        if (offset < str.length && str[offset] == ',') {
            offset++;
        }
        while (offset < str.length && Character.isDigit(str[offset])) {
            offset++;
        }
        if (offset >= str.length || str[offset] != '}') {
            return false;
        }
        return true;
    }

    private static int __parseHex(char[] str, int off, int ml, int[] scanned) {
        int index;
        int maxLength = ml;
        int offset = off;
        int val = 0;
        scanned[0] = 0;
        while (offset < str.length) {
            int i = maxLength;
            maxLength--;
            if (i <= 0 || (index = __HEX_DIGIT.indexOf(str[offset])) == -1) {
                break;
            }
            val = (val << 4) | (index & 15);
            offset++;
            scanned[0] = scanned[0] + 1;
        }
        return val;
    }

    private static int __parseOctal(char[] str, int off, int ml, int[] scanned) {
        int maxLength = ml;
        int val = 0;
        int offset = off;
        scanned[0] = 0;
        while (offset < str.length && maxLength > 0 && str[offset] >= '0' && str[offset] <= '7') {
            val = (val << 3) | (str[offset] - '0');
            maxLength--;
            offset++;
            scanned[0] = scanned[0] + 1;
        }
        return val;
    }

    private static void __setModifierFlag(char[] flags, char ch) {
        switch (ch) {
            case 'g':
                flags[0] = (char) (flags[0] | 2);
                break;
            case 'i':
                flags[0] = (char) (flags[0] | 1);
                break;
            case 'm':
                flags[0] = (char) (flags[0] | '\b');
                break;
            case 'o':
                flags[0] = (char) (flags[0] | 4);
                break;
            case 's':
                flags[0] = (char) (flags[0] | 16);
                break;
            case 'x':
                flags[0] = (char) (flags[0] | ' ');
                break;
        }
    }

    private void __emitCode(char code) {
        if (this.__program != null) {
            this.__program[this.__programSize] = code;
        }
        this.__programSize++;
    }

    private int __emitNode(char operator) {
        int offset = this.__programSize;
        if (this.__program == null) {
            this.__programSize += 2;
        } else {
            char[] cArr = this.__program;
            int i = this.__programSize;
            this.__programSize = i + 1;
            cArr[i] = operator;
            char[] cArr2 = this.__program;
            int i2 = this.__programSize;
            this.__programSize = i2 + 1;
            cArr2[i2] = 0;
        }
        return offset;
    }

    private int __emitArgNode(char operator, char arg) {
        int offset = this.__programSize;
        if (this.__program == null) {
            this.__programSize += 3;
        } else {
            char[] cArr = this.__program;
            int i = this.__programSize;
            this.__programSize = i + 1;
            cArr[i] = operator;
            char[] cArr2 = this.__program;
            int i2 = this.__programSize;
            this.__programSize = i2 + 1;
            cArr2[i2] = 0;
            char[] cArr3 = this.__program;
            int i3 = this.__programSize;
            this.__programSize = i3 + 1;
            cArr3[i3] = arg;
        }
        return offset;
    }

    private void __programInsertOperator(char operator, int oper) {
        int offset = OpCode._opType[operator] == '\n' ? 2 : 0;
        if (this.__program == null) {
            this.__programSize += 2 + offset;
            return;
        }
        int src = this.__programSize;
        this.__programSize += 2 + offset;
        int dest = this.__programSize;
        while (src > oper) {
            src--;
            dest--;
            this.__program[dest] = this.__program[src];
        }
        int operand = oper + 1;
        this.__program[oper] = operator;
        int operand2 = operand + 1;
        this.__program[operand] = 0;
        while (true) {
            int i = offset;
            offset--;
            if (i > 0) {
                int i2 = operand2;
                operand2++;
                this.__program[i2] = 0;
            } else {
                return;
            }
        }
    }

    private void __programAddTail(int current, int value) {
        int scan;
        int offset;
        if (this.__program == null || current == -1) {
            return;
        }
        int i = current;
        while (true) {
            scan = i;
            int temp = OpCode._getNext(this.__program, scan);
            if (temp == -1) {
                break;
            } else {
                i = temp;
            }
        }
        if (this.__program[scan] == '\r') {
            offset = scan - value;
        } else {
            offset = value - scan;
        }
        this.__program[scan + 1] = (char) offset;
    }

    private void __programAddOperatorTail(int current, int value) {
        if (this.__program == null || current == -1 || OpCode._opType[this.__program[current]] != '\f') {
            return;
        }
        __programAddTail(OpCode._getNextOperator(current), value);
    }

    private char __getNextChar() {
        char ret = this.__input._postIncrement();
        while (true) {
            char value = this.__input._getValue();
            if (value == '(' && this.__input._getValueRelative(1) == '?' && this.__input._getValueRelative(2) == '#') {
                while (value != 65535 && value != ')') {
                    value = this.__input._increment();
                }
                this.__input._increment();
            } else if ((this.__modifierFlags[0] & ' ') != 0) {
                if (Character.isWhitespace(value)) {
                    this.__input._increment();
                } else {
                    if (value != '#') {
                        break;
                    }
                    while (value != 65535 && value != '\n') {
                        value = this.__input._increment();
                    }
                    this.__input._increment();
                }
            } else {
                break;
            }
        }
        return ret;
    }

    private int __parseAlternation(int[] retFlags) throws MalformedPatternException {
        int flags = 0;
        retFlags[0] = 0;
        int offset = __emitNode('\f');
        int chain = -1;
        if (this.__input._getOffset() == 0) {
            this.__input._setOffset(-1);
            __getNextChar();
        } else {
            this.__input._decrement();
            __getNextChar();
        }
        char c_getValue = this.__input._getValue();
        while (true) {
            char value = c_getValue;
            if (value == 65535 || value == '|' || value == ')') {
                break;
            }
            flags &= -9;
            int latest = __parseBranch(retFlags);
            if (latest == -1) {
                if ((flags & 8) != 0) {
                    c_getValue = this.__input._getValue();
                } else {
                    return -1;
                }
            } else {
                retFlags[0] = retFlags[0] | (flags & 1);
                if (chain == -1) {
                    retFlags[0] = retFlags[0] | (flags & 4);
                } else {
                    this.__cost++;
                    __programAddTail(chain, latest);
                }
                chain = latest;
                c_getValue = this.__input._getValue();
            }
        }
        if (chain == -1) {
            __emitNode((char) 15);
        }
        return offset;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code duplicated, block: B:155:0x08a5  */
    /* JADX WARN: Code duplicated, block: B:156:0x08ae A[PHI: r14
      0x08ae: PHI (r14v4 'pOffset' int) = (r14v2 'pOffset' int), (r14v6 'pOffset' int) binds: [B:101:0x0556, B:154:0x08a2] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARN: Code duplicated, block: B:159:0x08c7  */
    /* JADX WARN: Code duplicated, block: B:164:0x08dd  */
    /* JADX WARN: Code duplicated, block: B:174:0x0925  */
    /* JADX WARN: Code duplicated, block: B:176:0x092f  */
    /* JADX WARN: Code duplicated, block: B:178:0x0934  */
    /* JADX WARN: Code duplicated, block: B:181:0x0941  */
    /* JADX WARN: Code duplicated, block: B:184:0x094f  */
    private int __parseAtom(int[] retFlags) throws MalformedPatternException {
        char ender;
        int[] flags = {0};
        retFlags[0] = 0;
        boolean doDefault = false;
        int offset = -1;
        while (true) {
            char value = this.__input._getValue();
            switch (value) {
                case '#':
                    if ((this.__modifierFlags[0] & ' ') != 0) {
                        while (!this.__input._isAtEnd() && this.__input._getValue() != '\n') {
                            this.__input._increment();
                        }
                        if (!this.__input._isAtEnd()) {
                            break;
                        }
                    }
                    this.__input._increment();
                    doDefault = true;
                    break;
                case '$':
                    __getNextChar();
                    if ((this.__modifierFlags[0] & '\b') != 0) {
                        offset = __emitNode((char) 5);
                    } else if ((this.__modifierFlags[0] & 16) != 0) {
                        offset = __emitNode((char) 6);
                    } else {
                        offset = __emitNode((char) 4);
                    }
                    break;
                case '(':
                    __getNextChar();
                    offset = __parseExpression(true, flags);
                    if (offset == -1) {
                        if ((flags[0] & 8) == 0) {
                            return -1;
                        }
                    } else {
                        retFlags[0] = retFlags[0] | (flags[0] & 5);
                    }
                    break;
                case ')':
                case '|':
                    if ((flags[0] & 8) != 0) {
                        retFlags[0] = retFlags[0] | 8;
                        return -1;
                    }
                    throw new MalformedPatternException("Error in expression at " + this.__input._toString(this.__input._getOffset()));
                case '*':
                case '+':
                case '?':
                    throw new MalformedPatternException("?+* follows nothing in expression");
                case '.':
                    __getNextChar();
                    if ((this.__modifierFlags[0] & 16) != 0) {
                        offset = __emitNode('\b');
                    } else {
                        offset = __emitNode((char) 7);
                    }
                    this.__cost++;
                    retFlags[0] = retFlags[0] | 3;
                    break;
                case '[':
                    this.__input._increment();
                    offset = __parseUnicodeClass();
                    retFlags[0] = retFlags[0] | 3;
                    break;
                case '\\':
                    char value2 = this.__input._increment();
                    switch (value2) {
                        case 0:
                        case 65535:
                            if (this.__input._isAtEnd()) {
                                throw new MalformedPatternException("Trailing \\ in expression.");
                            }
                            doDefault = true;
                            break;
                        case '0':
                        case 'a':
                        case 'c':
                        case 'e':
                        case 'f':
                        case 'n':
                        case 'r':
                        case 't':
                        case 'x':
                            doDefault = true;
                            break;
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                        case '8':
                        case '9':
                            StringBuffer buffer = new StringBuffer(10);
                            int num = 0;
                            char c_getValueRelative = this.__input._getValueRelative(0);
                            while (true) {
                                char value3 = c_getValueRelative;
                                if (Character.isDigit(value3)) {
                                    buffer.append(value3);
                                    num++;
                                    c_getValueRelative = this.__input._getValueRelative(num);
                                } else {
                                    try {
                                        int num2 = Integer.parseInt(buffer.toString());
                                        if (num2 > 9 && num2 >= this.__numParentheses) {
                                            doDefault = true;
                                        } else {
                                            if (num2 >= this.__numParentheses) {
                                                throw new MalformedPatternException("Invalid backreference: \\" + num2);
                                            }
                                            this.__sawBackreference = true;
                                            offset = __emitArgNode((char) 26, (char) num2);
                                            retFlags[0] = retFlags[0] | 1;
                                            char c_getValue = this.__input._getValue();
                                            while (Character.isDigit(value)) {
                                                c_getValue = this.__input._increment();
                                                break;
                                            }
                                            this.__input._decrement();
                                            __getNextChar();
                                        }
                                    } catch (NumberFormatException e) {
                                        throw new MalformedPatternException("Unexpected number format exception.  Please report this bug.NumberFormatException message: " + e.getMessage());
                                    }
                                }
                                break;
                            }
                            break;
                        case 'A':
                            offset = __emitNode((char) 3);
                            retFlags[0] = retFlags[0] | 2;
                            __getNextChar();
                            break;
                        case 'B':
                            offset = __emitNode((char) 21);
                            retFlags[0] = retFlags[0] | 2;
                            __getNextChar();
                            break;
                        case 'D':
                            offset = __emitNode((char) 25);
                            retFlags[0] = retFlags[0] | 3;
                            __getNextChar();
                            break;
                        case 'G':
                            offset = __emitNode((char) 30);
                            retFlags[0] = retFlags[0] | 2;
                            __getNextChar();
                            break;
                        case 'S':
                            offset = __emitNode((char) 23);
                            retFlags[0] = retFlags[0] | 3;
                            __getNextChar();
                            break;
                        case 'W':
                            offset = __emitNode((char) 19);
                            retFlags[0] = retFlags[0] | 3;
                            __getNextChar();
                            break;
                        case 'Z':
                            offset = __emitNode((char) 6);
                            retFlags[0] = retFlags[0] | 2;
                            __getNextChar();
                            break;
                        case 'b':
                            offset = __emitNode((char) 20);
                            retFlags[0] = retFlags[0] | 2;
                            __getNextChar();
                            break;
                        case 'd':
                            offset = __emitNode((char) 24);
                            retFlags[0] = retFlags[0] | 3;
                            __getNextChar();
                            break;
                        case 's':
                            offset = __emitNode((char) 22);
                            retFlags[0] = retFlags[0] | 3;
                            __getNextChar();
                            break;
                        case 'w':
                            offset = __emitNode((char) 18);
                            retFlags[0] = retFlags[0] | 3;
                            __getNextChar();
                            break;
                        default:
                            doDefault = true;
                            break;
                    }
                    break;
                case '^':
                    __getNextChar();
                    if ((this.__modifierFlags[0] & '\b') != 0) {
                        offset = __emitNode((char) 2);
                    } else if ((this.__modifierFlags[0] & 16) != 0) {
                        offset = __emitNode((char) 3);
                    } else {
                        offset = __emitNode((char) 1);
                    }
                    break;
                default:
                    this.__input._increment();
                    doDefault = true;
                    break;
            }
        }
        if (doDefault) {
            offset = __emitNode((char) 14);
            __emitCode((char) 65535);
            int length = 0;
            int pOffset = this.__input._getOffset() - 1;
            int maxOffset = this.__input._getLength();
            while (length < 127 && pOffset < maxOffset) {
                int lastOffset = pOffset;
                char value4 = this.__input._getValue(pOffset);
                switch (value4) {
                    case '\t':
                    case '\n':
                    case 11:
                    case '\f':
                    case '\r':
                    case ' ':
                        if ((this.__modifierFlags[0] & ' ') != 0) {
                            pOffset++;
                            length--;
                        } else {
                            int i = pOffset;
                            pOffset++;
                            ender = this.__input._getValue(i);
                            if ((this.__modifierFlags[0] & 1) != 0 && Character.isUpperCase(ender)) {
                                ender = Character.toLowerCase(ender);
                            }
                            if (pOffset >= maxOffset && __isComplexRepetitionOp(this.__input._array, pOffset)) {
                                if (length > 0) {
                                    pOffset = lastOffset;
                                } else {
                                    length++;
                                    __emitCode(ender);
                                }
                                this.__input._setOffset(pOffset - 1);
                                __getNextChar();
                                if (length < 0) {
                                    throw new MalformedPatternException("Unexpected compilation failure.  Please report this bug!");
                                }
                                if (length > 0) {
                                    retFlags[0] = retFlags[0] | 1;
                                }
                                if (length == 1) {
                                    retFlags[0] = retFlags[0] | 2;
                                }
                                if (this.__program != null) {
                                    this.__program[OpCode._getOperand(offset)] = (char) length;
                                }
                                __emitCode((char) 65535);
                            } else {
                                __emitCode(ender);
                            }
                        }
                        length++;
                        break;
                    case '#':
                        if ((this.__modifierFlags[0] & ' ') != 0) {
                            while (pOffset < maxOffset && this.__input._getValue(pOffset) != '\n') {
                                pOffset++;
                            }
                        }
                        if ((this.__modifierFlags[0] & ' ') != 0) {
                            pOffset++;
                            length--;
                        } else {
                            int i2 = pOffset;
                            pOffset++;
                            ender = this.__input._getValue(i2);
                            if ((this.__modifierFlags[0] & 1) != 0) {
                                ender = Character.toLowerCase(ender);
                            }
                            if (pOffset >= maxOffset) {
                            }
                            __emitCode(ender);
                        }
                        length++;
                        break;
                    case '$':
                    case '(':
                    case ')':
                    case '.':
                    case '[':
                    case '^':
                    case '|':
                        this.__input._setOffset(pOffset - 1);
                        __getNextChar();
                        if (length < 0) {
                            throw new MalformedPatternException("Unexpected compilation failure.  Please report this bug!");
                        }
                        if (length > 0) {
                            retFlags[0] = retFlags[0] | 1;
                        }
                        if (length == 1) {
                            retFlags[0] = retFlags[0] | 2;
                        }
                        if (this.__program != null) {
                            this.__program[OpCode._getOperand(offset)] = (char) length;
                        }
                        __emitCode((char) 65535);
                        break;
                        break;
                    case '\\':
                        int pOffset2 = pOffset + 1;
                        char value5 = this.__input._getValue(pOffset2);
                        switch (value5) {
                            case 0:
                            case 65535:
                                if (pOffset2 >= maxOffset) {
                                    throw new MalformedPatternException("Trailing \\ in expression.");
                                }
                                pOffset = pOffset2 + 1;
                                ender = this.__input._getValue(pOffset2);
                                if ((this.__modifierFlags[0] & 1) != 0) {
                                    ender = Character.toLowerCase(ender);
                                }
                                if (pOffset >= maxOffset) {
                                }
                                __emitCode(ender);
                                length++;
                                break;
                            case '0':
                            case '1':
                            case '2':
                            case '3':
                            case '4':
                            case '5':
                            case '6':
                            case '7':
                            case '8':
                            case '9':
                                boolean doOctal = false;
                                char value6 = this.__input._getValue(pOffset2);
                                if (value6 == '0') {
                                    doOctal = true;
                                }
                                char value7 = this.__input._getValue(pOffset2 + 1);
                                if (Character.isDigit(value7)) {
                                    StringBuffer buffer2 = new StringBuffer(10);
                                    int num3 = pOffset2;
                                    char c_getValue2 = this.__input._getValue(num3);
                                    while (true) {
                                        char value8 = c_getValue2;
                                        if (Character.isDigit(value8)) {
                                            buffer2.append(value8);
                                            num3++;
                                            c_getValue2 = this.__input._getValue(num3);
                                        } else {
                                            try {
                                                int num4 = Integer.parseInt(buffer2.toString());
                                                if (!doOctal) {
                                                    doOctal = num4 >= this.__numParentheses;
                                                }
                                            } catch (NumberFormatException e2) {
                                                throw new MalformedPatternException("Unexpected number format exception.  Please report this bug.NumberFormatException message: " + e2.getMessage());
                                            }
                                        }
                                    }
                                }
                                if (doOctal) {
                                    int[] numLength = new int[1];
                                    ender = (char) __parseOctal(this.__input._array, pOffset2, 3, numLength);
                                    pOffset = pOffset2 + numLength[0];
                                    if ((this.__modifierFlags[0] & 1) != 0) {
                                        ender = Character.toLowerCase(ender);
                                    }
                                    if (pOffset >= maxOffset) {
                                    }
                                    __emitCode(ender);
                                    length++;
                                } else {
                                    pOffset = pOffset2 - 1;
                                }
                                this.__input._setOffset(pOffset - 1);
                                __getNextChar();
                                if (length < 0) {
                                    throw new MalformedPatternException("Unexpected compilation failure.  Please report this bug!");
                                }
                                if (length > 0) {
                                    retFlags[0] = retFlags[0] | 1;
                                }
                                if (length == 1) {
                                    retFlags[0] = retFlags[0] | 2;
                                }
                                if (this.__program != null) {
                                    this.__program[OpCode._getOperand(offset)] = (char) length;
                                }
                                __emitCode((char) 65535);
                                break;
                                break;
                            case 'A':
                            case 'B':
                            case 'D':
                            case 'G':
                            case 'S':
                            case 'W':
                            case 'Z':
                            case 'b':
                            case 'd':
                            case 's':
                            case 'w':
                                pOffset = pOffset2 - 1;
                                this.__input._setOffset(pOffset - 1);
                                __getNextChar();
                                if (length < 0) {
                                    throw new MalformedPatternException("Unexpected compilation failure.  Please report this bug!");
                                }
                                if (length > 0) {
                                    retFlags[0] = retFlags[0] | 1;
                                }
                                if (length == 1) {
                                    retFlags[0] = retFlags[0] | 2;
                                }
                                if (this.__program != null) {
                                    this.__program[OpCode._getOperand(offset)] = (char) length;
                                }
                                __emitCode((char) 65535);
                                break;
                                break;
                            case 'a':
                                ender = 7;
                                pOffset = pOffset2 + 1;
                                if ((this.__modifierFlags[0] & 1) != 0) {
                                    ender = Character.toLowerCase(ender);
                                }
                                if (pOffset >= maxOffset) {
                                }
                                __emitCode(ender);
                                length++;
                                break;
                            case 'c':
                                int pOffset3 = pOffset2 + 1;
                                pOffset = pOffset3 + 1;
                                char ender2 = this.__input._getValue(pOffset3);
                                if (Character.isLowerCase(ender2)) {
                                    ender2 = Character.toUpperCase(ender2);
                                }
                                ender = (char) (ender2 ^ '@');
                                if ((this.__modifierFlags[0] & 1) != 0) {
                                    ender = Character.toLowerCase(ender);
                                }
                                if (pOffset >= maxOffset) {
                                }
                                __emitCode(ender);
                                length++;
                                break;
                            case 'e':
                                ender = 27;
                                pOffset = pOffset2 + 1;
                                if ((this.__modifierFlags[0] & 1) != 0) {
                                    ender = Character.toLowerCase(ender);
                                }
                                if (pOffset >= maxOffset) {
                                }
                                __emitCode(ender);
                                length++;
                                break;
                            case 'f':
                                ender = '\f';
                                pOffset = pOffset2 + 1;
                                if ((this.__modifierFlags[0] & 1) != 0) {
                                    ender = Character.toLowerCase(ender);
                                }
                                if (pOffset >= maxOffset) {
                                }
                                __emitCode(ender);
                                length++;
                                break;
                            case 'n':
                                ender = '\n';
                                pOffset = pOffset2 + 1;
                                if ((this.__modifierFlags[0] & 1) != 0) {
                                    ender = Character.toLowerCase(ender);
                                }
                                if (pOffset >= maxOffset) {
                                }
                                __emitCode(ender);
                                length++;
                                break;
                            case 'r':
                                ender = '\r';
                                pOffset = pOffset2 + 1;
                                if ((this.__modifierFlags[0] & 1) != 0) {
                                    ender = Character.toLowerCase(ender);
                                }
                                if (pOffset >= maxOffset) {
                                }
                                __emitCode(ender);
                                length++;
                                break;
                            case 't':
                                ender = '\t';
                                pOffset = pOffset2 + 1;
                                if ((this.__modifierFlags[0] & 1) != 0) {
                                    ender = Character.toLowerCase(ender);
                                }
                                if (pOffset >= maxOffset) {
                                }
                                __emitCode(ender);
                                length++;
                                break;
                            case 'x':
                                int[] numLength2 = new int[1];
                                int pOffset4 = pOffset2 + 1;
                                ender = (char) __parseHex(this.__input._array, pOffset4, 2, numLength2);
                                pOffset = pOffset4 + numLength2[0];
                                if ((this.__modifierFlags[0] & 1) != 0) {
                                    ender = Character.toLowerCase(ender);
                                }
                                if (pOffset >= maxOffset) {
                                }
                                __emitCode(ender);
                                length++;
                                break;
                            default:
                                pOffset = pOffset2 + 1;
                                ender = this.__input._getValue(pOffset2);
                                if ((this.__modifierFlags[0] & 1) != 0) {
                                    ender = Character.toLowerCase(ender);
                                }
                                if (pOffset >= maxOffset) {
                                }
                                __emitCode(ender);
                                length++;
                                break;
                        }
                        break;
                    default:
                        int i3 = pOffset;
                        pOffset++;
                        ender = this.__input._getValue(i3);
                        if ((this.__modifierFlags[0] & 1) != 0) {
                            ender = Character.toLowerCase(ender);
                        }
                        if (pOffset >= maxOffset) {
                        }
                        __emitCode(ender);
                        length++;
                        break;
                }
            }
            this.__input._setOffset(pOffset - 1);
            __getNextChar();
            if (length < 0) {
                throw new MalformedPatternException("Unexpected compilation failure.  Please report this bug!");
            }
            if (length > 0) {
                retFlags[0] = retFlags[0] | 1;
            }
            if (length == 1) {
                retFlags[0] = retFlags[0] | 2;
            }
            if (this.__program != null) {
                this.__program[OpCode._getOperand(offset)] = (char) length;
            }
            __emitCode((char) 65535);
        }
        return offset;
    }

    /* JADX WARN: Code duplicated, block: B:100:0x03dd A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:16:0x0072 A[PHI: r8
      0x0072: PHI (r8v2 'clss' char) = (r8v1 'clss' char), (r8v27 'clss' char) binds: [B:13:0x0061, B:15:0x006f] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARN: Code duplicated, block: B:96:0x03e9  */
    /* JADX WARN: Code duplicated, block: B:98:0x03f3  */
    private int __parseUnicodeClass() throws MalformedPatternException {
        int offset;
        boolean skipTest;
        boolean range = false;
        char lastclss = 65535;
        int[] numLength = {0};
        boolean[] negFlag = {false};
        if (this.__input._getValue() == '^') {
            offset = __emitNode('$');
            this.__input._increment();
        } else {
            offset = __emitNode('#');
        }
        char clss = this.__input._getValue();
        if (clss == ']' || clss == '-') {
            skipTest = true;
        } else {
            skipTest = false;
        }
        while (true) {
            if (!this.__input._isAtEnd()) {
                char c_getValue = this.__input._getValue();
                clss = c_getValue;
                if (c_getValue == ']') {
                    if (!skipTest) {
                        if (this.__input._getValue() != ']') {
                            throw new MalformedPatternException("Unmatched [] in expression.");
                        }
                        __getNextChar();
                        __emitCode((char) 0);
                        return offset;
                    }
                }
            } else if (!skipTest) {
                if (this.__input._getValue() != ']') {
                    throw new MalformedPatternException("Unmatched [] in expression.");
                }
                __getNextChar();
                __emitCode((char) 0);
                return offset;
            }
            skipTest = false;
            boolean opcodeFlag = false;
            this.__input._increment();
            if (clss == '\\' || clss == '[') {
                if (clss == '\\') {
                    clss = this.__input._postIncrement();
                } else {
                    char posixOpCode = __parsePOSIX(negFlag);
                    if (posixOpCode != 0) {
                        opcodeFlag = true;
                        clss = posixOpCode;
                    }
                }
                if (!opcodeFlag) {
                    switch (clss) {
                        case '0':
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                        case '8':
                        case '9':
                            clss = (char) __parseOctal(this.__input._array, this.__input._getOffset() - 1, 3, numLength);
                            this.__input._increment(numLength[0] - 1);
                            break;
                        case 'D':
                            opcodeFlag = true;
                            clss = 25;
                            lastclss = 65535;
                            break;
                        case 'S':
                            opcodeFlag = true;
                            clss = 23;
                            lastclss = 65535;
                            break;
                        case 'W':
                            opcodeFlag = true;
                            clss = 19;
                            lastclss = 65535;
                            break;
                        case 'a':
                            clss = 7;
                            break;
                        case 'b':
                            clss = '\b';
                            break;
                        case 'c':
                            char clss2 = this.__input._postIncrement();
                            if (Character.isLowerCase(clss2)) {
                                clss2 = Character.toUpperCase(clss2);
                            }
                            clss = (char) (clss2 ^ '@');
                            break;
                        case 'd':
                            opcodeFlag = true;
                            clss = 24;
                            lastclss = 65535;
                            break;
                        case 'e':
                            clss = 27;
                            break;
                        case 'f':
                            clss = '\f';
                            break;
                        case 'n':
                            clss = '\n';
                            break;
                        case 'r':
                            clss = '\r';
                            break;
                        case 's':
                            opcodeFlag = true;
                            clss = 22;
                            lastclss = 65535;
                            break;
                        case 't':
                            clss = '\t';
                            break;
                        case 'w':
                            opcodeFlag = true;
                            clss = 18;
                            lastclss = 65535;
                            break;
                        case 'x':
                            clss = (char) __parseHex(this.__input._array, this.__input._getOffset(), 2, numLength);
                            this.__input._increment(numLength[0]);
                            break;
                    }
                }
            }
            if (range) {
                if (lastclss > clss) {
                    throw new MalformedPatternException("Invalid [] range in expression.");
                }
                range = false;
            } else {
                lastclss = clss;
                if (!opcodeFlag && this.__input._getValue() == '-' && this.__input._getOffset() + 1 < this.__input._getLength() && this.__input._getValueRelative(1) != ']') {
                    this.__input._increment();
                    range = true;
                }
            }
            if (lastclss == clss) {
                if (opcodeFlag) {
                    if (!negFlag[0]) {
                        __emitCode('/');
                    } else {
                        __emitCode('0');
                    }
                } else {
                    __emitCode('1');
                }
                __emitCode(clss);
                if ((this.__modifierFlags[0] & 1) != 0 && Character.isUpperCase(clss) && Character.isUpperCase(lastclss)) {
                    this.__programSize--;
                    __emitCode(Character.toLowerCase(clss));
                }
            }
            if (lastclss < clss) {
                __emitCode('%');
                __emitCode(lastclss);
                __emitCode(clss);
                if ((this.__modifierFlags[0] & 1) != 0 && Character.isUpperCase(clss) && Character.isUpperCase(lastclss)) {
                    this.__programSize -= 2;
                    __emitCode(Character.toLowerCase(lastclss));
                    __emitCode(Character.toLowerCase(clss));
                }
                range = false;
            }
            lastclss = clss;
        }
    }

    private char __parsePOSIX(boolean[] negFlag) {
        Object opcode;
        int offset = this.__input._getOffset();
        int len = this.__input._getLength();
        int pos = offset + 1;
        if (this.__input._getValue(offset) != ':') {
            return (char) 0;
        }
        if (this.__input._getValue(pos) == '^') {
            negFlag[0] = true;
            pos++;
        } else {
            negFlag[0] = false;
        }
        StringBuffer buf = new StringBuffer();
        while (true) {
            try {
                int i = pos;
                pos++;
                char value = this.__input._getValue(i);
                if (value == ':' || pos >= len) {
                    break;
                }
                buf.append(value);
            } catch (Exception e) {
                return (char) 0;
            }
        }
        int pos2 = pos + 1;
        if (this.__input._getValue(pos) != ']' || (opcode = __hashPOSIX.get(buf.toString())) == null) {
            return (char) 0;
        }
        this.__input._setOffset(pos2);
        return ((Character) opcode).charValue();
    }

    private int __parseBranch(int[] retFlags) throws MalformedPatternException {
        char value;
        int pos;
        boolean nestCheck = false;
        boolean handleRepetition = false;
        int[] flags = {0};
        int min = 0;
        int max = 65535;
        int offset = __parseAtom(flags);
        if (offset == -1) {
            if ((flags[0] & 8) != 0) {
                retFlags[0] = retFlags[0] | 8;
                return -1;
            }
            return -1;
        }
        char operator = this.__input._getValue();
        if (operator == '(' && this.__input._getValueRelative(1) == '?' && this.__input._getValueRelative(2) == '#') {
            while (operator != 65535 && operator != ')') {
                operator = this.__input._increment();
            }
            if (operator != 65535) {
                __getNextChar();
                operator = this.__input._getValue();
            }
        }
        if (operator == '{' && __parseRepetition(this.__input._array, this.__input._getOffset())) {
            int next = this.__input._getOffset() + 1;
            int maxOffset = this.__input._getLength();
            int pos2 = maxOffset;
            char c_getValue = this.__input._getValue(next);
            while (true) {
                value = c_getValue;
                if (!Character.isDigit(value) && value != ',') {
                    break;
                }
                if (value == ',') {
                    if (pos2 != maxOffset) {
                        break;
                    }
                    pos2 = next;
                }
                next++;
                c_getValue = this.__input._getValue(next);
            }
            if (value == '}') {
                StringBuffer buffer = new StringBuffer(10);
                if (pos2 == maxOffset) {
                    pos2 = next;
                }
                this.__input._increment();
                int num = this.__input._getOffset();
                char c_getValue2 = this.__input._getValue(num);
                while (true) {
                    char value2 = c_getValue2;
                    if (Character.isDigit(value2)) {
                        buffer.append(value2);
                        num++;
                        c_getValue2 = this.__input._getValue(num);
                    } else {
                        try {
                            break;
                        } catch (NumberFormatException e) {
                            throw new MalformedPatternException("Unexpected number format exception.  Please report this bug.NumberFormatException message: " + e.getMessage());
                        }
                    }
                }
                min = Integer.parseInt(buffer.toString());
                if (this.__input._getValue(pos2) == ',') {
                    pos = pos2 + 1;
                } else {
                    pos = this.__input._getOffset();
                }
                int num2 = pos;
                StringBuffer buffer2 = new StringBuffer(10);
                char c_getValue3 = this.__input._getValue(num2);
                while (true) {
                    char value3 = c_getValue3;
                    if (!Character.isDigit(value3)) {
                        break;
                    }
                    buffer2.append(value3);
                    num2++;
                    c_getValue3 = this.__input._getValue(num2);
                }
                if (num2 != pos) {
                    try {
                        max = Integer.parseInt(buffer2.toString());
                    } catch (NumberFormatException e2) {
                        throw new MalformedPatternException("Unexpected number format exception.  Please report this bug.NumberFormatException message: " + e2.getMessage());
                    }
                }
                if (max == 0 && this.__input._getValue(pos) != '0') {
                    max = 65535;
                }
                this.__input._setOffset(next);
                __getNextChar();
                nestCheck = true;
                handleRepetition = true;
            }
        }
        if (!nestCheck) {
            handleRepetition = false;
            if (!__isSimpleRepetitionOp(operator)) {
                retFlags[0] = flags[0];
                return offset;
            }
            __getNextChar();
            retFlags[0] = operator != '+' ? 4 : 1;
            if (operator == '*' && (flags[0] & 2) != 0) {
                __programInsertOperator((char) 16, offset);
                this.__cost += 4;
            } else if (operator == '*') {
                min = 0;
                handleRepetition = true;
            } else if (operator == '+' && (flags[0] & 2) != 0) {
                __programInsertOperator((char) 17, offset);
                this.__cost += 3;
            } else if (operator == '+') {
                min = 1;
                handleRepetition = true;
            } else if (operator == '?') {
                min = 0;
                max = 1;
                handleRepetition = true;
            }
        }
        if (handleRepetition) {
            if ((flags[0] & 2) != 0) {
                this.__cost += (2 + this.__cost) / 2;
                __programInsertOperator('\n', offset);
            } else {
                this.__cost += 4 + this.__cost;
                __programAddTail(offset, __emitNode('\"'));
                __programInsertOperator((char) 11, offset);
                __programAddTail(offset, __emitNode((char) 15));
            }
            if (min > 0) {
                retFlags[0] = 1;
            }
            if (max != 0 && max < min) {
                throw new MalformedPatternException("Invalid interval {" + min + "," + max + "}");
            }
            if (this.__program != null) {
                this.__program[offset + 2] = (char) min;
                this.__program[offset + 3] = (char) max;
            }
        }
        if (this.__input._getValue() == '?') {
            __getNextChar();
            __programInsertOperator((char) 29, offset);
            __programAddTail(offset, offset + 2);
        }
        if (__isComplexRepetitionOp(this.__input._array, this.__input._getOffset())) {
            throw new MalformedPatternException("Nested repetitions *?+ in expression");
        }
        return offset;
    }

    private int __parseExpression(boolean isParenthesized, int[] hintFlags) throws MalformedPatternException {
        char paren;
        int ender;
        char value;
        char value2;
        char[] posFlags = {0};
        char[] negFlags = {0};
        int nodeOffset = -1;
        int parenthesisNum = 0;
        int[] flags = {0};
        char[] modifierFlags = posFlags;
        hintFlags[0] = 1;
        if (isParenthesized) {
            paren = 1;
            if (this.__input._getValue() == '?') {
                this.__input._increment();
                char value3 = this.__input._postIncrement();
                paren = value3;
                switch (value3) {
                    case '!':
                    case ':':
                    case '=':
                        break;
                    case '#':
                        char c_getValue = this.__input._getValue();
                        while (true) {
                            value2 = c_getValue;
                            if (value2 != 65535 && value2 != ')') {
                                c_getValue = this.__input._increment();
                            }
                        }
                        if (value2 != ')') {
                            throw new MalformedPatternException("Sequence (?#... not terminated");
                        }
                        __getNextChar();
                        hintFlags[0] = 8;
                        return -1;
                    default:
                        this.__input._decrement();
                        char c_getValue2 = this.__input._getValue();
                        while (true) {
                            value = c_getValue2;
                            if (value != 65535 && "iogmsx-".indexOf(value) != -1) {
                                if (value == '-') {
                                    modifierFlags = negFlags;
                                } else {
                                    __setModifierFlag(modifierFlags, value);
                                }
                                c_getValue2 = this.__input._increment();
                            }
                        }
                        char[] cArr = this.__modifierFlags;
                        cArr[0] = (char) (cArr[0] | posFlags[0]);
                        char[] cArr2 = this.__modifierFlags;
                        cArr2[0] = (char) (cArr2[0] & (negFlags[0] ^ (-1)));
                        if (value != ')') {
                            throw new MalformedPatternException("Sequence (?" + value + "...) not recognized");
                        }
                        __getNextChar();
                        hintFlags[0] = 8;
                        return -1;
                }
            } else {
                parenthesisNum = this.__numParentheses;
                this.__numParentheses++;
                nodeOffset = __emitArgNode((char) 27, (char) parenthesisNum);
            }
        } else {
            paren = 0;
        }
        int br = __parseAlternation(flags);
        if (br == -1) {
            return -1;
        }
        if (nodeOffset != -1) {
            __programAddTail(nodeOffset, br);
        } else {
            nodeOffset = br;
        }
        if ((flags[0] & 1) == 0) {
            hintFlags[0] = hintFlags[0] & (-2);
        }
        hintFlags[0] = hintFlags[0] | (flags[0] & 4);
        while (this.__input._getValue() == '|') {
            __getNextChar();
            int br2 = __parseAlternation(flags);
            if (br2 == -1) {
                return -1;
            }
            __programAddTail(nodeOffset, br2);
            if ((flags[0] & 1) == 0) {
                hintFlags[0] = hintFlags[0] & (-2);
            }
            hintFlags[0] = hintFlags[0] | (flags[0] & 4);
        }
        switch (paren) {
            case 0:
            default:
                ender = __emitNode((char) 0);
                break;
            case 1:
                ender = __emitArgNode((char) 28, (char) parenthesisNum);
                break;
            case '!':
            case '=':
                ender = __emitNode('!');
                hintFlags[0] = hintFlags[0] & (-2);
                break;
            case ':':
                ender = __emitNode((char) 15);
                break;
        }
        __programAddTail(nodeOffset, ender);
        int i_getNext = nodeOffset;
        while (true) {
            int br3 = i_getNext;
            if (br3 != -1) {
                __programAddOperatorTail(br3, ender);
                i_getNext = OpCode._getNext(this.__program, br3);
            } else {
                if (paren == '=') {
                    __programInsertOperator((char) 31, nodeOffset);
                    __programAddTail(nodeOffset, __emitNode((char) 15));
                } else if (paren == '!') {
                    __programInsertOperator(' ', nodeOffset);
                    __programAddTail(nodeOffset, __emitNode((char) 15));
                }
                if (paren != 0 && (this.__input._isAtEnd() || __getNextChar() != ')')) {
                    throw new MalformedPatternException("Unmatched parentheses.");
                }
                if (paren == 0 && !this.__input._isAtEnd()) {
                    if (this.__input._getValue() == ')') {
                        throw new MalformedPatternException("Unmatched parentheses.");
                    }
                    throw new MalformedPatternException("Unreached characters at end of expression.  Please report this bug!");
                }
                return nodeOffset;
            }
        }
    }

    /* JADX WARN: Code duplicated, block: B:39:0x018a  */
    /* JADX WARN: Code duplicated, block: B:40:0x0190  */
    @Override // org.apache.oro.text.regex.PatternCompiler
    public Pattern compile(char[] pattern, int options) throws MalformedPatternException {
        char op;
        int[] flags = {0};
        boolean sawOpen = false;
        boolean sawPlus = false;
        int minLength = 0;
        this.__input = new CharStringPointer(pattern);
        int i = options & 1;
        this.__modifierFlags[0] = (char) options;
        this.__sawBackreference = false;
        this.__numParentheses = 1;
        this.__programSize = 0;
        this.__cost = 0;
        this.__program = null;
        __emitCode((char) 0);
        if (__parseExpression(false, flags) == -1) {
            throw new MalformedPatternException("Unknown compilation error.");
        }
        if (this.__programSize >= 65534) {
            throw new MalformedPatternException("Expression is too large.");
        }
        this.__program = new char[this.__programSize];
        Perl5Pattern regexp = new Perl5Pattern();
        regexp._program = this.__program;
        regexp._expression = new String(pattern);
        this.__input._setOffset(0);
        this.__numParentheses = 1;
        this.__programSize = 0;
        this.__cost = 0;
        __emitCode((char) 0);
        if (__parseExpression(false, flags) == -1) {
            throw new MalformedPatternException("Unknown compilation error.");
        }
        int caseInsensitive = this.__modifierFlags[0] & 1;
        regexp._isExpensive = this.__cost >= 10;
        regexp._startClassOffset = -1;
        regexp._anchor = 0;
        regexp._back = -1;
        regexp._options = options;
        regexp._startString = null;
        regexp._mustString = null;
        String mustString = null;
        String startString = null;
        if (this.__program[OpCode._getNext(this.__program, 1)] == 0) {
            int i_getNextOperator = OpCode._getNextOperator(1);
            int scan = i_getNextOperator;
            int first = i_getNextOperator;
            char c = this.__program[first];
            while (true) {
                char op2 = c;
                if (op2 == 27) {
                    sawOpen = true;
                    if (1 == 0) {
                    }
                    if (op2 == 17) {
                        sawPlus = true;
                    } else {
                        first += OpCode._operandLength[op2];
                    }
                    first = OpCode._getNextOperator(first);
                    c = this.__program[first];
                }
                if ((op2 != '\f' || this.__program[OpCode._getNext(this.__program, first)] == '\f') && op2 != 17 && op2 != 29 && (OpCode._opType[op2] != '\n' || OpCode._getArg1(this.__program, first) <= 0)) {
                    break;
                }
                if (op2 == 17) {
                    sawPlus = true;
                } else {
                    first += OpCode._operandLength[op2];
                }
                first = OpCode._getNextOperator(first);
                c = this.__program[first];
            }
            boolean doItAgain = true;
            while (doItAgain) {
                doItAgain = false;
                char op3 = this.__program[first];
                if (op3 == 14) {
                    startString = new String(this.__program, OpCode._getOperand(first + 1), (int) this.__program[OpCode._getOperand(first)]);
                } else if (OpCode._isInArray(op3, OpCode._opLengthOne, 2)) {
                    regexp._startClassOffset = first;
                } else if (op3 == 20 || op3 == 21) {
                    regexp._startClassOffset = first;
                } else if (OpCode._opType[op3] == 1) {
                    if (op3 == 1) {
                        regexp._anchor = 1;
                    } else if (op3 == 2) {
                        regexp._anchor = 2;
                    } else {
                        regexp._anchor = 3;
                    }
                    first = OpCode._getNextOperator(first);
                    doItAgain = true;
                } else if (op3 == 16 && OpCode._opType[this.__program[OpCode._getNextOperator(first)]] == 7 && (regexp._anchor & 3) != 0) {
                    regexp._anchor = 11;
                    first = OpCode._getNextOperator(first);
                    doItAgain = true;
                }
            }
            if (sawPlus && (!sawOpen || !this.__sawBackreference)) {
                regexp._anchor |= 4;
            }
            StringBuffer lastLongest = new StringBuffer();
            StringBuffer longest = new StringBuffer();
            int length = 0;
            minLength = 0;
            int curBack = 0;
            int back = 0;
            int backmost = 0;
            while (scan > 0 && (op = this.__program[scan]) != 0) {
                if (op == '\f') {
                    if (this.__program[OpCode._getNext(this.__program, scan)] == '\f') {
                        curBack = -30000;
                        while (this.__program[scan] == '\f') {
                            scan = OpCode._getNext(this.__program, scan);
                        }
                    } else {
                        scan = OpCode._getNextOperator(scan);
                    }
                } else if (op == ' ') {
                    curBack = -30000;
                    scan = OpCode._getNext(this.__program, scan);
                } else {
                    if (op == 14) {
                        first = scan;
                        while (true) {
                            char[] cArr = this.__program;
                            int temp = OpCode._getNext(this.__program, scan);
                            if (cArr[temp] != 28) {
                                break;
                            }
                            scan = temp;
                        }
                        minLength += this.__program[OpCode._getOperand(first)];
                        char c2 = this.__program[OpCode._getOperand(first)];
                        if (curBack - back == length) {
                            lastLongest.append(new String(this.__program, OpCode._getOperand(first) + 1, (int) c2));
                            length += c2;
                            curBack += c2;
                            first = OpCode._getNext(this.__program, scan);
                        } else {
                            if (c2 >= length + (curBack >= 0 ? 1 : 0)) {
                                length = c2;
                                lastLongest = new StringBuffer(new String(this.__program, OpCode._getOperand(first) + 1, (int) c2));
                                back = curBack;
                                curBack += length;
                                first = OpCode._getNext(this.__program, scan);
                            } else {
                                curBack += c2;
                            }
                        }
                    } else if (OpCode._isInArray(op, OpCode._opLengthVaries, 0)) {
                        curBack = -30000;
                        length = 0;
                        if (lastLongest.length() > longest.length()) {
                            longest = lastLongest;
                            backmost = back;
                        }
                        lastLongest = new StringBuffer();
                        if (op == 17 && OpCode._isInArray(this.__program[OpCode._getNextOperator(scan)], OpCode._opLengthOne, 0)) {
                            minLength++;
                        } else if (OpCode._opType[op] == '\n' && OpCode._isInArray(this.__program[OpCode._getNextOperator(scan) + 2], OpCode._opLengthOne, 0)) {
                            minLength += OpCode._getArg1(this.__program, scan);
                        }
                    } else if (OpCode._isInArray(op, OpCode._opLengthOne, 0)) {
                        curBack++;
                        minLength++;
                        length = 0;
                        if (lastLongest.length() > longest.length()) {
                            longest = lastLongest;
                            backmost = back;
                        }
                        lastLongest = new StringBuffer();
                    }
                    scan = OpCode._getNext(this.__program, scan);
                }
            }
            if (lastLongest.length() + (OpCode._opType[this.__program[first]] == 4 ? 1 : 0) > longest.length()) {
                longest = lastLongest;
                backmost = back;
            } else {
                new StringBuffer();
            }
            if (longest.length() > 0 && startString == null) {
                mustString = longest.toString();
                if (backmost < 0) {
                    backmost = -1;
                }
                regexp._back = backmost;
            }
        }
        regexp._isCaseInsensitive = (caseInsensitive & 1) != 0;
        regexp._numParentheses = this.__numParentheses - 1;
        regexp._minLength = minLength;
        if (mustString != null) {
            regexp._mustString = mustString.toCharArray();
            regexp._mustUtility = 100;
        }
        if (startString != null) {
            regexp._startString = startString.toCharArray();
        }
        return regexp;
    }

    @Override // org.apache.oro.text.regex.PatternCompiler
    public Pattern compile(char[] pattern) throws MalformedPatternException {
        return compile(pattern, 0);
    }

    @Override // org.apache.oro.text.regex.PatternCompiler
    public Pattern compile(String pattern) throws MalformedPatternException {
        return compile(pattern.toCharArray(), 0);
    }

    @Override // org.apache.oro.text.regex.PatternCompiler
    public Pattern compile(String pattern, int options) throws MalformedPatternException {
        return compile(pattern.toCharArray(), options);
    }
}
