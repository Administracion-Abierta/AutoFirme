package org.apache.oro.text.regex;

/* JADX INFO: loaded from: autofirma.jar:org/apache/oro/text/regex/OpCode.class */
final class OpCode {
    static final char _END = 0;
    static final char _BOL = 1;
    static final char _MBOL = 2;
    static final char _SBOL = 3;
    static final char _EOL = 4;
    static final char _MEOL = 5;
    static final char _SEOL = 6;
    static final char _ANY = 7;
    static final char _SANY = '\b';
    static final char _ANYOF = '\t';
    static final char _CURLY = '\n';
    static final char _CURLYX = 11;
    static final char _BRANCH = '\f';
    static final char _BACK = '\r';
    static final char _EXACTLY = 14;
    static final char _NOTHING = 15;
    static final char _STAR = 16;
    static final char _PLUS = 17;
    static final char _ALNUM = 18;
    static final char _NALNUM = 19;
    static final char _BOUND = 20;
    static final char _NBOUND = 21;
    static final char _SPACE = 22;
    static final char _NSPACE = 23;
    static final char _DIGIT = 24;
    static final char _NDIGIT = 25;
    static final char _REF = 26;
    static final char _OPEN = 27;
    static final char _CLOSE = 28;
    static final char _MINMOD = 29;
    static final char _GBOL = 30;
    static final char _IFMATCH = 31;
    static final char _UNLESSM = ' ';
    static final char _SUCCEED = '!';
    static final char _WHILEM = '\"';
    static final char _ANYOFUN = '#';
    static final char _NANYOFUN = '$';
    static final char _RANGE = '%';
    static final char _ALPHA = '&';
    static final char _BLANK = '\'';
    static final char _CNTRL = '(';
    static final char _GRAPH = ')';
    static final char _LOWER = '*';
    static final char _PRINT = '+';
    static final char _PUNCT = ',';
    static final char _UPPER = '-';
    static final char _XDIGIT = '.';
    static final char _OPCODE = '/';
    static final char _NOPCODE = '0';
    static final char _ONECHAR = '1';
    static final char _ALNUMC = '2';
    static final char _ASCII = '3';
    static final int[] _operandLength = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    static final char[] _opType = {0, 1, 1, 1, 4, 4, 4, 7, 7, '\t', '\n', '\n', '\f', '\r', 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 1, '\f', '\f', 0, '\"', '#', '$', '%', '&', '\'', '(', ')', '*', '+', ',', '-', '.', '/', '0', '1', '2', '3'};
    static final char[] _opLengthVaries = {'\f', '\r', 16, 17, '\n', 11, 26, '\"'};
    static final char[] _opLengthOne = {7, '\b', '\t', 18, 19, 22, 23, 24, 25, '#', '$', '&', '\'', '(', ')', '*', '+', ',', '-', '.', '/', '0', '1', '2', '3'};
    static final int _NULL_OFFSET = -1;
    static final char _NULL_POINTER = 0;

    private OpCode() {
    }

    static final int _getNextOffset(char[] program, int offset) {
        return program[offset + 1];
    }

    static final char _getArg1(char[] program, int offset) {
        return program[offset + 2];
    }

    static final char _getArg2(char[] program, int offset) {
        return program[offset + 3];
    }

    static final int _getOperand(int offset) {
        return offset + 2;
    }

    static final boolean _isInArray(char ch, char[] array, int strt) {
        int start = strt;
        while (start < array.length) {
            int i = start;
            start++;
            if (ch == array[i]) {
                return true;
            }
        }
        return false;
    }

    static final int _getNextOperator(int offset) {
        return offset + 2;
    }

    static final int _getPrevOperator(int offset) {
        return offset - 2;
    }

    static final int _getNext(char[] program, int offset) {
        int offs;
        if (program == null || (offs = _getNextOffset(program, offset)) == 0) {
            return -1;
        }
        if (program[offset] == '\r') {
            return offset - offs;
        }
        return offset + offs;
    }

    static final boolean _isWordCharacter(char token) {
        return Character.isLetterOrDigit(token) || token == '_';
    }
}
