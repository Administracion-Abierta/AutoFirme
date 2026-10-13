package org.mozilla.javascript.regexp;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.IdFunctionObject;
import org.mozilla.javascript.IdScriptableObject;
import org.mozilla.javascript.Kit;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Symbol;
import org.mozilla.javascript.SymbolKey;
import org.mozilla.javascript.TopLevel;
import org.mozilla.javascript.Undefined;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/regexp/NativeRegExp.class */
public class NativeRegExp extends IdScriptableObject implements Function {
    private static final long serialVersionUID = 4965263491464903264L;
    private static final Object REGEXP_TAG;
    public static final int JSREG_GLOB = 1;
    public static final int JSREG_FOLD = 2;
    public static final int JSREG_MULTILINE = 4;
    public static final int TEST = 0;
    public static final int MATCH = 1;
    public static final int PREFIX = 2;
    private static final boolean debug = false;
    private static final byte REOP_SIMPLE_START = 1;
    private static final byte REOP_EMPTY = 1;
    private static final byte REOP_BOL = 2;
    private static final byte REOP_EOL = 3;
    private static final byte REOP_WBDRY = 4;
    private static final byte REOP_WNONBDRY = 5;
    private static final byte REOP_DOT = 6;
    private static final byte REOP_DIGIT = 7;
    private static final byte REOP_NONDIGIT = 8;
    private static final byte REOP_ALNUM = 9;
    private static final byte REOP_NONALNUM = 10;
    private static final byte REOP_SPACE = 11;
    private static final byte REOP_NONSPACE = 12;
    private static final byte REOP_BACKREF = 13;
    private static final byte REOP_FLAT = 14;
    private static final byte REOP_FLAT1 = 15;
    private static final byte REOP_FLATi = 16;
    private static final byte REOP_FLAT1i = 17;
    private static final byte REOP_UCFLAT1 = 18;
    private static final byte REOP_UCFLAT1i = 19;
    private static final byte REOP_CLASS = 22;
    private static final byte REOP_NCLASS = 23;
    private static final byte REOP_SIMPLE_END = 23;
    private static final byte REOP_QUANT = 25;
    private static final byte REOP_STAR = 26;
    private static final byte REOP_PLUS = 27;
    private static final byte REOP_OPT = 28;
    private static final byte REOP_LPAREN = 29;
    private static final byte REOP_RPAREN = 30;
    private static final byte REOP_ALT = 31;
    private static final byte REOP_JUMP = 32;
    private static final byte REOP_ASSERT = 41;
    private static final byte REOP_ASSERT_NOT = 42;
    private static final byte REOP_ASSERTTEST = 43;
    private static final byte REOP_ASSERTNOTTEST = 44;
    private static final byte REOP_MINIMALSTAR = 45;
    private static final byte REOP_MINIMALPLUS = 46;
    private static final byte REOP_MINIMALOPT = 47;
    private static final byte REOP_MINIMALQUANT = 48;
    private static final byte REOP_ENDCHILD = 49;
    private static final byte REOP_REPEAT = 51;
    private static final byte REOP_MINIMALREPEAT = 52;
    private static final byte REOP_ALTPREREQ = 53;
    private static final byte REOP_ALTPREREQi = 54;
    private static final byte REOP_ALTPREREQ2 = 55;
    private static final byte REOP_END = 57;
    private static final int ANCHOR_BOL = -2;
    private static final int INDEX_LEN = 2;
    private static final int Id_lastIndex = 1;
    private static final int Id_source = 2;
    private static final int Id_global = 3;
    private static final int Id_ignoreCase = 4;
    private static final int Id_multiline = 5;
    private static final int MAX_INSTANCE_ID = 5;
    private static final int Id_compile = 1;
    private static final int Id_toString = 2;
    private static final int Id_toSource = 3;
    private static final int Id_exec = 4;
    private static final int Id_test = 5;
    private static final int Id_prefix = 6;
    private static final int SymbolId_match = 7;
    private static final int SymbolId_search = 8;
    private static final int MAX_PROTOTYPE_ID = 8;
    private RECompiled re;
    Object lastIndex = ScriptRuntime.zeroObj;
    private int lastIndexAttr = 6;
    static final /* synthetic */ boolean $assertionsDisabled;

    static {
        $assertionsDisabled = !NativeRegExp.class.desiredAssertionStatus();
        REGEXP_TAG = new Object();
    }

    public static void init(Context cx, Scriptable scope, boolean sealed) {
        NativeRegExp nativeRegExp = new NativeRegExp();
        nativeRegExp.re = compileRE(cx, "", null, false);
        nativeRegExp.activatePrototypeMap(8);
        nativeRegExp.setParentScope(scope);
        nativeRegExp.setPrototype(getObjectPrototype(scope));
        NativeRegExpCtor nativeRegExpCtor = new NativeRegExpCtor();
        nativeRegExp.defineProperty("constructor", nativeRegExpCtor, 2);
        ScriptRuntime.setFunctionProtoAndParent(nativeRegExpCtor, scope);
        nativeRegExpCtor.setImmunePrototypeProperty(nativeRegExp);
        if (sealed) {
            nativeRegExp.sealObject();
            nativeRegExpCtor.sealObject();
        }
        defineProperty(scope, "RegExp", nativeRegExpCtor, 2);
    }

    NativeRegExp(Scriptable scope, RECompiled regexpCompiled) {
        this.re = regexpCompiled;
        setLastIndex(ScriptRuntime.zeroObj);
        ScriptRuntime.setBuiltinProtoAndParent(this, scope, TopLevel.Builtins.RegExp);
    }

    @Override // org.mozilla.javascript.ScriptableObject, org.mozilla.javascript.Scriptable
    public String getClassName() {
        return "RegExp";
    }

    @Override // org.mozilla.javascript.ScriptableObject
    public String getTypeOf() {
        return "object";
    }

    @Override // org.mozilla.javascript.Function, org.mozilla.javascript.Callable
    public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        if (cx.getLanguageVersion() < 200) {
            return execSub(cx, scope, args, 1);
        }
        throw ScriptRuntime.notFunctionError(thisObj);
    }

    @Override // org.mozilla.javascript.Function
    public Scriptable construct(Context cx, Scriptable scope, Object[] args) {
        if (cx.getLanguageVersion() < 200) {
            return (Scriptable) execSub(cx, scope, args, 1);
        }
        throw ScriptRuntime.notFunctionError(this);
    }

    Scriptable compile(Context cx, Scriptable scope, Object[] args) {
        if (args.length > 0 && (args[0] instanceof NativeRegExp)) {
            if (args.length > 1 && args[1] != Undefined.instance) {
                throw ScriptRuntime.typeError0("msg.bad.regexp.compile");
            }
            NativeRegExp thatObj = (NativeRegExp) args[0];
            this.re = thatObj.re;
            setLastIndex(thatObj.lastIndex);
            return this;
        }
        String s = (args.length == 0 || (args[0] instanceof Undefined)) ? "" : escapeRegExp(args[0]);
        String global = (args.length <= 1 || args[1] == Undefined.instance) ? null : ScriptRuntime.toString(args[1]);
        this.re = compileRE(cx, s, global, false);
        setLastIndex(ScriptRuntime.zeroObj);
        return this;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append('/');
        if (this.re.source.length != 0) {
            buf.append(this.re.source);
        } else {
            buf.append("(?:)");
        }
        buf.append('/');
        if ((this.re.flags & 1) != 0) {
            buf.append('g');
        }
        if ((this.re.flags & 2) != 0) {
            buf.append('i');
        }
        if ((this.re.flags & 4) != 0) {
            buf.append('m');
        }
        return buf.toString();
    }

    NativeRegExp() {
    }

    private static RegExpImpl getImpl(Context cx) {
        return (RegExpImpl) ScriptRuntime.getRegExpProxy(cx);
    }

    private static String escapeRegExp(Object src) {
        String s = ScriptRuntime.toString(src);
        StringBuilder sb = null;
        int start = 0;
        int iIndexOf = s.indexOf(47);
        while (true) {
            int slash = iIndexOf;
            if (slash <= -1) {
                break;
            }
            if (slash == start || s.charAt(slash - 1) != '\\') {
                if (sb == null) {
                    sb = new StringBuilder();
                }
                sb.append((CharSequence) s, start, slash);
                sb.append("\\/");
                start = slash + 1;
            }
            iIndexOf = s.indexOf(47, slash + 1);
        }
        if (sb != null) {
            sb.append((CharSequence) s, start, s.length());
            s = sb.toString();
        }
        return s;
    }

    private Object execSub(Context cx, Scriptable scopeObj, Object[] args, int matchType) {
        String str;
        Object rval;
        RegExpImpl reImpl = getImpl(cx);
        if (args.length == 0) {
            str = reImpl.input;
            if (str == null) {
                str = ScriptRuntime.toString(Undefined.instance);
            }
        } else {
            str = ScriptRuntime.toString(args[0]);
        }
        double d = 0.0d;
        if ((this.re.flags & 1) != 0) {
            d = ScriptRuntime.toInteger(this.lastIndex);
        }
        if (d < 0.0d || str.length() < d) {
            setLastIndex(ScriptRuntime.zeroObj);
            rval = null;
        } else {
            int[] indexp = {(int) d};
            rval = executeRegExp(cx, scopeObj, reImpl, str, indexp, matchType);
            if ((this.re.flags & 1) != 0) {
                if (rval == null || rval == Undefined.instance) {
                    setLastIndex(ScriptRuntime.zeroObj);
                } else {
                    setLastIndex(Double.valueOf(indexp[0]));
                }
            }
        }
        return rval;
    }

    static RECompiled compileRE(Context cx, String str, String global, boolean flat) {
        RECompiled regexp = new RECompiled(str);
        int length = str.length();
        int flags = 0;
        if (global != null) {
            for (int i = 0; i < global.length(); i++) {
                char c = global.charAt(i);
                int f = 0;
                if (c == 'g') {
                    f = 1;
                } else if (c == 'i') {
                    f = 2;
                } else if (c == 'm') {
                    f = 4;
                } else {
                    reportError("msg.invalid.re.flag", String.valueOf(c));
                }
                if ((flags & f) != 0) {
                    reportError("msg.invalid.re.flag", String.valueOf(c));
                }
                flags |= f;
            }
        }
        regexp.flags = flags;
        CompilerState state = new CompilerState(cx, regexp.source, length, flags);
        if (flat && length > 0) {
            state.result = new RENode((byte) 14);
            state.result.chr = state.cpbegin[0];
            state.result.length = length;
            state.result.flatIndex = 0;
            state.progLength += 5;
        } else {
            if (!parseDisjunction(state)) {
                return null;
            }
            if (state.maxBackReference > state.parenCount) {
                state = new CompilerState(cx, regexp.source, length, flags);
                state.backReferenceLimit = state.parenCount;
                if (!parseDisjunction(state)) {
                    return null;
                }
            }
        }
        regexp.program = new byte[state.progLength + 1];
        if (state.classCount != 0) {
            regexp.classList = new RECharSet[state.classCount];
            regexp.classCount = state.classCount;
        }
        int endPC = emitREBytecode(state, regexp, 0, state.result);
        int i2 = endPC + 1;
        regexp.program[endPC] = 57;
        regexp.parenCount = state.parenCount;
        switch (regexp.program[0]) {
            case 2:
                regexp.anchorCh = -2;
                break;
            case 14:
            case 16:
                int k = getIndex(regexp.program, 1);
                regexp.anchorCh = regexp.source[k];
                break;
            case 15:
            case 17:
                regexp.anchorCh = (char) (regexp.program[1] & 255);
                break;
            case 18:
            case 19:
                regexp.anchorCh = (char) getIndex(regexp.program, 1);
                break;
            case 31:
                RENode n = state.result;
                if (n.kid.op == 2 && n.kid2.op == 2) {
                    regexp.anchorCh = -2;
                }
                break;
        }
        return regexp;
    }

    static boolean isDigit(char c) {
        return '0' <= c && c <= '9';
    }

    private static boolean isWord(char c) {
        return ('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z') || isDigit(c) || c == '_';
    }

    private static boolean isControlLetter(char c) {
        return ('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z');
    }

    private static boolean isLineTerm(char c) {
        return ScriptRuntime.isJSLineTerminator(c);
    }

    private static boolean isREWhiteSpace(int c) {
        return ScriptRuntime.isJSWhitespaceOrLineTerminator(c);
    }

    private static char upcase(char ch) {
        if (ch < 128) {
            if ('a' <= ch && ch <= 'z') {
                return (char) (ch - ' ');
            }
            return ch;
        }
        char cu = Character.toUpperCase(ch);
        return cu < 128 ? ch : cu;
    }

    private static char downcase(char ch) {
        if (ch < 128) {
            if ('A' <= ch && ch <= 'Z') {
                return (char) (ch + ' ');
            }
            return ch;
        }
        char cl = Character.toLowerCase(ch);
        return cl < 128 ? ch : cl;
    }

    private static int toASCIIHexDigit(int c) {
        if (c < 48) {
            return -1;
        }
        if (c <= 57) {
            return c - 48;
        }
        int c2 = c | 32;
        if (97 <= c2 && c2 <= 102) {
            return (c2 - 97) + 10;
        }
        return -1;
    }

    private static boolean parseDisjunction(CompilerState state) {
        if (!parseAlternative(state)) {
            return false;
        }
        char[] source = state.cpbegin;
        int index = state.cp;
        if (index != source.length && source[index] == '|') {
            state.cp++;
            RENode result = new RENode((byte) 31);
            result.kid = state.result;
            if (!parseDisjunction(state)) {
                return false;
            }
            result.kid2 = state.result;
            state.result = result;
            if (result.kid.op == 14 && result.kid2.op == 14) {
                result.op = (state.flags & 2) == 0 ? (byte) 53 : (byte) 54;
                result.chr = result.kid.chr;
                result.index = result.kid2.chr;
                state.progLength += 13;
                return true;
            }
            if (result.kid.op == 22 && result.kid.index < 256 && result.kid2.op == 14 && (state.flags & 2) == 0) {
                result.op = (byte) 55;
                result.chr = result.kid2.chr;
                result.index = result.kid.index;
                state.progLength += 13;
                return true;
            }
            if (result.kid.op == 14 && result.kid2.op == 22 && result.kid2.index < 256 && (state.flags & 2) == 0) {
                result.op = (byte) 55;
                result.chr = result.kid.chr;
                result.index = result.kid2.index;
                state.progLength += 13;
                return true;
            }
            state.progLength += 9;
            return true;
        }
        return true;
    }

    private static boolean parseAlternative(CompilerState state) {
        RENode headTerm = null;
        RENode tailTerm = null;
        char[] source = state.cpbegin;
        while (state.cp != state.cpend && source[state.cp] != '|' && (state.parenNesting == 0 || source[state.cp] != ')')) {
            if (!parseTerm(state)) {
                return false;
            }
            if (headTerm == null) {
                headTerm = state.result;
                tailTerm = headTerm;
            } else {
                tailTerm.next = state.result;
            }
            while (tailTerm.next != null) {
                tailTerm = tailTerm.next;
            }
        }
        if (headTerm == null) {
            state.result = new RENode((byte) 1);
            return true;
        }
        state.result = headTerm;
        return true;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /*  JADX ERROR: JadxRuntimeException in pass: RegionMakerVisitor
        jadx.core.utils.exceptions.JadxRuntimeException: Not found exit edge by exit block: B:31:0x01eb
        	at jadx.core.dex.visitors.regions.maker.LoopRegionMaker.checkLoopExits(LoopRegionMaker.java:272)
        	at jadx.core.dex.visitors.regions.maker.LoopRegionMaker.makeLoopRegion(LoopRegionMaker.java:237)
        	at jadx.core.dex.visitors.regions.maker.LoopRegionMaker.process(LoopRegionMaker.java:80)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.traverse(RegionMaker.java:92)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.makeRegion(RegionMaker.java:69)
        	at jadx.core.dex.visitors.regions.maker.SwitchRegionMaker.addCases(SwitchRegionMaker.java:127)
        	at jadx.core.dex.visitors.regions.maker.SwitchRegionMaker.process(SwitchRegionMaker.java:75)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.traverse(RegionMaker.java:115)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.makeRegion(RegionMaker.java:69)
        	at jadx.core.dex.visitors.regions.maker.SwitchRegionMaker.addCases(SwitchRegionMaker.java:127)
        	at jadx.core.dex.visitors.regions.maker.SwitchRegionMaker.process(SwitchRegionMaker.java:75)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.traverse(RegionMaker.java:115)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.makeRegion(RegionMaker.java:69)
        	at jadx.core.dex.visitors.regions.maker.LoopRegionMaker.process(LoopRegionMaker.java:162)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.traverse(RegionMaker.java:92)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.makeRegion(RegionMaker.java:69)
        	at jadx.core.dex.visitors.regions.maker.IfRegionMaker.process(IfRegionMaker.java:117)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.traverse(RegionMaker.java:109)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.makeRegion(RegionMaker.java:69)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.makeMthRegion(RegionMaker.java:49)
        	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:25)
        */
    private static boolean calculateBitmapSize(org.mozilla.javascript.regexp.CompilerState r4, org.mozilla.javascript.regexp.RENode r5, char[] r6, int r7, int r8) {
        /*
            Method dump skipped, instruction units count: 816
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.mozilla.javascript.regexp.NativeRegExp.calculateBitmapSize(org.mozilla.javascript.regexp.CompilerState, org.mozilla.javascript.regexp.RENode, char[], int, int):boolean");
    }

    private static void doFlat(CompilerState state, char c) {
        state.result = new RENode((byte) 14);
        state.result.chr = c;
        state.result.length = 1;
        state.result.flatIndex = -1;
        state.progLength += 3;
    }

    private static int getDecimalValue(char c, CompilerState state, int maxValue, String overflowMessageId) {
        boolean overflow = false;
        int start = state.cp;
        char[] src = state.cpbegin;
        int value = c - 48;
        while (state.cp != state.cpend) {
            char c2 = src[state.cp];
            if (!isDigit(c2)) {
                break;
            }
            if (!overflow) {
                int v = (value * 10) + (c2 - '0');
                if (v < maxValue) {
                    value = v;
                } else {
                    overflow = true;
                    value = maxValue;
                }
            }
            state.cp++;
        }
        if (overflow) {
            reportError(overflowMessageId, String.valueOf(src, start, state.cp - start));
        }
        return value;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /*  JADX ERROR: JadxRuntimeException in pass: RegionMakerVisitor
        jadx.core.utils.exceptions.JadxRuntimeException: Not found exit edge by exit block: B:64:0x03d5
        	at jadx.core.dex.visitors.regions.maker.LoopRegionMaker.checkLoopExits(LoopRegionMaker.java:272)
        	at jadx.core.dex.visitors.regions.maker.LoopRegionMaker.makeLoopRegion(LoopRegionMaker.java:237)
        	at jadx.core.dex.visitors.regions.maker.LoopRegionMaker.process(LoopRegionMaker.java:80)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.traverse(RegionMaker.java:92)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.makeRegion(RegionMaker.java:69)
        	at jadx.core.dex.visitors.regions.maker.SwitchRegionMaker.addCases(SwitchRegionMaker.java:127)
        	at jadx.core.dex.visitors.regions.maker.SwitchRegionMaker.process(SwitchRegionMaker.java:75)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.traverse(RegionMaker.java:115)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.makeRegion(RegionMaker.java:69)
        	at jadx.core.dex.visitors.regions.maker.IfRegionMaker.process(IfRegionMaker.java:111)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.traverse(RegionMaker.java:109)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.makeRegion(RegionMaker.java:69)
        	at jadx.core.dex.visitors.regions.maker.SwitchRegionMaker.addCases(SwitchRegionMaker.java:127)
        	at jadx.core.dex.visitors.regions.maker.SwitchRegionMaker.process(SwitchRegionMaker.java:75)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.traverse(RegionMaker.java:115)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.makeRegion(RegionMaker.java:69)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.makeMthRegion(RegionMaker.java:49)
        	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:25)
        */
    private static boolean parseTerm(org.mozilla.javascript.regexp.CompilerState r9) {
        /*
            Method dump skipped, instruction units count: 2381
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.mozilla.javascript.regexp.NativeRegExp.parseTerm(org.mozilla.javascript.regexp.CompilerState):boolean");
    }

    private static void resolveForwardJump(byte[] array, int from, int pc) {
        if (from > pc) {
            throw Kit.codeBug();
        }
        addIndex(array, from, pc - from);
    }

    private static int getOffset(byte[] array, int pc) {
        return getIndex(array, pc);
    }

    private static int addIndex(byte[] array, int pc, int index) {
        if (index < 0) {
            throw Kit.codeBug();
        }
        if (index > 65535) {
            throw Context.reportRuntimeError("Too complex regexp");
        }
        array[pc] = (byte) (index >> 8);
        array[pc + 1] = (byte) index;
        return pc + 2;
    }

    private static int getIndex(byte[] array, int pc) {
        return ((array[pc] & 255) << 8) | (array[pc + 1] & 255);
    }

    private static int emitREBytecode(CompilerState state, RECompiled re, int pc, RENode t) {
        byte[] program = re.program;
        while (t != null) {
            int i = pc;
            pc++;
            program[i] = t.op;
            switch (t.op) {
                case 1:
                    pc--;
                    continue;
                    t = t.next;
                    break;
                case 13:
                    pc = addIndex(program, pc, t.parenIndex);
                    continue;
                    t = t.next;
                    break;
                case 14:
                    if (t.flatIndex != -1) {
                        while (t.next != null && t.next.op == 14 && t.flatIndex + t.length == t.next.flatIndex) {
                            t.length += t.next.length;
                            t.next = t.next.next;
                        }
                    }
                    if (t.flatIndex != -1 && t.length > 1) {
                        if ((state.flags & 2) != 0) {
                            program[pc - 1] = 16;
                        } else {
                            program[pc - 1] = 14;
                        }
                        pc = addIndex(program, addIndex(program, pc, t.flatIndex), t.length);
                        continue;
                    } else if (t.chr < 256) {
                        if ((state.flags & 2) != 0) {
                            program[pc - 1] = 17;
                        } else {
                            program[pc - 1] = 15;
                        }
                        pc++;
                        program[pc] = (byte) t.chr;
                    } else {
                        if ((state.flags & 2) != 0) {
                            program[pc - 1] = 19;
                        } else {
                            program[pc - 1] = 18;
                        }
                        pc = addIndex(program, pc, t.chr);
                    }
                    t = t.next;
                    break;
                case 22:
                    if (!t.sense) {
                        program[pc - 1] = 23;
                    }
                    pc = addIndex(program, pc, t.index);
                    re.classList[t.index] = new RECharSet(t.bmsize, t.startIndex, t.kidlen, t.sense);
                    continue;
                    t = t.next;
                    break;
                case 25:
                    if (t.min == 0 && t.max == -1) {
                        program[pc - 1] = t.greedy ? (byte) 26 : (byte) 45;
                    } else if (t.min == 0 && t.max == 1) {
                        program[pc - 1] = t.greedy ? (byte) 28 : (byte) 47;
                    } else if (t.min == 1 && t.max == -1) {
                        program[pc - 1] = t.greedy ? (byte) 27 : (byte) 46;
                    } else {
                        if (!t.greedy) {
                            program[pc - 1] = 48;
                        }
                        pc = addIndex(program, addIndex(program, pc, t.min), t.max + 1);
                    }
                    int pc2 = addIndex(program, addIndex(program, pc, t.parenCount), t.parenIndex);
                    int pc3 = emitREBytecode(state, re, pc2 + 2, t.kid);
                    pc = pc3 + 1;
                    program[pc3] = 49;
                    resolveForwardJump(program, pc2, pc);
                    continue;
                    t = t.next;
                    break;
                case 29:
                    int pc4 = emitREBytecode(state, re, addIndex(program, pc, t.parenIndex), t.kid);
                    program[pc4] = 30;
                    pc = addIndex(program, pc4 + 1, t.parenIndex);
                    continue;
                    t = t.next;
                    break;
                case 31:
                    break;
                case 41:
                    int pc5 = emitREBytecode(state, re, pc + 2, t.kid);
                    pc = pc5 + 1;
                    program[pc5] = 43;
                    resolveForwardJump(program, pc, pc);
                    continue;
                    t = t.next;
                    break;
                case 42:
                    int pc6 = emitREBytecode(state, re, pc + 2, t.kid);
                    pc = pc6 + 1;
                    program[pc6] = 44;
                    resolveForwardJump(program, pc, pc);
                    continue;
                    t = t.next;
                    break;
                case 53:
                case 54:
                case 55:
                    boolean ignoreCase = t.op == 54;
                    addIndex(program, pc, ignoreCase ? upcase(t.chr) : t.chr);
                    int pc7 = pc + 2;
                    addIndex(program, pc7, ignoreCase ? upcase((char) t.index) : t.index);
                    pc = pc7 + 2;
                    break;
                default:
                    continue;
                    t = t.next;
                    break;
            }
            RENode nextAlt = t.kid2;
            int nextAltFixup = pc;
            int pc8 = emitREBytecode(state, re, pc + 2, t.kid);
            int pc9 = pc8 + 1;
            program[pc8] = 32;
            int pc10 = pc9 + 2;
            resolveForwardJump(program, nextAltFixup, pc10);
            int pc11 = emitREBytecode(state, re, pc10, nextAlt);
            int pc12 = pc11 + 1;
            program[pc11] = 32;
            pc = pc12 + 2;
            resolveForwardJump(program, pc9, pc);
            resolveForwardJump(program, pc12, pc);
            t = t.next;
        }
        return pc;
    }

    private static void pushProgState(REGlobalData gData, int min, int max, int cp, REBackTrackData backTrackLastToSave, int continuationOp, int continuationPc) {
        gData.stateStackTop = new REProgState(gData.stateStackTop, min, max, cp, backTrackLastToSave, continuationOp, continuationPc);
    }

    private static REProgState popProgState(REGlobalData gData) {
        REProgState state = gData.stateStackTop;
        gData.stateStackTop = state.previous;
        return state;
    }

    private static void pushBackTrackState(REGlobalData gData, byte op, int pc) {
        REProgState state = gData.stateStackTop;
        gData.backTrackStackTop = new REBackTrackData(gData, op, pc, gData.cp, state.continuationOp, state.continuationPc);
    }

    private static void pushBackTrackState(REGlobalData gData, byte op, int pc, int cp, int continuationOp, int continuationPc) {
        gData.backTrackStackTop = new REBackTrackData(gData, op, pc, cp, continuationOp, continuationPc);
    }

    private static boolean flatNMatcher(REGlobalData gData, int matchChars, int length, String input, int end) {
        if (gData.cp + length > end) {
            return false;
        }
        for (int i = 0; i < length; i++) {
            if (gData.regexp.source[matchChars + i] != input.charAt(gData.cp + i)) {
                return false;
            }
        }
        gData.cp += length;
        return true;
    }

    private static boolean flatNIMatcher(REGlobalData gData, int matchChars, int length, String input, int end) {
        if (gData.cp + length > end) {
            return false;
        }
        char[] source = gData.regexp.source;
        for (int i = 0; i < length; i++) {
            char c1 = source[matchChars + i];
            char c2 = input.charAt(gData.cp + i);
            if (c1 != c2 && upcase(c1) != upcase(c2)) {
                return false;
            }
        }
        gData.cp += length;
        return true;
    }

    private static boolean backrefMatcher(REGlobalData gData, int parenIndex, String input, int end) {
        if (gData.parens == null || parenIndex >= gData.parens.length) {
            return false;
        }
        int parenContent = gData.parensIndex(parenIndex);
        if (parenContent == -1) {
            return true;
        }
        int len = gData.parensLength(parenIndex);
        if (gData.cp + len > end) {
            return false;
        }
        if ((gData.regexp.flags & 2) != 0) {
            for (int i = 0; i < len; i++) {
                char c1 = input.charAt(parenContent + i);
                char c2 = input.charAt(gData.cp + i);
                if (c1 != c2 && upcase(c1) != upcase(c2)) {
                    return false;
                }
            }
        } else if (!input.regionMatches(parenContent, input, gData.cp, len)) {
            return false;
        }
        gData.cp += len;
        return true;
    }

    private static void addCharacterToCharSet(RECharSet cs, char c) {
        int byteIndex = c / '\b';
        if (c >= cs.length) {
            throw ScriptRuntime.constructError("SyntaxError", "invalid range in character class");
        }
        byte[] bArr = cs.bits;
        bArr[byteIndex] = (byte) (bArr[byteIndex] | (1 << (c & 7)));
    }

    private static void addCharacterRangeToCharSet(RECharSet cs, char c1, char c2) {
        int byteIndex1 = c1 / '\b';
        int byteIndex2 = c2 / '\b';
        if (c2 >= cs.length || c1 > c2) {
            throw ScriptRuntime.constructError("SyntaxError", "invalid range in character class");
        }
        char c3 = (char) (c1 & 7);
        char c4 = (char) (c2 & 7);
        if (byteIndex1 == byteIndex2) {
            byte[] bArr = cs.bits;
            bArr[byteIndex1] = (byte) (bArr[byteIndex1] | ((255 >> (7 - (c4 - c3))) << c3));
            return;
        }
        byte[] bArr2 = cs.bits;
        bArr2[byteIndex1] = (byte) (bArr2[byteIndex1] | (255 << c3));
        for (int i = byteIndex1 + 1; i < byteIndex2; i++) {
            cs.bits[i] = -1;
        }
        byte[] bArr3 = cs.bits;
        bArr3[byteIndex2] = (byte) (bArr3[byteIndex2] | (255 >> (7 - c4)));
    }

    private static void processCharSet(REGlobalData gData, RECharSet charSet) {
        synchronized (charSet) {
            if (!charSet.converted) {
                processCharSetImpl(gData, charSet);
                charSet.converted = true;
            }
        }
    }

    /* JADX WARN: Code duplicated, block: B:116:0x041a  */
    /* JADX WARN: Code duplicated, block: B:118:0x0420  */
    /* JADX WARN: Code duplicated, block: B:125:0x043a  */
    /* JADX WARN: Code duplicated, block: B:127:0x0455  */
    /* JADX WARN: Code duplicated, block: B:130:0x0462  */
    /* JADX WARN: Code duplicated, block: B:135:0x047a  */
    /* JADX WARN: Code duplicated, block: B:139:0x0494  */
    /* JADX WARN: Code duplicated, block: B:140:0x04a9  */
    /* JADX WARN: Code duplicated, block: B:154:0x0488 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:156:0x04b6 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:157:0x040e A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:178:0x006a A[ADDED_TO_REGION, REMOVE, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:191:0x0482 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:193:? A[LOOP:5: B:123:0x0433->B:193:?, LOOP_END, SYNTHETIC] */
    /*  JADX ERROR: JadxRuntimeException in pass: RegionMakerVisitor
        jadx.core.utils.exceptions.JadxRuntimeException: Not found exit edge by exit block: B:41:0x023a
        	at jadx.core.dex.visitors.regions.maker.LoopRegionMaker.checkLoopExits(LoopRegionMaker.java:272)
        	at jadx.core.dex.visitors.regions.maker.LoopRegionMaker.makeLoopRegion(LoopRegionMaker.java:237)
        	at jadx.core.dex.visitors.regions.maker.LoopRegionMaker.process(LoopRegionMaker.java:80)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.traverse(RegionMaker.java:92)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.makeRegion(RegionMaker.java:69)
        	at jadx.core.dex.visitors.regions.maker.SwitchRegionMaker.addCases(SwitchRegionMaker.java:127)
        	at jadx.core.dex.visitors.regions.maker.SwitchRegionMaker.process(SwitchRegionMaker.java:75)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.traverse(RegionMaker.java:115)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.makeRegion(RegionMaker.java:69)
        	at jadx.core.dex.visitors.regions.maker.SwitchRegionMaker.addCases(SwitchRegionMaker.java:127)
        	at jadx.core.dex.visitors.regions.maker.SwitchRegionMaker.process(SwitchRegionMaker.java:75)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.traverse(RegionMaker.java:115)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.makeRegion(RegionMaker.java:69)
        	at jadx.core.dex.visitors.regions.maker.LoopRegionMaker.process(LoopRegionMaker.java:162)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.traverse(RegionMaker.java:92)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.makeRegion(RegionMaker.java:69)
        	at jadx.core.dex.visitors.regions.maker.IfRegionMaker.process(IfRegionMaker.java:117)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.traverse(RegionMaker.java:109)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.makeRegion(RegionMaker.java:69)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.makeMthRegion(RegionMaker.java:49)
        	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:25)
        */
    private static void processCharSetImpl(org.mozilla.javascript.regexp.REGlobalData r5, org.mozilla.javascript.regexp.RECharSet r6) {
        /*
            Method dump skipped, instruction units count: 1234
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.mozilla.javascript.regexp.NativeRegExp.processCharSetImpl(org.mozilla.javascript.regexp.REGlobalData, org.mozilla.javascript.regexp.RECharSet):void");
    }

    private static boolean classMatcher(REGlobalData gData, RECharSet charSet, char ch) {
        if (!charSet.converted) {
            processCharSet(gData, charSet);
        }
        int byteIndex = ch >> 3;
        return (charSet.length == 0 || ch >= charSet.length || (charSet.bits[byteIndex] & (1 << (ch & 7))) == 0) ^ charSet.sense;
    }

    private static boolean reopIsSimple(int op) {
        return op >= 1 && op <= 23;
    }

    private static int simpleMatch(REGlobalData gData, String input, int op, byte[] program, int pc, int end, boolean updatecp) {
        char c;
        char c2;
        boolean result = false;
        int startcp = gData.cp;
        switch (op) {
            case 1:
                result = true;
                break;
            case 2:
                if (gData.cp == 0 || (gData.multiline && isLineTerm(input.charAt(gData.cp - 1)))) {
                    result = true;
                }
                break;
            case 3:
                if (gData.cp == end || (gData.multiline && isLineTerm(input.charAt(gData.cp)))) {
                    result = true;
                }
                break;
            case 4:
                result = (gData.cp == 0 || !isWord(input.charAt(gData.cp - 1))) ^ (gData.cp >= end || !isWord(input.charAt(gData.cp)));
                break;
            case 5:
                result = (gData.cp == 0 || !isWord(input.charAt(gData.cp - 1))) ^ (gData.cp < end && isWord(input.charAt(gData.cp)));
                break;
            case 6:
                if (gData.cp != end && !isLineTerm(input.charAt(gData.cp))) {
                    result = true;
                    gData.cp++;
                }
                break;
            case 7:
                if (gData.cp != end && isDigit(input.charAt(gData.cp))) {
                    result = true;
                    gData.cp++;
                }
                break;
            case 8:
                if (gData.cp != end && !isDigit(input.charAt(gData.cp))) {
                    result = true;
                    gData.cp++;
                }
                break;
            case 9:
                if (gData.cp != end && isWord(input.charAt(gData.cp))) {
                    result = true;
                    gData.cp++;
                }
                break;
            case 10:
                if (gData.cp != end && !isWord(input.charAt(gData.cp))) {
                    result = true;
                    gData.cp++;
                }
                break;
            case 11:
                if (gData.cp != end && isREWhiteSpace(input.charAt(gData.cp))) {
                    result = true;
                    gData.cp++;
                }
                break;
            case 12:
                if (gData.cp != end && !isREWhiteSpace(input.charAt(gData.cp))) {
                    result = true;
                    gData.cp++;
                }
                break;
            case 13:
                int parenIndex = getIndex(program, pc);
                pc += 2;
                result = backrefMatcher(gData, parenIndex, input, end);
                break;
            case 14:
                int offset = getIndex(program, pc);
                int pc2 = pc + 2;
                int length = getIndex(program, pc2);
                pc = pc2 + 2;
                result = flatNMatcher(gData, offset, length, input, end);
                break;
            case 15:
                pc++;
                char matchCh = (char) (program[pc] & 255);
                if (gData.cp != end && input.charAt(gData.cp) == matchCh) {
                    result = true;
                    gData.cp++;
                }
                break;
            case 16:
                int offset2 = getIndex(program, pc);
                int pc3 = pc + 2;
                int length2 = getIndex(program, pc3);
                pc = pc3 + 2;
                result = flatNIMatcher(gData, offset2, length2, input, end);
                break;
            case 17:
                pc++;
                char matchCh2 = (char) (program[pc] & 255);
                if (gData.cp != end && (matchCh2 == (c2 = input.charAt(gData.cp)) || upcase(matchCh2) == upcase(c2))) {
                    result = true;
                    gData.cp++;
                }
                break;
            case 18:
                char matchCh3 = (char) getIndex(program, pc);
                pc += 2;
                if (gData.cp != end && input.charAt(gData.cp) == matchCh3) {
                    result = true;
                    gData.cp++;
                }
                break;
            case 19:
                char matchCh4 = (char) getIndex(program, pc);
                pc += 2;
                if (gData.cp != end && (matchCh4 == (c = input.charAt(gData.cp)) || upcase(matchCh4) == upcase(c))) {
                    result = true;
                    gData.cp++;
                }
                break;
            case 20:
            case 21:
            default:
                throw Kit.codeBug();
            case 22:
            case 23:
                int index = getIndex(program, pc);
                pc += 2;
                if (gData.cp != end && classMatcher(gData, gData.regexp.classList[index], input.charAt(gData.cp))) {
                    gData.cp++;
                    result = true;
                }
                break;
        }
        if (result) {
            if (!updatecp) {
                gData.cp = startcp;
            }
            return pc;
        }
        gData.cp = startcp;
        return -1;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code duplicated, block: B:218:0x020f A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:219:0x0200 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:220:0x01f0 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:223:0x0469 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:224:0x044c A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:225:0x0484 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:226:0x046e A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:49:0x01dd  */
    private static boolean executeREBytecode(REGlobalData gData, String input, int end) {
        int match;
        int min;
        int max;
        int nextpc;
        int match2;
        byte[] program = gData.regexp.program;
        int continuationOp = 57;
        int continuationPc = 0;
        boolean result = false;
        int pc = 0 + 1;
        int op = program[0];
        if (gData.regexp.anchorCh < 0 && reopIsSimple(op)) {
            boolean anchor = false;
            while (gData.cp <= end) {
                int match3 = simpleMatch(gData, input, op, program, pc, end, true);
                if (match3 >= 0) {
                    anchor = true;
                    pc = match3 + 1;
                    op = program[match3];
                    break;
                }
                gData.skipped++;
                gData.cp++;
            }
            if (!anchor) {
                return false;
            }
        }
        while (true) {
            if (reopIsSimple(op)) {
                int match4 = simpleMatch(gData, input, op, program, pc, end, true);
                result = match4 >= 0;
                if (result) {
                    pc = match4;
                }
            } else {
                switch (op) {
                    case 25:
                    case 26:
                    case 27:
                    case 28:
                    case 45:
                    case 46:
                    case 47:
                    case 48:
                        boolean greedy = false;
                        switch (op) {
                            case 25:
                                greedy = true;
                                min = getOffset(program, pc);
                                int pc2 = pc + 2;
                                max = getOffset(program, pc2) - 1;
                                pc = pc2 + 2;
                                pushProgState(gData, min, max, gData.cp, null, continuationOp, continuationPc);
                                if (greedy) {
                                    pushBackTrackState(gData, (byte) 51, pc);
                                    continuationOp = 51;
                                    continuationPc = pc;
                                    int pc3 = pc + 6;
                                    pc = pc3 + 1;
                                    op = program[pc3];
                                } else if (min != 0) {
                                    continuationOp = 52;
                                    continuationPc = pc;
                                    int pc4 = pc + 6;
                                    pc = pc4 + 1;
                                    op = program[pc4];
                                } else {
                                    pushBackTrackState(gData, (byte) 52, pc);
                                    popProgState(gData);
                                    int pc5 = pc + 4;
                                    int pc6 = pc5 + getOffset(program, pc5);
                                    pc = pc6 + 1;
                                    op = program[pc6];
                                    continue;
                                }
                                break;
                            case 26:
                                greedy = true;
                                min = 0;
                                max = -1;
                                pushProgState(gData, min, max, gData.cp, null, continuationOp, continuationPc);
                                if (greedy) {
                                    pushBackTrackState(gData, (byte) 51, pc);
                                    continuationOp = 51;
                                    continuationPc = pc;
                                    int pc7 = pc + 6;
                                    pc = pc7 + 1;
                                    op = program[pc7];
                                } else if (min != 0) {
                                    continuationOp = 52;
                                    continuationPc = pc;
                                    int pc8 = pc + 6;
                                    pc = pc8 + 1;
                                    op = program[pc8];
                                } else {
                                    pushBackTrackState(gData, (byte) 52, pc);
                                    popProgState(gData);
                                    int pc9 = pc + 4;
                                    int pc10 = pc9 + getOffset(program, pc9);
                                    pc = pc10 + 1;
                                    op = program[pc10];
                                    continue;
                                }
                                break;
                            case 27:
                                greedy = true;
                                min = 1;
                                max = -1;
                                pushProgState(gData, min, max, gData.cp, null, continuationOp, continuationPc);
                                if (greedy) {
                                    pushBackTrackState(gData, (byte) 51, pc);
                                    continuationOp = 51;
                                    continuationPc = pc;
                                    int pc11 = pc + 6;
                                    pc = pc11 + 1;
                                    op = program[pc11];
                                } else if (min != 0) {
                                    continuationOp = 52;
                                    continuationPc = pc;
                                    int pc12 = pc + 6;
                                    pc = pc12 + 1;
                                    op = program[pc12];
                                } else {
                                    pushBackTrackState(gData, (byte) 52, pc);
                                    popProgState(gData);
                                    int pc13 = pc + 4;
                                    int pc14 = pc13 + getOffset(program, pc13);
                                    pc = pc14 + 1;
                                    op = program[pc14];
                                    continue;
                                }
                                break;
                            case 28:
                                greedy = true;
                                min = 0;
                                max = 1;
                                pushProgState(gData, min, max, gData.cp, null, continuationOp, continuationPc);
                                if (greedy) {
                                    pushBackTrackState(gData, (byte) 51, pc);
                                    continuationOp = 51;
                                    continuationPc = pc;
                                    int pc15 = pc + 6;
                                    pc = pc15 + 1;
                                    op = program[pc15];
                                } else if (min != 0) {
                                    continuationOp = 52;
                                    continuationPc = pc;
                                    int pc16 = pc + 6;
                                    pc = pc16 + 1;
                                    op = program[pc16];
                                } else {
                                    pushBackTrackState(gData, (byte) 52, pc);
                                    popProgState(gData);
                                    int pc17 = pc + 4;
                                    int pc18 = pc17 + getOffset(program, pc17);
                                    pc = pc18 + 1;
                                    op = program[pc18];
                                    continue;
                                }
                                break;
                            case 29:
                            case 30:
                            case 31:
                            case 32:
                            case 33:
                            case 34:
                            case 35:
                            case 36:
                            case 37:
                            case 38:
                            case 39:
                            case 40:
                            case 41:
                            case 42:
                            case 43:
                            case 44:
                            default:
                                throw Kit.codeBug();
                            case 45:
                                min = 0;
                                max = -1;
                                pushProgState(gData, min, max, gData.cp, null, continuationOp, continuationPc);
                                if (greedy) {
                                    pushBackTrackState(gData, (byte) 51, pc);
                                    continuationOp = 51;
                                    continuationPc = pc;
                                    int pc19 = pc + 6;
                                    pc = pc19 + 1;
                                    op = program[pc19];
                                } else if (min != 0) {
                                    continuationOp = 52;
                                    continuationPc = pc;
                                    int pc110 = pc + 6;
                                    pc = pc110 + 1;
                                    op = program[pc110];
                                } else {
                                    pushBackTrackState(gData, (byte) 52, pc);
                                    popProgState(gData);
                                    int pc111 = pc + 4;
                                    int pc112 = pc111 + getOffset(program, pc111);
                                    pc = pc112 + 1;
                                    op = program[pc112];
                                    continue;
                                }
                                break;
                            case 46:
                                min = 1;
                                max = -1;
                                pushProgState(gData, min, max, gData.cp, null, continuationOp, continuationPc);
                                if (greedy) {
                                    pushBackTrackState(gData, (byte) 51, pc);
                                    continuationOp = 51;
                                    continuationPc = pc;
                                    int pc113 = pc + 6;
                                    pc = pc113 + 1;
                                    op = program[pc113];
                                } else if (min != 0) {
                                    continuationOp = 52;
                                    continuationPc = pc;
                                    int pc114 = pc + 6;
                                    pc = pc114 + 1;
                                    op = program[pc114];
                                } else {
                                    pushBackTrackState(gData, (byte) 52, pc);
                                    popProgState(gData);
                                    int pc115 = pc + 4;
                                    int pc116 = pc115 + getOffset(program, pc115);
                                    pc = pc116 + 1;
                                    op = program[pc116];
                                    continue;
                                }
                                break;
                            case 47:
                                min = 0;
                                max = 1;
                                pushProgState(gData, min, max, gData.cp, null, continuationOp, continuationPc);
                                if (greedy) {
                                    pushBackTrackState(gData, (byte) 51, pc);
                                    continuationOp = 51;
                                    continuationPc = pc;
                                    int pc117 = pc + 6;
                                    pc = pc117 + 1;
                                    op = program[pc117];
                                } else if (min != 0) {
                                    continuationOp = 52;
                                    continuationPc = pc;
                                    int pc118 = pc + 6;
                                    pc = pc118 + 1;
                                    op = program[pc118];
                                } else {
                                    pushBackTrackState(gData, (byte) 52, pc);
                                    popProgState(gData);
                                    int pc119 = pc + 4;
                                    int pc1110 = pc119 + getOffset(program, pc119);
                                    pc = pc1110 + 1;
                                    op = program[pc1110];
                                    continue;
                                }
                                break;
                            case 48:
                                min = getOffset(program, pc);
                                int pc20 = pc + 2;
                                max = getOffset(program, pc20) - 1;
                                pc = pc20 + 2;
                                pushProgState(gData, min, max, gData.cp, null, continuationOp, continuationPc);
                                if (greedy) {
                                    pushBackTrackState(gData, (byte) 51, pc);
                                    continuationOp = 51;
                                    continuationPc = pc;
                                    int pc1111 = pc + 6;
                                    pc = pc1111 + 1;
                                    op = program[pc1111];
                                } else if (min != 0) {
                                    continuationOp = 52;
                                    continuationPc = pc;
                                    int pc1112 = pc + 6;
                                    pc = pc1112 + 1;
                                    op = program[pc1112];
                                } else {
                                    pushBackTrackState(gData, (byte) 52, pc);
                                    popProgState(gData);
                                    int pc1113 = pc + 4;
                                    int pc1114 = pc1113 + getOffset(program, pc1113);
                                    pc = pc1114 + 1;
                                    op = program[pc1114];
                                    continue;
                                }
                                break;
                        }
                        break;
                    case 29:
                        int parenIndex = getIndex(program, pc);
                        int pc21 = pc + 2;
                        gData.setParens(parenIndex, gData.cp, 0);
                        pc = pc21 + 1;
                        op = program[pc21];
                        continue;
                    case 30:
                        int parenIndex2 = getIndex(program, pc);
                        int pc22 = pc + 2;
                        int cap_index = gData.parensIndex(parenIndex2);
                        gData.setParens(parenIndex2, cap_index, gData.cp - cap_index);
                        pc = pc22 + 1;
                        op = program[pc22];
                        continue;
                    case 31:
                        nextpc = pc + getOffset(program, pc);
                        int pc23 = pc + 2;
                        pc = pc23 + 1;
                        op = program[pc23];
                        int startcp = gData.cp;
                        if (reopIsSimple(op)) {
                            match2 = simpleMatch(gData, input, op, program, pc, end, true);
                            if (match2 < 0) {
                                op = program[nextpc];
                                pc = nextpc + 1;
                            } else {
                                result = true;
                                pc = match2 + 1;
                                op = program[match2];
                            }
                            break;
                        }
                        int nextpc2 = nextpc + 1;
                        byte nextop = program[nextpc];
                        pushBackTrackState(gData, nextop, nextpc2, startcp, continuationOp, continuationPc);
                        break;
                    case 32:
                        int offset = getOffset(program, pc);
                        int pc24 = pc + offset;
                        pc = pc24 + 1;
                        op = program[pc24];
                        continue;
                    case 33:
                    case 34:
                    case 35:
                    case 36:
                    case 37:
                    case 38:
                    case 39:
                    case 40:
                    case 50:
                    case 56:
                    default:
                        throw Kit.codeBug("invalid bytecode");
                    case 41:
                        int nextpc3 = pc + getIndex(program, pc);
                        int pc25 = pc + 2;
                        pc = pc25 + 1;
                        op = program[pc25];
                        if (reopIsSimple(op) && simpleMatch(gData, input, op, program, pc, end, false) < 0) {
                            result = false;
                        } else {
                            pushProgState(gData, 0, 0, gData.cp, gData.backTrackStackTop, continuationOp, continuationPc);
                            pushBackTrackState(gData, (byte) 43, nextpc3);
                        }
                        break;
                    case 42:
                        int nextpc4 = pc + getIndex(program, pc);
                        int pc26 = pc + 2;
                        pc = pc26 + 1;
                        op = program[pc26];
                        if (reopIsSimple(op) && (match = simpleMatch(gData, input, op, program, pc, end, false)) >= 0 && program[match] == 44) {
                            result = false;
                        } else {
                            pushProgState(gData, 0, 0, gData.cp, gData.backTrackStackTop, continuationOp, continuationPc);
                            pushBackTrackState(gData, (byte) 44, nextpc4);
                        }
                        break;
                    case 43:
                    case 44:
                        REProgState state = popProgState(gData);
                        gData.cp = state.index;
                        gData.backTrackStackTop = state.backTrack;
                        continuationPc = state.continuationPc;
                        continuationOp = state.continuationOp;
                        if (op == 44) {
                            result = !result;
                        }
                        break;
                    case 49:
                        result = true;
                        pc = continuationPc;
                        op = continuationOp;
                        continue;
                    case 51:
                        while (true) {
                            REProgState state2 = popProgState(gData);
                            if (!result) {
                                if (state2.min == 0) {
                                    result = true;
                                }
                                continuationPc = state2.continuationPc;
                                continuationOp = state2.continuationOp;
                                int pc27 = pc + 4;
                                pc = pc27 + getOffset(program, pc27);
                            } else if (state2.min == 0 && gData.cp == state2.index) {
                                result = false;
                                continuationPc = state2.continuationPc;
                                continuationOp = state2.continuationOp;
                                int pc28 = pc + 4;
                                pc = pc28 + getOffset(program, pc28);
                            } else {
                                int new_min = state2.min;
                                int new_max = state2.max;
                                if (new_min != 0) {
                                    new_min--;
                                }
                                if (new_max != -1) {
                                    new_max--;
                                }
                                if (new_max == 0) {
                                    result = true;
                                    continuationPc = state2.continuationPc;
                                    continuationOp = state2.continuationOp;
                                    int pc29 = pc + 4;
                                    pc = pc29 + getOffset(program, pc29);
                                } else {
                                    int nextpc5 = pc + 6;
                                    byte b = program[nextpc5];
                                    int startcp2 = gData.cp;
                                    if (reopIsSimple(b)) {
                                        int match5 = simpleMatch(gData, input, b, program, nextpc5 + 1, end, true);
                                        if (match5 < 0) {
                                            result = new_min == 0;
                                            continuationPc = state2.continuationPc;
                                            continuationOp = state2.continuationOp;
                                            int pc30 = pc + 4;
                                            pc = pc30 + getOffset(program, pc30);
                                        } else {
                                            result = true;
                                            nextpc5 = match5;
                                        }
                                    }
                                    continuationOp = 51;
                                    continuationPc = pc;
                                    pushProgState(gData, new_min, new_max, startcp2, null, state2.continuationOp, state2.continuationPc);
                                    if (new_min == 0) {
                                        pushBackTrackState(gData, (byte) 51, pc, startcp2, state2.continuationOp, state2.continuationPc);
                                        int parenCount = getIndex(program, pc);
                                        int parenIndex3 = getIndex(program, pc + 2);
                                        for (int k = 0; k < parenCount; k++) {
                                            gData.setParens(parenIndex3 + k, -1, 0);
                                        }
                                    }
                                    if (program[nextpc5] != 49) {
                                        int pc31 = nextpc5;
                                        pc = pc31 + 1;
                                        op = program[pc31];
                                        break;
                                    }
                                }
                            }
                            break;
                        }
                        break;
                    case 52:
                        REProgState state3 = popProgState(gData);
                        if (!result) {
                            if (state3.max == -1 || state3.max > 0) {
                                pushProgState(gData, state3.min, state3.max, gData.cp, null, state3.continuationOp, state3.continuationPc);
                                continuationOp = 52;
                                continuationPc = pc;
                                int parenCount2 = getIndex(program, pc);
                                int pc32 = pc + 2;
                                int parenIndex4 = getIndex(program, pc32);
                                int pc33 = pc32 + 4;
                                for (int k2 = 0; k2 < parenCount2; k2++) {
                                    gData.setParens(parenIndex4 + k2, -1, 0);
                                }
                                pc = pc33 + 1;
                                op = program[pc33];
                            } else {
                                continuationPc = state3.continuationPc;
                                continuationOp = state3.continuationOp;
                            }
                            break;
                        } else if (state3.min == 0 && gData.cp == state3.index) {
                            result = false;
                            continuationPc = state3.continuationPc;
                            continuationOp = state3.continuationOp;
                            break;
                        } else {
                            int new_min2 = state3.min;
                            int new_max2 = state3.max;
                            if (new_min2 != 0) {
                                new_min2--;
                            }
                            if (new_max2 != -1) {
                                new_max2--;
                            }
                            pushProgState(gData, new_min2, new_max2, gData.cp, null, state3.continuationOp, state3.continuationPc);
                            if (new_min2 != 0) {
                                continuationOp = 52;
                                continuationPc = pc;
                                int parenCount3 = getIndex(program, pc);
                                int pc34 = pc + 2;
                                int parenIndex5 = getIndex(program, pc34);
                                int pc35 = pc34 + 4;
                                for (int k3 = 0; k3 < parenCount3; k3++) {
                                    gData.setParens(parenIndex5 + k3, -1, 0);
                                }
                                pc = pc35 + 1;
                                op = program[pc35];
                            } else {
                                continuationPc = state3.continuationPc;
                                continuationOp = state3.continuationOp;
                                pushBackTrackState(gData, (byte) 52, pc);
                                popProgState(gData);
                                int pc36 = pc + 4;
                                int pc37 = pc36 + getOffset(program, pc36);
                                pc = pc37 + 1;
                                op = program[pc37];
                            }
                        }
                        break;
                    case 53:
                    case 54:
                    case 55:
                        char matchCh1 = (char) getIndex(program, pc);
                        int pc38 = pc + 2;
                        char matchCh2 = (char) getIndex(program, pc38);
                        pc = pc38 + 2;
                        if (gData.cp == end) {
                            result = false;
                        } else {
                            char c = input.charAt(gData.cp);
                            if (op == 55) {
                                if (c != matchCh1 && !classMatcher(gData, gData.regexp.classList[matchCh2], c)) {
                                    result = false;
                                } else {
                                    nextpc = pc + getOffset(program, pc);
                                    int pc210 = pc + 2;
                                    pc = pc210 + 1;
                                    op = program[pc210];
                                    int startcp3 = gData.cp;
                                    if (reopIsSimple(op)) {
                                        match2 = simpleMatch(gData, input, op, program, pc, end, true);
                                        if (match2 < 0) {
                                            op = program[nextpc];
                                            pc = nextpc + 1;
                                        } else {
                                            result = true;
                                            pc = match2 + 1;
                                            op = program[match2];
                                        }
                                    }
                                    int nextpc6 = nextpc + 1;
                                    byte nextop2 = program[nextpc];
                                    pushBackTrackState(gData, nextop2, nextpc6, startcp3, continuationOp, continuationPc);
                                }
                            } else {
                                if (op == 54) {
                                    c = upcase(c);
                                }
                                if (c != matchCh1 && c != matchCh2) {
                                    result = false;
                                } else {
                                    nextpc = pc + getOffset(program, pc);
                                    int pc211 = pc + 2;
                                    pc = pc211 + 1;
                                    op = program[pc211];
                                    int startcp4 = gData.cp;
                                    if (reopIsSimple(op)) {
                                        match2 = simpleMatch(gData, input, op, program, pc, end, true);
                                        if (match2 < 0) {
                                            op = program[nextpc];
                                            pc = nextpc + 1;
                                        } else {
                                            result = true;
                                            pc = match2 + 1;
                                            op = program[match2];
                                        }
                                    }
                                    int nextpc7 = nextpc + 1;
                                    byte nextop3 = program[nextpc];
                                    pushBackTrackState(gData, nextop3, nextpc7, startcp4, continuationOp, continuationPc);
                                }
                            }
                        }
                        break;
                    case 57:
                        return true;
                }
            }
            if (!result) {
                REBackTrackData backTrackData = gData.backTrackStackTop;
                if (backTrackData != null) {
                    gData.backTrackStackTop = backTrackData.previous;
                    gData.parens = backTrackData.parens;
                    gData.cp = backTrackData.cp;
                    gData.stateStackTop = backTrackData.stateStackTop;
                    continuationOp = backTrackData.continuationOp;
                    continuationPc = backTrackData.continuationPc;
                    pc = backTrackData.pc;
                    op = backTrackData.op;
                } else {
                    return false;
                }
            } else {
                int i = pc;
                pc++;
                op = program[i];
            }
        }
    }

    private static boolean matchRegExp(REGlobalData gData, RECompiled re, String input, int start, int end, boolean multiline) {
        if (re.parenCount != 0) {
            gData.parens = new long[re.parenCount];
        } else {
            gData.parens = null;
        }
        gData.backTrackStackTop = null;
        gData.stateStackTop = null;
        gData.multiline = multiline || (re.flags & 4) != 0;
        gData.regexp = re;
        int anchorCh = gData.regexp.anchorCh;
        int i = start;
        while (i <= end) {
            if (anchorCh >= 0) {
                while (i != end) {
                    char matchCh = input.charAt(i);
                    if (matchCh != anchorCh && ((gData.regexp.flags & 2) == 0 || upcase(matchCh) != upcase((char) anchorCh))) {
                        i++;
                    }
                }
                return false;
            }
            gData.cp = i;
            gData.skipped = i - start;
            for (int j = 0; j < re.parenCount; j++) {
                gData.parens[j] = -1;
            }
            boolean result = executeREBytecode(gData, input, end);
            gData.backTrackStackTop = null;
            gData.stateStackTop = null;
            if (result) {
                return true;
            }
            if (anchorCh == -2 && !gData.multiline) {
                gData.skipped = end;
                return false;
            }
            int i2 = start + gData.skipped;
            i = i2 + 1;
        }
        return false;
    }

    Object executeRegExp(Context cx, Scriptable scope, RegExpImpl res, String str, int[] indexp, int matchType) {
        Object result;
        Scriptable obj;
        REGlobalData gData = new REGlobalData();
        int start = indexp[0];
        int end = str.length();
        if (start > end) {
            start = end;
        }
        boolean matches = matchRegExp(gData, this.re, str, start, end, res.multiline);
        if (!matches) {
            if (matchType != 2) {
                return null;
            }
            return Undefined.instance;
        }
        int index = gData.cp;
        indexp[0] = index;
        int matchlen = index - (start + gData.skipped);
        int index2 = index - matchlen;
        if (matchType == 0) {
            result = Boolean.TRUE;
            obj = null;
        } else {
            result = cx.newArray(scope, 0);
            obj = (Scriptable) result;
            String matchstr = str.substring(index2, index2 + matchlen);
            obj.put(0, obj, matchstr);
        }
        if (this.re.parenCount == 0) {
            res.parens = null;
            res.lastParen = new SubString();
        } else {
            SubString parsub = null;
            res.parens = new SubString[this.re.parenCount];
            for (int num = 0; num < this.re.parenCount; num++) {
                int cap_index = gData.parensIndex(num);
                if (cap_index != -1) {
                    int cap_length = gData.parensLength(num);
                    parsub = new SubString(str, cap_index, cap_length);
                    res.parens[num] = parsub;
                    if (matchType != 0) {
                        obj.put(num + 1, obj, parsub.toString());
                    }
                } else if (matchType != 0) {
                    obj.put(num + 1, obj, Undefined.instance);
                }
            }
            res.lastParen = parsub;
        }
        if (matchType != 0) {
            obj.put("index", obj, Integer.valueOf(start + gData.skipped));
            obj.put("input", obj, str);
        }
        if (res.lastMatch == null) {
            res.lastMatch = new SubString();
            res.leftContext = new SubString();
            res.rightContext = new SubString();
        }
        res.lastMatch.str = str;
        res.lastMatch.index = index2;
        res.lastMatch.length = matchlen;
        res.leftContext.str = str;
        if (cx.getLanguageVersion() == 120) {
            res.leftContext.index = start;
            res.leftContext.length = gData.skipped;
        } else {
            res.leftContext.index = 0;
            res.leftContext.length = start + gData.skipped;
        }
        res.rightContext.str = str;
        res.rightContext.index = index;
        res.rightContext.length = end - index;
        return result;
    }

    int getFlags() {
        return this.re.flags;
    }

    private static void reportWarning(Context cx, String messageId, String arg) {
        if (cx.hasFeature(11)) {
            String msg = ScriptRuntime.getMessage1(messageId, arg);
            Context.reportWarning(msg);
        }
    }

    private static void reportError(String messageId, String arg) {
        String msg = ScriptRuntime.getMessage1(messageId, arg);
        throw ScriptRuntime.constructError("SyntaxError", msg);
    }

    @Override // org.mozilla.javascript.IdScriptableObject
    protected int getMaxInstanceId() {
        return 5;
    }

    @Override // org.mozilla.javascript.IdScriptableObject
    protected int findInstanceIdInfo(String s) {
        int attr;
        int id = 0;
        String X = null;
        int s_length = s.length();
        if (s_length == 6) {
            int c = s.charAt(0);
            if (c == 103) {
                X = "global";
                id = 3;
            } else if (c == 115) {
                X = "source";
                id = 2;
            }
        } else if (s_length == 9) {
            int c2 = s.charAt(0);
            if (c2 == 108) {
                X = "lastIndex";
                id = 1;
            } else if (c2 == 109) {
                X = "multiline";
                id = 5;
            }
        } else if (s_length == 10) {
            X = "ignoreCase";
            id = 4;
        }
        if (X != null && X != s && !X.equals(s)) {
            id = 0;
        }
        if (id == 0) {
            return super.findInstanceIdInfo(s);
        }
        switch (id) {
            case 1:
                attr = this.lastIndexAttr;
                break;
            case 2:
            case 3:
            case 4:
            case 5:
                attr = 7;
                break;
            default:
                throw new IllegalStateException();
        }
        return instanceIdInfo(attr, id);
    }

    @Override // org.mozilla.javascript.IdScriptableObject
    protected String getInstanceIdName(int id) {
        switch (id) {
            case 1:
                return "lastIndex";
            case 2:
                return "source";
            case 3:
                return "global";
            case 4:
                return "ignoreCase";
            case 5:
                return "multiline";
            default:
                return super.getInstanceIdName(id);
        }
    }

    @Override // org.mozilla.javascript.IdScriptableObject
    protected Object getInstanceIdValue(int id) {
        switch (id) {
            case 1:
                return this.lastIndex;
            case 2:
                return new String(this.re.source);
            case 3:
                return ScriptRuntime.wrapBoolean((this.re.flags & 1) != 0);
            case 4:
                return ScriptRuntime.wrapBoolean((this.re.flags & 2) != 0);
            case 5:
                return ScriptRuntime.wrapBoolean((this.re.flags & 4) != 0);
            default:
                return super.getInstanceIdValue(id);
        }
    }

    private void setLastIndex(Object value) {
        if ((this.lastIndexAttr & 1) != 0) {
            throw ScriptRuntime.typeError1("msg.modify.readonly", "lastIndex");
        }
        this.lastIndex = value;
    }

    @Override // org.mozilla.javascript.IdScriptableObject
    protected void setInstanceIdValue(int id, Object value) {
        switch (id) {
            case 1:
                setLastIndex(value);
                break;
            case 2:
            case 3:
            case 4:
            case 5:
                break;
            default:
                super.setInstanceIdValue(id, value);
                break;
        }
    }

    @Override // org.mozilla.javascript.IdScriptableObject
    protected void setInstanceIdAttributes(int id, int attr) {
        switch (id) {
            case 1:
                this.lastIndexAttr = attr;
                break;
            default:
                super.setInstanceIdAttributes(id, attr);
                break;
        }
    }

    @Override // org.mozilla.javascript.IdScriptableObject
    protected void initPrototypeId(int id) {
        int arity;
        String s;
        if (id == 7) {
            initPrototypeMethod(REGEXP_TAG, id, SymbolKey.MATCH, "[Symbol.match]", 1);
            return;
        }
        if (id == 8) {
            initPrototypeMethod(REGEXP_TAG, id, SymbolKey.SEARCH, "[Symbol.search]", 1);
            return;
        }
        switch (id) {
            case 1:
                arity = 2;
                s = "compile";
                break;
            case 2:
                arity = 0;
                s = "toString";
                break;
            case 3:
                arity = 0;
                s = "toSource";
                break;
            case 4:
                arity = 1;
                s = "exec";
                break;
            case 5:
                arity = 1;
                s = "test";
                break;
            case 6:
                arity = 1;
                s = "prefix";
                break;
            default:
                throw new IllegalArgumentException(String.valueOf(id));
        }
        initPrototypeMethod(REGEXP_TAG, id, s, arity);
    }

    @Override // org.mozilla.javascript.IdScriptableObject, org.mozilla.javascript.IdFunctionCall
    public Object execIdCall(IdFunctionObject f, Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        if (!f.hasTag(REGEXP_TAG)) {
            return super.execIdCall(f, cx, scope, thisObj, args);
        }
        int id = f.methodId();
        switch (id) {
            case 1:
                return realThis(thisObj, f).compile(cx, scope, args);
            case 2:
            case 3:
                return realThis(thisObj, f).toString();
            case 4:
                return realThis(thisObj, f).execSub(cx, scope, args, 1);
            case 5:
                Object x = realThis(thisObj, f).execSub(cx, scope, args, 0);
                return Boolean.TRUE.equals(x) ? Boolean.TRUE : Boolean.FALSE;
            case 6:
                return realThis(thisObj, f).execSub(cx, scope, args, 2);
            case 7:
                return realThis(thisObj, f).execSub(cx, scope, args, 1);
            case 8:
                Scriptable scriptable = (Scriptable) realThis(thisObj, f).execSub(cx, scope, args, 1);
                return scriptable.get("index", scriptable);
            default:
                throw new IllegalArgumentException(String.valueOf(id));
        }
    }

    private static NativeRegExp realThis(Scriptable thisObj, IdFunctionObject f) {
        if (!(thisObj instanceof NativeRegExp)) {
            throw incompatibleCallError(f);
        }
        return (NativeRegExp) thisObj;
    }

    @Override // org.mozilla.javascript.IdScriptableObject
    protected int findPrototypeId(Symbol k) {
        if (SymbolKey.MATCH.equals(k)) {
            return 7;
        }
        if (SymbolKey.SEARCH.equals(k)) {
            return 8;
        }
        return 0;
    }

    @Override // org.mozilla.javascript.IdScriptableObject
    protected int findPrototypeId(String s) {
        int id = 0;
        String X = null;
        switch (s.length()) {
            case 4:
                int c = s.charAt(0);
                if (c == 101) {
                    X = "exec";
                    id = 4;
                } else if (c == 116) {
                    X = "test";
                    id = 5;
                }
                break;
            case 6:
                X = "prefix";
                id = 6;
                break;
            case 7:
                X = "compile";
                id = 1;
                break;
            case 8:
                int c2 = s.charAt(3);
                if (c2 == 111) {
                    X = "toSource";
                    id = 3;
                } else if (c2 == 116) {
                    X = "toString";
                    id = 2;
                }
                break;
        }
        if (X != null && X != s && !X.equals(s)) {
            id = 0;
        }
        return id;
    }
}
