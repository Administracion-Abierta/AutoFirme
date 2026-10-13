package org.apache.oro.text.regex;

/* JADX INFO: loaded from: autofirma.jar:org/apache/oro/text/regex/PatternMatcher.class */
public interface PatternMatcher {
    boolean matchesPrefix(char[] cArr, Pattern pattern, int i);

    boolean matchesPrefix(String str, Pattern pattern);

    boolean matchesPrefix(char[] cArr, Pattern pattern);

    boolean matchesPrefix(PatternMatcherInput patternMatcherInput, Pattern pattern);

    boolean matches(String str, Pattern pattern);

    boolean matches(char[] cArr, Pattern pattern);

    boolean matches(PatternMatcherInput patternMatcherInput, Pattern pattern);

    boolean contains(String str, Pattern pattern);

    boolean contains(char[] cArr, Pattern pattern);

    boolean contains(PatternMatcherInput patternMatcherInput, Pattern pattern);

    MatchResult getMatch();
}
