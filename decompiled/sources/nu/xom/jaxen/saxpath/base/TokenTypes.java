package nu.xom.jaxen.saxpath.base;

import org.ini4j.Registry;
import org.slf4j.Marker;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/jaxen/saxpath/base/TokenTypes.class */
class TokenTypes {
    static final int EOF = -1;
    static final int SKIP = -2;
    static final int ERROR = -3;
    static final int EQUALS = 1;
    static final int NOT_EQUALS = 2;
    static final int LESS_THAN_SIGN = 3;
    static final int LESS_THAN_OR_EQUALS_SIGN = 4;
    static final int GREATER_THAN_SIGN = 5;
    static final int GREATER_THAN_OR_EQUALS_SIGN = 6;
    static final int PLUS = 7;
    static final int MINUS = 8;
    static final int STAR = 9;
    static final int MOD = 10;
    static final int DIV = 11;
    static final int SLASH = 12;
    static final int DOUBLE_SLASH = 13;
    static final int DOT = 14;
    static final int DOT_DOT = 15;
    static final int IDENTIFIER = 16;
    static final int AT = 17;
    static final int PIPE = 18;
    static final int COLON = 19;
    static final int DOUBLE_COLON = 20;
    static final int LEFT_BRACKET = 21;
    static final int RIGHT_BRACKET = 22;
    static final int LEFT_PAREN = 23;
    static final int RIGHT_PAREN = 24;
    static final int DOLLAR = 25;
    static final int LITERAL = 26;
    static final int AND = 27;
    static final int OR = 28;
    static final int DOUBLE = 29;
    static final int COMMA = 30;
    static final int STAR_OPERATOR = 31;

    TokenTypes() {
    }

    static String getTokenText(int i) {
        switch (i) {
            case -3:
                return "(error)";
            case -2:
                return "(skip)";
            case -1:
                return "(eof)";
            case 0:
                return "Unrecognized token type: 0";
            case 1:
                return "=";
            case 2:
                return "!=";
            case 3:
                return "<";
            case 4:
                return "<=";
            case 5:
                return ">";
            case 6:
                return ">=";
            case 7:
                return Marker.ANY_NON_NULL_MARKER;
            case 8:
                return "-";
            case 9:
                return "*";
            case 10:
                return "mod";
            case 11:
                return "div";
            case 12:
                return "/";
            case 13:
                return "//";
            case 14:
                return ".";
            case 15:
                return "..";
            case 16:
                return "(identifier)";
            case 17:
                return Registry.Key.DEFAULT_NAME;
            case 18:
                return "|";
            case 19:
                return ":";
            case 20:
                return "::";
            case 21:
                return "[";
            case 22:
                return "]";
            case 23:
                return "(";
            case 24:
                return ")";
            case 25:
                return "$";
            case 26:
                return "(literal)";
            case 27:
                return "and";
            case 28:
                return "or";
            case 29:
                return "(double)";
            case 30:
                return ",";
            case 31:
                return "*";
            default:
                return new StringBuffer().append("Unrecognized token type: ").append(i).toString();
        }
    }
}
