package org.apache.oro.text;

import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.Pattern;

/* JADX INFO: loaded from: autofirma.jar:org/apache/oro/text/PatternCache.class */
public interface PatternCache {
    Pattern addPattern(String str, int i) throws MalformedPatternException;

    Pattern getPattern(String str) throws MalformedCachePatternException;

    Pattern getPattern(String str, int i) throws MalformedCachePatternException;

    int capacity();
}
