package org.apache.oro.text.regex;

/* JADX INFO: loaded from: autofirma.jar:org/apache/oro/text/regex/PatternMatcherInput.class */
final class PatternMatcherInput {
    String _originalStringInput;
    char[] _originalCharInput;
    char[] _originalBuffer;
    char[] _toLowerBuffer;
    int _beginOffset;
    int _endOffset;
    int _currentOffset;
    int _matchBeginOffset;
    int _matchEndOffset;

    public PatternMatcherInput(String input, int begin, int length) {
        this._matchBeginOffset = -1;
        this._matchEndOffset = -1;
        setInput(input, begin, length);
    }

    public PatternMatcherInput(String input) {
        this(input, 0, input.length());
    }

    public PatternMatcherInput(char[] input, int begin, int length) {
        this._matchBeginOffset = -1;
        this._matchEndOffset = -1;
        setInput(input, begin, length);
    }

    public PatternMatcherInput(char[] input) {
        this(input, 0, input.length);
    }

    public int length() {
        return this._endOffset - this._beginOffset;
    }

    public void setInput(String input, int begin, int length) {
        this._originalStringInput = input;
        this._originalCharInput = null;
        this._toLowerBuffer = null;
        this._originalBuffer = input.toCharArray();
        setCurrentOffset(begin);
        setBeginOffset(begin);
        setEndOffset(this._beginOffset + length);
    }

    public void setInput(String input) {
        setInput(input, 0, input.length());
    }

    public void setInput(char[] input, int begin, int length) {
        this._originalStringInput = null;
        this._toLowerBuffer = null;
        this._originalCharInput = input;
        this._originalBuffer = input;
        setCurrentOffset(begin);
        setBeginOffset(begin);
        setEndOffset(this._beginOffset + length);
    }

    public void setInput(char[] input) {
        setInput(input, 0, input.length);
    }

    public char charAt(int offset) {
        return this._originalBuffer[this._beginOffset + offset];
    }

    public String substring(int beginOffset, int endOffset) {
        return new String(this._originalBuffer, this._beginOffset + beginOffset, endOffset - beginOffset);
    }

    public String substring(int bo) {
        int beginOffset = bo + this._beginOffset;
        return new String(this._originalBuffer, beginOffset, this._endOffset - beginOffset);
    }

    public Object getInput() {
        if (this._originalStringInput == null) {
            return this._originalCharInput;
        }
        return this._originalStringInput;
    }

    public char[] getBuffer() {
        return this._originalBuffer;
    }

    public boolean endOfInput() {
        return this._currentOffset >= this._endOffset;
    }

    public int getBeginOffset() {
        return this._beginOffset;
    }

    public int getEndOffset() {
        return this._endOffset;
    }

    public int getCurrentOffset() {
        return this._currentOffset;
    }

    public void setBeginOffset(int offset) {
        this._beginOffset = offset;
    }

    public void setEndOffset(int offset) {
        this._endOffset = offset;
    }

    public void setCurrentOffset(int offset) {
        this._currentOffset = offset;
        setMatchOffsets(-1, -1);
    }

    public String toString() {
        return new String(this._originalBuffer, this._beginOffset, length());
    }

    public String preMatch() {
        return new String(this._originalBuffer, this._beginOffset, this._matchBeginOffset - this._beginOffset);
    }

    public String postMatch() {
        return new String(this._originalBuffer, this._matchEndOffset, this._endOffset - this._matchEndOffset);
    }

    public String match() {
        return new String(this._originalBuffer, this._matchBeginOffset, this._matchEndOffset - this._matchBeginOffset);
    }

    public void setMatchOffsets(int matchBeginOffset, int matchEndOffset) {
        this._matchBeginOffset = matchBeginOffset;
        this._matchEndOffset = matchEndOffset;
    }

    public int getMatchBeginOffset() {
        return this._matchBeginOffset;
    }

    public int getMatchEndOffset() {
        return this._matchEndOffset;
    }
}
