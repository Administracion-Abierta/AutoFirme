package org.apache.oro.text.regex;

import java.util.Stack;

/* JADX INFO: loaded from: autofirma.jar:org/apache/oro/text/regex/Perl5Matcher.class */
public final class Perl5Matcher implements PatternMatcher {
    private static final char __EOS = 65535;
    private static final int __INITIAL_NUM_OFFSETS = 20;
    private char __previousChar;
    private char[] __input;
    private char[] __originalInput;
    private Perl5Repetition __currentRep;
    private int __numParentheses;
    private int __bol;
    private int __eol;
    private int __currentOffset;
    private int __endOffset;
    private char[] __program;
    private int __expSize;
    private int __inputOffset;
    private int __lastParen;
    private int[] __beginMatchOffsets;
    private int[] __endMatchOffsets;
    private static final int __DEFAULT_LAST_MATCH_END_OFFSET = -100;
    private final boolean __multiline = false;
    private boolean __lastSuccess = false;
    private boolean __caseInsensitive = false;
    private final Stack<int[]> __stack = new Stack<>();
    private Perl5MatchResult __lastMatchResult = null;
    private int __lastMatchInputEndOffset = __DEFAULT_LAST_MATCH_END_OFFSET;

    private static boolean __compare(char[] s1, int s1off, char[] s2, int s2off, int n) {
        int s2Offs = s2off;
        int s1Offs = s1off;
        int cnt = 0;
        while (cnt < n) {
            if (s1Offs < s1.length && s2Offs < s2.length && s1[s1Offs] == s2[s2Offs]) {
                cnt++;
                s1Offs++;
                s2Offs++;
            } else {
                return false;
            }
        }
        return true;
    }

    private static int __findFirst(char[] input, int curr, int endOffset, char[] mustString) {
        int current = curr;
        if (input.length == 0) {
            return endOffset;
        }
        char ch = mustString[0];
        while (current < endOffset) {
            if (ch == input[current]) {
                int saveCurrent = current;
                int count = 0;
                while (current < endOffset && count < mustString.length && mustString[count] == input[current]) {
                    count++;
                    current++;
                }
                current = saveCurrent;
                if (count >= mustString.length) {
                    break;
                }
            }
            current++;
        }
        return current;
    }

    private void __pushState(int parenFloor) {
        int[] state;
        int stateEntries = 3 * (this.__expSize - parenFloor);
        if (stateEntries <= 0) {
            state = new int[3];
        } else {
            state = new int[stateEntries + 3];
        }
        state[0] = this.__expSize;
        state[1] = this.__lastParen;
        state[2] = this.__inputOffset;
        int paren = this.__expSize;
        while (paren > parenFloor) {
            state[stateEntries] = this.__endMatchOffsets[paren];
            state[stateEntries + 1] = this.__beginMatchOffsets[paren];
            state[stateEntries + 2] = paren;
            paren--;
            stateEntries -= 3;
        }
        this.__stack.push(state);
    }

    private void __popState() {
        int[] state = this.__stack.pop();
        this.__expSize = state[0];
        this.__lastParen = state[1];
        this.__inputOffset = state[2];
        for (int entry = 3; entry < state.length; entry += 3) {
            int paren = state[entry + 2];
            this.__beginMatchOffsets[paren] = state[entry + 1];
            if (paren <= this.__lastParen) {
                this.__endMatchOffsets[paren] = state[entry];
            }
        }
        for (int paren2 = this.__lastParen + 1; paren2 <= this.__numParentheses; paren2++) {
            if (paren2 > this.__expSize) {
                this.__beginMatchOffsets[paren2] = -1;
            }
            this.__endMatchOffsets[paren2] = -1;
        }
    }

    private void __initInterpreterGlobals(Perl5Pattern expression, char[] input, int beginOffset, int endOff, int currentOffset) {
        this.__caseInsensitive = expression._isCaseInsensitive;
        this.__input = input;
        this.__endOffset = endOff;
        this.__currentRep = new Perl5Repetition();
        this.__currentRep._numInstances = 0;
        this.__currentRep._lastRepetition = null;
        this.__program = expression._program;
        this.__stack.setSize(0);
        if (currentOffset == beginOffset || currentOffset <= 0) {
            this.__previousChar = '\n';
        } else {
            this.__previousChar = input[currentOffset - 1];
            getClass();
            if (this.__previousChar == '\n') {
                this.__previousChar = (char) 0;
            }
        }
        this.__numParentheses = expression._numParentheses;
        this.__currentOffset = currentOffset;
        this.__bol = beginOffset;
        this.__eol = endOff;
        int endOffset = this.__numParentheses + 1;
        if (this.__beginMatchOffsets == null || endOffset > this.__beginMatchOffsets.length) {
            if (endOffset < 20) {
                endOffset = 20;
            }
            this.__beginMatchOffsets = new int[endOffset];
            this.__endMatchOffsets = new int[endOffset];
        }
    }

    private void __setLastMatchResult() {
        int maxEndOffs = 0;
        this.__lastMatchResult = new Perl5MatchResult(this.__numParentheses + 1);
        if (this.__endMatchOffsets[0] > this.__originalInput.length) {
            throw new ArrayIndexOutOfBoundsException();
        }
        this.__lastMatchResult._matchBeginOffset = this.__beginMatchOffsets[0];
        while (this.__numParentheses >= 0) {
            int offs = this.__beginMatchOffsets[this.__numParentheses];
            if (offs >= 0) {
                this.__lastMatchResult._beginGroupOffset[this.__numParentheses] = offs - this.__lastMatchResult._matchBeginOffset;
            } else {
                this.__lastMatchResult._beginGroupOffset[this.__numParentheses] = -1;
            }
            int offs2 = this.__endMatchOffsets[this.__numParentheses];
            if (offs2 >= 0) {
                this.__lastMatchResult._endGroupOffset[this.__numParentheses] = offs2 - this.__lastMatchResult._matchBeginOffset;
                if (offs2 > maxEndOffs && offs2 <= this.__originalInput.length) {
                    maxEndOffs = offs2;
                }
            } else {
                this.__lastMatchResult._endGroupOffset[this.__numParentheses] = -1;
            }
            this.__numParentheses--;
        }
        this.__lastMatchResult._match = new String(this.__originalInput, this.__beginMatchOffsets[0], maxEndOffs - this.__beginMatchOffsets[0]);
        this.__originalInput = null;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code duplicated, block: B:105:0x033b  */
    /* JADX WARN: Code duplicated, block: B:107:0x034b  */
    /* JADX WARN: Code duplicated, block: B:10:0x0041  */
    /* JADX WARN: Code duplicated, block: B:110:0x035b  */
    /* JADX WARN: Code duplicated, block: B:112:0x0377  */
    /* JADX WARN: Code duplicated, block: B:114:0x037c  */
    /* JADX WARN: Code duplicated, block: B:118:0x0394  */
    /* JADX WARN: Code duplicated, block: B:120:0x03a4  */
    /* JADX WARN: Code duplicated, block: B:122:0x03a9  */
    /* JADX WARN: Code duplicated, block: B:125:0x03b7  */
    /* JADX WARN: Code duplicated, block: B:126:0x03ce  */
    /* JADX WARN: Code duplicated, block: B:129:0x03e0  */
    /* JADX WARN: Code duplicated, block: B:12:0x005d  */
    /* JADX WARN: Code duplicated, block: B:131:0x03f5  */
    /* JADX WARN: Code duplicated, block: B:133:0x03fa  */
    /* JADX WARN: Code duplicated, block: B:134:0x03fe  */
    /* JADX WARN: Code duplicated, block: B:141:0x0424  */
    /* JADX WARN: Code duplicated, block: B:143:0x0429  */
    /* JADX WARN: Code duplicated, block: B:146:0x043a  */
    /* JADX WARN: Code duplicated, block: B:148:0x043f  */
    /* JADX WARN: Code duplicated, block: B:14:0x0067  */
    /* JADX WARN: Code duplicated, block: B:151:0x044d  */
    /* JADX WARN: Code duplicated, block: B:152:0x0464  */
    /* JADX WARN: Code duplicated, block: B:155:0x0476  */
    /* JADX WARN: Code duplicated, block: B:157:0x048b  */
    /* JADX WARN: Code duplicated, block: B:159:0x0490  */
    /* JADX WARN: Code duplicated, block: B:160:0x0494  */
    /* JADX WARN: Code duplicated, block: B:162:0x049a  */
    /* JADX WARN: Code duplicated, block: B:168:0x04bd  */
    /* JADX WARN: Code duplicated, block: B:16:0x0077  */
    /* JADX WARN: Code duplicated, block: B:170:0x04c2  */
    /* JADX WARN: Code duplicated, block: B:173:0x04d3 A[LOOP:8: B:173:0x04d3->B:184:0x050f, LOOP_START, PHI: r21
      0x04d3: PHI (r21v11 'tmp' boolean) = (r21v0 'tmp' boolean), (r21v12 'tmp' boolean) binds: [B:91:0x0266, B:184:0x050f] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARN: Code duplicated, block: B:175:0x04dc  */
    /* JADX WARN: Code duplicated, block: B:177:0x04ef  */
    /* JADX WARN: Code duplicated, block: B:179:0x04f4  */
    /* JADX WARN: Code duplicated, block: B:183:0x050c  */
    /* JADX WARN: Code duplicated, block: B:185:0x051c A[LOOP:9: B:185:0x051c->B:196:0x0558, LOOP_START, PHI: r21
      0x051c: PHI (r21v9 'tmp' boolean) = (r21v0 'tmp' boolean), (r21v10 'tmp' boolean) binds: [B:91:0x0266, B:196:0x0558] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARN: Code duplicated, block: B:187:0x0525  */
    /* JADX WARN: Code duplicated, block: B:189:0x0538  */
    /* JADX WARN: Code duplicated, block: B:18:0x007e  */
    /* JADX WARN: Code duplicated, block: B:191:0x053d  */
    /* JADX WARN: Code duplicated, block: B:195:0x0555  */
    /* JADX WARN: Code duplicated, block: B:197:0x0565 A[LOOP:10: B:197:0x0565->B:208:0x059d, LOOP_START, PHI: r21
      0x0565: PHI (r21v7 'tmp' boolean) = (r21v0 'tmp' boolean), (r21v8 'tmp' boolean) binds: [B:91:0x0266, B:208:0x059d] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARN: Code duplicated, block: B:199:0x056e  */
    /* JADX WARN: Code duplicated, block: B:201:0x057d  */
    /* JADX WARN: Code duplicated, block: B:203:0x0582  */
    /* JADX WARN: Code duplicated, block: B:207:0x059a  */
    /* JADX WARN: Code duplicated, block: B:209:0x05aa A[LOOP:11: B:209:0x05aa->B:220:0x05e2, LOOP_START, PHI: r21
      0x05aa: PHI (r21v5 'tmp' boolean) = (r21v0 'tmp' boolean), (r21v6 'tmp' boolean) binds: [B:91:0x0266, B:220:0x05e2] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARN: Code duplicated, block: B:20:0x0094  */
    /* JADX WARN: Code duplicated, block: B:211:0x05b3  */
    /* JADX WARN: Code duplicated, block: B:213:0x05c2  */
    /* JADX WARN: Code duplicated, block: B:215:0x05c7  */
    /* JADX WARN: Code duplicated, block: B:219:0x05df  */
    /* JADX WARN: Code duplicated, block: B:221:0x05ef A[LOOP:12: B:221:0x05ef->B:232:0x0627, LOOP_START, PHI: r21
      0x05ef: PHI (r21v3 'tmp' boolean) = (r21v0 'tmp' boolean), (r21v4 'tmp' boolean) binds: [B:91:0x0266, B:232:0x0627] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARN: Code duplicated, block: B:223:0x05f8  */
    /* JADX WARN: Code duplicated, block: B:225:0x0607  */
    /* JADX WARN: Code duplicated, block: B:227:0x060c  */
    /* JADX WARN: Code duplicated, block: B:22:0x00a7  */
    /* JADX WARN: Code duplicated, block: B:231:0x0624  */
    /* JADX WARN: Code duplicated, block: B:233:0x0634 A[LOOP:13: B:233:0x0634->B:244:0x066c, LOOP_START, PHI: r21
      0x0634: PHI (r21v1 'tmp' boolean) = (r21v0 'tmp' boolean), (r21v2 'tmp' boolean) binds: [B:91:0x0266, B:244:0x066c] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARN: Code duplicated, block: B:235:0x063d  */
    /* JADX WARN: Code duplicated, block: B:237:0x064c  */
    /* JADX WARN: Code duplicated, block: B:239:0x0651  */
    /* JADX WARN: Code duplicated, block: B:243:0x0669  */
    /* JADX WARN: Code duplicated, block: B:246:0x067c  */
    /* JADX WARN: Code duplicated, block: B:248:0x0681  */
    /* JADX WARN: Code duplicated, block: B:253:0x069f  */
    /* JADX WARN: Code duplicated, block: B:258:0x015c A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:262:0x013e A[ADDED_TO_REGION, REMOVE, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:266:0x01b4 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:268:0x01e8 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:271:0x06af A[EDGE_INSN: B:271:0x06af->B:255:0x06af BREAK  A[LOOP:3: B:75:0x01f5->B:80:0x021f], SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:272:0x0219 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:282:0x040c A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:283:0x0412 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:284:0x0412 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:286:0x04a5 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:289:0x04ab A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:29:0x00d7  */
    /* JADX WARN: Code duplicated, block: B:30:0x00e2 A[PHI: r15
      0x00e2: PHI (r15v1 'minLength' int) = 
      (r15v0 'minLength' int)
      (r15v2 'minLength' int)
      (r15v0 'minLength' int)
      (r15v3 'minLength' int)
      (r15v0 'minLength' int)
      (r15v0 'minLength' int)
     binds: [B:3:0x0020, B:29:0x00d7, B:28:0x00c6, B:21:0x009a, B:7:0x0037, B:9:0x003e] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARN: Code duplicated, block: B:316:0x06af A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:317:? A[LOOP:14: B:250:0x068e->B:317:?, LOOP_END, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:32:0x00eb  */
    /* JADX WARN: Code duplicated, block: B:34:0x00f3  */
    /* JADX WARN: Code duplicated, block: B:37:0x0101  */
    /* JADX WARN: Code duplicated, block: B:39:0x010f  */
    /* JADX WARN: Code duplicated, block: B:41:0x0119  */
    /* JADX WARN: Code duplicated, block: B:43:0x011e  */
    /* JADX WARN: Code duplicated, block: B:46:0x0134  */
    /* JADX WARN: Code duplicated, block: B:49:0x0147  */
    /* JADX WARN: Code duplicated, block: B:56:0x0176  */
    /* JADX WARN: Code duplicated, block: B:58:0x017d  */
    /* JADX WARN: Code duplicated, block: B:60:0x018c  */
    /* JADX WARN: Code duplicated, block: B:63:0x019b  */
    /* JADX WARN: Code duplicated, block: B:65:0x01a9  */
    /* JADX WARN: Code duplicated, block: B:68:0x01ba  */
    /* JADX WARN: Code duplicated, block: B:71:0x01cd  */
    /* JADX WARN: Code duplicated, block: B:75:0x01f5 A[LOOP:3: B:75:0x01f5->B:80:0x021f, LOOP_START] */
    /* JADX WARN: Code duplicated, block: B:77:0x020e  */
    /* JADX WARN: Code duplicated, block: B:80:0x021f A[LOOP:3: B:75:0x01f5->B:80:0x021f, LOOP_END] */
    /* JADX WARN: Code duplicated, block: B:81:0x022c  */
    /* JADX WARN: Code duplicated, block: B:83:0x0237  */
    /* JADX WARN: Code duplicated, block: B:85:0x0240  */
    /* JADX WARN: Code duplicated, block: B:86:0x0244  */
    /* JADX WARN: Code duplicated, block: B:89:0x024c  */
    /* JADX WARN: Code duplicated, block: B:92:0x02e4  */
    /* JADX WARN: Code duplicated, block: B:95:0x02f4  */
    /* JADX WARN: Code duplicated, block: B:97:0x0307  */
    private boolean __interpret(Perl5Pattern expression, char[] input, int beginOffset, int endOff, int currentOffset) {
        int offset;
        int dontTry;
        int endOffset;
        int i;
        boolean z;
        boolean doEvery;
        int endOffset2;
        boolean tmp;
        char op;
        boolean z2;
        boolean z3;
        boolean z4;
        boolean z5;
        char ch;
        boolean z6;
        char ch2;
        boolean z7;
        boolean tmp2;
        char ch3;
        boolean z8;
        boolean tmp3;
        char ch4;
        boolean z9;
        int offset2;
        char ch5;
        boolean z10;
        char ch6;
        boolean z11;
        char[] mustString;
        int i__findFirst;
        char ch7;
        int endOffset3;
        char[] cArr;
        int i2;
        int minLength = 0;
        __initInterpreterGlobals(expression, input, beginOffset, endOff, currentOffset);
        boolean success = false;
        char[] mustString2 = expression._mustString;
        if (mustString2 == null) {
            if ((expression._anchor & 3) != 0) {
                if (this.__currentOffset != beginOffset && __tryExpression(beginOffset)) {
                    success = true;
                } else {
                    getClass();
                    if ((expression._anchor & 2) == 0 || (expression._anchor & 8) != 0) {
                        dontTry = minLength > 0 ? minLength - 1 : 0;
                        endOffset3 = endOff - dontTry;
                        if (this.__currentOffset > currentOffset) {
                            this.__currentOffset--;
                        }
                        while (this.__currentOffset < endOffset3) {
                            cArr = this.__input;
                            i2 = this.__currentOffset;
                            this.__currentOffset = i2 + 1;
                            if (cArr[i2] != '\n' && this.__currentOffset < endOffset3 && __tryExpression(this.__currentOffset)) {
                                success = true;
                                break;
                            }
                        }
                    }
                }
            } else if (expression._startString != null) {
                mustString = expression._startString;
                if ((expression._anchor & 4) != 0) {
                    ch7 = mustString[0];
                    while (this.__currentOffset < endOff) {
                        if (ch7 != this.__input[this.__currentOffset]) {
                            if (__tryExpression(this.__currentOffset)) {
                                success = true;
                                break;
                            }
                            this.__currentOffset++;
                            while (this.__currentOffset < endOff && this.__input[this.__currentOffset] == ch7) {
                                this.__currentOffset++;
                            }
                        }
                        this.__currentOffset++;
                    }
                } else {
                    while (true) {
                        i__findFirst = __findFirst(this.__input, this.__currentOffset, endOff, mustString);
                        this.__currentOffset = i__findFirst;
                        if (i__findFirst >= endOff) {
                            break;
                        }
                        if (__tryExpression(this.__currentOffset)) {
                            success = true;
                            break;
                        }
                        this.__currentOffset++;
                    }
                }
            } else {
                offset = expression._startClassOffset;
                if (offset != -1) {
                    if ((expression._anchor & 4) == 0) {
                        z = true;
                    } else {
                        z = false;
                    }
                    doEvery = z;
                    dontTry = minLength > 0 ? minLength - 1 : 0;
                    endOffset2 = endOff - dontTry;
                    tmp = true;
                    op = this.__program[offset];
                    switch (op) {
                        case '\t':
                            int offset3 = OpCode._getOperand(offset);
                            while (this.__currentOffset < endOffset2) {
                                ch6 = this.__input[this.__currentOffset];
                                if (ch6 >= 256 && (this.__program[offset3 + (ch6 >> 4)] & (1 << (ch6 & 15))) == 0) {
                                    if (tmp && __tryExpression(this.__currentOffset)) {
                                        success = true;
                                    } else {
                                        z11 = doEvery;
                                    }
                                } else {
                                    z11 = true;
                                }
                                tmp = z11;
                                this.__currentOffset++;
                                break;
                            }
                            break;
                        case 18:
                            while (this.__currentOffset < endOffset2) {
                                ch2 = this.__input[this.__currentOffset];
                                if (OpCode._isWordCharacter(ch2)) {
                                    if (!tmp && __tryExpression(this.__currentOffset)) {
                                        success = true;
                                    } else {
                                        z7 = doEvery;
                                    }
                                } else {
                                    z7 = true;
                                }
                                tmp = z7;
                                this.__currentOffset++;
                                break;
                            }
                            break;
                        case 19:
                            while (this.__currentOffset < endOffset2) {
                                ch = this.__input[this.__currentOffset];
                                if (!OpCode._isWordCharacter(ch)) {
                                    if (!tmp && __tryExpression(this.__currentOffset)) {
                                        success = true;
                                    } else {
                                        z6 = doEvery;
                                    }
                                } else {
                                    z6 = true;
                                }
                                tmp = z6;
                                this.__currentOffset++;
                                break;
                            }
                            break;
                        case 20:
                            if (minLength > 0) {
                                int i3 = dontTry + 1;
                                endOffset2--;
                            }
                            if (this.__currentOffset != beginOffset) {
                                char ch8 = this.__input[this.__currentOffset - 1];
                                tmp3 = OpCode._isWordCharacter(ch8);
                            } else {
                                tmp3 = OpCode._isWordCharacter(this.__previousChar);
                            }
                            while (this.__currentOffset < endOffset2) {
                                ch4 = this.__input[this.__currentOffset];
                                if (tmp3 == OpCode._isWordCharacter(ch4)) {
                                    if (tmp3) {
                                        z9 = false;
                                    } else {
                                        z9 = true;
                                    }
                                    tmp3 = z9;
                                    if (__tryExpression(this.__currentOffset)) {
                                        success = true;
                                        break;
                                    }
                                }
                                this.__currentOffset++;
                            }
                            if ((minLength <= 0 || tmp3) && __tryExpression(this.__currentOffset)) {
                            }
                            break;
                        case 21:
                            if (minLength > 0) {
                                int i4 = dontTry + 1;
                                endOffset2--;
                            }
                            if (this.__currentOffset != beginOffset) {
                                char ch9 = this.__input[this.__currentOffset - 1];
                                tmp2 = OpCode._isWordCharacter(ch9);
                            } else {
                                tmp2 = OpCode._isWordCharacter(this.__previousChar);
                            }
                            while (this.__currentOffset < endOffset2) {
                                ch3 = this.__input[this.__currentOffset];
                                if (tmp2 != OpCode._isWordCharacter(ch3)) {
                                    if (tmp2) {
                                        z8 = false;
                                    } else {
                                        z8 = true;
                                    }
                                    tmp2 = z8;
                                } else if (__tryExpression(this.__currentOffset)) {
                                    success = true;
                                    break;
                                }
                                this.__currentOffset++;
                            }
                            if ((minLength <= 0 || !tmp2) && __tryExpression(this.__currentOffset)) {
                            }
                            break;
                        case 22:
                            while (this.__currentOffset < endOffset2) {
                                if (Character.isWhitespace(this.__input[this.__currentOffset])) {
                                    if (!tmp && __tryExpression(this.__currentOffset)) {
                                        success = true;
                                    } else {
                                        z5 = doEvery;
                                    }
                                } else {
                                    z5 = true;
                                }
                                tmp = z5;
                                this.__currentOffset++;
                                break;
                            }
                            break;
                        case 23:
                            while (this.__currentOffset < endOffset2) {
                                if (!Character.isWhitespace(this.__input[this.__currentOffset])) {
                                    if (!tmp && __tryExpression(this.__currentOffset)) {
                                        success = true;
                                    } else {
                                        z4 = doEvery;
                                    }
                                } else {
                                    z4 = true;
                                }
                                tmp = z4;
                                this.__currentOffset++;
                                break;
                            }
                            break;
                        case 24:
                            while (this.__currentOffset < endOffset2) {
                                if (Character.isDigit(this.__input[this.__currentOffset])) {
                                    if (!tmp && __tryExpression(this.__currentOffset)) {
                                        success = true;
                                    } else {
                                        z3 = doEvery;
                                    }
                                } else {
                                    z3 = true;
                                }
                                tmp = z3;
                                this.__currentOffset++;
                                break;
                            }
                            break;
                        case 25:
                            while (this.__currentOffset < endOffset2) {
                                if (!Character.isDigit(this.__input[this.__currentOffset])) {
                                    if (!tmp && __tryExpression(this.__currentOffset)) {
                                        success = true;
                                    } else {
                                        z2 = doEvery;
                                    }
                                } else {
                                    z2 = true;
                                }
                                tmp = z2;
                                this.__currentOffset++;
                                break;
                            }
                            break;
                        case '#':
                        case '$':
                            offset2 = OpCode._getOperand(offset);
                            while (this.__currentOffset < endOffset2) {
                                ch5 = this.__input[this.__currentOffset];
                                if (__matchUnicodeClass(ch5, this.__program, offset2, op)) {
                                    if (!tmp && __tryExpression(this.__currentOffset)) {
                                        success = true;
                                    } else {
                                        z10 = doEvery;
                                    }
                                } else {
                                    z10 = true;
                                }
                                tmp = z10;
                                this.__currentOffset++;
                                break;
                            }
                            break;
                    }
                } else {
                    dontTry = minLength > 0 ? minLength - 1 : 0;
                    endOffset = endOff - dontTry;
                    while (!__tryExpression(this.__currentOffset)) {
                        i = this.__currentOffset;
                        this.__currentOffset = i + 1;
                        if (i >= endOffset) {
                        }
                    }
                    success = true;
                }
            }
        } else if ((expression._anchor & 3) != 0) {
            getClass();
            if ((expression._anchor & 2) != 0 && expression._back >= 0) {
                this.__currentOffset = __findFirst(this.__input, this.__currentOffset, endOff, mustString2);
                if (this.__currentOffset >= endOff) {
                    if ((expression._options & 32768) == 0) {
                        expression._mustUtility++;
                    }
                    success = false;
                } else {
                    if (expression._back >= 0) {
                        this.__currentOffset -= expression._back;
                        if (this.__currentOffset < currentOffset) {
                            this.__currentOffset = currentOffset;
                        }
                        minLength = expression._back + mustString2.length;
                    } else if (expression._isExpensive && (expression._options & 32768) == 0) {
                        int i5 = expression._mustUtility - 1;
                        expression._mustUtility = i5;
                        if (i5 < 0) {
                            expression._mustString = null;
                            this.__currentOffset = currentOffset;
                        } else {
                            this.__currentOffset = currentOffset;
                            minLength = mustString2.length;
                        }
                    } else {
                        this.__currentOffset = currentOffset;
                        minLength = mustString2.length;
                    }
                    if ((expression._anchor & 3) != 0) {
                        if (this.__currentOffset != beginOffset) {
                            getClass();
                            if ((expression._anchor & 2) == 0) {
                                if (minLength > 0) {
                                }
                                endOffset3 = endOff - dontTry;
                                if (this.__currentOffset > currentOffset) {
                                    this.__currentOffset--;
                                }
                                while (this.__currentOffset < endOffset3) {
                                    cArr = this.__input;
                                    i2 = this.__currentOffset;
                                    this.__currentOffset = i2 + 1;
                                    if (cArr[i2] != '\n') {
                                    }
                                }
                            } else {
                                if (minLength > 0) {
                                }
                                endOffset3 = endOff - dontTry;
                                if (this.__currentOffset > currentOffset) {
                                    this.__currentOffset--;
                                }
                                while (this.__currentOffset < endOffset3) {
                                    cArr = this.__input;
                                    i2 = this.__currentOffset;
                                    this.__currentOffset = i2 + 1;
                                    if (cArr[i2] != '\n') {
                                    }
                                }
                            }
                        } else {
                            getClass();
                            if ((expression._anchor & 2) == 0) {
                                if (minLength > 0) {
                                }
                                endOffset3 = endOff - dontTry;
                                if (this.__currentOffset > currentOffset) {
                                    this.__currentOffset--;
                                }
                                while (this.__currentOffset < endOffset3) {
                                    cArr = this.__input;
                                    i2 = this.__currentOffset;
                                    this.__currentOffset = i2 + 1;
                                    if (cArr[i2] != '\n') {
                                    }
                                }
                            } else {
                                if (minLength > 0) {
                                }
                                endOffset3 = endOff - dontTry;
                                if (this.__currentOffset > currentOffset) {
                                    this.__currentOffset--;
                                }
                                while (this.__currentOffset < endOffset3) {
                                    cArr = this.__input;
                                    i2 = this.__currentOffset;
                                    this.__currentOffset = i2 + 1;
                                    if (cArr[i2] != '\n') {
                                    }
                                }
                            }
                        }
                    } else if (expression._startString != null) {
                        mustString = expression._startString;
                        if ((expression._anchor & 4) != 0) {
                            ch7 = mustString[0];
                            while (this.__currentOffset < endOff) {
                                if (ch7 != this.__input[this.__currentOffset]) {
                                    if (__tryExpression(this.__currentOffset)) {
                                        success = true;
                                        break;
                                    }
                                    this.__currentOffset++;
                                    while (this.__currentOffset < endOff) {
                                        this.__currentOffset++;
                                    }
                                }
                                this.__currentOffset++;
                            }
                        } else {
                            while (true) {
                                i__findFirst = __findFirst(this.__input, this.__currentOffset, endOff, mustString);
                                this.__currentOffset = i__findFirst;
                                if (i__findFirst >= endOff) {
                                    break;
                                    break;
                                }
                                if (__tryExpression(this.__currentOffset)) {
                                    success = true;
                                    break;
                                }
                                this.__currentOffset++;
                            }
                        }
                    } else {
                        offset = expression._startClassOffset;
                        if (offset != -1) {
                            if ((expression._anchor & 4) == 0) {
                                z = true;
                            } else {
                                z = false;
                            }
                            doEvery = z;
                            if (minLength > 0) {
                            }
                            endOffset2 = endOff - dontTry;
                            tmp = true;
                            op = this.__program[offset];
                            switch (op) {
                                case '\t':
                                    int offset4 = OpCode._getOperand(offset);
                                    while (this.__currentOffset < endOffset2) {
                                        ch6 = this.__input[this.__currentOffset];
                                        if (ch6 >= 256) {
                                            z11 = true;
                                        } else {
                                            z11 = true;
                                        }
                                        tmp = z11;
                                        this.__currentOffset++;
                                    }
                                    break;
                                case 18:
                                    while (this.__currentOffset < endOffset2) {
                                        ch2 = this.__input[this.__currentOffset];
                                        if (OpCode._isWordCharacter(ch2)) {
                                            if (!tmp) {
                                            }
                                            z7 = doEvery;
                                        } else {
                                            z7 = true;
                                        }
                                        tmp = z7;
                                        this.__currentOffset++;
                                        break;
                                    }
                                    break;
                                case 19:
                                    while (this.__currentOffset < endOffset2) {
                                        ch = this.__input[this.__currentOffset];
                                        if (!OpCode._isWordCharacter(ch)) {
                                            if (!tmp) {
                                            }
                                            z6 = doEvery;
                                        } else {
                                            z6 = true;
                                        }
                                        tmp = z6;
                                        this.__currentOffset++;
                                        break;
                                    }
                                    break;
                                case 20:
                                    if (minLength > 0) {
                                        int i6 = dontTry + 1;
                                        endOffset2--;
                                    }
                                    if (this.__currentOffset != beginOffset) {
                                        char ch10 = this.__input[this.__currentOffset - 1];
                                        tmp3 = OpCode._isWordCharacter(ch10);
                                    } else {
                                        tmp3 = OpCode._isWordCharacter(this.__previousChar);
                                    }
                                    while (this.__currentOffset < endOffset2) {
                                        ch4 = this.__input[this.__currentOffset];
                                        if (tmp3 == OpCode._isWordCharacter(ch4)) {
                                            if (tmp3) {
                                                z9 = true;
                                            } else {
                                                z9 = false;
                                            }
                                            tmp3 = z9;
                                            if (__tryExpression(this.__currentOffset)) {
                                                success = true;
                                                break;
                                            }
                                        }
                                        this.__currentOffset++;
                                    }
                                    success = minLength <= 0 ? true : true;
                                    break;
                                case 21:
                                    if (minLength > 0) {
                                        int i7 = dontTry + 1;
                                        endOffset2--;
                                    }
                                    if (this.__currentOffset != beginOffset) {
                                        char ch11 = this.__input[this.__currentOffset - 1];
                                        tmp2 = OpCode._isWordCharacter(ch11);
                                    } else {
                                        tmp2 = OpCode._isWordCharacter(this.__previousChar);
                                    }
                                    while (this.__currentOffset < endOffset2) {
                                        ch3 = this.__input[this.__currentOffset];
                                        if (tmp2 != OpCode._isWordCharacter(ch3)) {
                                            if (tmp2) {
                                                z8 = true;
                                            } else {
                                                z8 = false;
                                            }
                                            tmp2 = z8;
                                        } else if (__tryExpression(this.__currentOffset)) {
                                            success = true;
                                            break;
                                        }
                                        this.__currentOffset++;
                                    }
                                    success = minLength <= 0 ? true : true;
                                    break;
                                case 22:
                                    while (this.__currentOffset < endOffset2) {
                                        if (Character.isWhitespace(this.__input[this.__currentOffset])) {
                                            if (!tmp) {
                                            }
                                            z5 = doEvery;
                                        } else {
                                            z5 = true;
                                        }
                                        tmp = z5;
                                        this.__currentOffset++;
                                        break;
                                    }
                                    break;
                                case 23:
                                    while (this.__currentOffset < endOffset2) {
                                        if (!Character.isWhitespace(this.__input[this.__currentOffset])) {
                                            if (!tmp) {
                                            }
                                            z4 = doEvery;
                                        } else {
                                            z4 = true;
                                        }
                                        tmp = z4;
                                        this.__currentOffset++;
                                        break;
                                    }
                                    break;
                                case 24:
                                    while (this.__currentOffset < endOffset2) {
                                        if (Character.isDigit(this.__input[this.__currentOffset])) {
                                            if (!tmp) {
                                            }
                                            z3 = doEvery;
                                        } else {
                                            z3 = true;
                                        }
                                        tmp = z3;
                                        this.__currentOffset++;
                                        break;
                                    }
                                    break;
                                case 25:
                                    while (this.__currentOffset < endOffset2) {
                                        if (!Character.isDigit(this.__input[this.__currentOffset])) {
                                            if (!tmp) {
                                            }
                                            z2 = doEvery;
                                        } else {
                                            z2 = true;
                                        }
                                        tmp = z2;
                                        this.__currentOffset++;
                                        break;
                                    }
                                    break;
                                case '#':
                                case '$':
                                    offset2 = OpCode._getOperand(offset);
                                    while (this.__currentOffset < endOffset2) {
                                        ch5 = this.__input[this.__currentOffset];
                                        if (__matchUnicodeClass(ch5, this.__program, offset2, op)) {
                                            if (!tmp) {
                                            }
                                            z10 = doEvery;
                                        } else {
                                            z10 = true;
                                        }
                                        tmp = z10;
                                        this.__currentOffset++;
                                        break;
                                    }
                                    break;
                            }
                        } else {
                            if (minLength > 0) {
                            }
                            endOffset = endOff - dontTry;
                            while (!__tryExpression(this.__currentOffset)) {
                                i = this.__currentOffset;
                                this.__currentOffset = i + 1;
                                if (i >= endOffset) {
                                }
                            }
                            success = true;
                        }
                    }
                }
            } else if ((expression._anchor & 3) != 0) {
                if (this.__currentOffset != beginOffset) {
                    getClass();
                    if ((expression._anchor & 2) == 0) {
                        if (minLength > 0) {
                        }
                        endOffset3 = endOff - dontTry;
                        if (this.__currentOffset > currentOffset) {
                            this.__currentOffset--;
                        }
                        while (this.__currentOffset < endOffset3) {
                            cArr = this.__input;
                            i2 = this.__currentOffset;
                            this.__currentOffset = i2 + 1;
                            if (cArr[i2] != '\n') {
                            }
                        }
                    } else {
                        if (minLength > 0) {
                        }
                        endOffset3 = endOff - dontTry;
                        if (this.__currentOffset > currentOffset) {
                            this.__currentOffset--;
                        }
                        while (this.__currentOffset < endOffset3) {
                            cArr = this.__input;
                            i2 = this.__currentOffset;
                            this.__currentOffset = i2 + 1;
                            if (cArr[i2] != '\n') {
                            }
                        }
                    }
                } else {
                    getClass();
                    if ((expression._anchor & 2) == 0) {
                        if (minLength > 0) {
                        }
                        endOffset3 = endOff - dontTry;
                        if (this.__currentOffset > currentOffset) {
                            this.__currentOffset--;
                        }
                        while (this.__currentOffset < endOffset3) {
                            cArr = this.__input;
                            i2 = this.__currentOffset;
                            this.__currentOffset = i2 + 1;
                            if (cArr[i2] != '\n') {
                            }
                        }
                    } else {
                        if (minLength > 0) {
                        }
                        endOffset3 = endOff - dontTry;
                        if (this.__currentOffset > currentOffset) {
                            this.__currentOffset--;
                        }
                        while (this.__currentOffset < endOffset3) {
                            cArr = this.__input;
                            i2 = this.__currentOffset;
                            this.__currentOffset = i2 + 1;
                            if (cArr[i2] != '\n') {
                            }
                        }
                    }
                }
            } else if (expression._startString != null) {
                mustString = expression._startString;
                if ((expression._anchor & 4) != 0) {
                    ch7 = mustString[0];
                    while (this.__currentOffset < endOff) {
                        if (ch7 != this.__input[this.__currentOffset]) {
                            if (__tryExpression(this.__currentOffset)) {
                                success = true;
                                break;
                            }
                            this.__currentOffset++;
                            while (this.__currentOffset < endOff) {
                                this.__currentOffset++;
                            }
                        }
                        this.__currentOffset++;
                    }
                } else {
                    while (true) {
                        i__findFirst = __findFirst(this.__input, this.__currentOffset, endOff, mustString);
                        this.__currentOffset = i__findFirst;
                        if (i__findFirst >= endOff) {
                            break;
                            break;
                        }
                        if (__tryExpression(this.__currentOffset)) {
                            success = true;
                            break;
                        }
                        this.__currentOffset++;
                    }
                }
            } else {
                offset = expression._startClassOffset;
                if (offset != -1) {
                    if ((expression._anchor & 4) == 0) {
                        z = true;
                    } else {
                        z = false;
                    }
                    doEvery = z;
                    if (minLength > 0) {
                    }
                    endOffset2 = endOff - dontTry;
                    tmp = true;
                    op = this.__program[offset];
                    switch (op) {
                        case '\t':
                            int offset5 = OpCode._getOperand(offset);
                            while (this.__currentOffset < endOffset2) {
                                ch6 = this.__input[this.__currentOffset];
                                if (ch6 >= 256) {
                                    z11 = true;
                                } else {
                                    z11 = true;
                                }
                                tmp = z11;
                                this.__currentOffset++;
                            }
                            break;
                        case 18:
                            while (this.__currentOffset < endOffset2) {
                                ch2 = this.__input[this.__currentOffset];
                                if (OpCode._isWordCharacter(ch2)) {
                                    if (!tmp) {
                                    }
                                    z7 = doEvery;
                                } else {
                                    z7 = true;
                                }
                                tmp = z7;
                                this.__currentOffset++;
                                break;
                            }
                            break;
                        case 19:
                            while (this.__currentOffset < endOffset2) {
                                ch = this.__input[this.__currentOffset];
                                if (!OpCode._isWordCharacter(ch)) {
                                    if (!tmp) {
                                    }
                                    z6 = doEvery;
                                } else {
                                    z6 = true;
                                }
                                tmp = z6;
                                this.__currentOffset++;
                                break;
                            }
                            break;
                        case 20:
                            if (minLength > 0) {
                                int i8 = dontTry + 1;
                                endOffset2--;
                            }
                            if (this.__currentOffset != beginOffset) {
                                char ch12 = this.__input[this.__currentOffset - 1];
                                tmp3 = OpCode._isWordCharacter(ch12);
                            } else {
                                tmp3 = OpCode._isWordCharacter(this.__previousChar);
                            }
                            while (this.__currentOffset < endOffset2) {
                                ch4 = this.__input[this.__currentOffset];
                                if (tmp3 == OpCode._isWordCharacter(ch4)) {
                                    if (tmp3) {
                                        z9 = true;
                                    } else {
                                        z9 = false;
                                    }
                                    tmp3 = z9;
                                    if (__tryExpression(this.__currentOffset)) {
                                        success = true;
                                        break;
                                    }
                                }
                                this.__currentOffset++;
                            }
                            if (minLength <= 0) {
                            }
                            break;
                        case 21:
                            if (minLength > 0) {
                                int i9 = dontTry + 1;
                                endOffset2--;
                            }
                            if (this.__currentOffset != beginOffset) {
                                char ch13 = this.__input[this.__currentOffset - 1];
                                tmp2 = OpCode._isWordCharacter(ch13);
                            } else {
                                tmp2 = OpCode._isWordCharacter(this.__previousChar);
                            }
                            while (this.__currentOffset < endOffset2) {
                                ch3 = this.__input[this.__currentOffset];
                                if (tmp2 != OpCode._isWordCharacter(ch3)) {
                                    if (tmp2) {
                                        z8 = true;
                                    } else {
                                        z8 = false;
                                    }
                                    tmp2 = z8;
                                } else if (__tryExpression(this.__currentOffset)) {
                                    success = true;
                                    break;
                                }
                                this.__currentOffset++;
                            }
                            if (minLength <= 0) {
                            }
                            break;
                        case 22:
                            while (this.__currentOffset < endOffset2) {
                                if (Character.isWhitespace(this.__input[this.__currentOffset])) {
                                    if (!tmp) {
                                    }
                                    z5 = doEvery;
                                } else {
                                    z5 = true;
                                }
                                tmp = z5;
                                this.__currentOffset++;
                                break;
                            }
                            break;
                        case 23:
                            while (this.__currentOffset < endOffset2) {
                                if (!Character.isWhitespace(this.__input[this.__currentOffset])) {
                                    if (!tmp) {
                                    }
                                    z4 = doEvery;
                                } else {
                                    z4 = true;
                                }
                                tmp = z4;
                                this.__currentOffset++;
                                break;
                            }
                            break;
                        case 24:
                            while (this.__currentOffset < endOffset2) {
                                if (Character.isDigit(this.__input[this.__currentOffset])) {
                                    if (!tmp) {
                                    }
                                    z3 = doEvery;
                                } else {
                                    z3 = true;
                                }
                                tmp = z3;
                                this.__currentOffset++;
                                break;
                            }
                            break;
                        case 25:
                            while (this.__currentOffset < endOffset2) {
                                if (!Character.isDigit(this.__input[this.__currentOffset])) {
                                    if (!tmp) {
                                    }
                                    z2 = doEvery;
                                } else {
                                    z2 = true;
                                }
                                tmp = z2;
                                this.__currentOffset++;
                                break;
                            }
                            break;
                        case '#':
                        case '$':
                            offset2 = OpCode._getOperand(offset);
                            while (this.__currentOffset < endOffset2) {
                                ch5 = this.__input[this.__currentOffset];
                                if (__matchUnicodeClass(ch5, this.__program, offset2, op)) {
                                    if (!tmp) {
                                    }
                                    z10 = doEvery;
                                } else {
                                    z10 = true;
                                }
                                tmp = z10;
                                this.__currentOffset++;
                                break;
                            }
                            break;
                    }
                } else {
                    if (minLength > 0) {
                    }
                    endOffset = endOff - dontTry;
                    while (!__tryExpression(this.__currentOffset)) {
                        i = this.__currentOffset;
                        this.__currentOffset = i + 1;
                        if (i >= endOffset) {
                        }
                    }
                    success = true;
                }
            }
        } else {
            this.__currentOffset = __findFirst(this.__input, this.__currentOffset, endOff, mustString2);
            if (this.__currentOffset >= endOff) {
                if ((expression._options & 32768) == 0) {
                    expression._mustUtility++;
                }
                success = false;
            } else {
                if (expression._back >= 0) {
                    this.__currentOffset -= expression._back;
                    if (this.__currentOffset < currentOffset) {
                        this.__currentOffset = currentOffset;
                    }
                    minLength = expression._back + mustString2.length;
                } else if (expression._isExpensive) {
                    this.__currentOffset = currentOffset;
                    minLength = mustString2.length;
                } else {
                    this.__currentOffset = currentOffset;
                    minLength = mustString2.length;
                }
                if ((expression._anchor & 3) != 0) {
                    if (this.__currentOffset != beginOffset) {
                        getClass();
                        if ((expression._anchor & 2) == 0) {
                            if (minLength > 0) {
                            }
                            endOffset3 = endOff - dontTry;
                            if (this.__currentOffset > currentOffset) {
                                this.__currentOffset--;
                            }
                            while (this.__currentOffset < endOffset3) {
                                cArr = this.__input;
                                i2 = this.__currentOffset;
                                this.__currentOffset = i2 + 1;
                                if (cArr[i2] != '\n') {
                                }
                            }
                        } else {
                            if (minLength > 0) {
                            }
                            endOffset3 = endOff - dontTry;
                            if (this.__currentOffset > currentOffset) {
                                this.__currentOffset--;
                            }
                            while (this.__currentOffset < endOffset3) {
                                cArr = this.__input;
                                i2 = this.__currentOffset;
                                this.__currentOffset = i2 + 1;
                                if (cArr[i2] != '\n') {
                                }
                            }
                        }
                    } else {
                        getClass();
                        if ((expression._anchor & 2) == 0) {
                            if (minLength > 0) {
                            }
                            endOffset3 = endOff - dontTry;
                            if (this.__currentOffset > currentOffset) {
                                this.__currentOffset--;
                            }
                            while (this.__currentOffset < endOffset3) {
                                cArr = this.__input;
                                i2 = this.__currentOffset;
                                this.__currentOffset = i2 + 1;
                                if (cArr[i2] != '\n') {
                                }
                            }
                        } else {
                            if (minLength > 0) {
                            }
                            endOffset3 = endOff - dontTry;
                            if (this.__currentOffset > currentOffset) {
                                this.__currentOffset--;
                            }
                            while (this.__currentOffset < endOffset3) {
                                cArr = this.__input;
                                i2 = this.__currentOffset;
                                this.__currentOffset = i2 + 1;
                                if (cArr[i2] != '\n') {
                                }
                            }
                        }
                    }
                } else if (expression._startString != null) {
                    mustString = expression._startString;
                    if ((expression._anchor & 4) != 0) {
                        ch7 = mustString[0];
                        while (this.__currentOffset < endOff) {
                            if (ch7 != this.__input[this.__currentOffset]) {
                                if (__tryExpression(this.__currentOffset)) {
                                    success = true;
                                    break;
                                }
                                this.__currentOffset++;
                                while (this.__currentOffset < endOff) {
                                    this.__currentOffset++;
                                }
                            }
                            this.__currentOffset++;
                        }
                    } else {
                        while (true) {
                            i__findFirst = __findFirst(this.__input, this.__currentOffset, endOff, mustString);
                            this.__currentOffset = i__findFirst;
                            if (i__findFirst >= endOff) {
                                break;
                                break;
                            }
                            if (__tryExpression(this.__currentOffset)) {
                                success = true;
                                break;
                            }
                            this.__currentOffset++;
                        }
                    }
                } else {
                    offset = expression._startClassOffset;
                    if (offset != -1) {
                        if ((expression._anchor & 4) == 0) {
                            z = true;
                        } else {
                            z = false;
                        }
                        doEvery = z;
                        if (minLength > 0) {
                        }
                        endOffset2 = endOff - dontTry;
                        tmp = true;
                        op = this.__program[offset];
                        switch (op) {
                            case '\t':
                                int offset6 = OpCode._getOperand(offset);
                                while (this.__currentOffset < endOffset2) {
                                    ch6 = this.__input[this.__currentOffset];
                                    if (ch6 >= 256) {
                                        z11 = true;
                                    } else {
                                        z11 = true;
                                    }
                                    tmp = z11;
                                    this.__currentOffset++;
                                }
                                break;
                            case 18:
                                while (this.__currentOffset < endOffset2) {
                                    ch2 = this.__input[this.__currentOffset];
                                    if (OpCode._isWordCharacter(ch2)) {
                                        if (!tmp) {
                                        }
                                        z7 = doEvery;
                                    } else {
                                        z7 = true;
                                    }
                                    tmp = z7;
                                    this.__currentOffset++;
                                    break;
                                }
                                break;
                            case 19:
                                while (this.__currentOffset < endOffset2) {
                                    ch = this.__input[this.__currentOffset];
                                    if (!OpCode._isWordCharacter(ch)) {
                                        if (!tmp) {
                                        }
                                        z6 = doEvery;
                                    } else {
                                        z6 = true;
                                    }
                                    tmp = z6;
                                    this.__currentOffset++;
                                    break;
                                }
                                break;
                            case 20:
                                if (minLength > 0) {
                                    int i10 = dontTry + 1;
                                    endOffset2--;
                                }
                                if (this.__currentOffset != beginOffset) {
                                    char ch14 = this.__input[this.__currentOffset - 1];
                                    tmp3 = OpCode._isWordCharacter(ch14);
                                } else {
                                    tmp3 = OpCode._isWordCharacter(this.__previousChar);
                                }
                                while (this.__currentOffset < endOffset2) {
                                    ch4 = this.__input[this.__currentOffset];
                                    if (tmp3 == OpCode._isWordCharacter(ch4)) {
                                        if (tmp3) {
                                            z9 = true;
                                        } else {
                                            z9 = false;
                                        }
                                        tmp3 = z9;
                                        if (__tryExpression(this.__currentOffset)) {
                                            success = true;
                                            break;
                                        }
                                    }
                                    this.__currentOffset++;
                                }
                                if (minLength <= 0) {
                                }
                                break;
                            case 21:
                                if (minLength > 0) {
                                    int i11 = dontTry + 1;
                                    endOffset2--;
                                }
                                if (this.__currentOffset != beginOffset) {
                                    char ch15 = this.__input[this.__currentOffset - 1];
                                    tmp2 = OpCode._isWordCharacter(ch15);
                                } else {
                                    tmp2 = OpCode._isWordCharacter(this.__previousChar);
                                }
                                while (this.__currentOffset < endOffset2) {
                                    ch3 = this.__input[this.__currentOffset];
                                    if (tmp2 != OpCode._isWordCharacter(ch3)) {
                                        if (tmp2) {
                                            z8 = true;
                                        } else {
                                            z8 = false;
                                        }
                                        tmp2 = z8;
                                    } else if (__tryExpression(this.__currentOffset)) {
                                        success = true;
                                        break;
                                    }
                                    this.__currentOffset++;
                                }
                                if (minLength <= 0) {
                                }
                                break;
                            case 22:
                                while (this.__currentOffset < endOffset2) {
                                    if (Character.isWhitespace(this.__input[this.__currentOffset])) {
                                        if (!tmp) {
                                        }
                                        z5 = doEvery;
                                    } else {
                                        z5 = true;
                                    }
                                    tmp = z5;
                                    this.__currentOffset++;
                                    break;
                                }
                                break;
                            case 23:
                                while (this.__currentOffset < endOffset2) {
                                    if (!Character.isWhitespace(this.__input[this.__currentOffset])) {
                                        if (!tmp) {
                                        }
                                        z4 = doEvery;
                                    } else {
                                        z4 = true;
                                    }
                                    tmp = z4;
                                    this.__currentOffset++;
                                    break;
                                }
                                break;
                            case 24:
                                while (this.__currentOffset < endOffset2) {
                                    if (Character.isDigit(this.__input[this.__currentOffset])) {
                                        if (!tmp) {
                                        }
                                        z3 = doEvery;
                                    } else {
                                        z3 = true;
                                    }
                                    tmp = z3;
                                    this.__currentOffset++;
                                    break;
                                }
                                break;
                            case 25:
                                while (this.__currentOffset < endOffset2) {
                                    if (!Character.isDigit(this.__input[this.__currentOffset])) {
                                        if (!tmp) {
                                        }
                                        z2 = doEvery;
                                    } else {
                                        z2 = true;
                                    }
                                    tmp = z2;
                                    this.__currentOffset++;
                                    break;
                                }
                                break;
                            case '#':
                            case '$':
                                offset2 = OpCode._getOperand(offset);
                                while (this.__currentOffset < endOffset2) {
                                    ch5 = this.__input[this.__currentOffset];
                                    if (__matchUnicodeClass(ch5, this.__program, offset2, op)) {
                                        if (!tmp) {
                                        }
                                        z10 = doEvery;
                                    } else {
                                        z10 = true;
                                    }
                                    tmp = z10;
                                    this.__currentOffset++;
                                    break;
                                }
                                break;
                        }
                    } else {
                        if (minLength > 0) {
                        }
                        endOffset = endOff - dontTry;
                        while (!__tryExpression(this.__currentOffset)) {
                            i = this.__currentOffset;
                            this.__currentOffset = i + 1;
                            if (i >= endOffset) {
                            }
                        }
                        success = true;
                    }
                }
            }
        }
        this.__lastSuccess = success;
        this.__lastMatchResult = null;
        return success;
    }

    /* JADX WARN: Code duplicated, block: B:176:0x01bf A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:177:0x01f4 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:197:0x0011 A[DONT_GENERATE, SYNTHETIC] */
    private boolean __matchUnicodeClass(char code, char[] __program1, int off, char opcode) {
        int offset = off;
        boolean isANYOF = opcode == '#';
        while (__program1[offset] != 0) {
            if (__program1[offset] == '%') {
                int offset2 = offset + 1;
                if (code >= __program1[offset2] && code <= __program1[offset2 + 1]) {
                    return isANYOF;
                }
                offset = offset2 + 2;
            } else if (__program1[offset] == '1') {
                int offset3 = offset + 1;
                offset = offset3 + 1;
                if (__program1[offset3] == code) {
                    return isANYOF;
                }
            } else {
                isANYOF = __program1[offset] == '/' ? isANYOF : !isANYOF;
                int offset4 = offset + 1;
                offset = offset4 + 1;
                switch (__program1[offset4]) {
                    case 18:
                        if (OpCode._isWordCharacter(code)) {
                            return isANYOF;
                        }
                        break;
                    case 19:
                        if (!OpCode._isWordCharacter(code)) {
                            return isANYOF;
                        }
                        break;
                        break;
                    case 20:
                    case 21:
                    case 26:
                    case 27:
                    case 28:
                    case 29:
                    case 30:
                    case 31:
                    case ' ':
                    case '!':
                    case '\"':
                    case '#':
                    case '$':
                    case '%':
                    case '/':
                    case '0':
                    case '1':
                    default:
                        return !isANYOF;
                    case 22:
                        if (Character.isWhitespace(code)) {
                            return isANYOF;
                        }
                        break;
                    case 23:
                        if (!Character.isWhitespace(code)) {
                            return isANYOF;
                        }
                        break;
                        break;
                    case 24:
                        if (Character.isDigit(code)) {
                            return isANYOF;
                        }
                        break;
                    case 25:
                        if (!Character.isDigit(code)) {
                            return isANYOF;
                        }
                        break;
                        break;
                    case '&':
                        if (Character.isLetter(code)) {
                            return isANYOF;
                        }
                        break;
                    case '\'':
                        if (Character.isSpaceChar(code)) {
                            return isANYOF;
                        }
                        break;
                    case '(':
                        if (Character.isISOControl(code)) {
                            return isANYOF;
                        }
                        break;
                    case ')':
                        if (Character.isLetterOrDigit(code)) {
                            return isANYOF;
                        }
                        switch (Character.getType(code)) {
                            case 20:
                            case 21:
                            case 22:
                            case 23:
                            case 24:
                            case 25:
                            case 26:
                            case 27:
                                return isANYOF;
                        }
                    case '*':
                        if (Character.isLowerCase(code)) {
                            return isANYOF;
                        }
                        if (this.__caseInsensitive && Character.isUpperCase(code)) {
                            return isANYOF;
                        }
                        break;
                        break;
                    case '+':
                        if (Character.isSpaceChar(code)) {
                            return isANYOF;
                        }
                        if (Character.isLetterOrDigit(code)) {
                            return isANYOF;
                        }
                        switch (Character.getType(code)) {
                            case 20:
                            case 21:
                            case 22:
                            case 23:
                            case 24:
                            case 25:
                            case 26:
                            case 27:
                                return isANYOF;
                        }
                    case ',':
                        switch (Character.getType(code)) {
                            case 20:
                            case 21:
                            case 22:
                            case 23:
                            case 24:
                            case 25:
                            case 26:
                            case 27:
                                return isANYOF;
                        }
                    case '-':
                        if (Character.isUpperCase(code)) {
                            return isANYOF;
                        }
                        if (this.__caseInsensitive && Character.isLowerCase(code)) {
                            return isANYOF;
                        }
                        break;
                        break;
                    case '.':
                        if ((code >= '0' && code <= '9') || ((code >= 'a' && code <= 'f') || (code >= 'A' && code <= 'F'))) {
                            return isANYOF;
                        }
                        break;
                        break;
                    case '2':
                        if (Character.isLetterOrDigit(code)) {
                            return isANYOF;
                        }
                        break;
                    case '3':
                        if (code < 128) {
                            return isANYOF;
                        }
                        break;
                }
            }
        }
        return !isANYOF;
    }

    private boolean __tryExpression(int offset) {
        this.__inputOffset = offset;
        this.__lastParen = 0;
        this.__expSize = 0;
        if (this.__numParentheses > 0) {
            for (int count = 0; count <= this.__numParentheses; count++) {
                this.__beginMatchOffsets[count] = -1;
                this.__endMatchOffsets[count] = -1;
            }
        }
        if (__match(1)) {
            this.__beginMatchOffsets[0] = offset;
            this.__endMatchOffsets[0] = this.__inputOffset;
            return true;
        }
        return false;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    private int __repeat(int offset, int max) {
        int scan = this.__inputOffset;
        int eol = this.__eol;
        if (max != 65535 && max < eol - scan) {
            eol = scan + max;
        }
        int operand = OpCode._getOperand(offset);
        char op = this.__program[offset];
        switch (op) {
            case 7:
                while (scan < eol && this.__input[scan] != '\n') {
                    scan++;
                }
                break;
            case '\b':
                scan = eol;
                break;
            case '\t':
                if (scan < eol) {
                    char c = this.__input[scan];
                    char ch = c;
                    if (c < 256) {
                        while (ch < 256 && (this.__program[operand + (ch >> 4)] & (1 << (ch & 15))) == 0) {
                            scan++;
                            if (scan < eol) {
                                ch = this.__input[scan];
                            }
                        }
                    }
                }
                break;
            case 14:
                int operand2 = operand + 1;
                while (scan < eol && this.__program[operand2] == this.__input[scan]) {
                    scan++;
                }
                break;
            case 18:
                while (scan < eol && OpCode._isWordCharacter(this.__input[scan])) {
                    scan++;
                }
                break;
            case 19:
                while (scan < eol && !OpCode._isWordCharacter(this.__input[scan])) {
                    scan++;
                }
                break;
            case 22:
                while (scan < eol && Character.isWhitespace(this.__input[scan])) {
                    scan++;
                }
                break;
            case 23:
                while (scan < eol && !Character.isWhitespace(this.__input[scan])) {
                    scan++;
                }
                break;
            case 24:
                while (scan < eol && Character.isDigit(this.__input[scan])) {
                    scan++;
                }
                break;
            case 25:
                while (scan < eol && !Character.isDigit(this.__input[scan])) {
                    scan++;
                }
                break;
            case '#':
            case '$':
                if (scan < eol) {
                    char c2 = this.__input[scan];
                    while (__matchUnicodeClass(ch, this.__program, operand, op)) {
                        scan++;
                        if (scan < eol) {
                            c2 = this.__input[scan];
                        }
                    }
                }
                break;
        }
        int ret = scan - this.__inputOffset;
        this.__inputOffset = scan;
        return ret;
    }

    private boolean __match(int offset) {
        int line;
        int arg;
        int scan;
        char nextChar;
        int current;
        boolean a;
        boolean minMod = false;
        int input = this.__inputOffset;
        boolean inputRemains = input < this.__endOffset;
        char nextChar2 = inputRemains ? this.__input[input] : (char) 65535;
        int scan2 = offset;
        int maxScan = this.__program.length;
        while (scan2 < maxScan) {
            int next = OpCode._getNext(this.__program, scan2);
            char op = this.__program[scan2];
            switch (op) {
                case 0:
                case '!':
                    this.__inputOffset = input;
                    if (this.__inputOffset == this.__lastMatchInputEndOffset) {
                        return false;
                    }
                    return true;
                case 1:
                    if (input != this.__bol) {
                        getClass();
                        return false;
                    }
                    if (this.__previousChar != '\n') {
                        return false;
                    }
                    break;
                    break;
                case 2:
                    if (input == this.__bol) {
                        if (this.__previousChar != '\n') {
                            return false;
                        }
                    } else {
                        if ((!inputRemains && input >= this.__eol) || this.__input[input - 1] != '\n') {
                            return false;
                        }
                    }
                    break;
                case 3:
                    if (input != this.__bol || this.__previousChar != '\n') {
                        return false;
                    }
                    break;
                    break;
                case 4:
                    if ((inputRemains || input < this.__eol) && nextChar2 != '\n') {
                        return false;
                    }
                    getClass();
                    if (this.__eol - input > 1) {
                        return false;
                    }
                    break;
                    break;
                case 5:
                    if ((inputRemains || input < this.__eol) && nextChar2 != '\n') {
                        return false;
                    }
                    break;
                case 6:
                    if (((inputRemains || input < this.__eol) && nextChar2 != '\n') || this.__eol - input > 1) {
                        return false;
                    }
                    break;
                case 7:
                    if ((!inputRemains && input >= this.__eol) || nextChar2 == '\n') {
                        return false;
                    }
                    input++;
                    inputRemains = input < this.__endOffset;
                    nextChar2 = inputRemains ? this.__input[input] : (char) 65535;
                    break;
                    break;
                case '\b':
                    if (!inputRemains && input >= this.__eol) {
                        return false;
                    }
                    input++;
                    inputRemains = input < this.__endOffset;
                    nextChar2 = inputRemains ? this.__input[input] : (char) 65535;
                    break;
                    break;
                case '\t':
                    int current2 = OpCode._getOperand(scan2);
                    if (nextChar2 == 65535 && inputRemains) {
                        nextChar2 = this.__input[input];
                    }
                    if (nextChar2 >= 256 || (this.__program[current2 + (nextChar2 >> 4)] & (1 << (nextChar2 & 15))) != 0) {
                        return false;
                    }
                    if (!inputRemains && input >= this.__eol) {
                        return false;
                    }
                    input++;
                    inputRemains = input < this.__endOffset;
                    nextChar2 = inputRemains ? this.__input[input] : (char) 65535;
                    break;
                    break;
                case '\n':
                case 16:
                case 17:
                    if (op == '\n') {
                        line = OpCode._getArg1(this.__program, scan2);
                        arg = OpCode._getArg2(this.__program, scan2);
                        scan = OpCode._getNextOperator(scan2) + 2;
                    } else if (op == 16) {
                        line = 0;
                        arg = 65535;
                        scan = OpCode._getNextOperator(scan2);
                    } else {
                        line = 1;
                        arg = 65535;
                        scan = OpCode._getNextOperator(scan2);
                    }
                    if (this.__program[next] == 14) {
                        nextChar = this.__program[OpCode._getOperand(next) + 1];
                        current = 0;
                    } else {
                        nextChar = 65535;
                        current = -1000;
                    }
                    this.__inputOffset = input;
                    if (minMod) {
                        if (line > 0 && __repeat(scan, line) < line) {
                            return false;
                        }
                        while (true) {
                            if (arg >= line || (arg == 65535 && line > 0)) {
                                if ((current == -1000 || this.__inputOffset >= this.__endOffset || this.__input[this.__inputOffset] == nextChar) && __match(next)) {
                                    return true;
                                }
                                this.__inputOffset = input + line;
                                if (__repeat(scan, 1) != 0) {
                                    line++;
                                    this.__inputOffset = input + line;
                                } else {
                                    return false;
                                }
                            } else {
                                return false;
                            }
                        }
                    } else {
                        int arg2 = __repeat(scan, arg);
                        if (line < arg2 && OpCode._opType[this.__program[next]] == 4) {
                            getClass();
                            if (this.__program[next] != 5 || this.__program[next] == 6) {
                                line = arg2;
                            }
                        }
                        while (arg2 >= line) {
                            if ((current == -1000 || this.__inputOffset >= this.__endOffset || this.__input[this.__inputOffset] == nextChar) && __match(next)) {
                                return true;
                            }
                            arg2--;
                            this.__inputOffset = input + arg2;
                        }
                        return false;
                    }
                    break;
                case 11:
                    Perl5Repetition rep = new Perl5Repetition();
                    rep._lastRepetition = this.__currentRep;
                    this.__currentRep = rep;
                    rep._parenFloor = this.__lastParen;
                    rep._numInstances = -1;
                    rep._min = OpCode._getArg1(this.__program, scan2);
                    rep._max = OpCode._getArg2(this.__program, scan2);
                    rep._scan = OpCode._getNextOperator(scan2) + 2;
                    rep._next = next;
                    rep._minMod = minMod;
                    rep._lastLocation = -1;
                    this.__inputOffset = input;
                    boolean minMod2 = __match(OpCode._getPrevOperator(next));
                    this.__currentRep = rep._lastRepetition;
                    return minMod2;
                case '\f':
                    if (this.__program[next] != '\f') {
                        next = OpCode._getNextOperator(scan2);
                    } else {
                        int lastParen = this.__lastParen;
                        do {
                            this.__inputOffset = input;
                            if (__match(OpCode._getNextOperator(scan2))) {
                                return true;
                            }
                            int arg3 = this.__lastParen;
                            while (arg3 > lastParen) {
                                this.__endMatchOffsets[arg3] = -1;
                                arg3--;
                            }
                            this.__lastParen = arg3;
                            scan2 = OpCode._getNext(this.__program, scan2);
                            if (scan2 == -1) {
                                return false;
                            }
                        } while (this.__program[scan2] == '\f');
                        return false;
                    }
                    break;
                case 14:
                    int current3 = OpCode._getOperand(scan2);
                    int current4 = current3 + 1;
                    char c = this.__program[current3];
                    if (this.__program[current4] != nextChar2 || this.__eol - input < c) {
                        return false;
                    }
                    if (c > 1 && !__compare(this.__program, current4, this.__input, input, c)) {
                        return false;
                    }
                    input += c;
                    inputRemains = input < this.__endOffset;
                    nextChar2 = inputRemains ? this.__input[input] : (char) 65535;
                    break;
                    break;
                case 18:
                    if (!inputRemains || !OpCode._isWordCharacter(nextChar2)) {
                        return false;
                    }
                    input++;
                    inputRemains = input < this.__endOffset;
                    nextChar2 = inputRemains ? this.__input[input] : (char) 65535;
                    break;
                    break;
                case 19:
                    if ((!inputRemains && input >= this.__eol) || OpCode._isWordCharacter(nextChar2)) {
                        return false;
                    }
                    input++;
                    inputRemains = input < this.__endOffset;
                    nextChar2 = inputRemains ? this.__input[input] : (char) 65535;
                    break;
                    break;
                case 20:
                case 21:
                    if (input == this.__bol) {
                        a = OpCode._isWordCharacter(this.__previousChar);
                    } else {
                        a = OpCode._isWordCharacter(this.__input[input - 1]);
                    }
                    boolean b = OpCode._isWordCharacter(nextChar2);
                    if ((a == b) == (this.__program[scan2] == 20)) {
                        return false;
                    }
                    break;
                case 22:
                    if ((!inputRemains && input >= this.__eol) || !Character.isWhitespace(nextChar2)) {
                        return false;
                    }
                    input++;
                    inputRemains = input < this.__endOffset;
                    nextChar2 = inputRemains ? this.__input[input] : (char) 65535;
                    break;
                    break;
                case 23:
                    if (!inputRemains || Character.isWhitespace(nextChar2)) {
                        return false;
                    }
                    input++;
                    inputRemains = input < this.__endOffset;
                    nextChar2 = inputRemains ? this.__input[input] : (char) 65535;
                    break;
                    break;
                case 24:
                    if (!Character.isDigit(nextChar2)) {
                        return false;
                    }
                    input++;
                    inputRemains = input < this.__endOffset;
                    nextChar2 = inputRemains ? this.__input[input] : (char) 65535;
                    break;
                    break;
                case 25:
                    if ((!inputRemains && input >= this.__eol) || Character.isDigit(nextChar2)) {
                        return false;
                    }
                    input++;
                    inputRemains = input < this.__endOffset;
                    nextChar2 = inputRemains ? this.__input[input] : (char) 65535;
                    break;
                    break;
                case 26:
                    int arg4 = OpCode._getArg1(this.__program, scan2);
                    int current5 = this.__beginMatchOffsets[arg4];
                    if (current5 == -1 || this.__endMatchOffsets[arg4] == -1) {
                        return false;
                    }
                    if (current5 == this.__endMatchOffsets[arg4]) {
                        continue;
                    } else {
                        if (this.__input[current5] != nextChar2) {
                            return false;
                        }
                        int line2 = this.__endMatchOffsets[arg4] - current5;
                        if (input + line2 > this.__eol) {
                            return false;
                        }
                        if (line2 > 1 && !__compare(this.__input, current5, this.__input, input, line2)) {
                            return false;
                        }
                        input += line2;
                        inputRemains = input < this.__endOffset;
                        nextChar2 = inputRemains ? this.__input[input] : (char) 65535;
                    }
                    break;
                    break;
                case 27:
                    int arg5 = OpCode._getArg1(this.__program, scan2);
                    this.__beginMatchOffsets[arg5] = input;
                    if (arg5 > this.__expSize) {
                        this.__expSize = arg5;
                    }
                    break;
                case 28:
                    int arg6 = OpCode._getArg1(this.__program, scan2);
                    this.__endMatchOffsets[arg6] = input;
                    if (arg6 > this.__lastParen) {
                        this.__lastParen = arg6;
                    }
                    break;
                case 29:
                    minMod = true;
                    break;
                case 30:
                    if (input != this.__bol) {
                        return true;
                    }
                    break;
                    break;
                case 31:
                    this.__inputOffset = input;
                    if (!__match(OpCode._getNextOperator(scan2))) {
                        return false;
                    }
                    break;
                    break;
                case ' ':
                    this.__inputOffset = input;
                    if (__match(OpCode._getNextOperator(scan2))) {
                        return false;
                    }
                    break;
                case '\"':
                    Perl5Repetition rep2 = this.__currentRep;
                    int arg7 = rep2._numInstances + 1;
                    this.__inputOffset = input;
                    if (input == rep2._lastLocation) {
                        this.__currentRep = rep2._lastRepetition;
                        int line3 = this.__currentRep._numInstances;
                        if (__match(rep2._next)) {
                            return true;
                        }
                        this.__currentRep._numInstances = line3;
                        this.__currentRep = rep2;
                        return false;
                    }
                    if (arg7 < rep2._min) {
                        rep2._numInstances = arg7;
                        rep2._lastLocation = input;
                        if (__match(rep2._scan)) {
                            return true;
                        }
                        rep2._numInstances = arg7 - 1;
                        return false;
                    }
                    if (rep2._minMod) {
                        this.__currentRep = rep2._lastRepetition;
                        int line4 = this.__currentRep._numInstances;
                        if (__match(rep2._next)) {
                            return true;
                        }
                        this.__currentRep._numInstances = line4;
                        this.__currentRep = rep2;
                        if (arg7 >= rep2._max) {
                            return false;
                        }
                        this.__inputOffset = input;
                        rep2._numInstances = arg7;
                        rep2._lastLocation = input;
                        if (__match(rep2._scan)) {
                            return true;
                        }
                        rep2._numInstances = arg7 - 1;
                        return false;
                    }
                    if (arg7 < rep2._max) {
                        __pushState(rep2._parenFloor);
                        rep2._numInstances = arg7;
                        rep2._lastLocation = input;
                        if (__match(rep2._scan)) {
                            return true;
                        }
                        __popState();
                        this.__inputOffset = input;
                    }
                    this.__currentRep = rep2._lastRepetition;
                    int line5 = this.__currentRep._numInstances;
                    if (__match(rep2._next)) {
                        return true;
                    }
                    rep2._numInstances = line5;
                    this.__currentRep = rep2;
                    rep2._numInstances = arg7 - 1;
                    return false;
                case '#':
                case '$':
                    int current6 = OpCode._getOperand(scan2);
                    if (nextChar2 == 65535 && inputRemains) {
                        nextChar2 = this.__input[input];
                    }
                    if (!__matchUnicodeClass(nextChar2, this.__program, current6, op)) {
                        return false;
                    }
                    if (!inputRemains && input >= this.__eol) {
                        return false;
                    }
                    input++;
                    inputRemains = input < this.__endOffset;
                    nextChar2 = inputRemains ? this.__input[input] : (char) 65535;
                    break;
                    break;
            }
            scan2 = next;
        }
        return false;
    }

    static char[] _toLower(char[] in) {
        char[] input = (char[]) in.clone();
        char[] inp = new char[input.length];
        System.arraycopy(input, 0, inp, 0, input.length);
        for (int current = 0; current < inp.length; current++) {
            if (Character.isUpperCase(inp[current])) {
                inp[current] = Character.toLowerCase(inp[current]);
            }
        }
        return inp;
    }

    @Override // org.apache.oro.text.regex.PatternMatcher
    public boolean matchesPrefix(char[] in, Pattern pattern, int offset) {
        char[] input = (char[]) in.clone();
        Perl5Pattern expression = (Perl5Pattern) pattern;
        this.__originalInput = input;
        if (expression._isCaseInsensitive) {
            input = _toLower(input);
        }
        __initInterpreterGlobals(expression, input, 0, input.length, offset);
        this.__lastSuccess = __tryExpression(offset);
        this.__lastMatchResult = null;
        return this.__lastSuccess;
    }

    @Override // org.apache.oro.text.regex.PatternMatcher
    public boolean matchesPrefix(char[] input, Pattern pattern) {
        return matchesPrefix(input, pattern, 0);
    }

    @Override // org.apache.oro.text.regex.PatternMatcher
    public boolean matchesPrefix(String input, Pattern pattern) {
        return matchesPrefix(input.toCharArray(), pattern, 0);
    }

    @Override // org.apache.oro.text.regex.PatternMatcher
    public boolean matchesPrefix(PatternMatcherInput input, Pattern pattern) {
        char[] inp;
        Perl5Pattern expression = (Perl5Pattern) pattern;
        this.__originalInput = input._originalBuffer;
        if (expression._isCaseInsensitive) {
            if (input._toLowerBuffer == null) {
                input._toLowerBuffer = _toLower(this.__originalInput);
            }
            inp = input._toLowerBuffer;
        } else {
            inp = this.__originalInput;
        }
        __initInterpreterGlobals(expression, inp, input._beginOffset, input._endOffset, input._currentOffset);
        this.__lastSuccess = __tryExpression(input._currentOffset);
        this.__lastMatchResult = null;
        return this.__lastSuccess;
    }

    @Override // org.apache.oro.text.regex.PatternMatcher
    public boolean matches(char[] in, Pattern pattern) {
        char[] input = (char[]) in.clone();
        Perl5Pattern expression = (Perl5Pattern) pattern;
        this.__originalInput = input;
        if (expression._isCaseInsensitive) {
            input = _toLower(input);
        }
        __initInterpreterGlobals(expression, input, 0, input.length, 0);
        this.__lastSuccess = __tryExpression(0) && this.__endMatchOffsets[0] == input.length;
        this.__lastMatchResult = null;
        return this.__lastSuccess;
    }

    @Override // org.apache.oro.text.regex.PatternMatcher
    public boolean matches(String input, Pattern pattern) {
        return matches(input.toCharArray(), pattern);
    }

    @Override // org.apache.oro.text.regex.PatternMatcher
    public boolean matches(PatternMatcherInput input, Pattern pattern) {
        char[] inp;
        Perl5Pattern expression = (Perl5Pattern) pattern;
        this.__originalInput = input._originalBuffer;
        if (expression._isCaseInsensitive) {
            if (input._toLowerBuffer == null) {
                input._toLowerBuffer = _toLower(this.__originalInput);
            }
            inp = input._toLowerBuffer;
        } else {
            inp = this.__originalInput;
        }
        __initInterpreterGlobals(expression, inp, input._beginOffset, input._endOffset, input._beginOffset);
        this.__lastMatchResult = null;
        if (__tryExpression(input._beginOffset) && (this.__endMatchOffsets[0] == input._endOffset || input.length() == 0 || input._beginOffset == input._endOffset)) {
            this.__lastSuccess = true;
            return true;
        }
        this.__lastSuccess = false;
        return false;
    }

    @Override // org.apache.oro.text.regex.PatternMatcher
    public boolean contains(String input, Pattern pattern) {
        return contains(input.toCharArray(), pattern);
    }

    @Override // org.apache.oro.text.regex.PatternMatcher
    public boolean contains(char[] in, Pattern pattern) {
        char[] input = (char[]) in.clone();
        Perl5Pattern expression = (Perl5Pattern) pattern;
        this.__originalInput = input;
        if (expression._isCaseInsensitive) {
            input = _toLower(input);
        }
        return __interpret(expression, input, 0, input.length, 0);
    }

    @Override // org.apache.oro.text.regex.PatternMatcher
    public boolean contains(PatternMatcherInput input, Pattern pattern) {
        char[] inp;
        if (input._currentOffset > input._endOffset) {
            return false;
        }
        Perl5Pattern expression = (Perl5Pattern) pattern;
        this.__originalInput = input._originalBuffer;
        this.__originalInput = input._originalBuffer;
        if (expression._isCaseInsensitive) {
            if (input._toLowerBuffer == null) {
                input._toLowerBuffer = _toLower(this.__originalInput);
            }
            inp = input._toLowerBuffer;
        } else {
            inp = this.__originalInput;
        }
        this.__lastMatchInputEndOffset = input.getMatchEndOffset();
        boolean matchFound = __interpret(expression, inp, input._beginOffset, input._endOffset, input._currentOffset);
        if (matchFound) {
            input.setCurrentOffset(this.__endMatchOffsets[0]);
            input.setMatchOffsets(this.__beginMatchOffsets[0], this.__endMatchOffsets[0]);
        } else {
            input.setCurrentOffset(input._endOffset + 1);
        }
        this.__lastMatchInputEndOffset = __DEFAULT_LAST_MATCH_END_OFFSET;
        return matchFound;
    }

    @Override // org.apache.oro.text.regex.PatternMatcher
    public MatchResult getMatch() {
        if (!this.__lastSuccess) {
            return null;
        }
        if (this.__lastMatchResult == null) {
            __setLastMatchResult();
        }
        return this.__lastMatchResult;
    }
}
