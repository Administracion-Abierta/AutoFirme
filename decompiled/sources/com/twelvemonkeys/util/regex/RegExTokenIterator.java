package com.twelvemonkeys.util.regex;

import com.twelvemonkeys.util.AbstractTokenIterator;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/util/regex/RegExTokenIterator.class */
public class RegExTokenIterator extends AbstractTokenIterator {
    private final Matcher matcher;
    private boolean next;

    public RegExTokenIterator(String str) {
        this(str, "\\S+");
    }

    public RegExTokenIterator(String str, String str2) {
        this.next = false;
        if (str == null) {
            throw new IllegalArgumentException("string == null");
        }
        if (str2 == null) {
            throw new IllegalArgumentException("pattern == null");
        }
        this.matcher = Pattern.compile(str2).matcher(str);
    }

    @Override // com.twelvemonkeys.util.TokenIterator
    public void reset() {
        this.matcher.reset();
    }

    @Override // java.util.Iterator
    public boolean hasNext() {
        if (!this.next) {
            boolean zFind = this.matcher.find();
            this.next = zFind;
            if (!zFind) {
                return false;
            }
        }
        return true;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // java.util.Iterator
    public String next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        this.next = false;
        return this.matcher.group();
    }
}
