package com.twelvemonkeys.util.regex;

import java.io.PrintStream;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/util/regex/WildcardStringParser.class */
@Deprecated
public class WildcardStringParser {
    public static final char[] ALPHABET = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 230, 248, 229, 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'N', 'M', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 198, 216, 197, '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '.', '_', '-'};
    public static final char FREE_RANGE_CHARACTER = '*';
    public static final char FREE_PASS_CHARACTER = '?';
    boolean initialized;
    String stringMask;
    WildcardStringParserState initialState;
    int totalNumberOfStringsParsed;
    boolean debugging;
    PrintStream out;

    public WildcardStringParser(String str) {
        this(str, false);
    }

    public WildcardStringParser(String str, boolean z) {
        this(str, z, System.out);
    }

    public WildcardStringParser(String str, boolean z, PrintStream printStream) {
        this.stringMask = str;
        this.debugging = z;
        this.out = printStream;
        this.initialized = buildAutomaton();
    }

    private boolean checkIfStateInWildcardRange(WildcardStringParserState wildcardStringParserState) {
        WildcardStringParserState wildcardStringParserState2 = wildcardStringParserState;
        while (wildcardStringParserState2.previousState != null) {
            wildcardStringParserState2 = wildcardStringParserState2.previousState;
            if (isFreeRangeCharacter(wildcardStringParserState2.character)) {
                return true;
            }
            if (!isFreePassCharacter(wildcardStringParserState2.character)) {
                return false;
            }
        }
        return false;
    }

    private boolean checkIfLastFreeRangeState(WildcardStringParserState wildcardStringParserState) {
        if (isFreeRangeCharacter(wildcardStringParserState.character)) {
            return true;
        }
        if (isFreePassCharacter(wildcardStringParserState.character) && checkIfStateInWildcardRange(wildcardStringParserState)) {
            return true;
        }
        return false;
    }

    private boolean isTrivialAutomaton() {
        for (int i = 0; i < this.stringMask.length(); i++) {
            if (!isFreeRangeCharacter(this.stringMask.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    private boolean buildAutomaton() {
        WildcardStringParserState wildcardStringParserState = null;
        if (this.stringMask != null && this.stringMask.length() > 0) {
            WildcardStringParserState wildcardStringParserState2 = new WildcardStringParserState(this.stringMask.charAt(0));
            wildcardStringParserState2.automatonStateNumber = 0;
            wildcardStringParserState2.previousState = null;
            if (checkIfLastFreeRangeState(wildcardStringParserState2)) {
                wildcardStringParserState = wildcardStringParserState2;
            }
            WildcardStringParserState wildcardStringParserState3 = wildcardStringParserState2;
            this.initialState = wildcardStringParserState3;
            this.initialState.automatonStateNumber = 0;
            for (int i = 1; i < this.stringMask.length(); i++) {
                char cCharAt = this.stringMask.charAt(i);
                if (!isInAlphabet(cCharAt) && !isWildcardCharacter(cCharAt)) {
                    System.err.println("one or more characters in string mask are not legal characters - aborting!");
                    return false;
                }
                wildcardStringParserState3.lastFreeRangeState = wildcardStringParserState;
                WildcardStringParserState wildcardStringParserState4 = new WildcardStringParserState(cCharAt);
                wildcardStringParserState4.automatonStateNumber = i;
                wildcardStringParserState4.previousState = wildcardStringParserState3;
                if (checkIfLastFreeRangeState(wildcardStringParserState4)) {
                    wildcardStringParserState = wildcardStringParserState4;
                }
                wildcardStringParserState3.nextState = wildcardStringParserState4;
                wildcardStringParserState3 = wildcardStringParserState4;
                if (wildcardStringParserState3.automatonStateNumber == this.stringMask.length() - 1) {
                    wildcardStringParserState3.lastFreeRangeState = wildcardStringParserState;
                }
            }
            this.totalNumberOfStringsParsed = 0;
            return true;
        }
        System.err.println("string mask provided are null or empty - aborting!");
        return false;
    }

    public static boolean isInAlphabet(char c) {
        for (int i = 0; i < ALPHABET.length; i++) {
            if (c == ALPHABET[i]) {
                return true;
            }
        }
        return false;
    }

    public static boolean isFreeRangeCharacter(char c) {
        return c == '*';
    }

    public static boolean isFreePassCharacter(char c) {
        return c == '?';
    }

    public static boolean isWildcardCharacter(char c) {
        return isFreeRangeCharacter(c) || isFreePassCharacter(c);
    }

    public String getStringMask() {
        return this.stringMask;
    }

    public boolean parseString(String str) {
        if (this.debugging) {
            this.out.println("parsing \"" + str + "\"...");
        }
        this.totalNumberOfStringsParsed++;
        if (str == null) {
            if (this.debugging) {
                this.out.println("string to be parsed is null - rejection!");
                return false;
            }
            return false;
        }
        ParsableString parsableString = new ParsableString(str);
        if (!parsableString.checkString()) {
            if (this.debugging) {
                this.out.println("one or more characters in string to be parsed are not legal characters - rejection!");
                return false;
            }
            return false;
        }
        if (!this.initialized) {
            System.err.println("automaton is not initialized - rejection!");
            return false;
        }
        if (isTrivialAutomaton()) {
            if (this.debugging) {
                this.out.println("automaton represents a trivial string mask (accepts all strings) - acceptance!");
                return true;
            }
            return true;
        }
        if (parsableString.isEmpty()) {
            if (this.debugging) {
                this.out.println("string to be parsed is empty and not trivial automaton - rejection!");
                return false;
            }
            return false;
        }
        boolean z = false;
        int i = 0;
        int i2 = 0;
        if (parsableString.charArray[0] == this.initialState.character || isWildcardCharacter(this.initialState.character)) {
            WildcardStringParserState wildcardStringParserState = this.initialState;
            parsableString.index = 0;
            if (isFreePassCharacter(wildcardStringParserState.character)) {
                i = 0 + 1;
            }
            for (int i3 = 0; i3 < parsableString.length(); i3++) {
                if (this.debugging) {
                    this.out.println();
                }
                if (this.debugging) {
                    this.out.println("parsing - index number " + i3 + ", active char: '" + parsableString.getActiveChar() + "' char string index: " + parsableString.index + " number of chars since last free-range state: " + i2);
                }
                if (this.debugging) {
                    this.out.println("parsing - state: " + wildcardStringParserState.automatonStateNumber + " '" + wildcardStringParserState.character + "' - no of free-pass chars read: " + i);
                }
                if (this.debugging) {
                    this.out.println("parsing - hasPerformedFreeRangeMovement: " + z);
                }
                if (wildcardStringParserState.nextState == null) {
                    if (this.debugging) {
                        this.out.println("parsing - runnerState.nextState == null");
                    }
                    if (isFreeRangeCharacter(wildcardStringParserState.character)) {
                        if (z) {
                            if (parsableString.reachedEndOfString()) {
                                if (i > i2) {
                                    if (this.debugging) {
                                        this.out.println("no subsequent state (final state) and the state represents '*' - end of parsing string, but not enough characters read - rejection!");
                                        return false;
                                    }
                                    return false;
                                }
                                if (this.debugging) {
                                    this.out.println("no subsequent state (final state) and the state represents '*' - end of parsing string and enough characters read - acceptance!");
                                    return true;
                                }
                                return true;
                            }
                            if (i > i2) {
                                if (this.debugging) {
                                    this.out.println("no subsequent state (final state) and the state represents '*' - not the end of parsing string and not enough characters read - read next character");
                                }
                                parsableString.index++;
                                i2++;
                            } else {
                                if (this.debugging) {
                                    this.out.println("no subsequent state (final state) and the state represents '*' - not the end of parsing string, but enough characters read - acceptance!");
                                    return true;
                                }
                                return true;
                            }
                        } else {
                            if (this.debugging) {
                                this.out.println("no subsequent state (final state) and the state represents '*' - no skipping performed - acceptance!");
                                return true;
                            }
                            return true;
                        }
                    } else {
                        if (parsableString.reachedEndOfString()) {
                            if (z && i > i2) {
                                if (this.debugging) {
                                    this.out.println("no subsequent state (final state) and skipping has been performed and end of parsing string, but not enough characters read - rejection!");
                                    return false;
                                }
                                return false;
                            }
                            if (this.debugging) {
                                this.out.println("no subsequent state (final state) and the end of the string to test is reached - acceptance!");
                                return true;
                            }
                            return true;
                        }
                        if (this.debugging) {
                            this.out.println("parsing - escaping process...");
                        }
                    }
                } else {
                    if (this.debugging) {
                        this.out.println("parsing - runnerState.nextState != null");
                    }
                    if (isFreeRangeCharacter(wildcardStringParserState.character)) {
                        i = 0;
                        i2 = 0;
                        WildcardStringParserState wildcardStringParserState2 = wildcardStringParserState.nextState;
                        while (true) {
                            WildcardStringParserState wildcardStringParserState3 = wildcardStringParserState2;
                            if (wildcardStringParserState3 == null || !isFreePassCharacter(wildcardStringParserState3.character)) {
                                break;
                            }
                            wildcardStringParserState = wildcardStringParserState3;
                            z = true;
                            i++;
                            wildcardStringParserState2 = wildcardStringParserState3.nextState;
                        }
                        if (wildcardStringParserState.nextState == null) {
                            if (this.debugging) {
                                this.out.println();
                            }
                            if (this.debugging) {
                                this.out.println("parsing - index number " + i3 + ", active char: '" + parsableString.getActiveChar() + "' char string index: " + parsableString.index + " number of chars since last free-range state: 0");
                            }
                            if (this.debugging) {
                                this.out.println("parsing - state: " + wildcardStringParserState.automatonStateNumber + " '" + wildcardStringParserState.character + "' - no of free-pass chars read: " + i);
                            }
                            if (this.debugging) {
                                this.out.println("parsing - hasPerformedFreeRangeMovement: " + z);
                            }
                            if (z && i >= 0) {
                                return true;
                            }
                            return false;
                        }
                    }
                    if (isFreeRangeCharacter(wildcardStringParserState.nextState.character)) {
                        wildcardStringParserState = wildcardStringParserState.nextState;
                        parsableString.index++;
                        i2++;
                    } else if (isFreePassCharacter(wildcardStringParserState.nextState.character)) {
                        wildcardStringParserState = wildcardStringParserState.nextState;
                        parsableString.index++;
                        i++;
                        i2++;
                    } else if (!parsableString.reachedEndOfString() && wildcardStringParserState.nextState.character == parsableString.getSubsequentChar()) {
                        wildcardStringParserState = wildcardStringParserState.nextState;
                        parsableString.index++;
                        i2++;
                    } else if (wildcardStringParserState.lastFreeRangeState != null) {
                        wildcardStringParserState = wildcardStringParserState.lastFreeRangeState;
                        parsableString.index++;
                        i2++;
                    } else {
                        if (this.debugging) {
                            this.out.println("the next state does not represent the same character as the next character in the string to test, and there are no last-free-range-state - rejection!");
                            return false;
                        }
                        return false;
                    }
                }
            }
            if (this.debugging) {
                this.out.println("finished reading parsing string and not at any final state - rejection!");
                return false;
            }
            return false;
        }
        if (this.debugging) {
            this.out.println("cannot enter first automaton state - rejection!");
            return false;
        }
        return false;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (!this.initialized) {
            sb.append(getClass().getName());
            sb.append(":  Not initialized properly!");
            sb.append("\n");
            sb.append("\n");
        } else {
            sb.append(getClass().getName());
            sb.append(":  String mask ");
            sb.append(this.stringMask);
            sb.append("\n");
            sb.append("\n");
            sb.append("      Automaton: ");
            for (WildcardStringParserState wildcardStringParserState = this.initialState; wildcardStringParserState != null; wildcardStringParserState = wildcardStringParserState.nextState) {
                sb.append(wildcardStringParserState.automatonStateNumber);
                sb.append(": ");
                sb.append(wildcardStringParserState.character);
                sb.append(" (");
                if (wildcardStringParserState.lastFreeRangeState != null) {
                    sb.append(wildcardStringParserState.lastFreeRangeState.automatonStateNumber);
                } else {
                    sb.append("-");
                }
                sb.append(")");
                if (wildcardStringParserState.nextState != null) {
                    sb.append("   -->   ");
                }
            }
            sb.append("\n");
            sb.append("      Format: <state index>: <character> (<last free state>)");
            sb.append("\n");
            sb.append("      Number of strings parsed: ").append(this.totalNumberOfStringsParsed);
            sb.append("\n");
        }
        return sb.toString();
    }

    public boolean equals(Object obj) {
        if (obj instanceof WildcardStringParser) {
            WildcardStringParser wildcardStringParser = (WildcardStringParser) obj;
            return wildcardStringParser.initialized == this.initialized && wildcardStringParser.stringMask == this.stringMask;
        }
        return super.equals(obj);
    }

    public int hashCode() {
        return super.hashCode();
    }

    protected Object clone() throws CloneNotSupportedException {
        if (this.initialized) {
            return new WildcardStringParser(this.stringMask);
        }
        return null;
    }

    protected void finalize() throws Throwable {
    }

    /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/util/regex/WildcardStringParser$WildcardStringParserState.class */
    class WildcardStringParserState {
        int automatonStateNumber;
        char character;
        WildcardStringParserState previousState;
        WildcardStringParserState nextState;
        WildcardStringParserState lastFreeRangeState;

        public WildcardStringParserState(char c) {
            this.character = c;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/util/regex/WildcardStringParser$ParsableString.class */
    class ParsableString {
        char[] charArray;
        int index;

        ParsableString(String str) {
            if (str != null) {
                this.charArray = str.toCharArray();
            }
            this.index = -1;
        }

        boolean reachedEndOfString() {
            return this.index == this.charArray.length - 1;
        }

        int length() {
            return this.charArray.length;
        }

        char getActiveChar() {
            if (this.index > -1 && this.index < this.charArray.length) {
                return this.charArray[this.index];
            }
            System.err.println(getClass().getName() + ": trying to access character outside character array!");
            return ' ';
        }

        char getSubsequentChar() {
            if (this.index > -1 && this.index + 1 < this.charArray.length) {
                return this.charArray[this.index + 1];
            }
            System.err.println(getClass().getName() + ": trying to access character outside character array!");
            return ' ';
        }

        boolean checkString() {
            if (!isEmpty()) {
                for (int i = 0; i < this.charArray.length; i++) {
                    if (!WildcardStringParser.isInAlphabet(this.charArray[i])) {
                        return false;
                    }
                }
                return true;
            }
            return true;
        }

        boolean isEmpty() {
            return this.charArray == null || this.charArray.length == 0;
        }

        public String toString() {
            return new String(this.charArray);
        }
    }
}
