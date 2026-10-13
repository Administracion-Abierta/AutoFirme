package org.apache.oro.text.regex;

/* JADX INFO: loaded from: autofirma.jar:org/apache/oro/text/regex/CharStringPointer.class */
final class CharStringPointer {
    static final char _END_OF_STRING = 65535;
    int _offset;
    char[] _array;

    CharStringPointer(char[] charArray, int offset) {
        this._array = charArray;
        this._offset = offset;
    }

    CharStringPointer(char[] charArray) {
        this(charArray, 0);
    }

    char _getValue() {
        return _getValue(this._offset);
    }

    char _getValue(int offset) {
        if (offset < this._array.length && offset >= 0) {
            return this._array[offset];
        }
        return (char) 65535;
    }

    char _getValueRelative(int offset) {
        return _getValue(this._offset + offset);
    }

    int _getLength() {
        return this._array.length;
    }

    int _getOffset() {
        return this._offset;
    }

    void _setOffset(int offset) {
        this._offset = offset;
    }

    boolean _isAtEnd() {
        return this._offset >= this._array.length;
    }

    char _increment(int inc) {
        this._offset += inc;
        if (_isAtEnd()) {
            this._offset = this._array.length;
            return (char) 65535;
        }
        return this._array[this._offset];
    }

    char _increment() {
        return _increment(1);
    }

    char _decrement(int inc) {
        this._offset -= inc;
        if (this._offset < 0) {
            this._offset = 0;
        }
        return this._array[this._offset];
    }

    char _decrement() {
        return _decrement(1);
    }

    char _postIncrement() {
        char ret = _getValue();
        _increment();
        return ret;
    }

    char _postDecrement() {
        char ret = _getValue();
        _decrement();
        return ret;
    }

    String _toString(int offset) {
        return new String(this._array, offset, this._array.length - offset);
    }

    public String toString() {
        return _toString(0);
    }
}
