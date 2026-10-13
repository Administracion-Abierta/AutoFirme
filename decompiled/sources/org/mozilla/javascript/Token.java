package org.mozilla.javascript;

import es.gob.afirma.standalone.configurator.common.PreferencesManager;
import org.apache.pdfbox.pdmodel.documentinterchange.taggedpdf.StandardStructureTypes;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/Token.class */
public class Token {
    public static final boolean printTrees = false;
    static final boolean printICode = false;
    static final boolean printNames = false;
    public static final int ERROR = -1;
    public static final int EOF = 0;
    public static final int EOL = 1;
    public static final int FIRST_BYTECODE_TOKEN = 2;
    public static final int ENTERWITH = 2;
    public static final int LEAVEWITH = 3;
    public static final int RETURN = 4;
    public static final int GOTO = 5;
    public static final int IFEQ = 6;
    public static final int IFNE = 7;
    public static final int SETNAME = 8;
    public static final int BITOR = 9;
    public static final int BITXOR = 10;
    public static final int BITAND = 11;
    public static final int EQ = 12;
    public static final int NE = 13;
    public static final int LT = 14;
    public static final int LE = 15;
    public static final int GT = 16;
    public static final int GE = 17;
    public static final int LSH = 18;
    public static final int RSH = 19;
    public static final int URSH = 20;
    public static final int ADD = 21;
    public static final int SUB = 22;
    public static final int MUL = 23;
    public static final int DIV = 24;
    public static final int MOD = 25;
    public static final int NOT = 26;
    public static final int BITNOT = 27;
    public static final int POS = 28;
    public static final int NEG = 29;
    public static final int NEW = 30;
    public static final int DELPROP = 31;
    public static final int TYPEOF = 32;
    public static final int GETPROP = 33;
    public static final int GETPROPNOWARN = 34;
    public static final int SETPROP = 35;
    public static final int GETELEM = 36;
    public static final int SETELEM = 37;
    public static final int CALL = 38;
    public static final int NAME = 39;
    public static final int NUMBER = 40;
    public static final int STRING = 41;
    public static final int NULL = 42;
    public static final int THIS = 43;
    public static final int FALSE = 44;
    public static final int TRUE = 45;
    public static final int SHEQ = 46;
    public static final int SHNE = 47;
    public static final int REGEXP = 48;
    public static final int BINDNAME = 49;
    public static final int THROW = 50;
    public static final int RETHROW = 51;
    public static final int IN = 52;
    public static final int INSTANCEOF = 53;
    public static final int LOCAL_LOAD = 54;
    public static final int GETVAR = 55;
    public static final int SETVAR = 56;
    public static final int CATCH_SCOPE = 57;
    public static final int ENUM_INIT_KEYS = 58;
    public static final int ENUM_INIT_VALUES = 59;
    public static final int ENUM_INIT_ARRAY = 60;
    public static final int ENUM_INIT_VALUES_IN_ORDER = 61;
    public static final int ENUM_NEXT = 62;
    public static final int ENUM_ID = 63;
    public static final int THISFN = 64;
    public static final int RETURN_RESULT = 65;
    public static final int ARRAYLIT = 66;
    public static final int OBJECTLIT = 67;
    public static final int GET_REF = 68;
    public static final int SET_REF = 69;
    public static final int DEL_REF = 70;
    public static final int REF_CALL = 71;
    public static final int REF_SPECIAL = 72;
    public static final int YIELD = 73;
    public static final int STRICT_SETNAME = 74;
    public static final int DEFAULTNAMESPACE = 75;
    public static final int ESCXMLATTR = 76;
    public static final int ESCXMLTEXT = 77;
    public static final int REF_MEMBER = 78;
    public static final int REF_NS_MEMBER = 79;
    public static final int REF_NAME = 80;
    public static final int REF_NS_NAME = 81;
    public static final int LAST_BYTECODE_TOKEN = 81;
    public static final int TRY = 82;
    public static final int SEMI = 83;
    public static final int LB = 84;
    public static final int RB = 85;
    public static final int LC = 86;
    public static final int RC = 87;
    public static final int LP = 88;
    public static final int RP = 89;
    public static final int COMMA = 90;
    public static final int ASSIGN = 91;
    public static final int ASSIGN_BITOR = 92;
    public static final int ASSIGN_BITXOR = 93;
    public static final int ASSIGN_BITAND = 94;
    public static final int ASSIGN_LSH = 95;
    public static final int ASSIGN_RSH = 96;
    public static final int ASSIGN_URSH = 97;
    public static final int ASSIGN_ADD = 98;
    public static final int ASSIGN_SUB = 99;
    public static final int ASSIGN_MUL = 100;
    public static final int ASSIGN_DIV = 101;
    public static final int ASSIGN_MOD = 102;
    public static final int FIRST_ASSIGN = 91;
    public static final int LAST_ASSIGN = 102;
    public static final int HOOK = 103;
    public static final int COLON = 104;
    public static final int OR = 105;
    public static final int AND = 106;
    public static final int INC = 107;
    public static final int DEC = 108;
    public static final int DOT = 109;
    public static final int FUNCTION = 110;
    public static final int EXPORT = 111;
    public static final int IMPORT = 112;
    public static final int IF = 113;
    public static final int ELSE = 114;
    public static final int SWITCH = 115;
    public static final int CASE = 116;
    public static final int DEFAULT = 117;
    public static final int WHILE = 118;
    public static final int DO = 119;
    public static final int FOR = 120;
    public static final int BREAK = 121;
    public static final int CONTINUE = 122;
    public static final int VAR = 123;
    public static final int WITH = 124;
    public static final int CATCH = 125;
    public static final int FINALLY = 126;
    public static final int VOID = 127;
    public static final int RESERVED = 128;
    public static final int EMPTY = 129;
    public static final int BLOCK = 130;
    public static final int LABEL = 131;
    public static final int TARGET = 132;
    public static final int LOOP = 133;
    public static final int EXPR_VOID = 134;
    public static final int EXPR_RESULT = 135;
    public static final int JSR = 136;
    public static final int SCRIPT = 137;
    public static final int TYPEOFNAME = 138;
    public static final int USE_STACK = 139;
    public static final int SETPROP_OP = 140;
    public static final int SETELEM_OP = 141;
    public static final int LOCAL_BLOCK = 142;
    public static final int SET_REF_OP = 143;
    public static final int DOTDOT = 144;
    public static final int COLONCOLON = 145;
    public static final int XML = 146;
    public static final int DOTQUERY = 147;
    public static final int XMLATTR = 148;
    public static final int XMLEND = 149;
    public static final int TO_OBJECT = 150;
    public static final int TO_DOUBLE = 151;
    public static final int GET = 152;
    public static final int SET = 153;
    public static final int LET = 154;
    public static final int CONST = 155;
    public static final int SETCONST = 156;
    public static final int SETCONSTVAR = 157;
    public static final int ARRAYCOMP = 158;
    public static final int LETEXPR = 159;
    public static final int WITHEXPR = 160;
    public static final int DEBUGGER = 161;
    public static final int COMMENT = 162;
    public static final int GENEXPR = 163;
    public static final int METHOD = 164;
    public static final int ARROW = 165;
    public static final int YIELD_STAR = 166;
    public static final int LAST_TOKEN = 167;

    /* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/Token$CommentType.class */
    public enum CommentType {
        LINE,
        BLOCK_COMMENT,
        JSDOC,
        HTML
    }

    public static String name(int token) {
        return String.valueOf(token);
    }

    public static String typeToName(int token) {
        switch (token) {
            case -1:
                return "ERROR";
            case 0:
                return "EOF";
            case 1:
                return "EOL";
            case 2:
                return "ENTERWITH";
            case 3:
                return "LEAVEWITH";
            case 4:
                return "RETURN";
            case 5:
                return "GOTO";
            case 6:
                return "IFEQ";
            case 7:
                return "IFNE";
            case 8:
                return "SETNAME";
            case 9:
                return "BITOR";
            case 10:
                return "BITXOR";
            case 11:
                return "BITAND";
            case 12:
                return "EQ";
            case 13:
                return "NE";
            case 14:
                return "LT";
            case 15:
                return "LE";
            case 16:
                return "GT";
            case 17:
                return "GE";
            case 18:
                return "LSH";
            case 19:
                return "RSH";
            case 20:
                return "URSH";
            case 21:
                return "ADD";
            case 22:
                return "SUB";
            case 23:
                return "MUL";
            case 24:
                return "DIV";
            case 25:
                return "MOD";
            case 26:
                return "NOT";
            case 27:
                return "BITNOT";
            case 28:
                return "POS";
            case 29:
                return "NEG";
            case 30:
                return "NEW";
            case 31:
                return "DELPROP";
            case 32:
                return "TYPEOF";
            case 33:
                return "GETPROP";
            case 34:
                return "GETPROPNOWARN";
            case 35:
                return "SETPROP";
            case 36:
                return "GETELEM";
            case 37:
                return "SETELEM";
            case 38:
                return "CALL";
            case 39:
                return "NAME";
            case 40:
                return "NUMBER";
            case 41:
                return "STRING";
            case 42:
                return "NULL";
            case 43:
                return "THIS";
            case 44:
                return "FALSE";
            case 45:
                return "TRUE";
            case 46:
                return "SHEQ";
            case 47:
                return "SHNE";
            case 48:
                return "REGEXP";
            case 49:
                return "BINDNAME";
            case 50:
                return "THROW";
            case 51:
                return "RETHROW";
            case 52:
                return "IN";
            case 53:
                return "INSTANCEOF";
            case 54:
                return "LOCAL_LOAD";
            case 55:
                return "GETVAR";
            case 56:
                return "SETVAR";
            case 57:
                return "CATCH_SCOPE";
            case 58:
                return "ENUM_INIT_KEYS";
            case 59:
                return "ENUM_INIT_VALUES";
            case 60:
                return "ENUM_INIT_ARRAY";
            case 61:
                return "ENUM_INIT_VALUES_IN_ORDER";
            case 62:
                return "ENUM_NEXT";
            case 63:
                return "ENUM_ID";
            case 64:
                return "THISFN";
            case 65:
                return "RETURN_RESULT";
            case 66:
                return "ARRAYLIT";
            case 67:
                return "OBJECTLIT";
            case 68:
                return "GET_REF";
            case 69:
                return "SET_REF";
            case 70:
                return "DEL_REF";
            case 71:
                return "REF_CALL";
            case 72:
                return "REF_SPECIAL";
            case 73:
                return "YIELD";
            case 74:
            case 157:
            default:
                throw new IllegalStateException(String.valueOf(token));
            case 75:
                return "DEFAULTNAMESPACE";
            case 76:
                return "ESCXMLATTR";
            case 77:
                return "ESCXMLTEXT";
            case 78:
                return "REF_MEMBER";
            case 79:
                return "REF_NS_MEMBER";
            case 80:
                return "REF_NAME";
            case 81:
                return "REF_NS_NAME";
            case 82:
                return "TRY";
            case 83:
                return "SEMI";
            case 84:
                return "LB";
            case 85:
                return StandardStructureTypes.RB;
            case 86:
                return "LC";
            case 87:
                return "RC";
            case 88:
                return "LP";
            case 89:
                return StandardStructureTypes.RP;
            case 90:
                return "COMMA";
            case 91:
                return "ASSIGN";
            case 92:
                return "ASSIGN_BITOR";
            case 93:
                return "ASSIGN_BITXOR";
            case 94:
                return "ASSIGN_BITAND";
            case 95:
                return "ASSIGN_LSH";
            case 96:
                return "ASSIGN_RSH";
            case 97:
                return "ASSIGN_URSH";
            case 98:
                return "ASSIGN_ADD";
            case 99:
                return "ASSIGN_SUB";
            case 100:
                return "ASSIGN_MUL";
            case 101:
                return "ASSIGN_DIV";
            case 102:
                return "ASSIGN_MOD";
            case 103:
                return "HOOK";
            case 104:
                return "COLON";
            case 105:
                return "OR";
            case 106:
                return "AND";
            case 107:
                return "INC";
            case 108:
                return "DEC";
            case 109:
                return "DOT";
            case 110:
                return "FUNCTION";
            case 111:
                return "EXPORT";
            case 112:
                return "IMPORT";
            case 113:
                return "IF";
            case 114:
                return "ELSE";
            case 115:
                return "SWITCH";
            case 116:
                return "CASE";
            case 117:
                return "DEFAULT";
            case 118:
                return "WHILE";
            case 119:
                return "DO";
            case 120:
                return "FOR";
            case 121:
                return "BREAK";
            case 122:
                return "CONTINUE";
            case 123:
                return "VAR";
            case 124:
                return "WITH";
            case 125:
                return "CATCH";
            case 126:
                return "FINALLY";
            case 127:
                return "VOID";
            case 128:
                return "RESERVED";
            case 129:
                return "EMPTY";
            case 130:
                return "BLOCK";
            case 131:
                return "LABEL";
            case 132:
                return "TARGET";
            case 133:
                return "LOOP";
            case 134:
                return "EXPR_VOID";
            case 135:
                return "EXPR_RESULT";
            case 136:
                return "JSR";
            case 137:
                return "SCRIPT";
            case 138:
                return "TYPEOFNAME";
            case 139:
                return "USE_STACK";
            case 140:
                return "SETPROP_OP";
            case 141:
                return "SETELEM_OP";
            case 142:
                return "LOCAL_BLOCK";
            case 143:
                return "SET_REF_OP";
            case 144:
                return "DOTDOT";
            case 145:
                return "COLONCOLON";
            case 146:
                return "XML";
            case 147:
                return "DOTQUERY";
            case 148:
                return "XMLATTR";
            case 149:
                return "XMLEND";
            case 150:
                return "TO_OBJECT";
            case 151:
                return "TO_DOUBLE";
            case 152:
                return "GET";
            case 153:
                return "SET";
            case 154:
                return "LET";
            case 155:
                return "CONST";
            case 156:
                return "SETCONST";
            case 158:
                return "ARRAYCOMP";
            case 159:
                return "LETEXPR";
            case 160:
                return "WITHEXPR";
            case 161:
                return "DEBUGGER";
            case 162:
                return "COMMENT";
            case 163:
                return "GENEXPR";
            case 164:
                return "METHOD";
            case 165:
                return "ARROW";
            case 166:
                return "YIELD_STAR";
        }
    }

    public static String keywordToName(int token) {
        switch (token) {
            case 4:
                return "return";
            case 30:
                return "new";
            case 31:
                return "delete";
            case 32:
                return "typeof";
            case 42:
                return "null";
            case 43:
                return "this";
            case 44:
                return "false";
            case 45:
                return "true";
            case 50:
                return "throw";
            case 52:
                return "in";
            case 53:
                return "instanceof";
            case 73:
                return "yield";
            case 82:
                return "try";
            case 110:
                return "function";
            case 113:
                return "if";
            case 114:
                return "else";
            case 115:
                return "switch";
            case 116:
                return "case";
            case 117:
                return PreferencesManager.VALUE_KEYSTORE_DEFAULT;
            case 118:
                return "while";
            case 119:
                return "do";
            case 120:
                return "for";
            case 121:
                return "break";
            case 122:
                return "continue";
            case 123:
                return "var";
            case 124:
                return "with";
            case 125:
                return "catch";
            case 126:
                return "finally";
            case 127:
                return "void";
            case 154:
                return "let";
            case 155:
                return "const";
            case 161:
                return "debugger";
            default:
                return null;
        }
    }

    public static boolean isValidToken(int code) {
        return code >= -1 && code <= 167;
    }
}
