package org.apache.oro.text.regex;

/* JADX INFO: loaded from: autofirma.jar:org/apache/oro/text/regex/PatternCompiler.class */
public interface PatternCompiler {
    Pattern compile(String str) throws MalformedPatternException;

    Pattern compile(String str, int i) throws MalformedPatternException;

    Pattern compile(char[] cArr) throws MalformedPatternException;

    Pattern compile(char[] cArr, int i) throws MalformedPatternException;
}
