package org.apache.oro.text.regex;

/* JADX INFO: loaded from: autofirma.jar:org/apache/oro/text/regex/MatchResult.class */
public interface MatchResult {
    int length();

    int groups();

    String group(int i);

    int begin(int i);

    int end(int i);

    int beginOffset(int i);

    int endOffset(int i);

    String toString();
}
