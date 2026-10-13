package org.mozilla.javascript;

import java.io.Serializable;
import java.util.ArrayDeque;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/ConsString.class */
public class ConsString implements CharSequence, Serializable {
    private static final long serialVersionUID = -8432806714471372570L;
    private CharSequence left;
    private CharSequence right;
    private final int length;
    private boolean isFlat = false;

    public ConsString(CharSequence str1, CharSequence str2) {
        this.left = str1;
        this.right = str2;
        this.length = this.left.length() + this.right.length();
    }

    private Object writeReplace() {
        return toString();
    }

    @Override // java.lang.CharSequence
    public String toString() {
        return this.isFlat ? (String) this.left : flatten();
    }

    /* JADX WARN: Code duplicated, block: B:13:0x0080  */
    /* JADX WARN: Code duplicated, block: B:14:0x0084  */
    private synchronized String flatten() {
        CharSequence charSequenceRemoveFirst;
        if (!this.isFlat) {
            char[] chars = new char[this.length];
            int charPos = this.length;
            ArrayDeque<CharSequence> stack = new ArrayDeque<>();
            stack.addFirst(this.left);
            CharSequence next = this.right;
            do {
                if (next instanceof ConsString) {
                    ConsString casted = (ConsString) next;
                    if (casted.isFlat) {
                        next = casted.left;
                        String str = (String) next;
                        charPos -= str.length();
                        str.getChars(0, str.length(), chars, charPos);
                        if (stack.isEmpty()) {
                            charSequenceRemoveFirst = null;
                        } else {
                            charSequenceRemoveFirst = stack.removeFirst();
                        }
                        next = charSequenceRemoveFirst;
                    } else {
                        stack.addFirst(casted.left);
                        next = casted.right;
                    }
                } else {
                    String str2 = (String) next;
                    charPos -= str2.length();
                    str2.getChars(0, str2.length(), chars, charPos);
                    if (stack.isEmpty()) {
                        charSequenceRemoveFirst = null;
                    } else {
                        charSequenceRemoveFirst = stack.removeFirst();
                    }
                    next = charSequenceRemoveFirst;
                }
            } while (next != null);
            this.left = new String(chars);
            this.right = "";
            this.isFlat = true;
        }
        return (String) this.left;
    }

    @Override // java.lang.CharSequence
    public int length() {
        return this.length;
    }

    @Override // java.lang.CharSequence
    public char charAt(int index) {
        String str = this.isFlat ? (String) this.left : flatten();
        return str.charAt(index);
    }

    @Override // java.lang.CharSequence
    public CharSequence subSequence(int start, int end) {
        String str = this.isFlat ? (String) this.left : flatten();
        return str.substring(start, end);
    }
}
